package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.LocationDto;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.text.TextUtils;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class SendTracksService extends WakefulIntentService {

	public static final String TAG = "SendTracksService";

	public static boolean sendEnabled = true;

	private SettingsDao settingsDao;
	private LocationsDao locationsDao;

	public SendTracksService() {
		super(TAG);
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		// send pending messages related to sim card change
		WakefulIntentService.sendWakefulWork(this,
				BackgroundSmsSendService.class);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "In doWakefulWork.");
		}

		if (!sendEnabled) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Sync disabled. Doing nothing.");
			}
		} else {
			settingsDao = SettingsDao.getInstance(getApplicationContext());
			locationsDao = LocationsDao.getInstance(getApplicationContext());

			do {
				locationsDao.finalizeTimedOutLocations();
				boolean succeeded = send();
				if (!succeeded) {
					break;
				}
			} while (locationsDao.hasUnsyncedTracks());
		}
	}

	/**
	 * 
	 * @return true on success, false on failure
	 */
	private boolean send() {
		if (TextUtils.isEmpty(settingsDao.getString("employeeId"))) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "Employee ID is missing. Not sending tracks.");
				return false;
			}
		}

		List<LocationDto> locations = locationsDao.getUnsyncedTracks();

		if (locations == null) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "No unsynced tracks.");
			}

			return true;
		}

		int trackingMode = settingsDao.getInt(
				EffortProvider.Settings.KEY_TRACK_BY, 1);

		switch (trackingMode) {

		case 1:
			// Track via data only
			return trackViaData(locations);

		case 2:
			// Track via SMS only
			trackViaSMS(locations);
			return true;

		case 3:
			// Use SMS as fall back

			boolean isTrackedViaData = trackViaData(locations);

			boolean canTrack = Utils.canTrackNow(this.getApplicationContext());

			if (!isTrackedViaData && canTrack) {

				String lastUpdateViaSms = settingsDao
						.getString(EffortProvider.Settings.KEY_LAST_UPDATE_AT_SMS_SENT);
				String lastUpdateViaData = settingsDao
						.getString(EffortProvider.Settings.KEY_LAST_UPDATE_TO_CLOUD);

				long latestUpdateTime = SQLiteDateTimeUtils
						.getLatestUpdateTimeLocal(lastUpdateViaSms,
								lastUpdateViaData);

				long timeDifferent = System.currentTimeMillis()
						- latestUpdateTime;
				int fallbackInterval = settingsDao.getInt(
						EffortProvider.Settings.KEY_SMS_TRACK_FREQUENCY,
						EffortProvider.Settings.DEFAULT_FALL_BACK_INTERVAL);

				if (latestUpdateTime != 0 && timeDifferent >= fallbackInterval) {
					sendLatestTrack();
					return false;
				}
			} else {
				return isTrackedViaData;
			}
		}

		return false;

	}

	private boolean trackViaData(List<LocationDto> locations) {
		AndroidHttpClient httpClient = null;

		try {
			String trackUrl = getTrackUrl();

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Track URL: " + trackUrl);
			}

			httpClient = AndroidHttpClient.newInstance("EFFORT");
			HttpPost httpPost = new HttpPost(trackUrl);
			Utils.setTimeouts(httpPost);
			JSONArray tracksArray = new JSONArray();

			for (LocationDto location : locations) {
				tracksArray.put(location.getJsonObject(getApplicationContext(),
						Locations.PURPOSE_TRACKING));
			}

			JSONObject json = new JSONObject();

			try {
				json.put("tracks", tracksArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			String requestJson = json.toString();

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Request JSON: " + requestJson);
			}

			HttpEntity requestEntity = new StringEntity(requestJson);
			httpPost.setHeader("Content-Type", "application/json");
			httpPost.setEntity(requestEntity);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				Log.w(TAG, "Received non-ok response: "
						+ httpResponse.getStatusLine().toString());
				return false;
			}

			String response = EntityUtils.toString(httpResponse.getEntity());

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response JSON: " + response);
			}

			JSONTokener tokener = new JSONTokener(response);
			Object object = tokener.nextValue();

			if (object == null || object instanceof JSONObject == false) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Expected a JSON Object, but did not get it.");
				}

				return false;
			}

			JSONObject responseJson = (JSONObject) object;

			if (responseJson.has("updateAfter")) {
				String prevTrackingFrequency = settingsDao
						.getString(Settings.KEY_TRACKING_FREQUENCY);
				String updateAfter = responseJson.getString("updateAfter");

				if (!TextUtils.equals(updateAfter, prevTrackingFrequency)) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "Restarting tracking alarm.");
					}

					settingsDao.saveSetting(Settings.KEY_TRACKING_FREQUENCY,
							updateAfter);
					EffortApplication.restartTrackingAlarm();
				}

				onSendSuccess(locations);
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_UPDATE_TO_CLOUD,
						SQLiteDateTimeUtils.getCurrentSQLiteDateTime());
				return true;
			} else {
				Log.e(TAG, "Could not sync tracks.");
				return false;
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad Sync URL: " + e.toString(), e);
			return false;
		} catch (IOException e) {
			Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);
			return false;
		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse response json: " + e.toString(), e);
			return false;
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	private void trackViaSMS(List<LocationDto> locations) {
		for (LocationDto location : locations) {
			locationsDao.updateSmsProcessFlag(location.getLocalId(), true);
			sendSms(location);
		}
	}

	private void sendLatestTrack() {
		LocationDto location = locationsDao.getLatestUnsyncedTrack();
		location.setIsFallBack(true);
		sendSms(location);
	}

	private String getTrackUrl() {
		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri
				.parse(serverBaseUrl)
				.buildUpon()
				.appendEncodedPath(
						"service/track/" + settingsDao.getString("employeeId"));

		Utils.appendCommonQueryParameters(getApplicationContext(), builder);

		return builder.build().toString();
	}

	private void onSendSuccess(List<LocationDto> syncedTracks) {
		for (LocationDto track : syncedTracks) {
			locationsDao.updateLocationDirtyFlag(track.getLocalId(), false);
		}

		locationsDao.deleteSyncedLocations();
	}

	private void sendSms(LocationDto location) {
		String message = location.getSmsBody(settingsDao
				.getString(Settings.KEY_EMPLOYEE_ID));

		Intent sentIntent = new Intent(this.getApplicationContext(),
				SmsSentReceiver.class);
		sentIntent.setData(Uri.parse(Utils.SEND_SMS_URI + "/"
				+ location.getLocalId()));

		sentIntent.putExtra("locationDto", location);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				this.getApplicationContext(), 0, sentIntent, 0);

		String address = settingsDao
				.getString(EffortProvider.Settings.KEY_SMSC);

		if (!TextUtils.isEmpty(address)) {
			Utils.sendSms(address, message, pendingIntent);
		}

		settingsDao.saveSetting(
				EffortProvider.Settings.KEY_LAST_UPDATE_AT_SMS_SENT,
				SQLiteDateTimeUtils.getCurrentSQLiteDateTime());
	}

}

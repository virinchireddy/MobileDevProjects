package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class EffortApplication extends Application implements
		ConnectionCallbacks, OnConnectionFailedListener {
	public static final double INDIA_LATITUDE = 20.593684;
	public static final double INDIA_LONGITUDE = 78.96288;
	private EffortPhoneStateListener phoneStateListener = new EffortPhoneStateListener();
	public static final int SIGNAL_STRENGTH_UNKNOWN = -1;
	private static EffortApplication instance;
	private static final String TAG = "EffortApplication";;
	private static DBHelper dbHelper;
	private static SettingsDao settingsDao;
	private static Context applicationContext;
	public static final boolean loggingEnabled = false;
	public static LocationClient locationClient;

	@Override
	public void onCreate() {
		super.onCreate();

		if (BuildConfig.DEBUG) {
			Log.v(TAG, "Effort oncreate called");
		}
		instance = this;
		applicationContext = getApplicationContext();

		if (loggingEnabled) {
			dbHelper = DBHelper.getInstance(applicationContext);
		}

		settingsDao = SettingsDao.getInstance(applicationContext);

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		// Checking here is required , becuase for some phones, we can insert
		// sim card without doing switchoff
		// Utils.detectImsiChangeAndSave(applicationContext, "app");
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		log(TAG, "In onConfigurationChanged");
	}

	@Override
	public void onLowMemory() {
		log(TAG, "In onLowMemory");
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		log(TAG, "In onTerminate");
		super.onTerminate();
		Utils.flushLocations(getApplicationContext());
	}

	public static void log(String tag, String message) {
		if (BuildConfig.DEBUG) {
			Log.i(tag, message);
		}

		if (loggingEnabled) {
			if (dbHelper == null) {
				Log.w(TAG, "Not logged [tag=" + tag + ", message=" + message
						+ "] because dbHelper is null.");
				return;
			}

			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put("tag", tag);
			values.put("message", message);
			values.put("time", SQLiteDateTimeUtils.getCurrentSQLiteDateTime());
			db.insert("log", null, values);
		}
	}

	public static EffortApplication getInstance() {
		return instance;
	}

	public int getSignalStrength() {
		return phoneStateListener.getSignalStrength();
	}

	private class EffortPhoneStateListener extends PhoneStateListener {
		private int signalStrength = SIGNAL_STRENGTH_UNKNOWN;

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);

			if (signalStrength.isGsm()) {
				this.signalStrength = signalStrength.getGsmSignalStrength();
			}
		}

		public int getSignalStrength() {
			return signalStrength;
		}
	}

	public static boolean isActivityVisible() {
		return activityVisible;
	}

	public static void activityResumed() {
		activityVisible = true;
	}

	public static void activityPaused() {
		activityVisible = false;
		if (settingsDao != null) {
			settingsDao.saveSetting("activityPausedTime",
					SQLiteDateTimeUtils.getCurrentSQLiteDateTime());
			settingsDao.saveSetting("lastNotificationMessage", "");
		}
	}

	private static boolean activityVisible;

	public static void restartTrackingAlarm() {
		if (applicationContext == null) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "App hasn't started yet!");
			}

			return;
		}

		int trackingFrequency = settingsDao.getInt(
				EffortProvider.Settings.KEY_TRACKING_FREQUENCY, 60000);

		log(TAG, "Scheduling tracking service to run every "
				+ trackingFrequency + " millis.");
		Intent trackIntent = new Intent(applicationContext,
				TrackAlarmReceiver.class);
		PendingIntent trackPendingIntent = PendingIntent.getBroadcast(
				applicationContext, 0, trackIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) applicationContext
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(trackPendingIntent);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
				trackingFrequency, trackPendingIntent);
	}

	public static void restartSyncAlarm() {
		if (applicationContext == null) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "App hasn't started yet!");
			}

			return;
		}

		Intent syncIntent = new Intent(applicationContext,
				SyncAlarmReceiver.class);
		PendingIntent syncPendingIntent = PendingIntent.getBroadcast(
				applicationContext, 0, syncIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) applicationContext
				.getSystemService(Context.ALARM_SERVICE);

		alarmManager.cancel(syncPendingIntent);

		boolean trackOn = Utils.canTrackNow(applicationContext);
		// set alarms at begining of the day , middle of the day and at end of
		// the day if it is a valid working day
		Utils.setAlarmsForDay(applicationContext);
		if (BuildConfig.DEBUG) {
			Log.v(TAG, "in restartSyncAlarm");
		}

		if (trackOn) {
			log(TAG, "in restartSyncAlarm condition match");

			log(TAG, "Scheduling sync service to run every 5 minutes.");
			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
					300000, syncPendingIntent);
			Utils.flushLocations(getInstance().getApplicationContext());
		} else {
			log(TAG, "Scheduling sync service to run every 30 minutes.");
			alarmManager.setInexactRepeating(
					AlarmManager.ELAPSED_REALTIME_WAKEUP, 0,
					AlarmManager.INTERVAL_HALF_HOUR, syncPendingIntent);
		}

		if (TextUtils.isEmpty(settingsDao.getString("trackOn"))) {
			settingsDao.saveSetting("trackOn", "" + trackOn);
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		EffortApplication.log(TAG, "Connected to location client.");

		if (locationClient != null) {
			LocationRequest locationRequest = new LocationRequest();
			locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			// locationRequest.setNumUpdates(1);
			locationRequest.setInterval(250);
			Intent intent = new Intent(getApplicationContext(),
					FusedReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					getApplicationContext(), 1, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			locationClient.requestLocationUpdates(locationRequest,
					pendingIntent);
			EffortApplication.log(TAG, "Requested fused location updates.");
		}
	}

	@Override
	public void onDisconnected() {
		EffortApplication.log(TAG, "Disconnected from the location client.");
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		EffortApplication.log(TAG, "Connection to location client failed.");
	}

}

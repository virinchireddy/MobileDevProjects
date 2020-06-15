package in.spoors.effort1;

import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Customer;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.text.TextUtils;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class FetchNearbyCustomersService extends WakefulIntentService {

	public static final String TAG = "FetchNearbyCustomersService";
	private double latitude;
	private double longitude;
	SettingsDao settingsDao;
	/**
	 * Broadcasts this action when sync is done.
	 */
	public static final String ACTION_DONE = "in.spoors.effort1.FETCH_NEARBY_DONE";

	public FetchNearbyCustomersService() {
		super(TAG);
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "In doWakefulWork.");
		}

		settingsDao = SettingsDao.getInstance(getApplicationContext());
		CustomersDao customersDao = CustomersDao
				.getInstance(getApplicationContext());
		List<Customer> addedCustomers = new ArrayList<Customer>();

		AndroidHttpClient httpClient = null;

		try {
			String serverBaseUrl = getString(R.string.server_base_url);
			Builder builder = Uri
					.parse(serverBaseUrl)
					.buildUpon()
					.appendEncodedPath(
							"service/customer/search/nearest/"
									+ settingsDao.getString("employeeId"));
			Utils.appendCommonQueryParameters(getApplicationContext(), builder);

			latitude = intent.getDoubleExtra("latitude",
					EffortApplication.INDIA_LATITUDE);
			longitude = intent.getDoubleExtra("longitude",
					EffortApplication.INDIA_LONGITUDE);

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("lat", String.valueOf(latitude)));
			params.add(new BasicNameValuePair("lng", String.valueOf(longitude)));

			String limit = settingsDao
					.getString(Settings.KEY_DEFAULT_NBC_LIMIT);

			if (!TextUtils.isEmpty(limit)) {
				params.add(new BasicNameValuePair("limit", limit));
			} else {
				params.add(new BasicNameValuePair("limit", "10"));
			}

			String radius = settingsDao
					.getString(Settings.KEY_DEFAULT_NBC_RADIUS);

			if (!TextUtils.isEmpty(radius)) {
				params.add(new BasicNameValuePair("radius", radius));
			} else {
				params.add(new BasicNameValuePair("radius", "10"));
			}

			List<Long> customerIds = customersDao.getAllRemoteCustomerIds();

			if (customerIds != null) {
				params.add(new BasicNameValuePair("customerIds", TextUtils
						.join(",", customerIds)));
			} else {
				params.add(new BasicNameValuePair("customerIds", ""));
			}

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params);

			String url = builder.build().toString();

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Fetch nearby customers URL: " + url);
				Log.i(TAG,
						"Fetch nearby customers Parameters: "
								+ EntityUtils.toString(entity));
			}

			httpClient = AndroidHttpClient.newInstance("EFFORT");
			HttpPost httpPost = new HttpPost(url);
			Utils.setTimeouts(httpPost);
			httpPost.setEntity(entity);

			HttpResponse httpResponse = httpClient.execute(httpPost);
			String response = EntityUtils.toString(httpResponse.getEntity());

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response JSON: " + response);
			}

			JSONTokener tokener = new JSONTokener(response);
			Object obj = tokener.nextValue();

			if (!(obj instanceof JSONArray)) {
				Log.e(TAG,
						"Invalid nearby customers response. Expected a JSON array but did not get it.");
				return;
			}

			Utils.addCustomers((JSONArray) obj, addedCustomers,
					getApplicationContext(), true);

			for (Customer customer : addedCustomers) {
				customersDao.save(customer);
			}

		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad URL: " + e.toString(), e);
		} catch (IOException e) {
			Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);
		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
		} catch (ParseException e) {
			Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
		} finally {

			updateDistances();
			if (httpClient != null) {
				httpClient.close();
			}

			Intent broadcastIntent = new Intent(ACTION_DONE);
			sendBroadcast(broadcastIntent);
		}
	}

	void updateDistances() {
		try {
			Utils.updateCustomerDistances(getApplicationContext());
			settingsDao.saveSetting("nearbyLatitude", String.valueOf(latitude));
			settingsDao.saveSetting("nearbyLongitude",
					String.valueOf(longitude));
			settingsDao.saveSetting("nearbyAddress", String.valueOf(latitude)
					+ ", " + String.valueOf(longitude));

			// replace nearbyAddress with the addess, if it gets resolved
			Geocoder geocoder = new Geocoder(this);
			List<Address> addresses = geocoder.getFromLocation(latitude,
					longitude, 1);

			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				settingsDao.saveSetting("nearbyAddress", Utils
						.getAddressForMapDisplay(address.getSubLocality(),
								address.getLocality(), null, null, null, null));
			}
		} catch (IOException e) {
			Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);
		}
	}
}
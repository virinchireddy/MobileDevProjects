package in.spoors.effort1;

import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Customer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class FetchCustomerService extends WakefulIntentService {

	public static final String TAG = "FetchCustomerService";

	/**
	 * Broadcasts this action when sync is done.
	 */
	public static final String ACTION_DONE = "in.spoors.effort1.FETCH_CUSTOMER_DONE";

	private SettingsDao settingsDao;
	private CustomersDao customersDao;

	public FetchCustomerService() {
		super(TAG);
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "In doWakefulWork.");
		}

		settingsDao = SettingsDao.getInstance(getApplicationContext());
		customersDao = CustomersDao.getInstance(getApplicationContext());

		AndroidHttpClient httpClient = null;
		long localCustomerId = 0;

		try {
			if (intent == null) {
				// if there is no customerId, do nothing
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"No intent with remote customer ID. Doing nothing.");
				}

				return;
			}

			long remoteCustomerId = intent.getLongExtra("remoteCustomerId", 0);

			if (remoteCustomerId == 0) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"No remote customer ID found in the intent. Doing nothing.");
				}

				return;
			}

			localCustomerId = customersDao.getLocalId(remoteCustomerId);

			String url = getUrl(remoteCustomerId);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Customer fetch URL: " + url);
			}

			httpClient = AndroidHttpClient.newInstance("EFFORT");
			HttpGet httpGet = new HttpGet(url);
			Utils.setTimeouts(httpGet);
			HttpResponse httpResponse = httpClient.execute(httpGet);

			String response = EntityUtils.toString(httpResponse.getEntity());

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response JSON: " + response);
			}

			JSONTokener tokener = new JSONTokener(response);
			Object obj = tokener.nextValue();

			if (!(obj instanceof JSONObject)) {
				Log.e(TAG,
						"Invalid sync response. Expected a JSON object but did not get it.");
				return;
			}

			JSONObject json = (JSONObject) obj;

			Customer customer = Customer.parse(json, getApplicationContext());
			customer.setPartial(false);
			customersDao.save(customer);
			localCustomerId = customer.getLocalId();
		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad URL: " + e.toString(), e);
		} catch (IOException e) {
			Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);
		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
		} catch (ParseException e) {
			Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}

			Customer customer = customersDao
					.getCustomerWithLocalId(localCustomerId);
			customer.setInUse(true);
			customersDao.save(customer);

			Intent broadcastIntent = new Intent(ACTION_DONE);
			broadcastIntent.putExtra("localCustomerId", localCustomerId);
			sendBroadcast(broadcastIntent);
		}
	}

	private String getUrl(long remoteCustomerId) {
		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri
				.parse(serverBaseUrl)
				.buildUpon()
				.appendEncodedPath(
						"service/customer/get/" + remoteCustomerId + "/"
								+ settingsDao.getString("employeeId"));
		Utils.appendCommonQueryParameters(getApplicationContext(), builder);
		Uri syncUri = builder.build();
		return syncUri.toString();
	}

}

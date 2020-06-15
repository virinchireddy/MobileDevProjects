package in.spoors.effort1;

import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Customer;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.text.TextUtils;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class CustomerSearchService extends WakefulIntentService {

	public static final String TAG = "CustomerSearchService";

	/**
	 * Broadcasts this action when sync is done.
	 */
	public static final String ACTION_DONE = "in.spoors.effort1.SEARCH_DONE";

	private SettingsDao settingsDao;
	private CustomersDao customersDao;

	public CustomerSearchService() {
		super(TAG);
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "In onHandleIntent.");
		}

		settingsDao = SettingsDao.getInstance(getApplicationContext());
		customersDao = CustomersDao.getInstance(getApplicationContext());

		AndroidHttpClient httpClient = null;

		String error = null;

		try {
			if (intent == null) {
				// if there is no query, do nothing
				if (BuildConfig.DEBUG) {
					Log.w(TAG, "No search intent. Doing nothing.");
				}
				return;
			}

			String query = intent.getStringExtra("query");

			if (TextUtils.isEmpty(query)) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG, "No query found in the intent. Doing nothing.");
				}
				return;
			}

			String url = getUrl(query);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Customer search URL: " + url);
			}

			httpClient = AndroidHttpClient.newInstance("EFFORT");
			HttpGet httpGet = new HttpGet(url);
			Utils.setTimeouts(httpGet);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			String response = EntityUtils.toString(httpResponse.getEntity());

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Response JSON: " + response);
			}

			customersDao.deletePartialCustomers();
			CustomerSearchResponse searchResponse = new CustomerSearchResponse(
					getApplicationContext());
			boolean succeeded = searchResponse.parse(response);

			if (succeeded) {
				onSuccess(searchResponse);
			} else {
				error = "Received unexpected response from the cloud.";
			}
		} catch (MalformedURLException e) {
			Log.e(TAG, "Bad URL: " + e.toString(), e);
			error = "Bad URL";
		} catch (IOException e) {
			Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);
			error = "Network fetch failed.";
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}

			Intent broadcastIntent = new Intent(ACTION_DONE);

			if (!TextUtils.isEmpty(error)) {
				broadcastIntent.putExtra("error", error);
			}

			sendBroadcast(broadcastIntent);
		}
	}

	private String getUrl(String query) {
		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri
				.parse(serverBaseUrl)
				.buildUpon()
				.appendEncodedPath(
						"service/customer/search/"
								+ settingsDao.getString("employeeId"));
		Utils.appendCommonQueryParameters(getApplicationContext(), builder);
		builder.appendQueryParameter("query", query);
		Uri syncUri = builder.build();
		return syncUri.toString();
	}

	private void onSuccess(CustomerSearchResponse response) {

		for (Customer customer : response.getAddedCustomers()) {
			customersDao.save(customer);
		}

		Utils.updateCustomerDistances(getApplicationContext());
	}

}

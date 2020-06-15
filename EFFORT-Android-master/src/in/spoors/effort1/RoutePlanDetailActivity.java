package in.spoors.effort1;

import in.spoors.effort1.dao.AssignedRoutesDao;
import in.spoors.effort1.dao.CustomerStatusDao;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.AssignedRoute;
import in.spoors.effort1.dto.Customer;
import in.spoors.effort1.dto.CustomerStatusDto;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Customers;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

@SuppressLint("DefaultLocale")
public class RoutePlanDetailActivity extends ActionBarActivity implements
		OnItemClickListener {
	public static final String TAG = "AssignedRouteDetailActivity";
	public static final String CUSTOMERS_SORT = "customerSortActualOrderInRoutePlan";
	private long assignRouteId = 0;
	public static final long CUSTOMERS_SORT_NEAR_NESS = 0;
	public static final long CUSTOMERS_SORT_ACTUAL = 1;
	public static final String ASSIGN_ROUTE_ID = "assignRouteId";
	private AssignedRoute assignedRoute;

	private TextView fromToTextView;
	private TextView completTimeTextView;
	private List<Customer> customers;
	private CustomerStatusDao customerStatusDao;

	private AssignedRoutesDao assignedRoutesDao;
	private SettingsDao settingsDao;
	private CustomersDao customersDao;
	String customerIds;
	Button viewAllCustomersButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}

		if (savedInstanceState == null) {
			assignRouteId = getIntent().getLongExtra(
					EffortProvider.AssignedRoutes._ID, 0);
		} else {
			assignRouteId = savedInstanceState.getLong(ASSIGN_ROUTE_ID);
		}

		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_route_plan_detail);
		setSupportProgressBarIndeterminateVisibility(false);

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle("Assigned Route Plan Detail");

		assignedRoutesDao = AssignedRoutesDao
				.getInstance(getApplicationContext());
		customersDao = CustomersDao.getInstance(getApplicationContext());

		customerStatusDao = CustomerStatusDao
				.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());

		SimpleDateFormat dtf = Utils.getDateFormat(getApplicationContext());
		SimpleDateFormat cmpDtf = Utils
				.getDateTimeFormat(getApplicationContext());
		if (assignedRoute == null) {
			assignedRoute = assignedRoutesDao
					.getAssignedRouteWithId(assignRouteId);
		}

		if (assignedRoute.getRemoteCustomerIds() == null) {
			finish();
		}

		actionBar.setTitle("Route plan");
		actionBar.setSubtitle(assignedRoute.getRouteName());

		fromToTextView = (TextView) findViewById(R.id.fromTimeTextView);

		completTimeTextView = (TextView) findViewById(R.id.completeTimeTextView);

		TextView customersToVisit = (TextView) findViewById(R.id.customersToVisit);
		String customersLabel = settingsDao.getLabel(
				Settings.LABEL_CUSTOMER_PLURAL_KEY,
				Settings.LABEL_CUSTOMER_PLURAL_DEFAULT_VLAUE);
		customersToVisit.setText(customersLabel);
		fromToTextView.setText(dtf.format(assignedRoute.getStartDate()) + " - "
				+ dtf.format(assignedRoute.getEndDate()));

		if (assignedRoute.getCompletionTime() != null) {
			completTimeTextView.setText(cmpDtf.format(assignedRoute
					.getCompletionTime()));
		} else {
			completTimeTextView.setText("Not completed yet.");
		}

		Button completeRoutePlanBtn = (Button) findViewById(R.id.completeRoutePlan);

		if (assignedRoute != null && assignedRoute.getCompletionTime() != null) {
			completeRoutePlanBtn.setEnabled(false);
		}
		viewAllCustomersButton = (Button) findViewById(R.id.viewAllCustomersButton);
		viewAllCustomersButton.setText("View " + customersLabel + " on map");
		viewAllCustomersButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onViewAllCustomersButtonClick(v);
			}
		});

		setSupportProgressBarIndeterminateVisibility(true);
		new FetchAssignRouteDetailsTask().execute("" + assignedRoute.getId());

	}

	@Override
	protected void onResume() {
		super.onResume();

		createCustomerList();
	}

	private void createCustomerList() {
		customerIds = assignedRoute.getRemoteCustomerIds();

		if (customerIds != null) {
			viewAllCustomersButton = (Button) findViewById(R.id.viewAllCustomersButton);
			viewAllCustomersButton.setVisibility(View.VISIBLE);
			long customerSort = settingsDao.getLong(CUSTOMERS_SORT,
					CUSTOMERS_SORT_NEAR_NESS);
			Button customerSortBtn = (Button) findViewById(R.id.sortByNearnessButton);
			if (customerSort == CUSTOMERS_SORT_NEAR_NESS) {
				customers = customersDao.getCustomersWithRemoteIds(customerIds,
						Customers.DISTANCE);
				if (customers != null && customers.size() > 0) {
					customers = sortInDistanceFirstOrder(customers);
				}
				customerSortBtn.setText("Sort by actual");
			} else {
				customers = customersDao.getCustomersWithRemoteIds(customerIds,
						null);
				customers = sortInActualOrder(customers, customerIds);
				customerSortBtn.setText("Sort by nearness");
			}
		}

		if (customers != null && customers.size() > 0) {
			viewAllCustomersButton.setVisibility(View.VISIBLE);
		} else {
			viewAllCustomersButton.setVisibility(View.GONE);
		}
		LinearLayout customerLayout = (LinearLayout) findViewById(R.id.routePlanCustomers);
		customerLayout.removeAllViews();
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for (Customer customer : customers) {

			View view = li
					.inflate(R.layout.list_item_customer_route_plan, null);
			view.setTag(customer);

			ImageView imageView = (ImageView) view
					.findViewById(R.id.completeTypeImageView);
			CustomerStatusDto customerStatus = customerStatusDao
					.getCustomerStatusWithAssignedRouteIdAndCustomerId(
							customer.getRemoteId(), assignedRoute.getId());
			if (customerStatus != null && customerStatus.getStatus() != null
					&& customerStatus.getStatus() == true) {
				imageView.setImageResource(R.drawable.ic_route_plan_complete);
			} else {
				imageView.setImageResource(R.drawable.ic_empty_image);
			}
			TextView textView = (TextView) view
					.findViewById(R.id.customerTitleTextView);

			String customerDistanceString = "N/A";

			if (customer.getDistance() != null) {
				float distance = customer.getDistance();
				customerDistanceString = String.format("%.1f km",
						distance / 1000);
			}

			textView.setText(customer.getName() + "\n" + customerDistanceString);

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Customer customer = (Customer) v.getTag();
					Intent intent = new Intent(RoutePlanDetailActivity.this,
							CustomerActivities.class);
					intent.putExtra(CustomerActivities.CUSTOMER_ID,
							customer.getRemoteId());
					intent.putExtra(CustomerActivities.ASSIGN_ROUTE_ID,
							assignedRoute.getId());
					intent.putExtra(CustomerActivities.CUSTOMER_NAME,
							customer.getName());
					startActivity(intent);

				}
			});

			customerLayout.addView(view);
		}
	}

	private void captureLocation() {
		Intent intent = new Intent(this, LocationCaptureService.class);
		intent.putExtra(EffortProvider.Locations.PURPOSE,
				EffortProvider.Locations.PURPOSE_COMPLETE_ROUTE_PLAN);
		intent.putExtra(EffortProvider.Locations.FOR_ID, assignedRoute.getId());
		WakefulIntentService.sendWakefulWork(this, intent);
	}

	public void onSortByNearnessButtonClick(View view) {

		long customerSort = settingsDao.getLong(CUSTOMERS_SORT,
				CUSTOMERS_SORT_NEAR_NESS);

		if (customerSort == CUSTOMERS_SORT_NEAR_NESS) {
			customerSort = CUSTOMERS_SORT_ACTUAL;
		} else {
			customerSort = CUSTOMERS_SORT_NEAR_NESS;
		}
		// prefs.edit().putLong(CUSTOMERS_SORT, customerSort).commit();
		settingsDao.saveSetting(CUSTOMERS_SORT, customerSort + "");

		createCustomerList();
	}

	public void onCompleteRoutePlanButtonClick(View view) {

		int completedVisits = customerStatusDao
				.getNumberOfCompletedCustomerVisitsWithAssignRouteId(assignedRoute
						.getId());
		long minCustomersToComplete = 1;

		if (assignedRoute.getMinCustomersToComplete() != null) {
			minCustomersToComplete = assignedRoute.getMinCustomersToComplete();
		}

		if (completedVisits >= minCustomersToComplete) {
			assignedRoute.setCompletionTime(new Date());
			assignedRoute.setDirty(true);
			assignedRoutesDao.save(assignedRoute);
			captureLocation();
			Utils.sync(getApplicationContext());
			finish();
		} else {
			String customerLabel = settingsDao.getLabel(
					Settings.LABEL_CUSTOMER_SINGULAR_KEY,
					Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE);
			String singularVisit = settingsDao.getLabel(
					Settings.LABEL_JOB_SINGULAR_KEY,
					Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE);
			String pluralVisit = settingsDao.getLabel(
					Settings.LABEL_JOB_PLURAL_KEY,
					Settings.LABEL_JOB_PLURAL_DEFAULT_VLAUE);

			Toast.makeText(
					getApplicationContext(),
					"Please complete at least "
							+ minCustomersToComplete
							+ " "
							+ customerLabel
							+ " "
							+ (minCustomersToComplete > 1 ? pluralVisit
									: singularVisit)
							+ " to complete this route plan",
					Toast.LENGTH_SHORT).show();
		}
	}

	// private boolean isCompletedAtLeastOneCustomerVisit() {
	// try {
	// int noOfCompletedVisits = customerStatusDao
	// .getNumberOfCompletedCustomerVisitsWithAssignRouteId(assignedRoute
	// .getId());
	// if (noOfCompletedVisits > 0) {
	// return true;
	// }
	// } catch (Throwable e) {
	// if (BuildConfig.DEBUG) {
	// Log.d(TAG, "Throwable: " + e.toString());
	// }
	// }
	// return false;
	// }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putLong(ASSIGN_ROUTE_ID, assignRouteId);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onOptionsItemSelected: groupId=" + item.getGroupId()
					+ ", itemId=" + item.getItemId());
		}
		switch (item.getItemId()) {
		case android.R.id.home:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "ActionBar's Up/home button clicked.");
			}
			finish();
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class FetchAssignRouteDetailsTask extends
			AsyncTask<String, Integer, Long> {
		private List<CustomerStatusDto> customersStatus = new ArrayList<CustomerStatusDto>();

		@Override
		protected Long doInBackground(String... params) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In doInBackground.");
				Log.i(TAG, "Assignedroute: " + assignedRoute);
			}

			AndroidHttpClient httpClient = null;

			try {
				if (assignedRoute != null && assignedRoute.getCached() != null
						&& assignedRoute.getCached() == true) {

				} else {
					return null;
				}

				String url = getUrl(assignedRoute.getId());

				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Fetch AssignRoute URL: " + url);
				}

				httpClient = AndroidHttpClient.newInstance("EFFORT");
				HttpPost httpPost = new HttpPost(url);
				Utils.setTimeouts(httpPost);

				String existedFormSpecs = Utils
						.getExistedFormSpecsAsString(getApplicationContext());

				List<NameValuePair> namedValuePairs = new ArrayList<NameValuePair>();
				namedValuePairs.add(new BasicNameValuePair("formSpecIds",
						existedFormSpecs));
				HttpEntity requestEntity = new UrlEncodedFormEntity(
						namedValuePairs);
				httpPost.setEntity(requestEntity);

				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Fetch AssigneRoute detail BODY: "
							+ existedFormSpecs);
				}
				HttpResponse httpResponse = httpClient.execute(httpPost);
				String response = EntityUtils
						.toString(httpResponse.getEntity());

				JSONTokener tokener = new JSONTokener(response);
				Object obj = tokener.nextValue();

				if (!(obj instanceof JSONObject)) {
					Log.e(TAG,
							"Invalid assignedRoute details response. Expected a JSON object but did not get it.");
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									getApplicationContext(),
									"Received unexpected response from the cloud.",
									Toast.LENGTH_LONG).show();
						}
					});
					return null;
				}

				JSONObject jsonObject = (JSONObject) obj;

				if (jsonObject.get("customersStatus") instanceof JSONArray) {
					Utils.addCustomersStatus(
							jsonObject.optJSONArray("customersStatus"),
							customersStatus, getApplicationContext());
				}

				for (CustomerStatusDto customerStatus : customersStatus) {
					customerStatusDao.save(customerStatus);
				}

				FormResponse fetchResponse = new FormResponse(
						getApplicationContext());
				if (jsonObject.get("forms") instanceof JSONObject) {
					fetchResponse.parse(response);
					fetchResponse.save(false);
				}

			} catch (MalformedURLException e) {
				Log.e(TAG, "Bad URL: " + e.toString(), e);
			} catch (IOException e) {
				Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(
								getApplicationContext(),
								"Failed to fetch assignedRoute detail due to network error.",
								Toast.LENGTH_LONG).show();
					}
				});
			} catch (JSONException e) {
				Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
			} catch (ParseException e) {
				Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
			} finally {
				if (httpClient != null) {
					httpClient.close();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Fetch finished.");
			}

			createCustomerList();
			setSupportProgressBarIndeterminateVisibility(false);
		}

		private String getUrl(long remoteAssignRouteId) {
			String serverBaseUrl = getString(R.string.server_base_url);
			Builder builder = Uri
					.parse(serverBaseUrl)
					.buildUpon()
					.appendEncodedPath(
							"service/assignedRoute/detail/"
									+ remoteAssignRouteId + "/"
									+ settingsDao.getString("employeeId"));
			Utils.appendCommonQueryParameters(getApplicationContext(), builder);
			Uri syncUri = builder.build();
			return syncUri.toString();
		}
	}

	private List<Customer> sortInActualOrder(List<Customer> customers,
			String customerIds) {
		List<Customer> customList = new ArrayList<Customer>();
		if (customerIds != null) {

			String[] ids = customerIds.split(",");

			if (ids != null) {

				for (int i = 0; i < ids.length; i++) {
					if (customers != null) {
						for (Customer customer : customers) {
							if (Long.parseLong(ids[i]) == customer
									.getRemoteId()) {
								customList.add(customer);
							}
						}
					}
				}
			}
		}
		return customList;
	}

	private List<Customer> sortInDistanceFirstOrder(List<Customer> customers) {
		List<Customer> customersList = new ArrayList<Customer>();
		List<Customer> tempCustomList = new ArrayList<Customer>();

		for (Customer customer : customers) {
			if (customer.getDistance() != null) {
				customersList.add(customer);
			} else {
				tempCustomList.add(customer);
			}
		}
		customersList.addAll(tempCustomList);
		return customersList;
	}

	// used to navigate to form activity
	void onViewAllCustomersButtonClick(View view) {

		Intent mapIntent = new Intent(RoutePlanDetailActivity.this,
				CustomersRouteMapActivity.class);
		mapIntent.putExtra(CustomerActivities.ASSIGN_ROUTE_ID,
				assignedRoute.getId());
		mapIntent.putExtra("customerids", customerIds);
		startActivity(mapIntent);

	}
}
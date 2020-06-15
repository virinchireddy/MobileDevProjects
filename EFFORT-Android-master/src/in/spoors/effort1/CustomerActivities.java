package in.spoors.effort1;

import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.dao.ActivitySpecsDao;
import in.spoors.effort1.dao.CompletedActivitiesDao;
import in.spoors.effort1.dao.CustomerStatusDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.ActivitySpec;
import in.spoors.effort1.dto.CompletedActivity;
import in.spoors.effort1.dto.CustomerStatusDto;
import in.spoors.effort1.dto.Form;
import in.spoors.effort1.provider.EffortProvider;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class CustomerActivities extends ActionBarActivity implements
		TabListener {
	public static String ASSIGN_ROUTE_ID = "assignRouteId";
	public static String CUSTOMER_ID = "customerId";
	private static String TAG = "CustomerActivities";
	public static String CUSTOMER_NAME = "customerName";
	public static String SELECTED_TAB_POSITION = "selectedTabPosition";
	private Long assignRouteId;
	private Long customerId;
	private String customerName;
	public static String TITLE_THIS_ROUTE = "This Route";
	public static String TITLE_PREVIOUS_ROUTES = "Previous Routes";
	private CustomersRouteActivitiesFragment activitiesFragment;
	private PreviousCustomerRouteActivitiesFragment previousActivitiesFragment;
	private List<ActivitySpec> activities;
	public static final int FILL_FORM = 1;
	private boolean showFutureItemsOnly;
	private Button loadMoreButton;
	private Long oldestCompletedActivityFetched;
	private int selectedTabPosition;
	private SettingsDao settingsDao;
	public static final int PAGE_SIZE = 10;
	public static final String FORM_VIEW = "view";

	private ActivitySpecsDao activitiesDao;
	private CustomerStatusDao customerStatusDao;
	private CustomerStatusDto customerStatusDto;
	private CompletedActivitiesDao completedActivitiesDao;
	private TextView completTimeTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_route_plan_customer_activities);
		setSupportProgressBarIndeterminateVisibility(false);
		if (savedInstanceState == null) {
			assignRouteId = getIntent().getLongExtra(ASSIGN_ROUTE_ID, 0);
			customerId = getIntent().getLongExtra(CUSTOMER_ID, 0);
			customerName = getIntent().getStringExtra(CUSTOMER_NAME);
			showFutureItemsOnly = true;
			selectedTabPosition = getIntent().getIntExtra(
					SELECTED_TAB_POSITION, 0);
		} else {
			assignRouteId = savedInstanceState.getLong(ASSIGN_ROUTE_ID);
			customerId = savedInstanceState.getLong(CUSTOMER_ID);
			customerName = savedInstanceState.getString(CUSTOMER_NAME);
			showFutureItemsOnly = savedInstanceState.getBoolean(
					"showFutureItemsOnly", true);
			selectedTabPosition = 0;
		}

		activitiesDao = ActivitySpecsDao.getInstance(getApplicationContext());
		customerStatusDao = CustomerStatusDao
				.getInstance(getApplicationContext());
		completedActivitiesDao = CompletedActivitiesDao
				.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());

		activitiesFragment = (CustomersRouteActivitiesFragment) getSupportFragmentManager()
				.findFragmentById(R.id.customerActivities);
		previousActivitiesFragment = (PreviousCustomerRouteActivitiesFragment) getSupportFragmentManager()
				.findFragmentById(R.id.previousCustomerActivities);

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(customerName + " Activities");

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab thisRouteTab = actionBar.newTab();
		thisRouteTab.setText(TITLE_THIS_ROUTE);
		thisRouteTab.setTabListener(this);
		actionBar.addTab(thisRouteTab);

		Tab previousRoutesTab = actionBar.newTab();
		previousRoutesTab.setText(TITLE_PREVIOUS_ROUTES);
		previousRoutesTab.setTabListener(this);
		actionBar.addTab(previousRoutesTab);

		activities = activitiesDao.getAllActivities();
		customerStatusDto = customerStatusDao
				.getCustomerStatusWithAssignedRouteIdAndCustomerId(customerId,
						assignRouteId);

		Button completeVisitButton = (Button) findViewById(R.id.completeVisitButton);
		/*
		 * String customerLabel = settingsDao.getLabel(
		 * Settings.LABEL_CUSTOMER_SINGULAR_KEY,
		 * Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE); String singularVisit
		 * = settingsDao.getLabel( Settings.LABEL_JOB_SINGULAR_KEY,
		 * Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE);
		 */
		completeVisitButton.setText("Complete visit");
		loadMoreButton = (Button) findViewById(R.id.loadMoreButton);
		updateLoadMoreButtonText();

		SimpleDateFormat dtf = Utils.getDateTimeFormat(getApplicationContext());
		completTimeTextView = (TextView) findViewById(R.id.completeTimeTextView);
		if (customerStatusDto != null && customerStatusDto.getStatus() != null
				&& customerStatusDto.getStatus() == true) {
			completeVisitButton.setEnabled(false);
			if (customerStatusDto.getStatusChangeTime() != null) {
				completTimeTextView.setText(dtf.format(customerStatusDto
						.getStatusChangeTime()));
			}
		} else {
			completeVisitButton.setEnabled(true);
			completTimeTextView.setText("Not completed yet.");
		}

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		if (selectedTabPosition == 0) {

			ft.hide(previousActivitiesFragment);
			ft.show(activitiesFragment);
			ft.commit();
		} else {
			ft.hide(activitiesFragment);
			ft.show(previousActivitiesFragment);
			ft.commit();
		}

		LinearLayout activitiesLayout = (LinearLayout) findViewById(R.id.routePlanCustomerActivityButtons);
		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (activities != null) {
			TextView noActivities = (TextView) findViewById(R.id.emptyTextViewActivityBtns);
			noActivities.setVisibility(View.GONE);
			for (ActivitySpec activity : activities) {
				View view = li.inflate(R.layout.list_item_customer_activity,
						null);
				Button activityButton = (Button) view
						.findViewById(R.id.customerActivityButton);
				activityButton.setTag(activity);
				activityButton.setText(activity.getName());
				activitiesLayout.addView(view);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshRouteHistory();
		refreshCustomerRouteHistory();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("showFutureItemsOnly", showFutureItemsOnly);
		outState.putLong(ASSIGN_ROUTE_ID, assignRouteId);
		outState.putLong(CUSTOMER_ID, customerId);
		outState.putString(CUSTOMER_NAME, customerName);
		outState.putInt(SELECTED_TAB_POSITION, selectedTabPosition);
	}

	private void refreshCustomerRouteHistory() {
		try {
			LinearLayout previousActivitiesLayout = (LinearLayout) findViewById(R.id.previousActivitiesLayout);
			previousActivitiesLayout.removeAllViews();
			LayoutInflater liPrevious = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			List<CompletedActivity> completdActivities = completedActivitiesDao
					.getAllCompletedActivitiesWithCustomerId(customerId,
							showFutureItemsOnly);

			if (completdActivities != null) {
				TextView textView = (TextView) findViewById(R.id.emptyPreviousTextView);
				textView.setVisibility(View.GONE);
				Button loadMoreButton = (Button) findViewById(R.id.loadMoreButton);
				loadMoreButton
						.setText(R.string.fetch_more_route_history_from_cloud);
				for (CompletedActivity completedActivity : completdActivities) {
					ActivitySpec activity = activitiesDao
							.getActivityWithActivityId(completedActivity
									.getActivityId());
					View view = liPrevious.inflate(
							R.layout.list_item_previous_activities, null);
					TextView activityTextView = (TextView) view
							.findViewById(R.id.activityTextView);
					SimpleDateFormat dtf = Utils
							.getDateTimeFormat(getApplicationContext());

					Button viewButton = (Button) view
							.findViewById(R.id.viewButton);
					viewButton.setTag(completedActivity);
					if (activity != null) {

						if (completedActivity.getActivityCompletedTime() != null) {
							activityTextView.setText(activity.getName()
									+ "\n\n"
									+ dtf.format(completedActivity
											.getActivityCompletedTime()));
						} else {
							activityTextView.setText(activity.getName()
									+ "\n\n");
						}
						previousActivitiesLayout.addView(view);
					}
				}
			}

		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, " Throwable " + e.toString());
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.customer_activities, menu);

		customerStatusDto = customerStatusDao
				.getCustomerStatusWithAssignedRouteIdAndCustomerId(customerId,
						assignRouteId);

		if (customerStatusDto != null && customerStatusDto.getStatus() != null
				&& customerStatusDto.getStatus() == true) {
			menu.findItem(R.id.completeCustomerVisit).setVisible(false);
		} else {
			menu.findItem(R.id.completeCustomerVisit).setVisible(true);
		}

		return super.onCreateOptionsMenu(menu);
	}

	private void refreshRouteHistory() {
		try {

			List<CompletedActivity> completdActivities = completedActivitiesDao
					.getAllCompletedActivitiesWithAssignRouteIdAndCustomerId(
							assignRouteId, customerId);
			LinearLayout customerActivityHistoryLayout = (LinearLayout) findViewById(R.id.customerActivityHistory);
			customerActivityHistoryLayout.removeAllViews();
			LayoutInflater liDwn = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (completdActivities != null) {
				TextView textView = (TextView) findViewById(R.id.emptyTextView);
				textView.setVisibility(View.GONE);
				for (CompletedActivity completedActivity : completdActivities) {
					ActivitySpec activity = activitiesDao
							.getActivityWithActivityId(completedActivity
									.getActivityId());
					View view = liDwn.inflate(
							R.layout.list_item_previous_activities, null);
					TextView activityTextView = (TextView) view
							.findViewById(R.id.activityTextView);
					SimpleDateFormat dtf = Utils
							.getDateTimeFormat(getApplicationContext());

					Button viewButton = (Button) view
							.findViewById(R.id.viewButton);

					viewButton.setTag(completedActivity);
					if (activity != null) {
						completedActivity.setFormSpecId(activity
								.getFormSpecId());
						if (completedActivity.getActivityCompletedTime() != null) {
							activityTextView.setText(activity.getName()
									+ "\n\n"
									+ dtf.format(completedActivity
											.getActivityCompletedTime()));
						} else {
							activityTextView.setText(activity.getName()
									+ "\n\n");
						}
						customerActivityHistoryLayout.addView(view);
					}

				}
			}
		} catch (Throwable e) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, " Throwable " + e.toString());
			}
		}
	}

	public void onActivityClick(View view) {
		ActivitySpec activity = (ActivitySpec) view.getTag();

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Launching form activity for form spec id "
							+ activity.getFormSpecId());
		}

		// long formSpecId = formSpecsDao.getLatestFormSpecId(activity
		// .getFormSpecId());
		long formSpecId = activity.getFormSpecId();

		if (formSpecId == 0) {
			Toast.makeText(getApplicationContext(),
					"This activity doesn't mapped with any form spec",
					Toast.LENGTH_LONG).show();
			return;
		}

		Intent intent = new Intent(this, FormActivity.class);
		intent.setAction("fillFormForActivity");
		intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID, formSpecId);
		intent.putExtra(EffortProvider.CompletedActivities.ASSIGNED_ROUTE_ID,
				assignRouteId);
		intent.putExtra(EffortProvider.CompletedActivities.ACTIVITY_ID,
				activity.getId());
		intent.putExtra(EffortProvider.CompletedActivities.CUSTOMER_ID,
				customerId);

		intent.putExtra(FORM_VIEW, 0);
		startActivityForResult(intent, FILL_FORM);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "In onActivityResult. requestCode=" + requestCode
					+ ", resultCode=" + resultCode);
		}
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case FILL_FORM:
				long localFormId = data.getLongExtra("localFormId", 0);
				long assignRouteId = data
						.getLongExtra(
								EffortProvider.CompletedActivities.ASSIGNED_ROUTE_ID,
								0);
				long activityId = data.getLongExtra(
						EffortProvider.CompletedActivities.ACTIVITY_ID, 0);

				long view = data.getLongExtra(FORM_VIEW, 0);

				if (localFormId == 0 || view == 1) {
					return;
				}

				CompletedActivity completedActivity = new CompletedActivity();
				completedActivity.setAssignRouteId(assignRouteId);
				completedActivity.setActivityId(activityId);
				completedActivity.setDirty(true);
				completedActivity.setClientFormId(localFormId);
				completedActivity.setRemoteCustomerId(customerId);
				completedActivity.setActivityCompletedTime(new Date());
				completedActivitiesDao.save(completedActivity);
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "CompletedActivity Saved: " + completedActivity);
				}
				Utils.sync(getApplicationContext());

				break;

			default:
				break;
			}
		}
	}

	public void onViewClick(View view) {
		try {

			CompletedActivity completedActivity = (CompletedActivity) view
					.getTag();

			FormsDao formsDao = FormsDao.getInstance(getApplicationContext());

			Intent intent = new Intent(this, FormActivity.class);
			intent.setAction("fillFormForActivity");
			Form form = null;
			if (completedActivity != null
					&& completedActivity.getFormId() != null) {

				form = formsDao.getFormWithRemoteId(completedActivity
						.getFormId());
				completedActivity.setClientFormId(form.getLocalId());
			} else {
				form = formsDao.getFormWithLocalId(completedActivity
						.getClientFormId());
			}
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Launching form: " + form);
			}
			if (form != null) {
				long formSpecId = form.getFormSpecId();

				intent.putExtra(EffortProvider.Forms._ID,
						completedActivity.getClientFormId());
				intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID, formSpecId);
				intent.putExtra(
						EffortProvider.CompletedActivities.ASSIGNED_ROUTE_ID,
						assignRouteId);
				intent.putExtra(EffortProvider.CompletedActivities.ACTIVITY_ID,
						completedActivity.getActivityId());
				long formView = 1;
				intent.putExtra(FORM_VIEW, formView);
				startActivityForResult(intent, FILL_FORM);

			} else {
				return;
			}
		} catch (Throwable e) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Throwable: " + e.toString());
			}

		}
	}

	public void onLoadMoreButtonClick(View view) {

		int oldAssgnedRoutes = completedActivitiesDao
				.getNumberOfCustomerRouteHistoryOlderThanToday(customerId);
		updateLoadMoreButtonText();
		if (showFutureItemsOnly && oldAssgnedRoutes > 0) {
			showFutureItemsOnly = false;
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Loading all local completedactivities.");
			}
			refreshCustomerRouteHistory();

		} else {

			// if the job no longer exists in our local db, start from today
			String before = XsdDateTimeUtils.getXsdDateTimeFromLocalTime(Utils
					.getBeginningOfToday());

			if (oldestCompletedActivityFetched != null) {
				String oldestJobStartTime = completedActivitiesDao
						.getStartTime(oldestCompletedActivityFetched);

				if (oldestJobStartTime != null) {
					before = XsdDateTimeUtils
							.getXsdDateTimeFromSQLiteDateTime(oldestJobStartTime);
				}
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Fetching completedactivities before " + before);
			}

			loadMoreButton.setEnabled(false);
			setSupportProgressBarIndeterminateVisibility(true);
			new FetchPreviousCustomerRouteHistoryTask().execute(before);
		}
	}

	public void onCompleteRoutePlanButtonClick(View view) {
		completeVisit();
	}

	private void completeVisit() {
		if (isAnyActivityFilledForThisCustomer()) {
			customerStatusDto = new CustomerStatusDto();
			customerStatusDto.setAssignRouteId(assignRouteId);
			customerStatusDto.setCustomerId(customerId);
			customerStatusDto.setStatusChangeTime(new Date());
			customerStatusDto.setStatus(true);
			customerStatusDto.setDirty(true);
			customerStatusDao.save(customerStatusDto);

			CustomerStatusDto customerStatusWithAssignedRouteIdAndCustomerId = customerStatusDao
					.getCustomerStatusWithAssignedRouteIdAndCustomerId(
							customerId, assignRouteId);
			// TODO
			if (customerStatusWithAssignedRouteIdAndCustomerId != null) {
				captureLocation(customerStatusWithAssignedRouteIdAndCustomerId
						.getLoaclId());
			}
			Utils.sync(getApplicationContext());
			finish();
		} else {
			String customerLabel = settingsDao.getLabel(
					Settings.LABEL_CUSTOMER_SINGULAR_KEY,
					Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE);
			String singularVisit = settingsDao.getLabel(
					Settings.LABEL_JOB_SINGULAR_KEY,
					Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE);
			Toast.makeText(
					getApplicationContext(),
					"Please fill at least 1 activity to complete this "
							+ customerLabel + " " + singularVisit,
					Toast.LENGTH_LONG).show();
		}
	}

	private boolean isAnyActivityFilledForThisCustomer() {
		int noOfCompletedActivities = completedActivitiesDao
				.getNumberOfCompletedActivitiesWithAssignRouteIdAndCustomerId(
						assignRouteId, customerId);
		if (noOfCompletedActivities > 0) {
			return true;
		}
		return false;
	}

	private void captureLocation(long id) {
		Intent intent = new Intent(this, LocationCaptureService.class);
		intent.putExtra(EffortProvider.Locations.PURPOSE,
				EffortProvider.Locations.PURPOSE_CUSTOMER_STATUS_ACTIVITY);
		intent.putExtra(EffortProvider.Locations.FOR_ID, id);
		WakefulIntentService.sendWakefulWork(this, intent);
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onTabSelected: " + tab.getText());
		}
		selectedTabPosition = tab.getPosition();
		if (TITLE_THIS_ROUTE.equals(tab.getText())) {
			ft.hide(previousActivitiesFragment);
			ft.show(activitiesFragment);
		} else if (TITLE_PREVIOUS_ROUTES.equals(tab.getText())) {
			updateLoadMoreButtonText();
			ft.hide(activitiesFragment);
			ft.show(previousActivitiesFragment);
		}

	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		Log.i(TAG, "onTabUnselected");

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
		case R.id.completeCustomerVisit:
			completeVisit();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateLoadMoreButtonText() {

		int oldJobs = completedActivitiesDao
				.getNumberOfCustomerRouteHistoryOlderThanToday(customerId);

		if (showFutureItemsOnly && oldJobs > 0) {
			loadMoreButton.setText(R.string.fetch_all_route_history_from_local);
		} else {
			loadMoreButton
					.setText(R.string.fetch_more_route_history_from_cloud);
		}
	}

	private class FetchPreviousCustomerRouteHistoryTask extends
			AsyncTask<String, Integer, Long> {
		private List<CompletedActivity> completedActivities = new ArrayList<CompletedActivity>();

		@Override
		protected Long doInBackground(String... params) {

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In doInBackground.");
			}

			AndroidHttpClient httpClient = null;
			try {
				settingsDao = SettingsDao.getInstance(getApplicationContext());
				String url = getUrl(params[0]);
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Fetch previous assigned routes URL: " + url);
				}
				if (url == null) {
					return null;
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
					Log.i(TAG, "Fetch previous customersActivities BODY: "
							+ existedFormSpecs);
				}
				HttpResponse httpResponse = httpClient.execute(httpPost);
				String response = EntityUtils
						.toString(httpResponse.getEntity());

				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Response JSON: " + response);
				}

				JSONTokener tokener = new JSONTokener(response);
				Object obj = tokener.nextValue();
				if (!(obj instanceof JSONObject)) {
					Log.e(TAG,
							"Invalid previous customer completed activities response. Expected a JSON array but did not get it.");
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
				JSONArray jsonCompletedActivitiesArray = jsonObject
						.optJSONArray("completedActivities");

				int completedActivitieslength = 0;
				if (jsonCompletedActivitiesArray != null) {
					completedActivitieslength = jsonCompletedActivitiesArray
							.length();
				}

				if (completedActivitieslength <= 0) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									getApplicationContext(),
									"There are no more old customer route history on the cloud.",
									Toast.LENGTH_LONG).show();
						}
					});
					return null;
				}

				if (jsonObject.get("completedActivities") instanceof JSONArray) {
					Utils.addCompletedActivities(
							jsonObject.optJSONArray("completedActivities"),
							completedActivities, getApplicationContext());
				}

				for (CompletedActivity activity : completedActivities) {
					completedActivitiesDao.save(activity);
				}
				FormResponse formResponse = new FormResponse(
						getApplicationContext());
				formResponse.parse(response);
				boolean isCached = false;
				formResponse.save(isCached);

				if (completedActivitieslength > 0) {
					return completedActivities.get(
							completedActivities.size() - 1).getRemoteId();
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
								"Failed to fetch activities due to network error.",
								Toast.LENGTH_LONG).show();
					}
				});
			} catch (JSONException e) {
				Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
			} catch (ParseException e) {
				Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
			} catch (Throwable e) {
				Log.e(TAG, "Throwable exception: " + e.toString(), e);
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
				Log.d(TAG, "Fetch customer completed activities finished.");
			}
			oldestCompletedActivityFetched = result;
			refreshCustomerRouteHistory();
			setSupportProgressBarIndeterminateVisibility(false);
			loadMoreButton.setEnabled(true);

		}

	}

	private String getUrl(String before) {
		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri.parse(serverBaseUrl).buildUpon();

		builder.appendEncodedPath("service/custRoute/detail/" + customerId
				+ "/" + settingsDao.getString("employeeId"));

		Utils.appendCommonQueryParameters(getApplicationContext(), builder);
		builder.appendQueryParameter("before", before);
		builder.appendQueryParameter("pageSize", "" + PAGE_SIZE);
		Uri syncUri = builder.build();
		return syncUri.toString();
	}
}
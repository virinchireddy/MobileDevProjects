package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.dao.AssignedRoutesDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.AssignedRoute;
import in.spoors.effort1.provider.EffortProvider.AssignedRoutes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RoutePlansActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, OnCheckedChangeListener,
		RefreshListener {

	public static final String TAG = "RoutePlansActivity";
	public static final int PAGE_SIZE = 10;
	private ListView listView;
	private SimpleCursorAdapter adapter;
	private DrawerFragment drawerFragment;
	private SimpleDateFormat dateTimeFormatWithOutYear;
	private AssignedRoutesDao assignedRoutesDao;
	private SettingsDao settingsDao;

	private Long oldestJobFetched;
	private Button loadMoreButton;
	private boolean showFutureItemsOnly;
	private boolean doCleanUp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_route_plans);
		setSupportProgressBarIndeterminateVisibility(false);

		dateTimeFormatWithOutYear = Utils
				.getDateFormat(getApplicationContext());

		loadMoreButton = (Button) findViewById(R.id.loadMoreButton);

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle("Route Plans");
		
		if (savedInstanceState != null) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Has saved instance state.");
			}
			doCleanUp = savedInstanceState.getBoolean("doCleanUp", true);
			showFutureItemsOnly = savedInstanceState.getBoolean(
					"showFutureItemsOnly", true);
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Does not have saved instance state.");
			}
			doCleanUp = true;
			showFutureItemsOnly = true;
		}
		if (doCleanUp) {
			new CleanUpTask().execute();
		}

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);
		getSupportLoaderManager().initLoader(0, null, this);
		assignedRoutesDao = AssignedRoutesDao
				.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		updateLoadMoreButtonText();

		String[] columns = { AssignedRoutes._ID, AssignedRoutes.ROUTE_NAME,
				AssignedRoutes.START_DATE, AssignedRoutes._ID };

		int[] views = { R.id.typeImageView, R.id.titleTextView,
				R.id.timeTextView, R.id.nextImageView };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_route_plan, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);
		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_routeplan,
				"Route plan", null, this);

	}

	private void updateLoadMoreButtonText() {

		int oldJobs = assignedRoutesDao
				.getNumberOfAssignedRoutesOlderThanToday();

		if (showFutureItemsOnly && oldJobs > 0) {
			loadMoreButton.setText("Show all routes stored locally");
		} else {
			loadMoreButton.setText("Fetch more routes from cloud");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader.");
		}
		String today = SQLiteDateTimeUtils
				.getSQLiteDateTimeForBeginningOfToday();

		String condition = "("
				+ AssignedRoutes.DELETED
				+ " != 'true'"
				+ (showFutureItemsOnly ? " AND (" + AssignedRoutes.END_DATE
						+ " >= '" + today + "')" : "") + " OR ("
				+ AssignedRoutes.CACHED + " = 'true' AND "
				+ AssignedRoutes.DELETED + " != 'true'" + "))";

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Condition is: " + condition);
		}

		return new CursorLoader(getApplicationContext(),
				AssignedRoutes.CONTENT_URI, AssignedRoutes.ALL_COLUMNS,
				condition, null, AssignedRoutes.START_DATE + " ASC");

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}

		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoaderReset.");
		}

		// swapCursor method requires API level 11 or higher.
		// Thus, use SimpleCursorAdapter from support library.
		adapter.swapCursor(null);
	}

	class MyViewBinder implements ViewBinder {

		private final String TAG = "RoutePlansActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
					+ cursor.getPosition() + ", columnIndex=" + columnIndex);
			String type = cursor.getString(AssignedRoutes.ROUTE_NAME_INDEX);
			Log.i("Value: is ", type);

			switch (columnIndex) {
			case AssignedRoutes.ROUTE_NAME_INDEX:
				Date presentTime = new Date();
				TextView titleTV = (TextView) view;
				titleTV.setTextColor(0xff000000);
				titleTV.setText(cursor
						.getString(AssignedRoutes.ROUTE_NAME_INDEX));
				// overdue
				/*
				 * CompletedAssignedRoute cmpAsnRte = completedAssignedRoutesDao
				 * .getCompletedActivityWithRemoteId(cursor
				 * .getLong(AssignedRoutes._ID_INDEX));
				 */
				String completionTime = cursor
						.getString(AssignedRoutes.COMPLETION_TIME_INDEX);

				if (completionTime != null) {
					titleTV.setTextColor(0xff000000);
				} else {
					if (presentTime.after(SQLiteDateTimeUtils
							.getLocalTime(cursor
									.getString(AssignedRoutes.END_DATE_INDEX)))) {
						titleTV.setTextColor(0xffff0000);
					}
				}
				return true;

			case AssignedRoutes._ID_INDEX:
				if (view.getId() == R.id.typeImageView) {
					/*
					 * CompletedAssignedRoute cmpAsnRoute =
					 * completedAssignedRoutesDao
					 * .getCompletedActivityWithRemoteId(cursor
					 * .getLong(AssignedRoutes._ID_INDEX));
					 */
					String completionTime1 = cursor
							.getString(AssignedRoutes.COMPLETION_TIME_INDEX);

					ImageView imageView = (ImageView) view;

					if (completionTime1 != null) {
						imageView
								.setImageResource(R.drawable.ic_drawer_completed_route);
					} else {
						Date now = new Date();

						if (now.after(SQLiteDateTimeUtils.getLocalTime(cursor
								.getString(AssignedRoutes.END_DATE_INDEX)))) {
							imageView
									.setImageResource(R.drawable.route_overdue);
						} else {
							imageView
									.setImageResource(R.drawable.ic_drawer_routeplan);
						}
					}

				} else if (view.getId() == R.id.nextImageView) {

					view.setVisibility(View.VISIBLE);

				}

				return true;

			case AssignedRoutes.START_DATE_INDEX:

				Date startTime = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(AssignedRoutes.START_DATE_INDEX));
				Date endTime = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(AssignedRoutes.END_DATE_INDEX));
				TextView tv = (TextView) view;
				view.setVisibility(View.VISIBLE);

				tv.setText(dateTimeFormatWithOutYear.format(startTime) + " - "
						+ dateTimeFormatWithOutYear.format(endTime));
				return true;

			default:
				return false;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Parent: " + parent.toString() + ", View: "
							+ view.toString() + ", Position: " + position
							+ ", ID: " + id);
		}

		AssignedRoute assignedRoute = assignedRoutesDao
				.getAssignedRouteWithId(id);
		if (assignedRoute == null) {
			return;
		}
		if (assignedRoute.getCached() != null
				&& assignedRoute.getCached() == true) {
			assignedRoute.setCached(false);
			assignedRoutesDao.save(assignedRoute);
		}
		Intent intent = new Intent(this, RoutePlanDetailActivity.class);
		intent.putExtra(AssignedRoutes._ID, id);
		startActivity(intent);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}

		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("showFutureItemsOnly", showFutureItemsOnly);
		outState.putBoolean("doCleanUp", false);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);

		super.onBackPressed();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// checkedMap.put((Long) buttonView.getTag(), isChecked);
	}

	@Override
	public void onRefresh() {
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerFragment.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		drawerFragment.onConfigurationChanged(newConfig);
	}

	public void onLoadMoreButtonClick(View view) {
		// int oldJobs = jobsDao.getNumberOfJobsOlderThanToday();
		int oldAssgnedRoutes = assignedRoutesDao
				.getNumberOfAssignedRoutesOlderThanToday();

		if (showFutureItemsOnly && oldAssgnedRoutes > 0) {
			showFutureItemsOnly = false;

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Loading all local jobs.");
			}

			getSupportLoaderManager().restartLoader(0, null, this);
			updateLoadMoreButtonText();
		} else {
			setSupportProgressBarIndeterminateVisibility(true);

			// if the job no longer exists in our local db, start from today
			String before = XsdDateTimeUtils.getXsdDateTimeFromLocalTime(Utils
					.getBeginningOfToday());

			if (oldestJobFetched != null) {
				String oldestJobStartTime = assignedRoutesDao
						.getStartTime(oldestJobFetched);

				if (oldestJobStartTime != null) {
					before = XsdDateTimeUtils
							.getXsdDateTimeFromSQLiteDateTime(oldestJobStartTime);
				}
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Fetching jobs before " + before);
			}

			loadMoreButton.setEnabled(false);
			new FetchPreviousAssignedRoutesTask().execute(before);
		}
	}

	private class FetchPreviousAssignedRoutesTask extends
			AsyncTask<String, Integer, Long> {

		@Override
		protected Long doInBackground(String... params) {
			List<AssignedRoute> assignedRoutes = new ArrayList<AssignedRoute>();
			List<AssignedRoute> completedAssignedRoutes = new ArrayList<AssignedRoute>();

			AndroidHttpClient httpClient = null;
			settingsDao = SettingsDao.getInstance(getApplicationContext());

			try {
				String url = getUrl(params[0]);
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Fetch previous assigned routes URL: " + url);
				}
				if (url == null) {
					return null;
				}

				httpClient = AndroidHttpClient.newInstance("EFFORT");
				HttpGet httpGet = new HttpGet(url);
				Utils.setTimeouts(httpGet);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				String response = EntityUtils
						.toString(httpResponse.getEntity());

				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Response JSON: " + response);
				}

				JSONTokener tokener = new JSONTokener(response);
				Object obj = tokener.nextValue();
				if (!(obj instanceof JSONObject)) {
					Log.e(TAG,
							"Invalid previous assigned routes response. Expected a JSON array but did not get it.");
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

				JSONObject jsonRoutes = (JSONObject) obj;

				JSONArray jsonAssignedRoutesArray = Utils.getJsonArray(
						jsonRoutes, "assignedRoutes");
				JSONArray jsonCompletedActivitiesArray = Utils.getJsonArray(
						jsonRoutes, "completedAssignedActivities");

				assignedRoutesDao = AssignedRoutesDao
						.getInstance(getApplicationContext());

				int assignedRouteslength = 0;
				if (jsonAssignedRoutesArray != null) {
					assignedRouteslength = jsonAssignedRoutesArray.length();
				}

				int completedAssignedActivitieslength = 0;
				if (jsonCompletedActivitiesArray != null) {
					completedAssignedActivitieslength = jsonCompletedActivitiesArray
							.length();
				}

				if (assignedRouteslength <= 0) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									getApplicationContext(),
									"There are no more old assigned routes on the cloud.",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				for (int i = 0; i < assignedRouteslength; ++i) {
					JSONObject assignedRouteJson = jsonAssignedRoutesArray
							.getJSONObject(i);

					AssignedRoute assignedRoute = AssignedRoute.parse(
							assignedRouteJson, getApplicationContext(), false);

					if (assignedRoute != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "AssignedRoute " + i + ": "
									+ assignedRoute.toString());
						}

						assignedRoutes.add(assignedRoute);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "AssignedRoute " + i + " is null.");
						}
					}
				}
				for (int i = 0; i < completedAssignedActivitieslength; ++i) {
					JSONObject completedAssignRouteJson = jsonCompletedActivitiesArray
							.getJSONObject(i);

					AssignedRoute completedAssignedRoute = AssignedRoute.parse(
							completedAssignRouteJson, getApplicationContext(),
							true);

					if (completedAssignedRoute != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "CompletedAssignedRoute " + i + ": "
									+ completedAssignedRoute.toString());
						}

						completedAssignedRoutes.add(completedAssignedRoute);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "CompletedAssignedRoute " + i
									+ " is null.");
						}
					}
				}

				for (AssignedRoute assignedRoute : assignedRoutes) {
					if (assignedRoute.getCached() == null) {
						assignedRoute.setCached(true);
					}

					assignedRoutesDao.save(assignedRoute);
				}

				for (AssignedRoute completedAssignedRoute : completedAssignedRoutes) {
					if (completedAssignedRoute.getCached() == null) {
						completedAssignedRoute.setCached(true);
					}
					assignedRoutesDao
							.saveCompletedAssignedRoute(completedAssignedRoute);
				}

				if (assignedRoutes.size() > 0) {
					return assignedRoutes.get(assignedRoutes.size() - 1)
							.getId();
				}

			} catch (MalformedURLException e) {
				Log.e(TAG, "Bad URL: " + e.toString(), e);
			} catch (IOException e) {
				Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),
								"Fetch failed due to network error.",
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

			if (result != null) {
				getSupportLoaderManager().restartLoader(0, null,
						RoutePlansActivity.this);
				oldestJobFetched = result;
			}

			setSupportProgressBarIndeterminateVisibility(false);
			loadMoreButton.setEnabled(true);

		}

	}

	private String getUrl(String before) {
		String serverBaseUrl = getString(R.string.server_base_url);
		Builder builder = Uri.parse(serverBaseUrl).buildUpon();

		builder.appendEncodedPath("service/assignedRoute/before/"
				+ settingsDao.getString("employeeId"));

		Utils.appendCommonQueryParameters(getApplicationContext(), builder);
		builder.appendQueryParameter("before", before);
		builder.appendQueryParameter("pageSize", "" + PAGE_SIZE);
		Uri syncUri = builder.build();
		return syncUri.toString();
	}

	private class CleanUpTask extends AsyncTask<String, Integer, Long> {

		@Override
		protected Long doInBackground(String... params) {
			assignedRoutesDao = AssignedRoutesDao
					.getInstance(getApplicationContext());
			long rowsAffected = assignedRoutesDao.deleteCachedAssignedRoutes();
			rowsAffected = assignedRoutesDao.deleteOldAssignedRoutes();

			return rowsAffected;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Fetch finished.");
			}

			// cleanupFinished = true;

			getSupportLoaderManager().initLoader(0, null,
					RoutePlansActivity.this);

		}
	}

}
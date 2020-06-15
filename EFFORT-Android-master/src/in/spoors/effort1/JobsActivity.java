package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.InvitationsDao;
import in.spoors.effort1.dao.JobTypesDao;
import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Customer;
import in.spoors.effort1.dto.Invitation;
import in.spoors.effort1.dto.Job;
import in.spoors.effort1.dto.JobType;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Customers;
import in.spoors.effort1.provider.EffortProvider.Invitations;
import in.spoors.effort1.provider.EffortProvider.Jobs;
import in.spoors.effort1.provider.EffortProvider.Settings;

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
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class JobsActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, TabListener,
		RefreshListener {

	public static final String TAG = "JobsActivity";

	private static final String INVITATIONS_TAB_TITLE = "Unassigned";
	private static final String JOBS_TAB_TITLE = "Assigned";
	private static final int JOBS_TAB_INDEX = 0;
	private static final int INVITATIONS_TAB_INDEX = 1;

	private static final int LOAD_JOBS = 0;
	private static final int LOAD_CUSTOMER = 1;
	private static final int LOAD_INVITATIONS = 2;

	public static final int PAGE_SIZE = 10;
	private SimpleCursorAdapter jobsAdapter;
	private SimpleCursorAdapter invitationsAdapter;
	private SimpleDateFormat timeFormat;
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat dateTimeFormatWithOutYear;
	private long localCustomerId;
	private JobsDao jobsDao;
	private InvitationsDao invitationsDao;
	private JobTypesDao jobTypesDao;
	private CustomersDao customersDao;
	private SettingsDao settingsDao;
	private Button loadMoreButton;
	private Long oldestJobFetched;
	private boolean showFutureItemsOnly;
	private List<JobType> types;
	private int numTypes;
	private boolean cleanupFinished;
	private JobsFragment jobsFragment;
	private InvitationsFragment invitationsFragment;
	private boolean showJobs;
	private boolean showInvitations;
	private DrawerFragment drawerFragment;
	private String singular;
	private String plural;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}

		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_jobs);
		setSupportProgressBarIndeterminateVisibility(false);
		jobsDao = JobsDao.getInstance(getApplicationContext());
		invitationsDao = InvitationsDao.getInstance(getApplicationContext());
		jobTypesDao = JobTypesDao.getInstance(getApplicationContext());
		customersDao = CustomersDao.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		timeFormat = Utils.getTimeFormat(getApplicationContext());
		dateFormat = Utils.getDateFormat(getApplicationContext());
		dateTimeFormatWithOutYear = Utils
				.getDateTimeFormatWithOutYear(getApplicationContext());
		loadMoreButton = (Button) findViewById(R.id.loadMoreButton);
		singular = settingsDao.getLabel(Settings.LABEL_JOB_SINGULAR_KEY,
				Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE);
		plural = settingsDao.getLabel(Settings.LABEL_JOB_PLURAL_KEY,
				Settings.LABEL_JOB_PLURAL_DEFAULT_VLAUE);

		if (savedInstanceState != null) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Has saved instance state.");
			}

			localCustomerId = savedInstanceState.getLong("localCustomerId");
			showFutureItemsOnly = savedInstanceState.getBoolean(
					"showFutureItemsOnly", true);
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Does not have saved instance state.");
			}

			localCustomerId = getIntent().getLongExtra("localCustomerId", 0);
			showFutureItemsOnly = true;
		}

		jobsFragment = (JobsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.jobsFragment);
		invitationsFragment = (InvitationsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.invitationsFragment);

		updateLoadMoreButtonText();

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);

		showJobs = settingsDao
				.getBoolean(Settings.KEY_PERMISSION_JOB_ADD, true)
				|| settingsDao.getBoolean(Settings.KEY_PERMISSION_JOB_VIEW,
						true)
				|| settingsDao.getBoolean(Settings.KEY_PERMISSION_JOB_MODIFY,
						true);

		String title = null;

		if (showJobs) {
			title = plural;
		} else {
			title = INVITATIONS_TAB_TITLE + " " + plural;
		}

		actionBar.setTitle(title);

		// initialize the drawerFragment before showing the tabs
		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_jobs, title, null,
				this);

		showInvitations = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_INVITATION_ADD, true)
				|| settingsDao.getBoolean(
						Settings.KEY_PERMISSION_INVITATION_VIEW, true)
				|| settingsDao.getBoolean(
						Settings.KEY_PERMISSION_INVITATION_MODIFY, true);

		if (showJobs && showInvitations) {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			Tab jobsTab = actionBar.newTab();
			jobsTab.setText(JOBS_TAB_TITLE);
			jobsTab.setTabListener(this);
			actionBar.addTab(jobsTab);

			Tab invitationsTab = actionBar.newTab();
			invitationsTab.setText(INVITATIONS_TAB_TITLE);
			invitationsTab.setTabListener(this);
			actionBar.addTab(invitationsTab);
		}

		new CleanUpTask().execute();

		if (showJobs) {
			TextView textView = (TextView) jobsFragment.getView().findViewById(
					android.R.id.empty);
			textView.setText("No " + plural + ".");
			ListView jobsListView = (ListView) jobsFragment.getView()
					.findViewById(android.R.id.list);
			jobsListView.setOnItemClickListener(this);
			String[] jobColumns = { EffortProvider.Jobs.START_TIME,
					EffortProvider.Jobs.TITLE, EffortProvider.Jobs.END_TIME,
					EffortProvider.Jobs.JOB_TYPE_ID,
					EffortProvider.Jobs.COMPLETED,
					EffortProvider.Jobs.TREE_DIRTY };
			int[] jobViews = { R.id.dateTextView, R.id.titleTextView,
					R.id.timeTextView, R.id.extraInfoTextView,
					R.id.typeImageView, R.id.syncImageView };

			jobsAdapter = new SimpleCursorAdapter(getApplicationContext(),
					R.layout.list_item_job, null, jobColumns, jobViews,
					SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			jobsAdapter.setViewBinder(new JobsViewBinder());
			jobsListView.setAdapter(jobsAdapter);

			if (!showInvitations) {
				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				ft.hide(invitationsFragment);
				ft.commit();
			}
		}

		if (showInvitations) {
			TextView textView = (TextView) invitationsFragment.getView()
					.findViewById(android.R.id.empty);
			textView.setText("No " + singular + " invitations.");
			ListView invitationsListView = (ListView) invitationsFragment
					.getView().findViewById(android.R.id.list);
			invitationsListView.setOnItemClickListener(this);
			String[] invitationColumns = {
					EffortProvider.Invitations.START_TIME,
					EffortProvider.Invitations.TITLE,
					EffortProvider.Invitations.END_TIME,
					EffortProvider.Invitations.JOB_TYPE_ID,
					EffortProvider.Invitations.ACCEPTED };
			int[] invitationViews = { R.id.dateTextView, R.id.titleTextView,
					R.id.timeTextView, R.id.extraInfoTextView,
					R.id.typeImageView };

			invitationsAdapter = new SimpleCursorAdapter(
					getApplicationContext(), R.layout.list_item_invitation,
					null, invitationColumns, invitationViews,
					SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			invitationsAdapter.setViewBinder(new InvitationsViewBinder());
			invitationsListView.setAdapter(invitationsAdapter);

			if (!showJobs) {
				FragmentTransaction ft = getSupportFragmentManager()
						.beginTransaction();
				ft.hide(jobsFragment);
				ft.commit();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.jobs, menu);
		ActionBar actionBar = getSupportActionBar();

		types = jobTypesDao.getJobTypes();
		numTypes = types.size();

		if (showJobs
				&& ((actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS && actionBar
						.getSelectedNavigationIndex() == JOBS_TAB_INDEX) || actionBar
						.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS)) {
			menu.setGroupVisible(R.id.jobsGroup, drawerFragment != null
					&& !drawerFragment.isDrawerOpen());

			boolean addJob = settingsDao.getBoolean(
					Settings.KEY_PERMISSION_JOB_ADD, true);
			menu.findItem(R.id.addJob).setVisible(addJob);
			menu.findItem(R.id.addJob).setTitle("Add " + singular);

			boolean viewJob = settingsDao.getBoolean(
					Settings.KEY_PERMISSION_JOB_VIEW, true);
			menu.findItem(R.id.markAllJobsAsRead).setVisible(viewJob);
			menu.findItem(R.id.markAllJobsAsUnread).setVisible(viewJob);
			menu.findItem(R.id.jobActions).setVisible(
					drawerFragment != null && !drawerFragment.isDrawerOpen()
							&& (addJob || viewJob));

			if (numTypes > 1) {
				SubMenu subMenu = menu.addSubMenu(0, -1, 100, "Filter");
				MenuItem filterMenuItem = subMenu.getItem();
				filterMenuItem.setIcon(R.drawable.ic_content_filter);
				MenuItemCompat.setShowAsAction(filterMenuItem,
						MenuItemCompat.SHOW_AS_ACTION_ALWAYS
								| MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);

				int order = 0;
				for (JobType type : types) {
					MenuItem menuItem = subMenu.add(0, type.getId(), order,
							type.getName());
					menuItem.setCheckable(true);
					menuItem.setChecked(jobTypesDao.getChecked(type.getName()));
					order = order + 1;
				}

				filterMenuItem.setVisible(drawerFragment != null
						&& !drawerFragment.isDrawerOpen());
			}
		} else {
			menu.setGroupVisible(R.id.jobsGroup, false);
		}

		if (showInvitations
				&& ((actionBar.getNavigationMode() == ActionBar.NAVIGATION_MODE_TABS && actionBar
						.getSelectedNavigationIndex() == INVITATIONS_TAB_INDEX) || actionBar
						.getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS)) {
			menu.setGroupVisible(R.id.invitationsGroup, drawerFragment != null
					&& !drawerFragment.isDrawerOpen());

			if (numTypes > 1) {
				SubMenu subMenu = menu.addSubMenu(0, -1, 100, "Filter");
				MenuItem filterMenuItem = subMenu.getItem();
				filterMenuItem.setIcon(R.drawable.ic_content_filter);
				MenuItemCompat.setShowAsAction(filterMenuItem,
						MenuItemCompat.SHOW_AS_ACTION_ALWAYS
								| MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);

				for (JobType type : types) {
					MenuItem menuItem = subMenu.add(0, type.getId(),
							type.getId(), type.getName());
					menuItem.setCheckable(true);
					menuItem.setChecked(jobTypesDao.getChecked(type.getName()));
				}

				filterMenuItem.setVisible(drawerFragment != null
						&& !drawerFragment.isDrawerOpen());
			}
		} else {
			menu.setGroupVisible(R.id.invitationsGroup, false);
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();

		if (cleanupFinished) {
			if (showJobs) {
				getSupportLoaderManager().restartLoader(LOAD_JOBS, null, this);
				if (localCustomerId != 0) {
					getSupportLoaderManager().restartLoader(LOAD_CUSTOMER,
							null, this);
				}
			}

			if (showInvitations) {
				getSupportLoaderManager().restartLoader(LOAD_INVITATIONS, null,
						this);
			}
		}

		jobsDao.updateTreeDirtyFlags();
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("localCustomerId", localCustomerId);
		outState.putBoolean("showFutureItemsOnly", showFutureItemsOnly);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader.");
		}

		String today = SQLiteDateTimeUtils
				.getSQLiteDateTimeForBeginningOfToday();

		switch (id) {
		case LOAD_JOBS:
			boolean showCompletedJobs = settingsDao.getBoolean(
					Settings.KEY_SHOW_COMPLETED_JOBS, true);

			String completedFilter = showCompletedJobs ? "" : " AND ("
					+ Jobs.COMPLETED + " IS NULL OR " + Jobs.COMPLETED
					+ " = 'false')";

			return new CursorLoader(getApplicationContext(), Jobs.CONTENT_URI,
					Jobs.ALL_COLUMNS, Jobs.TEMPORARY
							+ " = 'false' AND "
							+ Jobs.JOB_TYPE_ID
							+ " IN "
							+ jobTypesDao.getCheckedTypesIn()
							+ (showFutureItemsOnly ? " AND (" + Jobs.END_TIME
									+ " >= '" + today + "' OR "
									+ Jobs.LOCAL_MODIFICATION_TIME + " >= '"
									+ today + "')" : "")
							+ (localCustomerId != 0 ? " AND "
									+ Jobs.LOCAL_CUSTOMER_ID + " = "
									+ localCustomerId : "") + completedFilter,
					null, Jobs.START_TIME);

		case LOAD_CUSTOMER:
			return new CursorLoader(getApplicationContext(),
					Customers.CONTENT_URI, Customers.SUMMARY_COLUMNS,
					Customers._ID + " = " + localCustomerId, null, null);

		case LOAD_INVITATIONS:
			return new CursorLoader(getApplicationContext(),
					Invitations.CONTENT_URI, Invitations.ALL_COLUMNS, null,
					null, Invitations.START_TIME);

		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished for loader " + loader.getId());
		}

		switch (loader.getId()) {
		case LOAD_JOBS:
			if (showJobs) {
				jobsAdapter.swapCursor(cursor);
			}
			break;

		case LOAD_INVITATIONS:
			if (showInvitations) {
				invitationsAdapter.swapCursor(cursor);
			}
			break;

		case LOAD_CUSTOMER:
			if (cursor.moveToNext()) {
				ActionBar actionBar = getSupportActionBar();
				actionBar.setSubtitle(cursor
						.getString(Customers.SUMMARY_NAME_INDEX));
			}
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoaderReset for loader " + loader.getId());
		}

		// swapCursor method requires API level 11 or higher.
		// Thus, use SimpleCursorAdapter from support library.
		switch (loader.getId()) {
		case LOAD_JOBS:
			if (showJobs) {
				jobsAdapter.swapCursor(null);
			}

			break;
		case LOAD_INVITATIONS:
			if (showInvitations) {
				invitationsAdapter.swapCursor(null);
			}

			break;
		}
	}

	class JobsViewBinder implements ViewBinder {

		@SuppressWarnings("unused")
		private final String TAG = "JobsActivity.JobsViewBinder";

		public boolean isSameDayAsPrevRow(Cursor cursor) {
			int originalPosition = cursor.getPosition();

			if (originalPosition <= 0) {
				return false;
			}

			String curDate = cursor.getString(Jobs.START_TIME_INDEX);
			cursor.moveToPrevious();
			String prevDate = cursor.getString(Jobs.START_TIME_INDEX);
			cursor.moveToPosition(originalPosition);

			return Utils.isSameDay(curDate, prevDate);
		}

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
			// + cursor.getPosition() + ", columnIndex=" + columnIndex);

			switch (columnIndex) {
			case Jobs.TITLE_INDEX:
				TextView titleTV = (TextView) view;
				boolean read = jobsDao.getRead(cursor.getLong(Jobs._ID_INDEX));

				if (read) {
					titleTV.setText(cursor.getString(Jobs.TITLE_INDEX));
				} else {
					titleTV.setText(Html.fromHtml("<b>"
							+ cursor.getString(Jobs.TITLE_INDEX) + "</b>"));
				}

				titleTV.setTextColor(0xff000000);

				// braces used only for creating a local scope
				{
					Job job = jobsDao.getJobWithLocalId(cursor
							.getLong(Jobs._ID_INDEX));

					if (job != null
							&& !(job.getCompleted() != null && job
									.getCompleted())) {
						Date now = new Date();

						// overdue
						if (now.after(job.getEndTime())) {
							titleTV.setTextColor(0xffff0000);
						}
					}
				}

				return true;

			case Jobs.START_TIME_INDEX:
				if (isSameDayAsPrevRow(cursor)) {
					view.setVisibility(View.GONE);
				} else {
					Date startTime = SQLiteDateTimeUtils.getLocalTime(cursor
							.getString(Jobs.START_TIME_INDEX));

					StringBuffer sb = new StringBuffer();

					if (DateUtils.isToday(startTime.getTime())) {
						sb.append("Today, ");
					}

					sb.append(new StringBuffer(dateFormat.format(startTime)));

					TextView tv = (TextView) view;
					tv.setText(sb.toString());
					view.setVisibility(View.VISIBLE);
				}

				return true;

			case Jobs.END_TIME_INDEX:
				Date startTime = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Jobs.START_TIME_INDEX));
				Date endTime = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Jobs.END_TIME_INDEX));

				TextView tv = (TextView) view;

				if (Utils.isSameDay(startTime, endTime)) {
					tv.setText(timeFormat.format(startTime) + " - "
							+ timeFormat.format(endTime));
				} else {
					tv.setText(dateTimeFormatWithOutYear.format(startTime)
							+ " - " + dateTimeFormatWithOutYear.format(endTime));
				}

				return true;

			case Jobs.JOB_TYPE_ID_INDEX:
				TextView extraInfoTV = (TextView) view;
				extraInfoTV.setText(jobTypesDao.getName(cursor
						.getInt(Jobs.JOB_TYPE_ID_INDEX)));
				// TODO: Display stage name
				return true;

			case Jobs.COMPLETED_INDEX:
				ImageView imageView = (ImageView) view;
				Job job = jobsDao.getJobWithLocalId(cursor
						.getLong(Jobs._ID_INDEX));

				if (job != null) {
					if (job.getCompleted() != null && job.getCompleted()) {
						imageView
								.setImageResource(R.drawable.ic_agenda_job_complete);
					} else {
						Date now = new Date();

						if (now.after(job.getEndTime())) {
							imageView
									.setImageResource(R.drawable.ic_agenda_job_overdue);
						} else {
							imageView
									.setImageResource(R.drawable.ic_agenda_job);
						}
					}
				} else {
					imageView.setImageResource(R.drawable.ic_agenda_job);
				}

				return true;

			case Jobs.TREE_DIRTY_INDEX:
				ImageView syncImageView = (ImageView) view;

				if ("true".equals(cursor.getString(Jobs.TREE_DIRTY_INDEX))) {
					syncImageView.setImageResource(R.drawable.ic_sync_pending);
					syncImageView.setVisibility(View.VISIBLE);
				} else {
					syncImageView.setVisibility(View.GONE);
				}

				return true;

			default:
				return false;
			}
		}
	}

	private void updateLoadMoreButtonText() {
		int oldJobs = jobsDao.getNumberOfJobsOlderThanToday();

		if (showFutureItemsOnly && oldJobs > 0) {
			loadMoreButton.setText("Show all " + plural + " stored locally");
		} else {
			loadMoreButton.setText("Fetch more " + plural + " from cloud");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// Log.i(TAG,
		// "Parent: " + parent.toString() + ", View: " + view.toString()
		// + ", Position: " + position + ", ID: " + id);

		if (showJobs && showInvitations) {
			int tabId = getSupportActionBar().getSelectedNavigationIndex();
			Intent intent = null;

			switch (tabId) {
			case JOBS_TAB_INDEX:
				intent = new Intent(this, JobActivity.class);
				intent.putExtra(EffortProvider.Jobs._ID, id);
				startActivity(intent);
				break;

			case INVITATIONS_TAB_INDEX:
				intent = new Intent(this, InvitationActivity.class);
				intent.putExtra(EffortProvider.Invitations._ID, id);
				startActivity(intent);
				break;
			}
		} else {
			if (showJobs) {
				Intent intent = new Intent(this, JobActivity.class);
				intent.putExtra(EffortProvider.Jobs._ID, id);
				startActivity(intent);
			}

			if (showInvitations) {
				Intent intent = new Intent(this, InvitationActivity.class);
				intent.putExtra(EffortProvider.Invitations._ID, id);
				startActivity(intent);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}

		int itemId = item.getItemId();

		if (itemId == android.R.id.home) {
			onBackPressed();
		} else if (itemId == R.id.addJob) {
			Intent intent = new Intent(this, JobActivity.class);
			intent.putExtra(EffortProvider.Jobs._ID, 0L);
			intent.putExtra("localCustomerId", localCustomerId);
			startActivity(intent);
		} else if (itemId == R.id.markAllJobsAsRead) {
			jobsDao.updateReadFlag(true);
			if (cleanupFinished) {
				getSupportLoaderManager().restartLoader(LOAD_JOBS, null, this);
			}
		} else if (itemId == R.id.markAllJobsAsUnread) {
			jobsDao.updateReadFlag(false);
			if (cleanupFinished) {
				getSupportLoaderManager().restartLoader(LOAD_JOBS, null, this);
			}
		} else if (itemId == R.id.markAllInvitationsAsRead) {
			invitationsDao.updateReadFlag(true);
			getSupportLoaderManager().restartLoader(LOAD_INVITATIONS, null,
					this);
		} else if (itemId == R.id.markAllInvitationsAsUnread) {
			invitationsDao.updateReadFlag(false);
			getSupportLoaderManager().restartLoader(LOAD_INVITATIONS, null,
					this);
		} else {
			if (numTypes > 1) {
				for (JobType type : types) {
					if (itemId == type.getId()) {
						item.setChecked(!item.isChecked());
						jobTypesDao.saveChecked(type.getName(),
								item.isChecked());
						getSupportLoaderManager().restartLoader(LOAD_JOBS,
								null, this);
					}
				}
			}
		}

		return super.onOptionsItemSelected(item);
	}

	public void onLoadMoreButtonClick(View view) {
		int oldJobs = jobsDao.getNumberOfJobsOlderThanToday();

		if (showFutureItemsOnly && oldJobs > 0) {
			showFutureItemsOnly = false;

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Loading all local jobs.");
			}

			getSupportLoaderManager().restartLoader(LOAD_JOBS, null, this);
			updateLoadMoreButtonText();
		} else {
			setSupportProgressBarIndeterminateVisibility(true);

			// if the job no longer exists in our local db, start from today
			String before = XsdDateTimeUtils.getXsdDateTimeFromLocalTime(Utils
					.getBeginningOfToday());

			if (oldestJobFetched != null) {
				String oldestJobStartTime = jobsDao
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
			new FetchPreviousJobsTask().execute(before);
		}
	}

	private class FetchPreviousJobsTask extends
			AsyncTask<String, Integer, Long> {
		private List<Customer> addedCustomers = new ArrayList<Customer>();
		private List<Job> addedJobs = new ArrayList<Job>();

		@Override
		protected Long doInBackground(String... params) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In doInBackground.");
			}

			settingsDao = SettingsDao.getInstance(getApplicationContext());
			AndroidHttpClient httpClient = null;

			try {
				String url = getUrl(params[0]);

				if (url == null) {
					return null;
				}

				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Fetch previous jobs URL: " + url);
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

				if (!(obj instanceof JSONArray)) {
					Log.e(TAG,
							"Invalid previous jobs response. Expected a JSON array but did not get it.");
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

				JSONArray jsonArray = (JSONArray) obj;
				jobsDao = JobsDao.getInstance(getApplicationContext());

				int length = jsonArray.length();
				if (length <= 0) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									getApplicationContext(),
									"There are no more old " + plural
											+ " on the cloud.",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				for (int i = 0; i < jsonArray.length(); ++i) {
					JSONObject jobJson = jsonArray.getJSONObject(i);

					Job job = Job.parse(jobJson, getApplicationContext());

					if (job != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Job " + i + ": " + job.toString());
						}

						addedJobs.add(job);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "Job " + i + " is null.");
						}
					}

					// if the job has a customer object inside it, add that
					// customer to customer list
					if (!jobJson.isNull("customer")) {
						Customer customer = Customer.parse(
								jobJson.getJSONObject("customer"),
								getApplicationContext());
						if (customer != null) {
							addedCustomers.add(customer);
						}
					}
				}

				for (Customer customer : addedCustomers) {
					customersDao.save(customer);
				}

				for (Job job : addedJobs) {
					if (job.getCached() == null) {
						job.setCached(true);
					}

					jobsDao.save(job);
				}

				if (addedJobs.size() > 0) {
					return addedJobs.get(addedJobs.size() - 1).getRemoteId();
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
				getSupportLoaderManager().restartLoader(LOAD_JOBS, null,
						JobsActivity.this);
				oldestJobFetched = result;
			}

			setSupportProgressBarIndeterminateVisibility(false);
			loadMoreButton.setEnabled(true);
		}

		private String getUrl(String before) {
			String serverBaseUrl = getString(R.string.server_base_url);
			Builder builder = Uri.parse(serverBaseUrl).buildUpon();

			if (localCustomerId != 0) {
				Long remoteCustomerId = customersDao
						.getRemoteId(localCustomerId);

				if (remoteCustomerId == null) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									JobsActivity.this,
									settingsDao
											.getLabel(
													Settings.LABEL_CUSTOMER_SINGULAR_KEY,
													Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE)
											+ " has not yet been synced. Hence, there cannot be any "
											+ plural
											+ " for this "
											+ settingsDao
													.getLabel(
															Settings.LABEL_CUSTOMER_SINGULAR_KEY,
															Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE)
											+ " on the server.",
									Toast.LENGTH_LONG).show();
						}
					});

					return null;
				}

				builder.appendEncodedPath("service/customer/history/"
						+ remoteCustomerId + "/"
						+ settingsDao.getString("employeeId"));
			} else {
				builder.appendEncodedPath("service/visit/before/"
						+ settingsDao.getString("employeeId"));

			}

			Utils.appendCommonQueryParameters(getApplicationContext(), builder);
			builder.appendQueryParameter("before", before);
			builder.appendQueryParameter("pageSize", "" + PAGE_SIZE);
			Uri syncUri = builder.build();
			return syncUri.toString();
		}
	}

	@Override
	public void onBackPressed() {
		// if (getIntent().getBooleanExtra("launchedFromNotification", false)) {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		// }

		super.onBackPressed();
	}

	private class CleanUpTask extends AsyncTask<String, Integer, Long> {

		@Override
		protected Long doInBackground(String... params) {
			long rowsAffected = jobsDao.deleteCachedJobs();
			rowsAffected += jobsDao.deleteOldJobs();

			return rowsAffected;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Fetch finished.");
			}

			cleanupFinished = true;

			if (showJobs) {
				getSupportLoaderManager().initLoader(LOAD_JOBS, null,
						JobsActivity.this);

				if (localCustomerId != 0) {
					getSupportLoaderManager().initLoader(LOAD_CUSTOMER, null,
							JobsActivity.this);
				}
			}

			if (showInvitations) {
				getSupportLoaderManager().initLoader(LOAD_INVITATIONS, null,
						JobsActivity.this);
			}
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onTabSelected: " + tab.getText());
		}

		if (JOBS_TAB_TITLE.equals(tab.getText())) {
			ft.hide(invitationsFragment);
			ft.show(jobsFragment);
		} else if (INVITATIONS_TAB_TITLE.equals(tab.getText())) {
			ft.hide(jobsFragment);
			ft.show(invitationsFragment);
		}

		supportInvalidateOptionsMenu();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onTabUnselected: " + tab.getText());
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onTabReselected: " + tab.getText());
		}

		supportInvalidateOptionsMenu();
	}

	class InvitationsViewBinder implements ViewBinder {

		@SuppressWarnings("unused")
		private final String TAG = "JobsActivity.InvitationsViewBinder";

		public boolean isSameDayAsPrevRow(Cursor cursor) {
			int originalPosition = cursor.getPosition();

			if (originalPosition <= 0) {
				return false;
			}

			String curDate = cursor.getString(Invitations.START_TIME_INDEX);
			cursor.moveToPrevious();
			String prevDate = cursor.getString(Invitations.START_TIME_INDEX);
			cursor.moveToPosition(originalPosition);

			return Utils.isSameDay(curDate, prevDate);
		}

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
			// + cursor.getPosition() + ", columnIndex=" + columnIndex);

			switch (columnIndex) {
			case Invitations.TITLE_INDEX:
				TextView titleTV = (TextView) view;
				boolean read = invitationsDao.getRead(cursor
						.getLong(Invitations._ID_INDEX));

				if (read) {
					titleTV.setText(cursor.getString(Invitations.TITLE_INDEX));
				} else {
					titleTV.setText(Html.fromHtml("<b>"
							+ cursor.getString(Invitations.TITLE_INDEX)
							+ "</b>"));
				}

				return true;

			case Invitations.START_TIME_INDEX:
				if (isSameDayAsPrevRow(cursor)) {
					view.setVisibility(View.GONE);
				} else {
					Date startTime = SQLiteDateTimeUtils.getLocalTime(cursor
							.getString(Invitations.START_TIME_INDEX));

					StringBuffer sb = new StringBuffer();

					if (DateUtils.isToday(startTime.getTime())) {
						sb.append("Today, ");
					}

					sb.append(new StringBuffer(dateFormat.format(startTime)));

					TextView tv = (TextView) view;
					tv.setText(sb.toString());
					view.setVisibility(View.VISIBLE);
				}

				return true;

			case Invitations.END_TIME_INDEX:
				Date startTime = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Invitations.START_TIME_INDEX));
				Date endTime = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Invitations.END_TIME_INDEX));

				TextView tv = (TextView) view;

				if (Utils.isSameDay(startTime, endTime)) {
					tv.setText(timeFormat.format(startTime) + " - "
							+ timeFormat.format(endTime));
				} else {
					tv.setText(dateTimeFormatWithOutYear.format(startTime)
							+ " - " + dateTimeFormatWithOutYear.format(endTime));
				}

				return true;

			case Invitations.JOB_TYPE_ID_INDEX:
				TextView extraInfoTV = (TextView) view;
				extraInfoTV.setText(jobTypesDao.getName(cursor
						.getInt(Invitations.JOB_TYPE_ID_INDEX)));

				return true;

			case Invitations.ACCEPTED_INDEX:
				ImageView imageView = (ImageView) view;
				Invitation invitation = invitationsDao
						.getInvitationWithId(cursor
								.getLong(Invitations._ID_INDEX));

				if (invitation != null) {
					if (invitation.getAccepted() != null
							&& invitation.getAccepted()) {
						imageView
								.setImageResource(R.drawable.ic_agenda_job_complete);
					} else {
						Date now = new Date();

						if (now.after(invitation.getEndTime())) {
							imageView
									.setImageResource(R.drawable.ic_agenda_job_overdue);
						} else {
							imageView
									.setImageResource(R.drawable.ic_agenda_job);
						}
					}
				} else {
					imageView.setImageResource(R.drawable.ic_agenda_job);
				}

				return true;

			default:
				return false;
			}
		}
	}

	@Override
	public void onRefresh() {
		jobsDao.updateTreeDirtyFlags();

		if (cleanupFinished) {
			getSupportLoaderManager().restartLoader(LOAD_JOBS, null,
					JobsActivity.this);
			getSupportLoaderManager().restartLoader(LOAD_INVITATIONS, null,
					JobsActivity.this);
		}
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

}

package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.AssignedRoutesDao;
import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dao.SpecialWorkingHoursDao;
import in.spoors.effort1.dto.AssignedRoute;
import in.spoors.effort1.dto.Job;
import in.spoors.effort1.dto.SpecialWorkingHour;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Agenda;
import in.spoors.effort1.provider.EffortProvider.Customers;
import in.spoors.effort1.provider.EffortProvider.Leaves;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AgendaActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, RefreshListener {

	public static final String TAG = "AgendaActivity";

	private ListView listView;
	private SimpleCursorAdapter adapter;
	private SimpleDateFormat timeFormat;
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat dateTimeFormatWithOutYear;

	private DrawerFragment drawerFragment;
	private SpecialWorkingHoursDao swhDao;
	private JobsDao jobsDao;
	private AssignedRoutesDao asignRoutesDao;
	private SettingsDao settingsDao;
	private StatusFragment statusFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}

		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_agenda);
		setSupportProgressBarIndeterminateVisibility(false);
		timeFormat = Utils.getTimeFormat(getApplicationContext());
		dateFormat = Utils.getDateFormat(getApplicationContext());
		dateTimeFormatWithOutYear = Utils
				.getDateTimeFormatWithOutYear(getApplicationContext());
		swhDao = SpecialWorkingHoursDao.getInstance(getApplicationContext());
		jobsDao = JobsDao.getInstance(getApplicationContext());
		asignRoutesDao = AssignedRoutesDao.getInstance(getApplicationContext());

		settingsDao = SettingsDao.getInstance(getApplicationContext());

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		String title = settingsDao.getLabel(Settings.LABEL_AGENDA_PLURAL_KEY,
				Settings.LABEL_AGENDA_PLURAL_DEFAULT_VALUE);
		actionBar.setTitle(title);

		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		emptyTextView.setText("No "
				+ settingsDao.getLabel(Settings.LABEL_AGENDA_SINGULAR_KEY,
						Settings.LABEL_AGENDA_SINGULAR_DEFAULT_VALUE)
				+ " events.");

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);
		getSupportLoaderManager().initLoader(0, null, this);

		String[] columns = { Agenda.TYPE, Agenda.START_TIME, Agenda.TITLE,
				Agenda.END_TIME, Agenda.TYPE, Agenda.EXTRA_INFO,
				Agenda.TREE_DIRTY };
		int[] views = { R.id.typeImageView, R.id.dateTextView,
				R.id.titleTextView, R.id.timeTextView, R.id.nextImageView,
				R.id.extraInfoTextView, R.id.syncImageView };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_agenda, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);
		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_agenda, title, null,
				this);
		statusFragment = (StatusFragment) getSupportFragmentManager()
				.findFragmentById(R.id.statusFragment);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.agenda, menu);
		boolean addCustomer = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_CUSTOMER_ADD, true);
		boolean addJob = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_JOB_ADD, true);
		boolean addForm = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_FORM_ADD, true);
		boolean addLeave = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_LEAVE_ADD, true);

		menu.findItem(R.id.addCustomer).setVisible(addCustomer);
		menu.findItem(R.id.addJob).setVisible(addJob);
		menu.findItem(R.id.submitForm).setVisible(addForm);
		menu.findItem(R.id.applyForLeave).setVisible(addLeave);

		boolean viewJob = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_JOB_VIEW, true);
		menu.findItem(R.id.markAllJobsAsRead).setVisible(viewJob);
		menu.findItem(R.id.markAllJobsAsUnread).setVisible(viewJob);

		menu.findItem(R.id.submitForm).setTitle(
				"Submit "
						+ settingsDao.getLabel(
								Settings.LABEL_FORM_SINGULAR_KEY,
								Settings.LABEL_FORM_SINGULAR_DEFAULT_VLAUE));
		menu.findItem(R.id.addJob).setTitle(
				"Add "
						+ settingsDao.getLabel(Settings.LABEL_JOB_SINGULAR_KEY,
								Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE));
		menu.findItem(R.id.addCustomer)
				.setTitle(
						"Add "
								+ settingsDao
										.getLabel(
												Settings.LABEL_CUSTOMER_SINGULAR_KEY,
												Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE));

		menu.findItem(R.id.actions)
				.setVisible(
						!drawerFragment.isDrawerOpen()
								&& (addCustomer || addJob || addForm
										|| addLeave || viewJob));

		if (Utils.isSyncInProgress(getApplicationContext())) {
			menu.findItem(R.id.sync).setVisible(false);
			setSupportProgressBarIndeterminateVisibility(true);
		} else {
			menu.findItem(R.id.sync).setVisible(!drawerFragment.isDrawerOpen());
			setSupportProgressBarIndeterminateVisibility(false);
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
		jobsDao.updateTreeDirtyFlags();
		getSupportLoaderManager().restartLoader(0, null, this);
		statusFragment.updateStatus();
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

		return new CursorLoader(getApplicationContext(), Agenda.CONTENT_URI,
				Agenda.ALL_COLUMNS, null, null, null);
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

		// private final String TAG = "JobsActivity.MyViewBinder";

		public boolean isSameDayAsPrevRow(Cursor cursor) {
			int originalPosition = cursor.getPosition();

			if (originalPosition <= 0) {
				return false;
			}

			String curDate = cursor.getString(Agenda.START_TIME_INDEX);
			cursor.moveToPrevious();
			String prevDate = cursor.getString(Agenda.START_TIME_INDEX);
			cursor.moveToPosition(originalPosition);

			return Utils.isSameDay(curDate, prevDate);
		}

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
			// + cursor.getPosition() + ", columnIndex=" + columnIndex);
			int type = cursor.getInt(Agenda.TYPE_INDEX);

			switch (columnIndex) {
			case Agenda.TITLE_INDEX:
				TextView titleTV = (TextView) view;

				if (type == Agenda.TYPE_JOB) {
					boolean read = jobsDao.getRead(cursor
							.getLong(Agenda._ID_INDEX) - Agenda.JOB_ID_OFFSET);

					if (read) {
						titleTV.setText(cursor.getString(Agenda.TITLE_INDEX));
					} else {
						titleTV.setText(Html.fromHtml("<b>"
								+ cursor.getString(Agenda.TITLE_INDEX) + "</b>"));
					}

					titleTV.setTextColor(0xff000000);

					Job job = jobsDao.getJobWithLocalId(cursor
							.getLong(Agenda._ID_INDEX) - Agenda.JOB_ID_OFFSET);

					if (job != null
							&& !(job.getCompleted() != null && job
									.getCompleted())) {
						Date now = new Date();

						// overdue
						if (now.after(job.getEndTime())) {
							titleTV.setTextColor(0xffff0000);
						}
					}
				} else {
					titleTV.setText(cursor.getString(Agenda.TITLE_INDEX));
				}
				// titleTV.setTypeface(titleTV.getTypeface(),
				// read ? Typeface.NORMAL : Typeface.BOLD);
				// } else {
				// titleTV.setTypeface(titleTV.getTypeface(), Typeface.NORMAL);
				// }

				return true;

			case Agenda.TYPE_INDEX:
				if (view.getId() == R.id.typeImageView) {
					ImageView imageView = (ImageView) view;

					if (type == Agenda.TYPE_JOB) {
						Job job = jobsDao.getJobWithLocalId(cursor
								.getLong(Agenda._ID_INDEX)
								- Agenda.JOB_ID_OFFSET);

						if (job != null) {
							if (job.getCompleted() != null
									&& job.getCompleted()) {
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
							imageView
									.setImageResource(R.drawable.ic_agenda_job);
						}
					} else if (type == Agenda.TYPE_HOLIDAY) {
						imageView
								.setImageResource(R.drawable.ic_agenda_holiday);
					} else if (type == Agenda.TYPE_LEAVE) {
						imageView.setImageResource(R.drawable.ic_agenda_leave);
					} else if (type == Agenda.TYPE_SPECIAL_WORKING_DAY) {
						imageView.setImageResource(R.drawable.ic_agenda_swd);
					} else if (type == Agenda.TYPE_ASSIGNED_ROUTE) {
						AssignedRoute assignedRoute = asignRoutesDao
								.getAssignedRouteWithId(cursor
										.getLong(Agenda._ID_INDEX)
										- Agenda.ASSIGNED_ROUTE_ID_OFFSET);
						/*
						 * CompletedAssignedRoute cmpAsnRoute =
						 * completedAssignedRoutesDao
						 * .getCompletedActivityWithRemoteId(assignedRoute
						 * .getId());
						 */

						if (assignedRoute != null
								&& assignedRoute.getStatus() != null
								&& assignedRoute.getStatus() == AssignedRoute.STATUS_COMPLETED) {
							imageView
									.setImageResource(R.drawable.ic_drawer_completed_route);
						} else {
							Date now = new Date();

							if (now.after(assignedRoute.getEndDate())) {
								imageView
										.setImageResource(R.drawable.route_overdue);
							} else {
								imageView
										.setImageResource(R.drawable.ic_drawer_routeplan);
							}
						}

					}
				} else if (view.getId() == R.id.nextImageView) {
					if (type == Agenda.TYPE_JOB || type == Agenda.TYPE_LEAVE
							|| type == Agenda.TYPE_ASSIGNED_ROUTE) {
						view.setVisibility(View.VISIBLE);
					} else {
						view.setVisibility(View.GONE);
					}
				}

				return true;

			case Agenda.START_TIME_INDEX:
				if (isSameDayAsPrevRow(cursor)) {
					view.setVisibility(View.GONE);
				} else {
					Date startTime = SQLiteDateTimeUtils.getLocalTime(cursor
							.getString(Agenda.START_TIME_INDEX));

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

			case Agenda.END_TIME_INDEX:
				Date startTime = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Agenda.START_TIME_INDEX));
				Date endTime = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Agenda.END_TIME_INDEX));

				TextView tv = (TextView) view;
				view.setVisibility(View.VISIBLE);

				if (type == Agenda.TYPE_JOB || type == Agenda.TYPE_LEAVE) {
					// display range
					if (Utils.isSameDay(startTime, endTime)) {
						tv.setText(timeFormat.format(startTime) + " - "
								+ timeFormat.format(endTime));
					} else {
						tv.setText(dateTimeFormatWithOutYear.format(startTime)
								+ " - "
								+ dateTimeFormatWithOutYear.format(endTime));
					}
				} else if (type == Agenda.TYPE_ASSIGNED_ROUTE) {
					tv.setText(dateFormat.format(startTime) + " - "
							+ dateFormat.format(endTime));
				} else {
					// date doesn't add any value, so display description if
					// it's available
					String description = cursor
							.getString(Agenda.DESCRIPTION_INDEX);

					if (TextUtils.isEmpty(description)) {
						// tv.setText(dateFormat.format(startTime));
						if (type == Agenda.TYPE_HOLIDAY) {
							tv.setText("Holiday");
						} else if (type == Agenda.TYPE_SPECIAL_WORKING_DAY) {
							tv.setText("Special Working Day");
						}
					} else {
						tv.setText(description);
					}
				}

				return true;

			case Agenda.EXTRA_INFO_INDEX:
				TextView extraInfoTV = (TextView) view;

				if (type == Agenda.TYPE_JOB) {
					extraInfoTV.setText(cursor
							.getString(Agenda.EXTRA_INFO_INDEX));
					extraInfoTV.setVisibility(View.VISIBLE);
				} else if (type == Agenda.TYPE_ASSIGNED_ROUTE) {
					extraInfoTV.setText(cursor
							.getString(Agenda.EXTRA_INFO_INDEX));
					extraInfoTV.setVisibility(View.VISIBLE);
				} else if (type == Agenda.TYPE_SPECIAL_WORKING_DAY) {
					long swdId = cursor.getLong(Agenda._ID_INDEX)
							- Agenda.SPECIAL_WORKING_DAY_ID_OFFSET;
					List<SpecialWorkingHour> swhList = swhDao
							.getWorkingHours(swdId);

					int size = swhList.size();

					if (size > 0) {
						StringBuffer sb = new StringBuffer();

						for (int i = 0; i < size; ++i) {
							SpecialWorkingHour swh = swhList.get(i);

							sb.append(timeFormat.format(swh.getStartTime()));
							sb.append(" - ");
							sb.append(timeFormat.format(swh.getEndTime()));

							if (i < size - 1) {
								sb.append(", ");
							}
						}

						extraInfoTV.setText(sb.toString());
						extraInfoTV.setVisibility(View.VISIBLE);
					} else {
						extraInfoTV.setVisibility(View.GONE);
					}
				} else {
					extraInfoTV.setVisibility(View.GONE);
				}

				return true;

			case Agenda.TREE_DIRTY_INDEX:
				ImageView syncImageView = (ImageView) view;

				if ("true".equals(cursor.getString(Agenda.TREE_DIRTY_INDEX))) {
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Parent: " + parent.toString() + ", View: "
							+ view.toString() + ", Position: " + position
							+ ", ID: " + id);
		}

		// Cursor cursor = null;
		// Long jobId = null;
		// try {
		// cursor = getContentResolver().query(
		// Agenda.CONTENT_URI,
		// new String[] { Agenda.TYPE },
		// Agenda._ID + " = " + id + " AND " + Agenda.TYPE + " = "
		// + Agenda.TYPE_JOB, null, null);
		//
		// if (cursor != null && cursor.moveToNext()) {
		// jobId = id - Agenda.JOB_ID_OFFSET;
		// }
		// } finally {
		// if (cursor != null) {
		// cursor.close();
		// }
		// }

		if (id > Agenda.JOB_ID_OFFSET && id < Agenda.HOLIDAY_ID_OFFSET) {
			long jobId = id - Agenda.JOB_ID_OFFSET;

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Launching job activity for job id " + jobId);
			}

			Intent intent = new Intent(this, JobActivity.class);
			intent.putExtra(EffortProvider.Jobs._ID, jobId);
			startActivity(intent);
		} else if (id > Agenda.LEAVE_ID_OFFSET
				&& id < Agenda.ASSIGNED_ROUTE_ID_OFFSET) {
			long leaveId = id - Agenda.LEAVE_ID_OFFSET;

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Launching leave activity for leave id " + leaveId);
			}

			Intent intent = new Intent(this, LeaveActivity.class);
			intent.putExtra(EffortProvider.Leaves._ID, leaveId);
			startActivity(intent);
		} else if (id > Agenda.ASSIGNED_ROUTE_ID_OFFSET) {
			long assignedRouteId = id - Agenda.ASSIGNED_ROUTE_ID_OFFSET;

			if (BuildConfig.DEBUG) {
				Log.i(TAG,
						"Launching Routeplan detail activity for assignedRouteId "
								+ assignedRouteId);
			}

			Intent intent = new Intent(this, RoutePlanDetailActivity.class);
			intent.putExtra(EffortProvider.AssignedRoutes._ID, assignedRouteId);
			startActivity(intent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case android.R.id.home:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "ActionBar's Up/home button clicked.");
			}

			finish();
			Intent homeIntent = new Intent(this, HomeActivity.class);
			startActivity(homeIntent);

			break;

		case R.id.addJob:
			Intent jobIntent = new Intent(this, JobActivity.class);
			jobIntent.putExtra(EffortProvider.Jobs._ID, 0L);
			startActivity(jobIntent);

			break;

		case R.id.addCustomer:
			Intent customerIntent = new Intent(this, CustomerActivity.class);
			customerIntent.putExtra(Customers._ID, 0L);
			startActivity(customerIntent);
			break;

		case R.id.applyForLeave:
			Intent leaveIntent = new Intent(this, LeaveActivity.class);
			leaveIntent.putExtra(Leaves._ID, 0L);
			startActivity(leaveIntent);
			break;

		case R.id.submitForm:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Launching form specs activity.");
			}

			Intent intent = new Intent(this, FormSpecsActivity.class);
			startActivity(intent);
			break;

		case R.id.markAllJobsAsRead:
			jobsDao.updateReadFlag(true);
			getSupportLoaderManager().restartLoader(0, null, this);
			break;

		case R.id.markAllJobsAsUnread:
			jobsDao.updateReadFlag(false);
			getSupportLoaderManager().restartLoader(0, null, this);
			break;

		case R.id.sync:
			Utils.manualSync(getApplicationContext());
			supportInvalidateOptionsMenu();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent homeIntent = new Intent(this, HomeActivity.class);
		startActivity(homeIntent);

		super.onBackPressed();
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

	@Override
	public void onRefresh() {
		getSupportLoaderManager().restartLoader(0, null, this);
		supportInvalidateOptionsMenu();
		statusFragment.updateStatus();
	}

}
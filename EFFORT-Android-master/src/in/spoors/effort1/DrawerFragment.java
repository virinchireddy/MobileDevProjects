package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.File;
import java.util.Date;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class DrawerFragment extends Fragment implements OnClickListener {

	private static final String TAG = "DrawerFragment";
	private int activeItem;
	private int defaultActiveItem;
	private String activityTitle;
	private String activitySubtitle;
	DrawerLayout drawerLayout;
	LinearLayout leftDrawer;
	private ActionBarDrawerToggle drawerToggle;
	LinearLayout itemsLayout;
	SettingsDao settingsDao;
	JobsDao jobsDao;
	FormsDao formsDao;
	ImageButton syncButton;
	ProgressBar syncProgressBar;
	TextView syncStatusTextView;
	TextView trackStatusTextView;
	RefreshListener refreshListener;
	LayoutParams itemParams;
	LayoutParams separatorParams;
	ImageView logoImageView;
	View employeeLayout;
	TextView companyNameTextView;
	TextView employeeNameTextView;
	TextView workItem;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		settingsDao = SettingsDao.getInstance(getActivity()
				.getApplicationContext());
		jobsDao = JobsDao.getInstance(getActivity().getApplicationContext());
		formsDao = FormsDao.getInstance(getActivity().getApplicationContext());
		View view = inflater
				.inflate(R.layout.fragment_drawer, container, false);

		logoImageView = (ImageView) view.findViewById(R.id.logoImageView);
		employeeLayout = view.findViewById(R.id.employeeLayout);
		companyNameTextView = (TextView) view
				.findViewById(R.id.companyNameTextView);
		employeeNameTextView = (TextView) view
				.findViewById(R.id.employeeNameTextView);

		updateEmployeeInfo();

		leftDrawer = (LinearLayout) view.findViewById(R.id.leftDrawer);
		itemsLayout = (LinearLayout) view.findViewById(R.id.itemsLayout);
		syncButton = (ImageButton) view.findViewById(R.id.syncButton);
		syncButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Sync icon clicked.");
				}

				if (Utils.isSyncInProgress(getActivity()
						.getApplicationContext())) {
					syncButton.setVisibility(View.GONE);
					syncProgressBar.setVisibility(View.VISIBLE);
				} else {
					Utils.manualSync(getActivity().getApplicationContext());
					syncProgressBar.setVisibility(View.VISIBLE);
					syncButton.setVisibility(View.GONE);
				}
			}
		});

		syncProgressBar = (ProgressBar) view.findViewById(R.id.syncProgressBar);
		syncStatusTextView = (TextView) view
				.findViewById(R.id.syncStatusTextView);
		trackStatusTextView = (TextView) view
				.findViewById(R.id.trackStatusTextView);

		int drawerItemHeight = getResources().getDimensionPixelSize(
				R.dimen.drawer_item_height);
		int separatorHeight = getResources().getDimensionPixelSize(
				R.dimen.separator_height);

		itemParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				drawerItemHeight);
		separatorParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				separatorHeight);

		// VISIBILITIES
		boolean visibilityAgenda = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_AGENDA_KEY, true);
		boolean visibilityRoutePlan = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_ROUTE_KEY, true);
		boolean visibilityApprovalStatus = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_APPROVAL_STATUS_KEY, true);
		boolean visibilityCustomers = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_CUSTOMER_KEY, true);
		boolean visibilityJobs = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_JOB_KEY, true);
		boolean visibilityForms = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_FORM_KEY, true);
		boolean visibilityMessages = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_MESSAGES_KEY, true);
		boolean visibilityNamedLocations = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_NAMED_LOCATION_KEY, true);
		boolean visibilityKnowledgeBases = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_KNOWLEDGEBASE_KEY, true);
		boolean visibilityLeaves = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_LEAVE_KEY, true);
		boolean visibilityHolidays = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_HOLIDAYS_KEY, true);
		boolean visibilitySettings = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_SETTINGS_KEY, true);
		boolean visibilityHelp = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_HELP_KEY, true);
		boolean visibilityStartWork = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_START_WORK_KEY, true);

		addDrawerItem(R.drawable.ic_drawer_home,
				Settings.LABEL_HOME_DEFAULT_VALUE);

		if (visibilityAgenda) {
			addDrawerItem(R.drawable.ic_drawer_agenda, settingsDao.getLabel(
					Settings.LABEL_AGENDA_PLURAL_KEY,
					Settings.LABEL_AGENDA_PLURAL_DEFAULT_VALUE));
		}

		boolean viewRoutes = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_ROUTE_VIEW, true);
		if (viewRoutes && visibilityRoutePlan) {
			addDrawerItem(R.drawable.ic_drawer_routeplan, "Route plans");
		}

		if (visibilityApprovalStatus) {
			addDrawerItem(R.drawable.ic_drawer_work_flow, "Approval Status");
		}

		boolean addCustomer = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_CUSTOMER_ADD, true);
		boolean viewCustomer = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_CUSTOMER_VIEW, true);
		boolean modifyCustomer = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_CUSTOMER_MODIFY, true);

		if ((addCustomer || viewCustomer || modifyCustomer)
				&& visibilityCustomers) {
			addDrawerItem(R.drawable.ic_drawer_customers, settingsDao.getLabel(
					Settings.LABEL_CUSTOMER_PLURAL_KEY,
					Settings.LABEL_CUSTOMER_PLURAL_DEFAULT_VLAUE));
		}

		boolean addJob = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_JOB_ADD, true);
		boolean viewJob = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_JOB_VIEW, true);
		boolean modifyJob = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_JOB_MODIFY, true);

		boolean addInvitations = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_INVITATION_ADD, true);
		boolean viewInvitations = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_INVITATION_VIEW, true);
		boolean modifyInvitations = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_INVITATION_MODIFY, true);

		if ((addJob || viewJob || modifyJob || addInvitations
				|| viewInvitations || modifyInvitations)
				&& visibilityJobs) {
			addDrawerItem(R.drawable.ic_drawer_jobs, settingsDao.getLabel(
					Settings.LABEL_JOB_PLURAL_KEY,
					Settings.LABEL_JOB_PLURAL_DEFAULT_VLAUE));
		}

		boolean addForm = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_FORM_ADD, true);
		boolean viewForm = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_FORM_VIEW, true);
		boolean modifyForm = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_FORM_MODIFY, true);

		if ((addForm || viewForm || modifyForm) && visibilityForms) {
			addDrawerItem(R.drawable.ic_drawer_forms, settingsDao.getLabel(
					Settings.LABEL_FORM_PLURAL_KEY,
					Settings.LABEL_FORM_PLURAL_DEFAULT_VLAUE));
		}

		if (visibilityMessages) {
			addDrawerItem(R.drawable.ic_drawer_messages, "Messages");
		}

		boolean manageNamedLocations = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_MANAGE_NAMED_LOCATIONS, true);

		if (manageNamedLocations && visibilityNamedLocations) {
			addDrawerItem(R.drawable.ic_drawer_locations, settingsDao.getLabel(
					Settings.LABEL_NAMEDLOCATION_PLURAL_KEY,
					Settings.LABEL_NAMEDLOCATION_PLURAL_DEFAULT_VLAUE));
		}

		if (visibilityKnowledgeBases) {
			addDrawerItem(R.drawable.ic_drawer_kb, settingsDao.getLabel(
					Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
					Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE));

		}

		boolean addLeave = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_LEAVE_ADD, true);
		boolean viewLeave = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_LEAVE_VIEW, true);
		boolean modifyLeave = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_LEAVE_MODIFY, true);
		boolean deleteLeave = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_LEAVE_DELETE, true);

		if ((addLeave || viewLeave || modifyLeave || deleteLeave)
				&& visibilityLeaves) {
			addDrawerItem(R.drawable.ic_drawer_leaves, "Leaves");
		}

		boolean viewHoliday = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_HOLIDAY_VIEW, true);

		if (viewHoliday && visibilityHolidays) {
			addDrawerItem(R.drawable.ic_drawer_holidays, "Holidays");
		}

		if (visibilitySettings) {
			addDrawerItem(R.drawable.ic_drawer_settings, "Settings");
		}
		if (visibilityHelp) {
			addDrawerItem(R.drawable.ic_drawer_help, "Help");
		}
		if (visibilityStartWork) {
			workItem = addDrawerItem(R.drawable.ic_drawer_startwork,
					"Start work");
			updateStatus();
			updateWorkItem();
		}

		return view;
	}

	private TextView getDrawerItem(int id, String text) {
		TextView textView = new TextView(getActivity());

		textView.setOnClickListener(this);
		textView.setId(id);
		textView.setText(text);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setPadding(
				getResources().getDimensionPixelSize(
						R.dimen.drawer_item_left_padding), 0, 0, 0);
		textView.setTextAppearance(getActivity(),
				android.R.style.TextAppearance_Medium);
		textView.setTextColor(Color.WHITE);
		textView.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0);
		textView.setCompoundDrawablePadding(getResources()
				.getDimensionPixelSize(R.dimen.drawer_item_left_padding));

		return textView;
	}

	public void setActiveItem(int resourceId, String activityTitle,
			String activitySubtitle, RefreshListener refreshListener) {
		activeItem = resourceId;
		defaultActiveItem = resourceId;
		updateBackground();
		this.activityTitle = activityTitle;
		this.activitySubtitle = activitySubtitle;
		setupToggle();
		this.refreshListener = refreshListener;
		drawerToggle.setDrawerIndicatorEnabled(resourceId != -1); // disable
		// indicator
		// if -1 is
		// passed
	}

	public void setActiveItem(int resourceId) {
		activeItem = resourceId;
		updateBackground();
	}

	private void updateBackground() {
		int count = itemsLayout.getChildCount();

		for (int i = 0; i < count; ++i) {
			View v = itemsLayout.getChildAt(i);

			if (v instanceof TextView) {
				v.setBackgroundResource(v.getId() == activeItem ? R.color.activeDrawerItemBackgroundColor
						: R.color.normalDrawerItemBackgroundColor);
			}
		}
	}

	private void setupToggle() {
		drawerLayout = (DrawerLayout) getActivity().findViewById(
				R.id.drawerLayout);
		final ActionBarActivity activity = (ActionBarActivity) getActivity();

		drawerToggle = new ActionBarDrawerToggle(activity, /* host Activity */
		drawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.open_drawer, /* "open drawer" description for accessibility */
		R.string.close_drawer /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				activity.getSupportActionBar().setTitle(activityTitle);
				activity.getSupportActionBar().setSubtitle(activitySubtitle);
				activity.supportInvalidateOptionsMenu(); // creates call to
				// onCreateOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				activity.getSupportActionBar().setTitle(R.string.app_name);
				activity.getSupportActionBar().setSubtitle(null);
				activity.supportInvalidateOptionsMenu(); // creates call to
				// onCreateOptionsMenu()
				updateStatus();
				updateWorkItem();
			}
		};

		drawerLayout.setDrawerListener(drawerToggle);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return drawerToggle.onOptionsItemSelected(item);
	}

	public void onPostCreate(Bundle savedInstanceState) {
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// Pass any configuration change to the drawer toggls
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onClick(View v) {
		activeItem = v.getId();
		updateBackground();

		if (activeItem == defaultActiveItem) {
			drawerLayout.closeDrawers();
			return;
		}

		if (activeItem != R.drawable.ic_drawer_startwork) {
			getActivity().finish();
		}

		Intent intent = null;

		switch (activeItem) {
		case R.drawable.ic_drawer_home:
			intent = new Intent(getActivity(), HomeActivity.class);
			break;
		case R.drawable.ic_drawer_agenda:
			intent = new Intent(getActivity(), AgendaActivity.class);
			break;
		case R.drawable.ic_drawer_customers:
			intent = new Intent(getActivity(), CustomersActivity.class);
			break;
		case R.drawable.ic_drawer_jobs:
			intent = new Intent(getActivity(), JobsActivity.class);
			break;
		case R.drawable.ic_drawer_forms:
			intent = new Intent(getActivity(), FormSpecsActivity.class);
			break;
		case R.drawable.ic_drawer_messages:
			intent = new Intent(getActivity(), MessagesActivity.class);
			break;
		case R.drawable.ic_drawer_locations:
			intent = new Intent(getActivity(), LocationsActivity.class);
			break;
		case R.drawable.ic_drawer_kb:
			intent = new Intent(getActivity(), ArticlesActivity.class);
			break;
		case R.drawable.ic_drawer_leaves:
			intent = new Intent(getActivity(), LeavesActivity.class);
			break;
		case R.drawable.ic_drawer_holidays:
			intent = new Intent(getActivity(), HolidaysActivity.class);
			break;
		case R.drawable.ic_drawer_settings:
			intent = new Intent(getActivity(), SettingsActivity.class);
			break;
		case R.drawable.ic_drawer_help:
			intent = new Intent(getActivity(), HelpActivity.class);
			break;
		case R.drawable.ic_drawer_routeplan:
			intent = new Intent(getActivity(), RoutePlansActivity.class);
			break;
		case R.drawable.ic_drawer_work_flow:
			// intent = new Intent(getActivity(), WorkFlowsActivity.class);
			intent = new Intent(getActivity(), WorkFlowsActivity.class);
			intent.putExtra(WorkFlowsActivity.SCREEN_PURPOSE,
					WorkFlowsActivity.FORM_CREATE);
			intent.putExtra(WorkFlowsActivity.onBackToHomeKey, true);
			startActivity(intent);
			break;
		case R.drawable.ic_drawer_startwork:
			toggleWorkItem();
			break;
		}

		if (intent != null) {
			startActivity(intent);
		} else {
			if (activeItem != R.drawable.ic_drawer_startwork) {
				Toast.makeText(getActivity(), "No intent to start activity.",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public void updateStatus() {
		if (getActivity() == null
				|| getActivity().getApplicationContext() == null) {
			return;
		}

		String localSyncTimeSQLite = settingsDao.getString("localSyncTime");
		String syncStatus = "Never";

		if (localSyncTimeSQLite != null) {
			Date localSyncTime = SQLiteDateTimeUtils
					.getLocalTime(localSyncTimeSQLite);
			if (DateUtils.isToday(localSyncTime.getTime())) {
				syncStatus = Utils.getTimeFormat(
						getActivity().getApplicationContext()).format(
						localSyncTime);
			} else {
				syncStatus = Utils.getDateTimeFormatWithOutYear(
						getActivity().getApplicationContext()).format(
						localSyncTime);
			}
		}

		syncStatusTextView.setText("Last sync: " + syncStatus);

		boolean canTrack = Utils.canTrackNow(getActivity()
				.getApplicationContext());

		if (canTrack) {
			trackStatusTextView.setText("Work: ON");
		} else {
			trackStatusTextView.setText("Work: OFF");
		}

		if (Utils.isSyncInProgress(getActivity().getApplicationContext())) {
			syncButton.setVisibility(View.GONE);
			syncProgressBar.setVisibility(View.VISIBLE);
		} else {
			syncButton.setVisibility(View.VISIBLE);
			syncProgressBar.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onPause. Unregistering sync done receiver.");
		}

		try {
			getActivity().unregisterReceiver(syncDoneReceiver);
		} catch (IllegalArgumentException e) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Ignored the exception caught while unregistering sync done receiver: "
								+ e.toString());
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().registerReceiver(syncDoneReceiver,
				new IntentFilter(SyncService.ACTION_SYNC_DONE));
	}

	private SyncDoneReceiver syncDoneReceiver = new SyncDoneReceiver();

	class SyncDoneReceiver extends BroadcastReceiver {
		/**
		 * Static constants cannot be declared in inner classes. Thus, the
		 * following TAG is non-static.
		 */
		public final String TAG = SyncDoneReceiver.class.getSimpleName();

		@Override
		public void onReceive(Context context, Intent intent) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "In onReceive. Restarting loader.");
			}

			jobsDao.updateTreeDirtyFlags();
			formsDao.updateTreeDirtyFlags();

			// String message = intent.getStringExtra("message");
			// Toast.makeText(AgendaActivity.this, message, Toast.LENGTH_SHORT)

			// .show();

			final String description = intent.getStringExtra("description");
			final String smsc = intent.getStringExtra("smsc");

			if (InitResponse.CODE_NOT_AUTHORIZED.equals(intent
					.getStringExtra("code"))) {
				String mcc = Utils.getMcc(getActivity());

				if ("404".equals(mcc) || "405".equals(mcc)) {
					new AlertDialog.Builder(getActivity())
							.setTitle("Send SMS for easy activation?")
							.setMessage(
									"Do you want to allow EFFORT to send an SMS to "
											+ smsc
											+ " for easy activation? Regular SMS charges apply. Tap don't send for manual activation instructions.")
							.setPositiveButton("Send",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											Utils.sendSms(
													smsc,
													"TRACK REGISTER 4 "
															+ settingsDao
																	.getString("code"));
											Toast.makeText(
													getActivity(),
													"Activation SMS sent. Please sync again when you receive the activation completed SMS.",
													Toast.LENGTH_LONG).show();
										}
									})
							.setNegativeButton("Don't send",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {
											Utils.showSignupHelp(getActivity());
										}
									}).show();
				} else {
					Utils.showSignupHelp(getActivity());
				}
			} else if (InitResponse.CODE_SWITCH_DEVICE.equals(intent
					.getStringExtra("code"))) {
				new AlertDialog.Builder(getActivity())
						.setTitle("Use this device?")
						.setMessage(description)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Intent syncIntent = new Intent(
												getActivity()
														.getApplicationContext(),
												SyncService.class);
										syncIntent.putExtra("override", true);
										WakefulIntentService
												.sendWakefulWork(
														getActivity()
																.getApplicationContext(),
														syncIntent);
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Toast.makeText(
												getActivity()
														.getApplicationContext(),
												"Please use your other device for EFFORT.",
												Toast.LENGTH_LONG).show();
									}
								}).show();
			}

			updateStatus();
			updateEmployeeInfo();

			if (refreshListener != null) {
				refreshListener.onRefresh();
			}

			Utils.handleVersionUpdate(getActivity(), settingsDao);
		}
	}

	public boolean isDrawerOpen() {
		return drawerLayout.isDrawerOpen(Gravity.LEFT);
	}

	public void closeDrawers() {
		drawerLayout.closeDrawers();
	}

	public void setActivityTitle(String title) {
		activityTitle = title;
	}

	public void setActivitySubtitle(String subtitle) {
		activitySubtitle = subtitle;
	}

	private void addSeparator() {
		View separator = new View(getActivity());
		separator.setBackgroundResource(R.color.drawerSeparatorBackground);
		itemsLayout.addView(separator, separatorParams);
	}

	public TextView addDrawerItem(int resourceId, String title) {
		TextView tv = getDrawerItem(resourceId, title);

		itemsLayout.addView(tv, itemParams);
		addSeparator();

		return tv;
	}

	private void updateWorkItem() {
		boolean visibilityStartWork = settingsDao.getBoolean(
				Settings.LABEL_VISIBILITY_START_WORK_KEY, true);
		if (visibilityStartWork) {
			boolean working = settingsDao.getBoolean("working", false);

			workItem.setCompoundDrawablesWithIntrinsicBounds(
					working ? R.drawable.ic_drawer_stopwork
							: R.drawable.ic_drawer_startwork, 0, 0, 0);
			workItem.setText(working ? "Stop work" : "Start work");
		}

	}

	public void toggleWorkItem() {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Toggling work.");
		}

		boolean working = settingsDao.getBoolean("working", false);
		boolean canTrack = Utils.canTrackNow(getActivity()
				.getApplicationContext());
		// track at the time of start work and stop work
		if (working) {
			// means he want to stop work
			Utils.captureLocation(getActivity(), Locations.PURPOSE_STOP_WORK);

		} else {
			// means he want to start work
			Utils.captureLocation(getActivity(), Locations.PURPOSE_START_WORK);

		}
		// toggle
		settingsDao.saveSetting("working", working ? "false" : "true");

		if (canTrack != Utils
				.canTrackNow(getActivity().getApplicationContext())) {
			EffortApplication.restartSyncAlarm();
		}

		updateWorkItem();
		updateStatus();
	}

	private void updateEmployeeInfo() {
		if (TextUtils.isEmpty(settingsDao
				.getString(EffortProvider.Settings.KEY_EMPLOYEE_ID))) {
			employeeLayout.setVisibility(View.GONE);
		} else {
			employeeLayout.setVisibility(View.VISIBLE);
			String path = Utils.getLocalLogoPath();
			File file = new File(path);

			if (file.exists()) {
				Drawable drawable = Drawable.createFromPath(path);

				if (drawable != null) {
					logoImageView.setImageDrawable(drawable);
					logoImageView.setVisibility(View.VISIBLE);
				} else {
					logoImageView.setVisibility(View.GONE);
				}
			} else {
				logoImageView.setVisibility(View.GONE);
			}

			String companyName = settingsDao
					.getString(EffortProvider.Settings.KEY_APP_TITLE);

			if (TextUtils.isEmpty(companyName)) {
				companyNameTextView.setVisibility(View.GONE);
			} else {
				companyNameTextView.setText(companyName);
				companyNameTextView.setVisibility(View.VISIBLE);
			}

			String employeeName = settingsDao
					.getString(EffortProvider.Settings.KEY_EMPLOYEE_NAME);

			if (TextUtils.isEmpty(employeeName)) {
				employeeNameTextView.setVisibility(View.GONE);
			} else {
				employeeNameTextView.setText(employeeName);
				employeeNameTextView.setVisibility(View.VISIBLE);
			}
		}
	}

}

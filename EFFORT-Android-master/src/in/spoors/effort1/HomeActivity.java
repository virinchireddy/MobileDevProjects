package in.spoors.effort1;

import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Customers;
import in.spoors.effort1.provider.EffortProvider.Leaves;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HomeActivity extends ActionBarActivity implements OnClickListener,
		RefreshListener {
	public static final String TAG = "HomeActivity";
	private SettingsDao settingsDao;
	private View workingButton;
	private DrawerFragment drawerFragment;
	private StatusFragment statusFragment;

	TableRow row = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_home);
		setSupportProgressBarIndeterminateVisibility(false);

		settingsDao = SettingsDao.getInstance(getApplicationContext());

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

		// For testing
		// visibilityAbout = true;
		// visibilityAgenda = true;
		// visibilityRoutePlan = false;
		// visibilityApprovalStatus = false;
		// visibilityCustomers = false;
		// visibilityJobs = false;
		// visibilityForms = false;
		// visibilityMessages = false;
		// visibilityNamedLocations = false;
		// visibilityKnowledgeBases = false;
		// visibilityLeaves = false;
		// visibilityHolidays = false;
		// visibilitySettings = false;
		// visibilityHelp = false;
		// visibilityStartWork = false;
		TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		List<View> buttons = new ArrayList<View>();

		if (visibilityAgenda) {
			buttons.add(getButton(R.drawable.agenda, settingsDao.getLabel(
					Settings.LABEL_AGENDA_PLURAL_KEY,
					Settings.LABEL_AGENDA_PLURAL_DEFAULT_VALUE)));

		}

		boolean viewRoutes = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_ROUTE_VIEW, true);

		if (viewRoutes && visibilityRoutePlan) {
			buttons.add(getButton(R.drawable.route_plan, "Route plans"));
		}

		if (visibilityApprovalStatus) {
			buttons.add(getButton(R.drawable.work_flow, "Approval Status"));
		}

		boolean addCustomer = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_CUSTOMER_ADD, true);
		boolean viewCustomer = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_CUSTOMER_VIEW, true);
		boolean modifyCustomer = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_CUSTOMER_MODIFY, true);

		if ((addCustomer || viewCustomer || modifyCustomer)
				&& visibilityCustomers) {
			buttons.add(getButton(R.drawable.customers, settingsDao.getLabel(
					Settings.LABEL_CUSTOMER_PLURAL_KEY,
					Settings.LABEL_CUSTOMER_PLURAL_DEFAULT_VLAUE)));
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
			// buttons.add(getButton(R.drawable.jobs));
			buttons.add(getButton(R.drawable.jobs, settingsDao.getLabel(
					Settings.LABEL_JOB_PLURAL_KEY,
					Settings.LABEL_JOB_PLURAL_DEFAULT_VLAUE)));

		}

		boolean addForm = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_FORM_ADD, true);
		boolean viewForm = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_FORM_VIEW, true);
		boolean modifyForm = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_FORM_MODIFY, true);

		if ((addForm || viewForm || modifyForm) && visibilityForms) {
			// buttons.add(getButton(R.drawable.forms));
			buttons.add(getButton(R.drawable.forms, settingsDao.getLabel(
					Settings.LABEL_FORM_PLURAL_KEY,
					Settings.LABEL_FORM_PLURAL_DEFAULT_VLAUE)));
		}

		// buttons.add(getButton(R.drawable.messages));
		if (visibilityMessages) {
			buttons.add(getButton(R.drawable.messages, settingsDao.getLabel(
					Settings.LABEL_MESSAGE_PLURAL_KEY,
					Settings.LABEL_MESSAGE_PLURAL_DEFAULT_VLAUE)));
		}

		boolean manageNamedLocations = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_MANAGE_NAMED_LOCATIONS, true);

		if (manageNamedLocations && visibilityNamedLocations) {
			// buttons.add(getButton(R.drawable.locations));
			buttons.add(getButton(R.drawable.locations, settingsDao.getLabel(
					Settings.LABEL_NAMEDLOCATION_PLURAL_KEY,
					Settings.LABEL_NAMEDLOCATION_PLURAL_DEFAULT_VLAUE)));

		}

		if (visibilityKnowledgeBases) {
			buttons.add(getButton(R.drawable.knowledgebase, settingsDao
					.getLabel(Settings.LABEL_KNOWLEDGEBASE_PLURAL_KEY,
							Settings.LABEL_KNOWLEDGEBASE_PLURAL_DEFAULT_VLAUE)));
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
			// buttons.add(getButton(R.drawable.leaves));
			buttons.add(getButton(R.drawable.leaves,
					Settings.LABEL_LEAVES_PLURAL_DEFAULT_VLAUE));

		}

		boolean viewHoliday = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_HOLIDAY_VIEW, true);

		if (viewHoliday && visibilityHolidays) {
			// buttons.add(getButton(R.drawable.holidays));
			buttons.add(getButton(R.drawable.holidays, "Holidays"));

		}

		// buttons.add(getButton(R.drawable.settings));
		if (visibilitySettings) {
			buttons.add(getButton(R.drawable.settings,
					Settings.LABEL_SETTING_PLURAL_DEFAULT_VLAUE));
		}

		if (visibilityHelp) {
			buttons.add(getButton(R.drawable.help,
					Settings.LABEL_HELP_DEFAULT_VLAUE));
		}

		if (visibilityStartWork) {
			workingButton = getButton(R.drawable.startwork,
					Settings.LABEL_STARTWORK_DEFAULT_VLAUE);

			buttons.add(workingButton);
			updateWorkingButton();

		}

		int numButtons = buttons.size();

		int numColumns = 3;
		int orientation = getResources().getConfiguration().orientation;

		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if (numButtons > 8) {
				numColumns = 3;
			} else {
				numColumns = 4;
			}
		}
		//
		// TableRow row = null;

		int margin = getResources().getDimensionPixelSize(
				R.dimen.home_image_margin);

		TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.MATCH_PARENT, 0, 1);

		DisplayMetrics displayMetrics = getApplicationContext().getResources()
				.getDisplayMetrics();

		/*
		 * TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(
		 * TableRow.LayoutParams.MATCH_PARENT,
		 * TableRow.LayoutParams.MATCH_PARENT);
		 */
		TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(
				(int) (displayMetrics.widthPixels) / numColumns,
				TableRow.LayoutParams.MATCH_PARENT);

		// TableLayout.LayoutParams rowParams = new TableLayout.LayoutParams(
		// TableLayout.LayoutParams.WRAP_CONTENT, 0, 0);
		//
		// TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(
		// TableRow.LayoutParams.WRAP_CONTENT,
		// TableRow.LayoutParams.WRAP_CONTENT);
		buttonParams.leftMargin = margin;
		buttonParams.rightMargin = margin;
		buttonParams.topMargin = margin;
		buttonParams.bottomMargin = margin;
		int rows = 0;

		for (int i = 0; i < numButtons; ++i) {
			if (i % numColumns == 0) {
				rows++;
				row = new TableRow(this);
				tableLayout.addView(row, rowParams);
			}

			row.addView(buttons.get(i), buttonParams);
		}

		// For Empty
		Drawable d = getApplicationContext().getResources().getDrawable(
				R.drawable.agenda);

		TableRow.LayoutParams emptyButtonParams = new TableRow.LayoutParams(
				(int) (displayMetrics.widthPixels) / numColumns,
				d.getIntrinsicHeight(), 1);
		TableLayout.LayoutParams emptyRowParams = new TableLayout.LayoutParams(
				displayMetrics.widthPixels, d.getIntrinsicHeight(), 1);

		int totalRows = 4;

		int emptyColumnsToBeAdd = 0;
		if ((numButtons % numColumns) == 0) {
			emptyColumnsToBeAdd = 0;
		} else {
			emptyColumnsToBeAdd = numColumns - (numButtons % numColumns);
		}

		int emptyRowsToBeAdd = totalRows - rows;
		// emptyRowsToBeAdd = 0;
		for (int j = 0; j < emptyRowsToBeAdd; j++) {

			row = new TableRow(this);
			tableLayout.addView(row, emptyRowParams);

			// LAST ROW
			if (j == emptyRowsToBeAdd - 1) {
				if (emptyColumnsToBeAdd == 0) {
					for (int k = 0; k < numColumns; k++) {
						row.addView(new View(getApplicationContext()),
								emptyButtonParams);
					}
				} else {
					for (int k = 0; k < emptyColumnsToBeAdd; k++) {
						row.addView(new View(getApplicationContext()),
								emptyButtonParams);
					}
				}
			} else {
				for (int k = 0; k < numColumns; k++) {
					row.addView(new View(getApplicationContext()),
							emptyButtonParams);
				}
			}
		}

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		SettingsDao settingsDao = SettingsDao
				.getInstance(getApplicationContext());

		String title = settingsDao
				.getString(EffortProvider.Settings.KEY_APP_TITLE);
		String subtitle = null;
		actionBar.setTitle(title);

		if (settingsDao.getString(EffortProvider.Settings.KEY_EMPLOYEE_NAME) != null) {
			subtitle = settingsDao
					.getString(EffortProvider.Settings.KEY_EMPLOYEE_NAME);
			actionBar.setSubtitle(subtitle);
		}

		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_home, title,
				subtitle, this);
		statusFragment = (StatusFragment) getSupportFragmentManager()
				.findFragmentById(R.id.statusFragment);
	}

	// public ImageButton getButton(int id) {
	// ImageButton imageButton = new ImageButton(this);
	// imageButton.setBackgroundResource(R.drawable.bg_selector_home);
	// imageButton.setScaleType(ScaleType.FIT_CENTER);
	// imageButton.setOnClickListener(this);
	// imageButton.setId(id);
	// imageButton.setImageResource(id);
	// imageButton.setContentDescription("jobs");
	// int padding = getResources().getDimensionPixelSize(
	// R.dimen.home_image_padding);
	// imageButton.setPadding(padding, padding, padding, padding);
	// return imageButton;
	// }

	// Button getButton(int id, String label) {
	//
	// Button button = new Button(this);
	// button.setOnClickListener(this);
	// button.setId(id);
	// button.setBackgroundResource(R.drawable.bg_selector_home);
	// button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
	// LayoutParams.WRAP_CONTENT));
	// Drawable drawable = getResources().getDrawable(id);
	// // drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*0.5),
	// // (int)(drawable.getIntrinsicHeight()*0.5));
	// // drawable.setBounds(0, 0, 30,
	// // 30);
	// button.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null,
	// null);
	//
	// // Drawable drawable = getResources().getDrawable(id);
	// // button.setCompoundDrawables(null,drawable, null, null);
	// button.setText(label);
	// int padding = getResources().getDimensionPixelSize(
	// R.dimen.home_image_padding);
	// button.setPadding(padding, padding, padding, padding);
	//
	// return button;
	//
	// }

	View getButton(int id, String label) {

		View buttonView = this.getLayoutInflater().inflate(
				R.layout.custom_button, null);
		buttonView.setOnClickListener(this);
		buttonView.setId(id);
		TextView textView = (TextView) buttonView
				.findViewById(R.id.labelTextView);
		ImageView imageView = (ImageView) buttonView.findViewById(R.id.img);
		textView.setText(label);
		imageView.setImageResource(id);

		return buttonView;

	}

	/*
	 * View getEmptyButton(int id, String label) {
	 * 
	 * View buttonView = this.getLayoutInflater().inflate(
	 * R.layout.custom_button, null); buttonView.setOnClickListener(this);
	 * buttonView.setId(id); TextView textView = (TextView) buttonView
	 * .findViewById(R.id.labelTextView); ImageView imageView = (ImageView)
	 * buttonView.findViewById(R.id.img); textView.setText(label);
	 * imageView.setImageResource(id);
	 * 
	 * return buttonView;
	 * 
	 * }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
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
		menu.findItem(R.id.add).setVisible(
				!drawerFragment.isDrawerOpen()
						&& (addCustomer || addJob || addForm || addLeave));

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

		if (Utils.isSyncInProgress(getApplicationContext())) {
			menu.findItem(R.id.sync).setVisible(false);
			setSupportProgressBarIndeterminateVisibility(true);
		} else {
			menu.findItem(R.id.sync).setVisible(!drawerFragment.isDrawerOpen());
			setSupportProgressBarIndeterminateVisibility(false);
		}

		return true;
	}

	public void onCustomersButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching customers activity.");
		}

		finish();
		Intent intent = new Intent(this, CustomersActivity.class);
		startActivity(intent);
	}

	public void onAgendaButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching agenda activity.");
		}

		finish();
		Intent intent = new Intent(this, AgendaActivity.class);
		startActivity(intent);
	}

	public void onJobsButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching jobs activity.");
		}

		finish();
		Intent intent = new Intent(this, JobsActivity.class);
		startActivity(intent);
	}

	public void onFormsButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching form specs activity.");
		}

		finish();
		Intent intent = new Intent(this, FormSpecsActivity.class);
		startActivity(intent);
	}

	public void onLeavesButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching leaves activity.");
		}

		finish();
		Intent intent = new Intent(this, LeavesActivity.class);
		startActivity(intent);
	}

	public void onHolidaysButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching holidays activity.");
		}

		finish();
		Intent intent = new Intent(this, HolidaysActivity.class);
		startActivity(intent);
	}

	public void onKnowledgeBaseButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching knowledge base (articles) activity.");
		}

		finish();
		Intent intent = new Intent(this, ArticlesActivity.class);
		startActivity(intent);
	}

	public void onLocationsButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching locations activity.");
		}

		finish();
		Intent intent = new Intent(this, LocationsActivity.class);
		startActivity(intent);
	}

	public void onMessagesButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching messages activity.");
		}

		finish();
		Intent intent = new Intent(this, MessagesActivity.class);
		startActivity(intent);
	}

	public void onSettingsButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching settings activity.");
		}

		finish();
		Intent intent = new Intent(this, SettingsActivity.class);
		startActivity(intent);
	}

	public void onHelpButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching help activity.");
		}

		finish();
		Intent intent = new Intent(this, HelpActivity.class);
		startActivity(intent);
	}

	public void onWorkButtonClick(View view) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Toggling work.");
		}

		boolean working = settingsDao.getBoolean("working", false);
		boolean canTrack = Utils.canTrackNow(getApplicationContext());
		// track at the time of start work and stop work
		if (working) {
			// means he want to stop work
			Utils.captureLocation(this, Locations.PURPOSE_STOP_WORK);

		} else {
			// means he want to start work
			Utils.captureLocation(this, Locations.PURPOSE_START_WORK);

		}

		// toggle
		settingsDao.saveSetting("working", working ? "false" : "true");
		if (canTrack != Utils.canTrackNow(getApplicationContext())) {
			EffortApplication.restartSyncAlarm();
		}

		updateWorkingButton();
		statusFragment.updateStatus();
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
			// Intent agendaIntent = new Intent(this, AgendaActivity.class);
			// startActivity(agendaIntent);

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

		case R.id.sync:
			Utils.manualSync(getApplicationContext());
			supportInvalidateOptionsMenu();
			break;

		default:
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
		statusFragment.updateStatus();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.drawable.agenda:
			onAgendaButtonClick(v);
			break;

		case R.drawable.customers:
			onCustomersButtonClick(v);
			break;

		case R.drawable.jobs:
			onJobsButtonClick(v);
			break;

		case R.drawable.forms:
			onFormsButtonClick(v);
			break;

		case R.drawable.leaves:
			onLeavesButtonClick(v);
			break;

		case R.drawable.holidays:
			onHolidaysButtonClick(v);
			break;

		case R.drawable.knowledgebase:
			onKnowledgeBaseButtonClick(v);
			break;

		case R.drawable.locations:
			onLocationsButtonClick(v);
			break;

		case R.drawable.messages:
			onMessagesButtonClick(v);
			break;

		case R.drawable.settings:
			onSettingsButtonClick(v);
			break;

		case R.drawable.help:
			onHelpButtonClick(v);
			break;

		case R.drawable.startwork:
			onWorkButtonClick(v);
			break;
		case R.drawable.route_plan:
			onRoutePlanButtonClick(v);
			break;
		case R.drawable.work_flow:
			onWorkFlowButtonClick(v);
			break;
		default:
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "No click handler found for " + v.getId());
			}
		}
	}

	private void onWorkFlowButtonClick(View v) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching WorkFlowsActivity.");
		}
		finish();
		Intent intent = new Intent(this, WorkFlowsActivity.class);
		intent.putExtra(WorkFlowsActivity.SCREEN_PURPOSE,
				WorkFlowsActivity.FORM_CREATE);
		intent.putExtra(WorkFlowsActivity.onBackToHomeKey, true);
		startActivity(intent);
	}

	private void onRoutePlanButtonClick(View v) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Launching route plans activity.");
		}

		finish();
		Intent intent = new Intent(this, RoutePlansActivity.class);
		startActivity(intent);
	}

	private void updateWorkingButton() {
		boolean working = settingsDao.getBoolean("working", false);

		// workingButton.setImageResource(working ? R.drawable.stopwork
		// : R.drawable.startwork);
		try {

			// workingButton.setCompoundDrawablesWithIntrinsicBounds(0,
			// working ? R.drawable.stopwork : R.drawable.startwork, 0, 0);
			TextView textView = (TextView) workingButton
					.findViewById(R.id.labelTextView);
			ImageView imageView = (ImageView) workingButton
					.findViewById(R.id.img);
			imageView.setImageResource(working ? R.drawable.stopwork
					: R.drawable.startwork);
			textView.setText(working ? "Stop work" : "Start work");
		} catch (Exception e) {
			System.out.println("" + e.toString());
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

	@Override
	public void onRefresh() {
		supportInvalidateOptionsMenu();
		statusFragment.updateStatus();
	}
}

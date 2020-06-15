package in.spoors.effort1;

import in.spoors.common.EffortDatePickerDialog;
import in.spoors.common.EffortTimePickerDialog;
import in.spoors.effort1.dao.LeaveStatusDao;
import in.spoors.effort1.dao.LeavesDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Leave;
import in.spoors.effort1.provider.EffortProvider.Leaves;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class LeaveActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnDateSetListener, OnTimeSetListener {

	public static final String TAG = "LeaveActivity";

	private long localLeaveId;
	private boolean editMode;

	/**
	 * Leave on activity start
	 */
	private Leave originalLeave;

	/**
	 * Customer that acts as the scratch pad
	 */
	private Leave currentLeave;

	private LeavesDao leavesDao;
	private LeaveStatusDao leaveStatusDao;
	private SettingsDao settingsDao;

	// view mode controls
	private View employeeRemarksLayout;
	private TextView employeeRemarksTextView;
	private View managerRemarksLayout;
	private TextView managerRemarksLabel;
	private TextView managerRemarksTextView;
	private TextView startTimeTextView;
	private TextView endTimeTextView;

	// edit mode controls
	private Button startDateButton;
	private Button startTimeButton;
	private Button endDateButton;
	private Button endTimeButton;
	private EffortDatePickerDialog datePickerDialog;
	private EffortTimePickerDialog timePickerDialog;

	private EditLeaveFragment editLeaveFragment;
	private ViewLeaveFragment viewLeaveFragment;

	private EditText employeeRemarksEditText;
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat timeFormat;
	private SimpleDateFormat dateTimeFormat;

	private TextView statusTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leave);
		leavesDao = LeavesDao.getInstance(getApplicationContext());
		leaveStatusDao = LeaveStatusDao.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());

		dateFormat = Utils.getDateFormat(getApplicationContext());
		timeFormat = Utils.getTimeFormat(getApplicationContext());
		dateTimeFormat = Utils.getDateTimeFormat(getApplicationContext());

		if (savedInstanceState == null) {
			localLeaveId = getIntent().getLongExtra(Leaves._ID, 0);
		} else {
			localLeaveId = savedInstanceState.getLong("localLeaveId");
			editMode = savedInstanceState.getBoolean("editMode");
			originalLeave = (Leave) savedInstanceState
					.getSerializable("originalLeave");
			currentLeave = (Leave) savedInstanceState
					.getSerializable("originalLeave");
		}

		if (localLeaveId == 0) {
			editMode = true;
		}

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Activity launched for Local Leave ID: " + localLeaveId);
		}

		getSupportLoaderManager().initLoader(0, null, this);

		// edit mode widgets
		employeeRemarksEditText = (EditText) findViewById(R.id.employeeRemarksEditText);
		startDateButton = (Button) findViewById(R.id.startDateButton);
		startTimeButton = (Button) findViewById(R.id.startTimeButton);
		endDateButton = (Button) findViewById(R.id.endDateButton);
		endTimeButton = (Button) findViewById(R.id.endTimeButton);

		// view mode widgets
		startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
		endTimeTextView = (TextView) findViewById(R.id.endTimeTextView);
		statusTextView = (TextView) findViewById(R.id.statusTextView);
		employeeRemarksLayout = findViewById(R.id.employeeRemarksLayout);
		employeeRemarksTextView = (TextView) findViewById(R.id.employeeRemarksTextView);
		managerRemarksLayout = findViewById(R.id.managerRemarksLayout);
		managerRemarksLabel = (TextView) findViewById(R.id.managerRemarksLabel);
		managerRemarksTextView = (TextView) findViewById(R.id.managerRemarksTextView);

		viewLeaveFragment = (ViewLeaveFragment) getSupportFragmentManager()
				.findFragmentById(R.id.viewLeaveFragment);
		editLeaveFragment = (EditLeaveFragment) getSupportFragmentManager()
				.findFragmentById(R.id.editLeaveFragment);
		showRightFragment();
	}

	private void showRightFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		if (editMode) {
			ft.hide(viewLeaveFragment);
			ft.show(editLeaveFragment);
		} else {
			ft.hide(editLeaveFragment);
			ft.show(viewLeaveFragment);
		}

		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.leave, menu);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		boolean hasModifyLeavePermission = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_LEAVE_MODIFY, true);
		boolean hasDeleteLeavePermission = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_LEAVE_DELETE, true);

		if (!editMode) {
			menu.findItem(R.id.editLeave)
					.setVisible(
							hasModifyLeavePermission
									&& (currentLeave == null || (currentLeave != null && currentLeave
											.getStatus() == Leaves.STATUS_APPLIED)));
			menu.findItem(R.id.cancelLeave)
					.setVisible(
							hasDeleteLeavePermission
									&& (currentLeave == null || (currentLeave != null && currentLeave
											.getStatus() != Leaves.STATUS_REJECTED)));
			// menu.findItem(R.id.cancelLeave)
			// .setVisible(hasDeleteLeavePermission);
			menu.findItem(R.id.saveLeave).setVisible(false);
			menu.findItem(R.id.discardLeave).setVisible(false);
		} else {
			menu.findItem(R.id.editLeave).setVisible(false);
			menu.findItem(R.id.cancelLeave).setVisible(false);
			menu.findItem(R.id.saveLeave).setVisible(true);
			menu.findItem(R.id.discardLeave).setVisible(true);
		}

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("localLeaveId", localLeaveId);
		outState.putBoolean("editMode", editMode);
		outState.putSerializable("originalLeave", originalLeave);

		if (editMode) {
			useEditTextValues();
		}

		outState.putSerializable("currentLeave", currentLeave);
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
			Log.d(TAG, "In onCreateLoader for Loader ID: " + id
					+ ", Leave ID: " + localLeaveId);
		}

		return new CursorLoader(getApplicationContext(), Uri.withAppendedPath(
				Leaves.CONTENT_URI, "" + localLeaveId), Leaves.ALL_COLUMNS,
				null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}

		// DatabaseUtils.dumpCursor(cursor);

		if (cursor == null || !cursor.moveToFirst()) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			Date startTime = cal.getTime();

			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);

			Date endTime = cal.getTime();

			if (originalLeave == null) {
				originalLeave = new Leave();

				originalLeave.setStartTime(startTime);
				originalLeave.setEndTime(endTime);
				originalLeave.setCancelled(false);
				originalLeave.setStatus(Leaves.STATUS_APPLIED);
			}

			if (currentLeave == null) {
				currentLeave = new Leave();

				currentLeave.setStartTime(startTime);
				currentLeave.setEndTime(endTime);
				currentLeave.setCancelled(false);
				currentLeave.setStatus(Leaves.STATUS_APPLIED);
			}
		}

		// don't touch these job objects, if they already exist
		// so that we don't lose the user's changes due to
		// orientation change
		if (originalLeave == null) {
			originalLeave = new Leave();
			originalLeave.load(cursor);
		}

		if (currentLeave == null) {
			currentLeave = new Leave();
			currentLeave.load(cursor);
			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Current leave after loading: "
								+ currentLeave.toString());
			}
		}

		if (editMode) {
			employeeRemarksEditText.setText(currentLeave.getEmployeeRemarks());
			employeeRemarksEditText.requestFocus();

			updateDateTimeButtons();
		} else {
			startTimeTextView.setText(dateTimeFormat.format(currentLeave
					.getStartTime()));
			endTimeTextView.setText(dateTimeFormat.format(currentLeave
					.getEndTime()));
			statusTextView.setText(leaveStatusDao.getName(currentLeave
					.getStatus()));
			employeeRemarksTextView.setText(currentLeave.getEmployeeRemarks());
			employeeRemarksLayout.setVisibility(Utils
					.getVisibility(currentLeave.getEmployeeRemarks()));
			managerRemarksTextView.setText(currentLeave.getManagerRemarks());
			managerRemarksLabel.setText(currentLeave.getStatusChangedByName()
					+ "'s remarks");
			managerRemarksLayout.setVisibility(Utils.getVisibility(currentLeave
					.getManagerRemarks()));
		}

		supportInvalidateOptionsMenu();
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoaderReset.");
		}
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

			onBackPressed();
			break;

		case R.id.editLeave:
			editMode = true;

			// invalidate to show save and discard buttons
			supportInvalidateOptionsMenu();
			getSupportLoaderManager().restartLoader(0, null, this);
			showRightFragment();

			break;

		case R.id.cancelLeave:
			new AlertDialog.Builder(this)
					.setTitle("Leave cancellation")
					.setMessage("Do you want to cancel this leave?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									leavesDao.cancelLeave(localLeaveId);
									Toast.makeText(LeaveActivity.this,
											"Leave has been cancelled.",
											Toast.LENGTH_LONG).show();
									finish();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									Toast.makeText(LeaveActivity.this,
											"Leave has been left as-is.",
											Toast.LENGTH_LONG).show();
								}
							}).show();

			break;

		case R.id.saveLeave:
			save();
			break;

		case R.id.discardLeave:
			discard();
			break;

		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "No menu item handler found.");
			}
		}

		return super.onOptionsItemSelected(item);
	}

	private void useEditTextValues() {
		currentLeave.setEmployeeRemarks(Utils
				.getString(employeeRemarksEditText));
	}

	@Override
	public void onBackPressed() {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "In onBackPressed.");
		}

		if (editMode) {
			useEditTextValues();
			if (hasUnsavedChanges()) {
				DialogFragment dialogFragment = MyAlertDialogFragment
						.newInstance(R.string.alert_dialog_title);
				dialogFragment.show(getSupportFragmentManager(), "dialog");
			} else {
				discard();
			}
		} else {
			super.onBackPressed();
		}
	}

	public static class MyAlertDialogFragment extends DialogFragment {
		public static MyAlertDialogFragment newInstance(int title) {
			MyAlertDialogFragment frag = new MyAlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int title = getArguments().getInt("title");

			return new AlertDialog.Builder(getActivity())
					.setTitle(title)
					.setPositiveButton(R.string.alert_dialog_save,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									((LeaveActivity) (getActivity())).save();
								}
							})
					.setNegativeButton(R.string.alert_dialog_discard,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									((LeaveActivity) (getActivity())).discard();
								}
							}).create();
		}

	}

	private void save() {
		useEditTextValues();
		currentLeave.setDirty(true);
		leavesDao.save(currentLeave);
		Utils.sync(getApplicationContext());
		finish();
	}

	private void discard() {
		finish();
	}

	private boolean hasUnsavedChanges() {
		return !(TextUtils.equals(originalLeave.getEmployeeRemarks(),
				currentLeave.getEmployeeRemarks())
				&& Utils.datesEqual(originalLeave.getStartTime(),
						currentLeave.getStartTime()) && Utils.datesEqual(
				originalLeave.getStartTime(), currentLeave.getStartTime()));
	}

	public void onStartDateButtonClick(View view) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentLeave.getStartTime());

		datePickerDialog = new EffortDatePickerDialog(R.id.startDateButton,
				this, this, cal);
		datePickerDialog.show();
	}

	public void onEndDateButtonClick(View view) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentLeave.getEndTime());

		datePickerDialog = new EffortDatePickerDialog(R.id.endDateButton, this,
				this, cal);
		datePickerDialog.show();
	}

	// update text of the date time buttons
	// based on the data from currentJob
	private void updateDateTimeButtons() {
		startDateButton.setText(dateFormat.format(currentLeave.getStartTime()));
		startTimeButton.setText(timeFormat.format(currentLeave.getStartTime()));
		endDateButton.setText(dateFormat.format(currentLeave.getEndTime()));
		endTimeButton.setText(timeFormat.format(currentLeave.getEndTime()));
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Date set for " + datePickerDialog.getUsedForViewId()
					+ ", " + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
		}

		Date date;
		Calendar cal;
		long offsetMillis;

		switch (datePickerDialog.getUsedForViewId()) {
		case R.id.startDateButton:
			offsetMillis = currentLeave.getEndTime().getTime()
					- currentLeave.getStartTime().getTime();
			date = currentLeave.getStartTime();
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			currentLeave.setStartTime(cal.getTime());

			if (currentLeave.getStartTime()
					.compareTo(currentLeave.getEndTime()) > 0) {
				cal.add(Calendar.MILLISECOND, (int) offsetMillis);
				currentLeave.setEndTime(cal.getTime());
			}

			break;

		case R.id.endDateButton:
			offsetMillis = currentLeave.getStartTime().getTime()
					- currentLeave.getEndTime().getTime();
			date = currentLeave.getEndTime();
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			currentLeave.setEndTime(cal.getTime());

			if (currentLeave.getEndTime()
					.compareTo(currentLeave.getStartTime()) < 0) {
				cal.add(Calendar.MILLISECOND, (int) offsetMillis);
				currentLeave.setStartTime(cal.getTime());
			}

			break;

		default:
			if (BuildConfig.DEBUG) {
				Log.w(TAG,
						"Date picker launched for unknown control. Please fix this.");
			}
		}

		updateDateTimeButtons();
	}

	public void onStartTimeButtonClick(View view) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentLeave.getStartTime());

		timePickerDialog = new EffortTimePickerDialog(R.id.startTimeButton,
				this, this, cal);
		timePickerDialog.show();
	}

	public void onEndTimeButtonClick(View view) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentLeave.getEndTime());

		timePickerDialog = new EffortTimePickerDialog(R.id.endTimeButton, this,
				this, cal);
		timePickerDialog.show();
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Time set for " + timePickerDialog.getUsedForViewId()
					+ ", " + hourOfDay + ":" + minute);
		}

		Date date;
		Calendar cal;
		long offsetMillis;

		switch (timePickerDialog.getUsedForViewId()) {
		case R.id.startTimeButton:
			offsetMillis = currentLeave.getEndTime().getTime()
					- currentLeave.getStartTime().getTime();
			date = currentLeave.getStartTime();
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			currentLeave.setStartTime(cal.getTime());

			if (currentLeave.getStartTime()
					.compareTo(currentLeave.getEndTime()) >= 0) {
				cal.add(Calendar.MILLISECOND, (int) offsetMillis);
				currentLeave.setEndTime(cal.getTime());
			}
			break;

		case R.id.endTimeButton:
			offsetMillis = currentLeave.getStartTime().getTime()
					- currentLeave.getEndTime().getTime();
			date = currentLeave.getEndTime();
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			currentLeave.setEndTime(cal.getTime());

			if (currentLeave.getEndTime()
					.compareTo(currentLeave.getStartTime()) <= 0) {
				cal.add(Calendar.MILLISECOND, (int) offsetMillis);
				currentLeave.setStartTime(cal.getTime());
			}

			break;

		default:
			if (BuildConfig.DEBUG) {
				Log.w(TAG,
						"Time picker launched for unknown control. Please fix this.");
			}
		}

		updateDateTimeButtons();
	}

}

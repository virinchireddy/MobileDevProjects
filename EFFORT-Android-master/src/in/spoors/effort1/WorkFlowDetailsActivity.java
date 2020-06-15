package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.EmployeesDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dao.WorkFlowStatusDao;
import in.spoors.effort1.dto.Form;
import in.spoors.effort1.dto.WorkFlowStatusDto;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Settings;
import in.spoors.effort1.provider.EffortProvider.WorkFlowHistories;

import java.text.SimpleDateFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class WorkFlowDetailsActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, TabListener {

	public static final String TAG = "WorkFlowDetailsActivity";
	private static final String FORM_VIEW = "view";
	private static final int FILL_FORM = 45;
	private long localFormId;
	private String localWorkflowName;
	private String localFormName;
	private String localStageName;

	// private TextView formNameTextView;
	private TextView workflowNameTextView;
	private TextView stageNameTextView;

	private ListView listView;
	private SimpleCursorAdapter adapter;
	String[] columns = null;
	private FormsDao formsDao;
	private SettingsDao settingsDao;
	private WorkFlowStatusDao workFlowStatusDao;
	private EmployeesDao employeesDao;
	Context context;
	String remarks = "";
	public static String TITLE_DETAILS = "Details";
	public static String TITLE_HISTORY = "History";
	private WorkFlowDetailFragment detailsFragment;
	private WorkflowDetailsHistoryFragment detailsHistoryFragment;
	public static String SELECTED_TAB_POSITION = "selectedTabPosition";
	private int selectedTabPosition;
	Long empLongId = null;
	private String singularFormLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_work_flow);
		context = this;
		if (savedInstanceState == null) {
			localFormId = getIntent().getLongExtra("localFormId", 0);
			localWorkflowName = getIntent().getStringExtra("workflowName");
			localFormName = getIntent().getStringExtra("formIdentifier");
			localStageName = getIntent().getStringExtra("stageName");
			selectedTabPosition = 0;

		} else {
			localFormId = savedInstanceState.getLong("localFormId");
			localWorkflowName = savedInstanceState.getString("workflowName");
			localFormName = savedInstanceState.getString("formIdentifier");
			localStageName = savedInstanceState.getString("stageName");
			selectedTabPosition = getIntent().getIntExtra(
					SELECTED_TAB_POSITION, 0);
		}
		formsDao = FormsDao.getInstance(getApplicationContext());
		employeesDao = EmployeesDao.getInstance(getApplicationContext());

		detailsFragment = (WorkFlowDetailFragment) getSupportFragmentManager()
				.findFragmentById(R.id.workflowDetailFragment);
		detailsHistoryFragment = (WorkflowDetailsHistoryFragment) getSupportFragmentManager()
				.findFragmentById(R.id.workflowHistoriesFragment);

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		if (localFormName != null) {
			actionBar.setTitle(localFormName + " details");
		} else {
			actionBar.setTitle(R.string.title_activity_work_flow_details);
		}

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab detailsTab = actionBar.newTab();
		detailsTab.setText(TITLE_DETAILS);
		detailsTab.setTabListener(this);
		actionBar.addTab(detailsTab);

		Tab historyTab = actionBar.newTab();
		historyTab.setText(TITLE_HISTORY);
		historyTab.setTabListener(this);
		actionBar.addTab(historyTab);

		workflowNameTextView = (TextView) findViewById(R.id.workflowTitleTextView);
		stageNameTextView = (TextView) findViewById(R.id.stageTextView);
		listView = (ListView) findViewById(android.R.id.list);
		workflowNameTextView.setText(localWorkflowName);
		stageNameTextView.setText(localStageName);
		settingsDao = SettingsDao.getInstance(getApplicationContext());

		singularFormLabel = settingsDao.getLabel(
				Settings.LABEL_FORM_SINGULAR_KEY,
				Settings.LABEL_FORM_SINGULAR_DEFAULT_VLAUE);
		Button viewFormBtn = (Button) findViewById(R.id.viewFormButton);
		viewFormBtn.setText("View " + singularFormLabel);
		Button reSubmitBtn = (Button) findViewById(R.id.reSubmitButton);
		Button cancelBtn = (Button) findViewById(R.id.cancelButton);
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		workFlowStatusDao = WorkFlowStatusDao
				.getInstance(getApplicationContext());

		String empId = settingsDao.getString("employeeId");

		if (empId != null) {
			empLongId = Long.parseLong(empId);
		}

		Long rank = employeesDao.getEmployeeRankWithId(empLongId);

		WorkFlowStatusDto workFlowStatus = workFlowStatusDao
				.getWorkFlowStatusWithLocalFormId(localFormId);

		boolean isRejected = Utils.isRejected(workFlowStatus.getCurrentRank(),
				rank, workFlowStatus.getManagerRank(),
				workFlowStatus.getEmpGroupId(), workFlowStatus.getStageType(),
				workFlowStatus.getStatus(), empLongId);

		if (workFlowStatus == null
				|| (workFlowStatus.getStatus() != null && workFlowStatus
						.getStatus() == WorkFlowStatusDto.STATUS_CANCELLED)
				|| !isRejected) {
			reSubmitBtn.setEnabled(false);
			cancelBtn.setEnabled(false);
		} else {
			if (isRejected
					&& (workFlowStatus.getStatus() != null && workFlowStatus
							.getStatus() != WorkFlowStatusDto.STATUS_CANCELLED)) {
				reSubmitBtn.setEnabled(true);
				cancelBtn.setEnabled(true);
			} else {
				reSubmitBtn.setEnabled(false);
				cancelBtn.setEnabled(false);
			}
		}

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		if (selectedTabPosition == 0) {

			ft.hide(detailsHistoryFragment);
			ft.show(detailsFragment);
			ft.commit();
		} else {
			ft.hide(detailsFragment);
			ft.show(detailsHistoryFragment);
			ft.commit();
		}

		getSupportLoaderManager().initLoader(0, null, this);

		columns = new String[] { WorkFlowHistories.APPROVED_BY_NAME,
				WorkFlowHistories.APPROVED_TIME, WorkFlowHistories.REMARKS };
		int[] views = { R.id.approvedByTextView, R.id.approvedOnTextView,
				R.id.rearksTextView };
		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_work_flow_details, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);
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

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onResume.");
		}
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("localFormId", localFormId);
		outState.putString("workflowName", localWorkflowName);
		outState.putString("formIdentifier", localFormName);
		outState.putString("stageName", localStageName);
		outState.putInt(SELECTED_TAB_POSITION, selectedTabPosition);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getApplicationContext(),
				WorkFlowHistories.CONTENT_URI, WorkFlowHistories.ALL_COLUMNS,
				WorkFlowHistories.LOCAL_FORM_ID + " = ?", new String[] { ""
						+ localFormId }, null);
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

		@SuppressWarnings("unused")
		private final String TAG = "WorkFlowDetailsActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			switch (columnIndex) {
			case WorkFlowHistories.APPROVED_BY_NAME_INDEX:
				TextView tv = (TextView) view;
				String stageName = cursor
						.isNull(WorkFlowHistories.STAGE_NAME_INDEX) ? ""
						: cursor.getString(WorkFlowHistories.STAGE_NAME_INDEX);

				String statusMessage = cursor
						.isNull(WorkFlowHistories.STATUS_MESSAGE_INDEX) ? ""
						: cursor.getString(WorkFlowHistories.STATUS_MESSAGE_INDEX);

				String approvedByName = cursor
						.isNull(WorkFlowHistories.APPROVED_BY_NAME_INDEX) ? ""
						: cursor.getString(WorkFlowHistories.APPROVED_BY_NAME_INDEX)
								+ "(" + stageName + ")";
				String prefix = "";
				// int status = cursor.getInt(WorkFlowHistories.STATUS_INDEX);
				Long status = cursor.isNull(WorkFlowHistories.STATUS_INDEX) ? null
						: cursor.getLong(WorkFlowHistories.STATUS_INDEX);

				if (status == WorkFlowStatusDto.STATUS_APPROVED) {
					prefix = "Approved by : ";
				} else if (status == WorkFlowStatusDto.STATUS_CANCELLED) {
					prefix = "Cancelled by : ";
				} else if (status == WorkFlowStatusDto.STATUS_REJECTED) {
					prefix = "Rejected by : ";
				} else if (status == WorkFlowStatusDto.STATUS_RESUBMITTED) {
					prefix = "Resubmitted by : ";
				} else if (status == WorkFlowStatusDto.STATUS_WAITING) {
					prefix = "Submitted by : ";
					String stageNameValue = cursor
							.isNull(WorkFlowHistories.STAGE_NAME_INDEX) ? ""
							: cursor.getString(WorkFlowHistories.STAGE_NAME_INDEX);
					approvedByName = cursor
							.isNull(WorkFlowHistories.CREATED_BY_NAME_INDEX) ? ""
							: cursor.getString(WorkFlowHistories.CREATED_BY_NAME_INDEX)
									+ "(" + stageNameValue + ")";
				}
				if (!TextUtils.isEmpty(statusMessage)) {
					if (statusMessage
							.equalsIgnoreCase("Rejected by the system")
							|| statusMessage
									.equalsIgnoreCase("Cancelled by the system")) {
						approvedByName = statusMessage;
					}
				}

				tv.setText(prefix + approvedByName);
				return true;

			case WorkFlowHistories.APPROVED_TIME_INDEX:
				TextView time = (TextView) view;
				String approvedAt = "";
				SimpleDateFormat dateTimeFormat = Utils
						.getDateTimeFormat(getApplicationContext());

				String timeString = cursor
						.isNull(WorkFlowHistories.APPROVED_TIME_INDEX) ? ""
						: cursor.getString(WorkFlowHistories.APPROVED_TIME_INDEX);
				if (!TextUtils.isEmpty(timeString)) {
					approvedAt = dateTimeFormat.format(SQLiteDateTimeUtils
							.getLocalTime(timeString));
				}
				String prefixForTime = "";
				int statusForTime = cursor
						.getInt(WorkFlowHistories.STATUS_INDEX);
				if (statusForTime == WorkFlowStatusDto.STATUS_APPROVED) {
					prefixForTime = "Approved on : ";
				} else if (statusForTime == WorkFlowStatusDto.STATUS_CANCELLED) {
					prefixForTime = "Cancelled on : ";
				} else if (statusForTime == WorkFlowStatusDto.STATUS_REJECTED) {
					prefixForTime = "Rejected on : ";
				} else if (statusForTime == WorkFlowStatusDto.STATUS_RESUBMITTED) {
					prefixForTime = "Resubmitted on : ";
				} else if (statusForTime == WorkFlowStatusDto.STATUS_WAITING) {
					prefixForTime = "Submitted on : ";
					timeString = cursor
							.isNull(WorkFlowHistories.CREATED_TIME_INDEX) ? ""
							: cursor.getString(WorkFlowHistories.CREATED_TIME_INDEX);
					if (!TextUtils.isEmpty(timeString)) {
						approvedAt = dateTimeFormat.format(SQLiteDateTimeUtils
								.getLocalTime(timeString));
					}
				}
				time.setText(prefixForTime + approvedAt);

				return true;

			case WorkFlowHistories.REMARKS_INDEX:
				TextView remarksTxt = (TextView) view;
				String remarks = cursor.isNull(WorkFlowHistories.REMARKS_INDEX) ? ""
						: cursor.getString(WorkFlowHistories.REMARKS_INDEX);

				remarksTxt.setText(Html.fromHtml("Remarks : " + remarks));
				return true;

			default:
				return false;
			}
		}
	}

	public void onReSubmitButtonClick(View view) {

		showRemarksDialog();

	}

	public void onCancelButtonClick(View view) {
		WorkFlowStatusDto workFlowStatus = workFlowStatusDao
				.getWorkFlowStatusWithLocalFormId(localFormId);
		workFlowStatus.setStatus(WorkFlowStatusDto.STATUS_CANCELLED);
		workFlowStatus.setStatusMessage("Cancelled");
		workFlowStatus.setRemarks("");
		workFlowStatus.setDirty(true);
		workFlowStatusDao.save(workFlowStatus);
		Utils.sync(getApplicationContext());
		finish();
	}

	public void onViewFormButtonClick(View view) {

		Form form = formsDao.getFormWithLocalId(localFormId);

		Intent intent = new Intent(this, FormActivity.class);
		intent.setAction(FormActivity.ACTION_OPEN_WORKFLOW_FORM);
		if (form != null) {
			long formSpecId = form.getFormSpecId();

			intent.putExtra(EffortProvider.Forms._ID, form.getLocalId());
			intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID, formSpecId);
			long formView = 1;
			intent.putExtra(FORM_VIEW, formView);
			startActivityForResult(intent, FILL_FORM);

		} else {
			return;
		}

	}

	void showRemarksDialog() {
		remarks = "";
		final EditText remarksEditText = new EditText(this);
		new AlertDialog.Builder(this)
				.setTitle("Remarks")
				.setMessage(R.string.workflow_remarks_label)
				.setView(remarksEditText)
				.setPositiveButton(R.string.workflow_remarks_submit_label,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								remarks = remarksEditText.getText().toString();
								resubmitForm(remarks);
							}
						}).show();

	}

	void resubmitForm(String remarks) {

		WorkFlowStatusDto workFlowStatus = workFlowStatusDao
				.getWorkFlowStatusWithLocalFormId(localFormId);

		workFlowStatus.setStatus(WorkFlowStatusDto.STATUS_RESUBMITTED);
		workFlowStatus.setStatusMessage("Resubmitted");
		workFlowStatus.setDirty(true);
		workFlowStatus.setRemarks(remarks);
		workFlowStatusDao.save(workFlowStatus);
		Utils.sync(getApplicationContext());

		finish();
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
		if (TITLE_DETAILS.equals(tab.getText())) {
			ft.hide(detailsHistoryFragment);
			ft.show(detailsFragment);
		} else if (TITLE_HISTORY.equals(tab.getText())) {
			ft.hide(detailsFragment);
			ft.show(detailsHistoryFragment);
			getSupportLoaderManager().restartLoader(0, null, this);
		}

	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {

	}
}
package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.EmployeesDao;
import in.spoors.effort1.dao.EntitiesDao;
import in.spoors.effort1.dao.FieldValueSpecsDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dao.WorkFlowFormSpecMappingsDao;
import in.spoors.effort1.dao.WorkFlowStatusDao;
import in.spoors.effort1.dto.WorkFlowFormSpecFilter;
import in.spoors.effort1.dto.WorkFlowFormSpecMapping;
import in.spoors.effort1.dto.WorkFlowStatusDto;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;
import in.spoors.effort1.provider.EffortProvider.Fields;
import in.spoors.effort1.provider.EffortProvider.FormSpecs;
import in.spoors.effort1.provider.EffortProvider.Forms;
import in.spoors.effort1.provider.EffortProvider.WorkFlowFormSpecMappings;
import in.spoors.effort1.provider.EffortProvider.WorkFlowStatus;
import in.spoors.effort1.provider.EffortProvider.WorkFlowsView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class WorkFlowsActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, OnCheckedChangeListener,
		RefreshListener {

	public static final String TAG = "WorkFlowsActivity";
	private ListView listView;
	private SimpleCursorAdapter adapter;
	private DrawerFragment drawerFragment;
	private SettingsDao settingsDao;
	private WorkFlowFormSpecMappingsDao workFlowFormSpecMappingsDao;

	private CustomersDao customersDao;
	private EmployeesDao employeesDao;
	private EntitiesDao entitiesDao;
	private WorkFlowStatusDao workFlowStatusDao;
	private FieldValueSpecsDao fieldValueSpecsDao;
	// private WorkFlowStagesDao workFlowStagesDao;
	private SimpleDateFormat dateFormat;
	public static String SCREEN_PURPOSE = "key_purpose";
	public static String FORM_CREATE = "create";
	public static String FORM_SELECT = "select";
	public static String onBackToHomeKey = "onBackToHome";
	boolean onBackToHome = false;
	private String screenPurpose;
	TextView textView;
	Long rank;
	Long empLongId = null;
	private Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_work_flows);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}
		setSupportProgressBarIndeterminateVisibility(false);
		
		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		new CleanUpTask().execute();
		textView = (TextView) findViewById(android.R.id.empty);
		if (savedInstanceState == null) {
			screenPurpose = getIntent().getStringExtra(
					WorkFlowsActivity.SCREEN_PURPOSE);
			onBackToHome = getIntent().getBooleanExtra(
					WorkFlowsActivity.onBackToHomeKey, true);

		} else {
			screenPurpose = savedInstanceState
					.getString(WorkFlowsActivity.SCREEN_PURPOSE);
			onBackToHome = savedInstanceState.getBoolean(
					WorkFlowsActivity.onBackToHomeKey, true);
		}

		if (screenPurpose != null
				&& screenPurpose.equalsIgnoreCase(FORM_CREATE)) {
			actionBar.setTitle(R.string.title_activity_work_flows);
			textView.setText(R.string.no_work_flows);
		} else {
			actionBar.setTitle(R.string.title_pending_submissions);
			textView.setText(R.string.no_pending_submisssions);
		}

		settingsDao = SettingsDao.getInstance(getApplicationContext());

		workFlowFormSpecMappingsDao = WorkFlowFormSpecMappingsDao
				.getInstance(getApplicationContext());
		workFlowStatusDao = WorkFlowStatusDao
				.getInstance(getApplicationContext());
		/*
		 * workFlowStagesDao = WorkFlowStagesDao
		 * .getInstance(getApplicationContext());
		 */
		customersDao = CustomersDao.getInstance(getApplicationContext());
		employeesDao = EmployeesDao.getInstance(getApplicationContext());
		entitiesDao = EntitiesDao.getInstance(getApplicationContext());
		fieldValueSpecsDao = FieldValueSpecsDao
				.getInstance(getApplicationContext());

		dateFormat = Utils.getDateFormat(getApplicationContext());
		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);
		getSupportLoaderManager().initLoader(0, null, this);
		String empId = settingsDao.getString("employeeId");

		if (empId != null) {
			empLongId = Long.parseLong(empId);
		}
		if (empLongId != null) {
			rank = employeesDao.getEmployeeRankWithId(empLongId);
		}

		String[] columns = { WorkFlowStatus.STATUS, Fields.LOCAL_VALUE,
				FormSpecs.TITLE, WorkFlowStatus.STATUS_MESSAGE,
				WorkFlowStatus.DIRTY };

		int[] views = { R.id.typeImageView, R.id.titleTextView,
				R.id.extraInfoTextView, R.id.timeTextView, R.id.syncImageView };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_work_flow, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);

		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_work_flow,
				"Approvals", null, this);

	}

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
            super.onPostCreate(savedInstanceState);
            // Sync the toggle state after onRestoreInstanceState has occurred.
            if (screenPurpose != null
                            && screenPurpose.equalsIgnoreCase(FORM_CREATE)) {
                    drawerFragment.onPostCreate(savedInstanceState);
            }
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(WorkFlowsActivity.SCREEN_PURPOSE, screenPurpose);
		outState.putBoolean(WorkFlowsActivity.onBackToHomeKey, onBackToHome);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// screenPurpose
		getMenuInflater().inflate(R.menu.work_flows, menu);
		this.menu = menu;
		if (screenPurpose != null
				&& screenPurpose.equalsIgnoreCase(FORM_SELECT)) {
			menu.setGroupVisible(R.id.workFlowGroup, false);
		} else {
			SubMenu subMenu = menu.addSubMenu(0, -1, 100, "Filter");
			MenuItem filterMenuItem = subMenu.getItem();
			filterMenuItem.setIcon(R.drawable.ic_content_filter);
			MenuItemCompat.setShowAsAction(filterMenuItem,
					MenuItemCompat.SHOW_AS_ACTION_ALWAYS
							| MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);

			int order = 0;

			List<WorkFlowFormSpecFilter> allWorkFlowFormMappingData = workFlowFormSpecMappingsDao
					.getAllWorkFlowFormMappingData(null);

			if (allWorkFlowFormMappingData != null) {
				for (WorkFlowFormSpecFilter workFlowSpec : allWorkFlowFormMappingData) {
					MenuItem menuItem = subMenu.add(0,
							(int) (long) workFlowSpec.getId(), order,
							workFlowSpec.getFormTemplateName());
					menuItem.setCheckable(true);
					menuItem.setChecked(workFlowSpec.getChecked());
					order = order + 1;
				}
			}

			filterMenuItem.setVisible(drawerFragment != null
					&& !drawerFragment.isDrawerOpen());
		}

		return true;
	}

	@Override
	public void onRefresh() {
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerFragment.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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

		if (FORM_CREATE.equalsIgnoreCase(screenPurpose)) {
			String identifier = ((TextView) view
					.findViewById(R.id.titleTextView)).getText().toString();
			String stageName = (((TextView) view
					.findViewById(R.id.timeTextView)).getTag().toString())
					+ "("
					+ ((TextView) view.findViewById(R.id.timeTextView))
							.getText().toString() + ")";
			String workFlowName = ((TextView) view
					.findViewById(R.id.extraInfoTextView)).getText().toString();
			Long localFormId = (Long) ((TextView) view
					.findViewById(R.id.titleTextView)).getTag();
			Intent intent = new Intent(this, WorkFlowDetailsActivity.class);

			intent.putExtra("localFormId", localFormId);
			intent.putExtra("workflowName", workFlowName);
			intent.putExtra("formIdentifier", identifier);
			intent.putExtra("stageName", stageName);

			startActivity(intent);

		} else {
			Long localFormId = (Long) ((TextView) view
					.findViewById(R.id.titleTextView)).getTag();
			FormsDao formsDao = FormsDao.getInstance(getApplicationContext());
			Long formSpecId = formsDao.getFormSpecId(localFormId);
			if (localFormId == 0 || formSpecId == null || formSpecId == 0) {
				return;
			}
			Intent intent = new Intent(this, FormActivity.class);
			intent.putExtra(EffortProvider.Forms._ID, localFormId);
			intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID, formSpecId);
			startActivity(intent);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader.");
		}
		// formSpecUniqueId
		String formIds = workFlowStatusDao.getFormIdsIn();
		String inCondition;
		// String workFlowIds;
		String filterTemporary = "";
		if (TextUtils.isEmpty(formIds)) {
			formIds = "()";
		}
		if (FORM_CREATE.equalsIgnoreCase(screenPurpose)) {
			// workFlowIds = workFlowSpecsDao
			// .getCheckedWorkFlowIdsFromWorkFlowSpecs();
			inCondition = " IN ";
			String[] uniqueIds = workFlowFormSpecMappingsDao
					.getSelectedWorkFlowFormSpecUniqueIds(WorkFlowFormSpecMappings.TABLE
							+ "."
							+ WorkFlowFormSpecMappings.CHECKED
							+ " = 'true'");
			String join = null;
			if (uniqueIds != null) {
				join = TextUtils.join(",", uniqueIds);
				if (!TextUtils.isEmpty(join)) {
					filterTemporary = Forms.TABLE + "."
							+ Forms.FORM_SPEC_UNIQUE_ID + " IN (" + join + ")";
				}
			} else {
				filterTemporary = Forms.TABLE + "." + Forms.FORM_SPEC_UNIQUE_ID
						+ " IN ()";
			}

			return new CursorLoader(getApplicationContext(),
					WorkFlowsView.CONTENT_URI, WorkFlowsView.ALL_COLUMNS,
					filterTemporary, null, screenPurpose);
		} else {

			inCondition = " NOT IN ";

			filterTemporary = " AND " + Forms.TABLE + "." + Forms.TEMPORARY
					+ "='false' AND " + Forms.TABLE + "."
					+ Forms.FORM_SPEC_UNIQUE_ID + " = '"
					+ getIntent().getStringExtra("formSpecUniqueId") + "'";
			// screenPurpose for differentiating pending submissions and
			// workflow
			// submissions
			return new CursorLoader(getApplicationContext(),
					WorkFlowsView.CONTENT_URI, WorkFlowsView.ALL_COLUMNS,
					WorkFlowsView.LOCAL_FORM_ID + inCondition + formIds
							+ filterTemporary, null, screenPurpose);
		}

	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}
		setSupportProgressBarIndeterminateVisibility(false);
		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoaderReset.");
		}

		// swapCursor method requires API level 11 or higher.
		// Thus, use SimpleCursorAdapter from support library.
		adapter.swapCursor(null);
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
	public void onBackPressed() {
		Intent intent;

		if (onBackToHome) {
			intent = new Intent(this, HomeActivity.class);
		} else {
			intent = new Intent(this, WorkFlowFormSpecsMappingsActivity.class);
		}

		startActivity(intent);

		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}
		int itemId = item.getItemId();
		switch (itemId) {

		case android.R.id.home:
			onBackPressed();
			break;

		case R.id.addWorkFlow:
			Intent intent = new Intent(this,
					WorkFlowFormSpecsMappingsActivity.class);
			startActivity(intent);
			break;

		default:

			WorkFlowFormSpecMapping specMappingWithId = workFlowFormSpecMappingsDao
					.getWorkFlowFormSpecMappingWithId(itemId);
			if (specMappingWithId != null) {
				specMappingWithId.setChecked(!item.isChecked());
				workFlowFormSpecMappingsDao.save(specMappingWithId);
				if (menu != null) {
					menu.clear();
				}
				onCreateOptionsMenu(menu);
				getSupportLoaderManager().restartLoader(0, null, this);

			}

		}

		return super.onOptionsItemSelected(item);
	}

	class MyViewBinder implements ViewBinder {
		// private final String TAG = "WorkFlowsActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

			Long status = cursor.isNull(WorkFlowsView.STATUS_INDEX) ? null
					: cursor.getLong(WorkFlowsView.STATUS_INDEX);
			Long currentRank = cursor.isNull(WorkFlowsView.CURRENT_RANK_INDEX) ? null
					: cursor.getLong(WorkFlowsView.CURRENT_RANK_INDEX);
			Long stageType = cursor.isNull(WorkFlowsView.STAGE_TYPE_INDEX) ? null
					: cursor.getLong(WorkFlowsView.STAGE_TYPE_INDEX);
			Long managerRank = cursor.isNull(WorkFlowsView.MANAGER_RANK_INDEX) ? null
					: cursor.getLong(WorkFlowsView.MANAGER_RANK_INDEX);
			Long empGroupId = cursor.isNull(WorkFlowsView.EMP_GROUP_ID_INDEX) ? null
					: cursor.getLong(WorkFlowsView.EMP_GROUP_ID_INDEX);

			switch (columnIndex) {
			case WorkFlowsView.STATUS_INDEX:
				if (view.getId() == R.id.typeImageView) {

					ImageView imageView = (ImageView) view;

					if (status != null) {
						if (status == WorkFlowStatusDto.STATUS_REJECTED) {
							imageView
									.setImageResource(R.drawable.ic_drawer_work_flow_rejected);
						} else if (status == WorkFlowStatusDto.STATUS_CANCELLED) {
							imageView
									.setImageResource(R.drawable.ic_drawer_work_flow_cancelled);
						} else if (status == WorkFlowStatusDto.STATUS_APPROVED) {
							// if (currentRank != null && rank != null
							// && (currentRank == rank)) {
							imageView
									.setImageResource(R.drawable.ic_drawer_work_flow_accepted);
							// } else {
							// imageView
							// .setImageResource(R.drawable.ic_drawer_work_flow);
							// }
						} else if (status == WorkFlowStatusDto.STATUS_RESUBMITTED) {
							imageView
									.setImageResource(R.drawable.ic_drawer_work_flow_resubmitted);
						} else {
							imageView
									.setImageResource(R.drawable.ic_drawer_work_flow);
						}
					}
				}
				return true;
			case WorkFlowsView.LOCAL_VALUE_INDEX:
				// USE COMMON METHOD TO GET IDENTIFIER
				String value = getIdentifierValue(cursor);
				boolean rejected = Utils.isRejected(currentRank, rank,
						managerRank, empGroupId, stageType, status, empLongId);

				TextView tv = (TextView) view;

				tv.setText(value);
				Long formId = cursor.isNull(WorkFlowsView.LOCAL_FORM_ID_INDEX) ? null
						: cursor.getLong(WorkFlowsView.LOCAL_FORM_ID_INDEX);

				if (rejected && status != null
						&& status != WorkFlowStatusDto.STATUS_CANCELLED) {
					tv.setTextColor(0xffff0000);
				} else {
					tv.setTextColor(0xff000000);
				}

				tv.setTag(formId);

				return true;

			case WorkFlowsView.FORM_SPEC_NAME_INDEX:

				TextView workFlowNameTV = (TextView) view;
				workFlowNameTV.setText(cursor
						.isNull(WorkFlowsView.FORM_SPEC_NAME_INDEX) ? ""
						: cursor.getString(WorkFlowsView.FORM_SPEC_NAME_INDEX));

				boolean isRejected = Utils.isRejected(currentRank, rank,
						managerRank, empGroupId, stageType, status, empLongId);

				if (isRejected && status != null
						&& status != WorkFlowStatusDto.STATUS_CANCELLED) {
					workFlowNameTV.setTextColor(0xffff0000);
				} else {
					workFlowNameTV.setTextColor(0xff000000);
				}

				return true;

			case WorkFlowsView.STATUS_MESSAGE_INDEX:

				TextView tv1 = (TextView) view;
				String statusMessage = cursor
						.isNull(WorkFlowsView.STATUS_MESSAGE_INDEX) ? ""
						: cursor.getString(WorkFlowsView.STATUS_MESSAGE_INDEX);
				String stageName = cursor
						.isNull(WorkFlowsView.WORK_FLOW_STAGE_NAME_INDEX) ? ""
						: cursor.getString(WorkFlowsView.WORK_FLOW_STAGE_NAME_INDEX);
				tv1.setText(statusMessage);

				tv1.setTag(stageName);

				boolean isRejected1 = Utils.isRejected(currentRank, rank,
						managerRank, empGroupId, stageType, status, empLongId);

				if (isRejected1 && status != null
						&& status != WorkFlowStatusDto.STATUS_CANCELLED) {
					tv1.setTextColor(0xffff0000);
				} else {
					tv1.setTextColor(0xff000000);
				}

				return true;
			case WorkFlowsView.TREE_DIRTY_INDEX:
				ImageView syncImageView = (ImageView) view;

				if ("true".equals(cursor
						.getString(WorkFlowsView.TREE_DIRTY_INDEX))) {
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

	private String getIdentifierValue(Cursor cursor) {
		String value = cursor.getString(WorkFlowsView.LOCAL_VALUE_INDEX);

		if (TextUtils.isEmpty(value)) {
			value = cursor.getString(WorkFlowsView.REMOTE_VALUE_INDEX);
		}

		if (cursor.getInt(WorkFlowsView.TYPE_INDEX) == FieldSpecs.TYPE_CUSTOMER
				&& !TextUtils.isEmpty(value)) {
			value = customersDao.getCompanyNameWithLocalId(Long
					.parseLong(value));
		}

		if (cursor.getInt(WorkFlowsView.TYPE_INDEX) == FieldSpecs.TYPE_EMPLOYEE
				&& !TextUtils.isEmpty(value)) {
			value = employeesDao.getEmployeeNameWithLocalId(Long
					.parseLong(value));
		}

		if (cursor.getInt(WorkFlowsView.TYPE_INDEX) == FieldSpecs.TYPE_ENTITY
				&& !TextUtils.isEmpty(value)) {
			value = entitiesDao.getEntityName(Long.parseLong(value));
		}

		if (cursor.getInt(WorkFlowsView.TYPE_INDEX) == FieldSpecs.TYPE_MULTI_LIST
				&& !TextUtils.isEmpty(value)) {
			String[] ids = value.split(",");
			ArrayList<String> displayValues = new ArrayList<String>();
			for (int i = 0; i < ids.length; i++) {
				ids[i] = ids[i].trim();
				displayValues.add(entitiesDao.getEntityName(Long
						.parseLong(ids[i])));
			}
			value = TextUtils.join(", ", displayValues);
		}
		if (cursor.getInt(WorkFlowsView.TYPE_INDEX) == FieldSpecs.TYPE_MULTI_SELECT_LIST
				&& !TextUtils.isEmpty(value)) {
			String idsAsString = value;
			if (!TextUtils.isEmpty(idsAsString)) {
				String[] ids = idsAsString.split(",");
				ArrayList<String> values = new ArrayList<String>();
				for (int i = 0; i < ids.length; i++) {
					ids[i] = ids[i].trim();
					values.add(fieldValueSpecsDao.getValue(Long
							.parseLong(ids[i])));
				}
				value = TextUtils.join(", ", values);
			}
		}
		if (!TextUtils.isEmpty(value)) {
			switch (cursor.getInt(WorkFlowsView.TYPE_INDEX)) {
			case FieldSpecs.TYPE_DATE:
				value = dateFormat.format(SQLiteDateTimeUtils.getDate(value));
				break;

			case FieldSpecs.TYPE_YES_OR_NO:
				value = Boolean.parseBoolean(value) ? "Yes" : "No";
				break;

			case FieldSpecs.TYPE_SINGLE_SELECT_LIST:
				value = fieldValueSpecsDao.getValue(Long.parseLong(value));
				break;
			}
		}
		return value;
	}

	private class CleanUpTask extends AsyncTask<String, Integer, Long> {

		public static final String TAG = "WorkFlowsActivity.CleanUpTask";

		@Override
		protected Long doInBackground(String... params) {
			Log.i(TAG, "doInBackground");
			WorkFlowStatusDao workFlowStatusDao = WorkFlowStatusDao
					.getInstance(getApplicationContext());
			long deletedWorkFlowStatus = workFlowStatusDao.deleteOldWorkFlows();

			return deletedWorkFlowStatus;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Fetch finished.");
			}

			getSupportLoaderManager().initLoader(0, null,
					WorkFlowsActivity.this);
		}
	}

}

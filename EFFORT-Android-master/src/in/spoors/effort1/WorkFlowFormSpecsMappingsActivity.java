package in.spoors.effort1;

import in.spoors.effort1.dao.EmployeesDao;
import in.spoors.effort1.dao.FormSpecsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dao.WorkFlowHistoriesDao;
import in.spoors.effort1.dao.WorkFlowSpecsDao;
import in.spoors.effort1.dao.WorkFlowStagesDao;
import in.spoors.effort1.dao.WorkFlowStatusDao;
import in.spoors.effort1.dto.WorkFlowFormSpecMapping;
import in.spoors.effort1.dto.WorkFlowSpec;
import in.spoors.effort1.dto.WorkFlowStage;
import in.spoors.effort1.dto.WorkFlowStatusDto;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.FormSpecs;
import in.spoors.effort1.provider.EffortProvider.Settings;
import in.spoors.effort1.provider.EffortProvider.WorkFlowSpecs;
import in.spoors.effort1.provider.EffortProvider.WorkFlowSpecsView;

import java.util.Date;

import android.app.Activity;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WorkFlowFormSpecsMappingsActivity extends ActionBarActivity
		implements LoaderCallbacks<Cursor>, OnItemClickListener,
		OnCheckedChangeListener, RefreshListener, OnClickListener {
	public static final String TAG = "WorkFlowFormSpecsMappingsActivity";
	public static final int FILL_FORM = 2;
	public static final String WORK_FLOW_ID = "workFlowId";
	private ListView listView;
	private SimpleCursorAdapter adapter;
	WorkFlowSpecsDao workFlowSpecsDao;
	WorkFlowStatusDao workFlowStatusDao;
	WorkFlowHistoriesDao workFlowHistoriesDao;
	private SettingsDao settingsDao;
	private WorkFlowStagesDao workFlowStagesDao;
	private String screenPurpose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_work_flow_specs);

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle(R.string.title_activity_work_flow_specs);

		if (savedInstanceState == null) {
			screenPurpose = getIntent().getStringExtra(
					WorkFlowsActivity.SCREEN_PURPOSE);
		} else {
			screenPurpose = savedInstanceState
					.getString(WorkFlowsActivity.SCREEN_PURPOSE);
		}

		settingsDao = SettingsDao.getInstance(getApplicationContext());
		workFlowSpecsDao = WorkFlowSpecsDao
				.getInstance(getApplicationContext());
		workFlowStatusDao = WorkFlowStatusDao
				.getInstance(getApplicationContext());
		workFlowHistoriesDao = WorkFlowHistoriesDao
				.getInstance(getApplicationContext());
		workFlowStagesDao = WorkFlowStagesDao
				.getInstance(getApplicationContext());
		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);
		getSupportLoaderManager().initLoader(0, null, this);

		// String[] columns = { EffortProvider.FormSpecs.TITLE,
		// EffortProvider.WorkFlowSpecs._ID };
		String[] columns = { WorkFlowSpecsView.FORM_SPEC_TITLE,
				EffortProvider.WorkFlowSpecs._ID };
		int[] views = { R.id.titleTV, R.id.alternateAB };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_work_flow_spec, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.work_flow_specs, menu);
		return true;
	}

	@Override
	public void onRefresh() {
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Parent: " + parent.toString() + ", View: "
							+ view.toString() + ", Position: " + position
							+ ", ID: " + id);
		}
		TextView textViewForEntitySpecMapId = (TextView) view
				.findViewById(R.id.titleTV);
		String formSpecUniqueId = (String) textViewForEntitySpecMapId.getTag();

		long workFlowId = id;
		WorkFlowSpec workFlowSpec = workFlowSpecsDao
				.getWorkFlowSpecWithId(workFlowId);

		if (workFlowSpec == null) {
			return;
		}
		if (workFlowSpec.getHasRoleBasedStages() != null
				&& workFlowSpec.getHasRoleBasedStages() == true) {
			EmployeesDao employeesDao = EmployeesDao
					.getInstance(getApplicationContext());
			String empId = settingsDao.getString("employeeId");
			Long empLongId = null;

			if (empId != null) {
				empLongId = Long.parseLong(empId);
			}

			// IF RANK IS 0 and canSubmit is true we no need to submit
			// the form to
			// workflow

			String singularEmployee = settingsDao.getLabel(
					Settings.LABEL_EMPLOYEE_SINGULAR_KEY,
					Settings.LABEL_EMPLOYEE_SINGULAR_DEFAULT_VLAUE);

			Long rank = employeesDao.getEmployeeRankWithId(empLongId);
			if (rank != null && rank == 0 && workFlowSpec != null
					&& workFlowSpec.getHasRoleBasedStages()) {
				Toast.makeText(getApplicationContext(),
						"The " + singularEmployee + " doesn't have any role.",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		finish();

		Intent intent = new Intent(this, WorkFlowsActivity.class);
		intent.putExtra(WorkFlowsActivity.SCREEN_PURPOSE,
				WorkFlowsActivity.FORM_SELECT);
		intent.putExtra("formSpecUniqueId", formSpecUniqueId);
		// intent.putExtra(WORK_FLOW_ID, workFlowSpec.getId());
		intent.putExtra(WorkFlowsActivity.onBackToHomeKey, false);
		startActivity(intent);
		// }

	}

	@Override
	public void onClick(View v) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "on  click");
		}
		if (v instanceof ImageButton) {

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "on plus click");
			}

			if (v.getTag() != null) {
				// long workFlowId = (Long) v.getTag();
				// WorkFlowSpec wfsTag = (WorkFlowSpec) v.getTag();
				WorkFlowFormSpecMapping mapping = (WorkFlowFormSpecMapping) v
						.getTag();
				WorkFlowSpec workFlowSpec = workFlowSpecsDao
						.getWorkFlowSpecWithId(mapping.getWorkflowId());
				if (workFlowSpec == null) {
					return;
				}
				if (workFlowSpec.getHasRoleBasedStages() != null
						&& workFlowSpec.getHasRoleBasedStages() == true) {
					EmployeesDao employeesDao = EmployeesDao
							.getInstance(getApplicationContext());
					String empId = settingsDao.getString("employeeId");
					Long empLongId = null;

					if (empId != null) {
						empLongId = Long.parseLong(empId);
					}

					// IF RANK IS 0 and canSubmit is true we no need to submit
					// the form to
					// workflow

					String singularEmployee = settingsDao.getLabel(
							Settings.LABEL_EMPLOYEE_SINGULAR_KEY,
							Settings.LABEL_EMPLOYEE_SINGULAR_DEFAULT_VLAUE);

					Long rank = employeesDao.getEmployeeRankWithId(empLongId);
					if (rank != null && rank == 0 && workFlowSpec != null
							&& workFlowSpec.getHasRoleBasedStages()) {
						Toast.makeText(
								getApplicationContext(),
								"The " + singularEmployee
										+ " doesn't have any role.",
								Toast.LENGTH_SHORT).show();
						return;
					}
				}

				Intent intent = new Intent(this, FormActivity.class);
				intent.setAction(FormActivity.ACTION_CREATE_WORKFLOW_FORM);
				// WorkFlowFormSpecMappingsDao workFlowFormSpecMappingsDao =
				// WorkFlowFormSpecMappingsDao
				// .getInstance(getApplicationContext());
				// Long formSpecId = workFlowFormSpecMappingsDao
				// .getFormSpecId(workFlowSpec.getId());
				FormSpecsDao formSpecsDao = FormSpecsDao
						.getInstance(getApplicationContext());
				Long formSpecId = formSpecsDao
						.getFormSpecIdWithUniqueId(mapping.getMappingEntityId());
				// Long formSpecId = formSp
				if (formSpecId == null) {
					return;
				}

				intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID, formSpecId);
				intent.putExtra(WORK_FLOW_ID, workFlowSpec.getId());

				startActivityForResult(intent, FILL_FORM);
			}
		}
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
				Long localFormId = data.getLongExtra("localFormId", 0);
				Long workFlowId = data.getLongExtra(WORK_FLOW_ID, 0);

				if (localFormId == 0) {
					return;
				}

				WorkFlowStatusDto workFlowStatus = new WorkFlowStatusDto();
				workFlowStatus.setClientFormId(localFormId);
				workFlowStatus.setCreatedBy(settingsDao
						.getLong(Settings.KEY_EMPLOYEE_ID));
				workFlowStatus.setWorkFlowId(workFlowId);

				workFlowStatus.setLocalCreationTime(new Date());
				workFlowStatus.setLocalModificationTime(new Date());
				workFlowStatus.setStatus(WorkFlowStatusDto.STATUS_WAITING);
				WorkFlowStage workFlowStage = workFlowStagesDao
						.getWorkFlowStageWithWorkFlowId(workFlowId);
				workFlowStatus.setStatusMessage("Waiting");
				if (workFlowStage != null) {
					workFlowStatus.setStageName(workFlowStage.getStageName());
				}
				workFlowStatus.setCreatedTime(new Date());
				workFlowStatus.setModifiedTime(new Date());
				workFlowStatus.setDirty(true);
				workFlowStatusDao.save(workFlowStatus);
				Utils.sync(getApplicationContext());
				finish();
				break;

			default:
				break;
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader.");
		}
		String selection = FormSpecs.TABLE + "." + FormSpecs.WITHDRAWN
				+ "='false' AND " + WorkFlowSpecs.TABLE + "."
				+ WorkFlowSpecs.DELETED + "='false'";

		return new CursorLoader(getApplicationContext(),
				WorkFlowSpecsView.CONTENT_URI, WorkFlowSpecsView.ALL_COLUMNS,
				selection, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}

		adapter.swapCursor(cursor);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString(WorkFlowsActivity.SCREEN_PURPOSE, screenPurpose);

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

	class MyViewBinder implements ViewBinder {

		// @SuppressWarnings("unused")
		private final String TAG = "WorkFlowSpecsActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
					+ cursor.getPosition() + ", columnIndex=" + columnIndex);

			switch (columnIndex) {

			// case EffortProvider.FormSpecs.TITLE_INDEX:
			case WorkFlowSpecsView.FORM_SPEC_TITLE_INDEX:
				TextView tv = (TextView) view;
				// tv.setOnClickListener(WorkFlowSpecsActivity.this);
				tv.setText(cursor
						.getString(WorkFlowSpecsView.FORM_SPEC_TITLE_INDEX));
				String formspecTitle = cursor
						.getString(WorkFlowSpecsView.FORM_SPEC_TITLE_INDEX);
				Log.i(TAG, "Formspectitle: " + formspecTitle);
				String formspecUniqueId = cursor
						.getString(WorkFlowSpecsView.WORKFLOW_MAP_ENTITY_ID_INDEX);
				Log.i(TAG, "FormspecuniqueId: " + formspecUniqueId);
				tv.setTag(formspecUniqueId);

				return true;

			case EffortProvider.WorkFlowSpecsView.WORK_FLOW_ID_INDEX:
				ImageButton button = (ImageButton) view;
				button.setOnClickListener(WorkFlowFormSpecsMappingsActivity.this);
				// button.setTag(cursor
				// .getLong(WorkFlowSpecsView.WORK_FLOW_ID_INDEX));
				// WorkFlowSpec wfsTag = new WorkFlowSpec();
				WorkFlowFormSpecMapping mapping = new WorkFlowFormSpecMapping();
				mapping.setWorkflowId(cursor
						.getLong(WorkFlowSpecsView.WORK_FLOW_ID_INDEX));
				mapping.setMappingEntityId(cursor
						.getString(WorkFlowSpecsView.WORKFLOW_MAP_ENTITY_ID_INDEX));

				button.setTag(mapping);
				// button.setTag(R.id., tag);
				// set only the right drawable
				button.setImageResource(R.drawable.ic_content_new_inverse);
				button.setVisibility(View.VISIBLE);

				return true;

			default:
				return false;
			}
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
			finish();
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}

package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.WorkFlowFormSpecFilter;
import in.spoors.effort1.dto.WorkFlowFormSpecMapping;
import in.spoors.effort1.provider.EffortProvider.FormSpecs;
import in.spoors.effort1.provider.EffortProvider.WorkFlowFormSpecFilterView;
import in.spoors.effort1.provider.EffortProvider.WorkFlowFormSpecMappings;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

public class WorkFlowFormSpecMappingsDao {
	public static final String TAG = "WorkFlowFormSpecMappingsDao";
	private static WorkFlowFormSpecMappingsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static WorkFlowFormSpecMappingsDao getInstance(
			Context applicationContext) {
		if (instance == null) {
			instance = new WorkFlowFormSpecMappingsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private WorkFlowFormSpecMappingsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(WorkFlowFormSpecMapping wfMapping) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		// ContentValues values = wfMapping.getContentValues(null);
		// long id = db.insert(WorkFlowFormSpecMappings.TABLE, null, values);
		//
		// if (BuildConfig.DEBUG) {
		// Log.d(TAG, "Saved: " + wfMapping
		// + ". Inserted WorkFlowFormSpecMappings ID: " + id);
		// }
		if (wfMapping.getId() != null
				&& isExistsWorkFlowFormSpecMapping(wfMapping)) {
			ContentValues values = wfMapping.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG,
						"Updating the existing workFlowSpec: "
								+ wfMapping.toString());
			}

			db.update(WorkFlowFormSpecMappings.TABLE, values,
					WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID + " = '"
							+ wfMapping.getMappingEntityId() + "' AND "
							+ WorkFlowFormSpecMappings.ENTITY_TYPE + " = "
							+ wfMapping.getEntityType(), null);

			return;
		}
		ContentValues values = wfMapping.getContentValues(null);
		db.insert(WorkFlowFormSpecMappings.TABLE, null, values);

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Saved a new WorkFlowFormSpecMappings: "
							+ wfMapping.toString());
		}
	}

	private boolean isExistsWorkFlowFormSpecMapping(
			WorkFlowFormSpecMapping wfMapping) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery(
					"SELECT COUNT(" + WorkFlowFormSpecMappings._ID
							+ ") AS count FROM "
							+ WorkFlowFormSpecMappings.TABLE + " WHERE "
							+ WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID
							+ " = '" + wfMapping.getMappingEntityId()
							+ "' AND " + WorkFlowFormSpecMappings.ENTITY_TYPE
							+ " = " + wfMapping.getEntityType(), null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.delete(WorkFlowFormSpecMappings.TABLE, null, null);
	}

	public String getFormSpecUniqueId(long workflowSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db
					.query(WorkFlowFormSpecMappings.TABLE,
							new String[] { WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID },
							WorkFlowFormSpecMappings.WORK_FLOW_ID + " = "
									+ workflowSpecId, null, null, null, null);

			// cursor.moveToNext();
			if (cursor.moveToNext()) {
				return cursor.getString(0);
			} else {
				return null;
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Long getWorkflowSpecId(String formSpecUniqueId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(WorkFlowFormSpecMappings.TABLE,
					new String[] { WorkFlowFormSpecMappings.WORK_FLOW_ID },
					WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID + " = '"
							+ formSpecUniqueId + "'", null, null, null, null);

			if (cursor.moveToNext()) {
				return cursor.getLong(0);
			} else {
				return null;
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Long getFormSpecId(long workflowSpecId) {

		String formSpecUniqueId = getFormSpecUniqueId(workflowSpecId);
		if (formSpecUniqueId == null) {
			return null;
		}
		FormSpecsDao formSpecsDao = FormSpecsDao
				.getInstance(applicationContext);
		return formSpecsDao.getFormSpecIdWithUniqueId(formSpecUniqueId);
	}

	public List<WorkFlowFormSpecFilter> getAllWorkFlowFormMappingData(
			String selection) {
		Cursor cursor = null;
		List<WorkFlowFormSpecFilter> wfMappings = null;

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		SQLiteQueryBuilder builder = null;
		builder = new SQLiteQueryBuilder();

		try {

			builder.setTables(WorkFlowFormSpecMappings.TABLE + " JOIN "
					+ FormSpecs.TABLE + " ON " + WorkFlowFormSpecMappings.TABLE
					+ "." + WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID
					+ " = " + FormSpecs.TABLE + "." + FormSpecs.UNIQUE_ID
					+ " LEFT JOIN " + FormSpecs.TABLE + " fs2 ON "
					+ WorkFlowFormSpecMappings.TABLE + "."
					+ WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID
					+ " = fs2." + FormSpecs.UNIQUE_ID + " AND "
					+ FormSpecs.TABLE + "." + FormSpecs._ID + " < fs2."
					+ FormSpecs._ID);

			cursor = builder.query(
					db,
					WorkFlowFormSpecFilterView.ALL_COLUMNS,
					" fs2."
							+ FormSpecs._ID
							+ " IS NULL"
							+ (TextUtils.isEmpty(selection) ? "" : " AND "
									+ selection), null, null, null, "UPPER("
							+ WorkFlowFormSpecFilterView.FORM_TEMPLATE_NAME
							+ ")" + " ASC ");

			if (cursor.getCount() > 0) {
				wfMappings = new ArrayList<WorkFlowFormSpecFilter>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				WorkFlowFormSpecFilter wfFT = new WorkFlowFormSpecFilter();
				wfFT.load(cursor);
				wfMappings.add(wfFT);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return wfMappings;
	}

	public String[] getSelectedWorkFlowFormSpecUniqueIds(String selection) {
		Cursor cursor = null;
		String[] wfMappings = null;
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		SQLiteQueryBuilder builder = null;
		builder = new SQLiteQueryBuilder();

		try {
			builder.setTables(WorkFlowFormSpecMappings.TABLE + " JOIN "
					+ FormSpecs.TABLE + " ON " + WorkFlowFormSpecMappings.TABLE
					+ "." + WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID
					+ " = " + FormSpecs.TABLE + "." + FormSpecs.UNIQUE_ID
					+ " LEFT JOIN " + FormSpecs.TABLE + " fs2 ON "
					+ WorkFlowFormSpecMappings.TABLE + "."
					+ WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID
					+ " = fs2." + FormSpecs.UNIQUE_ID + " AND "
					+ FormSpecs.TABLE + "." + FormSpecs._ID + " < fs2."
					+ FormSpecs._ID);

			cursor = builder
					.query(db,
							new String[] { WorkFlowFormSpecFilterView.WORKFLOW_MAP_ENTITY_ID },
							" fs2."
									+ FormSpecs._ID
									+ " IS NULL"
									+ (TextUtils.isEmpty(selection) ? ""
											: " AND " + selection),
							null,
							null,
							null,
							"UPPER("
									+ WorkFlowFormSpecFilterView.FORM_TEMPLATE_NAME
									+ ")" + " ASC ");

			if (cursor.getCount() > 0) {
				wfMappings = new String[cursor.getCount()];// (cursor.getCount());
			}

			int index = 0;

			while (cursor.moveToNext()) {
				wfMappings[index] = "'" + cursor.getString(0) + "'";
				index++;
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return wfMappings;
	}

	public WorkFlowFormSpecMapping getWorkFlowFormSpecMappingWithId(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		WorkFlowFormSpecMapping workFlowSpec = null;

		try {
			cursor = db.query(WorkFlowFormSpecMappings.TABLE,
					WorkFlowFormSpecMappings.ALL_COLUMNS,
					WorkFlowFormSpecMappings._ID + " = " + id, null, null,
					null, null);

			if (cursor.moveToNext()) {

				workFlowSpec = new WorkFlowFormSpecMapping();
				workFlowSpec.load(cursor);
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return workFlowSpec;
	}

}

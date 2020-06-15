package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.WorkFlowSpec;
import in.spoors.effort1.provider.EffortProvider.WorkFlowSpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WorkFlowSpecsDao {

	public static final String TAG = "WorkFlowSpecsDao";
	private static WorkFlowSpecsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static WorkFlowSpecsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new WorkFlowSpecsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private WorkFlowSpecsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(WorkFlowSpec workFlowSpec) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		if (workFlowSpec.getId() != null
				&& workFlowSpecWithIdExists(workFlowSpec.getId())) {
			ContentValues values = workFlowSpec.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the existing workFlowSpec: "
						+ workFlowSpec.toString());
			}

			db.update(WorkFlowSpecs.TABLE, values, WorkFlowSpecs._ID + " = "
					+ workFlowSpec.getId(), null);

			return;
		}
		ContentValues values = workFlowSpec.getContentValues(null);
		db.insert(WorkFlowSpecs.TABLE, null, values);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saved a new workFlowSpec: " + workFlowSpec.toString());
		}
	}

	public boolean workFlowSpecWithIdExists(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + WorkFlowSpecs._ID
					+ ") AS count FROM " + WorkFlowSpecs.TABLE + " WHERE "
					+ WorkFlowSpecs._ID + " = " + id, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public WorkFlowSpec getWorkFlowSpecWithFormSpecUniqueId(
			String formSpecUniqueId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		WorkFlowFormSpecMappingsDao workFlowFormSpecMappingsDao = WorkFlowFormSpecMappingsDao
				.getInstance(applicationContext);
		Long workflowId = workFlowFormSpecMappingsDao
				.getWorkflowSpecId(formSpecUniqueId);
		if (workflowId == null) {
			return null;
		}
		Cursor cursor = null;
		WorkFlowSpec workFlowSpec = null;

		try {
			cursor = db.query(WorkFlowSpecs.TABLE, WorkFlowSpecs.ALL_COLUMNS,
					WorkFlowSpecs._ID + " = '" + workflowId + "' AND "
							+ WorkFlowSpecs.DELETED + " = 'false'", null, null,
					null, null);
			// cursor = db.query(WorkFlowSpecs.TABLE, WorkFlowSpecs.ALL_COLUMNS,
			// WorkFlowSpecs._ID +" = ("+ " = '"
			// + formSpecUniqueId + "' AND "
			// + WorkFlowSpecs.DELETED + " = 'false'", null, null,
			// null, null);

			if (cursor.moveToNext()) {

				workFlowSpec = new WorkFlowSpec();
				workFlowSpec.load(cursor);
				return workFlowSpec;
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public WorkFlowSpec getWorkFlowSpecWithLocalFormId(Long localFormId) {
		FormsDao formsDao = FormsDao.getInstance(applicationContext);
		String formSpecUniqueId = formsDao.getFormSpecUniqueId(localFormId);
		if (formSpecUniqueId == null) {
			return null;
		}
		return getWorkFlowSpecWithFormSpecUniqueId(formSpecUniqueId);
	}

	public WorkFlowSpec getWorkFlowSpecWithId(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		WorkFlowSpec workFlowSpec = null;

		try {
			cursor = db.query(WorkFlowSpecs.TABLE, WorkFlowSpecs.ALL_COLUMNS,
					WorkFlowSpecs._ID + " = " + id, null, null, null, null);

			if (cursor.moveToNext()) {

				workFlowSpec = new WorkFlowSpec();
				workFlowSpec.load(cursor);
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return workFlowSpec;
	}

	public WorkFlowSpec getWorkFlowSpecWithFormSpecId(long formSpecId) {

		FormSpecsDao formsDao = FormSpecsDao.getInstance(applicationContext);
		String unique_id = formsDao.getUniqueId(formSpecId);
		return getWorkFlowSpecWithFormSpecUniqueId(unique_id);
	}

	public List<WorkFlowSpec> getAllWorkFlowSpecs() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<WorkFlowSpec> workFlowSpecs = null;

		try {

			cursor = db.query(WorkFlowSpecs.TABLE, WorkFlowSpecs.ALL_COLUMNS,
					null, null, null, null, "UPPER("
							+ WorkFlowSpecs.WORK_FLOW_NAME + ")" + " ASC");

			if (cursor.getCount() > 0) {
				workFlowSpecs = new ArrayList<WorkFlowSpec>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				WorkFlowSpec workFlowSpec = new WorkFlowSpec();
				workFlowSpec.load(cursor);
				workFlowSpecs.add(workFlowSpec);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return workFlowSpecs;
	}

	public void saveChecked(Long id, boolean checked) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(WorkFlowSpecs.CHECKED, String.valueOf(checked));

		int rowsAffected = db.update(WorkFlowSpecs.TABLE, values,
				WorkFlowSpecs._ID + " = ?", new String[] { id + "" });
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Updated the checked state of " + rowsAffected
					+ " workflowspec.");
		}
	}

	/**
	 * 
	 * @return null if there are no form spec id's
	 */
	// public String getCheckedFormSpecUniqueIdsFromWorkFlowSpecs() {
	// StringBuffer sb = new StringBuffer("(");
	// SQLiteDatabase db = DBHelper.getInstance(applicationContext)
	// .getReadableDatabase();
	// Cursor cursor = null;
	//
	// try {
	// cursor = db.query(WorkFlowSpecs.TABLE,
	// new String[] { WorkFlowSpecs.FORM_SPE_UNIQUE_ID },
	// WorkFlowSpecs.CHECKED + " = 'true' AND "
	// + WorkFlowSpecs.DELETED + " = 'false'", null, null,
	// null, null);
	//
	// int count = cursor.getCount();
	// if (count == 0) {
	// sb.append("'");
	// }
	// for (int i = 0; i < count; ++i) {
	// cursor.moveToNext();
	// sb.append("'");
	// sb.append(cursor.getString(0));
	//
	// if (i < count - 1) {
	// sb.append("', ");
	// }
	// }
	//
	// sb.append("')");
	//
	// } finally {
	// if (cursor != null)
	// cursor.close();
	// }
	//
	// String in = sb.toString();
	//
	// if (BuildConfig.DEBUG) {
	// Log.d(TAG, "In clause: " + in);
	// }
	//
	// return in;
	// }

	public String getCheckedWorkFlowIdsFromWorkFlowSpecs() {
		StringBuffer sb = new StringBuffer("(");
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			// AND " + WorkFlowSpecs.DELETED
			// + " = 'false'
			cursor = db.query(WorkFlowSpecs.TABLE,
					new String[] { WorkFlowSpecs._ID }, WorkFlowSpecs.CHECKED
							+ " = 'true'", null, null, null, null);

			int count = cursor.getCount();
			// if (count == 0) {
			// sb.append("'");
			// }
			for (int i = 0; i < count; ++i) {
				cursor.moveToNext();
				// sb.append("'");
				sb.append(cursor.getLong(0));

				if (i < count - 1) {
					sb.append(", ");
				}
			}

			sb.append(")");

		} finally {
			if (cursor != null)
				cursor.close();
		}

		String in = sb.toString();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In clause: " + in);
		}

		return in;
	}

	public String getFormTemplateNameWithWorkFlowId(Long workFlowId) {
		WorkFlowFormSpecMappingsDao workFlowFormSpecMappingsDao = WorkFlowFormSpecMappingsDao
				.getInstance(applicationContext);
		String formSpecuniqueId = workFlowFormSpecMappingsDao
				.getFormSpecUniqueId(workFlowId);
		if (formSpecuniqueId == null) {
			return null;
		}
		FormSpecsDao formSpecsDao = FormSpecsDao
				.getInstance(applicationContext);
		Long formSpecIdWithUniqueId = formSpecsDao
				.getFormSpecIdWithUniqueId(formSpecuniqueId);
		if (formSpecIdWithUniqueId == null) {
			return null;
		}
		return formSpecsDao.getFormTitle(formSpecsDao
				.getLatestFormSpecId(formSpecIdWithUniqueId));
	}
}
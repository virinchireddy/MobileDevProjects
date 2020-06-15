package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.WorkFlowStage;
import in.spoors.effort1.provider.EffortProvider.WorkFlowStages;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WorkFlowStagesDao {

	public static final String TAG = "WorkFlowStagesDao";
	private static WorkFlowStagesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static WorkFlowStagesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new WorkFlowStagesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private WorkFlowStagesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(WorkFlowStage workFlowStage) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		if (workFlowStage.getId() != null
				&& workFlowStageWithIdExists(workFlowStage.getId())) {
			ContentValues values = workFlowStage.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the existing workFlowStage: "
						+ workFlowStage.toString());
			}

			db.update(WorkFlowStages.TABLE, values, WorkFlowStages._ID + " = "
					+ workFlowStage.getId(), null);

			return;
		}
		ContentValues values = workFlowStage.getContentValues(null);
		db.insert(WorkFlowStages.TABLE, null, values);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saved a new workFlowSpec: " + workFlowStage.toString());
		}

	}

	public boolean workFlowStageWithIdExists(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + WorkFlowStages._ID
					+ ") AS count FROM " + WorkFlowStages.TABLE + " WHERE "
					+ WorkFlowStages._ID + " = " + id, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public WorkFlowStage getWorkFlowStageWithWorkFlowId(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		WorkFlowStage workFlowStage = null;

		try {
			cursor = db.query(WorkFlowStages.TABLE, WorkFlowStages.ALL_COLUMNS,
					WorkFlowStages.WORK_FLOW_ID + " = " + id, null, null, null,
					null);

			if (cursor.moveToNext()) {

				workFlowStage = new WorkFlowStage();
				workFlowStage.load(cursor);
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return workFlowStage;
	}
}

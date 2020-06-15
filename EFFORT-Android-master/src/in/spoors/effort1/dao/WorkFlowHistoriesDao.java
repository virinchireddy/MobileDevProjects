package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.WorkFlowHistory;
import in.spoors.effort1.provider.EffortProvider.WorkFlowHistories;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WorkFlowHistoriesDao {

	public static final String TAG = "WorkFlowHistoriesDao";
	private static WorkFlowHistoriesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static WorkFlowHistoriesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new WorkFlowHistoriesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private WorkFlowHistoriesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(WorkFlowHistory workFlowHistory) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		if (workFlowHistory.getClientFormId() == null
				&& workFlowHistory.getRemoteFormId() != null) {
			FormsDao formsDao = FormsDao.getInstance(applicationContext);
			workFlowHistory.setClientFormId(formsDao.getLocalId(workFlowHistory
					.getRemoteFormId()));
		}

		if (workFlowHistory.getId() != null
				&& workFlowHistoryWithRemoteIdExists(workFlowHistory.getId())) {
			ContentValues values = workFlowHistory.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the existing WorkFlowHistory: "
						+ workFlowHistory.toString());
			}

			db.update(WorkFlowHistories.TABLE, values, WorkFlowHistories._ID
					+ " = " + workFlowHistory.getId(), null);

			return;
		}
		ContentValues values = workFlowHistory.getContentValues(null);
		db.insert(WorkFlowHistories.TABLE, null, values);

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Saved a new WorkFlowHistory: "
							+ workFlowHistory.toString());
		}
	}

	public boolean workFlowHistoryWithLocalIdExists(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + WorkFlowHistories._ID
					+ ") AS count FROM " + WorkFlowHistories.TABLE + " WHERE "
					+ WorkFlowHistories._ID + " = " + id, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean workFlowHistoryWithRemoteIdExists(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + WorkFlowHistories._ID
					+ ") AS count FROM " + WorkFlowHistories.TABLE + " WHERE "
					+ WorkFlowHistories._ID + " = " + remoteId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized List<WorkFlowHistory> getAllHistories(String formid) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Cursor cursor = null;
		List<WorkFlowHistory> histories = new ArrayList<WorkFlowHistory>();

		try {
			// cursor = db.query(WorkFlowHistories.TABLE,
			// WorkFlowHistories.ALL_COLUMNS,
			// WorkFlowHistories.FORM_ID + " = ?",
			// new String[] {formid }, null, null,
			// null);
			cursor = db
					.query(WorkFlowHistories.TABLE,
							WorkFlowHistories.ALL_COLUMNS, null, null, null,
							null, null);
			System.out.println(" cursor size " + cursor.getCount());
			if (cursor.getCount() > 0) {
				histories = new ArrayList<WorkFlowHistory>(cursor.getCount());
			}
			//
			while (cursor.moveToNext()) {
				WorkFlowHistory history = new WorkFlowHistory();
				history.load(cursor);
				histories.add(history);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return histories;
	}

	public void deleteWorkFlowHistory(Long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(WorkFlowHistories.TABLE,
				WorkFlowHistories.LOCAL_FORM_ID + " = " + localFormId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows
					+ " WorkflowStatus with local form id " + localFormId);
		}
	}
}

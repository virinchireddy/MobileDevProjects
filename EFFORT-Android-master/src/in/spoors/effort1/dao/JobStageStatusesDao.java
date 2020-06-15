package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.JobStageStatus;
import in.spoors.effort1.provider.EffortProvider.JobStageStatuses;
import in.spoors.effort1.provider.EffortProvider.JobStates;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.commonsware.cwac.wakeful.BuildConfig;

/**
 * 
 * @author tiru
 */
public class JobStageStatusesDao {
	public static final String TAG = "JobStageStatusesDao";

	private static JobStageStatusesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static JobStageStatusesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new JobStageStatusesDao(applicationContext);
		}

		return instance;
	}

	private JobStageStatusesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean isDone(long localJobId, int stageId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(JobStageStatuses.TABLE,
					new String[] { JobStageStatuses._ID },
					JobStageStatuses.LOCAL_JOB_ID + " = " + localJobId
							+ " AND " + JobStageStatuses.STATE_ID + " = "
							+ stageId + " AND " + JobStageStatuses.DONE
							+ " = 'true'", null, null, null, null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public boolean isDoneForCompletion(long localJobId, int stageId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {

			String query = "SELECT " + JobStageStatuses.DONE + " FROM "
					+ JobStates.TABLE + " LEFT JOIN " + JobStageStatuses.TABLE
					+ " ON " + JobStates.TABLE + "." + JobStates._ID + " = "
					+ JobStageStatuses.STATE_ID + " AND "
					+ JobStageStatuses.LOCAL_JOB_ID + " = " + localJobId
					+ " WHERE " + JobStates.TABLE + "." + JobStates._ID + " = "
					+ stageId + " AND (" + JobStates.MANDATORY_FOR_COMPLETION
					+ " = 'false' OR " + JobStageStatuses.DONE + " = 'true'"
					+ ")";

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "isDoneForCompletion query: " + query);
			}

			cursor = db.rawQuery(query, null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public Date getLocalModifiedTime(long localJobId, int stageId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(JobStageStatuses.TABLE,
					new String[] { JobStageStatuses.LOCAL_MODIFICATION_TIME },
					JobStageStatuses.LOCAL_JOB_ID + " = " + localJobId
							+ " AND " + JobStageStatuses.STATE_ID + " = "
							+ stageId, null, null, null, null);

			if (cursor.moveToNext()) {
				return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
			} else {
				return null;
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	private boolean recordExists(long localJobId, int stageId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(JobStageStatuses.TABLE,
					new String[] { JobStageStatuses.DONE },
					JobStageStatuses.LOCAL_JOB_ID + " = " + localJobId
							+ " AND " + JobStageStatuses.STATE_ID + " = "
							+ stageId, null, null, null, null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public void updateDone(long localJobId, int stageId, boolean done,
			boolean dirty, boolean temporary) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(JobStageStatuses.DONE, String.valueOf(done));
		values.put(JobStageStatuses.DIRTY, String.valueOf(dirty));
		// ADDED FOR TEMPORARY
		values.put(JobStageStatuses.TEMPORARY, String.valueOf(temporary));

		String now = SQLiteDateTimeUtils.getCurrentSQLiteDateTime();
		values.put(JobStageStatuses.LOCAL_MODIFICATION_TIME, now);

		if (recordExists(localJobId, stageId)) {
			db.update(JobStageStatuses.TABLE, values,
					JobStageStatuses.LOCAL_JOB_ID + " = " + localJobId
							+ " AND " + JobStageStatuses.STATE_ID + " = "
							+ stageId, null);
		} else {
			values.put(JobStageStatuses.LOCAL_JOB_ID, localJobId);
			values.put(JobStageStatuses.STATE_ID, stageId);
			values.put(JobStageStatuses.LOCAL_CREATION_TIME, now);
			db.insert(JobStageStatuses.TABLE, null, values);
		}
	}

	public List<JobStageStatus> getStatusesToBeSynced() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<JobStageStatus> statuses = null;

		try {
			cursor = db.query(JobStageStatuses.TABLE,
					JobStageStatuses.ALL_COLUMNS, JobStageStatuses.DIRTY
							+ " = 'true' AND " + JobStageStatuses.TEMPORARY
							+ " = 'false'", null, null, null, null);

			if (cursor.getCount() > 0) {
				statuses = new ArrayList<JobStageStatus>(cursor.getCount());

				while (cursor.moveToNext()) {
					JobStageStatus status = new JobStageStatus();
					status.load(cursor, applicationContext);
					statuses.add(status);
				}
			}

			return statuses;
		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	public synchronized void updateTemporaryFlag(Long localJobId,
			boolean temporary) {
		{
			SQLiteDatabase db = DBHelper.getInstance(applicationContext)
					.getWritableDatabase();

			ContentValues values = new ContentValues();
			values.put(JobStageStatuses.TEMPORARY, String.valueOf(temporary));

			db.update(JobStageStatuses.TABLE, values,
					JobStageStatuses.LOCAL_JOB_ID + " = " + localJobId, null);
		}
	}
}

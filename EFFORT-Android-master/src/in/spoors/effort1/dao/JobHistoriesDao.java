package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.JobHistory;
import in.spoors.effort1.provider.EffortProvider.Forms;
import in.spoors.effort1.provider.EffortProvider.JobHistories;
import in.spoors.effort1.provider.EffortProvider.Locations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class JobHistoriesDao {

	public static final String TAG = "JobHistoriesDao";
	private static JobHistoriesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static JobHistoriesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new JobHistoriesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private JobHistoriesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean jobHistoryWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + JobHistories._ID
					+ ") AS count FROM " + JobHistories.TABLE + " WHERE "
					+ JobHistories._ID + " = " + localId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean jobHistoryWithRemoteIdExists(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + JobHistories.REMOTE_ID
					+ ") AS count FROM " + JobHistories.TABLE + " WHERE "
					+ JobHistories.REMOTE_ID + " = " + remoteId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Saves the given job.
	 * 
	 * Local ID of the job history is updated with the inserted ID.
	 * 
	 * @param jobHistory
	 */
	public synchronized void save(JobHistory jobHistory) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		Date now = new Date();

		if (jobHistory.getLocalCustomerId() == null
				&& jobHistory.getRemoteCustomerId() != null) {
			CustomersDao customersDao = CustomersDao
					.getInstance(applicationContext);
			jobHistory.setLocalCustomerId(customersDao.getLocalId(jobHistory
					.getRemoteCustomerId()));
		}

		if (jobHistory.getLocalJobId() == null
				&& jobHistory.getRemoteJobId() != null) {
			JobsDao jobsDao = JobsDao.getInstance(applicationContext);
			jobHistory.setLocalJobId(jobsDao.getLocalId(jobHistory
					.getRemoteJobId()));
		}

		if (jobHistory.getLocalFormId() == null
				&& jobHistory.getRemoteFormId() != null) {
			FormsDao formsDao = FormsDao.getInstance(applicationContext);
			jobHistory.setLocalFormId(formsDao.getLocalId(jobHistory
					.getRemoteFormId()));
		}

		if (jobHistory.getLocalId() != null
				&& jobHistoryWithLocalIdExists(jobHistory.getLocalId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting job history.");
			}

			JobHistory oldJobHistory = getJobHistoryWithLocalId(jobHistory
					.getLocalId());
			jobHistory.setLocalModificationTime(new Date());

			if (jobHistory.getLocalCreationTime() == null) {
				jobHistory.setLocalCreationTime(oldJobHistory
						.getLocalCreationTime());
			}

			ContentValues values = jobHistory.getContentValues(null);

			db.update(JobHistories.TABLE, values, JobHistories._ID + " = "
					+ jobHistory.getLocalId(), null);

			return;
		}

		if (jobHistory.getRemoteId() != null
				&& jobHistoryWithRemoteIdExists(jobHistory.getRemoteId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting job history.");
			}

			JobHistory oldJobHistory = getJobHistoryWithRemoteId(jobHistory
					.getRemoteId());

			jobHistory.setLocalModificationTime(new Date());

			if (jobHistory.getLocalCreationTime() == null) {
				jobHistory.setLocalCreationTime(oldJobHistory
						.getLocalCreationTime());
			}

			ContentValues values = jobHistory.getContentValues(null);

			db.update(JobHistories.TABLE, values, JobHistories.REMOTE_ID
					+ " = " + jobHistory.getRemoteId(), null);

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new job history.");
		}

		jobHistory.setLocalCreationTime(now);
		jobHistory.setLocalModificationTime(now);

		ContentValues values = jobHistory.getContentValues(null);
		long insertedId = db.insert(JobHistories.TABLE, null, values);
		jobHistory.setLocalId(insertedId);
	}

	/**
	 * Returns the local job history id of the given remote job history id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Long getLocalId(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + JobHistories._ID + " FROM "
					+ JobHistories.TABLE + " WHERE " + JobHistories.REMOTE_ID
					+ " = " + remoteId, null);

			if (cursor.moveToNext() && !cursor.isNull(0)) {
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

	/**
	 * Returns the remote job history id of the given local job history id.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public Long getRemoteId(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + JobHistories.REMOTE_ID + " FROM "
					+ JobHistories.TABLE + " WHERE " + JobHistories._ID + " = "
					+ localId, null);

			if (cursor.moveToNext() && !cursor.isNull(0)) {
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

	/**
	 * Returns the local creation time of the given local job history id.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithLocalId(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + JobHistories.LOCAL_CREATION_TIME
					+ " FROM " + JobHistories.TABLE + " WHERE "
					+ JobHistories._ID + " = " + localId, null);

			if (cursor.moveToNext()) {
				return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
			} else {
				return null;
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Returns the local creation time of the given remote job id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithRemoteId(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + JobHistories.LOCAL_CREATION_TIME
					+ " FROM " + JobHistories.TABLE + " WHERE "
					+ JobHistories.REMOTE_ID + " = " + remoteId, null);

			if (cursor.moveToNext()) {
				return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
			} else {
				return null;
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public JobHistory getJobHistoryWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		JobHistory jobHistory = null;
		Cursor cursor = null;

		try {
			cursor = db.query(JobHistories.TABLE, JobHistories.ALL_COLUMNS,
					JobHistories._ID + " = " + localId, null, null, null, null);

			if (cursor.moveToNext()) {
				jobHistory = new JobHistory();
				jobHistory.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobHistory;
	}

	public JobHistory getJobHistoryWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		JobHistory jobHistory = null;
		Cursor cursor = null;

		try {
			cursor = db.query(JobHistories.TABLE, JobHistories.ALL_COLUMNS,
					JobHistories.REMOTE_ID + " = " + remoteId, null, null,
					null, null);

			if (cursor.moveToNext()) {
				jobHistory = new JobHistory();
				jobHistory.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobHistory;
	}

	private Cursor queryWith2WayJoin(String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(JobHistories.TABLE + " LEFT JOIN " + Locations.TABLE
				+ " ON " + Locations.PURPOSE + " = "
				+ Locations.PURPOSE_HISTORY + " AND " + Locations.FOR_ID
				+ " = " + JobHistories.TABLE + "." + JobHistories._ID);

		// qualify the columns by the table name, so that join
		// doesn't result confuse the query engine with ambiguous column
		// names
		String[] columns = new String[JobHistories.ALL_COLUMNS.length];
		for (int i = 0; i < JobHistories.ALL_COLUMNS.length; ++i) {
			columns[i] = JobHistories.TABLE + "." + JobHistories.ALL_COLUMNS[i];
		}

		return builder.query(db, columns, selection, selectionArgs, null, null,
				sortOrder);

	}

	/**
	 * Get the job histories that were added on the device, and need to be
	 * synced.
	 * 
	 * @return
	 */
	public List<JobHistory> getAddedJobHistories() {
		Cursor cursor = null;
		List<JobHistory> jobHistories = null;

		try {
			cursor = queryWith2WayJoin(JobHistories.REMOTE_ID + " IS NULL AND "
					+ JobHistories.TABLE + "." + JobHistories.TEMPORARY
					+ " = 'false' AND (" + Locations.LOCATION_FINALIZED
					+ " = 'true' OR " + Locations.LOCATION_FINALIZED
					+ " IS NULL)", null, null);

			if (cursor.getCount() > 0) {
				jobHistories = new ArrayList<JobHistory>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				JobHistory jobHistory = new JobHistory();
				jobHistory.load(cursor, applicationContext);
				jobHistories.add(jobHistory);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobHistories;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasUnsyncedChanges() {
		Cursor cursor = null;

		try {
			cursor = queryWith2WayJoin(JobHistories.REMOTE_ID
					+ " IS NULL AND (" + Locations.LOCATION_FINALIZED
					+ " = 'true' OR " + Locations.LOCATION_FINALIZED
					+ " IS NULL)", null, null);
			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 
	 * @param remoteId
	 * @return job start time in SQLite date time format.
	 */
	public String getStartTime(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + JobHistories.START_TIME + " FROM "
					+ JobHistories.TABLE + " WHERE " + JobHistories.REMOTE_ID
					+ " = " + remoteId, null);

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

	public synchronized void updateRemoteId(long localId, long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(JobHistories.REMOTE_ID, remoteId);

		db.update(JobHistories.TABLE, values, JobHistories._ID + " = "
				+ localId, null);
	}

	public JobHistory getRecentJobHistory(long localJobId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		JobHistory jobHistory = null;

		try {
			cursor = db.query(JobHistories.TABLE, JobHistories.ALL_COLUMNS,
					JobHistories.LOCAL_JOB_ID + " = " + localJobId, null, null,
					null, JobHistories._ID + " DESC", "1");

			if (cursor.moveToNext()) {
				jobHistory = new JobHistory();
				jobHistory.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobHistory;
	}

	public JobHistory getJobHistory(long localJobId, int stateId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		JobHistory jobHistory = null;

		try {
			cursor = db.query(JobHistories.TABLE, JobHistories.ALL_COLUMNS,
					JobHistories.LOCAL_JOB_ID + " = " + localJobId + " AND "
							+ JobHistories.JOB_STATE_ID + " = " + stateId,
					null, null, null, JobHistories._ID + " DESC", "1");

			if (cursor.moveToNext()) {
				jobHistory = new JobHistory();
				jobHistory.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobHistory;
	}

	public JobHistory getJobHistory(long localJobId, int stateId,
			long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		JobHistory jobHistory = null;

		try {
			cursor = db.query(JobHistories.TABLE, JobHistories.ALL_COLUMNS,
					JobHistories.LOCAL_JOB_ID + " = " + localJobId + " AND "
							+ JobHistories.JOB_STATE_ID + " = " + stateId
							+ " AND " + JobHistories.LOCAL_FORM_ID + " = "
							+ localFormId, null, null, null, JobHistories._ID
							+ " DESC", "1");

			if (cursor.moveToNext()) {
				jobHistory = new JobHistory();
				jobHistory.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobHistory;
	}

	public long getRecentLocalFormId(long localJobId, int typeId, int stateId) {
		JobHistory jobHistory = getRecentJobHistory(localJobId);

		if (jobHistory != null && jobHistory.getTypeId() == typeId
				&& jobHistory.getStateId() == stateId
				&& jobHistory.getLocalFormId() != null) {
			return jobHistory.getLocalFormId();
		} else {
			return 0;
		}
	}

	public boolean hasRecentHistory(long localJobId, int typeId, int stateId) {
		JobHistory jobHistory = getRecentJobHistory(localJobId);

		if (jobHistory != null && jobHistory.getTypeId() == typeId
				&& jobHistory.getStateId() == stateId) {
			return true;
		} else {
			return false;
		}
	}

	public List<JobHistory> getHistories(long localJobId, int stateId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<JobHistory> histories = null;

		try {
			cursor = db.query(JobHistories.TABLE, JobHistories.ALL_COLUMNS,
					JobHistories.LOCAL_JOB_ID + " = " + localJobId + " AND "
							+ JobHistories.JOB_STATE_ID + " = " + stateId,
					null, null, null, null);

			if (cursor.getCount() > 0) {
				histories = new ArrayList<JobHistory>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				JobHistory jobHistory = new JobHistory();
				jobHistory.load(cursor, applicationContext);
				histories.add(jobHistory);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return histories;
	}

	public synchronized void deleteJobHistory(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		LocationsDao locationsDao = LocationsDao
				.getInstance(applicationContext);
		locationsDao.deleteHistoryLocations(localId);

		// now, delete the job history
		int affectedRows = db.delete(JobHistories.TABLE, JobHistories._ID
				+ " = ?", new String[] { "" + localId });

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted job history with local job history id: "
					+ localId + ", affectedRows=" + affectedRows);
		}
	}

	
	public int getFormCount(long localJobId, int stateId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(JobHistories.TABLE,
					new String[] { JobHistories._ID },
					JobHistories.LOCAL_JOB_ID + " = " + localJobId + " AND "
							+ JobHistories.JOB_STATE_ID + " = " + stateId
							+ " AND " + JobHistories.LOCAL_FORM_ID
							+ " IS NOT NULL", null, null, null, null);

			return cursor.getCount();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean hasHistory(long localJobId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(JobHistories.TABLE,
					new String[] { JobHistories._ID },
					JobHistories.LOCAL_JOB_ID + " = " + localJobId, null, null,
					null, null, null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void deleteJobHistoryWithJobId(long localJobId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		Cursor cursor = db.query(JobHistories.TABLE,
				new String[] { JobHistories._ID }, JobHistories.LOCAL_JOB_ID
						+ " = " + localJobId, null, null, null, null);
		while (cursor.moveToNext()) {
			// delete locations
			LocationsDao locationsDao = LocationsDao
					.getInstance(applicationContext);
			locationsDao.deleteHistoryLocations(cursor.getLong(0));
		}

		// now, delete the job history
		int affectedRows = db.delete(JobHistories.TABLE,
				JobHistories.LOCAL_JOB_ID + " = ?", new String[] { ""
						+ localJobId });

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted job history with local job id: " + localJobId
					+ ", affectedRows=" + affectedRows);
		}
	}

	public synchronized void updateTemporaryFlagWithLocalJobId(long localJobId,
			boolean temporary) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Forms.TEMPORARY, String.valueOf(temporary));

		db.update(JobHistories.TABLE, values, JobHistories.LOCAL_JOB_ID + " = "
				+ localJobId, null);
	}
	
}

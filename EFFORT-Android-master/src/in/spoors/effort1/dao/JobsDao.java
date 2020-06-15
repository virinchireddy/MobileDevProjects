package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dto.Job;
import in.spoors.effort1.dto.WorkingHour;
import in.spoors.effort1.provider.EffortProvider.FormFiles;
import in.spoors.effort1.provider.EffortProvider.Forms;
import in.spoors.effort1.provider.EffortProvider.JobHistories;
import in.spoors.effort1.provider.EffortProvider.JobTypes;
import in.spoors.effort1.provider.EffortProvider.Jobs;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.Notes;
import in.spoors.effort1.provider.EffortProvider.SectionFiles;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.commonsware.cwac.wakeful.BuildConfig;

public class JobsDao {

	public static final String TAG = "JobsDao";
	private static JobsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static JobsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new JobsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private JobsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean jobWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Jobs._ID
					+ ") AS count FROM " + Jobs.TABLE + " WHERE " + Jobs._ID
					+ " = " + localId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean jobWithRemoteIdExists(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Jobs.REMOTE_ID
					+ ") AS count FROM " + Jobs.TABLE + " WHERE "
					+ Jobs.REMOTE_ID + " = " + remoteId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean jobsAreDifferent(Job job1, Job job2) {
		if (job1 == null && job2 == null) {
			return false;
		}

		if (job1 == null || job2 == null) {
			return true;
		}

		// both the jobs are not null
		if (!Utils.integersEqual(job1.getAndroidEventId(),
				job2.getAndroidEventId())
				|| !Utils.booleansEqual(job1.getApproved(), job2.getApproved())
				|| !Utils.booleansEqual(job1.getCompleted(),
						job2.getCompleted())
				|| !Utils.datesEqual(job1.getCompletionTime(),
						job2.getCompletionTime())
				|| !Utils.datesEqual(job1.getStartTime(), job2.getStartTime())
				|| !Utils.datesEqual(job1.getEndTime(), job2.getEndTime())
				|| !TextUtils.equals(job1.getDescription(),
						job2.getDescription())
				|| !TextUtils.equals(job1.getTitle(), job2.getTitle())
				|| !Utils.longsEqual(job1.getLocalCustomerId(),
						job2.getLocalCustomerId())) {
			return true;
		}

		return false;
	}

	/**
	 * Saves the given job.
	 * 
	 * Local ID of the job is updated with the inserted ID.
	 * 
	 * @param job
	 */
	public synchronized void save(Job job) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		Date now = new Date();

		if (now.before(job.getEndTime())
				&& (job.getCached() == null || (job.getCached() != null && job
						.getCached() == false)) && job.getTemporary() != null
				&& job.getTemporary() == false) {
			// SettingsDao settingsDao = SettingsDao
			// .getInstance(applicationContext);
			// Utils.insertOrUpdateCalendarEvent(applicationContext
			// .getContentResolver(), job, settingsDao.getInt(
			// EffortProvider.Settings.KEY_REMINDER_MINUTES,
			// EffortProvider.Settings.DEFAULT_REMINDER_MINUTES));
		}

		if (job.getLocalCustomerId() == null
				&& job.getRemoteCustomerId() != null) {
			CustomersDao customersDao = CustomersDao
					.getInstance(applicationContext);
			job.setLocalCustomerId(customersDao.getLocalId(job
					.getRemoteCustomerId()));
		}

		if (job.getLocalId() != null && jobWithLocalIdExists(job.getLocalId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting job.");
			}

			Job oldJob = getJobWithLocalId(job.getLocalId());

			if (jobsAreDifferent(job, oldJob)) {
				job.setLocalModificationTime(new Date());
			}

			if (job.getLocalCreationTime() == null) {
				job.setLocalCreationTime(oldJob.getLocalCreationTime());
			}

			ContentValues values = job.getContentValues(null);

			db.update(Jobs.TABLE, values, Jobs._ID + " = " + job.getLocalId(),
					null);

			return;
		}

		if (job.getRemoteId() != null
				&& jobWithRemoteIdExists(job.getRemoteId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting job.");
			}

			Job oldJob = getJobWithRemoteId(job.getRemoteId());

			if (jobsAreDifferent(job, oldJob)) {
				job.setLocalModificationTime(new Date());
			}

			if (job.getLocalCreationTime() == null) {
				job.setLocalCreationTime(oldJob.getLocalCreationTime());
			}

			ContentValues values = job.getContentValues(null);

			db.update(Jobs.TABLE, values,
					Jobs.REMOTE_ID + " = " + job.getRemoteId(), null);

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new job.");
		}

		job.setLocalCreationTime(now);
		job.setLocalModificationTime(now);

		ContentValues values = job.getContentValues(null);
		long insertedId = db.insert(Jobs.TABLE, null, values);
		job.setLocalId(insertedId);
	}

	/**
	 * Completes the given job.
	 * 
	 * @param job
	 */
	public synchronized void completeJob(long localJobId,
			String completionComment) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Job job = getJobWithLocalId(localJobId);

		if (job == null) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "Cannot complete job with local job ID "
						+ localJobId + ". It doesn't exist!");
			}

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Completing the job with local ID " + localJobId);
		}

		Date now = new Date();
		job.setLocalModificationTime(now);
		job.setDirty(true);
		job.setCompleted(true);
		job.setCompletionTime(now);

		ContentValues values = job.getContentValues(null);

		db.update(Jobs.TABLE, values, Jobs._ID + " = " + job.getLocalId(), null);

		// Note note = new Note();
		// note.setNote(completionComment);
		// note.setNoteTime(now);
		// note.setDirty(true);
		// note.setLocalJobId(localJobId);
		// note.setMimeType("comment");
		// note.setType(Notes.TYPE_NOTE);
		// note.setState(Notes.STATE_ON_COMPLETE);
		//
		// SettingsDao settingsDao =
		// SettingsDao.getInstance(applicationContext);
		// String employeeId = settingsDao.getString("employeeId");
		//
		// if (employeeId != null) {
		// note.setById(Long.parseLong(employeeId));
		// }
		//
		// note.setByName(settingsDao.getString("employeeName"));
		//
		// NotesDao notesDao = NotesDao.getInstance(applicationContext);
		// notesDao.save(note);
		// Log.i(TAG, "Saved job completion note for local job ID " +
		// localJobId);
		// Intent intent = new Intent(applicationContext,
		// LocationCaptureService.class);
		// intent.putExtra(EffortProvider.Locations.PURPOSE,
		// EffortProvider.Locations.PURPOSE_NOTE);
		// intent.putExtra(EffortProvider.Locations.FOR_ID, note.getLocalId());
		// applicationContext.startService(intent);
	}

	/**
	 * Returns the local job id of the given remote job id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Long getLocalId(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Jobs._ID + " FROM " + Jobs.TABLE
					+ " WHERE " + Jobs.REMOTE_ID + " = " + remoteId, null);

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
	 * Returns the remote job id of the given local job id.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public Long getRemoteId(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db
					.rawQuery("SELECT " + Jobs.REMOTE_ID + " FROM "
							+ Jobs.TABLE + " WHERE " + Jobs._ID + " = "
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
	 * Returns the local creation time of the given local job id.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithLocalId(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Jobs.LOCAL_CREATION_TIME
					+ " FROM " + Jobs.TABLE + " WHERE " + Jobs._ID + " = "
					+ localId, null);

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
			cursor = db.rawQuery("SELECT " + Jobs.LOCAL_CREATION_TIME
					+ " FROM " + Jobs.TABLE + " WHERE " + Jobs.REMOTE_ID
					+ " = " + remoteId, null);

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
	 * Deletes all temporary jobs along with their comments.
	 * 
	 * Ideally, there should be only 1 temporary job.
	 * 
	 */
	public synchronized void deleteTemporaryJobs() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Cursor cursor = db.query(Jobs.TABLE, new String[] { Jobs._ID },
				Jobs.TEMPORARY + " = 'true'", null, null, null, null);
		FormsDao formsDao = FormsDao.getInstance(applicationContext);
		JobHistoriesDao jobHistoriesDao = JobHistoriesDao
				.getInstance(applicationContext);

		while (cursor.moveToNext()) {
			long localJobId = cursor.getLong(0);
			// deleting temporary forms
			formsDao.deleteTemporaryJobForms(localJobId);
			// deleting temporary job history
			jobHistoriesDao.deleteJobHistoryWithJobId(localJobId);
			// delete the job
			deleteJob(cursor.getLong(0));
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + cursor.getCount() + " temporary jobs.");
		}

		cursor.close();
	}

	/**
	 * Deletes all cached jobs along with their comments.
	 * 
	 */
	public synchronized int deleteCachedJobs() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Cursor cursor = db.query(Jobs.TABLE, new String[] { Jobs._ID },
				Jobs.CACHED + " = 'true'", null, null, null, null);

		while (cursor.moveToNext()) {
			deleteJob(cursor.getLong(0));
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + affectedRows + " cached jobs.");
		}

		cursor.close();
		return affectedRows;
	}

	public synchronized int deleteOldJobs() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		String today = SQLiteDateTimeUtils
				.getSQLiteDateTimeForBeginningOfToday();

		Cursor cursor = db.query(Jobs.TABLE, new String[] { Jobs._ID },
				Jobs.END_TIME + " < ?  AND " + Jobs.DIRTY + " = ? AND "
						+ Jobs.TREE_DIRTY + " = ? AND "
						+ Jobs.LOCAL_MODIFICATION_TIME + " < ?", new String[] {
						today, "false", "false", today }, null, null, null);

		while (cursor.moveToNext()) {
			deleteJob(cursor.getLong(0));
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + affectedRows + " old jobs.");
		}

		cursor.close();
		return affectedRows;
	}

	public synchronized void deleteAllJobs() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Cursor cursor = db.query(Jobs.TABLE, new String[] { Jobs._ID }, null,
				null, null, null, null);

		while (cursor.moveToNext()) {
			deleteJob(cursor.getLong(0));
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + cursor.getCount() + " jobs.");
		}

		cursor.close();
	}

	public synchronized void deleteJob(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Job job = getJobWithLocalId(localId);

		if (job == null) {
			Log.w(TAG, "job with " + localId + " is null.", new Exception());
			return;
		}

		// delete notes first
		NotesDao notesDao = NotesDao.getInstance(applicationContext);
		notesDao.deleteNotes(localId);

		// delete the calendar event
		if (job.getAndroidEventId() != null) {
			Utils.deleteCalendarEvent(applicationContext.getContentResolver(),
					job.getAndroidEventId());
		}

		// now, delete the job
		int affectedRows = db.delete(Jobs.TABLE, Jobs._ID + " = ?",
				new String[] { "" + localId });
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted job with local job id: " + localId
					+ ", affectedRows=" + affectedRows);
		}
	}

	public Job getJobWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Job job = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Jobs.TABLE, Jobs.ALL_COLUMNS, Jobs._ID + " = "
					+ localId, null, null, null, null);

			if (cursor.moveToNext()) {
				job = new Job();
				job.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return job;
	}

	public Job getJobWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Job job = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Jobs.TABLE, Jobs.ALL_COLUMNS, Jobs.REMOTE_ID
					+ " = " + remoteId, null, null, null, null);

			if (cursor.moveToNext()) {
				job = new Job();
				job.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return job;
	}

	/**
	 * Gets all non-temporary jobs.
	 * 
	 * @return
	 */
	public List<Job> getAllPermanentJobs() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Job> jobs = null;

		try {
			cursor = db.query(Jobs.TABLE, Jobs.ALL_COLUMNS, Jobs.TEMPORARY
					+ " = 'false'", null, null, null, Jobs.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				jobs = new ArrayList<Job>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Job job = new Job();
				job.load(cursor, applicationContext);
				jobs.add(job);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobs;
	}

	/**
	 * Get the jobs that are added on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<Job> getAddedJobs() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Job> jobs = null;

		try {
			cursor = db.query(Jobs.TABLE, Jobs.ALL_COLUMNS, Jobs.REMOTE_ID
					+ " IS NULL AND " + Jobs.TEMPORARY + " = 'false'", null,
					null, null, Jobs.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				jobs = new ArrayList<Job>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Job job = new Job();
				job.load(cursor, applicationContext);
				jobs.add(job);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobs;
	}

	/**
	 * Get the jobs that are modified on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<Job> getModifiedJobs() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Job> jobs = null;

		try {
			cursor = db.query(Jobs.TABLE, Jobs.ALL_COLUMNS, Jobs.REMOTE_ID
					+ " IS NOT NULL AND " + Jobs.DIRTY + " = 'true'", null,
					null, null, Jobs.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				jobs = new ArrayList<Job>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Job job = new Job();
				job.load(cursor, applicationContext);
				jobs.add(job);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobs;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasUnsyncedChanges() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Jobs.TABLE, new String[] { Jobs._ID }, "("
					+ Jobs.REMOTE_ID + " IS NULL AND " + Jobs.TEMPORARY
					+ " = 'false') OR (" + Jobs.REMOTE_ID + " IS NOT NULL AND "
					+ Jobs.DIRTY + " = 'true'" + ")", null, null, null, null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean hasJobsNow() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			String query = "SELECT COUNT(" + Jobs._ID + ") AS count FROM "
					+ Jobs.TABLE + " WHERE " + Jobs.TEMPORARY
					+ " = 'false' AND '"
					+ SQLiteDateTimeUtils.getCurrentSQLiteDateTime()
					+ "' >= DATETIME(" + Jobs.START_TIME
					+ ", '-30 minutes') AND '"
					+ SQLiteDateTimeUtils.getCurrentSQLiteDateTime()
					+ "' <= DATETIME(" + Jobs.END_TIME + ", '+30 minutes')";

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Has jobs now query: " + query);
			}

			cursor = db.rawQuery(query, null);

			cursor.moveToNext();

			long count = cursor.getLong(0);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, count + " jobs matched query.");
			}

			return count > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Date getOldestJobStartTime() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT MIN(" + Jobs.START_TIME + ") FROM "
					+ Jobs.TABLE, null);

			cursor.moveToNext();
			return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public int getNumberOfJobsOlderThanToday() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			// today in UTC
			String today = SQLiteDateTimeUtils
					.getSQLiteDateTimeForBeginningOfToday();

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Today: " + today);
			}

			cursor = db.rawQuery("SELECT COUNT(" + Jobs._ID + ") FROM "
					+ Jobs.TABLE + " WHERE " + Jobs.END_TIME + " < '" + today
					+ "' AND " + Jobs.LOCAL_MODIFICATION_TIME + " < '" + today
					+ "'", null);

			cursor.moveToNext();
			return cursor.getInt(0);
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
			cursor = db.rawQuery("SELECT " + Jobs.START_TIME + " FROM "
					+ Jobs.TABLE + " WHERE " + Jobs.REMOTE_ID + " = "
					+ remoteId, null);

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

	public int getAddedCount(Date after) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		String afterStr = SQLiteDateTimeUtils
				.getSQLiteDateTimeFromLocalTime(after);

		try {
			String query = "SELECT COUNT(" + Jobs._ID + ") FROM " + Jobs.TABLE
					+ " WHERE " + Jobs.TEMPORARY + " = 'false' AND "
					+ Jobs.LOCAL_CREATION_TIME + " > ?";

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"addedCount query: "
								+ query.replace("?", "'" + afterStr + "'"));
			}

			cursor = db.rawQuery(query, new String[] { afterStr });

			cursor.moveToNext();

			return cursor.getInt(0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public int getModifiedCount(Date after) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		String afterStr = SQLiteDateTimeUtils
				.getSQLiteDateTimeFromLocalTime(after);

		try {
			String query = "SELECT COUNT(DISTINCT " + Jobs.TABLE + "."
					+ Jobs._ID + ") FROM " + Jobs.TABLE + " LEFT JOIN "
					+ Notes.TABLE + " ON " + Jobs.TABLE + "." + Jobs._ID
					+ " = " + Notes.LOCAL_JOB_ID + " WHERE " + Jobs.TEMPORARY
					+ " = 'false' AND " + Jobs.TABLE + "."
					+ Jobs.LOCAL_CREATION_TIME + " <= ? AND ((" + Jobs.TABLE
					+ "." + Jobs.LOCAL_MODIFICATION_TIME + " > ?) OR ("
					+ Notes.TABLE + "." + Notes.REMOTE_ID
					+ " IS NOT NULL AND (" + Notes.TABLE + "."
					+ Notes.LOCAL_CREATION_TIME + " > ? OR " + Notes.TABLE
					+ "." + Notes.LOCAL_MODIFICATION_TIME + " > ?)))";

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"modifiedCount query: "
								+ query.replace("?", "'" + afterStr + "'"));
			}

			cursor = db.rawQuery(query, new String[] { afterStr, afterStr,
					afterStr, afterStr });

			cursor.moveToNext();
			return cursor.getInt(0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void updateCachedFlag(long localJobId, boolean cached) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Jobs.CACHED, "" + cached);

		db.update(Jobs.TABLE, values, Jobs._ID + " = " + localJobId, null);

		// Job job = getJobWithLocalId(localJobId);
		// Date now = new Date();
		//
		// if (now.before(job.getEndTime()) && job.getCached() != null
		// && job.getCached() == false && job.getTemporary() != null
		// && job.getTemporary() == false) {
		// SettingsDao settingsDao = SettingsDao
		// .getInstance(applicationContext);
		// Utils.insertOrUpdateCalendarEvent(applicationContext
		// .getContentResolver(), job, settingsDao.getInt(
		// EffortProvider.Settings.KEY_REMINDER_MINUTES,
		// EffortProvider.Settings.DEFAULT_REMINDER_MINUTES));
		// }
	}

	public synchronized void updateDirtyFlag(long localJobId, boolean dirty) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Jobs.DIRTY, "" + dirty);

		db.update(Jobs.TABLE, values, Jobs._ID + " = " + localJobId, null);
	}

	public synchronized void updateTreeDirtyFlag(long localJobId,
			boolean treeDirty) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Jobs.TREE_DIRTY, "" + treeDirty);

		db.update(Jobs.TABLE, values, Jobs._ID + " = " + localJobId, null);
	}

	public synchronized void updateState(long localJobId, int stateId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Jobs.JOB_STATE_ID, stateId);
		values.put(Jobs.DIRTY, "true");

		db.update(Jobs.TABLE, values, Jobs._ID + " = " + localJobId, null);
	}

	/**
	 * 
	 * @param remoteId
	 */
	public boolean getRead(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Jobs.READ + " FROM " + Jobs.TABLE
					+ " WHERE " + Jobs._ID + " = " + localId, null);

			if (cursor.moveToNext()) {
				String read = cursor.getString(0);

				if (TextUtils.isEmpty(read)) {
					return false;
				} else {
					return Boolean.parseBoolean(read);
				}
			} else {
				return false;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void updateReadFlag(long localJobId, boolean read) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Jobs.READ, "" + read);

		db.update(Jobs.TABLE, values, Jobs._ID + " = " + localJobId, null);
	}

	public synchronized void updateRemoteId(long localJobId, long remoteJobId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Jobs.REMOTE_ID, remoteJobId);

		db.update(Jobs.TABLE, values, Jobs._ID + " = " + localJobId, null);
	}

	public synchronized void updateReadFlag(boolean read) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.execSQL("UPDATE " + Jobs.TABLE + " SET " + Jobs.READ + " = '" + read
				+ "' WHERE " + Jobs.JOB_TYPE_ID + " IN (SELECT " + JobTypes._ID
				+ " FROM " + JobTypes.TABLE + " WHERE " + JobTypes.CHECKED
				+ " = 'true')");
	}

	public synchronized void updateTreeDirtyFlags() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		String query = "SELECT " + Jobs._ID + " FROM " + Jobs.TABLE + " WHERE "
				+ Jobs.REMOTE_ID + " IS NULL OR " + Jobs.DIRTY
				+ " = 'true' OR " + Jobs._ID + " IN (SELECT local_job_id FROM "
				+ Notes.TABLE + " LEFT JOIN " + Locations.TABLE + " ON "
				+ Notes.TABLE + "." + Notes._ID + " = " + Locations.TABLE + "."
				+ Locations.FOR_ID + " AND " + Locations.TABLE + "."
				+ Locations.PURPOSE + " = " + Locations.PURPOSE_NOTE
				+ " WHERE " + Notes.TABLE + "." + Notes.REMOTE_ID
				+ " IS NULL OR (" + Notes.TABLE + "." + Notes.LOCAL_MEDIA_PATH
				+ " IS NOT NULL AND " + Notes.TABLE + "." + Notes.MEDIA_ID
				+ " IS NULL) OR ((" + Notes.TABLE + "." + Notes.REMOTE_ID
				+ " IS NOT NULL AND " + Notes.TABLE + "." + Notes.DIRTY
				+ " = 'true') OR (" + Notes.TABLE + "." + Notes.REMOTE_ID
				+ " IS NULL AND " + Locations.TABLE + "."
				+ Locations.LOCATION_FINALIZED + " = 'false'))) OR " + Jobs._ID
				+ " IN (SELECT local_job_id FROM " + JobHistories.TABLE
				+ " LEFT JOIN " + Locations.TABLE + " ON " + JobHistories.TABLE
				+ "." + JobHistories._ID + " = " + Locations.TABLE + "."
				+ Locations.FOR_ID + " AND " + Locations.TABLE + "."
				+ Locations.PURPOSE + " = " + Locations.PURPOSE_HISTORY
				+ " WHERE " + JobHistories.TABLE + "." + JobHistories.REMOTE_ID
				+ " IS NULL OR (" + JobHistories.TABLE + "."
				+ JobHistories.REMOTE_ID + " IS NOT NULL AND ("
				+ JobHistories.TABLE + "." + JobHistories.DIRTY
				+ " = 'true' OR " + Locations.TABLE + "."
				+ Locations.LOCATION_FINALIZED + " = 'false'))) OR " + Jobs._ID
				+ " IN (SELECT " + JobHistories.LOCAL_JOB_ID + " FROM "
				+ JobHistories.TABLE + " WHERE " + JobHistories.LOCAL_FORM_ID
				+ " IN (SELECT " + Forms.TABLE + "." + Forms._ID + " FROM "
				+ Forms.TABLE + " LEFT JOIN " + Locations.TABLE + " ON "
				+ Forms.TABLE + "." + Forms._ID + " = " + Locations.TABLE + "."
				+ Locations.FOR_ID + " AND " + Locations.TABLE + "."
				+ Locations.PURPOSE + " = " + Locations.PURPOSE_FORM
				+ " WHERE " + Forms.TABLE + "." + Forms.REMOTE_ID
				+ " IS NULL OR " + Forms.TABLE + "." + Forms.DIRTY
				+ " = 'true' OR (" + Forms.TABLE + "." + Forms.REMOTE_ID
				+ " IS NULL AND " + Locations.TABLE + "."
				+ Locations.LOCATION_FINALIZED + " = 'false') UNION SELECT "
				+ FormFiles.LOCAL_FORM_ID + " FROM " + FormFiles.TABLE
				+ " WHERE " + FormFiles.LOCAL_MEDIA_PATH + " IS NOT NULL AND "
				+ FormFiles.MEDIA_ID + " IS NULL UNION SELECT "
				+ SectionFiles.LOCAL_FORM_ID + " FROM " + SectionFiles.TABLE
				+ " WHERE " + SectionFiles.LOCAL_MEDIA_PATH
				+ " IS NOT NULL AND " + SectionFiles.MEDIA_ID + " IS NULL))";

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Update tree dirty query: " + query);
		}

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.getCount() > 0) {
			List<Long> dirtyJobIds = new ArrayList<Long>(cursor.getCount());

			while (cursor.moveToNext()) {
				dirtyJobIds.add(cursor.getLong(0));
			}

			String ids = TextUtils.join(",", dirtyJobIds);
			String inQuery = "UPDATE " + Jobs.TABLE + " SET " + Jobs.TREE_DIRTY
					+ " = 'true' " + " WHERE " + Jobs._ID + " IN (" + ids + ")";
			String notInQuery = "UPDATE " + Jobs.TABLE + " SET "
					+ Jobs.TREE_DIRTY + " = 'false' " + " WHERE " + Jobs._ID
					+ " NOT IN (" + ids + ")";

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In query: " + inQuery);
				Log.i(TAG, "Not In query: " + notInQuery);
			}

			db.execSQL(inQuery);
			db.execSQL(notInQuery);
		} else {
			db.execSQL("UPDATE " + Jobs.TABLE + " SET " + Jobs.TREE_DIRTY
					+ " = 'false'");
		}

		cursor.close();
	}

	/**
	 * Returns a future job
	 * 
	 * @return
	 */
	public Job getNextFutureIncompleteJob() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Job job = null;
		Cursor cursor = null;
		String where = "";
		try {
			where = Jobs.START_TIME + " >= '"
					+ SQLiteDateTimeUtils.getCurrentSQLiteDateTime()
					+ "' AND (" + Jobs.LATE_ALERT_DISMISSED + " IS NULL OR "
					+ Jobs.LATE_ALERT_DISMISSED + " = 'false') AND ("
					+ Jobs.COMPLETED + " IS NULL OR " + Jobs.COMPLETED
					+ " = 'false')";
			cursor = db.query(Jobs.TABLE, Jobs.ALL_COLUMNS, where, null, null,
					null, Jobs.START_TIME + " ASC", "1");

			if (cursor.moveToNext()) {
				job = new Job();
				job.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return job;
	}

	/**
	 * Returns a future job which will be completed first in future
	 * 
	 * @return
	 */
	public Job getNextFutureJobBasedOnCompletionTime() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Job job = null;
		Cursor cursor = null;
		String where = "";
		try {
			// SELECT * , min(end_time) FROM jobs where completed="false"
			where = Jobs.END_TIME + " >= ? AND " + Jobs.COMPLETED + " = ?";

			// String[] coloumns = new String[Jobs.ALL_COLUMNS.length];//
			// Jobs.ALL_COLUMNS;
			// coloumns[9] = "MIN(" + Jobs.END_TIME + ") AS " + Jobs.END_TIME;

			cursor = db.query(
					Jobs.TABLE,
					Jobs.ALL_COLUMNS,
					where,
					new String[] {
							SQLiteDateTimeUtils.getCurrentSQLiteDateTime(),
							"false" }, null, null, Jobs.END_TIME + " ASC", "1");

			if (cursor.moveToNext()) {
				job = new Job();
				job.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return job;
	}

	public Job getNextFutureJobBasedOnStartTime() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Job job = null;
		Cursor cursor = null;
		String where = "";
		try {
			// SELECT * , min(end_time) FROM jobs where completed="false"
			where = Jobs.START_TIME + " >= ? AND " + Jobs.COMPLETED + " = ?";

			// String[] coloumns = new String[Jobs.ALL_COLUMNS.length];//
			// Jobs.ALL_COLUMNS;
			// coloumns[9] = "MIN(" + Jobs.END_TIME + ") AS " + Jobs.END_TIME;

			cursor = db.query(
					Jobs.TABLE,
					Jobs.ALL_COLUMNS,
					where,
					new String[] {
							SQLiteDateTimeUtils.getCurrentSQLiteDateTime(),
							"false" }, null, null, Jobs.START_TIME + " ASC",
					"1");

			if (cursor.moveToNext()) {
				job = new Job();
				job.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return job;
	}

	public synchronized void updateLateAlertDismissedFlag(long localJobId,
			boolean dismissed) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Jobs.LATE_ALERT_DISMISSED, "" + dismissed);

		db.update(Jobs.TABLE, values, Jobs._ID + " = " + localJobId, null);
	}

	/**
	 * 
	 * @return List of jobs to the given working for today.
	 */
	public List<Job> getJobsForWorkingHours() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Job> jobs = new ArrayList<Job>();

		try {
			Calendar now = Calendar.getInstance();
			int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
			WorkingHour workingHour = WorkingHoursDao.getInstance(
					applicationContext).getWorkingHour(dayOfWeek);
			if (workingHour != null) {
				cursor = db
						.query(Jobs.TABLE,
								Jobs.ALL_COLUMNS,
								Jobs.START_TIME + " >= ? AND " + Jobs.END_TIME
										+ " <= ? AND " + Jobs.COMPLETED
										+ " = ?",
								new String[] {
										SQLiteDateTimeUtils
												.getSQLiteDateTimeFromLocalTime(workingHour
														.getStartTime()),
										SQLiteDateTimeUtils
												.getSQLiteDateTimeFromLocalTime(workingHour
														.getEndTime()), "false" },
								null, null, null);

				if (cursor != null && cursor.moveToFirst()) {
					jobs = new ArrayList<Job>(cursor.getCount());
					do {
						Job job = new Job();
						job.load(cursor, applicationContext);
						jobs.add(job);
					} while (cursor.moveToNext());
				}
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobs;
	}

	/**
	 * 
	 * @return List of pending jobs.
	 */
	public List<Job> getPendingJobs(Date endTime) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Job> jobs = new ArrayList<Job>();

		try {
			cursor = db.query(
					Jobs.TABLE,
					Jobs.ALL_COLUMNS,
					Jobs.END_TIME + " < ? AND " + Jobs.COMPLETED + " = ?",
					new String[] {
							SQLiteDateTimeUtils
									.getSQLiteDateTimeFromLocalTime(endTime),
							"false" }, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				jobs = new ArrayList<Job>(cursor.getCount());
				do {
					Job job = new Job();
					job.load(cursor, applicationContext);
					jobs.add(job);
				} while (cursor.moveToNext());
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobs;
	}

	/**
	 * 
	 * @return List of jobs to the given working for today.
	 */
	public List<Job> getPendingJobsWithinTimerange(WorkingHour workingHour) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Job> jobs = new ArrayList<Job>();

		try {

			if (workingHour != null) {
				cursor = db
						.query(Jobs.TABLE,
								Jobs.ALL_COLUMNS,
								Jobs.START_TIME + " >= ? AND " + Jobs.END_TIME
										+ " <= ? AND " + Jobs.COMPLETED
										+ " = ?",
								new String[] {
										SQLiteDateTimeUtils
												.getSQLiteDateTimeFromLocalTime(workingHour
														.getStartTime()),
										SQLiteDateTimeUtils
												.getSQLiteDateTimeFromLocalTime(workingHour
														.getEndTime()), "false" },
								null, null, null);

				if (cursor != null && cursor.moveToFirst()) {
					jobs = new ArrayList<Job>(cursor.getCount());
					do {
						Job job = new Job();
						job.load(cursor, applicationContext);
						jobs.add(job);
					} while (cursor.moveToNext());
				}
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobs;
	}
}

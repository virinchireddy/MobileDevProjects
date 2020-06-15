package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.Leave;
import in.spoors.effort1.provider.EffortProvider.Leaves;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LeavesDao {

	public static final String TAG = "LeavesDao";
	private static LeavesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static LeavesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new LeavesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private LeavesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(Leave leave) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (leave.getLocalId() != null
				&& leaveWithLocalIdExists(leave.getLocalId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting leave.");
			}

			leave.setLocalModificationTime(new Date());
			leave.setLocalCreationTime(getLocalCreationTimeWithLocalId(leave
					.getLocalId()));

			ContentValues values = leave.getContentValues(null);

			db.update(Leaves.TABLE, values,
					Leaves._ID + " = " + leave.getLocalId(), null);

			return;
		}

		if (leave.getRemoteId() != null
				&& leaveWithRemoteIdExists(leave.getRemoteId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting leave.");
			}

			leave.setLocalModificationTime(new Date());
			leave.setLocalCreationTime(getLocalCreationTimeWithRemoteId(leave
					.getRemoteId()));

			ContentValues values = leave.getContentValues(null);

			db.update(Leaves.TABLE, values,
					Leaves.REMOTE_ID + " = " + leave.getRemoteId(), null);

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new leave.");
		}

		Date now = new Date();
		leave.setLocalCreationTime(now);
		leave.setLocalModificationTime(now);

		ContentValues values = leave.getContentValues(null);
		long insertedId = db.insert(Leaves.TABLE, null, values);
		leave.setLocalId(insertedId);
	}

	public synchronized void cancelLeave(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Leave leave = getLeaveWithLocalId(localId);

		if (leave == null) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "Leave with local ID " + localId
						+ " does not exist (while trying to cancel the leave).");
			}

			return;
		}

		// if it has not been synced yet, delete the record right away
		if (leave.getRemoteId() == null) {
			db.execSQL("DELETE FROM " + Leaves.TABLE + " WHERE " + Leaves._ID
					+ " = " + localId);
		} else {
			leave.setCancelled(true);
			leave.setDirty(true);
			save(leave);
		}
	}

	public synchronized void deleteLeaveWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.execSQL("DELETE FROM " + Leaves.TABLE + " WHERE " + Leaves._ID
				+ " = " + localId);
	}

	public synchronized void deleteLeaveWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.execSQL("DELETE FROM " + Leaves.TABLE + " WHERE " + Leaves.REMOTE_ID
				+ " = " + remoteId);
	}

	public boolean leaveWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Leaves._ID
					+ ") AS count FROM " + Leaves.TABLE + " WHERE "
					+ Leaves._ID + " = " + localId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean leaveWithRemoteIdExists(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Leaves._ID
					+ ") AS count FROM " + Leaves.TABLE + " WHERE "
					+ Leaves.REMOTE_ID + " = " + remoteId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Leave getLeaveWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Leave leave = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Leaves.TABLE, Leaves.ALL_COLUMNS, Leaves._ID
					+ " = " + localId, null, null, null, null);

			if (cursor.moveToNext()) {
				leave = new Leave();
				leave.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return leave;
	}

	public Leave getLeaveWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Leave leave = null;
		Cursor cursor = null;

		try {
			cursor = db
					.query(Leaves.TABLE, Leaves.ALL_COLUMNS, Leaves.REMOTE_ID
							+ " = " + remoteId, null, null, null, null);

			if (cursor.moveToNext()) {
				leave = new Leave();
				leave.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return leave;
	}

	public Date getLocalCreationTimeWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Leaves.LOCAL_CREATION_TIME
					+ " FROM " + Leaves.TABLE + " WHERE " + Leaves._ID + " = "
					+ localId, null);

			cursor.moveToNext();

			return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Date getLocalCreationTimeWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Leaves.LOCAL_CREATION_TIME
					+ " FROM " + Leaves.TABLE + " WHERE " + Leaves.REMOTE_ID
					+ " = " + remoteId, null);

			cursor.moveToNext();

			return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Get the leaves that were added on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<Leave> getAddedLeaves() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Leave> leaves = null;

		try {
			cursor = db.query(Leaves.TABLE, Leaves.ALL_COLUMNS,
					Leaves.REMOTE_ID + " IS NULL", null, null, null,
					Leaves.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				leaves = new ArrayList<Leave>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Leave leave = new Leave();
				leave.load(cursor);
				leaves.add(leave);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return leaves;
	}

	/**
	 * Get the leaves that were modified on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<Leave> getModifiedLeaves() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Leave> leaves = null;

		try {
			cursor = db.query(Leaves.TABLE, Leaves.ALL_COLUMNS,
					Leaves.REMOTE_ID + " IS NOT NULL AND " + Leaves.DIRTY
							+ " = 'true'", null, null, null,
					Leaves.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				leaves = new ArrayList<Leave>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Leave leave = new Leave();
				leave.load(cursor);
				leaves.add(leave);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return leaves;
	}

	/**
	 * Get the leaves that were cancelled on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<Long> getCancelledLeaves() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Long> leaves = null;

		try {
			cursor = db
					.query(Leaves.TABLE, new String[] { Leaves.REMOTE_ID },
							Leaves.REMOTE_ID + " IS NOT NULL AND "
									+ Leaves.DIRTY + " = 'true' AND "
									+ Leaves.CANCELLED + " = 'true'", null,
							null, null, Leaves.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				leaves = new ArrayList<Long>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				leaves.add(cursor.getLong(0));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return leaves;
	}

	public boolean hasUnsyncedChanges() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db
					.query(Leaves.TABLE, new String[] { Leaves._ID },
							Leaves.REMOTE_ID + " IS NULL OR ("
									+ Leaves.REMOTE_ID + " IS NOT NULL AND "
									+ Leaves.DIRTY + " = 'true')", null, null,
							null, null);

			return (cursor.getCount() > 0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean isOnLeaveNow() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery(
					"SELECT COUNT(" + Leaves._ID + ") AS count FROM "
							+ Leaves.TABLE + " WHERE " + Leaves.STATUS + " = "
							+ Leaves.STATUS_APPROVED + " AND '"
							+ SQLiteDateTimeUtils.getCurrentSQLiteDateTime()
							+ "' >= " + Leaves.START_TIME + " AND '"
							+ SQLiteDateTimeUtils.getCurrentSQLiteDateTime()
							+ "' <= " + Leaves.END_TIME, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Returns the remote note id of the given local note id.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public Long getRemoteId(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Leaves.REMOTE_ID + " FROM "
					+ Leaves.TABLE + " WHERE " + Leaves._ID + " = " + localId,
					null);

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

}

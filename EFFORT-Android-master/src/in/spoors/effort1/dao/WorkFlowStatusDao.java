package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dto.WorkFlowStatusDto;
import in.spoors.effort1.provider.EffortProvider.Forms;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.WorkFlowStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

public class WorkFlowStatusDao {

	public static final String TAG = "WorkFlowStatusDao";
	private static WorkFlowStatusDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static WorkFlowStatusDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new WorkFlowStatusDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private WorkFlowStatusDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(WorkFlowStatusDto workFlowStatus) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		Date now = new Date();

		if (workFlowStatus.getClientFormId() == null
				&& workFlowStatus.getRemoteFormId() != null) {
			FormsDao formsDao = FormsDao.getInstance(applicationContext);
			workFlowStatus.setClientFormId(formsDao.getLocalId(workFlowStatus
					.getRemoteFormId()));
		}
		if (workFlowStatus.getLocalId() != null
				&& workFlowStatusWithLocalIdExists(workFlowStatus.getLocalId())) {
			WorkFlowStatusDto oldWorkFlowStatus = getWorkFlowStatusWithLocalId(workFlowStatus
					.getLocalId());
			if (workFlowsAreDifferent(workFlowStatus, oldWorkFlowStatus)) {
				workFlowStatus.setLocalModificationTime(new Date());
			}
			if (workFlowStatus.getLocalCreationTime() == null) {
				workFlowStatus.setLocalCreationTime(oldWorkFlowStatus
						.getLocalCreationTime());
			}

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the existing workFlowStatus: "
						+ workFlowStatus.toString());
			}
			ContentValues values = workFlowStatus.getContentValues(null);

			db.update(WorkFlowStatus.TABLE, values, WorkFlowStatus._ID + " = "
					+ workFlowStatus.getLocalId(), null);

			return;
		}
		if (workFlowStatus.getRemotelId() != null
				&& workFlowStatusWithRemoteIdExists(workFlowStatus
						.getRemotelId())) {
			WorkFlowStatusDto oldWorkFlowStatus = getWorkFlowStatusWithRemoteId(workFlowStatus
					.getRemotelId());
			if (workFlowsAreDifferent(workFlowStatus, oldWorkFlowStatus)) {
				workFlowStatus.setLocalModificationTime(new Date());
			}
			if (workFlowStatus.getLocalCreationTime() == null) {
				workFlowStatus.setLocalCreationTime(oldWorkFlowStatus
						.getLocalCreationTime());
			}
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the existing workFlowStatus: "
						+ workFlowStatus.toString());
			}

			ContentValues values = workFlowStatus.getContentValues(null);

			db.update(WorkFlowStatus.TABLE, values, WorkFlowStatus.REMOTE_ID
					+ " = " + workFlowStatus.getRemotelId(), null);

			return;
		}
		workFlowStatus.setLocalCreationTime(now);
		workFlowStatus.setLocalModificationTime(now);

		ContentValues values = workFlowStatus.getContentValues(null);
		long id = db.insert(WorkFlowStatus.TABLE, null, values);
		workFlowStatus.setLocalId(id);
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saved a new workFlowSpec: " + workFlowStatus.toString());
		}
	}

	public boolean workFlowStatusWithLocalIdExists(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + WorkFlowStatus._ID
					+ ") AS count FROM " + WorkFlowStatus.TABLE + " WHERE "
					+ WorkFlowStatus._ID + " = " + id, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean workFlowStatusWithRemoteIdExists(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + WorkFlowStatus.REMOTE_ID
					+ ") AS count FROM " + WorkFlowStatus.TABLE + " WHERE "
					+ WorkFlowStatus.REMOTE_ID + " = " + remoteId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<WorkFlowStatusDto> getPendingUnSyncedWorkFlowStatus() {
		// SQLiteDatabase db = DBHelper.getInstance(applicationContext)
		// .getReadableDatabase();

		Cursor cursor = null;
		List<WorkFlowStatusDto> workFlowStatusList = null;

		try {

			cursor = queryWith2WayJoinForModify(null, null, null);

			if (cursor.getCount() > 0) {
				workFlowStatusList = new ArrayList<WorkFlowStatusDto>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				WorkFlowStatusDto workFlowStatus = new WorkFlowStatusDto();
				workFlowStatus.load(cursor, applicationContext);
				workFlowStatusList.add(workFlowStatus);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return workFlowStatusList;
	}

	public List<WorkFlowStatusDto> getAddedUnSyncedWorkFlowStatus() {
		// SQLiteDatabase db = DBHelper.getInstance(applicationContext)
		// .getReadableDatabase();

		Cursor cursor = null;
		List<WorkFlowStatusDto> workFlowStatusList = null;

		try {

			cursor = queryWith2WayJoin(WorkFlowStatus.REMOTE_ID
					+ " IS NULL AND (" + Locations.LOCATION_FINALIZED
					+ " = 'true' OR " + Locations.LOCATION_FINALIZED
					+ " IS NULL) AND " + WorkFlowStatus.TABLE + "."
					+ WorkFlowStatus.DIRTY + " = 'true'", null, null);

			if (cursor.getCount() > 0) {
				workFlowStatusList = new ArrayList<WorkFlowStatusDto>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				WorkFlowStatusDto workFlowStatus = new WorkFlowStatusDto();
				workFlowStatus.load(cursor, applicationContext);
				workFlowStatusList.add(workFlowStatus);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return workFlowStatusList;
	}

	public List<WorkFlowStatusDto> getModifiedUnSyncedWorkFlowStatus() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		Cursor cursor = null;
		List<WorkFlowStatusDto> workFlowStatusList = null;

		try {

			cursor = db.query(WorkFlowStatus.TABLE, WorkFlowStatus.ALL_COLUMNS,
					WorkFlowStatus.DIRTY + " = 'true' AND "
							+ WorkFlowStatus.REMOTE_ID + " IS NOT NULL ", null,
					null, null, null);

			if (cursor.getCount() > 0) {
				workFlowStatusList = new ArrayList<WorkFlowStatusDto>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				WorkFlowStatusDto workFlowStatus = new WorkFlowStatusDto();
				workFlowStatus.load(cursor, applicationContext);
				workFlowStatusList.add(workFlowStatus);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return workFlowStatusList;
	}

	private Cursor queryWith2WayJoin(String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(WorkFlowStatus.TABLE + " LEFT JOIN "
				+ Locations.TABLE + " ON " + Locations.PURPOSE + " = "
				+ Locations.PURPOSE_FORM + " AND " + Locations.FOR_ID + " = "
				+ WorkFlowStatus.TABLE + "." + WorkFlowStatus.LOCAL_FORM_ID);

		// qualify the columns by the table name, so that join
		// doesn't result confuse the query engine with ambiguous column
		// names
		String[] columns = new String[WorkFlowStatus.ALL_COLUMNS.length];
		for (int i = 0; i < WorkFlowStatus.ALL_COLUMNS.length; ++i) {
			columns[i] = WorkFlowStatus.TABLE + "."
					+ WorkFlowStatus.ALL_COLUMNS[i];
		}

		return builder.query(db, columns, selection, selectionArgs, null, null,
				sortOrder);

	}

	private Cursor queryWith2WayJoinForModify(String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(WorkFlowStatus.TABLE + " JOIN " + Forms.TABLE
				+ " ON " + WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.LOCAL_FORM_ID + " = " + Forms.TABLE + "."
				+ Forms._ID + " AND " + WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.REMOTE_ID + " IS NULL AND "
				+ WorkFlowStatus.TABLE + "." + WorkFlowStatus.DIRTY
				+ "= 'true' AND " + Forms.TABLE + "." + Forms.REMOTE_ID
				+ " IS NOT NULL");

		// qualify the columns by the table name, so that join
		// doesn't result confuse the query engine with ambiguous column
		// names
		String[] columns = new String[WorkFlowStatus.ALL_COLUMNS.length];
		for (int i = 0; i < WorkFlowStatus.ALL_COLUMNS.length; ++i) {
			columns[i] = WorkFlowStatus.TABLE + "."
					+ WorkFlowStatus.ALL_COLUMNS[i];
		}

		return builder.query(db, columns, selection, selectionArgs, null, null,
				sortOrder);

	}

	/**
	 * 
	 * @return null if there are no form spec id's
	 */
	public String getFormIdsIn() {
		// StringBuffer sb = new StringBuffer("(");
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Long> ids = null;
		try {

			cursor = db.query(WorkFlowStatus.TABLE,
					new String[] { WorkFlowStatus.LOCAL_FORM_ID },
					WorkFlowStatus.LOCAL_FORM_ID + " IS NOT NULL", null, null,
					null, WorkFlowStatus.LOCAL_FORM_ID);

			if (cursor.getCount() > 0) {
				ids = new ArrayList<Long>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				ids.add(cursor.getLong(0));
			}
			String join;
			if (ids == null) {
				return null;
			} else {
				join = TextUtils.join(",", ids);
				join = "(" + join + ")";
				return join;
			}
			// int count = cursor.getCount();
			//
			// for (int i = 0; i < count; ++i) {
			// cursor.moveToNext();
			// sb.append(cursor.getInt(0));
			//
			// if (i < count - 1) {
			// sb.append(", ");
			// }
			// }

			// sb.append(")");
		} finally {
			if (cursor != null)
				cursor.close();
		}

		// String in = sb.toString();
		//
		// if (BuildConfig.DEBUG) {
		// Log.d(TAG, "In clause: " + in);
		// }

		// return in;
	}

	public WorkFlowStatusDto getWorkFlowStatusWithRemoteId(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		WorkFlowStatusDto workFlowSpec = null;

		try {
			cursor = db.query(WorkFlowStatus.TABLE, WorkFlowStatus.ALL_COLUMNS,
					WorkFlowStatus.REMOTE_ID + " = " + id, null, null, null,
					null);

			if (cursor.moveToNext()) {

				workFlowSpec = new WorkFlowStatusDto();
				workFlowSpec.load(cursor, applicationContext);
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return workFlowSpec;
	}

	public WorkFlowStatusDto getWorkFlowStatusWithLocalFormId(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		WorkFlowStatusDto workFlowStatus = null;

		try {
			cursor = db.query(WorkFlowStatus.TABLE, WorkFlowStatus.ALL_COLUMNS,
					WorkFlowStatus.LOCAL_FORM_ID + " = " + id, null, null,
					null, null);

			if (cursor.moveToNext()) {

				workFlowStatus = new WorkFlowStatusDto();
				workFlowStatus.load(cursor, applicationContext);
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return workFlowStatus;
	}

	public WorkFlowStatusDto getWorkFlowStatusWithLocalId(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		WorkFlowStatusDto workFlowSpec = null;

		try {
			cursor = db.query(WorkFlowStatus.TABLE, WorkFlowStatus.ALL_COLUMNS,
					WorkFlowStatus._ID + " = " + id, null, null, null, null);

			if (cursor.moveToNext()) {

				workFlowSpec = new WorkFlowStatusDto();
				workFlowSpec.load(cursor, applicationContext);
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return workFlowSpec;
	}

	public boolean workFlowsAreDifferent(WorkFlowStatusDto wfs1,
			WorkFlowStatusDto wfs2) {
		if (wfs1 == null && wfs2 == null) {
			return false;
		}

		if (wfs1 == null || wfs2 == null) {
			return true;
		}

		// both the workflowstatus are not null
		if (!TextUtils.equals(wfs1.getStageName(), wfs2.getStageName())
				|| !Utils.longsEqual(wfs1.getStatus(), wfs2.getStatus())) {
			return true;
		}

		return false;
	}

	public int getAddedWorkFlowStatusCount(Date after) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		String afterStr = SQLiteDateTimeUtils
				.getSQLiteDateTimeFromLocalTime(after);

		try {
			String query = "SELECT COUNT(" + WorkFlowStatus._ID + ") FROM "
					+ WorkFlowStatus.TABLE + " WHERE "
					+ WorkFlowStatus.LOCAL_CREATION_TIME + " > ?";

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

	public int getModifiedWorkFlowStatusCount(Date after) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		String afterStr = SQLiteDateTimeUtils
				.getSQLiteDateTimeFromLocalTime(after);

		try {
			String query = "SELECT COUNT(" + WorkFlowStatus._ID + ") FROM "
					+ WorkFlowStatus.TABLE + " WHERE "
					+ WorkFlowStatus.LOCAL_CREATION_TIME + " <= ? AND "
					+ WorkFlowStatus.LOCAL_MODIFICATION_TIME + " > ?";

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"modifiedCount query: "
								+ query.replace("?", "'" + afterStr + "'"));
			}

			cursor = db.rawQuery(query, new String[] { afterStr, afterStr });

			cursor.moveToNext();
			return cursor.getInt(0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized int deleteOldWorkFlows() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		String cleanUpFromDay = SQLiteDateTimeUtils
				.getSQLiteDateTimeForBeginningOfDayFromTodayBefore(7);

		// Cursor cursor = db
		// .query(WorkFlowStatus.TABLE,
		// new String[] { WorkFlowStatus._ID },
		// WorkFlowStatus.MODIFIED_TIME + " < ?  AND "
		// + WorkFlowStatus.DIRTY + " = ? AND ("
		// + WorkFlowStatus.STATUS + " = ? OR "
		// + WorkFlowStatus.STATUS + " = ? ) AND "
		// + WorkFlowStatus.LOCAL_MODIFICATION_TIME
		// + " < ?", new String[] { cleanUpFromDay,
		// "false",
		// WorkFlowStatusDto.STATUS_CANCELLED + "",
		// WorkFlowStatusDto.STATUS_APPROVED + "",
		// cleanUpFromDay }, null, null, null);
		Cursor cursor = db.query(WorkFlowStatus.TABLE,
				new String[] { WorkFlowStatus._ID },
				WorkFlowStatus.MODIFIED_TIME + " < '" + cleanUpFromDay
						+ "'  AND " + WorkFlowStatus.DIRTY + " = 'false' AND ("
						+ WorkFlowStatus.STATUS + " = "
						+ WorkFlowStatusDto.STATUS_CANCELLED + " OR "
						+ WorkFlowStatus.STATUS + " = "
						+ WorkFlowStatusDto.STATUS_APPROVED + " ) AND "
						+ WorkFlowStatus.LOCAL_MODIFICATION_TIME + " < '"
						+ cleanUpFromDay + "'", null, null, null, null);

		while (cursor.moveToNext()) {
			deleteWorkFlowStatus(cursor.getLong(0));
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + affectedRows + " old workflowstatus.");
		}

		cursor.close();
		return affectedRows;
	}

	public synchronized void deleteWorkFlowStatus(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		// Job job = getJobWithLocalId(localId);
		WorkFlowStatusDto wfStatus = getWorkFlowStatusWithLocalId(localId);

		if (wfStatus == null) {
			Log.w(TAG, "WorkFlowStatus with " + localId + " is null.",
					new Exception());
			return;
		}

		// Deleting Workflow History
		WorkFlowHistoriesDao workFlowHistoriesDao = WorkFlowHistoriesDao
				.getInstance(applicationContext);
		workFlowHistoriesDao.deleteWorkFlowHistory(wfStatus.getClientFormId());

		// Deleting forms
		FormsDao formsDao = FormsDao.getInstance(applicationContext);
		formsDao.deleteForm(wfStatus.getClientFormId());
		// delete notes first

		// now, delete the workflowstate
		int affectedRows = db.delete(WorkFlowStatus.TABLE, WorkFlowStatus._ID
				+ " = ?", new String[] { "" + localId });
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted WorkFlowStatus with local WorkFlowStatus id: "
					+ localId + ", affectedRows=" + affectedRows);
		}
	}
}

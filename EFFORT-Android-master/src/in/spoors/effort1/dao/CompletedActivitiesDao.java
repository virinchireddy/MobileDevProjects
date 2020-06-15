package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.CompletedActivity;
import in.spoors.effort1.provider.EffortProvider.CompletedActivities;
import in.spoors.effort1.provider.EffortProvider.Locations;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class CompletedActivitiesDao {
	public static final String TAG = "CompletedActivitiesDao";
	private static CompletedActivitiesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static CompletedActivitiesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new CompletedActivitiesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private CompletedActivitiesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public CompletedActivity getCompletedActivityWithRemoteId(long remoteId) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(CompletedActivities.TABLE,
					CompletedActivities.ALL_COLUMNS,
					CompletedActivities.REMOTE_ID + " = " + remoteId, null,
					null, null, null);

			if (cursor.moveToNext()) {
				CompletedActivity completedActivity = new CompletedActivity();
				completedActivity.load(cursor, applicationContext);
				return completedActivity;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public CompletedActivity getCompletedActivityWithLocalId(Long localId) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(CompletedActivities.TABLE,
					CompletedActivities.ALL_COLUMNS, CompletedActivities._ID
							+ " = " + localId, null, null, null, null);

			if (cursor.moveToNext()) {
				CompletedActivity completedActivity = new CompletedActivity();
				completedActivity.load(cursor, applicationContext);
				return completedActivity;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public synchronized void save(CompletedActivity completedActivity) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (completedActivity.getClientFormId() == null
				&& completedActivity.getFormId() != null) {
			FormsDao formsDao = FormsDao.getInstance(applicationContext);
			completedActivity.setClientFormId(formsDao
					.getLocalId(completedActivity.getFormId()));
		}

		if (completedActivity.getLocalId() != null
				&& completedActivityWithIdExists(completedActivity.getLocalId())) {

			ContentValues values = completedActivity.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the existing CompletedActivity: "
						+ completedActivity.toString());
			}

			db.update(
					CompletedActivities.TABLE,
					values,
					CompletedActivities._ID + " = "
							+ completedActivity.getLocalId(), null);

			return;
		}

		ContentValues values = completedActivity.getContentValues(null);
		db.insert(CompletedActivities.TABLE, null, values);

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Saved a new CompletedActivity: "
							+ completedActivity.toString());
		}
	}

	public boolean completedActivityWithIdExists(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + CompletedActivities._ID
					+ ") AS count FROM " + CompletedActivities.TABLE
					+ " WHERE " + CompletedActivities._ID + " = " + id, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<CompletedActivity> getAllUnSyncCompletedActivities() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		Cursor cursor = null;
		List<CompletedActivity> completedActivities = null;

		try {
			cursor = db.query(CompletedActivities.TABLE,
					CompletedActivities.ALL_COLUMNS,
					CompletedActivities.REMOTE_ID + " IS NULL AND "
							+ CompletedActivities.DIRTY + " = 'true'", null,
					null, null, null);
			if (cursor.getCount() > 0) {
				completedActivities = new ArrayList<CompletedActivity>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				CompletedActivity completedActivity = new CompletedActivity();
				completedActivity.load(cursor, applicationContext);
				completedActivities.add(completedActivity);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return completedActivities;
	}

	public List<CompletedActivity> getAllCompletedActivitiesWithAssignRouteIdAndCustomerId(
			Long assignRouteId, Long customerId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		Cursor cursor = null;
		List<CompletedActivity> completedActivities = null;

		try {
			// cursor = queryWith2WayJoin(JobHistories.REMOTE_ID +
			// " IS NULL AND "
			// + Locations.LOCATION_FINALIZED + " = 'true'", null, null);
			cursor = db.query(CompletedActivities.TABLE,
					CompletedActivities.ALL_COLUMNS,
					CompletedActivities.ASSIGNED_ROUTE_ID + "=" + assignRouteId
							+ " AND " + CompletedActivities.CUSTOMER_ID + "="
							+ customerId, null, null, null,
					CompletedActivities.ACTIVITY_COMPLETED_TIME + " DESC");
			if (cursor.getCount() > 0) {
				completedActivities = new ArrayList<CompletedActivity>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				CompletedActivity completedActivity = new CompletedActivity();
				completedActivity.load(cursor, applicationContext);
				completedActivities.add(completedActivity);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return completedActivities;

	}

	public int getNumberOfCompletedActivitiesWithAssignRouteIdAndCustomerId(
			Long assignRouteId, Long customerId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + CompletedActivities._ID
					+ ") FROM " + CompletedActivities.TABLE + " WHERE "
					+ CompletedActivities.ASSIGNED_ROUTE_ID + "="
					+ assignRouteId + " AND " + CompletedActivities.CUSTOMER_ID
					+ "=" + customerId, null);

			cursor.moveToNext();

			return cursor.getInt(0);

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<CompletedActivity> getAllCompletedActivitiesWithCustomerId(
			Long customerId, boolean showFutureItemsOnly) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		Cursor cursor = null;
		List<CompletedActivity> completedActivities = null;

		try {
			String projection;
			if (showFutureItemsOnly) {
				String today = SQLiteDateTimeUtils
						.getSQLiteDateTimeForBeginningOfToday();
				projection = CompletedActivities.CUSTOMER_ID + "=" + customerId
						+ " AND " + CompletedActivities.ACTIVITY_COMPLETED_TIME
						+ " >= '" + today + "'";
			} else {
				projection = CompletedActivities.CUSTOMER_ID + "=" + customerId;

			}

			cursor = db
					.query(CompletedActivities.TABLE,
							CompletedActivities.ALL_COLUMNS, projection, null,
							null, null,
							CompletedActivities.ACTIVITY_COMPLETED_TIME
									+ " DESC");
			if (cursor.getCount() > 0) {
				completedActivities = new ArrayList<CompletedActivity>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				CompletedActivity completedActivity = new CompletedActivity();
				completedActivity.load(cursor, applicationContext);
				completedActivities.add(completedActivity);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return completedActivities;

	}

	// public void updateClientFormIdWithRemoteFormId(Form form) {
	//
	// SQLiteDatabase db = DBHelper.getInstance(applicationContext)
	// .getReadableDatabase();
	// Cursor cursor = null;
	// if (form == null || form.getLocalId() == null
	// || form.getRemoteId() == null) {
	// return;
	// }
	// try {
	//
	// cursor = db.rawQuery(
	// "UPDATE " + CompletedActivities.TABLE + " set "
	// + CompletedActivities.FORM_ID + " = "
	// + form.getRemoteId() + " where "
	// + CompletedActivities.CLIENT_FORM_ID + " = "
	// + form.getLocalId(), null);
	// cursor.moveToNext();
	//
	// // return cursor.getLong(0) > 0;
	// } finally {
	// if (cursor != null) {
	// cursor.close();
	// }
	// }
	// }

	public int getNumberOfCustomerRouteHistoryOlderThanToday(long customerId) {
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
			/*
			 * + "' AND " + Jobs.LOCAL_MODIFICATION_TIME + " < '" + today + "'"
			 */
			cursor = db.rawQuery("SELECT COUNT(" + CompletedActivities._ID
					+ ") FROM " + CompletedActivities.TABLE + " WHERE "
					+ CompletedActivities.ACTIVITY_COMPLETED_TIME + " < '"
					+ today + "' AND " + CompletedActivities.CUSTOMER_ID
					+ " = " + customerId, null);

			cursor.moveToNext();
			return cursor.getInt(0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public String getStartTime(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT "
					+ CompletedActivities.ACTIVITY_COMPLETED_TIME + " FROM "
					+ CompletedActivities.TABLE + " WHERE "
					+ CompletedActivities.REMOTE_ID + " = " + remoteId, null);

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

	public void deleteCompletedActivitiesWithAssignRouteId(long assignRouteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(CompletedActivities.TABLE,
					new String[] { CompletedActivities.ASSIGNED_ROUTE_ID },
					CompletedActivities.ASSIGNED_ROUTE_ID + " = "
							+ assignRouteId, null, null, null, null);

			while (cursor.moveToNext()) {
				deleteCompletedActivityWithAssignRouteId(cursor.getLong(0));
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Deleted " + cursor.getCount()
						+ " completed completed activitys for assignRouteId:  "
						+ assignRouteId);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void deleteCompletedActivityWithAssignRouteId(
			long assignedRouteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		// delete locations first
		// LocationsDao locationsDao = LocationsDao
		// .getInstance(applicationContext);
		// locationsDao.deleteNoteLocations(assignedRouteId);

		int affectedRows = db
				.delete(CompletedActivities.TABLE,
						CompletedActivities.ASSIGNED_ROUTE_ID + " = "
								+ assignedRouteId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows
					+ " CompletedActivity with asssign route id "
					+ assignedRouteId);
		}
	}

	public List<CompletedActivity> getAddedUnSyncedCompletedActivivties() {
		Cursor cursor = null;
		List<CompletedActivity> completedActivities = null;

		try {
			// cursor = db.query(CompletedActivities.TABLE,
			// CompletedActivities.ALL_COLUMNS,
			// CompletedActivities.REMOTE_ID + " IS NULL AND "
			// + CompletedActivities.DIRTY + " = 'true'", null,
			// null, null, null);
			cursor = queryWith2WayJoin(CompletedActivities.REMOTE_ID
					+ " IS NULL AND (" + Locations.LOCATION_FINALIZED
					+ " = 'true' OR " + Locations.LOCATION_FINALIZED
					+ " IS NULL) AND " + CompletedActivities.TABLE + "."
					+ CompletedActivities.DIRTY + " = 'true'", null, null);

			if (cursor.getCount() > 0) {
				completedActivities = new ArrayList<CompletedActivity>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				CompletedActivity completedActivity = new CompletedActivity();
				completedActivity.load(cursor, applicationContext);
				completedActivities.add(completedActivity);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return completedActivities;
	}

	private Cursor queryWith2WayJoin(String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(CompletedActivities.TABLE + " LEFT JOIN "
				+ Locations.TABLE + " ON " + Locations.PURPOSE + " = "
				+ Locations.PURPOSE_FORM + " AND " + Locations.FOR_ID + " = "
				+ CompletedActivities.TABLE + "."
				+ CompletedActivities.CLIENT_FORM_ID);

		// qualify the columns by the table name, so that join
		// doesn't result confuse the query engine with ambiguous column
		// names
		String[] columns = new String[CompletedActivities.ALL_COLUMNS.length];
		for (int i = 0; i < CompletedActivities.ALL_COLUMNS.length; ++i) {
			columns[i] = CompletedActivities.TABLE + "."
					+ CompletedActivities.ALL_COLUMNS[i];
		}

		return builder.query(db, columns, selection, selectionArgs, null, null,
				sortOrder);

	}

}

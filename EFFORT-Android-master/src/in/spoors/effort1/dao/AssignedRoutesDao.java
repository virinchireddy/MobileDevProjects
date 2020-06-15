package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.AssignedRoute;
import in.spoors.effort1.provider.EffortProvider.AssignedRoutes;
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

public class AssignedRoutesDao {
	public static final String TAG = "AssignedRoutesDao";
	private static AssignedRoutesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static AssignedRoutesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new AssignedRoutesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private AssignedRoutesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(AssignedRoute assignedRoute) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (assignedRoute.getId() != null
				&& assignedRouteWithIdExists(assignedRoute.getId())) {
			assignedRoute.setLocalModificationTime(new Date());
			ContentValues values = assignedRoute.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the existing assignedRoute: "
						+ assignedRoute.toString());
			}

			db.update(AssignedRoutes.TABLE, values, AssignedRoutes._ID + " = "
					+ assignedRoute.getId(), null);

			return;
		}
		assignedRoute.setLocalModificationTime(new Date());
		ContentValues values = assignedRoute.getContentValues(null);
		db.insert(AssignedRoutes.TABLE, null, values);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saved a new AssignedRoute: " + assignedRoute.toString());
		}
	}

	public synchronized void saveCompletedAssignedRoute(
			AssignedRoute assignedRoute) {

		AssignedRoute existedAssignedRoute = getAssignedRouteWithId(assignedRoute
				.getId());

		if (existedAssignedRoute != null) {
			existedAssignedRoute.setCompletionTime(assignedRoute
					.getCompletionTime());
			existedAssignedRoute.setDirty(assignedRoute.getDirty());
			save(existedAssignedRoute);
		} else {
			save(assignedRoute);
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Saved a new completedAssignedRoute: "
							+ assignedRoute.toString());
		}
	}

	public List<AssignedRoute> getAllAssignedRoutes() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<AssignedRoute> assignedRoutes = null;

		try {
			cursor = db.query(AssignedRoutes.TABLE, AssignedRoutes.ALL_COLUMNS,
					AssignedRoutes.DELETED + " != " + AssignedRoute.DELETED,
					null, null, null, AssignedRoutes.START_DATE);

			if (cursor.getCount() > 0) {
				assignedRoutes = new ArrayList<AssignedRoute>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				AssignedRoute assignedRoute = new AssignedRoute();
				assignedRoute.load(cursor);
				assignedRoutes.add(assignedRoute);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return assignedRoutes;
	}

	public AssignedRoute getAssignedRouteWithId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		AssignedRoute assignedRoute = null;

		try {
			cursor = db.query(AssignedRoutes.TABLE, AssignedRoutes.ALL_COLUMNS,
					AssignedRoutes._ID + " = " + localId, null, null, null,
					null);
			if (cursor.getCount() > 0) {
				assignedRoute = new AssignedRoute();
			}
			if (cursor.moveToNext()) {

				assignedRoute = new AssignedRoute();
				assignedRoute.load(cursor);
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return assignedRoute;
	}

	public boolean assignedRouteWithIdExists(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + AssignedRoutes._ID
					+ ") AS count FROM " + AssignedRoutes.TABLE + " WHERE "
					+ AssignedRoutes._ID + " = " + id, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public int getNumberOfAssignedRoutesOlderThanToday() {
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
			cursor = db.rawQuery("SELECT COUNT(" + AssignedRoutes._ID
					+ ") FROM " + AssignedRoutes.TABLE + " WHERE "
					+ AssignedRoutes.END_DATE + " < '" + today + "'", null);

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
	 * @return assigned route start time in SQLite date time format.
	 */
	public String getStartTime(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + AssignedRoutes.START_DATE
					+ " FROM " + AssignedRoutes.TABLE + " WHERE "
					+ AssignedRoutes._ID + " = " + remoteId, null);

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

	/**
	 * Deletes all deleteCachedAssignedRoutes along with their
	 * completedActivities.
	 * 
	 */
	public synchronized int deleteCachedAssignedRoutes() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Cursor cursor = db.query(AssignedRoutes.TABLE,
				new String[] { AssignedRoutes._ID }, AssignedRoutes.CACHED
						+ " = 'true'", null, null, null, null);

		while (cursor.moveToNext()) {
			deleteAssignedRoute(cursor.getLong(0));
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + affectedRows + " cached AssignedRoutes.");
		}

		cursor.close();
		return affectedRows;
	}

	public synchronized int deleteOldAssignedRoutes() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		String today = SQLiteDateTimeUtils
				.getSQLiteDateTimeForBeginningOfToday();
		// Cursor cursor = db.query(Jobs.TABLE, new String[] { Jobs._ID },
		// Jobs.END_TIME + " < ?  AND " + Jobs.DIRTY + " = ? AND "
		// + Jobs.TREE_DIRTY + " = ? AND "
		// + Jobs.LOCAL_MODIFICATION_TIME + " < ?", new String[] {
		// today, "false", "false", today }, null, null, null);
		Cursor cursor = db.query(AssignedRoutes.TABLE,
				new String[] { AssignedRoutes._ID }, AssignedRoutes.END_DATE
						+ " < ?  AND " + AssignedRoutes.LOCAL_MODIFICATION_TIME
						+ " < ?", new String[] { today, today }, null, null,
				null);

		while (cursor.moveToNext()) {
			deleteAssignedRoute(cursor.getLong(0));
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + affectedRows + " old AssignedRoutes.");
		}

		cursor.close();
		return affectedRows;
	}

	public synchronized void deleteAssignedRoute(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		AssignedRoute route = getAssignedRouteWithId(id);

		if (route == null) {
			Log.w(TAG, "AssignedRoute with " + id + " is null.",
					new Exception());
			return;
		}

		// Cleaning completed completed activities
		CompletedActivitiesDao completedActivitiesDao = CompletedActivitiesDao
				.getInstance(applicationContext);

		completedActivitiesDao.deleteCompletedActivitiesWithAssignRouteId(id);

		// now, delete the AssignedRoute
		int affectedRows = db.delete(AssignedRoutes.TABLE, AssignedRoutes._ID
				+ " = ?", new String[] { "" + id });
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted AssignedRoute with AssignedRoute id: " + id
					+ ", affectedRows=" + affectedRows);
		}
	}

	public List<AssignedRoute> getUnsyncedAssignedRoutes() {
		Cursor cursor = null;
		List<AssignedRoute> assignedRoutes = null;

		try {
			cursor = queryWithLocations(AssignedRoutes.TABLE + "."
					+ AssignedRoutes.DIRTY + " = 'true' AND ("
					+ Locations.LOCATION_FINALIZED + " = 'true' OR "
					+ Locations.LOCATION_FINALIZED + " IS NULL)", null, null);

			if (cursor.getCount() > 0) {
				assignedRoutes = new ArrayList<AssignedRoute>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				AssignedRoute assignedRoute = new AssignedRoute();
				assignedRoute.load(cursor);
				assignedRoutes.add(assignedRoute);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return assignedRoutes;
	}

	private Cursor queryWithLocations(String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(AssignedRoutes.TABLE + " LEFT JOIN "
				+ Locations.TABLE + " ON " + AssignedRoutes.TABLE + "."
				+ AssignedRoutes._ID + " = " + Locations.TABLE + "."
				+ Locations.FOR_ID + " AND " + Locations.TABLE + "."
				+ Locations.PURPOSE + " = "
				+ Locations.PURPOSE_COMPLETE_ROUTE_PLAN);

		// qualify the columns by the table name, so that join
		// doesn't confuse the query engine with ambiguous column
		// names
		String[] columns = new String[AssignedRoutes.ALL_COLUMNS.length];
		for (int i = 0; i < AssignedRoutes.ALL_COLUMNS.length; ++i) {
			columns[i] = AssignedRoutes.TABLE + "."
					+ AssignedRoutes.ALL_COLUMNS[i];
		}

		builder.setDistinct(true);
		return builder.query(db, columns, selection, selectionArgs, null, null,
				sortOrder);
	}

}
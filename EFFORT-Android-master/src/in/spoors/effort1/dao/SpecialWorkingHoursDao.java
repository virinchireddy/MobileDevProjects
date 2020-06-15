package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.SpecialWorkingHour;
import in.spoors.effort1.provider.EffortProvider.SpecialWorkingHours;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SpecialWorkingHoursDao {

	public static final String TAG = "SpecialWorkingHoursDao";
	private static SpecialWorkingHoursDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SpecialWorkingHoursDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new SpecialWorkingHoursDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private SpecialWorkingHoursDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(SpecialWorkingHour swh) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new special working hour.");
		}

		ContentValues values = swh.getContentValues(null);
		db.insert(SpecialWorkingHours.TABLE, null, values);
	}

	public List<SpecialWorkingHour> getWorkingHours(long specialWorkingDayId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		ArrayList<SpecialWorkingHour> workingHours = null;

		try {
			cursor = db.query(SpecialWorkingHours.TABLE,
					SpecialWorkingHours.ALL_COLUMNS,
					SpecialWorkingHours.SPECIAL_WORKING_DAY_ID + " = "
							+ specialWorkingDayId, null, null, null,
					SpecialWorkingHours.START_TIME);

			if (cursor.getCount() > 0) {
				workingHours = new ArrayList<SpecialWorkingHour>();
			}

			while (cursor.moveToNext()) {
				SpecialWorkingHour swh = new SpecialWorkingHour();
				swh.load(cursor);
				workingHours.add(swh);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return workingHours;
	}

	/**
	 * Delete all special working hours
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(SpecialWorkingHours.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " special working hours.");
		}
	}

	/**
	 * Delete special working hours of the given special working day.
	 */
	public synchronized void deleteSpecialWorkingHours(long specialWorkingDayId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(SpecialWorkingHours.TABLE,
				SpecialWorkingHours.SPECIAL_WORKING_DAY_ID + " = "
						+ specialWorkingDayId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows
					+ " special working hours of day: " + specialWorkingDayId);
		}
	}

	public boolean isSwhNow() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery(
					"SELECT COUNT(" + SpecialWorkingHours._ID
							+ ") AS count FROM " + SpecialWorkingHours.TABLE
							+ " WHERE '"
							+ SQLiteDateTimeUtils.getCurrentSQLiteDateTime()
							+ "' >= " + SpecialWorkingHours.START_TIME
							+ " AND '"
							+ SQLiteDateTimeUtils.getCurrentSQLiteDateTime()
							+ "' <= " + SpecialWorkingHours.END_TIME, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

}

package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.Holiday;
import in.spoors.effort1.provider.EffortProvider.Holidays;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HolidaysDao {

	public static final String TAG = "HolidaysDao";
	private static HolidaysDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static HolidaysDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new HolidaysDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private HolidaysDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(Holiday holiday) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new holiday.");
		}

		ContentValues values = holiday.getContentValues(null);
		db.insert(Holidays.TABLE, null, values);
	}

	/**
	 * Delete all holidays
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(Holidays.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " holidays.");
		}
	}

	public synchronized void deleteHoliday(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.execSQL("DELETE FROM " + Holidays.TABLE + " WHERE " + Holidays._ID
				+ " = " + id);
	}

	public boolean isHolidayNow() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery(
					"SELECT COUNT(" + Holidays._ID + ") AS count FROM "
							+ Holidays.TABLE + " WHERE '"
							+ SQLiteDateTimeUtils.getCurrentSQLiteDateTime()
							+ "' >= " + Holidays.START_TIME + " AND '"
							+ SQLiteDateTimeUtils.getCurrentSQLiteDateTime()
							+ "' <= " + Holidays.END_TIME, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

}

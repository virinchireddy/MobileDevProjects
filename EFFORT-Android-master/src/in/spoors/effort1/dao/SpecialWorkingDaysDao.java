package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.SpecialWorkingDay;
import in.spoors.effort1.dto.SpecialWorkingHour;
import in.spoors.effort1.provider.EffortProvider.SpecialWorkingDays;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SpecialWorkingDaysDao {

	public static final String TAG = "SpecialWorkingDayDao";
	private static SpecialWorkingDaysDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SpecialWorkingDaysDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new SpecialWorkingDaysDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private SpecialWorkingDaysDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(SpecialWorkingDay swd) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new special working day.");
		}

		ContentValues values = swd.getContentValues(null);
		long insertedId = db.insert(SpecialWorkingDays.TABLE, null, values);
		swd.setId(insertedId);

		if (swd.getWorkingHours() != null) {
			SpecialWorkingHoursDao swhDao = SpecialWorkingHoursDao
					.getInstance(applicationContext);

			for (SpecialWorkingHour swh : swd.getWorkingHours()) {
				swh.setSpecialWorkingDayId(insertedId);
				swhDao.save(swh);
			}
		}
	}

	/**
	 * Delete all special working days (and their special working hours).
	 */
	public synchronized void deleteAll() {
		// delete all special working hours first
		SpecialWorkingHoursDao swhDao = SpecialWorkingHoursDao
				.getInstance(applicationContext);
		swhDao.deleteAll();

		// now, delete the special working days
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(SpecialWorkingDays.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " special working days.");
		}
	}

	public synchronized void deleteSpecialWorkingDay(long id) {
		// delete all special working hours first
		SpecialWorkingHoursDao swhDao = SpecialWorkingHoursDao
				.getInstance(applicationContext);
		swhDao.deleteSpecialWorkingHours(id);

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.execSQL("DELETE FROM " + SpecialWorkingDays.TABLE + " WHERE "
				+ SpecialWorkingDays._ID + " = " + id);
	}

}

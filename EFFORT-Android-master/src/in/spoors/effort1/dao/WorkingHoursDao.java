package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dto.WorkingHour;
import in.spoors.effort1.provider.EffortProvider.WorkingHours;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class WorkingHoursDao {

	public static final String TAG = "WorkingHoursDao";
	private static WorkingHoursDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static WorkingHoursDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new WorkingHoursDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private WorkingHoursDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(WorkingHour workingHour) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new working hour.");
		}

		ContentValues values = workingHour.getContentValues(null);
		db.insert(WorkingHours.TABLE, null, values);
	}

	/**
	 * Delete all holidays
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(WorkingHours.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " working hours.");
		}
	}

	/**
	 * returns working hours of a particular day
	 */
	public List<WorkingHour> getWorkingHours(int dayOfWeek) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		List<WorkingHour> workingHours = null;
		Cursor cursor = null;

		try {
			cursor = db.query(WorkingHours.TABLE, WorkingHours.ALL_COLUMNS,
					WorkingHours.DAY_OF_WEEK + " = " + dayOfWeek, null, null,
					null, WorkingHours.START_TIME);

			if (cursor.getCount() > 0) {
				workingHours = new ArrayList<WorkingHour>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				WorkingHour workingHour = new WorkingHour();
				workingHour.load(cursor);
				workingHours.add(workingHour);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return workingHours;
	}

	/*
	 * returns is it working hour or not
	 */

	public boolean workingHoursNow() {
		Calendar now = Calendar.getInstance();
		int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
		List<WorkingHour> workingHours = getWorkingHours(dayOfWeek);

		if (workingHours != null) {
			for (WorkingHour workingHour : workingHours) {
				workingHour = Utils
						.addYearMonthDateToTheWorkingHour(workingHour);
				if (BuildConfig.DEBUG) {
					Log.i(TAG, "starts at " + workingHour.getStartTime());
					Log.i(TAG, "ends at " + workingHour.getEndTime());
				}
				if (now.getTimeInMillis() >= workingHour.getStartTime()
						.getTime()
						&& now.getTimeInMillis() <= workingHour.getEndTime()
								.getTime()) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Returns only starttime and endtime of the working day, even if there are
	 * breaking working hours
	 */
	public WorkingHour getWorkingHour(int dayOfWeek) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		WorkingHour workingHour = null;

		try {
			cursor = db.rawQuery("SELECT _ID, DAY_OF_WEEK,TITLE,MIN("
					+ WorkingHours.START_TIME + ") AS "
					+ WorkingHours.START_TIME + ",MAX(" + WorkingHours.END_TIME
					+ ") AS " + WorkingHours.END_TIME + " FROM "
					+ WorkingHours.TABLE + " WHERE " + WorkingHours.DAY_OF_WEEK
					+ "= " + dayOfWeek, null);

			if (cursor != null && cursor.moveToFirst()) {
				workingHour = new WorkingHour();
				workingHour.load(cursor);
				// if we call this method ,then we are with hours add year,
				// hours, days etc to the hours
				Utils.addYearMonthDateToTheWorkingHour(workingHour);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return workingHour;
	}
}

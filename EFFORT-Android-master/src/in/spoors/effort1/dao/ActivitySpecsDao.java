package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.ActivitySpec;
import in.spoors.effort1.provider.EffortProvider.ActivitySpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ActivitySpecsDao {
	public static final String TAG = "ActivitySpecsDao";
	private static ActivitySpecsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static ActivitySpecsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new ActivitySpecsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private ActivitySpecsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(ActivitySpec activity) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (activity.getId() != null && activityWithIdExists(activity.getId())) {
			ContentValues values = activity.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG,
						"Updating the existing activity: "
								+ activity.toString());
			}

			db.update(ActivitySpecs.TABLE, values, ActivitySpecs._ID + " = "
					+ activity.getId(), null);

			return;
		}

		ContentValues values = activity.getContentValues(null);
		db.insert(ActivitySpecs.TABLE, null, values);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saved a new activity: " + activity.toString());
		}
	}

	public boolean activityWithIdExists(long id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + ActivitySpecs._ID
					+ ") AS count FROM " + ActivitySpecs.TABLE + " WHERE "
					+ ActivitySpecs._ID + " = " + id, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<ActivitySpec> getActivitiesWithActivityId(long activityId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<ActivitySpec> activities = null;

		try {
			cursor = db.query(ActivitySpecs.TABLE, ActivitySpecs.ALL_COLUMNS,
					ActivitySpecs._ID + " = " + activityId, null, null, null,
					null);

			if (cursor.getCount() > 0) {
				activities = new ArrayList<ActivitySpec>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				ActivitySpec activity = new ActivitySpec();
				activity.load(cursor);
				activities.add(activity);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return activities;
	}

	public ActivitySpec getActivityWithActivityId(Long activityId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		if (activityId == null || activityId == 0) {
			return null;
		}

		try {
			cursor = db.query(ActivitySpecs.TABLE, ActivitySpecs.ALL_COLUMNS,
					ActivitySpecs._ID + " = " + activityId, null, null, null,
					null);

			if (cursor.moveToNext()) {

				ActivitySpec activity = new ActivitySpec();
				activity.load(cursor);
				return activity;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public List<ActivitySpec> getAllActivities() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<ActivitySpec> activities = null;

		try {

			cursor = db.query(ActivitySpecs.TABLE, ActivitySpecs.ALL_COLUMNS,
					ActivitySpecs.DELETED + " != 'true'", null, null, null,
					null);

			if (cursor.getCount() > 0) {
				activities = new ArrayList<ActivitySpec>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				ActivitySpec activity = new ActivitySpec();
				activity.load(cursor);
				activities.add(activity);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return activities;
	}
}

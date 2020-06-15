package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.JobType;
import in.spoors.effort1.provider.EffortProvider.JobTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 
 * @author tiru
 */
public class JobTypesDao {
	public static final String TAG = "JobTypesDao";

	private static JobTypesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static JobTypesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new JobTypesDao(applicationContext);
		}

		return instance;
	}

	private JobTypesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public String getName(int id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(JobTypes.TABLE, new String[] { JobTypes.NAME },
					JobTypes._ID + " = " + id, null, null, null, null);

			if (cursor.moveToNext()) {
				return cursor.getString(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public Integer getId(String name) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(JobTypes.TABLE, new String[] { JobTypes._ID },
					JobTypes.NAME + " = ?", new String[] { name }, null, null,
					null);

			if (cursor.moveToNext()) {
				return cursor.getInt(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public Integer getDefaultTypeId() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			// if none is marked as default, picks the first non-default
			// if multiple types are marked as default, picks the first one in
			// alphabetical order
			cursor = db.query(JobTypes.TABLE, new String[] { JobTypes._ID },
					null, null, null, null, JobTypes.DEFAULT_TYPE + " DESC, "
							+ JobTypes.NAME);

			if (cursor.moveToNext()) {
				return cursor.getInt(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * 
	 * @param name
	 * @return null if name is not found
	 */
	public Boolean getChecked(String name) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(JobTypes.TABLE,
					new String[] { JobTypes.CHECKED }, JobTypes.NAME + " = ?",
					new String[] { name }, null, null, null);

			if (cursor.moveToNext()) {
				return Boolean.parseBoolean(cursor.getString(0));
			} else {
				return null;
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public synchronized void saveChecked(String name, boolean checked) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(JobTypes.CHECKED, String.valueOf(checked));

		int rowsAffected = db.update(JobTypes.TABLE, values, JobTypes.NAME
				+ " = ?", new String[] { name });
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Updated the checked state of " + rowsAffected
					+ " job type.");
		}
	}

	/**
	 * 
	 * @return null if there are no job types
	 */
	public List<String> getNames() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<String> names = null;

		try {
			cursor = db.query(JobTypes.TABLE, new String[] { JobTypes.NAME },
					null, null, null, null, JobTypes.NAME);

			if (cursor.getCount() > 0) {
				names = new ArrayList<String>();
			}

			while (cursor.moveToNext()) {
				names.add(cursor.getString(0));
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return names;
	}

	/**
	 * 
	 * @return null if there are no job types
	 */
	public List<JobType> getJobTypes() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<JobType> types = null;

		try {
			cursor = db.query(JobTypes.TABLE, JobTypes.ALL_COLUMNS, null, null,
					null, null, "UPPER(" + JobTypes.NAME + ")" + " ASC");

			if (cursor.getCount() > 0) {
				types = new ArrayList<JobType>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				JobType type = new JobType();
				type.load(cursor);
				types.add(type);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return types;
	}

	public boolean areTypesDifferent(List<JobType> list1, List<JobType> list2) {
		if (list1 == null && list2 == null) {
			return false;
		} else if (list1 == null || list2 == null) {
			return true;
		}

		int size = list1.size();

		if (size != list2.size()) {
			return true;
		}

		List<JobType> sortedList1 = new ArrayList<JobType>(list1);
		List<JobType> sortedList2 = new ArrayList<JobType>(list2);
		Collections.sort(sortedList1);
		Collections.sort(sortedList2);

		// we'll reach here when both the lists contains the same number of
		// items
		for (int i = 0; i < size; ++i) {
			JobType type1 = sortedList1.get(i);
			JobType type2 = sortedList2.get(i);

			if (type1.getId() != type2.getId()
					|| !type1.getName().equals(type2.getName())
					|| type1.isDefaultType() != type2.isDefaultType()) {
				return true;
			}
		}

		return false;
	}

	public synchronized void save(JobType jobType) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = jobType.getContentValues(null);
		long id = db.insert(JobTypes.TABLE, null, values);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Saved: " + jobType + ". Inserted job type ID: " + id);
		}
	}

	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.delete(JobTypes.TABLE, null, null);
	}

	/**
	 * 
	 * @return null if there are no job types
	 */
	public String getCheckedTypesIn() {
		StringBuffer sb = new StringBuffer("(");
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(JobTypes.TABLE, new String[] { JobTypes._ID },
					JobTypes.CHECKED + " = 'true'", null, null, null, null);

			int count = cursor.getCount();

			for (int i = 0; i < count; ++i) {
				cursor.moveToNext();
				sb.append(cursor.getInt(0));

				if (i < count - 1) {
					sb.append(", ");
				}
			}

			sb.append(")");
		} finally {
			if (cursor != null)
				cursor.close();
		}

		String in = sb.toString();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In clause: " + in);
		}

		return in;
	}
}

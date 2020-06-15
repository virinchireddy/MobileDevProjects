package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.provider.EffortProvider.LeaveStatus;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.SparseArray;

/**
 * 
 * For better performance, leave statuses are cached in a SparseArray.
 * 
 * @author tiru
 * 
 */
public class LeaveStatusDao {
	public static final String TAG = "LeaveStatusDao";

	private static LeaveStatusDao instance;
	private Context applicationContext;

	/** Stores id to name mapping */
	private SparseArray<String> names;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static LeaveStatusDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new LeaveStatusDao(applicationContext);
		}

		return instance;
	}

	private LeaveStatusDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Load data from the database, and store it in hash maps.
	 */
	private synchronized void load() {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Loading settings from the database.");
		}

		names = new SparseArray<String>();

		Cursor cursor = null;

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		try {
			cursor = db.query(LeaveStatus.TABLE, new String[] {
					LeaveStatus._ID, LeaveStatus.NAME }, null, null, null,
					null, null);

			while (cursor.moveToNext()) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Caching " + cursor.getInt(0) + "="
									+ cursor.getString(1));
				}

				names.append(cursor.getInt(0), cursor.getString(1));
			}

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Loaded " + names.size() + " leave status from db.");
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

	}

	/**
	 * Returns the name corresponding to the given ID.
	 * 
	 * Returns null if no mapping is found.
	 * 
	 * @param id
	 * @return
	 */
	public String getName(int id) {
		if (names == null) {
			load();
		}

		return names.get(id);
	}

	public List<String> getNames() {
		if (names == null) {
			load();
		}

		List<String> list = new ArrayList<String>();

		for (int i = 0; i < names.size(); ++i) {
			list.add(names.valueAt(i));
		}

		return list;
	}

	/**
	 * Returns the ID corresponding to the given name.
	 * 
	 * @param name
	 * @return
	 */
	public int getId(String name) {
		int index = getIndex(name);

		if (index < 0) {
			return -1;
		} else {
			return names.keyAt(index);
		}
	}

	/**
	 * Returns the index corresponding to the given name.
	 * 
	 * @param name
	 * @return
	 */
	public int getIndex(String name) {
		if (names == null) {
			load();
		}

		// indexOfValues uses reference comparison,
		// thus, we cannot use it here
		int index = -1;

		for (int i = 0; i < names.size(); i++) {
			if (name.equals(names.valueAt(i))) {
				index = i;
				break;
			}
		}

		return index;
	}

	/**
	 * Returns the index corresponding to the given id.
	 * 
	 * @param name
	 * @return
	 */
	public int getIndex(int id) {
		if (names == null) {
			load();
		}

		return names.indexOfKey(id);
	}

	/**
	 * Saves ID, and name mappings to leave status table
	 * 
	 * Deletes all entries in the table, before inserting the new rows.
	 * 
	 * Loads all records into an internal sparse array after saving.
	 * 
	 * @param names
	 */
	public synchronized void save(SparseArray<String> names) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		db.delete(LeaveStatus.TABLE, null, null);

		// Insert it in to the DB
		ContentValues values = new ContentValues();
		for (int i = 0; i < names.size(); ++i) {
			values.put(LeaveStatus._ID, names.keyAt(i));
			values.put(LeaveStatus.NAME, names.valueAt(i));
			db.insert(LeaveStatus.TABLE, null, values);
		}

		load();
	}

}

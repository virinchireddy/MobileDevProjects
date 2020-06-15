package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.provider.EffortProvider;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

/**
 * Settings are stored in DB to make it easy during application upgrades.
 * 
 * For better performance, settings are cached in a HashMap.
 * 
 * @author tiru
 * 
 */
public class SettingsDao {
	public static final String TAG = SettingsDao.class.getSimpleName();
	private static SettingsDao instance;
	private Context applicationContext;
	private ConcurrentHashMap<String, String> settings;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SettingsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new SettingsDao(applicationContext);
		}

		return instance;
	}

	private SettingsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Read settings from the database, and store them in a hash map.
	 */
	private synchronized void loadSettings() {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Loading settings from the database.");
		}

		Cursor cursor = null;
		settings = new ConcurrentHashMap<String, String>();

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		try {
			cursor = db.query(EffortProvider.Settings.TABLE,
					new String[] { EffortProvider.Settings.KEY,
							EffortProvider.Settings.VALUE }, null, null, null,
					null, null);

			while (cursor.moveToNext()) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Caching " + cursor.getString(0) + "="
									+ cursor.getString(1));
				}

				if (cursor.getString(1) != null) {
					settings.put(cursor.getString(0), cursor.getString(1));
				}
			}

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Loaded " + settings.size() + " settings from db.");
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public String getString(String key) {
		if (settings == null) {
			loadSettings();
		}

		return settings.get(key);
	}

	public String getString(String key, String dafaultValue) {
		if (settings == null) {
			loadSettings();
		}
		String str = settings.get(key);
		if (TextUtils.isEmpty(str)) {
			return dafaultValue;
		} else {
			return settings.get(key);
		}
	}

	public Date getDate(String key, Date defaultDate) {
		if (settings == null) {
			loadSettings();
		}

		String str = settings.get(key);
		if (TextUtils.isEmpty(str)) {
			return defaultDate;
		} else {
			return SQLiteDateTimeUtils.getLocalTime(str);
		}
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return returns default value if setting is not found.
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		if (settings == null) {
			loadSettings();
		}

		String value = settings.get(key);

		if (value == null) {
			return defaultValue;
		} else {
			return Boolean.parseBoolean(value);
		}
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return returns default value if setting is not found.
	 */
	public int getInt(String key, int defaultValue) {
		if (settings == null) {
			loadSettings();
		}

		String value = settings.get(key);

		if (value == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(value);
		}
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return returns default value if setting is not found.
	 */
	public long getLong(String key, long defaultValue) {
		if (settings == null) {
			loadSettings();
		}

		String value = settings.get(key);

		if (value == null) {
			return defaultValue;
		} else {
			return Long.parseLong(value);
		}
	}

	/**
	 * 
	 * @param key
	 * @param defaultValue
	 * @return returns default value if setting is not found.
	 */
	public Long getLong(String key) {
		if (settings == null) {
			loadSettings();
		}

		String value = settings.get(key);

		if (TextUtils.isEmpty(value)) {
			return null;
		} else {
			return Long.parseLong(value);
		}
	}

	public synchronized void saveSetting(String key, String value) {

		if (settings == null) {
			loadSettings();
		}

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (settings.containsKey(key)) {
			ContentValues values = new ContentValues();
			values.put(EffortProvider.Settings.VALUE, value);

			// Update the DB
			db.update(EffortProvider.Settings.TABLE, values,
					EffortProvider.Settings.KEY + " = ?", new String[] { key });
		} else {
			// Insert it in to the DB
			ContentValues values = new ContentValues();
			values.put(EffortProvider.Settings.KEY, key);
			values.put(EffortProvider.Settings.VALUE, value);
			db.insert(EffortProvider.Settings.TABLE, null, values);
		}

		// Replace the value stored in the cache
		if (value != null) {
			settings.put(key, value);
		}
	}

	public String getLabel(String labelKey, String labelDafaultvalue) {
		String value = getString(labelKey);
		if (TextUtils.isEmpty(value)) {
			return labelDafaultvalue;
		} else {
			return value;
		}
	}

	public ConcurrentHashMap<String, String> getSettings() {
		return settings;
	}

	public void setSettings(ConcurrentHashMap<String, String> settings) {
		this.settings = settings;
	}
}

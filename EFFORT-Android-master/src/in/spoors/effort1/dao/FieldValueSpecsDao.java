package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.FieldValueSpec;
import in.spoors.effort1.provider.EffortProvider.FieldValueSpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FieldValueSpecsDao {

	public static final String TAG = "FieldValueSpecsDao";
	private static FieldValueSpecsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static FieldValueSpecsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new FieldValueSpecsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private FieldValueSpecsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean fieldValueSpecWithIdExists(long fieldValueSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + FieldValueSpecs._ID
					+ ") AS count FROM " + FieldValueSpecs.TABLE + " WHERE "
					+ FieldValueSpecs._ID + " = " + fieldValueSpecId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(FieldValueSpec fieldValueSpec) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		ContentValues values = fieldValueSpec.getContentValues(null);

		if (fieldValueSpecWithIdExists(fieldValueSpec.getId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating an existing field value spec.");
			}

			db.update(FieldValueSpecs.TABLE, values, FieldValueSpecs._ID
					+ " = " + fieldValueSpec.getId(), null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new field value spec.");
			}

			db.insert(FieldValueSpecs.TABLE, null, values);
		}
	}

	/**
	 * Delete all
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(FieldValueSpecs.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " field value specs.");
		}
	}

	public String getValue(long fieldValueSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		String name = null;
		Cursor cursor = null;

		try {
			cursor = db.query(FieldValueSpecs.TABLE,
					new String[] { FieldValueSpecs.VALUE }, FieldValueSpecs._ID
							+ " = " + fieldValueSpecId, null, null, null, null);

			if (cursor.moveToNext()) {
				name = cursor.getString(0);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return name;
	}

	public Long getId(long fieldSpecId, String fieldValue) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Long id = null;
		Cursor cursor = null;

		try {
			cursor = db.query(FieldValueSpecs.TABLE,
					new String[] { FieldValueSpecs._ID },
					FieldValueSpecs.FIELD_SPEC_ID + " = ? AND "
							+ FieldValueSpecs.VALUE + " = ?", new String[] {
							"" + fieldSpecId, fieldValue }, null, null, null);

			if (cursor.moveToNext()) {
				id = cursor.getLong(0);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return id;
	}

	public List<String> getValues(long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<String> values = null;

		try {
			cursor = db.query(FieldValueSpecs.TABLE,
					new String[] { FieldValueSpecs.VALUE },
					FieldValueSpecs.FIELD_SPEC_ID + " = " + fieldSpecId, null,
					null, null, null);

			if (cursor.getCount() > 0) {
				values = new ArrayList<String>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				values.add(cursor.getString(0));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return values;
	}

}

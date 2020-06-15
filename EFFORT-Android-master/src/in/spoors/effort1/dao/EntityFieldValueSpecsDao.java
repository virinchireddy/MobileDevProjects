package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.EntityFieldValueSpec;
import in.spoors.effort1.provider.EffortProvider.EntityFieldValueSpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class EntityFieldValueSpecsDao {

	public static final String TAG = "EntityFieldValueSpecsDao";
	private static EntityFieldValueSpecsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static EntityFieldValueSpecsDao getInstance(
			Context applicationContext) {
		if (instance == null) {
			instance = new EntityFieldValueSpecsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private EntityFieldValueSpecsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean fieldValueSpecWithIdExists(long fieldValueSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + EntityFieldValueSpecs._ID
					+ ") AS count FROM " + EntityFieldValueSpecs.TABLE
					+ " WHERE " + EntityFieldValueSpecs._ID + " = "
					+ fieldValueSpecId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(EntityFieldValueSpec fieldValueSpec) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		ContentValues values = fieldValueSpec.getContentValues(null);

		if (fieldValueSpecWithIdExists(fieldValueSpec.getId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating an existing entity field value spec.");
			}

			db.update(EntityFieldValueSpecs.TABLE, values,
					EntityFieldValueSpecs._ID + " = " + fieldValueSpec.getId(),
					null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new entity field value spec.");
			}

			db.insert(EntityFieldValueSpecs.TABLE, null, values);
		}
	}

	/**
	 * Delete all
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(EntityFieldValueSpecs.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " entity field value specs.");
		}
	}

	public String getValue(long fieldValueSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		String name = null;
		Cursor cursor = null;

		try {
			cursor = db.query(EntityFieldValueSpecs.TABLE,
					new String[] { EntityFieldValueSpecs.VALUE },
					EntityFieldValueSpecs._ID + " = " + fieldValueSpecId, null,
					null, null, null);

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
			cursor = db.query(EntityFieldValueSpecs.TABLE,
					new String[] { EntityFieldValueSpecs._ID },
					EntityFieldValueSpecs.FIELD_SPEC_ID + " = ? AND "
							+ EntityFieldValueSpecs.VALUE + " = ?",
					new String[] { "" + fieldSpecId,
							fieldValue == null ? "" : fieldValue }, null, null,
					null);

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
			cursor = db.query(EntityFieldValueSpecs.TABLE,
					new String[] { EntityFieldValueSpecs.VALUE },
					EntityFieldValueSpecs.FIELD_SPEC_ID + " = " + fieldSpecId,
					null, null, null, null);

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

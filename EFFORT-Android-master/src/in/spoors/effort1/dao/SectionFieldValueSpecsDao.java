package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.SectionFieldValueSpec;
import in.spoors.effort1.provider.EffortProvider.SectionFieldValueSpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SectionFieldValueSpecsDao {

	public static final String TAG = "SectionFieldValueSpecsDao";
	private static SectionFieldValueSpecsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SectionFieldValueSpecsDao getInstance(
			Context applicationContext) {
		if (instance == null) {
			instance = new SectionFieldValueSpecsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private SectionFieldValueSpecsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean fieldValueSpecWithIdExists(long fieldValueSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + SectionFieldValueSpecs._ID
					+ ") AS count FROM " + SectionFieldValueSpecs.TABLE
					+ " WHERE " + SectionFieldValueSpecs._ID + " = "
					+ fieldValueSpecId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(SectionFieldValueSpec fieldValueSpec) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		ContentValues values = fieldValueSpec.getContentValues(null);

		if (fieldValueSpecWithIdExists(fieldValueSpec.getId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating an existing section field value spec.");
			}

			db.update(
					SectionFieldValueSpecs.TABLE,
					values,
					SectionFieldValueSpecs._ID + " = " + fieldValueSpec.getId(),
					null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new section field value spec.");
			}

			db.insert(SectionFieldValueSpecs.TABLE, null, values);
		}
	}

	/**
	 * Delete all
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(SectionFieldValueSpecs.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows
					+ " section field value specs.");
		}
	}

	public String getValue(long fieldValueSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		String name = null;
		Cursor cursor = null;

		try {
			cursor = db.query(SectionFieldValueSpecs.TABLE,
					new String[] { SectionFieldValueSpecs.VALUE },
					SectionFieldValueSpecs._ID + " = " + fieldValueSpecId,
					null, null, null, null);

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
			cursor = db.query(SectionFieldValueSpecs.TABLE,
					new String[] { SectionFieldValueSpecs._ID },
					SectionFieldValueSpecs.FIELD_SPEC_ID + " = ? AND "
							+ SectionFieldValueSpecs.VALUE + " = ?",
					new String[] { "" + fieldSpecId, fieldValue }, null, null,
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
			cursor = db.query(SectionFieldValueSpecs.TABLE,
					new String[] { SectionFieldValueSpecs.VALUE },
					SectionFieldValueSpecs.FIELD_SPEC_ID + " = " + fieldSpecId,
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

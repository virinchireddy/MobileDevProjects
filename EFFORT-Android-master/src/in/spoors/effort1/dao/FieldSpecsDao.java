package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.FieldSpec;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FieldSpecsDao {

	public static final String TAG = "FieldSpecsDao";
	private static FieldSpecsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static FieldSpecsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new FieldSpecsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private FieldSpecsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean fieldSpecWithIdExists(long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + FieldSpecs._ID
					+ ") AS count FROM " + FieldSpecs.TABLE + " WHERE "
					+ FieldSpecs._ID + " = " + fieldSpecId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(FieldSpec fieldSpec) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		ContentValues values = fieldSpec.getContentValues(null);

		if (fieldSpecWithIdExists(fieldSpec.getId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating an existing field spec.");
			}
			db.update(FieldSpecs.TABLE, values, FieldSpecs._ID + " = "
					+ fieldSpec.getId(), null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new field spec.");
			}
			db.insert(FieldSpecs.TABLE, null, values);
		}
	}

	/**
	 * Delete all
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(FieldSpecs.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " field specs.");
		}
	}

	public int getType(long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(FieldSpecs.TABLE,
					new String[] { FieldSpecs.TYPE }, FieldSpecs._ID + " = "
							+ fieldSpecId, null, null, null, null);

			if (cursor.moveToNext()) {
				return cursor.getInt(0);
			} else {
				if (BuildConfig.DEBUG) {
					Log.w(TAG, "Missing field spec type for field spec id "
							+ fieldSpecId);
				}

				return -1;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public FieldSpec getFieldSpec(long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(FieldSpecs.TABLE, FieldSpecs.ALL_COLUMNS,
					FieldSpecs._ID + " = " + fieldSpecId, null, null, null,
					null);

			if (cursor.moveToNext()) {
				FieldSpec fieldSpec = new FieldSpec();
				fieldSpec.load(cursor);
				return fieldSpec;
			} else {
				if (BuildConfig.DEBUG) {
					Log.w(TAG, "Missing field spec for field spec id "
							+ fieldSpecId);
				}

				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<FieldSpec> getFieldSpecs(long formSpecId,
			boolean requiredFieldsOnly) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<FieldSpec> fieldSpecs = new ArrayList<FieldSpec>();

		try {
			cursor = db.query(FieldSpecs.TABLE, FieldSpecs.ALL_COLUMNS,
					FieldSpecs.FORM_SPEC_ID
							+ " = "
							+ formSpecId
							+ (requiredFieldsOnly ? " AND "
									+ FieldSpecs.REQUIRED + " = 'true'" : ""),
					null, null, null, null);

			while (cursor.moveToNext()) {
				FieldSpec fieldSpec = new FieldSpec();
				fieldSpec.load(cursor);
				fieldSpecs.add(fieldSpec);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return fieldSpecs;
	}

	public String getUniqueId(long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(FieldSpecs.TABLE,
					new String[] { FieldSpecs.UNIQUE_ID }, FieldSpecs._ID
							+ " = " + fieldSpecId, null, null, null, null);

			if (cursor.moveToNext()) {
				return cursor.getString(0);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

}

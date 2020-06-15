package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.SectionFieldSpec;
import in.spoors.effort1.provider.EffortProvider.SectionFieldSpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SectionFieldSpecsDao {

	public static final String TAG = "SectionFieldSpecsDao";
	private static SectionFieldSpecsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SectionFieldSpecsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new SectionFieldSpecsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private SectionFieldSpecsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean fieldSpecWithIdExists(long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + SectionFieldSpecs._ID
					+ ") AS count FROM " + SectionFieldSpecs.TABLE + " WHERE "
					+ SectionFieldSpecs._ID + " = " + fieldSpecId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(SectionFieldSpec fieldSpec) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		ContentValues values = fieldSpec.getContentValues(null);

		if (fieldSpecWithIdExists(fieldSpec.getId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating an existing field spec.");
			}
			db.update(SectionFieldSpecs.TABLE, values, SectionFieldSpecs._ID
					+ " = " + fieldSpec.getId(), null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new field spec.");
			}
			db.insert(SectionFieldSpecs.TABLE, null, values);
		}
	}

	/**
	 * Delete all
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(SectionFieldSpecs.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " section field specs.");
		}
	}

	public int getType(long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(SectionFieldSpecs.TABLE,
					new String[] { SectionFieldSpecs.TYPE },
					SectionFieldSpecs._ID + " = " + fieldSpecId, null, null,
					null, null);

			if (cursor.moveToNext()) {
				return cursor.getInt(0);
			} else {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Missing section field spec type for field spec id "
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

	public SectionFieldSpec getFieldSpec(long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(SectionFieldSpecs.TABLE,
					SectionFieldSpecs.ALL_COLUMNS, SectionFieldSpecs._ID
							+ " = " + fieldSpecId, null, null, null, null);

			if (cursor.moveToNext()) {
				SectionFieldSpec fieldSpec = new SectionFieldSpec();
				fieldSpec.load(cursor);
				return fieldSpec;
			} else {
				if (BuildConfig.DEBUG) {
					Log.w(TAG, "Missing section field spec for field spec id "
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

	public List<SectionFieldSpec> getFieldSpecs(long formSpecId,
			long sectionSpecId, boolean requiredFieldsOnly) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<SectionFieldSpec> fieldSpecs = null;

		try {
			cursor = db.query(SectionFieldSpecs.TABLE,
					SectionFieldSpecs.ALL_COLUMNS,
					SectionFieldSpecs.FORM_SPEC_ID
							+ " = "
							+ formSpecId
							+ " AND "
							+ SectionFieldSpecs.SECTION_SPEC_ID
							+ " = "
							+ sectionSpecId
							+ (requiredFieldsOnly ? " AND "
									+ SectionFieldSpecs.REQUIRED + " = 'true'"
									: ""), null, null, null,
					SectionFieldSpecs.DISPLAY_ORDER);

			if (cursor.getCount() > 0) {
				fieldSpecs = new ArrayList<SectionFieldSpec>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				SectionFieldSpec fieldSpec = new SectionFieldSpec();
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
			cursor = db.query(SectionFieldSpecs.TABLE,
					new String[] { SectionFieldSpecs.UNIQUE_ID },
					SectionFieldSpecs._ID + " = " + fieldSpecId, null, null,
					null, null);

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

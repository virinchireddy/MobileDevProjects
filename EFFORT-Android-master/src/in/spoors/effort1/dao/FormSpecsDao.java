package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.FormSpec;
import in.spoors.effort1.provider.EffortProvider.FormSpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class FormSpecsDao {

	public static final String TAG = "FormSpecsDao";
	private static FormSpecsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static FormSpecsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new FormSpecsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private FormSpecsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean formSpecWithIdExists(long formSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + FormSpecs._ID
					+ ") AS count FROM " + FormSpecs.TABLE + " WHERE "
					+ FormSpecs._ID + " = " + formSpecId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(FormSpec formSpec) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = formSpec.getContentValues(null);

		if (formSpecWithIdExists(formSpec.getId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating an existing form spec.");
			}

			db.update(FormSpecs.TABLE, values,
					FormSpecs._ID + " = " + formSpec.getId(), null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new form spec.");
			}

			db.insert(FormSpecs.TABLE, null, values);
		}
	}

	public synchronized void updateActive(List<Long> activeFormSpecIds) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		String in = "(" + TextUtils.join(",", activeFormSpecIds) + ")";
		db.execSQL("UPDATE " + FormSpecs.TABLE + " SET " + FormSpecs.VISIBLE
				+ " = 'true' WHERE " + FormSpecs._ID + " IN " + in);
	}

	public synchronized void updateInactive(List<Long> inactiveFormSpecIds) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		String in = "(" + TextUtils.join(",", inactiveFormSpecIds) + ")";
		db.execSQL("UPDATE " + FormSpecs.TABLE + " SET " + FormSpecs.VISIBLE
				+ " = 'false' WHERE " + FormSpecs._ID + " IN " + in);
	}

	/**
	 * Delete all
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(FormSpecs.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " form specs.");
		}
	}

	public String getFormTitle(long formSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		String title = null;

		try {
			cursor = db.query(FormSpecs.TABLE,
					new String[] { FormSpecs.TITLE }, FormSpecs._ID + " = "
							+ formSpecId, null, null, null, null);

			if (cursor.moveToNext()) {
				title = cursor.getString(0);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return title;
	}

	public String getPrintTemplate(long formSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		String title = null;

		try {
			cursor = db.query(FormSpecs.TABLE,
					new String[] { FormSpecs.PRINT_TEMPLATE }, FormSpecs._ID
							+ " = " + formSpecId, null, null, null, null);

			if (cursor.moveToNext()) {
				title = cursor.getString(0);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return title;
	}

	public FormSpec getFormSpec(long formSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		FormSpec formSpec = null;

		try {
			cursor = db.query(FormSpecs.TABLE, FormSpecs.ALL_COLUMNS,
					FormSpecs._ID + " = " + formSpecId, null, null, null, null);

			if (cursor.moveToNext()) {
				formSpec = new FormSpec();
				formSpec.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return formSpec;
	}

	/**
	 * Get all the form spec IDs.
	 * 
	 * @return an empty array if there are no form specs stored locally. (This
	 *         is a server side requirement)
	 * 
	 */
	public List<Long> getAllFormSpecIds() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Long> ids = new ArrayList<Long>();

		try {
			cursor = db.query(FormSpecs.TABLE, new String[] { FormSpecs._ID },
					null, null, null, null, FormSpecs._ID);

			while (cursor.moveToNext()) {
				ids.add(cursor.getLong(0));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return ids;
	}

	public String getUniqueId(long formSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		String title = null;

		try {
			cursor = db.query(FormSpecs.TABLE,
					new String[] { FormSpecs.UNIQUE_ID }, FormSpecs._ID + " = "
							+ formSpecId, null, null, null, null);

			if (cursor.moveToNext()) {
				title = cursor.getString(0);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return title;
	}

	public Long getFormSpecIdWithUniqueId(String uniqueId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		Long title = null;

		try {
			cursor = db.query(FormSpecs.TABLE, new String[] { FormSpecs._ID },
					FormSpecs.UNIQUE_ID + " = '" + uniqueId + "'", null, null,
					null, null);

			if (cursor.moveToNext()) {
				title = cursor.getLong(0);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return title;
	}

	public Long getLatestFormSpecId(long formSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT s1." + FormSpecs._ID
					+ " AS oldId, s2." + FormSpecs._ID + " AS newId FROM "
					+ FormSpecs.TABLE + " s1 LEFT JOIN " + FormSpecs.TABLE
					+ " s2 ON s1." + FormSpecs.UNIQUE_ID + " = s2."
					+ FormSpecs.UNIQUE_ID + " AND s1." + FormSpecs._ID
					+ " < s2." + FormSpecs._ID + " WHERE s1." + FormSpecs._ID
					+ " = " + formSpecId + " ORDER BY newId DESC", null);

			if (cursor.moveToNext()) {
				// if newId is null send oldId
				if (cursor.isNull(1)) {
					return cursor.getLong(0);
				} else {
					return cursor.getLong(1);
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public boolean hasPrintTemplates() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + FormSpecs._ID
					+ ") AS count FROM " + FormSpecs.TABLE + " WHERE "
					+ FormSpecs.PRINT_TEMPLATE + " IS NOT NULL AND "
					+ FormSpecs.WITHDRAWN + " = 'false'", null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

}

package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.SectionSpec;
import in.spoors.effort1.provider.EffortProvider.SectionSpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SectionSpecsDao {

	public static final String TAG = "SectionSpecsDao";
	private static SectionSpecsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SectionSpecsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new SectionSpecsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private SectionSpecsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean sectionSpecWithIdExists(long sectionSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + SectionSpecs._ID
					+ ") AS count FROM " + SectionSpecs.TABLE + " WHERE "
					+ SectionSpecs._ID + " = " + sectionSpecId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(SectionSpec sectionSpec) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = sectionSpec.getContentValues(null);

		if (sectionSpecWithIdExists(sectionSpec.getId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating an existing section spec.");
			}

			db.update(SectionSpecs.TABLE, values, SectionSpecs._ID + " = "
					+ sectionSpec.getId(), null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new section spec.");
			}

			db.insert(SectionSpecs.TABLE, null, values);
		}
	}

	/**
	 * Delete all
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(SectionSpecs.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " section specs.");
		}
	}

	public String getTitle(long sectionSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		String title = null;

		try {
			cursor = db.query(SectionSpecs.TABLE,
					new String[] { SectionSpecs.TITLE }, SectionSpecs._ID
							+ " = " + sectionSpecId, null, null, null, null);

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

	public SectionSpec getSectionSpec(long sectionSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		SectionSpec sectionSpec = null;

		try {
			cursor = db.query(SectionSpecs.TABLE, SectionSpecs.ALL_COLUMNS,
					SectionSpecs._ID + " = " + sectionSpecId, null, null, null,
					null);

			if (cursor.moveToNext()) {
				sectionSpec = new SectionSpec();
				sectionSpec.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return sectionSpec;
	}

	public List<SectionSpec> getSectionSpecs(long formSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<SectionSpec> sectionSpecs = new ArrayList<SectionSpec>();

		try {
			cursor = db.query(SectionSpecs.TABLE, SectionSpecs.ALL_COLUMNS,
					SectionSpecs.FORM_SPEC_ID + " = " + formSpecId, null, null,
					null, SectionSpecs.DISPLAY_ORDER);

			while (cursor.moveToNext()) {
				SectionSpec sectionSpec = new SectionSpec();
				sectionSpec.load(cursor);
				sectionSpecs.add(sectionSpec);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return sectionSpecs;
	}

}

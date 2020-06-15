package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.PageSpec;
import in.spoors.effort1.provider.EffortProvider.PageSpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PageSpecsDao {

	public static final String TAG = "PageSpecsDao";
	private static PageSpecsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static PageSpecsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new PageSpecsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private PageSpecsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean pageSpecExists(long formSpecId, long pageId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + PageSpecs._ID
					+ ") AS count FROM " + PageSpecs.TABLE + " WHERE "
					+ PageSpecs.FORM_SPEC_ID + " = " + formSpecId + " AND "
					+ PageSpecs.PAGE_ID + " = " + pageId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(PageSpec pageSpec) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = pageSpec.getContentValues(null);

		if (pageSpecExists(pageSpec.getFormSpecId(), pageSpec.getPageId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating an existing page spec.");
			}

			db.update(PageSpecs.TABLE, values,
					PageSpecs._ID + " = " + pageSpec.getId(), null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new page spec.");
			}

			db.insert(PageSpecs.TABLE, null, values);
		}
	}

	/**
	 * Delete form specs of the given form spec
	 */
	public synchronized void deletePageSpecs(long formSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(PageSpecs.TABLE, PageSpecs.FORM_SPEC_ID
				+ " = " + formSpecId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " page specs.");
		}
	}

	public String getTitle(long formSpecId, long pageId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		String title = null;

		try {
			cursor = db.query(PageSpecs.TABLE,
					new String[] { PageSpecs.TITLE }, PageSpecs._ID + " = "
							+ pageId, null, null, null, null);

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

	public PageSpec getPageSpec(long formSpecId, long pageId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		PageSpec pageSpec = null;

		try {
			cursor = db.query(PageSpecs.TABLE, PageSpecs.ALL_COLUMNS,
					PageSpecs.FORM_SPEC_ID + " = " + formSpecId + " AND "
							+ PageSpecs.PAGE_ID + " = " + pageId, null, null,
					null, null);

			if (cursor.moveToNext()) {
				pageSpec = new PageSpec();
				pageSpec.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return pageSpec;
	}

	public List<PageSpec> getPageSpecs(long formSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<PageSpec> pageSpecs = new ArrayList<PageSpec>();

		try {
			cursor = db.query(PageSpecs.TABLE, PageSpecs.ALL_COLUMNS,
					PageSpecs.FORM_SPEC_ID + " = " + formSpecId, null, null,
					null, PageSpecs.PAGE_ID);

			while (cursor.moveToNext()) {
				PageSpec pageSpec = new PageSpec();
				pageSpec.load(cursor);
				pageSpecs.add(pageSpec);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return pageSpecs;
	}

}

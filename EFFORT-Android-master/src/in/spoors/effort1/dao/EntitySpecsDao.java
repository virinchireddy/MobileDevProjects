package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.EntitySpec;
import in.spoors.effort1.provider.EffortProvider.EntitySpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class EntitySpecsDao {

	public static final String TAG = "EntitySpecsDao";
	private static EntitySpecsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static EntitySpecsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new EntitySpecsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private EntitySpecsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean entitySpecWithIdExists(long entitySpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + EntitySpecs._ID
					+ ") AS count FROM " + EntitySpecs.TABLE + " WHERE "
					+ EntitySpecs._ID + " = " + entitySpecId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(EntitySpec entitySpec) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = entitySpec.getContentValues(null);

		if (entitySpecWithIdExists(entitySpec.getId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating an existing entity spec.");
			}

			db.update(EntitySpecs.TABLE, values, EntitySpecs._ID + " = "
					+ entitySpec.getId(), null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new entity spec.");
			}

			db.insert(EntitySpecs.TABLE, null, values);
		}
	}

	/**
	 * Delete all
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(EntitySpecs.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " entity specs.");
		}
	}

	public String getFormTitle(long entitySpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		String title = null;

		try {
			cursor = db.query(EntitySpecs.TABLE,
					new String[] { EntitySpecs.TITLE }, EntitySpecs._ID + " = "
							+ entitySpecId, null, null, null, null);

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

	public EntitySpec getEntitySpec(long entitySpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		EntitySpec entitySpec = null;

		try {
			cursor = db.query(EntitySpecs.TABLE, EntitySpecs.ALL_COLUMNS,
					EntitySpecs._ID + " = " + entitySpecId, null, null, null,
					null);

			if (cursor.moveToNext()) {
				entitySpec = new EntitySpec();
				entitySpec.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return entitySpec;
	}

	/**
	 * Get all the entity spec IDs.
	 * 
	 * @return an empty array if there are no entity specs stored locally. (This
	 *         is a server side requirement)
	 * 
	 */
	public List<Long> getAllEntitySpecIds() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Long> ids = new ArrayList<Long>();

		try {
			cursor = db.query(EntitySpecs.TABLE,
					new String[] { EntitySpecs._ID }, null, null, null, null,
					EntitySpecs._ID);

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

}

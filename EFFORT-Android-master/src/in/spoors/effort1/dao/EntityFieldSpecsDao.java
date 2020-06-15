package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.EntityFieldSpec;
import in.spoors.effort1.provider.EffortProvider.EntityFieldSpecs;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class EntityFieldSpecsDao {

	public static final String TAG = "EntityFieldSpecsDao";
	private static EntityFieldSpecsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static EntityFieldSpecsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new EntityFieldSpecsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private EntityFieldSpecsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean fieldSpecWithIdExists(long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + EntityFieldSpecs._ID
					+ ") AS count FROM " + EntityFieldSpecs.TABLE + " WHERE "
					+ EntityFieldSpecs._ID + " = " + fieldSpecId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(EntityFieldSpec fieldSpec) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		ContentValues values = fieldSpec.getContentValues(null);

		if (fieldSpecWithIdExists(fieldSpec.getId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating an existing field spec.");
			}
			db.update(EntityFieldSpecs.TABLE, values, EntityFieldSpecs._ID
					+ " = " + fieldSpec.getId(), null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new field spec.");
			}
			db.insert(EntityFieldSpecs.TABLE, null, values);
		}
	}

	/**
	 * Delete all
	 */
	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(EntityFieldSpecs.TABLE, null, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " field specs.");
		}
	}

	public int getType(long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(EntityFieldSpecs.TABLE,
					new String[] { EntityFieldSpecs.TYPE },
					EntityFieldSpecs._ID + " = " + fieldSpecId, null, null,
					null, null);

			if (cursor.moveToNext()) {
				return cursor.getInt(0);
			} else {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Missing field spec type for entity field spec id "
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

	public EntityFieldSpec getFieldSpec(long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(EntityFieldSpecs.TABLE,
					EntityFieldSpecs.ALL_COLUMNS, EntityFieldSpecs._ID + " = "
							+ fieldSpecId, null, null, null, null);

			if (cursor.moveToNext()) {
				EntityFieldSpec fieldSpec = new EntityFieldSpec();
				fieldSpec.load(cursor);
				return fieldSpec;
			} else {
				if (BuildConfig.DEBUG) {
					Log.w(TAG, "Missing field spec for entity field spec id "
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

	
	public List<EntityFieldSpec> getFieldSpecs(long entitySpecId,
			boolean requiredFieldsOnly) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<EntityFieldSpec> fieldSpecs = null;

		try {
			cursor = db.query(EntityFieldSpecs.TABLE,
					EntityFieldSpecs.ALL_COLUMNS,
					EntityFieldSpecs.ENTITY_SPEC_ID
							+ " = "
							+ entitySpecId
							+ (requiredFieldsOnly ? " AND "
									+ EntityFieldSpecs.REQUIRED + " = 'true'"
									: ""), null, null, null, null);

			if (cursor.getCount() > 0) {
				fieldSpecs = new ArrayList<EntityFieldSpec>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				EntityFieldSpec fieldSpec = new EntityFieldSpec();
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

	public EntityFieldSpec getIdintifierFieldSpecHavingNumberOrCurrency(
			long entitySpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(EntityFieldSpecs.TABLE,
					EntityFieldSpecs.ALL_COLUMNS,
					EntityFieldSpecs.ENTITY_SPEC_ID + " = " + entitySpecId
							+ " AND " + EntityFieldSpecs.IDENTIFIER
							+ " = 'true' AND (" + EntityFieldSpecs.TYPE + " = "
							+ FieldSpecs.TYPE_NUMBER + " OR "
							+ EntityFieldSpecs.TYPE + " = "
							+ FieldSpecs.TYPE_CURRENCY + ")", null, null, null,
					null);

			if (cursor.moveToNext()) {
				EntityFieldSpec fieldSpec = new EntityFieldSpec();
				fieldSpec.load(cursor);
				return fieldSpec;
			} else {
				if (BuildConfig.DEBUG) {
					Log.w(TAG,
							"Missing identifier field spec for entity spec id "
									+ entitySpecId);
				}

				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<EntityFieldSpec> getFieldSpecsHavingNumberOrCurrency(
			long entitySpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		List<EntityFieldSpec> fieldSpecs = null;
		try {
			cursor = db.query(EntityFieldSpecs.TABLE,
					EntityFieldSpecs.ALL_COLUMNS,
					EntityFieldSpecs.ENTITY_SPEC_ID + " = " + entitySpecId
							+ " AND " + EntityFieldSpecs.TYPE + " IN("
							+ FieldSpecs.TYPE_NUMBER + ", "
							+ FieldSpecs.TYPE_CURRENCY + ")", null, null, null,
					null);
			if (cursor.getCount() > 0) {
				fieldSpecs = new ArrayList<EntityFieldSpec>(cursor.getCount());
			}
			while (cursor.moveToNext()) {
				EntityFieldSpec fieldSpec = new EntityFieldSpec();
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

	public boolean fieldSpecsWithTypeNumberOrCurrencyExists(long entitySpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(EntityFieldSpecs.TABLE,
					new String[] { EntityFieldSpecs._ID },
					EntityFieldSpecs.ENTITY_SPEC_ID + " = " + entitySpecId
							+ " AND " + EntityFieldSpecs.TYPE + " IN("
							+ FieldSpecs.TYPE_NUMBER + ", "
							+ FieldSpecs.TYPE_CURRENCY + ")", null, null, null,
					null);

			cursor.moveToNext();

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

}

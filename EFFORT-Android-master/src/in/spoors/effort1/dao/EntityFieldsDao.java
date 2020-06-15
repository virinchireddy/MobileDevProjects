package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.EntityField;
import in.spoors.effort1.provider.EffortProvider.EntityFields;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class EntityFieldsDao {

	public static final String TAG = "EntityFieldsDao";
	private static EntityFieldsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static EntityFieldsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new EntityFieldsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private EntityFieldsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean isFieldExists(EntityField field) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(EntityFields.TABLE,
					new String[] { EntityFields._ID },
					EntityFields.FIELD_SPEC_ID + " = " + field.getFieldSpecId()
							+ " AND " + EntityFields.LOCAL_ENTITY_ID + " = "
							+ field.getLocalEntityId(), null, null, null, null);

			if (cursor.getCount() > 0) {
				return true;
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return false;
	}

	public synchronized void save(EntityField field) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (field.getRemoteEntityId() != null
				&& field.getLocalEntityId() == null) {
			EntitiesDao entitiesDao = EntitiesDao
					.getInstance(applicationContext);
			field.setLocalEntityId(entitiesDao.getLocalId(field
					.getRemoteEntityId()));
		}

		EntityFieldSpecsDao fieldSpecsDao = EntityFieldSpecsDao
				.getInstance(applicationContext);
		int type = fieldSpecsDao.getType(field.getFieldSpecId());

		if (TextUtils.isEmpty(field.getLocalValue())
				&& !TextUtils.isEmpty(field.getRemoteValue())) {
			if (type == FieldSpecs.TYPE_CUSTOMER) {
				CustomersDao customersDao = CustomersDao
						.getInstance(applicationContext);
				if (BuildConfig.DEBUG) {
					Log.v(TAG, "remote value" + field.getRemoteValue());
				}
				Long localCustomerId = customersDao.getLocalId(Long
						.parseLong(field.getRemoteValue()));
				field.setLocalValue("" + localCustomerId);
			}
			if (type == FieldSpecs.TYPE_EMPLOYEE) {
				EmployeesDao employeesDao = EmployeesDao
						.getInstance(applicationContext);
				if (BuildConfig.DEBUG) {
					Log.v(TAG, "remote value" + field.getRemoteValue());
				}
				Long localEmployeerId = employeesDao.getLocalId(Long
						.parseLong(field.getRemoteValue()));
				field.setLocalValue("" + localEmployeerId);
			}
			if (type == FieldSpecs.TYPE_ENTITY) {
				
				EntitiesDao entitiesDao = EntitiesDao
						.getInstance(applicationContext);
				if (BuildConfig.DEBUG) {
					Log.v(TAG, "remote value" + field.getRemoteValue());
				}
				Long localEntityId = entitiesDao.getLocalId(Long
						.parseLong(field.getRemoteValue()));
				field.setLocalValue("" + localEntityId);
				// EntitiesDao entitiesDao = EntitiesDao
				// .getInstance(applicationContext);
				// if (BuildConfig.DEBUG) {
				// Log.v(TAG, "remote value" + field.getEntitySpecId());
				// }
				// EntityFieldSpec entityFieldSpec =
				// EntityFieldSpecsDao.getInstance(
				// applicationContext).getFieldSpec(
				// field.getFieldSpecId());
				// Long localEntityId = entitiesDao.getLocalId(Long
				// .parseLong(entityFieldSpec.getTypeExtra()));
				// field.setLocalValue("" + localEntityId);
			}
		}

		if (isFieldExists(field)) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating field: " + field.toString());
			}

			ContentValues values = field.getContentValues(null);
			db.update(EntityFields.TABLE, values,
					EntityFields.FIELD_SPEC_ID + " = " + field.getFieldSpecId()
							+ " AND " + EntityFields.LOCAL_ENTITY_ID + " = "
							+ field.getLocalEntityId(), null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new field: " + field.toString());
			}
			ContentValues values = field.getContentValues(null);
			db.insert(EntityFields.TABLE, null, values);
		}
	}

	/**
	 * Delete fields of the given local form ID
	 * 
	 * @param localJobId
	 */
	public synchronized void deleteFields(long localEntityId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(EntityFields.TABLE,
				EntityFields.LOCAL_ENTITY_ID + " = " + localEntityId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " fields of entity "
					+ localEntityId);
		}
	}

	public boolean entityHasFields(long localEntityId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + EntityFields._ID
					+ ") AS count FROM " + EntityFields.TABLE + " WHERE "
					+ EntityFields.LOCAL_ENTITY_ID + " = " + localEntityId,
					null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Return the field with given local field ID.
	 * 
	 * Null if the given field is not found.
	 * 
	 * @param localId
	 * @return
	 */
	public EntityField getFieldWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(EntityFields.TABLE, EntityFields.ALL_COLUMNS,
					EntityFields._ID + " = " + localId, null, null, null, null);

			if (cursor.moveToNext()) {
				EntityField field = new EntityField();
				field.load(cursor, applicationContext);
				return field;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public EntityField getField(long localEntityId, long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(EntityFields.TABLE, EntityFields.ALL_COLUMNS,
					EntityFields.LOCAL_ENTITY_ID + " = " + localEntityId
							+ " AND " + EntityFields.FIELD_SPEC_ID + " = "
							+ fieldSpecId, null, null, null, null);

			if (cursor.moveToNext()) {
				EntityField field = new EntityField();
				field.load(cursor, applicationContext);
				return field;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public List<EntityField> getFields(long localEntityId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<EntityField> fields = null;

		try {
			cursor = db.query(EntityFields.TABLE, EntityFields.ALL_COLUMNS,
					EntityFields.LOCAL_ENTITY_ID + " = " + localEntityId, null,
					null, null, null);

			if (cursor.getCount() > 0) {
				fields = new ArrayList<EntityField>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				EntityField field = new EntityField();
				field.load(cursor, applicationContext);
				fields.add(field);
			}

			return fields;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<String> getFields(long entitySpecId, long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<String> strings = new ArrayList<String>();

		try {
			cursor = db.query(EntityFields.TABLE,
					new String[] { EntityFields.REMOTE_VALUE },
					EntityFields.ENTITY_SPEC_ID + " = " + entitySpecId
							+ " AND " + EntityFields.FIELD_SPEC_ID + " = "
							+ fieldSpecId, null, null, null,
					EntityFields.REMOTE_VALUE);

			while (cursor.moveToNext()) {
				strings.add(cursor.getString(0));
			}

			return strings;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Long getLocalEntityId(long entitySpecId, long fieldSpecId,
			String remoteValue) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(EntityFields.TABLE,
					new String[] { EntityFields.LOCAL_ENTITY_ID },
					EntityFields.ENTITY_SPEC_ID + " = " + entitySpecId
							+ " AND " + EntityFields.FIELD_SPEC_ID + " = "
							+ fieldSpecId + " AND " + EntityFields.REMOTE_VALUE
							+ " = ?", new String[] { remoteValue }, null, null,
					null);

			if (cursor.moveToNext()) {
				return cursor.getLong(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

}

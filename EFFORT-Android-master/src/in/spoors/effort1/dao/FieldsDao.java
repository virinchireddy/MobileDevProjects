package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dto.Field;
import in.spoors.effort1.dto.FormFile;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;
import in.spoors.effort1.provider.EffortProvider.Fields;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class FieldsDao {

	public static final String TAG = "FieldsDao";
	private static FieldsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static FieldsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new FieldsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private FieldsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void updateFileId(Long localFileId, Long localFieldId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		Utils.putNullOrValue(values, Fields.LOCAL_VALUE, localFileId);
		db.update(Fields.TABLE, values, Fields._ID + " = ?", new String[] { ""
				+ localFieldId });

	}

	public boolean isFieldExists(Field field) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(
					Fields.TABLE,
					new String[] { Fields._ID },
					"field_spec_id = ? AND local_form_id = ?",
					new String[] { "" + field.getFieldSpecId(),
							"" + field.getLocalFormId() }, null, null, null);

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

	public synchronized void save(Field field) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (field.getRemoteFormId() != null && field.getLocalFormId() == null) {
			FormsDao formsDao = FormsDao.getInstance(applicationContext);
			field.setLocalFormId(formsDao.getLocalId(field.getRemoteFormId()));
		}

		FieldSpecsDao fieldSpecsDao = FieldSpecsDao
				.getInstance(applicationContext);
		int type = fieldSpecsDao.getType(field.getFieldSpecId());

		if (field.getFieldSpecId() != null
				&& TextUtils.isEmpty(field.getFieldSpecUniqueId())) {
			field.setFieldSpecUniqueId(fieldSpecsDao.getUniqueId(field
					.getFieldSpecId()));
		}

		if (TextUtils.isEmpty(field.getLocalValue())
				&& !TextUtils.isEmpty(field.getRemoteValue())) {
			if (type == FieldSpecs.TYPE_CUSTOMER) {
				CustomersDao customersDao = CustomersDao
						.getInstance(applicationContext);
				// TODO
				// long localCustomerId = customersDao.getLocalId(Long
				// .parseLong(field.getRemoteValue()));
				// field.setLocalValue("" + localCustomerId);
				// field.setRemoteValue(null);
				Long localCustomerId = customersDao.getLocalId(Long
						.parseLong(field.getRemoteValue()));
				if (localCustomerId != null) {
					field.setLocalValue("" + localCustomerId);
					field.setRemoteValue(null);
				} else {
					field.setLocalValue(null);
					field.setRemoteValue(null);
				}

			} else if (type == FieldSpecs.TYPE_ENTITY) {
				EntitiesDao entitiesDao = EntitiesDao
						.getInstance(applicationContext);
				// TODO
				// long localEntityId = entitiesDao.getLocalId(Long
				// .parseLong(field.getRemoteValue()));
				// field.setLocalValue("" + localEntityId);
				// field.setRemoteValue(null);

				Long localEntityId = entitiesDao.getLocalId(Long
						.parseLong(field.getRemoteValue()));
				if (localEntityId != null) {
					field.setLocalValue("" + localEntityId);
					field.setRemoteValue(null);
				} else {
					field.setLocalValue(null);
					field.setRemoteValue(null);
				}

			} else if (type == FieldSpecs.TYPE_EMPLOYEE) {
				EmployeesDao employeesDao = EmployeesDao
						.getInstance(applicationContext);
				// TODO
				// long localEmployeeId = employeesDao.getLocalId(Long
				// .parseLong(field.getRemoteValue()));
				// field.setLocalValue("" + localEmployeeId);
				// field.setRemoteValue(null);
				Long localEmployeeId = employeesDao.getLocalId(Long
						.parseLong(field.getRemoteValue()));

				if (localEmployeeId != null) {
					field.setLocalValue("" + localEmployeeId);
					field.setRemoteValue(null);
				} else {
					field.setLocalValue(null);
					field.setRemoteValue(null);
				}

			} else if (type == FieldSpecs.TYPE_MULTI_LIST) {
				EntitiesDao entitiesDao = EntitiesDao
						.getInstance(applicationContext);
				String localIdsString = null;
				String remoteIdsAsString = field.getRemoteValue();
				if (remoteIdsAsString != null) {
					String[] selectedIds = remoteIdsAsString.split(",");
					ArrayList<String> values = new ArrayList<String>();

					for (int i = 0; i < selectedIds.length; i++) {
						selectedIds[i] = selectedIds[i].trim();
						Long localEntityId = entitiesDao.getLocalId(Long
								.parseLong(selectedIds[i]));
						if (localEntityId != null) {
							values.add(localEntityId + "");
						}
					}
					localIdsString = TextUtils.join(",", values);
				}
				if (!TextUtils.isEmpty(localIdsString)) {
					field.setLocalValue("" + localIdsString);
					field.setRemoteValue(null);
				} else {
					field.setLocalValue(null);
					field.setRemoteValue(null);
				}

			}

		}

		if (type == FieldSpecs.TYPE_IMAGE || type == FieldSpecs.TYPE_SIGNATURE) {
			if (!TextUtils.isEmpty(field.getRemoteValue())) {
				FormFilesDao formFilesDao = FormFilesDao
						.getInstance(applicationContext);

				if (field.getLocalFormId() == null) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"Local form id for remote form id "
										+ field.getRemoteFormId()
										+ " is missing. Not saving the field.");
					}

					return;
				}

				FormFile formFile = formFilesDao.getFile(
						field.getFieldSpecId(), field.getLocalFormId());
				long newMediaId = Long.parseLong(field.getRemoteValue());

				if (formFile == null) {
					formFile = new FormFile();
					formFile.setFieldSpecId(field.getFieldSpecId());
					formFile.setLocalFormId(field.getLocalFormId());
					formFile.setMediaId(newMediaId);
					formFile.setLocalMediaPath(null);
				} else {
					if (newMediaId != -1
							&& !Utils.longsEqual(formFile.getMediaId(),
									newMediaId)) {
						formFile.setMediaId(newMediaId);
						formFile.setLocalMediaPath(null);
					} else if (newMediaId == -1
							&& formFile.getMediaId() != null
							&& formFile.getMediaId() > 0) {
						// mark the form dirty
						FormsDao formsDao = FormsDao
								.getInstance(applicationContext);
						formsDao.updateDirtyFlag(field.getLocalFormId(), true);
					}
				}

				formFilesDao.save(formFile);
				field.setLocalValue(null);
				field.setRemoteValue(null);
			}
		}

		if (isFieldExists(field)) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating field: " + field.toString());
			}

			ContentValues values = field.getContentValues(null);
			db.update(Fields.TABLE, values, Fields.FIELD_SPEC_ID + " = "
					+ field.getFieldSpecId() + " AND " + Fields.LOCAL_FORM_ID
					+ " = " + field.getLocalFormId(), null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new field: " + field.toString());
			}
			ContentValues values = field.getContentValues(null);
			db.insert(Fields.TABLE, null, values);
		}
	}

	/**
	 * Delete fields of the given local form ID
	 * 
	 * @param localJobId
	 */
	public synchronized void deleteFields(long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(Fields.TABLE, Fields.LOCAL_FORM_ID + " = "
				+ localFormId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " fields of form "
					+ localFormId);
		}
	}

	/**
	 * Delete fields with a given fieldspecid, formspecid
	 * 
	 * @param localJobId
	 */
	public synchronized void deleteFields(long localFormId, long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(Fields.TABLE, Fields.LOCAL_FORM_ID + " = "
				+ localFormId + " AND " + Fields.FIELD_SPEC_ID + " = "
				+ fieldSpecId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " fields of form "
					+ localFormId);
		}
	}

	public boolean formHasFields(long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Fields._ID
					+ ") AS count FROM " + Fields.TABLE + " WHERE "
					+ Fields.LOCAL_FORM_ID + " = " + localFormId, null);

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
	public Field getFieldWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Fields.TABLE, Fields.ALL_COLUMNS, Fields._ID
					+ " = " + localId, null, null, null, null);

			if (cursor.moveToNext()) {
				Field field = new Field();
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

	public Field getField(long localFormId, long fieldSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			FieldSpecsDao fieldSpecsDao = FieldSpecsDao
					.getInstance(applicationContext);
			String fieldSpecUniqueId = fieldSpecsDao.getUniqueId(fieldSpecId);

			cursor = db.query(Fields.TABLE, Fields.ALL_COLUMNS,
					Fields.LOCAL_FORM_ID + " = " + localFormId + " AND "
							+ Fields.FIELD_SPEC_UNIQUE_ID + " = '"
							+ fieldSpecUniqueId + "'", null, null, null, null);

			if (cursor.moveToNext()) {
				Field field = new Field();
				field.load(cursor, applicationContext);

				// make sure that data type remained the same as this version
				if (field.getFieldSpecId() != fieldSpecId) {
					if (fieldSpecsDao.getType(field.getFieldSpecId()) != fieldSpecsDao
							.getType(fieldSpecId)) {
						return null;
					}
				}

				return field;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public List<Field> getFields(long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Field> fields = null;

		try {
			cursor = db.query(Fields.TABLE, Fields.ALL_COLUMNS,
					Fields.LOCAL_FORM_ID + " = " + localFormId, null, null,
					null, null);

			if (cursor.getCount() > 0) {
				fields = new ArrayList<Field>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Field field = new Field();
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

}

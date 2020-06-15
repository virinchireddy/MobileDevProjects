package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dto.SectionField;
import in.spoors.effort1.dto.SectionFile;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;
import in.spoors.effort1.provider.EffortProvider.SectionFields;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class SectionFieldsDao {

	public static final String TAG = "SectionFieldsDao";
	private static SectionFieldsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SectionFieldsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new SectionFieldsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private SectionFieldsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public void updateFileId(Long localFileId, Long localFieldId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		Utils.putNullOrValue(values, SectionFields.LOCAL_VALUE, localFileId);
		db.update(SectionFields.TABLE, values, SectionFields._ID + " = ?",
				new String[] { "" + localFieldId });

	}

	public boolean isFieldExists(SectionField field) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(
					SectionFields.TABLE,
					new String[] { SectionFields._ID },
					SectionFields.FIELD_SPEC_ID + " = "
							+ field.getFieldSpecId() + " AND "
							+ SectionFields.LOCAL_FORM_ID + " = "
							+ field.getLocalFormId() + " AND "
							+ SectionFields.SECTION_INSTANCE_ID + " = "
							+ field.getSectionInstanceId(), null, null, null,
					null);

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

	public synchronized void save(SectionField field) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (field.getRemoteFormId() != null && field.getLocalFormId() == null) {
			FormsDao formsDao = FormsDao.getInstance(applicationContext);
			field.setLocalFormId(formsDao.getLocalId(field.getRemoteFormId()));
		}

		SectionFieldSpecsDao fieldSpecsDao = SectionFieldSpecsDao
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
				long localCustomerId = customersDao.getLocalId(Long
						.parseLong(field.getRemoteValue()));
				field.setLocalValue("" + localCustomerId);
				field.setRemoteValue(null);
			} else if (type == FieldSpecs.TYPE_ENTITY) {
				EntitiesDao entitiesDao = EntitiesDao
						.getInstance(applicationContext);
				long localEntityId = entitiesDao.getLocalId(Long
						.parseLong(field.getRemoteValue()));
				field.setLocalValue("" + localEntityId);
				field.setRemoteValue(null);
			} else if (type == FieldSpecs.TYPE_EMPLOYEE) {
				EmployeesDao employeesDao = EmployeesDao
						.getInstance(applicationContext);
				long localEmployeeId = employeesDao.getLocalId(Long
						.parseLong(field.getRemoteValue()));
				field.setLocalValue("" + localEmployeeId);
				field.setRemoteValue(null);
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
						values.add(entitiesDao.getLocalId(Long
								.parseLong(selectedIds[i])) + "");
					}
					localIdsString = TextUtils.join(",", values);
				}
				field.setLocalValue("" + localIdsString);
				field.setRemoteValue(null);
			}

		}

		if (type == FieldSpecs.TYPE_IMAGE || type == FieldSpecs.TYPE_SIGNATURE) {
			if (!TextUtils.isEmpty(field.getRemoteValue())) {
				SectionFilesDao sectionFilesDao = SectionFilesDao
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

				SectionFile sectionFile = sectionFilesDao.getFile(
						field.getFieldSpecId(), field.getLocalFormId(),
						field.getSectionInstanceId());
				long newMediaId = Long.parseLong(field.getRemoteValue());

				if (sectionFile == null) {
					sectionFile = new SectionFile();
					sectionFile.setFieldSpecId(field.getFieldSpecId());
					sectionFile.setLocalFormId(field.getLocalFormId());
					sectionFile.setSectionInstanceId(field
							.getSectionInstanceId());
					sectionFile.setMediaId(newMediaId);
					sectionFile.setLocalMediaPath(null);
				} else {
					if (newMediaId != -1
							&& !Utils.longsEqual(sectionFile.getMediaId(),
									newMediaId)) {
						sectionFile.setMediaId(newMediaId);
						sectionFile.setLocalMediaPath(null);
					} else if (newMediaId == -1
							&& sectionFile.getMediaId() != null
							&& sectionFile.getMediaId() > 0) {
						// mark the form dirty
						FormsDao formsDao = FormsDao
								.getInstance(applicationContext);
						formsDao.updateDirtyFlag(field.getLocalFormId(), true);
					}
				}

				sectionFilesDao.save(sectionFile);
				field.setLocalValue(null);
				field.setRemoteValue(null);
			}
		}

		if (isFieldExists(field)) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating section field: " + field.toString());
			}

			ContentValues values = field.getContentValues(null);
			db.update(
					SectionFields.TABLE,
					values,
					SectionFields.FIELD_SPEC_ID + " = "
							+ field.getFieldSpecId() + " AND "
							+ SectionFields.LOCAL_FORM_ID + " = "
							+ field.getLocalFormId() + " AND "
							+ SectionFields.SECTION_INSTANCE_ID + " = "
							+ field.getSectionInstanceId(), null);
		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Saving a new section field: " + field.toString());
			}
			ContentValues values = field.getContentValues(null);
			db.insert(SectionFields.TABLE, null, values);
		}
	}

	/**
	 * Delete fields of the given local form ID
	 * 
	 * @param localJobId
	 */
	public synchronized void deleteFields(long localFormId, long sectionSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db
				.delete(SectionFields.TABLE,
						SectionFields.LOCAL_FORM_ID + " = " + localFormId
								+ " AND " + SectionFields.SECTION_SPEC_ID
								+ " = " + sectionSpecId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " fields of section "
					+ sectionSpecId + " of form " + localFormId);
		}
	}

	/**
	 * Delete fields with a given fieldspecid, fromspecid and sectioninstanceid
	 * 
	 * @param localJobId
	 */

	public synchronized void deleteFields(long localFormId, long fieldSpecId,
			int sectionInstanceId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(SectionFields.TABLE,
				SectionFields.LOCAL_FORM_ID + " = " + localFormId + " AND "
						+ SectionFields.FIELD_SPEC_ID + " = " + fieldSpecId
						+ " AND " + SectionFields.SECTION_INSTANCE_ID + " = "
						+ sectionInstanceId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " fields of section "
					+ fieldSpecId + " of form " + localFormId);
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
		int affectedRows = db.delete(SectionFields.TABLE,
				SectionFields.LOCAL_FORM_ID + " = " + localFormId, null);

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
			cursor = db.rawQuery("SELECT COUNT(" + SectionFields._ID
					+ ") AS count FROM " + SectionFields.TABLE + " WHERE "
					+ SectionFields.LOCAL_FORM_ID + " = " + localFormId, null);

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
	public SectionField getFieldWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db
					.query(SectionFields.TABLE, SectionFields.ALL_COLUMNS,
							SectionFields._ID + " = " + localId, null, null,
							null, null);

			if (cursor.moveToNext()) {
				SectionField field = new SectionField();
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

	public SectionField getField(long localFormId, long fieldSpecId,
			int sectionInstanceId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			SectionFieldSpecsDao fieldSpecsDao = SectionFieldSpecsDao
					.getInstance(applicationContext);
			String fieldSpecUniqueId = fieldSpecsDao.getUniqueId(fieldSpecId);
			cursor = db.query(SectionFields.TABLE, SectionFields.ALL_COLUMNS,
					SectionFields.LOCAL_FORM_ID + " = " + localFormId + " AND "
							+ SectionFields.FIELD_SPEC_UNIQUE_ID + " = '"
							+ fieldSpecUniqueId + "' AND "
							+ SectionFields.SECTION_INSTANCE_ID + " = "
							+ sectionInstanceId, null, null, null, null);

			if (cursor.moveToNext()) {
				SectionField field = new SectionField();
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

	public List<SectionField> getFields(long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<SectionField> fields = null;

		try {
			cursor = db.query(SectionFields.TABLE, SectionFields.ALL_COLUMNS,
					SectionFields.LOCAL_FORM_ID + " = " + localFormId, null,
					null, null, null);

			if (cursor.getCount() > 0) {
				fields = new ArrayList<SectionField>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				SectionField field = new SectionField();
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

	public List<SectionField> getFields(long localFormId, long sectionSpecId,
			int sectionInstanceId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<SectionField> fields = null;

		try {
			cursor = db.query(SectionFields.TABLE, SectionFields.ALL_COLUMNS,
					SectionFields.LOCAL_FORM_ID + " = " + localFormId + " AND "
							+ SectionFields.SECTION_SPEC_ID + " = "
							+ sectionSpecId + " AND "
							+ SectionFields.SECTION_INSTANCE_ID + " = "
							+ sectionInstanceId, null, null, null, null);

			if (cursor.getCount() > 0) {
				fields = new ArrayList<SectionField>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				SectionField field = new SectionField();
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

	public List<Integer> getSectionInstances(long localFormId,
			long sectionSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Integer> instances = new ArrayList<Integer>();

		try {
			cursor = db.rawQuery("SELECT DISTINCT "
					+ SectionFields.SECTION_INSTANCE_ID + " FROM "
					+ SectionFields.TABLE + " WHERE "
					+ SectionFields.LOCAL_FORM_ID + " = " + localFormId
					+ " AND " + SectionFields.SECTION_SPEC_ID + " = "
					+ sectionSpecId + " ORDER BY "
					+ SectionFields.SECTION_INSTANCE_ID, null);

			while (cursor.moveToNext()) {
				instances.add(cursor.getInt(0));
			}

			return instances;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}

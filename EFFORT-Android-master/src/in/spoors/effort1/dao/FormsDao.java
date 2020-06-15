package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.Form;
import in.spoors.effort1.provider.EffortProvider.CompletedActivities;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;
import in.spoors.effort1.provider.EffortProvider.Fields;
import in.spoors.effort1.provider.EffortProvider.FormFiles;
import in.spoors.effort1.provider.EffortProvider.Forms;
import in.spoors.effort1.provider.EffortProvider.JobHistories;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.SectionFiles;
import in.spoors.effort1.provider.EffortProvider.WorkFlowStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

public class FormsDao {

	public static final String TAG = "FormsDao";
	private static FormsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static FormsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new FormsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private FormsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean formWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Forms._ID
					+ ") AS count FROM " + Forms.TABLE + " WHERE " + Forms._ID
					+ " = " + localId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean formWithRemoteIdExists(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Forms.REMOTE_ID
					+ ") AS count FROM " + Forms.TABLE + " WHERE "
					+ Forms.REMOTE_ID + " = " + remoteId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Saves the given form.
	 * 
	 * Local ID of the form is updated with the inserted ID.
	 * 
	 * @param form
	 */
	public synchronized void save(Form form) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (form.getFormSpecId() != null
				&& TextUtils.isEmpty(form.getFormSpecUniqueId())) {
			FormSpecsDao formSpecsDao = FormSpecsDao
					.getInstance(applicationContext);
			form.setFormSpecUniqueId(formSpecsDao.getUniqueId(form
					.getFormSpecId()));
		}

		if (form.getLocalId() != null
				&& formWithLocalIdExists(form.getLocalId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting form.");
			}

			form.setLocalModificationTime(new Date());
			form.setLocalCreationTime(getLocalCreationTimeWithLocalId(form
					.getLocalId()));

			if (form.getRemoteCreationTime() == null) {
				form.setRemoteCreationTime(form.getLocalCreationTime());
			}

			ContentValues values = form.getContentValues(null);

			db.update(Forms.TABLE, values,
					Forms._ID + " = " + form.getLocalId(), null);

			return;
		}

		if (form.getRemoteId() != null
				&& formWithRemoteIdExists(form.getRemoteId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting form.");
			}

			form.setLocalModificationTime(new Date());
			form.setLocalCreationTime(getLocalCreationTimeWithRemoteId(form
					.getRemoteId()));

			if (form.getRemoteCreationTime() == null) {
				form.setRemoteCreationTime(form.getLocalCreationTime());
			}

			ContentValues values = form.getContentValues(null);
			// This is used to set local form id when server sends same form
			// multiple times because of a server side bug.
			form.setLocalId(getLocalId(form.getRemoteId()));

			db.update(Forms.TABLE, values,
					Forms.REMOTE_ID + " = " + form.getRemoteId(), null);

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new form.");
		}

		Date now = new Date();
		form.setLocalCreationTime(now);

		if (form.getRemoteCreationTime() == null) {
			form.setRemoteCreationTime(form.getLocalCreationTime());
		}

		form.setLocalModificationTime(now);

		ContentValues values = form.getContentValues(null);
		long insertedId = db.insert(Forms.TABLE, null, values);
		form.setLocalId(insertedId);
	}

	/**
	 * Returns the local form id of the given remote form id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Long getLocalId(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Forms._ID + " FROM " + Forms.TABLE
					+ " WHERE " + Forms.REMOTE_ID + " = " + remoteId, null);

			if (cursor.moveToNext() && !cursor.isNull(0)) {
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

	/**
	 * Returns the remote form id of the given local form id.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public Long getRemoteId(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Forms.REMOTE_ID + " FROM "
					+ Forms.TABLE + " WHERE " + Forms._ID + " = " + localId,
					null);

			if (cursor.moveToNext() && !cursor.isNull(0)) {
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

	/**
	 * Returns the local creation time of the given local form id.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithLocalId(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Forms.LOCAL_CREATION_TIME
					+ " FROM " + Forms.TABLE + " WHERE " + Forms._ID + " = "
					+ localId, null);

			if (cursor.moveToNext()) {
				return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
			} else {
				return null;
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Returns the local creation time of the given remote form id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithRemoteId(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Forms.LOCAL_CREATION_TIME
					+ " FROM " + Forms.TABLE + " WHERE " + Forms.REMOTE_ID
					+ " = " + remoteId, null);

			if (cursor.moveToNext()) {
				return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
			} else {
				return null;
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void deleteForm(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		FieldsDao fieldsDao = FieldsDao.getInstance(applicationContext);
		fieldsDao.deleteFields(localId);

		FormFilesDao formFilesDao = FormFilesDao
				.getInstance(applicationContext);
		formFilesDao.deleteFormFiles(localId);

		SectionFieldsDao sectionFieldsDao = SectionFieldsDao
				.getInstance(applicationContext);
		sectionFieldsDao.deleteFields(localId);

		SectionFilesDao sectionFilesDao = SectionFilesDao
				.getInstance(applicationContext);
		sectionFilesDao.deleteSectionFiles(localId);

		LocationsDao locationsDao = LocationsDao
				.getInstance(applicationContext);
		locationsDao.deleteFormLocations(localId);

		// now, delete the form
		int affectedRows = db.delete(Forms.TABLE, Forms._ID + " = ?",
				new String[] { "" + localId });

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted form with local form id: " + localId
					+ ", affectedRows=" + affectedRows);
		}
	}

	public Form getFormWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Form form = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Forms.TABLE, Forms.ALL_COLUMNS, Forms._ID + " = "
					+ localId, null, null, null, null);

			if (cursor.moveToNext()) {
				form = new Form();
				form.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return form;
	}

	public Form getFormWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Form form = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Forms.TABLE, Forms.ALL_COLUMNS, Forms.REMOTE_ID
					+ " = " + remoteId, null, null, null, null);

			if (cursor.moveToNext()) {
				form = new Form();
				form.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return form;
	}

	private Cursor queryWithLocations(String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(Forms.TABLE + " LEFT JOIN " + Locations.TABLE
				+ " ON " + Forms.TABLE + "." + Forms._ID + " = "
				+ Locations.TABLE + "." + Locations.FOR_ID + " AND "
				+ Locations.TABLE + "." + Locations.PURPOSE + " = "
				+ Locations.PURPOSE_FORM);

		// qualify the columns by the table name, so that join
		// doesn't confuse the query engine with ambiguous column
		// names
		String[] columns = new String[Forms.ALL_COLUMNS.length];
		for (int i = 0; i < Forms.ALL_COLUMNS.length; ++i) {
			columns[i] = Forms.TABLE + "." + Forms.ALL_COLUMNS[i];
		}

		builder.setDistinct(true);
		return builder.query(db, columns, selection, selectionArgs, null, null,
				sortOrder);
	}

	/**
	 * Get the forms that are added on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<Form> getAddedForms() {
		Cursor cursor = null;
		List<Form> forms = null;

		try {
			cursor = queryWithLocations(Forms.TABLE + "." + Forms.REMOTE_ID
					+ " IS NULL AND " + Forms.TABLE + "." + Forms.TEMPORARY
					+ " = 'false' AND (" + Locations.TABLE + "."
					+ Locations.LOCATION_FINALIZED + " = 'true' OR "
					+ Locations.TABLE + "." + Locations.LOCATION_FINALIZED
					+ " IS NULL)", null, Forms.TABLE + "."
					+ Forms.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				forms = new ArrayList<Form>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Form form = new Form();
				form.load(cursor, applicationContext);
				forms.add(form);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return forms;
	}

	/**
	 * Get the forms that are modified on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<Form> getModifiedForms() {
		Cursor cursor = null;
		List<Form> forms = null;

		try {
			cursor = queryWithLocations(Forms.TABLE + "." + Forms.REMOTE_ID
					+ " IS NOT NULL AND " + Forms.TABLE + "." + Forms.DIRTY
					+ " = 'true' AND (" + Locations.TABLE + "."
					+ Locations.LOCATION_FINALIZED + " = 'true' OR "
					+ Locations.TABLE + "." + Locations.LOCATION_FINALIZED
					+ " IS NULL)", null, Forms.TABLE + "."
					+ Forms.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				forms = new ArrayList<Form>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Form form = new Form();
				form.load(cursor, applicationContext);
				forms.add(form);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return forms;
	}

	public long getFormSpecId(long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Forms.TABLE, new String[] { Forms.FORM_SPEC_ID },
					Forms._ID + " = " + localFormId, null, null, null, null);

			cursor.moveToNext();
			return cursor.getLong(0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public String getFormSpecUniqueId(long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Forms.TABLE,
					new String[] { Forms.FORM_SPEC_UNIQUE_ID }, Forms._ID
							+ " = " + localFormId, null, null, null, null);

			cursor.moveToNext();
			return cursor.getString(0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Date getOldestFormStartTime(long formSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT MIN(" + Forms.REMOTE_CREATION_TIME
					+ ") FROM " + Forms.TABLE, null);

			cursor.moveToNext();
			return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public int getNumberOfFormsOlderThanToday() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			// today in UTC
			String today = SQLiteDateTimeUtils
					.getSQLiteDateTimeForBeginningOfToday();

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Today: " + today);
			}

			cursor = db.rawQuery("SELECT COUNT(" + Forms._ID + ") FROM "
					+ Forms.TABLE + " WHERE " + Forms.REMOTE_CREATION_TIME
					+ " < '" + today + "' AND " + Forms.LOCAL_MODIFICATION_TIME
					+ " < '" + today + "'", null);

			cursor.moveToNext();
			return cursor.getInt(0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 
	 * @param remoteId
	 * @return remote creation time of the form in SQLite Date Time format.
	 * 
	 */
	public String getRemoteCreationTime(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Forms.REMOTE_CREATION_TIME
					+ " FROM " + Forms.TABLE + " WHERE " + Forms.REMOTE_ID
					+ " = " + remoteId, null);

			if (cursor.moveToNext()) {
				return cursor.getString(0);
			} else {
				return null;
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Deletes all cached forms along with their fields.
	 * 
	 */
	public synchronized int deleteCachedForms() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Cursor cursor = db.query(Forms.TABLE, new String[] { Forms._ID },
				Forms.CACHED + " = 'true'", null, null, null, null);

		while (cursor.moveToNext()) {
			deleteForm(cursor.getLong(0));
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + affectedRows + " cached forms.");
		}

		cursor.close();
		return affectedRows;
	}

	// public synchronized int deleteOldForms() {
	// SQLiteDatabase db = DBHelper.getInstance(applicationContext)
	// .getWritableDatabase();
	//
	// String today = SQLiteDateTimeUtils
	// .getSQLiteDateTimeForBeginningOfToday();
	//
	// Cursor cursor = db.query(Forms.TABLE, new String[] { Forms._ID },
	// Forms.DIRTY + " = ? AND " + Forms.TREE_DIRTY + " = ? AND "
	// + Forms.LOCAL_MODIFICATION_TIME + " < ?", new String[] {
	// "false", "false", today }, null, null, null);
	//
	// while (cursor.moveToNext()) {
	// deleteForm(cursor.getLong(0));
	// }
	//
	// int affectedRows = cursor.getCount();
	//
	// if (BuildConfig.DEBUG) {
	// Log.i(TAG, "Deleted " + affectedRows + " old forms.");
	// }
	//
	// cursor.close();
	// return affectedRows;
	// }

	public synchronized int deleteOldForms() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		String today = SQLiteDateTimeUtils
				.getSQLiteDateTimeForBeginningOfToday();

		Cursor cursor = db.query(Forms.TABLE, new String[] { Forms._ID },
				Forms.DIRTY + " = ? AND " + Forms.TREE_DIRTY + " = ? AND "
						+ Forms.LOCAL_MODIFICATION_TIME + " < ?", new String[] {
						"false", "false", today }, null, null, null);

		while (cursor.moveToNext()) {
			deleteForm(cursor.getLong(0));
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + affectedRows + " old forms.");
		}

		cursor.close();
		return affectedRows;
	}

	public synchronized int deleteTemporaryJobForms(long localJobId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		String query = "SELECT " + Forms.TABLE + "." + Forms._ID + " FROM "
				+ Forms.TABLE + " WHERE " + Forms.TABLE + "." + Forms._ID
				+ " IN (SELECT " + JobHistories.TABLE + "."
				+ JobHistories.LOCAL_FORM_ID + " FROM " + JobHistories.TABLE
				+ " WHERE " + JobHistories.TABLE + "."
				+ JobHistories.LOCAL_JOB_ID + " = " + localJobId + ")";

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Query: " + query);
		}

		Cursor cursor = db.rawQuery(query, null);

		while (cursor.moveToNext()) {
			deleteForm(cursor.getLong(0));
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + affectedRows + " temparory job forms.");
		}

		cursor.close();
		return affectedRows;
	}

	public synchronized int updateTemporaryFlagToJobRelatedForms(
			long localJobId, boolean temporary) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		String query = "SELECT " + Forms.TABLE + "." + Forms._ID + " FROM "
				+ Forms.TABLE + " WHERE " + Forms.TABLE + "." + Forms._ID
				+ " IN (SELECT " + JobHistories.TABLE + "."
				+ JobHistories.LOCAL_FORM_ID + " FROM " + JobHistories.TABLE
				+ " WHERE " + JobHistories.TABLE + "."
				+ JobHistories.LOCAL_JOB_ID + " = " + localJobId + ")";

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Query: " + query);
		}

		Cursor cursor = db.rawQuery(query, null);

		while (cursor.moveToNext()) {
			updateTemporaryFlag(cursor.getLong(0), temporary);
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Updated " + affectedRows + " temparory job forms.");
		}

		cursor.close();
		return affectedRows;
	}

	/**
	 * Deletes all temporary forms along with their fields.
	 * 
	 */
	public synchronized int deleteTemporaryForms() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Cursor cursor = db.query(Forms.TABLE, new String[] { Forms._ID },
				Forms.TEMPORARY + " = 'true'", null, null, null, null);

		while (cursor.moveToNext()) {
			deleteForm(cursor.getLong(0));
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + affectedRows + " cached forms.");
		}

		cursor.close();
		return affectedRows;
	}

	public synchronized void updateCachedFlag(long localFormId, boolean cached) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Forms.CACHED, String.valueOf(cached));

		db.update(Forms.TABLE, values, Forms._ID + " = " + localFormId, null);
	}

	public synchronized void updateDirtyFlag(long localFormId, boolean dirty) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Forms.DIRTY, String.valueOf(dirty));

		db.update(Forms.TABLE, values, Forms._ID + " = " + localFormId, null);
	}

	private Cursor query(String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(Forms.TABLE + " JOIN " + Fields.TABLE + " ON "
				+ Forms.TABLE + "." + Forms._ID + " = " + Fields.TABLE + "."
				+ Fields.LOCAL_FORM_ID + " JOIN " + FormFiles.TABLE + " ON "
				+ FormFiles.TABLE + "." + FormFiles._ID + " = " + Fields.TABLE
				+ "." + Fields.LOCAL_VALUE + " JOIN " + FieldSpecs.TABLE
				+ " ON " + Fields.TABLE + "." + Fields.FIELD_SPEC_ID + "="
				+ FieldSpecs.TABLE + "." + FieldSpecs._ID + " AND "
				+ FieldSpecs.TYPE + " IN (" + FieldSpecs.TYPE_IMAGE + ","
				+ FieldSpecs.TYPE_SIGNATURE + ")");

		// qualify the columns by the table name, so that join
		// doesn't result confuse the query engine with ambiguous column
		// names
		String[] columns = new String[Forms.ALL_COLUMNS.length];
		for (int i = 0; i < Forms.ALL_COLUMNS.length; ++i) {
			columns[i] = Forms.TABLE + "." + Forms.ALL_COLUMNS[i];
		}

		return builder.query(db, columns, selection, selectionArgs, null, null,
				sortOrder);
	}

	public Long getLocalFormIdOfFile(long localFileId) {
		Cursor cursor = query(FormFiles.TABLE + "." + FormFiles._ID + " = "
				+ localFileId, null, null);
		if (cursor.moveToNext()) {
			return cursor.getLong(Forms._ID_INDEX);
		}

		return null;
	}

	public synchronized void updateTemporaryFlag(long localFormId,
			boolean temporary) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Forms.TEMPORARY, String.valueOf(temporary));

		db.update(Forms.TABLE, values, Forms._ID + " = " + localFormId, null);
	}

	public synchronized void updateTreeDirtyFlags() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		String query = "SELECT " + Forms.TABLE + "." + Forms._ID + " FROM "
				+ Forms.TABLE + " LEFT JOIN " + Locations.TABLE + " ON "
				+ Forms.TABLE + "." + Forms._ID + " = " + Locations.TABLE + "."
				+ Locations.FOR_ID + " AND " + Locations.TABLE + "."
				+ Locations.PURPOSE + " = " + Locations.PURPOSE_FORM
				+ " WHERE " + Forms.TABLE + "." + Forms.REMOTE_ID
				+ " IS NULL OR " + Forms.TABLE + "." + Forms.DIRTY
				+ " = 'true' OR (" + Forms.TABLE + "." + Forms.REMOTE_ID
				+ " IS NULL AND " + Locations.TABLE + "."
				+ Locations.LOCATION_FINALIZED + " = 'false') UNION SELECT "
				+ FormFiles.LOCAL_FORM_ID + " FROM " + FormFiles.TABLE
				+ " WHERE " + FormFiles.LOCAL_MEDIA_PATH + " IS NOT NULL AND "
				+ FormFiles.MEDIA_ID + " IS NULL UNION SELECT "
				+ SectionFiles.LOCAL_FORM_ID + " FROM " + SectionFiles.TABLE
				+ " WHERE " + SectionFiles.LOCAL_MEDIA_PATH
				+ " IS NOT NULL AND " + SectionFiles.MEDIA_ID + " IS NULL";

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Update tree dirty query: " + query);
		}

		Cursor cursor = db.rawQuery(query, null);

		if (cursor.getCount() > 0) {
			List<Long> idList = new ArrayList<Long>(cursor.getCount());

			while (cursor.moveToNext()) {
				idList.add(cursor.getLong(0));
			}

			String ids = TextUtils.join(",", idList);
			String inQuery = "UPDATE " + Forms.TABLE + " SET "
					+ Forms.TREE_DIRTY + " = 'true' " + " WHERE " + Forms._ID
					+ " IN (" + ids + ")";
			String notInQuery = "UPDATE " + Forms.TABLE + " SET "
					+ Forms.TREE_DIRTY + " = 'false' " + " WHERE " + Forms._ID
					+ " NOT IN (" + ids + ")";

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In query: " + inQuery);
				Log.i(TAG, "Not In query: " + notInQuery);
			}

			db.execSQL(inQuery);
			db.execSQL(notInQuery);
		} else {
			db.execSQL("UPDATE " + Forms.TABLE + " SET " + Forms.TREE_DIRTY
					+ " = 'false'");
		}

		cursor.close();
	}

	public synchronized int deleteOldFormsThatAreNotRelatedWithAnyActivities() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		String today = SQLiteDateTimeUtils
				.getSQLiteDateTimeForBeginningOfToday();

		String query = "SELECT " + Forms.TABLE + "." + Forms._ID + " FROM "
				+ Forms.TABLE + " WHERE (" + Forms.TABLE + "." + Forms._ID
				+ " NOT IN (SELECT " + CompletedActivities.TABLE + "."
				+ CompletedActivities.CLIENT_FORM_ID + " FROM "
				+ CompletedActivities.TABLE + " WHERE "
				+ CompletedActivities.TABLE + "."
				+ CompletedActivities.CLIENT_FORM_ID + " IS NOT NULL) AND "
				+ Forms.TABLE + "." + Forms._ID + " NOT IN (SELECT "
				+ JobHistories.TABLE + "." + JobHistories.LOCAL_FORM_ID
				+ " FROM " + JobHistories.TABLE + " WHERE "
				+ JobHistories.TABLE + "." + JobHistories.LOCAL_FORM_ID
				+ " IS NOT NULL) AND " + Forms.TABLE + "." + Forms._ID
				+ " NOT IN (SELECT " + WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.LOCAL_FORM_ID + " FROM "
				+ WorkFlowStatus.TABLE + " WHERE " + WorkFlowStatus.TABLE + "."
				+ WorkFlowStatus.LOCAL_FORM_ID + " IS NOT NULL)) AND "
				+ Forms.TABLE + "." + Forms.DIRTY + " = 'false' AND "
				+ Forms.TABLE + "." + Forms.TREE_DIRTY + " = 'false' AND "
				+ Forms.TABLE + "." + Forms.LOCAL_MODIFICATION_TIME + " < '"
				+ today + "'";

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Query: " + query);
		}

		Cursor cursor = db.rawQuery(query, null);

		while (cursor.moveToNext()) {
			deleteForm(cursor.getLong(0));
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + affectedRows
					+ " old forms that are not related to any activity.");
		}

		cursor.close();
		return affectedRows;
	}
}

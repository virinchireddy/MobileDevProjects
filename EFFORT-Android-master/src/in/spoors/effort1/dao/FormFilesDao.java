package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dto.FormFile;
import in.spoors.effort1.dto.LocationDto;
import in.spoors.effort1.provider.EffortProvider.Fields;
import in.spoors.effort1.provider.EffortProvider.FormFiles;
import in.spoors.effort1.provider.EffortProvider.Forms;
import in.spoors.effort1.provider.EffortProvider.Locations;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

public class FormFilesDao {

	public static final String TAG = "FormFilesDao";
	private static FormFilesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static FormFilesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new FormFilesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private FormFilesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean fileWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + FormFiles._ID
					+ ") AS count FROM " + FormFiles.TABLE + " WHERE "
					+ FormFiles._ID + " = " + localId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(FormFile fileDto) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (fileDto.getFieldSpecId() != null
				&& TextUtils.isEmpty(fileDto.getFieldSpecUniqueId())) {
			FieldSpecsDao fieldSpecsDao = FieldSpecsDao
					.getInstance(applicationContext);

			fileDto.setFieldSpecUniqueId(fieldSpecsDao.getUniqueId(fileDto
					.getFieldSpecId()));
		}

		if (fileDto.getLocalId() != null
				&& fileWithLocalIdExists(fileDto.getLocalId())) {
			ContentValues values = fileDto.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the existing form file using local ID: "
						+ fileDto.toString());
			}

			db.update(FormFiles.TABLE, values,
					FormFiles._ID + " = " + fileDto.getLocalId(), null);

			return;
		}

		if (getFile(fileDto.getFieldSpecId(), fileDto.getLocalFormId()) != null) {
			ContentValues values = fileDto.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG,
						"Updating the existing form file: "
								+ fileDto.toString());
			}

			db.update(FormFiles.TABLE, values,
					FormFiles.FIELD_SPEC_ID + " = " + fileDto.getFieldSpecId()
							+ " AND " + FormFiles.LOCAL_FORM_ID + " = "
							+ fileDto.getLocalFormId(), null);

			return;
		}

		ContentValues values = fileDto.getContentValues(null);
		long insertionId = db.insert(FormFiles.TABLE, null, values);
		fileDto.setLocalId(insertionId);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saved a new form file: " + fileDto.toString());
		}
	}

	/**
	 * Persists only media ID, transfer percentage, dirty flag, and local
	 * modification time.
	 * 
	 * @param fileDto
	 */
	public synchronized void updateTransferStatus(FormFile fileDto) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Updating the existing note (for updating transfer percentage): "
							+ fileDto.toString());
		}

		ContentValues values = new ContentValues();
		Utils.putNullOrValue(values, FormFiles.TRANSFER_PERCENTAGE,
				fileDto.getTransferPercentage());
		Utils.putNullOrValue(values, FormFiles.MEDIA_ID, fileDto.getMediaId());

		db.update(FormFiles.TABLE, values,
				FormFiles._ID + " = " + fileDto.getLocalId(), null);
	}

	/**
	 * Delete file with the given local file ID It delete only the file record
	 * but not the file
	 * 
	 * @param localJobId
	 */
	public synchronized void deleteFile(long localFileId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(FormFiles.TABLE, FormFiles._ID + " = "
				+ localFileId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " file with local file id "
					+ localFileId);
		}
	}

	/**
	 * Return the file with given local file ID.
	 * 
	 * Null if the given FileDto is not found.
	 * 
	 * @param localId
	 * @return
	 */
	public FormFile getFileWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(FormFiles.TABLE, FormFiles.ALL_COLUMNS,
					FormFiles._ID + " = " + localId, null, null, null, null);

			if (cursor.moveToNext()) {
				FormFile fileDto = new FormFile();
				fileDto.load(cursor, applicationContext);
				return fileDto;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public FormFile getFile(long fieldSpecId, long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			String fieldSpecUniqueId = FieldSpecsDao.getInstance(
					applicationContext).getUniqueId(fieldSpecId);
			cursor = db.query(FormFiles.TABLE, FormFiles.ALL_COLUMNS,
					FormFiles.FIELD_SPEC_UNIQUE_ID + " = '" + fieldSpecUniqueId
							+ "' AND " + FormFiles.TABLE + "."
							+ FormFiles.LOCAL_FORM_ID + " = " + localFormId,
					null, null, null, null);

			if (cursor.moveToNext()) {
				FormFile fileDto = new FormFile();
				fileDto.load(cursor, applicationContext);
				return fileDto;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public List<FormFile> getFormFiles(long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<FormFile> formFiles = null;

		try {
			cursor = db.query(FormFiles.TABLE, FormFiles.ALL_COLUMNS,
					FormFiles.LOCAL_FORM_ID + " = " + localFormId, null, null,
					null, null);

			if (cursor.getCount() > 0) {
				formFiles = new ArrayList<FormFile>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				FormFile fileDto = new FormFile();
				fileDto.load(cursor, applicationContext);
				formFiles.add(fileDto);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return formFiles;
	}

	public Long getMediaId(long fieldSpecId, long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(FormFiles.TABLE,
					new String[] { FormFiles.MEDIA_ID },
					FormFiles.FIELD_SPEC_ID + " = " + fieldSpecId + " AND "
							+ FormFiles.TABLE + "." + FormFiles.LOCAL_FORM_ID
							+ " = " + localFormId, null, null, null, null);

			if (cursor.moveToNext()) {
				if (cursor.isNull(0)) {
					return null;
				} else {
					return cursor.getLong(0);
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public Long getMediaId(long localFileId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(FormFiles.TABLE,
					new String[] { FormFiles.MEDIA_ID }, FormFiles._ID + " = "
							+ localFileId, null, null, null, null);

			if (cursor.moveToNext()) {
				if (cursor.isNull(0)) {
					return null;
				} else {
					return cursor.getLong(0);
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	/**
	 * Checks whether there are any pending downloads.
	 * 
	 * @param applicationContext
	 *            Used for getting DBHelper instance.
	 * @param localJobId
	 *            If not-null searches only for notes attached to the given job
	 *            id
	 * @return
	 */
	public boolean hasPendingDownloads(Context applicationContext,
			Long localFormId) {
		Cursor cursor = null;

		try {
			cursor = queryPendingDownloads(localFormId);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Number of pending downloads for form "
						+ localFormId + ": " + cursor.getCount());
			}

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Checks whether there are any pending uploads.
	 * 
	 * @param applicationContext
	 *            Used for getting DBHelper instance.
	 * @param localFormId
	 *            If not-null searches only for given files id
	 * @return
	 */
	public boolean hasPendingUploads(Context applicationContext,
			Long localFormId, boolean includeLargeFiles) {
		Cursor cursor = null;

		try {
			cursor = queryPendingUploads(localFormId, includeLargeFiles);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Number of pending uploads for form " + localFormId
						+ ": " + cursor.getCount());
			}

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Checks whether there are any pending uploads or downloads.
	 * 
	 * @param applicationContext
	 *            Used for getting DBHelper instance.
	 * @param localFormId
	 *            If not-null searches only for given file ids
	 * 
	 * @return
	 */
	public boolean hasPendingTransfers(Context applicationContext,
			Long localFormId, boolean includeLargeFiles) {
		return hasPendingDownloads(applicationContext, localFormId)
				|| hasPendingUploads(applicationContext, localFormId,
						includeLargeFiles);
	}

	private Cursor queryWithForms(String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(FormFiles.TABLE + " JOIN " + Forms.TABLE + " ON "
				+ FormFiles.TABLE + "." + FormFiles.LOCAL_FORM_ID + " = "
				+ Forms.TABLE + "." + Forms._ID + " AND " + Forms.TABLE + "."
				+ Forms.TEMPORARY + " = 'false'");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "SELECT * FROM " + builder.getTables() + " WHERE "
					+ selection);
		}

		// qualify the columns by the table name, so that join
		// doesn't confuse the query engine with ambiguous column
		// names
		String[] columns = new String[Forms.ALL_COLUMNS.length];
		for (int i = 0; i < FormFiles.ALL_COLUMNS.length; ++i) {
			columns[i] = FormFiles.TABLE + "." + FormFiles.ALL_COLUMNS[i];
		}

		return builder.query(db, columns, selection, selectionArgs, null, null,
				sortOrder);
	}

	/**
	 * 
	 * @param localJobId
	 *            If not-null searches only for notes attached to the given job
	 *            id
	 * 
	 * @return
	 */
	private Cursor queryPendingDownloads(Long localFormId) {
		String form = "";

		if (localFormId != null) {
			form = " AND " + FormFiles.LOCAL_FORM_ID + " = " + localFormId;
		}

		return queryWithForms(FormFiles.LOCAL_MEDIA_PATH + " IS NULL AND "
				+ FormFiles.MEDIA_ID + " IS NOT NULL AND "
				+ FormFiles.DOWNLOAD_REQUESTED + " = 'true'" + form, null,
				FormFiles.TABLE + "." + FormFiles._ID);
	}

	public List<FormFile> getPendingDownloads() {
		Cursor cursor = null;
		List<FormFile> files = null;

		try {
			cursor = queryPendingDownloads(null);

			if (cursor.getCount() > 0) {
				files = new ArrayList<FormFile>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				FormFile fileDto = new FormFile();
				fileDto.load(cursor, applicationContext);
				files.add(fileDto);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return files;
	}

	public FormFile getNextPendingDownload() {
		List<FormFile> pendingDownloads = getPendingDownloads();

		if (pendingDownloads == null || pendingDownloads.size() <= 0) {
			return null;
		}

		if (pendingDownloads.size() > 0) {
			return pendingDownloads.get(0);
		}

		return null;
	}

	/**
	 * 
	 * @param localJobId
	 *            If not-null searches only for notes attached to the given job
	 *            id
	 * 
	 * @return
	 */
	private Cursor queryPendingUploads(Long localFormId,
			boolean includeLargeFiles) {

		String form = "";

		if (localFormId != null) {
			form = " AND " + Fields.LOCAL_FORM_ID + " = " + localFormId;
		}

		return queryWithForms(FormFiles.LOCAL_MEDIA_PATH
				+ " IS NOT NULL AND "
				+ FormFiles.MEDIA_ID
				+ " IS NULL"
				+ form
				+ (includeLargeFiles ? "" : " AND (" + FormFiles.FILE_SIZE
						+ " < 524288 OR " + FormFiles.UPLOAD_REQUESTED
						+ " = 'true')"), null, FormFiles.UPLOAD_PRIORITY
				+ " DESC, " + FormFiles.TABLE + "." + FormFiles._ID);
	}

	public List<FormFile> getPendingUploads(boolean includeLargeFiles) {
		Cursor cursor = null;
		List<FormFile> files = null;

		try {
			cursor = queryPendingUploads(null, includeLargeFiles);

			if (cursor.getCount() > 0) {
				files = new ArrayList<FormFile>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				FormFile fileDto = new FormFile();
				fileDto.load(cursor, applicationContext);
				files.add(fileDto);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return files;
	}

	public FormFile getNextPendingUpload(boolean includeLargeFiles) {
		List<FormFile> pendingUploads = getPendingUploads(includeLargeFiles);

		if (pendingUploads == null || pendingUploads.size() <= 0) {
			return null;
		}

		if (pendingUploads.size() > 0) {
			return pendingUploads.get(0);
		}

		return null;
	}

	public synchronized void cancelAllDownloads() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(FormFiles.DOWNLOAD_REQUESTED, "false");

		db.update(FormFiles.TABLE, values, null, null);
	}

	public synchronized void updateUploadPriority(long localFileId) {
		cancelAllDownloads();

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(FormFiles.UPLOAD_REQUESTED, "true");
		values.put(FormFiles.UPLOAD_PRIORITY, System.currentTimeMillis());

		db.update(FormFiles.TABLE, values, FormFiles._ID + " = " + localFileId,
				null);
	}

	/**
	 * Delete files of the given local form ID
	 * 
	 * @param localJobId
	 */
	public synchronized void deleteFormFiles(long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		// delete the files first
		List<FormFile> formFiles = getFormFiles(localFormId);

		if (formFiles != null) {
			for (FormFile formFile : formFiles) {
				if (!TextUtils.isEmpty(formFile.getLocalMediaPath())
						&& formFile.getLocalMediaPath().startsWith(
								Utils.getEffortPath())) {
					File file = new File(formFile.getLocalMediaPath());
					file.delete();
				}
			}
		}

		// delete the DTOs
		int affectedRows = db.delete(FormFiles.TABLE, FormFiles.LOCAL_FORM_ID
				+ " = " + localFormId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " files of form "
					+ localFormId);
		}
	}

	/**
	 * Delete file of the given field spec id and local form ID
	 * 
	 * @param localJobId
	 */
	public synchronized void deleteFormFile(long fieldSpecId, long localFormId,
			boolean deletePhysicalFileAlso) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		// delete the file first
		FormFile formFile = getFile(fieldSpecId, localFormId);

		if (formFile == null) {
			return;
		}

		if (deletePhysicalFileAlso) {
			if (!TextUtils.isEmpty(formFile.getLocalMediaPath())
					&& formFile.getLocalMediaPath().startsWith(
							Utils.getEffortPath())) {
				File file = new File(formFile.getLocalMediaPath());
				file.delete();
			}
		}

		// delete the DTOs
		int affectedRows = db.delete(FormFiles.TABLE, FormFiles.LOCAL_FORM_ID
				+ " = " + localFormId + " AND " + FormFiles.FIELD_SPEC_ID
				+ " = " + fieldSpecId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " file of form "
					+ localFormId + " and field spec " + fieldSpecId);
		}
	}

	public synchronized boolean isFileNeededForSync(String path) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(*) FROM " + FormFiles.TABLE
					+ " WHERE " + FormFiles.LOCAL_MEDIA_PATH + " = ? AND "
					+ FormFiles.MEDIA_ID + " IS NULL", new String[] { path });
			cursor.moveToNext();
			return cursor.getInt(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Get the form files that are added on the device, and need to be synced
	 * media id with locations.
	 * 
	 * @return
	 */
	public List<LocationDto> getLocationsForFormFiles() {
		Cursor cursor = null;
		List<LocationDto> locations = null;

		try {
			cursor = Utils.queryWithLocationsForFiles(FormFiles.TABLE + "."
					+ FormFiles.MEDIA_ID + " IS NOT NULL AND "
					+ Locations.TABLE + "." + Locations.LOCATION_FINALIZED
					+ " = 'true'", null, FormFiles.TABLE + "." + FormFiles._ID,
					applicationContext, Locations.PURPOSE_FORM_FILE);

			if (cursor.getCount() > 0) {
				locations = new ArrayList<LocationDto>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				LocationDto location = new LocationDto();
				location.load(cursor);
				locations.add(location);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return locations;
	}

	/**
	 * Return the file with given media ID.
	 * 
	 * Null if the given FileDto is not found.
	 * 
	 * @param mediaId
	 * @return
	 */
	public FormFile getFileWithMediaId(long mediaId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(FormFiles.TABLE, FormFiles.ALL_COLUMNS,
					FormFiles.MEDIA_ID + " = " + mediaId, null, null, null,
					null);

			if (cursor.moveToNext()) {
				FormFile fileDto = new FormFile();
				fileDto.load(cursor, applicationContext);
				return fileDto;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

}

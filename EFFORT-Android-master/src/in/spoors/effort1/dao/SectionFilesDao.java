package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dto.FormFile;
import in.spoors.effort1.dto.LocationDto;
import in.spoors.effort1.dto.SectionFile;
import in.spoors.effort1.provider.EffortProvider.Forms;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.SectionFields;
import in.spoors.effort1.provider.EffortProvider.SectionFiles;

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

public class SectionFilesDao {

	public static final String TAG = "SectionFilesDao";
	private static SectionFilesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SectionFilesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new SectionFilesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private SectionFilesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean fileWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + SectionFiles._ID
					+ ") AS count FROM " + SectionFiles.TABLE + " WHERE "
					+ SectionFiles._ID + " = " + localId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(SectionFile fileDto) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (fileDto.getFieldSpecId() != null
				&& TextUtils.isEmpty(fileDto.getFieldSpecUniqueId())) {
			SectionFieldSpecsDao fieldSpecsDao = SectionFieldSpecsDao
					.getInstance(applicationContext);

			fileDto.setFieldSpecUniqueId(fieldSpecsDao.getUniqueId(fileDto
					.getFieldSpecId()));
		}

		if (fileDto.getLocalId() != null
				&& fileWithLocalIdExists(fileDto.getLocalId())) {
			ContentValues values = fileDto.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG,
						"Updating the existing section file using local ID: "
								+ fileDto.toString());
			}

			db.update(SectionFiles.TABLE, values, SectionFiles._ID + " = "
					+ fileDto.getLocalId(), null);

			return;
		}

		if (getFile(fileDto.getFieldSpecId(), fileDto.getLocalFormId(),
				fileDto.getSectionInstanceId()) != null) {
			ContentValues values = fileDto.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG,
						"Updating the existing section file "
								+ fileDto.toString());
			}

			db.update(
					SectionFiles.TABLE,
					values,
					SectionFiles.FIELD_SPEC_ID + " = "
							+ fileDto.getFieldSpecId() + " AND "
							+ SectionFiles.LOCAL_FORM_ID + " = "
							+ fileDto.getLocalFormId() + " AND "
							+ SectionFiles.SECTION_INSTANCE_ID + " = "
							+ fileDto.getSectionInstanceId(), null);

			return;
		}

		ContentValues values = fileDto.getContentValues(null);
		long insertionId = db.insert(SectionFiles.TABLE, null, values);
		fileDto.setLocalId(insertionId);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saved a new section file: " + fileDto.toString());
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
		Utils.putNullOrValue(values, SectionFiles.TRANSFER_PERCENTAGE,
				fileDto.getTransferPercentage());
		Utils.putNullOrValue(values, SectionFiles.MEDIA_ID,
				fileDto.getMediaId());

		db.update(SectionFiles.TABLE, values, SectionFiles._ID + " = "
				+ fileDto.getLocalId(), null);
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

		int affectedRows = db.delete(SectionFiles.TABLE, SectionFiles._ID
				+ " = " + localFileId, null);

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
	public SectionFile getFileWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(SectionFiles.TABLE, SectionFiles.ALL_COLUMNS,
					SectionFiles._ID + " = " + localId, null, null, null, null);

			if (cursor.moveToNext()) {
				SectionFile fileDto = new SectionFile();
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

	public SectionFile getFile(long fieldSpecId, long localFormId,
			int sectionInstanceId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			String fieldSpecUniqueId = SectionFieldSpecsDao.getInstance(
					applicationContext).getUniqueId(fieldSpecId);

			cursor = db
					.query(SectionFiles.TABLE, SectionFiles.ALL_COLUMNS,
							SectionFiles.FIELD_SPEC_UNIQUE_ID + " = '"
									+ fieldSpecUniqueId + "' AND "
									+ SectionFiles.LOCAL_FORM_ID + " = "
									+ localFormId + " AND "
									+ SectionFiles.SECTION_INSTANCE_ID + " = "
									+ sectionInstanceId, null, null, null, null);

			if (cursor.moveToNext()) {
				SectionFile sectionFile = new SectionFile();
				sectionFile.load(cursor, applicationContext);
				return sectionFile;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public List<SectionFile> getSectionFiles(long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<SectionFile> sectionFiles = null;

		try {
			cursor = db.query(SectionFiles.TABLE, SectionFiles.ALL_COLUMNS,
					SectionFiles.LOCAL_FORM_ID + " = " + localFormId, null,
					null, null, null);

			if (cursor.getCount() > 0) {
				sectionFiles = new ArrayList<SectionFile>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				SectionFile fileDto = new SectionFile();
				fileDto.load(cursor, applicationContext);
				sectionFiles.add(fileDto);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return sectionFiles;
	}

	public Long getMediaId(long fieldSpecId, long localFormId,
			int sectionInstanceId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(SectionFiles.TABLE,
					new String[] { SectionFiles.MEDIA_ID },
					SectionFiles.FIELD_SPEC_ID + " = " + fieldSpecId + " AND "
							+ SectionFiles.TABLE + "."
							+ SectionFiles.LOCAL_FORM_ID + " = " + localFormId
							+ " AND " + SectionFiles.TABLE + "."
							+ SectionFiles.SECTION_INSTANCE_ID + " = "
							+ sectionInstanceId, null, null, null, null);

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
			cursor = db.query(SectionFiles.TABLE,
					new String[] { SectionFiles.MEDIA_ID }, SectionFiles._ID
							+ " = " + localFileId, null, null, null, null);

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
				Log.d(TAG, "Number of pending downloads for sections in form "
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
				Log.d(TAG, "Number of pending uploads for sections in form "
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

		builder.setTables(SectionFiles.TABLE + " JOIN " + Forms.TABLE + " ON "
				+ SectionFiles.TABLE + "." + SectionFiles.LOCAL_FORM_ID + " = "
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
		for (int i = 0; i < SectionFiles.ALL_COLUMNS.length; ++i) {
			columns[i] = SectionFiles.TABLE + "." + SectionFiles.ALL_COLUMNS[i];
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
			form = " AND " + SectionFiles.LOCAL_FORM_ID + " = " + localFormId;
		}

		return queryWithForms(SectionFiles.LOCAL_MEDIA_PATH + " IS NULL AND "
				+ SectionFiles.MEDIA_ID + " IS NOT NULL AND "
				+ SectionFiles.DOWNLOAD_REQUESTED + " = 'true'" + form, null,
				SectionFiles.TABLE + "." + SectionFiles._ID);
	}

	public List<SectionFile> getPendingDownloads() {
		Cursor cursor = null;
		List<SectionFile> files = null;

		try {
			cursor = queryPendingDownloads(null);

			if (cursor.getCount() > 0) {
				files = new ArrayList<SectionFile>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				SectionFile fileDto = new SectionFile();
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

	public SectionFile getNextPendingDownload() {
		List<SectionFile> pendingDownloads = getPendingDownloads();

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
			form = " AND " + SectionFields.LOCAL_FORM_ID + " = " + localFormId;
		}

		return queryWithForms(SectionFiles.LOCAL_MEDIA_PATH
				+ " IS NOT NULL AND "
				+ SectionFiles.MEDIA_ID
				+ " IS NULL"
				+ form
				+ (includeLargeFiles ? "" : " AND (" + SectionFiles.FILE_SIZE
						+ " < 524288 OR " + SectionFiles.UPLOAD_REQUESTED
						+ " = 'true')"), null, SectionFiles.UPLOAD_PRIORITY
				+ " DESC, " + SectionFiles.TABLE + "." + SectionFiles._ID);
	}

	public List<SectionFile> getPendingUploads(boolean includeLargeFiles) {
		Cursor cursor = null;
		List<SectionFile> files = null;

		try {
			cursor = queryPendingUploads(null, includeLargeFiles);

			if (cursor.getCount() > 0) {
				files = new ArrayList<SectionFile>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				SectionFile fileDto = new SectionFile();
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

	public SectionFile getNextPendingUpload(boolean includeLargeFiles) {
		List<SectionFile> pendingUploads = getPendingUploads(includeLargeFiles);

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
		values.put(SectionFiles.DOWNLOAD_REQUESTED, "false");

		db.update(SectionFiles.TABLE, values, null, null);
	}

	public synchronized void updateUploadPriority(long localFileId) {
		cancelAllDownloads();

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(SectionFiles.UPLOAD_REQUESTED, "true");
		values.put(SectionFiles.UPLOAD_PRIORITY, System.currentTimeMillis());

		db.update(SectionFiles.TABLE, values, SectionFiles._ID + " = "
				+ localFileId, null);
	}

	/**
	 * Delete files of the given local form ID
	 * 
	 * @param localJobId
	 */
	public synchronized void deleteSectionFiles(long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		// delete the files first
		List<SectionFile> sectionFiles = getSectionFiles(localFormId);

		if (sectionFiles != null) {
			for (SectionFile sectionFile : sectionFiles) {
				if (!TextUtils.isEmpty(sectionFile.getLocalMediaPath())
						&& sectionFile.getLocalMediaPath().startsWith(
								Utils.getEffortPath())) {
					File file = new File(sectionFile.getLocalMediaPath());
					file.delete();
				}
			}
		}

		// delete the DTOs
		int affectedRows = db.delete(SectionFiles.TABLE,
				SectionFiles.LOCAL_FORM_ID + " = " + localFormId, null);

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
	public synchronized void deleteSectionFile(long fieldSpecId,
			long localFormId, int sectionInstanceId,
			boolean deletePhysicalFileAlso) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		// delete the file first
		SectionFile sectionFile = getFile(fieldSpecId, localFormId,
				sectionInstanceId);

		if (sectionFile == null) {
			return;
		}

		if (deletePhysicalFileAlso) {
			if (!TextUtils.isEmpty(sectionFile.getLocalMediaPath())
					&& sectionFile.getLocalMediaPath().startsWith(
							Utils.getEffortPath())) {
				File file = new File(sectionFile.getLocalMediaPath());
				file.delete();
			}
		}

		// delete the DTOs
		int affectedRows = db.delete(SectionFiles.TABLE,
				SectionFiles.LOCAL_FORM_ID + " = " + localFormId + " AND "
						+ SectionFiles.FIELD_SPEC_ID + " = " + fieldSpecId,
				null);

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
			cursor = db
					.rawQuery("SELECT COUNT(*) FROM " + SectionFiles.TABLE
							+ " WHERE " + SectionFiles.LOCAL_MEDIA_PATH
							+ " = ? AND " + SectionFiles.MEDIA_ID + " IS NULL",
							new String[] { path });
			cursor.moveToNext();
			return cursor.getInt(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Get the section files that are added on the device, and need to be synced
	 * media id with locations.
	 * 
	 * @return
	 */
	public List<LocationDto> getLocationsForSectionFiles() {
		Cursor cursor = null;
		List<LocationDto> locations = null;

		try {
			cursor = Utils.queryWithLocationsForFiles(SectionFiles.TABLE + "."
					+ SectionFiles.MEDIA_ID + " IS NOT NULL AND "
					+ Locations.TABLE + "." + Locations.LOCATION_FINALIZED
					+ " = 'true'", null, SectionFiles.TABLE + "."
					+ SectionFiles._ID, applicationContext,
					Locations.PURPOSE_SECTION_FILE);

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
	public SectionFile getFileWithMediaId(long mediaId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(SectionFiles.TABLE, SectionFiles.ALL_COLUMNS,
					SectionFiles.MEDIA_ID + " = " + mediaId, null, null, null,
					null);

			if (cursor.moveToNext()) {
				SectionFile fileDto = new SectionFile();
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

package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dto.Note;
import in.spoors.effort1.provider.EffortProvider.Jobs;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.Notes;

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

public class NotesDao {

	public static final String TAG = "NotesDao";
	private static NotesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static NotesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new NotesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private NotesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean noteWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Notes._ID
					+ ") AS count FROM " + Notes.TABLE + " WHERE " + Notes._ID
					+ " = " + localId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean noteWithRemoteIdExists(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Notes.REMOTE_ID
					+ ") AS count FROM " + Notes.TABLE + " WHERE "
					+ Notes.REMOTE_ID + " = " + remoteId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Date getLocalCreationTimeWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Notes.LOCAL_CREATION_TIME
					+ " FROM " + Notes.TABLE + " WHERE " + Notes._ID + " = "
					+ localId, null);

			cursor.moveToNext();

			return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Date getLocalCreationTimeWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Notes.LOCAL_CREATION_TIME
					+ " FROM " + Notes.TABLE + " WHERE " + Notes.REMOTE_ID
					+ " = " + remoteId, null);

			cursor.moveToNext();

			return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean notesAreDifferent(Note note1, Note note2) {
		if (note1 == null && note2 == null) {
			return false;
		}

		if (note1 == null || note2 == null) {
			return true;
		}

		// both the notes are not null
		if (!Utils.integersEqual(note1.getState(), note2.getState())
				|| !Utils.integersEqual(note1.getNoteType(),
						note2.getNoteType())
				|| !TextUtils.equals(note1.getMimeType(), note2.getMimeType())
				|| !TextUtils.equals(note1.getNote(), note2.getNote())) {
			return true;
		}

		return false;
	}

	public synchronized void save(Note note) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (note.getLocalJobId() == null && note.getRemoteJobId() != null) {
			JobsDao jobsDao = JobsDao.getInstance(applicationContext);
			note.setLocalJobId(jobsDao.getLocalId(note.getRemoteJobId()));
		}

		if (note.getLocalId() != null
				&& noteWithLocalIdExists(note.getLocalId())) {
			Note oldNote = getNoteWithLocalId(note.getLocalId());

			if (note.getLocalCreationTime() == null) {
				note.setLocalCreationTime(oldNote.getLocalCreationTime());
			}

			if (notesAreDifferent(note, oldNote)) {
				note.setLocalModificationTime(new Date());
			}

			if (note.getRemoteId() == null && oldNote.getRemoteId() != null) {
				note.setRemoteId(oldNote.getRemoteId());
			}

			ContentValues values = note.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG,
						"Updating the existing note using local ID: "
								+ note.toString());
			}

			db.update(Notes.TABLE, values,
					Notes._ID + " = " + note.getLocalId(), null);

			return;
		}

		if (note.getRemoteId() != null
				&& noteWithRemoteIdExists(note.getRemoteId())) {
			Note oldNote = getNoteWithRemoteId(note.getRemoteId());

			if (note.getLocalCreationTime() == null) {
				note.setLocalCreationTime(oldNote.getLocalCreationTime());
			}

			if (notesAreDifferent(note, oldNote)) {
				note.setLocalModificationTime(new Date());
			}

			ContentValues values = note.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the existing note: " + note.toString());
			}

			db.update(Notes.TABLE, values,
					Notes.REMOTE_ID + " = " + note.getRemoteId(), null);

			return;
		}

		Date now = new Date();
		note.setLocalCreationTime(now);
		note.setLocalModificationTime(now);

		ContentValues values = note.getContentValues(null);
		long insertionId = db.insert(Notes.TABLE, null, values);
		note.setLocalId(insertionId);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saved a new note: " + note.toString());
		}
	}

	/**
	 * Persists only media ID, transfer percentage, dirty flag, and local
	 * modification time.
	 * 
	 * @param note
	 */
	public synchronized void updateTransferStatus(Note note) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Updating the existing note (for updating transfer percentage): "
							+ note.toString());
		}

		note.setLocalModificationTime(new Date());

		ContentValues values = new ContentValues();
		Utils.putNullOrValue(values, Notes.TRANSFER_PERCENTAGE,
				note.getTransferPercentage());
		Utils.putNullOrValue(values, Notes.MEDIA_ID, note.getMediaId());

		// save the dirty flag, only if it is not null
		if (note.getDirty() != null) {
			Utils.putNullOrValue(values, Notes.DIRTY, note.getDirty());
		}

		db.update(Notes.TABLE, values, Notes._ID + " = " + note.getLocalId(),
				null);
	}

	/**
	 * Delete note with the given local note ID
	 * 
	 * @param localJobId
	 */
	public synchronized void deleteNote(long localNoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		// delete locations first
		LocationsDao locationsDao = LocationsDao
				.getInstance(applicationContext);
		locationsDao.deleteNoteLocations(localNoteId);

		int affectedRows = db.delete(Notes.TABLE, Notes._ID + " = "
				+ localNoteId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " note with local note id "
					+ localNoteId);
		}
	}

	/**
	 * Delete notes of the given local job ID
	 * 
	 * @param localJobId
	 */
	public synchronized void deleteNotes(long localJobId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Notes.TABLE, new String[] { Notes._ID },
					Notes.LOCAL_JOB_ID + " = " + localJobId, null, null, null,
					null);

			while (cursor.moveToNext()) {
				deleteNote(cursor.getLong(0));
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Deleted " + cursor.getCount() + " notes of job "
						+ localJobId);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean jobHasNotes(long localJobId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Notes._ID
					+ ") AS count FROM " + Notes.TABLE + " WHERE "
					+ Notes.LOCAL_JOB_ID + " = " + localJobId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Return the note with given local note ID.
	 * 
	 * Null if the given note is not found.
	 * 
	 * @param localId
	 * @return
	 */
	public Note getNoteWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Notes.TABLE, Notes.ALL_COLUMNS, Notes._ID + " = "
					+ localId, null, null, null, null);

			if (cursor.moveToNext()) {
				Note note = new Note();
				note.load(cursor, applicationContext);
				return note;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	/**
	 * Return the note with given remote note ID.
	 * 
	 * Null if the given note is not found.
	 * 
	 * @param localId
	 * @return
	 */
	public Note getNoteWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Notes.TABLE, Notes.ALL_COLUMNS, Notes.REMOTE_ID
					+ " = " + remoteId, null, null, null, null);

			if (cursor.moveToNext()) {
				Note note = new Note();
				note.load(cursor, applicationContext);
				return note;
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
			Long localJobId) {
		Cursor cursor = null;

		try {
			cursor = queryPendingDownloads(localJobId);

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
	 * @param localJobId
	 *            If not-null searches only for notes attached to the given job
	 *            id
	 * @return
	 */
	public boolean hasPendingUploads(Context applicationContext,
			Long localJobId, boolean includeLargeFiles) {
		Cursor cursor = null;

		try {
			cursor = queryPendingUploads(localJobId, includeLargeFiles);

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
	 * @param localJobId
	 *            If not-null searches only for notes attached to the given job
	 *            id
	 * 
	 * @return
	 */
	public boolean hasPendingTransfers(Context applicationContext,
			Long localJobId, boolean includeLargeFiles) {
		return hasPendingDownloads(applicationContext, localJobId)
				|| hasPendingUploads(applicationContext, localJobId,
						includeLargeFiles);
	}

	private Cursor queryWith2WayJoin(String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(Notes.TABLE + " JOIN " + Jobs.TABLE + " ON "
				+ Notes.TABLE + "." + Notes.LOCAL_JOB_ID + " = " + Jobs.TABLE
				+ "." + Jobs._ID);

		// qualify the columns by the table name, so that join
		// doesn't result confuse the query engine with ambiguous column
		// names
		String[] columns = new String[Notes.ALL_COLUMNS.length];
		for (int i = 0; i < Notes.ALL_COLUMNS.length; ++i) {
			columns[i] = Notes.TABLE + "." + Notes.ALL_COLUMNS[i];
		}

		return builder.query(db, columns, selection, selectionArgs, null, null,
				sortOrder);
	}

	private Cursor queryWith3WayJoin(String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(Notes.TABLE + " LEFT JOIN " + Jobs.TABLE + " ON "
				+ Notes.TABLE + "." + Notes.LOCAL_JOB_ID + " = " + Jobs.TABLE
				+ "." + Jobs._ID + " LEFT JOIN " + Locations.TABLE + " ON "
				+ Locations.PURPOSE + " = " + Locations.PURPOSE_NOTE + " AND "
				+ Locations.FOR_ID + " = " + Notes.TABLE + "." + Notes._ID);

		// qualify the columns by the table name, so that join
		// doesn't confuse the query engine with ambiguous column
		// names
		String[] columns = new String[Notes.ALL_COLUMNS.length];
		for (int i = 0; i < Notes.ALL_COLUMNS.length; ++i) {
			columns[i] = Notes.TABLE + "." + Notes.ALL_COLUMNS[i];
		}

		return builder.query(db, columns, selection, selectionArgs, null, null,
				sortOrder);
	}

	/**
	 * Get the notes that were added on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<Note> getAddedNotes() {
		Cursor cursor = null;
		List<Note> notes = null;

		try {
			cursor = queryWith3WayJoin(Notes.TABLE + "." + Notes.REMOTE_ID
					+ " IS NULL AND " + Jobs.TEMPORARY + " = 'false' AND ("
					+ Locations.LOCATION_FINALIZED + " = 'true' OR "
					+ Locations.LOCATION_FINALIZED + " IS NULL)", null,
					Notes.TABLE + "." + Notes.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				notes = new ArrayList<Note>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Note note = new Note();
				note.load(cursor, applicationContext);
				notes.add(note);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return notes;
	}

	/**
	 * Get the notes that were modified on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<Note> getModifiedNotes() {
		Cursor cursor = null;
		List<Note> notes = null;

		try {
			cursor = queryWith3WayJoin(Notes.TABLE + "." + Notes.REMOTE_ID
					+ " IS NOT NULL AND " + Jobs.TEMPORARY + " = 'false' AND "
					+ Notes.TABLE + "." + Notes.DIRTY + " = 'true' AND ("
					+ Locations.LOCATION_FINALIZED + " = 'true' OR "
					+ Locations.LOCATION_FINALIZED + " IS NULL)", null,
					Notes.TABLE + "." + Notes.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				notes = new ArrayList<Note>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Note note = new Note();
				note.load(cursor, applicationContext);
				notes.add(note);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return notes;
	}

	/**
	 * 
	 * @param localJobId
	 *            If not-null searches only for notes attached to the given job
	 *            id
	 * 
	 * @return
	 */
	private Cursor queryPendingDownloads(Long localJobId) {
		return queryWith2WayJoin(Notes.LOCAL_MEDIA_PATH
				+ " IS NULL AND "
				+ Notes.MEDIA_ID
				+ " IS NOT NULL AND "
				+ Notes.NOTE_TYPE
				+ " != "
				+ Notes.NOTE_TYPE_NOTE
				+ " AND "
				+ Notes.DOWNLOAD_REQUESTED
				+ " = 'true' AND "
				+ Jobs.TEMPORARY
				+ " = 'false'"
				+ (localJobId == null ? "" : " AND " + Notes.LOCAL_JOB_ID
						+ " = " + localJobId), null, Notes.TABLE + "."
				+ Notes.LOCAL_CREATION_TIME);
	}

	public List<Note> getPendingDownloads() {
		Cursor cursor = null;
		List<Note> notes = null;

		try {
			cursor = queryPendingDownloads(null);

			if (cursor.getCount() > 0) {
				notes = new ArrayList<Note>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Note note = new Note();
				note.load(cursor, applicationContext);
				notes.add(note);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return notes;
	}

	public Note getNextPendingDownload() {
		List<Note> pendingDownloads = getPendingDownloads();

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
	private Cursor queryPendingUploads(Long localJobId,
			boolean includeLargeFiles) {
		return queryWith2WayJoin(Notes.LOCAL_MEDIA_PATH
				+ " IS NOT NULL AND "
				+ Notes.MEDIA_ID
				+ " IS NULL AND "
				+ Notes.NOTE_TYPE
				+ " != "
				+ Notes.NOTE_TYPE_NOTE
				+ " AND "
				+ Jobs.TEMPORARY
				+ " = 'false'"
				+ (localJobId == null ? "" : " AND " + Notes.LOCAL_JOB_ID
						+ " = " + localJobId)
				+ (includeLargeFiles ? "" : " AND (" + Notes.FILE_SIZE
						+ " < 524288 OR " + Notes.UPLOAD_REQUESTED
						+ " = 'true')"), null, Notes.UPLOAD_PRIORITY
				+ " DESC, " + Notes.TABLE + "." + Notes.LOCAL_CREATION_TIME);
	}

	public List<Note> getPendingUploads(boolean includeLargeFiles) {
		Cursor cursor = null;
		List<Note> notes = null;

		try {
			cursor = queryPendingUploads(null, includeLargeFiles);

			if (cursor.getCount() > 0) {
				notes = new ArrayList<Note>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Note note = new Note();
				note.load(cursor, applicationContext);
				notes.add(note);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return notes;
	}

	public Note getNextPendingUpload(boolean includeLargeFiles) {
		List<Note> pendingUploads = getPendingUploads(includeLargeFiles);

		if (pendingUploads == null || pendingUploads.size() <= 0) {
			return null;
		}

		if (pendingUploads.size() > 0) {
			return pendingUploads.get(0);
		}

		return null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasUnsyncedChanges() {
		Cursor cursor = null;

		try {
			cursor = queryWith3WayJoin(Jobs.TEMPORARY + " = 'false' AND ("
					+ Locations.LOCATION_FINALIZED + " = 'true' OR "
					+ Locations.LOCATION_FINALIZED + " IS NULL) AND ("
					+ Notes.TABLE + "." + Notes.REMOTE_ID + " IS NULL OR ("
					+ Notes.TABLE + "." + Notes.REMOTE_ID + " IS NULL AND "
					+ Notes.TABLE + "." + Notes.DIRTY + " = 'true'))", null,
					null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void updateRemoteId(long localNoteId, long remoteNoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Notes.REMOTE_ID, remoteNoteId);

		db.update(Notes.TABLE, values, Notes._ID + " = " + localNoteId, null);
	}

	/**
	 * Returns the remote note id of the given local note id.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public Long getRemoteId(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Notes.REMOTE_ID + " FROM "
					+ Notes.TABLE + " WHERE " + Notes._ID + " = " + localId,
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

	public synchronized void cancelAllDownloads() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Notes.DOWNLOAD_REQUESTED, "false");

		db.update(Notes.TABLE, values, null, null);
	}

	public synchronized void updateUploadPriority(long localNoteId) {
		cancelAllDownloads();

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Notes.UPLOAD_REQUESTED, "true");
		values.put(Notes.UPLOAD_PRIORITY, System.currentTimeMillis());

		db.update(Notes.TABLE, values, Notes._ID + " = " + localNoteId, null);
	}

	public synchronized boolean isFileNeededForSync(String path) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(*) FROM " + Notes.TABLE
					+ " WHERE " + Notes.LOCAL_MEDIA_PATH + " = ?  AND "
					+ Notes.MEDIA_ID + " IS NULL", new String[] { path });
			cursor.moveToNext();
			return cursor.getInt(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

}

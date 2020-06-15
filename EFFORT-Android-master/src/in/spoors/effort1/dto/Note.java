package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.dao.NotesDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.Notes;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Note implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "Note";
	public static final String JSON_NOTE = "comment";
	public static final String JSON_LOCAL_ID = "clientVisitCommentId";
	public static final String JSON_REMOTE_ID = "id";
	public static final String JSON_LOCAL_JOB_ID = "clientVisitId";
	public static final String JSON_REMOTE_JOB_ID = "visitId";
	public static final String JSON_STATE = "state";
	public static final String JSON_NOTE_TYPE = "commentType";
	public static final String JSON_MIME_TYPE = "type";
	public static final String JSON_MEDIA_ID = "mediaId";
	public static final String JSON_MEDIA_TYPE = "mediaType";
	public static final String JSON_TIME = "time";
	public static final String JSON_BY_ID = "byId";
	public static final String JSON_BY_NAME = "by";

	private Long localId;
	private Long remoteId;
	private String note;
	private Integer noteType;
	private String mimeType;
	private Long mediaId;
	private Integer mediaType;
	private String localMediaPath;
	private Long localJobId;

	/**
	 * Don't persist remote job id.
	 */
	private Long remoteJobId;

	private Integer state;
	private Date noteTime;
	private Long byId;
	private String byName;
	private Boolean downloadRequested;
	private Boolean uploadRequested;
	private Long uploadPriority;
	private Integer transferPercentage;
	private Long fileSize;
	private Boolean dirty;
	private Date localCreationTime;
	private Date localModificationTime;

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for accessing resource strings, and creating DAO
	 *            objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static Note parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		// Remote Note Id is mandatory. Thus, null check is not required.
		long remoteId = json.getLong(JSON_REMOTE_ID);
		NotesDao notesDao = NotesDao.getInstance(applicationContext);
		Note note = notesDao.getNoteWithRemoteId(remoteId);

		Long localId = Utils.getLong(json, JSON_LOCAL_ID);

		if (note == null && localId != null) {
			note = notesDao.getNoteWithLocalId(localId);
		}

		if (note == null) {
			note = new Note();
		}

		if (localId != null) {
			note.localId = localId;
		}

		note.remoteId = remoteId;
		note.dirty = false;

		note.note = Utils.getString(json, JSON_NOTE);
		note.noteType = Utils.getInteger(json, JSON_NOTE_TYPE);
		note.mimeType = Utils.getString(json, JSON_MIME_TYPE);

		// don't overwrite the media, if we already have it
		if (note.mediaId == null) {
			note.mediaId = Utils.getLong(json, JSON_MEDIA_ID);
		}

		note.mediaType = Utils.getInteger(json, JSON_MEDIA_TYPE);

		// Note: local media path is not exchanged with server
		note.localJobId = Utils.getLong(json, JSON_LOCAL_JOB_ID);
		note.remoteJobId = Utils.getLong(json, JSON_REMOTE_JOB_ID);
		note.state = Utils.getInteger(json, JSON_STATE);
		note.noteTime = Utils.getDate(json, JSON_TIME);
		note.byId = Utils.getLong(json, JSON_BY_ID);
		note.byName = Utils.getString(json, JSON_BY_NAME);

		// Note: dirty flag, local creation time, and local modification time
		// are not exchanged with server
		return note;
	}

	/**
	 * 
	 * @param values
	 *            fills this object, instead of creating a new one
	 * @return
	 */
	public ContentValues getContentValues(ContentValues values) {
		if (values == null) {
			values = new ContentValues();
		}

		// Database doesn't accept null values for auto increment columns.
		// As this method may be called for an update, skip the id column,
		// if it is null
		if (localId != null) {
			values.put(Notes._ID, localId);
		}

		Utils.putNullOrValue(values, Notes.REMOTE_ID, remoteId);
		Utils.putNullOrValue(values, Notes.NOTE, note);
		Utils.putNullOrValue(values, Notes.NOTE_TYPE, noteType);
		Utils.putNullOrValue(values, Notes.MIME_TYPE, mimeType);
		Utils.putNullOrValue(values, Notes.MEDIA_ID, mediaId);
		Utils.putNullOrValue(values, Notes.MEDIA_TYPE, mediaType);
		Utils.putNullOrValue(values, Notes.LOCAL_MEDIA_PATH, localMediaPath);
		Utils.putNullOrValue(values, Notes.LOCAL_JOB_ID, localJobId);
		Utils.putNullOrValue(values, Notes.STATE, state);
		Utils.putNullOrValue(values, Notes.NOTE_TIME, noteTime);
		Utils.putNullOrValue(values, Notes.BY_ID, byId);
		Utils.putNullOrValue(values, Notes.BY_NAME, byName);
		Utils.putNullOrValue(values, Notes.DOWNLOAD_REQUESTED,
				downloadRequested);
		Utils.putNullOrValue(values, Notes.UPLOAD_REQUESTED, uploadRequested);
		Utils.putNullOrValue(values, Notes.UPLOAD_PRIORITY, uploadPriority);
		Utils.putNullOrValue(values, Notes.TRANSFER_PERCENTAGE,
				transferPercentage);
		Utils.putNullOrValue(values, Notes.FILE_SIZE, fileSize);
		Utils.putNullOrValue(values, Notes.DIRTY, dirty);
		Utils.putNullOrValue(values, Notes.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, Notes.LOCAL_MODIFICATION_TIME,
				localModificationTime);

		return values;
	}

	public void load(Cursor cursor, Context applicationContext) {
		localId = cursor.isNull(Notes._ID_INDEX) ? null : cursor
				.getLong(Notes._ID_INDEX);
		remoteId = cursor.isNull(Notes.REMOTE_ID_INDEX) ? null : cursor
				.getLong(Notes.REMOTE_ID_INDEX);
		note = cursor.getString(Notes.NOTE_INDEX);
		noteType = cursor.isNull(Notes.NOTE_TYPE_INDEX) ? null : cursor
				.getInt(Notes.NOTE_TYPE_INDEX);
		mimeType = cursor.getString(Notes.MIME_TYPE_INDEX);
		mediaId = cursor.isNull(Notes.MEDIA_ID_INDEX) ? null : cursor
				.getLong(Notes.MEDIA_ID_INDEX);
		mediaType = cursor.isNull(Notes.MEDIA_TYPE_INDEX) ? null : cursor
				.getInt(Notes.MEDIA_TYPE_INDEX);
		localMediaPath = cursor.getString(Notes.LOCAL_MEDIA_PATH_INDEX);
		localJobId = cursor.isNull(Notes.LOCAL_JOB_ID_INDEX) ? null : cursor
				.getLong(Notes.LOCAL_JOB_ID_INDEX);

		JobsDao jobsDao = JobsDao.getInstance(applicationContext);

		remoteJobId = jobsDao.getRemoteId(localJobId);

		state = cursor.isNull(Notes.STATE_INDEX) ? null : cursor
				.getInt(Notes.STATE_INDEX);
		noteTime = cursor.isNull(Notes.NOTE_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Notes.NOTE_TIME_INDEX));
		byId = cursor.isNull(Notes.BY_ID_INDEX) ? null : cursor
				.getLong(Notes.BY_ID_INDEX);
		byName = cursor.getString(Notes.BY_NAME_INDEX);
		downloadRequested = cursor.isNull(Notes.DOWNLOAD_REQUESTED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Notes.DOWNLOAD_REQUESTED_INDEX));
		uploadRequested = cursor.isNull(Notes.UPLOAD_REQUESTED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Notes.UPLOAD_REQUESTED_INDEX));
		uploadPriority = cursor.isNull(Notes.UPLOAD_PRIORITY_INDEX) ? null
				: cursor.getLong(Notes.UPLOAD_PRIORITY_INDEX);
		transferPercentage = cursor.isNull(Notes.TRANSFER_PERCENTAGE_INDEX) ? null
				: cursor.getInt(Notes.TRANSFER_PERCENTAGE_INDEX);
		fileSize = cursor.isNull(Notes.FILE_SIZE_INDEX) ? null : cursor
				.getLong(Notes.FILE_SIZE_INDEX);
		dirty = cursor.isNull(Notes.DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Notes.DIRTY_INDEX));
		localCreationTime = cursor.isNull(Notes.LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Notes.LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(Notes.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Notes.LOCAL_MODIFICATION_TIME_INDEX));
	}

	@Override
	public String toString() {
		return "Note [localId=" + localId + ", remoteId=" + remoteId
				+ ", note=" + note + ", noteType=" + noteType + ", mimeType="
				+ mimeType + ", mediaId=" + mediaId + ", mediaType="
				+ mediaType + ", localMediaPath=" + localMediaPath
				+ ", localJobId=" + localJobId + ", remoteJobId=" + remoteJobId
				+ ", state=" + state + ", noteTime=" + noteTime + ", byId="
				+ byId + ", byName=" + byName + ", downloadRequested="
				+ downloadRequested + ", uploadRequested=" + uploadRequested
				+ ", uploadPriority=" + uploadPriority
				+ ", transferPercentage=" + transferPercentage + ", fileSize="
				+ fileSize + ", dirty=" + dirty + ", localCreationTime="
				+ localCreationTime + ", localModificationTime="
				+ localModificationTime + "]";
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
	}

	public Long getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(Long remoteId) {
		this.remoteId = remoteId;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getNoteType() {
		return noteType;
	}

	public void setNoteType(Integer noteType) {
		this.noteType = noteType;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Long getMediaId() {
		return mediaId;
	}

	public void setMediaId(Long mediaId) {
		this.mediaId = mediaId;
	}

	public Integer getMediaType() {
		return mediaType;
	}

	public void setMediaType(Integer mediaType) {
		this.mediaType = mediaType;
	}

	public String getLocalMediaPath() {
		return localMediaPath;
	}

	public void setLocalMediaPath(String localMediaPath) {
		this.localMediaPath = localMediaPath;
	}

	public Long getLocalJobId() {
		return localJobId;
	}

	public void setLocalJobId(Long localJobId) {
		this.localJobId = localJobId;
	}

	public Long getRemoteJobId() {
		return remoteJobId;
	}

	// Don't add the setter for remoteJobId

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getNoteTime() {
		return noteTime;
	}

	public void setNoteTime(Date noteTime) {
		this.noteTime = noteTime;
	}

	public Long getById() {
		return byId;
	}

	public void setById(Long byId) {
		this.byId = byId;
	}

	public String getByName() {
		return byName;
	}

	public void setByName(String byName) {
		this.byName = byName;
	}

	public Boolean getDownloadRequested() {
		return downloadRequested;
	}

	public void setDownloadRequested(Boolean downloadRequested) {
		this.downloadRequested = downloadRequested;
	}

	public Boolean getUploadRequested() {
		return uploadRequested;
	}

	public void setUploadRequested(Boolean uploadRequested) {
		this.uploadRequested = uploadRequested;
	}

	public Long getUploadPriority() {
		return uploadPriority;
	}

	public void setUploadPriority(Long uploadPriority) {
		this.uploadPriority = uploadPriority;
	}

	public Integer getTransferPercentage() {
		return transferPercentage;
	}

	public void setTransferPercentage(Integer transferPercentage) {
		this.transferPercentage = transferPercentage;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Boolean getDirty() {
		return dirty;
	}

	public void setDirty(Boolean dirty) {
		this.dirty = dirty;
	}

	public Date getLocalCreationTime() {
		return localCreationTime;
	}

	public void setLocalCreationTime(Date localCreationTime) {
		this.localCreationTime = localCreationTime;
	}

	public Date getLocalModificationTime() {
		return localModificationTime;
	}

	public void setLocalModificationTime(Date localModificationTime) {
		this.localModificationTime = localModificationTime;
	}

	/**
	 * Clears all the fields.
	 * 
	 * This allows the note object to be re-used, for performance reasons.
	 */
	public void clear() {
		localId = null;
		remoteId = null;
		note = null;
		noteType = null;
		mimeType = null;
		mediaId = null;
		mediaType = null;
		localMediaPath = null;
		localJobId = null;
		remoteJobId = null;
		state = null;
		noteTime = null;
		byId = null;
		byName = null;
		downloadRequested = null;
		uploadRequested = null;
		uploadPriority = null;
		transferPercentage = null;
		fileSize = null;
		dirty = null;
		localCreationTime = null;
		localModificationTime = null;
	}

	public boolean isVideo() {
		return mimeType != null && mimeType.startsWith("video/");
	}

	public boolean isImage() {
		return mimeType != null && mimeType.startsWith("image/");
	}

	public boolean isAudio() {
		return mimeType != null && mimeType.startsWith("audio/");
	}

	public boolean isTextNote() {
		return noteType != null && noteType == Notes.NOTE_TYPE_NOTE;
	}

	public boolean isProof() {
		return noteType != null && noteType == Notes.NOTE_TYPE_PROOF;
	}

	public boolean isExtra() {
		return noteType != null && noteType == Notes.NOTE_TYPE_EXTRA;
	}

	/**
	 * 
	 * @param context
	 *            This is required for reading SD card status.
	 * @return
	 */
	public String getActionString(Context context, boolean isTemporaryJob) {
		if (!Utils.isSDCardValid(context, false)) {
			return "SD card not mouted";
		}

		boolean mediaExists = false;

		if (localMediaPath != null) {
			File file = new File(localMediaPath);
			mediaExists = file.exists();
		}

		if (mediaExists) {
			String str = "";
			if (mediaId == null) {
				if (transferPercentage != null) {
					if (transferPercentage == 0) {
						str = "Upload started. ";
					} else {
						str = transferPercentage + "% uploaded. ";
					}
				} else {
					if (isTemporaryJob) {
						SettingsDao settingsDao = SettingsDao
								.getInstance(context.getApplicationContext());
						str = settingsDao.getLabel(
								Settings.LABEL_JOB_SINGULAR_KEY,
								Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE)
								+ " not saved yet. ";
					} else {
						str = "Waiting in upload queue. ";
					}
				}
			}

			if (isImage()) {
				return str + "Tap to view";
			} else if (isAudio() || isVideo()) {
				return str + "Tap to play";
			} else {
				return str + "Tap to open";
			}
		} else {
			if (mediaId != null) {
				if (downloadRequested != null && downloadRequested) {
					if (transferPercentage != null) {
						if (transferPercentage > 0) {
							return transferPercentage
									+ "% downloaded. Tap to cancel download.";
						} else {
							return "Waiting in download queue. Tap to cancel download";
						}
					} else {
						return "Waiting in download queue. Tap to cancel download";
					}
				} else {
					return "Tap to download";
				}
			}
		}

		return "Unknown action";
	}

	/**
	 * Get the JSON object that can be sent to the server.
	 * 
	 * @param applicationContext
	 * 
	 * @return
	 */
	public JSONObject getJsonObject(Context applicationContext) {
		JSONObject json = new JSONObject();

		try {
			if (remoteId == null) {
				json.put(JSON_LOCAL_ID, localId);

				LocationsDao locationsDao = LocationsDao
						.getInstance(applicationContext);
				LocationDto location = locationsDao.getNoteLocation(localId);

				if (location != null) {
					json.put("location", location.getJsonObject(
							applicationContext, Locations.PURPOSE_NOTE));
				}
			} else {
				json.put(JSON_REMOTE_ID, remoteId);
			}

			// if remote job id is present, don't send the local job
			// id to the server
			if (remoteJobId != null) {
				json.put(JSON_REMOTE_JOB_ID, remoteJobId);
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_LOCAL_JOB_ID, localJobId);
			}

			Utils.putValueOnlyIfNotNull(json, JSON_NOTE, note);
			Utils.putValueOnlyIfNotNull(json, JSON_NOTE_TYPE, noteType);
			Utils.putValueOnlyIfNotNull(json, JSON_MIME_TYPE, mimeType);
			Utils.putValueOnlyIfNotNull(json, JSON_MEDIA_ID, mediaId);
			Utils.putValueOnlyIfNotNull(json, JSON_MEDIA_TYPE, mediaType);
			Utils.putValueOnlyIfNotNull(json, JSON_STATE, state);
			Utils.putValueOnlyIfNotNull(json, JSON_TIME, noteTime);

			if (byId == null) {
				SettingsDao settingsDao = SettingsDao
						.getInstance(applicationContext);
				Utils.putValueOnlyIfNotNull(json, JSON_BY_ID,
						settingsDao.getLong(Settings.KEY_EMPLOYEE_ID));
				Utils.putValueOnlyIfNotNull(json, JSON_BY_NAME,
						settingsDao.getString(Settings.KEY_EMPLOYEE_NAME));
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_BY_ID, byId);
				Utils.putValueOnlyIfNotNull(json, JSON_BY_NAME, byName);
			}
		} catch (JSONException e) {
			Log.e(TAG, "Failed to compose JSON for note: " + e.toString(), e);
			return null;
		}

		return json;
	}

}

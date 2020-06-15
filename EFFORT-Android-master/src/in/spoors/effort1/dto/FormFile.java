package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.FormFiles;

import java.io.File;
import java.io.Serializable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class FormFile implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "FormFile";
	protected Long localId;
	protected Long fieldSpecId;
	protected Long localFormId;
	protected String mimeType;
	protected Long mediaId;
	protected String localMediaPath;
	protected Boolean downloadRequested;
	protected Boolean uploadRequested;
	protected Long uploadPriority;
	protected Integer transferPercentage;
	protected Long fileSize;
	protected String fieldSpecUniqueId;
	// Used for saving location
	protected boolean isNewMedia;

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
			values.put(FormFiles._ID, localId);
		}

		Utils.putNullOrValue(values, FormFiles.FIELD_SPEC_ID, fieldSpecId);
		Utils.putNullOrValue(values, FormFiles.LOCAL_FORM_ID, localFormId);
		Utils.putNullOrValue(values, FormFiles.MIME_TYPE, mimeType);
		Utils.putNullOrValue(values, FormFiles.MEDIA_ID, mediaId);
		Utils.putNullOrValue(values, FormFiles.LOCAL_MEDIA_PATH, localMediaPath);
		Utils.putNullOrValue(values, FormFiles.DOWNLOAD_REQUESTED,
				downloadRequested);
		Utils.putNullOrValue(values, FormFiles.UPLOAD_REQUESTED,
				uploadRequested);
		Utils.putNullOrValue(values, FormFiles.UPLOAD_PRIORITY, uploadPriority);
		Utils.putNullOrValue(values, FormFiles.TRANSFER_PERCENTAGE,
				transferPercentage);
		Utils.putNullOrValue(values, FormFiles.FILE_SIZE, fileSize);
		Utils.putNullOrValue(values, FormFiles.FIELD_SPEC_UNIQUE_ID,
				fieldSpecUniqueId);

		return values;
	}

	public void load(Cursor cursor, Context applicationContext) {
		localId = cursor.isNull(FormFiles._ID_INDEX) ? null : cursor
				.getLong(FormFiles._ID_INDEX);
		fieldSpecId = cursor.isNull(FormFiles.FIELD_SPEC_ID_INDEX) ? null
				: cursor.getLong(FormFiles.FIELD_SPEC_ID_INDEX);
		localFormId = cursor.isNull(FormFiles.LOCAL_FORM_ID_INDEX) ? null
				: cursor.getLong(FormFiles.LOCAL_FORM_ID_INDEX);
		mimeType = cursor.getString(FormFiles.MIME_TYPE_INDEX);
		mediaId = cursor.isNull(FormFiles.MEDIA_ID_INDEX) ? null : cursor
				.getLong(FormFiles.MEDIA_ID_INDEX);
		localMediaPath = cursor.getString(FormFiles.LOCAL_MEDIA_PATH_INDEX);
		downloadRequested = cursor.isNull(FormFiles.DOWNLOAD_REQUESTED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(FormFiles.DOWNLOAD_REQUESTED_INDEX));
		uploadRequested = cursor.isNull(FormFiles.UPLOAD_REQUESTED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(FormFiles.UPLOAD_REQUESTED_INDEX));
		uploadPriority = cursor.isNull(FormFiles.UPLOAD_PRIORITY_INDEX) ? null
				: cursor.getLong(FormFiles.UPLOAD_PRIORITY_INDEX);
		transferPercentage = cursor.isNull(FormFiles.TRANSFER_PERCENTAGE_INDEX) ? null
				: cursor.getInt(FormFiles.TRANSFER_PERCENTAGE_INDEX);
		fileSize = cursor.isNull(FormFiles.FILE_SIZE_INDEX) ? null : cursor
				.getLong(FormFiles.FILE_SIZE_INDEX);
		fieldSpecUniqueId = cursor
				.getString(FormFiles.FIELD_SPEC_UNIQUE_ID_INDEX);
	}

	@Override
	public String toString() {
		return "FormFile [localId=" + localId + ", fieldSpecId=" + fieldSpecId
				+ ", localFormId=" + localFormId + ", mimeType=" + mimeType
				+ ", mediaId=" + mediaId + ", localMediaPath=" + localMediaPath
				+ ", downloadRequested=" + downloadRequested
				+ ", uploadRequested=" + uploadRequested + ", uploadPriority="
				+ uploadPriority + ", transferPercentage=" + transferPercentage
				+ ", fileSize=" + fileSize + ", fieldSpecUniqueId="
				+ fieldSpecUniqueId + ", isNewMedia=" + isNewMedia + "]";
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
	}

	public Long getFieldSpecId() {
		return fieldSpecId;
	}

	public void setFieldSpecId(Long fieldSpecId) {
		this.fieldSpecId = fieldSpecId;
	}

	public Long getLocalFormId() {
		return localFormId;
	}

	public void setLocalFormId(Long localFormId) {
		this.localFormId = localFormId;
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

	public String getLocalMediaPath() {
		return localMediaPath;
	}

	public void setLocalMediaPath(String localMediaPath) {
		this.localMediaPath = localMediaPath;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static String getTag() {
		return TAG;
	}

	public String getFieldSpecUniqueId() {
		return fieldSpecUniqueId;
	}

	public void setFieldSpecUniqueId(String fieldSpecUniqueId) {
		this.fieldSpecUniqueId = fieldSpecUniqueId;
	}

	public boolean isNewMedia() {
		return isNewMedia;
	}

	public void setNewMedia(boolean isNewMedia) {
		this.isNewMedia = isNewMedia;
	}

	/**
	 * 
	 * @param context
	 *            This is required for reading SD card status.
	 * @return
	 */
	public String getActionString(Context context) {
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
					str = "Waiting in upload queue. ";
				}
			}
			if (str.length() > 0) {
				return str;
			} else {
				return "Tap to view";
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

}

package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.SectionFiles;

import java.io.File;
import java.io.Serializable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SectionFile extends FormFile implements Serializable {

	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private static final String TAG = "SectionFile";
	private Integer sectionInstanceId;

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
			values.put(SectionFiles._ID, localId);
		}

		Utils.putNullOrValue(values, SectionFiles.FIELD_SPEC_ID, fieldSpecId);
		Utils.putNullOrValue(values, SectionFiles.LOCAL_FORM_ID, localFormId);
		Utils.putNullOrValue(values, SectionFiles.SECTION_INSTANCE_ID,
				sectionInstanceId);
		Utils.putNullOrValue(values, SectionFiles.MIME_TYPE, mimeType);
		Utils.putNullOrValue(values, SectionFiles.MEDIA_ID, mediaId);
		Utils.putNullOrValue(values, SectionFiles.LOCAL_MEDIA_PATH,
				localMediaPath);
		Utils.putNullOrValue(values, SectionFiles.DOWNLOAD_REQUESTED,
				downloadRequested);
		Utils.putNullOrValue(values, SectionFiles.UPLOAD_REQUESTED,
				uploadRequested);
		Utils.putNullOrValue(values, SectionFiles.UPLOAD_PRIORITY,
				uploadPriority);
		Utils.putNullOrValue(values, SectionFiles.TRANSFER_PERCENTAGE,
				transferPercentage);
		Utils.putNullOrValue(values, SectionFiles.FILE_SIZE, fileSize);
		Utils.putNullOrValue(values, SectionFiles.FIELD_SPEC_UNIQUE_ID,
				fieldSpecUniqueId);

		return values;
	}

	public void load(Cursor cursor, Context applicationContext) {
		localId = cursor.isNull(SectionFiles._ID_INDEX) ? null : cursor
				.getLong(SectionFiles._ID_INDEX);
		fieldSpecId = cursor.isNull(SectionFiles.FIELD_SPEC_ID_INDEX) ? null
				: cursor.getLong(SectionFiles.FIELD_SPEC_ID_INDEX);
		localFormId = cursor.isNull(SectionFiles.LOCAL_FORM_ID_INDEX) ? null
				: cursor.getLong(SectionFiles.LOCAL_FORM_ID_INDEX);
		sectionInstanceId = cursor
				.isNull(SectionFiles.SECTION_INSTANCE_ID_INDEX) ? null : cursor
				.getInt(SectionFiles.SECTION_INSTANCE_ID_INDEX);
		mimeType = cursor.getString(SectionFiles.MIME_TYPE_INDEX);
		mediaId = cursor.isNull(SectionFiles.MEDIA_ID_INDEX) ? null : cursor
				.getLong(SectionFiles.MEDIA_ID_INDEX);
		localMediaPath = cursor.getString(SectionFiles.LOCAL_MEDIA_PATH_INDEX);
		downloadRequested = cursor
				.isNull(SectionFiles.DOWNLOAD_REQUESTED_INDEX) ? null : Boolean
				.parseBoolean(cursor
						.getString(SectionFiles.DOWNLOAD_REQUESTED_INDEX));
		uploadRequested = cursor.isNull(SectionFiles.UPLOAD_REQUESTED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(SectionFiles.UPLOAD_REQUESTED_INDEX));
		uploadPriority = cursor.isNull(SectionFiles.UPLOAD_PRIORITY_INDEX) ? null
				: cursor.getLong(SectionFiles.UPLOAD_PRIORITY_INDEX);
		transferPercentage = cursor
				.isNull(SectionFiles.TRANSFER_PERCENTAGE_INDEX) ? null : cursor
				.getInt(SectionFiles.TRANSFER_PERCENTAGE_INDEX);
		fileSize = cursor.isNull(SectionFiles.FILE_SIZE_INDEX) ? null : cursor
				.getLong(SectionFiles.FILE_SIZE_INDEX);
		fieldSpecUniqueId = cursor
				.getString(SectionFiles.FIELD_SPEC_UNIQUE_ID_INDEX);
	}

	@Override
	public String toString() {
		return "SectionFile [localId=" + localId + ", fieldSpecId="
				+ fieldSpecId + ", localFormId=" + localFormId
				+ ", sectionInstanceId=" + sectionInstanceId + ", mimeType="
				+ mimeType + ", mediaId=" + mediaId + ", localMediaPath="
				+ localMediaPath + ", downloadRequested=" + downloadRequested
				+ ", uploadRequested=" + uploadRequested + ", uploadPriority="
				+ uploadPriority + ", transferPercentage=" + transferPercentage
				+ ", fileSize=" + fileSize + ", fieldSpecUniqueId="
				+ fieldSpecUniqueId + "]";
	}

	public Integer getSectionInstanceId() {
		return sectionInstanceId;
	}

	public void setSectionInstanceId(Integer sectionInstanceId) {
		this.sectionInstanceId = sectionInstanceId;
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
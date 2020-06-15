package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.ArticlesDao;
import in.spoors.effort1.provider.EffortProvider.Articles;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Article implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "Article";

	private Long id;
	private String title;
	private String description;
	private Long mediaId;
	private String mimeType;
	private String localMediaPath;
	private Boolean downloadRequested;
	private Integer transferPercentage;
	private Long fileSize;
	private String createdByName;
	private String modifiedByName;
	private Date remoteCreationTime;
	private Date remoteModificationTime;
	private Boolean gotViaSearch;

	// Added for Articles enhancement
	private Long fileType;
	private String parentPath;
	private Long parentId;
	private Boolean isSecured;
	private Boolean deleted;

	// Added for notification
	private Date localCreationTime;
	private Date localModificationTime;

	public static final String JSON_ID = "articleId";
	public static final String JSON_TITLE = "title";
	public static final String JSON_DESCRIPTION = "description";
	public static final String JSON_MEDIA_ID = "mediaId";
	public static final String JSON_MIME_TYPE = "mimeType";
	public static final String JSON_CREATED_BY_NAME = "createdByName";
	public static final String JSON_MODIFIED_BY_NAME = "modifiedByName";
	public static final String JSON_REMOTE_CREATION_TIME = "createdTime";
	public static final String JSON_REMOTE_MODIFICATION_TIME = "modifiedTime";

	public static final String JSON_FILE_TYPE = "fileType";
	public static final String JSON_PARENT_PATH = "parentPath";
	public static final String JSON_PARENT_ID = "parentId";
	public static final String JSON_IS_SECURED = "secure";
	public static final String JSON_DELETED = "deleted";

	public static final int TYPE_FILE = 0;
	public static final int TYPE_FOLDER = 1;

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static Article parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		Article article = new Article();

		article.id = Utils.getLong(json, JSON_ID);
		article.mediaId = Utils.getLong(json, JSON_MEDIA_ID);

		if (article.id != null) {
			ArticlesDao articlesDao = ArticlesDao
					.getInstance(applicationContext);
			Article oldArticle = articlesDao.getArticleWithId(article.id);

			if (oldArticle != null
					&& Utils.longsEqual(article.mediaId,
							oldArticle.getMediaId())) {
				article.setLocalMediaPath(oldArticle.getLocalMediaPath());
				article.setTransferPercentage(oldArticle
						.getTransferPercentage());
				article.setDownloadRequested(oldArticle.getDownloadRequested());
			}
		}

		article.title = Utils.getString(json, JSON_TITLE);
		article.description = Utils.getString(json, JSON_DESCRIPTION);
		article.mimeType = Utils.getString(json, JSON_MIME_TYPE);
		article.createdByName = Utils.getString(json, JSON_CREATED_BY_NAME);
		article.modifiedByName = Utils.getString(json, JSON_MODIFIED_BY_NAME);
		article.remoteCreationTime = Utils.getDate(json,
				JSON_REMOTE_CREATION_TIME);
		article.remoteModificationTime = Utils.getDate(json,
				JSON_REMOTE_MODIFICATION_TIME);
		// Added for Enhancement
		article.fileType = Utils.getLong(json, JSON_FILE_TYPE);
		article.parentPath = Utils.getString(json, JSON_PARENT_PATH);
		article.parentId = Utils.getLong(json, JSON_PARENT_ID);
		article.isSecured = Utils.getBoolean(json, JSON_IS_SECURED);
		article.deleted = Utils.getBoolean(json, JSON_DELETED);
		return article;
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
		if (id != null) {
			values.put(Articles._ID, id);
		}

		Utils.putNullOrValue(values, Articles.TITLE, title);
		Utils.putNullOrValue(values, Articles.DESCRIPTION, description);
		Utils.putNullOrValue(values, Articles.MEDIA_ID, mediaId);
		Utils.putNullOrValue(values, Articles.MIME_TYPE, mimeType);
		Utils.putNullOrValue(values, Articles.LOCAL_MEDIA_PATH, localMediaPath);
		Utils.putNullOrValue(values, Articles.DOWNLOAD_REQUESTED,
				downloadRequested);
		Utils.putNullOrValue(values, Articles.TRANSFER_PERCENTAGE,
				transferPercentage);
		Utils.putNullOrValue(values, Articles.FILE_SIZE, fileSize);
		Utils.putNullOrValue(values, Articles.CREATED_BY_NAME, createdByName);
		Utils.putNullOrValue(values, Articles.MODIFIED_BY_NAME, modifiedByName);
		Utils.putNullOrValue(values, Articles.REMOTE_CREATION_TIME,
				remoteCreationTime);
		Utils.putNullOrValue(values, Articles.REMOTE_MODIFICATION_TIME,
				remoteModificationTime);
		Utils.putNullOrValue(values, Articles.GOT_VIA_SEARCH, gotViaSearch);
		// Added for Enhancement
		Utils.putNullOrValue(values, Articles.FILE_TYPE, fileType);
		Utils.putNullOrValue(values, Articles.PARENT_PATH, parentPath);
		Utils.putNullOrValue(values, Articles.PARENT_ID, parentId);
		Utils.putNullOrValue(values, Articles.IS_SECURED, isSecured);
		Utils.putNullOrValue(values, Articles.DELETED, deleted);
		Utils.putNullOrValue(values, Articles.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, Articles.LOCAL_MODIFICATION_TIME,
				localModificationTime);
		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.isNull(Articles._ID_INDEX) ? null : cursor
				.getLong(Articles._ID_INDEX);
		title = cursor.getString(Articles.TITLE_INDEX);
		description = cursor.getString(Articles.DESCRIPTION_INDEX);
		mediaId = cursor.isNull(Articles.MEDIA_ID_INDEX) ? null : cursor
				.getLong(Articles.MEDIA_ID_INDEX);
		mimeType = cursor.getString(Articles.MIME_TYPE_INDEX);
		localMediaPath = cursor.getString(Articles.LOCAL_MEDIA_PATH_INDEX);
		downloadRequested = cursor.isNull(Articles.DOWNLOAD_REQUESTED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Articles.DOWNLOAD_REQUESTED_INDEX));
		transferPercentage = cursor.isNull(Articles.TRANSFER_PERCENTAGE_INDEX) ? null
				: cursor.getInt(Articles.TRANSFER_PERCENTAGE_INDEX);
		fileSize = cursor.isNull(Articles.FILE_SIZE_INDEX) ? null : cursor
				.getLong(Articles.FILE_SIZE_INDEX);
		createdByName = cursor.getString(Articles.CREATED_BY_NAME_INDEX);
		modifiedByName = cursor.getString(Articles.MODIFIED_BY_NAME_INDEX);
		remoteCreationTime = cursor.isNull(Articles.REMOTE_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Articles.REMOTE_CREATION_TIME_INDEX));
		remoteModificationTime = cursor
				.isNull(Articles.REMOTE_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Articles.REMOTE_MODIFICATION_TIME_INDEX));
		gotViaSearch = cursor.isNull(Articles.GOT_VIA_SEARCH_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Articles.GOT_VIA_SEARCH_INDEX));
		// Added for Enhancement
		parentPath = cursor.getString(Articles.PARENT_PATH_INDEX);
		fileType = cursor.isNull(Articles.FILE_TYPE_INDEX) ? null : cursor
				.getLong(Articles.FILE_TYPE_INDEX);
		parentId = cursor.isNull(Articles.PARENT_ID_INDEX) ? null : cursor
				.getLong(Articles.PARENT_ID_INDEX);
		isSecured = cursor.isNull(Articles.IS_SECURED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Articles.IS_SECURED_INDEX));
		deleted = cursor.isNull(Articles.DELETED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Articles.DELETED_INDEX));
		localCreationTime = cursor.isNull(Articles.LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Articles.LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(Articles.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Articles.LOCAL_MODIFICATION_TIME_INDEX));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getMediaId() {
		return mediaId;
	}

	public void setMediaId(Long mediaId) {
		this.mediaId = mediaId;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
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

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public String getModifiedByName() {
		return modifiedByName;
	}

	public void setModifiedByName(String modifiedByName) {
		this.modifiedByName = modifiedByName;
	}

	public Date getRemoteCreationTime() {
		return remoteCreationTime;
	}

	public void setRemoteCreationTime(Date remoteCreationTime) {
		this.remoteCreationTime = remoteCreationTime;
	}

	public Date getRemoteModificationTime() {
		return remoteModificationTime;
	}

	public void setRemoteModificationTime(Date remoteModificationTime) {
		this.remoteModificationTime = remoteModificationTime;
	}

	public Boolean getGotViaSearch() {
		return gotViaSearch;
	}

	public void setGotViaSearch(Boolean gotViaSearch) {
		this.gotViaSearch = gotViaSearch;
	}

	public Long getFileType() {
		return fileType;
	}

	public void setFileType(Long fileType) {
		this.fileType = fileType;
	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Boolean getIsSecured() {
		return isSecured;
	}

	public void setIsSecured(Boolean isSecured) {
		this.isSecured = isSecured;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
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

	@Override
	public String toString() {
		return "Article [id=" + id + ", title=" + title + ", description="
				+ description + ", mediaId=" + mediaId + ", mimeType="
				+ mimeType + ", localMediaPath=" + localMediaPath
				+ ", downloadRequested=" + downloadRequested
				+ ", transferPercentage=" + transferPercentage + ", fileSize="
				+ fileSize + ", createdByName=" + createdByName
				+ ", modifiedByName=" + modifiedByName
				+ ", remoteCreationTime=" + remoteCreationTime
				+ ", remoteModificationTime=" + remoteModificationTime
				+ ", gotViaSearch=" + gotViaSearch + ", fileType=" + fileType
				+ ", parentPath=" + parentPath + ", parentId=" + parentId
				+ ", isSecured=" + isSecured + ", deleted=" + deleted
				+ ", localCreationTime=" + localCreationTime
				+ ", localModificationTime=" + localModificationTime + "]";
	}

}

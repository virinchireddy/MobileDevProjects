package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.Messages;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Message implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "Message";

	private Long id;
	private String subject;
	private String body;
	private Date remoteCreationTime;
	private Date localCreationTime;
	private Date localModificationTime;
	private Integer qualityType;
	public static final String JSON_QUALITY_TYPE = "qualityType";
	public static final String JSON_ID = "id";
	public static final String JSON_SUBJECT = "subject";
	public static final String JSON_BODY = "body";
	public static final String JSON_REMOTE_CREATION_TIME = "createdTime";

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static Message parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		Message message = new Message();

		message.id = Utils.getLong(json, JSON_ID);
		message.subject = Utils.getString(json, JSON_SUBJECT);
		message.body = Utils.getString(json, JSON_BODY);
		message.remoteCreationTime = Utils.getDate(json,
				JSON_REMOTE_CREATION_TIME);
		message.qualityType = Utils.getInteger(json, JSON_QUALITY_TYPE);
		return message;
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
			values.put(Messages._ID, id);
		}

		Utils.putNullOrValue(values, Messages.SUBJECT, subject);
		Utils.putNullOrValue(values, Messages.BODY, body);
		Utils.putNullOrValue(values, Messages.REMOTE_CREATION_TIME,
				remoteCreationTime);
		Utils.putNullOrValue(values, Messages.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, Messages.LOCAL_MODIFICATION_TIME,
				localModificationTime);
		Utils.putNullOrValue(values, Messages.QUALITY_TYPE, qualityType);
		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.isNull(Messages._ID_INDEX) ? null : cursor
				.getLong(Messages._ID_INDEX);
		subject = cursor.getString(Messages.SUBJECT_INDEX);
		body = cursor.getString(Messages.BODY_INDEX);
		remoteCreationTime = cursor.isNull(Messages.REMOTE_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Messages.REMOTE_CREATION_TIME_INDEX));
		localCreationTime = cursor.isNull(Messages.LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Messages.LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(Messages.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Messages.LOCAL_MODIFICATION_TIME_INDEX));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Date getRemoteCreationTime() {
		return remoteCreationTime;
	}

	public void setRemoteCreationTime(Date remoteCreationTime) {
		this.remoteCreationTime = remoteCreationTime;
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
		return "Message [id=" + id + ", subject=" + subject + ", body=" + body
				+ ", remoteCreationTime=" + remoteCreationTime
				+ ", localCreationTime=" + localCreationTime
				+ ", localModificationTime=" + localModificationTime + "]";
	}

}

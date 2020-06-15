package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.ActivitySpecs;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ActivitySpec implements Serializable {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "ActivitySpec";
	private Long id;
	private String name;
	private Long formSpecId;
	private Boolean deleted;

	public static final String JSON_ACTIVITY_ID = "activityId";
	public static final String JSON_ACTIVITY_NAME = "activityName";
	public static final String JSON_FORM_SPEC_ID = "formSpecId";
	public static final String JSON_IS_DELETED = "isDeleted";

	public static final int STATUS_CREATED = 0;
	public static final int STATUS_APPROVED = 1;
	public static final int STATUS_REJECTED = 2;
	public static final int STATUS_DELETED = 3;

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static ActivitySpec parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		ActivitySpec activity = new ActivitySpec();

		activity.id = Utils.getLong(json, JSON_ACTIVITY_ID);
		activity.name = Utils.getString(json, JSON_ACTIVITY_NAME);
		activity.formSpecId = Utils.getLong(json, JSON_FORM_SPEC_ID);
		Long isDeleted = Utils.getLong(json, JSON_IS_DELETED);
		activity.deleted = (isDeleted != null && (isDeleted == 3 || isDeleted == 1)) ? true
				: false;
		return activity;
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
		// if (id != null) {
		// values.put(Activities._ID, id);
		// }

		Utils.putNullOrValue(values, ActivitySpecs._ID, id);
		Utils.putNullOrValue(values, ActivitySpecs.NAME, name);
		Utils.putNullOrValue(values, ActivitySpecs.FORM_SPEC_ID, formSpecId);
		Utils.putNullOrValue(values, ActivitySpecs.DELETED, deleted);
		return values;
	}

	public void load(Cursor cursor) {
		// id = cursor.isNull(Activities._ID_INDEX) ? null : cursor
		// .getLong(Activities._ID_INDEX);
		id = cursor.isNull(ActivitySpecs._ID_INDEX) ? null : cursor
				.getLong(ActivitySpecs._ID_INDEX);
		name = cursor.getString(ActivitySpecs.NAME_INDEX);
		formSpecId = cursor.isNull(ActivitySpecs.FORM_SPEC_ID_INDEX) ? null
				: cursor.getLong(ActivitySpecs.FORM_SPEC_ID_INDEX);
		deleted = cursor.isNull(ActivitySpecs.DELETED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(ActivitySpecs.DELETED_INDEX));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getFormSpecId() {
		return formSpecId;
	}

	public void setFormSpecId(Long formSpecId) {
		this.formSpecId = formSpecId;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return "ActivitySpec [id=" + id + ", name=" + name + ", formSpecId="
				+ formSpecId + ", deleted=" + deleted + "]";
	}

}

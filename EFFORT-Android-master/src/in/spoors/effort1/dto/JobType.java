package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.JobTypes;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class JobType implements Comparable<JobType> {

	public static final String JSON_ID = "visitTypeId";
	public static final String JSON_NAME = "visitTypeValue";
	public static final String JSON_DEFAULT_TYPE = "makeDefault";

	private int id;
	private String name;
	private boolean checked;
	private boolean defaultType;

	public JobType() {
	}

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static JobType parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		JobType jobType = new JobType();

		// Id is mandatory. Thus, null check is not required.
		jobType.id = Utils.getInteger(json, JSON_ID);
		jobType.name = Utils.getString(json, JSON_NAME);
		jobType.checked = true;
		jobType.defaultType = Utils.getBoolean(json, JSON_DEFAULT_TYPE);

		return jobType;
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

		Utils.putNullOrValue(values, JobTypes._ID, id);
		Utils.putNullOrValue(values, JobTypes.NAME, name);
		Utils.putNullOrValue(values, JobTypes.CHECKED, checked);
		Utils.putNullOrValue(values, JobTypes.DEFAULT_TYPE, defaultType);

		return values;
	}

	public void load(Cursor cursor) {

		id = cursor.getInt(JobTypes._ID_INDEX);
		name = cursor.getString(JobTypes.NAME_INDEX);
		checked = Boolean
				.parseBoolean(cursor.getString(JobTypes.CHECKED_INDEX));
		defaultType = Boolean.parseBoolean(cursor
				.getString(JobTypes.DEFAULT_TYPE_INDEX));
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isDefaultType() {
		return defaultType;
	}

	public void setDefaultType(boolean defaultType) {
		this.defaultType = defaultType;
	}

	@Override
	public int compareTo(JobType another) {
		return name.compareTo(another.name);
	}

	@Override
	public String toString() {
		return "JobType [id=" + id + ", name=" + name + ", checked=" + checked
				+ ", defaultType=" + defaultType + "]";
	}

}

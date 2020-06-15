package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.JobTypes;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class CustomerType implements Comparable<CustomerType> {

	public static final String JSON_ID = "customerTypeId";
	public static final String JSON_NAME = "customerTypeName";

	private int id;
	private String name;
	private boolean checked;

	public CustomerType() {
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
	public static CustomerType parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		CustomerType customerType = new CustomerType();

		// Id is mandatory. Thus, null check is not required.
		customerType.id = json.getInt(JSON_ID);
		customerType.name = json.getString(JSON_NAME);
		customerType.checked = true;

		return customerType;
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

		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.getInt(JobTypes._ID_INDEX);
		name = cursor.getString(JobTypes.NAME_INDEX);
		checked = Boolean
				.parseBoolean(cursor.getString(JobTypes.CHECKED_INDEX));
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

	@Override
	public int compareTo(CustomerType another) {
		return name.compareTo(another.name);
	}

	@Override
	public String toString() {
		return "CustomerType [id=" + id + ", name=" + name + ", checked="
				+ checked + "]";
	}

}

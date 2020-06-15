package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.Holidays;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Holiday implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "Holiday";

	private Long id;
	private String title;
	private String description;
	private Date startTime;
	private Date endTime;

	// <string name="json_exception_working_hours">workingHours</string>

	public static final String JSON_ID = "id";
	public static final String JSON_TITLE = "title";
	public static final String JSON_DESCRIPTION = "desc";
	public static final String JSON_DATE = "date";

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static Holiday parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		Holiday holiday = new Holiday();

		holiday.id = Utils.getLong(json, JSON_ID);
		holiday.title = Utils.getString(json, JSON_TITLE);
		holiday.description = Utils.getString(json, JSON_DESCRIPTION);

		if (!json.isNull(JSON_DATE)) {
			Date date = XsdDateTimeUtils.getDate(json.getString(JSON_DATE));
			Calendar dateCal = Calendar.getInstance();
			dateCal.setTimeInMillis(date.getTime());

			Calendar startCal = Calendar.getInstance();
			startCal.clear();
			startCal.set(dateCal.get(Calendar.YEAR),
					dateCal.get(Calendar.MONTH),
					dateCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

			Calendar endCal = Calendar.getInstance();
			endCal.clear();
			endCal.set(dateCal.get(Calendar.YEAR), dateCal.get(Calendar.MONTH),
					dateCal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);

			holiday.startTime = startCal.getTime();
			holiday.endTime = endCal.getTime();
		}

		return holiday;
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
			values.put(Holidays._ID, id);
		}

		Utils.putNullOrValue(values, Holidays.TITLE, title);
		Utils.putNullOrValue(values, Holidays.DESCRIPTION, description);
		Utils.putNullOrValue(values, Holidays.START_TIME, startTime);
		Utils.putNullOrValue(values, Holidays.END_TIME, endTime);

		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.isNull(Holidays._ID_INDEX) ? null : cursor
				.getLong(Holidays._ID_INDEX);
		title = cursor.getString(Holidays.TITLE_INDEX);
		description = cursor.getString(Holidays.DESCRIPTION_INDEX);
		startTime = cursor.isNull(Holidays.START_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getDate(cursor
						.getString(Holidays.START_TIME_INDEX));
		endTime = cursor.isNull(Holidays.END_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getDate(cursor
						.getString(Holidays.END_TIME_INDEX));
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

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return "Holiday [id=" + id + ", title=" + title + ", description="
				+ description + ", startTime=" + startTime + ", endTime="
				+ endTime + "]";
	}

}

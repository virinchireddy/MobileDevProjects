package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.SpecialWorkingHours;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SpecialWorkingHour implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "SpecialWorkingHour";
	public static final String JSON_ID = "id";
	public static final String JSON_TITLE = "title";
	public static final String JSON_DAY_OF_WEEK = "day";
	public static final String JSON_START_TIME = "startTime";
	public static final String JSON_END_TIME = "endTime";

	private Long id;
	private Long specialWorkingDayId;
	private String title;
	private Date startTime;
	private Date endTime;

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static SpecialWorkingHour parse(JSONObject json, String xsdDate,
			Context applicationContext) throws JSONException, ParseException {
		SpecialWorkingHour duration = new SpecialWorkingHour();

		// trim the trailing Z
		if (xsdDate.endsWith("Z")) {
			xsdDate = xsdDate.substring(0, xsdDate.length() - 1);
		}

		duration.id = Utils.getLong(json, JSON_ID);
		duration.title = Utils.getString(json, JSON_TITLE);

		if (!json.isNull(JSON_START_TIME)) {
			/*
			 * duration.startTime = XsdDateTimeUtils.getLocalTime(xsdDate + "T"
			 * + json.getString(JSON_START_TIME));
			 */

			// TODO FOR TIMEZONEAWRE

			duration.startTime = XsdDateTimeUtils.getUtcTime(xsdDate + "T"
					+ json.getString(JSON_START_TIME));

		}

		if (!json.isNull(JSON_END_TIME)) {
			/*
			 * duration.endTime = XsdDateTimeUtils.getLocalTime(xsdDate + "T" +
			 * json.getString(JSON_END_TIME));
			 */

			// TODO FOR TIMEZONEAWRE

			duration.endTime = XsdDateTimeUtils.getUtcTime(xsdDate + "T"
					+ json.getString(JSON_END_TIME));

		}

		return duration;
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
			values.put(SpecialWorkingHours._ID, id);
		}

		Utils.putNullOrValue(values,
				SpecialWorkingHours.SPECIAL_WORKING_DAY_ID, specialWorkingDayId);
		Utils.putNullOrValue(values, SpecialWorkingHours.TITLE, title);
		Utils.putNullOrValue(values, SpecialWorkingHours.START_TIME, startTime);
		Utils.putNullOrValue(values, SpecialWorkingHours.END_TIME, endTime);

		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.isNull(SpecialWorkingHours._ID_INDEX) ? null : cursor
				.getLong(SpecialWorkingHours._ID_INDEX);
		specialWorkingDayId = cursor
				.isNull(SpecialWorkingHours.SPECIAL_WORKING_DAY_ID_INDEX) ? null
				: cursor.getLong(SpecialWorkingHours.SPECIAL_WORKING_DAY_ID_INDEX);
		title = cursor.getString(SpecialWorkingHours.TITLE_INDEX);
		startTime = cursor.isNull(SpecialWorkingHours.START_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime((cursor
						.getString(SpecialWorkingHours.START_TIME_INDEX)));
		endTime = cursor.isNull(SpecialWorkingHours.END_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime((cursor
						.getString(SpecialWorkingHours.END_TIME_INDEX)));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSpecialWorkingDayId() {
		return specialWorkingDayId;
	}

	public void setSpecialWorkingDayId(Long specialWorkingDayId) {
		this.specialWorkingDayId = specialWorkingDayId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
		return "SpecialWorkingHour [id=" + id + ", specialWorkingDayId="
				+ specialWorkingDayId + ", title=" + title + ", startTime="
				+ startTime + ", endTime=" + endTime + "]";
	}

}

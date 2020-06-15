package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.SpecialWorkingHours;
import in.spoors.effort1.provider.EffortProvider.WorkingHours;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class WorkingHour implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "WorkingHour";
	public static final String JSON_ID = "id";
	public static final String JSON_TITLE = "title";
	public static final String JSON_DAY_OF_WEEK = "day";
	public static final String JSON_START_TIME = "startTime";
	public static final String JSON_END_TIME = "endTime";

	private Long id;
	private Integer dayOfWeek;
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
	public static WorkingHour parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		WorkingHour wh = new WorkingHour();

		wh.id = Utils.getLong(json, JSON_ID);
		wh.title = Utils.getString(json, JSON_TITLE);
		wh.dayOfWeek = Utils.getInteger(json, JSON_DAY_OF_WEEK);

		if (!json.isNull(JSON_START_TIME)) {
			/*
			 * wh.startTime = XsdDateTimeUtils.getLocalTimeFromXsdTime(json
			 * .getString(JSON_START_TIME));
			 */
			// TODO FOR TIMEZONEAWRE

			wh.startTime = XsdDateTimeUtils.getUtcTimeFromXsdTime(json
					.getString(JSON_START_TIME));
		}

		if (!json.isNull(JSON_END_TIME)) {
			/*
			 * wh.endTime = XsdDateTimeUtils.getLocalTimeFromXsdTime(json
			 * .getString(JSON_END_TIME));
			 */
			// TODO FOR TIMEZONEAWRE

			wh.endTime = XsdDateTimeUtils.getUtcTimeFromXsdTime(json
					.getString(JSON_END_TIME));
		}

		return wh;
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

		Utils.putNullOrValue(values, WorkingHours.DAY_OF_WEEK, dayOfWeek);
		Utils.putNullOrValue(values, WorkingHours.TITLE, title);
		Utils.putNullOrValue(values, WorkingHours.START_TIME, startTime);
		Utils.putNullOrValue(values, WorkingHours.END_TIME, endTime);

		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.isNull(WorkingHours._ID_INDEX) ? null : cursor
				.getLong(WorkingHours._ID_INDEX);
		dayOfWeek = cursor.isNull(WorkingHours.DAY_OF_WEEK_INDEX) ? null
				: cursor.getInt(WorkingHours.DAY_OF_WEEK_INDEX);
		title = cursor.getString(WorkingHours.TITLE_INDEX);
		startTime = cursor.isNull(WorkingHours.START_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime((cursor
						.getString(WorkingHours.START_TIME_INDEX)));
		endTime = cursor.isNull(WorkingHours.END_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime((cursor
						.getString(WorkingHours.END_TIME_INDEX)));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
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
		return "WorkingHour [id=" + id + ", dayOfWeek=" + dayOfWeek
				+ ", title=" + title + ", startTime=" + startTime
				+ ", endTime=" + endTime + "]";
	}

}

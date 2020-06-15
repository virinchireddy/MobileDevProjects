package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.SpecialWorkingHoursDao;
import in.spoors.effort1.provider.EffortProvider.SpecialWorkingDays;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SpecialWorkingDay implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "SpecialWorkingDay";

	private Long id;
	private String title;
	private String description;
	private Date date;
	private List<SpecialWorkingHour> workingHours;

	public static final String JSON_ID = "id";
	public static final String JSON_TITLE = "title";
	public static final String JSON_DESCRIPTION = "description";
	public static final String JSON_DATE = "date";
	public static final String JSON_WORKING_HOURS = "workingHours";

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static SpecialWorkingDay parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {
		SpecialWorkingDay swd = new SpecialWorkingDay();

		swd.id = Utils.getLong(json, JSON_ID);
		swd.title = Utils.getString(json, JSON_TITLE);
		swd.description = Utils.getString(json, JSON_DESCRIPTION);

		if (!json.isNull(JSON_DATE)) {
			swd.date = XsdDateTimeUtils.getDate(json.getString(JSON_DATE));
		}

		if (!json.isNull(JSON_WORKING_HOURS)) {
			JSONArray workingHoursJson = json.getJSONArray(JSON_WORKING_HOURS);

			int n = workingHoursJson.length();
			swd.workingHours = new ArrayList<SpecialWorkingHour>(n);

			for (int i = 0; i < n; ++i) {
				JSONObject workingHourJson = (JSONObject) workingHoursJson
						.get(i);
				SpecialWorkingHour swh = SpecialWorkingHour.parse(
						workingHourJson, json.getString(JSON_DATE),
						applicationContext);
				swd.workingHours.add(swh);
			}
		}

		return swd;
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
			values.put(SpecialWorkingDays._ID, id);
		}

		Utils.putNullOrValue(values, SpecialWorkingDays.TITLE, title);
		Utils.putNullOrValue(values, SpecialWorkingDays.DESCRIPTION,
				description);
		Utils.putNullOrValue(values, SpecialWorkingDays.DATE, date);

		return values;
	}

	public void load(Cursor cursor, Context applicationContext) {
		id = cursor.isNull(SpecialWorkingDays._ID_INDEX) ? null : cursor
				.getLong(SpecialWorkingDays._ID_INDEX);
		title = cursor.getString(SpecialWorkingDays.TITLE_INDEX);
		description = cursor.getString(SpecialWorkingDays.DESCRIPTION_INDEX);
		date = cursor.isNull(SpecialWorkingDays.DATE_INDEX) ? null
				: SQLiteDateTimeUtils.getDate(cursor
						.getString(SpecialWorkingDays.DATE_INDEX));

		SpecialWorkingHoursDao swhDao = SpecialWorkingHoursDao
				.getInstance(applicationContext);
		workingHours = swhDao.getWorkingHours(id);
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<SpecialWorkingHour> getWorkingHours() {
		return workingHours;
	}

	public void setWorkingHours(List<SpecialWorkingHour> workingHours) {
		this.workingHours = workingHours;
	}

	@Override
	public String toString() {
		return "SpecialWorkingDay [id=" + id + ", title=" + title
				+ ", description=" + description + ", date=" + date
				+ ", workingHours=" + workingHours + "]";
	}

}

package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.CustomerStatus;
import in.spoors.effort1.provider.EffortProvider.Locations;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class CustomerStatusDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String TAG = "CustomerStatusDto";
	private Long loaclId;
	private Long assignRouteId;
	private Date statusChangeTime;
	private Long customerId;
	// private Long status;
	private Boolean status;
	private Boolean dirty;

	public static final String JSON_ASSIGN_ROUTE_ID = "assignRouteId";
	public static final String JSON_STATUS_CHANGE_TIME = "statusChangedTime";
	public static final String JSON_CUSTOMER_ID = "customerId";
	public static final String JSON_STATUS = "status";

	public static final long STATUS_COMPLETED = 1;
	public static final long STATUS_NOT_COMPLETED = 0;

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static CustomerStatusDto parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {
		CustomerStatusDto customerStatus = new CustomerStatusDto();

		customerStatus.assignRouteId = Utils
				.getLong(json, JSON_ASSIGN_ROUTE_ID);
		customerStatus.statusChangeTime = Utils.getDate(json,
				JSON_STATUS_CHANGE_TIME);
		customerStatus.customerId = Utils.getLong(json, JSON_CUSTOMER_ID);
		// customerStatus.status = Utils.getLong(json, JSON_STATUS);
		Long isCompleted = Utils.getLong(json, JSON_STATUS);
		customerStatus.status = (isCompleted != null && (isCompleted == 1 || isCompleted == 3)) ? true
				: false;
		return customerStatus;
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
		Utils.putNullOrValue(values, CustomerStatus._ID, loaclId);

		Utils.putNullOrValue(values, CustomerStatus.ASSIGN_ROUTE_ID,
				assignRouteId);
		Utils.putNullOrValue(values, CustomerStatus.STATUS_CHANGE_TIME,
				statusChangeTime);
		Utils.putNullOrValue(values, CustomerStatus.CUSTOMER_ID, customerId);
		Utils.putNullOrValue(values, CustomerStatus.STATUS, status);
		Utils.putNullOrValue(values, CustomerStatus.DIRTY, dirty);
		return values;
	}

	public void load(Cursor cursor) {
		loaclId = cursor.isNull(CustomerStatus._ID_INDEX) ? null : cursor
				.getLong(CustomerStatus._ID_INDEX);
		assignRouteId = cursor.isNull(CustomerStatus.ASSIGN_ROUTE_ID_INDEX) ? null
				: cursor.getLong(CustomerStatus.ASSIGN_ROUTE_ID_INDEX);
		statusChangeTime = cursor
				.isNull(CustomerStatus.STATUS_CHANGE_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(CustomerStatus.STATUS_CHANGE_TIME_INDEX));

		customerId = cursor.isNull(CustomerStatus.CUSTOMER_ID_INDEX) ? null
				: cursor.getLong(CustomerStatus.CUSTOMER_ID_INDEX);

		// status = cursor.isNull(CustomerStatus.STATUS_INDEX) ? null : cursor
		// .getLong(CustomerStatus.STATUS_INDEX);

		status = cursor.isNull(CustomerStatus.STATUS_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(CustomerStatus.STATUS_INDEX));
		dirty = cursor.isNull(CustomerStatus.DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(CustomerStatus.DIRTY_INDEX));
	}

	public Long getLoaclId() {
		return loaclId;
	}

	public void setLoaclId(Long loaclId) {
		this.loaclId = loaclId;
	}

	public Long getAssignRouteId() {
		return assignRouteId;
	}

	public void setAssignRouteId(Long assignRouteId) {
		this.assignRouteId = assignRouteId;
	}

	public Date getStatusChangeTime() {
		return statusChangeTime;
	}

	public void setStatusChangeTime(Date statusChangeTime) {
		this.statusChangeTime = statusChangeTime;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Boolean getDirty() {
		return dirty;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public void setDirty(Boolean dirty) {
		this.dirty = dirty;
	}

	@Override
	public String toString() {
		return "CustomerStatusDto [id=" + loaclId + ", assignRouteId="
				+ assignRouteId + ", statusChangeTime=" + statusChangeTime
				+ ", customerId=" + customerId + ", status=" + status
				+ ", dirty=" + dirty + "]";
	}

	public JSONObject getJsonObject(Context applicationContext) {
		JSONObject json = new JSONObject();

		try {
			json.put(JSON_ASSIGN_ROUTE_ID, assignRouteId);
			json.put(JSON_CUSTOMER_ID, customerId);
			// json.put(JSON_STATUS, status);
			if (status == true) {
				json.put(JSON_STATUS, STATUS_COMPLETED);
			} else {
				json.put(JSON_STATUS, STATUS_NOT_COMPLETED);
			}

			Utils.putValueOnlyIfNotNull(json, JSON_STATUS_CHANGE_TIME,
					statusChangeTime);

			LocationsDao locationsDao = LocationsDao
					.getInstance(applicationContext);

			LocationDto location = locationsDao.getLocation(
					EffortProvider.Locations.PURPOSE_CUSTOMER_STATUS_ACTIVITY,
					loaclId);
			if (location != null) {
				json.put("location", location.getJsonObject(applicationContext,
						Locations.PURPOSE_CUSTOMER_STATUS_ACTIVITY));
			}

		} catch (JSONException e) {
			Log.e(TAG,
					"Failed to compose JSON for CustomerStatusDto: "
							+ e.toString(), e);
			return null;
		}

		return json;
	}
}

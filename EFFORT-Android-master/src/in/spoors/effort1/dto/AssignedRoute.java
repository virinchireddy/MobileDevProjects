package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.AssignedRoutesDao;
import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.provider.EffortProvider.AssignedRoutes;
import in.spoors.effort1.provider.EffortProvider.Locations;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

public class AssignedRoute implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String TAG = "AssignedRoute";
	private Long id;
	private Boolean deleted;
	private Long status;
	private String routeName;
	private Long duration;
	private Date startDate;
	private Date endDate;
	private String remoteCustomerIds;
	private Boolean cached;
	private Date localModificationTime;
	private Date completionTime;
	private Boolean dirty;
	private Long minCustomersToComplete;

	public static final String JSON_ASSIGNED_ROUTE_ID = "assignRouteId";
	public static final String JSON_ROUTE_NAME = "routeName";
	public static final String JSON_DURATION = "duration";
	public static final String JSON_START_DATE = "startDate";
	public static final String JSON_END_DATE = "endDate";
	public static final String JSON_ARRAY_CUSTOMERS = "customers";
	public static final String JSON_IS_DELETED = "isDeleted";
	public static final String JSON_STATUS = "status";
	public static final String JSON_COMPLETE_TIME = "completeTime";
	public static final String JSON_MIN_CUSTOMERS_TO_COMPLETE = "minCustomersToComplete";
	public static final String JSON_ASSIGNED_ROUTE_ID_FOR_COMPLETE = "assignedRouteId";

	public static final int DELETED = 1;
	public static final int STATUS_LEAVE = 0;
	public static final int STATUS_PROGRESS = 1;
	public static final int STATUS_COMPLETED = 2;

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static AssignedRoute parse(JSONObject json,
			Context applicationContext, boolean forCompletedAssignedRoute)
			throws JSONException, ParseException {
		Long id = null;

		if (forCompletedAssignedRoute) {
			id = Utils.getLong(json, JSON_ASSIGNED_ROUTE_ID_FOR_COMPLETE);
		} else {
			id = Utils.getLong(json, JSON_ASSIGNED_ROUTE_ID);
		}

		AssignedRoutesDao assignedRoutesDao = AssignedRoutesDao
				.getInstance(applicationContext);
		AssignedRoute assignedRoute = assignedRoutesDao
				.getAssignedRouteWithId(id);

		if (assignedRoute == null) {
			assignedRoute = new AssignedRoute();
		}

		if (forCompletedAssignedRoute) {

		} else {
			assignedRoute.routeName = Utils.getString(json, JSON_ROUTE_NAME);
			assignedRoute.duration = Utils.getLong(json, JSON_DURATION);
			assignedRoute.status = Utils.getLong(json, JSON_STATUS);
			assignedRoute.startDate = Utils.getDate(json, JSON_START_DATE);
			assignedRoute.endDate = Utils.getDate(json, JSON_END_DATE);
			assignedRoute.minCustomersToComplete = Utils.getLong(json,
					JSON_MIN_CUSTOMERS_TO_COMPLETE);

			Long isDeleted = Utils.getLong(json, JSON_IS_DELETED);
			assignedRoute.deleted = (isDeleted != null && (isDeleted == 3 || isDeleted == 1)) ? true
					: false;
			List<Long> remoteCustomersIdsArray = Utils.getLongArray(json,
					JSON_ARRAY_CUSTOMERS);

			if (remoteCustomersIdsArray != null) {
				assignedRoute.remoteCustomerIds = TextUtils.join(",",
						remoteCustomersIdsArray);
			}
		}

		assignedRoute.id = id;

		assignedRoute.completionTime = Utils.getDate(json, JSON_COMPLETE_TIME);
		assignedRoute.dirty = false;

		return assignedRoute;
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
		Utils.putNullOrValue(values, AssignedRoutes._ID, id);
		Utils.putNullOrValue(values, AssignedRoutes.ROUTE_NAME, routeName);
		Utils.putNullOrValue(values, AssignedRoutes.DURATION, duration);
		Utils.putNullOrValue(values, AssignedRoutes.START_DATE, startDate);
		Utils.putNullOrValue(values, AssignedRoutes.END_DATE, endDate);
		Utils.putNullOrValue(values, AssignedRoutes.CACHED, cached);
		Utils.putNullOrValue(values, AssignedRoutes.DELETED, deleted);
		Utils.putNullOrValue(values, AssignedRoutes.REMOTE_CUSTOMER_IDS,
				remoteCustomerIds);
		Utils.putNullOrValue(values, AssignedRoutes.STATUS, status);
		Utils.putNullOrValue(values, AssignedRoutes.LOCAL_MODIFICATION_TIME,
				localModificationTime);
		Utils.putNullOrValue(values, AssignedRoutes.COMPLETION_TIME,
				completionTime);
		Utils.putNullOrValue(values, AssignedRoutes.MIN_CUSTOMERS_TO_COMPLETE,
				minCustomersToComplete);
		Utils.putNullOrValue(values, AssignedRoutes.DIRTY, dirty);

		return values;
	}

	public void load(Cursor cursor) {

		id = cursor.isNull(AssignedRoutes._ID_INDEX) ? null : cursor
				.getLong(AssignedRoutes._ID_INDEX);
		routeName = cursor.getString(AssignedRoutes.ROUTE_NAME_INDEX);
		duration = cursor.isNull(AssignedRoutes.DURATION_INDEX) ? null : cursor
				.getLong(AssignedRoutes.DURATION_INDEX);
		startDate = cursor.isNull(AssignedRoutes.START_DATE_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(AssignedRoutes.START_DATE_INDEX));
		endDate = cursor.isNull(AssignedRoutes.END_DATE_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(AssignedRoutes.END_DATE_INDEX));
		cached = cursor.isNull(AssignedRoutes.CACHED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(AssignedRoutes.CACHED_INDEX));
		deleted = cursor.isNull(AssignedRoutes.DELETED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(AssignedRoutes.DELETED_INDEX));
		status = cursor.isNull(AssignedRoutes.STATUS_INDEX) ? null : cursor
				.getLong(AssignedRoutes.STATUS_INDEX);
		remoteCustomerIds = cursor
				.getString(AssignedRoutes.REMOTE_CUSTOMER_IDS_INDEX);
		localModificationTime = cursor
				.isNull(AssignedRoutes.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils
						.getLocalTime(cursor
								.getString(AssignedRoutes.LOCAL_MODIFICATION_TIME_INDEX));
		completionTime = cursor.isNull(AssignedRoutes.COMPLETION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(AssignedRoutes.COMPLETION_TIME_INDEX));
		minCustomersToComplete = cursor
				.isNull(AssignedRoutes.MIN_CUSTOMERS_TO_COMPLETE_INDEX) ? null
				: cursor.getLong(AssignedRoutes.MIN_CUSTOMERS_TO_COMPLETE_INDEX);
		dirty = cursor.isNull(AssignedRoutes.DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(AssignedRoutes.DIRTY_INDEX));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Boolean getCached() {
		return cached;
	}

	public void setCached(Boolean cached) {
		this.cached = cached;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public Date getLocalModificationTime() {
		return localModificationTime;
	}

	public void setLocalModificationTime(Date localModificationTime) {
		this.localModificationTime = localModificationTime;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getRemoteCustomerIds() {
		return remoteCustomerIds;
	}

	public void setRemoteCustomerIds(String remoteCustomerIds) {
		this.remoteCustomerIds = remoteCustomerIds;
	}

	public Date getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(Date completionTime) {
		this.completionTime = completionTime;
	}

	public Boolean getDirty() {
		return dirty;
	}

	public void setDirty(Boolean dirty) {
		this.dirty = dirty;
	}

	public Long getMinCustomersToComplete() {
		return minCustomersToComplete;
	}

	public void setMinCustomersToComplete(Long minCustomersToComplete) {
		this.minCustomersToComplete = minCustomersToComplete;
	}

	@Override
	public String toString() {
		return "AssignedRoute [id=" + id + ", deleted=" + deleted + ", status="
				+ status + ", routeName=" + routeName + ", duration="
				+ duration + ", startDate=" + startDate + ", endDate="
				+ endDate + ", remoteCustomerIds=" + remoteCustomerIds
				+ ", cached=" + cached + ", localModificationTime="
				+ localModificationTime + ", completionTime=" + completionTime
				+ ", dirty=" + dirty + ", minCustomersToComplete="
				+ minCustomersToComplete + "]";
	}

	/**
	 * Get the JSON object that can be sent to the server.
	 * 
	 * @param applicationContext
	 * @return
	 */
	public JSONObject getJsonObject(Context applicationContext) {
		JSONObject json = new JSONObject();

		try {
			json.put(JSON_ASSIGNED_ROUTE_ID_FOR_COMPLETE, id);

			LocationsDao locationsDao = LocationsDao
					.getInstance(applicationContext);

			Utils.putValueOnlyIfNotNull(json, JSON_COMPLETE_TIME,
					completionTime);
			LocationDto location = locationsDao.getLocation(
					Locations.PURPOSE_COMPLETE_ROUTE_PLAN, id);
			if (location != null) {
				json.put("location", location.getJsonObject(applicationContext,
						Locations.PURPOSE_COMPLETE_ROUTE_PLAN));
			}

		} catch (JSONException e) {
			Log.e(TAG,
					"Failed to compose JSON for CompleteRoutePlan: "
							+ e.toString(), e);
			return null;
		}
		return json;
	}

}
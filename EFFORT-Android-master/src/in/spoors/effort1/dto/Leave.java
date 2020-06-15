package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.LeavesDao;
import in.spoors.effort1.provider.EffortProvider.Leaves;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Leave implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "Leave";
	public static final String JSON_LOCAL_ID = "clientLeaveId";
	public static final String JSON_REMOTE_ID = "empLeaveId";
	// TODO: get the typo fixed on the server side
	public static final String JSON_START_TIME = "formDateTime";
	public static final String JSON_END_TIME = "toDateTime";
	public static final String JSON_EMPLOYEE_REMARKS = "employeeNote";
	public static final String JSON_MANAGER_REMARKS = "managerNote";
	public static final String JSON_STATUS = "status";
	public static final String JSON_STATUS_CHANGED_BY_ID = "statusChangedBy";
	public static final String JSON_STATUS_CHANGED_BY_NAME = "statusChangedByName";
	public static final String JSON_CANCELLED = "deleted";

	private Long localId;
	private Long remoteId;
	private Integer status;
	private Date startTime;
	private Date endTime;
	private String employeeRemarks;
	private String managerRemarks;
	private Long statusChangedById;
	private String statusChangedByName;
	private Boolean cancelled;
	private Boolean dirty;
	private Date localCreationTime;
	private Date localModificationTime;

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for accessing resource strings, and creating DAO
	 *            objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static Leave parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {

		// Remote leave Id is mandatory. Thus, null check is not required.
		long remoteId = json.getLong(JSON_REMOTE_ID);
		LeavesDao leavesDao = LeavesDao.getInstance(applicationContext);
		Leave leave = leavesDao.getLeaveWithRemoteId(remoteId);

		Long localId = Utils.getLong(json, JSON_LOCAL_ID);
		if (leave == null && localId != null) {
			leave = leavesDao.getLeaveWithLocalId(localId);
		}

		if (leave == null) {
			leave = new Leave();
		}

		leave.remoteId = remoteId;
		leave.dirty = false;
		leave.status = Utils.getInteger(json, JSON_STATUS);
		leave.startTime = Utils.getDate(json, JSON_START_TIME);
		leave.endTime = XsdDateTimeUtils.getLocalTime(json
				.getString(JSON_END_TIME));
		leave.employeeRemarks = Utils.getString(json, JSON_EMPLOYEE_REMARKS);
		leave.managerRemarks = Utils.getString(json, JSON_MANAGER_REMARKS);
		leave.statusChangedById = Utils
				.getLong(json, JSON_STATUS_CHANGED_BY_ID);
		leave.statusChangedByName = Utils.getString(json,
				JSON_STATUS_CHANGED_BY_NAME);
		leave.cancelled = Utils.getBoolean(json, JSON_CANCELLED);

		return leave;
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
		if (localId != null) {
			values.put(Leaves._ID, localId);
		}

		Utils.putNullOrValue(values, Leaves.REMOTE_ID, remoteId);
		Utils.putNullOrValue(values, Leaves.STATUS, status);
		Utils.putNullOrValue(values, Leaves.START_TIME, startTime);
		Utils.putNullOrValue(values, Leaves.END_TIME, endTime);
		Utils.putNullOrValue(values, Leaves.EMPLOYEE_REMARKS, employeeRemarks);
		Utils.putNullOrValue(values, Leaves.MANAGER_REMARKS, managerRemarks);
		Utils.putNullOrValue(values, Leaves.STATUS_CHANGED_BY_ID,
				statusChangedById);
		Utils.putNullOrValue(values, Leaves.STATUS_CHANGED_BY_NAME,
				statusChangedByName);
		Utils.putNullOrValue(values, Leaves.CANCELLED, cancelled);
		Utils.putNullOrValue(values, Leaves.DIRTY, dirty);
		Utils.putNullOrValue(values, Leaves.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, Leaves.LOCAL_MODIFICATION_TIME,
				localModificationTime);

		return values;
	}

	public void load(Cursor cursor) {
		localId = cursor.isNull(Leaves._ID_INDEX) ? null : cursor
				.getLong(Leaves._ID_INDEX);
		remoteId = cursor.isNull(Leaves.REMOTE_ID_INDEX) ? null : cursor
				.getLong(Leaves.REMOTE_ID_INDEX);
		status = cursor.isNull(Leaves.STATUS_INDEX) ? null : cursor
				.getInt(Leaves.STATUS_INDEX);
		startTime = cursor.isNull(Leaves.START_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime((cursor
						.getString(Leaves.START_TIME_INDEX)));
		endTime = cursor.isNull(Leaves.END_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime((cursor
						.getString(Leaves.END_TIME_INDEX)));
		employeeRemarks = cursor.getString(Leaves.EMPLOYEE_REMARKS_INDEX);
		managerRemarks = cursor.getString(Leaves.MANAGER_REMARKS_INDEX);
		statusChangedById = cursor.isNull(Leaves.STATUS_CHANGED_BY_ID_INDEX) ? null
				: cursor.getLong(Leaves.STATUS_CHANGED_BY_ID_INDEX);
		statusChangedByName = cursor
				.isNull(Leaves.STATUS_CHANGED_BY_NAME_INDEX) ? null : cursor
				.getString(Leaves.STATUS_CHANGED_BY_NAME_INDEX);
		cancelled = cursor.isNull(Leaves.CANCELLED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Leaves.CANCELLED_INDEX));
		dirty = cursor.isNull(Leaves.DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Leaves.DIRTY_INDEX));
		localCreationTime = cursor.isNull(Leaves.LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Leaves.LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(Leaves.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Leaves.LOCAL_MODIFICATION_TIME_INDEX));
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
	}

	public Long getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(Long remoteId) {
		this.remoteId = remoteId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public String getEmployeeRemarks() {
		return employeeRemarks;
	}

	public void setEmployeeRemarks(String employeeRemarks) {
		this.employeeRemarks = employeeRemarks;
	}

	public String getManagerRemarks() {
		return managerRemarks;
	}

	public void setManagerRemarks(String managerRemarks) {
		this.managerRemarks = managerRemarks;
	}

	public Long getStatusChangedById() {
		return statusChangedById;
	}

	public void setStatusChangedById(Long statusChangedById) {
		this.statusChangedById = statusChangedById;
	}

	public String getStatusChangedByName() {
		return statusChangedByName;
	}

	public void setStatusChangedByName(String statusChangedByName) {
		this.statusChangedByName = statusChangedByName;
	}

	public Boolean getCancelled() {
		return cancelled;
	}

	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Boolean getDirty() {
		return dirty;
	}

	public void setDirty(Boolean dirty) {
		this.dirty = dirty;
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
		return "Leave [localId=" + localId + ", remoteId=" + remoteId
				+ ", status=" + status + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", employeeRemarks="
				+ employeeRemarks + ", managerRemarks=" + managerRemarks
				+ ", statusChangedById=" + statusChangedById
				+ ", statusChangedByName=" + statusChangedByName
				+ ", canceled=" + cancelled + ", dirty=" + dirty
				+ ", localCreationTime=" + localCreationTime
				+ ", localModificationTime=" + localModificationTime + "]";
	}

	/**
	 * Get the JSON object that can be sent to the server.
	 * 
	 * @param applicationContext
	 *            Used for reading JSON path constants from resources.
	 * @return
	 */
	public JSONObject getJsonObject(Context applicationContext) {
		JSONObject json = new JSONObject();

		try {
			if (remoteId == null) {
				json.put(JSON_LOCAL_ID, localId);
			} else {
				json.put(JSON_REMOTE_ID, remoteId);
			}

			Utils.putValueOnlyIfNotNull(json, JSON_STATUS, status);
			Utils.putValueOnlyIfNotNull(json, JSON_START_TIME, startTime);
			Utils.putValueOnlyIfNotNull(json, JSON_END_TIME, endTime);
			Utils.putValueOnlyIfNotNull(json, JSON_EMPLOYEE_REMARKS,
					employeeRemarks);

			// server expects an int (not a boolean)
			if (cancelled != null) {
				Utils.putValueOnlyIfNotNull(json, JSON_CANCELLED, cancelled ? 1
						: 0);
			}

			// Utils.putValueOnlyIfNotNull(json, JSON_MANAGER_REMARKS,
			// managerRemarks);
			// Utils.putValueOnlyIfNotNull(json, JSON_STATUS_CHANGED_BY_ID,
			// statusChangedById);
			// Utils.putValueOnlyIfNotNull(json, JSON_STATUS_CHANGED_BY_NAME,
			// statusChangedByName);
		} catch (JSONException e) {
			Log.e(TAG, "Failed to compose JSON for leave: " + e.toString(), e);
			return null;
		}

		return json;
	}

}

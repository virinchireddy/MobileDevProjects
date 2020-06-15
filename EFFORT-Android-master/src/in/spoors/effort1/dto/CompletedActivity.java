package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.CompletedActivitiesDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.CompletedActivities;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class CompletedActivity implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final String TAG = "CompletedActivity";
	private Long localId;
	private Long remoteId;
	private Long assignRouteId;
	private Long activityId;
	private Long formId;
	private Long clientFormId;
	private Date activityCompletedTime;
	private Long remoteCustomerId;
	private Boolean dirty;
	private Long formSpecId;

	public static final String JSON_LOCAL_ID = "clientActivityId";
	public static final String JSON_REMOTE_ID = "completedActivityId";
	public static final String JSON_ASSIGN_ROUTE_ID = "assignRouteId";
	public static final String JSON_ACTIVITY_ID = "activityId";
	public static final String JSON_FORM_ID = "formId";
	public static final String JSON_CLIENT_FORM_ID = "clientFormId";
	public static final String JSON_ACTIVITY_COMPLETED_TIME = "activityCompletedTime";
	public static final String JSON_CUSTOMER_ID = "customerId";

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static CompletedActivity parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {

		Long remoteId = json.optLong(JSON_REMOTE_ID);
		Long localId = Utils.getLong(json, JSON_LOCAL_ID);
		CompletedActivitiesDao caDao = CompletedActivitiesDao
				.getInstance(applicationContext);

		CompletedActivity completedActivity = null;
		if (remoteId != null) {
			completedActivity = caDao
					.getCompletedActivityWithRemoteId(remoteId);
		}

		if (completedActivity == null && localId != null) {
			completedActivity = caDao.getCompletedActivityWithLocalId(localId);
		}

		if (completedActivity == null) {
			completedActivity = new CompletedActivity();
		}

		if (localId != null) {
			completedActivity.localId = localId;
		}

		completedActivity.remoteId = remoteId;
		completedActivity.dirty = false;

		completedActivity.assignRouteId = Utils.getLong(json,
				JSON_ASSIGN_ROUTE_ID);
		completedActivity.activityId = Utils.getLong(json, JSON_ACTIVITY_ID);
		completedActivity.formId = Utils.getLong(json, JSON_FORM_ID);
		completedActivity.clientFormId = Utils.getLong(json,
				JSON_CLIENT_FORM_ID);

		completedActivity.activityCompletedTime = Utils.getDate(json,
				JSON_ACTIVITY_COMPLETED_TIME);
		if (completedActivity.activityCompletedTime == null) {
			completedActivity.activityCompletedTime = new Date();
		}
		completedActivity.remoteCustomerId = Utils.getLong(json,
				JSON_CUSTOMER_ID);

		return completedActivity;
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
			values.put(CompletedActivities._ID, localId);
		}

		Utils.putNullOrValue(values, CompletedActivities.REMOTE_ID, remoteId);
		Utils.putNullOrValue(values, CompletedActivities.ASSIGNED_ROUTE_ID,
				assignRouteId);
		Utils.putNullOrValue(values, CompletedActivities.ACTIVITY_ID,
				activityId);
		// Utils.putNullOrValue(values, CompletedActivities.FORM_ID, formId);
		Utils.putNullOrValue(values, CompletedActivities.CLIENT_FORM_ID,
				clientFormId);
		Utils.putNullOrValue(values,
				CompletedActivities.ACTIVITY_COMPLETED_TIME,
				activityCompletedTime);
		Utils.putNullOrValue(values, CompletedActivities.CUSTOMER_ID,
				remoteCustomerId);
		Utils.putNullOrValue(values, CompletedActivities.DIRTY, dirty);
		return values;
	}

	public void load(Cursor cursor, Context applicationContext) {

		localId = cursor.isNull(CompletedActivities._ID_INDEX) ? null : cursor
				.getLong(EffortProvider.CompletedActivities._ID_INDEX);
		remoteId = cursor.isNull(CompletedActivities.REMOTE_ID_INDEX) ? null
				: cursor.getLong(CompletedActivities.REMOTE_ID_INDEX);
		assignRouteId = cursor
				.isNull(CompletedActivities.ASSIGNED_ROUTE_ID_INDEX) ? null
				: cursor.getLong(CompletedActivities.ASSIGNED_ROUTE_ID_INDEX);
		activityId = cursor.isNull(CompletedActivities.ACTIVITY_ID_INDEX) ? null
				: cursor.getLong(CompletedActivities.ACTIVITY_ID_INDEX);
		// formId = cursor.isNull(CompletedActivities.FORM_ID_INDEX) ? null
		// : cursor.getLong(CompletedActivities.FORM_ID_INDEX);
		clientFormId = cursor.isNull(CompletedActivities.CLIENT_FORM_ID_INDEX) ? null
				: cursor.getLong(CompletedActivities.CLIENT_FORM_ID_INDEX);
		FormsDao formsDao = FormsDao.getInstance(applicationContext);
		formId = formsDao.getRemoteId(clientFormId);

		remoteCustomerId = cursor.isNull(CompletedActivities.CUSTOMER_ID_INDEX) ? null
				: cursor.getLong(CompletedActivities.CUSTOMER_ID_INDEX);

		activityCompletedTime = cursor
				.isNull(CompletedActivities.ACTIVITY_COMPLETED_TIME_INDEX) ? null
				: SQLiteDateTimeUtils
						.getLocalTime(cursor
								.getString(CompletedActivities.ACTIVITY_COMPLETED_TIME_INDEX));

		dirty = cursor.isNull(CompletedActivities.DIRTY_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(CompletedActivities.DIRTY_INDEX));
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

	public Long getAssignRouteId() {
		return assignRouteId;
	}

	public void setAssignRouteId(Long assignRouteId) {
		this.assignRouteId = assignRouteId;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Long getFormId() {
		return formId;
	}

	public void setFormId(Long formId) {
		this.formId = formId;
	}

	public Long getClientFormId() {
		return clientFormId;
	}

	public void setClientFormId(Long clientFormId) {
		this.clientFormId = clientFormId;
	}

	public Date getActivityCompletedTime() {
		return activityCompletedTime;
	}

	public void setActivityCompletedTime(Date activityCompletedTime) {
		this.activityCompletedTime = activityCompletedTime;
	}

	public Long getRemoteCustomerId() {
		return remoteCustomerId;
	}

	public void setRemoteCustomerId(Long remoteCustomerId) {
		this.remoteCustomerId = remoteCustomerId;
	}

	public Boolean getDirty() {
		return dirty;
	}

	public void setDirty(Boolean dirty) {
		this.dirty = dirty;
	}

	public Long getFormSpecId() {
		return formSpecId;
	}

	public void setFormSpecId(Long formSpecId) {
		this.formSpecId = formSpecId;
	}

	@Override
	public String toString() {
		return "CompletedActivity [localId=" + localId + ", remoteId="
				+ remoteId + ", assignRouteId=" + assignRouteId
				+ ", activityId=" + activityId + ", formId=" + formId
				+ ", clientFormId=" + clientFormId + ", activityCompletedTime="
				+ activityCompletedTime + ", customerId=" + remoteCustomerId
				+ ", dirty=" + dirty + "]";
	}

	public JSONObject getJsonObject(Context applicationContext) {
		JSONObject json = new JSONObject();

		try {
			if (remoteId == null) {
				json.put(JSON_LOCAL_ID, localId);
			} else {
				json.put(JSON_REMOTE_ID, remoteId);
			}

			if (formId == null) {
				json.put(JSON_CLIENT_FORM_ID, clientFormId);
			} else {
				json.put(JSON_FORM_ID, formId);
			}

			json.put(JSON_CUSTOMER_ID, remoteCustomerId);

			json.put(JSON_ASSIGN_ROUTE_ID, assignRouteId);
			json.put(JSON_ACTIVITY_ID, activityId);

			Utils.putValueOnlyIfNotNull(json, JSON_ACTIVITY_COMPLETED_TIME,
					activityCompletedTime);

		} catch (JSONException e) {
			Log.e(TAG,
					"Failed to compose JSON for CompletedActivity: "
							+ e.toString(), e);
			return null;
		}

		return json;
	}

}

package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.JobHistoriesDao;
import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.JobHistories;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class JobHistory implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "JobHistory";
	private static final String JSON_LOCAL_ID = "clientEmpVisitHistId";
	private static final String JSON_REMOTE_ID = "empVisitHistId";
	private static final String JSON_LOCAL_JOB_ID = "clientEmpVisitId";
	private static final String JSON_REMOTE_JOB_ID = "empVisitId";
	private static final String JSON_LOCAL_FORM_ID = "clientFormId";
	private static final String JSON_REMOTE_FORM_ID = "formId";
	private static final String JSON_TYPE_ID = "visitTypeId";
	private static final String JSON_STATE_ID = "visitStateId";
	private static final String JSON_TITLE = "empVisitTitle";
	private static final String JSON_DESCRIPTION = "empVisitDesc";
	private static final String JSON_EMPLOYEE_ID = "empId";
	private static final String JSON_LOCAL_CUSTOMER_ID = "clientCustomerId";
	private static final String JSON_REMOTE_CUSTOMER_ID = "customerId";
	private static final String JSON_START_TIME = "visitStartTime";
	private static final String JSON_END_TIME = "visitEndTime";
	private static final String JSON_COMPLETION_TIME = "completeTime";
	private static final String JSON_COMPLETED = "completed";
	private static final String JSON_APPROVED = "approved";
	private static final String JSON_REMOTE_CREATION_TIME = "visitHistCreatedTime";

	private Long localId;
	private Long remoteId;
	private Long localJobId;
	private Long localFormId;
	private Integer typeId;
	private Integer stateId;
	private String title;
	private String description;
	private Date startTime;
	private Date endTime;
	private Long employeeId;
	private Long localCustomerId;
	private Boolean temporary;

	/**
	 * These must not be persisted into the database.
	 */
	private Long remoteJobId;
	private Long remoteFormId;
	private Long remoteCustomerId;

	private Boolean completed;
	private Date completionTime;
	private Boolean approved;
	private Date remoteCreationTime;
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
	public static JobHistory parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		// Remote Job Id is mandatory. Thus, null check is not required.
		long remoteId = json.getLong(JSON_REMOTE_ID);
		JobHistoriesDao jobHistoriesDao = JobHistoriesDao
				.getInstance(applicationContext);
		JobHistory jobHistory = jobHistoriesDao
				.getJobHistoryWithRemoteId(remoteId);

		Long localId = Utils.getLong(json, JSON_LOCAL_ID);

		if (jobHistory == null && localId != null) {
			jobHistory = jobHistoriesDao.getJobHistoryWithLocalId(localId);
		}

		if (jobHistory == null) {
			jobHistory = new JobHistory();
		}

		if (localId != null) {
			jobHistory.localId = localId;
		}

		jobHistory.remoteId = remoteId;

		jobHistory.typeId = Utils.getInteger(json, JSON_TYPE_ID);
		jobHistory.stateId = Utils.getInteger(json, JSON_STATE_ID);
		jobHistory.title = Utils.getString(json, JSON_TITLE);
		jobHistory.description = Utils.getString(json, JSON_DESCRIPTION);
		jobHistory.employeeId = Utils.getLong(json, JSON_EMPLOYEE_ID);

		jobHistory.localJobId = Utils.getLong(json, JSON_LOCAL_JOB_ID);
		jobHistory.remoteJobId = Utils.getLong(json, JSON_REMOTE_JOB_ID);
		jobHistory.localFormId = Utils.getLong(json, JSON_LOCAL_FORM_ID);
		jobHistory.remoteFormId = Utils.getLong(json, JSON_REMOTE_FORM_ID);

		jobHistory.localCustomerId = Utils
				.getLong(json, JSON_LOCAL_CUSTOMER_ID);
		jobHistory.remoteCustomerId = Utils.getLong(json,
				JSON_REMOTE_CUSTOMER_ID);
		jobHistory.startTime = Utils.getDate(json, JSON_START_TIME);
		jobHistory.endTime = Utils.getDate(json, JSON_END_TIME);
		jobHistory.completionTime = Utils.getDate(json, JSON_COMPLETION_TIME);
		jobHistory.completed = Utils.getBoolean(json, JSON_COMPLETED);
		jobHistory.approved = Utils.getBoolean(json, JSON_APPROVED);
		jobHistory.remoteCreationTime = Utils.getDate(json,
				JSON_REMOTE_CREATION_TIME);

		return jobHistory;
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

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
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

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Long getLocalCustomerId() {
		return localCustomerId;
	}

	public void setLocalCustomerId(Long localCustomerId) {
		this.localCustomerId = localCustomerId;
	}

	public Long getRemoteCustomerId() {
		return remoteCustomerId;
	}

	public void setRemoteCustomerId(Long remoteCustomerId) {
		this.remoteCustomerId = remoteCustomerId;
	}

	public Long getLocalJobId() {
		return localJobId;
	}

	public void setLocalJobId(Long localJobId) {
		this.localJobId = localJobId;
	}

	public Long getLocalFormId() {
		return localFormId;
	}

	public void setLocalFormId(Long localFormId) {
		this.localFormId = localFormId;
	}

	public Long getRemoteJobId() {
		return remoteJobId;
	}

	public void setRemoteJobId(Long remoteJobId) {
		this.remoteJobId = remoteJobId;
	}

	public Long getRemoteFormId() {
		return remoteFormId;
	}

	public void setRemoteFormId(Long remoteFormId) {
		this.remoteFormId = remoteFormId;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public Date getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(Date completionTime) {
		this.completionTime = completionTime;
	}

	public Date getRemoteCreationTime() {
		return remoteCreationTime;
	}

	public void setRemoteCreationTime(Date remoteCreationTime) {
		this.remoteCreationTime = remoteCreationTime;
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

	public Boolean getTemporary() {
		return temporary;
	}

	public void setTemporary(Boolean temporary) {
		this.temporary = temporary;
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
			values.put(JobHistories._ID, localId);
		}

		Utils.putNullOrValue(values, JobHistories.REMOTE_ID, remoteId);
		Utils.putNullOrValue(values, JobHistories.JOB_TYPE_ID, typeId);
		Utils.putNullOrValue(values, JobHistories.JOB_STATE_ID, stateId);
		Utils.putNullOrValue(values, JobHistories.TITLE, title);
		Utils.putNullOrValue(values, JobHistories.DESCRIPTION, description);
		Utils.putNullOrValue(values, JobHistories.START_TIME, startTime);
		Utils.putNullOrValue(values, JobHistories.END_TIME, endTime);
		Utils.putNullOrValue(values, JobHistories.EMPLOYEE_ID, employeeId);
		Utils.putNullOrValue(values, JobHistories.LOCAL_JOB_ID, localJobId);
		Utils.putNullOrValue(values, JobHistories.LOCAL_FORM_ID, localFormId);
		Utils.putNullOrValue(values, JobHistories.LOCAL_CUSTOMER_ID,
				localCustomerId);
		Utils.putNullOrValue(values, JobHistories.COMPLETED, completed);
		Utils.putNullOrValue(values, JobHistories.COMPLETION_TIME,
				completionTime);
		Utils.putNullOrValue(values, JobHistories.APPROVED, approved);
		Utils.putNullOrValue(values, JobHistories.REMOTE_CREATION_TIME,
				remoteCreationTime);
		Utils.putNullOrValue(values, JobHistories.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, JobHistories.LOCAL_MODIFICATION_TIME,
				localModificationTime);
		Utils.putNullOrValue(values, JobHistories.TEMPORARY, temporary);

		return values;
	}

	public void load(Cursor cursor, Context applicationContext) {
		localId = cursor.isNull(JobHistories._ID_INDEX) ? null : cursor
				.getLong(EffortProvider.JobHistories._ID_INDEX);
		remoteId = cursor.isNull(JobHistories.REMOTE_ID_INDEX) ? null : cursor
				.getLong(JobHistories.REMOTE_ID_INDEX);
		typeId = cursor.isNull(JobHistories.JOB_TYPE_ID_INDEX) ? null : cursor
				.getInt(JobHistories.JOB_TYPE_ID_INDEX);
		stateId = cursor.isNull(JobHistories.JOB_STATE_ID_INDEX) ? null
				: cursor.getInt(JobHistories.JOB_STATE_ID_INDEX);
		employeeId = cursor.isNull(JobHistories.EMPLOYEE_ID_INDEX) ? null
				: cursor.getLong(JobHistories.EMPLOYEE_ID_INDEX);

		localJobId = cursor.isNull(JobHistories.LOCAL_JOB_ID_INDEX) ? null
				: cursor.getLong(JobHistories.LOCAL_JOB_ID_INDEX);
		JobsDao jobsDao = JobsDao.getInstance(applicationContext);
		remoteJobId = jobsDao.getRemoteId(localJobId);

		localFormId = cursor.isNull(JobHistories.LOCAL_FORM_ID_INDEX) ? null
				: cursor.getLong(JobHistories.LOCAL_FORM_ID_INDEX);
		FormsDao formsDao = FormsDao.getInstance(applicationContext);
		remoteFormId = formsDao.getRemoteId(localFormId);

		localCustomerId = cursor.isNull(JobHistories.LOCAL_CUSTOMER_ID_INDEX) ? null
				: cursor.getLong(JobHistories.LOCAL_CUSTOMER_ID_INDEX);
		CustomersDao customersDao = CustomersDao
				.getInstance(applicationContext);

		if (localCustomerId != null) {
			remoteCustomerId = customersDao.getRemoteId(localCustomerId);
		}

		title = cursor.getString(JobHistories.TITLE_INDEX);
		description = cursor.getString(JobHistories.DESCRIPTION_INDEX);
		startTime = cursor.isNull(JobHistories.START_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(JobHistories.START_TIME_INDEX));
		endTime = cursor.isNull(JobHistories.END_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(JobHistories.END_TIME_INDEX));
		completed = cursor.isNull(JobHistories.COMPLETED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(JobHistories.COMPLETED_INDEX));
		completionTime = cursor.isNull(JobHistories.COMPLETION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(JobHistories.COMPLETION_TIME_INDEX));
		approved = cursor.isNull(JobHistories.APPROVED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(JobHistories.APPROVED_INDEX));
		remoteCreationTime = cursor
				.isNull(JobHistories.REMOTE_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(JobHistories.REMOTE_CREATION_TIME_INDEX));
		localCreationTime = cursor
				.isNull(JobHistories.LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(JobHistories.LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(JobHistories.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(JobHistories.LOCAL_MODIFICATION_TIME_INDEX));
		temporary = cursor.isNull(JobHistories.TEMPORARY_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(JobHistories.TEMPORARY_INDEX));
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

			// if remote customer id is present, don't send the local customer
			// id to the server
			if (remoteCustomerId != null) {
				json.put(JSON_REMOTE_CUSTOMER_ID, remoteCustomerId);
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_LOCAL_CUSTOMER_ID,
						localCustomerId);
			}

			// if remote job id is present, don't send the local job
			// id to the server
			if (remoteJobId != null) {
				json.put(JSON_REMOTE_JOB_ID, remoteJobId);
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_LOCAL_JOB_ID, localJobId);
			}

			// if remote form id is present, don't send the local form
			// id to the server
			if (remoteFormId != null) {
				json.put(JSON_REMOTE_FORM_ID, remoteFormId);
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_LOCAL_FORM_ID,
						localFormId);
			}

			Utils.putValueOnlyIfNotNull(json, JSON_TYPE_ID, typeId);
			Utils.putValueOnlyIfNotNull(json, JSON_STATE_ID, stateId);
			Utils.putValueOnlyIfNotNull(json, JSON_TITLE, title);
			Utils.putValueOnlyIfNotNull(json, JSON_DESCRIPTION, description);

			if (employeeId == null) {
				SettingsDao settingsDao = SettingsDao
						.getInstance(applicationContext);
				Utils.putValueOnlyIfNotNull(json, JSON_EMPLOYEE_ID,
						settingsDao.getLong(Settings.KEY_EMPLOYEE_ID));
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_EMPLOYEE_ID, employeeId);
			}

			Utils.putValueOnlyIfNotNull(json, JSON_START_TIME, startTime);
			Utils.putValueOnlyIfNotNull(json, JSON_END_TIME, endTime);
			Utils.putValueOnlyIfNotNull(json, JSON_COMPLETED, completed);
			Utils.putValueOnlyIfNotNull(json, JSON_COMPLETION_TIME,
					completionTime);
			Utils.putValueOnlyIfNotNull(json, JSON_REMOTE_CREATION_TIME,
					localCreationTime);
			Utils.putValueOnlyIfNotNull(json, JSON_APPROVED, approved);
			// we shouldn't send remote modification time to server
			LocationsDao locationsDao = LocationsDao
					.getInstance(applicationContext);
			LocationDto location = locationsDao.getHistoryLocation(localId);

			if (location != null) {
				json.put("location", location.getJsonObject(applicationContext,
						Locations.PURPOSE_HISTORY));
			}
		} catch (JSONException e) {
			Log.e(TAG, "Failed to compose JSON for job: " + e.toString(), e);
			return null;
		}

		return json;
	}

	@Override
	public String toString() {
		return "JobHistory [localId=" + localId + ", remoteId=" + remoteId
				+ ", localJobId=" + localJobId + ", localFormId=" + localFormId
				+ ", typeId=" + typeId + ", stateId=" + stateId + ", title="
				+ title + ", description=" + description + ", startTime="
				+ startTime + ", endTime=" + endTime + ", employeeId="
				+ employeeId + ", localCustomerId=" + localCustomerId
				+ ", temporary=" + temporary + ", remoteJobId=" + remoteJobId
				+ ", remoteFormId=" + remoteFormId + ", remoteCustomerId="
				+ remoteCustomerId + ", completed=" + completed
				+ ", completionTime=" + completionTime + ", approved="
				+ approved + ", remoteCreationTime=" + remoteCreationTime
				+ ", localCreationTime=" + localCreationTime
				+ ", localModificationTime=" + localModificationTime + "]";
	}

	public void load(Job job) {
		localJobId = job.getLocalId();
		remoteJobId = job.getRemoteId();
		typeId = job.getTypeId();
		stateId = job.getStateId();
		employeeId = job.getEmployeeId();
		localCustomerId = job.getLocalCustomerId();
		remoteCustomerId = job.getRemoteCustomerId();
		title = job.getTitle();
		description = job.getDescription();
		startTime = job.getStartTime();
		endTime = job.getEndTime();
		completed = job.getCompleted();
		completionTime = job.getCompletionTime();
		approved = job.getApproved();
		temporary = job.getTemporary();
	}

}

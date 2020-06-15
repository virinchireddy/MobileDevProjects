package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dao.WorkFlowStatusDao;
import in.spoors.effort1.provider.EffortProvider.Settings;
import in.spoors.effort1.provider.EffortProvider.WorkFlowStatus;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class WorkFlowStatusDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "WorkFlowStatusDto";

	private Long localId;
	private Long remotelId;
	private Long workFlowId;
	private Long formSpecId;
	private Long localFormId;
	private Long remoteFormId;
	// private String formIdentifier;
	private Long createdBy;
	private Long modifiedBy;
	private Long approvedBy;
	private String createdByName;
	private String modifiedByName;
	private String approvedByName;
	private String stageName;
	private Long previousRank;
	private Long currentRank;
	private Long nextRank;
	private Date createdTime;
	private Date modifiedTime;
	private Date approvedTime;
	private Long status;
	private String statusMessage;
	private String remarks;
	private Boolean dirty;
	private Date localCreationTime;
	private Date localModificationTime;
	private Long stageType;
	private Boolean temparory;
	private Long empGroupId;
	private Long managerRank;

	public static final String JSON_LOCAL_ID = "clientWorkflowStatusId";
	public static final String JSON_REMOTE_ID = "workflowStatusId";
	public static final String JSON_WORK_FLOW_ID = "workFlowId";
	public static final String JSON_FORM_SPEC_ID = "formSpecId";
	public static final String JSON_CLIENT_FORM_ID = "clientFormId";
	public static final String JSON_FORM_ID = "formId";
	public static final String JSON_FORM_IDENTIFIER = "formIdentifier";
	public static final String JSON_CREATED_BY = "submittedBy";
	public static final String JSON_MODIFIED_BY = "modifiedBy";
	public static final String JSON_APPROVED_BY = "approvedBy";
	public static final String JSON_CREATED_BY_NAME = "submittedByName";
	public static final String JSON_MODIFIED_BY_NAME = "modifiedByName";
	public static final String JSON_APPROVED_BY_NAME = "approvedByName";
	public static final String JSON_STAGE_TYPE = "stageType";
	public static final String JSON_STAGE_NAME = "stageName";
	public static final String JSON_EMP_GROUP_ID = "empgroupId";
	public static final String JSON_MANAGER_RANK = "managerRank";
	public static final String JSON_PREVIOUS_RANK = "previousRank";
	public static final String JSON_CURRENT_RANK = "currentRank";
	public static final String JSON_NEXT_RANK = "nextRank";
	public static final String JSON_CREATED_TIME = "createdTime";
	public static final String JSON_MODIFIED_TIME = "modifiedTime";
	public static final String JSON_APPROVED_TIME = "approvedTime";
	public static final String JSON_STATUS = "status";
	public static final String JSON_STATUS_MESSAGE = "statusMessage";
	public static final String JSON_REMARKS = "remarks";

	public static final long STATUS_WAITING = 0;
	public static final long STATUS_APPROVED = 1;
	public static final long STATUS_RESUBMITTED = 2;
	public static final long STATUS_CANCELLED = 3;
	public static final long STATUS_REJECTED = -1;

	public static final long STAGE_TYPE_ROLE_BASED = 1;
	public static final long STAGE_TYPE_GROUP_BASED = 2;
	public static final long STAGE_TYPE_HIRARCHIAL_BASED = 3;

	public static WorkFlowStatusDto parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {
		WorkFlowStatusDto workFlowStatus = new WorkFlowStatusDto();
		workFlowStatus.localId = Utils.getLong(json, JSON_LOCAL_ID);
		workFlowStatus.remotelId = Utils.getLong(json, JSON_REMOTE_ID);
		workFlowStatus.workFlowId = Utils.getLong(json, JSON_WORK_FLOW_ID);
		workFlowStatus.remoteFormId = Utils.getLong(json, JSON_FORM_ID);
		workFlowStatus.stageType = Utils.getLong(json, JSON_STAGE_TYPE);

		if (workFlowStatus.remoteFormId != null) {
			FormsDao formsDao = FormsDao.getInstance(applicationContext);
			workFlowStatus.localFormId = formsDao
					.getLocalId(workFlowStatus.remoteFormId);
		} else {
			workFlowStatus.localFormId = Utils.getLong(json,
					JSON_CLIENT_FORM_ID);
		}

		workFlowStatus.createdBy = Utils.getLong(json, JSON_CREATED_BY);
		workFlowStatus.modifiedBy = Utils.getLong(json, JSON_MODIFIED_BY);
		workFlowStatus.approvedBy = Utils.getLong(json, JSON_APPROVED_BY);
		workFlowStatus.createdByName = Utils.getString(json,
				JSON_CREATED_BY_NAME);
		workFlowStatus.modifiedByName = Utils.getString(json,
				JSON_MODIFIED_BY_NAME);
		workFlowStatus.approvedByName = Utils.getString(json,
				JSON_APPROVED_BY_NAME);
		workFlowStatus.stageName = Utils.getString(json, JSON_STAGE_NAME);
		workFlowStatus.previousRank = Utils.getLong(json, JSON_PREVIOUS_RANK);
		workFlowStatus.currentRank = Utils.getLong(json, JSON_CURRENT_RANK);
		workFlowStatus.nextRank = Utils.getLong(json, JSON_NEXT_RANK);
		workFlowStatus.createdTime = Utils.getDate(json, JSON_CREATED_TIME);
		workFlowStatus.modifiedTime = Utils.getDate(json, JSON_MODIFIED_TIME);
		workFlowStatus.approvedTime = Utils.getDate(json, JSON_APPROVED_TIME);
		workFlowStatus.empGroupId = Utils.getLong(json, JSON_EMP_GROUP_ID);
		workFlowStatus.managerRank = Utils.getLong(json, JSON_MANAGER_RANK);
		workFlowStatus.status = Utils.getLong(json, JSON_STATUS);
		workFlowStatus.statusMessage = Utils.getString(json,
				JSON_STATUS_MESSAGE);
		workFlowStatus.remarks = Utils.getString(json, JSON_REMARKS);
		workFlowStatus.dirty = false;
		workFlowStatus.temparory = null;
		WorkFlowStatusDao workFlowStatusDao = WorkFlowStatusDao
				.getInstance(applicationContext);
		WorkFlowStatusDto localworkFlowStatus = workFlowStatusDao
				.getWorkFlowStatusWithRemoteId(workFlowStatus.getRemotelId());
		if (localworkFlowStatus != null
				&& localworkFlowStatus.getDirty() != null
				&& localworkFlowStatus.getDirty() == true
				&& (localworkFlowStatus.getStatus() != null
						&& localworkFlowStatus.getStatus() == WorkFlowStatusDto.STATUS_CANCELLED || localworkFlowStatus
						.getStatus() == WorkFlowStatusDto.STATUS_RESUBMITTED)
				&& (localworkFlowStatus.getStatus() != null
						&& workFlowStatus.getStatus() != null && localworkFlowStatus
						.getStatus() != workFlowStatus.getStatus())) {

			workFlowStatus.setStatus(localworkFlowStatus.getStatus());
			workFlowStatus.setDirty(localworkFlowStatus.getDirty());
			workFlowStatus.setRemarks(localworkFlowStatus.getRemarks());
			workFlowStatus.setLocalCreationTime(localworkFlowStatus
					.getLocalCreationTime());
			workFlowStatus.setLocalModificationTime(localworkFlowStatus
					.getLocalModificationTime());
			workFlowStatus.setStatusMessage(localworkFlowStatus
					.getStatusMessage());
			workFlowStatus.setStageType(localworkFlowStatus.getStageType());
			workFlowStatus.setTemparory(localworkFlowStatus.getTemparory());
			workFlowStatus.setEmpGroupId(localworkFlowStatus.getEmpGroupId());
			workFlowStatus.setManagerRank(localworkFlowStatus.getManagerRank());
		}

		//
		// local dirty -false , server data tho update
		//
		return workFlowStatus;
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
		if (localId != null) {
			Utils.putNullOrValue(values, WorkFlowStatus._ID, localId);
		}

		Utils.putNullOrValue(values, WorkFlowStatus.REMOTE_ID, remotelId);
		Utils.putNullOrValue(values, WorkFlowStatus.LOCAL_FORM_ID, localFormId);
		Utils.putNullOrValue(values, WorkFlowStatus.WORK_FLOW_ID, workFlowId);
		;
		Utils.putNullOrValue(values, WorkFlowStatus.CREATED_BY, createdBy);
		Utils.putNullOrValue(values, WorkFlowStatus.MODIFIED_BY, modifiedBy);
		Utils.putNullOrValue(values, WorkFlowStatus.APPROVED_BY, approvedBy);
		Utils.putNullOrValue(values, WorkFlowStatus.CREATED_BY_NAME,
				createdByName);
		Utils.putNullOrValue(values, WorkFlowStatus.MODIFIED_BY_NAME,
				modifiedByName);
		Utils.putNullOrValue(values, WorkFlowStatus.APPROVED_BY_NAME,
				approvedByName);
		Utils.putNullOrValue(values, WorkFlowStatus.CREATED_TIME, createdTime);
		Utils.putNullOrValue(values, WorkFlowStatus.MODIFIED_TIME, modifiedTime);
		Utils.putNullOrValue(values, WorkFlowStatus.APPROVED_TIME, approvedTime);
		Utils.putNullOrValue(values, WorkFlowStatus.STAGE_TYPE, stageType);
		Utils.putNullOrValue(values, WorkFlowStatus.STAGE_NAME, stageName);
		Utils.putNullOrValue(values, WorkFlowStatus.PREVIOUS_RANK, previousRank);
		Utils.putNullOrValue(values, WorkFlowStatus.CURRENT_RANK, currentRank);
		Utils.putNullOrValue(values, WorkFlowStatus.NEXT_RANK, nextRank);
		Utils.putNullOrValue(values, WorkFlowStatus.STATUS, status);
		Utils.putNullOrValue(values, WorkFlowStatus.STATUS_MESSAGE,
				statusMessage);
		Utils.putNullOrValue(values, WorkFlowStatus.REMARKS, remarks);
		Utils.putNullOrValue(values, WorkFlowStatus.DIRTY, dirty);
		Utils.putNullOrValue(values, WorkFlowStatus.TEMPORARY, temparory);
		Utils.putNullOrValue(values, WorkFlowStatus.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, WorkFlowStatus.LOCAL_MODIFICATION_TIME,
				localModificationTime);
		Utils.putNullOrValue(values, WorkFlowStatus.EMP_GROUP_ID, empGroupId);
		Utils.putNullOrValue(values, WorkFlowStatus.MANAGER_RANK, managerRank);
		return values;
	}

	public void load(Cursor cursor, Context context) {

		localId = cursor.isNull(WorkFlowStatus._ID_INDEX) ? null : cursor
				.getLong(WorkFlowStatus._ID_INDEX);
		remotelId = cursor.isNull(WorkFlowStatus.REMOTE_ID_INDEX) ? null
				: cursor.getLong(WorkFlowStatus.REMOTE_ID_INDEX);
		localFormId = cursor.isNull(WorkFlowStatus.LOCAL_FORM_ID_INDEX) ? null
				: cursor.getLong(WorkFlowStatus.LOCAL_FORM_ID_INDEX);
		workFlowId = cursor.isNull(WorkFlowStatus.WORK_FLOW_ID_INDEX) ? null
				: cursor.getLong(WorkFlowStatus.WORK_FLOW_ID_INDEX);

		createdBy = cursor.isNull(WorkFlowStatus.CREATED_BY_INDEX) ? null
				: cursor.getLong(WorkFlowStatus.CREATED_BY_INDEX);
		modifiedBy = cursor.isNull(WorkFlowStatus.MODIFIED_BY_INDEX) ? null
				: cursor.getLong(WorkFlowStatus.MODIFIED_BY_INDEX);
		approvedBy = cursor.isNull(WorkFlowStatus.APPROVED_BY_INDEX) ? null
				: cursor.getLong(WorkFlowStatus.APPROVED_BY_INDEX);
		createdByName = cursor.getString(WorkFlowStatus.CREATED_BY_NAME_INDEX);
		modifiedByName = cursor
				.getString(WorkFlowStatus.MODIFIED_BY_NAME_INDEX);
		approvedByName = cursor
				.getString(WorkFlowStatus.APPROVED_BY_NAME_INDEX);
		createdTime = cursor.isNull(WorkFlowStatus.CREATED_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(WorkFlowStatus.CREATED_TIME_INDEX));
		modifiedTime = cursor.isNull(WorkFlowStatus.MODIFIED_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(WorkFlowStatus.MODIFIED_TIME_INDEX));
		approvedTime = cursor.isNull(WorkFlowStatus.APPROVED_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(WorkFlowStatus.APPROVED_TIME_INDEX));
		stageType = cursor.isNull(WorkFlowStatus.STAGE_TYPE_INDEX) ? null
				: cursor.getLong(WorkFlowStatus.STAGE_TYPE_INDEX);
		stageName = cursor.getString(WorkFlowStatus.STAGE_NAME_INDEX);
		previousRank = cursor.isNull(WorkFlowStatus.PREVIOUS_RANK_INDEX) ? null
				: cursor.getLong(WorkFlowStatus.PREVIOUS_RANK_INDEX);
		currentRank = cursor.isNull(WorkFlowStatus.CURRENT_RANK_INDEX) ? null
				: cursor.getLong(WorkFlowStatus.CURRENT_RANK_INDEX);
		nextRank = cursor.isNull(WorkFlowStatus.NEXT_RANK_INDEX) ? null
				: cursor.getLong(WorkFlowStatus.NEXT_RANK_INDEX);
		status = cursor.isNull(WorkFlowStatus.STATUS_INDEX) ? null : cursor
				.getLong(WorkFlowStatus.STATUS_INDEX);
		statusMessage = cursor.getString(WorkFlowStatus.STATUS_MESSAGE_INDEX);
		remarks = cursor.getString(WorkFlowStatus.REMARKS_INDEX);
		dirty = cursor.isNull(WorkFlowStatus.DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(WorkFlowStatus.DIRTY_INDEX));
		localCreationTime = cursor
				.isNull(WorkFlowStatus.LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(WorkFlowStatus.LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(WorkFlowStatus.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils
						.getLocalTime(cursor
								.getString(WorkFlowStatus.LOCAL_MODIFICATION_TIME_INDEX));
		temparory = cursor.isNull(WorkFlowStatus.TEMPARORY_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(WorkFlowStatus.TEMPARORY_INDEX));
		empGroupId = cursor.isNull(WorkFlowStatus.EMP_GROUP_ID_INDEX) ? null
				: cursor.getLong(WorkFlowStatus.EMP_GROUP_ID_INDEX);
		managerRank = cursor.isNull(WorkFlowStatus.MANAGER_RANK_INDEX) ? null
				: cursor.getLong(WorkFlowStatus.MANAGER_RANK_INDEX);
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
	}

	public Long getRemotelId() {
		return remotelId;
	}

	public void setRemotelId(Long remotelId) {
		this.remotelId = remotelId;
	}

	public Long getWorkFlowId() {
		return workFlowId;
	}

	public void setWorkFlowId(Long workFlowId) {
		this.workFlowId = workFlowId;
	}

	public Long getClientFormId() {
		return localFormId;
	}

	public void setClientFormId(Long clientFormId) {
		this.localFormId = clientFormId;
	}

	public Long getRemoteFormId() {
		return remoteFormId;
	}

	public void setRemoteFormId(Long formId) {
		this.remoteFormId = formId;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Long getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(Long approvedBy) {
		this.approvedBy = approvedBy;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public String getModifiedByName() {
		return modifiedByName;
	}

	public void setModifiedByName(String modifiedByName) {
		this.modifiedByName = modifiedByName;
	}

	public String getApprovedByName() {
		return approvedByName;
	}

	public void setApprovedByName(String approvedByName) {
		this.approvedByName = approvedByName;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public Long getPreviousRank() {
		return previousRank;
	}

	public void setPreviousRank(Long previousRank) {
		this.previousRank = previousRank;
	}

	public Long getCurrentRank() {
		return currentRank;
	}

	public void setCurrentRank(Long currentRank) {
		this.currentRank = currentRank;
	}

	public Long getNextRank() {
		return nextRank;
	}

	public void setNextRank(Long nextRank) {
		this.nextRank = nextRank;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public Date getApprovedTime() {
		return approvedTime;
	}

	public void setApprovedTime(Date approvedTime) {
		this.approvedTime = approvedTime;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public Long getFormSpecId() {
		return formSpecId;
	}

	public void setFormSpecId(Long formSpecId) {
		this.formSpecId = formSpecId;
	}

	public Long getStageType() {
		return stageType;
	}

	public void setStageType(Long stageType) {
		this.stageType = stageType;
	}

	public Boolean getTemparory() {
		return temparory;
	}

	public void setTemparory(Boolean temparory) {
		this.temparory = temparory;
	}

	public Long getEmpGroupId() {
		return empGroupId;
	}

	public void setEmpGroupId(Long empGroupId) {
		this.empGroupId = empGroupId;
	}

	public Long getManagerRank() {
		return managerRank;
	}

	public void setManagerRank(Long managerRank) {
		this.managerRank = managerRank;
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
			SettingsDao settingsDao = SettingsDao
					.getInstance(applicationContext);
			if (remotelId == null) {
				json.put(JSON_LOCAL_ID, localId);
			} else {
				json.put(JSON_REMOTE_ID, remotelId);
			}
			FormsDao formsDao = FormsDao.getInstance(applicationContext);
			if (remoteFormId == null && localFormId != null) {
				remoteFormId = formsDao.getRemoteId(localFormId);
			}
			json.put(JSON_FORM_SPEC_ID, formsDao.getFormSpecId(localFormId));
			if (remoteFormId == null) {
				json.put(JSON_CLIENT_FORM_ID, localFormId);
			} else {
				json.put(JSON_FORM_ID, remoteFormId);
			}

			json.put(JSON_CREATED_BY,
					settingsDao.getLong(Settings.KEY_EMPLOYEE_ID));

			json.put(JSON_WORK_FLOW_ID, workFlowId);
			if (status != null) {
				json.put(JSON_STATUS, status);
			}
			if (remarks != null) {
				json.put(JSON_REMARKS, remarks);
			}
			if (statusMessage != null) {
				json.put(JSON_STATUS_MESSAGE, statusMessage);
			}
			Utils.putValueOnlyIfNotNull(json, JSON_CREATED_TIME, createdTime);

		} catch (JSONException e) {
			Log.e(TAG,
					"Failed to compose JSON for CustomerStatusDto: "
							+ e.toString(), e);
			return null;
		}

		return json;
	}

	@Override
	public String toString() {
		return "WorkFlowStatusDto [localId=" + localId + ", remotelId="
				+ remotelId + ", workFlowId=" + workFlowId + ", formSpecId="
				+ formSpecId + ", localFormId=" + localFormId
				+ ", remoteFormId=" + remoteFormId + ", createdBy=" + createdBy
				+ ", modifiedBy=" + modifiedBy + ", approvedBy=" + approvedBy
				+ ", createdByName=" + createdByName + ", modifiedByName="
				+ modifiedByName + ", approvedByName=" + approvedByName
				+ ", stageName=" + stageName + ", previousRank=" + previousRank
				+ ", currentRank=" + currentRank + ", nextRank=" + nextRank
				+ ", createdTime=" + createdTime + ", modifiedTime="
				+ modifiedTime + ", approvedTime=" + approvedTime + ", status="
				+ status + ", statusMessage=" + statusMessage + ", remarks="
				+ remarks + ", dirty=" + dirty + ", localCreationTime="
				+ localCreationTime + ", localModificationTime="
				+ localModificationTime + ", stageType=" + stageType
				+ ", temparory=" + temparory + "]";
	}
}

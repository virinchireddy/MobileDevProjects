package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.WorkFlowHistories;
import in.spoors.effort1.provider.EffortProvider.WorkFlowStatus;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class WorkFlowHistory implements Serializable {

	private static final long serialVersionUID = 1L;
	// private static final String TAG = "WorkFlowHistory";

	private Long id;
	private Long workFlowId;
	private Long localFormId;
	private Long remoteFormId;
	private Long createdBy;
	private Long modifiedBy;
	private Long approvedBy;
	private String createdByName;
	private String modifiedByName;
	private String approvedByName;
	private Date createdTime;
	private Date modifiedTime;
	private Date approvedTime;
	private Long previousRank;
	private Long currentRank;
	private Long nextRank;
	private String stageName;
	private Long status;
	private String statusMessage;
	private String remarks;
	private Boolean dirty;

	public static final String JSON_WORK_FLOW_HISTORY_ID = "workflowStatusHistoryId";
	public static final String JSON_WORK_FLOW_ID = "workFlowId";
	public static final String JSON_FORM_SPEC_ID = "formSpecId";
	public static final String JSON_CLIENT_FORM_ID = "clientFormId";
	public static final String JSON_FORM_ID = "formId";
	public static final String JSON_FORM_IDENTIFIER = "formIdentifier";
	public static final String JSON_CREATED_BY = "createdBy";
	public static final String JSON_MODIFIED_BY = "modifiedBy";
	public static final String JSON_APPROVED_BY = "approvedBy";
	public static final String JSON_CREATED_BY_NAME = "createdByName";
	public static final String JSON_MODIFIED_BY_NAME = "modifiedByName";
	public static final String JSON_APPROVED_BY_NAME = "approvedByName";
	public static final String JSON_STAGE_NAME = "stageName";
	public static final String JSON_PREVIOUS_RANK = "previousRank";
	public static final String JSON_CURRENT_RANK = "currentRank";
	public static final String JSON_NEXT_RANK = "nextRank";
	public static final String JSON_CREATED_TIME = "createdTime";
	public static final String JSON_MODIFIED_TIME = "modifiedTime";
	public static final String JSON_APPROVED_TIME = "approvedTime";
	public static final String JSON_STATUS = "status";
	public static final String JSON_STATUS_MESSAGE = "statusMessage";
	public static final String JSON_REMARKS = "remarks";

	public static WorkFlowHistory parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {
		WorkFlowHistory workFlowHistory = new WorkFlowHistory();
		workFlowHistory.id = Utils.getLong(json, JSON_WORK_FLOW_HISTORY_ID);
		workFlowHistory.workFlowId = Utils.getLong(json, JSON_WORK_FLOW_ID);
		workFlowHistory.localFormId = Utils.getLong(json, JSON_CLIENT_FORM_ID);
		workFlowHistory.remoteFormId = Utils.getLong(json, JSON_FORM_ID);
		workFlowHistory.createdBy = Utils.getLong(json, JSON_CREATED_BY);
		workFlowHistory.modifiedBy = Utils.getLong(json, JSON_MODIFIED_BY);
		workFlowHistory.approvedBy = Utils.getLong(json, JSON_APPROVED_BY);
		workFlowHistory.createdByName = Utils.getString(json,
				JSON_CREATED_BY_NAME);
		workFlowHistory.modifiedByName = Utils.getString(json,
				JSON_MODIFIED_BY_NAME);
		workFlowHistory.approvedByName = Utils.getString(json,
				JSON_APPROVED_BY_NAME);
		workFlowHistory.stageName = Utils.getString(json, JSON_STAGE_NAME);
		workFlowHistory.previousRank = Utils.getLong(json, JSON_PREVIOUS_RANK);
		workFlowHistory.currentRank = Utils.getLong(json, JSON_CURRENT_RANK);
		workFlowHistory.nextRank = Utils.getLong(json, JSON_NEXT_RANK);
		workFlowHistory.createdTime = Utils.getDate(json, JSON_CREATED_TIME);
		workFlowHistory.modifiedTime = Utils.getDate(json, JSON_MODIFIED_TIME);
		workFlowHistory.approvedTime = Utils.getDate(json, JSON_APPROVED_TIME);
		workFlowHistory.status = Utils.getLong(json, JSON_STATUS);
		workFlowHistory.statusMessage = Utils.getString(json,
				JSON_STATUS_MESSAGE);
		workFlowHistory.remarks = Utils.getString(json, JSON_REMARKS);
		return workFlowHistory;
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

		Utils.putNullOrValue(values, WorkFlowHistories._ID, id);
		Utils.putNullOrValue(values, WorkFlowHistories.WORK_FLOW_ID, workFlowId);
		Utils.putNullOrValue(values, WorkFlowHistories.LOCAL_FORM_ID,
				localFormId);
		Utils.putNullOrValue(values, WorkFlowHistories.CREATED_BY, createdBy);
		Utils.putNullOrValue(values, WorkFlowHistories.MODIFIED_BY, modifiedBy);
		Utils.putNullOrValue(values, WorkFlowHistories.APPROVED_BY, approvedBy);
		Utils.putNullOrValue(values, WorkFlowHistories.CREATED_BY_NAME,
				createdByName);
		Utils.putNullOrValue(values, WorkFlowHistories.MODIFIED_BY_NAME,
				modifiedByName);
		Utils.putNullOrValue(values, WorkFlowHistories.APPROVED_BY_NAME,
				approvedByName);
		Utils.putNullOrValue(values, WorkFlowHistories.CREATED_TIME,
				createdTime);
		Utils.putNullOrValue(values, WorkFlowHistories.MODIFIED_TIME,
				modifiedTime);
		Utils.putNullOrValue(values, WorkFlowHistories.APPROVED_TIME,
				approvedTime);
		Utils.putNullOrValue(values, WorkFlowHistories.STAGE_NAME, stageName);
		Utils.putNullOrValue(values, WorkFlowStatus.PREVIOUS_RANK, previousRank);
		Utils.putNullOrValue(values, WorkFlowStatus.CURRENT_RANK, currentRank);
		Utils.putNullOrValue(values, WorkFlowStatus.NEXT_RANK, nextRank);
		Utils.putNullOrValue(values, WorkFlowHistories.STATUS, status);
		Utils.putNullOrValue(values, WorkFlowHistories.STATUS_MESSAGE,
				statusMessage);
		Utils.putNullOrValue(values, WorkFlowHistories.REMARKS, remarks);

		return values;
	}

	public void load(Cursor cursor) {

		id = cursor.isNull(WorkFlowHistories._ID_INDEX) ? null : cursor
				.getLong(WorkFlowHistories._ID_INDEX);
		workFlowId = cursor.isNull(WorkFlowHistories.WORK_FLOW_ID_INDEX) ? null
				: cursor.getLong(WorkFlowHistories.WORK_FLOW_ID_INDEX);
		localFormId = cursor.isNull(WorkFlowHistories.LOCAL_FORM_ID_INDEX) ? null
				: cursor.getLong(WorkFlowHistories.LOCAL_FORM_ID_INDEX);
		createdBy = cursor.isNull(WorkFlowHistories.CREATED_BY_INDEX) ? null
				: cursor.getLong(WorkFlowHistories.CREATED_BY_INDEX);
		modifiedBy = cursor.isNull(WorkFlowHistories.MODIFIED_BY_INDEX) ? null
				: cursor.getLong(WorkFlowHistories.MODIFIED_BY_INDEX);
		approvedBy = cursor.isNull(WorkFlowHistories.APPROVED_BY_INDEX) ? null
				: cursor.getLong(WorkFlowHistories.APPROVED_BY_INDEX);
		createdByName = cursor
				.getString(WorkFlowHistories.CREATED_BY_NAME_INDEX);
		modifiedByName = cursor
				.getString(WorkFlowHistories.MODIFIED_BY_NAME_INDEX);
		approvedByName = cursor
				.getString(WorkFlowHistories.APPROVED_BY_NAME_INDEX);
		createdTime = cursor.isNull(WorkFlowHistories.CREATED_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(WorkFlowHistories.CREATED_TIME_INDEX));
		modifiedTime = cursor.isNull(WorkFlowHistories.MODIFIED_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(WorkFlowHistories.MODIFIED_TIME_INDEX));
		approvedTime = cursor.isNull(WorkFlowHistories.APPROVED_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(WorkFlowHistories.APPROVED_TIME_INDEX));
		stageName = cursor.getString(WorkFlowHistories.STAGE_NAME_INDEX);
		previousRank = cursor.isNull(WorkFlowHistories.PREVIOUS_RANK_INDEX) ? null
				: cursor.getLong(WorkFlowHistories.PREVIOUS_RANK_INDEX);
		currentRank = cursor.isNull(WorkFlowHistories.CURRENT_RANK_INDEX) ? null
				: cursor.getLong(WorkFlowHistories.CURRENT_RANK_INDEX);
		nextRank = cursor.isNull(WorkFlowHistories.NEXT_RANK_INDEX) ? null
				: cursor.getLong(WorkFlowHistories.NEXT_RANK_INDEX);
		status = cursor.isNull(WorkFlowHistories.STATUS_INDEX) ? null : cursor
				.getLong(WorkFlowHistories.STATUS_INDEX);
		statusMessage = cursor
				.getString(WorkFlowHistories.STATUS_MESSAGE_INDEX);
		remarks = cursor.getString(WorkFlowHistories.REMARKS_INDEX);

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
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

	@Override
	public String toString() {
		return "WorkFlowHistory [id=" + id + ", workFlowId=" + workFlowId
				+ ", localFormId=" + localFormId + ", remoteFormId="
				+ remoteFormId + ", createdBy=" + createdBy + ", modifiedBy="
				+ modifiedBy + ", approvedBy=" + approvedBy
				+ ", createdByName=" + createdByName + ", modifiedByName="
				+ modifiedByName + ", approvedByName=" + approvedByName
				+ ", createdTime=" + createdTime + ", modifiedTime="
				+ modifiedTime + ", approvedTime=" + approvedTime
				+ ", previousRank=" + previousRank + ", currentRank="
				+ currentRank + ", nextRank=" + nextRank + ", stageName="
				+ stageName + ", status=" + status + ", statusMessage="
				+ statusMessage + ", remarks=" + remarks + ", dirty=" + dirty
				+ "]";
	}

}

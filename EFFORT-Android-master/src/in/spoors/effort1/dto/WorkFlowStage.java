package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.WorkFlowStages;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class WorkFlowStage implements Serializable {

	private static final long serialVersionUID = 1L;

	// private static final String TAG = "WorkFlowStage";

	private Long id;
	private Long workFlowId;
	private Boolean deleted;
	private String stageName;

	public static final String JSON_ID = "stageId";
	public static final String JSON_WORK_FLOW_ID = "workflowId";
	public static final String JSON_STAGE_NAME = "stageName";
	public static final String JSON_DELETED = "deleted";

	public static WorkFlowStage parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {
		WorkFlowStage workFlowStage = new WorkFlowStage();
		workFlowStage.id = Utils.getLong(json, JSON_ID);
		workFlowStage.stageName = Utils.getString(json, JSON_STAGE_NAME);
		workFlowStage.workFlowId = Utils.getLong(json, JSON_WORK_FLOW_ID);
		workFlowStage.deleted = Utils.getBoolean(json, JSON_DELETED);
		return workFlowStage;
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

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
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
		Utils.putNullOrValue(values, WorkFlowStages._ID, id);
		Utils.putNullOrValue(values, WorkFlowStages.WORK_FLOW_ID, workFlowId);
		Utils.putNullOrValue(values, WorkFlowStages.STAGE_NAME, stageName);
		Utils.putNullOrValue(values, WorkFlowStages.DELETED, deleted);
		return values;
	}

	public void load(Cursor cursor) {

		id = cursor.isNull(WorkFlowStages._ID_INDEX) ? null : cursor
				.getLong(WorkFlowStages._ID_INDEX);
		stageName = cursor.getString(WorkFlowStages.STAGE_NAME_INDEX);

		workFlowId = cursor.isNull(WorkFlowStages.WORK_FLOW_ID_INDEX) ? null
				: cursor.getLong(WorkFlowStages.WORK_FLOW_ID_INDEX);
		deleted = cursor.isNull(WorkFlowStages.DELETED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(WorkFlowStages.DELETED_INDEX));
	}

}

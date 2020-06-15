package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.WorkFlowFormSpecMappings;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class WorkFlowFormSpecMapping implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private Long workflowId;
	private String mappingEntityId;
	private Long entityType;
	private Boolean checked;

	public static final String JSON_ID = "id";
	public static final String JSON_WORKFLOW_ID = "workflowId";
	public static final String JSON_MAPPING_ENTITY_ID = "entityId";
	public static final String JSON_ENTITY_TYPE = "entityType";

	public static WorkFlowFormSpecMapping parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {
		WorkFlowFormSpecMapping workFlowSpec = new WorkFlowFormSpecMapping();

		workFlowSpec.id = Utils.getLong(json, JSON_ID);
		workFlowSpec.workflowId = Utils.getLong(json, JSON_WORKFLOW_ID);
		workFlowSpec.mappingEntityId = Utils.getString(json,
				JSON_MAPPING_ENTITY_ID);
		workFlowSpec.entityType = Utils.getLong(json, JSON_ENTITY_TYPE);
		workFlowSpec.checked = true;
		return workFlowSpec;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}

	public String getMappingEntityId() {
		return mappingEntityId;
	}

	public void setMappingEntityId(String mappingEntityId) {
		this.mappingEntityId = mappingEntityId;
	}

	public Long getEntityType() {
		return entityType;
	}

	public void setEntityType(Long entityType) {
		this.entityType = entityType;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
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
		if (id != null) {
			Utils.putNullOrValue(values, WorkFlowFormSpecMappings._ID, id);
		}

		Utils.putNullOrValue(values, WorkFlowFormSpecMappings.WORK_FLOW_ID,
				workflowId);
		Utils.putNullOrValue(values,
				WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID,
				mappingEntityId);
		Utils.putNullOrValue(values, WorkFlowFormSpecMappings.ENTITY_TYPE,
				entityType);
		Utils.putNullOrValue(values, WorkFlowFormSpecMappings.CHECKED, checked);

		return values;
	}

	public void load(Cursor cursor) {

		id = cursor.isNull(WorkFlowFormSpecMappings._ID_INDEX) ? null : cursor
				.getLong(WorkFlowFormSpecMappings._ID_INDEX);
		workflowId = cursor.isNull(WorkFlowFormSpecMappings.WORK_FLOW_ID_INDEX) ? null
				: cursor.getLong(WorkFlowFormSpecMappings.WORK_FLOW_ID_INDEX);
		mappingEntityId = cursor
				.getString(WorkFlowFormSpecMappings.WORKFLOW_MAP_ENTITY_ID_INDEX);
		entityType = cursor.getLong(WorkFlowFormSpecMappings.ENTITY_TYPE_INDEX);

		checked = cursor.isNull(WorkFlowFormSpecMappings.CHECKED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(WorkFlowFormSpecMappings.CHECKED_INDEX));
	}

	@Override
	public String toString() {
		return "WorkFlowFormSpecMapping [id=" + id + ", workflowId="
				+ workflowId + ", mappingEntityId=" + mappingEntityId
				+ ", entityType=" + entityType + ", checked=" + checked + "]";
	}

}

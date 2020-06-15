package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.WorkFlowSpecs;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class WorkFlowSpec implements Serializable {

	private static final long serialVersionUID = 1L;

	// private static final String TAG = "WorkFlowSpec";

	private Long id;
	private String workFlowName;
	private Date createdTime;
	private Date modifiedTime;
	private Long createdBy;
	// private String formSpecUniqueId;
	// private Boolean checked;
	private Boolean deleted;
	private Boolean editable;
	private Boolean hasRoleBasedStages;
	// private String formTemplateName;

	public static final String JSON_ID = "workflowId";
	public static final String JSON_WORK_FLOW_NAME = "workflowName";
	public static final String JSON_CREATED_TIME = "createdTime";
	public static final String JSON_CREATED_BY = "createdBy";
	// public static final String JSON_FORM_SPEC_ID = "formSpecId";
	public static final String JSON_EDITABLE = "editable";
	public static final String JSON_FORM_SPEC_UNIQUE_ID = "formSpecUniqueId";
	public static final String JSON_DELETED = "deleted";
	public static final String JSON_HAS_ROLE_BASED_STAGES = "hasRoleBasedStages";

	public static WorkFlowSpec parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		WorkFlowSpec workFlowSpec = new WorkFlowSpec();
		workFlowSpec.id = Utils.getLong(json, JSON_ID);
		workFlowSpec.workFlowName = Utils.getString(json, JSON_WORK_FLOW_NAME);
		workFlowSpec.createdTime = Utils.getDate(json, JSON_CREATED_TIME);
		workFlowSpec.createdBy = Utils.getLong(json, JSON_CREATED_BY);
		/*
		 * workFlowSpec.formSpecUniqueId = Utils.getString(json,
		 * JSON_FORM_SPEC_UNIQUE_ID);
		 */
		// workFlowSpec.checked = true;
		workFlowSpec.deleted = Utils.getBoolean(json, JSON_DELETED);
		workFlowSpec.editable = Utils.getBoolean(json, JSON_EDITABLE);
		workFlowSpec.hasRoleBasedStages = Utils.getBoolean(json,
				JSON_HAS_ROLE_BASED_STAGES);

		return workFlowSpec;
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
		Utils.putNullOrValue(values, WorkFlowSpecs._ID, id);
		Utils.putNullOrValue(values, WorkFlowSpecs.WORK_FLOW_NAME, workFlowName);
		Utils.putNullOrValue(values, WorkFlowSpecs.CREATED_TIME, createdTime);
		Utils.putNullOrValue(values, WorkFlowSpecs.CREATED_BY, createdBy);
		/*
		 * Utils.putNullOrValue(values, WorkFlowSpecs.FORM_SPE_UNIQUE_ID,
		 * formSpecUniqueId);
		 */
		// Utils.putNullOrValue(values, WorkFlowSpecs.CHECKED, checked);
		Utils.putNullOrValue(values, WorkFlowSpecs.DELETED, deleted);
		Utils.putNullOrValue(values, WorkFlowSpecs.EDITABLE, editable);
		Utils.putNullOrValue(values, WorkFlowSpecs.HAS_ROLE_BASED_STAGES,
				hasRoleBasedStages);
		return values;
	}

	public void load(Cursor cursor) {

		id = cursor.isNull(WorkFlowSpecs._ID_INDEX) ? null : cursor
				.getLong(WorkFlowSpecs._ID_INDEX);
		workFlowName = cursor.getString(WorkFlowSpecs.WORK_FLOW_NAME_INDEX);

		createdTime = cursor.isNull(WorkFlowSpecs.CREATED_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(WorkFlowSpecs.CREATED_TIME_INDEX));

		createdBy = cursor.getLong(WorkFlowSpecs.CREATED_BY_INDEX);

		/*
		 * formSpecUniqueId = cursor
		 * .isNull(WorkFlowSpecs.FORM_SPEC_UNIQUE_ID_INDEX) ? null :
		 * cursor.getString(WorkFlowSpecs.FORM_SPEC_UNIQUE_ID_INDEX);
		 */
		// checked = cursor.isNull(WorkFlowSpecs.CHECKED_INDEX) ? null : Boolean
		// .parseBoolean(cursor.getString(WorkFlowSpecs.CHECKED_INDEX));
		deleted = cursor.isNull(WorkFlowSpecs.DELETED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(WorkFlowSpecs.DELETED_INDEX));
		editable = cursor.isNull(WorkFlowSpecs.EDITABLE_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(WorkFlowSpecs.EDITABLE_INDEX));
		hasRoleBasedStages = cursor
				.isNull(WorkFlowSpecs.HAS_ROLE_BASED_STAGES_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(WorkFlowSpecs.HAS_ROLE_BASED_STAGES_INDEX));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWorkFlowName() {
		return workFlowName;
	}

	public void setWorkFlowName(String workFlowName) {
		this.workFlowName = workFlowName;
	}

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
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

	// public Boolean getChecked() {
	// return checked;
	// }
	//
	// public void setChecked(Boolean checked) {
	// this.checked = checked;
	// }

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getEditable() {
		return editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}

	// public String getFormSpecUniqueId() {
	// return formSpecUniqueId;
	// }

	// public void setFormSpecUniqueId(String formSpecUniqueId) {
	// this.formSpecUniqueId = formSpecUniqueId;
	// }

	public Boolean getHasRoleBasedStages() {
		return hasRoleBasedStages;
	}

	public void setHasRoleBasedStages(Boolean canSubmit) {
		this.hasRoleBasedStages = canSubmit;
	}

	@Override
	public String toString() {
		return "WorkFlowSpec [id=" + id + ", workFlowName=" + workFlowName
				+ ", createdTime=" + createdTime + ", modifiedTime="
				+ modifiedTime + ", createdBy=" + createdBy + ", deleted="
				+ deleted + ", editable=" + editable + ", hasRoleBasedStages="
				+ hasRoleBasedStages + "]";
	}

	// public String getFormTemplateName() {
	// return formTemplateName;
	// }
	//
	// public void setFormTemplateName(String formTemplateName) {
	// this.formTemplateName = formTemplateName;
	// }
}

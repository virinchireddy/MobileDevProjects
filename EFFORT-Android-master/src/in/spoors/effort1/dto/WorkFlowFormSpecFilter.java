package in.spoors.effort1.dto;

import in.spoors.effort1.provider.EffortProvider.WorkFlowFormSpecFilterView;

import java.io.Serializable;

import android.database.Cursor;

public class WorkFlowFormSpecFilter implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private Long workFlowId;
	private Boolean checked;
	private String entityMapId;
	private String formTemplateName;
	private Long entityType;

	public void load(Cursor cursor) {
		id = cursor.isNull(WorkFlowFormSpecFilterView._ID_INDEX) ? null
				: cursor.getLong(WorkFlowFormSpecFilterView._ID_INDEX);
		workFlowId = cursor
				.isNull(WorkFlowFormSpecFilterView.WORK_FLOW_ID_INDEX) ? null
				: cursor.getLong(WorkFlowFormSpecFilterView.WORK_FLOW_ID_INDEX);
		formTemplateName = cursor
				.getString(WorkFlowFormSpecFilterView.FORM_TEMPLATE_NAME_INDEX);

		entityMapId = cursor
				.isNull(WorkFlowFormSpecFilterView.WORKFLOW_MAP_ENTITY_ID_INDEX) ? null
				: cursor.getString(WorkFlowFormSpecFilterView.WORKFLOW_MAP_ENTITY_ID_INDEX);

		checked = cursor.isNull(WorkFlowFormSpecFilterView.CHECKED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(WorkFlowFormSpecFilterView.CHECKED_INDEX));

		entityType = cursor
				.isNull(WorkFlowFormSpecFilterView.ENTITY_TYPE_INDEX) ? null
				: cursor.getLong(WorkFlowFormSpecFilterView.ENTITY_TYPE_INDEX);

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

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public String getEntityMapId() {
		return entityMapId;
	}

	public void setEntityMapId(String entityMapId) {
		this.entityMapId = entityMapId;
	}

	public String getFormTemplateName() {
		return formTemplateName;
	}

	public void setFormTemplateName(String formTemplateName) {
		this.formTemplateName = formTemplateName;
	}

	public Long getEntityType() {
		return entityType;
	}

	public void setEntityType(Long entityType) {
		this.entityType = entityType;
	}

	@Override
	public String toString() {
		return "WorkFlowFormSpecFilter [workFlowId=" + workFlowId
				+ ", checked=" + checked + ", entityMapId=" + entityMapId
				+ ", formTemplateName=" + formTemplateName + ", entityType="
				+ entityType + "]";
	}

}

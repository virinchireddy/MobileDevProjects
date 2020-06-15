package in.spoors.effort1.dto;

import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.provider.EffortProvider.EntityFieldSpecs;
import in.spoors.effort1.provider.EffortProvider.FieldsView;

import java.io.Serializable;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

public class ViewField implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "Field";

	protected Long fieldId;
	protected Long formSpecId;
	protected Long fieldSpecId;
	protected Long localFormId;
	protected Long remoteFormId;
	protected String localValue;
	protected String remoteValue;
	protected String label;
	protected Integer type;
	protected Boolean identifier;
	protected Boolean required;
	protected Integer displayOrder;
	protected transient View view;
	protected FormFile file;
	protected String typeExtra;
	protected Boolean computed;
	protected Boolean enabledBarcode;
	protected String formula;
	protected String selector;
	protected Integer pageId;
	private Integer visible;
	private Integer editable;
	private Long minValue;
	private Long maxValue;
	protected Boolean defaultField;
	private HashMap<String, String> formulaFieldValues;
	boolean isItVisibleByCriteria;
	boolean isItEnabledByCriteria;
	
	public void load(Cursor cursor, Context applicationContext) {
		FormsDao formsDao = FormsDao.getInstance(applicationContext);

		fieldId = cursor.isNull(FieldsView.FIELD_ID_INDEX) ? null : cursor
				.getLong(FieldsView.FIELD_ID_INDEX);
		formSpecId = cursor.isNull(FieldsView.FORM_SPEC_ID_INDEX) ? null
				: cursor.getLong(FieldsView.FORM_SPEC_ID_INDEX);
		fieldSpecId = cursor.isNull(FieldsView.FIELD_SPEC_ID_INDEX) ? null
				: cursor.getLong(FieldsView.FIELD_SPEC_ID_INDEX);
		localFormId = cursor.isNull(FieldsView.LOCAL_FORM_ID_INDEX) ? null
				: cursor.getLong(FieldsView.LOCAL_FORM_ID_INDEX);
		remoteFormId = formsDao.getRemoteId(localFormId);
		localValue = cursor.getString(FieldsView.LOCAL_VALUE_INDEX);
		remoteValue = cursor.getString(FieldsView.REMOTE_VALUE_INDEX);
		label = cursor.getString(FieldsView.LABEL_INDEX);
		type = cursor.isNull(FieldsView.TYPE_INDEX) ? null : cursor
				.getInt(FieldsView.TYPE_INDEX);
		identifier = cursor.isNull(FieldsView.IDENTIFIER_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(FieldsView.IDENTIFIER_INDEX));
		required = cursor.isNull(FieldsView.REQUIRED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(FieldsView.REQUIRED_INDEX));
		displayOrder = cursor.isNull(FieldsView.REMOTE_VALUE_INDEX) ? null
				: cursor.getInt(FieldsView.REMOTE_VALUE_INDEX);
		typeExtra = cursor.getString(FieldsView.TYPE_EXTRA_INDEX);
		computed = cursor.isNull(FieldsView.COMPUTED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(FieldsView.COMPUTED_INDEX));
		enabledBarcode = cursor.isNull(EntityFieldSpecs.BARCODE_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(EntityFieldSpecs.BARCODE_INDEX));
		formula = cursor.getString(FieldsView.FORMULA_INDEX);
		selector = cursor.getString(FieldsView.FORMULA_INDEX);
		pageId = cursor.isNull(FieldsView.PAGE_ID_INDEX) ? null : cursor
				.getInt(FieldsView.PAGE_ID_INDEX);
	}

	public Integer getVisible() {
		return visible;
	}

	public void setVisible(Integer visible) {
		this.visible = visible;
	}

	public Integer getEditable() {
		return editable;
	}

	public void setEditable(Integer editable) {
		this.editable = editable;
	}

	public Long getLocalId() {
		return fieldId;
	}

	public void setLocalId(Long localId) {
		this.fieldId = localId;
	}

	public Long getFormSpecId() {
		return formSpecId;
	}

	public void setFormSpecId(Long formSpecId) {
		this.formSpecId = formSpecId;
	}

	public Long getFieldSpecId() {
		return fieldSpecId;
	}

	public void setFieldSpecId(Long fieldSpecId) {
		this.fieldSpecId = fieldSpecId;
	}

	public Long getLocalFormId() {
		return localFormId;
	}

	public void setLocalFormId(Long localFormId) {
		this.localFormId = localFormId;
	}

	public String getLocalValue() {
		return localValue;
	}

	public void setLocalValue(String localValue) {
		this.localValue = localValue;
	}

	public String getRemoteValue() {
		return remoteValue;
	}

	public void setRemoteValue(String remoteValue) {
		this.remoteValue = remoteValue;
	}

	public Long getRemoteFormId() {
		return remoteFormId;
	}

	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Boolean getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Boolean identifier) {
		this.identifier = identifier;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public FormFile getFile() {
		return file;
	}

	public void setFile(FormFile file) {
		this.file = file;
	}

	public String getTypeExtra() {
		return typeExtra;
	}

	public void setTypeExtra(String typeExtra) {
		this.typeExtra = typeExtra;
	}

	public Boolean getComputed() {
		return computed;
	}

	public void setComputed(Boolean computed) {
		this.computed = computed;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	public Boolean getEnabledBarcode() {
		return enabledBarcode;
	}

	public void setEnabledBarcode(Boolean enabledBarcode) {
		this.enabledBarcode = enabledBarcode;
	}

	public Long getMinValue() {
		return minValue;
	}

	public void setMinValue(Long minValue) {
		this.minValue = minValue;
	}

	public Long getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Long maxValue) {
		this.maxValue = maxValue;
	}

	public boolean isItVisibleByCriteria() {
		return isItVisibleByCriteria;
	}

	public void setIsItVisibleByCriteria(boolean isItVisibleByCriteria) {
		this.isItVisibleByCriteria = isItVisibleByCriteria;
	}

	public Boolean getDefaultField() {
		return defaultField;
	}

	public void setDefaultField(Boolean defaultField) {
		this.defaultField = defaultField;
	}

	public HashMap<String, String> getFormulaFieldValues() {
		return formulaFieldValues;
	}

	public void setFormulaFieldValues(HashMap<String, String> formulaFieldValues) {
		this.formulaFieldValues = formulaFieldValues;
	}
	
	public boolean isItEnabledByCriteria() {
		return isItEnabledByCriteria;
	}

	public void setItEnabledByCriteria(boolean isItEnabledByCriteria) {
		this.isItEnabledByCriteria = isItEnabledByCriteria;
	}

	@Override
	public String toString() {
		return "ViewField [fieldId=" + fieldId + ", formSpecId=" + formSpecId
				+ ", fieldSpecId=" + fieldSpecId + ", localFormId="
				+ localFormId + ", remoteFormId=" + remoteFormId
				+ ", localValue=" + localValue + ", remoteValue=" + remoteValue
				+ ", label=" + label + ", type=" + type + ", identifier="
				+ identifier + ", required=" + required + ", displayOrder="
				+ displayOrder + ", file=" + file + ", typeExtra=" + typeExtra
				+ ", computed=" + computed + ", enabledBarcode="
				+ enabledBarcode + ", formula=" + formula + ", selector="
				+ selector + ", pageId=" + pageId + ", visible=" + visible
				+ ", editable=" + editable + ", minValue=" + minValue
				+ ", maxValue=" + maxValue + ", isItVisibleByCriteria="
				+ isItVisibleByCriteria + "]";
	}

}

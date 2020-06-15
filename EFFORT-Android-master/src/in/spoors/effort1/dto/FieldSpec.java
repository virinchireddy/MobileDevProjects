package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.EntityFieldSpecs;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class FieldSpec implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "FieldSpec";

	private Long id;
	private Long formSpecId;
	private String label;
	private Integer type;
	private Boolean identifier;
	private Boolean required;
	private Integer displayOrder;
	private String typeExtra;
	private String selector;
	private Boolean computed;
	private Boolean barcodeField;
	private String formula;
	private Integer pageId;
	private String uniqueId;
	private Integer visible;
	private Integer editable;
	private Long minValue;
	private Long maxValue;
	private Boolean defaultField;

	public static final String JSON_PATH = "forms/specs/fields";
	public static final String JSON_ID = "fieldSpecId";
	public static final String JSON_FORM_SPEC_ID = "formSpecId";
	public static final String JSON_LABEL = "fieldLabel";
	public static final String JSON_TYPE = "fieldType";
	public static final String JSON_IDENTIFIER = "identifier";
	public static final String JSON_REQUIRED = "required";
	public static final String JSON_DISPLAY_ORDER = "displayOrder";
	public static final String JSON_TYPE_EXTRA = "fieldTypeExtra";
	public static final String JSON_SELECTOR = "fieldSelector";
	public static final String JSON_COMPUTED = "computedField";
	public static final String JSON_BARCODE = "barcodeField";
	public static final String JSON_FORMULA = "formula";
	public static final String JSON_PAGE_ID = "pageId";
	public static final String JSON_UNIQUE_ID = "uniqueId";
	public static final String JSON_VILIBLE = "visible";
	public static final String JSON_EDITABLE = "editable";
	public static final String JSON_MIN_VALUE = "min";
	public static final String JSON_MAX_VALUE = "max";
	public static final String JSON_IS_DEFAULT = "defaultField";

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static FieldSpec parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		FieldSpec fieldSpec = new FieldSpec();

		fieldSpec.id = Utils.getLong(json, JSON_ID);
		fieldSpec.formSpecId = Utils.getLong(json, JSON_FORM_SPEC_ID);
		fieldSpec.label = Utils.getString(json, JSON_LABEL);
		fieldSpec.type = Utils.getInteger(json, JSON_TYPE);
		fieldSpec.identifier = Utils.getBoolean(json, JSON_IDENTIFIER);
		fieldSpec.required = Utils.getBoolean(json, JSON_REQUIRED);
		fieldSpec.displayOrder = Utils.getInteger(json, JSON_DISPLAY_ORDER);
		fieldSpec.typeExtra = Utils.getString(json, JSON_TYPE_EXTRA);
		fieldSpec.selector = Utils.getString(json, JSON_SELECTOR);
		fieldSpec.computed = Utils.getBoolean(json, JSON_COMPUTED);
		fieldSpec.barcodeField = Utils.getBoolean(json, JSON_BARCODE);
		fieldSpec.formula = Utils.getString(json, JSON_FORMULA);
		fieldSpec.pageId = Utils.getInteger(json, JSON_PAGE_ID);
		fieldSpec.uniqueId = Utils.getString(json, JSON_UNIQUE_ID);
		fieldSpec.visible = Utils.getInteger(json, JSON_VILIBLE);
		fieldSpec.editable = Utils.getInteger(json, JSON_EDITABLE);
		fieldSpec.minValue = Utils.getLong(json, JSON_MIN_VALUE);
		fieldSpec.maxValue = Utils.getLong(json, JSON_MAX_VALUE);
		fieldSpec.defaultField = Utils.getBoolean(json, JSON_IS_DEFAULT);

		return fieldSpec;
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
		if (id != null) {
			values.put(FieldSpecs._ID, id);
		}
		// if (id == 3245) {
		// System.out.println("id");
		// }
		Utils.putNullOrValue(values, FieldSpecs.FORM_SPEC_ID, formSpecId);
		Utils.putNullOrValue(values, FieldSpecs.LABEL, label);
		Utils.putNullOrValue(values, FieldSpecs.TYPE, type);
		Utils.putNullOrValue(values, FieldSpecs.IDENTIFIER, identifier);
		Utils.putNullOrValue(values, FieldSpecs.REQUIRED, required);
		Utils.putNullOrValue(values, FieldSpecs.DISPLAY_ORDER, displayOrder);
		Utils.putNullOrValue(values, FieldSpecs.TYPE_EXTRA, typeExtra);
		Utils.putNullOrValue(values, FieldSpecs.SELECTOR, selector);
		Utils.putNullOrValue(values, FieldSpecs.COMPUTED, computed);
		Utils.putNullOrValue(values, FieldSpecs.BARCODE, barcodeField);
		Utils.putNullOrValue(values, FieldSpecs.FORMULA, formula);
		Utils.putNullOrValue(values, FieldSpecs.PAGE_ID, pageId);
		Utils.putNullOrValue(values, FieldSpecs.UNIQUE_ID, uniqueId);
		Utils.putNullOrValue(values, FieldSpecs.VISIBLE, visible);
		Utils.putNullOrValue(values, FieldSpecs.EDITABLE, editable);
		Utils.putNullOrValue(values, FieldSpecs.MIN_VALUE, minValue);
		Utils.putNullOrValue(values, FieldSpecs.MAX_VALUE, maxValue);
		Utils.putNullOrValue(values, FieldSpecs.DEFAULT_FIELD, defaultField);
		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.isNull(FieldSpecs._ID_INDEX) ? null : cursor
				.getLong(FieldSpecs._ID_INDEX);
		formSpecId = cursor.isNull(FieldSpecs.FORM_SPEC_ID_INDEX) ? null
				: cursor.getLong(FieldSpecs.FORM_SPEC_ID_INDEX);
		label = cursor.getString(FieldSpecs.LABEL_INDEX);
		type = cursor.isNull(FieldSpecs.TYPE_INDEX) ? null : cursor
				.getInt(FieldSpecs.TYPE_INDEX);
		identifier = cursor.isNull(FieldSpecs.IDENTIFIER_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(FieldSpecs.IDENTIFIER_INDEX));
		required = cursor.isNull(FieldSpecs.REQUIRED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(FieldSpecs.REQUIRED_INDEX));
		displayOrder = cursor.isNull(FieldSpecs.DISPLAY_ORDER_INDEX) ? null
				: cursor.getInt(FieldSpecs.DISPLAY_ORDER_INDEX);
		typeExtra = cursor.getString(FieldSpecs.TYPE_EXTRA_INDEX);
		selector = cursor.getString(FieldSpecs.SELECTOR_INDEX);
		computed = cursor.isNull(FieldSpecs.COMPUTED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(FieldSpecs.COMPUTED_INDEX));
		barcodeField = cursor.isNull(EntityFieldSpecs.BARCODE_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(EntityFieldSpecs.BARCODE_INDEX));
		formula = cursor.getString(FieldSpecs.FORMULA_INDEX);
		pageId = cursor.isNull(FieldSpecs.PAGE_ID_INDEX) ? null : cursor
				.getInt(FieldSpecs.PAGE_ID_INDEX);
		uniqueId = cursor.getString(FieldSpecs.UNIQUE_ID_INDEX);
		visible = cursor.getInt(FieldSpecs.VISIBLE_INDEX);
		editable = cursor.getInt(FieldSpecs.EDITABLE_INDEX);
		minValue = cursor.isNull(FieldSpecs.MIN_VALUE_INDEX) ? null : cursor
				.getLong(FieldSpecs.MIN_VALUE_INDEX);
		maxValue = cursor.isNull(FieldSpecs.MAX_VALUE_INDEX) ? null : cursor
				.getLong(FieldSpecs.MAX_VALUE_INDEX);
		defaultField = cursor.isNull(FieldSpecs.DEFAULT_FIELD_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(FieldSpecs.DEFAULT_FIELD_INDEX));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFormSpecId() {
		return formSpecId;
	}

	public void setFormSpecId(Long formSpecId) {
		this.formSpecId = formSpecId;
	}

	public String getLabel() {
		return label;
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

	public String getTypeExtra() {
		return typeExtra;
	}

	public void setTypeExtra(String typeExtra) {
		this.typeExtra = typeExtra;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
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

	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public Boolean getBarcodeField() {
		return barcodeField;
	}

	public void setBarcodeField(Boolean barcodeField) {
		this.barcodeField = barcodeField;
	}

	public Boolean getDefaultField() {
		return defaultField;
	}

	public void setDefaultField(Boolean defaultField) {
		this.defaultField = defaultField;
	}

	@Override
	public String toString() {
		return "FieldSpec [id=" + id + ", formSpecId=" + formSpecId
				+ ", label=" + label + ", type=" + type + ", identifier="
				+ identifier + ", required=" + required + ", displayOrder="
				+ displayOrder + ", typeExtra=" + typeExtra + ", selector="
				+ selector + ", computed=" + computed + ", barcodeField="
				+ barcodeField + ", formula=" + formula + ", pageId=" + pageId
				+ ", uniqueId=" + uniqueId + ", visible=" + visible
				+ ", editable=" + editable + ", minValue=" + minValue
				+ ", maxValue=" + maxValue + ", defaultField=" + defaultField
				+ "]";
	}

}

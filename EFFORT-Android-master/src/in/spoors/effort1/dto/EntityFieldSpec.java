package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.EntityFieldSpecs;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class EntityFieldSpec implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "EntityFieldSpec";

	private Long id;
	private Long entitySpecId;
	private String label;
	private Integer type;
	private Boolean identifier;
	private Boolean required;
	private Integer displayOrder;
	private String typeExtra;
	private String selector;
	private Boolean computed;
	private String formula;
	private Boolean barcodeField;

	public static final String JSON_PATH = "entities/specs/fields";
	public static final String JSON_ID = "entityFieldSpecId";
	public static final String JSON_ENTITY_SPEC_ID = "entitySpecId";
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

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static EntityFieldSpec parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {
		EntityFieldSpec fieldSpec = new EntityFieldSpec();

		fieldSpec.id = Utils.getLong(json, JSON_ID);
		fieldSpec.entitySpecId = Utils.getLong(json, JSON_ENTITY_SPEC_ID);
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
			values.put(EntityFieldSpecs._ID, id);
		}

		Utils.putNullOrValue(values, EntityFieldSpecs.ENTITY_SPEC_ID,
				entitySpecId);
		Utils.putNullOrValue(values, EntityFieldSpecs.LABEL, label);
		Utils.putNullOrValue(values, EntityFieldSpecs.TYPE, type);
		Utils.putNullOrValue(values, EntityFieldSpecs.IDENTIFIER, identifier);
		Utils.putNullOrValue(values, EntityFieldSpecs.REQUIRED, required);
		Utils.putNullOrValue(values, EntityFieldSpecs.DISPLAY_ORDER,
				displayOrder);
		Utils.putNullOrValue(values, EntityFieldSpecs.TYPE_EXTRA, typeExtra);
		Utils.putNullOrValue(values, EntityFieldSpecs.SELECTOR, selector);
		Utils.putNullOrValue(values, EntityFieldSpecs.COMPUTED, computed);
		Utils.putNullOrValue(values, EntityFieldSpecs.FORMULA, formula);
		Utils.putNullOrValue(values, EntityFieldSpecs.BARCODE, barcodeField);

		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.isNull(EntityFieldSpecs._ID_INDEX) ? null : cursor
				.getLong(EntityFieldSpecs._ID_INDEX);
		entitySpecId = cursor.isNull(EntityFieldSpecs.ENTITY_SPEC_ID_INDEX) ? null
				: cursor.getLong(EntityFieldSpecs.ENTITY_SPEC_ID_INDEX);
		label = cursor.getString(EntityFieldSpecs.LABEL_INDEX);
		type = cursor.isNull(EntityFieldSpecs.TYPE_INDEX) ? null : cursor
				.getInt(EntityFieldSpecs.TYPE_INDEX);
		identifier = cursor.isNull(EntityFieldSpecs.IDENTIFIER_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(EntityFieldSpecs.IDENTIFIER_INDEX));
		required = cursor.isNull(EntityFieldSpecs.REQUIRED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(EntityFieldSpecs.REQUIRED_INDEX));
		displayOrder = cursor.isNull(EntityFieldSpecs.DISPLAY_ORDER_INDEX) ? null
				: cursor.getInt(EntityFieldSpecs.DISPLAY_ORDER_INDEX);
		typeExtra = cursor.getString(EntityFieldSpecs.TYPE_EXTRA_INDEX);
		selector = cursor.getString(EntityFieldSpecs.SELECTOR_INDEX);
		computed = cursor.isNull(EntityFieldSpecs.COMPUTED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(EntityFieldSpecs.COMPUTED_INDEX));
		barcodeField = cursor.isNull(EntityFieldSpecs.BARCODE_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(EntityFieldSpecs.BARCODE_INDEX));
		formula = cursor.getString(EntityFieldSpecs.FORMULA_INDEX);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEntitySpecId() {
		return entitySpecId;
	}

	public void setEntitySpecId(Long entitySpecId) {
		this.entitySpecId = entitySpecId;
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

	public Boolean getBarcodeField() {
		return barcodeField;
	}

	public void setBarcodeField(Boolean barcodeField) {
		this.barcodeField = barcodeField;
	}

	@Override
	public String toString() {
		return "EntityFieldSpec [id=" + id + ", entitySpecId=" + entitySpecId
				+ ", label=" + label + ", type=" + type + ", identifier="
				+ identifier + ", required=" + required + ", displayOrder="
				+ displayOrder + ", typeExtra=" + typeExtra + ", selector="
				+ selector + ", computed=" + computed + ", formula=" + formula
				+ ", barcodeField=" + barcodeField + "]";
	}

}

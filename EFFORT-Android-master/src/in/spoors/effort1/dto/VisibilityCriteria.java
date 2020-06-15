package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.VisibilityCriterias;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class VisibilityCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String JSON_VALUE = "value";
	public static final String JSON_ID = "id";
	public static final String JSON_FIELD_TYPE = "fieldType";
	public static final String JSON_CONDITION = "condition";
	public static final String JSON_FORMSPEC_ID = "formSpecId";
	public static final String JSON_FIELD_SPEC_ID = "fieldSpecId";
	public static final String JSON_TARGET_FIELD_EXPRESSION = "targetFieldExpression";
	public static final String JSON_FIELD_DATA_TYPE = "fieldDataType";
	public static final String JSON_VALUE_IDS = "valueIds";
	public static final String JSON_VISIBILITY_TYPE = "visibilityType";

	private String value;
	private Long id;
	private Integer fieldType;
	private Integer condition;
	private Long formSpecId;
	private Long fieldSpecId;
	private String targetFieldExpression;
	private Integer fieldDataType;
	private String valueIds;
	private Integer visibilityType;
	private Long localId;

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
	public static VisibilityCriteria parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {

		if (json == null) {
			return null;
		}

		VisibilityCriteria visibilityCriteria = new VisibilityCriteria();
		visibilityCriteria.value = Utils.getString(json, JSON_VALUE);
		visibilityCriteria.id = Utils.getLong(json, JSON_ID);
		visibilityCriteria.fieldType = Utils.getInteger(json, JSON_FIELD_TYPE);
		visibilityCriteria.condition = Utils.getInteger(json, JSON_CONDITION);
		visibilityCriteria.formSpecId = Utils.getLong(json, JSON_FORMSPEC_ID);
		visibilityCriteria.fieldSpecId = Utils
				.getLong(json, JSON_FIELD_SPEC_ID);
		visibilityCriteria.targetFieldExpression = Utils.getString(json,
				JSON_TARGET_FIELD_EXPRESSION);
		visibilityCriteria.fieldDataType = Utils.getInteger(json,
				JSON_FIELD_DATA_TYPE);
		visibilityCriteria.valueIds = Utils.getString(json, JSON_VALUE_IDS);
		visibilityCriteria.visibilityType = Utils.getInteger(json,
				JSON_VISIBILITY_TYPE);

		return visibilityCriteria;
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
			values.put(VisibilityCriterias._ID, localId);
		}

		Utils.putNullOrValue(values, VisibilityCriterias.VALUE, value);
		Utils.putNullOrValue(values, VisibilityCriterias.REMOTE_ID, id);
		Utils.putNullOrValue(values, VisibilityCriterias.FIELD_TYPE, fieldType);
		Utils.putNullOrValue(values, VisibilityCriterias.CONDITION, condition);
		Utils.putNullOrValue(values, VisibilityCriterias.FORM_SPEC_ID,
				formSpecId);
		Utils.putNullOrValue(values, VisibilityCriterias.FIELD_SPEC_ID,
				fieldSpecId);
		Utils.putNullOrValue(values,
				VisibilityCriterias.TARGET_FIELD_EXPRESSION,
				targetFieldExpression);
		Utils.putNullOrValue(values, VisibilityCriterias.FIELD_DATA_TYPE,
				fieldDataType);
		Utils.putNullOrValue(values, VisibilityCriterias.VALUE_IDS, valueIds);
		Utils.putNullOrValue(values, VisibilityCriterias.VISIBILITY_TYPE,
				visibilityType);

		return values;
	}

	public void load(Cursor cursor) {
		localId = cursor.isNull(VisibilityCriterias._ID_INDEX) ? null : cursor
				.getLong(VisibilityCriterias._ID_INDEX);
		value = cursor.isNull(VisibilityCriterias.VALUE_INDEX) ? null : cursor
				.getString(VisibilityCriterias.VALUE_INDEX);
		id = cursor.isNull(VisibilityCriterias.REMOTE_ID_INDEX) ? null : cursor
				.getLong(VisibilityCriterias.REMOTE_ID_INDEX);
		fieldType = cursor.isNull(VisibilityCriterias.FIELD_TYPE_INDEX) ? null
				: cursor.getInt(VisibilityCriterias.FIELD_TYPE_INDEX);
		condition = cursor.isNull(VisibilityCriterias.CONDITION_INDEX) ? null
				: cursor.getInt(VisibilityCriterias.CONDITION_INDEX);
		formSpecId = cursor.isNull(VisibilityCriterias.FORM_SPEC_ID_INDEX) ? null
				: cursor.getLong(VisibilityCriterias.FORM_SPEC_ID_INDEX);
		fieldSpecId = cursor.isNull(VisibilityCriterias.FIELD_SPEC_ID_INDEX) ? null
				: cursor.getLong(VisibilityCriterias.FIELD_SPEC_ID_INDEX);
		targetFieldExpression = cursor
				.isNull(VisibilityCriterias.TARGET_FIELD_EXPRESSION_INDEX) ? null
				: cursor.getString(VisibilityCriterias.TARGET_FIELD_EXPRESSION_INDEX);
		fieldDataType = cursor
				.isNull(VisibilityCriterias.FIELD_DATA_TYPE_INDEX) ? null
				: cursor.getInt(VisibilityCriterias.FIELD_DATA_TYPE_INDEX);
		valueIds = cursor.isNull(VisibilityCriterias.VALUE_IDS_INDEX) ? null
				: cursor.getString(VisibilityCriterias.VALUE_IDS_INDEX);
		visibilityType = cursor
				.isNull(VisibilityCriterias.VISIBILITY_TYPE_INDEX) ? null
				: cursor.getInt(VisibilityCriterias.VISIBILITY_TYPE_INDEX);

	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getFieldType() {
		return fieldType;
	}

	public void setFieldType(Integer fieldType) {
		this.fieldType = fieldType;
	}

	public Integer getCondition() {
		return condition;
	}

	public void setCondition(Integer condition) {
		this.condition = condition;
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

	public String getTargetFieldExpression() {
		return targetFieldExpression;
	}

	public void setTargetFieldExpression(String targetFieldExpression) {
		this.targetFieldExpression = targetFieldExpression;
	}

	public Integer getFieldDataType() {
		return fieldDataType;
	}

	public void setFieldDataType(Integer fieldDataType) {
		this.fieldDataType = fieldDataType;
	}

	public String getValueIds() {
		return valueIds;
	}

	public void setValueIds(String valueIds) {
		this.valueIds = valueIds;
	}

	public Integer getVisibilityType() {
		return visibilityType;
	}

	public void setVisibilityType(Integer visibilityType) {
		this.visibilityType = visibilityType;
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
	}

}

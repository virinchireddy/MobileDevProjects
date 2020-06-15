package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.ListFilteringCriterias;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class ListFilteringCriteria implements Serializable {

	private static final long serialVersionUID = 1L;
	// private static final String TAG = "VisibilityCriteria";

	public static final String JSON_VALUE = "value";
	public static final String JSON_TYPE = "type";
	public static final String JSON_CONDITION = "condition";
	public static final String JSON_FORMSPEC_ID = "formSpecId";
	public static final String JSON_FIELD_SPEC_ID = "fieldSpecId";
	public static final String JSON_REFERENCE_FIELD_EXPRESSION_ID = "referenceFieldExpressionId";
	public static final String JSON_LIST_FIELD_SPEC_ID = "listFieldSpecId";

	private String value;
	private Integer type;
	private Integer condition;
	private Long formSpecId;
	private Long fieldSpecId;
	private String referenceFieldExpressionId;
	private Long listFieldSpecId;
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
	public static ListFilteringCriteria parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {

		if (json == null) {
			return null;
		}

		ListFilteringCriteria listFilteringCriteria = new ListFilteringCriteria();
		listFilteringCriteria.value = Utils.getString(json, JSON_VALUE);
		listFilteringCriteria.type = Utils.getInteger(json, JSON_TYPE);
		listFilteringCriteria.condition = Utils
				.getInteger(json, JSON_CONDITION);
		listFilteringCriteria.formSpecId = Utils
				.getLong(json, JSON_FORMSPEC_ID);
		listFilteringCriteria.fieldSpecId = Utils.getLong(json,
				JSON_FIELD_SPEC_ID);
		listFilteringCriteria.referenceFieldExpressionId = Utils.getString(
				json, JSON_REFERENCE_FIELD_EXPRESSION_ID);
		listFilteringCriteria.listFieldSpecId = Utils.getLong(json,
				JSON_LIST_FIELD_SPEC_ID);

		return listFilteringCriteria;
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
			values.put(ListFilteringCriterias._ID, localId);
		}

		Utils.putNullOrValue(values, ListFilteringCriterias.VALUE, value);
		Utils.putNullOrValue(values, ListFilteringCriterias.TYPE, type);
		Utils.putNullOrValue(values, ListFilteringCriterias.CONDITION,
				condition);
		Utils.putNullOrValue(values, ListFilteringCriterias.FORM_SPEC_ID,
				formSpecId);
		Utils.putNullOrValue(values, ListFilteringCriterias.FIELD_SPEC_ID,
				fieldSpecId);
		Utils.putNullOrValue(values,
				ListFilteringCriterias.REFERENCE_FIELD_EXPRESSION_ID,
				referenceFieldExpressionId);
		Utils.putNullOrValue(values, ListFilteringCriterias.LIST_FIELD_SPEC_ID,
				listFieldSpecId);

		return values;
	}

	public void load(Cursor cursor) {
		localId = cursor.isNull(ListFilteringCriterias._ID_INDEX) ? null
				: cursor.getLong(ListFilteringCriterias._ID_INDEX);
		value = cursor.isNull(ListFilteringCriterias.VALUE_INDEX) ? null
				: cursor.getString(ListFilteringCriterias.VALUE_INDEX);
		type = cursor.isNull(ListFilteringCriterias.TYPE_INDEX) ? null : cursor
				.getInt(ListFilteringCriterias.TYPE_INDEX);
		condition = cursor.isNull(ListFilteringCriterias.CONDITION_INDEX) ? null
				: cursor.getInt(ListFilteringCriterias.CONDITION_INDEX);
		formSpecId = cursor.isNull(ListFilteringCriterias.FORM_SPEC_ID_INDEX) ? null
				: cursor.getLong(ListFilteringCriterias.FORM_SPEC_ID_INDEX);
		fieldSpecId = cursor.isNull(ListFilteringCriterias.FIELD_SPEC_ID_INDEX) ? null
				: cursor.getLong(ListFilteringCriterias.FIELD_SPEC_ID_INDEX);
		referenceFieldExpressionId = cursor
				.isNull(ListFilteringCriterias.REFERENCE_FIELD_EXPRESSION_ID_INDEX) ? null
				: cursor.getString(ListFilteringCriterias.REFERENCE_FIELD_EXPRESSION_ID_INDEX);
		listFieldSpecId = cursor
				.isNull(ListFilteringCriterias.LIST_FIELD_SPEC_ID_INDEX) ? null
				: cursor.getLong(ListFilteringCriterias.LIST_FIELD_SPEC_ID_INDEX);

	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public String getReferenceFieldExpressionId() {
		return referenceFieldExpressionId;
	}

	public void setReferenceFieldExpressionId(String referenceFieldExpressionId) {
		this.referenceFieldExpressionId = referenceFieldExpressionId;
	}

	public Long getListFieldSpecId() {
		return listFieldSpecId;
	}

	public void setListFieldSpecId(Long listFieldSpecId) {
		this.listFieldSpecId = listFieldSpecId;
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
	}

}

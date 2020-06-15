package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.FormSpecs;
import in.spoors.effort1.provider.EffortProvider.SectionFieldValueSpecs;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SectionFieldValueSpec implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "SectionFieldValueSpec";

	private Long id;
	private Long fieldSpecId;
	private String value;

	public static final String JSON_ID = "valueId";
	public static final String JSON_FIELD_SPEC_ID = "sectionFieldSpecId";
	public static final String JSON_VALUE = "value";

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static SectionFieldValueSpec parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {
		SectionFieldValueSpec valueSpec = new SectionFieldValueSpec();

		valueSpec.id = Utils.getLong(json, JSON_ID);
		valueSpec.fieldSpecId = Utils.getLong(json, JSON_FIELD_SPEC_ID);
		valueSpec.value = Utils.getString(json, JSON_VALUE);

		return valueSpec;
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
			values.put(FormSpecs._ID, id);
		}

		Utils.putNullOrValue(values, SectionFieldValueSpecs.FIELD_SPEC_ID,
				fieldSpecId);
		Utils.putNullOrValue(values, SectionFieldValueSpecs.VALUE, value);

		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.isNull(SectionFieldValueSpecs._ID_INDEX) ? null : cursor
				.getLong(SectionFieldValueSpecs._ID_INDEX);
		fieldSpecId = cursor.isNull(SectionFieldValueSpecs.FIELD_SPEC_ID_INDEX) ? null
				: cursor.getLong(SectionFieldValueSpecs.FIELD_SPEC_ID_INDEX);
		value = cursor.getString(SectionFieldValueSpecs.VALUE_INDEX);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getFieldSpecId() {
		return fieldSpecId;
	}

	public void setFieldSpecId(Long fieldSpecId) {
		this.fieldSpecId = fieldSpecId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "FieldValueSpec [id=" + id + ", fieldSpecId=" + fieldSpecId
				+ ", value=" + value + "]";
	}

}

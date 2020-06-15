package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.EmployeesDao;
import in.spoors.effort1.dao.EntitiesDao;
import in.spoors.effort1.dao.FieldSpecsDao;
import in.spoors.effort1.dao.FormFilesDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;
import in.spoors.effort1.provider.EffortProvider.Fields;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

public class Field implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "Field";

	public static final String JSON_FORM_SPEC_ID = "formSpecId";
	public static final String JSON_FIELD_SPEC_ID = "fieldSpecId";
	public static final String JSON_LOCAL_FORM_ID = "clientFormId";
	public static final String JSON_REMOTE_FORM_ID = "formId";
	public static final String JSON_LOCAL_VALUE = "fieldValueSubstitute";
	public static final String JSON_REMOTE_VALUE = "fieldValue";
	public static final String JSON_CAN_IGNORE_UPDATE = "canIgnoreUpdate";

	private Long localId;
	private Long formSpecId;
	private Long fieldSpecId;
	private Long localFormId;
	private Long remoteFormId;
	private String localValue;
	private String remoteValue;
	private String fieldSpecUniqueId;

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
	public static Field parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		Field field = new Field();

		field.formSpecId = Utils.getLong(json, JSON_FORM_SPEC_ID);
		field.fieldSpecId = Utils.getLong(json, JSON_FIELD_SPEC_ID);
		field.localFormId = Utils.getLong(json, JSON_LOCAL_FORM_ID);
		field.remoteFormId = Utils.getLong(json, JSON_REMOTE_FORM_ID);
		field.localValue = Utils.getString(json, JSON_LOCAL_VALUE);
		field.remoteValue = Utils.getString(json, JSON_REMOTE_VALUE);

		if (field.localFormId == null && field.remoteFormId != null) {
			FormsDao formsDao = FormsDao.getInstance(applicationContext);
			field.localFormId = formsDao.getLocalId(field.remoteFormId);
		}

		return field;
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
		if (localId != null) {
			values.put(Fields._ID, localId);
		}

		Utils.putNullOrValue(values, Fields.FORM_SPEC_ID, formSpecId);
		Utils.putNullOrValue(values, Fields.FIELD_SPEC_ID, fieldSpecId);
		Utils.putNullOrValue(values, Fields.LOCAL_FORM_ID, localFormId);
		Utils.putNullOrValue(values, Fields.LOCAL_VALUE, localValue);
		Utils.putNullOrValue(values, Fields.REMOTE_VALUE, remoteValue);
		Utils.putNullOrValue(values, Fields.FIELD_SPEC_UNIQUE_ID,
				fieldSpecUniqueId);

		return values;
	}

	public void load(Cursor cursor, Context applicationContext) {
		FormsDao formsDao = FormsDao.getInstance(applicationContext);

		localId = cursor.isNull(Fields._ID_INDEX) ? null : cursor
				.getLong(Fields._ID_INDEX);
		formSpecId = cursor.isNull(Fields.FORM_SPEC_ID_INDEX) ? null : cursor
				.getLong(Fields.FORM_SPEC_ID_INDEX);
		fieldSpecId = cursor.isNull(Fields.FIELD_SPEC_ID_INDEX) ? null : cursor
				.getLong(Fields.FIELD_SPEC_ID_INDEX);
		localFormId = cursor.isNull(Fields.LOCAL_FORM_ID_INDEX) ? null : cursor
				.getLong(Fields.LOCAL_FORM_ID_INDEX);
		remoteFormId = formsDao.getRemoteId(localFormId);
		localValue = cursor.getString(Fields.LOCAL_VALUE_INDEX);
		remoteValue = cursor.getString(Fields.REMOTE_VALUE_INDEX);
		fieldSpecUniqueId = cursor.getString(Fields.FIELD_SPEC_UNIQUE_ID_INDEX);
	}

	@Override
	public String toString() {
		return "Field [localId=" + localId + ", formSpecId=" + formSpecId
				+ ", fieldSpecId=" + fieldSpecId + ", localFormId="
				+ localFormId + ", remoteFormId=" + remoteFormId
				+ ", localValue=" + localValue + ", remoteValue=" + remoteValue
				+ ", fieldSpecUniqueId=" + fieldSpecUniqueId + "]";
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
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

	public String getFieldSpecUniqueId() {
		return fieldSpecUniqueId;
	}

	public void setFieldSpecUniqueId(String fieldSpecUniqueId) {
		this.fieldSpecUniqueId = fieldSpecUniqueId;
	}

	/**
	 * Get the JSON object that can be sent to the server.
	 * 
	 * @param applicationContext
	 * @return
	 */
	public JSONObject getJsonObject(Context applicationContext) {
		JSONObject json = new JSONObject();

		try {
			FieldSpecsDao fieldSpecsDao = FieldSpecsDao
					.getInstance(applicationContext);
			FieldSpec fieldSpec = fieldSpecsDao.getFieldSpec(fieldSpecId);
			int type = fieldSpec.getType();

			if (type == FieldSpecs.TYPE_IMAGE
					|| type == FieldSpecs.TYPE_SIGNATURE) {
				FormFilesDao filesDao = FormFilesDao
						.getInstance(applicationContext);
				FormFile fileDto = filesDao.getFile(fieldSpecId, localFormId);

				if (fieldSpec.getRequired()) {

					if (fileDto == null) {
						return null;
					}

					if (fileDto.getMediaId() != null) {
						remoteValue = "" + fileDto.getMediaId();
					} else {
						remoteValue = "-1";
					}

				} else {
					if (fileDto == null) {
						// skip this field
						return null;
					}

					if (fileDto.getMediaId() != null) {
						remoteValue = "" + fileDto.getMediaId();
					} else if (!TextUtils.isEmpty(fileDto.getLocalMediaPath())) {
						remoteValue = "-1";
					} else {
						// skip this field
						return null;
					}
				}
			}

			if (localValue != null) {
				if (type == FieldSpecs.TYPE_CUSTOMER) {
					CustomersDao customersDao = CustomersDao
							.getInstance(applicationContext);
					Long remoteId = customersDao.getRemoteId(Long
							.parseLong(localValue));

					if (remoteId != null) {
						remoteValue = "" + remoteId;
					}
				} else if (type == FieldSpecs.TYPE_ENTITY) {
					EntitiesDao entitiesDao = EntitiesDao
							.getInstance(applicationContext);
					Long remoteId = entitiesDao.getRemoteId(Long
							.parseLong(localValue));

					if (remoteId != null) {
						remoteValue = "" + remoteId;
					}
				} else if (type == FieldSpecs.TYPE_EMPLOYEE) {
					EmployeesDao employeesDao = EmployeesDao
							.getInstance(applicationContext);

					Long remoteId = employeesDao.getRemoteId(Long
							.parseLong(localValue));

					if (remoteId != null) {
						remoteValue = "" + remoteId;
					}
				} else if (type == FieldSpecs.TYPE_MULTI_LIST) {
					EntitiesDao entitiesDao = EntitiesDao
							.getInstance(applicationContext);
					String idsString = null;
					String selctedValuesAsString = localValue;
					if (selctedValuesAsString != null) {
						String[] selectedValues = selctedValuesAsString
								.split(",");
						ArrayList<String> values = new ArrayList<String>();
						for (int i = 0; i < selectedValues.length; i++) {
							selectedValues[i] = selectedValues[i].trim();
							values.add(entitiesDao.getRemoteId(Long
									.parseLong(selectedValues[i])) + "");
						}
						idsString = TextUtils.join(",", values);
						;
					}
					remoteValue = idsString;
				}

			}

			// Dont skip this field if it its visibility or editability is
			// restricted
			if ((fieldSpec.getVisible() != null && fieldSpec.getVisible() != 0)
					&& ((fieldSpec.getEditable() != null && fieldSpec
							.getEditable() != 0))
					&& TextUtils.isEmpty(localValue)
					&& TextUtils.isEmpty(remoteValue)) {
				// skip this field
				return null;
			}

			if (remoteFormId != null) {
				json.put(JSON_REMOTE_FORM_ID, remoteFormId);
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_LOCAL_FORM_ID,
						localFormId);
			}

			Utils.putValueOnlyIfNotNull(json, JSON_FORM_SPEC_ID, formSpecId);
			Utils.putValueOnlyIfNotNull(json, JSON_FIELD_SPEC_ID, fieldSpecId);

			if (remoteValue != null) {
				json.put(JSON_REMOTE_VALUE, remoteValue);
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_LOCAL_VALUE, localValue);
			}

			// inform to server that "this field is restricted to  me" and if
			// and only if that field not computed field and default field
			if (((fieldSpec.getVisible() != null && fieldSpec.getVisible() == 0) || ((fieldSpec
					.getEditable() != null && fieldSpec.getEditable() == 0)))
					&& (!fieldSpec.getComputed() && !fieldSpec
							.getDefaultField())) {
				json.put(JSON_CAN_IGNORE_UPDATE, 1);
			} else {
				json.put(JSON_CAN_IGNORE_UPDATE, 0);
			}
		} catch (JSONException e) {
			Log.e(TAG, "Failed to compose JSON for job: " + e.toString(), e);
			return null;
		}

		return json;
	}
}

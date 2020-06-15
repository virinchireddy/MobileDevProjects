package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.EntitiesDao;
import in.spoors.effort1.dao.EntityFieldSpecsDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.provider.EffortProvider.EntityFields;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

public class EntityField implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "EntityField";

	public static final String JSON_ENTITY_SPEC_ID = "entitySpecId";
	public static final String JSON_FIELD_SPEC_ID = "entityFieldSpecId";
	public static final String JSON_LOCAL_ENTITY_ID = "clientEntityId";
	public static final String JSON_REMOTE_ENTITY_ID = "entityId";
	public static final String JSON_LOCAL_VALUE = "fieldValueSubstitute";
	public static final String JSON_REMOTE_VALUE = "fieldValue";

	private Long localId;
	private Long entitySpecId;
	private Long fieldSpecId;
	private Long localEntityId;
	private Long remoteEntityId;
	private String localValue;
	private String remoteValue;

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
	public static EntityField parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		EntityField field = new EntityField();

		field.entitySpecId = Utils.getLong(json, JSON_ENTITY_SPEC_ID);
		field.fieldSpecId = Utils.getLong(json, JSON_FIELD_SPEC_ID);
		field.localEntityId = Utils.getLong(json, JSON_LOCAL_ENTITY_ID);
		field.remoteEntityId = Utils.getLong(json, JSON_REMOTE_ENTITY_ID);
		field.localValue = Utils.getString(json, JSON_LOCAL_VALUE);
		field.remoteValue = Utils.getString(json, JSON_REMOTE_VALUE);

		if (field.localEntityId == null && field.remoteEntityId != null) {
			EntitiesDao entitiesDao = EntitiesDao
					.getInstance(applicationContext);
			field.localEntityId = entitiesDao.getLocalId(field.remoteEntityId);
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
			values.put(EntityFields._ID, localId);
		}

		Utils.putNullOrValue(values, EntityFields.ENTITY_SPEC_ID, entitySpecId);
		Utils.putNullOrValue(values, EntityFields.FIELD_SPEC_ID, fieldSpecId);
		Utils.putNullOrValue(values, EntityFields.LOCAL_ENTITY_ID,
				localEntityId);
		Utils.putNullOrValue(values, EntityFields.LOCAL_VALUE, localValue);
		Utils.putNullOrValue(values, EntityFields.REMOTE_VALUE, remoteValue);

		return values;
	}

	public void load(Cursor cursor, Context applicationContext) {
		FormsDao formsDao = FormsDao.getInstance(applicationContext);

		localId = cursor.isNull(EntityFields._ID_INDEX) ? null : cursor
				.getLong(EntityFields._ID_INDEX);
		entitySpecId = cursor.isNull(EntityFields.ENTITY_SPEC_ID_INDEX) ? null
				: cursor.getLong(EntityFields.ENTITY_SPEC_ID_INDEX);
		fieldSpecId = cursor.isNull(EntityFields.FIELD_SPEC_ID_INDEX) ? null
				: cursor.getLong(EntityFields.FIELD_SPEC_ID_INDEX);
		localEntityId = cursor.isNull(EntityFields.LOCAL_ENTITY_ID_INDEX) ? null
				: cursor.getLong(EntityFields.LOCAL_ENTITY_ID_INDEX);
		remoteEntityId = formsDao.getRemoteId(localEntityId);
		localValue = cursor.getString(EntityFields.LOCAL_VALUE_INDEX);
		remoteValue = cursor.getString(EntityFields.REMOTE_VALUE_INDEX);
	}

	@Override
	public String toString() {
		return "EntityField [localId=" + localId + ", entitySpecId="
				+ entitySpecId + ", fieldSpecId=" + fieldSpecId
				+ ", localEntityId=" + localEntityId + ", remoteEntityId="
				+ remoteEntityId + ", localValue=" + localValue
				+ ", remoteValue=" + remoteValue + "]";
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
	}

	public Long getEntitySpecId() {
		return entitySpecId;
	}

	public void setEntitySpecId(Long entitySpecId) {
		this.entitySpecId = entitySpecId;
	}

	public Long getFieldSpecId() {
		return fieldSpecId;
	}

	public void setFieldSpecId(Long fieldSpecId) {
		this.fieldSpecId = fieldSpecId;
	}

	public Long getLocalEntityId() {
		return localEntityId;
	}

	public void setLocalEntityId(Long localEntityId) {
		this.localEntityId = localEntityId;
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

	public Long getRemoteEntityId() {
		return remoteEntityId;
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
			EntityFieldSpecsDao fieldSpecsDao = EntityFieldSpecsDao
					.getInstance(applicationContext);
			EntityFieldSpec fieldSpec = fieldSpecsDao.getFieldSpec(fieldSpecId);
			int type = fieldSpec.getType();

			if (type == FieldSpecs.TYPE_IMAGE
					|| type == FieldSpecs.TYPE_SIGNATURE) {
				Log.w(TAG,
						"Images and signatures are not supported for entities yet.");
				// FormFilesDao filesDao = FormFilesDao
				// .getInstance(applicationContext);
				// FormFile fileDto = filesDao.getFile(fieldSpecId,
				// localEntityId);
				//
				// if (fieldSpec.getRequired()) {
				// if (fileDto.getMediaId() != null) {
				// remoteValue = "" + fileDto.getMediaId();
				// } else {
				// remoteValue = "-1";
				// }
				// } else {
				// if (fileDto == null) {
				// // skip this field
				// return null;
				// }
				//
				// if (fileDto.getMediaId() != null) {
				// remoteValue = "" + fileDto.getMediaId();
				// } else if (!TextUtils.isEmpty(fileDto.getLocalMediaPath())) {
				// remoteValue = "-1";
				// } else {
				// // skip this field
				// return null;
				// }
				// }
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
				}
			}

			if (TextUtils.isEmpty(localValue) && TextUtils.isEmpty(remoteValue)) {
				// skip this field
				return null;
			}

			if (remoteEntityId != null) {
				json.put(JSON_REMOTE_ENTITY_ID, remoteEntityId);
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_LOCAL_ENTITY_ID,
						localEntityId);
			}

			Utils.putValueOnlyIfNotNull(json, JSON_ENTITY_SPEC_ID, entitySpecId);
			Utils.putValueOnlyIfNotNull(json, JSON_FIELD_SPEC_ID, fieldSpecId);

			if (remoteValue != null) {
				json.put(JSON_REMOTE_VALUE, remoteValue);
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_LOCAL_VALUE, localValue);
			}
		} catch (JSONException e) {
			Log.e(TAG,
					"Failed to compose JSON for entity field: " + e.toString(),
					e);
			return null;
		}

		return json;
	}

}

package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.EntitiesDao;
import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Entities;
import in.spoors.effort1.provider.EffortProvider.Locations;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Entity implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "Entity";
	private static final String JSON_LOCAL_ID = "clientEntityId";
	private static final String JSON_REMOTE_ID = "entityId";
	private static final String JSON_ENTITY_SPEC_ID = "entitySpecId";
	private static final String JSON_FILLED_BY_ID = "filledBy";
	private static final String JSON_FILLED_BY_NAME = "filledByName";
	private static final String JSON_MODIFIED_BY_ID = "modifiedBy";
	private static final String JSON_MODIFIED_BY_NAME = "modifiedByName";
	private static final String JSON_ASSIGNED_TO_ID = "assignTo";
	private static final String JSON_ASSIGNED_TO_NAME = "assignToName";
	private static final String JSON_DELETED = "deleted";
	private static final String JSON_STATUS = "entityStatus";
	private static final String JSON_REMOTE_CREATION_TIME = "createdTime";
	private static final String JSON_REMOTE_MODIFICATION_TIME = "modifiedTime";
	public static final String JSON_PATH_INTERESTED = "interestedCustomer";

	private Long localId;
	private Long remoteId;
	private Long entitySpecId;
	private Long filledById;
	private String filledByName;
	private Long modifiedById;
	private String modifiedByName;
	private Long assignedToId;
	private String assignedToName;
	private Boolean deleted;
	private Integer status;
	private Boolean cached;
	private Boolean temporary;
	private Boolean dirty;
	private Boolean treeDirty;
	private Date remoteCreationTime;
	private Date remoteModificationTime;
	private Date localCreationTime;
	private Date localModificationTime;

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
	public static Entity parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		// Remote Job Id is mandatory. Thus, null check is not required.
		long remoteId = json.getLong(JSON_REMOTE_ID);
		EntitiesDao entitiesDao = EntitiesDao.getInstance(applicationContext);
		Entity entity = entitiesDao.getEntityWithRemoteId(remoteId);

		if (entity == null && !json.isNull(JSON_LOCAL_ID)) {
			entity = entitiesDao.getEntityWithLocalId(json
					.getLong(JSON_LOCAL_ID));
		}

		if (entity == null) {
			entity = new Entity();
		}

		entity.remoteId = remoteId;
		entity.temporary = false;
		entity.dirty = false;

		entity.entitySpecId = Utils.getLong(json, JSON_ENTITY_SPEC_ID);
		entity.filledById = Utils.getLong(json, JSON_FILLED_BY_ID);
		entity.filledByName = Utils.getString(json, JSON_FILLED_BY_NAME);
		entity.modifiedById = Utils.getLong(json, JSON_MODIFIED_BY_ID);
		entity.modifiedByName = Utils.getString(json, JSON_MODIFIED_BY_NAME);
		entity.assignedToId = Utils.getLong(json, JSON_ASSIGNED_TO_ID);
		entity.assignedToName = Utils.getString(json, JSON_ASSIGNED_TO_NAME);
		entity.deleted = Utils.getBoolean(json, JSON_DELETED);
		entity.status = Utils.getInteger(json, JSON_STATUS);

		if (!json.isNull(JSON_REMOTE_CREATION_TIME)) {
			entity.remoteCreationTime = XsdDateTimeUtils.getLocalTime(json
					.getString(JSON_REMOTE_CREATION_TIME));
		}

		if (!json.isNull(JSON_REMOTE_MODIFICATION_TIME)) {
			entity.remoteModificationTime = XsdDateTimeUtils.getLocalTime(json
					.getString(JSON_REMOTE_MODIFICATION_TIME));
		}

		return entity;
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
	}

	public Long getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(Long remoteId) {
		this.remoteId = remoteId;
	}

	public Long getEntitySpecId() {
		return entitySpecId;
	}

	public void setEntitySpecId(Long entitySpecId) {
		this.entitySpecId = entitySpecId;
	}

	public Long getFilledById() {
		return filledById;
	}

	public void setFilledById(Long filledById) {
		this.filledById = filledById;
	}

	public String getFilledByName() {
		return filledByName;
	}

	public void setFilledByName(String filledByName) {
		this.filledByName = filledByName;
	}

	public Long getModifiedById() {
		return modifiedById;
	}

	public void setModifiedById(Long modifiedById) {
		this.modifiedById = modifiedById;
	}

	public String getModifiedByName() {
		return modifiedByName;
	}

	public void setModifiedByName(String modifiedByName) {
		this.modifiedByName = modifiedByName;
	}

	public Long getAssignedToId() {
		return assignedToId;
	}

	public void setAssignedToId(Long assignedToId) {
		this.assignedToId = assignedToId;
	}

	public String getAssignedToName() {
		return assignedToName;
	}

	public void setAssignedToName(String assignedToName) {
		this.assignedToName = assignedToName;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Boolean getCached() {
		return cached;
	}

	public void setCached(Boolean cached) {
		this.cached = cached;
	}

	public Boolean getTemporary() {
		return temporary;
	}

	public void setTemporary(Boolean temporary) {
		this.temporary = temporary;
	}

	public Boolean getDirty() {
		return dirty;
	}

	public void setDirty(Boolean dirty) {
		this.dirty = dirty;
	}

	public Boolean getTreeDirty() {
		return treeDirty;
	}

	public void setTreeDirty(Boolean treeDirty) {
		this.treeDirty = treeDirty;
	}

	public Date getRemoteCreationTime() {
		return remoteCreationTime;
	}

	public void setRemoteCreationTime(Date remoteCreationTime) {
		this.remoteCreationTime = remoteCreationTime;
	}

	public Date getRemoteModificationTime() {
		return remoteModificationTime;
	}

	public void setRemoteModificationTime(Date remoteModificationTime) {
		this.remoteModificationTime = remoteModificationTime;
	}

	public Date getLocalCreationTime() {
		return localCreationTime;
	}

	public void setLocalCreationTime(Date localCreationTime) {
		this.localCreationTime = localCreationTime;
	}

	public Date getLocalModificationTime() {
		return localModificationTime;
	}

	public void setLocalModificationTime(Date localModificationTime) {
		this.localModificationTime = localModificationTime;
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
			values.put(Entities._ID, localId);
		}

		Utils.putNullOrValue(values, Entities.REMOTE_ID, remoteId);
		Utils.putNullOrValue(values, Entities.ENTITY_SPEC_ID, entitySpecId);
		Utils.putNullOrValue(values, Entities.FILLED_BY_ID, filledById);
		Utils.putNullOrValue(values, Entities.FILLED_BY_NAME, filledByName);
		Utils.putNullOrValue(values, Entities.MODIFIED_BY_ID, modifiedById);
		Utils.putNullOrValue(values, Entities.MODIFIED_BY_NAME, modifiedByName);
		Utils.putNullOrValue(values, Entities.ASSIGNED_TO_ID, assignedToId);
		Utils.putNullOrValue(values, Entities.ASSIGNED_TO_NAME, assignedToName);
		Utils.putNullOrValue(values, Entities.DELETED, deleted);
		Utils.putNullOrValue(values, Entities.STATUS, status);
		Utils.putNullOrValue(values, Entities.CACHED, cached);
		Utils.putNullOrValue(values, Entities.TEMPORARY, temporary);
		Utils.putNullOrValue(values, Entities.DIRTY, dirty);
		Utils.putNullOrValue(values, Entities.TREE_DIRTY, treeDirty);
		Utils.putNullOrValue(values, Entities.REMOTE_CREATION_TIME,
				remoteCreationTime);
		Utils.putNullOrValue(values, Entities.REMOTE_MODIFICATION_TIME,
				remoteModificationTime);
		Utils.putNullOrValue(values, Entities.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, Entities.LOCAL_MODIFICATION_TIME,
				localModificationTime);

		return values;
	}

	public void load(Cursor cursor, Context applicationContext) {
		localId = cursor.isNull(Entities._ID_INDEX) ? null : cursor
				.getLong(EffortProvider.Entities._ID_INDEX);
		remoteId = cursor.isNull(Entities.REMOTE_ID_INDEX) ? null : cursor
				.getLong(Entities.REMOTE_ID_INDEX);
		entitySpecId = cursor.isNull(Entities.ENTITY_SPEC_ID_INDEX) ? null
				: cursor.getLong(Entities.ENTITY_SPEC_ID_INDEX);
		filledById = cursor.isNull(Entities.FILLED_BY_ID_INDEX) ? null : cursor
				.getLong(Entities.FILLED_BY_ID_INDEX);
		filledByName = cursor.getString(Entities.FILLED_BY_NAME_INDEX);
		modifiedById = cursor.isNull(Entities.MODIFIED_BY_ID_INDEX) ? null
				: cursor.getLong(Entities.MODIFIED_BY_ID_INDEX);
		modifiedByName = cursor.getString(Entities.MODIFIED_BY_NAME_INDEX);
		assignedToId = cursor.isNull(Entities.ASSIGNED_TO_ID_INDEX) ? null
				: cursor.getLong(Entities.ASSIGNED_TO_ID_INDEX);
		assignedToName = cursor.getString(Entities.ASSIGNED_TO_NAME_INDEX);
		deleted = cursor.isNull(Entities.DELETED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Entities.DELETED_INDEX));
		status = cursor.isNull(Entities.STATUS_INDEX) ? null : cursor
				.getInt(Entities.STATUS_INDEX);
		cached = cursor.isNull(Entities.CACHED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Entities.CACHED_INDEX));
		temporary = cursor.isNull(Entities.TEMPORARY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Entities.TEMPORARY_INDEX));
		dirty = cursor.isNull(Entities.DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Entities.DIRTY_INDEX));
		treeDirty = cursor.isNull(Entities.TREE_DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Entities.TREE_DIRTY_INDEX));
		remoteCreationTime = cursor.isNull(Entities.REMOTE_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Entities.REMOTE_CREATION_TIME_INDEX));
		remoteModificationTime = cursor
				.isNull(Entities.REMOTE_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Entities.REMOTE_MODIFICATION_TIME_INDEX));
		localCreationTime = cursor.isNull(Entities.LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Entities.LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(Entities.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Entities.LOCAL_MODIFICATION_TIME_INDEX));
	}

	@Override
	public String toString() {
		return "Entity [localId=" + localId + ", remoteId=" + remoteId
				+ ", entitySpecId=" + entitySpecId + ", filledById="
				+ filledById + ", filledByName=" + filledByName
				+ ", modifiedById=" + modifiedById + ", modifiedByName="
				+ modifiedByName + ", assignedToId=" + assignedToId
				+ ", assignedToName=" + assignedToName + ", deleted=" + deleted
				+ ", status=" + status + ", cached=" + cached + ", temporary="
				+ temporary + ", dirty=" + dirty + ", treeDirty=" + treeDirty
				+ ", remoteCreationTime=" + remoteCreationTime
				+ ", remoteModificationTime=" + remoteModificationTime
				+ ", localCreationTime=" + localCreationTime
				+ ", localModificationTime=" + localModificationTime + "]";
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

			if (remoteId == null) {
				json.put(JSON_LOCAL_ID, localId);

				LocationsDao locationsDao = LocationsDao
						.getInstance(applicationContext);
				LocationDto location = locationsDao.getFormLocation(localId);

				if (location != null) {
					json.put("location", location.getJsonObject(
							applicationContext, Locations.PURPOSE_NOTE));
				}
			} else {
				json.put(JSON_REMOTE_ID, remoteId);
			}

			if (entitySpecId != null) {
				json.put(JSON_ENTITY_SPEC_ID, entitySpecId);
			}

			// if (filledById != null) {
			// json.put(JSON_FILLED_BY_ID, filledById);
			// }
			//
			// if (filledByName != null) {
			// json.put(JSON_FILLED_BY_NAME, filledByName);
			// }
			//
			// if (modifiedById != null) {
			// json.put(JSON_MODIFIED_BY_ID, modifiedById);
			// }
			//
			// if (modifiedByName != null) {
			// json.put(JSON_MODIFIED_BY_NAME, modifiedByName);
			// }
			//
			// if (assignedToId != null) {
			// json.put(JSON_ASSIGNED_TO_ID, assignedToId);
			// }
			//
			// if (assignedToName != null) {
			// json.put(JSON_ASSIGNED_TO_NAME, assignedToName);
			// }
			//
			// if (deleted != null) {
			// json.put(JSON_DELETED, deleted);
			// }
			//
			// if (status != null) {
			// json.put(JSON_STATUS, status);
			// }
		} catch (JSONException e) {
			Log.e(TAG, "Failed to compose JSON for entity: " + e.toString(), e);
			return null;
		}

		return json;
	}
}

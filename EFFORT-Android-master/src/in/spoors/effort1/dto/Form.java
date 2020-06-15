package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Forms;
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

public class Form implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "Form";
	private static final String JSON_LOCAL_ID = "clientFormId";
	private static final String JSON_REMOTE_ID = "formId";
	private static final String JSON_FORM_SPEC_ID = "formSpecId";
	private static final String JSON_FILLED_BY_ID = "filledBy";
	private static final String JSON_FILLED_BY_NAME = "filledByName";
	private static final String JSON_MODIFIED_BY_ID = "modifiedBy";
	private static final String JSON_MODIFIED_BY_NAME = "modifiedByName";
	private static final String JSON_ASSIGNED_TO_ID = "assignTo";
	private static final String JSON_ASSIGNED_TO_NAME = "assignToName";
	private static final String JSON_DELETED = "deleted";
	private static final String JSON_STATUS = "formStatus";
	private static final String JSON_REMOTE_CREATION_TIME = "createdTime";
	private static final String JSON_REMOTE_MODIFICATION_TIME = "modifiedTime";
	private static final String JSON_REMOTE_LOCATION_ID = "locationId";

	private Long localId;
	private Long remoteId;
	private Long formSpecId;
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
	private Long remoteLocationId;
	private String formSpecUniqueId;

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
	public static Form parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		// Remote Job Id is mandatory. Thus, null check is not required.
		long remoteId = json.getLong(JSON_REMOTE_ID);
		FormsDao formsDao = FormsDao.getInstance(applicationContext);
		Form form = formsDao.getFormWithRemoteId(remoteId);

		if (form == null && !json.isNull(JSON_LOCAL_ID)) {
			form = formsDao.getFormWithLocalId(json.getLong(JSON_LOCAL_ID));
		}

		if (form == null) {
			form = new Form();
		}

		form.remoteId = remoteId;
		form.temporary = false;
		form.dirty = false;

		form.formSpecId = Utils.getLong(json, JSON_FORM_SPEC_ID);
		form.filledById = Utils.getLong(json, JSON_FILLED_BY_ID);
		form.filledByName = Utils.getString(json, JSON_FILLED_BY_NAME);
		form.modifiedById = Utils.getLong(json, JSON_MODIFIED_BY_ID);
		form.modifiedByName = Utils.getString(json, JSON_MODIFIED_BY_NAME);
		form.assignedToId = Utils.getLong(json, JSON_ASSIGNED_TO_ID);
		form.assignedToName = Utils.getString(json, JSON_ASSIGNED_TO_NAME);
		form.deleted = Utils.getBoolean(json, JSON_DELETED);
		form.status = Utils.getInteger(json, JSON_STATUS);
		form.remoteLocationId = Utils.getLong(json, JSON_REMOTE_LOCATION_ID);

		if (!json.isNull(JSON_REMOTE_CREATION_TIME)) {
			form.remoteCreationTime = XsdDateTimeUtils.getLocalTime(json
					.getString(JSON_REMOTE_CREATION_TIME));
		}

		if (!json.isNull(JSON_REMOTE_MODIFICATION_TIME)) {
			form.remoteModificationTime = XsdDateTimeUtils.getLocalTime(json
					.getString(JSON_REMOTE_MODIFICATION_TIME));
		}

		return form;
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

	public Long getFormSpecId() {
		return formSpecId;
	}

	public void setFormSpecId(Long formSpecId) {
		this.formSpecId = formSpecId;
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

	public Long getRemoteLocationId() {
		return remoteLocationId;
	}

	public void setRemoteLocationId(Long remoteLocationId) {
		this.remoteLocationId = remoteLocationId;
	}

	public String getFormSpecUniqueId() {
		return formSpecUniqueId;
	}

	public void setFormSpecUniqueId(String formSpecUniqueId) {
		this.formSpecUniqueId = formSpecUniqueId;
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
			values.put(Forms._ID, localId);
		}

		Utils.putNullOrValue(values, Forms.REMOTE_ID, remoteId);
		Utils.putNullOrValue(values, Forms.FORM_SPEC_ID, formSpecId);
		Utils.putNullOrValue(values, Forms.FILLED_BY_ID, filledById);
		Utils.putNullOrValue(values, Forms.FILLED_BY_NAME, filledByName);
		Utils.putNullOrValue(values, Forms.MODIFIED_BY_ID, modifiedById);
		Utils.putNullOrValue(values, Forms.MODIFIED_BY_NAME, modifiedByName);
		Utils.putNullOrValue(values, Forms.ASSIGNED_TO_ID, assignedToId);
		Utils.putNullOrValue(values, Forms.ASSIGNED_TO_NAME, assignedToName);
		Utils.putNullOrValue(values, Forms.DELETED, deleted);
		Utils.putNullOrValue(values, Forms.STATUS, status);
		Utils.putNullOrValue(values, Forms.CACHED, cached);
		Utils.putNullOrValue(values, Forms.TEMPORARY, temporary);
		Utils.putNullOrValue(values, Forms.DIRTY, dirty);
		Utils.putNullOrValue(values, Forms.TREE_DIRTY, treeDirty);
		Utils.putNullOrValue(values, Forms.REMOTE_CREATION_TIME,
				remoteCreationTime);
		Utils.putNullOrValue(values, Forms.REMOTE_MODIFICATION_TIME,
				remoteModificationTime);
		Utils.putNullOrValue(values, Forms.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, Forms.LOCAL_MODIFICATION_TIME,
				localModificationTime);
		Utils.putNullOrValue(values, Forms.REMOTE_LOCATION_ID, remoteLocationId);
		Utils.putNullOrValue(values, Forms.FORM_SPEC_UNIQUE_ID,
				formSpecUniqueId);

		return values;
	}

	public void load(Cursor cursor, Context applicationContext) {
		localId = cursor.isNull(Forms._ID_INDEX) ? null : cursor
				.getLong(EffortProvider.Forms._ID_INDEX);
		remoteId = cursor.isNull(Forms.REMOTE_ID_INDEX) ? null : cursor
				.getLong(Forms.REMOTE_ID_INDEX);
		formSpecId = cursor.isNull(Forms.FORM_SPEC_ID_INDEX) ? null : cursor
				.getLong(Forms.FORM_SPEC_ID_INDEX);
		filledById = cursor.isNull(Forms.FILLED_BY_ID_INDEX) ? null : cursor
				.getLong(Forms.FILLED_BY_ID_INDEX);
		filledByName = cursor.getString(Forms.FILLED_BY_NAME_INDEX);
		modifiedById = cursor.isNull(Forms.MODIFIED_BY_ID_INDEX) ? null
				: cursor.getLong(Forms.MODIFIED_BY_ID_INDEX);
		modifiedByName = cursor.getString(Forms.MODIFIED_BY_NAME_INDEX);
		assignedToId = cursor.isNull(Forms.ASSIGNED_TO_ID_INDEX) ? null
				: cursor.getLong(Forms.ASSIGNED_TO_ID_INDEX);
		assignedToName = cursor.getString(Forms.ASSIGNED_TO_NAME_INDEX);
		deleted = cursor.isNull(Forms.DELETED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Forms.DELETED_INDEX));
		status = cursor.isNull(Forms.STATUS_INDEX) ? null : cursor
				.getInt(Forms.STATUS_INDEX);
		cached = cursor.isNull(Forms.CACHED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Forms.CACHED_INDEX));
		temporary = cursor.isNull(Forms.TEMPORARY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Forms.TEMPORARY_INDEX));
		dirty = cursor.isNull(Forms.DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Forms.DIRTY_INDEX));
		treeDirty = cursor.isNull(Forms.TREE_DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Forms.TREE_DIRTY_INDEX));
		remoteCreationTime = cursor.isNull(Forms.REMOTE_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Forms.REMOTE_CREATION_TIME_INDEX));
		remoteModificationTime = cursor
				.isNull(Forms.REMOTE_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Forms.REMOTE_MODIFICATION_TIME_INDEX));
		localCreationTime = cursor.isNull(Forms.LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Forms.LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(Forms.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Forms.LOCAL_MODIFICATION_TIME_INDEX));
		remoteLocationId = cursor.isNull(Forms.REMOTE_LOCATION_ID_INDEX) ? null
				: cursor.getLong(Forms.REMOTE_LOCATION_ID_INDEX);
		formSpecUniqueId = cursor.getString(Forms.FORM_SPEC_UNIQUE_ID_INDEX);
	}

	@Override
	public String toString() {
		return "Form [localId=" + localId + ", remoteId=" + remoteId
				+ ", formSpecId=" + formSpecId + ", filledById=" + filledById
				+ ", filledByName=" + filledByName + ", modifiedById="
				+ modifiedById + ", modifiedByName=" + modifiedByName
				+ ", assignedToId=" + assignedToId + ", assignedToName="
				+ assignedToName + ", deleted=" + deleted + ", status="
				+ status + ", cached=" + cached + ", temporary=" + temporary
				+ ", dirty=" + dirty + ", treeDirty=" + treeDirty
				+ ", remoteCreationTime=" + remoteCreationTime
				+ ", remoteModificationTime=" + remoteModificationTime
				+ ", localCreationTime=" + localCreationTime
				+ ", localModificationTime=" + localModificationTime
				+ ", remoteLocationId=" + remoteLocationId
				+ ", formSpecUniqueId=" + formSpecUniqueId + "]";
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
			} else {
				json.put(JSON_REMOTE_ID, remoteId);
			}

			LocationsDao locationsDao = LocationsDao
					.getInstance(applicationContext);
			LocationDto location = locationsDao.getFormLocation(localId);

			if (location != null) {
				json.put("location", location.getJsonObject(applicationContext,
						Locations.PURPOSE_FORM));
			}

			Utils.putValueOnlyIfNotNull(json, JSON_FORM_SPEC_ID, formSpecId);
			Utils.putValueOnlyIfNotNull(json, JSON_REMOTE_LOCATION_ID,
					remoteLocationId);

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
			Log.e(TAG, "Failed to compose JSON for job: " + e.toString(), e);
			return null;
		}

		return json;
	}
}

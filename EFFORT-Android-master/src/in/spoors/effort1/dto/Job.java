package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Jobs;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Job implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "Job";
	private static final String JSON_LOCAL_ID = "clientVisitId";
	private static final String JSON_REMOTE_ID = "visitId";
	private static final String JSON_TYPE_ID = "visitTypeId";
	private static final String JSON_STATE_ID = "visitStateId";
	private static final String JSON_TITLE = "empVisitTitle";
	private static final String JSON_DESCRIPTION = "empVisitDesc";
	private static final String JSON_EMPLOYEE_ID = "empId";
	private static final String JSON_LOCAL_CUSTOMER_ID = "clientCustomerId";
	private static final String JSON_REMOTE_CUSTOMER_ID = "customerId";
	private static final String JSON_START_TIME = "visitStartTime";
	private static final String JSON_END_TIME = "visitEndTime";
	private static final String JSON_COMPLETION_TIME = "completeTime";
	private static final String JSON_COMPLETED = "completed";
	private static final String JSON_APPROVED = "approved";
	private static final String JSON_REMOTE_MODIFICATION_TIME = "modifiedTime";
	private static final String JSON_LATITUDE = "addressLat";
	private static final String JSON_LONGITUDE = "addressLng";
	private static final String JSON_STREET = "addressStreet";
	private static final String JSON_AREA = "addressArea";
	private static final String JSON_CITY = "addressCity";
	private static final String JSON_STATE = "addressState";
	private static final String JSON_COUNTRY = "addressCountry";
	private static final String JSON_PIN_CODE = "addressPincode";
	private static final String JSON_LANDMARK = "addressLandMark";
	private static final String JSON_SAME_AS_CUSTOMER_ADDRESS = "addressSameAsCustomer";

	private Long localId;
	private Long remoteId;
	private Integer typeId;
	private Integer stateId;
	private String title;
	private String description;
	private Date startTime;
	private Date endTime;
	private Long employeeId;
	private Long localCustomerId;

	/**
	 * This must not be persisted into the database.
	 */
	private Long remoteCustomerId;

	private Boolean completed;
	private Date completionTime;
	private Boolean approved;
	private Integer androidEventId;
	private Boolean dirty;
	private Boolean treeDirty;
	private Boolean read;
	private Boolean temporary;
	private Boolean cached;
	private Date remoteModificationTime;
	private Date localCreationTime;
	private Date localModificationTime;
	private Double latitude;
	private Double longitude;
	private String street;
	private String area;
	private String city;
	private String state;
	private String country;
	private String pinCode;
	private String landmark;
	private Boolean sameAsCustomerAddress;
	private Boolean lateAlertDismissed;

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
	public static Job parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		// Remote Job Id is mandatory. Thus, null check is not required.
		long remoteId = json.getLong(JSON_REMOTE_ID);
		JobsDao jobsDao = JobsDao.getInstance(applicationContext);
		Job job = jobsDao.getJobWithRemoteId(remoteId);

		Long localId = Utils.getLong(json, JSON_LOCAL_ID);

		if (job == null && localId != null) {
			job = jobsDao.getJobWithLocalId(localId);
		}

		if (job == null) {
			job = new Job();
		}

		if (localId != null) {
			job.localId = localId;
		}

		job.remoteId = remoteId;
		job.temporary = false;
		job.lateAlertDismissed = false;

		job.typeId = Utils.getInteger(json, JSON_TYPE_ID);
		job.stateId = Utils.getInteger(json, JSON_STATE_ID);
		job.title = Utils.getString(json, JSON_TITLE);
		job.description = Utils.getString(json, JSON_DESCRIPTION);
		job.employeeId = Utils.getLong(json, JSON_EMPLOYEE_ID);

		job.localCustomerId = Utils.getLong(json, JSON_LOCAL_CUSTOMER_ID);
		job.remoteCustomerId = Utils.getLong(json, JSON_REMOTE_CUSTOMER_ID);
		job.startTime = Utils.getDate(json, JSON_START_TIME);
		job.endTime = Utils.getDate(json, JSON_END_TIME);
		job.completionTime = Utils.getDate(json, JSON_COMPLETION_TIME);
		job.completed = Utils.getBoolean(json, JSON_COMPLETED);
		job.approved = Utils.getBoolean(json, JSON_APPROVED);
		job.remoteModificationTime = Utils.getDate(json,
				JSON_REMOTE_MODIFICATION_TIME);
		job.latitude = Utils.getDouble(json, JSON_LATITUDE);
		job.longitude = Utils.getDouble(json, JSON_LONGITUDE);
		job.street = Utils.getString(json, JSON_STREET);
		job.area = Utils.getString(json, JSON_AREA);
		job.city = Utils.getString(json, JSON_CITY);
		job.state = Utils.getString(json, JSON_STATE);
		job.country = Utils.getString(json, JSON_COUNTRY);
		job.pinCode = Utils.getString(json, JSON_PIN_CODE);
		job.landmark = Utils.getString(json, JSON_LANDMARK);
		job.sameAsCustomerAddress = Utils.getBoolean(json,
				JSON_SAME_AS_CUSTOMER_ADDRESS);

		return job;
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

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Long getLocalCustomerId() {
		return localCustomerId;
	}

	public void setLocalCustomerId(Long localCustomerId) {
		this.localCustomerId = localCustomerId;
	}

	public Long getRemoteCustomerId() {
		return remoteCustomerId;
	}

	public void setRemoteCustomerId(Long remoteCustomerId) {
		this.remoteCustomerId = remoteCustomerId;
	}

	public Boolean getCompleted() {
		return completed;
	}

	public void setCompleted(Boolean completed) {
		this.completed = completed;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	/**
	 * Flag that indicates whether there are any unsynced changes
	 * 
	 * @return
	 */
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

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public Boolean getTemporary() {
		return temporary;
	}

	public void setTemporary(Boolean temporary) {
		this.temporary = temporary;
	}

	public Boolean getCached() {
		return cached;
	}

	public void setCached(Boolean cached) {
		this.cached = cached;
	}

	public Date getCompletionTime() {
		return completionTime;
	}

	public void setCompletionTime(Date completionTime) {
		this.completionTime = completionTime;
	}

	public Integer getAndroidEventId() {
		return androidEventId;
	}

	public void setAndroidEventId(Integer androidEventId) {
		this.androidEventId = androidEventId;
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

	public Date getRemoteModificationTime() {
		return remoteModificationTime;
	}

	public void setRemoteModificationTime(Date remoteModificationTime) {
		this.remoteModificationTime = remoteModificationTime;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getLandmark() {
		return landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	public Boolean getSameAsCustomerAddress() {
		return sameAsCustomerAddress;
	}

	public void setSameAsCustomerAddress(Boolean sameAsCustomerAddress) {
		this.sameAsCustomerAddress = sameAsCustomerAddress;
	}

	public Boolean getLateAlertDismissed() {
		return lateAlertDismissed;
	}

	public void setLateAlertDismissed(Boolean lateAlertDismissed) {
		this.lateAlertDismissed = lateAlertDismissed;
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
			values.put(Jobs._ID, localId);
		}

		Utils.putNullOrValue(values, Jobs.REMOTE_ID, remoteId);
		Utils.putNullOrValue(values, Jobs.JOB_TYPE_ID, typeId);
		Utils.putNullOrValue(values, Jobs.JOB_STATE_ID, stateId);
		Utils.putNullOrValue(values, Jobs.TITLE, title);
		Utils.putNullOrValue(values, Jobs.DESCRIPTION, description);
		Utils.putNullOrValue(values, Jobs.START_TIME, startTime);
		Utils.putNullOrValue(values, Jobs.END_TIME, endTime);
		Utils.putNullOrValue(values, Jobs.EMPLOYEE_ID, employeeId);
		Utils.putNullOrValue(values, Jobs.LOCAL_CUSTOMER_ID, localCustomerId);
		Utils.putNullOrValue(values, Jobs.COMPLETED, completed);
		Utils.putNullOrValue(values, Jobs.COMPLETION_TIME, completionTime);
		Utils.putNullOrValue(values, Jobs.APPROVED, approved);
		Utils.putNullOrValue(values, Jobs.ANDROID_EVENT_ID, androidEventId);
		Utils.putNullOrValue(values, Jobs.DIRTY, dirty);
		Utils.putNullOrValue(values, Jobs.TREE_DIRTY, treeDirty);
		Utils.putNullOrValue(values, Jobs.READ, read);
		Utils.putNullOrValue(values, Jobs.TEMPORARY, temporary);
		Utils.putNullOrValue(values, Jobs.CACHED, cached);
		Utils.putNullOrValue(values, Jobs.REMOTE_MODIFICATION_TIME,
				remoteModificationTime);
		Utils.putNullOrValue(values, Jobs.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, Jobs.LOCAL_MODIFICATION_TIME,
				localModificationTime);
		Utils.putNullOrValue(values, Jobs.LATITUDE, latitude);
		Utils.putNullOrValue(values, Jobs.LONGITUDE, longitude);
		Utils.putNullOrValue(values, Jobs.STREET, street);
		Utils.putNullOrValue(values, Jobs.AREA, area);
		Utils.putNullOrValue(values, Jobs.CITY, city);
		Utils.putNullOrValue(values, Jobs.STATE, state);
		Utils.putNullOrValue(values, Jobs.COUNTRY, country);
		Utils.putNullOrValue(values, Jobs.PIN_CODE, pinCode);
		Utils.putNullOrValue(values, Jobs.LANDMARK, landmark);
		Utils.putNullOrValue(values, Jobs.SAME_AS_CUSTOMER_ADDRESS,
				sameAsCustomerAddress);
		Utils.putNullOrValue(values, Jobs.LATE_ALERT_DISMISSED,
				lateAlertDismissed);

		return values;
	}

	public void load(Cursor cursor, Context applicationContext) {
		localId = cursor.isNull(Jobs._ID_INDEX) ? null : cursor
				.getLong(EffortProvider.Jobs._ID_INDEX);
		remoteId = cursor.isNull(Jobs.REMOTE_ID_INDEX) ? null : cursor
				.getLong(Jobs.REMOTE_ID_INDEX);
		typeId = cursor.isNull(Jobs.JOB_TYPE_ID_INDEX) ? null : cursor
				.getInt(Jobs.JOB_TYPE_ID_INDEX);
		stateId = cursor.isNull(Jobs.JOB_STATE_ID_INDEX) ? null : cursor
				.getInt(Jobs.JOB_STATE_ID_INDEX);
		employeeId = cursor.isNull(Jobs.EMPLOYEE_ID_INDEX) ? null : cursor
				.getLong(Jobs.EMPLOYEE_ID_INDEX);
		localCustomerId = cursor.isNull(Jobs.LOCAL_CUSTOMER_ID_INDEX) ? null
				: cursor.getLong(Jobs.LOCAL_CUSTOMER_ID_INDEX);

		CustomersDao customersDao = CustomersDao
				.getInstance(applicationContext);

		if (localCustomerId != null) {
			remoteCustomerId = customersDao.getRemoteId(localCustomerId);
		}

		title = cursor.getString(Jobs.TITLE_INDEX);
		description = cursor.getString(Jobs.DESCRIPTION_INDEX);
		startTime = cursor.isNull(Jobs.START_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Jobs.START_TIME_INDEX));
		endTime = cursor.isNull(Jobs.END_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Jobs.END_TIME_INDEX));
		completed = cursor.isNull(Jobs.COMPLETED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Jobs.COMPLETED_INDEX));
		completionTime = cursor.isNull(Jobs.COMPLETION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Jobs.COMPLETION_TIME_INDEX));
		approved = cursor.isNull(Jobs.APPROVED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Jobs.APPROVED_INDEX));
		androidEventId = cursor.isNull(Jobs.ANROID_EVENT_ID_INDEX) ? null
				: cursor.getInt(Jobs.ANROID_EVENT_ID_INDEX);
		dirty = cursor.isNull(Jobs.DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Jobs.DIRTY_INDEX));
		treeDirty = cursor.isNull(Jobs.TREE_DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Jobs.TREE_DIRTY_INDEX));
		read = cursor.isNull(Jobs.READ_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Jobs.READ_INDEX));
		temporary = cursor.isNull(Jobs.TEMPORARY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Jobs.TEMPORARY_INDEX));
		cached = cursor.isNull(Jobs.CACHED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Jobs.CACHED_INDEX));
		remoteModificationTime = cursor
				.isNull(Jobs.REMOTE_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Jobs.REMOTE_MODIFICATION_TIME_INDEX));
		localCreationTime = cursor.isNull(Jobs.LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Jobs.LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(Jobs.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Jobs.LOCAL_MODIFICATION_TIME_INDEX));
		latitude = cursor.isNull(Jobs.LATITUDE_INDEX) ? null : cursor
				.getDouble(Jobs.LATITUDE_INDEX);
		longitude = cursor.isNull(Jobs.LONGITUDE_INDEX) ? null : cursor
				.getDouble(Jobs.LONGITUDE_INDEX);
		street = cursor.getString(Jobs.STREET_INDEX);
		area = cursor.getString(Jobs.AREA_INDEX);
		city = cursor.getString(Jobs.CITY_INDEX);
		state = cursor.getString(Jobs.STATE_INDEX);
		country = cursor.getString(Jobs.COUNTRY_INDEX);
		pinCode = cursor.getString(Jobs.PIN_CODE_INDEX);
		landmark = cursor.getString(Jobs.LANDMARK_INDEX);
		sameAsCustomerAddress = cursor
				.isNull(Jobs.SAME_AS_CUSTOMER_ADDRESS_INDEX) ? null : Boolean
				.parseBoolean(cursor
						.getString(Jobs.SAME_AS_CUSTOMER_ADDRESS_INDEX));
		lateAlertDismissed = cursor.isNull(Jobs.LATE_ALERT_DISMISSED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Jobs.LATE_ALERT_DISMISSED_INDEX));
	}

	@Override
	public String toString() {
		return "Job [localId=" + localId + ", remoteId=" + remoteId
				+ ", typeId=" + typeId + ", stateId=" + stateId + ", title="
				+ title + ", description=" + description + ", startTime="
				+ startTime + ", endTime=" + endTime + ", employeeId="
				+ employeeId + ", localCustomerId=" + localCustomerId
				+ ", remoteCustomerId=" + remoteCustomerId + ", completed="
				+ completed + ", completionTime=" + completionTime
				+ ", approved=" + approved + ", androidEventId="
				+ androidEventId + ", dirty=" + dirty + ", treeDirty="
				+ treeDirty + ", read=" + read + ", temporary=" + temporary
				+ ", cached=" + cached + ", remoteModificationTime="
				+ remoteModificationTime + ", localCreationTime="
				+ localCreationTime + ", localModificationTime="
				+ localModificationTime + ", latitude=" + latitude
				+ ", longitude=" + longitude + ", street=" + street + ", area="
				+ area + ", city=" + city + ", state=" + state + ", country="
				+ country + ", pinCode=" + pinCode + ", landmark=" + landmark
				+ ", sameAsCustomerAddress=" + sameAsCustomerAddress
				+ ", lateAlertDismissed=" + lateAlertDismissed + "]";
	}

	/**
	 * Get the JSON object that can be sent to the server.
	 * 
	 * @param applicationContext
	 *            Used for reading JSON path constants from resources.
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

			// if remote customer id is present, don't send the local customer
			// id to the server
			if (remoteCustomerId != null) {
				json.put(JSON_REMOTE_CUSTOMER_ID, remoteCustomerId);
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_LOCAL_CUSTOMER_ID,
						localCustomerId);
			}

			Utils.putValueOnlyIfNotNull(json, JSON_TYPE_ID, typeId);
			Utils.putValueOnlyIfNotNull(json, JSON_STATE_ID, stateId);
			Utils.putValueOnlyIfNotNull(json, JSON_TITLE, title);
			Utils.putValueOnlyIfNotNull(json, JSON_DESCRIPTION, description);

			if (employeeId == null) {
				SettingsDao settingsDao = SettingsDao
						.getInstance(applicationContext);
				Utils.putValueOnlyIfNotNull(json, JSON_EMPLOYEE_ID,
						settingsDao.getLong(Settings.KEY_EMPLOYEE_ID));
			} else {
				Utils.putValueOnlyIfNotNull(json, JSON_EMPLOYEE_ID, employeeId);
			}

			Utils.putValueOnlyIfNotNull(json, JSON_START_TIME, startTime);
			Utils.putValueOnlyIfNotNull(json, JSON_END_TIME, endTime);
			Utils.putValueOnlyIfNotNull(json, JSON_COMPLETED, completed);
			Utils.putValueOnlyIfNotNull(json, JSON_COMPLETION_TIME,
					completionTime);
			Utils.putValueOnlyIfNotNull(json, JSON_APPROVED, approved);
			Utils.putValueOnlyIfNotNull(json, JSON_LATITUDE, latitude);
			Utils.putValueOnlyIfNotNull(json, JSON_LONGITUDE, longitude);
			Utils.putValueOnlyIfNotNull(json, JSON_STREET, street);
			Utils.putValueOnlyIfNotNull(json, JSON_AREA, area);
			Utils.putValueOnlyIfNotNull(json, JSON_CITY, city);
			Utils.putValueOnlyIfNotNull(json, JSON_STATE, state);
			Utils.putValueOnlyIfNotNull(json, JSON_COUNTRY, country);
			Utils.putValueOnlyIfNotNull(json, JSON_PIN_CODE, pinCode);
			Utils.putValueOnlyIfNotNull(json, JSON_LANDMARK, landmark);
			Utils.putValueOnlyIfNotNull(json, JSON_SAME_AS_CUSTOMER_ADDRESS,
					sameAsCustomerAddress);
			// we shouldn't send remote modification time to server
		} catch (JSONException e) {
			Log.e(TAG, "Failed to compose JSON for job: " + e.toString(), e);
			return null;
		}

		return json;
	}

}

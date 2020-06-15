package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.InvitationsDao;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Invitations;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Invitation implements Serializable {

	public static final String TAG = "JobInvitation";
	private static final long serialVersionUID = 1L;
	private static final String JSON_ID = "visitInvitationId";
	private static final String JSON_TYPE_ID = "visitTypeId";
	private static final String JSON_TITLE = "visitTitle";
	private static final String JSON_DESCRIPTION = "visitDesc";
	private static final String JSON_REMOTE_CUSTOMER_ID = "customerId";
	private static final String JSON_START_TIME = "visitStartTime";
	private static final String JSON_END_TIME = "visitEndTime";
	private static final String JSON_LATITUDE = "lat";
	private static final String JSON_LONGITUDE = "lng";
	private static final String JSON_STREET = "street";
	private static final String JSON_AREA = "area";
	private static final String JSON_CITY = "city";
	private static final String JSON_STATE = "addState";
	private static final String JSON_COUNTRY = "country";
	private static final String JSON_PIN_CODE = "pincode";
	private static final String JSON_LANDMARK = "landMark";

	private Long id;
	private Integer typeId;
	private String title;
	private String description;
	private Date startTime;
	private Date endTime;
	private Long localCustomerId;

	/**
	 * This must not be persisted into the database.
	 */
	private Long remoteCustomerId;

	private Boolean read;
	private Boolean accepted;
	private Boolean dirty;
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
	public static Invitation parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		// Remote Job Id is mandatory. Thus, null check is not required.
		long id = json.getLong(JSON_ID);
		InvitationsDao jobInvitationsDao = InvitationsDao
				.getInstance(applicationContext);
		Invitation invitation = jobInvitationsDao.getInvitationWithId(id);

		if (invitation == null) {
			invitation = new Invitation();
			invitation.read = false;
			invitation.accepted = false;
			invitation.dirty = false;
		}

		invitation.id = id;

		invitation.typeId = Utils.getInteger(json, JSON_TYPE_ID);
		invitation.title = Utils.getString(json, JSON_TITLE);
		invitation.description = Utils.getString(json, JSON_DESCRIPTION);
		invitation.remoteCustomerId = Utils.getLong(json,
				JSON_REMOTE_CUSTOMER_ID);
		invitation.startTime = Utils.getDate(json, JSON_START_TIME);
		invitation.endTime = Utils.getDate(json, JSON_END_TIME);
		invitation.latitude = Utils.getDouble(json, JSON_LATITUDE);
		invitation.longitude = Utils.getDouble(json, JSON_LONGITUDE);
		invitation.street = Utils.getString(json, JSON_STREET);
		invitation.area = Utils.getString(json, JSON_AREA);
		invitation.city = Utils.getString(json, JSON_CITY);
		invitation.state = Utils.getString(json, JSON_STATE);
		invitation.country = Utils.getString(json, JSON_COUNTRY);
		invitation.pinCode = Utils.getString(json, JSON_PIN_CODE);
		invitation.landmark = Utils.getString(json, JSON_LANDMARK);

		return invitation;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
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

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
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
			values.put(Invitations._ID, id);
		}

		Utils.putNullOrValue(values, Invitations.JOB_TYPE_ID, typeId);
		Utils.putNullOrValue(values, Invitations.TITLE, title);
		Utils.putNullOrValue(values, Invitations.DESCRIPTION, description);
		Utils.putNullOrValue(values, Invitations.START_TIME, startTime);
		Utils.putNullOrValue(values, Invitations.END_TIME, endTime);
		Utils.putNullOrValue(values, Invitations.LOCAL_CUSTOMER_ID,
				localCustomerId);
		Utils.putNullOrValue(values, Invitations.READ, read);
		Utils.putNullOrValue(values, Invitations.ACCEPTED, accepted);
		Utils.putNullOrValue(values, Invitations.DIRTY, dirty);
		Utils.putNullOrValue(values, Invitations.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, Invitations.LOCAL_MODIFICATION_TIME,
				localModificationTime);
		Utils.putNullOrValue(values, Invitations.LATITUDE, latitude);
		Utils.putNullOrValue(values, Invitations.LONGITUDE, longitude);
		Utils.putNullOrValue(values, Invitations.STREET, street);
		Utils.putNullOrValue(values, Invitations.AREA, area);
		Utils.putNullOrValue(values, Invitations.CITY, city);
		Utils.putNullOrValue(values, Invitations.STATE, state);
		Utils.putNullOrValue(values, Invitations.COUNTRY, country);
		Utils.putNullOrValue(values, Invitations.PIN_CODE, pinCode);
		Utils.putNullOrValue(values, Invitations.LANDMARK, landmark);

		return values;
	}

	public void load(Cursor cursor, Context applicationContext) {
		id = cursor.isNull(Invitations._ID_INDEX) ? null : cursor
				.getLong(EffortProvider.Invitations._ID_INDEX);
		typeId = cursor.isNull(Invitations.JOB_TYPE_ID_INDEX) ? null : cursor
				.getInt(Invitations.JOB_TYPE_ID_INDEX);
		localCustomerId = cursor.isNull(Invitations.LOCAL_CUSTOMER_ID_INDEX) ? null
				: cursor.getLong(Invitations.LOCAL_CUSTOMER_ID_INDEX);

		CustomersDao customersDao = CustomersDao
				.getInstance(applicationContext);
		remoteCustomerId = customersDao.getRemoteId(localCustomerId);

		title = cursor.getString(Invitations.TITLE_INDEX);
		description = cursor.getString(Invitations.DESCRIPTION_INDEX);
		startTime = cursor.isNull(Invitations.START_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Invitations.START_TIME_INDEX));
		endTime = cursor.isNull(Invitations.END_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Invitations.END_TIME_INDEX));
		read = cursor.isNull(Invitations.READ_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Invitations.READ_INDEX));
		accepted = cursor.isNull(Invitations.ACCEPTED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Invitations.ACCEPTED_INDEX));
		dirty = cursor.isNull(Invitations.DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Invitations.DIRTY_INDEX));
		localCreationTime = cursor
				.isNull(Invitations.LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Invitations.LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(Invitations.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Invitations.LOCAL_MODIFICATION_TIME_INDEX));
		latitude = cursor.isNull(Invitations.LATITUDE_INDEX) ? null : cursor
				.getDouble(Invitations.LATITUDE_INDEX);
		longitude = cursor.isNull(Invitations.LONGITUDE_INDEX) ? null : cursor
				.getDouble(Invitations.LONGITUDE_INDEX);
		street = cursor.getString(Invitations.STREET_INDEX);
		area = cursor.getString(Invitations.AREA_INDEX);
		city = cursor.getString(Invitations.CITY_INDEX);
		state = cursor.getString(Invitations.STATE_INDEX);
		country = cursor.getString(Invitations.COUNTRY_INDEX);
		pinCode = cursor.getString(Invitations.PIN_CODE_INDEX);
		landmark = cursor.getString(Invitations.LANDMARK_INDEX);
	}

	@Override
	public String toString() {
		return "Invitation [id=" + id + ", typeId=" + typeId + ", title="
				+ title + ", description=" + description + ", startTime="
				+ startTime + ", endTime=" + endTime + ", localCustomerId="
				+ localCustomerId + ", remoteCustomerId=" + remoteCustomerId
				+ ", read=" + read + ", accepted=" + accepted + ", dirty="
				+ dirty + ", localCreationTime=" + localCreationTime
				+ ", localModificationTime=" + localModificationTime
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", street=" + street + ", area=" + area + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", pinCode="
				+ pinCode + ", landmark=" + landmark + "]";
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

		// try {
		// if (remoteId == null) {
		// json.put(JSON_LOCAL_ID, localId);
		// } else {
		// json.put(JSON_REMOTE_ID, remoteId);
		// }
		//
		// // if remote customer id is present, don't send the local customer
		// // id to the server
		// if (remoteCustomerId != null) {
		// json.put(JSON_REMOTE_CUSTOMER_ID, remoteCustomerId);
		// } else {
		// if (localCustomerId != null) {
		// json.put(JSON_LOCAL_CUSTOMER_ID, localCustomerId);
		// }
		// }
		//
		// Utils.putValueOnlyIfNotNull(json, JSON_TYPE_ID, typeId);
		// Utils.putValueOnlyIfNotNull(json, JSON_TITLE, title);
		// Utils.putValueOnlyIfNotNull(json, JSON_DESCRIPTION, description);
		// // Utils.putValueOnlyIfNotNull(json, JSON_EMPLOYEE_ID, employeeId);
		// Utils.putValueOnlyIfNotNull(json, JSON_START_TIME, startTime);
		// Utils.putValueOnlyIfNotNull(json, JSON_END_TIME, endTime);
		// Utils.putValueOnlyIfNotNull(json, JSON_COMPLETED, completed);
		// Utils.putValueOnlyIfNotNull(json, JSON_COMPLETION_TIME,
		// completionTime);
		// Utils.putValueOnlyIfNotNull(json, JSON_APPROVED, approved);
		// // we shouldn't send remote modification time to server
		// } catch (JSONException e) {
		// Log.e(TAG, "Failed to compose JSON for job: " + e.toString());
		// return null;
		// }

		return json;
	}

}

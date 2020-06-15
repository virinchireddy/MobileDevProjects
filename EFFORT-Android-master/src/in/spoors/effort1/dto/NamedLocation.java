package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.NamedLocationsDao;
import in.spoors.effort1.provider.EffortProvider.NamedLocations;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class NamedLocation implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "Customer";

	private Long localId;
	private Long remoteId;
	private String name;
	private String description;
	private Double latitude;
	private Double longitude;
	private String street;
	private String area;
	private String city;
	private String state;
	private String country;
	private String pinCode;
	private String landmark;
	private Boolean dirty;
	private Boolean partial;
	private Date localCreationTime;
	private Date localModificationTime;

	public static final String JSON_PATH_ADDED = "namedLocations/added";
	public static final String JSON_PATH_MODIFIED = "namedLocations/modified";
	// public static final String JSON_PATH_INTERESTED =
	// "interestedNamedLocations";
	public static final String JSON_LOCAL_ID = "clientNamedLocationId";
	public static final String JSON_REMOTE_ID = "namedLocationId";
	public static final String JSON_NAME = "name";
	public static final String JSON_DESCRIPTION = "description";
	public static final String JSON_PHONE = "customerPhone";
	public static final String JSON_LATITUDE = "lat";
	public static final String JSON_LONGITUDE = "lng";
	public static final String JSON_STREET = "street";
	public static final String JSON_AREA = "area";
	public static final String JSON_CITY = "city";
	public static final String JSON_STATE = "state";
	public static final String JSON_COUNTRY = "country";
	public static final String JSON_PIN_CODE = "pincode";
	public static final String JSON_LANDMARK = "landMark";

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static NamedLocation parse(JSONObject json,
			Context applicationContext, boolean calledfromSearch)
			throws JSONException, ParseException {
		// Remote Job Id is mandatory. Thus, null check is not required.
		long remoteId = json.getLong(JSON_REMOTE_ID);
		NamedLocationsDao namedLocationsDao = NamedLocationsDao
				.getInstance(applicationContext);
		NamedLocation location = namedLocationsDao
				.getLocationWithRemoteId(remoteId);

		Long localId = Utils.getLong(json, JSON_LOCAL_ID);

		if (location == null && localId != null) {
			location = namedLocationsDao.getLocationWithLocalId(localId);
		}

		if (location == null) {
			location = new NamedLocation();
			location.partial = calledfromSearch;
		}

		location.remoteId = remoteId;
		location.dirty = false;

		location.name = Utils.getString(json, JSON_NAME);
		location.description = Utils.getString(json, JSON_DESCRIPTION);
		location.latitude = Utils.getDouble(json, JSON_LATITUDE);
		location.longitude = Utils.getDouble(json, JSON_LONGITUDE);
		location.street = Utils.getString(json, JSON_STREET);
		location.area = Utils.getString(json, JSON_AREA);
		location.city = Utils.getString(json, JSON_CITY);
		location.state = Utils.getString(json, JSON_STATE);
		location.country = Utils.getString(json, JSON_COUNTRY);
		location.pinCode = Utils.getString(json, JSON_PIN_CODE);
		location.landmark = Utils.getString(json, JSON_LANDMARK);

		return location;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public Boolean getDirty() {
		return dirty;
	}

	public void setDirty(Boolean dirty) {
		this.dirty = dirty;
	}

	public Boolean getPartial() {
		return partial;
	}

	public void setPartial(Boolean partial) {
		this.partial = partial;
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
			values.put(NamedLocations._ID, localId);
		}

		Utils.putNullOrValue(values, NamedLocations.REMOTE_ID, remoteId);
		Utils.putNullOrValue(values, NamedLocations.NAME, name);
		Utils.putNullOrValue(values, NamedLocations.DESCRIPTION, description);
		Utils.putNullOrValue(values, NamedLocations.LATITUDE, latitude);
		Utils.putNullOrValue(values, NamedLocations.LONGITUDE, longitude);
		Utils.putNullOrValue(values, NamedLocations.STREET, street);
		Utils.putNullOrValue(values, NamedLocations.AREA, area);
		Utils.putNullOrValue(values, NamedLocations.CITY, city);
		Utils.putNullOrValue(values, NamedLocations.STATE, state);
		Utils.putNullOrValue(values, NamedLocations.COUNTRY, country);
		Utils.putNullOrValue(values, NamedLocations.PIN_CODE, pinCode);
		Utils.putNullOrValue(values, NamedLocations.LANDMARK, landmark);
		Utils.putNullOrValue(values, NamedLocations.DIRTY, dirty);
		Utils.putNullOrValue(values, NamedLocations.PARTIAL, partial);
		Utils.putNullOrValue(values, NamedLocations.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, NamedLocations.LOCAL_MODIFICATION_TIME,
				localModificationTime);

		return values;
	}

	public void load(Cursor cursor) {
		localId = cursor.isNull(NamedLocations._ID_INDEX) ? null : cursor
				.getLong(NamedLocations._ID_INDEX);
		remoteId = cursor.isNull(NamedLocations.REMOTE_ID_INDEX) ? null
				: cursor.getLong(NamedLocations.REMOTE_ID_INDEX);
		name = cursor.getString(NamedLocations.NAME_INDEX);
		description = cursor.getString(NamedLocations.DESCRIPTION_INDEX);
		latitude = cursor.isNull(NamedLocations.LATITUDE_INDEX) ? null : cursor
				.getDouble((NamedLocations.LATITUDE_INDEX));
		longitude = cursor.isNull(NamedLocations.LONGITUDE_INDEX) ? null
				: cursor.getDouble((NamedLocations.LONGITUDE_INDEX));
		street = cursor.getString(NamedLocations.STREET_INDEX);
		area = cursor.getString(NamedLocations.AREA_INDEX);
		city = cursor.getString(NamedLocations.CITY_INDEX);
		state = cursor.getString(NamedLocations.STATE_INDEX);
		country = cursor.getString(NamedLocations.COUNTRY_INDEX);
		pinCode = cursor.getString(NamedLocations.PIN_CODE_INDEX);
		landmark = cursor.getString(NamedLocations.LANDMARK_INDEX);
		dirty = cursor.isNull(NamedLocations.DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(NamedLocations.DIRTY_INDEX));
		partial = cursor.isNull(NamedLocations.PARTIAL_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(NamedLocations.PARTIAL_INDEX));
		localCreationTime = cursor
				.isNull(NamedLocations.LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(NamedLocations.LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(NamedLocations.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils
						.getLocalTime(cursor
								.getString(NamedLocations.LOCAL_MODIFICATION_TIME_INDEX));
	}

	@Override
	public String toString() {
		return "NamedLocation [localId=" + localId + ", remoteId=" + remoteId
				+ ", name=" + name + ", description=" + description
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", street=" + street + ", area=" + area + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", pinCode="
				+ pinCode + ", landmark=" + landmark + ", dirty=" + dirty
				+ ", partial=" + partial + ", localCreationTime="
				+ localCreationTime + ", localModificationTime="
				+ localModificationTime + "]";
	}

	/**
	 * Get the JSON object that can be sent to the server.
	 * 
	 * @param applicationContext
	 * @return
	 */
	public JSONObject getJsonObject(Context applicationContext) {
		JSONObject location = new JSONObject();

		try {

			if (remoteId == null) {
				location.put(JSON_LOCAL_ID, localId);
			} else {
				location.put(JSON_REMOTE_ID, remoteId);
			}

			Utils.putValueOnlyIfNotNull(location, JSON_NAME, name);
			Utils.putValueOnlyIfNotNull(location, JSON_DESCRIPTION, description);
			Utils.putValueOnlyIfNotNull(location, JSON_LATITUDE, latitude);
			Utils.putValueOnlyIfNotNull(location, JSON_LONGITUDE, longitude);

			Utils.putValueOnlyIfNotNull(location, JSON_STREET, street);
			Utils.putValueOnlyIfNotNull(location, JSON_AREA, area);
			Utils.putValueOnlyIfNotNull(location, JSON_CITY, city);
			Utils.putValueOnlyIfNotNull(location, JSON_STATE, state);
			Utils.putValueOnlyIfNotNull(location, JSON_COUNTRY, country);
			Utils.putValueOnlyIfNotNull(location, JSON_PIN_CODE, pinCode);
			Utils.putValueOnlyIfNotNull(location, JSON_LANDMARK, landmark);
		} catch (JSONException e) {
			Log.e(TAG,
					"Failed to compose JSON for named location: "
							+ e.toString(), e);
			return null;
		}

		return location;
	}
}

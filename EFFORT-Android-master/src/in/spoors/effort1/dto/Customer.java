package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.provider.EffortProvider.Customers;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Customer implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "Customer";

	private Long localId;
	private Long remoteId;
	private String name;
	private Integer typeId;
	private String phone;
	private Double latitude;
	private Double longitude;
	private String street;
	private String area;
	private String city;
	private String state;
	private String country;
	private String pinCode;
	private String landmark;
	private String pcFirstName;
	private String pcLastName;
	private String pcTitle;
	private String pcPhone;
	private String pcEmail;
	private String scFirstName;
	private String scLastName;
	private String scTitle;
	private String scPhone;
	private String scEmail;
	private Boolean dirty;
	private Boolean partial;
	private Date localCreationTime;
	private Date localModificationTime;
	private Boolean inUse;
	private Float distance;
	private Boolean deleted;
	private String customerNum;

	public static final String JSON_PATH_ADDED = "addedCustomers";
	public static final String JSON_PATH_MODIFIED = "modifiedCustomers";
	// public static final String JSON_PATH_INTERESTED = "interestedCustomer";
	public static final String JSON_LOCAL_ID = "clientCustomerId";
	public static final String JSON_REMOTE_ID = "customerId";
	public static final String JSON_NAME = "customerName";
	public static final String JSON_TYPE_ID = "customerTypeId";
	public static final String JSON_PHONE = "customerPhone";
	public static final String JSON_LATITUDE = "customerLat";
	public static final String JSON_LONGITUDE = "customerLong";
	public static final String JSON_STREET = "customerAddressStreet";
	public static final String JSON_AREA = "customerAddressArea";
	public static final String JSON_CITY = "customerAddressCity";
	public static final String JSON_STATE = "customerAddressState";
	public static final String JSON_COUNTRY = "customerAddressCountry";
	public static final String JSON_PIN_CODE = "customerAddressPincode";
	public static final String JSON_LANDMARK = "customerAddressLandMark";
	public static final String JSON_PC_FIRST_NAME = "primaryContactFirstName";
	public static final String JSON_PC_LAST_NAME = "primaryContactLastName";
	public static final String JSON_PC_TITLE = "primaryContactTitle";
	public static final String JSON_PC_PHONE = "primaryContactPhone";
	public static final String JSON_PC_EMAIL = "primaryContactEmail";
	public static final String JSON_SC_FIRST_NAME = "secondaryContactFirstName";
	public static final String JSON_SC_LAST_NAME = "secondaryContactLastName";
	public static final String JSON_SC_TITLE = "secondaryContactTitle";
	public static final String JSON_SC_PHONE = "secondaryContactPhone";
	public static final String JSON_SC_EMAIL = "secondaryContactEmail";
	public static final String JSON_DELETED = "deleted";
	public static final String JSON_CUSTOMER_NUM = "customerNo";

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static Customer parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		// Remote Job Id is mandatory. Thus, null check is not required.
		long remoteId = json.getLong(JSON_REMOTE_ID);
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Customer remote id is " + remoteId);
		}
		CustomersDao customersDao = CustomersDao
				.getInstance(applicationContext);
		Customer customer = customersDao.getCustomerWithRemoteId(remoteId);
		Long localId = Utils.getLong(json, JSON_LOCAL_ID);
		if (customer == null && localId != null) {
			customer = customersDao.getCustomerWithLocalId(localId);
		}
		if (customer == null) {
			customer = new Customer();
		}

		customer.remoteId = remoteId;
		customer.dirty = false;
		customer.partial = false;
		customer.inUse = true;
		customer.deleted = false;

		customer.name = Utils.getString(json, JSON_NAME);
		customer.typeId = Utils.getInteger(json, JSON_TYPE_ID);
		customer.phone = Utils.getString(json, JSON_PHONE);
		customer.latitude = Utils.getDouble(json, JSON_LATITUDE);
		customer.longitude = Utils.getDouble(json, JSON_LONGITUDE);
		customer.street = Utils.getString(json, JSON_STREET);
		customer.area = Utils.getString(json, JSON_AREA);
		customer.city = Utils.getString(json, JSON_CITY);
		customer.state = Utils.getString(json, JSON_STATE);
		customer.country = Utils.getString(json, JSON_COUNTRY);
		customer.pinCode = Utils.getString(json, JSON_PIN_CODE);
		customer.landmark = Utils.getString(json, JSON_LANDMARK);
		customer.pcFirstName = Utils.getString(json, JSON_PC_FIRST_NAME);
		customer.pcLastName = Utils.getString(json, JSON_PC_LAST_NAME);
		customer.pcTitle = Utils.getString(json, JSON_PC_TITLE);
		customer.pcPhone = Utils.getString(json, JSON_PC_PHONE);
		customer.pcEmail = Utils.getString(json, JSON_PC_EMAIL);
		customer.scFirstName = Utils.getString(json, JSON_SC_FIRST_NAME);
		customer.scLastName = Utils.getString(json, JSON_SC_LAST_NAME);
		customer.scTitle = Utils.getString(json, JSON_SC_TITLE);
		customer.scPhone = Utils.getString(json, JSON_SC_PHONE);
		customer.scEmail = Utils.getString(json, JSON_SC_EMAIL);
		if (Utils.getBoolean(json, JSON_DELETED) != null) {
			customer.deleted = Utils.getBoolean(json, JSON_DELETED);
		}
		customer.customerNum = Utils.getString(json, JSON_CUSTOMER_NUM);
		return customer;
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

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getPcFirstName() {
		return pcFirstName;
	}

	public void setPcFirstName(String pcFirstName) {
		this.pcFirstName = pcFirstName;
	}

	public String getPcLastName() {
		return pcLastName;
	}

	public void setPcLastName(String pcLastName) {
		this.pcLastName = pcLastName;
	}

	public String getPcTitle() {
		return pcTitle;
	}

	public void setPcTitle(String pcTitle) {
		this.pcTitle = pcTitle;
	}

	public String getPcPhone() {
		return pcPhone;
	}

	public void setPcPhone(String pcPhone) {
		this.pcPhone = pcPhone;
	}

	public String getPcEmail() {
		return pcEmail;
	}

	public void setPcEmail(String pcEmail) {
		this.pcEmail = pcEmail;
	}

	public String getScFirstName() {
		return scFirstName;
	}

	public void setScFirstName(String scFirstName) {
		this.scFirstName = scFirstName;
	}

	public String getScLastName() {
		return scLastName;
	}

	public void setScLastName(String scLastName) {
		this.scLastName = scLastName;
	}

	public String getScTitle() {
		return scTitle;
	}

	public void setScTitle(String scTitle) {
		this.scTitle = scTitle;
	}

	public String getScPhone() {
		return scPhone;
	}

	public void setScPhone(String scPhone) {
		this.scPhone = scPhone;
	}

	public String getScEmail() {
		return scEmail;
	}

	public void setScEmail(String scEmail) {
		this.scEmail = scEmail;
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

	public Boolean getInUse() {
		return inUse;
	}

	public void setInUse(Boolean inUse) {
		this.inUse = inUse;
	}

	public Float getDistance() {
		return distance;
	}

	public void setDistance(Float distance) {
		this.distance = distance;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getCustomerNum() {
		return customerNum;
	}

	public void setCustomerNum(String customerNum) {
		this.customerNum = customerNum;
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
			values.put(Customers._ID, localId);
		}

		Utils.putNullOrValue(values, Customers.REMOTE_ID, remoteId);
		Utils.putNullOrValue(values, Customers.NAME, name);
		Utils.putNullOrValue(values, Customers.CUSTOMER_TYPE_ID, typeId);
		Utils.putNullOrValue(values, Customers.PHONE, phone);
		Utils.putNullOrValue(values, Customers.LATITUDE, latitude);
		Utils.putNullOrValue(values, Customers.LONGITUDE, longitude);
		Utils.putNullOrValue(values, Customers.STREET, street);
		Utils.putNullOrValue(values, Customers.AREA, area);
		Utils.putNullOrValue(values, Customers.CITY, city);
		Utils.putNullOrValue(values, Customers.STATE, state);
		Utils.putNullOrValue(values, Customers.COUNTRY, country);
		Utils.putNullOrValue(values, Customers.PIN_CODE, pinCode);
		Utils.putNullOrValue(values, Customers.LANDMARK, landmark);
		Utils.putNullOrValue(values, Customers.PC_FIRST_NAME, pcFirstName);
		Utils.putNullOrValue(values, Customers.PC_LAST_NAME, pcLastName);
		Utils.putNullOrValue(values, Customers.PC_TITLE, pcTitle);
		Utils.putNullOrValue(values, Customers.PC_PHONE, pcPhone);
		Utils.putNullOrValue(values, Customers.PC_EMAIL, pcEmail);
		Utils.putNullOrValue(values, Customers.SC_FIRST_NAME, scFirstName);
		Utils.putNullOrValue(values, Customers.SC_LAST_NAME, scLastName);
		Utils.putNullOrValue(values, Customers.SC_TITLE, scTitle);
		Utils.putNullOrValue(values, Customers.SC_PHONE, scPhone);
		Utils.putNullOrValue(values, Customers.SC_EMAIL, scEmail);
		Utils.putNullOrValue(values, Customers.DIRTY, dirty);
		Utils.putNullOrValue(values, Customers.PARTIAL, partial);
		Utils.putNullOrValue(values, Customers.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, Customers.LOCAL_MODIFICATION_TIME,
				localModificationTime);
		Utils.putNullOrValue(values, Customers.IN_USE, inUse);
		Utils.putNullOrValue(values, Customers.DISTANCE, distance);
		Utils.putNullOrValue(values, Customers.DELETED, deleted);
		Utils.putNullOrValue(values, Customers.CUSTOMER_NUM, customerNum);

		return values;
	}

	public void load(Cursor cursor) {
		localId = cursor.isNull(Customers.ALL_ID_INDEX) ? null : cursor
				.getLong(Customers.ALL_ID_INDEX);
		remoteId = cursor.isNull(Customers.ALL_REMOTE_ID_INDEX) ? null : cursor
				.getLong(Customers.ALL_REMOTE_ID_INDEX);
		name = cursor.getString(Customers.ALL_NAME_INDEX);
		typeId = cursor.isNull(Customers.ALL_CUSTOMER_TYPE_ID_INDEX) ? null
				: cursor.getInt(Customers.ALL_CUSTOMER_TYPE_ID_INDEX);
		phone = cursor.getString(Customers.ALL_PHONE_INDEX);
		latitude = cursor.isNull(Customers.ALL_LATITUDE_INDEX) ? null : cursor
				.getDouble((Customers.ALL_LATITUDE_INDEX));
		longitude = cursor.isNull(Customers.ALL_LONGITUDE_INDEX) ? null
				: cursor.getDouble((Customers.ALL_LONGITUDE_INDEX));
		street = cursor.getString(Customers.ALL_STREET_INDEX);
		area = cursor.getString(Customers.ALL_AREA_INDEX);
		city = cursor.getString(Customers.ALL_CITY_INDEX);
		state = cursor.getString(Customers.ALL_STATE_INDEX);
		country = cursor.getString(Customers.ALL_COUNTRY_INDEX);
		pinCode = cursor.getString(Customers.ALL_PIN_CODE_INDEX);
		landmark = cursor.getString(Customers.ALL_LANDMARK_INDEX);

		// primary contact fields
		pcFirstName = cursor.getString(Customers.ALL_PC_FIRST_NAME_INDEX);
		pcLastName = cursor.getString(Customers.ALL_PC_LAST_NAME_INDEX);
		pcTitle = cursor.getString(Customers.ALL_PC_TITLE_INDEX);
		pcPhone = cursor.getString(Customers.ALL_PC_PHONE_INDEX);
		pcEmail = cursor.getString(Customers.ALL_PC_EMAIL_INDEX);

		// secondary contact fields
		scFirstName = cursor.getString(Customers.ALL_SC_FIRST_NAME_INDEX);
		scLastName = cursor.getString(Customers.ALL_SC_LAST_NAME_INDEX);
		scTitle = cursor.getString(Customers.ALL_SC_TITLE_INDEX);
		scPhone = cursor.getString(Customers.ALL_SC_PHONE_INDEX);
		scEmail = cursor.getString(Customers.ALL_SC_EMAIL_INDEX);

		dirty = cursor.isNull(Customers.ALL_DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Customers.ALL_DIRTY_INDEX));
		partial = cursor.isNull(Customers.ALL_PARTIAL_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Customers.ALL_PARTIAL_INDEX));
		localCreationTime = cursor
				.isNull(Customers.ALL_LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Customers.ALL_LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(Customers.ALL_LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils
						.getLocalTime(cursor
								.getString(Customers.ALL_LOCAL_MODIFICATION_TIME_INDEX));
		inUse = cursor.isNull(Customers.ALL_IN_USE_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Customers.ALL_IN_USE_INDEX));
		distance = cursor.isNull(Customers.ALL_DISTANCE_INDEX) ? null : cursor
				.getFloat(Customers.ALL_DISTANCE_INDEX);
		deleted = cursor.isNull(Customers.ALL_DELETED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Customers.ALL_DELETED_INDEX));
		customerNum = cursor.getString(Customers.ALL_CUSTOMER_NUM_INDEX);
	}

	@Override
	public String toString() {
		return "Customer [localId=" + localId + ", remoteId=" + remoteId
				+ ", name=" + name + ", typeId=" + typeId + ", phone=" + phone
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", street=" + street + ", area=" + area + ", city=" + city
				+ ", state=" + state + ", country=" + country + ", pinCode="
				+ pinCode + ", landmark=" + landmark + ", pcFirstName="
				+ pcFirstName + ", pcLastName=" + pcLastName + ", pcTitle="
				+ pcTitle + ", pcPhone=" + pcPhone + ", pcEmail=" + pcEmail
				+ ", scFirstName=" + scFirstName + ", scLastName=" + scLastName
				+ ", scTitle=" + scTitle + ", scPhone=" + scPhone
				+ ", scEmail=" + scEmail + ", dirty=" + dirty + ", partial="
				+ partial + ", localCreationTime=" + localCreationTime
				+ ", localModificationTime=" + localModificationTime
				+ ", inUse=" + inUse + ", distance=" + distance + ", deleted="
				+ deleted + ", customerNum=" + customerNum + "]";
	}

	/**
	 * Get the JSON object that can be sent to the server.
	 * 
	 * @param applicationContext
	 * @return
	 */
	public JSONObject getJsonObject(Context applicationContext) {
		JSONObject customer = new JSONObject();

		try {

			if (remoteId == null) {
				customer.put(JSON_LOCAL_ID, localId);
			} else {
				customer.put(JSON_REMOTE_ID, remoteId);
			}

			Utils.putValueOnlyIfNotNull(customer, JSON_NAME, name);
			Utils.putValueOnlyIfNotNull(customer, JSON_TYPE_ID, typeId);
			Utils.putValueOnlyIfNotNull(customer, JSON_PHONE, phone);
			Utils.putValueOnlyIfNotNull(customer, JSON_LATITUDE, latitude);
			Utils.putValueOnlyIfNotNull(customer, JSON_LONGITUDE, longitude);

			Utils.putValueOnlyIfNotNull(customer, JSON_STREET, street);
			Utils.putValueOnlyIfNotNull(customer, JSON_AREA, area);
			Utils.putValueOnlyIfNotNull(customer, JSON_CITY, city);
			Utils.putValueOnlyIfNotNull(customer, JSON_STATE, state);
			Utils.putValueOnlyIfNotNull(customer, JSON_COUNTRY, country);
			Utils.putValueOnlyIfNotNull(customer, JSON_PIN_CODE, pinCode);
			Utils.putValueOnlyIfNotNull(customer, JSON_LANDMARK, landmark);

			// primary contact fields
			Utils.putValueOnlyIfNotNull(customer, JSON_PC_FIRST_NAME,
					pcFirstName);
			Utils.putValueOnlyIfNotNull(customer, JSON_PC_LAST_NAME, pcLastName);
			Utils.putValueOnlyIfNotNull(customer, JSON_PC_TITLE, pcTitle);
			Utils.putValueOnlyIfNotNull(customer, JSON_PC_PHONE, pcPhone);
			Utils.putValueOnlyIfNotNull(customer, JSON_PC_EMAIL, pcEmail);

			// secondary contact fields
			Utils.putValueOnlyIfNotNull(customer, JSON_SC_FIRST_NAME,
					scFirstName);
			Utils.putValueOnlyIfNotNull(customer, JSON_SC_LAST_NAME, scLastName);
			Utils.putValueOnlyIfNotNull(customer, JSON_SC_TITLE, scTitle);
			Utils.putValueOnlyIfNotNull(customer, JSON_SC_PHONE, scPhone);
			Utils.putValueOnlyIfNotNull(customer, JSON_SC_EMAIL, scEmail);
			Utils.putValueOnlyIfNotNull(customer, JSON_DELETED, deleted);
			Utils.putValueOnlyIfNotNull(customer, JSON_CUSTOMER_NUM,
					customerNum);
		} catch (JSONException e) {
			Log.e(TAG, "Failed to compose JSON for customer: " + e.toString(),
					e);
			return null;
		}

		return customer;
	}
}

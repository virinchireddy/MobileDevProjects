package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.Employees;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Employee implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "Employee";

	public static final String JSON_EMPLOYEE_ID = "empId";
	public static final String JSON_EMPLOYEE_FIRST_NAME = "empFirstName";
	public static final String JSON_EMPLOYEE_LAST_NAME = "empLastName";
	public static final String JSON_EMPLOYEE_EMAIL = "empEmail";
	public static final String JSON_MANAGER_ID = "managerId";
	public static final String JSON_EMPLOYEE_PHONE = "empPhone";
	public static final String JSON_IMEI = "imei";
	public static final String JSON_COMPANY_ID = "companyId";
	public static final String JSON_MANAGER = "manager";
	public static final String JSON_EMPLOYEE_TYPE_ID = "empTypeId";
	public static final String JSON_CALENDAR_ID = "calendarId";
	public static final String JSON_EMPLOYEE_NO = "empNo";
	public static final String JSON_HOME_LAT = "homeLat";
	public static final String JSON_HOME_LONG = "homeLong";
	public static final String JSON_CLIENT_EMPLOYEE_ID = "clientEmpId";
	public static final String JSON_EMPLOYEE_ADDRESS_STREET = "empAddressStreet";
	public static final String JSON_EMPLOYEE_ADDRESS_AREA = "empAddressArea";
	public static final String JSON_EMPLOYEE_ADDRESS_CITY = "empAddressCity";
	public static final String JSON_EMPLOYEE_ADDRESS_PINCODE = "empAddressPincode";
	public static final String JSON_EMPLOYEE_ADDRESS_LANDMARK = "empAddressLandMark";
	public static final String JSON_EMPLOYEE_ADDRESS_STATE = "empAddressState";
	public static final String JSON_EMPLOYEE_ADDRESS_COUNTRY = "empAddressCountry";
	public static final String JSON_WORK_LAT = "workLat";
	public static final String JSON_WORK_LONG = "workLong";
	public static final String JSON_PROVISIONING = "provisioning";
	public static final String JSON_RANK = "rank";

	private Long localId;
	private Long empId;
	private String empFirstName;
	private String empLastName;
	private String empEmail;
	private Long managerId;
	private String empPhone;
	private String imei;
	private Long companyId;
	private String manager;
	private Integer empTypeId;
	private Integer calendarId;
	private String empNo;
	private Double homeLat;
	private Double homeLong;
	private Long clientEmpId;
	private String empAddressStreet;
	private String empAddressArea;
	private String empAddressCity;
	private String empAddressPincode;
	private String empAddressLandMark;
	private String empAddressState;
	private String empAddressCountry;
	private Double workLat;
	private Double workLong;
	private String provisioning;
	private Long rank;

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
	public static Employee parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {

		if (json == null) {
			return null;
		}

		Employee employee = new Employee();
		employee.empId = Utils.getLong(json, JSON_EMPLOYEE_ID);
		employee.clientEmpId = Utils.getLong(json, JSON_CLIENT_EMPLOYEE_ID);
		employee.companyId = Utils.getLong(json, JSON_COMPANY_ID);
		employee.empAddressArea = Utils.getString(json,
				JSON_EMPLOYEE_ADDRESS_AREA);
		employee.empAddressCity = Utils.getString(json,
				JSON_EMPLOYEE_ADDRESS_CITY);
		employee.empAddressCountry = Utils.getString(json,
				JSON_EMPLOYEE_ADDRESS_COUNTRY);
		employee.empAddressLandMark = Utils.getString(json,
				JSON_EMPLOYEE_ADDRESS_LANDMARK);
		employee.empAddressPincode = Utils.getString(json,
				JSON_EMPLOYEE_ADDRESS_PINCODE);
		employee.empAddressState = Utils.getString(json,
				JSON_EMPLOYEE_ADDRESS_STATE);
		employee.empAddressStreet = Utils.getString(json,
				JSON_EMPLOYEE_ADDRESS_STREET);
		employee.empEmail = Utils.getString(json, JSON_EMPLOYEE_EMAIL);
		employee.empFirstName = Utils.getString(json, JSON_EMPLOYEE_FIRST_NAME);
		employee.empLastName = Utils.getString(json, JSON_EMPLOYEE_LAST_NAME);
		employee.empNo = Utils.getString(json, JSON_EMPLOYEE_NO);
		employee.empPhone = Utils.getString(json, JSON_EMPLOYEE_PHONE);
		employee.empTypeId = Utils.getInteger(json, JSON_EMPLOYEE_TYPE_ID);
		employee.homeLat = Utils.getDouble(json, JSON_HOME_LAT);
		employee.homeLong = Utils.getDouble(json, JSON_HOME_LONG);
		employee.imei = Utils.getString(json, JSON_IMEI);
		employee.manager = Utils.getString(json, JSON_MANAGER);
		employee.managerId = Utils.getLong(json, JSON_MANAGER_ID);
		employee.provisioning = Utils.getString(json, JSON_PROVISIONING);
		employee.workLat = Utils.getDouble(json, JSON_WORK_LAT);
		employee.workLong = Utils.getDouble(json, JSON_WORK_LONG);
		employee.calendarId = Utils.getInteger(json, JSON_CALENDAR_ID);
		employee.rank = Utils.getLong(json, JSON_RANK);
		return employee;
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
			values.put(Employees._ID, localId);
		}
		Utils.putNullOrValue(values, Employees.EMPLOYEE_ID, empId);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_FIRST_NAME,
				empFirstName);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_LAST_NAME, empLastName);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_EMAIL, empEmail);
		Utils.putNullOrValue(values, Employees.MANAGER_ID, managerId);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_PHONE, empPhone);
		Utils.putNullOrValue(values, Employees.IMEI, imei);
		Utils.putNullOrValue(values, Employees.COMPANY_ID, companyId);
		Utils.putNullOrValue(values, Employees.MANAGER, manager);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_TYPE_ID, empTypeId);
		Utils.putNullOrValue(values, Employees.CALENDAR_ID, calendarId);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_NO, empNo);
		Utils.putNullOrValue(values, Employees.HOME_LAT, homeLat);
		Utils.putNullOrValue(values, Employees.HOME_LONG, homeLong);
		Utils.putNullOrValue(values, Employees.CLIENT_EMPLOYEE_ID, clientEmpId);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_ADDRESS_AREA,
				empAddressArea);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_ADDRESS_CITY,
				empAddressCity);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_ADDRESS_COUNTRY,
				empAddressCountry);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_ADDRESS_LANDMARK,
				empAddressLandMark);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_ADDRESS_PINCODE,
				empAddressPincode);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_ADDRESS_STATE,
				empAddressState);
		Utils.putNullOrValue(values, Employees.EMPLOYEE_ADDRESS_STREET,
				empAddressStreet);
		Utils.putNullOrValue(values, Employees.WORK_LAT, workLat);
		Utils.putNullOrValue(values, Employees.WORK_LONG, workLong);
		Utils.putNullOrValue(values, Employees.PROVISIONING, provisioning);
		Utils.putNullOrValue(values, Employees.RANK, rank);

		return values;
	}

	public void load(Cursor cursor) {
		localId = cursor.isNull(Employees._ID_INDEX) ? null : cursor
				.getLong(Employees._ID_INDEX);
		calendarId = cursor.isNull(Employees.CALENDAR_ID_INDEX) ? null : cursor
				.getInt(Employees.CALENDAR_ID_INDEX);
		clientEmpId = cursor.isNull(Employees.CLIENT_EMPLOYEE_ID_INDEX) ? null
				: cursor.getLong(Employees.CLIENT_EMPLOYEE_ID_INDEX);
		companyId = cursor.isNull(Employees.COMPANY_ID_INDEX) ? null : cursor
				.getLong(Employees.COMPANY_ID_INDEX);
		empAddressArea = cursor.isNull(Employees.EMPLOYEE_ADDRESS_AREA_INDEX) ? null
				: cursor.getString(Employees.EMPLOYEE_ADDRESS_AREA_INDEX);
		empAddressCity = cursor.isNull(Employees.EMPLOYEE_ADDRESS_CITY_INDEX) ? null
				: cursor.getString(Employees.EMPLOYEE_ADDRESS_CITY_INDEX);
		empAddressCountry = cursor
				.isNull(Employees.EMPLOYEE_ADDRESS_COUNTRY_INDEX) ? null
				: cursor.getString(Employees.EMPLOYEE_ADDRESS_COUNTRY_INDEX);
		empAddressLandMark = cursor
				.isNull(Employees.EMPLOYEE_ADDRESS_LANDMARK_INDEX) ? null
				: cursor.getString(Employees.EMPLOYEE_ADDRESS_LANDMARK_INDEX);
		empAddressPincode = cursor
				.isNull(Employees.EMPLOYEE_ADDRESS_PINCODE_INDEX) ? null
				: cursor.getString(Employees.EMPLOYEE_ADDRESS_PINCODE_INDEX);
		empAddressState = cursor.isNull(Employees.EMPLOYEE_ADDRESS_STATE_INDEX) ? null
				: cursor.getString(Employees.EMPLOYEE_ADDRESS_STATE_INDEX);
		empAddressStreet = cursor
				.isNull(Employees.EMPLOYEE_ADDRESS_STREET_INDEX) ? null
				: cursor.getString(Employees.EMPLOYEE_ADDRESS_STREET_INDEX);
		empEmail = cursor.isNull(Employees.EMPLOYEE_EMAIL_INDEX) ? null
				: cursor.getString(Employees.EMPLOYEE_EMAIL_INDEX);
		empFirstName = cursor.isNull(Employees.EMPLOYEE_FIRST_NAME_INDEX) ? null
				: cursor.getString(Employees.EMPLOYEE_FIRST_NAME_INDEX);
		empId = cursor.isNull(Employees.EMPLOYEE_ID_INDEX) ? null : cursor
				.getLong(Employees.EMPLOYEE_ID_INDEX);
		empLastName = cursor.isNull(Employees.EMPLOYEE_LAST_NAME_INDEX) ? null
				: cursor.getString(Employees.EMPLOYEE_LAST_NAME_INDEX);
		empNo = cursor.isNull(Employees.EMPLOYEE_NO_INDEX) ? null : cursor
				.getString(Employees.EMPLOYEE_NO_INDEX);
		empPhone = cursor.isNull(Employees.EMPLOYEE_PHONE_INDEX) ? null
				: cursor.getString(Employees.EMPLOYEE_PHONE_INDEX);
		empTypeId = cursor.isNull(Employees.EMPLOYEE_TYPE_ID_INDEX) ? null
				: cursor.getInt(Employees.EMPLOYEE_TYPE_ID_INDEX);
		homeLat = cursor.isNull(Employees.HOME_LAT_INDEX) ? null : cursor
				.getDouble(Employees.HOME_LAT_INDEX);
		homeLong = cursor.isNull(Employees.HOME_LONG_INDEX) ? null : cursor
				.getDouble(Employees.HOME_LONG_INDEX);
		imei = cursor.isNull(Employees.IMEI_INDEX) ? null : cursor
				.getString(Employees.IMEI_INDEX);
		manager = cursor.isNull(Employees.MANAGER_INDEX) ? null : cursor
				.getString(Employees.MANAGER_INDEX);
		managerId = cursor.isNull(Employees.MANAGER_ID_INDEX) ? null : cursor
				.getLong(Employees.MANAGER_ID_INDEX);
		provisioning = cursor.isNull(Employees.PROVISIONING_INDEX) ? null
				: cursor.getString(Employees.PROVISIONING_INDEX);
		workLat = cursor.isNull(Employees.WORK_LAT_INDEX) ? null : cursor
				.getDouble(Employees.WORK_LAT_INDEX);
		workLong = cursor.isNull(Employees.WORK_LONG_INDEX) ? null : cursor
				.getDouble(Employees.WORK_LONG_INDEX);
		rank = cursor.isNull(Employees.RANK_INDEX) ? null : cursor
				.getLong(Employees.RANK_INDEX);
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
	}

	public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
	}

	public String getEmpFirstName() {
		return empFirstName;
	}

	public void setEmpFirstName(String empFirstName) {
		this.empFirstName = empFirstName;
	}

	public String getEmpLastName() {
		return empLastName;
	}

	public void setEmpLastName(String empLastName) {
		this.empLastName = empLastName;
	}

	public String getEmpEmail() {
		return empEmail;
	}

	public void setEmpEmail(String empEmail) {
		this.empEmail = empEmail;
	}

	public Long getManagerId() {
		return managerId;
	}

	public void setManagerId(Long managerId) {
		this.managerId = managerId;
	}

	public String getEmpPhone() {
		return empPhone;
	}

	public void setEmpPhone(String empPhone) {
		this.empPhone = empPhone;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public Integer getEmpTypeId() {
		return empTypeId;
	}

	public void setEmpTypeId(Integer empTypeId) {
		this.empTypeId = empTypeId;
	}

	public Integer getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(Integer calendarId) {
		this.calendarId = calendarId;
	}

	public String getEmpNo() {
		return empNo;
	}

	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}

	public Double getHomeLat() {
		return homeLat;
	}

	public void setHomeLat(Double homeLat) {
		this.homeLat = homeLat;
	}

	public Double getHomeLong() {
		return homeLong;
	}

	public void setHomeLong(Double homeLong) {
		this.homeLong = homeLong;
	}

	public Long getClientEmpId() {
		return clientEmpId;
	}

	public void setClientEmpId(Long clientEmpId) {
		this.clientEmpId = clientEmpId;
	}

	public String getEmpAddressStreet() {
		return empAddressStreet;
	}

	public void setEmpAddressStreet(String empAddressStreet) {
		this.empAddressStreet = empAddressStreet;
	}

	public String getEmpAddressArea() {
		return empAddressArea;
	}

	public void setEmpAddressArea(String empAddressArea) {
		this.empAddressArea = empAddressArea;
	}

	public String getEmpAddressCity() {
		return empAddressCity;
	}

	public void setEmpAddressCity(String empAddressCity) {
		this.empAddressCity = empAddressCity;
	}

	public String getEmpAddressPincode() {
		return empAddressPincode;
	}

	public void setEmpAddressPincode(String empAddressPincode) {
		this.empAddressPincode = empAddressPincode;
	}

	public String getEmpAddressLandMark() {
		return empAddressLandMark;
	}

	public void setEmpAddressLandMark(String empAddressLandMark) {
		this.empAddressLandMark = empAddressLandMark;
	}

	public String getEmpAddressState() {
		return empAddressState;
	}

	public void setEmpAddressState(String empAddressState) {
		this.empAddressState = empAddressState;
	}

	public String getEmpAddressCountry() {
		return empAddressCountry;
	}

	public void setEmpAddressCountry(String empAddressCountry) {
		this.empAddressCountry = empAddressCountry;
	}

	public Double getWorkLat() {
		return workLat;
	}

	public void setWorkLat(Double workLat) {
		this.workLat = workLat;
	}

	public Double getWorkLong() {
		return workLong;
	}

	public void setWorkLong(Double workLong) {
		this.workLong = workLong;
	}

	public String getProvisioning() {
		return provisioning;
	}

	public void setProvisioning(String provisioning) {
		this.provisioning = provisioning;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public static String getTag() {
		return TAG;
	}

	public static String getJsonEmployeeId() {
		return JSON_EMPLOYEE_ID;
	}

	public static String getJsonEmployeeFirstName() {
		return JSON_EMPLOYEE_FIRST_NAME;
	}

	public static String getJsonEmployeeLastName() {
		return JSON_EMPLOYEE_LAST_NAME;
	}

	public static String getJsonEmployeeEmail() {
		return JSON_EMPLOYEE_EMAIL;
	}

	public static String getJsonManagerId() {
		return JSON_MANAGER_ID;
	}

	public static String getJsonEmployeePhone() {
		return JSON_EMPLOYEE_PHONE;
	}

	public static String getJsonCompanyId() {
		return JSON_COMPANY_ID;
	}

	public static String getJsonManager() {
		return JSON_MANAGER;
	}

	public static String getJsonEmployeeTypeId() {
		return JSON_EMPLOYEE_TYPE_ID;
	}

	public static String getJsonCalendarId() {
		return JSON_CALENDAR_ID;
	}

	public static String getJsonEmployeeNo() {
		return JSON_EMPLOYEE_NO;
	}

	public static String getJsonHomeLat() {
		return JSON_HOME_LAT;
	}

	public static String getJsonHomeLong() {
		return JSON_HOME_LONG;
	}

	public static String getJsonClientEmployeeId() {
		return JSON_CLIENT_EMPLOYEE_ID;
	}

	public static String getJsonEmployeeAddressStreet() {
		return JSON_EMPLOYEE_ADDRESS_STREET;
	}

	public static String getJsonEmployeeAddressArea() {
		return JSON_EMPLOYEE_ADDRESS_AREA;
	}

	public static String getJsonEmployeeAddressCity() {
		return JSON_EMPLOYEE_ADDRESS_CITY;
	}

	public static String getJsonEmployeeAddressPincode() {
		return JSON_EMPLOYEE_ADDRESS_PINCODE;
	}

	public static String getJsonEmployeeAddressLandmark() {
		return JSON_EMPLOYEE_ADDRESS_LANDMARK;
	}

	public static String getJsonEmployeeAddressState() {
		return JSON_EMPLOYEE_ADDRESS_STATE;
	}

	public static String getJsonEmployeeAddressCountry() {
		return JSON_EMPLOYEE_ADDRESS_COUNTRY;
	}

	public static String getJsonWorkLat() {
		return JSON_WORK_LAT;
	}

	public static String getJsonWorkLong() {
		return JSON_WORK_LONG;
	}

	public static String getJsonProvisioning() {
		return JSON_PROVISIONING;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	@Override
	public String toString() {
		return "Employee [localId=" + localId + ", empId=" + empId
				+ ", empFirstName=" + empFirstName + ", empLastName="
				+ empLastName + ", empEmail=" + empEmail + ", managerId="
				+ managerId + ", empPhone=" + empPhone + ", imei=" + imei
				+ ", companyId=" + companyId + ", manager=" + manager
				+ ", empTypeId=" + empTypeId + ", calendarId=" + calendarId
				+ ", empNo=" + empNo + ", homeLat=" + homeLat + ", homeLong="
				+ homeLong + ", clientEmpId=" + clientEmpId
				+ ", empAddressStreet=" + empAddressStreet
				+ ", empAddressArea=" + empAddressArea + ", empAddressCity="
				+ empAddressCity + ", empAddressPincode=" + empAddressPincode
				+ ", empAddressLandMark=" + empAddressLandMark
				+ ", empAddressState=" + empAddressState
				+ ", empAddressCountry=" + empAddressCountry + ", workLat="
				+ workLat + ", workLong=" + workLong + ", provisioning="
				+ provisioning + ", rank=" + rank + "]";
	}

}

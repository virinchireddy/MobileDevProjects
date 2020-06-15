package in.spoors.effort1.dto;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dao.NeighboringCellInfosDao;
import in.spoors.effort1.provider.EffortProvider.Locations;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * This has been named LocationDto, instead of just Location to prevent
 * confusion with android.location.Location.
 * 
 * @author tiru
 * 
 */
public class LocationDto implements Serializable {

	public static final int CONNECTED_USING_WIFI = 2;
	public static final int CONNECTED = 1;
	public static final int NOT_CONNECTED = 0;
	private static final String JSON_NEAR_BY_CELL_INFO = "nearByCellInfo";
	private static final long serialVersionUID = 1L;
	private static final String TAG = "Leave";
	public static final String JSON_SIGNAL_STRENGTH = "carrierSignalStrength";
	public static final String JSON_BATTERY_LEVEL = "batteryLevel";
	public static final String JSON_GPS_ON = "gpsProvider";
	public static final String JSON_GPS_LATITUDE = "gpsLat";
	public static final String JSON_GPS_LONGITUDE = "gpsLng";
	public static final String JSON_GPS_ALTITUDE = "gpsAlt";
	public static final String JSON_GPS_ACCURACY = "gpsAccuracy";
	public static final String JSON_GPS_SPEED = "speed";
	public static final String JSON_GPS_BEARING = "bearingAngle";
	public static final String JSON_NETWORK_ON = "cellProvider";
	public static final String JSON_NETWORK_LATITUDE = "cellLat";
	public static final String JSON_NETWORK_LONGITUDE = "cellLng";
	public static final String JSON_NETWORK_ACCURACY = "cellAccuracy";
	public static final String JSON_CELL_ID = "cellId";
	public static final String JSON_CELL_LAC = "lac";
	public static final String JSON_CELL_MCC = "mcc";
	public static final String JSON_CELL_MNC = "mnc";
	public static final String JSON_LOCAL_CREATION_TIME = "clientTime";
	public static final String JSON_GPS_FIX_TIME = "gpsFixTime";
	public static final String JSON_NETWORK_FIX_TIME = "cellFixTime";
	public static final String JSON_CONNECTIVITY_STATUS = "connectivityStatus";
	public static final String JSON_FRESH_FIX = "freshFix";
	public static final String JSON_REASON = "reason";
	public static final String JSON_FUSED_ON = "otherProvider";
	public static final String JSON_FUSED_LATITUDE = "unknownLat";
	public static final String JSON_FUSED_LONGITUDE = "unknownLng";
	// public static final String JSON_FUSED_ALTITUDE = "gpsAlt";
	public static final String JSON_FUSED_ACCURACY = "unknownAccuracy";
	public static final String JSON_FUSED_FIX_TIME = "unknownFixTime";
	// public static final String JSON_FUSED_SPEED = "speed";
	// public static final String JSON_FUSED_BEARING = "bearingAngle";
	public static final String JSON_PURPOSE = "purpose";

	private Long localId;
	private Integer purpose;
	private Boolean locationFinalized;
	private Boolean gpsOn;
	private Boolean gpsFinalized;
	private Boolean gpsCached;
	private Date gpsFixTime;
	private Float gpsAccuracy;
	private Double gpsLatitude;
	private Double gpsLongitude;
	private Double gpsAltitude;
	private Float gpsSpeed;
	private Float gpsBearing;

	private Boolean networkOn;
	private Boolean networkFinalized;
	private Boolean networkCached;
	private Date networkFixTime;
	private Float networkAccuracy;
	private Double networkLatitude;
	private Double networkLongitude;

	private Integer cellId;
	private Integer cellLac;
	private Integer cellPsc;
	private Integer cellMcc;
	private Integer cellMnc;
	private Integer signalStrength;
	private Integer batteryLevel;

	private Boolean connected;
	private Boolean wifiConnected;
	private Long forId;
	private Boolean dirty;
	private Boolean smsProcessState = false;
	private Boolean isFallBack = false;

	private Date localCreationTime;
	private Date localModificationTime;
	private Boolean freshFix = false;
	private Long remoteLocationId;
	private Integer reasonForTracking;

	private Boolean fusedOn;
	private Boolean fusedFinalized;
	private Boolean fusedCached;
	private Date fusedFixTime;
	private Float fusedAccuracy;
	private Double fusedLatitude;
	private Double fusedLongitude;
	private Double fusedAltitude;
	private Float fusedSpeed;
	private Float fusedBearing;

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
			values.put(Locations._ID, localId);
		}

		Utils.putNullOrValue(values, Locations.PURPOSE, purpose);
		Utils.putNullOrValue(values, Locations.LOCATION_FINALIZED,
				locationFinalized);
		Utils.putNullOrValue(values, Locations.GPS_ON, gpsOn);
		Utils.putNullOrValue(values, Locations.GPS_FINALIZED, gpsFinalized);
		Utils.putNullOrValue(values, Locations.GPS_CACHED, gpsCached);
		Utils.putNullOrValue(values, Locations.GPS_FIX_TIME, gpsFixTime);
		Utils.putNullOrValue(values, Locations.GPS_ACCURACY, gpsAccuracy);
		Utils.putNullOrValue(values, Locations.GPS_LATITUDE, gpsLatitude);
		Utils.putNullOrValue(values, Locations.GPS_LONGITUDE, gpsLongitude);
		Utils.putNullOrValue(values, Locations.GPS_ALTITUDE, gpsAltitude);
		Utils.putNullOrValue(values, Locations.GPS_SPEED, gpsSpeed);
		Utils.putNullOrValue(values, Locations.GPS_BEARING, gpsBearing);
		Utils.putNullOrValue(values, Locations.NETWORK_ON, networkOn);
		Utils.putNullOrValue(values, Locations.NETWORK_FINALIZED,
				networkFinalized);
		Utils.putNullOrValue(values, Locations.NETWORK_CACHED, networkCached);
		Utils.putNullOrValue(values, Locations.NETWORK_FIX_TIME, networkFixTime);
		Utils.putNullOrValue(values, Locations.NETWORK_ACCURACY,
				networkAccuracy);
		Utils.putNullOrValue(values, Locations.NETWORK_LATITUDE,
				networkLatitude);
		Utils.putNullOrValue(values, Locations.NETWORK_LONGITUDE,
				networkLongitude);
		Utils.putNullOrValue(values, Locations.CELL_ID, cellId);
		Utils.putNullOrValue(values, Locations.CELL_LAC, cellLac);
		Utils.putNullOrValue(values, Locations.CELL_PSC, cellPsc);
		Utils.putNullOrValue(values, Locations.CELL_MCC, cellMcc);
		Utils.putNullOrValue(values, Locations.CELL_MNC, cellMnc);
		Utils.putNullOrValue(values, Locations.SIGNAL_STRENGTH, signalStrength);
		Utils.putNullOrValue(values, Locations.BATTERY_LEVEL, batteryLevel);
		Utils.putNullOrValue(values, Locations.CONNECTED, connected);
		Utils.putNullOrValue(values, Locations.WIFI_CONNECTED, wifiConnected);
		Utils.putNullOrValue(values, Locations.FOR_ID, forId);
		Utils.putNullOrValue(values, Locations.DIRTY, dirty);
		Utils.putNullOrValue(values, Locations.SMS_PROCESS_STATE,
				smsProcessState);
		Utils.putNullOrValue(values, Locations.LOCAL_CREATION_TIME,
				localCreationTime);
		Utils.putNullOrValue(values, Locations.LOCAL_MODIFICATION_TIME,
				localModificationTime);
		Utils.putNullOrValue(values, Locations.FRESH_FIX, freshFix);
		Utils.putNullOrValue(values, Locations.REMOTE_LOCATION_ID,
				remoteLocationId);
		Utils.putNullOrValue(values, Locations.REASON_FOR_TRACKING,
				reasonForTracking);
		Utils.putNullOrValue(values, Locations.FUSED_ON, fusedOn);
		Utils.putNullOrValue(values, Locations.FUSED_FINALIZED, fusedFinalized);
		Utils.putNullOrValue(values, Locations.FUSED_CACHED, fusedCached);
		Utils.putNullOrValue(values, Locations.FUSED_FIX_TIME, fusedFixTime);
		Utils.putNullOrValue(values, Locations.FUSED_ACCURACY, fusedAccuracy);
		Utils.putNullOrValue(values, Locations.FUSED_LATITUDE, fusedLatitude);
		Utils.putNullOrValue(values, Locations.FUSED_LONGITUDE, fusedLongitude);
		Utils.putNullOrValue(values, Locations.FUSED_ALTITUDE, fusedAltitude);
		Utils.putNullOrValue(values, Locations.FUSED_SPEED, fusedSpeed);
		Utils.putNullOrValue(values, Locations.FUSED_BEARING, fusedBearing);

		return values;
	}

	public void load(Cursor cursor) {
		localId = cursor.isNull(Locations._ID_INDEX) ? null : cursor
				.getLong(Locations._ID_INDEX);
		purpose = cursor.isNull(Locations.PURPOSE_INDEX) ? null : cursor
				.getInt(Locations.PURPOSE_INDEX);
		locationFinalized = cursor.isNull(Locations.LOCATION_FINALIZED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Locations.LOCATION_FINALIZED_INDEX));
		gpsOn = cursor.isNull(Locations.GPS_ON_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Locations.GPS_ON_INDEX));
		gpsFinalized = cursor.isNull(Locations.GPS_FINALIZED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Locations.GPS_FINALIZED_INDEX));
		gpsCached = cursor.isNull(Locations.GPS_CACHED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Locations.GPS_CACHED_INDEX));
		gpsFixTime = cursor.isNull(Locations.GPS_FIX_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Locations.GPS_FIX_TIME_INDEX));
		gpsAccuracy = cursor.isNull(Locations.GPS_ACCURACY_INDEX) ? null
				: cursor.getFloat(Locations.GPS_ACCURACY_INDEX);
		gpsLatitude = cursor.isNull(Locations.GPS_LATITUDE_INDEX) ? null
				: cursor.getDouble(Locations.GPS_LATITUDE_INDEX);
		gpsLongitude = cursor.isNull(Locations.GPS_LONGITUDE_INDEX) ? null
				: cursor.getDouble(Locations.GPS_LONGITUDE_INDEX);
		gpsAltitude = cursor.isNull(Locations.GPS_ALTITUDE_INDEX) ? null
				: cursor.getDouble(Locations.GPS_ALTITUDE_INDEX);
		gpsSpeed = cursor.isNull(Locations.GPS_SPEED_INDEX) ? null : cursor
				.getFloat(Locations.GPS_SPEED_INDEX);
		gpsBearing = cursor.isNull(Locations.GPS_BEARING_INDEX) ? null : cursor
				.getFloat(Locations.GPS_BEARING_INDEX);

		networkOn = cursor.isNull(Locations.NETWORK_ON_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Locations.NETWORK_ON_INDEX));
		networkFinalized = cursor.isNull(Locations.NETWORK_FINALIZED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Locations.NETWORK_FINALIZED_INDEX));
		networkCached = cursor.isNull(Locations.NETWORK_CACHED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Locations.NETWORK_CACHED_INDEX));
		networkFixTime = cursor.isNull(Locations.NETWORK_FIX_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Locations.NETWORK_FIX_TIME_INDEX));
		networkAccuracy = cursor.isNull(Locations.NETWORK_ACCURACY_INDEX) ? null
				: cursor.getFloat(Locations.NETWORK_ACCURACY_INDEX);
		networkLatitude = cursor.isNull(Locations.NETWORK_LATITUDE_INDEX) ? null
				: cursor.getDouble(Locations.NETWORK_LATITUDE_INDEX);
		networkLongitude = cursor.isNull(Locations.NETWORK_LONGITUDE_INDEX) ? null
				: cursor.getDouble(Locations.NETWORK_LONGITUDE_INDEX);

		cellId = cursor.isNull(Locations.CELL_ID_INDEX) ? null : cursor
				.getInt(Locations.CELL_ID_INDEX);
		cellLac = cursor.isNull(Locations.CELL_LAC_INDEX) ? null : cursor
				.getInt(Locations.CELL_LAC_INDEX);
		cellPsc = cursor.isNull(Locations.CELL_PSC_INDEX) ? null : cursor
				.getInt(Locations.CELL_PSC_INDEX);
		cellMcc = cursor.isNull(Locations.CELL_MCC_INDEX) ? null : cursor
				.getInt(Locations.CELL_MCC_INDEX);
		cellMnc = cursor.isNull(Locations.CELL_MNC_INDEX) ? null : cursor
				.getInt(Locations.CELL_MNC_INDEX);
		signalStrength = cursor.isNull(Locations.SIGNAL_STRENGTH_INDEX) ? null
				: cursor.getInt(Locations.SIGNAL_STRENGTH_INDEX);
		batteryLevel = cursor.isNull(Locations.BATTERY_LEVEL_INDEX) ? null
				: cursor.getInt(Locations.BATTERY_LEVEL_INDEX);

		connected = cursor.isNull(Locations.CONNECTED_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Locations.CONNECTED_INDEX));
		wifiConnected = cursor.isNull(Locations.WIFI_CONNECTED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Locations.WIFI_CONNECTED_INDEX));
		forId = cursor.isNull(Locations.FOR_ID_INDEX) ? null : cursor
				.getLong(Locations.FOR_ID_INDEX);
		dirty = cursor.isNull(Locations.DIRTY_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Locations.DIRTY_INDEX));
		smsProcessState = cursor.isNull(Locations.SMS_PROCESS_STATE_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Locations.SMS_PROCESS_STATE_INDEX));

		localCreationTime = cursor.isNull(Locations.LOCAL_CREATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Locations.LOCAL_CREATION_TIME_INDEX));
		localModificationTime = cursor
				.isNull(Locations.LOCAL_MODIFICATION_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Locations.LOCAL_MODIFICATION_TIME_INDEX));
		freshFix = cursor.isNull(Locations.FRESH_FIX_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Locations.FRESH_FIX_INDEX));
		remoteLocationId = cursor.isNull(Locations.REMOTE_LOCATION_ID_INDEX) ? null
				: cursor.getLong(Locations.REMOTE_LOCATION_ID_INDEX);
		reasonForTracking = cursor.isNull(Locations.REASON_FOR_TRACKING_INDEX) ? null
				: cursor.getInt(Locations.REASON_FOR_TRACKING_INDEX);
		fusedOn = cursor.isNull(Locations.FUSED_ON_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(Locations.FUSED_ON_INDEX));
		fusedFinalized = cursor.isNull(Locations.FUSED_FINALIZED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Locations.FUSED_FINALIZED_INDEX));
		fusedCached = cursor.isNull(Locations.FUSED_CACHED_INDEX) ? null
				: Boolean.parseBoolean(cursor
						.getString(Locations.FUSED_CACHED_INDEX));
		fusedFixTime = cursor.isNull(Locations.FUSED_FIX_TIME_INDEX) ? null
				: SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Locations.FUSED_FIX_TIME_INDEX));
		fusedAccuracy = cursor.isNull(Locations.FUSED_ACCURACY_INDEX) ? null
				: cursor.getFloat(Locations.FUSED_ACCURACY_INDEX);
		fusedLatitude = cursor.isNull(Locations.FUSED_LATITUDE_INDEX) ? null
				: cursor.getDouble(Locations.FUSED_LATITUDE_INDEX);
		fusedLongitude = cursor.isNull(Locations.FUSED_LONGITUDE_INDEX) ? null
				: cursor.getDouble(Locations.FUSED_LONGITUDE_INDEX);
		fusedAltitude = cursor.isNull(Locations.FUSED_ALTITUDE_INDEX) ? null
				: cursor.getDouble(Locations.FUSED_ALTITUDE_INDEX);
		fusedSpeed = cursor.isNull(Locations.FUSED_SPEED_INDEX) ? null : cursor
				.getFloat(Locations.FUSED_SPEED_INDEX);
		fusedBearing = cursor.isNull(Locations.FUSED_BEARING_INDEX) ? null
				: cursor.getFloat(Locations.FUSED_BEARING_INDEX);
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
	}

	public Integer getPurpose() {
		return purpose;
	}

	public void setPurpose(Integer purpose) {
		this.purpose = purpose;
	}

	public Boolean getLocationFinalized() {
		return locationFinalized;
	}

	public void setLocationFinalized(Boolean locationFinalized) {
		this.locationFinalized = locationFinalized;
	}

	public Boolean getGpsOn() {
		return gpsOn;
	}

	public void setGpsOn(Boolean gpsOn) {
		this.gpsOn = gpsOn;
	}

	public Boolean getGpsFinalized() {
		return gpsFinalized;
	}

	public void setGpsFinalized(Boolean gpsFinalized) {
		this.gpsFinalized = gpsFinalized;
	}

	public Boolean getGpsCached() {
		return gpsCached;
	}

	public void setGpsCached(Boolean gpsCached) {
		this.gpsCached = gpsCached;
	}

	public Date getGpsFixTime() {
		return gpsFixTime;
	}

	public void setGpsFixTime(Date gpsFixTime) {
		this.gpsFixTime = gpsFixTime;
	}

	public Float getGpsAccuracy() {
		return gpsAccuracy;
	}

	public void setGpsAccuracy(Float gpsAccuracy) {
		this.gpsAccuracy = gpsAccuracy;
	}

	public Double getGpsLatitude() {
		return gpsLatitude;
	}

	public void setGpsLatitude(Double gpsLatitude) {
		this.gpsLatitude = gpsLatitude;
	}

	public Double getGpsLongitude() {
		return gpsLongitude;
	}

	public void setGpsLongitude(Double gpsLongitude) {
		this.gpsLongitude = gpsLongitude;
	}

	public Double getGpsAltitude() {
		return gpsAltitude;
	}

	public void setGpsAltitude(Double gpsAltitude) {
		this.gpsAltitude = gpsAltitude;
	}

	public Float getGpsSpeed() {
		return gpsSpeed;
	}

	public void setGpsSpeed(Float gpsSpeed) {
		this.gpsSpeed = gpsSpeed;
	}

	public Float getGpsBearing() {
		return gpsBearing;
	}

	public void setGpsBearing(Float gpsBearing) {
		this.gpsBearing = gpsBearing;
	}

	public Boolean getNetworkOn() {
		return networkOn;
	}

	public void setNetworkOn(Boolean networkOn) {
		this.networkOn = networkOn;
	}

	public Boolean getNetworkFinalized() {
		return networkFinalized;
	}

	public void setNetworkFinalized(Boolean networkFinalized) {
		this.networkFinalized = networkFinalized;
	}

	public Boolean getNetworkCached() {
		return networkCached;
	}

	public void setNetworkCached(Boolean networkCached) {
		this.networkCached = networkCached;
	}

	public Date getNetworkFixTime() {
		return networkFixTime;
	}

	public void setNetworkFixTime(Date networkFixTime) {
		this.networkFixTime = networkFixTime;
	}

	public Float getNetworkAccuracy() {
		return networkAccuracy;
	}

	public void setNetworkAccuracy(Float networkAccuracy) {
		this.networkAccuracy = networkAccuracy;
	}

	public Double getNetworkLatitude() {
		return networkLatitude;
	}

	public void setNetworkLatitude(Double networkLatitude) {
		this.networkLatitude = networkLatitude;
	}

	public Double getNetworkLongitude() {
		return networkLongitude;
	}

	public void setNetworkLongitude(Double networkLongitude) {
		this.networkLongitude = networkLongitude;
	}

	public Integer getCellId() {
		return cellId;
	}

	public void setCellId(Integer cellId) {
		this.cellId = cellId;
	}

	public Integer getCellLac() {
		return cellLac;
	}

	public void setCellLac(Integer cellLac) {
		this.cellLac = cellLac;
	}

	public Integer getCellPsc() {
		return cellPsc;
	}

	public void setCellPsc(Integer cellPsc) {
		this.cellPsc = cellPsc;
	}

	public Integer getCellMcc() {
		return cellMcc;
	}

	public void setCellMcc(Integer cellMcc) {
		this.cellMcc = cellMcc;
	}

	public Integer getCellMnc() {
		return cellMnc;
	}

	public void setCellMnc(Integer cellMnc) {
		this.cellMnc = cellMnc;
	}

	public Integer getSignalStrength() {
		return signalStrength;
	}

	public void setSignalStrength(Integer signalStrength) {
		this.signalStrength = signalStrength;
	}

	public Integer getBatteryLevel() {
		return batteryLevel;
	}

	public void setBatteryLevel(Integer batteryLevel) {
		this.batteryLevel = batteryLevel;
	}

	public Boolean getConnected() {
		return connected;
	}

	public void setConnected(Boolean connected) {
		this.connected = connected;
	}

	public Boolean getWifiConnected() {
		return wifiConnected;
	}

	public void setWifiConnected(Boolean wifiConnected) {
		this.wifiConnected = wifiConnected;
	}

	public Long getForId() {
		return forId;
	}

	public void setForId(Long forId) {
		this.forId = forId;
	}

	public Boolean getDirty() {
		return dirty;
	}

	public void setDirty(Boolean dirty) {
		this.dirty = dirty;
	}

	public Boolean getSmsProcessState() {
		return smsProcessState;
	}

	public void setSmsProcessState(Boolean smsProcessState) {
		this.smsProcessState = smsProcessState;
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

	public Boolean getIsFallBack() {
		return isFallBack;
	}

	public void setIsFallBack(Boolean isFallBack) {
		this.isFallBack = isFallBack;
	}

	public Boolean getFreshFix() {
		return freshFix;
	}

	public void setFreshFix(Boolean freshFix) {
		this.freshFix = freshFix;
	}

	public Long getRemoteLocationId() {
		return remoteLocationId;
	}

	public void setRemoteLocationId(Long remoteLocationId) {
		this.remoteLocationId = remoteLocationId;
	}

	public Integer getReasonForTracking() {
		return reasonForTracking;
	}

	public void setReasonForTracking(Integer reasonForTracking) {
		this.reasonForTracking = reasonForTracking;
	}

	public Boolean getFusedOn() {
		return fusedOn;
	}

	public void setFusedOn(Boolean fusedOn) {
		this.fusedOn = fusedOn;
	}

	public Boolean getFusedFinalized() {
		return fusedFinalized;
	}

	public void setFusedFinalized(Boolean fusedFinalized) {
		this.fusedFinalized = fusedFinalized;
	}

	public Boolean getFusedCached() {
		return fusedCached;
	}

	public void setFusedCached(Boolean fusedCached) {
		this.fusedCached = fusedCached;
	}

	public Date getFusedFixTime() {
		return fusedFixTime;
	}

	public void setFusedFixTime(Date fusedFixTime) {
		this.fusedFixTime = fusedFixTime;
	}

	public Float getFusedAccuracy() {
		return fusedAccuracy;
	}

	public void setFusedAccuracy(Float fusedAccuracy) {
		this.fusedAccuracy = fusedAccuracy;
	}

	public Double getFusedLatitude() {
		return fusedLatitude;
	}

	public void setFusedLatitude(Double fusedLatitude) {
		this.fusedLatitude = fusedLatitude;
	}

	public Double getFusedLongitude() {
		return fusedLongitude;
	}

	public void setFusedLongitude(Double fusedLongitude) {
		this.fusedLongitude = fusedLongitude;
	}

	public Double getFusedAltitude() {
		return fusedAltitude;
	}

	public void setFusedAltitude(Double fusedAltitude) {
		this.fusedAltitude = fusedAltitude;
	}

	public Float getFusedSpeed() {
		return fusedSpeed;
	}

	public void setFusedSpeed(Float fusedSpeed) {
		this.fusedSpeed = fusedSpeed;
	}

	public Float getFusedBearing() {
		return fusedBearing;
	}

	public void setFusedBearing(Float fusedBearing) {
		this.fusedBearing = fusedBearing;
	}

	public int getConnectivityStatus() {
		if (wifiConnected) {
			return CONNECTED_USING_WIFI;
		} else if (connected) {
			return CONNECTED;
		} else {
			return NOT_CONNECTED;
		}
	}

	/**
	 * Get the JSON object that can be sent to the server.
	 * 
	 * @param applicationContext
	 * @return
	 */
	public JSONObject getJsonObject(Context applicationContext,
			int requestedPurpose) {
		JSONObject trackJson = new JSONObject();
		JSONObject json = new JSONObject();

		try {
			int connectivityStatus = getConnectivityStatus();

			Utils.putValueOnlyIfNotNull(json, JSON_CONNECTIVITY_STATUS,
					connectivityStatus);
			Utils.putValueOnlyIfNotNull(json, JSON_FRESH_FIX, freshFix);
			Utils.putValueOnlyIfNotNull(json, JSON_LOCAL_CREATION_TIME,
					localCreationTime);
			Utils.putValueOnlyIfNotNull(trackJson, JSON_SIGNAL_STRENGTH,
					signalStrength);
			Utils.putValueOnlyIfNotNull(trackJson, JSON_BATTERY_LEVEL,
					batteryLevel);
			Utils.putValueOnlyIfNotNull(json, JSON_GPS_ON, gpsOn);

			if (gpsOn != null && gpsOn) {
				Utils.putValueOnlyIfNotNull(json, JSON_GPS_FIX_TIME, gpsFixTime);
				Utils.putValueOnlyIfNotNull(json, JSON_GPS_LATITUDE,
						gpsLatitude);
				Utils.putValueOnlyIfNotNull(json, JSON_GPS_LONGITUDE,
						gpsLongitude);
				Utils.putValueOnlyIfNotNull(json, JSON_GPS_ALTITUDE,
						gpsAltitude);
				Utils.putValueOnlyIfNotNull(json, JSON_GPS_ACCURACY,
						gpsAccuracy);
				Utils.putValueOnlyIfNotNull(json, JSON_GPS_SPEED, gpsSpeed);
				Utils.putValueOnlyIfNotNull(json, JSON_GPS_BEARING, gpsBearing);
			}

			Utils.putValueOnlyIfNotNull(json, JSON_NETWORK_ON, networkOn);
			Utils.putValueOnlyIfNotNull(json, JSON_REASON, reasonForTracking);

			if (connectivityStatus != NOT_CONNECTED && networkOn != null
					&& networkOn) {
				Utils.putValueOnlyIfNotNull(json, JSON_NETWORK_FIX_TIME,
						networkFixTime);
				Utils.putValueOnlyIfNotNull(json, JSON_NETWORK_LATITUDE,
						networkLatitude);
				Utils.putValueOnlyIfNotNull(json, JSON_NETWORK_LONGITUDE,
						networkLongitude);
				Utils.putValueOnlyIfNotNull(json, JSON_NETWORK_ACCURACY,
						networkAccuracy);
				Utils.putValueOnlyIfNotNull(json, JSON_CELL_ID, cellId);
				Utils.putValueOnlyIfNotNull(json, JSON_CELL_LAC, cellLac);
				Utils.putValueOnlyIfNotNull(json, JSON_CELL_MCC, cellMcc);
				Utils.putValueOnlyIfNotNull(json, JSON_CELL_MNC, cellMnc);

				NeighboringCellInfosDao cellInfoDao = NeighboringCellInfosDao
						.getInstance(applicationContext);
				List<NeighboringCellInfoDto> cellInfos = cellInfoDao
						.getNeighboringCellInfos(localId);

				if (cellInfos != null) {
					JSONArray cellsArray = new JSONArray();

					for (NeighboringCellInfoDto cellInfo : cellInfos) {
						cellsArray.put(cellInfo
								.getJsonObject(applicationContext));
					}

					json.put(JSON_NEAR_BY_CELL_INFO, cellsArray);
				}
			}

			Utils.putValueOnlyIfNotNull(json, JSON_FUSED_ON, fusedOn);

			if (fusedOn != null && fusedOn) {
				Utils.putValueOnlyIfNotNull(json, JSON_FUSED_FIX_TIME,
						fusedFixTime);
				Utils.putValueOnlyIfNotNull(json, JSON_FUSED_LATITUDE,
						fusedLatitude);
				Utils.putValueOnlyIfNotNull(json, JSON_FUSED_LONGITUDE,
						fusedLongitude);
				// Utils.putValueOnlyIfNotNull(json, JSON_FUSED_ALTITUDE,
				// fusedAltitude);
				Utils.putValueOnlyIfNotNull(json, JSON_FUSED_ACCURACY,
						fusedAccuracy);
				// Utils.putValueOnlyIfNotNull(json, JSON_FUSED_SPEED,
				// fusedSpeed);
				// Utils.putValueOnlyIfNotNull(json, JSON_FUSED_BEARING,
				// fusedBearing);
			}

			if (requestedPurpose == Locations.PURPOSE_TRACKING) {
				// Get purpose from location object, don't use requestedPurpose
				if (BuildConfig.DEBUG) {
					Log.v("track", " loc purpose is " + purpose);
				}
				Utils.putValueOnlyIfNotNull(json, JSON_PURPOSE, purpose);
			}
			trackJson.put("location", json);
		} catch (JSONException e) {
			Log.e(TAG, "Failed to compose JSON for location: " + e.toString(),
					e);
			return null;
		}

		if (requestedPurpose == Locations.PURPOSE_TRACKING) {
			return trackJson;
		} else {
			return json;
		}
	}

	/**
	 * Get the SMS body to send location via SMS.
	 */
	public String getSmsBody(String employeeId) {
		int connectivityStatus = NOT_CONNECTED;

		if (wifiConnected) {
			connectivityStatus = CONNECTED_USING_WIFI;
		} else if (connected) {
			connectivityStatus = CONNECTED;
		}

		StringBuffer stringBuffer = new StringBuffer();

		if (fusedLatitude == null || fusedLongitude == null) {
			stringBuffer.append("TRACK UPDATE 4 ");
			Utils.putValueOnlyIfNotNull(stringBuffer, localCreationTime);
			Utils.putValueOnlyIfNotNull(stringBuffer, gpsAccuracy);
			Utils.putValueOnlyIfNotNull(stringBuffer, gpsFixTime);
			Utils.putValueOnlyIfNotNull(stringBuffer, gpsLatitude);
			Utils.putValueOnlyIfNotNull(stringBuffer, gpsLongitude);
			Utils.putValueOnlyIfNotNull(stringBuffer, networkAccuracy);
			Utils.putValueOnlyIfNotNull(stringBuffer, networkFixTime);
			Utils.putValueOnlyIfNotNull(stringBuffer, networkLatitude);
			Utils.putValueOnlyIfNotNull(stringBuffer, networkLongitude);
			Utils.putValueOnlyIfNotNull(stringBuffer, connectivityStatus);
			Utils.putValueOnlyIfNotNull(stringBuffer, batteryLevel);
			Utils.putValueOnlyIfNotNull(stringBuffer, employeeId);
			Utils.putValueOnlyIfNotNull(stringBuffer, freshFix ? 1 : 0);
		} else {
			stringBuffer.append("TRACK UPDATEUN 4 ");
			Utils.putValueOnlyIfNotNull(stringBuffer, localCreationTime);
			Utils.putValueOnlyIfNotNull(stringBuffer, fusedAccuracy);
			Utils.putValueOnlyIfNotNull(stringBuffer, fusedFixTime);
			Utils.putValueOnlyIfNotNull(stringBuffer, fusedLatitude);
			Utils.putValueOnlyIfNotNull(stringBuffer, fusedLongitude);
			Utils.putValueOnlyIfNotNull(stringBuffer, connectivityStatus);
			Utils.putValueOnlyIfNotNull(stringBuffer, batteryLevel);
			Utils.putValueOnlyIfNotNull(stringBuffer, employeeId);
			Utils.putValueOnlyIfNotNull(stringBuffer, freshFix ? 1 : 0);
			Utils.putValueOnlyIfNotNull(stringBuffer, gpsOn ? 1 : 0);
			Utils.putValueOnlyIfNotNull(stringBuffer, networkOn ? 1 : 0);
		}

		if (purpose == Locations.PURPOSE_TRACKING
				|| purpose == Locations.PURPOSE_START_WORK
				|| purpose == Locations.PURPOSE_STOP_WORK) {
			Utils.putValueOnlyIfNotNull(stringBuffer, purpose);
		}
		String smsBody = stringBuffer.toString();
		smsBody = smsBody.substring(0, smsBody.length() - 1);

		return smsBody;

	}

	@Override
	public String toString() {
		return "LocationDto [localId=" + localId + ", purpose=" + purpose
				+ ", locationFinalized=" + locationFinalized + ", gpsOn="
				+ gpsOn + ", gpsFinalized=" + gpsFinalized + ", gpsCached="
				+ gpsCached + ", gpsFixTime=" + gpsFixTime + ", gpsAccuracy="
				+ gpsAccuracy + ", gpsLatitude=" + gpsLatitude
				+ ", gpsLongitude=" + gpsLongitude + ", gpsAltitude="
				+ gpsAltitude + ", gpsSpeed=" + gpsSpeed + ", gpsBearing="
				+ gpsBearing + ", networkOn=" + networkOn
				+ ", networkFinalized=" + networkFinalized + ", networkCached="
				+ networkCached + ", networkFixTime=" + networkFixTime
				+ ", networkAccuracy=" + networkAccuracy + ", networkLatitude="
				+ networkLatitude + ", networkLongitude=" + networkLongitude
				+ ", cellId=" + cellId + ", cellLac=" + cellLac + ", cellPsc="
				+ cellPsc + ", cellMcc=" + cellMcc + ", cellMnc=" + cellMnc
				+ ", signalStrength=" + signalStrength + ", batteryLevel="
				+ batteryLevel + ", connected=" + connected
				+ ", wifiConnected=" + wifiConnected + ", forId=" + forId
				+ ", dirty=" + dirty + ", smsProcessState=" + smsProcessState
				+ ", isFallBack=" + isFallBack + ", localCreationTime="
				+ localCreationTime + ", localModificationTime="
				+ localModificationTime + ", freshFix=" + freshFix
				+ ", remoteLocationId=" + remoteLocationId + "]";
	}

}

package in.spoors.effort1;

import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.dao.NeighboringCellInfosDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.LocationDto;
import in.spoors.effort1.dto.NeighboringCellInfoDto;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class LocationCaptureService extends WakefulIntentService {
	public static final String TAG = "LocationCaptureService";
	private LocationManager locationManager;
	private SettingsDao settingsDao;
	private LocationsDao locationsDao;
	private int purpose;

	private static final int UNKNOWN = -1;

	public LocationCaptureService() {
		super(TAG);
	}

	private boolean useFused() {
		return Settings.LOCATION_PROVIDER_FUSED.equals(settingsDao
				.getString(Settings.KEY_LOCATION_PROVIDER));
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void doWakefulWork(Intent intent) {
		EffortApplication.log(TAG, "In doWakefulWork.");
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		locationsDao = LocationsDao.getInstance(getApplicationContext());
		locationsDao.finalizeTimedOutLocations();
		LocationDto locationDto = new LocationDto();

		// set purpose
		purpose = intent.getIntExtra(Locations.PURPOSE,
				Locations.PURPOSE_TRACKING);

		// TRACKING SCNARIOS
		int trackingType = settingsDao.getInt(Settings.KEY_TRACKING_TYPE,
				Settings.TRACKING_TYPE_IN_WORK_HOURS);

		if (trackingType == Settings.TRACKING_TYPE_NEVER) {
			Log.i(TAG,
					"Tracking never happens, because employee setting is set to never track");
			return;
		} else if (trackingType == Settings.TRACKING_TYPE_IN_ACTIVITIES) {
			if (purpose == Locations.PURPOSE_NOTE
					|| purpose == Locations.PURPOSE_HISTORY
					|| purpose == Locations.PURPOSE_FORM
					|| purpose == Locations.PURPOSE_COMPLETE_ACTIVITY
					|| purpose == Locations.PURPOSE_CUSTOMER_STATUS_ACTIVITY
					|| purpose == Locations.PURPOSE_NEARBY_CUSTOMERS
					|| purpose == Locations.PURPOSE_COMPLETE_ROUTE_PLAN
					|| purpose == Locations.PURPOSE_FORM_FILE
					|| purpose == Locations.PURPOSE_SECTION_FILE
					|| purpose == Locations.PURPOSE_START_WORK
					|| purpose == Locations.PURPOSE_STOP_WORK) {
				Log.i(TAG,
						"Tracking happens, because it is in effort activities");
			} else {
				Log.i(TAG, "Tracking not happens in activities");
				return;
			}

		} else {
			Log.i(TAG, "Tracking happens always");
		}

		locationDto.setPurpose(purpose);
		locationDto.setDirty(true);
		locationDto.setSmsProcessState(false);

		int reasonForTracking = -1;

		if (purpose == Locations.PURPOSE_TRACKING) {
			reasonForTracking = Utils
					.canTrackNowWithReason(getApplicationContext());
			if (reasonForTracking == -1) {
				EffortApplication.log(TAG,
						"Location capture requested at a non-trackable time.");

				if (useFused()) {
					Utils.stopFusedReceiver(getApplicationContext(), false);
					EffortApplication
							.log(TAG,
									"Stopped fused receivers (non-forecefully) because it is non-trackable time.");
				} else {
					Utils.stopGpsReceiver(getApplicationContext(), false);
					Utils.stopNetworkReceiver(getApplicationContext(), false);
					EffortApplication
							.log(TAG,
									"Stopped both the receivers (non-forecefully) because it is non-trackable time.");
				}

				return;
			}

			locationDto.setReasonForTracking(reasonForTracking);
		}

		EffortApplication.log(TAG, "Location capture requested for " + purpose);

		// set forId
		long forId = intent.getLongExtra(Locations.FOR_ID, 0);

		if (forId != 0) {
			locationDto.setForId(forId);
		}

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		CellLocation cellLocation = telephonyManager.getCellLocation();

		if (cellLocation instanceof GsmCellLocation) {
			GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;

			if (gsmCellLocation.getCid() != UNKNOWN) {
				locationDto.setCellId(gsmCellLocation.getCid());
			}

			if (gsmCellLocation.getLac() != UNKNOWN) {
				locationDto.setCellLac(gsmCellLocation.getLac());
			}

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				if (gsmCellLocation.getPsc() != UNKNOWN) {
					locationDto.setCellPsc(gsmCellLocation.getPsc());
				}
			}
		}

		int signalStrength = ((EffortApplication) getApplication())
				.getSignalStrength();
		if (signalStrength != EffortApplication.SIGNAL_STRENGTH_UNKNOWN) {
			locationDto.setSignalStrength(Utils.toDbm(signalStrength));
		}

		int batteryLevel = Utils.getBatteryLevel(getApplicationContext());
		if (batteryLevel != -1) {
			locationDto.setBatteryLevel(batteryLevel);
		}

		// TODO: add support for CDMA networks as well

		String mccMnc = telephonyManager.getNetworkOperator();
		final int MCC_LENGTH = 3;

		if (mccMnc != null && mccMnc.length() > MCC_LENGTH
				&& TextUtils.isDigitsOnly(mccMnc)) {
			locationDto.setCellMcc(Integer.parseInt(mccMnc.substring(0,
					MCC_LENGTH)));
			locationDto.setCellMnc(Integer.parseInt(mccMnc
					.substring(MCC_LENGTH)));
		}

		locationDto.setConnected(Utils.isConnected(getApplicationContext()));
		locationDto.setWifiConnected(Utils
				.isConnectedToWifi(getApplicationContext()));

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		boolean gpsOn = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean networkOn = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		boolean fusedOn = gpsOn || networkOn;
		locationDto.setGpsOn(gpsOn);
		locationDto.setGpsFinalized(!gpsOn);
		locationDto.setNetworkOn(networkOn);
		locationDto.setNetworkFinalized(!networkOn);
		locationDto.setFusedOn(fusedOn);
		locationDto.setFusedFinalized(!fusedOn);
		locationDto.setLocationFinalized(locationDto.getGpsFinalized()
				&& locationDto.getNetworkFinalized());

		if (useFused()) {
			Location location = Utils.getLastKnownFusedLocation(settingsDao);

			if (location != null && fusedOn) {
				Utils.fillLocationDto(locationDto, "fused", location, true,
						false);
			}
		} else {
			Location location = Utils.getLastKnownGpsLocation(settingsDao);

			if (location != null && gpsOn) {
				Utils.fillLocationDto(locationDto,
						LocationManager.GPS_PROVIDER, location, true, false);
			}

			location = Utils.getLastKnownNetworkLocation(settingsDao);

			if (location != null && networkOn) {
				Utils.fillLocationDto(locationDto,
						LocationManager.NETWORK_PROVIDER, location, true, false);
			}
		}

		locationsDao.save(locationDto);

		NeighboringCellInfosDao cellInfosDao = NeighboringCellInfosDao
				.getInstance(getApplicationContext());

		List<NeighboringCellInfo> cellInfos = telephonyManager
				.getNeighboringCellInfo();
		if (cellInfos != null) {
			for (NeighboringCellInfo cellInfo : cellInfos) {
				if (cellInfo == null) {
					continue;
				}

				NeighboringCellInfoDto cellInfoDto = new NeighboringCellInfoDto();

				if (cellInfo.getCid() != NeighboringCellInfo.UNKNOWN_CID) {
					cellInfoDto.setCellId(cellInfo.getCid());
				}

				if (cellInfo.getLac() != NeighboringCellInfo.UNKNOWN_CID) {
					cellInfoDto.setCellLac(cellInfo.getLac());
				}

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
					if (cellInfo.getPsc() != NeighboringCellInfo.UNKNOWN_CID) {
						cellInfoDto.setCellPsc(cellInfo.getPsc());
					}
				}

				if (cellInfo.getRssi() != NeighboringCellInfo.UNKNOWN_RSSI) {
					cellInfoDto.setCellRssi(Utils.toDbm(cellInfo.getRssi()));
				}

				cellInfoDto.setLocationId(locationDto.getLocalId());
				cellInfosDao.save(cellInfoDto);
			}
		}

		if (useFused()) {
			if (!fusedOn) {
				Utils.stopFusedReceiver(getApplicationContext(), true);
				EffortApplication
						.log(TAG,
								"Forcefully stopped fused receiver as provider is off.");
			}
		} else {
			if (!gpsOn) {
				Utils.stopGpsReceiver(getApplicationContext(), true);
				EffortApplication.log(TAG,
						"Forcefully stopped gps receiver as provider is off.");
			}

			if (!networkOn) {
				Utils.stopNetworkReceiver(getApplicationContext(), true);
				EffortApplication
						.log(TAG,
								"Forcefully stopped network receiver as provider is off.");
			}
		}

		if (!locationDto.getLocationFinalized()) {
			if (useFused()) {
				Utils.startFusedReceiver(getApplicationContext());
				EffortApplication.log(TAG, "Started fused receiver.");
			} else {
				if (gpsOn) {
					Utils.startGpsReceiver(getApplicationContext());
					EffortApplication.log(TAG, "Started gps receiver.");
				}

				if (networkOn) {
					Utils.startNetworkReceiver(getApplicationContext());
					EffortApplication.log(TAG, "Started network receiver.");
				}
			}
		}
	}
}

package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.LocationDto;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class LocationUpdateService extends WakefulIntentService {

	private static final String TAG = "LocationUpdateService";
	private SettingsDao settingsDao;
	private LocationsDao locationsDao;

	public LocationUpdateService() {
		super(TAG);
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		if (intent == null
				|| !intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)) {
			EffortApplication.log(TAG, "Location missing.");
			return;
		}

		Location location = (Location) intent
				.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);

		if (location == null) {
			EffortApplication.log(TAG, "Location is null.");
			return;
		}

		locationsDao = LocationsDao.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());

		boolean freshFix = false;

		Utils.nagIfRequired(getApplicationContext(), location);

		String prevLatitude = settingsDao.getString("nearbyLatitude");
		String prevLongitude = settingsDao.getString("nearbyLongitude");

		if (settingsDao.getBoolean(Settings.KEY_PREFETCH_NEARBY_CUSTOMERS,
				false)) {
			Utils.fetchNearbyCustomersIfRequired(getApplicationContext(),
					prevLatitude, prevLongitude, location.getLatitude(),
					location.getLongitude());
		}

		locationsDao.deleteNearbyLocations();

		if ("fused".equals(location.getProvider())) {
			if (TextUtils
					.isEmpty(settingsDao
							.getString(EffortProvider.Settings.KEY_LAST_FUSED_LATITUDE))) {
				freshFix = true;
			}

			settingsDao.saveSetting(
					EffortProvider.Settings.KEY_LAST_FUSED_FIX_TIME,
					SQLiteDateTimeUtils
							.getSQLiteDateTimeFromLocalMillis(location
									.getTime()));

			settingsDao.saveSetting(
					EffortProvider.Settings.KEY_LAST_FUSED_LATITUDE,
					String.valueOf(location.getLatitude()));

			settingsDao.saveSetting(
					EffortProvider.Settings.KEY_LAST_FUSED_LONGITUDE,
					String.valueOf(location.getLongitude()));

			if (location.hasAltitude()) {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_FUSED_ALTITUDE,
						String.valueOf(location.getAltitude()));
			} else {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_FUSED_ALTITUDE, "");
			}

			if (location.hasAccuracy()) {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_FUSED_ACCURACY,
						String.valueOf(location.getAccuracy()));
			} else {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_FUSED_ACCURACY, "");
			}

			if (location.hasSpeed()) {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_FUSED_SPEED,
						String.valueOf(location.getSpeed()));
			} else {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_FUSED_SPEED, "");
			}

			if (location.hasBearing()) {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_FUSED_BEARING,
						String.valueOf(location.getBearing()));
			} else {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_FUSED_BEARING, "");
			}

			boolean updated = updateFusedLocations(location, freshFix);

			if (updated) {
				if (locationsDao.hasUnsyncedNonTracks()) {
					Utils.sync(getApplicationContext());
				}

				WakefulIntentService.sendWakefulWork(this,
						SendTracksService.class);
			}
		} else if (LocationManager.GPS_PROVIDER.equals(location.getProvider())) {
			if (TextUtils.isEmpty(settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_GPS_LATITUDE))) {
				freshFix = true;
			}

			settingsDao.saveSetting(
					EffortProvider.Settings.KEY_LAST_GPS_FIX_TIME,
					SQLiteDateTimeUtils
							.getSQLiteDateTimeFromLocalMillis(location
									.getTime()));

			settingsDao.saveSetting(
					EffortProvider.Settings.KEY_LAST_GPS_LATITUDE,
					String.valueOf(location.getLatitude()));

			settingsDao.saveSetting(
					EffortProvider.Settings.KEY_LAST_GPS_LONGITUDE,
					String.valueOf(location.getLongitude()));

			if (location.hasAltitude()) {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_GPS_ALTITUDE,
						String.valueOf(location.getAltitude()));
			} else {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_GPS_ALTITUDE, "");
			}

			if (location.hasAccuracy()) {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_GPS_ACCURACY,
						String.valueOf(location.getAccuracy()));
			} else {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_GPS_ACCURACY, "");
			}

			if (location.hasSpeed()) {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_GPS_SPEED,
						String.valueOf(location.getSpeed()));
			} else {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_GPS_SPEED, "");
			}

			if (location.hasBearing()) {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_GPS_BEARING,
						String.valueOf(location.getBearing()));
			} else {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_GPS_BEARING, "");
			}

			boolean updated = updateGpsLocations(location, freshFix);

			if (updated) {
				if (locationsDao.hasUnsyncedNonTracks()) {
					Utils.sync(getApplicationContext());
				}

				WakefulIntentService.sendWakefulWork(this,
						SendTracksService.class);
			}
		} else if (LocationManager.NETWORK_PROVIDER.equals(location
				.getProvider())) {
			if (TextUtils
					.isEmpty(settingsDao
							.getString(EffortProvider.Settings.KEY_LAST_NETWORK_LATITUDE))) {
				freshFix = true;
			}

			settingsDao.saveSetting(
					EffortProvider.Settings.KEY_LAST_NETWORK_FIX_TIME,
					SQLiteDateTimeUtils
							.getSQLiteDateTimeFromLocalMillis(location
									.getTime()));
			settingsDao.saveSetting(
					EffortProvider.Settings.KEY_LAST_NETWORK_LATITUDE,
					String.valueOf(location.getLatitude()));

			settingsDao.saveSetting(
					EffortProvider.Settings.KEY_LAST_NETWORK_LONGITUDE,
					String.valueOf(location.getLongitude()));

			if (location.hasAccuracy()) {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_NETWORK_ACCURACY,
						String.valueOf(location.getAccuracy()));
			} else {
				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_NETWORK_ACCURACY, "");
			}

			boolean updated = updateNetworkLocations(location, freshFix);

			if (updated) {
				if (locationsDao.hasUnsyncedNonTracks()) {
					Utils.sync(getApplicationContext());
				}

				WakefulIntentService.sendWakefulWork(this,
						SendTracksService.class);
			}
		}
	}

	private boolean updateNetworkLocations(Location location, boolean freshFix) {
		boolean updated = false;
		List<LocationDto> locations = null;
		locations = locationsDao
				.getUnfinalizedNetworkLocations(SQLiteDateTimeUtils
						.getSQLiteDateTimeFromUtcMillis(location.getTime()));

		if (locations == null) {
			EffortApplication.log(TAG, "No unfinalized network locations.");
			boolean keepListening = settingsDao.getBoolean(
					Settings.KEY_KEEP_LISTENING_FOR_GPS_UPDATES, false);

			if (!keepListening) {
				EffortApplication
						.log(TAG,
								"Forcefully stopped network receiver (no more locations to be updated).");
				Utils.stopNetworkReceiver(getApplicationContext(), true);
			}
		} else {
			boolean markedAsFreshFix = false;

			for (int i = 0; i < locations.size(); ++i) {
				LocationDto locationDto = locations.get(i);
				EffortApplication.log(TAG,
						"Updating location " + locationDto.getLocalId()
								+ " with received network location.");
				Utils.fillLocationDto(locationDto,
						LocationManager.NETWORK_PROVIDER, location, false, true);

				// mark only first location in the set as the fresh fix
				if (freshFix
						&& !markedAsFreshFix
						&& locationDto.getConnectivityStatus() != LocationDto.NOT_CONNECTED) {
					locationDto.setFreshFix(true);
					markedAsFreshFix = true;
				} else {
					locationDto.setFreshFix(false);
				}

				locationsDao.save(locationDto);
				EffortApplication.log(TAG, "Saved network location: "
						+ locationDto.toString());
				updated = true;
			}

			int rowsAffected = locationsDao.finalizeTimedOutLocations();
			EffortApplication.log(TAG, "Finalized " + rowsAffected
					+ " timed out locations (network).");

			locations = locationsDao
					.getUnfinalizedNetworkLocations(SQLiteDateTimeUtils
							.getSQLiteDateTimeFromUtcMillis(location.getTime()));

			if (locations == null) {
				boolean keepListening = settingsDao.getBoolean(
						Settings.KEY_KEEP_LISTENING_FOR_GPS_UPDATES, false);

				if (!keepListening) {
					EffortApplication
							.log(TAG,
									"Forcefully stopped network receiver (no more locations to be updated).");
					Utils.stopNetworkReceiver(getApplicationContext(), true);
				}
			}
		}

		return updated;
	}

	private boolean updateGpsLocations(Location location, boolean freshFix) {
		boolean updated = false;
		List<LocationDto> locations = null;
		locations = locationsDao.getUnfinalizedGpsLocations(SQLiteDateTimeUtils
				.getSQLiteDateTimeFromUtcMillis(location.getTime()));

		if (locations == null) {
			EffortApplication.log(TAG, "No unfinalized gps locations.");

			boolean keepListening = settingsDao.getBoolean(
					Settings.KEY_KEEP_LISTENING_FOR_GPS_UPDATES, false);

			if (!keepListening) {
				EffortApplication
						.log(TAG,
								"Forcefully stopped gps receiver (no more locations to be updated).");
				Utils.stopGpsReceiver(getApplicationContext(), true);
			}
		} else {
			for (int i = 0; i < locations.size(); ++i) {
				LocationDto locationDto = locations.get(i);
				EffortApplication.log(TAG,
						"Updating location " + locationDto.getLocalId()
								+ " with received gps location.");
				Utils.fillLocationDto(locationDto,
						LocationManager.GPS_PROVIDER, location, false, true);

				// mark only first location in the set as the fresh fix
				locationDto.setFreshFix(freshFix && i == 0);

				locationsDao.save(locationDto);
				EffortApplication.log(TAG,
						"Saved gps location: " + locationDto.toString());
				updated = true;
			}

			int rowsAffected = locationsDao.finalizeTimedOutLocations();
			EffortApplication.log(TAG, "Finalized " + rowsAffected
					+ " timed out locations (gps).");

			locations = locationsDao
					.getUnfinalizedGpsLocations(SQLiteDateTimeUtils
							.getSQLiteDateTimeFromUtcMillis(location.getTime()));
			if (locations == null) {
				boolean keepListening = settingsDao.getBoolean(
						Settings.KEY_KEEP_LISTENING_FOR_GPS_UPDATES, false);

				if (!keepListening) {
					EffortApplication
							.log(TAG,
									"Forcefully stopped gps receiver (no more locations to be updated).");
					Utils.stopGpsReceiver(getApplicationContext(), true);
				}
			}
		}

		return updated;
	}

	private boolean updateFusedLocations(Location location, boolean freshFix) {
		boolean updated = false;
		List<LocationDto> locations = null;
		locations = locationsDao
				.getUnfinalizedFusedLocations(SQLiteDateTimeUtils
						.getSQLiteDateTimeFromUtcMillis(location.getTime()));

		if (locations == null) {
			EffortApplication.log(TAG, "No unfinalized fused locations.");

			EffortApplication
					.log(TAG,
							"Forcefully stopped fused receiver (no more locations to be updated).");
			Utils.stopFusedReceiver(getApplicationContext(), true);
		} else {
			for (int i = 0; i < locations.size(); ++i) {
				LocationDto locationDto = locations.get(i);
				EffortApplication.log(TAG,
						"Updating location " + locationDto.getLocalId()
								+ " with received fused location.");
				Utils.fillLocationDto(locationDto, "fused", location, false,
						true);

				// mark only first location in the set as the fresh fix
				locationDto.setFreshFix(freshFix && i == 0);

				locationsDao.save(locationDto);
				EffortApplication.log(TAG, "Saved fused location: "
						+ locationDto.toString());
				updated = true;
			}

			int rowsAffected = locationsDao.finalizeTimedOutLocations();
			EffortApplication.log(TAG, "Finalized " + rowsAffected
					+ " timed out locations (fused).");

			locations = locationsDao
					.getUnfinalizedGpsLocations(SQLiteDateTimeUtils
							.getSQLiteDateTimeFromUtcMillis(location.getTime()));
			if (locations == null) {
				EffortApplication
						.log(TAG,
								"Forcefully stopped fused receiver (no more locations to be updated).");
				Utils.stopGpsReceiver(getApplicationContext(), true);
			}
		}

		return updated;
	}

}

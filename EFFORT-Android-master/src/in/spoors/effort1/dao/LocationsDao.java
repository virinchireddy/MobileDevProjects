package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.LocationDto;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Locations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocationsDao {

	public static final String TAG = "LocationsDao";
	private static LocationsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static LocationsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new LocationsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private LocationsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(LocationDto locationDto) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (locationDto.getLocalId() != null
				&& locationWithLocalIdExists(locationDto.getLocalId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG,
						"Updating the exisiting location: "
								+ locationDto.toString());
			}

			locationDto.setLocalModificationTime(new Date());

			if (locationDto.getLocalCreationTime() == null) {
				locationDto
						.setLocalCreationTime(getLocalCreationTimeWithLocalId(locationDto
								.getLocalId()));
			}

			ContentValues values = locationDto.getContentValues(null);

			db.update(Locations.TABLE, values, Locations._ID + " = "
					+ locationDto.getLocalId(), null);

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new location: " + locationDto.toString());
		}

		Date now = new Date();
		locationDto.setLocalCreationTime(now);
		locationDto.setLocalModificationTime(now);

		ContentValues values = locationDto.getContentValues(null);
		long insertedId = db.insert(Locations.TABLE, null, values);
		locationDto.setLocalId(insertedId);
	}

	public boolean locationWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Locations._ID
					+ ") AS count FROM " + Locations.TABLE + " WHERE "
					+ Locations._ID + " = " + localId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public LocationDto getLocationWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		LocationDto locationDto = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, Locations.ALL_COLUMNS,
					Locations._ID + " = " + localId, null, null, null, null);

			if (cursor.moveToNext()) {
				locationDto = new LocationDto();
				locationDto.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return locationDto;
	}

	public LocationDto getNoteLocation(long localNoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		LocationDto locationDto = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, Locations.ALL_COLUMNS,
					Locations.LOCATION_FINALIZED + " = 'true' AND "
							+ Locations.PURPOSE + " = "
							+ Locations.PURPOSE_NOTE + " AND "
							+ Locations.FOR_ID + " = " + localNoteId, null,
					null, null, null);

			if (cursor.moveToNext()) {
				locationDto = new LocationDto();
				locationDto.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return locationDto;
	}

	public LocationDto getHistoryLocation(long localHistoryId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		LocationDto locationDto = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, Locations.ALL_COLUMNS,
					Locations.LOCATION_FINALIZED + " = 'true' AND "
							+ Locations.PURPOSE + " = "
							+ Locations.PURPOSE_HISTORY + " AND "
							+ Locations.FOR_ID + " = " + localHistoryId, null,
					null, null, null);

			if (cursor.moveToNext()) {
				locationDto = new LocationDto();
				locationDto.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return locationDto;
	}

	public LocationDto getFormLocation(long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		LocationDto locationDto = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, Locations.ALL_COLUMNS,
					Locations.LOCATION_FINALIZED + " = 'true' AND "
							+ Locations.PURPOSE + " = "
							+ Locations.PURPOSE_FORM + " AND "
							+ Locations.FOR_ID + " = " + localFormId, null,
					null, null, Locations._ID + " DESC", "1");

			if (cursor.moveToNext()) {
				locationDto = new LocationDto();
				locationDto.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return locationDto;
	}

	public LocationDto getLocation(int purposeLocation, long forId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		LocationDto locationDto = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, Locations.ALL_COLUMNS,
					Locations.LOCATION_FINALIZED + " = 'true' AND "
							+ Locations.PURPOSE + " = " + purposeLocation
							+ " AND " + Locations.FOR_ID + " = " + forId, null,
					null, null, Locations._ID + " DESC", "1");

			if (cursor.moveToNext()) {
				locationDto = new LocationDto();
				locationDto.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return locationDto;
	}

	public boolean isLocationRequestExists(int purposeLocation, long forId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Locations._ID
					+ ") AS count FROM " + Locations.TABLE + " WHERE "
					+ Locations.PURPOSE + " = " + purposeLocation + " AND "
					+ Locations.FOR_ID + " = " + forId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Date getLocalCreationTimeWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Locations.LOCAL_CREATION_TIME
					+ " FROM " + Locations.TABLE + " WHERE " + Locations._ID
					+ " = " + localId, null);

			cursor.moveToNext();

			return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean hasUnfinalizedGpsLocations() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, new String[] { Locations._ID },
					Locations.LOCATION_FINALIZED + " = 'false' AND "
							+ Locations.GPS_FINALIZED + " = 'false'", null,
					null, null, null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean hasUnfinalizedFusedLocations() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, new String[] { Locations._ID },
					Locations.LOCATION_FINALIZED + " = 'false' AND "
							+ Locations.FUSED_FINALIZED + " = 'false'", null,
					null, null, null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 
	 * @param fixTime
	 *            if null, returns all unfinalized locations (without
	 *            considering time)
	 * @return null if no matches found
	 */
	public List<LocationDto> getUnfinalizedGpsLocations(String fixTime) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		List<LocationDto> locations = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, Locations.ALL_COLUMNS,
					Locations.LOCATION_FINALIZED
							+ " = 'false' AND "
							+ Locations.GPS_FINALIZED
							+ " = 'false'"
							+ (fixTime == null ? "" : " AND "
									+ Locations.LOCAL_CREATION_TIME + " <= '"
									+ fixTime + "'"), null, null, null, null);

			if (cursor.getCount() > 0) {
				locations = new ArrayList<LocationDto>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				LocationDto locationDto = new LocationDto();
				locationDto.load(cursor);
				locations.add(locationDto);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return locations;
	}

	/**
	 * 
	 * @param fixTime
	 *            if null, returns all unfinalized locations (without
	 *            considering time)
	 * @return null if no matches found
	 */
	public List<LocationDto> getUnfinalizedFusedLocations(String fixTime) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		List<LocationDto> locations = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, Locations.ALL_COLUMNS,
					Locations.LOCATION_FINALIZED
							+ " = 'false' AND "
							+ Locations.FUSED_FINALIZED
							+ " = 'false'"
							+ (fixTime == null ? "" : " AND "
									+ Locations.LOCAL_CREATION_TIME + " <= '"
									+ fixTime + "'"), null, null, null, null);

			if (cursor.getCount() > 0) {
				locations = new ArrayList<LocationDto>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				LocationDto locationDto = new LocationDto();
				locationDto.load(cursor);
				locations.add(locationDto);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return locations;
	}

	public boolean hasUnfinalizedNetworkLocations() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, new String[] { Locations._ID },
					Locations.LOCATION_FINALIZED + " = 'false' AND "
							+ Locations.NETWORK_FINALIZED + " = 'false'", null,
					null, null, null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * @param fixTime
	 *            if null, returns all unfinalized locations (without
	 *            considering time)
	 * @return null if no matches found
	 */
	public List<LocationDto> getUnfinalizedNetworkLocations(String fixTime) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		List<LocationDto> locations = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, Locations.ALL_COLUMNS,
					Locations.LOCATION_FINALIZED
							+ " = 'false' AND "
							+ Locations.NETWORK_FINALIZED
							+ " = 'false'"
							+ (fixTime == null ? "" : " AND "
									+ Locations.LOCAL_CREATION_TIME + " <= '"
									+ fixTime + "'")

					, null, null, null, null);

			if (cursor.getCount() > 0) {
				locations = new ArrayList<LocationDto>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				LocationDto locationDto = new LocationDto();
				locationDto.load(cursor);
				locations.add(locationDto);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return locations;
	}

	public synchronized int finalizeTimedOutLocations() {
		SettingsDao settingsDao = SettingsDao.getInstance(applicationContext);
		int timeout = settingsDao.getInt(
				EffortProvider.Settings.KEY_COMMENT_LOCATION_TIMEOUT,
				EffortProvider.Settings.DEFAULT_COMMENT_LOCATION_TIMEOUT);

		Calendar cal = Calendar.getInstance();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Current time: " + cal.getTime().toString()
					+ ", millis=" + cal.getTimeInMillis());
		}

		cal.add(Calendar.MILLISECOND, 0 - timeout);

		if (BuildConfig.DEBUG) {
			Log.d(TAG,
					"Finalizing locations before: " + cal.getTime().toString()
							+ ", millis=" + cal.getTimeInMillis());
		}

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Locations.LOCATION_FINALIZED, "true");
		values.put(Locations.LOCAL_MODIFICATION_TIME,
				SQLiteDateTimeUtils.getCurrentSQLiteDateTime());

		String time = SQLiteDateTimeUtils.getSQLiteDateTimeFromLocalMillis(cal
				.getTimeInMillis());
		
		// TODO we will get crash when employee mapping changes
		return db.update(Locations.TABLE, values, Locations.LOCAL_CREATION_TIME
				+ " <= ? AND " + Locations.LOCATION_FINALIZED + " = ?",
				new String[] { time, "false" });
	}

	/**
	 * Delete location with the given location ID
	 * 
	 * @param locationId
	 */
	public synchronized void deleteLocation(long locationId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		// delete neighboring cell infos first
		NeighboringCellInfosDao cellInfosDao = NeighboringCellInfosDao
				.getInstance(applicationContext);
		cellInfosDao.deleteNeighboringCellInfos(locationId);

		int affectedRows = db.delete(Locations.TABLE, Locations._ID + " = "
				+ locationId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows
					+ " location with location id " + locationId);
		}
	}

	/**
	 * Delete locations of the given local note ID
	 * 
	 * @param localNoteId
	 */
	public synchronized void deleteNoteLocations(long localNoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, new String[] { Locations._ID },
					Locations.PURPOSE + " = " + Locations.PURPOSE_NOTE
							+ " AND " + Locations.FOR_ID + " = " + localNoteId,
					null, null, null, null);

			while (cursor.moveToNext()) {
				deleteLocation(cursor.getLong(0));
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Deleted " + cursor.getCount()
						+ " locations of note " + localNoteId);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Delete locations of the given local history ID
	 * 
	 * @param localHistoryId
	 */
	public synchronized void deleteHistoryLocations(long localHistoryId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, new String[] { Locations._ID },
					Locations.PURPOSE + " = " + Locations.PURPOSE_HISTORY
							+ " AND " + Locations.FOR_ID + " = "
							+ localHistoryId, null, null, null, null);

			while (cursor.moveToNext()) {
				deleteLocation(cursor.getLong(0));
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Deleted " + cursor.getCount()
						+ " locations of history " + localHistoryId);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Delete locations of the given assignRoouteId
	 * 
	 * @param assignRouteId
	 */
	public synchronized void deleteAssignedRouteLocations(long assignRouteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, new String[] { Locations._ID },
					Locations.PURPOSE + " = "
							+ Locations.PURPOSE_COMPLETE_ROUTE_PLAN + " AND "
							+ Locations.FOR_ID + " = " + assignRouteId, null,
					null, null, null);

			while (cursor.moveToNext()) {
				deleteLocation(cursor.getLong(0));
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Deleted " + cursor.getCount()
						+ " locations of history " + assignRouteId);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Delete locations of the given local form ID
	 * 
	 * @param localFormId
	 */
	public synchronized void deleteFormLocations(long localFormId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, new String[] { Locations._ID },
					Locations.PURPOSE + " = " + Locations.PURPOSE_FORM
							+ " AND " + Locations.FOR_ID + " = " + localFormId,
					null, null, null, null);

			while (cursor.moveToNext()) {
				deleteLocation(cursor.getLong(0));
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Deleted " + cursor.getCount()
						+ " locations of form " + localFormId);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasUnsyncedTracks() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, new String[] { Locations._ID },
					Locations.PURPOSE + " IN(" + Locations.PURPOSE_TRACKING
							+ ", " + Locations.PURPOSE_START_WORK + ", "
							+ Locations.PURPOSE_STOP_WORK + ") AND "
							+ Locations.LOCATION_FINALIZED + " = 'true' AND "
							+ Locations.DIRTY + " = 'true' AND "
							+ Locations.SMS_PROCESS_STATE + " = 'false'", null,
					null, null, null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasUnsyncedNonTracks() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, new String[] { Locations._ID },
					Locations.PURPOSE + " NOT IN(" + Locations.PURPOSE_TRACKING
							+ ", " + Locations.PURPOSE_START_WORK + ", "
							+ Locations.PURPOSE_STOP_WORK + ")" + " AND "
							+ Locations.LOCATION_FINALIZED + " = 'true' AND "
							+ Locations.DIRTY + " = 'true'", null, null, null,
					null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 
	 * @return null if there are no unsynced tracks
	 */
	public List<LocationDto> getUnsyncedTracks() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<LocationDto> locations = null;

		try {
			cursor = db.query(Locations.TABLE, Locations.ALL_COLUMNS,
					Locations.PURPOSE + " IN(" + Locations.PURPOSE_TRACKING
							+ ", " + Locations.PURPOSE_START_WORK + ", "
							+ Locations.PURPOSE_STOP_WORK + ") AND "
							+ Locations.LOCATION_FINALIZED + " = 'true' AND "
							+ Locations.DIRTY + " = 'true' AND "
							+ Locations.SMS_PROCESS_STATE + " = 'false'", null,
					null, null, Locations.LOCAL_CREATION_TIME, "50");

			if (cursor.getCount() > 0) {
				locations = new ArrayList<LocationDto>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				LocationDto location = new LocationDto();
				location.load(cursor);
				locations.add(location);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return locations;
	}

	/**
	 * 
	 * @return null if there are no unsynced tracks
	 */
	public LocationDto getLatestUnsyncedTrack() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		LocationDto location = null;
		try {
			cursor = db.query(Locations.TABLE, Locations.ALL_COLUMNS,
					Locations.PURPOSE + " = " + Locations.PURPOSE_TRACKING
							+ " AND " + Locations.LOCATION_FINALIZED
							+ " = 'true' AND " + Locations.DIRTY + " = 'true'",
					null, null, null, Locations.LOCAL_CREATION_TIME + " DESC ",
					"1");

			while (cursor.moveToNext()) {
				location = new LocationDto();
				location.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return location;
	}

	public synchronized void finalizeGpsLocations() {
		List<LocationDto> locations = getUnfinalizedGpsLocations(null);

		if (locations != null) {
			for (LocationDto location : locations) {
				location.setGpsFinalized(true);

				if (location.getNetworkFinalized()) {
					location.setLocationFinalized(true);
				}

				save(location);
			}
		}
	}

	public synchronized void finalizeNetworkLocations() {
		List<LocationDto> locations = getUnfinalizedNetworkLocations(null);

		if (locations != null) {
			for (LocationDto location : locations) {
				location.setNetworkFinalized(true);

				if (location.getGpsFinalized()) {
					location.setLocationFinalized(true);
				}

				save(location);
			}
		}
	}

	/**
	 */
	public synchronized void deleteSyncedLocations() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, new String[] { Locations._ID },
					Locations.LOCATION_FINALIZED + " = 'true' AND "
							+ Locations.DIRTY + " = 'false'", null, null, null,
					null);

			while (cursor.moveToNext()) {
				deleteLocation(cursor.getLong(0));
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Deleted " + cursor.getCount() + " synced locations");
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 
	 * @param localNoteId
	 * @param dirty
	 * @return number of affected rows
	 */
	public synchronized int updateDirtyFlag(long localNoteId, boolean dirty) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Locations.DIRTY, "" + dirty);

		return db.update(Locations.TABLE, values, Locations.PURPOSE + " = "
				+ Locations.PURPOSE_NOTE + " AND " + Locations.FOR_ID + " = "
				+ localNoteId, null);
	}

	/**
	 * 
	 * @param localNoteId
	 * @param dirty
	 * @return number of affected rows
	 */
	public synchronized int updateLocationDirtyFlag(long locationId,
			boolean dirty) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Locations.DIRTY, "" + dirty);

		return db.update(Locations.TABLE, values, Locations._ID + " = "
				+ locationId, null);
	}

	/**
	 * 
	 * @param localNoteId
	 * @param dirty
	 * @return number of affected rows
	 */
	public synchronized int updateRemoteLocationId(long localFormId,
			long remoteLocationId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Locations.REMOTE_LOCATION_ID, remoteLocationId);

		return db.update(Locations.TABLE, values, Locations.PURPOSE + " = "
				+ Locations.PURPOSE_FORM + " AND " + Locations.FOR_ID + " = "
				+ localFormId + " AND " + Locations.REMOTE_LOCATION_ID
				+ " IS NULL", null);
	}

	/**
	 * 
	 * @param localNoteId
	 * @param state
	 * @return number of affected rows
	 */
	public synchronized int updateSmsProcessFlag(long locationId, boolean state) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Locations.SMS_PROCESS_STATE, "" + state);

		return db.update(Locations.TABLE, values, Locations._ID + " = "
				+ locationId, null);
	}

	/**
	 * Delete nearby location triggers
	 * 
	 * @param localFormId
	 */
	public synchronized void deleteNearbyLocations() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, new String[] { Locations._ID },
					Locations.PURPOSE + " = "
							+ Locations.PURPOSE_NEARBY_CUSTOMERS, null, null,
					null, null);

			while (cursor.moveToNext()) {
				deleteLocation(cursor.getLong(0));
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Deleted " + cursor.getCount() + " locations");
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Delete locations of the given purpose, forId
	 * 
	 * @param purpose
	 *            , forId
	 */
	public synchronized void deleteLocation(int purpose, long forId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Locations.TABLE, new String[] { Locations._ID },
					Locations.PURPOSE + " = " + purpose + " AND "
							+ Locations.FOR_ID + " = " + forId, null, null,
					null, null);

			while (cursor.moveToNext()) {
				deleteLocation(cursor.getLong(0));
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Deleted " + cursor.getCount() + " locations for "
						+ forId);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
}

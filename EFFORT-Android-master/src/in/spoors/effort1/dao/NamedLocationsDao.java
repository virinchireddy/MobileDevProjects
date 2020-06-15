package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.NamedLocation;
import in.spoors.effort1.provider.EffortProvider.NamedLocations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NamedLocationsDao {

	public static final String TAG = "NamedLocationsDao";
	private static NamedLocationsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static NamedLocationsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new NamedLocationsDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private NamedLocationsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean locationWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + NamedLocations._ID
					+ ") AS count FROM " + NamedLocations.TABLE + " WHERE "
					+ NamedLocations._ID + " = " + localId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean locationWithRemoteIdExists(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + NamedLocations.REMOTE_ID
					+ ") AS count FROM " + NamedLocations.TABLE + " WHERE "
					+ NamedLocations.REMOTE_ID + " = " + remoteId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Saves the given job.
	 * 
	 * Local ID of the job is updated with the inserted ID.
	 * 
	 * @param namedLocation
	 */
	public synchronized void save(NamedLocation namedLocation) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		if (namedLocation.getLocalId() != null
				&& locationWithLocalIdExists(namedLocation.getLocalId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting named location.");
			}

			namedLocation.setLocalModificationTime(new Date());
			namedLocation
					.setLocalCreationTime(getLocalCreationTimeWithLocalId(namedLocation
							.getLocalId()));

			ContentValues values = namedLocation.getContentValues(null);

			db.update(NamedLocations.TABLE, values, NamedLocations._ID + " = "
					+ namedLocation.getLocalId(), null);

			return;
		}

		if (namedLocation.getRemoteId() != null
				&& locationWithRemoteIdExists(namedLocation.getRemoteId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting named location.");
			}

			namedLocation.setLocalModificationTime(new Date());
			namedLocation
					.setLocalCreationTime(getLocalCreationTimeWithRemoteId(namedLocation
							.getRemoteId()));

			ContentValues values = namedLocation.getContentValues(null);

			db.update(NamedLocations.TABLE, values, NamedLocations.REMOTE_ID
					+ " = " + namedLocation.getRemoteId(), null);

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new named location.");
		}

		Date now = new Date();
		namedLocation.setLocalCreationTime(now);
		namedLocation.setLocalModificationTime(now);

		ContentValues values = namedLocation.getContentValues(null);
		long insertedId = db.insert(NamedLocations.TABLE, null, values);
		namedLocation.setLocalId(insertedId);
	}

	/**
	 * Returns the local named location id of the given remote named location
	 * id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Long getLocalId(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + NamedLocations._ID + " FROM "
					+ NamedLocations.TABLE + " WHERE "
					+ NamedLocations.REMOTE_ID + " = " + remoteId, null);

			if (cursor.moveToNext() && !cursor.isNull(0)) {
				return cursor.getLong(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Returns the remote named location id of the given local named location
	 * id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Long getRemoteId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + NamedLocations.REMOTE_ID
					+ " FROM " + NamedLocations.TABLE + " WHERE "
					+ NamedLocations._ID + " = " + localId, null);

			if (cursor.moveToNext() && !cursor.isNull(0)) {
				return cursor.getLong(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Returns the local creation time of the given local named location id.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithLocalId(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + NamedLocations.LOCAL_CREATION_TIME
					+ " FROM " + NamedLocations.TABLE + " WHERE "
					+ NamedLocations._ID + " = " + localId, null);

			if (cursor.moveToNext()) {
				return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
			} else {
				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Returns the local creation time of the given remote named location id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithRemoteId(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + NamedLocations.LOCAL_CREATION_TIME
					+ " FROM " + NamedLocations.TABLE + " WHERE "
					+ NamedLocations.REMOTE_ID + " = " + remoteId, null);

			if (cursor.moveToNext()) {
				return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
			} else {
				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public void deleteLocationWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(NamedLocations.TABLE, NamedLocations._ID
				+ " = ?", new String[] { "" + localId });

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted named location with local location id: "
					+ localId + ", affectedRows=" + affectedRows);
		}
	}

	public void deleteLocationWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(NamedLocations.TABLE,
				NamedLocations.REMOTE_ID + " = ?",
				new String[] { "" + remoteId });

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted named location with remote location id: "
					+ remoteId + ", affectedRows=" + affectedRows);
		}
	}

	public void deletePartialLocations() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(NamedLocations.TABLE,
				NamedLocations.PARTIAL + " = 'true'", null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " partial locations.");
		}
	}

	public NamedLocation getLocationWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		NamedLocation location = null;
		Cursor cursor = null;

		try {
			cursor = db.query(NamedLocations.TABLE, NamedLocations.ALL_COLUMNS,
					NamedLocations._ID + " = " + localId, null, null, null,
					null);

			if (cursor.moveToNext()) {
				location = new NamedLocation();
				location.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return location;
	}

	public NamedLocation getLocationWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		NamedLocation location = null;
		Cursor cursor = null;

		try {
			cursor = db.query(NamedLocations.TABLE, NamedLocations.ALL_COLUMNS,
					NamedLocations.REMOTE_ID + " = " + remoteId, null, null,
					null, null);

			if (cursor.moveToNext()) {
				location = new NamedLocation();
				location.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return location;
	}

	public String getNameWithLocalId(long localNamedLocationId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		String name = null;
		Cursor cursor = null;

		try {
			cursor = db.query(NamedLocations.TABLE,
					new String[] { NamedLocations.NAME }, NamedLocations._ID
							+ " = " + localNamedLocationId, null, null, null,
					null);

			if (cursor.moveToNext()) {
				name = cursor.getString(0);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return name;
	}

	/**
	 * Get the customers that are added on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<NamedLocation> getAddedLocations() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<NamedLocation> locations = null;

		try {
			cursor = db.query(NamedLocations.TABLE, NamedLocations.ALL_COLUMNS,
					NamedLocations.REMOTE_ID + " IS NULL", null, null, null,
					NamedLocations.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				locations = new ArrayList<NamedLocation>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				NamedLocation location = new NamedLocation();
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
	 * Get the customers that are modified on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<NamedLocation> getModifiedLocations() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<NamedLocation> locations = null;

		try {
			cursor = db.query(NamedLocations.TABLE, NamedLocations.ALL_COLUMNS,
					NamedLocations.REMOTE_ID + " IS NOT NULL AND "
							+ NamedLocations.DIRTY + " = 'true'", null, null,
					null, NamedLocations.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				locations = new ArrayList<NamedLocation>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				NamedLocation location = new NamedLocation();
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
	 * @return
	 */
	public boolean hasUnsyncedChanges() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(NamedLocations.TABLE,
					new String[] { NamedLocations._ID },
					NamedLocations.REMOTE_ID + " IS NULL OR "
							+ NamedLocations.DIRTY + " = 'true'", null, null,
					null, null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Get all the remote named location IDs.
	 * 
	 * @return
	 */
	public List<Long> getAllRemoteLocationIds() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Long> ids = null;

		try {
			cursor = db.query(NamedLocations.TABLE,
					new String[] { NamedLocations.REMOTE_ID },
					NamedLocations.REMOTE_ID + " IS NOT NULL", null, null,
					null, NamedLocations.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				ids = new ArrayList<Long>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				ids.add(cursor.getLong(0));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return ids;
	}

	public synchronized void updateLocationIdMapping(long localLocationId,
			long remoteLocationId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		db.execSQL("UPDATE " + NamedLocations.TABLE + " SET "
				+ NamedLocations.REMOTE_ID + " = " + remoteLocationId + ", "
				+ NamedLocations.DIRTY + " = 'false' WHERE "
				+ NamedLocations._ID + " = " + localLocationId);
	}

	public synchronized void updateDirtyFlag(boolean dirty,
			long remoteLocationId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		db.execSQL("UPDATE " + NamedLocations.TABLE + " SET "
				+ NamedLocations.DIRTY + " = '" + dirty + "' WHERE "
				+ NamedLocations.REMOTE_ID + " = " + remoteLocationId);
	}

	public synchronized void updatePartialFlag(boolean partial,
			long localLocationId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		db.execSQL("UPDATE " + NamedLocations.TABLE + " SET "
				+ NamedLocations.PARTIAL + " = '" + partial + "' WHERE "
				+ NamedLocations._ID + " = " + localLocationId);
	}

	public List<NamedLocation> getLocationsWithCoordinates() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<NamedLocation> locations = new ArrayList<NamedLocation>();

		try {
			cursor = db.query(NamedLocations.TABLE, NamedLocations.ALL_COLUMNS,
					NamedLocations.LATITUDE + " IS NOT NULL AND "
							+ NamedLocations.LONGITUDE + " IS NOT NULL", null,
					null, null, NamedLocations.LOCAL_CREATION_TIME);

			while (cursor.moveToNext()) {
				NamedLocation location = new NamedLocation();
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
	 * Get the customers with remote IDs.
	 * 
	 * @return
	 */
	public List<NamedLocation> getLocationsWithRemoteIds(String remoteIds) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<NamedLocation> locations = new ArrayList<NamedLocation>();

		try {
			cursor = db.query(NamedLocations.TABLE, NamedLocations.ALL_COLUMNS,
					NamedLocations.REMOTE_ID + " IN (" + remoteIds + ")", null,
					null, null, NamedLocations.NAME);

			while (cursor.moveToNext()) {
				NamedLocation location = new NamedLocation();
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

}

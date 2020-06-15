package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.NeighboringCellInfoDto;
import in.spoors.effort1.provider.EffortProvider.NeighboringCellInfos;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NeighboringCellInfosDao {

	public static final String TAG = "NeighboringCellInfosDao";
	private static NeighboringCellInfosDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static NeighboringCellInfosDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new NeighboringCellInfosDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private NeighboringCellInfosDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(NeighboringCellInfoDto cellInfoDto) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Saving a new neighboring cell info: "
							+ cellInfoDto.toString());
		}

		ContentValues values = cellInfoDto.getContentValues(null);
		long insertedId = db.insert(NeighboringCellInfos.TABLE, null, values);
		cellInfoDto.setLocalId(insertedId);
	}

	/**
	 * Delete neighboring cell infos of the given location ID
	 * 
	 * @param locationId
	 */
	public synchronized void deleteNeighboringCellInfos(long locationId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		int affectedRows = db.delete(NeighboringCellInfos.TABLE,
				NeighboringCellInfos.LOCATION_ID + " = " + locationId, null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows
					+ " neighboring cell infos of location " + locationId);
		}
	}

	/**
	 * 
	 * @return null if there are no neighboring cells of the given location ID.
	 */
	public List<NeighboringCellInfoDto> getNeighboringCellInfos(long locationId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<NeighboringCellInfoDto> cells = null;

		try {
			cursor = db.query(NeighboringCellInfos.TABLE,
					NeighboringCellInfos.ALL_COLUMNS,
					NeighboringCellInfos.LOCATION_ID + " = " + locationId,
					null, null, null, null);

			if (cursor.getCount() > 0) {
				cells = new ArrayList<NeighboringCellInfoDto>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				NeighboringCellInfoDto cell = new NeighboringCellInfoDto();
				cell.load(cursor);
				cells.add(cell);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return cells;
	}

}

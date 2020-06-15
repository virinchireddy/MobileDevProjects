package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.NeighboringCellInfos;

import java.io.Serializable;

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
public class NeighboringCellInfoDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final String TAG = "Leave";

	public static final String JSON_CELL_ID = "cellId";
	public static final String JSON_CELL_LAC = "lac";
	public static final String JSON_CELL_MCC = "mcc";
	public static final String JSON_CELL_MNC = "mnc";
	public static final String JSON_CELL_RSSI = "signalStrength";

	private Long localId;

	private Integer cellId;
	private Integer cellLac;
	private Integer cellPsc;
	private Integer cellMcc;
	private Integer cellMnc;
	private Integer cellRssi;
	private Long locationId;

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
			values.put(NeighboringCellInfos._ID, localId);
		}

		Utils.putNullOrValue(values, NeighboringCellInfos.CELL_ID, cellId);
		Utils.putNullOrValue(values, NeighboringCellInfos.CELL_LAC, cellLac);
		Utils.putNullOrValue(values, NeighboringCellInfos.CELL_PSC, cellPsc);
		Utils.putNullOrValue(values, NeighboringCellInfos.CELL_RSSI, cellRssi);
		Utils.putNullOrValue(values, NeighboringCellInfos.CELL_MCC, cellMcc);
		Utils.putNullOrValue(values, NeighboringCellInfos.CELL_MNC, cellMnc);
		Utils.putNullOrValue(values, NeighboringCellInfos.LOCATION_ID,
				locationId);

		return values;
	}

	public void load(Cursor cursor) {
		localId = cursor.isNull(NeighboringCellInfos._ID_INDEX) ? null : cursor
				.getLong(NeighboringCellInfos._ID_INDEX);

		cellId = cursor.isNull(NeighboringCellInfos.CELL_ID_INDEX) ? null
				: cursor.getInt(NeighboringCellInfos.CELL_ID_INDEX);
		cellLac = cursor.isNull(NeighboringCellInfos.CELL_LAC_INDEX) ? null
				: cursor.getInt(NeighboringCellInfos.CELL_LAC_INDEX);
		cellPsc = cursor.isNull(NeighboringCellInfos.CELL_PSC_INDEX) ? null
				: cursor.getInt(NeighboringCellInfos.CELL_PSC_INDEX);
		cellMcc = cursor.isNull(NeighboringCellInfos.CELL_MCC_INDEX) ? null
				: cursor.getInt(NeighboringCellInfos.CELL_MCC_INDEX);
		cellMnc = cursor.isNull(NeighboringCellInfos.CELL_MNC_INDEX) ? null
				: cursor.getInt(NeighboringCellInfos.CELL_MNC_INDEX);
		cellRssi = cursor.isNull(NeighboringCellInfos.CELL_RSSI_INDEX) ? null
				: cursor.getInt(NeighboringCellInfos.CELL_RSSI_INDEX);

		locationId = cursor.isNull(NeighboringCellInfos.LOCATION_ID_INDEX) ? null
				: cursor.getLong(NeighboringCellInfos.LOCATION_ID_INDEX);
	}

	public Long getLocalId() {
		return localId;
	}

	public void setLocalId(Long localId) {
		this.localId = localId;
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

	public Integer getCellRssi() {
		return cellRssi;
	}

	public void setCellRssi(Integer cellRssi) {
		this.cellRssi = cellRssi;
	}

	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
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

		try {
			Utils.putValueOnlyIfNotNull(json, JSON_CELL_ID, cellId);
			Utils.putValueOnlyIfNotNull(json, JSON_CELL_LAC, cellLac);
			Utils.putValueOnlyIfNotNull(json, JSON_CELL_MCC, cellMcc);
			Utils.putValueOnlyIfNotNull(json, JSON_CELL_MNC, cellMnc);
			Utils.putValueOnlyIfNotNull(json, JSON_CELL_RSSI, cellRssi);
		} catch (JSONException e) {
			Log.e(TAG,
					"Failed to compose JSON for neighboring cell info: "
							+ e.toString(), e);
			return null;
		}

		return json;
	}

	@Override
	public String toString() {
		return "NeighboringCellInfoDto [localId=" + localId + ", cellId="
				+ cellId + ", cellLac=" + cellLac + ", cellPsc=" + cellPsc
				+ ", cellMcc=" + cellMcc + ", cellMnc=" + cellMnc
				+ ", cellRssi=" + cellRssi + ", locationId=" + locationId + "]";
	}

}

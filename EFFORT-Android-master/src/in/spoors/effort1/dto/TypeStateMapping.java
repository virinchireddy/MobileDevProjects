package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.TypeStateMappings;

import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class TypeStateMapping implements Comparable<TypeStateMapping> {

	public static final String JSON_TYPE_ID = "visitTypeId";
	public static final String JSON_STATE_ID = "visitStateId";
	public static final String JSON_DISPLY_ORDER = "order";

	private long id;
	private long typeId;
	private long stateId;
	private long displayOrder;

	public TypeStateMapping() {
	}

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required for creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static TypeStateMapping parse(JSONObject json,
			Context applicationContext) throws JSONException, ParseException {
		TypeStateMapping mapping = new TypeStateMapping();

		// Id is mandatory. Thus, null check is not required.
		mapping.typeId = json.getLong(JSON_TYPE_ID);
		mapping.stateId = json.getLong(JSON_STATE_ID);
		mapping.displayOrder = json.getLong(JSON_DISPLY_ORDER);

		return mapping;
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

		Utils.putNullOrValue(values, TypeStateMappings.TYPE_ID, typeId);
		Utils.putNullOrValue(values, TypeStateMappings.STATE_ID, stateId);
		Utils.putNullOrValue(values, TypeStateMappings.DISPLAY_ORDER,
				displayOrder);

		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.getLong(TypeStateMappings._ID_INDEX);
		typeId = cursor.getLong(TypeStateMappings.TYPE_ID_INDEX);
		stateId = cursor.getLong(TypeStateMappings.STATE_ID_INDEX);
		displayOrder = cursor.getLong(TypeStateMappings.DISPLAY_ORDER_INDEX);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTypeId() {
		return typeId;
	}

	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}

	public long getStateId() {
		return stateId;
	}

	public void setStateId(long stateId) {
		this.stateId = stateId;
	}

	public long getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(long displayOrder) {
		this.displayOrder = displayOrder;
	}

	@Override
	public String toString() {
		return "TypeStateMapping [id=" + id + ", typeId=" + typeId
				+ ", stateId=" + stateId + ", displayOrder=" + displayOrder
				+ "]";
	}

	public String toStringWithoutId() {
		return "TypeStateMapping [typeId=" + typeId + ", stateId=" + stateId
				+ ", displayOrder=" + displayOrder + "]";
	}

	@Override
	public int compareTo(TypeStateMapping mapping) {
		return toStringWithoutId().compareTo(mapping.toStringWithoutId());
	}

}
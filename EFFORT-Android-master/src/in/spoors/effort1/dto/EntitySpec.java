package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.EntitySpecs;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class EntitySpec implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "EntitySpec";

	private Long id;
	private String title;
	private Boolean withdrawn;

	public static final String JSON_ID = "entitySpecId";
	public static final String JSON_TITLE = "entityTitle";
	public static final String JSON_WITHDRAWN = "deleted";

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static EntitySpec parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		EntitySpec spec = new EntitySpec();

		spec.id = Utils.getLong(json, JSON_ID);
		spec.title = Utils.getString(json, JSON_TITLE);
		spec.withdrawn = Utils.getBoolean(json, JSON_WITHDRAWN);

		return spec;
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
		if (id != null) {
			values.put(EntitySpecs._ID, id);
		}

		Utils.putNullOrValue(values, EntitySpecs.TITLE, title);
		Utils.putNullOrValue(values, EntitySpecs.WITHDRAWN, withdrawn);

		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.isNull(EntitySpecs._ID_INDEX) ? null : cursor
				.getLong(EntitySpecs._ID_INDEX);
		title = cursor.getString(EntitySpecs.TITLE_INDEX);
		withdrawn = cursor.isNull(EntitySpecs.WITHDRAWN_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(EntitySpecs.WITHDRAWN_INDEX));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getWithdrawn() {
		return withdrawn;
	}

	public void setWithdrawn(Boolean withdrawn) {
		this.withdrawn = withdrawn;
	}

	@Override
	public String toString() {
		return "EntitySpec [id=" + id + ", title=" + title + ", withdrawn="
				+ withdrawn + "]";
	}

}

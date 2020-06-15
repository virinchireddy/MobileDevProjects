package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.FormSpecs;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class FormSpec implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "FormSpec";

	private Long id;
	private String title;
	private Boolean withdrawn;
	private Boolean visible;
	private String uniqueId;
	private String printTemplate;

	public static final String JSON_ID = "formSpecId";
	public static final String JSON_TITLE = "formTitle";
	public static final String JSON_WITHDRAWN = "deleted";
	public static final String JSON_UNIQUE_ID = "uniqueId";
	public static final String JSON_PRINT_TEMPLATE = "mobilePrintTemplate";

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static FormSpec parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		FormSpec formSpec = new FormSpec();

		formSpec.id = Utils.getLong(json, JSON_ID);
		formSpec.title = Utils.getString(json, JSON_TITLE);
		formSpec.withdrawn = Utils.getBoolean(json, JSON_WITHDRAWN);
		formSpec.uniqueId = Utils.getString(json, JSON_UNIQUE_ID);
		formSpec.printTemplate = Utils.getString(json, JSON_PRINT_TEMPLATE);

		return formSpec;
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
			values.put(FormSpecs._ID, id);
		}

		Utils.putNullOrValue(values, FormSpecs.TITLE, title);
		Utils.putNullOrValue(values, FormSpecs.WITHDRAWN, withdrawn);
		Utils.putNullOrValue(values, FormSpecs.VISIBLE, visible);
		Utils.putNullOrValue(values, FormSpecs.UNIQUE_ID, uniqueId);
		Utils.putNullOrValue(values, FormSpecs.PRINT_TEMPLATE, printTemplate);

		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.isNull(FormSpecs._ID_INDEX) ? null : cursor
				.getLong(FormSpecs._ID_INDEX);
		title = cursor.getString(FormSpecs.TITLE_INDEX);
		withdrawn = cursor.isNull(FormSpecs.WITHDRAWN_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(FormSpecs.WITHDRAWN_INDEX));
		visible = cursor.isNull(FormSpecs.VISIBLE_INDEX) ? null : Boolean
				.parseBoolean(cursor.getString(FormSpecs.VISIBLE_INDEX));
		uniqueId = cursor.getString(FormSpecs.UNIQUE_ID_INDEX);
		printTemplate = cursor.getString(FormSpecs.PRINT_TEMPLATE_INDEX);
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

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public String getPrintTemplate() {
		return printTemplate;
	}

	public void setPrintTemplate(String printTemplate) {
		this.printTemplate = printTemplate;
	}

	@Override
	public String toString() {
		return "FormSpec [id=" + id + ", title=" + title + ", withdrawn="
				+ withdrawn + ", visible=" + visible + ", uniqueId=" + uniqueId
				+ ", printTemplate=" + printTemplate + "]";
	}

}

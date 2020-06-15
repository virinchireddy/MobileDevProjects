package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.SectionSpecs;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SectionSpec implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "SectionSpec";

	private Long id;
	private String title;
	private Long formSpecId;
	private Integer displayOrder;
	private Integer minEntries;
	private Integer maxEntries;
	private Integer pageId;

	public static final String JSON_ID = "sectionSpecId";
	public static final String JSON_TITLE = "sectionTitle";
	public static final String JSON_FORM_SPEC_ID = "formSpecId";
	public static final String JSON_DISPLAY_ORDER = "displayOrder";
	public static final String JSON_MIN_ENTRIES = "minEntries";
	public static final String JSON_MAX_ENTRIES = "maxEntries";
	public static final String JSON_PAGE_ID = "pageId";

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static SectionSpec parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		SectionSpec sectionSpec = new SectionSpec();

		sectionSpec.id = Utils.getLong(json, JSON_ID);
		sectionSpec.title = Utils.getString(json, JSON_TITLE);
		sectionSpec.formSpecId = Utils.getLong(json, JSON_FORM_SPEC_ID);
		sectionSpec.displayOrder = Utils.getInteger(json, JSON_DISPLAY_ORDER);
		sectionSpec.minEntries = Utils.getInteger(json, JSON_MIN_ENTRIES);
		sectionSpec.maxEntries = Utils.getInteger(json, JSON_MAX_ENTRIES);
		sectionSpec.pageId = Utils.getInteger(json, JSON_PAGE_ID);

		return sectionSpec;
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
			values.put(SectionSpecs._ID, id);
		}

		Utils.putNullOrValue(values, SectionSpecs.TITLE, title);
		Utils.putNullOrValue(values, SectionSpecs.FORM_SPEC_ID, formSpecId);
		Utils.putNullOrValue(values, SectionSpecs.DISPLAY_ORDER, displayOrder);
		Utils.putNullOrValue(values, SectionSpecs.MIN_ENTRIES, minEntries);
		Utils.putNullOrValue(values, SectionSpecs.MAX_ENTRIES, maxEntries);
		Utils.putNullOrValue(values, SectionSpecs.PAGE_ID, pageId);

		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.isNull(SectionSpecs._ID_INDEX) ? null : cursor
				.getLong(SectionSpecs._ID_INDEX);
		title = cursor.getString(SectionSpecs.TITLE_INDEX);
		formSpecId = cursor.isNull(SectionSpecs.FORM_SPEC_ID_INDEX) ? null
				: cursor.getLong(SectionSpecs.FORM_SPEC_ID_INDEX);
		displayOrder = cursor.isNull(SectionSpecs.DISPLAY_ORDER_INDEX) ? null
				: cursor.getInt(SectionSpecs.DISPLAY_ORDER_INDEX);
		minEntries = cursor.isNull(SectionSpecs.MIN_ENTRIES_INDEX) ? null
				: cursor.getInt(SectionSpecs.MIN_ENTRIES_INDEX);
		maxEntries = cursor.isNull(SectionSpecs.MAX_ENTRIES_INDEX) ? null
				: cursor.getInt(SectionSpecs.MAX_ENTRIES_INDEX);
		pageId = cursor.isNull(SectionSpecs.PAGE_ID_INDEX) ? null : cursor
				.getInt(SectionSpecs.PAGE_ID_INDEX);
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

	public Long getFormSpecId() {
		return formSpecId;
	}

	public void setFormSpecId(Long formSpecId) {
		this.formSpecId = formSpecId;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Integer getMinEntries() {
		return minEntries;
	}

	public void setMinEntries(Integer minEntries) {
		this.minEntries = minEntries;
	}

	public Integer getMaxEntries() {
		return maxEntries;
	}

	public void setMaxEntries(Integer maxEntries) {
		this.maxEntries = maxEntries;
	}

	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
	}

	@Override
	public String toString() {
		return "SectionSpec [id=" + id + ", title=" + title + ", formSpecId="
				+ formSpecId + ", displayOrder=" + displayOrder
				+ ", minEntries=" + minEntries + ", maxEntries=" + maxEntries
				+ ", pageId=" + pageId + "]";
	}

}

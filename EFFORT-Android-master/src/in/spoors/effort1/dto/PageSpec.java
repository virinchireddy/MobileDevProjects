package in.spoors.effort1.dto;

import in.spoors.effort1.Utils;
import in.spoors.effort1.provider.EffortProvider.PageSpecs;

import java.io.Serializable;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class PageSpec implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "PageSpec";

	private Long id;
	private Integer pageId;
	private String title;
	private Long formSpecId;

	public static final String JSON_PAGE_ID = "pageId";
	public static final String JSON_TITLE = "pageTitle";
	public static final String JSON_FORM_SPEC_ID = "formSpecId";

	/**
	 * 
	 * @param json
	 * @param applicationContext
	 *            Required creating DAO objects.
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static PageSpec parse(JSONObject json, Context applicationContext)
			throws JSONException, ParseException {
		PageSpec pageSpec = new PageSpec();

		pageSpec.pageId = Utils.getInteger(json, JSON_PAGE_ID);
		pageSpec.title = Utils.getString(json, JSON_TITLE);
		pageSpec.formSpecId = Utils.getLong(json, JSON_FORM_SPEC_ID);

		return pageSpec;
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

		Utils.putNullOrValue(values, PageSpecs.PAGE_ID, pageId);
		Utils.putNullOrValue(values, PageSpecs.TITLE, title);
		Utils.putNullOrValue(values, PageSpecs.FORM_SPEC_ID, formSpecId);

		return values;
	}

	public void load(Cursor cursor) {
		id = cursor.isNull(PageSpecs._ID_INDEX) ? null : cursor
				.getLong(PageSpecs._ID_INDEX);
		pageId = cursor.isNull(PageSpecs.PAGE_ID_INDEX) ? null : cursor
				.getInt(PageSpecs.PAGE_ID_INDEX);
		title = cursor.getString(PageSpecs.TITLE_INDEX);
		formSpecId = cursor.isNull(PageSpecs.FORM_SPEC_ID_INDEX) ? null
				: cursor.getLong(PageSpecs.FORM_SPEC_ID_INDEX);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPageId() {
		return pageId;
	}

	public void setPageId(Integer pageId) {
		this.pageId = pageId;
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

	@Override
	public String toString() {
		return "PageSpec [id=" + id + ", pageId=" + pageId + ", title=" + title
				+ ", formSpecId=" + formSpecId + "]";
	}

}

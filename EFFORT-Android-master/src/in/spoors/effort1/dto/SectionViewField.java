package in.spoors.effort1.dto;

import in.spoors.effort1.provider.EffortProvider.FieldsView;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

public class SectionViewField extends ViewField implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private static final String TAG = "SectionViewField";

	private Long sectionSpecId;
	private String sectionTitle;
	private Integer minEntries;
	private Integer maxEntries;
	private Integer sectionInstanceId;
	private LinkedHashMap<Integer, List<SectionViewField>> instanceFieldsMap;
	protected transient LinkedHashMap<Integer, View> instanceFieldsViewMap;
	private Boolean collapsed;

	public void load(Cursor cursor, Context applicationContext) {
		super.load(cursor, applicationContext);
		sectionSpecId = cursor.isNull(FieldsView.SECTION_SPEC_ID_INDEX) ? null
				: cursor.getLong(FieldsView.SECTION_SPEC_ID_INDEX);
		sectionInstanceId = cursor.isNull(FieldsView.SECTION_INSTANCE_ID_INDEX) ? null
				: cursor.getInt(FieldsView.SECTION_INSTANCE_ID_INDEX);
	}

	@Override
	public String toString() {
		return "SectionViewField [sectionSpecId=" + sectionSpecId
				+ ", sectionTitle=" + sectionTitle + ", minEntries="
				+ minEntries + ", maxEntries=" + maxEntries
				+ ", sectionInstanceId=" + sectionInstanceId
				+ ", instanceFieldsMap=" + instanceFieldsMap + ", collapsed="
				+ collapsed + "]";
	}

	public Long getSectionSpecId() {
		return sectionSpecId;
	}

	public void setSectionSpecId(Long sectionSpecId) {
		this.sectionSpecId = sectionSpecId;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
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

	public Integer getSectionInstanceId() {
		return sectionInstanceId;
	}

	public void setSectionInstanceId(Integer sectionInstanceId) {
		this.sectionInstanceId = sectionInstanceId;
	}

	public LinkedHashMap<Integer, List<SectionViewField>> getInstanceFieldsMap() {
		return instanceFieldsMap;
	}

	public void setInstanceFieldsMap(
			LinkedHashMap<Integer, List<SectionViewField>> instanceFieldsMap) {
		this.instanceFieldsMap = instanceFieldsMap;
	}

	public Boolean getCollapsed() {
		return collapsed;
	}

	public void setCollapsed(Boolean collapsed) {
		this.collapsed = collapsed;
	}

	public LinkedHashMap<Integer, View> getInstanceFieldsViewMap() {
		return instanceFieldsViewMap;
	}

	public void setInstanceFieldsViewMap(
			LinkedHashMap<Integer, View> instanceFieldsViewMap) {
		this.instanceFieldsViewMap = instanceFieldsViewMap;
	}

}
package in.spoors.effort1.dto;

import java.io.Serializable;
import java.util.List;

public class EntityFilterResultsDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String selector;
	String query;
	List<String> idList;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<String> getIdList() {
		return idList;
	}

	public void setIdList(List<String> idList) {
		this.idList = idList;
	}

	public String getSelector() {
		return selector;
	}

	public void setSelector(String selector) {
		this.selector = selector;
	}

}

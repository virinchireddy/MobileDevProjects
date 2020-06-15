package in.spoors.effort1.dto;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class AppVersion {

	public static final String JSON_VERSION = "version";
	public static final String JSON_URL = "url";
	public static final String JSON_MODIFIFIED_TIME = "modifiedTime";
	public static final String JSON_CHANGE_LOG = "changeLog";

	private String version;
	private String url;
	private String changeLog;
	private Date modifiedTime;

	public AppVersion() {
	}

	/**
	 * 
	 * @param json
	 * @return
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static AppVersion parse(JSONObject json) throws JSONException,
			ParseException {
		AppVersion appVersion = new AppVersion();

		appVersion.version = json.getString(JSON_VERSION);
		appVersion.url = json.getString(JSON_URL);
		appVersion.changeLog = json.getString(JSON_CHANGE_LOG);

		// TODO: don't parse modified time for now
		// server is not sending it in XSD date time format
		// appVersion.modifiedTime =

		return appVersion;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getChangeLog() {
		return changeLog;
	}

	public void setChangeLog(String changeLog) {
		this.changeLog = changeLog;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	@Override
	public String toString() {
		return "AppVersion [version=" + version + ", url=" + url
				+ ", changeLog=" + changeLog + ", modifiedTime=" + modifiedTime
				+ "]";
	}

}

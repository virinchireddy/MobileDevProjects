package in.spoors.effort1;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.text.TextUtils;
import android.util.Log;

public class InitResponse {

	private static String TAG = "InitResponse";

	private String employeeId;
	private String employeeName;
	private String employeeRank;
	private String description;
	private String code;
	private String smsc;

	public static final String CODE_BAD_JSON = "4001";
	public static final String CODE_NOT_AUTHORIZED = "4018";
	public static final String CODE_SWITCH_DEVICE = "4019";
	public static final String CODE_EMPLOYEE_DISABLED = "4020";
	public static final String CODE_COMPANY_INACTIVE = "4028";

	public static final String JSON_EMPLOYEE_ID = "empId";
	public static final String JSON_EMPLOYEE_NAME = "empFirstName";
	public static final String JSON_EMPLOYEE_RANK = "rank";
	public static final String JSON_DESCRIPTION = "description";
	public static final String JSON_CODE = "code";
	public static final String JSON_SMSC = "smsc";

	/**
	 * Don't instantiate this directly.
	 */
	private InitResponse() {

	}

	public static InitResponse parse(String responseJson) {
		InitResponse response = null;

		try {
			JSONTokener tokener = new JSONTokener(responseJson);
			Object jsonObject = tokener.nextValue();

			if (!(jsonObject instanceof JSONObject)) {
				Log.w(TAG,
						"Expected init response to be a JSON object, but got this: "
								+ responseJson);
				return null;
			}

			JSONObject json = (JSONObject) jsonObject;

			response = new InitResponse();
			response.employeeId = Utils.getString(json, JSON_EMPLOYEE_ID);

			if (TextUtils.isEmpty(response.employeeId)) {
				response.description = Utils.getString(json, JSON_DESCRIPTION);
				response.code = Utils.getString(json, JSON_CODE);
				response.smsc = Utils.getString(json, JSON_SMSC);
			} else {
				response.employeeName = Utils.getString(json,
						JSON_EMPLOYEE_NAME);
				response.employeeRank = Utils.getString(json,
						JSON_EMPLOYEE_RANK);
			}
		} catch (JSONException e) {
			Log.d(TAG, "Failed to parse init response: " + responseJson + ": "
					+ e.toString(), e);
		}

		return response;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSmsc() {
		return smsc;
	}

	public void setSmsc(String smsc) {
		this.smsc = smsc;
	}

	public String getEmployeeRank() {
		return employeeRank;
	}

	public void setEmployeeRank(String employeeRank) {
		this.employeeRank = employeeRank;
	}
}

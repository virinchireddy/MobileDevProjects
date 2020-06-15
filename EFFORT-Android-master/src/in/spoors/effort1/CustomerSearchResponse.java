package in.spoors.effort1;

import in.spoors.effort1.dto.Customer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;

/**
 * Sync response acts as a JSON parser and container that holds the results of a
 * successful sync.
 * 
 * @author tiru
 * 
 */
public class CustomerSearchResponse {

	public static final String TAG = CustomerSearchResponse.class
			.getSimpleName();

	private List<Customer> addedCustomers = new ArrayList<Customer>();

	private Context applicationContext;

	public CustomerSearchResponse(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * 
	 * @param jsonString
	 * @param applicationContext
	 *            Required for accessing resource strings, and creating DAO
	 *            objects.
	 * @return true if parsing succeeded
	 */
	public boolean parse(String jsonString) {
		try {
			JSONTokener tokener = new JSONTokener(jsonString);
			Object obj = tokener.nextValue();

			if (!(obj instanceof JSONArray)) {
				Log.e(TAG,
						"Invalid customer search response. Expected a JSON array but did not get it.");
				return false;
			}

			JSONArray jsonArray = (JSONArray) obj;

			// PROCESS CUSTOMERS
			Utils.addCustomers(jsonArray, addedCustomers, applicationContext,
					true);
		} catch (JSONException e) {
			Log.e(TAG,
					"Failed to parse customer search response JSON: "
							+ e.toString(), e);
			return false;
		} catch (ParseException e) {
			Log.e(TAG,
					"Failed to parse customer search response JSON: "
							+ e.toString(), e);
			return false;
		}

		return true;
	}

	public List<Customer> getAddedCustomers() {
		return addedCustomers;
	}

}

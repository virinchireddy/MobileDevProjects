package sooner.om.com.sooner.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sooner.om.com.sooner.LoginScreen;
import sooner.om.com.sooner.PatientLandingScreen;
import sooner.om.com.sooner.app.AppController;
import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.network.ConnectionDetector;

//this is very important class.with this class we are managing sessions of "user login" and "logout" .
public class SessionManager {
    // User name (make variable public to access from outside)
    public static final String authenticationToken = "authenticationToken";
    public static final String userId = "userId";
    public static final String password = "password";
    public static final String facilityId = "facilityId";
    public static final String selectedFacilityName = "selectedFacilityName";
    // Sharedpref file name
    private static final String PREF_NAME = "Jumpcall";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private Editor editor;
    // Context
    private Context _context;
    private Others others;
    private ConnectionDetector con;


    // Constructor
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this._context = context;
        int PRIVATE_MODE = 0;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        others = new Others(_context);
        con = new ConnectionDetector(_context);
    }

    /**
     * Create login session
     */
    public void createLoginSession(String authenticationToken, String userId, String password, String facilityId, String facilityName) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(SessionManager.authenticationToken, authenticationToken);
        editor.putString(SessionManager.userId, userId);
        editor.putString(SessionManager.password, password);
        editor.putString(SessionManager.facilityId, facilityId);
        editor.putString(SessionManager.selectedFacilityName, facilityName);
        // commit changes
        editor.commit();
    }

    public void updateLoginSession(String facilityId,String facilityName) {

        editor.putString(SessionManager.facilityId, facilityId);
        editor.putString(SessionManager.selectedFacilityName, facilityName);
        editor.commit();
    }
    public void updateLoginSession(String facilityId) {

        editor.putString(SessionManager.facilityId, facilityId);
        editor.commit();
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        // user name
        user.put(authenticationToken, pref.getString(authenticationToken, null));
        user.put(userId, pref.getString(userId, null));
        user.put(password, pref.getString(password, null));
        user.put(facilityId, pref.getString(facilityId, null));
        user.put(selectedFacilityName, pref.getString(selectedFacilityName, null));
        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        try {
            if (con.isConnectingToInternet()) {
                JSONObject obj = new JSONObject();
                obj.put("_UserId", PatientLandingScreen.userId);
                logOutUrlRequest(obj);
            } else {
                con.failureAlert();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        // After logout redirect user to Loing Activity


    }

    private void logOutUrlRequest(JSONObject obj) {
        // TODO Auto-generated method stub
        String tag_string_req = "req_Notifications";
        others.showProgressWithOutMessage();
        Log.v("logout", obj.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_LOGOUT, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        // String changedemail = etEmail.getText().toString();
                        Log.v("am here", "");
                        Intent i = new Intent(_context, LoginScreen.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        _context.startActivity(i);
                        ((Activity) _context).finish();
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.i("volley", "error: " + error);
                others.hideDialog();
                // Toast.makeText(getActivity(),
                // "Something went wrong please try again later",
                // Toast.LENGTH_LONG).show();
            }

        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        jsObjRequest.setShouldCache(false);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest,
                tag_string_req);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}
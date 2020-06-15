package sooner.om.com.sooner.network;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sooner.om.com.sooner.LoginScreen;
import sooner.om.com.sooner.app.AppController;
import sooner.om.com.sooner.helper.CustomizeDialog;
import sooner.om.com.sooner.helper.Others;
import sooner.om.com.sooner.helper.SessionManager;

 //Created by Ndroid on 12/8/2016.

public class ServerCall implements CustomizeDialog.AsyncTaskInterface {

    private AsyncTaskInterface mAsyncTaskInterface;

    private Context mContext;
    private Others others;
    private SessionManager session;
    private CustomizeDialog customizeDialog;

    /*// constructor with only context
    public ServerCall(Context context) {

        mContext = context;
        others = new Others(context);
        session = new SessionManager(context);

    }*/

    // constructor with screen type
    public ServerCall(Context context, AsyncTaskInterface ai) {

        mContext = context;
        mAsyncTaskInterface = ai;
        others = new Others(context);
        session = new SessionManager(context);

    }

    // post request for url
    public void postUrlRequest(String url, JSONObject obj, String tag) {
        // TODO Auto-generated method stub
        final String tag_string_req = tag;
        others.showProgressWithOutMessage();


        Log.v("url", obj.toString());
        // json object request
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, url, obj,
                new Response.Listener<JSONObject>() {
                    // response from json object
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            Log.v("response", response.toString());
                            boolean error = response.getBoolean("Error");
                            // success response
                            if (!error) {
                                mAsyncTaskInterface.onAsyncTaskInterfaceResponse(response, tag_string_req);
                            }
                            // failure response
                            else {
                                if (!tag_string_req.equals("isWhiteSlotAvilable"))
                                    others.ToastMessage(response.getString("error_msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            // error handling here
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.i("volley", "error: " + error);
                others.hideDialog();

                NetworkResponse response = error.networkResponse;

                if (response != null) {
                    switch (response.statusCode) {
                        //not found
                        case 404:
                            if (tag_string_req.equals("login"))
                                Toast.makeText(mContext,
                                        "User does not  exist",
                                        Toast.LENGTH_LONG).show();
                            else {
                                session.logoutUser();
                                Toast.makeText(mContext,
                                        "User does not  exist",
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                        // Forbidden error user inactive
                        case 403:
                            Toast.makeText(mContext,
                                    "User is Inactive", Toast.LENGTH_LONG)
                                    .show();
                            break;
                        // Unauthorized
                        case 401:
                            if (tag_string_req.equals("login"))
                                Toast.makeText(mContext,
                                        "Please enter correct password",
                                        Toast.LENGTH_LONG).show();
                            else {
                                session.logoutUser();
                                Toast.makeText(mContext,
                                        "User Logged in from other device",
                                        Toast.LENGTH_LONG).show();
                            }
                            break;
                        // specific condition is doesn't match
                        case 412:
                            Toast.makeText(mContext,
                                    "Please Accept terms and conditions",
                                    Toast.LENGTH_LONG).show();
                            Log.v("Tc", response.toString() + "");
                            break;
                        // gone
                        case 410:
                            customizeDialog = new CustomizeDialog(mContext, "requestAppointment", ServerCall.this);
                            customizeDialog.setMessage("Sorry! This appointment is booked already by other user.");
                            customizeDialog.show();
                            break;
                    }
                }

            }
        }) {


             // Passing some request headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        // setting the url cache
        jsObjRequest.setShouldCache(false);
        // setting up the retry policy
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest,
                tag_string_req);
    }

    // server request for method put
    public void putUrlRequest(String url, JSONObject obj, String tag) {
        // TODO Auto-generated method stub
        final String tag_string_req = tag;
        others.showProgressWithOutMessage();
        Log.v("url", obj.toString());
        // json object request with put method
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.PUT, url, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    // response block
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            Log.v("response", response.toString());
                            boolean error = response.getBoolean("Error");
                            // response if success
                            if (!error) {
                                mAsyncTaskInterface.onAsyncTaskInterfaceResponse(response, tag_string_req);
                            }
                            // failure response
                            else {
                                others.ToastMessage(response
                                        .getString("error_msg"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            // error block
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.i("volley", "error: " + error);
                others.hideDialog();
            }
        }) {
            // Passing some request headers
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        // enabling the cache
        jsObjRequest.setShouldCache(false);
        // setting up the retry policy
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest,
                tag_string_req);
    }

    // handling server request
    @Override
    public void onAsyncTaskInterfaceResponse(String result) {
        Intent i = new Intent(mContext, LoginScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        mContext.startActivity(i);
    }

    // interface for sending response to main activity
    public interface AsyncTaskInterface {

        void onAsyncTaskInterfaceResponse(JSONObject result, String tag);

    }

}

package com.info.socialnetworking.app.service;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.meetatbars.BarHopperLogin;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;



@SuppressWarnings("deprecation")
public class LocationWebService {

	private Context _context;
	private ProgressDialog pDialog;
	ConnectionDetector con;
	
	public LocationWebService(Context context) {
		// TODO Auto-generated constructor stub
		_context=context;
		con = new ConnectionDetector(_context);
	}
	
	
	public void SendLocation(JSONObject verifyEmail) {

		if(con.isConnectingToInternet())
		{
			Toast.makeText(_context, verifyEmail.toString(), Toast.LENGTH_SHORT).show();
		}
		/*String tag_string_req = "req_SendLocation";
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_LOCATION_UPDATE, verifyEmail,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								
								
								
							} else {

								String error_msg = response
										.getString("error_msg");
								Log.v("error_delete_email", error_msg);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("volley", "error: " + error);
						
						con.serverErrorAlert();

					}
				});
		jsObjRequest.setShouldCache(false);
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);
*/
	}
	
	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	// Hiding the dialog box
	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}
	
	/*public void ToastMessage(String message) {
		Toast toast = Toast.makeText(_context.getApplicationContext(), message,
				Toast.LENGTH_SHORT);
		View view = toast.getView();
		view.setBackgroundDrawable(_context.getResources().getDrawable(
				R.drawable.toast_message_background));
		toast.setGravity(Gravity.CENTER, 0, 0);
		// TextView text = (TextView) view.findViewById(android.R.id.message);
		// text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
		toast.show();

	}*/
	
	
}

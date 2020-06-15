package com.info.socialnetworking.app.meetatbars;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.Fragment.FragmentBarOwnerLandingPage;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.helper.SessionManager;
import com.info.socialnetworking.app.network.ConnectionDetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class BarOwnerLandingPage extends Activity {

	int back_count = 0;
	SessionManager session;
	public static int user_id;
	Others others;
	ConnectionDetector con;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bar_owner_landingpage);
		savedInstanceState = getIntent().getExtras();
		user_id = savedInstanceState.getInt("user_id");
		session = new SessionManager(this);
		others = new Others(BarOwnerLandingPage.this);
		con = new ConnectionDetector(BarOwnerLandingPage.this);

		FragmentBarOwnerLandingPage barOwner = new FragmentBarOwnerLandingPage();
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flContainer, barOwner).addToBackStack(null)
				.commit();
	}

	public void menu(View v) {
		LogoutUrl();
	}

	public void account(View v) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"My account will open a new link in the brower of you device. Click continue to proceed further.")
				.setCancelable(false)
				.setPositiveButton("Continue",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								Uri uriUrl = Uri
										.parse("http://www.meetatbars.com/prod/m/mlogin");
								Intent i = new Intent(Intent.ACTION_VIEW,
										uriUrl);
								startActivity(i);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Action for 'NO' Button
								dialog.cancel();

							}
						});
		// Creating dialog box
		AlertDialog alert = builder.create();
		// Setting the title manually
		alert.setTitle("Alert");
		alert.show();
	}

	public void memberPage(View v) {
		/*
		 * Intent i=new Intent(getApplicationContext(),HomeFragment.class);
		 * startActivity(i);
		 */
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		back_count++;
		if (back_count == 1) {
			Toast.makeText(getApplicationContext(),
					"Pressing back will divert you to login screen",
					Toast.LENGTH_SHORT).show();
		} else if (back_count == 2) {

			Intent i = new Intent(getApplicationContext(), BarHopperLogin.class);
			startActivity(i);
			finish();
		}
	}

	private void LogoutUrl() {
		// TODO Auto-generated method stub
		String tag_string_req = "req_Logout";
		others.showProgressWithOutMessage();
		JSONObject logOut = null;
		try {

			logOut = new JSONObject();
			logOut.put("user_id", user_id);

			JsonObjectRequest jsObjRequest = new JsonObjectRequest(
					Request.Method.POST, Config.URL_LOGOUT, logOut,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							others.hideDialog();
							try {
								Log.v("logout",	response.toString());
								boolean error = response.getBoolean("error");
								if (!error) {
									session.logoutUser();
									finish();
								} else {
									if (response.getString("error_msg").equals(
											"User already logged out")) {
										session.logoutUser();
										finish();
									} else {
										Log.v("logout",
												response.getString("error_msg"));
										others.ToastMessage(response
												.getString("error_msg"));
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Log.i("volley", "error: " + error);
							others.ToastMessage(error.getMessage());
							others.hideDialog();
						}
					});
			jsObjRequest.setShouldCache(false);
			// Adding request to request queue
			AppController.getInstance().addToRequestQueue(jsObjRequest,
					tag_string_req);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}

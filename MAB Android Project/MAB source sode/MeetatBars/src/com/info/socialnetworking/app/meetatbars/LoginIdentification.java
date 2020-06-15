package com.info.socialnetworking.app.meetatbars;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.network.ConnectionDetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class LoginIdentification extends Activity implements OnClickListener {

	String LoginIdentification, email, password, userName;
	EditText etUserName;
	RadioGroup rgLoginIdentification;
	RadioButton rbBarOwner, rbBarHopper;
	private ProgressDialog pDialog;
	LinearLayout liAvilable, liUnAvilable;
	Button btnCheckAvilability, btnRegistor;
	int proceed_count = 0, user_type;
	int success;
	ConnectionDetector con;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_identification);
		savedInstanceState = getIntent().getExtras();
		LoginIdentification = savedInstanceState
				.getString("Login_Identification");
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		con = new ConnectionDetector(LoginIdentification.this);
		if (LoginIdentification.equals("Normal Login")) {
			email = savedInstanceState.getString("email");
			password = savedInstanceState.getString("password");
		} else if (LoginIdentification.equals("Facebook Login")) {
			email = savedInstanceState.getString("email");
			password = savedInstanceState.getString("password");
		}
		initiolize();
		btnCheckAvilability.setOnClickListener(this);
		btnRegistor.setOnClickListener(this);
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		rbBarOwner = (RadioButton) findViewById(R.id.rbBarOwner);
		rbBarHopper = (RadioButton) findViewById(R.id.rbBarHopper);
		etUserName = (EditText) findViewById(R.id.etUserName);
		btnCheckAvilability = (Button) findViewById(R.id.btnCheckAvilability);
		liAvilable = (LinearLayout) findViewById(R.id.liAvilable);
		liUnAvilable = (LinearLayout) findViewById(R.id.liUnAvilable);
		btnRegistor = (Button) findViewById(R.id.btnRegistor);
	}

	private void validate(int id) {
		// TODO Auto-generated method stub
		if (rbBarHopper.isChecked()) {
			proceed_count++;
			user_type = 3;
			makeUserNameRequest(id);

		} else if (rbBarOwner.isChecked()) {
			alertToContinueAsBarOwner();
		} else {
			ToastMessage("Please choose Account Type");
		}
	}

	public boolean ValidateUserName(String uName) {

		if (uName.length() <= 0) {
			ToastMessage("Please enter nick name / alias");
			liAvilable.setVisibility(View.GONE);
			liUnAvilable.setVisibility(View.GONE);
			editText_red_round(etUserName);
			return false;
		} else if (uName.length() > 0) {
			editText_default_round(etUserName);
			return true;
		} else {
			return false;
		}
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

	public void ToastMessage(String message) {
		Toast toast = Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_SHORT);
		View view = toast.getView();
		view.setBackgroundDrawable(getResources().getDrawable(R.drawable.toast_message_background));
		toast.setGravity(Gravity.CENTER, 0, 0);
		// TextView text = (TextView) view.findViewById(android.R.id.message);
		// text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
		toast.show();
	}
	public void editText_red_round(EditText value) {
		value.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.edittext_red_round_corners));
	}

	public void editText_default_round(EditText value) {
		value.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.login_using_border));
	}

	@Override
	public void onClick(View v) {
		if (con.isConnectingToInternet()) {
			switch (v.getId()) {
			case R.id.btnCheckAvilability:

				makeUserNameRequest(R.id.btnCheckAvilability);
				break;
			case R.id.btnRegistor:
				validate(R.id.btnRegistor);
				break;
			default:
				break;
			}
		} else {
			con.failureAlert();
		}

	}

	public void makeUserNameRequest(int id) {
		userName = etUserName.getText().toString().trim();

		if (ValidateUserName(userName)) {

			try {
				JSONObject obj = new JSONObject();
				obj.put("username", userName);
				checkUserName(obj, id);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {

		}
	}

	private void checkUserName(JSONObject userObj, final int viewId) {
		// TODO Auto-generated method stub

		String tag_string_req = "req_check_user_name";

		pDialog.setMessage("Checking Nick name ...");
		showDialog();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_CHECK_USERNAME, userObj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								liAvilable.setVisibility(View.VISIBLE);
								liUnAvilable.setVisibility(View.GONE);
								if (viewId == R.id.btnRegistor) {
									Intent i = new Intent(getApplicationContext(),UploadPicture.class);
									i.putExtra("email", email);
									i.putExtra("password", password);
									i.putExtra("user_type", user_type);
									i.putExtra("user_name", userName);
									i.putExtra("Login_Identification",LoginIdentification);
									startActivity(i);
									finish();
								}
							} else {
								liUnAvilable.setVisibility(View.VISIBLE);
								liAvilable.setVisibility(View.GONE);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("volley", "error: " + error);
						hideDialog();
						con.serverErrorAlert();
					}
				});
		jsObjRequest.setShouldCache(false);
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);
	}

public void alertToContinueAsBarOwner() {
	
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setMessage(
			"Clicking OK will continue your registration as a bar owner")
			.setCancelable(false)
			.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int dialog_id) {
							dialog.cancel();
							proceed_count++;
							user_type = 2;
							makeUserNameRequest(R.id.btnRegistor);
						}
					})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int dialog_id) {
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

}




package com.info.socialnetworking.app.meetatbars;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressWarnings("deprecation")
public class ForgotEmail extends Activity implements OnClickListener {

	RadioButton rbEmail, rbPassword;
	EditText etEmail, etPassword;
	private ProgressDialog pDialog;
	Button btnRequest,btnCancel;
	ConnectionDetector con;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_email);

		/*
		 * WindowManager.LayoutParams params = getWindow().getAttributes();
		 * params.x = -20; params.height = 500; params.width = 550; params.y =
		 * -10;
		 * 
		 * this.getWindow().setAttributes(params);
		 */

		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		rbEmail = (RadioButton) findViewById(R.id.rbEmail);
		rbPassword = (RadioButton) findViewById(R.id.rbPassword);
		btnRequest = (Button) findViewById(R.id.btnRequest);
		btnCancel = (Button)findViewById(R.id.btnCancel);

		etEmail = (EditText) findViewById(R.id.etEmail);
		etPassword = (EditText) findViewById(R.id.etPassword);

		etEmail.setVisibility(View.GONE);
		con = new ConnectionDetector(ForgotEmail.this);

		rbEmail.setOnClickListener(this);
		rbPassword.setOnClickListener(this);
		btnRequest.setOnClickListener(this);
		btnCancel.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rbEmail:
			etEmail.setVisibility(View.VISIBLE);
			etPassword.setVisibility(View.GONE);
			break;
		case R.id.rbPassword:
			etPassword.setVisibility(View.VISIBLE);
			etEmail.setVisibility(View.GONE);
			break;
		case R.id.btnCancel:
			finish();
			break;
		case R.id.btnRequest:
			if (rbEmail.isChecked()) {
				if (etEmail.getText().toString().trim().length() > 0) {
					if (con.isConnectingToInternet()) {
						try {
							editText_default_round(etEmail);
							JSONObject obj = new JSONObject();
							obj.put("username", etEmail.getText().toString()
									.trim());
							RequestEmail(obj);
						} catch (Exception e) {
							e.printStackTrace();
							// TODO: handle exception
						}
					} else {
						con.failureAlert();
					}
				} else {

					editText_red_round(etEmail);
					ToastMessage("Please enter username");

				}
			} else if (rbPassword.isChecked()) {

				String email = etPassword.getText().toString().trim();
				final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

				if (email.matches(emailPattern)) {
					if (con.isConnectingToInternet()) {
						try {
							editText_default_round(etPassword);
							JSONObject obj = new JSONObject();
							obj.put("email", etPassword.getText().toString()
									.trim());
							RequestPassword(obj);
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					} else {
						con.failureAlert();
					}
				} else if (email.length() <= 0) {
					editText_red_round(etPassword);
					ToastMessage("Please enter email");
				} else {
					editText_red_round(etPassword);
					ToastMessage("Email Id is not valid");
				}
			} else {

				ToastMessage("Choose atleast one");
			}
		default:
			break;
		}
	}

	private void RequestPassword(final JSONObject forgotObj) {

		// TODO Auto-generated method stub
		String tag_string_req = "req_login";

		pDialog.setMessage("Getting Details Please wait ...");
		showDialog();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_FORGOT_PASSWORD, forgotObj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								Toast.makeText(getApplicationContext(),
										"Password is sent to mail",
										Toast.LENGTH_SHORT).show();

							} else {
								String error_msg = response
										.getString("error_msg");
								Toast.makeText(getApplicationContext(),
										error_msg, Toast.LENGTH_SHORT).show();
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

	private void RequestEmail(final JSONObject forgotObj) {

		// TODO Auto-generated method stub
		String tag_string_req = "req_login";

		pDialog.setMessage("Getting Details Please wait ...");
		showDialog();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_FORGOT_EMAIL, forgotObj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								String email = response.getString("email");
								alert(email);
							} else {
								String error_msg = response
										.getString("error_msg");
								Toast.makeText(getApplicationContext(),
										error_msg, Toast.LENGTH_SHORT).show();
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

	// Displaying the dialog box
	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	// Hiding the dialog box
	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

	public void alert(String email) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// Setting message manually and performing action on button click

		builder.setMessage(email)
				.setCancelable(false)
				.setPositiveButton("Login",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								Intent i = new Intent(getApplicationContext(),
										BarHopperLogin.class);
								startActivity(i);

								finish();
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
		alert.setTitle("Your Email is");
		alert.show();
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

}

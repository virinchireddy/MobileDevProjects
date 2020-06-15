package com.info.socialnetworking.app.helper;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.sax.StartElementListener;
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
import com.info.socialnetworking.app.meetatbars.BarOwnerQuestions;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressWarnings("deprecation")
public class DeleteEmail {

	private Context _context;
	private ProgressDialog pDialog;
	ConnectionDetector con;

	public DeleteEmail(Context context) {
		// TODO Auto-generated constructor stub
		this._context = context;
		pDialog = new ProgressDialog(_context);
		pDialog.setCancelable(false);
		con = new ConnectionDetector(_context);

	}

	public void deleteEmail(final JSONObject verifyEmail) {

		String tag_string_req = "req_Delete_account";

		pDialog.setMessage("Deleting...");
		showDialog();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_DELETE_EMAIL, verifyEmail,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								Intent i = new Intent(_context
										.getApplicationContext(),
										BarHopperLogin.class);
								((Activity) _context).startActivity(i);
								((Activity) _context).finish();
								Toast.makeText(
										_context.getApplicationContext(),
										"Details have been deleted successfully",
										Toast.LENGTH_SHORT).show();
								/*
								 * Intent i = new
								 * Intent(getApplicationContext(),
								 * BarHopperLogin.class); startActivity(i);
								 */

							} else {

								String error_msg = response
										.getString("error_msg");
								ToastMessage(error_msg);
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

	public void deleteEmailAlert(final String email) {

		AlertDialog.Builder builder = new AlertDialog.Builder(_context);
		builder.setMessage(
				"Clicking OK will delete the following email and related data. \n\n"
						+ email)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						try {
							JSONObject email_obj = new JSONObject();
							email_obj.put("email", email);
							deleteEmail(email_obj);
						} catch (Exception e) {
							e.printStackTrace();
						}
						//
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
							}
						});

		AlertDialog alert = builder.create();
		alert.setTitle("Delete email");
		alert.show();
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
		Toast toast = Toast.makeText(_context.getApplicationContext(), message,
				Toast.LENGTH_SHORT);
		View view = toast.getView();
		view.setBackgroundDrawable(_context.getResources().getDrawable(
				R.drawable.toast_message_background));
		toast.setGravity(Gravity.CENTER, 0, 0);
		// TextView text = (TextView) view.findViewById(android.R.id.message);
		// text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
		toast.show();

	}

}

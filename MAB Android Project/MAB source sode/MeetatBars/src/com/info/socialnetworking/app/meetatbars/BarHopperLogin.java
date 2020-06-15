package com.info.socialnetworking.app.meetatbars;

import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.SessionManager;
import com.info.socialnetworking.app.helper.UserInfo;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressWarnings("deprecation")
public class BarHopperLogin extends Activity {

	public static LoginManager loginManager;
	CallbackManager callbackManager;
	static AccessToken accessToken;

	public static final String TAG = "FACEBOOK";
	final private static int DIALOG_LOGIN = 1;
	String FILENAME = "AndroidSSO_data";
	TextView tvRegistor,tvWarning;
	Button btnLoginWithFB;
	private SessionManager session;
	private ProgressDialog pDialog;
	int ValidationCount = 0, user_id, is_verified, is_login;
	public static int is_facebook;
	EditText etEmail, etPassword;
	String email, password;
	String email_alert = "Email not verified!", verificationCodeString = null;
	

	private static final int REQUEST_CODE_PERMISSION = 2;

	String[] mPermission = { Manifest.permission.INTERNET,
			Manifest.permission.ACCESS_NETWORK_STATE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.CAMERA,
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION };

	ConnectionDetector con;
	CurrentLocation location;
	public static Activity activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FacebookSdk.sdkInitialize(getApplicationContext());
		callbackManager = CallbackManager.Factory.create();

		setContentView(R.layout.activity_login_screen);
		Initiolize();

		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		activity = this;
		// for custom loader icon

		// pDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.temp_animation));

		con = new ConnectionDetector(BarHopperLogin.this);

		// Session manager
		session = new SessionManager(getApplicationContext());
		if (session.isLoggedIn()) {
			// User is already logged in. Take him to main activity
			HashMap<String, Integer> user = session.getUserDetails();
			// name
			int user_type = user.get(SessionManager.USER_TYPE);
			int user_id = user.get(SessionManager.USER_ID);

			// bar Owner
			if (user_type == 2) {
				Intent i = new Intent(getApplicationContext(),
						BarOwnerLandingPage.class);
				i.putExtra("user_id", user_id);
				startActivity(i);

			}
			//bar Hopper
			else if (user_type == 3) {
				Intent i = new Intent(getApplicationContext(), MainMenu.class);
				i.putExtra("user_id", user_id);
				startActivity(i);
			}
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isLoggedInWithFB()) {
			facebookLogout();
		}
	}

	@SuppressWarnings("static-access")
	public static boolean isLoggedInWithFB() {

		return accessToken.getCurrentAccessToken() != null;
	}

	private void Initiolize() {
		// TODO Auto-generated method stub
		btnLoginWithFB = (Button) findViewById(R.id.btnLoginWithFB);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etPassword = (EditText) findViewById(R.id.etPassword);
		tvWarning=(TextView)findViewById(R.id.tvWarningMessage);
		permissionCheckMarshMallow();
	}

	private void permissionCheckMarshMallow() {
		// TODO Auto-generated method stub
		try {
			if (ActivityCompat.checkSelfPermission(this, mPermission[0]) != MockPackageManager.PERMISSION_GRANTED
					|| ActivityCompat.checkSelfPermission(this, mPermission[1]) != MockPackageManager.PERMISSION_GRANTED
					|| ActivityCompat.checkSelfPermission(this, mPermission[2]) != MockPackageManager.PERMISSION_GRANTED
					|| ActivityCompat.checkSelfPermission(this, mPermission[3]) != MockPackageManager.PERMISSION_GRANTED) {

				ActivityCompat.requestPermissions(this, mPermission,
						REQUEST_CODE_PERMISSION);

				// If any permission aboe not allowed by user, this condition
				// will execute every tim, else your else part will work
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ValidateEmailPassword() {

		email = etEmail.getText().toString().trim();

		final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

		password = etPassword.getText().toString().trim();

		// final String passwordPattern =
		// "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

		if (email.matches(emailPattern)) {
			if (password.length() >= 8) {
				ValidationCount = 2;
			} else if (password.length() <= 0) {
				ValidationCount = 0;
				editText_default_round(etEmail);
				editText_red_round(etPassword);
				ToastMessage("Please enter password");
			} else {
				ValidationCount = 0;
				editText_default_round(etEmail);
				editText_red_round(etPassword);
				if (is_login == 1)
					ToastMessage("Invalid Password");
				else if (is_login == 0)
					ToastMessage("Password should be minimum 8 charecters");
			}

		} else {
			if (email.length() <= 0) {
				editText_red_round(etEmail);
				ToastMessage("Please enter email id");
			} else {
				editText_red_round(etEmail);
				ToastMessage("Email Id is not valid");
			}
		}
	}

	public void facebookButtonClicked(View view) {
		// startProgressDialog();
		is_facebook = 1;
		is_login = 1;
		loginManager = LoginManager.getInstance();
		loginManager.registerCallback(callbackManager,new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {

						Log.v("gettingdetails", "GetAccess token");

						pDialog.setMessage("Getting facebook please wait");
						showDialog();
						GraphRequest request = GraphRequest.newMeRequest(
								loginResult.getAccessToken(),

								new GraphRequest.GraphJSONObjectCallback() {
									@Override
									public void onCompleted(JSONObject object,
											GraphResponse response) {
										try {

											Log.v("TAG", "facebook details "
													+ object.toString());
											Log.v("TAG", "Grap response "
													+ response.getJSONArray());
											email = object.getString("email");
											password = object.getString("id");
											etEmail.setText(email);
											JSONObject emailObj = new JSONObject();
											emailObj.put("email", email);
											emailObj.put("password", password);
											Log.v("sending data",
													emailObj.toString());
											DirectLogin(emailObj);

										} catch (Exception e) {
											Log.v("TAG",
													"exception=="
															+ e.toString());
										}
									}
								});

						Log.v("TAG", "GetAccess token");

						Bundle parameters = new Bundle();
						parameters
								.putString("fields",
										"id, name,first_name,last_name, age_range,email,gender,location, birthday");
						request.setParameters(parameters);
						request.executeAsync();

					}

					@Override
					public void onCancel() {
						hideDialog();
					}

					@Override
					public void onError(FacebookException error) {
						hideDialog();
					}
				});
		loginManager.logInWithReadPermissions(this,
				Arrays.asList("public_profile", "email", "user_birthday"));

	}

	public static void facebookLogout() {
		LoginManager.getInstance().logOut();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	public void login(View v) {

		is_facebook = 0;
		is_login = 1;
		ValidateEmailPassword();

		if (ValidationCount == 2) {
			if (con.isConnectingToInternet()) {
				try {
					JSONObject obj = new JSONObject();
					obj.put("email", email);
					obj.put("password", password);
					DirectLogin(obj);

				} catch (Exception e) {
					// TODO: handle exception
				}
			} else {
				con.failureAlert();
			}

		}
		// runTestLogout();
	}

	public void signup(View v) {

		is_facebook = 0;
		is_login = 0;
		ValidateEmailPassword();
		if (ValidationCount == 2) {
			if (con.isConnectingToInternet()) {
				try {
					JSONObject obj = new JSONObject();
					obj.put("email", email);
					obj.put("password", password);
					Log.v("hello", obj.toString());

					DirectLogin(obj);

				} catch (Exception e) {
					// TODO: handle exception
				}
			} else {
				con.failureAlert();
			}

		}

	}

	private void DirectLogin(JSONObject loginObj) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_login";

		pDialog.setMessage("Loading ...");

		showDialog();

		Log.v("sending data", loginObj.toString());

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_LOGIN, loginObj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");

							if (!error) {

								if (is_login == 1) {
									if (is_facebook == 0
											&& response
													.getString("status_msg")
													.equals("Facebook Login Success")) {
										ToastMessage("Login using facebook to continue");

									} else {
										Log.v("hello", response.toString());

										editText_default_round(etEmail);
										editText_default_round(etPassword);
										user_id = response.getInt("user_id");
										UserInfo ob = new UserInfo();
										ob.setUserId(user_id);
										int user_type = response
												.getInt("user_type");
										session.createLoginSession(user_id,
												user_type);
										// bar owner
										if (user_type == 2) {
											Intent i = new Intent(
													getApplicationContext(),
													BarOwnerLandingPage.class);
											i.putExtra("user_id", user_id);
											startActivity(i);
											// finish();
										}
										// bar hopper
										else if (user_type == 3) {
											Intent i = new Intent(
													getApplicationContext(),
													MainMenu.class);
		 									i.putExtra("user_id", user_id);
											startActivity(i);

										} else if (user_type == 1) {
											ToastMessage("Login using browser");
										}
									}
								} else {
									ToastMessage("You are already registered click login to continue");
			 					}

							} else {

								editText_default_round(etEmail);
								editText_default_round(etPassword);
								 String error_msg = response
										.getString("error_msg");
								// ToastMessage(error_msg);
								if(error_msg.equals("Your account has been blocked")){
									//Toast.makeText(getApplicationContext(), error_msg, Toast.LENGTH_SHORT).show();
									tvWarning.setVisibility(View.VISIBLE);
								}
								
								else if (!error_msg.equals("User already logged in from other device")) {
									if (response.has("is_verified")) {
										user_id = response.getInt("user_id");
										is_verified = response
												.getInt("is_verified");
										if (is_verified == 0) {
											showDialog(DIALOG_LOGIN);
										}
										// sendEmailAlert();
									} else {
										if (is_facebook == 0) {

											if (is_login == 1) {
												ToastMessage(error_msg);
											} else if (is_login == 0) {
												if (error_msg
														.equals("Email not yet registered")) {
													Intent i = new Intent(
															getApplicationContext(),
															LoginIdentification.class);
													i.putExtra("Login_Identification","Normal Login");
													i.putExtra("email", email);
													i.putExtra("password",password);
													
													startActivity(i);

												} else {
													ToastMessage(error_msg);
												}

											}
//////////comes upto here without app but with existing email
										} else if (is_facebook == 1) {
											if (error_msg.equals("Password is not matching")) {
												Log.v("myblock","hi");
												Toast.makeText(
														getApplicationContext(),
														"Please enter your meet at bars password.",
														Toast.LENGTH_LONG)
														.show();
												BarHopperLogin.facebookLogout();
											} else {

												// Toast.makeText(getApplicationContext(),
												// "hello",Toast.LENGTH_SHORT).show();
												Intent i = new Intent(
														getApplicationContext(),
														LoginIdentification.class);
												i.putExtra(
														"Login_Identification",
														"Facebook Login");
												i.putExtra("email", email);
												i.putExtra("password", password);
												
												startActivity(i);
											}
										}
									}
								}else{
									ToastMessage("User already logged in from other device");

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
						hideDialog();
						con.serverErrorAlert();
						// ToastMessage(error.getMessage());
					}
				});
		jsObjRequest.setShouldCache(false);

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);

	}

	@Override
	protected Dialog onCreateDialog(int id) {

		AlertDialog dialogDetails = null;
		switch (id) {
		case DIALOG_LOGIN:
			LayoutInflater inflater = LayoutInflater.from(this);
			View dialogview = inflater.inflate(
					R.layout.dialog_email_verification, null);
			AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
			dialogbuilder.setTitle("Email Verification");
			dialogbuilder.setView(dialogview);
			dialogDetails = dialogbuilder.create();
			break;

		}
		return dialogDetails;

	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {

		switch (id) {
		case DIALOG_LOGIN:
			final AlertDialog alertDialog = (AlertDialog) dialog;
			Button verify = (Button) alertDialog.findViewById(R.id.btnVerify);
			Button cancelbutton = (Button) alertDialog
					.findViewById(R.id.btnCancel);
			TextView tvResendEmail = (TextView) alertDialog
					.findViewById(R.id.tvClickToSend);

			final EditText verificationCode = (EditText) alertDialog
					.findViewById(R.id.etVerificationCode);
			verificationCode.setText("");
			verify.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					verificationCodeString = verificationCode.getText()
							.toString();
					if (verificationCodeString.length() == 4) {
						if (con.isConnectingToInternet()) {
							try {
								JSONObject verifyEmailNumber = new JSONObject();
								verifyEmailNumber.put("user_id", user_id);
								verifyEmailNumber.put("random_num",
										verificationCodeString);
								verifyEmailNumber(verifyEmailNumber);
								alertDialog.dismiss();
							} catch (Exception e) {
								// TODO: handle exception
							}
						} else {
							con.failureAlert();
						}
					} else {
						ToastMessage("Enter a valid 4-digit verification code");
					}
				}
			});
			tvResendEmail.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					alertDialog.dismiss();
					emailAlert();
				}
			});
			cancelbutton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
				}
			});
			break;
		}
	}

	private void verifyEmailNumber(final JSONObject verifyEmailNumber) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_ResendEmailVerificationLink";

		pDialog.setMessage("Verifying ...");
		showDialog();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_VERIFY_MAILNUMBER,
				verifyEmailNumber, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								Toast.makeText(getApplicationContext(),
										"Your Email has been verified successfully",
										Toast.LENGTH_SHORT).show();
								Intent i = new Intent(getApplicationContext(),
										BarOwnerLandingPage.class);
								i.putExtra("user_id", user_id);
								startActivity(i);

							} else {

								String error_msg = response
										.getString("error_msg");
								showDialog(DIALOG_LOGIN);
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
						ToastMessage(error.getMessage());
					}
				});
		jsObjRequest.setShouldCache(false);
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);

	}

	private void emailAlert() {
		// TODO Auto-generated method stub
		String message = "Send 4-digit verification code to";

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message + "\n\n" + email)
				.setCancelable(false)
				.setPositiveButton("Send",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (con.isConnectingToInternet()) {
									dialog.cancel();
									try {
										JSONObject obj = new JSONObject();
										obj.put("email", email);
										sendVerificationLink(obj);
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									con.failureAlert();
								}
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
		alert.setTitle("Resend verification");
		alert.show();

	}

	private void sendVerificationLink(final JSONObject verifyEmail) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_ResendEmailVerificationLink";

		pDialog.setMessage("Sending ...");
		showDialog();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_RESEND_EMAIL_VERIFICATION,
				verifyEmail, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								Toast.makeText(
										getApplicationContext(),
										"Verification code sent to registered Email",
										Toast.LENGTH_SHORT).show();

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
						ToastMessage(error.getMessage());

					}
				});
		jsObjRequest.setShouldCache(false);
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);
	}

	public void forgotEmail(View v) {

		Intent i = new Intent(getApplicationContext(), ForgotEmail.class);
		startActivity(i);

	}

	/*public void barsFeedBack(final int user_id) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Would you like to give a feedback on your recent visit to Hardrock?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								Intent i = new Intent(getApplicationContext(),
										FeedbackQuestions.class);
								i.putExtra("user_id", user_id);
								startActivity(i);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Action for 'NO' Button
						dialog.cancel();
						Intent i = new Intent(getApplicationContext(),
								BarHopperHomeScreen.class);
						startActivity(i);
					}
				});

		// Creating dialog box
		AlertDialog alert = builder.create();
		// Setting the title manually
		alert.setTitle("Alert");
		alert.show();

	}*/

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

	public void editText_red_round(EditText value) {
		value.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.edittext_red_round_corners));
	}

	public void editText_default_round(EditText value) {
		value.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.login_using_border));
	}

	public void ToastMessage(String message) {
		Toast toast = Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_SHORT);
		View view = toast.getView();
		view.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.toast_message_background));
		toast.setGravity(Gravity.CENTER, 0, 0);
		// TextView text = (TextView) view.findViewById(android.R.id.message);
		// text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
		toast.show();

	}

	@TargetApi(23)
	@Override
	public void onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Log.e("Req Code", "" + requestCode);
		if (requestCode == REQUEST_CODE_PERMISSION) {
			if (grantResults.length == 4
					&& grantResults[0] == MockPackageManager.PERMISSION_GRANTED
					&& grantResults[1] == MockPackageManager.PERMISSION_GRANTED
					&& grantResults[2] == MockPackageManager.PERMISSION_GRANTED
					&& grantResults[3] == MockPackageManager.PERMISSION_GRANTED) {

				// Success Stuff here

			}
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		// System.exit(0);
	}

}

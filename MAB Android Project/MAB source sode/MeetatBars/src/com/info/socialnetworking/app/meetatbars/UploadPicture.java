package com.info.socialnetworking.app.meetatbars;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.imageCrop.CropOption;
import com.info.socialnetworking.app.imageCrop.CropOptionAdapter;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressWarnings("deprecation")
public class UploadPicture extends Activity implements OnClickListener {

	private Uri mImageCaptureUri;
	private ImageView mImageView;
	ImageView ivUpLoad;

	SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "user_details";

	private ProgressDialog pDialog;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;

	String profile_image = null, LoginIdentification, email, password,
			userName;
	int user_type, user_id;
	ConnectionDetector con;
	Button btnRegistor;
	
	LinearLayout liInstructions, liBarOwnerHeadding;
	RelativeLayout liProfilePic;
	boolean StatusOfImage = false;
	public static byte[] image_Array;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_picture);
		savedInstanceState = getIntent().getExtras();
		LoginIdentification = savedInstanceState
				.getString("Login_Identification");
		initiolize();

		con = new ConnectionDetector(UploadPicture.this);
		if (LoginIdentification.equals("Normal Login")) {
			email = savedInstanceState.getString("email");
			password = savedInstanceState.getString("password");
			userName = savedInstanceState.getString("user_name");
			user_type = savedInstanceState.getInt("user_type");
			hideInstructions();
		} else if (LoginIdentification.equals("Facebook Login")) {
			email = savedInstanceState.getString("email");
			password = savedInstanceState.getString("password");
			userName = savedInstanceState.getString("user_name");
			user_type = savedInstanceState.getInt("user_type");
			hideInstructions();
		}
		sharedpreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);
		ivUpLoad.setOnClickListener(this);
		//mImageView.setOnClickListener(this);
	}

	public void hideInstructions() {
		if (user_type == 3) {
			liInstructions.setVisibility(View.VISIBLE);
			liBarOwnerHeadding.setVisibility(View.GONE);
			liProfilePic.setVisibility(View.GONE);
			btnRegistor.setVisibility(View.GONE);
		} else if (user_type == 2) {
			liInstructions.setVisibility(View.GONE);
			liBarOwnerHeadding.setVisibility(View.VISIBLE);
			btnRegistor.setVisibility(View.VISIBLE);
		}
	}

	public void upload_pic(View v) {

		btnRegistor.setVisibility(View.VISIBLE);
		TextView tvPicUpload = (TextView) findViewById(R.id.tvPicUpload);
		tvPicUpload.setText("Upload your picture here");
		liProfilePic.setVisibility(View.VISIBLE);
		liInstructions.setVisibility(View.GONE);
		liBarOwnerHeadding.setVisibility(View.VISIBLE);

	}

	public void proceed(View v) {
		if (LoginIdentification.equals("Normal Login")) {

			if (!StatusOfImage) {
				ToastMessage("Please upload Picture");
			} else {
				if (con.isConnectingToInternet()) {
					try {
						JSONObject obj = new JSONObject();
						obj.put("email", email);
						obj.put("password", password);
						obj.put("user_type", user_type);
						obj.put("username", userName);
						obj.put("is_facebook", "0");
						obj.put("photo", profile_image);
						Log.v("registration String", obj.toString());
						registor(obj);
					} catch (Exception e) {
						Log.e("saikrupa", " " + e.toString());
						e.printStackTrace();
					}
				} else {
					con.failureAlert();
				}
			}
		} else if (LoginIdentification.equals("Facebook Login")) {
			if (!StatusOfImage) {
				ToastMessage("Please upload picture");
			} else {
				if (con.isConnectingToInternet()) {
					try {
						JSONObject obj = new JSONObject();
						obj.put("email", email);
						obj.put("password", password);
						obj.put("user_type", user_type);
						obj.put("username", userName);
						obj.put("is_facebook", "1");
						obj.put("photo", profile_image);

						registor(obj);
					} catch (Exception e) {
						Log.e("saikrupa", " " + e.toString());
						e.printStackTrace();
					}
				} else {
					con.failureAlert();
				}
			}
		}
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		ivUpLoad = (ImageView) findViewById(R.id.ivUpLoad);
		mImageView = (ImageView) findViewById(R.id.ivPhoto);
		btnRegistor=(Button)findViewById(R.id.btnRegistor);
		liInstructions = (LinearLayout) findViewById(R.id.liInstructions);
		liBarOwnerHeadding = (LinearLayout) findViewById(R.id.liBarOwnerHeadding);
		liProfilePic = (RelativeLayout) findViewById(R.id.liProfilePic);
	}

	private void registor(final JSONObject regObj) {
		String tag_string_req = "req_register";
		pDialog.setMessage("Registering ...");
		showDialog();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_REGISTER, regObj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							// String obj=response.toString();
							// Log.d("saikrupa",obj);
							if (!error) {
								user_id = response.getInt("user_id");
								Editor editor = sharedpreferences.edit();
								editor.putInt("user_id", user_id);
								editor.putInt("user_type", user_type);
								editor.commit();
								if (user_type == 3) {
									Intent i = new Intent(
											getApplicationContext(),
											MatchMeQuestionsScreen.class);
									i.putExtra("user_id", user_id);
									i.putExtra("email", email);
									startActivity(i);
									finish();
								} else if (user_type == 2) {
										Intent i = new Intent(
												getApplicationContext(),
												BarOwnerQuestions.class);
										i.putExtra("Login_Identification",
												LoginIdentification);
										i.putExtra("user_id", user_id);
										i.putExtra("email", email);
										startActivity(i);
										finish();
								}
							} else {
								String error_msg = response
										.getString("error_msg");
								/*if (error_msg
										.equals("Email already registered")) {
									if (user_type == 3) {
										Intent i = new Intent(
												getApplicationContext(),
												MatchMeQuestionsScreen.class);
										i.putExtra("user_id", 293);
										i.putExtra("email", email);
										startActivity(i);
										finish();
									} else if (user_type == 2) {
										Intent i = new Intent(getApplicationContext(),
												BarOwnerQuestions.class);
										i.putExtra("Login_Identification", LoginIdentification);
										i.putExtra("user_id", 296);
										i.putExtra("email", email);
										startActivity(i);
										finish();
									}
								} else {*/
									ToastMessage(error_msg);
								//}
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

		
		 jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000,0,
		 DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		final String[] items = new String[] { "Take from camera",
				"Select from gallery" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(
							"android.intent.extras.CAMERA_FACING",
							android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
					mImageCaptureUri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), "tmp_avatar_"
							+ String.valueOf(System.currentTimeMillis())
							+ ".jpg"));

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
							mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);

						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { // pick from file
					Intent intent = new Intent(Intent.ACTION_PICK);
					intent.setType("image/*");
					// startActivityForResult(intent, SELECT_PICTURE);
					startActivityForResult(intent, PICK_FROM_FILE);
				}
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			doCrop();

			break;

		case PICK_FROM_FILE:
			mImageCaptureUri = data.getData();

			doCrop();

			break;

		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();

			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				StatusOfImage = true;
				profile_image = encodeTobase64(photo);
				BitmapDrawable image = new BitmapDrawable(getResources(), photo);
				mImageView.setBackgroundDrawable(image);
			}
			File f = new File(mImageCaptureUri.getPath());
			if (f.exists())
				f.delete();
			break;
		}
	}

	private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);
		int size = list.size();
		if (size == 0) {
			Toast.makeText(this, "Can not find image crop app",
					Toast.LENGTH_SHORT).show();
			return;
		} else {
			intent.setData(mImageCaptureUri);
			intent.putExtra("crop", "true");
			intent.putExtra("outputX", 300);
			intent.putExtra("outputY", 300);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			//intent.putExtra("circleCrop", new String(""));
			intent.putExtra("return-data", true);
			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));
				startActivityForResult(i, CROP_FROM_CAMERA);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getPackageManager().getApplicationLabel(
							res.activityInfo.applicationInfo);
					co.icon = getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);
					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));
					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Choose Crop App");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										CROP_FROM_CAMERA);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (mImageCaptureUri != null) {
							getContentResolver().delete(mImageCaptureUri, null,
									null);
							mImageCaptureUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}

	public String encodeTobase64(Bitmap image) {
		Bitmap immagex = image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		image_Array = baos.toByteArray();
		String imageEncoded = Base64
				.encodeToString(image_Array, Base64.DEFAULT);
		return imageEncoded;
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

	public void editText_red_round(EditText value) {
		value.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.edittext_red_round_corners));
	}

	public void editText_default_round(EditText value) {
		value.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.login_using_border));
	}

	public void verificationAlert(final int user_id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Uncomment the below code to Set the message and title from the
		// strings.xml file
		// builder.setMessage(R.string.dialog_message)
		// .setTitle(R.string.dialog_title);
		// Setting message manually and performing action on button click
		builder.setMessage(
				"We have sent a 4-digit verification code to your registered email address which needs to be verified to continue further. Please check your junk mail as well.")
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						Intent i = new Intent(getApplicationContext(),
								BarOwnerQuestions.class);
						i.putExtra("Login_Identification", LoginIdentification);
						i.putExtra("user_id", user_id);
						i.putExtra("email", email);
						startActivity(i);
						finish();
					}
				});
		// Creating dialog box
		AlertDialog alert = builder.create();
		// Setting the title manually
		alert.setTitle("Email verification");
		alert.show();
	}

}

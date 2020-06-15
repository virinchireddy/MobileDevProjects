package com.info.socialnetworking.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.helper.UserInfo;
import com.info.socialnetworking.app.imageCrop.CropOption;
import com.info.socialnetworking.app.imageCrop.CropOptionAdapter;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressWarnings("deprecation")
public class FragmentMyProfile extends Fragment implements OnClickListener {

	TextView tvEditProfile, tvManagePhotos, tvUserName, tvPhotoCount, tvStatus;
	Button btnEditUserName, btnAddPhoto, btnChangePassword;
	LinearLayout liAvilable, liUnAvilable, liMainView;
	private ProgressDialog pDialog;
	ConnectionDetector con;
	boolean error_status;
	String userName, viewType, saveType, profile_image;
	Dialog dialog;
	ImageView ivProfilePhoto;
	int user_id;

	private Uri mImageCaptureUri;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	public static byte[] image_Array;
	BitmapDrawable image;
	Bitmap photo;
	UserInfo info;
	AppController mSingleton;
	View rootView;
	JSONObject ViewResponse;
	Others others;
	String SelectedStatusText = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_my_profile, container,
				false);

		initiolize();

		return rootView;
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		pDialog = new ProgressDialog(getActivity());
		pDialog.setCancelable(false);
		con = new ConnectionDetector(getActivity());
		others = new Others(getActivity());
		info = new UserInfo();
		mSingleton = AppController.getInstance();
		user_id = BarHopperHomeScreen.user_id;

		tvEditProfile = (TextView) rootView.findViewById(R.id.tvEditProfile);
		tvManagePhotos = (TextView) rootView.findViewById(R.id.tvManagePhotos);
		btnEditUserName = (Button) rootView.findViewById(R.id.btnEditUserName);
		tvUserName = (TextView) rootView.findViewById(R.id.tvUserName);
		btnAddPhoto = (Button) rootView.findViewById(R.id.btnAddPhotos);
		ivProfilePhoto = (ImageView) rootView.findViewById(R.id.ivProfilePhoto);
		tvPhotoCount = (TextView) rootView.findViewById(R.id.tvPhotosCount);
		tvStatus = (TextView) rootView.findViewById(R.id.tvStatus);
		liMainView = (LinearLayout) rootView.findViewById(R.id.liMainView);

		btnAddPhoto.setOnClickListener(this);
		tvEditProfile.setOnClickListener(this);
		tvManagePhotos.setOnClickListener(this);
		btnEditUserName.setOnClickListener(this);
		tvStatus.setOnClickListener(this);
		try {
			viewType = "show";
			JSONObject obj = new JSONObject();
			obj.put("user_id", user_id);
			obj.put("mode", viewType);
			if (con.isConnectingToInternet()) {
				getData(obj);
			} else {
				con.failureAlert();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void getData(JSONObject dataReq) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_userData";
		others.showProgressWithOutMessage();
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_HOPPER_PROFILE, dataReq,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("hello", "I am at Responce block");
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								if (viewType.equals("show")) {
									handleResponseShow(response);
								} else if (viewType.equals("save")) {
									if (SelectedStatusText.isEmpty()
											|| SelectedStatusText == null)
										tvStatus.setText("");
									else
										tvStatus.setText(SelectedStatusText);
								}

							} else {

								String error_msg = response
										.getString("error_msg");
								Log.v("hello", error_msg.toString());
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("volley", "error: " + error);
						others.hideDialog();
						con.serverErrorAlert();
					}
				});
		jsObjRequest.setShouldCache(false);
		// Adding request to request queue
		jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);
	}

	private void handleResponseShow(JSONObject response) {
		// TODO Auto-generated method stub
		liMainView.setVisibility(View.VISIBLE);
		ViewResponse = response;
		try {
			String userProfileUrl = response.getString("profile_photo");
			userName = response.getString("username");
			String photos_count = response.getString("total_user_photos");
			String statusText = response.getString("hopping_status");

			tvUserName.setText(userName);
			tvPhotoCount.setText(photos_count);
			tvStatus.setText(statusText);
			// tvPhotoCount.setText(photos_count);
			if (!response.optString("profile_photo").isEmpty()) {
				Others.setImageAsBackground(userProfileUrl, ivProfilePhoto);
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tvEditProfile:
			Bundle b = new Bundle();
			b.putString("view_response", ViewResponse.toString());
			Others.navigation = "oneStepBack";
			FragmentEditMyProfile editProfile = new FragmentEditMyProfile();
			editProfile.setArguments(b);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, editProfile)
					.addToBackStack(null).commit();
			break;
		case R.id.tvManagePhotos:
			ManagePhotos userPhotos = new ManagePhotos();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, userPhotos).addToBackStack(null)
					.commit();
			break;
		case R.id.btnEditUserName:
			editUserName();
			break;
		case R.id.btnAddPhotos:
			/*
			 * UploadPhotoAll obj=new UploadPhotoAll(getActivity());
			 * obj.getPhoto();
			 */
			getPhoto();
			break;
		case R.id.tvStatus:
			StatusDialog();

		default:
			break;
		}

	}

	private void StatusDialog() {
		// TODO Auto-generated method stub
		dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.status_dialog);
		dialog.setTitle("Change Status");

		String statusText = tvStatus.getText().toString();

		final RadioGroup rgStatus = (RadioGroup) dialog
				.findViewById(R.id.rgStatus);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		Button btnSet = (Button) dialog.findViewById(R.id.btnSet);
		RadioButton rbBarName = (RadioButton) dialog.findViewById(R.id.rbBarName);
		RadioButton rbHome=(RadioButton)dialog.findViewById(R.id.rbHome);
		RadioButton rbBarHopping=(RadioButton)dialog.findViewById(R.id.rbBarHopping);
		
		
		if (statusText.equals("Home") || statusText.equals("Bar Hopping")) {
			Log.v("statuText",statusText);
			rbBarName.setVisibility(View.GONE);
			if(statusText.equals("Home"))
				rbHome.setChecked(true);
			else
				rbBarHopping.setChecked(true);
		}else{
			rbBarName.setVisibility(View.VISIBLE);
			rbBarName.setText(statusText);
			rbBarName.setChecked(true);
		}

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		btnSet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (rgStatus.getCheckedRadioButtonId() != -1) {
					dialog.cancel();
					int id = rgStatus.getCheckedRadioButtonId();
					View radioButton = rgStatus.findViewById(id);
					int radioId = rgStatus.indexOfChild(radioButton);
					RadioButton btn = (RadioButton) rgStatus
							.getChildAt(radioId);
					SelectedStatusText = (String) btn.getText();
					if (SelectedStatusText.equals("Home")
							|| SelectedStatusText.equals("Bar Hopping")) {
						try {
							viewType = "save";
							JSONObject obj = new JSONObject();
							obj.put("user_id", user_id);
							obj.put("mode", viewType);
							JSONObject response = new JSONObject();
							JSONObject profile = new JSONObject();
							profile.put("hopping_status", SelectedStatusText);
							response.put("profile", profile);
							obj.put("responses", response);
							if (con.isConnectingToInternet()) {
								getData(obj);
							} else {
								con.failureAlert();
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				} else {
					others.ToastMessage("Choose atleast one");
				}
			}
		});
		dialog.show();
	}

	private void editUserName() {
		// TODO Auto-generated method stub
		dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.edit_username);
		dialog.setTitle("Change Nickname");

		final EditText text = (EditText) dialog.findViewById(R.id.etUserName);
		text.setText(userName);
		Button btnCheckUserName = (Button) dialog
				.findViewById(R.id.btnCheckAvilability);
		Button btnRegistor = (Button) dialog.findViewById(R.id.btnRegistor);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		liAvilable = (LinearLayout) dialog.findViewById(R.id.liAvilable);
		liUnAvilable = (LinearLayout) dialog.findViewById(R.id.liUnAvilable);

		btnCheckUserName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (text.getText().toString().length() > 0) {
					try {
						JSONObject usernameObj = new JSONObject();
						usernameObj.put("username", text.getText().toString());
						checkUserName(usernameObj, R.id.btnCheckAvilability);
					} catch (Exception e) {
						// TODO: handle exception
					}

				} else {
					ToastMessage("Enter a valid username");
				}
			}
		});
		btnRegistor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (text.getText().toString().length() > 0) {
					try {
						userName = text.getText().toString();
						JSONObject usernameObj = new JSONObject();
						usernameObj.put("username", userName);
						checkUserName(usernameObj, R.id.btnRegistor);

					} catch (Exception e) {
						// TODO: handle exception
					}
				} else {
					ToastMessage("Enter a valid username");
				}
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
		dialog.show();
	}

	private void checkUserName(JSONObject userObj, final int viewId) {
		// TODO Auto-generated method stub

		String tag_string_req = "req_check_user_name";

		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_CHECK_USERNAME, userObj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								liAvilable.setVisibility(View.VISIBLE);
								liUnAvilable.setVisibility(View.GONE);
								if (viewId == R.id.btnRegistor) {

									editUserNameReq(userName);

									dialog.cancel();
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
						others.hideDialog();
						con.serverErrorAlert();
					}
				});
		jsObjRequest.setShouldCache(false);
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);
	}

	public void ToastMessage(String message) {
		Toast toast = Toast.makeText(getActivity().getApplicationContext(),
				message, Toast.LENGTH_SHORT);
		View view = toast.getView();
		view.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.toast_message_background));
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();

	}

	public void getPhoto() {

		final String[] items = new String[] { "Take from camera",
				"Select from gallery" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

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
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK)
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
				photo = extras.getParcelable("data");
				profile_image = encodeTobase64(photo);
				saveType = "photo";
				try {
					JSONObject obj = new JSONObject();
					obj.put("user_id", user_id);
					obj.put("mode", saveType);
					obj.put("photo", profile_image);
					obj.put("is_profile", 1);
					saveResponse(obj);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}

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
		List<ResolveInfo> list = getActivity().getPackageManager()
				.queryIntentActivities(intent, 0);
		int size = list.size();
		if (size == 0) {
			Toast.makeText(getActivity(), "Can not find image crop app",
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

					co.title = getActivity().getPackageManager()
							.getApplicationLabel(
									res.activityInfo.applicationInfo);
					co.icon = getActivity().getPackageManager()
							.getApplicationIcon(
									res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);
					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));
					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getActivity(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
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
							getActivity().getContentResolver().delete(
									mImageCaptureUri, null, null);
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

	private void editUserNameReq(final String userName) {
		// TODO Auto-generated method stub
		saveType = "username";
		JSONObject obj = new JSONObject();
		try {
			obj.put("user_id", user_id);
			obj.put("mode", saveType);
			obj.put("username", userName);
			saveResponse(obj);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void saveResponse(final JSONObject obj) {
		String tag_string_req = "req_set_user_name";
		others.showProgressWithOutMessage();
		Log.v("Photo_string", obj.toString());
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_PROFILEMAILSCREEN, obj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								if (saveType == "username") {
									tvUserName.setText(userName);
								} else if (saveType == "photo") {
									image = new BitmapDrawable(getResources(),
											photo);
									ivProfilePhoto.setImageBitmap(photo);
									int photo_count=Integer.parseInt(tvPhotoCount.getText().toString());
									photo_count=photo_count+1;
									tvPhotoCount.setText(photo_count+"");
								}
							} else {
								ToastMessage("Please try after some time");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("volley", "error: " + error);
						others.hideDialog();
						con.serverErrorAlert();
					}
				});
		jsObjRequest.setShouldCache(false);
		jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);
	}

}

package com.info.socialnetworking.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.test.mock.MockPackageManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.adapters.ManagePhotosAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.ManagePhotosDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.imageCrop.CropOption;
import com.info.socialnetworking.app.imageCrop.CropOptionAdapter;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class ManagePhotos extends Fragment implements OnItemClickListener,
		OnClickListener {

	View rootView;
	int user_id;

	private GridView mGridView;
	private ManagePhotosAdapter mGridAdapter;
	private ArrayList<ManagePhotosDetails> mGridData;
	ConnectionDetector con;
	Others others;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	public static byte[] image_Array;
	private Uri mImageCaptureUri;
	String profile_image, saveType;
	ImageView ivUploadPhoto;
	Bitmap photo;

	TextView tvwarningmessage;
	private File photoFile = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_manage_photos, container,
				false);
		initiolize();

		loadData();

		mGridView.setOnItemClickListener(this);

		return rootView;
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		con = new ConnectionDetector(getActivity());
		others = new Others(getActivity());
		Others.navigation = "oneStepBack";
		mGridView = (GridView) rootView.findViewById(R.id.gvManagePhotos);
		ivUploadPhoto = (ImageView) rootView.findViewById(R.id.ivUploadPhotos);
		ivUploadPhoto.setOnClickListener(this);
		user_id = BarHopperHomeScreen.user_id;
		mGridData = new ArrayList<>();
		tvwarningmessage = (TextView) rootView
				.findViewById(R.id.tvWarningMessage);
	}

	private void loadData() {
		// TODO Auto-generated method stub
		try {
			JSONObject images = new JSONObject();
			images.put("user_id", user_id);
			images.put("mode", "show");
			if (con.isConnectingToInternet()) {
				getImagesResults(images);
			} else {
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void getImagesResults(JSONObject bars) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_manage_photos";
		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_HOPPER_PROFILE, bars,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								loadInitialImages(response);
								mGridAdapter = new ManagePhotosAdapter(
										getActivity(),
										R.layout.grid_item_photo, mGridData);
								mGridView.setAdapter(mGridAdapter);

							} else {

								others.ToastMessage(response
										.getString("error_msg"));

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

	private void loadInitialImages(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			JSONArray photos = response.getJSONArray("user_photos");

			for (int i = 0; i < photos.length(); i++) {

				JSONObject pics = photos.getJSONObject(i);
				String imageUrl = pics.getString("image");
				String is_blocked = pics.getString("is_blocked");

				// Log.v("imagedata",imageUrl);
				// Log.v("imagedata",is_blocked);

				if (is_blocked.equals("1")) {
					tvwarningmessage.setVisibility(View.VISIBLE);
				}

				mGridData.add(new ManagePhotosDetails(imageUrl, is_blocked));
				// Log.v("gridData"+i, mGridData.get(i).getImage());
			}

			// mGridAdapter.setGridData(mGridData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub

		// Log.v("msgonclick","positionresponse");
		Bundle obj = new Bundle();
		FullScreenImageView friends = new FullScreenImageView(mGridData,
				position);
		obj.putString("fullImageNavigation", "ManagePhotos");
		friends.setArguments(obj);
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flContainer, friends).addToBackStack(null)
				.commit();
	}

	// upload photo listener on button click
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		getPhoto();

	}

	private void getPhoto() {
		// TODO Auto-generated method stub
		final String[] items = new String[] { "Take from  camera",
				"Select from gallery" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// pick from camera
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

	private File createImageFile() throws IOException {
		String imageFileName = "JPEG_" + "_";
		File storageDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		Log.v("pic", storageDir.toString());
		return File.createTempFile(imageFileName, ".jpg", storageDir);
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
					obj.put("is_profile", 0);
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

	private void saveResponse(JSONObject obj) {
		// TODO Auto-generated method stub
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
								reload();

							} else {
								others.ToastMessage("Please try after some time");
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

	protected void reload() {
		// TODO Auto-generated method stub
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.detach(this).attach(this).commit();
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
			// intent.putExtra("circleCrop", new String(""));
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

}

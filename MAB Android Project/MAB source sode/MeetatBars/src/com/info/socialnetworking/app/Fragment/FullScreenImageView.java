package com.info.socialnetworking.app.Fragment;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.adapters.FullScreenImageAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.ManagePhotosDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class FullScreenImageView extends Fragment implements OnClickListener {

	View rootView;
	private ViewPager pager;
	ImageView ivDelete;
	ManagePhotosDetails photos;
	FullScreenImageAdapter adapter;
	int position;
	Others others; 
	ConnectionDetector con;
	TextView tvMakeAsProfile;
	
	private ArrayList<ManagePhotosDetails> mGridData = new ArrayList<ManagePhotosDetails>();
	
	
	
	public FullScreenImageView(ArrayList<ManagePhotosDetails> mGridData,
			int position) {
		// TODO Auto-generated constructor stub
		this.mGridData = mGridData;  
		this.position = position;
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_full_screen_image,
				container, false);
		initiolize();
		savedInstanceState = getArguments();
		adapter = new FullScreenImageAdapter(getActivity(), mGridData);
		pager.setAdapter(adapter);
		
		if (savedInstanceState.getString("fullImageNavigation").equals(
				"OtherUserPhotos")) {
			ivDelete.setVisibility(View.GONE);
			tvMakeAsProfile.setVisibility(View.GONE);
		} else if (savedInstanceState.getString("fullImageNavigation").equals(
				"ManagePhotos")) {
			ivDelete.setVisibility(View.VISIBLE);
			tvMakeAsProfile.setVisibility(View.VISIBLE);
			
		}
		else if (savedInstanceState.getString("fullImageNavigation").equals(
				"barPhotos")) {
			ivDelete.setVisibility(View.GONE);
			tvMakeAsProfile.setVisibility(View.GONE);
			Others.navigation="barProfile";
		}
		ivDelete.setOnClickListener(this);
		// displaying selected image first
		pager.setCurrentItem(position);
		return rootView;
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		pager = (ViewPager) rootView.findViewById(R.id.pager);
		ivDelete = (ImageView) rootView.findViewById(R.id.ivDelete);
		tvMakeAsProfile = (TextView) rootView
				.findViewById(R.id.tvMakeAsProfile);
		tvMakeAsProfile.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivDelete:
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("Are you sure to delete this photo?")
					.setCancelable(false)
					.setPositiveButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// Action for 'NO' Button
									dialog.cancel();
								}
							}).setNegativeButton("Yes",

					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							deleteUrlCall();
						}
					});
			// Creating dialog box
			AlertDialog alert = builder.create();
			alert.setCanceledOnTouchOutside(true);
			// Setting the title manually
			alert.setTitle("Delete Photo");
			alert.show();

			break;
		case R.id.tvMakeAsProfile:
			try {
				con = new ConnectionDetector(getActivity());
				others = new Others(getActivity());

				JSONObject obj = new JSONObject();
				obj.put("user_id", BarHopperHomeScreen.user_id);
				obj.put("photo_url", mGridData.get(pager.getCurrentItem())
						.getImage());
				obj.put("profile_type", "user");
				if (con.isConnectingToInternet()) {
					makeAsProfile(obj);
				} else {
					con.failureAlert();
				}

			} catch (Exception e) {
				// TODO: handle exception 
				e.printStackTrace();
			}
		default:
			break;
		} 

	}

	private void makeAsProfile(JSONObject obj) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_make_as_profile";
		others.showProgressWithOutMessage();
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_SET_PROFILE_PICTURE, obj,  
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								Toast.makeText(getActivity(),
										"Profile picture has been changed",
										Toast.LENGTH_SHORT).show();
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

	protected void deleteUrlCall() {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		try {
			con = new ConnectionDetector(getActivity());
			others = new Others(getActivity());

			JSONObject obj = new JSONObject();
			obj.put("user_id", BarHopperHomeScreen.user_id);
			obj.put("mode", "delete_photo");
			obj.put("photo", mGridData.get(pager.getCurrentItem()).getImage());
			if (con.isConnectingToInternet()) {
				deleteImage(obj);
			} else {
				con.failureAlert();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void deleteImage(JSONObject obj) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_manage_photos";
		others.showProgressWithOutMessage();
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_PROFILEMAILSCREEN, obj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								getFragmentManager().popBackStack();
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
	
	
}

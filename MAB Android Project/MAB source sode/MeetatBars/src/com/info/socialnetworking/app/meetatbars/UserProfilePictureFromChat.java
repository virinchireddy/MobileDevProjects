package com.info.socialnetworking.app.meetatbars;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.adapters.ManagePhotosAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.ExpandableHeightGridView;
import com.info.socialnetworking.app.helper.ManagePhotosDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.network.ConnectionDetector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class UserProfilePictureFromChat extends Activity implements OnItemClickListener{

	ConnectionDetector con;
	Others others;
	private GridView mGridView;
	String user_id;
	public static ArrayList<ManagePhotosDetails> mGridData;
	private ManagePhotosAdapter mGridAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_other_user_photos);
		savedInstanceState=getIntent().getExtras();
		user_id=savedInstanceState.getString("user_id");
		ExpandableHeightGridView.expanded=false;
		initiolize();
		loadData();
		mGridView.setOnItemClickListener(this);
		
	}
	

	private void loadData() {
		// TODO Auto-generated method stub
		try {
			JSONObject images = new JSONObject();
			images.put("user_id", user_id);
			images.put("mode", "show");
			if (con.isConnectingToInternet()) {
				getImagesResults(images);
			}else{
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void getImagesResults(JSONObject images) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_manage_photos";
		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_HOPPER_PROFILE, images,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								loadInitialImages(response);
								mGridAdapter = new ManagePhotosAdapter(UserProfilePictureFromChat.this,R.layout.grid_item_photo, mGridData);
								mGridView.setAdapter(mGridAdapter);
							} else {
								others.ToastMessage(response.getString("error_msg"));
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

	protected void loadInitialImages(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			JSONArray photos = response.getJSONArray("user_photos");
			for (int i = 0; i < photos.length(); i++) {
				mGridData.add(new ManagePhotosDetails(photos.get(i).toString(),"0"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		con = new ConnectionDetector(UserProfilePictureFromChat.this);
		others = new Others(UserProfilePictureFromChat.this);
		mGridView = (GridView) findViewById(R.id.gvOtherUserPhotos);
		mGridData = new ArrayList<>();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// TODO Auto-generated method stub
		/*Bundle obj=new Bundle();
		FullScreenImageView friends = new FullScreenImageView(mGridData,
				position);
		obj.putString("fullImageNavigation","OtherUserPhotos");
		friends.setArguments(obj);
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flContainer, friends).addToBackStack(null)
				.commit();*/
		
		Intent i=new Intent(getApplicationContext(),FullScreenImageViewFromChat.class);
		i.putExtra("fullImageNavigation","OtherUserPhotos");
		//i.putExtra("Photos", mGridData);
		i.putExtra("position", position);
		startActivity(i);
		
	}
	
	
	public void back(View v)
	{
		onBackPressed();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
}

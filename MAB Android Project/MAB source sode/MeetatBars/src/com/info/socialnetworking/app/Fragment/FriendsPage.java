package com.info.socialnetworking.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.adapters.FriendsAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.FriendsDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;


public class FriendsPage extends Fragment implements OnItemClickListener {

	View rootView;

	private GridView mGridView;
	public FriendsAdapter mAdapter;

	private List<FriendsDetails> friendsList = null;
	private TextView tvEmptyView;

	private int user_id;

	private int total_records, limit = 100;
	private String nextPageNumber = "initi";

	EditText etFriendName;

	ImageView ivFriendSearch;

	Others others;
	CurrentLocation location;
	ConnectionDetector con;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_friends_page, container,
				false);
		initiolize();
		loadData();
		// listening friends item click by user
		mGridView.setOnItemClickListener(this);
		return rootView;
	}

	private void loadData() {
		// TODO Auto-generated method stub
		
		// sending search criteria to server for find friends
		try {
			JSONObject bars = new JSONObject();
			bars.put("user_id", user_id);
			if (location.canGetLocation()) {
				bars.put("latitude", location.getLatitude());
				bars.put("longitude", location.getLongitude());
			}
			bars.put("criteria", "friends");
			bars.put("limit", limit);
			if (con.isConnectingToInternet()) {
				getFriendResults(bars);
			}else{
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// Rest API call 
	private void getFriendResults(JSONObject bars) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_Bars";
		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_MAIN_MENU, bars,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

									loadInitialFriends(response);

									// create an Object for Adapter
									mAdapter = new FriendsAdapter(friendsList,
											getActivity());

									// set the adapter object to the
									// Recyclerview
									mGridView.setAdapter(mAdapter);
									// mAdapter.notifyDataSetChanged();
									
									// if friends list is empty showing message 
									if (friendsList.isEmpty()) {
										mGridView.setVisibility(View.GONE);
										tvEmptyView.setVisibility(View.VISIBLE);

									} else {
										mGridView.setVisibility(View.VISIBLE);
										tvEmptyView.setVisibility(View.GONE);
									}
								
							} else {
								String message = response
										.getString("error_msg");
								if (message
										.equals("No friends are found near you")) {
									mGridView.setVisibility(View.GONE);
									tvEmptyView.setVisibility(View.VISIBLE);
								} else {
									others.ToastMessage(message);
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
						others.hideDialog();
						con.serverErrorAlert();
					}
				});
		// removing the cache from url
		jsObjRequest.setShouldCache(false);
		// retry policy if server is not responding 
		jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);

	}

	private void loadInitialFriends(JSONObject response) {
		// TODO Auto-generated method stub
		try {

			Log.v("output responce", response.toString());
			total_records = response.getInt("total_users");
			JSONArray FriendsData = response.optJSONArray("responses");
			
			if (total_records < limit) {
				limit = total_records;
			}
			for (int i = 0; i < limit; i++) {
				JSONObject singleUser = FriendsData.getJSONObject(i);
				String user_id = singleUser.getString("user_id");
				String user_name = optString(singleUser, "username");
				String is_online=optString(singleUser, "is_online");
				String age = optString(singleUser, "age");
				
				// if age is not entered hide the view
				if (!age.equals(""))
					age = ", " + age;
				Double user_distance=singleUser.getDouble("user_distance");
				
				String distance = Others.round(user_distance, 2)+"";
				
				// converting distance from miles to feats if it is less then 200 fts
				if (Float.parseFloat(distance) < 1) {

					float distance_float = (Float.parseFloat(distance) * 5280);
					int distance_int = (int) distance_float;
					if (distance_int <= 200) {
						distance = 200 + " fts";
					} else {
						distance = distance_int + " fts";
					}

				} else {
					distance = distance + " mi";
				}

				String city = optString(singleUser, "state");
				// if city is not entered hide the view
				if (!city.equals(""))
					city = ", " + city;
				String status = optString(singleUser, "hopping_status");
				String imageUrl = singleUser.getString("user_photo");

				String[] finalString = { user_id, user_name, age, distance,
						city, status, imageUrl,is_online };
				// if user name is not avilabel don't display them in friends list
				if (user_name == "" || user_name.isEmpty()) {

				} else {
					friendsList.add(new FriendsDetails(finalString));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static String optString(JSONObject json, String key) {
		if (json.isNull(key) || json.optString(key).equals("-1")
				|| json.optString(key).equals("-1.00"))
			return "";
		else
			return json.optString(key);
	}

	// Initializing the views 
	private void initiolize() {
		// TODO Auto-generated method stub
		con = new ConnectionDetector(getActivity());
		mGridView = (GridView) rootView.findViewById(R.id.gvFriendsPage);
		tvEmptyView = (TextView) rootView.findViewById(R.id.tvEmptyView);
		friendsList = new ArrayList<FriendsDetails>();
		user_id = BarHopperHomeScreen.user_id;
		others = new Others(getActivity());
		location = new CurrentLocation(getActivity());

	}

	// open clicked friend
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		bundle.putString("user_id", friendsList.get(position).getUserId());
		bundle.putString("Navigation","Friends");
		FragmentUserProfile user = new FragmentUserProfile();
		user.setArguments(bundle);
		this.getFragmentManager().beginTransaction()
		.replace(R.id.flContainer, user)
		.commit();
		
	}

}

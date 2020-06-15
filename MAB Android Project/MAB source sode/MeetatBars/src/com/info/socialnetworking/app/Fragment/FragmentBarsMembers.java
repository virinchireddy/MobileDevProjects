package com.info.socialnetworking.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.adapters.BarMembersAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.BarMembersDetails;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.ExpandableHeightGridView;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentBarsMembers extends Fragment implements
		OnItemClickListener {

	View rootView;

	private ExpandableHeightGridView mGridView;
	public BarMembersAdapter mAdapter;

	private List<BarMembersDetails> membersList = null;
	private TextView tvEmptyView;

	private int user_id;
	String bar_id;

	private String nextPageNumber = "initi";

	Others others;
	CurrentLocation location;
	ConnectionDetector con;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_bars_members,
				container, false);
		ExpandableHeightGridView.expanded=true;
		initiolize();
		loadData();
		mGridView.setOnItemClickListener(this);
		return rootView;
	}

	private void loadData() {
		// TODO Auto-generated method stub
		try {
			JSONObject barMembers = new JSONObject();
			barMembers.put("user_id", user_id);
			barMembers.put("bar_id", bar_id);
			if (location.canGetLocation()) {
				barMembers.put("latitude", location.getLatitude());
				barMembers.put("longitude", location.getLongitude());
			}
			barMembers.put("criteria", "members");
			if (con.isConnectingToInternet()) {
				getBarMembers(barMembers);
			}else{
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void getBarMembers(JSONObject barMembers) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_barMembers";
		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_BAR_PROFILE, barMembers,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								if (nextPageNumber.equals("initi")) {
									Log.v("String", response.toString());
									loadInitialFriends(response);

									// create an Object for Adapter
									mAdapter = new BarMembersAdapter(
											membersList, getActivity());

									// set the adapter object to the
									// Recyclerview
									mGridView.setAdapter(mAdapter);
									// mAdapter.notifyDataSetChanged();

									if (membersList.isEmpty()) {
										mGridView.setVisibility(View.GONE);
										tvEmptyView.setVisibility(View.VISIBLE);

									} else {
										mGridView.setVisibility(View.VISIBLE);
										tvEmptyView.setVisibility(View.GONE);
									}
								}
							} else {
								String message = response
										.getString("error_msg");
								Toast.makeText(getActivity(), message,Toast.LENGTH_SHORT).show();
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
		jsObjRequest.setShouldCache(false);
		jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);

	}

	protected void loadInitialFriends(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			JSONArray visinityUserData = response
					.optJSONArray("vicinity_users");
			for (int i = 0; i < visinityUserData.length(); i++) {
				JSONObject singleUser = visinityUserData.getJSONObject(i);
				String user_id = singleUser.getString("arr_user_id");
				String user_name = optString(singleUser, "username");
				String user_image = optString(singleUser, "arr_user_image");
				String is_online = optString(singleUser, "is_online");
				String[] finalString = { user_id, user_name, user_image,is_online};
				membersList.add(new BarMembersDetails(finalString));
			}
			JSONArray checkin_users=response.optJSONArray("checkin_users");
			for(int i=0;i<checkin_users.length();i++)
			{
				JSONObject singleUser = checkin_users.getJSONObject(i);
				String user_id = singleUser.getString("arr_user_id");
				String user_name = optString(singleUser, "username");
				String user_image = optString(singleUser, "arr_user_image");
				String is_online = optString(singleUser, "is_online");
				String[] finalString = { user_id, user_name, user_image,is_online};
				membersList.add(new BarMembersDetails(finalString));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		con = new ConnectionDetector(getActivity());
		mGridView = (ExpandableHeightGridView) rootView.findViewById(R.id.gvBarMembers);
		mGridView.setExpanded(true);

		tvEmptyView = (TextView) rootView.findViewById(R.id.tvEmptyView);
		membersList = new ArrayList<BarMembersDetails>();
		user_id = BarHopperHomeScreen.user_id;
		bar_id=FragmentBarsProfileFb.bar_id;
		others = new Others(getActivity());
		location = new CurrentLocation(getActivity());
	}

	public static String optString(JSONObject json, String key) {
		// http://code.google.com/p/android/issues/detail?id=13830
		if (json.isNull(key) || json.optString(key).equals("-1")
				|| json.optString(key).equals("-1.00"))
			return "";
		else
			return json.optString(key);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		BarHopperHomeScreen.BarId=bar_id;
		BarHopperHomeScreen.barProfileNavigation="barMembers";
		Bundle bundle = new Bundle();
		bundle.putString("user_id", membersList.get(position).getUserId());
		bundle.putString("Navigation","barProfile");
		FragmentUserProfile user = new FragmentUserProfile();
		user.setArguments(bundle);
		getActivity().getFragmentManager().beginTransaction()
		.replace(R.id.flContainer, user)
		.commit();
	}
}
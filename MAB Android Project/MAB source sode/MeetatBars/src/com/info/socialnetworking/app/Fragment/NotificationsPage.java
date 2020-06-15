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
import com.info.socialnetworking.app.adapters.NotificationsAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.DividerItemDecoration;
import com.info.socialnetworking.app.helper.NotificationDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NotificationsPage extends Fragment {

	View rootView;
	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	Others others;
	CurrentLocation location;
	ConnectionDetector con;
	TextView tvEmptyView;
	public NotificationsAdapter mAdapter;
	private List<NotificationDetails> notificationList = null;

	private int total_records, limit = 100;
	int user_id;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_notifications_page,
				container, false);
		initiolize();
		loadData();
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getActivity());
		// use a linear layout manager
		mRecyclerView.setLayoutManager(mLayoutManager);

		return rootView;
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		con = new ConnectionDetector(getActivity());
		others = new Others(getActivity());
		Others.navigation = Others.NavigationDefaultValue;
		user_id = BarHopperHomeScreen.user_id;
		notificationList = new ArrayList<NotificationDetails>();
		mRecyclerView = (RecyclerView) rootView
				.findViewById(R.id.rvNotifications);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(
				getActivity(), DividerItemDecoration.VERTICAL_LIST));
		tvEmptyView = (TextView) rootView.findViewById(R.id.tvEmptyView);

	}

	private void loadData() {
		// TODO Auto-generated method stub
		try {
			JSONObject notifications = new JSONObject();
			notifications.put("user_id", user_id);
			notifications.put("limit", limit);
			if (con.isConnectingToInternet()) {
				getNotifications(notifications);
			}else{
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void getNotifications(JSONObject bars) {

		String tag_string_req = "req_Notifications";
		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_NOTIFICATIONS, bars,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								loadNotifications(response);

								// create an Object for Adapter
								mAdapter = new NotificationsAdapter(
										notificationList, mRecyclerView,
										getActivity());

								mRecyclerView.setAdapter(mAdapter);
								// mAdapter.notifyDataSetChanged();

								if (notificationList.isEmpty()) {
									mRecyclerView.setVisibility(View.GONE);
									tvEmptyView.setVisibility(View.VISIBLE);

								} else {
									mRecyclerView.setVisibility(View.VISIBLE);
									tvEmptyView.setVisibility(View.GONE);
								}
							} else {
								if (notificationList.isEmpty()) {
									mRecyclerView.setVisibility(View.GONE);
									tvEmptyView.setVisibility(View.VISIBLE);
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

	private void loadNotifications(JSONObject response) {
		// TODO Auto-generated method stub
		try {

			Log.v("output responce", response.toString());
			total_records = response.getInt("total_notifications");

			JSONArray meetOthersData = response.optJSONArray("responses");
			// nextPageNumber = response.getString("page");
			if (total_records < limit) {
				limit = total_records;
			}

			// for pofile completion dialog
			for (int i = 0; i < meetOthersData.length(); i++) {
				JSONObject singleUser = meetOthersData.getJSONObject(i);
				String user_id = singleUser.getString("user_id");
				String user_name = optString(singleUser, "username");
				String imageUrl = singleUser.getString("user_photo");
				String message = optString(singleUser, "message");
				String time=optString(singleUser, "notification_time");
				String declineStatus=optString(singleUser,"decline_status");

				String[] finalString = { user_id, user_name, imageUrl, message,declineStatus,time };
				if (user_name == "" || user_name.isEmpty()) {

				} else {
					notificationList.add(new NotificationDetails(finalString));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static String optString(JSONObject json, String key) {
		// http://code.google.com/p/android/issues/detail?id=13830
		if (json.isNull(key) || json.optString(key).equals("-1")
				|| json.optString(key).equals("-1.00"))
			return "";
		else
			return json.optString(key);
	}
}

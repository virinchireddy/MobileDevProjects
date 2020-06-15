package com.info.socialnetworking.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.adapters.ChatListAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.ChatListDetails;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.DividerItemDecoration;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class ChatListPage extends Fragment{
	
	View rootView;
	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	Others others;
	CurrentLocation location;
	ConnectionDetector con;
	TextView tvEmptyView;
	public static ChatListAdapter mAdapter;
	public static List<ChatListDetails> chatList = null;

	private int total_records, limit = 100;
	int user_id;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.chat_list_page,
				container, false);
		initiolize();
		loadData();
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getActivity());
		// use a linear layout manager
		mRecyclerView.setLayoutManager(mLayoutManager);

		return rootView;
	}

	private void loadData() {
		// TODO Auto-generated method stub
		try {
			JSONObject chat = new JSONObject();
			chat.put("user_id", user_id);
			chat.put("limit", limit);
			if (con.isConnectingToInternet()) {
				getChatList(chat);
			}else{
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void getChatList(JSONObject chat) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_ChatList";
		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_CHAT_MAINSCREEN, chat,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								loadChatList(response);

								// create an Object for Adapter
								mAdapter = new ChatListAdapter(
										chatList, mRecyclerView,
										getActivity());

								mRecyclerView.setAdapter(mAdapter);
								// mAdapter.notifyDataSetChanged();

								if (chatList.isEmpty()) {
									mRecyclerView.setVisibility(View.GONE);
									tvEmptyView.setVisibility(View.VISIBLE);

								} else {
									mRecyclerView.setVisibility(View.VISIBLE);
									tvEmptyView.setVisibility(View.GONE);
								}
							} else {
								if (chatList.isEmpty()) {
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

	protected void loadChatList(JSONObject response) {
		// TODO Auto-generated method stub
		try {

			Log.v("output responce", response.toString());
			//total_records = response.getInt("total_notifications");

			JSONArray chatData = response.optJSONArray("messages");
			// nextPageNumber = response.getString("page");
			/*if (total_records < limit) {
				limit = total_records;
			}*/

			// for pofile completion dialog
			for (int i = 0; i < chatData.length(); i++) {
				JSONObject singleUser = chatData.getJSONObject(i);
				String friend_id = singleUser.getString("friend_id");
				String friend_username = optString(singleUser, "friend_username");
				String friend_pic = singleUser.getString("friend_pic");
				String message = optString(singleUser, "message");
				String unreadCount=optString(singleUser, "unread_count");

				String[] finalString = { friend_id, friend_username, friend_pic, message,unreadCount };
					Log.v("finalstring",Arrays.toString(finalString));
					chatList.add(new ChatListDetails(finalString));
				
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
	
	private void initiolize() {
		// TODO Auto-generated method stub
		con = new ConnectionDetector(getActivity());
		others = new Others(getActivity());
		Others.navigation = Others.NavigationDefaultValue;
		user_id = BarHopperHomeScreen.user_id;
		chatList = new ArrayList<ChatListDetails>();
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvChatList);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(
				getActivity(), DividerItemDecoration.VERTICAL_LIST));
		tvEmptyView = (TextView) rootView.findViewById(R.id.tvEmptyView);
	}
	
}

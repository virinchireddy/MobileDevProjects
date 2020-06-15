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
import com.info.socialnetworking.app.adapters.BlockListAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.BlockListDetails;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.DividerItemDecoration;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class BlockListPage extends Fragment{

	View rootView;
	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	Others others;
	CurrentLocation location;
	ConnectionDetector con;
	TextView tvEmptyView;
	public BlockListAdapter mAdapter;
	private List<BlockListDetails> blockList = null;

	int user_id;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_blocklist_page,
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
			JSONObject blockList = new JSONObject();
			blockList.put("user_id", user_id);
			blockList.put("mode", "show");
			if (con.isConnectingToInternet()) {
				getBlockList(blockList);
			}else{
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void getBlockList(JSONObject blockList2) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_BlockList";
		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_BLOCK_LIST, blockList2,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								String message=response.getString("status_msg");
								if(message.equals("No users are blocked"));
								else
								loadBlockList(response);

								// create an Object for Adapter
								mAdapter = new BlockListAdapter(
										blockList, mRecyclerView,
										getActivity());
								mRecyclerView.setAdapter(mAdapter);
								// mAdapter.notifyDataSetChanged();
								if (blockList.isEmpty()) {
									mRecyclerView.setVisibility(View.GONE);
									tvEmptyView.setVisibility(View.VISIBLE);
								} else {
									mRecyclerView.setVisibility(View.VISIBLE);
									tvEmptyView.setVisibility(View.GONE);
								}
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
	protected void loadBlockList(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			JSONArray blockedListArray=response.optJSONArray("blocked_users");
			for(int i=0;i<blockedListArray.length();i++)
			{
				JSONObject singleUser=blockedListArray.getJSONObject(i);
				String user_id=singleUser.getString("user_id");
				String user_image=singleUser.getString("user_image");
				String userName=singleUser.getString("username");
				String timeStamp=singleUser.getString("block_time");
				String[] finalString = { user_id, userName, user_image, timeStamp };
				Log.v("am here",Arrays.toString(finalString));

				blockList.add(new BlockListDetails(finalString));
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		con = new ConnectionDetector(getActivity());
		others = new Others(getActivity());
		user_id = BarHopperHomeScreen.user_id;
		blockList = new ArrayList<BlockListDetails>();
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvBlockList);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
		tvEmptyView = (TextView) rootView.findViewById(R.id.tvEmptyView);
	}

}

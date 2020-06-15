package com.info.socialnetworking.app.meetatbars;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.adapters.CheckAvilableBarsAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.CheckAvilableBarsDetails;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.DividerItemDecoration;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class CheckAvilableBars extends Activity implements OnClickListener {

	private RecyclerView mRecyclerView;
	public CheckAvilableBarsAdapter mAdapter;

	private LinearLayoutManager mLayoutManager;

	private List<CheckAvilableBarsDetails> checkBarsList = null;
	private TextView tvEmptyView;

	protected Handler handler;

	public static int user_id;

	private int total_records, limit = 100;
	private String nextPageNumber = "initi";
	String barName, navigation;

	Others others;
	CurrentLocation location;
	ConnectionDetector con;
	ImageView ivCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_avilable_bars);
		savedInstanceState = getIntent().getExtras();
		barName = savedInstanceState.getString("bar_name");
		user_id = savedInstanceState.getInt("user_id");
		navigation = savedInstanceState.getString("navigation");

		Log.v("user_id", user_id + "");

		initiolize();
		loadData();
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(this);
		// use a linear layout manager
		mRecyclerView.setLayoutManager(mLayoutManager);

	}

	private void loadData() {
		// TODO Auto-generated method stub
		try {
			JSONObject bars = new JSONObject();
			bars.put("user_id", user_id);
			if (location.canGetLocation()) {
				bars.put("latitude", location.getLatitude());
				bars.put("longitude", location.getLongitude());
			}

			bars.put("criteria", "bars");
			bars.put("bar_name", barName);
			bars.put("limit", limit);
			if (con.isConnectingToInternet()) {
				Log.v("request", bars.toString());

				getBarsResults(bars);
			} else {
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void getBarsResults(JSONObject bars) {
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

								loadInitialHoppers(response);

								// create an Object for Adapter
								mAdapter = new CheckAvilableBarsAdapter(
										checkBarsList, mRecyclerView,
										CheckAvilableBars.this, navigation);																																	

								// set the adapter object to the
								// Recyclerview
								mRecyclerView.setAdapter(mAdapter);
								// mAdapter.notifyDataSetChanged();

								if (checkBarsList.isEmpty()) {
									mRecyclerView.setVisibility(View.GONE);
									tvEmptyView.setVisibility(View.VISIBLE);

								} else {
									mRecyclerView.setVisibility(View.VISIBLE);
									tvEmptyView.setVisibility(View.GONE);
								}
							} else {
								mRecyclerView.setVisibility(View.GONE);
								tvEmptyView.setVisibility(View.VISIBLE);
								// others.ToastMessage(response.getString("error_msg"));
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

	private void loadInitialHoppers(JSONObject response) {
		// TODO Auto-generated method stub
		try {

			total_records = response.getInt("total_bars");
			// Log.v("checkinStatus", successCheckInBarId);
			JSONArray barsData = response.optJSONArray("responses");
			if (total_records < limit) {
				limit = total_records;
			}
			for (int i = 0; i < limit; i++) {
				JSONObject singleBar = barsData.getJSONObject(i);
				Log.v("response6", singleBar.toString());

				String bar_id = singleBar.getString("bar_id");
				String bar_name = singleBar.getString("bar_name");
				String bar_address = singleBar.getString("bar_address");
				if (bar_address.equals("null"))
					bar_address = "";
				String owner_id = singleBar.getString("owner_id");
				String bar_image = singleBar.getString("bar_image");
				// if (owner_id.equals("null")) {
				String[] finalBarsString = new String[5];
				finalBarsString[0] = bar_id;
				finalBarsString[1] = bar_name;
				finalBarsString[2] = bar_address;
				finalBarsString[3] = owner_id;
				finalBarsString[4] = bar_image;
				// Log.v("finalString",Arrays.toString(finalBarsString));
				checkBarsList
						.add(new CheckAvilableBarsDetails(finalBarsString));
				// }
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initiolize() {

		con = new ConnectionDetector(this);

		mRecyclerView = (RecyclerView) findViewById(R.id.rvCheckAvilableBars);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
				DividerItemDecoration.VERTICAL_LIST));
		tvEmptyView = (TextView) findViewById(R.id.tvEmptyView);
		ivCancel = (ImageView) findViewById(R.id.ivCancel);
		ivCancel.setOnClickListener(this);
		checkBarsList = new ArrayList<CheckAvilableBarsDetails>();
		handler = new Handler();
		others = new Others(this);
		location = new CurrentLocation(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		finish();
	}
}

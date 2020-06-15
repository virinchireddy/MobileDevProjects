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
import com.info.socialnetworking.app.adapters.PromotionsAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.DividerItemDecoration;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.helper.PromotionsDetails;
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

public class PromotionsPage extends Fragment {

	View rootView;
	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	Others others;
	CurrentLocation location;
	ConnectionDetector con;
	TextView tvEmptyView;
	public PromotionsAdapter mAdapter;
	private List<PromotionsDetails> promotionsList = null;

	private int total_records, limit = 100;
	int user_id;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_promotions_page,
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
		location = new CurrentLocation(getActivity());

		Others.navigation = Others.NavigationDefaultValue;
		user_id = BarHopperHomeScreen.user_id;
		promotionsList = new ArrayList<PromotionsDetails>();
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvPromotions);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(
				getActivity(), DividerItemDecoration.VERTICAL_LIST));
		tvEmptyView = (TextView) rootView.findViewById(R.id.tvEmptyView);
	}

	private void loadData() {
		// TODO Auto-generated method stub
		try {
			JSONObject promotions = new JSONObject();
			promotions.put("user_id", user_id);

			if (location.canGetLocation()) {
				promotions.put("latitude", location.getLatitude());
				promotions.put("longitude", location.getLongitude());
			}

			promotions.put("limit", limit);
			promotions.put("criteria", "barpromotions");

			if (con.isConnectingToInternet()) {
				getPromotionsResults(promotions);
			}else{
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void getPromotionsResults(JSONObject promotions) {
		// TODO Auto-generated method stub

		String tag_string_req = "req_Notifications";
		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_MAIN_MENU, promotions,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								loadPromotions(response);

								// create an Object for Adapter
								mAdapter = new PromotionsAdapter(
										promotionsList, mRecyclerView,
										getActivity());

								mRecyclerView.setAdapter(mAdapter);
								// mAdapter.notifyDataSetChanged();

								if (promotionsList.isEmpty()) {
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

	protected void loadPromotions(JSONObject response) {
		// TODO Auto-generated method stub

		try {

			Log.v("output responce", response.toString());
			total_records = response.getInt("total_bars");

			JSONArray promotionsData = response.optJSONArray("responses");
			// nextPageNumber = response.getString("page");
			if (total_records < limit) {
				limit = total_records;
			}

			// for pofile completion dialog
			for (int i = 0; i < limit; i++) {
				JSONObject singlePromotions = promotionsData.getJSONObject(i);
				String bar_id = singlePromotions.getString("bar_id");
				String bar_name = optString(singlePromotions, "bar_name");
				String promotion_image = singlePromotions
						.getString("promotion_image");
				String promotion_title = optString(singlePromotions,
						"promotion_title");
				String promotion_description = optString(singlePromotions,
						"promotion_description");
				String city = optString(singlePromotions, "bar_address");
				String promotion_id=optString(singlePromotions,"promotion_id");
				String promotion_type=optString(singlePromotions,"type");

				String[] finalString = { bar_id, bar_name, city,
						promotion_title, promotion_description, promotion_image,promotion_id,promotion_type};
				

				promotionsList.add(new PromotionsDetails(finalString));

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

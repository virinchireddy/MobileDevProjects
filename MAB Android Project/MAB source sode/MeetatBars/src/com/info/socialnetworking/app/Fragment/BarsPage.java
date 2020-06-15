package com.info.socialnetworking.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.adapters.BarsAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.BarsDetails;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.MainMenu;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class BarsPage extends Fragment implements OnClickListener {

	View rootView;

	int counter;
	int page = 1;
	private RecyclerView mRecyclerView;
	private BarsAdapter mAdapter;
	private LinearLayoutManager mLayoutManager;

	private List<BarsDetails> barsList = null;
	private TextView tvEmptyView;

	protected Handler handler;

	private static int user_id;

	private int total_records, limit = 20;
	private String nextPageNumber = "initi";

	List<String> checkInBarID = new ArrayList<String>();
	List<String> checkInBarName = new ArrayList<String>();

	EditText etBarName;
	static String successCheckInBarId = -1 + "";
	ImageView ivSearchBars;
	Button btnBarsFilter;

	Others others;
	CurrentLocation location;
	ConnectionDetector con;
	int checkInAlertCount = 0;

	public static DialogInterface checkInDialog;
	public static AlertDialog alert = null;
	String jsonObject;

	boolean showVisinityDialog = true;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_bars_landing_page,
				container, false);
		initiolize();
		savedInstanceState = getArguments();
		if (savedInstanceState.getString("FilterIdentification").equals(
				"home_screen")) {
			// for filter color
			Log.v("filterbhar", "color not changed");
			btnBarsFilter.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.login_button_selector));
			btnBarsFilter.setText("Filter");
			loadData(page);
		} else if (savedInstanceState.getString("FilterIdentification").equals(
				"filter_applied")) {
			// for filter color
			if (MainMenu.barsFilterString.length() <= 2) {

				btnBarsFilter.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.login_button_selector));
				btnBarsFilter.setText("Filter");
			} else {

				btnBarsFilter.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.signup_button_selector));
				btnBarsFilter.setText("Filtered");
			}

			jsonObject = MainMenu.barsFilterString;
			loadFilterData();
		}
		mRecyclerView.setHasFixedSize(true);

		mLayoutManager = new LinearLayoutManager(getActivity());

		// use a linear layout manager
		mRecyclerView.setLayoutManager(mLayoutManager);

		mAdapter = new BarsAdapter(barsList, mRecyclerView, getActivity());
		// set the adapter object to the Recyclerview
		mRecyclerView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

		mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {

				// add null , so the adapter will check view_type and show
				// progress bar at bottom

				barsList.add(null);
				mAdapter.notifyItemInserted(barsList.size() - 1);

				if (page <= counter) {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {

							
							page++;
							loadData(page);
						}
					},1000);
				} else {
					barsList.remove(barsList.size() - 1);
				}

			}

		});

		return rootView;
	}

	private void loadFilterData() {
		// TODO Auto-generated method stub
		try {
			JSONObject bars = new JSONObject(jsonObject);
			bars.put("user_id", user_id);
			if (location.canGetLocation()) {
				bars.put("latitude", location.getLatitude());
				bars.put("longitude", location.getLongitude());
			}
			bars.put("limit", limit);
			bars.put("criteria", "bars");

			if (con.isConnectingToInternet()) {
				getBarResults(bars);
				Log.v("json response", bars.toString());
			} else {
				con.failureAlert();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		checkInBarID.clear();
		checkInBarName.clear();
		if (alert != null && alert.isShowing()) {
			alert.dismiss();
		}
		con = new ConnectionDetector(getActivity());
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
		tvEmptyView = (TextView) rootView.findViewById(R.id.tvEmptyView);
		etBarName = (EditText) rootView.findViewById(R.id.etBarName);
		ivSearchBars = (ImageView) rootView.findViewById(R.id.ivSearch);
		btnBarsFilter = (Button) rootView.findViewById(R.id.btnBarsFilter);
		btnBarsFilter.setOnClickListener(this);
		showVisinityDialog = true;

		etBarName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (etBarName.getText().length() > 0) {
					ivSearchBars.setVisibility(View.VISIBLE);
				} else {
					ivSearchBars.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		ivSearchBars.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				barsSearch();
				showVisinityDialog = false;
			}
		});

		barsList = new ArrayList<BarsDetails>();
		barsList.clear();
		handler = new Handler();
		user_id = BarHopperHomeScreen.user_id;
		others = new Others(getActivity());
		Others.navigation = "BarsPage";
		CurrentLocation.inVisinity = true;

		location = new CurrentLocation(getActivity());
		// Log.v("user_id", user_id + "");
	}

	private void loadData(int page) {

		try {
			JSONObject bars = new JSONObject();
			bars.put("user_id", user_id);

			if (location.canGetLocation()) {
				bars.put("latitude", location.getLatitude());
				bars.put("longitude", location.getLongitude());
			}

			bars.put("limit", limit);
			bars.put("criteria", "bars");
			bars.put("page", page);

			Log.v("server_req", bars.toString());
			if (con.isConnectingToInternet()) {
				getBarResults(bars);

			} else {
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void barsSearch() {
		String barName = etBarName.getText().toString();
		if (barName.length() > 0) {
			try {
				others.hideDialog();

				JSONObject bars = new JSONObject();
				bars.put("user_id", user_id);
				// Log.v("userid",Integer.toString(user_id));
				/*
				 * if (location.canGetLocation()) { bars.put("latitude",
				 * 29.7522031); bars.put("longitude", -95.3555475); }
				 */
				bars.put("bar_name", barName);
				bars.put("limit", limit);
				if (location.canGetLocation()) {
					bars.put("latitude", location.getLatitude());
					bars.put("longitude", location.getLongitude());
				}
				bars.put("criteria", "bars");
				// bars.put("limit", limit);
				// Log.v("server req", bars.toString());
				limit = 20;
				barsList.clear();
				mAdapter.notifyDataSetChanged();
				if (con.isConnectingToInternet()) {

					getBarResults(bars);
				} else {
					con.failureAlert();
				}
				// loadInitialBars(bars);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getActivity(), "enter valid bar name",
					Toast.LENGTH_SHORT).show();
		}

	}

	public void clearData() {
		int size = this.barsList.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				this.barsList.remove(0);
			}

			mAdapter.notifyItemRangeRemoved(0, size);
		}
	}

	private void getBarResults(final JSONObject bars) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_Bars";

		Log.v("getBarResul", "hi");

		if (page == 1) {
			// others.setMessage("Getting results just a minutate");
			others.showProgressWithOutMessage();

		}/*else{
			barsList.remove(barsList.size() - 1);
			mAdapter.notifyItemRemoved(barsList.size());
		}*/

		/*
		 * if (nextPageNumber != null && nextPageNumber != "initi") {
		 * barsList.add(null); mAdapter.notifyItemInserted(barsList.size() - 1);
		 * }
		 */
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_MAIN_MENU, bars,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						
						try {
							if (page == 1) {
								others.hideDialog();
							}else{
								barsList.remove(barsList.size()-1);
								mAdapter.notifyItemRemoved(barsList.size());
							}
							boolean error = response.getBoolean("error");
							if (!error) {

								loadInitialBars(response);

								mAdapter.notifyItemInserted(barsList.size());
								mAdapter.setLoaded();
							
								/*
								 * // create an Object for Adapter mAdapter =
								 * new BarsAdapter(barsList, mRecyclerView,
								 * getActivity());
								 * 
								 * // set the adapter object to the //
								 * Recyclerview
								 * mRecyclerView.setAdapter(mAdapter); //
								 * mAdapter.notifyDataSetChanged();
								 * 
								 * if (barsList.isEmpty()) {
								 * mRecyclerView.setVisibility(View.GONE);
								 * tvEmptyView.setVisibility(View.VISIBLE);
								 * 
								 * } else {
								 * mRecyclerView.setVisibility(View.VISIBLE);
								 * tvEmptyView.setVisibility(View.GONE); } //
								 * loadMoreData();
								 */
							} else {

								mRecyclerView.setVisibility(View.GONE);
								tvEmptyView.setVisibility(View.VISIBLE);

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

	private void loadInitialBars(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			total_records = response.getInt("total_bars");
			if (page == 1) {
				counter = total_records / 20;
			}
			others.setCheckInStatus(response.getBoolean("is_checkin"));
			successCheckInBarId = response.getString("checkin_bar_id");
			if (response.getBoolean("is_checkin")) {
				successCheckInBarId = response.getString("checkin_bar_id");
			} else {
				successCheckInBarId = -1 + "";
			}
			// Log.v("checkinStatus", successCheckInBarId);
			JSONArray barsData = response.optJSONArray("responses");

			if (total_records < limit) {
				limit = total_records;
			}
			if (barsData != null) {
				/*
				 * if (page > 1) { barsList.remove(barsList.size() - 1);
				 * mAdapter.notifyItemRemoved(barsList.size()); }
				 */

				for (int i = 0; i <limit; i++) {
					JSONObject singleBar = barsData.getJSONObject(i);
					String bar_id = singleBar.getString("bar_id");
					String bar_name = singleBar.getString("bar_name");
					Double distance = singleBar.getDouble("bar_distance");

					String bar_distance = Others.round(distance, 2) + "";
					String typeOfBar = optString(singleBar, "categories");

					if (!typeOfBar.equals("")) {
						typeOfBar = ", " + typeOfBar; 

					}

					String BarImgUrl = singleBar.getString("bar_image");
					// String barVisinityCount = singleBar
					// .getString("vicinity_user_count");
					String barVisinityCount = singleBar
							.getString("checkin_user_count");
					// Log.v("visinityUserCount", barVisinityCount);
					String facilitiesCount = singleBar
							.getString("facilities_count");

					String[] finalBarsString = new String[19];

					if (Float.parseFloat(bar_distance) < 1) {
						float bar_distance_float = (Float
								.parseFloat(bar_distance) * 5280);
						int bar_distance_int = (int) bar_distance_float;
						if (bar_distance_int <= 500) {
							checkInBarID.add(bar_id);
							checkInBarName.add(bar_name);
							if (bar_distance_int <= 200)
								bar_distance = 200 + " fts";
							else
								bar_distance = bar_distance_int + " fts";
						} else {
							bar_distance = bar_distance_int + " fts";
						}

					} else {
						bar_distance = bar_distance + " mi";
					}
					finalBarsString[0] = bar_id;
					finalBarsString[1] = bar_name;
					finalBarsString[2] = bar_distance;
					finalBarsString[3] = typeOfBar;
					finalBarsString[4] = BarImgUrl;
					finalBarsString[5] = barVisinityCount;
					finalBarsString[6] = facilitiesCount;

					JSONArray array = null;
					String[] user_id = null, user_ImageUrl = null;
					if (Integer.parseInt(barVisinityCount) != 0) {
						// array = singleBar.optJSONArray("vicinity_users");
						array = singleBar.optJSONArray("checkin_users");
						user_id = new String[array.length()];
						user_ImageUrl = new String[array.length()];
					}
					for (int j = 0; j < 4; j++) {
						if (j < Integer.parseInt(barVisinityCount)) {
							JSONObject userObj = array.getJSONObject(j);
							// user_ImageUrl[j] =
							// userObj.getString("vicinity_user_image");
							user_ImageUrl[j] = userObj
									.getString("checkin_user_image");
							finalBarsString[j + 7] = user_ImageUrl[j];

						} else {
							finalBarsString[j + 7] = 1 + "";
						}
					}
					for (int j = 0; j < 4; j++) {
						if (j < Integer.parseInt(barVisinityCount)) {
							JSONObject userObj = array.getJSONObject(j);
							// user_id[j] =
							// userObj.getString("vicinity_user_image");
							user_id[j] = userObj.getString("checkin_user_id");

							finalBarsString[j + 11] = user_id[j];

						} else {
							finalBarsString[j + 11] = 1 + "";
						}
					}

					// for facilities

					JSONArray arrayFacilities = null;
					String[] facilitiesUrl = null;
					if (Integer.parseInt(facilitiesCount) != 0) {
						arrayFacilities = singleBar.optJSONArray("facilities");
						facilitiesUrl = new String[arrayFacilities.length()];
					}
					for (int j = 0; j < 4; j++) {
						if (j < Integer.parseInt(facilitiesCount)) {
							JSONObject facilityObj = arrayFacilities
									.getJSONObject(j);
							facilitiesUrl[j] = facilityObj
									.getString("facility_image");

							finalBarsString[j + 15] = facilitiesUrl[j];
						} else {
							finalBarsString[j + 15] = 1 + "";
						}
					}

					barsList.add(new BarsDetails(finalBarsString));
				
				
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (location.canGetLocation()) {
			if (showVisinityDialog)
				barCheckInAlert();
		}
	}

	public void barCheckInAlert() {

		if (!others.getCheckInStatus()) {
			if (checkInAlertCount < checkInBarID.size()
					&& checkInBarID.size() >= 1) {

				AlertDialog.Builder builder = new AlertDialog.Builder(
						getActivity());
				builder.setItems(checkInBarName
						.toArray(new String[checkInBarName.size()]),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int clicked) {
								// TODO Auto-generated method stub
								checkInDialog = dialog;
								barCheckIn(checkInBarID.get(clicked));
								checkInBarID.clear();
								checkInBarName.clear();
							}
						});
				builder.setCancelable(false)

				.setNeutralButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								checkInDialog = dialog;
								dialog.cancel();
								others.setCheckInStatus(true);
							}
						});
				// Creating dialog box

				alert = builder.create();
				// Setting the title manually
				// alert.getWindow().setLayout(200, 400);
				alert.setTitle("Check-in to Bar");
				alert.show();
			} else {
				/*
				 * Toast.makeText(getActivity(), "not checkin bars",
				 * Toast.LENGTH_SHORT).show();
				 */
			}
		}

	}

	public static String optString(JSONObject json, String key) {
		// http://code.google.com/p/android/issues/detail?id=13830
		if (json.isNull(key) || json.optString(key).equals("-1"))
			return "";
		else
			return json.optString(key);
	}

	// Bars Checkin

	private void barCheckIn(String bar_id) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_Bars";

		others.setMessage("Just a minuate");
		others.showDialog();
		JSONObject checkIn = null;
		try {

			checkIn = new JSONObject();
			checkIn.put("user_id", user_id);

			checkIn.put("bar_id", bar_id);

			JsonObjectRequest jsObjRequest = new JsonObjectRequest(
					Request.Method.POST, Config.URL_BARS_CHECKIN, checkIn,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							others.hideDialog();
							try {
								boolean error = response.getBoolean("error");
								if (!error) {
									others.setCheckInStatus(true);
									// Log.v("checkInStatus",
									// others.isCheckedIn+ "");

									Toast.makeText(getActivity(),
											"You have checked-in to the bar",
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
							others.ToastMessage(error.getMessage());
						}
					});
			jsObjRequest.setShouldCache(false);
			// Adding request to request queue
			AppController.getInstance().addToRequestQueue(jsObjRequest,
					tag_string_req);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// bars filter button pressed
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Others.navigation = "oneStepBack";
		BarsFilter barsFiler = new BarsFilter();
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flContainer, barsFiler).addToBackStack(null)
				.commit();

	}

}

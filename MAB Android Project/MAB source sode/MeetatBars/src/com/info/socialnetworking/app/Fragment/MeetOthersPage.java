package com.info.socialnetworking.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.adapters.BarsAdapter;
import com.info.socialnetworking.app.adapters.MeetOthersAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.BarsDetails;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.DividerItemDecoration;
import com.info.socialnetworking.app.helper.MeetOthersDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.MainMenu;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class MeetOthersPage extends Fragment implements OnClickListener {

	View rootView;

	int nilDataProfiles = 0;
	int counter;
	int page = 1;
	private RecyclerView mRecyclerView;
	public MeetOthersAdapter mAdapter;

	private LinearLayoutManager mLayoutManager;

	private List<MeetOthersDetails> meetOthersList = null;
	private TextView tvEmptyView;

	protected Handler handler;

	private int user_id;
	Button btnMeetOthersFilter;
	private ArrayList<String> userIds = new ArrayList<String>();

	private int total_records, limit = 20;
	private String nextPageNumber = "initi";

	EditText etHopperName;
	ImageView ivHopperSearch;

	String jsonObject, match_profile_completion, private_profile_completion;
	Others others;
	CurrentLocation location;
	ConnectionDetector con;

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_meet_otherspage,
				container, false);
		initiolize();
		savedInstanceState = getArguments();
		if (MainMenu.meetOthersFilterIdentification.equals("home_screen")) {

			// for filter color
			Log.v("filterbhar", "color not changed");
			btnMeetOthersFilter.setBackgroundDrawable(getResources()
					.getDrawable(R.drawable.login_button_selector));
			btnMeetOthersFilter.setText("Filter");
			loadData();
		}

		else if (MainMenu.meetOthersFilterIdentification
				.equals("filter_applied")) {

			// for filter color
			if (MainMenu.meetOthersFilterString.length() <= 50) {
				Log.v("filterbhar", "color not changed");
				btnMeetOthersFilter.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.login_button_selector));
				btnMeetOthersFilter.setText("Filter");
			} else {
				Log.v("filterbhar", "colour changed");
				btnMeetOthersFilter.setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.signup_button_selector));
				btnMeetOthersFilter.setText("Filtered");
			}

			jsonObject = MainMenu.meetOthersFilterString;
			loadFilterData();
		}

		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getActivity());
		// use a linear layout manager
		mRecyclerView.setLayoutManager(mLayoutManager);

		mAdapter = new MeetOthersAdapter(meetOthersList, mRecyclerView,
				getActivity(), userIds);
		// set the adapter object to the Recyclerview
		mRecyclerView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

		mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
			@Override
			public void onLoadMore() {

				// add null , so the adapter will check view_type and show
				// progress bar at bottom
				meetOthersList.add(null);
				mAdapter.notifyItemInserted(meetOthersList.size() - 1);

				if (page <= counter) {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							//meetOthersList.remove(meetOthersList.size() - 1);
							mAdapter.notifyItemRemoved(meetOthersList.size());
							page++;
							loadData();

						}
					}, 1000);

				} else {
					meetOthersList.remove(meetOthersList.size() - 1);
				}

			}

		});

		return rootView;
	}

	private void loadFilterData() {
		// Toast.makeText(getActivity(), "you clicked apply",
		// Toast.LENGTH_SHORT).show();
		try {
			JSONObject bars = new JSONObject(jsonObject);
			bars.put("user_id", user_id);
			if (location.canGetLocation()) {
				bars.put("latitude", location.getLatitude());
				bars.put("longitude", location.getLongitude());
			}
			bars.put("limit", limit);
			// meetOthersList.clear();
			// mAdapter.notifyDataSetChanged();
			if (con.isConnectingToInternet()) {
				getHopperResults(bars);
				// Log.v("json response",bars.toString());
			} else {
				con.failureAlert();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
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
			bars.put("criteria", "meet_others");
			bars.put("limit", limit);
			bars.put("page", page);
			if (con.isConnectingToInternet()) {
				getHopperResults(bars);
			} else {
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void getHopperResults(JSONObject bars) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_Bars";
		if (page == 1) {
			// others.setMessage("Getting results just a minutate");
			others.showProgressWithOutMessage();

		}
		/*
		 * else if (nextPageNumber != null && nextPageNumber != "initi") {
		 * meetOthersList.add(null);
		 * mAdapter.notifyItemInserted(meetOthersList.size() - 1); }
		 */

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_MAIN_MENU, bars,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (page == 1) {
							others.hideDialog();
						}
					else{
						meetOthersList.remove(meetOthersList.size()-1);
						mAdapter.notifyItemRemoved(meetOthersList.size());
					}
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								loadInitialHoppers(response);

								/*
								 * // create an Object for Adapter mAdapter =
								 * new MeetOthersAdapter( meetOthersList,
								 * mRecyclerView, getActivity(), userIds);
								 * 
								 * // set the adapter object to the //
								 * Recyclerview
								 * mRecyclerView.setAdapter(mAdapter);
								 * mAdapter.notifyDataSetChanged();
								 * 
								 * if (meetOthersList.isEmpty()) {
								 * mRecyclerView.setVisibility(View.GONE);
								 * tvEmptyView.setVisibility(View.VISIBLE);
								 * 
								 * } else { mRecyclerView
								 * .setVisibility(View.VISIBLE);
								 * tvEmptyView.setVisibility(View.GONE); }
								 */
							}

							else {
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

	private void loadInitialHoppers(JSONObject response) {
		// TODO Auto-generated method stub
		try {

			// Log.v("output respone", response.toString());
			total_records = response.getInt("total_users");
			if (page == 1) {
				counter = total_records / 20;
			}
			String genderPreference = response.getString("gender_preference");
			match_profile_completion = response.getString("match_profile");
			private_profile_completion = response.getString("private_profile");

			if (genderPreference.equals("3"))
				genderPreferenceAlert();
			else {
				JSONArray meetOthersData = response.optJSONArray("responses");
				// nextPageNumber = response.getString("page");

				if (page == 1) {
					// for profile completion dialog
					if (match_profile_completion.equals("Incomplete")
							|| private_profile_completion.equals("Incomplete")) {
						profileIncompletionAlert();
					}

				}

				if (total_records < limit) {
					limit = total_records;
				}

				/*
				 * //to find out the total number of invalidated profiles coming
				 * 
				 * if (meetOthersData != null) {
				 * 
				 * for (int i = 0; i < meetOthersData.length(); i++) {
				 * JSONObject singleUser = meetOthersData.getJSONObject(i);
				 * String user_id = singleUser.getString("user_id"); String
				 * user_name = optString(singleUser, "username"); if (user_name
				 * == "" || user_name.isEmpty()) { nilDataProfiles++; } else {
				 * 
				 * } } }
				 */

				// to implementing purpose

				if (meetOthersData != null) {

					for (int i = 0; i < limit; i++) {
						JSONObject singleUser = meetOthersData.getJSONObject(i);
						String user_id = singleUser.getString("user_id");
						userIds.add(user_id);
						String user_name = optString(singleUser, "username");
						String age = optString(singleUser, "age");
						if (!age.equals(""))
							age = ", " + age;

						Double user_distance = singleUser
								.getDouble("user_distance");

						String distance = Others.round(user_distance, 2) + "";

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
						if (!city.equals(""))
							city = ", " + city;
						String tagline = optString(singleUser, "tagline");
						String status = optString(singleUser, "hopping_status");
						String imageUrl = singleUser.getString("user_photo");
						String lastActivity = singleUser
								.getString("last_activity");
						String is_online = singleUser.getString("is_online");

						String[] finalString = { user_id, user_name, age,
								distance, city, tagline, status, imageUrl,
								lastActivity, is_online };
						if (user_name == "" || user_name.isEmpty()) {

						} else {
							meetOthersList.add(new MeetOthersDetails(
									finalString));

							mAdapter.notifyItemInserted(meetOthersList.size());

							mAdapter.setLoaded();
						}
					}

				}

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void profileIncompletionAlert() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Complete your profile now to search more members.")
				.setCancelable(false)
				.setPositiveButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
				.setNegativeButton("Complete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {

								// Action for 'NO' Button
								dialog.cancel();
								if (match_profile_completion
										.equals("Incomplete")) {
									Others.navigation = "meetOthers";
									FragmentMyProfile activeUserProfile = new FragmentMyProfile();
									getActivity()
											.getFragmentManager()
											.beginTransaction()
											.replace(R.id.flContainer,
													activeUserProfile)
											.addToBackStack(null).commit();
									Toast.makeText(
											getActivity(),
											"Click on edit my profile to fill details",
											Toast.LENGTH_SHORT).show();
								} else if (private_profile_completion
										.equals("Incomplete")) {
									Others.navigation = "meetOthers";
									PrivateQuestions privateQuestions = new PrivateQuestions();
									getActivity()
											.getFragmentManager()
											.beginTransaction()
											.replace(R.id.flContainer,
													privateQuestions)
											.addToBackStack(null).commit();
								}
							}

						});
		// Creating dialog box
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		// Setting the title manually
		alert.setTitle("Complete profile");
		alert.show();
	}

	private void genderPreferenceAlert() {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.gender_preference_dialogue);
		dialog.setTitle("Who are you looking for?");
		dialog.show();
		RadioGroup radioGroup = (RadioGroup) dialog
				.findViewById(R.id.rgGenderPreference);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.rbFemale:
					sendGenderPreference(0);
					dialog.dismiss();
					break;
				case R.id.rbMale:
					sendGenderPreference(1);
					dialog.dismiss();
					break;
				case R.id.rbBoth:
					sendGenderPreference(2);
					dialog.dismiss();
					break;
				default:
					break;
				}

			}

		});

	}

	private void sendGenderPreference(int preference) {
		// TODO Auto-generated method stub
		try {
			JSONObject bars = new JSONObject();
			bars.put("user_id", user_id);
			if (location.canGetLocation()) {
				bars.put("latitude", location.getLatitude());
				bars.put("longitude", location.getLongitude());
			}
			bars.put("criteria", "meet_others");
			bars.put("limit", limit);
			bars.put("looking_for", preference);
			if (con.isConnectingToInternet()) {
				Log.v("gender Preference", bars.toString());
				getHopperResults(bars);
			} else {
				con.failureAlert();
			}
			// loadInitialBars(bars);
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
		Others.navigation = Others.NavigationDefaultValue;
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvMeetOthers);
		btnMeetOthersFilter = (Button) rootView
				.findViewById(R.id.btnMeetOthersFilter);
		btnMeetOthersFilter.setOnClickListener(this);
		mRecyclerView.addItemDecoration(new DividerItemDecoration(
				getActivity(), DividerItemDecoration.VERTICAL_LIST));

		tvEmptyView = (TextView) rootView.findViewById(R.id.tvEmptyView);

		etHopperName = (EditText) rootView.findViewById(R.id.etHopperName);
		etHopperName.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

				if (etHopperName.getText().length() > 0) {
					ivHopperSearch.setVisibility(View.VISIBLE);
				} else {
					ivHopperSearch.setVisibility(View.GONE);
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

		ivHopperSearch = (ImageView) rootView.findViewById(R.id.ivHopperSearch);
		ivHopperSearch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				hopperSearch();
			}
		});

		meetOthersList = new ArrayList<MeetOthersDetails>();
		handler = new Handler();
		user_id = BarHopperHomeScreen.user_id;
		others = new Others(getActivity());
		location = new CurrentLocation(getActivity());

	}

	private void hopperSearch() {
		// TODO Auto-generated method stub
		String hopperName = etHopperName.getText().toString();
		if (hopperName.length() > 0) {
			try {
				Others.hideKeyBoad(getActivity());
				meetOthersList.clear();
				mAdapter.notifyDataSetChanged();
				limit = 100;
				JSONObject bars = new JSONObject();
				bars.put("user_id", user_id);
				if (location.canGetLocation()) {
					bars.put("latitude", location.getLatitude());
					bars.put("longitude", location.getLongitude());
				}
				bars.put("hopper_name", hopperName);
				bars.put("limit", limit);
				bars.put("criteria", "meet_others");
				// bars.put("limit", limit);
				Log.v("server req", bars.toString());

				if (con.isConnectingToInternet()) {
					getHopperResults(bars);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Others.navigation = "meetOthers";
		MeetOthersFilter meetOthers = new MeetOthersFilter();
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flContainer, meetOthers).commit();

	}

}

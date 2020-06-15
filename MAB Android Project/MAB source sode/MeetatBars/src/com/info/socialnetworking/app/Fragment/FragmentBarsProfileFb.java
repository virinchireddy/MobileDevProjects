package com.info.socialnetworking.app.Fragment;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressWarnings("deprecation")
public class FragmentBarsProfileFb extends Fragment implements OnClickListener {

	// TextView tvProfile, tvMembers, tvPhotos, tvBarName, tvCity,tvBarNumber;
	TextView tvProfile, tvMembers, tvPhotos, tvBarName, tvCity, tvBarNumber,
			tvCity1, tvState, tvPincode;
	String bar_name, bar_distance, responseString;
	static String bar_id, barProfileNavigation;
	int user_id;
	Button btnCheckIn, btnLocateOnMap, btnJoinChatRoom;
	View rootView;
	LinearLayout liBarProfile;
	Others others;
	ImageView ivBarPhoto;
	CurrentLocation location;
	ConnectionDetector con;

	String promotion_id, promotion_type;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_bars_profile_fb,
	container, false);
//saved data from other fragment class to this class
		savedInstanceState = getArguments();
		bar_id = savedInstanceState.getString("bar_id");
		barProfileNavigation = savedInstanceState
				.getString("barProfileNavigation");
		
		((BarHopperHomeScreen) getActivity()).insideScreens();
		initiolize();
		loadData();
		if (Others.navigation.equals("promotions")) {
			promotion_id = savedInstanceState.getString("promotion_id");
			promotion_type = savedInstanceState.getString("promotion_type");
			sendPromotionCount();
		}

		return rootView;
	}

	private void sendPromotionCount() {
		// TODO Auto-generated method stub
		JSONObject promtionCount = new JSONObject();

		try {
			promtionCount.put("type", promotion_type);
			promtionCount.put("id", promotion_id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		String tag_string_req = "req_promotionCount";

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_PROMOTIONCOUNT, promtionCount,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								Log.v("promotion count", "count success");
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

	private void loadData() {
		// TODO Auto-generated method stub
		try {
			JSONObject bars = new JSONObject();
			bars.put("user_id", user_id);
			bars.put("bar_id", bar_id);
			Log.v("bhara", Integer.toString(user_id));
			Log.v("bhara", bar_id);
			// if(Others.navigation.equals(other))
			if (location.canGetLocation()) {
				bars.put("latitude", location.getLatitude());
				bars.put("longitude", location.getLongitude());
			}
			bars.put("criteria", "profile");
			// Log.v("server req", bars.toString());
			if (con.isConnectingToInternet()) {
				getBarProfileResults(bars);
			} else {
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void getBarProfileResults(JSONObject bars) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_barProfile";
		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_BAR_PROFILE, bars,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								loadDataToScreen(response);
								if (barProfileNavigation.equals("homeScreen"))
									openProfile();
								else if (barProfileNavigation
										.equals("barMembers"))
									openMembers();
								else if (barProfileNavigation
										.equals("barPhotos"))
									openPhotos();

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

	protected void loadDataToScreen(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			Boolean is_checkIn = response.getBoolean("is_checkin");
			responseString = response.toString();
			liBarProfile.setVisibility(View.VISIBLE);
			// remove the checkin button if he is already checkdIn
			if (is_checkIn)
				btnCheckIn.setVisibility(View.GONE);
			else
				btnCheckIn.setVisibility(View.VISIBLE);

			bar_distance = response.getString("bar_distance");

			JSONObject profile = response.getJSONObject("profile");
			Log.v("inside_bar_data", profile.toString());

			bar_name = profile.getString("bar_name");
			tvBarName.setText(bar_name);

			// tvCity.setText(optString(profile, "address_1"));
			tvCity.setText(cityNameRepetitionChecker(
					(optString(profile, "address_1")),
					(optString(profile, "city")), (optString(profile, "state"))));
			tvBarNumber.setText(optString(profile, "contact_phone"));
			ivBarPhoto.setBackgroundDrawable(getActivity().getResources()
					.getDrawable(R.drawable.image_loading));
			Others.setImageAsBackground(response.getString("bar_logo"),
					ivBarPhoto);
			tvCity1.setText(optString(profile, "city"));

			tvState.setText(optString(profile, "state"));
			tvPincode.setText(optString(profile, "pincode"));
		} catch (Exception e) {

		}
	}

	// to eliminate the repeated city name in the address
	public String cityNameRepetitionChecker(String address, String city,
			String state) {

		String addres = address;
		String addresTrim = addres.trim();
		String str[] = addresTrim.split(",");
		for (int i = 0; i < str.length; i++) {
			String deleteSpacesAddress = str[i].replace(" ", "");
			String cityTrim = city.trim();
			String deleteSpacesCity = cityTrim.replace(" ", "");
			if (deleteSpacesAddress.equalsIgnoreCase(deleteSpacesCity)) {
				str[i] = "";
				break;
			}

		}
		String finalAddress = Arrays.toString(str);
		String finalAddress1 = finalAddress.replace("[", "");
		String finalAddress2 = finalAddress1.replace("]", "");
		// String finalAddress3=finalAddress2.replace(",","");

		String addre = stateNameRepetitionChecker(finalAddress2, state);
		return addre;
	}

	// to eliminate the repeated state name in the address
	public String stateNameRepetitionChecker(String address, String state) {

		String addres = address;
		String addresTrim = addres.trim();
		String str[] = addresTrim.split(",");
		for (int i = 0; i < str.length; i++) {
			String deleteSpacesAddress = str[i].replace(" ", "");
			String stateTrim = state.trim();
			String deleteSpacesstate = stateTrim.replace(" ", "");
			if (deleteSpacesAddress.equalsIgnoreCase(deleteSpacesstate)) {
				str[i] = "";
				break;
			}

		}
		String finalAddress = Arrays.toString(str);
		String finalAddress1 = finalAddress.replace("[", "");
		String finalAddress2 = finalAddress1.replace("]", "");
		String finalAddress3 = finalAddress2.replace(",", "");

		return finalAddress3;
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		tvProfile = (TextView) rootView.findViewById(R.id.tvProfile);
		tvMembers = (TextView) rootView.findViewById(R.id.tvMembers);
		tvPhotos = (TextView) rootView.findViewById(R.id.tvPhotos);
		tvBarName = (TextView) rootView.findViewById(R.id.tvBarName);
		btnCheckIn = (Button) rootView.findViewById(R.id.btnCheckIn);
		ivBarPhoto = (ImageView) rootView.findViewById(R.id.ivBarPhoto);
		liBarProfile = (LinearLayout) rootView.findViewById(R.id.liBarProfile);
		btnLocateOnMap = (Button) rootView.findViewById(R.id.btnLocateOnMap);
		btnJoinChatRoom = (Button) rootView.findViewById(R.id.btnJoinChatRoom);
		tvBarNumber = (TextView) rootView.findViewById(R.id.tvBarNumber);
		tvCity = (TextView) rootView.findViewById(R.id.tvCity);
		// Log.v("checkinStatus", BarsPage.successCheckInBarId);
		others = new Others(getActivity());
		location = new CurrentLocation(getActivity());
		con = new ConnectionDetector(getActivity());
		user_id = BarHopperHomeScreen.user_id;
		tvProfile.setOnClickListener(this);
		tvMembers.setOnClickListener(this);
		tvPhotos.setOnClickListener(this);
		btnCheckIn.setOnClickListener(this);
		btnLocateOnMap.setOnClickListener(this);
		btnJoinChatRoom.setOnClickListener(this);

		tvCity1 = (TextView) rootView.findViewById(R.id.tvCity1);
		tvState = (TextView) rootView.findViewById(R.id.tvState);
		tvPincode = (TextView) rootView.findViewById(R.id.tvzipcode);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.tvProfile) {
			openProfile();
		} else if (v.getId() == R.id.tvMembers) {
			openMembers();

		} else if (v.getId() == R.id.tvPhotos) {
			openPhotos();
		} else if (v.getId() == R.id.btnLocateOnMap
				|| v.getId() == R.id.btnJoinChatRoom) {
			Toast.makeText(getActivity(), "This is a wish list item",
					Toast.LENGTH_SHORT).show();
		} else if (v.getId() == R.id.btnCheckIn) {

			if (Float.parseFloat(bar_distance) < 1) {
				float bar_distance_float = (Float.parseFloat(bar_distance) * 5280);
				int bar_distance_int = (int) bar_distance_float;
				if (bar_distance_int <= 500)
					barCheckIn(bar_id);
				else
					barCheckInFailureAlert();
			} else
				barCheckInFailureAlert();
		}
	}

	private void openPhotos() {
		// TODO Auto-generated method stub
		tvProfile
				.setBackgroundColor(getResources().getColor(R.color.tab_color));
		tvMembers
				.setBackgroundColor(getResources().getColor(R.color.tab_color));
		tvPhotos.setBackgroundColor(getResources().getColor(
				R.color.login_pressed));

		// Others.navigation="oneStepBack";
		Bundle obj = new Bundle();
		obj.putString("BarID", bar_id + "");
		BarPhotos otherUserPhotos = new BarPhotos();
		otherUserPhotos.setArguments(obj);
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flBarProfile, otherUserPhotos)
				.addToBackStack(null).commit();
	}

	private void openMembers() {
		// TODO Auto-generated method stub
		tvProfile
				.setBackgroundColor(getResources().getColor(R.color.tab_color));
		tvMembers.setBackgroundColor(getResources().getColor(
				R.color.login_pressed));
		tvPhotos.setBackgroundColor(getResources().getColor(R.color.tab_color));

		FragmentBarsMembers barsProfileDetails = new FragmentBarsMembers();
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flBarProfile, barsProfileDetails).commit();
	}

	private void openProfile() {
		// TODO Auto-generated method stub
		Bundle obj = new Bundle();
		obj.putString("responseString", responseString);
		tvProfile.setBackgroundColor(getResources().getColor(
				R.color.login_pressed));
		tvMembers
				.setBackgroundColor(getResources().getColor(R.color.tab_color));
		tvPhotos.setBackgroundColor(getResources().getColor(R.color.tab_color));

		FragmentBarsProfile barsProfile = new FragmentBarsProfile();
		barsProfile.setArguments(obj);
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flBarProfile, barsProfile).commit();
	}

	public void barCheckInFailureAlert() {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Please be in the vicinity of the bar to check-in")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		// Creating dialog box
		AlertDialog alert = builder.create();
		// Setting the title manually
		alert.setTitle("Alert");
		alert.show();
	}

	private void barCheckIn(String bar_id) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_Bars";
		others.showDialog();
		others.setMessage("Just a minuate");
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
									Toast.makeText(getActivity(),
											"You have checked-in to the bar",
											Toast.LENGTH_SHORT).show();
									btnCheckIn.setVisibility(View.GONE);
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

	public static String optString(JSONObject json, String key) {
		// http://code.google.com/p/android/issues/detail?id=13830
		if (json.isNull(key) || json.optString(key).equals("-1"))
			return "";
		else
			return json.optString(key);
	}

}

package com.info.socialnetworking.app.Fragment;

import java.util.Arrays;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.CheckAvilableBars;
import com.info.socialnetworking.app.meetatbars.ForFunOptions;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentEditMyProfile extends Fragment implements OnClickListener {

	//EditText etName, etLocation, etLastName, etTagLine, etInterests, etHobbies,
	//		etTvShows, etForfun, etfavorite_bar_name,etabout_me,etAttractionInPerson,etFavoriteDrink;
	
	EditText etName, etAddress,etCity,etState,etZipcode, etLastName, etTagLine, etInterests, etHobbies,
	etTvShows, etForfun, etfavorite_bar_name,etabout_me,etAttractionInPerson,etFavoriteDrink;
	Button btnCancel, btnSave, btnCheckBars, btnForFun;
	JSONObject viewResponse;
	View rootView;
	//String name, location, LastName, tagLine, interests, hobbies, tvShows;
	String name, Address,City,State,Zipcode, LastName, tagLine, interests, hobbies, tvShows;
	public static String favorite_bar = null, forFun = null;
	Others others;
	TextView tvEditMatchme;
	NumberPicker height_ft, height_in, age, income;
	ConnectionDetector con;

	public String[] incomeArray = new String[] { "  ", "No income",
			"$1000-$10000", "$10000-$20000 ", "$20000-$30000 ",
			"$30000-$50000 ", "$50000-$80000", "$80000-$100000",
			"$100000-$150000", "$150000-$199999", "$200000-$300000",
			"$300000-$500000", "$500000 -$1 million", "$1 million- $2 million",
			"$2 million - $5 million", "$5 million - $10 million",
			"$10million - $20 million", "$20 million - $40 million",
			"$40 million and above" };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_edit_myprofile,
				container, false);
		savedInstanceState = getArguments();
		try {
			viewResponse = new JSONObject(
					savedInstanceState.getString("view_response"));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		initiolize();
		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		others = new Others(getActivity());
		con = new ConnectionDetector(getActivity());
		etfavorite_bar_name.setText(favorite_bar);
		/*
		 * btnCancel = (Button) rootView.findViewById(R.id.btnCancel); btnSave =
		 * (Button) rootView.findViewById(R.id.btnSave);
		 * tvEditMatchme.setOnClickListener(this);
		 * btnCancel.setOnClickListener(this); btnSave.setOnClickListener(this);
		 */
		etForfun.getText().clearSpans();
		etForfun.setText(forFun);
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		/*
		 * others = new Others(getActivity()); con = new
		 * ConnectionDetector(getActivity());
		 */

		etName = (EditText) rootView.findViewById(R.id.etName);
		//etLocation = (EditText) rootView.findViewById(R.id.etLocation);
		
		etAddress=(EditText)rootView.findViewById(R.id.etAddress);
		etCity=(EditText)rootView.findViewById(R.id.etCity);
		etState=(EditText)rootView.findViewById(R.id.etState);
		etZipcode=(EditText)rootView.findViewById(R.id.etZipcode);
		etLastName = (EditText) rootView.findViewById(R.id.etLastName);
		etTagLine = (EditText) rootView.findViewById(R.id.etTagLine);
		etInterests = (EditText) rootView.findViewById(R.id.etInterests);
		etHobbies = (EditText) rootView.findViewById(R.id.etHobbies);
		etTvShows = (EditText) rootView.findViewById(R.id.etTvShows);
		height_ft = (NumberPicker) rootView.findViewById(R.id.npHightFt);
		height_in = (NumberPicker) rootView.findViewById(R.id.npHightIn);
		age = (NumberPicker) rootView.findViewById(R.id.npAge);
		income = (NumberPicker) rootView.findViewById(R.id.npIncome);
		tvEditMatchme = (TextView) rootView.findViewById(R.id.tvEditMatchme);
		etForfun = (EditText) rootView.findViewById(R.id.etForFun);
		etAttractionInPerson=(EditText)rootView.findViewById(R.id.etAttractionInPerson);
		etFavoriteDrink=(EditText)rootView.findViewById(R.id.etFavoriteDrink);
		etabout_me=(EditText)rootView.findViewById(R.id.etabout_me);
		
		etForfun.setKeyListener(null);
		etfavorite_bar_name = (EditText) rootView
				.findViewById(R.id.etfavorite_bar_name);
		btnCheckBars = (Button) rootView.findViewById(R.id.btnCheckBars);
		btnForFun = (Button) rootView.findViewById(R.id.btnForFun);

		age.setMaxValue(70);
		age.setMinValue(18);
		height_ft.setMaxValue(8);
		height_ft.setMinValue(3);
		height_ft.setWrapSelectorWheel(false);
		height_in.setMaxValue(11);
		height_in.setMinValue(0);
		income.setMaxValue(17);
		income.setDisplayedValues(incomeArray);

		btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
		btnSave = (Button) rootView.findViewById(R.id.btnSave);
		tvEditMatchme.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnSave.setOnClickListener(this);
		btnCheckBars.setOnClickListener(this);
		btnForFun.setOnClickListener(this);

		try {
			etName.setText(optString(viewResponse, "firstname"));
			
			//etLocation.setText(optString(viewResponse, "state"));
			etAddress.setText(optString(viewResponse,"address_1"));
			etCity.setText(optString(viewResponse,"city"));
			etState.setText(optString(viewResponse,"state"));
			etZipcode.setText(optString(viewResponse, "pincode"));
			etLastName.setText(optString(viewResponse, "lastname"));
			etTagLine.setText(optString(viewResponse, "tagline"));
			etInterests.setText(optString(viewResponse, "interests"));
			etHobbies.setText(optString(viewResponse, "hobbies"));
			etTvShows.setText(optString(viewResponse, "tv_shows"));
			etabout_me.setText(optString(viewResponse, "about_me"));
			etAttractionInPerson.setText(optString(viewResponse, "attraction_in_person"));
			etFavoriteDrink.setText(optString(viewResponse, "favorite_drink"));
			forFun = optString(viewResponse, "for_fun");
			etForfun.setText(forFun);
			favorite_bar = optString(viewResponse, "favorite_bar_name");
			etfavorite_bar_name.setText(favorite_bar);

			JSONArray match = new JSONArray(optString(viewResponse, "match"));
			if (optString(viewResponse, "match").isEmpty()
					|| optString(viewResponse, "match") == "") {

			} else {
				double height = 3.0;
				int age = 0, income = 0;
				for (int i = 0; i < match.length(); i++) {
					JSONObject obj = match.getJSONObject(i);
					Iterator<String> iter = obj.keys();
					while (iter.hasNext()) {
						String key = iter.next();
						if (key.equals(1 + "")) {
							height = obj.getDouble(key);
						} else if (key.equals(10 + "")) {
							age = obj.getInt(key);
						} else if (key.equals(11 + "")) {
							income = obj.getInt(key);
						}
					}
				}
				// spliting double to height_ft and height_in
				Log.v("height", height + "");
				int height_ft, height_in;
				height_ft = (int) height;
				double temp = (height - height_ft) * 100;
				height_in = (int) temp;

				this.height_ft.setValue(height_ft);
				this.height_in.setValue(height_in);
				this.age.setValue(age);
				this.income.setValue(income);
				// Log.v("values", height_ft+"/"+height_in+"/"+age+"/"+income);
			}

			// Log.v("matchString", match.toString());

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static String optString(JSONObject json, String key) {
		// http://code.google.com/p/android/issues/detail?id=13830
		if (json.isNull(key))
			return "";
		else
			return json.optString(key);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnCancel:
			getActivity().getFragmentManager().popBackStack();
			
			break;
		case R.id.btnSave:
			name = etName.getText().toString();
			//location = etLocation.getText().toString();
			Address=etAddress.getText().toString();
			City=etCity.getText().toString();
			State=etState.getText().toString();
			LastName = etLastName.getText().toString();
			Zipcode=etZipcode.getText().toString();
			tagLine = etTagLine.getText().toString();
			interests = etInterests.getText().toString();
			hobbies = etHobbies.getText().toString();
			tvShows = etTvShows.getText().toString();
			forFun = etForfun.getText().toString();
			String aboutMe=etabout_me.getText().toString();
			String attractionInPerson=etAttractionInPerson.getText().toString();
			String favoriteDrink=etFavoriteDrink.getText().toString();
			
			favorite_bar = etfavorite_bar_name.getText().toString();
			try {
				JSONObject obj = new JSONObject();
				obj.put("user_id", BarHopperHomeScreen.user_id);
				obj.put("mode", "save");
				String[] array = forFun.replace(", ", ",").split(",");
				JSONArray mJSONArray = new JSONArray(Arrays.asList(array));
				JSONObject responses = new JSONObject();
				JSONObject profile = new JSONObject();
				profile.put("hobbies", hobbies);
				profile.put("lastname", LastName);
				profile.put("tagline", tagLine);
				profile.put("interests", interests);
				profile.put("firstname", name);
				//profile.put("state", location);
				profile.put("address_1",Address);
				profile.put("city",City);
				profile.put("state",State);
				profile.put("pincode", Zipcode);
				profile.put("tv_shows", tvShows);
				profile.put("about_me", aboutMe);
				profile.put("attraction_in_person", attractionInPerson);
				profile.put("favorite_drink", favoriteDrink);
				profile.put("for_fun", mJSONArray);
				profile.put("favorite_bar", favorite_bar);
				

				responses.put("profile", profile);

				float a = height_ft.getValue();
				float b = (float) height_in.getValue() / 100;
				float height = a + b;
				JSONObject match = new JSONObject();
				match.put("1", height);
				match.put("10", age.getValue());
				match.put("11", income.getValue());
				responses.put("match", match);
				obj.put("responses", responses);
				Log.v("save Response", obj.toString());
				if (con.isConnectingToInternet()) {
					sendJsonData(obj);
				} else {
					con.failureAlert();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

			break;
		case R.id.tvEditMatchme:
			MatchMeQuestionsMain barsProfile = new MatchMeQuestionsMain();
			getActivity().getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, barsProfile)
					.addToBackStack(null).commit();
			break;
		case R.id.btnCheckBars:
			favorite_bar = etfavorite_bar_name.getText().toString();
			if (favorite_bar.length() > 0) {
				Intent i = new Intent(getActivity(), CheckAvilableBars.class);
				i.putExtra("user_id", BarHopperHomeScreen.user_id);
				i.putExtra("bar_name", favorite_bar);
				i.putExtra("navigation", "UserProfile");
				startActivity(i);
			} else {
				others.ToastMessage("Please enter the bar name");
			}
			break;
		case R.id.btnForFun:
			Intent i = new Intent(getActivity(), ForFunOptions.class);
			i.putExtra("options", forFun);
			i.putExtra("navigation", "myProfile");

			// Log.v("testing Strign", forFun);
			startActivity(i);
			break;
		default:
			break;
		}
	}

	private void sendJsonData(final JSONObject dataSend) {
		// TODO Auto-generated method stub

		String tag_string_req = "req_userData";
		others.showProgressWithOutMessage();
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_HOPPER_PROFILE, dataSend,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								FragmentMyProfile barsProfile = new FragmentMyProfile();
								getActivity().getFragmentManager()
										.beginTransaction()
										.replace(R.id.flContainer, barsProfile)
										.addToBackStack(null).commit();

								Toast.makeText(getActivity(),
										"Profile has updated",
										Toast.LENGTH_SHORT).show();
							} else {
								String error_msg = response
										.getString("error_msg");
								Log.v("hello", error_msg.toString());
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

					}
				});
		jsObjRequest.setShouldCache(false);
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);

	}
}

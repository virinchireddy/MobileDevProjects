package com.info.socialnetworking.app.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.Fragment.FragmentBarsProfileFb;
import com.info.socialnetworking.app.Fragment.FragmentOtherUserPhotos;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class HopperProfileSwipeAdapter extends PagerAdapter implements
		OnClickListener {

	private Activity _activity;
	Context _context;
	private LayoutInflater inflater;
	View viewLayout;
	LinearLayout btnPhotos, liTesting, liMainView, liFavBars;
	Button btnAddFriend;
	ImageView ivUserPhoto, ivPhotos;
	String user_id;
	String viewType, favBarName, favBarId, addButtonText;
	ViewGroup container;
	TextView tvName, tvToatalPhotos, tvAlcholic, tvHairColor, tvIncome, tvAge,
			tvUserName, tvHeight, tvChildren, tvEthnicity, tvEducation,
			tvGender, tvReligion, tvBodyType, tvSmoking, tvStatus,
			tvRelationShip, tvTagLine, tvLastActive, tvActivePhotos,
			tvFavorite;
	Others others;
	HashMap<String, String> privateQuestions;
	ConnectionDetector con;
	ArrayList<String> userIds;

	public HopperProfileSwipeAdapter(Activity activity,
			ArrayList<String> userIds) {
		// TODO Auto-generated constructor stub
		this._activity = activity;
		_context = activity.getApplicationContext();
		this.userIds = userIds;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.userIds.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == ((LinearLayout) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub

		inflater = (LayoutInflater) _activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		viewLayout = inflater.inflate(R.layout.fragment_userprofile, container,
				false);

		user_id = userIds.get(position);
		this.container = container;
		intitiolize();

		return viewLayout;
	}

	private void intitiolize() {
		// TODO Auto-generated method stub
		btnPhotos = (LinearLayout) viewLayout.findViewById(R.id.btnPhotos);
		liTesting = (LinearLayout) viewLayout.findViewById(R.id.liTesting);
		liMainView = (LinearLayout) viewLayout.findViewById(R.id.liMainView);

		btnAddFriend = (Button) viewLayout.findViewById(R.id.btnAddFriend);
		ivUserPhoto = (ImageView) viewLayout.findViewById(R.id.ivUserPhoto);
		ivPhotos = (ImageView) viewLayout.findViewById(R.id.ivPhotos);
		tvName = (TextView) viewLayout.findViewById(R.id.tvName);
		tvToatalPhotos = (TextView) viewLayout.findViewById(R.id.tvTotalPhotos);
		tvAge = (TextView) viewLayout.findViewById(R.id.tvAge);
		tvUserName = (TextView) viewLayout.findViewById(R.id.tvUserName);
		tvHeight = (TextView) viewLayout.findViewById(R.id.tvHeight);
		tvEthnicity = (TextView) viewLayout.findViewById(R.id.tvEthnicity);
		tvChildren = (TextView) viewLayout.findViewById(R.id.tvChildern);
		tvEducation = (TextView) viewLayout.findViewById(R.id.tvEducation);
		tvBodyType = (TextView) viewLayout.findViewById(R.id.tvBodyType);
		tvSmoking = (TextView) viewLayout.findViewById(R.id.tvSmoking);
		tvStatus = (TextView) viewLayout.findViewById(R.id.tvStatus);
		tvRelationShip = (TextView) viewLayout
				.findViewById(R.id.tvRelationShip);
		tvTagLine = (TextView) viewLayout.findViewById(R.id.tvTagLine);
		tvAlcholic = (TextView) viewLayout.findViewById(R.id.tvAlcholic);
		tvHairColor = (TextView) viewLayout.findViewById(R.id.tvHairColor);
		tvIncome = (TextView) viewLayout.findViewById(R.id.tvIncome);
		tvLastActive = (TextView) viewLayout.findViewById(R.id.tvLastActive);
		tvActivePhotos = (TextView) viewLayout
				.findViewById(R.id.tvActivePhotos);
		tvFavorite = (TextView) viewLayout.findViewById(R.id.tvFavorite);
		liFavBars = (LinearLayout) viewLayout.findViewById(R.id.liFavBars);

		others = new Others(_activity);
		con = new ConnectionDetector(_activity);
		Others.navigation = "meetOthers";

		btnPhotos.setOnClickListener(this);
		btnAddFriend.setOnClickListener(this);
		try {
			viewType = "show";
			JSONObject obj = new JSONObject();
			obj.put("user_id", user_id);
			obj.put("mode", viewType);
			if (con.isConnectingToInternet()) {
				getData(obj);
				((ViewPager) container).addView(viewLayout);
			} else {
				con.failureAlert();
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	private void loadDynamicDataToTextView(JSONObject privateAnswers) {
		// TODO Auto-generated method stub
		privateQuestions = new HashMap<String, String>();
		privateQuestions.put("religion_attitude", "Attitude about religion:");
		privateQuestions.put("religion_or_spiritual_importance",
				"Importance of religion or spirituality:");
		privateQuestions.put("sex_attitude", "Attitude about sex:");
		privateQuestions.put("sex_drive", "Sex drive:");
		privateQuestions.put("marriage_attitude", "Attitude about marriage:");
		privateQuestions.put("relationship_type", "Type of relationship:");
		privateQuestions.put("honesty", "Honesty:");
		privateQuestions.put("children", "Children:");
		privateQuestions.put("want_children", "Want to have children:");
		privateQuestions.put("dearone_cheats", "Someone cheats you:");
		privateQuestions
				.put("marry_for_relation",
						"Need to be married to have long lasting and happy relationship:");
		privateQuestions.put("looks_importance",
				"Importance of looks in finding a partner:");
		privateQuestions.put("education_importance",
				"Education level in finding a partner:");
		privateQuestions.put("financial_status",
				"Financial status of a partner:");
		privateQuestions.put("financial_support",
				"Looking for someone to support you financially:");
		privateQuestions.put("family_worker",
				"Who should work in the a family:");
		privateQuestions.put("travel_importance", "Importance of traveling:");
		privateQuestions.put("sports_importance", "Important is sports:");
		privateQuestions.put("political_belief", "Political belief:");
		privateQuestions.put("generous_for_loved", "Generous for loved once:");
		privateQuestions.put("money_importance", "Importance of money:");
		privateQuestions.put("like_movies", "Like watching movies:");
		privateQuestions.put("ambitious", "Ambitious:");
		privateQuestions.put("life_goals", "Life goals:");
		privateQuestions.put("how_happy", "How happy are you in life:");

		for (Map.Entry<String, String> entry : privateQuestions.entrySet()) {
			String key = entry.getKey();
			final TextView question = new TextView(_context);
			question.setTypeface(null, Typeface.BOLD);
			question.setTextColor(Color.BLACK);
			question.setPadding(0, 20, 0, 0);
			question.setText(privateQuestions.get(key));
			liTesting.addView(question);

			final TextView answer = new TextView(_context);
			try {
				answer.setPadding(0, 2, 0, 0);
				answer.setText(optString(privateAnswers, key));
				liTesting.addView(answer);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}

	private void getData(JSONObject dataReq) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_userData";
		// others.showProgressWithOutMessage();
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_HOPPER_PROFILE, dataReq,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("hello", "I am at Responce block");
						// others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								handleResponseShow(response);

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
						// others.hideDialog();
						con.serverErrorAlert();
					}
				});
		jsObjRequest.setShouldCache(false);
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);

	}

	protected void handleResponseShow(JSONObject response) {
		// TODO Auto-generated method stub
		liMainView.setVisibility(View.VISIBLE);
		Log.v("response", response.toString());
		try {
			String userProfileUrl = response.getString("profile_photo");
			Others.setImageAsBackground(userProfileUrl, ivUserPhoto);
			Others.setImageAsBackground(userProfileUrl, ivPhotos);
			tvName.setText(optStringEmpty(response, "firstname") + " "
					+ optStringEmpty(response, "lastname"));
			tvUserName.setText(optString(response, "username"));
			if (response.getString("total_user_photos").equals("0"))
				tvActivePhotos.setText("0");

			tvToatalPhotos.setText(response.getString("total_user_photos"));
			if (!optString(response, "tagline").equals("N/A"))
				tvTagLine.setText(optString(response, "tagline"));

			if (!optString(response, "favorite_bar_name").equals("N/A")) {
				favBarName = optString(response, "favorite_bar_name");
				Log.v("testing",
						favBarName + "/"
								+ optString(response, "favorite_bar_name")
								+ "/" + response.getString("favorite_bar_name"));
				tvFavorite.setText(favBarName);
			} else {
				liFavBars.setVisibility(View.GONE);
			}
			if (!optString(response, "favorite_bar_id").equals("N/A")) {
				favBarId = optString(response, "favorite_bar_id");
				tvFavorite.setTextColor(Color.BLUE);
				tvFavorite.setOnClickListener(this);
			}

			JSONObject match_text = response.getJSONObject("match_text");
			if (!optString(match_text, "age").equals("N/A")) {
				String age = ", " + optString(match_text, "age");
				Log.v("age", age);
				tvAge.setText(age);
			}
			String height = optString(match_text, "height");
			if (!height.equals("N/A")) {
				String[] height_ft = height.split("\\.");
				tvHeight.setText(height_ft[0] + "' "
						+ height_ft[1].replace("00", "0") + "\"");
			} else
				tvHeight.setText(height.replace("00", "0"));
			tvChildren.setText(optString(match_text, "children"));
			tvEthnicity.setText(optString(match_text, "ethnicity"));
			tvEducation.setText(optString(match_text, "education_level"));
			tvBodyType.setText(optString(match_text, "body_type"));
			tvSmoking.setText(optString(match_text, "smoke"));
			tvStatus.setText(optString(response, "hopping_status"));
			tvRelationShip.setText(optString(match_text, "marital_status"));
			tvAlcholic.setText(optString(match_text, "alcoholic_beverages"));
			tvHairColor.setText(optString(match_text, "hair_color"));
			tvIncome.setText(optString(match_text, "income"));
			tvLastActive.setText(optString(response, "last_activity"));
			addButtonText = optString(response, "frnd_status");
			btnAddFriend.setText(addButtonText);

			if (!response.isNull("private_text")) {
				JSONObject privateResponse = response
						.getJSONObject("private_text");
				loadDynamicDataToTextView(privateResponse);
				Log.v("privateResponse", privateResponse.toString());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnPhotos:
			Others.navigation = "oneStepBack";
			Bundle obj = new Bundle();
			obj.putString("user_id", user_id);
			FragmentOtherUserPhotos otherUserPhotos = new FragmentOtherUserPhotos();
			otherUserPhotos.setArguments(obj);
			_activity.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, otherUserPhotos)
					.addToBackStack(null).commit();
			break;
		case R.id.tvFavorite:
			Others.navigation = "oneStepBack";
			Bundle favNav = new Bundle();
			favNav.putString("bar_id", favBarId + "");
			favNav.putString("bar_name", favBarName);
			favNav.putString("bar_distance", 10000 + "");

			FragmentBarsProfileFb barsProfile = new FragmentBarsProfileFb();
			barsProfile.setArguments(favNav);
			_activity.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, barsProfile)
					.addToBackStack(null).commit();
			break;
		case R.id.btnAddFriend:
			try {
				JSONObject addFriend = new JSONObject();
				addFriend.put("user_id", BarHopperHomeScreen.user_id);
				addFriend.put("friend_id", user_id);
				sendFriendRequest(addFriend);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	private void sendFriendRequest(JSONObject addFriend) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_friendRequest";
		others.showProgressWithOutMessage();
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_FRIEND_REQUEST, addFriend,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("hello", "I am at Responce block");
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								Toast.makeText(_activity, "Request sent",
										Toast.LENGTH_SHORT).show();
								btnAddFriend.setText("Friend Request Sent");
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
						con.serverErrorAlert();
					}
				});
		jsObjRequest.setShouldCache(false);
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);
	}

	public static String optString(JSONObject json, String key) {
		// http://code.google.com/p/android/issues/detail?id=13830
		if (json.isNull(key) || json.optString(key).equals("-1.00")
				|| json.optString(key).equals("-1"))
			return "N/A";
		else {
			// Log.v("ProfileText",json.optString(key) );
			return json.optString(key);
		}
	}

	public static String optStringEmpty(JSONObject json, String key) {
		// http://code.google.com/p/android/issues/detail?id=13830
		if (json.isNull(key))
			return "";
		else
			return json.optString(key);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		
		((ViewPager) container).removeView((LinearLayout) object);
	}

}

 package com.info.socialnetworking.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class FragmentBarsProfile extends Fragment implements OnClickListener {

	String responseString;

	ImageView[] ivMemberPhoto = new ImageView[4];
	String[] imageUrls = new String[4];
	String[] userIds = new String[4];
	TextView tvNoOfOnline, tvCapacity, tvDrinkSpl, tvHoursOfOperation,
			tvLiveMusic, tvFacilities;
	TextView[] tvCoverCharges = new TextView[7];
	TextView[] tvOperationalHours = new TextView[7];

	
	
	
	int[] coverChargesIds = { R.id.tvSun, R.id.tvMon, R.id.tvTue, R.id.tvWed,
			R.id.tvThu, R.id.tvFri, R.id.tvSat };
	int [] hoursOfOperationIds={R.id.tvMonday,R.id.tvTuesday,R.id.tvWednesday,R.id.tvThursday,R.id.tvFriday,R.id.tvSaturday,R.id.tvSunday};
	View rootView;
	TextView tvNoEvents,tvNoPromotions;
	Others others;
	LinearLayout liEvents, liPromotions;
	CurrentLocation location;
	ConnectionDetector con;
	JSONObject operationalHours;

	 TableRow trCapacity, trOnlineMembers,trDrink, trLivemusic,trHoursOperation;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_bar_profile_profile,
				container, false);
		savedInstanceState = getArguments();
		responseString = savedInstanceState.getString("responseString");
		initiolize();
		loadData();
		return rootView;
	}

	private void initiolize() {
		// TODO Auto-generated method stub

		trCapacity=(TableRow)rootView.findViewById(R.id.trcapacity);
		trOnlineMembers=(TableRow)rootView.findViewById(R.id.tronlinemembers); 
		trDrink=(TableRow)rootView.findViewById(R.id.trdrinkspecial);
		trLivemusic=(TableRow)rootView.findViewById(R.id.trlivemusic);
		trHoursOperation=(TableRow)rootView.findViewById(R.id.trHoursoperation);
		
		 
		ivMemberPhoto[0] = (ImageView) rootView
				.findViewById(R.id.ivMemberPhoto1);
		ivMemberPhoto[1] = (ImageView) rootView
				.findViewById(R.id.ivMemberPhoto2);
		ivMemberPhoto[2] = (ImageView) rootView
				.findViewById(R.id.ivMemberPhoto3);
		ivMemberPhoto[3] = (ImageView) rootView
				.findViewById(R.id.ivMemberPhoto4);
		
		
		tvNoOfOnline = (TextView) rootView.findViewById(R.id.tvNoOfOnline);
		tvNoEvents=(TextView)rootView.findViewById(R.id.tvNoEvents);
		tvNoPromotions=(TextView)rootView.findViewById(R.id.tvNoPromotions);
		tvCapacity = (TextView) rootView.findViewById(R.id.tvCapacity);
		tvDrinkSpl = (TextView) rootView.findViewById(R.id.tvDrinkSpl);
		tvHoursOfOperation = (TextView) rootView
				.findViewById(R.id.tvHoursOfOperation);
		tvLiveMusic = (TextView) rootView.findViewById(R.id.tvLiveMusic);
		tvFacilities = (TextView) rootView.findViewById(R.id.tvFacilities);
		liEvents = (LinearLayout) rootView.findViewById(R.id.liEvents);
		liPromotions = (LinearLayout) rootView.findViewById(R.id.liPromotions);
		for (int i = 0; i < coverChargesIds.length; i++) {
			tvCoverCharges[i] = (TextView) rootView
					.findViewById(coverChargesIds[i]);
		}
		others = new Others(getActivity());
		location = new CurrentLocation(getActivity());
		con = new ConnectionDetector(getActivity());
		ivMemberPhoto[0].setOnClickListener(this);
		ivMemberPhoto[1].setOnClickListener(this);
		ivMemberPhoto[2].setOnClickListener(this);
		ivMemberPhoto[3].setOnClickListener(this);

	}

	@SuppressWarnings("deprecation")
	private void loadData() {
		// TODO Auto-generated method stub
		try {
			Log.v("String", responseString);
			JSONObject response = new JSONObject(responseString);
			JSONObject profile = response.getJSONObject("profile");
			tvNoOfOnline.setText(response.getString("online_users"));
			
			
		/**	
			tvCapacity.setText(optString(profile, "capacity"));
			
			if(optString(profile, "have_drink_specials").equals("0"))
				tvDrinkSpl.setText("Call for info.");
			else 
				tvDrinkSpl.setText("Yes");

			// for operational hours
			if(optString(response, "operational_hours").equals(""))
				tvHoursOfOperation.setText("Call for info.");
			else{
				operationalHours=response.getJSONObject("operational_hours");
				tvHoursOfOperation.setOnClickListener(this);
			}
			if(optString(profile, "have_live_music").equals("0"))
				tvLiveMusic.setText("Call for info.");
			else
				tvLiveMusic.setText("Yes");
			
		*/
			
			if(optString(profile, "capacity").equals("0"))
			{
				trCapacity.setVisibility(View.GONE);
			}
			else if(optString(profile, "capacity").equals(""))
			{
				trCapacity.setVisibility(View.GONE);
			}
			else
			{
				tvCapacity.setText(optString(profile, "capacity"));
			}
			
			
			
			if(optString(profile, "have_drink_specials").equals("0"))
			{
				//tvDrinkSpl.setText("Call for info.");
				trDrink.setVisibility(View.GONE);
			}
			else if(optString(profile, "have_drink_specials").equals(""))
			{
				trDrink.setVisibility(View.GONE);
			}
			else 
			{
				tvDrinkSpl.setText("Yes");
			}

			//for live music
			if(optString(profile, "have_live_music").equals("0"))
			{
				
				trLivemusic.setVisibility(View.GONE);
			}
			else if(optString(profile, "have_live_music").equals(""))
			{
				trLivemusic.setVisibility(View.GONE);
			}
			else
			{
				tvLiveMusic.setText("Yes");
			}
			
			
			
			// for operational hours
			if(optString(response,"operational_hours").equals(""))
			{
				
				trHoursOperation.setVisibility(View.GONE);
			}
			else{
				
				operationalHours=response.getJSONObject("operational_hours");
				tvHoursOfOperation.setOnClickListener(this);
			}
			
			
			
			
			
			
			
			
			if(optString(response, "facilities").equals(""))
				tvFacilities.setText("No facilities available");
			else			
				tvFacilities.setText(optString(response, "facilities").replace("_"," "));
			int profile_users_count = response.getInt("profile_users_count");
			if (profile_users_count > 0) {
				JSONArray profileUsers = response.getJSONArray("profile_users");
				for (int i = 0; i < profileUsers.length(); i++) {
					JSONObject singleMember = profileUsers.getJSONObject(i);
					// Log.v("singleMembers",singleMember.toString());
					userIds[i] = singleMember.getString("arr_user_id");
					imageUrls[i] = singleMember.getString("arr_user_image");
					ivMemberPhoto[i].setVisibility(View.VISIBLE);
					ivMemberPhoto[i].setImageResource(R.drawable.image_loading);
					Others.loadImages(imageUrls[i], ivMemberPhoto[i]);
				}
			}
			JSONArray events = response.optJSONArray("events");
			int eventCount = response.getInt("events_count");
			if(eventCount>0)
				tvNoEvents.setVisibility(View.GONE);
			for (int i = 0; i < eventCount; i++) {
				JSONObject singleEvent = events.getJSONObject(i);
				LayoutInflater inflater = (LayoutInflater) getActivity()
						.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(
						R.layout.bar_profile_events_promotions_item, null);
				TextView tvEPTitle = (TextView) view
						.findViewById(R.id.tvEPTitle);
				TextView tvEPMessage = (TextView) view
						.findViewById(R.id.tvEPMessage);
				ImageView ivEPImage = (ImageView) view
						.findViewById(R.id.ivEPImage);
				tvEPTitle.setText(optString(singleEvent, "event_title"));
				tvEPMessage
						.setText(optString(singleEvent, "event_description"));
				ivEPImage.setBackgroundDrawable(getActivity().getResources()
						.getDrawable(R.drawable.image_loading));
				Others.setImageAsBackground(
						optString(singleEvent, "event_image"), ivEPImage);
				liEvents.addView(view);

			}
			JSONArray promotions = response.optJSONArray("promotions");
			int promotionsCount = response.getInt("promotions_count");
			if(promotionsCount>0)
				tvNoPromotions.setVisibility(View.GONE);
			if(promotionsCount>0)
				tvNoPromotions.setVisibility(View.GONE);

			for (int i = 0; i < promotionsCount; i++) {
				JSONObject singlePromotion = promotions.getJSONObject(i);
				//LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
				
				LayoutInflater inflater=(LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.bar_profile_events_promotions_item, null);
				TextView tvEPTitle = (TextView) view.findViewById(R.id.tvEPTitle);
				TextView tvEPMessage = (TextView) view.findViewById(R.id.tvEPMessage);
				ImageView ivEPImage = (ImageView) view.findViewById(R.id.ivEPImage);
			//	tvEPTitle.setText(optString(singlePromotion, "promotion_title"));
			//	tvEPMessage.setText(optString(singlePromotion,"promotion_description"));
				
				
			tvEPTitle.setText(Others.firstLetterCapital(optString(singlePromotion, "promotion_title")));
			tvEPMessage.setText(Html.fromHtml(optString(singlePromotion,"promotion_description")));
					
				
				ivEPImage.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.image_loading));
				Others.setImageAsBackground(optString(singlePromotion, "promotion_image"),ivEPImage);
				liPromotions.addView(view);
			}
			if (!optString(response, "cover_charges").equals("")) {
				JSONObject coverCharges = response
						.optJSONObject("cover_charges");
				tvCoverCharges[0].setText(optString(coverCharges, "sun"));
				tvCoverCharges[1].setText(optString(coverCharges, "mon"));
				tvCoverCharges[2].setText(optString(coverCharges, "tue"));
				tvCoverCharges[3].setText(optString(coverCharges, "wed"));
				tvCoverCharges[4].setText(optString(coverCharges, "thu"));
				tvCoverCharges[5].setText(optString(coverCharges, "fri"));
				tvCoverCharges[6].setText(optString(coverCharges, "sat"));
			}

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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ivMemberPhoto1:
			userProfile(userIds[0]);
			break;
		case R.id.ivMemberPhoto2:
			userProfile(userIds[1]);
			break;
		case R.id.ivMemberPhoto3:
			userProfile(userIds[2]);
			break;
		case R.id.ivMemberPhoto4:
			userProfile(userIds[3]);
			break;
		case R.id.tvHoursOfOperation:
			openHoursOfOperationDialog();
			break;
		default:
			break;
		}
	}

	private void openHoursOfOperationDialog() {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(getActivity());
		dialog.setContentView(R.layout.hours_of_operations_display);
		dialog.setTitle("Hours of operation");
		dialog.show();
		for(int i=0;i<hoursOfOperationIds.length;i++)
		{
			tvOperationalHours[i]=(TextView)dialog.findViewById(hoursOfOperationIds[i]);
		}
		Log.v("operational hours",operationalHours.toString() );
		tvOperationalHours[0].setText(optString(operationalHours, "Monday"));
		tvOperationalHours[1].setText(optString(operationalHours,"Tuesday"));
		tvOperationalHours[2].setText(optString(operationalHours, "Wednesday"));
		tvOperationalHours[3].setText(optString(operationalHours, "Thursday"));
		tvOperationalHours[4].setText(optString(operationalHours, "Friday"));
		tvOperationalHours[5].setText(optString(operationalHours, "Saturday"));
		tvOperationalHours[6].setText(optString(operationalHours, "Sunday"));
		
	}

	private void userProfile(String userId) {
		// TODO Auto-generated method stub
		BarHopperHomeScreen.BarId=FragmentBarsProfileFb.bar_id;
		Bundle bundle = new Bundle();
		bundle.putString("user_id", userId);
		bundle.putString("Navigation","barProfile");
		FragmentUserProfile user = new FragmentUserProfile();
		user.setArguments(bundle);
		getActivity().getFragmentManager().beginTransaction()
		.replace(R.id.flContainer, user)
		.commit();
	}

}

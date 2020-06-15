package com.info.socialnetworking.app.meetatbars;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.info.socialnetworking.app.Fragment.ChatListPage;
import com.info.socialnetworking.app.Fragment.FragmentBarHoppersMenu;
import com.info.socialnetworking.app.Fragment.FragmentBarsPramotionsPage;
import com.info.socialnetworking.app.Fragment.FragmentBarsProfileFb;
import com.info.socialnetworking.app.Fragment.FragmentFriends;
import com.info.socialnetworking.app.Fragment.NotificationsPage;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.interfaces.FusedLocationReceiver;
import com.info.socialnetworking.app.service.FusedLocationService;
import com.info.socialnetworking.app.service.LocationUpdate;

@SuppressWarnings("deprecation")
public class BarHopperHomeScreen extends FragmentActivity {

	LinearLayout liBars;
	public static int user_id;
	ImageView ivBars, ivNotification, ivFriends, ivChat;
	TextView tvBars, tvNotification, tvFriends, tvChat;

	int back_count;
	String NavigationDefaultValue = -1 + "";
	public static String barProfileNavigation;
	public static String BarId;
	FusedLocationService fusedLocationService;
	LocationUpdate obj;
	public static CurrentLocation location;
	public static Activity activity;

	int initialLoad = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/*
		 * if (!isGooglePlayServicesAvailable()) { finish(); }
		 */
		setContentView(R.layout.barhopper_homescreen);
		liBars = (LinearLayout) findViewById(R.id.liBars);
		
		initiolize();
		activity=this;
		user_id = MainMenu.user_id;
		int screenSelected = MainMenu.clickedmenu;
		// obj=new LocationUpdate(BarHopperHomeScreen.this);

		initialSelection(screenSelected);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		location = new CurrentLocation(BarHopperHomeScreen.this,
				new FusedLocationReceiver() {
					@Override
					public void onLocationChanged() {
						// TODO Auto-generated method stub
						/*
						 * Toast.makeText(getApplicationContext(),""+CurrentLocation
						 * .inVisinity, Toast.LENGTH_SHORT).show(); if
						 * (Others.navigation.equals("BarsPage")) {
						 * initialLoad++; if (CurrentLocation.inVisinity) {
						 * if(initialLoad>1) initialSelection(1);
						 * //Toast.makeText
						 * (getApplicationContext(),"visinity changed",
						 * Toast.LENGTH_SHORT).show(); } else {
						 * Toast.makeText(getApplicationContext(),
						 * "in same visinity", Toast.LENGTH_SHORT) .show(); } }
						 */
					}
				});
		location.updateLocation(user_id);
	}

	private void initialSelection(int selectedScreen) {
		// TODO Auto-generated method stub

		if (selectedScreen == 1 || selectedScreen == 2) {

			bars();
			Bundle obj = new Bundle();
			obj.putString("FilterIdentification", "home_screen");
			FragmentBarsPramotionsPage bars = new FragmentBarsPramotionsPage();
			bars.setArguments(obj);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, bars).addToBackStack(null)
					.commit();

			
		} else if (selectedScreen == 3 || selectedScreen == 4) {
			meet();
			Bundle obj = new Bundle();
			FragmentFriends meet = new FragmentFriends();
			obj.putString("FilterIdentification", "home_screen");
			meet.setArguments(obj);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, meet).addToBackStack(null)
					.commit();
		}else if(selectedScreen==5){
			Chats();
			Others.navigation = Others.NavigationDefaultValue;
			ChatListPage Chats = new ChatListPage();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, Chats).addToBackStack(null).commit();
		}else if(selectedScreen==6){
			notification();
			Others.navigation = Others.NavigationDefaultValue;
			NotificationsPage notifications = new NotificationsPage();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, notifications).addToBackStack(null)
					.commit();
		}

	}

	public void menu(View v) {

		FragmentBarHoppersMenu barHopperMenu = new FragmentBarHoppersMenu();
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flContainer, barHopperMenu).addToBackStack(null)
				.commit();
	}

	private void initiolize() {

		// TODO Auto-generated method stub
		ivBars = (ImageView) findViewById(R.id.ivBars);
		ivNotification = (ImageView) findViewById(R.id.ivNotification);
		ivFriends = (ImageView) findViewById(R.id.ivFriends);
		ivChat = (ImageView) findViewById(R.id.ivChat);
		tvBars = (TextView) findViewById(R.id.tvBars);
		tvFriends = (TextView) findViewById(R.id.tvFriends);
		tvChat = (TextView) findViewById(R.id.tvChat);
		tvNotification = (TextView) findViewById(R.id.tvNotification);
		barProfileNavigation="homeScreen";
	}

	public void back(View v) {
		onBackPressed();
		// the below lines to solve filter items repetition problem added on 8/25/2016.
		
		if (MainMenu.barsFilterIdentification.equals("filter_applied")){
		}else{
				MainMenu.barsFilterList.clear();
		}
		if(MainMenu.meetOthersFilterIdentification.equals("filter_applied")){
		}else{
			MainMenu.meetOthersFilterList.clear();
		}
		
	}

	public void bars(View v) {

		bars();
		MainMenu.clickedmenu = 1;
		initialLoad = 0;
		Bundle obj = new Bundle();
		obj.putString("FilterIdentification", MainMenu.barsFilterIdentification);
		FragmentBarsPramotionsPage bars = new FragmentBarsPramotionsPage();
		bars.setArguments(obj);
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flContainer, bars).addToBackStack(null).commit();
		//for avoiding repetition of filterdata
		if (MainMenu.barsFilterIdentification.equals("filter_applied")){}
		else{MainMenu.barsFilterList.clear();}	
	}

	public void friends(View v) {
		meet();
		Others.navigation = Others.NavigationDefaultValue;
		MainMenu.clickedmenu = 3;
		Bundle obj = new Bundle();
		obj.putString("FilterIdentification", MainMenu.meetOthersFilterIdentification);
		FragmentFriends meet = new FragmentFriends();
		meet.setArguments(obj);
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flContainer, meet).addToBackStack(null).commit();
		//for avoiding repetition of filterdata
		if(MainMenu.meetOthersFilterIdentification.equals("filter_applied")){}
		else{MainMenu.meetOthersFilterList.clear();}
		
	}

	public void Notification(View v) {

		notification();
		Others.navigation = Others.NavigationDefaultValue;
		NotificationsPage notifications = new NotificationsPage();
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flContainer, notifications).addToBackStack(null)
				.commit();
		
	}

	public void Chat(View v) {

		Chats();
		Others.navigation = Others.NavigationDefaultValue;
		/*GroupChat Chats = new GroupChat();
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flContainer, Chats).addToBackStack(null).commit();*/
		ChatListPage Chats = new ChatListPage();
		this.getFragmentManager().beginTransaction()
				.replace(R.id.flContainer, Chats).addToBackStack(null).commit();
		
	}

	public void bars() {
		insideScreens();
		ivBars.setImageDrawable(getResources()
				.getDrawable(R.drawable.glass_red));
		tvBars.setTextColor(getResources().getColor(
				R.color.home_buttons_pressed));

	}

	public void meet() {

		insideScreens();
		ivFriends.setImageDrawable(getResources().getDrawable(
				R.drawable.meet_red));
		tvFriends.setTextColor(getResources().getColor(
				R.color.home_buttons_pressed));

	}

	public void notification() {

		insideScreens();
		ivNotification.setImageDrawable(getResources().getDrawable(
				R.drawable.notification_red));
		tvNotification.setTextColor(getResources().getColor(
				R.color.home_buttons_pressed));
	}

	public void Chats() {
		insideScreens();
		ivChat.setImageDrawable(getResources().getDrawable(R.drawable.chat_red));
		tvChat.setTextColor(getResources().getColor(
				R.color.home_buttons_pressed));

	}

	public void insideScreens() {
		ivBars.setImageDrawable(getResources().getDrawable(
				R.drawable.glass_white));
		tvBars.setTextColor(getResources().getColor(R.color.white));
		ivFriends.setImageDrawable(getResources().getDrawable(
				R.drawable.meet_white));
		tvFriends.setTextColor(getResources().getColor(R.color.white));
		ivChat.setImageDrawable(getResources().getDrawable(
				R.drawable.chat_white));
		tvChat.setTextColor(getResources().getColor(R.color.white));
		ivNotification.setImageDrawable(getResources().getDrawable(
				R.drawable.notification_white));
		tvNotification.setTextColor(getResources().getColor(R.color.white));

	}

	public void menuScreen(View v) {
		
		Others.navigation = Others.NavigationDefaultValue;
		Intent i=new Intent(getApplicationContext(),MainMenu.class);
		i.putExtra("user_id", user_id);
		startActivity(i);
		finish();
		
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		if (Others.navigation.equals("meetOthers")) {
			MainMenu.clickedmenu = 3;
			Others.navigation = Others.NavigationDefaultValue;
			Bundle obj = new Bundle();
			obj.putString("FilterIdentification", MainMenu.meetOthersFilterIdentification);
			meet();
			FragmentFriends meet = new FragmentFriends();
			meet.setArguments(obj);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, meet).addToBackStack(null)
					.commit();
			// Toast.makeText(getApplicationContext(),
			// "Pressing back will divert you to menu screen",
			// Toast.LENGTH_SHORT).show();
		} else if (Others.navigation.equals("bars")) {
			MainMenu.clickedmenu = 1;
			Others.navigation = Others.NavigationDefaultValue;
			Bundle obj = new Bundle();
			obj.putString("FilterIdentification", MainMenu.barsFilterIdentification);
			bars();
			FragmentBarsPramotionsPage bars = new FragmentBarsPramotionsPage();
			bars.setArguments(obj);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, bars).addToBackStack(null)
					.commit();
		} else if (Others.navigation.equals("Friends")) {
			MainMenu.clickedmenu = 4;
			Others.navigation = Others.NavigationDefaultValue;
			Bundle obj = new Bundle();
			obj.putString("FilterIdentification", MainMenu.meetOthersFilterIdentification);
			meet();
			FragmentFriends meet = new FragmentFriends();
			meet.setArguments(obj);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, meet).addToBackStack(null)
					.commit();

		}else if(Others.navigation.equals("Notifications"))
		{
			notification();
			Others.navigation = Others.NavigationDefaultValue;
			NotificationsPage notifications = new NotificationsPage();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, notifications).addToBackStack(null)
					.commit();
		}
		else if(Others.navigation.equals("promotions"))
		{
			MainMenu.clickedmenu = 2;
			Others.navigation = Others.NavigationDefaultValue;
			Bundle obj = new Bundle();
			obj.putString("FilterIdentification", "home_screen");
			bars();
			FragmentBarsPramotionsPage bars = new FragmentBarsPramotionsPage();
			bars.setArguments(obj);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, bars).addToBackStack(null)
					.commit();
		}
		else if (Others.navigation.equals("menu")) {
			Others.navigation = Others.NavigationDefaultValue;
			FragmentBarHoppersMenu barHopperMenu = new FragmentBarHoppersMenu();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, barHopperMenu)
					.addToBackStack(null).commit();
		}else if (Others.navigation.equals("barProfile")) {
			Others.navigation="bars";
			Bundle bundle = new Bundle();
			bundle.putString("bar_id", BarId);
			bundle.putString("barProfileNavigation", barProfileNavigation);
			FragmentBarsProfileFb bars = new FragmentBarsProfileFb();
			bars.setArguments(bundle);
			this.getFragmentManager().beginTransaction()
			.replace(R.id.flContainer, bars)
			.commit();
		}
		
		else if (Others.navigation.equals("oneStepBack")) {
			getFragmentManager().popBackStack();
		}
		else if (back_count == 1) {
			// super.onBackPressed();
			Others.navigation = Others.NavigationDefaultValue;
			Intent i=new Intent(getApplicationContext(),MainMenu.class);
			i.putExtra("user_id", user_id);
			startActivity(i);
			finish();
		} else {
			back_count++;
			Toast.makeText(getApplicationContext(),
					"Pressing back will divert you to menu screen",
					Toast.LENGTH_SHORT).show();
		}
	}

	
	
}

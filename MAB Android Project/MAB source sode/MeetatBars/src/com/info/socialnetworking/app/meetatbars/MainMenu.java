package com.info.socialnetworking.app.meetatbars;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.AgeFilterDetails;
import com.info.socialnetworking.app.helper.BarsFilterDetails;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.HeightFilterHelper;
import com.info.socialnetworking.app.helper.LocationFilterDetails;
import com.info.socialnetworking.app.helper.MeetOthersFilterDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class MainMenu extends Activity implements OnClickListener {

	public static int clickedmenu;
	public static int user_id;
	int back_count = 0;
	public static final String TAG = "MainMenu";

	TextView tvLookingFor, tvBars, tvPromotions, tvMeetFriends, tvMeetOthers,
			tvChat, tvGift;
	ImageView ivInstructionsOverlay, ivInfo;
	LinearLayout liBars, liPromotions, liMeetFriends, liMeetOthers;
	CurrentLocation location;
	boolean with_location;

	public static List<MeetOthersFilterDetails> meetOthersFilterList = null;
	public static List<BarsFilterDetails> barsFilterList = null;
	public static List<LocationFilterDetails> locationFilterList;
	public static List<HeightFilterHelper> heightFilterList = null;
	public static List<AgeFilterDetails> ageFilterList = null;

	public static List<LocationFilterDetails> BarslocationFilterList = null;
	public static List<MeetOthersFilterDetails> BarsCapacityFilterList = null;


	
	public static String meetOthersFilterIdentification;
	public static String barsFilterIdentification;

	public static String meetOthersFilterString = null;
	public static String barsFilterString = null;


	private static int barsOnClick = 1;
	private static int promotionsOnClick = 2;
	private static int meetOthersOnClick = 3;
	private static int meetFriendsOnClick = 4;

	
	Others others;
	ConnectionDetector con;

	// for push notifcations
	GoogleCloudMessaging gcm;
	Context context;
	String regId;
	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		savedInstanceState = getIntent().getExtras();
		user_id = savedInstanceState.getInt("user_id");
		setContentView(R.layout.main_menu_screen);
		meetOthersFilterList = new ArrayList<MeetOthersFilterDetails>();
		locationFilterList=new ArrayList<LocationFilterDetails>();
		barsFilterList=new ArrayList<BarsFilterDetails>();
		BarslocationFilterList=new ArrayList<LocationFilterDetails>();
		BarsCapacityFilterList=new ArrayList<MeetOthersFilterDetails>();
		heightFilterList=new ArrayList<HeightFilterHelper>();
		ageFilterList=new ArrayList<AgeFilterDetails>();
		meetOthersFilterIdentification = "home_screen";
		barsFilterIdentification = "home_screen";
		initioloze();
		registerGcm();

	}

	private void registerGcm() {
		// TODO Auto-generated method stub
		// Log.v("hello", obj.toString());
		if (TextUtils.isEmpty(regId)) {

			regId = registerGCM();

			Log.d("sending data", "GCM RegId: " + regId);

		} else {
			Toast.makeText(getApplicationContext(),"Already Registered with GCM Server!", Toast.LENGTH_LONG).show();
		}
	}

	// for gcm
	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(this);
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
			// Toast.makeText(getApplicationContext(),"RegId already available. RegId: "
			// + regId,Toast.LENGTH_LONG).show();
			Log.v("regId", regId);
		}
		return regId;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(
				BarHopperLogin.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("RegisterActivity",
					"I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@SuppressWarnings("deprecation")
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(Config.GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;

					// storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// Toast.makeText(getApplicationContext(),"Registered with GCM Server."
				// + msg, Toast.LENGTH_LONG).show();
				try {
					JSONObject obj = new JSONObject();
					obj.put("user_id", user_id);
					obj.put("device_platform", "android");
					obj.put("gcm_registration_id", regId);
					registerDevice(obj);
				} catch (Exception e) {

				}

				Log.v("gcm key", msg);
			}
		}.execute(null, null, null);
	}

	protected void registerDevice(JSONObject obj) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_Gcm";

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_SETGCM, obj,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							boolean error = response.getBoolean("error");
							Log.v("GCM Status",response.toString());
							if (!error) {

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

	private void initioloze() {
		// TODO Auto-generated method stub
		liBars = (LinearLayout) findViewById(R.id.liBars);
		liPromotions = (LinearLayout) findViewById(R.id.liPromotions);
		liMeetOthers = (LinearLayout) findViewById(R.id.liMeetOthers);
		liMeetFriends = (LinearLayout) findViewById(R.id.liMeetFriends);
		ivInstructionsOverlay = (ImageView) findViewById(R.id.ivInstructionsOverlay);
		ivInfo = (ImageView) findViewById(R.id.ivInfo);
		//ivInstructionsOverlay.setOnClickListener(this);
		ivInstructionsOverlay.setVisibility(View.GONE);
		others=new Others(MainMenu.this);
		con=new ConnectionDetector(MainMenu.this);
		
		liBars.setOnClickListener(this);
		liPromotions.setOnClickListener(this);
		liMeetFriends.setOnClickListener(this);
		liMeetOthers.setOnClickListener(this);
		ivInfo.setOnClickListener(this);
		
		//for making gcm hartbeat
		this.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
		this.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// location=new CurrentLocation(MainMenu.this);
		// location();

	}

	public void location() {

		location.updateLocation(user_id);
	}

	public void bars() {
		clickedmenu = barsOnClick;
		// barsFeedBack(user_id);
		Intent i = new Intent(getApplicationContext(),
				BarHopperHomeScreen.class);
		startActivity(i);
		finish();

	}

	public void promotions() {
		clickedmenu = promotionsOnClick;
		Intent i = new Intent(getApplicationContext(),
				BarHopperHomeScreen.class);
		startActivity(i);
		finish();
	}

	public void meetOthers() {
		clickedmenu = meetOthersOnClick;
		Intent i = new Intent(getApplicationContext(),
				BarHopperHomeScreen.class);
		startActivity(i);
		finish();
	}

	public void meetFriends() {
		clickedmenu = meetFriendsOnClick;
		Intent i = new Intent(getApplicationContext(),
				BarHopperHomeScreen.class);
		startActivity(i);
		finish();
	}

	public void info() {

		Intent i = new Intent(getApplicationContext(), RegistrationHelp.class);
		startActivity(i);

	}

	public void barsFeedBack(final int user_id) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Fill all the feedback questions.")
				.setCancelable(false)
				.setPositiveButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// Action for 'NO' Button
						dialog.cancel();
						Intent i = new Intent(getApplicationContext(),
								BarHopperHomeScreen.class);
						startActivity(i);
						finish();
					}
				}).setNegativeButton("Yes",

				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						Intent i = new Intent(getApplicationContext(),
								FeedbackQuestions.class);
						i.putExtra("user_id", user_id);
						startActivity(i);
					}
				});
		// Creating dialog box
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		// Setting the title manually
		alert.setTitle("Feedback");
		alert.show();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
		BarHopperLogin.activity.finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ivInstructionsOverlay:
			ivInstructionsOverlay.setVisibility(View.GONE);
			liBars.setOnClickListener(this);
			liPromotions.setOnClickListener(this);
			liMeetFriends.setOnClickListener(this);
			liMeetOthers.setOnClickListener(this);
			ivInfo.setOnClickListener(this);
			// Toast.makeText(getApplicationContext(), "hello",
			// Toast.LENGTH_SHORT).show();
			break;
		case R.id.liBars:
			bars();
			break;
		case R.id.liPromotions:
			promotions();
			break;
		case R.id.liMeetFriends:
			meetFriends();
			break;
		case R.id.liMeetOthers:
			meetOthers();
			break;
		case R.id.ivInfo:
			info();
			break;
		default:
			break;
		}
	}
}

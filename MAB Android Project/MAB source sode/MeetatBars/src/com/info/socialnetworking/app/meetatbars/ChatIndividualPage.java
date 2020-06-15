package com.info.socialnetworking.app.meetatbars;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.adapters.ChatIndividualAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.ChatIndividualDetails;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.network.ConnectionDetector;


public class ChatIndividualPage extends Activity implements OnClickListener {

	public static RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	Others others;
	CurrentLocation location;
	ConnectionDetector con;
	TextView tvEmptyView;
	public static ChatIndividualAdapter mAdapter;
	public static List<ChatIndividualDetails> chatList = null;
	ImageButton btnSendMessage;
	public static EditText etMessage;
	TextView tvUserName;
	ImageView ivBack;
	ImageView ivFriendProfile;

	public static Activity activity;

	private int total_records, limit = 300;
	public static String user_id, friend_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_individual_page);
		savedInstanceState = getIntent().getExtras();
		Log.v("user_id", user_id + "/" + friend_id);
		friend_id = savedInstanceState.getString("friend_id");
		user_id = savedInstanceState.getString("user_id");
		Log.v("user_id", savedInstanceState.getString("user_id") + "/"
				+ savedInstanceState.getString("friend_id"));
		Log.v("details",user_id+""+friend_id);
		activity=this;
		initiolize();
		loadData();
		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(this);
		// mLayoutManager.setReverseLayout(true);
		mLayoutManager.setStackFromEnd(true);
		// use a linear layout manager
		mRecyclerView.setLayoutManager(mLayoutManager);

	}

	private void initiolize() {
		// TODO Auto-generated method stub
		con = new ConnectionDetector(this);
		others = new Others(this);
		Others.navigation = Others.NavigationDefaultValue;
		chatList = new ArrayList<ChatIndividualDetails>();
		mRecyclerView = (RecyclerView) findViewById(R.id.rvChatIndividual);
		tvEmptyView = (TextView) findViewById(R.id.tvEmptyView);
		btnSendMessage = (ImageButton) findViewById(R.id.btnsendMessage);
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		ivBack = (ImageView) findViewById(R.id.ivBack);
		ivFriendProfile=(ImageView)findViewById(R.id.ivFriendProfile);
		ivBack.setOnClickListener(this);
		etMessage = (EditText) findViewById(R.id.etMessage);
		btnSendMessage.setOnClickListener(this);
		ivFriendProfile.setOnClickListener(this);
	}

	private void loadData() {
		// TODO Auto-generated method stub
		try {
			JSONObject chat = new JSONObject();
			chat.put("user_id", user_id);
			chat.put("mode", "show_messages");
			chat.put("friend_id", friend_id);
			if (con.isConnectingToInternet()) {
				Log.v("sending string", chat.toString());
				getChat(chat);
			} else {
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void getChat(JSONObject chat) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_ChatList";
		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_CHAT_INDIVIDUAL_CHAT, chat,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								loadChat(response);
								// create an Object for Adapter
								mAdapter = new ChatIndividualAdapter(chatList,
										mRecyclerView, ChatIndividualPage.this);

								mRecyclerView.setAdapter(mAdapter);
								// mAdapter.notifyDataSetChanged();

								if (chatList.isEmpty()) {
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

	protected void loadChat(JSONObject response) {
		// TODO Auto-generated method stub
		try {

			Log.v("output responce", response.toString());
			total_records = response.getInt("message_count");
			tvUserName.setText(response.getString("friend_username"));
			String image_url=response.getString("friend_pic");
			Others.loadImages(image_url, ivFriendProfile);
			JSONArray chatData = response.optJSONArray("messages");
			
			// nextPageNumber = response.getString("page");
			if (total_records < limit) {
				limit = total_records;
			}

			// for pofile completion dialog
			for (int i = 0; i < limit; i++) {
				JSONObject singleUser = chatData.getJSONObject(i);
				String is_sender = singleUser.getString("is_mine");
				if (is_sender.equals("true") || is_sender.equals(true))
					is_sender = "1";

				String message = optString(singleUser, "message");
				String timeStamp=optString(singleUser, "sent_at");
				String[] finalString = { is_sender, message,timeStamp};

				chatList.add(new ChatIndividualDetails(finalString));

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

	@SuppressLint("SimpleDateFormat") @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btnsendMessage) {
			String message = etMessage.getText().toString().trim();
			if (message.length() > 0) {
				try {
					
					//Log.v("present time",date);
					
					
					JSONObject sendMessage = new JSONObject();
					sendMessage.put("user_id", user_id);
					sendMessage.put("friend_id", friend_id);
					sendMessage.put("mode", "send");
					sendMessage.put("created_at",Others.getTimeInCST());
					sendMessage.put("message", message);
					sendMessage(sendMessage, message);
					Log.v("sending data",sendMessage.toString());

					etMessage.setText("");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				// others.ToastMessage("Please enter text to send");
			}
		}else if(v.getId() == R.id.ivBack)
		{
			onBackPressed();
		}else if(v.getId()==R.id.ivFriendProfile){
		Intent i=new Intent(getApplicationContext(),UserProfileFromChat.class);
		MainMenu.user_id=Integer.parseInt(user_id);
		i.putExtra("user_id", friend_id);
		
		startActivity(i);
		}
	}

	private void sendMessage(JSONObject sendMessage, final String message) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_ChatList";
		// others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_CHAT_INDIVIDUAL_CHAT,
				sendMessage, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						// others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								String time=response.getString("msg_sent_time");
								String[] finalString = { "1", message ,time};
								chatList.add(new ChatIndividualDetails(finalString));
								mAdapter.notifyItemInserted(chatList.size());
								mRecyclerView.smoothScrollToPosition(chatList
										.size());
								final MediaPlayer mPlayer2;
								mPlayer2 = MediaPlayer.create(
										getApplicationContext(),
										R.raw.send_message);
								mPlayer2.start();
								mPlayer2.setOnCompletionListener(new OnCompletionListener() {

									@Override
									public void onCompletion(MediaPlayer mp) {
										// TODO Auto-generated method stub
										mPlayer2.stop();
									}
								});

							} else {
								others.ToastMessageLong(response
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
						// others.hideDialog();
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		MainMenu.clickedmenu=5;
		// here user id nothing but application user id
		MainMenu.user_id=Integer.parseInt(user_id);
		
		Intent i=new Intent(this,BarHopperHomeScreen.class);
		startActivity(i);
		finish();
	}
}

package com.info.socialnetworking.app.meetatbars;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.Fragment.FragmentBarsProfileFb;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class UserProfileFromChat extends Activity implements OnClickListener {

	LinearLayout btnPhotos, liTesting, liMainView, liFavBars, liMessage,
			liAddFrdLine;
	Button btnAddFriend, btnSendMessage;
	ImageView ivUserPhoto, ivPhotos, ivReportAbuse, ivBlockUser;
	EditText etMessage;
	int privateinfoCount = 0;
	String user_id, user_name;
	String viewType, favBarName, favBarId, addButtonText, addFriendStatus;
	TextView tvName, tvToatalPhotos, tvAlcholic, tvHairColor, tvIncome, tvAge,
			tvUserName, tvHeight, tvChildren, tvEthnicity, tvEducation,
			tvGender, tvReligion, tvBodyType, tvSmoking, tvStatus,
			tvRelationShip, tvTagLine, tvLastActive, tvActivePhotos,
			tvFavorite, tvInterests, tvHobbies, tvTvShows, tvForFun,tvPeopleAboutMe,tvAttractiveInAPerson,tvFavDrink;

	TextView tvPrivateInfo;
	ImageView ivOnlineOffline;

	Others others;
	HashMap<String, String> privateQuestions;
	ConnectionDetector con;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_profile);

		savedInstanceState = getIntent().getExtras();

		user_id = savedInstanceState.getString("user_id");

		intitiolize();

	}

	private void intitiolize() {
		// TODO Auto-generated method stub
		btnPhotos = (LinearLayout) findViewById(R.id.btnPhotos);
		liTesting = (LinearLayout) findViewById(R.id.liTesting);
		liTesting.setVisibility(View.GONE);
		liMainView = (LinearLayout) findViewById(R.id.liMainView);
		liFavBars = (LinearLayout) findViewById(R.id.liFavBars);

		btnAddFriend = (Button) findViewById(R.id.btnAddFriend);
		ivUserPhoto = (ImageView) findViewById(R.id.ivUserPhoto);
		ivPhotos = (ImageView) findViewById(R.id.ivPhotos);
		ivBlockUser = (ImageView) findViewById(R.id.ivBlockUser);
		tvInterests = (TextView) findViewById(R.id.tvInterests);
		tvHobbies = (TextView) findViewById(R.id.tvHobbies);
		tvTvShows = (TextView) findViewById(R.id.tvTvShows);
		tvForFun = (TextView) findViewById(R.id.tvForFun);
		tvPeopleAboutMe=(TextView)findViewById(R.id.tvPeopleAboutMe);
		tvFavDrink=(TextView)findViewById(R.id.tvFavDrink);
		tvAttractiveInAPerson=(TextView)findViewById(R.id.tvAttractiveInAPerson);
		
		
		tvName = (TextView) findViewById(R.id.tvName);
		tvToatalPhotos = (TextView) findViewById(R.id.tvTotalPhotos);
		tvAge = (TextView) findViewById(R.id.tvAge);
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		tvHeight = (TextView) findViewById(R.id.tvHeight);
		tvEthnicity = (TextView) findViewById(R.id.tvEthnicity);
		tvChildren = (TextView) findViewById(R.id.tvChildern);
		tvEducation = (TextView) findViewById(R.id.tvEducation);
		tvBodyType = (TextView) findViewById(R.id.tvBodyType);
		tvSmoking = (TextView) findViewById(R.id.tvSmoking);
		tvStatus = (TextView) findViewById(R.id.tvStatus);
		tvGender = (TextView) findViewById(R.id.tvGender);
		tvRelationShip = (TextView) findViewById(R.id.tvRelationShip);
		tvTagLine = (TextView) findViewById(R.id.tvTagLine);
		tvAlcholic = (TextView) findViewById(R.id.tvAlcholic);
		tvHairColor = (TextView) findViewById(R.id.tvHairColor);
		tvIncome = (TextView) findViewById(R.id.tvIncome);
		tvLastActive = (TextView) findViewById(R.id.tvLastActive);
		tvActivePhotos = (TextView) findViewById(R.id.tvActivePhotos);
		tvFavorite = (TextView) findViewById(R.id.tvFavorite);
		tvPrivateInfo = (TextView) findViewById(R.id.tvPrivateInfo);
		liMessage = (LinearLayout) findViewById(R.id.liMessage);
		liAddFrdLine = (LinearLayout) findViewById(R.id.liAddFrdLine);
		btnSendMessage = (Button) findViewById(R.id.btnSendMessage);
		etMessage = (EditText) findViewById(R.id.etMessage);
		ivOnlineOffline = (ImageView) findViewById(R.id.ivOnlineOffline);

		btnSendMessage.setOnClickListener(this);
		if (user_id.equals(BarHopperHomeScreen.user_id + "")) {
			liMessage.setVisibility(View.GONE);
			liAddFrdLine.setVisibility(View.GONE);
			ivBlockUser.setVisibility(View.GONE);
		}

		others = new Others(UserProfileFromChat.this);
		con = new ConnectionDetector(UserProfileFromChat.this);

		btnPhotos.setOnClickListener(this);
		btnAddFriend.setOnClickListener(this);
		ivBlockUser.setOnClickListener(this);
		tvPrivateInfo.setOnClickListener(this);
		try {
			viewType = "show";
			JSONObject obj = new JSONObject();
			obj.put("user_id", user_id);
			obj.put("mode", viewType);
			obj.put("visitor_id", BarHopperHomeScreen.user_id);
			if (con.isConnectingToInternet()) {
				getData(obj);
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
			final TextView question = new TextView(getApplicationContext());
			question.setTypeface(null, Typeface.BOLD);
			question.setTextColor(Color.BLACK);
			question.setPadding(10, 20, 0, 0);
			question.setText(privateQuestions.get(key));
			liTesting.addView(question);

			final TextView answer = new TextView(getApplicationContext());
			try {
				answer.setPadding(10, 2, 0, 0);
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
		others.showProgressWithOutMessage();
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_HOPPER_PROFILE, dataReq,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("hello", "I am at Responce block");
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								if (viewType.equals("show")) {
									handleResponseShow(response);
								}
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

	@SuppressWarnings("deprecation")
	private void handleResponseShow(JSONObject response) {
		// TODO Auto-generated method stub
		liMainView.setVisibility(View.VISIBLE);
		Log.v("response", response.toString());
		try {
			String userProfileUrl = response.getString("profile_photo");
			Others.setImageAsBackground(userProfileUrl, ivUserPhoto);
			Others.setImageAsBackground(userProfileUrl, ivPhotos);
			tvName.setText(optStringEmpty(response, "firstname") + " "
					+ optStringEmpty(response, "lastname"));
			user_name = optString(response, "username");
			tvUserName.setText(user_name);
			if (response.getString("total_user_photos").equals("0"))
				tvActivePhotos.setText("0");

			tvToatalPhotos.setText(response.getString("total_user_photos"));
			if (!optString(response, "tagline").equals("N/A"))
				tvTagLine.setText(optString(response, "tagline"));
			else
				tvTagLine.setVisibility(View.GONE);
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
			// for displaying gender
			if (optString(response, "sex").equals("0"))
				tvGender.setText(", F");
			else if (optString(response, "sex").equals("1"))
				tvGender.setText(", M");
			// displaying gende is finished
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

			tvInterests.setText(optString(response, "interests"));
			tvHobbies.setText(optString(response, "hobbies"));
			tvTvShows.setText(optString(response, "tv_shows"));
			tvForFun.setText(optString(response, "for_fun"));
			Log.v("about me",optString(response, "about_me"));
			tvPeopleAboutMe.setText(optString(response, "about_me"));
			tvAttractiveInAPerson.setText(optString(response, "attraction_in_person"));
			tvFavDrink.setText(optString(response, "favorite_drink"));
			
			// show online offline status

			if (optString(response, "is_online").equals("1"))
				ivOnlineOffline.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.online_icon));
			else if (optString(response, "is_online").equals("0"))
				ivOnlineOffline.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.offline_icon));

			tvLastActive.setText(optString(response, "last_activity"));
			addButtonText = optString(response, "frnd_status");
			btnAddFriend.setText(addButtonText);
			if (!response.isNull("private_text")) {
				JSONObject privateResponse = response
						.getJSONObject("private_text");
				loadDynamicDataToTextView(privateResponse);
				Log.v("privateResponse", privateResponse.toString());
			} else {
				final TextView question = new TextView(getApplicationContext());
				question.setTextColor(Color.BLACK);
				question.setPadding(10, 20, 0, 0);
				question.setText("Private info not found");
				// question.setGravity(Gravity.CENTER_HORIZONTAL);
				liTesting.addView(question);
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
			/*
			 * Others.navigation = "oneStepBack"; Bundle obj = new Bundle();
			 * obj.putString("user_id", user_id); FragmentOtherUserPhotos
			 * otherUserPhotos = new FragmentOtherUserPhotos();
			 * otherUserPhotos.setArguments(obj);
			 * this.getFragmentManager().beginTransaction()
			 * .replace(R.id.flContainer, otherUserPhotos)
			 * .addToBackStack(null).commit();
			 */
			Intent i = new Intent(getApplicationContext(),
					UserProfilePictureFromChat.class);
			i.putExtra("user_id", user_id);
			startActivity(i);

			break;
		case R.id.tvFavorite:
			Others.navigation = "oneStepBack";
			Bundle favNav = new Bundle();
			favNav.putString("bar_id", favBarId + "");
			favNav.putString("barProfileNavigation", "homeScreen");
			FragmentBarsProfileFb barsProfile = new FragmentBarsProfileFb();
			barsProfile.setArguments(favNav);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, barsProfile)
					.addToBackStack(null).commit();
			break;
		case R.id.tvPrivateInfo:
			privateinfoCount++;
			privateinfoCount = privateinfoCount % 2;
			if (privateinfoCount == 1)
				liTesting.setVisibility(View.VISIBLE);
			else if (privateinfoCount == 0)
				liTesting.setVisibility(View.GONE);
			break;
		case R.id.btnSendMessage:
			if (etMessage.getText().toString().trim().length() <= 0)
				others.ToastMessage("Please enter the text to send");
			else {
				try {
					JSONObject message = new JSONObject();
					message.put("user_id", BarHopperHomeScreen.user_id);
					message.put("friend_id", user_id);
					message.put("mode", "send");
					message.put("created_at", Others.getTimeInCST());
					message.put("message", etMessage.getText().toString()
							.trim().replaceAll(" +", " "));
					sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			break;
		case R.id.btnAddFriend:
			try {
				final JSONObject addFriend = new JSONObject();
				addFriend.put("user_id", BarHopperHomeScreen.user_id);
				addFriend.put("friend_id", user_id);
				addFriendStatus = btnAddFriend.getText().toString();
				if (addFriendStatus.equals("Add as friend")) {
					sendFriendRequest(addFriend);
				} else if (addFriendStatus.equals("Cancel request")) {
					addFriend.put("decline", "decline");
					sendFriendRequest(addFriend);
				} else if (addFriendStatus.equals("Unfriend")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(
							"Are you sure you want to remove " + user_name
									+ " as your friend?")
							.setCancelable(false)
							.setPositiveButton("Unfriend",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
											addFriendStatus = "unfriend";
											try {
												addFriend.put("decline",
														"decline");
												sendFriendRequest(addFriend);
											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											// Action for 'NO' Button
											dialog.cancel();

										}
									});
					// Creating dialog box
					AlertDialog alert = builder.create();
					// Setting the title manually
					alert.setTitle("Unfriend");
					alert.setCanceledOnTouchOutside(true);
					alert.show();
				} else if (addFriendStatus.equals("Accept request")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							getApplicationContext());
					builder.setMessage("Would you like to accept as friend.")
							.setCancelable(false)
							.setPositiveButton("Accept",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
											addFriendStatus = "Accept";
											sendFriendRequest(addFriend);
										}
									})
							.setNegativeButton("Decline",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											// Action for 'NO' Button
											dialog.cancel();
											try {
												addFriendStatus = "Decline";
												addFriend.put("decline",
														"decline");
												sendFriendRequest(addFriend);
											} catch (JSONException e) {
												// TODO Auto-generated catch
												// block
												e.printStackTrace();
											}
										}
									});
					// Creating dialog box
					AlertDialog alert = builder.create();
					// Setting the title manually
					alert.setTitle("Add Friend");
					alert.setCanceledOnTouchOutside(true);
					alert.show();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			break;
		case R.id.ivBlockUser:
			blockAlert();
			break;
		default:
			break;
		}

	}

	private void blockAlert() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Would you like to block " + tvUserName.getText().toString())
				.setCancelable(false)
				.setPositiveButton("Block",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								try {
									JSONObject blockUser = new JSONObject();
									blockUser.put("user_id",
											BarHopperHomeScreen.user_id);
									blockUser.put("mode", "block");
									blockUser.put("other_id", user_id);

									String tag_string_req = "req_blockUser";
									others.showProgressWithOutMessage();
									JsonObjectRequest jsObjRequest = new JsonObjectRequest(
											Request.Method.POST,
											Config.URL_BLOCK_LIST,
											blockUser,
											new Response.Listener<JSONObject>() {
												@Override
												public void onResponse(
														JSONObject response) {
													Log.v("hello",
															"I am at Responce block");
													others.hideDialog();
													try {
														boolean error = response
																.getBoolean("error");
														if (!error) {

															openHomeScreen();

														} else {
															String error_msg = response
																	.getString("error_msg");
															Log.v("hello",
																	error_msg
																			.toString());
														}
													} catch (JSONException e) {
														e.printStackTrace();
													}
												}

											}, new Response.ErrorListener() {
												@Override
												public void onErrorResponse(
														VolleyError error) {
													Log.i("volley", "error: "
															+ error);
													others.hideDialog();
													con.serverErrorAlert();
												}
											});
									jsObjRequest.setShouldCache(false);
									// Adding request to request queue
									AppController.getInstance()
											.addToRequestQueue(jsObjRequest,
													tag_string_req);
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Action for 'NO' Button
								dialog.cancel();

							}
						});
		// Creating dialog box
		AlertDialog alert = builder.create();
		// Setting the title manually
		alert.setTitle("Block");
		alert.setCanceledOnTouchOutside(true);
		alert.show();

	}

	private void openHomeScreen() {
		// TODO Auto-generated method stub
		Toast.makeText(getApplicationContext(), "Blocked successfully",
				Toast.LENGTH_SHORT).show();
		MainMenu.clickedmenu = 5;
		Intent i = new Intent(this, BarHopperHomeScreen.class);
		startActivity(i);
		ChatIndividualPage.activity.finish();
		finish();
	}

	private void sendMessage(JSONObject message) {
		try {
			String tag_string_req = "req_message";
			JsonObjectRequest jsObjRequest = new JsonObjectRequest(
					Request.Method.POST, Config.URL_CHAT_INDIVIDUAL_CHAT,
					message, new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							Log.v("hello", "I am at Responce block");
							others.hideDialog();
							try {
								boolean error = response.getBoolean("error");
								if (!error) {
									etMessage.setText("");
									Toast.makeText(getApplicationContext(),
											"Message sent", Toast.LENGTH_SHORT)
											.show();
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
									String error_msg = response
											.getString("error_msg");
									others.ToastMessageLong(error_msg);
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
							con.serverErrorAlert();
						}
					});
			jsObjRequest.setShouldCache(false);
			// Adding request to request queue
			AppController.getInstance().addToRequestQueue(jsObjRequest,
					tag_string_req);
		} catch (Exception e) {
			e.printStackTrace();
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
						Log.v("friend response", response.toString());
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								if (addFriendStatus.equals("Add as friend")) {
									Toast.makeText(getApplicationContext(),
											"Request sent", Toast.LENGTH_SHORT)
											.show();
									btnAddFriend.setText("Cancel request");
								} else if (addFriendStatus
										.equals("Accept request")) {
									Toast.makeText(getApplicationContext(),
											"Request accepted",
											Toast.LENGTH_SHORT).show();
									btnAddFriend.setText("Unfriend");
								} else if (addFriendStatus.equals("Decline")) {
									Toast.makeText(getApplicationContext(),
											"Request declined",
											Toast.LENGTH_SHORT).show();
									btnAddFriend.setText("Add as friend");
								} else if (addFriendStatus
										.equals("Cancel request")) {
									Toast.makeText(getApplicationContext(),
											"Request canceled",
											Toast.LENGTH_SHORT).show();
									btnAddFriend.setText("Add as friend");
								} else if (addFriendStatus.equals("unfriend")) {
									btnAddFriend.setText("Add as friend");
								}
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

	public void back(View v) {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

}

package com.info.socialnetworking.app.meetatbars;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.DeleteEmail;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressWarnings("deprecation")
public class BarOwnerQuestions extends Activity implements OnClickListener {

	public static int bar_owners = 2;
	LinearLayout liQuestins[];

	EditText etBarName, etBarAddress, etBarWebSite, etBarContactNumber,
			etBarContactPerson;
	NumberPicker npStartTime, npEndTime, npStartMin, npEndMin;

	final private static int DIALOG_VERIFYEMAIL = 1;
	final private static int DIALOG_HOURSOFOPERATION = 2;

	private ProgressBar pbQuestions;

	public static String barName = null, barAddress = null, barWebSite = null,
			startTime_full = null, endTime_full = null, email = null,
			barNumber = null, barContactPerson = null, LoginIdentification,barId=null;
	
	int questionsId[] = { R.id.liQuestion1, R.id.liQuestion3, R.id.liQuestion4,
			R.id.liQuestion5, R.id.liQuestion6 };
	int question_number = 0, user_id, startTime = 0, endTime = 0, startMin = 0,
			endMin = 0, i, openingHoursCount = 0, back_count = 0;
	boolean barStatus;
	int[] question_answers = new int[3];
	TextView[] tvOpeningHours = new TextView[7];

	Button btnBack, btnCheckAvilableBars;

	String[] getSelectedText;
	Button selected = null;
	public Button[] selected_views = new Button[3];

	ConnectionDetector con;
	public static ImageView user_photo;

	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bar_owner_questions);
		liQuestins = new LinearLayout[10];

		savedInstanceState = getIntent().getExtras();

		//user_id =4;
		//email = "tsrtc@gmail.com";
		//LoginIdentification = "Normal Login";
		user_id = savedInstanceState.getInt("user_id");
		email = savedInstanceState.getString("email");
		LoginIdentification = savedInstanceState.getString("Login_Identification");

		byte[] byteArray = UploadPicture.image_Array;
		Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0,byteArray.length);

		user_photo = (ImageView) findViewById(R.id.user_image);
		BitmapDrawable ob = new BitmapDrawable(getResources(), bmp);
		user_photo.setBackgroundDrawable(ob);

		con = new ConnectionDetector(BarOwnerQuestions.this);

		pbQuestions = (ProgressBar) findViewById(R.id.pbQuestions);
		pbQuestions.setProgress(0);
		getSelectedText = new String[7];
		questionsInitiolize();
		answersInitiolize();
		hoursOfOperation();

		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setVisibility(View.GONE);
		liQuestins[question_number].setVisibility(View.VISIBLE);
	}

	private void answersInitiolize() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 3; i++) {
			question_answers[i] = -1;
		}

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		viewsInitiolize();
	}
	
	private void viewsInitiolize() {
		// TODO Auto-generated method stub
		etBarName = (EditText) findViewById(R.id.etBarName);
		etBarAddress = (EditText) findViewById(R.id.etBarAddress);
		etBarWebSite = (EditText) findViewById(R.id.etBarWebSite);
		etBarContactNumber = (EditText) findViewById(R.id.etBarContactNumber);
		etBarContactPerson = (EditText) findViewById(R.id.etBarContactPerson);
		etBarName.setText(barName);
		etBarAddress.setText(barAddress);
		etBarWebSite.setText(barWebSite);
		etBarContactNumber.setText(barNumber);
		etBarContactPerson.setText(barContactPerson);

	}

	public void checkAvilableBars(View v) {
		barName=etBarName.getText().toString();
		if(barName.length()>0){
			Intent i=new Intent(getApplicationContext(),CheckAvilableBars.class);
			i.putExtra("user_id", user_id);
			i.putExtra("bar_name",barName);
			i.putExtra("navigation","BarOwners");
			startActivity(i);
		}
		else
		{
			ToastMessage("Please enter the bar name");
		}
	}

	private void questionsInitiolize() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 5; i++) {
			liQuestins[i] = (LinearLayout) findViewById(questionsId[i]);
			liQuestins[i].setVisibility(View.GONE);
			if (i < 3) {
				getSelectedText[i] = null;
				selected_views[i] = null;
			}
		}
	}

	@SuppressLint("NewApi")
	public void next(View v) {

		switch (question_number) {
		case 0:
			barName = etBarName.getText().toString();
			barAddress = etBarAddress.getText().toString();
			barWebSite = etBarWebSite.getText().toString();
			barContactPerson = etBarContactPerson.getText().toString();
			barNumber = etBarContactNumber.getText().toString();
			if (barName.length() > 0 && barAddress.length() > 0
					&& barWebSite.length() > 0 && barContactPerson.length() > 0
					&& barNumber.length() > 0) {
				btnBack.setVisibility(View.VISIBLE);

				liQuestins[question_number].setVisibility(View.GONE);
				question_number++;
				liQuestins[question_number].setVisibility(View.VISIBLE);
				user_photo.setVisibility(View.GONE);
			} else {
				ToastMessage("Please complete the details");
			}
			break;
		case 1:
			if (openingHoursCount > 0) {

				// Toast.makeText(getApplicationContext(),
				// ""+tvOpeningHours[0].getText()+tvOpeningHours[1].getText(),
				// Toast.LENGTH_SHORT).show();
				liQuestins[question_number].setVisibility(View.GONE);
				question_number++;
				liQuestins[question_number].setVisibility(View.VISIBLE);
				user_photo.setVisibility(View.VISIBLE);
			} else {
				ToastMessage("Please choose opening and closing hours");
			}
			break;
		case 2:
		case 3:
			if (selected != null) {
				selected.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.blue_stoke));
				selected.setTextColor(getResources().getColor(R.color.black));
				selected = null;
			}
			if (selected_views[question_number - 2] != null) {
				liQuestins[question_number].setVisibility(View.GONE);
				question_number++;
				liQuestins[question_number].setVisibility(View.VISIBLE);
				if (question_number > 1
						&& selected_views[question_number - 2] != null) {
					selected_views[question_number - 2]
							.setBackgroundDrawable(getResources().getDrawable(
									R.drawable.questions_button_selected));
					selected_views[question_number - 2]
							.setTextColor(getResources()
									.getColor(R.color.white));
				}
			} else {
				ToastMessage("Please choose any one option");
			}
			break;
		case 4:

			if (selected != null) {
				selected.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.blue_stoke));
				selected.setTextColor(getResources().getColor(R.color.black));
				selected = null;
			}
			if (selected_views[question_number - 2] != null) {

				selected_views[question_number - 2]
						.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.questions_button_selected));
				selected_views[question_number - 2].setTextColor(getResources()
						.getColor(R.color.white));
				createJsonObject();
			} else {
				ToastMessage("Please choose any one option");
			}
			break;
		default:
			break;
		}
		pbQuestions.setProgress(question_number);

	}

	private void hoursOfOperation() {
		// TODO Auto-generated method stub
		int HoursOfOperationQuestions[] = { R.id.liDay1, R.id.liDay2,
				R.id.liDay3, R.id.liDay4, R.id.liDay5, R.id.liDay6, R.id.liDay7 };
		int OpeningHours[] = { R.id.tvOpeningHoursDay1,
				R.id.tvOpeningHoursDay2, R.id.tvOpeningHoursDay3,
				R.id.tvOpeningHoursDay4, R.id.tvOpeningHoursDay5,
				R.id.tvOpeningHoursDay6, R.id.tvOpeningHoursDay7 };
		LinearLayout[] liQuestions = new LinearLayout[7];

		for (int i = 0; i < 7; i++) {
			liQuestions[i] = (LinearLayout) findViewById(HoursOfOperationQuestions[i]);
			tvOpeningHours[i] = (TextView) findViewById(OpeningHours[i]);
			// tvOpeningHours[i].setText(null);
		}

		for (i = 0; i < 7; i++) {

			liQuestions[i].setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.liDay1:
			getData();
			i = 0;
			break;
		case R.id.liDay2:
			getData();
			i = 1;
			break;
		case R.id.liDay3:
			getData();
			i = 2;
			break;
		case R.id.liDay4:
			getData();
			i = 3;
			break;
		case R.id.liDay5:
			getData();
			i = 4;
			break;
		case R.id.liDay6:
			getData();
			i = 5;
			break;
		case R.id.liDay7:
			getData();
			i = 6;
			break;

		default:
			break;
		}
	}

	public void getData() {

		showDialog(DIALOG_HOURSOFOPERATION);

		// return obj;
	}

	@SuppressWarnings({ "unused" })
	@SuppressLint("NewApi")
	public void questionAnswersClicked(View v) {

		int qNumber = 0;
		if (question_number >= 2) {
			qNumber = question_number - 2;
		}
		int button_id = v.getId();
		// Toast.makeText(getApplicationContext(), ""+button_id,
		// Toast.LENGTH_SHORT).show();
		Button b = (Button) v;
		String buttonText = b.getText().toString();
		String temp = getSelectedText[qNumber];

		// Toast.makeText(getApplicationContext(), b.getId(),
		// Toast.LENGTH_SHORT).show();
		if (temp != null && temp.equals(buttonText)) {
			selected = null;
			b.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.blue_stoke));
			b.setTextColor(getResources().getColor(R.color.black));
			getSelectedText[qNumber] = null;
			selected_views[qNumber] = selected;
		}
		//
		else {
			if (selected != null
					&& !selected.getText().toString().equals(buttonText)) {

				if (selected_views[qNumber] != null) {

					selected_views[qNumber]
							.setBackgroundDrawable(getResources().getDrawable(
									R.drawable.blue_stoke));
					selected_views[qNumber].setTextColor(getResources()
							.getColor(R.color.black));
					selected = (Button) v;
					selected_views[qNumber] = selected;
					b.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.questions_button_selected));
					b.setTextColor(getResources().getColor(R.color.white));
					getSelectedText[qNumber] = buttonText;
				} else {

					selected.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.blue_stoke));
					selected.setTextColor(getResources()
							.getColor(R.color.black));
					selected = (Button) v;
					selected_views[qNumber] = selected;
					b.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.questions_button_selected));
					b.setTextColor(getResources().getColor(R.color.white));
					getSelectedText[qNumber] = buttonText;
				}
			} else {

				if (selected_views[qNumber] != null) {

					selected_views[qNumber]
							.setBackgroundDrawable(getResources().getDrawable(
									R.drawable.blue_stoke));
					selected_views[qNumber].setTextColor(getResources()
							.getColor(R.color.black));
					selected = (Button) v;
					selected_views[qNumber] = selected;
					b.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.questions_button_selected));
					b.setTextColor(getResources().getColor(R.color.white));
					getSelectedText[qNumber] = buttonText;
				} else {
					selected = (Button) v;
					selected_views[qNumber] = selected;
					b.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.questions_button_selected));
					b.setTextColor(getResources().getColor(R.color.white));
					getSelectedText[qNumber] = buttonText;
				}
			}
		}
		// Toast.makeText(getApplicationContext(), ""+buttonText,
		// Toast.LENGTH_SHORT).show();
	}

	@SuppressLint("NewApi")
	public void skip(View v) {
		switch (question_number) {
		case 0:
			btnBack.setVisibility(View.VISIBLE);
			liQuestins[question_number].setVisibility(View.GONE);
			question_number++;
			liQuestins[question_number].setVisibility(View.VISIBLE);
			user_photo.setVisibility(View.GONE);
			barName = null;
			barAddress = null;
			barWebSite = null;
			barContactPerson=null;
			barNumber=null;
			barId=null;
			etBarName.setText("");
			etBarAddress.setText("");
			etBarWebSite.setText("");
			etBarContactPerson.setText("");
			etBarContactNumber.setText("");

			break;
		case 1:
			/*
			 * liQuestins[question_number].setVisibility(View.GONE);
			 * question_number++;
			 * liQuestins[question_number].setVisibility(View.VISIBLE);
			 * npStartTime.setValue(0); npEndTime.setValue(0);
			 * npStartMin.setValue(0); npEndMin.setValue(0);
			 */
			liQuestins[question_number].setVisibility(View.GONE);
			question_number++;
			liQuestins[question_number].setVisibility(View.VISIBLE);
			user_photo.setVisibility(View.VISIBLE);

			break;
		case 2:
		case 3:

			question_answers[question_number - 2] = -1;
			if (selected_views[question_number - 2] != null) {

				selected_views[question_number - 2]
						.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.blue_stoke));
				selected_views[question_number - 2].setTextColor(getResources()
						.getColor(R.color.black));
				getSelectedText[question_number - 2] = null;
				selected_views[question_number - 2] = null;
			}

			question_answers[question_number - 2] = -1;

			liQuestins[question_number].setVisibility(View.GONE);
			question_number++;
			liQuestins[question_number].setVisibility(View.VISIBLE);

			break;
		case 4:

			question_answers[question_number - 3] = -1;
			if (selected_views[question_number - 3] != null) {
				selected_views[question_number - 3]
						.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.blue_stoke));
				selected_views[question_number - 3].setTextColor(getResources()
						.getColor(R.color.black));
				getSelectedText[question_number - 3] = null;
				selected_views[question_number - 3] = null;
			}
			createJsonObject();

			break;
		default:
			break;
		}
		pbQuestions.setProgress(question_number);

	}

	@SuppressLint("NewApi")
	public void previous(View v) {
		if (question_number > 0) {
			liQuestins[question_number].setVisibility(View.GONE);
			question_number--;
			liQuestins[question_number].setVisibility(View.VISIBLE);
			if (question_number == 1) {
				user_photo.setVisibility(View.GONE);
			} else {
				user_photo.setVisibility(View.VISIBLE);
			}

			if (question_number >= 2
					&& selected_views[question_number - 2] != null) {

				if (selected != null) {
					selected.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.blue_stoke));
					selected.setTextColor(getResources()
							.getColor(R.color.black));
					selected = null;
				}
				selected_views[question_number - 2]
						.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.questions_button_selected));
				selected_views[question_number - 2].setTextColor(getResources()
						.getColor(R.color.white));
			}
		}
		if (question_number <= 0) {
			btnBack.setVisibility(View.GONE);
		}
		pbQuestions.setProgress(question_number);
	}

	private void createJsonObject() {
		// TODO Auto-generated method stub
		if (con.isConnectingToInternet()) {

			try {
				JSONObject questions = new JSONObject();
				questions.put("user_id", user_id);
				questions.put("bar_id", barId);
				questions.put("bar_name", barName);
				questions.put("address_1", barAddress);
				questions.put("website", barWebSite);
				questions.put("contact_person", barContactPerson);
				questions.put("contact_phone", barNumber);
				
				String specials[]={"have_specials","have_live_music","have_special_events"};
				
				for (int i = 0; i <3; i++) {
					if(selected_views[i]!=null)
					{
						questions.put(specials[i], selected_views[i].getTag());
					}else
					{
						questions.put(specials[i], -1+"");
					}
				}
				
				JSONObject timings=new JSONObject();
				String days[]={"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
				for(int i=0;i<7;i++)
				{
					if(!tvOpeningHours[i].getText().toString().equals("Tap to select"))
					{
						timings.put(days[i], tvOpeningHours[i].getText().toString());;
					}
				}

				questions.put("bar_timings",timings);
				
				//Log.v("response",questions.toString());
				
				postQuestions(questions);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			con.failureAlert();
		}
	}

	private void postQuestions(final JSONObject spyQuestions) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_BarOwnerQuestions";

		pDialog.setMessage("Sending Your Responses ...");
		showDialog();
		Log.v("data", spyQuestions.toString());
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_BAROWNERQUESTIONS,
				spyQuestions, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								if (LoginIdentification.equals("Normal Login")) {
									showDialog(DIALOG_VERIFYEMAIL);
								} else if (LoginIdentification
										.equals("Facebook Login")) {
									Intent i = new Intent(
											getApplicationContext(),
											BarOwnerLandingPage.class);
									i.putExtra("user_id", user_id);
									startActivity(i);
									finish();
								}

							} else {
								String error_msg = response
										.getString("error_msg");
								ToastMessage(error_msg);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("volley", "error: " + error);
						hideDialog();
						con.serverErrorAlert();
					}
				});
		jsObjRequest.setShouldCache(false);

		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);
	}

	@Override
	protected Dialog onCreateDialog(int id) {

		AlertDialog dialogDetails = null;
		switch (id) {
		case DIALOG_VERIFYEMAIL:
			LayoutInflater inflater = LayoutInflater.from(this);
			View dialogview = inflater.inflate(
					R.layout.dialog_email_verification, null);
			AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
			dialogbuilder.setTitle("Email Verification");
			dialogbuilder.setView(dialogview);
			dialogDetails = dialogbuilder.create();
			break;
		case DIALOG_HOURSOFOPERATION:

			LayoutInflater barStatus = LayoutInflater.from(this);
			View barStatusView = barStatus.inflate(R.layout.hours_of_operation,
					null);
			AlertDialog.Builder dialogbarStatus = new AlertDialog.Builder(this);
			dialogbarStatus.setTitle("Set Timings");
			dialogbarStatus.setView(barStatusView);
			dialogDetails = dialogbarStatus.create();
		}
		return dialogDetails;

	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {

		switch (id) {
		case DIALOG_VERIFYEMAIL:
			final AlertDialog alertDialog = (AlertDialog) dialog;
			Button verify = (Button) alertDialog.findViewById(R.id.btnVerify);
			Button cancelbutton = (Button) alertDialog
					.findViewById(R.id.btnCancel);
			TextView tvResendEmail = (TextView) alertDialog
					.findViewById(R.id.tvClickToSend);

			final EditText verificationCode = (EditText) alertDialog
					.findViewById(R.id.etVerificationCode);
			verificationCode.setText("");
			verify.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					String verificationCodeString = verificationCode.getText()
							.toString();
					if (verificationCodeString.length() == 4) {
						if (con.isConnectingToInternet()) {
							try {
								JSONObject verifyEmailNumber = new JSONObject();
								verifyEmailNumber.put("user_id", user_id);
								verifyEmailNumber.put("random_num",
										verificationCodeString);
								verifyEmailNumber(verifyEmailNumber);
								alertDialog.dismiss();
							} catch (Exception e) {
								// TODO: handle exception
							}
						} else {
							con.failureAlert();
						}
					} else {
						ToastMessage("Enter a valid 4-digit verification code");
					}
				}
			});
			tvResendEmail.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					alertDialog.dismiss();
					emailAlert();
				}
			});
			cancelbutton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
					Intent i = new Intent(getApplicationContext(),
							BarHopperLogin.class);
					startActivity(i);
					finish();
				}
			});
			break;
		case DIALOG_HOURSOFOPERATION:

			final AlertDialog barStatusAlertDialog = (AlertDialog) dialog;
			Button btnOk = (Button) barStatusAlertDialog
					.findViewById(R.id.btnOk);
			Button btnCancel = (Button) barStatusAlertDialog
					.findViewById(R.id.btnClose);
			Button btnCloseToday = (Button) barStatusAlertDialog
					.findViewById(R.id.btnCloseToday);
			final Button btnOpeningHours=(Button)barStatusAlertDialog
					.findViewById(R.id.btnOpeningHours);
			final Button btnClosingHours=(Button)barStatusAlertDialog
					.findViewById(R.id.btnClosingHours);
			
			final TimePicker tpOpeningHour = (TimePicker) barStatusAlertDialog
					.findViewById(R.id.tpOpeningHours);
			final TimePicker tpClosingHour = (TimePicker) barStatusAlertDialog
					.findViewById(R.id.tpClosingHours);
			btnOpeningHours.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					btnOpeningHours.setBackgroundColor(getResources().getColor(R.color.login));
					btnClosingHours.setBackgroundColor(getResources().getColor(R.color.login_pressed));

					tpOpeningHour.setVisibility(View.VISIBLE);
					tpClosingHour.setVisibility(View.GONE);
				}
			});
			btnClosingHours.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					btnOpeningHours.setBackgroundColor(getResources().getColor(R.color.login_pressed));
					btnClosingHours.setBackgroundColor(getResources().getColor(R.color.login));
					tpOpeningHour.setVisibility(View.GONE);
					tpClosingHour.setVisibility(View.VISIBLE);
				}
			});
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					tvOpeningHours[i].setText(""
							+ hours24Format(tpOpeningHour.getCurrentHour(),
									tpOpeningHour.getCurrentMinute())
							+ " - "
							+ hours24Format(tpClosingHour.getCurrentHour(),
									tpClosingHour.getCurrentMinute()));
					tvOpeningHours[i].setTextColor(Color.BLACK);
					openingHoursCount++;
					barStatusAlertDialog.dismiss();
				}
			});
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					barStatusAlertDialog.dismiss();
				}
			});
			btnCloseToday.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					tvOpeningHours[i].setText("Closed");
					tvOpeningHours[i].setTextColor(Color.RED);
					barStatusAlertDialog.dismiss();
					openingHoursCount++;
				}
			});
		}
	}

	public String hours24Format(int hours, int mins) {
		String timeSet = "";
		if (hours > 12) {
			hours -= 12;
			timeSet = "PM";
		} else if (hours == 0) {
			hours += 12;
			timeSet = "AM";
		} else if (hours == 12)
			timeSet = "PM";
		else
			timeSet = "AM";

		String minutes = "";
		if (mins < 10)
			minutes = "0" + mins;
		else
			minutes = String.valueOf(mins);

		// Append in a StringBuilder
		String aTime = new StringBuilder().append(hours).append(':')
				.append(minutes).append(" ").append(timeSet).toString();

		return aTime;
	}

	private void verifyEmailNumber(final JSONObject verifyEmailNumber) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_ResendEmailVerificationLink";

		pDialog.setMessage("Verifying ...");
		showDialog();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_VERIFY_MAILNUMBER,
				verifyEmailNumber, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								Toast.makeText(
										getApplicationContext(),
										"Your Email has been verified successfully",
										Toast.LENGTH_SHORT).show();
								Intent i = new Intent(getApplicationContext(),
										BarOwnerLandingPage.class);
								i.putExtra("user_id", user_id);

								startActivity(i);
								finish();
							} else {

								String error_msg = response
										.getString("error_msg");
								showDialog(DIALOG_VERIFYEMAIL);
								ToastMessage(error_msg);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("volley", "error: " + error);
						hideDialog();
						con.serverErrorAlert();

					}
				});
		jsObjRequest.setShouldCache(false);
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);

	}

	private void emailAlert() {
		// TODO Auto-generated method stub
		String message = "Send 4-digit verification code to";

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message + "\n\n" + email)
				.setCancelable(false)
				.setPositiveButton("Send",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (con.isConnectingToInternet()) {
									dialog.cancel();
									try {
										JSONObject obj = new JSONObject();
										obj.put("email", email);
										sendVerificationLink(obj);
									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									con.failureAlert();
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
		alert.setTitle("Resend verification");
		alert.show();

	}

	private void sendVerificationLink(final JSONObject verifyEmail) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_ResendEmailVerificationLink";

		pDialog.setMessage("Sending ...");
		showDialog();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_RESEND_EMAIL_VERIFICATION,
				verifyEmail, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								Toast.makeText(
										getApplicationContext(),
										"Verification code sent to registered Email",
										Toast.LENGTH_SHORT).show();
								/*
								 * Intent i = new
								 * Intent(getApplicationContext(),
								 * BarHopperLogin.class); startActivity(i);
								 */
								showDialog(DIALOG_VERIFYEMAIL);
							} else {

								String error_msg = response
										.getString("error_msg");
								ToastMessage(error_msg);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("volley", "error: " + error);
						hideDialog();
						con.serverErrorAlert();

					}
				});
		jsObjRequest.setShouldCache(false);
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);
	}

	public void profileDialogBox() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"A 4-digit verification code has been sent to your email address which needs to be verified to continue further, please check your junk mail as well.")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								Intent i = new Intent(getApplicationContext(),
										BarHopperLogin.class);
								startActivity(i);
								finish();

							}
						});

		AlertDialog alert = builder.create();
		alert.setTitle("Thank you for registering with us");
		alert.show();

	}

	public void ToastMessage(String message) {
		Toast toast = Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_SHORT);
		View view = toast.getView();
		view.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.toast_message_background));
		toast.setGravity(Gravity.CENTER, 0, 0);
		// TextView text = (TextView) view.findViewById(android.R.id.message);
		// text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
		toast.show();
	}

	public void cross(View v) {
		DeleteEmail delete = new DeleteEmail(BarOwnerQuestions.this);
		delete.deleteEmailAlert(email);

	}

	private void showDialog() {
		if (!pDialog.isShowing())
			pDialog.show();
	}

	// Hiding the dialog box
	private void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		back_count++;
		if (back_count == 1) {
			Toast.makeText(getApplicationContext(),
					"Pressing back will divert you to login screen",
					Toast.LENGTH_SHORT).show();
		} else if (back_count == 2) {

			Intent i = new Intent(getApplicationContext(), BarHopperLogin.class);
			startActivity(i);
			finish();
		}
	}

}

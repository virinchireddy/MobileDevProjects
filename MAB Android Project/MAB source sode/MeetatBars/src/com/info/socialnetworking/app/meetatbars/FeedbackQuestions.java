package com.info.socialnetworking.app.meetatbars;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressWarnings("deprecation")
public class FeedbackQuestions extends Activity implements
		OnSeekBarChangeListener {

	LinearLayout liQuestins[];
	int questionsId[] = { R.id.liQuestion1, R.id.liQuestion2, R.id.liQuestion3,
			R.id.liQuestion4, R.id.liQuestion5, R.id.liQuestion6,
			R.id.liQuestion7, R.id.liQuestion8 };
	int question_number = 0, sbFoodValue = -1, sbDrinksValue = -1, user_id,back_count=0;
	int[] question_answers = new int[8];
	private ProgressDialog pDialog;
	private ProgressBar pbQuestions;
	ConnectionDetector con;
	Button btnBack;
	SeekBar sbFood, sbDrinks;
	TextView tvFoodRating, tvDrinksRating;
	boolean sendFreeRose=true;
	
	String[] getSelectedText;
	Button selected = null;
	public Button[] selected_views = new Button[7];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback_questions);
		liQuestins = new LinearLayout[10];

		savedInstanceState = getIntent().getExtras();
		user_id = savedInstanceState.getInt("user_id");
		con = new ConnectionDetector(FeedbackQuestions.this);

		pbQuestions = (ProgressBar) findViewById(R.id.pbQuestions);
		pbQuestions.setProgress(0);
		getSelectedText = new String[7];
		questionsInitiolize();
		answersInitiolize();

		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		btnBack = (Button) findViewById(R.id.btnBack);
		sbFood = (SeekBar) findViewById(R.id.sbFood);
		sbDrinks = (SeekBar) findViewById(R.id.sbDrinks);
		tvFoodRating = (TextView) findViewById(R.id.tvFoodRating);
		tvDrinksRating = (TextView) findViewById(R.id.tvDrinksRating);
		sbFood.setOnSeekBarChangeListener(this);
		sbDrinks.setOnSeekBarChangeListener(this);
		btnBack.setVisibility(View.GONE);
		liQuestins[question_number].setVisibility(View.VISIBLE);
	}

	private void questionsInitiolize() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 8; i++) {
			liQuestins[i] = (LinearLayout) findViewById(questionsId[i]);
			liQuestins[i].setVisibility(View.GONE);

			if (i < 7) {
				getSelectedText[i] = null;
				selected_views[i] = null;
			}
		}
	}

	private void answersInitiolize() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 8; i++) {
			question_answers[i] = -1;
		}
	}

	@SuppressLint("NewApi")
	public void next(View v) {
		if (question_number < 7) {
			if (question_number < 3) {
				// add somthing here
				if (selected != null) {
					selected.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.blue_stoke));
					selected.setTextColor(getResources().getColor(R.color.black));
					selected = null;
				}
				if (selected_views[question_number] != null) {
					liQuestins[question_number].setVisibility(View.GONE);
					question_number++;
					liQuestins[question_number].setVisibility(View.VISIBLE);
					btnBack.setVisibility(View.VISIBLE);
					if (question_number < 3
							&& selected_views[question_number] != null) {
						selected_views[question_number]
								.setBackgroundDrawable(getResources()
										.getDrawable(
												R.drawable.questions_button_selected));
						selected_views[question_number]
								.setTextColor(getResources().getColor(
										R.color.white));
					}
				} else {
					ToastMessage("Please choose any one option");
				}

			} else if (question_number == 3) {

				question_answers[question_number] = sbFoodValue;
				if (question_answers[question_number] > -1) {

					liQuestins[question_number].setVisibility(View.GONE);
					question_number++;
					liQuestins[question_number].setVisibility(View.VISIBLE);
				} else {
					ToastMessage("Please choose your rating");
				}

			} else if (question_number == 4) {

				question_answers[question_number] = sbDrinksValue;
				if (question_answers[question_number] > -1) {

					liQuestins[question_number].setVisibility(View.GONE);
					question_number++;
					liQuestins[question_number].setVisibility(View.VISIBLE);
				} else {
					ToastMessage("Please choose your rating");
				}

			}

			else if (question_number >= 5) {

				// add somthing here

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
					if (question_number >= 5
							&& selected_views[question_number - 2] != null) {
						selected_views[question_number - 2]
								.setBackgroundDrawable(getResources()
										.getDrawable(
												R.drawable.questions_button_selected));
						selected_views[question_number - 2]
								.setTextColor(getResources().getColor(
										R.color.white));
					}
				} else {
					ToastMessage("Please choose any one option");
				}

			}

		} else if (question_number == 7) {

			// add somthing here
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
		}

		pbQuestions.setProgress(question_number);

	}

	@SuppressWarnings({ "unused" })
	@SuppressLint("NewApi")
	public void questionAnswersClicked(View v) {

		int qNumber = 0;
		if (question_number < 3) {
			qNumber = question_number;
		} else if (question_number >= 5) {
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
					selected.setTextColor(getResources().getColor(R.color.black));
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
		
		if(!sendFreeRose)
		{
		if (question_number < 7) {

			btnBack.setVisibility(View.VISIBLE);
			liQuestins[question_number].setVisibility(View.GONE);
			question_number++;
			liQuestins[question_number].setVisibility(View.VISIBLE);

			if (question_number < 4) {

				// add somthing here

				question_answers[question_number - 1] = -1;
				if (selected_views[question_number - 1] != null) {

					selected_views[question_number - 1]
							.setBackgroundDrawable(getResources().getDrawable(
									R.drawable.blue_stoke));
					selected_views[question_number - 1]
							.setTextColor(getResources().getColor(R.color.black));
					getSelectedText[question_number - 1] = null;
					selected_views[question_number - 1] = null;
				}

			} else if (question_number == 4) {
				sbFood.setProgress(0);
				question_answers[question_number - 1] = -1;

			} else if (question_number == 5) {
				sbDrinks.setProgress(0);
				question_answers[question_number - 1] = -1;

			} else if (question_number >= 6) {
				// add somthing here
				question_answers[question_number - 3] = -1;
				if (selected_views[question_number - 3] != null) {
					selected_views[question_number - 3]
							.setBackgroundDrawable(getResources().getDrawable(
									R.drawable.blue_stoke));
					selected_views[question_number - 3]
							.setTextColor(getResources().getColor(R.color.black));
					getSelectedText[question_number - 3] = null;
					selected_views[question_number - 3] = null;
				}

			}
		} else if (question_number == 7) {

			// add somthing here
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
			// Toast.makeText(getApplicationContext(),
			// "   "+question_answers[0]+"   "+question_answers[1]+"   "+question_answers[2]+"   "+question_answers[3]+"   "+question_answers[4]+"   "+question_answers[5]+"   "+question_answers[6]+"   "+question_answers[7],Toast.LENGTH_LONG).show();
		}
		pbQuestions.setProgress(question_number);
		}else {
			
			String message="Clicking on ok will not send you give you the free rose";
			skipQuestionAlert(message,v);
		}
	}

	private void skipQuestionAlert(String message,final View v) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						sendFreeRose=false;
						skip(v);
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
				
		AlertDialog alert = builder.create();
		alert.setTitle("Alert");
		alert.show();
		
		
	}

	/*
	 * public void getDataFromRadioButtons(int que_no) {
	 * 
	 * }
	 */
	@SuppressLint("NewApi")
	public void previous(View v) {
		if (question_number > 0) {
			liQuestins[question_number].setVisibility(View.GONE);
			question_number--;
			liQuestins[question_number].setVisibility(View.VISIBLE);

			if (question_number >= 0 && question_number < 3
					&& selected_views[question_number] != null) {

				if (selected != null) {
					selected.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.blue_stoke));
					selected.setTextColor(getResources().getColor(R.color.black));
					selected = null;
				}
				selected_views[question_number]
						.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.questions_button_selected));
				selected_views[question_number].setTextColor(getResources()
						.getColor(R.color.white));
			} else if (question_number >= 4
					&& selected_views[question_number - 2] != null) {

				if (selected != null) {
					selected.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.blue_stoke));
					selected.setTextColor(getResources().getColor(R.color.black));
					selected = null;
				}
				selected_views[question_number - 2]
						.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.questions_button_selected));
				selected_views[question_number - 2].setTextColor(getResources()
						.getColor(R.color.white));
			}
		} else if (question_number <= 0) {
			btnBack.setVisibility(View.GONE);
		}
		pbQuestions.setProgress(question_number);
	}
	public void cross(View v) {
		Intent i = new Intent(getApplicationContext(),
				BarHopperHomeScreen.class);
		startActivity(i);
		finish();
	}

	public void ToastMessage(String message) {
		Toast toast = Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_SHORT);
		View view = toast.getView();
		view.setBackgroundDrawable(getResources().getDrawable(R.drawable.toast_message_background));
		toast.setGravity(Gravity.CENTER, 0, 0);
		// TextView text = (TextView) view.findViewById(android.R.id.message);
		// text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
		toast.show();
	}

	private void createJsonObject() {
		// TODO Auto-generated method stub
		if (con.isConnectingToInternet()) {
			try {
				JSONObject questions = new JSONObject();
				for (int i = 1; i <= 8; i++) {
					questions.put(i + "", question_answers[i - 1]);
				}
				JSONObject data = new JSONObject();
				data.put("user_id", user_id);
				data.put("owner_id", 4);
				data.put("responses", questions);
				postQuestions(data);
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
		String tag_string_req = "req_SpyQuestions";

		pDialog.setMessage("Sending Your FeedBack ...");
		showDialog();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_SPYQUESTIONS, spyQuestions,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								feedBackDialogBox();

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

	public void feedBackDialogBox() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"This will be shared with the bar so as to serve you better.")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						Intent i = new Intent(getApplicationContext(),
								BarHopperHomeScreen.class);
						startActivity(i);
						finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.setTitle("Thank you for your valuable feedback!");
		alert.show();
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
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub
		switch (seekBar.getId()) {
		case R.id.sbFood:
			sbFoodValue = progress;
			tvFoodRating.setText(String.valueOf(progress));
			break;
		case R.id.sbDrinks:
			sbDrinksValue = progress;
			tvDrinksRating.setText(String.valueOf(progress));
			break;
		default:
			break;
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		back_count++;
		if(back_count==1)
		{
			Toast.makeText(getApplicationContext(), "Pressing back will divert you to home screen", Toast.LENGTH_SHORT).show();
		}else if (back_count==2) {
			
			Intent i=new Intent(getApplicationContext(),BarHopperHomeScreen.class);
			startActivity(i);
		}
	}
}

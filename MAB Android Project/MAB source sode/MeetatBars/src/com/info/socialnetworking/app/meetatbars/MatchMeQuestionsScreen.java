package com.info.socialnetworking.app.meetatbars;

import java.util.Arrays;

import org.json.JSONArray;
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
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
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
public class MatchMeQuestionsScreen extends Activity {

	public static int bar_member = 1;
	NumberPicker height_ft, height_in, age, income;
	LinearLayout liQuestins[];
	int questionsId[] = { R.id.liQuestion1, R.id.liQuestion2, R.id.liQuestion3,
			R.id.liQuestion4, R.id.liQuestion5, R.id.liQuestion6,
			R.id.liQuestion7, R.id.liQuestion8, R.id.liQuestion9,
			R.id.liQuestion10,R.id.liQuestion11,R.id.liQuestion12,R.id.liQuestion13,R.id.liQuestion14};
	int question_number = 0, user_id,back_count=0,incomeValue,ageValue;
	private ProgressDialog pDialog;
	private ProgressBar pbQuestions;
	float height = -1;
	public String[] incomeArray = new String[] {"  ", "No income", "$1000-$10000",
			"$10000-$20000 ", "$20000-$30000 ", "$30000-$50000 ",
			"$50000-$80000", "$80000-$100000", "$100000-$150000",
			"$150000-$199999", "$200000-$300000", "$300000-$500000",
			"$500000 -$1 million", "$1 million- $2 million",
			"$2 million - $5 million", "$5 million - $10 million",
			"$10million - $20 million", "$20 million - $40 million",
			"$40 million and above" };
	Button btnBack,btnSkip;
	String[] getSelectedText;
	String email;
	public static String favouriteBar=null,forFun=null;
	Button selected = null;
	public Button[] selected_views = new Button[8];
	ImageView user_photo;
	EditText etForFun,etFavouriteBar;
	RadioGroup rgGender;
	int gender=-1;
	//float[] question_answers = new float[10];
	ConnectionDetector con;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matchme_questions);
		savedInstanceState = getIntent().getExtras();
		user_id = savedInstanceState.getInt("user_id");
		email = savedInstanceState.getString("email");
		byte[] byteArray = UploadPicture.image_Array;
		Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		initiolize();
		user_photo.setImageBitmap(bmp);
		con = new ConnectionDetector(MatchMeQuestionsScreen.this);
		btnBack = (Button) findViewById(R.id.btnBack);
		pbQuestions = (ProgressBar) findViewById(R.id.pbQuestions);
		pbQuestions.setProgress(0);

		liQuestins = new LinearLayout[14];
		age.setMaxValue(70);
		age.setMinValue(18);
		height_ft.setMaxValue(8);
		height_ft.setMinValue(3);
		height_ft.setWrapSelectorWheel(false);
		height_in.setMaxValue(11);
		height_in.setMinValue(0);
		income.setMaxValue(17);
		income.setDisplayedValues(incomeArray);
		getSelectedText = new String[8];
		btnBack.setVisibility(View.GONE);
		pDialog = new ProgressDialog(this);
		pDialog.setCancelable(false);

		questionsInitiolize();
		answersInitiolize();

		liQuestins[question_number].setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		etFavouriteBar.setText(favouriteBar);
		etForFun.setText(forFun);
		etForFun.getText().clearSpans();
	}
	
	private void initiolize() {
		// TODO Auto-generated method stub
		height_ft = (NumberPicker) findViewById(R.id.npHightFt);
		height_in = (NumberPicker) findViewById(R.id.npHightIn);
		age = (NumberPicker) findViewById(R.id.npAge);
		income = (NumberPicker) findViewById(R.id.npIncome);
		user_photo=(ImageView)findViewById(R.id.user_image);
		btnSkip=(Button)findViewById(R.id.btnSkip);
		etForFun=(EditText)findViewById(R.id.etForFun);
		etForFun.setKeyListener(null);
		etFavouriteBar=(EditText)findViewById(R.id.etFavouriteBar);
		rgGender=(RadioGroup)findViewById(R.id.rgGender);
		rgGender.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				RadioButton rbGender=(RadioButton)findViewById(checkedId);
				gender=Integer.parseInt(rbGender.getTag().toString());
			}
		});
	}

	
	
	private void answersInitiolize() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 10; i++) {
			//question_answers[i] = -1;
			if (i < 8) {
				getSelectedText[i] = null;
				selected_views[i] = null;
			}
		}

	}

	private void questionsInitiolize() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 14; i++) {
			liQuestins[i] = (LinearLayout) findViewById(questionsId[i]);
			liQuestins[i].setVisibility(View.GONE);
		}
	}

	@SuppressLint("NewApi")
	public void next(View v) {

		if (question_number < 10) {

			if (question_number == 0) {
				float a = height_ft.getValue();
				float b = (float) height_in.getValue() / 100;
				height = a + b;

				if (height > 3.0) {
					btnBack.setVisibility(View.VISIBLE);
					liQuestins[question_number].setVisibility(View.GONE);
					question_number++;
					liQuestins[question_number].setVisibility(View.VISIBLE);
					if (selected_views[question_number - 1] != null) {
						selected_views[question_number - 1]
								.setBackgroundDrawable(getResources().getDrawable(
										R.drawable.questions_button_selected));
						selected_views[question_number - 1]
								.setTextColor(getResources().getColor(
										R.color.white));
					}
				} else {
					ToastMessage("Please Choose Height");
				}

			} else if (question_number >= 1 && question_number < 9) {

				/*
				 * if (selected_views[question_number - 1] == null) {
				 * selected_views[question_number - 1] = selected; }
				 */
				if (selected != null) {
					selected.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.blue_stoke));
					selected.setTextColor(getResources().getColor(R.color.black));
					selected = null;
				}
				if(selected_views[question_number-1]!=null)
				{
				liQuestins[question_number].setVisibility(View.GONE);
				question_number++;
				liQuestins[question_number].setVisibility(View.VISIBLE);
				if (question_number < 8
						&& selected_views[question_number - 1] != null) {
					selected_views[question_number - 1]
							.setBackgroundDrawable(getResources().getDrawable(
									R.drawable.questions_button_selected));
					selected_views[question_number - 1]
							.setTextColor(getResources()
									.getColor(R.color.white));
				}
				}
				else{
					ToastMessage("Please choose any one option");
				}
			} 
			else if (question_number == 9) {
				incomeValue = income.getValue();

				if (incomeValue >0) {
					liQuestins[question_number].setVisibility(View.GONE);
					question_number++;
					liQuestins[question_number].setVisibility(View.VISIBLE);

				} else {
					ToastMessage("Please choose income");
				}
			}
		} else if (question_number == 10) {

			ageValue = age.getValue();
			if (ageValue >= 18) {

				liQuestins[question_number].setVisibility(View.GONE);
				question_number++;
				liQuestins[question_number].setVisibility(View.VISIBLE);
			} else {
				ToastMessage("Please Choose Age");
			}
		}else if(question_number==11)
		{
			forFun=etForFun.getText().toString();
			if(forFun.length()>0)
			{
				liQuestins[question_number].setVisibility(View.GONE);
				question_number++;
				liQuestins[question_number].setVisibility(View.VISIBLE);
			}else
			{
				ToastMessage("Please fill above");
			}
			
		}else if(question_number==12)
		{
			favouriteBar=etFavouriteBar.getText().toString();
			if(favouriteBar.length()>0)
			{				
				btnSkip.setVisibility(View.GONE);
				liQuestins[question_number].setVisibility(View.GONE);
				question_number++;
				liQuestins[question_number].setVisibility(View.VISIBLE);
			}else
			{
				ToastMessage("Please enter your favourite bar");
			}
			
		}else if(question_number==13)
		{
			if(gender==-1)
			{
				ToastMessage("Please choose your gender");
			}
			else
			{
				createJsonObject();
			}
		}
		pbQuestions.setProgress(question_number);
	}

	@SuppressLint("NewApi")
	public void skip(View v) {
		if (question_number < 9) {
			btnBack.setVisibility(View.VISIBLE);
			liQuestins[question_number].setVisibility(View.GONE);
			question_number++;
			liQuestins[question_number].setVisibility(View.VISIBLE);
			if (question_number == 1) {
				height_ft.setValue(3);
				height_in.setValue(0);
				height = -1;
			} else if (question_number > 1) {
				//question_answers[question_number - 2] = -1;
				if(selected_views[question_number-2]!=null)
				{
				selected_views[question_number-2].setBackgroundDrawable(getResources().getDrawable(
						R.drawable.blue_stoke));
				selected_views[question_number - 2].setTextColor(getResources()
						.getColor(R.color.black));
				getSelectedText[question_number - 2] = null;
				selected_views[question_number - 2] = null;
				}
			}
		} else if (question_number == 9) {
			liQuestins[question_number].setVisibility(View.GONE);
			question_number++;
			liQuestins[question_number].setVisibility(View.VISIBLE);
			income.setValue(0);
			incomeValue = -1;
		} else if (question_number == 10) {
			liQuestins[question_number].setVisibility(View.GONE);
			question_number++;
			liQuestins[question_number].setVisibility(View.VISIBLE);
			ageValue = -1;
			age.setValue(18);
		}else if(question_number == 11)
		{
			liQuestins[question_number].setVisibility(View.GONE);
			question_number++;
			liQuestins[question_number].setVisibility(View.VISIBLE);
			forFun="";
			etForFun.setText(forFun);
		}else if (question_number==12) {
			btnSkip.setVisibility(View.GONE);
			liQuestins[question_number].setVisibility(View.GONE);
			question_number++;
			liQuestins[question_number].setVisibility(View.VISIBLE);
			favouriteBar="";
			etFavouriteBar.setText(favouriteBar);
		}else if (question_number==13) {
			gender=3;
			createJsonObject();
		}
		pbQuestions.setProgress(question_number);
	}

	@SuppressLint("NewApi")
	public void previous(View v) {

		if (question_number > 0) {
			
			liQuestins[question_number].setVisibility(View.GONE);
			question_number--;
			liQuestins[question_number].setVisibility(View.VISIBLE);
			if (question_number > 0 && question_number < 9
					&& selected_views[question_number - 1] != null) {
				if (selected != null) {
					selected.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.blue_stoke));
					selected.setTextColor(getResources().getColor(R.color.black));
					selected = null;
				}
				selected_views[question_number - 1]
						.setBackgroundDrawable(getResources().getDrawable(
								R.drawable.questions_button_selected));
				selected_views[question_number - 1].setTextColor(getResources()
						.getColor(R.color.white));
			}else if(question_number==12)
			{
				btnSkip.setVisibility(View.VISIBLE);

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
				questions.put("1", height);
				for (int i = 2; i <= 9; i++) {
					if(selected_views[i-2]!=null)
					{
						questions.put(i+"",selected_views[i-2].getTag());
					}else
					{
						questions.put(i+"",-1);
					}
				}
				String[] array=forFun.replace(", ","," ).split(",");
				JSONArray mJSONArray=new JSONArray(Arrays.asList(array));
				questions.put("10", ageValue);
				questions.put("11",incomeValue);
				JSONObject data = new JSONObject();
				data.put("user_id", user_id);
				data.put("profile_type", 1);
				data.put("responses", questions);
				data.put("for_fun", mJSONArray);
				data.put("favorite_bar", favouriteBar);
				data.put("sex", gender);
				Log.v("sending data",data.toString());
				postQuestions(data);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			con.failureAlert();
		}
	}

	@SuppressWarnings({ "unused" })
	public void questionAnswersClicked(View v) {

		int button_id = v.getId();
		// Toast.makeText(getApplicationContext(), ""+button_id,
		// Toast.LENGTH_SHORT).show();
		Button b = (Button) v;
		String buttonText = b.getText().toString();
		String temp = getSelectedText[question_number - 1];

		// Toast.makeText(getApplicationContext(), b.getId(),
		// Toast.LENGTH_SHORT).show();

		if (temp != null && temp.equals(buttonText)) {
			selected = null;
			b.setBackgroundDrawable(getResources().getDrawable(R.drawable.blue_stoke));
			b.setTextColor(getResources().getColor(R.color.black));
			getSelectedText[question_number - 1] = null;
			selected_views[question_number - 1] = selected;

		}
		//
		else {
			if (selected != null
					&& !selected.getText().toString().equals(buttonText)) {

				if (selected_views[question_number - 1] != null) {

					selected_views[question_number - 1]
							.setBackgroundDrawable(getResources().getDrawable(
									R.drawable.blue_stoke));
					selected_views[question_number - 1]
							.setTextColor(getResources().getColor(R.color.black));
					selected = (Button) v;
					selected_views[question_number - 1] = selected;
					b.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.questions_button_selected));
					b.setTextColor(getResources().getColor(R.color.white));
					getSelectedText[question_number - 1] = buttonText;
				} else {

					selected.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.blue_stoke));
					selected.setTextColor(getResources().getColor(R.color.black));
					selected = (Button) v;
					selected_views[question_number - 1] = selected;
					b.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.questions_button_selected));
					b.setTextColor(getResources().getColor(R.color.white));
					getSelectedText[question_number - 1] = buttonText;
				}
			} else {

				if (selected_views[question_number - 1] != null) {

					selected_views[question_number - 1]
							.setBackgroundDrawable(getResources().getDrawable(
									R.drawable.blue_stoke));
					selected_views[question_number - 1]
							.setTextColor(getResources().getColor(R.color.black));
					selected = (Button) v;
					selected_views[question_number - 1] = selected;
					b.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.questions_button_selected));
					b.setTextColor(getResources().getColor(R.color.white));
					getSelectedText[question_number - 1] = buttonText;
				} else {
					selected = (Button) v;
					selected_views[question_number - 1] = selected;
					b.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.questions_button_selected));
					b.setTextColor(getResources().getColor(R.color.white));
					getSelectedText[question_number - 1] = buttonText;
				}
			}
		}
		// Toast.makeText(getApplicationContext(), ""+buttonText,
		// Toast.LENGTH_SHORT).show();
	}

	private void postQuestions(final JSONObject matchMeQuestions) {
		// TODO Auto-generated method stub

		String tag_string_req = "req_MatchMeQuestions";
		pDialog.setMessage("Sending Your Responses ...");
		Log.v("sendingData",matchMeQuestions.toString());
		showDialog();
		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_MATCHMEQUESTIONS,
				matchMeQuestions, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						hideDialog();
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								profileDialogBox();

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

	public void checkBars(View v) {
	
		favouriteBar=etFavouriteBar.getText().toString();
		if(favouriteBar.length()>0){
			Intent i=new Intent(getApplicationContext(),CheckAvilableBars.class);
			i.putExtra("user_id", user_id);
			i.putExtra("bar_name",favouriteBar);
			i.putExtra("navigation","BarHopper");
			startActivity(i);
		}
		else
		{
			ToastMessage("Please enter the bar name");
		}
	}
	
	public void profileDialogBox() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						Intent i = new Intent(getApplicationContext(),
								MainMenu.class);
						i.putExtra("user_id", user_id);
						startActivity(i);
						finish();
					}
				});
		AlertDialog alert = builder.create();
		alert.setTitle("Thank you for registering with us.");
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

	public void cross(View v) {
		
		DeleteEmail delete=new DeleteEmail(MatchMeQuestionsScreen.this);
		delete.deleteEmailAlert(email);
	}

	public void selectForFun(View v)
	{
		
		/*Dialog dialog=new Dialog(this);
		dialog.setContentView(R.layout.for_fun_options);
		dialog.setTitle("Select Options");
		dialog.show();*/
		Intent i=new Intent(getApplicationContext(),ForFunOptions.class);
		i.putExtra("options", forFun);
		i.putExtra("navigation","matchMe");

		//Log.v("testing Strign", forFun);
		startActivity(i);
		
		
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		back_count++;
		if(back_count==1)
		{
			Toast.makeText(getApplicationContext(), "Pressing back will divert you to login screen", Toast.LENGTH_SHORT).show();
		}else if (back_count==2) {
			
			Intent i=new Intent(getApplicationContext(),BarHopperLogin.class);
			startActivity(i);
		}
	}
}

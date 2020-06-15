package com.info.socialnetworking.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.adapters.PrivateQuestionsAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.helper.PrivateQuestionsDetails;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class PrivateQuestions extends Fragment implements OnClickListener {

	View rootView;
	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	public PrivateQuestionsAdapter mAdapter;
	int user_id;
	Others others;
	int answersInt[];
	String viewType;
	Button tvSave, tvCancel;

	String questionsString[] = {
			"What is your attitude about religion?",
			"How important is religion or spirituality in your life?",
			"What is your attitude about sex?",
			"Describe you sex drive?",
			"What is your attitude about marriage?",
			"What type of relationship do you prefer?",
			"How honest are you?",
			"Do you want to have children?",
			"How soon do you want to have children?",
			"If someone you dearly love cheats on you with another person, what is the likelihood that you will forgive and move on?",
			"Do you believe that you need to be married to have long lasting and happy relationship?",
			"How important is physical appearance in finding a partner? ",
			"How important is education level in finding a partner?",
			"How important is the financial status of a partner?",
			"Are you looking for someone to support you financially?",
			"Who should work in the family?",
			"How important is traveling to you? ",
			"How important is sports to you?", "what is your political belief?",
			"How generous are you to your loved one?",
			"How important is money in your life?",
			"How much do you like watching movies?",
			"How ambitious are you in life?",
			"How successful are your life goals?", "How happy are you in life?" };
	String answers1String[] = { "No answer", "No answer", "No answer",
			"No answer", "No answer", "No answer", "No answer", "No answer",
			"Never", "No answer", "No answer", "No answer", "No answer",
			"No answer", "No answer", "No answer", "No answer", "No answer",
			"No answer", "No answer", "No answer", "No answer", "No answer",
			"No answer", "No answer" };
	String answers2String[] = { "Not religious", " Not important ",
			"Very liberal ", "Very weak ", "No marriage ", "Casual ",
			"Dishonest ", "Never ", "No desire ", "Definitely ", "No ",
			"Not important ", "Not important ", " Not important", "No",
			"Woman ", "Not important ", "Not important", "Democratic ",
			"Not Generous ", "Not important ", "Never watch",
			" Not very ambitious", "Not very successful ", "Not very happy " };
	String answer3String[] = { "Somewhat ", "Somewhat ", "Somewhat ",
			"Medium ", "Married within 5 years ", "Fun Committed", "Somewhat ", "Not sure ",
			"Less than 5 yrs ", "Forgive Maybe ", "Yes ", "Somewhat",
			"Somewhat ", "Somewhat", "Maybe", "Both ", "Somewhat", "Somewhat",
			"Independent ", "Somewhat", " Somewhat ", "Occasional ",
			"Somewhat ", "Somewhat ", "Somewhat", };
	String answer4String[] = { "Very religious", "Very important",
			"Conservative ", "Very strong", "Married within 10 yrs or more", "Serious commitment",
			"Very Honest", "Definitely", "Greater than 5 years", "Break up", "Not sure",
			"Very important", "Very Educated", "Very rich", "Yes", "Man",
			"Very Important", "Very important", "Republican", "Very generous ",
			"Very important", "Always watch", "Very Ambitious ",
			"Very successful", "Very Happy" };

	private List<PrivateQuestionsDetails> privateQuestionsList = null;
	ConnectionDetector con;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.private_questions, container,
				false);
		initiolize();
		loadData();

		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getActivity());
		// use a linear layout manager
		mRecyclerView.setLayoutManager(mLayoutManager);

		return rootView;
	}

	private void loadData() {
		// TODO Auto-generated method stub
		viewType = "show";
		try {
			JSONObject bars = new JSONObject();
			
			bars.put("user_id", user_id);
			bars.put("mode", viewType);
			if (con.isConnectingToInternet()) {
				getPrivateQuestion(bars);
			}else{
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		mRecyclerView = (RecyclerView) rootView
				.findViewById(R.id.rvPrivateQuestions);
		tvSave = (Button) rootView.findViewById(R.id.tvSave);
		tvCancel = (Button) rootView.findViewById(R.id.tvCancel);

		tvSave.setOnClickListener(this);
		tvCancel.setOnClickListener(this);
		privateQuestionsList = new ArrayList<PrivateQuestionsDetails>();
		user_id = BarHopperHomeScreen.user_id;
		others = new Others(getActivity());
		con = new ConnectionDetector(getActivity());
		answersInt = new int[questionsString.length];
		for (int i = 0; i < questionsString.length; i++) {
			answersInt[i] = -1;
		}
	}

	private void getPrivateQuestion(final JSONObject bars) {
		// TODO Auto-generated method stub
		String tag_string_req = "req_Bars";

		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_PRIVATE_RESPONSES, bars,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								if (viewType == "show") {
									loadInitialAnswers(response);

								} else if (viewType == "save") {
									Toast.makeText(getActivity(),
											"profile has updated",
											Toast.LENGTH_SHORT).show();
									getActivity().getFragmentManager()
											.popBackStack();

								}
							} else {

								loadInitialAnswers(response);
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

	private void loadInitialAnswers(JSONObject response) {
		// TODO Auto-generated method stub
		// privateQuestionsList.clear();
		try {
			if (response.getBoolean("error")) {

			} else {
				JSONArray answers = response.optJSONArray("responses");

				for (int i = 0; i < answers.length(); i++) {
					try {
						JSONObject obj = answers.getJSONObject(i);
						// Iterator<String> key=obj.keys();

						Iterator<String> iter = obj.keys();
						while (iter.hasNext()) {
							String key = iter.next();
							try {
								String value = obj.getString(key);
								answersInt[   (Integer.parseInt(key) - 1)      ] = Integer.parseInt(value);
							} catch (JSONException e) {
								// Something went wrong!
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (int i = 0; i < questionsString.length; i++) {
			privateQuestionsList.add(new PrivateQuestionsDetails(
					new String[]{questionsString[i], i + "", answers1String[i],answers2String[i], answer3String[i], answer4String[i],answersInt[i] + "" }));
			 Log.v("jsonString", privateQuestionsList.toString());

		}
		
		mAdapter = new PrivateQuestionsAdapter(privateQuestionsList,mRecyclerView, getActivity());
		// set the adapter object to the
		// Recyclerview
		mRecyclerView.setAdapter(mAdapter);
		Log.v("jsonString", Arrays.toString(answersInt));

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tvSave:
			saveResponses();
			break;
		case R.id.tvCancel:
			getActivity().getFragmentManager().popBackStack();
			break;

		default:
			break;
		}

	}

	private void saveResponses() {
		// TODO Auto-generated method stub
		viewType = "save";
		try {
			JSONObject obj = new JSONObject();
			obj.put("user_id", user_id);
			obj.put("mode", viewType);
			JSONObject response = new JSONObject();
			for (int i = 0; i < questionsString.length; i++) {
				response.put(((i + 1) + ""), privateQuestionsList.get(i).getAnswer1Value());
			}
			obj.put("responses", response);
			getPrivateQuestion(obj);
			// Log.v("Edited Answers",obj.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}

package com.info.socialnetworking.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.adapters.MatchMeQuestionsAdapter;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.MatchMeQuestions;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

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
import android.widget.Toast;

public class MatchMeQuestionsMain extends Fragment implements OnClickListener {
	View rootView;
	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	public MatchMeQuestionsAdapter mAdapter;
	int user_id;
	Others others;
	int answersInt[];
	String viewType;
	Button tvSave, tvCancel;
	ConnectionDetector con;

	private List<MatchMeQuestions> matchMeQuestionsList = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.matchme_questions_editing,
				container, false);

		initiolize();
		loadData();

		mRecyclerView.setHasFixedSize(true);
		mLayoutManager = new LinearLayoutManager(getActivity());
		// use a linear layout manager
		mRecyclerView.setLayoutManager(mLayoutManager);

		return rootView;
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		mRecyclerView = (RecyclerView) rootView
				.findViewById(R.id.rvMatchMeQuestions);
		tvSave = (Button) rootView.findViewById(R.id.tvSave);
		tvCancel = (Button) rootView.findViewById(R.id.tvCancel);
		answersInt = new int[13];
		for (int i = 0; i < answersInt.length; i++)
			answersInt[i] = -1;

		tvSave.setOnClickListener(this);
		tvCancel.setOnClickListener(this);
		matchMeQuestionsList = new ArrayList<MatchMeQuestions>();
		user_id = BarHopperHomeScreen.user_id;
		others = new Others(getActivity());
		con = new ConnectionDetector(getActivity());

	}

	private void loadData() {
		// TODO Auto-generated method stub
		viewType = "show";
		try {
			JSONObject bars = new JSONObject();
			bars.put("user_id", user_id);
			bars.put("mode", viewType);
			if (con.isConnectingToInternet()) {
				getMatchMeQuestions(bars);
			} else {
				con.failureAlert();
			}
			// loadInitialBars(bars);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void getMatchMeQuestions(JSONObject bars) {
		// TODO Auto-generated method stub

		String tag_string_req = "req_Bars";

		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_HOPPER_PROFILE, bars,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								if (viewType == "show") {
									loadInitialAnswers(response);

									mAdapter = new MatchMeQuestionsAdapter(
											matchMeQuestionsList,
											mRecyclerView, getActivity());
									// set the adapter object to the
									// Recyclerview
									mRecyclerView.setAdapter(mAdapter);
								} else if (viewType == "save") {
									Toast.makeText(getActivity(),
											"profile has updated",
											Toast.LENGTH_SHORT).show();
									getActivity().getFragmentManager()
											.popBackStack();

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
						others.ToastMessage(error.getMessage());
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
		try {
			if (!optString(response,"match").equals("")) {

				JSONArray answers = response.optJSONArray("match");

				for (int i = 0; i < answers.length(); i++) {
					JSONObject obj = answers.getJSONObject(i);
					// Iterator<String> key=obj.keys();

					Iterator<String> iter = obj.keys();
					while (iter.hasNext()) {
						String key = iter.next();
						try {
							double value = obj.getDouble(key);

							answersInt[(Integer.parseInt(key) - 1)] = (int) value;

						} catch (JSONException e) {
							// Something went wrong!
							e.printStackTrace();
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.v("answers", Arrays.toString(answersInt));
		matchMeQuestionsList.add(new MatchMeQuestions(new String[] {
				"What is your hair color?", answersInt[1] + "", "Black",
				"Blonde", "Red", "Brunette", "Brown", "Others" }));
		matchMeQuestionsList.add(new MatchMeQuestions(new String[] {
				"What is your body type?", answersInt[2] + "", "Slim",
				"Average", "Sporty", "Muscular", "Large" }));
		matchMeQuestionsList.add(new MatchMeQuestions(new String[] {
				"Do you smoke?", answersInt[3] + "", "Occasionally", "Yes",
				"No" }));
		matchMeQuestionsList.add(new MatchMeQuestions(new String[] {
				"Do you drink alcoholic beverages?", answersInt[4] + "",
				"Occasionally", "Yes", "No" }));
		matchMeQuestionsList.add(new MatchMeQuestions(new String[] {
				"What is your marital status?", answersInt[5] + "", "Single",
				"Divorced", "Married", "Separated" }));
		matchMeQuestionsList.add(new MatchMeQuestions(
				new String[] { "What is your ethnicity?", answersInt[6] + "",
						"Caucasian/white", "Hispanic",
						"Black/African American", "Middle eastern",
						"American Indian", "Asian", "Mixed", "Others" }));
		matchMeQuestionsList.add(new MatchMeQuestions(new String[] {
				"Do you have any children?", answersInt[7] + "", "None", "One",
				"Two", "Three", "Four or more" }));
		matchMeQuestionsList.add(new MatchMeQuestions(new String[] {
				"What is your education level?", answersInt[8] + "",
				"Did't finish H.S", "High school", "Still in college",
				"Associate degree", "Bachelors degree", "Masters",
				"Doctorate Degree" }));
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

	public static String optString(JSONObject json, String key) {
		// http://code.google.com/p/android/issues/detail?id=13830
		if (json.isNull(key) || json.optString(key).equals("-1"))
			return "";
		else
			return json.optString(key);
	}

	private void saveResponses() {
		// TODO Auto-generated method stub
		viewType = "save";
		try {
			JSONObject obj = new JSONObject();
			obj.put("user_id", user_id);
			obj.put("mode", viewType);
			JSONObject response = new JSONObject();
			JSONObject match = new JSONObject();
			for (int i = 0; i < matchMeQuestionsList.size(); i++) {
				match.put(((i + 2) + ""), matchMeQuestionsList.get(i)
						.getAnswerSelected());
			}
			response.put("match", match);
			obj.put("responses", response);
			if (con.isConnectingToInternet()) {
				getMatchMeQuestions(obj);
			} else {
				con.failureAlert();
			}
			// Log.v("Edited Answers",obj.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}

 package com.info.socialnetworking.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.info.socialnetworking.app.adapters.MeetOthersFilterAdapter;
import com.info.socialnetworking.app.helper.AgeFilterDetails;
import com.info.socialnetworking.app.helper.HeightFilterHelper;
import com.info.socialnetworking.app.helper.LocationFilterDetails;
import com.info.socialnetworking.app.helper.MeetOthersFilterDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.MainMenu;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class MeetOthersFilter extends Fragment implements OnClickListener {

	View rootView;
	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	Button btnReset, btnApply;

	String questionsString[] = { "","","","Looking For", "Hair color", "Body type",
			"Smoke", "Alcohol", "Marital status", "Ethnicity",
			"Children", "Education level", "Attitude about religion",
			"Importance of religion or spirituality", "Attitude about sex",
			"Sex drive", "Attitude about marriage", "Type of relationship",
			"Honesty", "Children", "Want to have children",
			"Someone cheats you",
			"Need to be married to have long lasting and happy relationship",
			"Importance of looks in finding a partner",
			"Education level in finding a partner",
			"Financial status of a partner",
			"Looking for someone to support you financially",
			"Who should work in the family", "Traveling",
			"Sports", "Political belief",
			"Generous for loved once", "Importance of money",
			"Movies", "Ambitious", "Life goals",
			"How happy are you in life" };
	String answers1String[] = { "","","","Female", "Black", "Slim", "Occasionally",
			"Occasionally", "Single", "Caucasian/white", "None",
			"Not finish high school", "No answer", "No answer", "No answer",
			"No answer", "No answer", "No answer", "No answer", "No answer",
			"Never", "No answer", "No answer", "No answer", "No answer",
			"No answer", "No answer", "No answer", "No answer", "No answer",
			"No answer", "No answer", "No answer", "No answer", "No answer",
			"No answer", "No answer" };
	String answers2String[] = { "","","", "Male", "Blonde", "Average", "Yes", "Yes",
			"Divorced", "Hispanic", "One", "High school", "Not religious",
			" Not important ", "Very liberal ", "Very weak ", "No marriage ",
			"Casual ", "Dishonest ", "Never ", "No desire ", "Definitely ",
			"No ", "Not important ", "Not important ", " Not important", "No",
			"Woman ", "Not important ", "Not important", "Democratic ",
			"Not Generous ", "Not important ", "Never watch",
			" Not very ambitious", "Not very successful ", "Not very happy " };
	String answer3String[] = { "","","", "", "Red", "Sporty", "No", "No", "Married",
			"Black/African American", "Two", "Still in college", "Somewhat ",
			"Somewhat ", "Somewhat ", "Medium ", "Married within 5 years ", "Fun Committed  ",
			"Somewhat ", "Not sure ", "Less than 5 yrs ", "Forgive Maybe ",
			"Yes ", "Somewhat", "Somewhat ", "Somewhat", "Maybe", "Both ",
			"Somewhat", "Somewhat", "Independent ", "Somewhat", " Somewhat ",
			"Occasional ", "Somewhat ", "Somewhat ", "Somewhat", };
	String answer4String[] = { "","","", "", "Brunette", "Muscular", "", "",
			"Separated", "Middle eastern", "Three", "Associate degree",
			"Very religious", "Very important", "Conservative ", "Very strong",
			"Married within 10 yrs or more", "Serious commitment", "Very Honest", "Definitely",
			"Greater than 5 years", "Break up", "Not sure", "Very important", "Very Educated",
			"Very rich", "Yes", "Man", "Very Important", "Very important",
			"Republican", "Very generous ", "Very important", "Always watch",
			"Very Ambitious ", "Very successful", "Very Happy" };
	String answer5String[] = { "","","", "", "Brown", "Large", "", "", "",
			"American Indian", "Four or more", "Bachelors degree" };
	String answer6String[] = { "","","", "", "Others", "", "", "", "", "Asian", "",
			"Masters" };
	String answer7String[] = { "","", "","", "", "", "", "", "", "Mixed", "",
			"Doctorate Degree" };
	String answer8String[] = { "","","", "", "", "", "", "", "", "Others", "", "" };

	ConnectionDetector con;
	Others others;
	MeetOthersFilterAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_meet_others_filter,
				container, false);
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
		if (MainMenu.meetOthersFilterIdentification.equals("filter_applied"));
		else {
			for (int i = 0; i < questionsString.length; i++) {
				if (i == 0) {
					MainMenu.locationFilterList.add(new LocationFilterDetails());
					MainMenu.meetOthersFilterList.add(new MeetOthersFilterDetails(new String[] {"", "","", "","" }));

				}else if(i==1){
					MainMenu.heightFilterList.add(new HeightFilterHelper("Height"));
					MainMenu.meetOthersFilterList.add(new MeetOthersFilterDetails(new String[] {"", "","", "","" }));
					
				}else if(i==2){
					MainMenu.ageFilterList.add(new AgeFilterDetails("Age"));
					MainMenu.meetOthersFilterList.add(new MeetOthersFilterDetails(new String[] {"", "","", "","" }));
					
				}else if (i < 12) {
					MainMenu.meetOthersFilterList.add(new MeetOthersFilterDetails(new String[] {
									questionsString[i], answers1String[i],
									answers2String[i], answer3String[i],
									answer4String[i], answer5String[i],
									answer6String[i], answer7String[i],
									answer8String[i] }));
				} else {
					MainMenu.meetOthersFilterList
							.add(new MeetOthersFilterDetails(new String[] {
									questionsString[i], answers1String[i],
									answers2String[i], answer3String[i],
									answer4String[i] }));
				}
			}
		}
		
		mAdapter = new MeetOthersFilterAdapter(MainMenu.meetOthersFilterList,MainMenu.locationFilterList,MainMenu.heightFilterList,MainMenu.ageFilterList, mRecyclerView, getActivity());
		mRecyclerView.setAdapter(mAdapter);
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		mRecyclerView = (RecyclerView) rootView
				.findViewById(R.id.rvMeetOthersFilter);
		btnReset = (Button) rootView.findViewById(R.id.btnReset);
		btnApply = (Button) rootView.findViewById(R.id.btnApply);
		btnReset.setOnClickListener(this);
		btnApply.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnReset:
			MainMenu.meetOthersFilterList.clear();
			MainMenu.meetOthersFilterIdentification="home_screen";
			MainMenu.locationFilterList.clear();
			MainMenu.heightFilterList.clear();
			MainMenu.ageFilterList.clear();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.detach(this).attach(this).commit();
			break;
		case R.id.btnApply:
			try {
				JSONObject obj = new JSONObject();
				JSONObject match = new JSONObject();
				JSONObject privateRes = new JSONObject();
				for (int i = 0; i <  MainMenu.meetOthersFilterList.size(); i++) {
					List<Integer> selections = new ArrayList<Integer>();

					for (int j = 0; j < MainMenu.meetOthersFilterList.get(i).answersSelected.length; j++) {
						boolean selectedAnswers[] = MainMenu.meetOthersFilterList
								.get(i).answersSelected;
						if (selectedAnswers[j]) {
							selections.add(j + 1);
						}
					}
					obj.put("criteria", "meet_others");

					if (i == 0) {
						LocationFilterDetails location = MainMenu.locationFilterList
								.get(i);
						Log.v("city",
								location.getCity() + "/" + location.getState()
										+ "/" + location.getDistance());
						if (location.getCity().length() > 0)
							obj.put("hopper_city", location.getCity());
						if (location.getState().length() > 0)
							obj.put("hopper_state", location.getState());
						if (location.getDistance().length() > 0)
							obj.put("distance", location.getDistance());
						// obj.put("is_online", selections.toString());
					}else if(i==1){
						String from,to;
						from=MainMenu.heightFilterList.get(0).getHeightFt()+"."+MainMenu.heightFilterList.get(0).getHeightIn();
						to=MainMenu.heightFilterList.get(0).toGetHeightFt()+"."+MainMenu.heightFilterList.get(0).toGetHeightIn();
						if(!(from.equals("3.0")&&to.equals("3.0"))){
							JSONArray height=new JSONArray();
							height.put(from);
							height.put(to);
							match.put("1", height);
						}
					}else if(i==2){
						String from,to;
						from=MainMenu.ageFilterList.get(0).getFromAge()+"";
						to=MainMenu.ageFilterList.get(0).getToAge()+"";
						if(!(from.equals("18")&&to.equals("18"))){
							JSONArray age=new JSONArray();
							age.put(from);
							age.put(to);
							match.put("10", age);
						}
						
					}else if (i == 3) {
						if (!selections.toString().equals("[]")) {
							JSONArray jsArray = new JSONArray(selections);
							if (jsArray.toString().equals("[1]"))
								obj.put("looking_for", 0);
							else if (jsArray.toString().equals("[2]"))
								obj.put("looking_for", 1);
							else if (jsArray.toString().equals("[1,2]"))
								obj.put("looking_for", 2);
						}
					}
					else if (i > 3 && i <= 11) {
						if (!selections.toString().equals("[]")) {
							JSONArray jsArray = new JSONArray(selections);
							match.put(i-2 + "", jsArray);
						}
					} else if (i > 11 && i <= questionsString.length) {
						if (!selections.toString().equals("[]")) {
							Log.v("i value", i + "");
							JSONArray jsArray = new JSONArray(selections);
							privateRes.put(i - 11 + "", jsArray);
						}
					}
				}
				obj.put("match", match);
				obj.put("private", privateRes);
				
				int i=obj.toString().length();
				//Log.("length",i);
				//System.out.println("length"+i);
				Log.v("Filter response", obj.toString());

				Bundle bund = new Bundle();
				MainMenu.clickedmenu = 3;
				MainMenu.meetOthersFilterIdentification = "filter_applied";
				MainMenu.meetOthersFilterString = obj.toString();
				FragmentFriends meetOthers = new FragmentFriends();
				bund.putString("FilterIdentification", "filter_applied");
				bund.putString("jsonObject", obj.toString());
				meetOthers.setArguments(bund);
				this.getFragmentManager().beginTransaction()
						.replace(R.id.flContainer, meetOthers)
						.addToBackStack(null).commit();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			// tvHoppers.setBackgroundColor(getResources().getColor(R.color.login_pressed));
		default:
			break;
		}
	}
}

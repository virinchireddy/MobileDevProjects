
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

import com.info.socialnetworking.app.adapters.BarsFilterAdapter;
import com.info.socialnetworking.app.helper.BarsFilterDetails;
import com.info.socialnetworking.app.helper.LocationFilterDetails;
import com.info.socialnetworking.app.helper.MeetOthersFilterDetails;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.MainMenu;
import com.info.socialnetworking.app.meetatbars.R;

public class BarsFilter extends Fragment implements OnClickListener {
	View rootView;
	private RecyclerView mRecyclerView;
	private LinearLayoutManager mLayoutManager;
	Button btnReset, btnApply;
	BarsFilterAdapter mAdapter;

	String questionsString[] = { "","","Featured", "Drink specials",
			"Entertainment", "Outdoor seating", "Dance floor", "Dj music",
			"Parking", "Slot Machine", "Smoking", "Swimming pool", "Taxi",
			"Wifi", "Billiards" };
	String questionsStringTags[] = { "","", "is_featured", "drink_specials",
			"live_entertainment", "outdoor_seating", "dance_floor", "dj",
			"parking", "slot_machines", "smoking", "swimming_pool", "taxi",
			"wifi", "billiards" };
	String jsonObject;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_bars_filter, container,
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
		Log.v("filter_identification",MainMenu.barsFilterIdentification);
		if (MainMenu.barsFilterIdentification.equals("filter_applied"))
			;
		else {
			for (int i = 0; i < questionsString.length; i++) {
				if (i == 0) {
					MainMenu.BarslocationFilterList.add(new LocationFilterDetails());
					MainMenu.barsFilterList.add(new BarsFilterDetails());
				}else if(i==1){
					MainMenu.BarsCapacityFilterList.add(new MeetOthersFilterDetails(new String[]{"Capacity","Small","Medium","Large"}));
					MainMenu.barsFilterList.add(new BarsFilterDetails());
				}
				else {
	////				
					MainMenu.barsFilterList.add(
							new BarsFilterDetails(questionsString[i], questionsStringTags[i]));
				}
			}
		}
		mAdapter = new BarsFilterAdapter(MainMenu.barsFilterList,MainMenu.BarslocationFilterList,MainMenu.BarsCapacityFilterList, mRecyclerView, getActivity());
		mRecyclerView.setAdapter(mAdapter);
	}

	
	
	//just to check the filter problem
	
	
	private void initiolize() {
		// TODO Auto-generated method stub
		mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvBarsFilter);
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
			MainMenu.barsFilterList.clear();
			MainMenu.barsFilterIdentification="home_screen";
			MainMenu.BarslocationFilterList.clear();
			MainMenu.BarsCapacityFilterList.clear();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.detach(this).attach(this).commit();
			break;
		case R.id.btnApply:
			
			JSONObject obj=new JSONObject();
			JSONObject barFacilities=new JSONObject();
			try{
				
			for(int i=0;i<MainMenu.barsFilterList.size();i++)
			{
					if(i==0)
					{
						LocationFilterDetails location = MainMenu.BarslocationFilterList
								.get(i);
						Log.v("city",
								location.getCity() + "/" + location.getState()
										+ "/" + location.getDistance());

						if (location.getCity().length() > 0)
							obj.put("bar_city", location.getCity());
						if (location.getState().length() > 0)
							obj.put("bar_state", location.getState());
						if (location.getDistance().length() > 0)
							obj.put("distance", location.getDistance());
						
					}else if(i==1){
						
						MeetOthersFilterDetails capacity=MainMenu.BarsCapacityFilterList.get(i-1);
						List<String> selections = new ArrayList<String>();
						boolean selectedAnswers[] = capacity.answersSelected;
						for(int j=0;j<capacity.answersSize();j++){
							if(selectedAnswers[j]){
								if(j==0)
								selections.add(capacity.getAnswer1());
								if(j==1)
									selections.add(capacity.getAnswer2());
								if(j==2)
									selections.add(capacity.getAnswer3());
							}
							
						}
						JSONArray jsArray = new JSONArray(selections);
						if(!jsArray.toString().equals("[]"))
							obj.put("capacity", jsArray);
					}
					else{
	/////			
					if(MainMenu.barsFilterList.get(i).getIsSelected())
					{
						if(MainMenu.barsFilterList.get(i).getQuestion().equals("Featured"))
							obj.put("is_featured",1);
						else if(MainMenu.barsFilterList.get(i).getQuestion().equals("Drink specials"))
							obj.put("drink_specials",1);
						else
							barFacilities.put(MainMenu.barsFilterList.get(i).getQuestionTag(),"1");
					}}
			}
			if(!barFacilities.toString().equals("{}"))
				obj.put("bar_facilities",barFacilities);
			
			Bundle bund = new Bundle();
			MainMenu.clickedmenu = 1;
			MainMenu.barsFilterIdentification= "filter_applied";
			MainMenu.barsFilterString = obj.toString();
			FragmentBarsPramotionsPage bars = new FragmentBarsPramotionsPage();
			bund.putString("FilterIdentification", "filter_applied");
			bund.putString("jsonObject", obj.toString());
			bars.setArguments(bund);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, bars)
					.addToBackStack(null).commit();
			
			
			Log.v("filterbars", obj.toString());
			
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
			break;
		default:
			break;
		}
	

	}
}

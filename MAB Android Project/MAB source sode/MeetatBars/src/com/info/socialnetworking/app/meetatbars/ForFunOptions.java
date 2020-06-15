package com.info.socialnetworking.app.meetatbars;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.info.socialnetworking.app.Fragment.FragmentEditMyProfile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class ForFunOptions extends Activity implements OnClickListener {

	int[] checkBoxIds = { R.id.cbBasketBall, R.id.cbFootball, R.id.cbReading,
			R.id.cbSwimming, R.id.cbShopping, R.id.cbCooking, R.id.cbTravel,
			R.id.cbOutdoorActivities, R.id.cbCamping, R.id.cbHiking,
			R.id.cbWorkout, R.id.cbWatchMovies, R.id.cbComputerGames,
			R.id.cbOpera, R.id.cbSymphony, R.id.cbAdventuresSinging,
			R.id.cbGoingToMuseum, R.id.cbCrossfit, R.id.cbMartialArts,
			R.id.cbBoxing, R.id.cbAerobics, R.id.cbSoccer, R.id.cbBaseball,
			R.id.cbBarHopping };
	CheckBox[] for_fun = new CheckBox[checkBoxIds.length];
	List<String> selectedText;
	List<String> forFunText=new ArrayList<String>();
	String mainText,navigation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.for_fun_options);
		savedInstanceState=getIntent().getExtras();
		mainText=savedInstanceState.getString("options");
		//mainText=mainText.trim();
		navigation=savedInstanceState.getString("navigation");
		//Log.v("testing Strign", mainText);

		initiolize();
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		for(int i=0;i<checkBoxIds.length;i++)
		{	
			for_fun[i]=(CheckBox)findViewById(checkBoxIds[i]);
			for_fun[i].setOnClickListener(this);
			forFunText.add(for_fun[i].getText().toString());
			
		}
		if(mainText!=null)
		{
			String[] array=mainText.replace(", ","," ).split(",");
			selectedText=new ArrayList<String>(Arrays.asList(array));		
			Log.v("testing Strign", selectedText.toString());
		}
		else
			selectedText = new ArrayList<String>();
		Log.v("selectedText size", selectedText.size()+"");
		for(int i=0;i<selectedText.size();i++)
		{
			if(forFunText.contains(selectedText.get(i).toString()))
			{
				int value=forFunText.indexOf(selectedText.get(i).toString().trim());
				Log.v("indexces", value+"");	
				for_fun[value].setChecked(true);
			}
		}
	}
	
	public boolean containsCaseInsensitive(String s, List<String> l){
	     for (String string : l){
	        if (string.equalsIgnoreCase(s)){
	            return true;
	         }
	     }
	    return false;
	  }
	public void select(View v) {
		
		String selectedTextTemp;
		selectedTextTemp=selectedText.toString().replace("[, ", "[");
		
		Log.v("selected", selectedText.toString());

		selectedTextTemp=selectedTextTemp.replace("[", "");
		selectedTextTemp=selectedTextTemp.replace("]", "");
		selectedTextTemp=selectedTextTemp.trim();
		if(selectedTextTemp.length()>0)
		if(selectedTextTemp.substring(0, 1).equals(","))
			selectedTextTemp=selectedTextTemp.substring(1);
		if(navigation.equals("matchMe"))
			MatchMeQuestionsScreen.forFun=selectedTextTemp;
		else if(navigation.equals("myProfile"))
			FragmentEditMyProfile.forFun=selectedTextTemp;
		Log.v("selected", selectedTextTemp);

		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		CheckBox checkBox = (CheckBox) v;
		if (checkBox.isChecked()) {
			selectedText.add(checkBox.getText().toString());

		} else if (selectedText.contains(checkBox.getText().toString())) {
			selectedText.remove(checkBox.getText().toString());
		}
	}
	public void cancel(View v){
		finish();
	}
}

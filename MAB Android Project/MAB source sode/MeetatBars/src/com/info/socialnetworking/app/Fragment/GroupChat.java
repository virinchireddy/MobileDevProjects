package com.info.socialnetworking.app.Fragment;

import com.info.socialnetworking.app.meetatbars.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GroupChat extends Fragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View rootView=inflater.inflate(R.layout.group_chat, container,false);
		
		return rootView;
	}
	
	
}

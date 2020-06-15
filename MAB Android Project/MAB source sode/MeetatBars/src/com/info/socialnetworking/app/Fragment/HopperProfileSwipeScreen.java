package com.info.socialnetworking.app.Fragment;

import java.util.ArrayList;

import com.info.socialnetworking.app.adapters.HopperProfileSwipeAdapter;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HopperProfileSwipeScreen extends Fragment{

	View rootView;
	private ViewPager pager;
	HopperProfileSwipeAdapter adapter;
	int position;
	Others others;
	ConnectionDetector con;
	private ArrayList<String> userIds = new ArrayList<String>();
	private FragmentActivity context;
	
	public HopperProfileSwipeScreen() {
		// TODO Auto-generated constructor stub
	}
	
	public HopperProfileSwipeScreen(ArrayList<String> userIds,
			int position) {
		// TODO Auto-generated constructor stub
		this.userIds = userIds;
		this.position = position;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_hopper_profile_swipe,
				container, false);
		
		initiolize();
		adapter = new HopperProfileSwipeAdapter(getActivity(), userIds);
		pager.setAdapter(adapter);
		
		pager.setCurrentItem(position);

		return rootView;
	}

	@Override
	@Deprecated
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		context = (FragmentActivity) activity;
		super.onAttach(activity);
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		pager = (ViewPager) rootView.findViewById(R.id.vpHopperSwipe);
	}
	
	
}

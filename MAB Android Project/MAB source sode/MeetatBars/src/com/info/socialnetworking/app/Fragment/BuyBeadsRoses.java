package com.info.socialnetworking.app.Fragment;

import com.info.socialnetworking.app.meetatbars.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BuyBeadsRoses extends Fragment {

	View rootView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_buy_beads_roses,
				container, false);
		return rootView;
	}
}

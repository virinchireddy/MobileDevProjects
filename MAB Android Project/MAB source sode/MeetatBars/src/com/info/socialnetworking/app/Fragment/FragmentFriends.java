package com.info.socialnetworking.app.Fragment;

import com.info.socialnetworking.app.meetatbars.MainMenu;
import com.info.socialnetworking.app.meetatbars.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class FragmentFriends extends Fragment implements OnClickListener {

	TextView tvHoppers, tvFriends;
	String filterIdentification, jsonObject;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_friends_tabs,
				container, false);
		savedInstanceState = getArguments();
		filterIdentification = savedInstanceState
				.getString("FilterIdentification");
		if (filterIdentification.equals("filter_applied"))
			jsonObject = savedInstanceState.getString("jsonObject");
		tvHoppers = (TextView) rootView.findViewById(R.id.tvHoppers);
		tvFriends = (TextView) rootView.findViewById(R.id.tvFriends);

		tvHoppers.setOnClickListener(this);
		tvFriends.setOnClickListener(this);

		initialNavigation();
		return rootView;
	}

	private void initialNavigation() {
		// TODO Auto-generated method stub

		if (MainMenu.clickedmenu == 4) {
			MainMenu.clickedmenu = 3;
			FriendsPage friends = new FriendsPage();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flMeet, friends).addToBackStack(null)
					.commit();
			tvFriends.setBackgroundColor(getResources().getColor(
					R.color.tab_color));
		} else {
			Bundle obj = new Bundle();
			MeetOthersPage meetOthers = new MeetOthersPage();
			if (filterIdentification.equals("filter_applied"))
				obj.putString("jsonObject", jsonObject);
			obj.putString("FilterIdentification", filterIdentification);
			meetOthers.setArguments(obj);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flMeet, meetOthers).addToBackStack(null)
					.commit();
			tvHoppers.setBackgroundColor(getResources().getColor(
					R.color.tab_color));
		}
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.tvHoppers) {

			tvHoppers.setBackgroundColor(getResources().getColor(
					R.color.tab_color));
			tvFriends.setBackgroundColor(getResources().getColor(
					R.color.login_pressed));

			Bundle obj = new Bundle();
			obj.putString("FilterIdentification", MainMenu.meetOthersFilterIdentification);
			MeetOthersPage wantToMeet = new MeetOthersPage();
			wantToMeet.setArguments(obj);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flMeet, wantToMeet).addToBackStack(null)
					.commit();

		} else if (v.getId() == R.id.tvFriends) {
			tvHoppers.setBackgroundColor(getResources().getColor(
					R.color.login_pressed));
			tvFriends.setBackgroundColor(getResources().getColor(
					R.color.tab_color));
			FriendsPage friends = new FriendsPage();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flMeet, friends).addToBackStack(null)
					.commit();
		}

	}

}

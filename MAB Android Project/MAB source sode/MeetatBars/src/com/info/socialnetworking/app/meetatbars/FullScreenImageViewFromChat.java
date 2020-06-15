package com.info.socialnetworking.app.meetatbars;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;


import com.info.socialnetworking.app.adapters.FullScreenImageAdapter;
import com.info.socialnetworking.app.helper.ManagePhotosDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class FullScreenImageViewFromChat extends Activity {

	private ViewPager pager;
	ManagePhotosDetails photos;
	FullScreenImageAdapter adapter;
	int position;
	Others others;
	ConnectionDetector con;
	
	private ArrayList<ManagePhotosDetails> mGridData = new ArrayList<ManagePhotosDetails>();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_screen_imageview);

		initiolize();
		savedInstanceState = getIntent().getExtras();
		mGridData=UserProfilePictureFromChat.mGridData;
		position=savedInstanceState.getInt("position");
		adapter = new FullScreenImageAdapter(this, mGridData);
		pager.setAdapter(adapter);
		/*if (savedInstanceState.getString("fullImageNavigation").equals(
				"OtherUserPhotos")) {
			ivDelete.setVisibility(View.GONE);
			tvMakeAsProfile.setVisibility(View.GONE);
		} else if (savedInstanceState.getString("fullImageNavigation").equals(
				"ManagePhotos")) {
			ivDelete.setVisibility(View.VISIBLE);
			tvMakeAsProfile.setVisibility(View.VISIBLE);
		}
		else if (savedInstanceState.getString("fullImageNavigation").equals(
				"barPhotos")) {
			ivDelete.setVisibility(View.GONE);
			tvMakeAsProfile.setVisibility(View.GONE);
			Others.navigation="barProfile";
		}
		ivDelete.setOnClickListener(this);*/
		// displaying selected image first
		pager.setCurrentItem(position);
		
		
	}
	
	private void initiolize() {
		// TODO Auto-generated method stub
		pager = (ViewPager) findViewById(R.id.pager);
		/*ivDelete = (ImageView)findViewById(R.id.ivDelete);
		tvMakeAsProfile = (TextView)findViewById(R.id.tvMakeAsProfile);
		tvMakeAsProfile.setOnClickListener(this);*/

	}

	public void back(View v)
	{
		onBackPressed();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	
}

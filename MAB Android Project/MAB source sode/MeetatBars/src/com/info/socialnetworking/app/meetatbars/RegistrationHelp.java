package com.info.socialnetworking.app.meetatbars;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RegistrationHelp extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration_help);
	}

	public void back(View v) {
		
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		
		finish();

	}

}

package com.info.socialnetworking.app.meetatbars;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;

public class WelcomeScreen extends Activity {

	private static int SPLASH_TIME_OUT = 3000;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome_screen);
		Log.v("device Token", Secure.getString(this.getContentResolver(),Secure.ANDROID_ID));
		
		
	}

	public void splash() {

		try {
			PackageInfo info = getPackageManager().getPackageInfo("com.info.socialnetworking.app.meetatbars",PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:",Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// dialog.dismiss();
				nextActivity();
			}
		}, SPLASH_TIME_OUT);
	}

	public void nextActivity() {
		Intent i = new Intent(getApplicationContext(), BarHopperLogin.class);
		startActivity(i);
		finish();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// nextActivity();
		splash();
	}
}

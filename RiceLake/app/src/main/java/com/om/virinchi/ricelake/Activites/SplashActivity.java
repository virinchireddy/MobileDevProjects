package com.om.virinchi.ricelake.Activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.om.virinchi.ricelake.R;
// Screen on opening the app
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splash();
    }
    public void splash() {

        int SPLASH_TIME_OUT = 2500;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent i = new Intent(getApplicationContext(), LoginScreen.class);


                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

}

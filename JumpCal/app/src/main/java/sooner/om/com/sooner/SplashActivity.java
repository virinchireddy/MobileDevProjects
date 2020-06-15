package sooner.om.com.sooner;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

// this activity is related to "welcome screen"
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splash();
    }

    //this method makes the welcome screen stay on the device screen for 2500 milliseconds
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

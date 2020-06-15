package om.healthcare.hetero.Activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import om.healthcare.hetero.Landing_Screen_Activity;
import om.healthcare.hetero.R;

/**
 * Created by Virinchi on 1/24/2017.
 */

public class Login_Screen_Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
    }

    public void btnLogin(View v) {
        Intent i = new Intent(getApplicationContext(), Landing_Screen_Activity.class);
        startActivity(i);
    }
}
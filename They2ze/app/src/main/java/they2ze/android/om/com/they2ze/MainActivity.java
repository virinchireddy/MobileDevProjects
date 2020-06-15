package they2ze.android.om.com.they2ze;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.support.v4.view.ViewPager;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;

import java.util.Map;

import they2ze.android.om.com.they2ze.Network.AppController;
import they2ze.android.om.com.they2ze.Network.Config;
import they2ze.android.om.com.they2ze.Network.ConnectionDetector;
import they2ze.android.om.com.they2ze.adapter.ViewPagerAdapter;
import they2ze.android.om.com.they2ze.fragments.ScreenOne;
import they2ze.android.om.com.they2ze.fragments.ScreenTwo;


public class MainActivity extends AppCompatActivity {
    public ViewPagerAdapter adapter;
    int btnNextCount = 0;
    private PrefManager prefManager;
    ViewPager pager;
    ConnectionDetector con;

    public static String welcome_screen,proceed_screen;
   public static Button btn_next ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        super.setContentView(R.layout.activity_welcome);
        //initialsie the pager
        btn_next = (Button) findViewById(R.id.btn_next) ;
        con = new ConnectionDetector(MainActivity.this);

        if (con.isConnectingToInternet()) {
            getdetails();
        }else{
            con.failureAlert();
            btn_next.setVisibility(View.INVISIBLE);

        }





    }
    public void getdetails() {
        String tag_string_req = "req_Notifications";

        StringRequest postRequest = new StringRequest(Request.Method.GET, Config.URL_STORY_BOARD,
                new Response.Listener<String>()  {

                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj=new JSONObject(response);
                            pager = (ViewPager) findViewById(R.id.view_pager);
                            //Log.v("hello",obj.getString("welcome_screen"));
                            proceed_screen=obj.getString("proceed_screen");
                            welcome_screen=obj.getString("welcome_screen");
                            setupViewPager(pager);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.i("volley", "error: " + error);

                NetworkResponse response = error.networkResponse;

                if (response != null) {
                    switch (response.statusCode) {
                        case 401:

                            break;
                    }
                }
            }

        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        postRequest.setShouldCache(false);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(postRequest,
                tag_string_req);
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ScreenOne());
        adapter.addFragment(new ScreenTwo());

        viewPager.setAdapter(adapter);
    }


    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        //startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        startActivity(new Intent(MainActivity.this, LoginScreen.class));
        finish();
    }

    public void next(View v) {
        btnNextCount=pager.getCurrentItem();;
        if (btnNextCount == 0) {

            pager.setCurrentItem((btnNextCount+1));


        } else if (btnNextCount == 1) {
            Intent i=new Intent(getApplicationContext(),LoginScreen.class);
            startActivity(i);
            launchHomeScreen() ;
            finish();
        }

    }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }
}





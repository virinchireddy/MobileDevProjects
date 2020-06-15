package they2ze.android.om.com.they2ze.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import they2ze.android.om.com.they2ze.MainActivity;
import they2ze.android.om.com.they2ze.Network.AppController;
import they2ze.android.om.com.they2ze.Network.Config;
import they2ze.android.om.com.they2ze.Network.ConnectionDetector;
import they2ze.android.om.com.they2ze.Network.Others;
import they2ze.android.om.com.they2ze.R;


/**
 * Created by Ndroid on 1/10/2017.
 */

public class ScreenOne extends Fragment {
    View rootView;
    ConnectionDetector con;
    Others others;
    TextView tvAppName;
    public static String proceed_screen;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_screen_one, container, false);


        intilize();
        //details();
        return rootView;
    }

    public void intilize() {
        others = new Others(getActivity());
        con = new ConnectionDetector(getActivity());
        tvAppName = (TextView) rootView.findViewById(R.id.tvAppName);
        tvAppName.setText(MainActivity.welcome_screen);
        tvAppName.setMovementMethod(new ScrollingMovementMethod());

    }

    public void details() {
        try {
            JSONObject obj = new JSONObject();

            if (con.isConnectingToInternet()) {
                Log.v("sending  data", obj.toString());
                getdetails(obj);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getdetails(JSONObject obj) {
        String tag_string_req = "req_Notifications";
        others.showProgressWithOutMessage();
        StringRequest postRequest = new StringRequest(Request.Method.GET, Config.URL_STORY_BOARD,
                new Response.Listener<String>()  {

                    @Override
                    public void onResponse(String response) {
                        others.hideDialog();
                        try {
                                  JSONObject obj=new JSONObject(response);
                                    //Log.v("hello",obj.getString("welcome_screen"));
                                proceed_screen=obj.getString("proceed_screen");
                                tvAppName.setText(obj.getString("welcome_screen"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.i("volley", "error: " + error);
                others.hideDialog();
                // Toast.makeText(getActivity(),
                // "Something went wrong please try again later",
                // Toast.LENGTH_LONG).show();
                NetworkResponse response = error.networkResponse;

                if (response != null) {
                    switch (response.statusCode) {
                        case 401:
                           /* Intent i = new Intent(getActivity(), LoginScreen.class);
                            session.logoutUser();
                            startActivity(i);
                            Toast.makeText(getActivity(),
                                    "User Logged in from other device",
                                    Toast.LENGTH_LONG).show();*/
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
}

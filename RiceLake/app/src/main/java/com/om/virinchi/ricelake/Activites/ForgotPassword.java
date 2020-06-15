package com.om.virinchi.ricelake.Activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.om.virinchi.ricelake.Network.AppController;
import com.om.virinchi.ricelake.Network.Config;
import com.om.virinchi.ricelake.Network.ConnectionDetector;
import com.om.virinchi.ricelake.Network.Others;
import com.om.virinchi.ricelake.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ForgotPassword extends Activity {
    public static String email;
    EditText etEmail;
    Others others;
    ConnectionDetector con;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        intilize();
    }

    //intilizing the widgets used in xml file
    public void intilize() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        others = new Others(ForgotPassword.this);
        con = new ConnectionDetector(ForgotPassword.this);
    }

    // on clicking Submit button
    public void btnSubmit(View v) {
        email = etEmail.getText().toString();
        if (email.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter Email",
                    Toast.LENGTH_LONG).show();
        } else if (!isValidEmail(email)) {
            etEmail.setError("Invalid Email");
        }
        if (isValidEmail(email)) {

            try {
                JSONObject obj = new JSONObject();
                obj.put("vcEmail", email);
                if (con.isConnectingToInternet()) {
                    sendrequest(obj);
                } else {
                    con.failureAlert();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // sending data to server
    public void sendrequest(JSONObject obj) {
        Log.v("url_request", obj.toString());
        String tag_string_req = "req_Notifications";

        others.showProgressWithOutMessage();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_FORGOT_PASSWORD, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            boolean error = response.getBoolean("Error");
                            Log.v("response", response.toString());
                            if (!error) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword.this);
                                builder.setMessage(
                                        "Password reset link has been sent to your Email")
                                        .setCancelable(false)
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int id) {
                                                        dialog.cancel();
                                                        Intent i = new Intent(getApplicationContext(), LoginScreen.class);
                                                        startActivity(i);
                                                    }
                                                });
                                // Creating dialog box
                                AlertDialog alert = builder.create();
                                alert.setCanceledOnTouchOutside(true);

                                alert.show();


                            } else {
                                others.ToastMessage(response.getString("error_msg"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("volley", "error: " + error);
                others.hideDialog();
            // handling status codes
                NetworkResponse response = error.networkResponse;

                if (response != null) {
                    switch (response.statusCode) {

                        case 401:
                            Toast.makeText(getApplicationContext(),
                                    "Email not verified",
                                    Toast.LENGTH_LONG).show();
                            break;
                        case 404:
                            Toast.makeText(getApplicationContext(),
                                    "User does not exist",
                                    Toast.LENGTH_LONG).show();
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
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        jsObjRequest.setShouldCache(false);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest,
                tag_string_req);
    }

    public void btnLogin(View v) {
        Intent i = new Intent(getApplicationContext(), LoginScreen.class);
        startActivity(i);
    }

    //regular expression to validate the  email.
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

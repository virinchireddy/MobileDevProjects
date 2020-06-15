package com.om.virinchi.ricelake.Activites;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.om.virinchi.ricelake.Helper.BaseActivity;
import com.om.virinchi.ricelake.Helper.SessionManager;
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


public class LoginScreen extends BaseActivity {
    public String pass, email;
    EditText etEmail, etPassword;

    Others others;
    ConnectionDetector con;
   // ImageView bottomContainer;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginscreen);
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to landing Screen
            HashMap<String, String> user = session.getUserDetails();
            String authenticationToken = user.get(SessionManager.authenticationToken);
            String userId = user.get(SessionManager.userId);
            String password = user.get(SessionManager.password);
            String userName = user.get(SessionManager.userName);
            String userType = user.get(SessionManager.userType);

            Log.v("login screen", authenticationToken + "/" + userId + "/" + userName);

            Intent i = new Intent(getApplicationContext(), LandingScreen.class);
            i.putExtra("authenticationToken", authenticationToken);
            i.putExtra("userId", userId);
            i.putExtra("password", password);
            i.putExtra("userName", userName);
            i.putExtra("userType", userType);
            startActivity(i);

            finish();

        }

        intilize();
        attachKeyboardListeners();
    }

    @Override
    protected void onShowKeyboard(int keyboardHeight) {
        // do things when keyboard is shown
      //  bottomContainer.setVisibility(View.GONE);

       /* LinearLayout liEditText = (LinearLayout) findViewById(R.id.liEditText);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) liEditText.getLayoutParams();
        params.setMargins(0, 30, 0, 0);
        liEditText.setLayoutParams(params);*/
    }

    @Override
    protected void onHideKeyboard() {
        // do things when keyboard is hidden
        //bottomContainer.setVisibility(View.VISIBLE);

       /* LinearLayout liEditText = (LinearLayout) findViewById(R.id.liEditText);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) liEditText.getLayoutParams();
        params.setMargins(0, 30, 0, 0);

        liEditText.setLayoutParams(params);*/
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    // Intilising the widgets used
    public void intilize() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        others = new Others(LoginScreen.this);
        con = new ConnectionDetector(LoginScreen.this);
      //  bottomContainer = (ImageView) findViewById(R.id.imageContainer);


    }


    //on click on login button checking validations
    public void btnLogin(View v) {
        email = etEmail.getText().toString();
        pass = etPassword.getText().toString();
        if (email.length() == 0 && pass.length() == 0) {
            Toast.makeText(getApplicationContext(),
                    "Please enter Email and Password", Toast.LENGTH_LONG)
                    .show();

        } else if (email.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter Email",
                    Toast.LENGTH_LONG).show();
        } else if (!isValidEmail(email)) {
            etEmail.setError("Invalid Email");
        } else if (pass.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter Password",
                    Toast.LENGTH_LONG).show();

        } else if (!isValidPassword(pass)) {
            etPassword.setError("Invalid Password");
        }
        if (isValidEmail(email) && isValidPassword(pass)) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("_UserName", email);
                obj.put("_Password", pass);
                if (con.isConnectingToInternet()) {
                    login(obj);
                } else {
                    con.failureAlert();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // sending data to server
    private void login(JSONObject obj) {
        // TODO Auto-generated method stub
        String tag_string_req = "req_Notifications";
        others.showProgressWithOutMessage();
        Log.v("url", obj.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_LOGIN, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            Log.v("response", response.toString());
                            boolean error = response.getBoolean("Error");

                            if (!error) {

                                String authenticationToken = response.getString("AuthenToken");
                                String userId = response.getString("UserId");
                                String message = response.getString("Message");
                                String userType = response.getString("UserType");
                                String userName = response.getString("UserName");
                                Log.v("Neeeeeee", message);


                                if (message.equals("Successfully logged in")) {

                                    Intent i = new Intent(
                                            getApplicationContext(),
                                            LandingScreen.class);

                                    i.putExtra("authenticationToken",
                                            authenticationToken);
                                    i.putExtra("userId", userId);
                                    i.putExtra("password", pass);
                                    i.putExtra("userName", userName);
                                    i.putExtra("userType", userType);
                                    startActivity(i);
                                    session.createLoginSession(
                                            authenticationToken, userId, pass,
                                            userName, userType);
                                }

                            } else {
                                others.ToastMessage(response
                                        .getString("Message"));
                                Log.v("error", response.toString());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.i("volley", "error: " + error);
                others.hideDialog();
// Handling response codes
                NetworkResponse response = error.networkResponse;

                if (response != null) {
                    switch (response.statusCode) {
                        case 404:
                            Toast.makeText(getApplicationContext(),
                                    "User does not  exist",
                                    Toast.LENGTH_LONG).show();
                            break;
                        case 401:
                            Toast.makeText(getApplicationContext(),
                                    "Please enter correct password",
                                    Toast.LENGTH_LONG).show();
                            break;
                        case 503:
                            Toast.makeText(getApplicationContext(),
                                    "Your account is inactive, please contact administrator",
                                    Toast.LENGTH_LONG).show();
                            break;
                        case 403:
                            Toast.makeText(getApplicationContext(),
                                    "Your account is not verified",
                                    Toast.LENGTH_LONG).show();
                            break;
                        case 502:
                            Toast.makeText(getApplicationContext(),
                                    "Super Admin or Staff does not have access to the  application",
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

    // validating Email
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password
    private boolean isValidPassword(String pass) {
        boolean value;
        value = pass != null && pass.length() > 4;
        return value;
    }

    public void tvForgotPassword(View v) {
        Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
        startActivity(i);
    }

    // on clicking default back button
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }

}
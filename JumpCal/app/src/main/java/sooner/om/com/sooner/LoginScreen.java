package sooner.om.com.sooner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;

import sooner.om.com.sooner.app.AppController;
import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.helper.AcceptTermsConditions;
import sooner.om.com.sooner.helper.Others;
import sooner.om.com.sooner.helper.SessionManager;
import sooner.om.com.sooner.network.ConnectionDetector;
import sooner.om.com.sooner.network.ServerCall;


//this class contains the details of login into the application

public class LoginScreen extends Activity implements ServerCall.AsyncTaskInterface {
    public static boolean cTerms = false;
    public static boolean isDinayed;
    public static boolean isLocationDisabled;
    public static int count = 0;
    public ConnectionDetector con;
    private CheckBox cbTerms;
    private EditText etEmail, etPassword;
    private String pass, email;
    private SessionManager session;
    private Others others;

    //first executable block in this class
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        cTerms=false;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        count = 0;
        session = new SessionManager(getApplicationContext());

        // checking if the application is already logged in or not

        if (session.isLoggedIn()) { // User is already logged in. Take him to landing screen
            HashMap<String, String> user = session.getUserDetails();
            String authenticationToken = user
                    .get(SessionManager.authenticationToken);
            String userId = user.get(SessionManager.userId);
            String password = user.get(SessionManager.password);
            String facilityName = user.get(SessionManager.selectedFacilityName);
            String facilityId = user.get(SessionManager.facilityId);

            // sending the parameters to next screen
            Intent i = new Intent(getApplicationContext(),
                    PatientLandingScreen.class);
            i.putExtra("authenticationToken", authenticationToken);
            i.putExtra("userId", userId);
            i.putExtra("password", password);
            i.putExtra("facilityId", facilityId);
            i.putExtra("selectedFacilityName", facilityName);
            startActivity(i);

            finish();

        }

        initialize();
        cbTerms.setChecked(cTerms);

    }

    //this method initialise all the view elements in this class
    public void initialize() {

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        cbTerms = (CheckBox) findViewById(R.id.cbTerms);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "font.ttf");
        etPassword.setTypeface(typeFace);
        etEmail.setTypeface(typeFace);
        cbTerms.setClickable(false);
        others = new Others(LoginScreen.this);
        // creating the internet connection object
        con = new ConnectionDetector(LoginScreen.this);

    }

    //this method is used to divert the user to another class ForgotPassword.class , incase if he forgot his passord
    public void tvForgotPassword(View v) {
        email = etEmail.getText().toString();
        if (validateEmail(email))
            requestPassword(email);

    }

    //the functionality of login button is written here(conditions and acceptance criteria of login)
    public void btnLogin(View v) {
        LoginScreen.isDinayed = false;
        LoginScreen.isLocationDisabled = false;
        email = etEmail.getText().toString();
        pass = etPassword.getText().toString();
       if (validateEmail(email)) {
            if(isValidPassword(pass))
                sendData();
        }

    }

    // validating Email
    private boolean validateEmail(String email) {
        if (email.length() == 0) {
               etEmail.setError("Enter Email");
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.setError("Invalid Email");
            return false;
        } else {
            return true;
        }
    }

    //this method is called when the username and passord are valid,then the below details are send to server for further information
    private void sendData() {
        // TODO Auto-generated method stub
        // adding required parameters to the json object and sending the request
        try {
            JSONObject obj = new JSONObject();
            obj.put("_UserName", email);
            obj.put("_Password", pass);
            obj.put("_AcceptTC", cTerms);
            obj.put("_UserType", "CLIENT");
            if (con.isConnectingToInternet()) {
                login(obj);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //this method is used to send the above data to the server using JSON
    private void login(JSONObject obj) {

        ServerCall servercal = new ServerCall(LoginScreen.this, LoginScreen.this);
        servercal.postUrlRequest(Config.URL_LOGIN, obj, "login");
    }


    // validating password
    private boolean isValidPassword(String pass) {
        if (pass.length() > 3)
            return true;
        else if(pass.length()<=0)
            etPassword.setError("Enter password");
        else
            etPassword.setError("Invalid Password");
        return false;
    }

    // login response after successful login
    @Override
    public void onAsyncTaskInterfaceResponse(JSONObject result, String tag) {
        try {

            // user successfully logged in navigate him to landing screen
            Intent i = new Intent(getApplicationContext(), PatientLandingScreen.class);
            i.putExtra("authenticationToken", result.getString("AuthenToken"));
            i.putExtra("userId", result.getString("UserId"));
            i.putExtra("password", pass);
            i.putExtra("facilityId", result.getString("FacilityId"));
            i.putExtra("selectedFacilityName", result.getString("FacilityName"));
            startActivity(i);
            // creating the session for after successful login
            session.createLoginSession(result.getString("AuthenToken"), result.getString("UserId"), pass,
                    result.getString("FacilityId"), result.getString("FacilityName"));
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestPassword(final String email) {
        // TODO Auto-generated method stub

        others.showProgressWithOutMessage();
        StringRequest putRequest = new StringRequest(Request.Method.PUT,
                Config.URL_FORGET_PASSWORD + email + "&userType=client", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                Log.v("Response", response);
                others.hideDialog();
                Toast.makeText(getApplicationContext(), "Password reset link has been sent to your Email", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.v("Error.Response", error.toString());
                others.hideDialog();
                Toast.makeText(getApplicationContext(), "Email does not exist", Toast.LENGTH_LONG).show();
            }
        });
        AppController.getInstance().addToRequestQueue(putRequest);

    }


    //the below method is used to find the status of checkbox in the screen
    public void termsConditions(View v) {
        if (cbTerms.isChecked()) {
            cTerms = false;
            cbTerms.setChecked(false);
        } else {
            Intent i = new Intent(getApplicationContext(), AcceptTermsConditions.class);
            startActivity(i);
        }
    }
}

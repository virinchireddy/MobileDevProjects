package sooner.om.com.sooner;


import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.helper.CustomizeDialog;
import sooner.om.com.sooner.helper.SessionManager;
import sooner.om.com.sooner.network.ConnectionDetector;
import sooner.om.com.sooner.network.ServerCall;


//this class is used for changing the password of the user.
public class ChangePassword extends Activity implements ServerCall.AsyncTaskInterface, CustomizeDialog.AsyncTaskInterface {
    private EditText etPassword, etNwPassword, etCnPassword;
    private ConnectionDetector con;
    private SessionManager session;

    //first method that executes on this class
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initialize();
        increaseDialogSize();

    }

    // this will increase the size of the dialog
    private void increaseDialogSize() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
    }

    //initializes the view elements and other attributes of this class
    public void initialize() {
        etPassword = (EditText) findViewById(R.id.etPassword);
        etNwPassword = (EditText) findViewById(R.id.etNwPassword);
        etCnPassword = (EditText) findViewById(R.id.etCnPassword);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "font.ttf");
        etPassword.setTypeface(typeFace);
        etNwPassword.setTypeface(typeFace);
        etCnPassword.setTypeface(typeFace);

        session = new SessionManager(ChangePassword.this);
        con = new ConnectionDetector(ChangePassword.this);
    }

    //this performs the operation of cancel button on the screen
    public void btnCancel(View v) {
        //it finishes this activity.
        finish();
    }

    //this performs the operation of submit button in the layout.
    //this method validates the new password .if everything is ok, the new password is sent to the server along with user details
    public void btnSubmit(View v) {
        String password = etPassword.getText().toString();
        String Nwpassword = etNwPassword.getText().toString();
        String Cnpassword = etCnPassword.getText().toString();
        // checking the password authentication


        if (password.length() > 0 && Nwpassword.length() > 0 && Cnpassword.length() > 0) {
            if (PatientLandingScreen.password.equals(password)) {
                if (Nwpassword.equals(Cnpassword)) {
                    try {
                        // adding parameters to json object
                        JSONObject obj = new JSONObject();
                        obj.put("_CurrentPassword", password);
                        obj.put("_NewPassword", Cnpassword);
                        obj.put("_AuthenticationToken", PatientLandingScreen.authenticationToken);
                        obj.put("_UserId", PatientLandingScreen.userId);
                        Log.v("sending data", obj.toString() + PatientLandingScreen.authenticationToken + "/" + PatientLandingScreen.userId);
                        // checking for internet connection
                        if (con.isConnectingToInternet()) {
                            changepassword(obj);
                        } else {
                            con.failureAlert();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    etCnPassword.setError("Password does not match");
                }
            } else {
                etPassword.setError("Current password is incorrect");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please enter all the fields",
                    Toast.LENGTH_LONG).show();
        }

    }

    //this method takes the json object which has details of user and ne password as an object to the server using JSON.
    private void changepassword(JSONObject obj) {
        // TODO Auto-generated method stub

        ServerCall serverCall = new ServerCall(ChangePassword.this, ChangePassword.this);
        serverCall.putUrlRequest(Config.URL_CHANGE_PASSWORD, obj, "changePassword");

    }


    // this method contains condition of "length of new password" .
    private boolean isValidPassword(String pass) {
        return pass != null && pass.length() > 4;
    }

    // success message for changed password
    @Override
    public void onAsyncTaskInterfaceResponse(JSONObject result, String tag) {
        CustomizeDialog customizeDialog = new CustomizeDialog(ChangePassword.this, "requestAppointment", ChangePassword.this);
        customizeDialog.setMessage("Password has been changed, Click OK to login.");
        customizeDialog.show();
    }

    @Override
    public void onAsyncTaskInterfaceResponse(String result) {

        session.logoutUser();

    }
}
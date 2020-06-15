package sooner.om.com.sooner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sooner.om.com.sooner.app.AppController;
import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.helper.Others;


//this class contains details about forgot password.
public class ForgotPassword extends Activity {
    EditText etEmail;
    Others others;


    //first executed method in this class
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        intilize();
        increaseDialogSize();
    }

    // this method will make the dialog box width to the entire screen
    private void increaseDialogSize() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;

    }

    //initialises the other "class objects" and "view elements" of this class and other attributes called in this class.
    public void intilize() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        others = new Others(ForgotPassword.this);
    }

    // cancel button operation
    public void btnCancel(View v) {
        finish();
    }

    //request button operation
    public void btnRequest(View v) {
        String email = etEmail.getText().toString();
        if (email.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter Email",
                    Toast.LENGTH_LONG).show();
        } else if (!isValidEmail(email)) {
            etEmail.setError("Invalid Email");
        } else if (isValidEmail(email)) {
            try {
                requestpassword(email);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //this method sends the email to the server and provides the appropriate response from the server.
    private void requestpassword(final String email) {
        // TODO Auto-generated method stub

        others.showProgressWithOutMessage();
        StringRequest putRequest = new StringRequest(Request.Method.PUT,
                Config.URL_FORGET_PASSWORD + email, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                Log.v("Response", response);
                others.hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Password reset link has been sent to your Email", Toast.LENGTH_LONG)
                        .show();

                Intent i = new Intent(getApplicationContext(), LoginScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.v("Error.Response", error.toString());
                others.hideDialog();
                Toast.makeText(getApplicationContext(),
                        "Email does not exist", Toast.LENGTH_LONG)
                        .show();
            }
        }) {

//			@Override
//			protected Map<String, String> getParams() {
//				Map<String, String> params = new HashMap<String, String>();
//				params.put("Email", email);
//				return params;
//			}
        };
        AppController.getInstance().addToRequestQueue(putRequest);

    }

    //this method is used for checking the entered "email" with the "regular expression"
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}

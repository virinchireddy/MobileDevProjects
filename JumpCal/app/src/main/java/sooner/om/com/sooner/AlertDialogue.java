package sooner.om.com.sooner;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import sooner.om.com.sooner.helper.SessionManager;
import sooner.om.com.sooner.pushnotification.GCMNotificationIntentService;

//
public class AlertDialogue extends Activity implements OnClickListener {
    TextView tvAlertMessage;
    Button btnOk, btnCancel;
    String navigation, message;
    private SessionManager session;

    //first executable block
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setting the UI
        setContentView(R.layout.activity_alert_dialogue);
        initialize();
    }

    //initialize the view elements and other parameters like push notification elements ,session manager
    private void initialize() {
        // TODO Auto-generated method stub
        tvAlertMessage = (TextView) findViewById(R.id.tvAlertMessage);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        session = new SessionManager(getApplicationContext());
        message = GCMNotificationIntentService.alert;
        tvAlertMessage.setText(message);
        navigation = GCMNotificationIntentService.cancelNavigation;

    }


    //contains the details of all clickable view elements in this class
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.btnOk) {

            // if the user in any other screen or out of the applicaion
            if (GCMNotificationIntentService.alertType.equals("notification")) {
                session.updateLoginSession(GCMNotificationIntentService.facilityid);
                PatientLandingScreen.facilityId = GCMNotificationIntentService.facilityid;
                Intent i = new Intent(getApplicationContext(),
                        AlertsScreen.class);
                i.putExtra("nav_type", "gcm");
                i.putExtra("facilityId", GCMNotificationIntentService.facilityid);
                i.putExtra("appointmentId", GCMNotificationIntentService.appointmentid);
                startActivity(i);
                finish();
                PatientLandingScreen.activity.finish();

            }// if the user in dashboard then this will run
            else if (GCMNotificationIntentService.alertType
                    .equals("refreshDashboard")) {
                Intent i = new Intent(getApplicationContext(),
                        LoginScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
                finish();
                PatientLandingScreen.activity.finish();

            }
        } else if (v.getId() == R.id.btnCancel) {
            finish();
        }
    }
}

package sooner.om.com.sooner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.IOException;

import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.fragment.FragmentDashboard;
import sooner.om.com.sooner.fragment.FragmentFacility;
import sooner.om.com.sooner.fragment.FragmentProfile;
import sooner.om.com.sooner.fragment.FragmentRequestAppointment;
import sooner.om.com.sooner.helper.Others;
import sooner.om.com.sooner.helper.SessionManager;
import sooner.om.com.sooner.network.ConnectionDetector;
import sooner.om.com.sooner.network.ServerCall;
import sooner.om.com.sooner.services.CurrentLocation;
import sooner.om.com.sooner.services.FusedLocationReceiver;
import sooner.om.com.sooner.stickylistheaders.newdesign.WhiteSlotes;


// this class is called when the details entered in the login screen are valid and get a success response from server .
public class PatientLandingScreen extends Activity implements ServerCall.AsyncTaskInterface {
    public static final String REG_ID = "regId";
    private static final String APP_VERSION = "appVersion";
    private static final String TAG = "Register Activity";
    public static TextView tvTicker;
    public static String authenticationToken, userId, password, facilityId,
            selectedFacilityName;
    public static Activity activity;
    private static CurrentLocation location;
    Context context;
    String regId;
    private ImageView ivdashboard, ivprofile, ivRequestappointment,
            ivfacility;
    private TextView tvDashboard, tvRequestappointment, tvProfile, tvfacility;
    private LinearLayout liDash, liReq, liProfile, liFacility;
    private GoogleCloudMessaging gcm;
    private ConnectionDetector con;

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_screen);

        savedInstanceState = getIntent().getExtras();
        // get the details from login screen
        authenticationToken = savedInstanceState
                .getString("authenticationToken");
        userId = savedInstanceState.getString("userId");
        password = savedInstanceState.getString("password");
        facilityId = savedInstanceState.getString("facilityId");
        selectedFacilityName = savedInstanceState
                .getString("selectedFacilityName");

        activity = this;
        initialize();
        registerGcm();
        // open the dashboard while loading
        dashbordNavigation();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // check weather the application is able to access the location is not ask the use for permission
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
            // checking location permission
            if (Others.checkPermission(PatientLandingScreen.this,
                    "android.permission.ACCESS_FINE_LOCATION")) {
                startLocationSharing();
            } else {
                if (!LoginScreen.isDinayed) {
                    Log.v("Fragment Profile", "not denayed");

                    Others.permissionCheckMarshMallow(
                            PatientLandingScreen.this,
                            "android.permission.ACCESS_FINE_LOCATION");
                    LoginScreen.isDinayed = true;

                } else
                    Log.v("Fragment Profile", "denayed");
            }

        } else {
            // start the location sharing with the server
            startLocationSharing();
        }
    }

    // this method is used for sharing  the location  of the user
    private void startLocationSharing() {
        // TODO Auto-generated method stub
        location = new CurrentLocation(PatientLandingScreen.this,
                new FusedLocationReceiver() {
                    public void onLocationChanged() {

                    }
                });
        location.updateLocation(userId, authenticationToken);
    }

    // this method initialize all the view elements and other attributes wghich are used in this class
    public void initialize() {
        liDash = (LinearLayout) findViewById(R.id.liDash);
        liReq = (LinearLayout) findViewById(R.id.liReq);
        liProfile = (LinearLayout) findViewById(R.id.liProfile);
        liFacility = (LinearLayout) findViewById(R.id.liFacility);
        ivdashboard = (ImageView) findViewById(R.id.ivdashboard);
        ivRequestappointment = (ImageView) findViewById(R.id.ivRequestappointment);
        ivprofile = (ImageView) findViewById(R.id.ivprofile);
        ivfacility = (ImageView) findViewById(R.id.ivfacility);
        tvDashboard = (TextView) findViewById(R.id.tvDashboard);
        tvRequestappointment = (TextView) findViewById(R.id.tvRequestappointment);
        tvProfile = (TextView) findViewById(R.id.tvProfile);
        tvfacility = (TextView) findViewById(R.id.tvfacility);
        tvTicker = (TextView) findViewById(R.id.tvTicker);
        con = new ConnectionDetector(PatientLandingScreen.this);
    }

    //this method is called when the user clicks the "dashboard" button on the screen
    public void liDash(View v) {

        dashbordNavigation();
    }

    // opening the dashboard
    public void dashbordNavigation() {
        Others.navigationDashbord = "dashbord";
        clearAllViews();


        Others.displayImageDrawable(ivdashboard, R.drawable.d_c, getApplicationContext());

        Others.displayTextColor(tvDashboard, R.color.white, getApplicationContext());

        Others.displayBackGroundColor(liDash, R.color.new_selected, getApplicationContext());
        FragmentDashboard dashboard = new FragmentDashboard();
        this.getFragmentManager().beginTransaction()
                .replace(R.id.flContainer, dashboard).addToBackStack(null)
                .commit();
    }

    //this method is called when the user clicks the "request appointment" button on the screen
    public void liReq(View v) {
        Others.navigationDashbord = "otherScreen";
        clearAllViews();

        Others.displayImageDrawable(ivRequestappointment, R.drawable.ra_c, getApplicationContext());

        Others.displayTextColor(tvRequestappointment, R.color.white, getApplicationContext());

        Others.displayBackGroundColor(liReq, R.color.new_selected, getApplicationContext());
        FragmentRequestAppointment profile = new FragmentRequestAppointment();
        this.getFragmentManager().beginTransaction()
                .replace(R.id.flContainer, profile).addToBackStack(null)
                .commit();

    }

    //this method is called when the user clicks the "profile" button on the screen
    public void liProfile(View v) {
        Others.navigationDashbord = "otherScreen";

        clearAllViews();

        // identify it is navigated from profile so that insurance card images will display from 0 to last
        Others.isInsuranceUploaded = false;
        Others.displayImageDrawable(ivprofile, R.drawable.p_c, getApplicationContext());

        Others.displayTextColor(tvProfile, R.color.white, getApplicationContext());

        Others.displayBackGroundColor(liProfile, R.color.new_selected, getApplicationContext());

        FragmentProfile request = new FragmentProfile();
        this.getFragmentManager().beginTransaction()
                .replace(R.id.flContainer, request).addToBackStack(null)
                .commit();

    }

    //this method is called when the user clicks the "facility" button on the screen
    public void liFacility(View v) {
        Others.navigationDashbord = "otherScreen";

        clearAllViews();

        Others.displayImageDrawable(ivfacility, R.drawable.settings_c, getApplicationContext());

        Others.displayTextColor(tvfacility, R.color.white, getApplicationContext());
        Others.displayBackGroundColor(liFacility, R.color.new_selected, getApplicationContext());

        FragmentFacility request = new FragmentFacility();
        this.getFragmentManager().beginTransaction()
                .replace(R.id.flContainer, request).addToBackStack(null)
                .commit();

    }

    // clearing all the views
    public void clearAllViews() {

        Others.displayImageDrawable(ivdashboard, R.drawable.d_n, getApplicationContext());

        Others.displayTextColor(tvDashboard, R.color.grey, getApplicationContext());


        Others.displayImageDrawable(ivprofile, R.drawable.p_n, getApplicationContext());

        Others.displayTextColor(tvProfile, R.color.grey, getApplicationContext());

        Others.displayImageDrawable(ivRequestappointment, R.drawable.ra_n, getApplicationContext());

        Others.displayTextColor(tvRequestappointment, R.color.grey, getApplicationContext());

        Others.displayImageDrawable(ivfacility, R.drawable.settings_n, getApplicationContext());

        Others.displayTextColor(tvfacility, R.color.grey, getApplicationContext());

        Others.displayBackGroundColor(liFacility, R.color.new_not_selected, getApplicationContext());
        Others.displayBackGroundColor(liDash, R.color.new_not_selected, getApplicationContext());
        Others.displayBackGroundColor(liProfile, R.color.new_not_selected, getApplicationContext());
        Others.displayBackGroundColor(liReq, R.color.new_not_selected, getApplicationContext());
    }

    //this method is called when the user clicks the alerts button on the screen
    public void rlAlert(View v) {
        Intent i = new Intent(getApplicationContext(), WhiteSlotes.class);
        i.putExtra("nav_type", "landingScreen");
        startActivity(i);
        //finish();
    }

    // registering our mobile with the GCM(Google Cloud Messenger) service.
    //this helps for sending "push notifications" to the device by capturing the "device token"
    private void registerGcm() {
        // TODO Auto-generated method stub
        // Log.v("hello", obj.toString());
        if (TextUtils.isEmpty(regId)) {
            if (con.isConnectingToInternet())
                regId = registerGCM();
        }
    }

    public String registerGCM() {

        gcm = GoogleCloudMessaging.getInstance(this);
        regId = getRegistrationId(context);

        if (TextUtils.isEmpty(regId)) {
            registerInBackground();
        } else {
            Toast.makeText(getApplicationContext(),
                    "RegId already available. RegId: " + regId,
                    Toast.LENGTH_LONG).show();
        }
        return regId;
    }

    // get the registration id from google server
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getSharedPreferences(
                PatientLandingScreen.class.getSimpleName(),
                Context.MODE_PRIVATE);
        String registrationId = prefs.getString(REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @SuppressWarnings("deprecation")
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(Config.GOOGLE_PROJECT_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;

                    // storeRegistrationId(context, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            //this method is used for sending the "registration id"of the android application,"device token", "user id" to the server
            protected void onPostExecute(String msg) {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("_AuthenticationToken",
                            PatientLandingScreen.authenticationToken);
                    obj.put("_UserId", PatientLandingScreen.userId);
                    obj.put("_DevicePlatform", "Android");
                    obj.put("_DeviceToken", regId);
                    registerDevice(obj);
                    Log.v("gcm key", regId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.execute(null, null, null);

    }

    protected void registerDevice(JSONObject obj) {
        // TODO Auto-generated method stub
        ServerCall servercal = new ServerCall(PatientLandingScreen.this, PatientLandingScreen.this);
        servercal.postUrlRequest(Config.URL_SET_GCM, obj, "registerGCM");
    }

    //predefined android method, which has the default operation of back button on the mobile
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //super.onBackPressed();
        if (Others.navigationDashbord.equals("dashbord")) {
            finish();
        } else {
            dashbordNavigation();
        }
    }

    @Override
    public void onAsyncTaskInterfaceResponse(JSONObject result, String tag) {

    }
}

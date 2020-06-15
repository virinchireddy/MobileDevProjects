package sooner.om.com.sooner;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.helper.CustomizeDialog;
import sooner.om.com.sooner.helper.Others;
import sooner.om.com.sooner.helper.SessionManager;
import sooner.om.com.sooner.helper.Utility;
import sooner.om.com.sooner.network.ConnectionDetector;
import sooner.om.com.sooner.network.ServerCall;
import sooner.om.com.sooner.stickylistheaders.newdesign.WhiteSlotes;
import sooner.om.com.sooner.stickylistheaders.newdesign.WhiteSlotesAdapter;

public class AlertsScreen extends Activity implements OnClickListener, CustomizeDialog.AsyncTaskInterface, ServerCall.AsyncTaskInterface {
    private TextView tvAppointmentisat, tvTimer, tvPhysician, tvFacility, tvLocation,
            tvDate, tvNoalerts, tvSpeciality;
    private LinearLayout liAlerts;
    private LinearLayout liOffers;
    private RelativeLayout rlGift;
    private SessionManager session;
    private ConnectionDetector con;
    private String facilityid, appointmentID = null, _NotificationAppointmentId = null;
    private String navigationType;
    private int potentialConfiltsCount;
    private LinearLayout liPotentialConfilts;
    private TextView tvEvent1;
    private TextView tvEvent2;
    private TextView tvEvent3;
    private TextView tvEvent1Date;
    private TextView tvEvent2Date;
    private TextView tvEvent3Date;
    private TextView tvGiftPrice;
    private TextView tvTime;

    private String beforeTime = null,
            afterTime = null,
            endDateBeforeTime = null,
            endDateAfterTime = null,
            beforeEvent = "No events within 2 hours prior to your scheduled appointment.",
            afterEvent = "No events within 2 hours after your scheduled appointment.";

    private String inputDateGlobal;

    private String jsonData;
    private String fullDate;
    private ImageView ivArrow;// view potential conflits arrow

    private boolean potentialConflictsAlert = false;
    private boolean acceptButtonPermissionCheck = false;
    private boolean isInitial = true;

    private String authenticationToken, userId, facilityId;
    private ServerCall serverCall;

    // first executed method on this class
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts_screen);

        savedInstanceState = getIntent().getExtras();
        navigationType = savedInstanceState.getString("nav_type");
        Log.v("navigationType", navigationType);
        // if the user comes from push notification
        if (navigationType.equals("gcm")) {
            facilityid = savedInstanceState.getString("facilityId");
            _NotificationAppointmentId = savedInstanceState
                    .getString("appointmentId");
        }// if the user comes from rescheduling options
        else {
            jsonData = savedInstanceState.getString("jsonData");
        }

        intilize();
        loadData();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // for showing after the permission alert
        if (!isInitial)
            potentialConflictsLogic();
    }

    private void loadData() {
        // TODO Auto-generated method stub
        // if the user comes from push notification

        if (navigationType.equals("gcm")) {
            try {
                // check if the user logged in or not
                if (session.isLoggedIn()) {

                    // User is already logged in. Take him to
                    HashMap<String, String> user = session.getUserDetails();
                    String authenticationToken = user
                            .get(SessionManager.authenticationToken);
                    String userId = user.get(SessionManager.userId);
                    String facilityName = user
                            .get(SessionManager.selectedFacilityName);
                    String password = user.get(SessionManager.password);
                    Log.v("hello 123", PatientLandingScreen.facilityId + "/"
                            + _NotificationAppointmentId);
                    // assigning data to entire applicaion
                    PatientLandingScreen.authenticationToken = authenticationToken;
                    PatientLandingScreen.userId = userId;
                    PatientLandingScreen.facilityId = facilityid;
                    PatientLandingScreen.selectedFacilityName = facilityName;
                    PatientLandingScreen.password = password;

                    sendData();

                }// if the user not logged in take him to login screen
                else {
                    Intent i = new Intent(getApplicationContext(),
                            LoginScreen.class);
                    startActivity(i);
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            sendData();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public void sendData() {
        JSONObject obj = new JSONObject();
        // send data request to server if it is from push notification
        if (navigationType.equals("gcm")) {
            try {
                obj.put("_AuthenticationToken",
                        PatientLandingScreen.authenticationToken);
                obj.put("_UserId", PatientLandingScreen.userId);
                obj.put("_FacilityId", PatientLandingScreen.facilityId);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String _currentTime = sdf.format(new Date());
                SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");
                String _currentDate = sdfDate.format(new Date());

                obj.put("_currentTime", _currentTime);
                obj.put("_currentDate", _currentDate);

                if (navigationType.equals("gcm")) {
                    obj.put("_NotificationAppointmentId",
                            _NotificationAppointmentId);
                }

                jsonObjectRequest(obj);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }// send data request to server if it is from more alert screen
        else {
            try {
                JSONObject obj1 = new JSONObject(jsonData);
                Log.v("Hello", obj1.toString());

                loadAlertData(obj1);
                // jsonObjectRequest(obj1);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    // url request for server
    private void jsonObjectRequest(JSONObject obj) {
        serverCall = new ServerCall(AlertsScreen.this, AlertsScreen.this);
        serverCall.postUrlRequest(Config.URL_SINGLE_WHITE_SLOTE_DETAILS, obj, "loadAlertScreen");

    }

    // load the data to secreen
    @SuppressLint("SimpleDateFormat")
    protected void loadAlertData(JSONObject resp) {
        // TODO Auto-generated method stub
        try {

            int whiteSlotes = Integer.parseInt(resp.getString("TotalSlots"));

            if (whiteSlotes > 0) {
                liAlerts.setVisibility(View.VISIBLE);
                tvNoalerts.setVisibility(View.GONE);
                JSONObject response = resp.getJSONObject("ValidSlot");
                tvPhysician.setText(response.getString("ProviderName"));
                tvLocation.setText(response.getString("LocationName"));
                tvFacility.setText(resp.getString("FacilityName"));
                tvSpeciality.setText(Others
                        .optString(response, "SpecialtyName"));
                // hide the gifts view if there are no gifts avilable
                if (Others.optString(response, "GiftValue").equals("0.00") || Others.optString(response, "GiftValue").equals("NA") || Others.optString(response, "GiftValue").equals("")) {
                    rlGift.setVisibility(View.GONE);
                    liOffers.setVisibility(View.GONE);
                } else {
                    rlGift.setVisibility(View.VISIBLE);
                    liOffers.setVisibility(View.VISIBLE);
                    tvGiftPrice.setText("1 - Gift card of $" + Others
                            .optString(response, "GiftValue") + " on arrival.");
                }
                appointmentID = response.getString("AppointmentId");
                readUrlRequest(appointmentID);

                String date = response.getString("Date").substring(0,
                        response.getString("Date").indexOf("T"));
                Log.v("converted code",
                        date + " " + response.getString("StartTime"));

                inputDateGlobal = date + " " + response.getString("StartTime");
                String timeZone = response.getString("VcTimeZone");
                tvTime.setText(Others.timeTo12Hours(response.getString("StartTime")) + " " + timeZone);
                tvDate.setText(Others.changeDateFormat(date).replace("/", "-"));
                fullDate = date + " " + timeTo12Hours(response.getString("StartTime"));
                tvEvent2Date.setText(timeTo12HoursWithDate(date + " "
                        + response.getString("StartTime")));

                // display the timer if it is less then rescheduling settings
                int timeGapSeconds = Integer.parseInt(response.getString("TimeGap").substring(0, response.getString("TimeGap").indexOf('.')));

                boolean showTime;
                showTime = timeGapSeconds < (Integer.parseInt(resp.getString("ShowTimerBefore")) * 60 * 60);


                if (showTime) {
                    // convert the seconds to milli seconds
                    long duration = timeGapSeconds * 1000;
                    CounterClass timer = new CounterClass(duration, 1000, tvTimer);
                    timer.start();
                }
                // hide timer
                else {
                    tvAppointmentisat.setVisibility(View.GONE);
                    tvTimer.setVisibility(View.GONE);
                    // btnMoreoptions.setVisibility(View.GONE);
                }
            } else {
                liAlerts.setVisibility(View.GONE);
                tvNoalerts.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // display the list of events which is matching in device calender so with the particular appointment date
    public void viewPotentialConflicts(String inputDate) {

        Utility.readCalendarEvent(getApplicationContext(), inputDate);
        // returns the list of events exist in the same day
        for (int i = 0; i < Utility.liEvents.size(); i++) {
            String name = Utility.liEvents.get(i).getEventName();
            String date = Utility.liEvents.get(i).getEventDate();
            String endDateDisply = Utility.liEvents.get(i).getEventEndDate();

            long startDate = Utility.returnDate(inputDate).getTime();
            long endDate = Utility.returnDate(date).getTime();
            if (startDate < endDate) {

                if (afterTime == null) {
                    afterTime = date;
                    afterEvent = name;
                    endDateAfterTime = endDateDisply;

                } else {
                    startDate = Utility.returnDate(afterTime).getTime();
                    endDate = Utility.returnDate(date).getTime();
                    if (startDate > endDate) {
                        afterTime = date;
                        afterEvent = name;
                        endDateAfterTime = endDateDisply;
                    }
                }


            } else if (startDate > endDate) {

                if (beforeTime == null) {
                    beforeTime = date;
                    beforeEvent = name;
                    endDateBeforeTime = endDateDisply;

                } else {
                    startDate = Utility.returnDate(beforeTime).getTime();
                    endDate = Utility.returnDate(date).getTime();
                    if (startDate < endDate) {
                        beforeTime = date;
                        beforeEvent = name;
                        endDateBeforeTime = endDateDisply;
                    }
                }
            } else {
                tvEvent2.setText(getString(R.string.conflict) + name);
                tvEvent2.setTextColor(Color.RED);
                tvEvent2Date.setText(fullDate
                        + " to "
                        + WhiteSlotesAdapter.changeTimeFormat(endDateDisply
                        .substring(endDateDisply.indexOf(" "))));
            }
        }

        // -----> for displaying the time in before the white slot
        tvEvent1.setText(beforeEvent);
        // if event exist date will print
        if (beforeTime != null) {
            if (endDateBeforeTime != null)
                tvEvent1Date.setText(timeTo12HoursWithDate(beforeTime)
                        + " to "
                        + WhiteSlotesAdapter.changeTimeFormat(endDateBeforeTime
                        .substring(endDateBeforeTime.indexOf(" "))));
            else
                tvEvent1Date.setText(timeTo12HoursWithDate(beforeTime));
        }
        // if event doesn't exist empty will print
        else
            tvEvent1Date.setText(beforeTime);

        // -----> for displaying the time in before the white slote
        tvEvent3.setText(afterEvent);
        if (afterTime != null) {

            if (endDateAfterTime != null)
                tvEvent3Date.setText(timeTo12HoursWithDate(afterTime)
                        + " to "
                        + WhiteSlotesAdapter.changeTimeFormat(endDateAfterTime
                        .substring(endDateAfterTime.indexOf(" "))));
            else
                tvEvent3Date.setText(timeTo12HoursWithDate(afterTime));
        } else
            tvEvent3Date.setText(afterTime);
        // tvEvent3Date.setText(afterTime);
        // -----> check the conflicts data if any
        if (tvEvent2
                .getText()
                .toString()
                .equals("Appointment with Dr. "
                        + tvPhysician.getText().toString()))
            if (endDateBeforeTime != null && beforeTime != null) {
                boolean isInBetween = Others.checkTimeLiesBetween(beforeTime,
                        endDateBeforeTime, tvEvent2Date.getText().toString());
                // identify if the time is in between the event time
                if (isInBetween) {
                    tvEvent2.setText(getString(R.string.conflict) + beforeEvent);
                    tvEvent2.setTextColor(Color.RED);
                    tvEvent2Date.setText(fullDate);

                } else {
                    tvEvent2.setText("Appointment with Dr. "
                            + tvPhysician.getText().toString());
                    Others.displayTextColor(tvEvent2, R.color.signup_color, getApplicationContext());
                    tvEvent2Date.setText(fullDate);
                }
            }
    }


    //this method converts given time to hh:mm:am/pm format.
    @SuppressLint("SimpleDateFormat")
    private String timeTo12Hours(String time) {

        try {
            // //log.v("time",time);
            String format = "hh:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date cDate = sdf.parse(time);
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
            time = formatter.format(cDate);
            // //log.v("converted time",time);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    // changing the time format
    @SuppressLint("SimpleDateFormat")
    private String timeTo12HoursWithDate(String time) {

        String newDate = null;
        try {
            String format = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date cDate = sdf.parse(time);
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "MMM dd, yyyy' at 'hh:mm a");

            newDate = formatter.format(cDate);
            // Log.v("date in converted",time+"/"+newDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    // after accepting the appointment navigate to login screen
    @Override
    public void onAsyncTaskInterfaceResponse(String result) {
        if (!acceptButtonPermissionCheck) {
            createEventInCalender();
        }
        Intent i = new Intent(getApplicationContext(), LoginScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();
    }

    @Override
    public void onAsyncTaskInterfaceResponse(JSONObject result, String tag) {
        // if the user comes for first time load the date to the screen
        if (tag.equals("loadAlertScreen"))
            loadAlertData(result);
            // if the user presses the accept button display the dialog box
        else if (tag.equals("accept")) {
            CustomizeDialog customizeDialog = new CustomizeDialog(AlertsScreen.this, "requestAppointment", AlertsScreen.this);
            customizeDialog.setMessage("Appointment has been rescheduled successfully.");
            customizeDialog.show();
        }


    }

    public void home(View v) {
        Intent i = new Intent(getApplicationContext(), LoginScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    // initiolizing the view elements
    public void intilize() {
        tvNoalerts = (TextView) findViewById(R.id.tvNoalerts);
        tvAppointmentisat = (TextView) findViewById(R.id.tvAppointmentisat);
        tvTimer = (TextView) findViewById(R.id.tvTimer);
        tvSpeciality = (TextView) findViewById(R.id.tvSpeciality);
        liOffers = (LinearLayout) findViewById(R.id.liOffers);
        rlGift = (RelativeLayout) findViewById(R.id.rlGift);
        rlGift.setOnClickListener(this);
        tvPhysician = (TextView) findViewById(R.id.tvPhysician);
        tvFacility = (TextView) findViewById(R.id.tvFacility);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvDate = (TextView) findViewById(R.id.tvDate);
        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
        tvTime = (TextView) findViewById(R.id.tvTime);
        ivBack.setOnClickListener(this);
        con = new ConnectionDetector(this);
        session = new SessionManager(getApplicationContext());
        liAlerts = (LinearLayout) findViewById(R.id.liAlerts);
        RelativeLayout rlPotentialConfilts = (RelativeLayout) findViewById(R.id.rlPotentialConfilts);
        liPotentialConfilts = (LinearLayout) findViewById(R.id.liPotentialConfilts);
        rlPotentialConfilts.setOnClickListener(this);
        ivArrow = (ImageView) findViewById(R.id.ivArrow);
        tvEvent1 = (TextView) findViewById(R.id.tvEvent1);
        tvEvent2 = (TextView) findViewById(R.id.tvEvent2);
        tvEvent3 = (TextView) findViewById(R.id.tvEvent3);
        tvEvent1Date = (TextView) findViewById(R.id.tvEventDate1);
        tvEvent2Date = (TextView) findViewById(R.id.tvEventDate2);
        tvEvent3Date = (TextView) findViewById(R.id.tvEventDate3);
        tvGiftPrice = (TextView) findViewById(R.id.tvGiftPrice);


    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.ivBack) {
            /*Intent i = new Intent(getApplicationContext(), WhiteSlotes.class);
            startActivity(i);
            finish();*/
            onBackPressed();

        } else if (v.getId() == R.id.rlPotentialConfilts) {
            potentialConflictsAlert = false;
            potentialConflictsLogic();
        }
    }



    private void potentialConflictsLogic() {
        // TODO Auto-generated method stub

        potentialConfiltsCount++;
        potentialConfiltsCount = potentialConfiltsCount % 2;
        tvEvent2.setText("Appointment with Dr. "
                + tvPhysician.getText().toString());

        Others.displayTextColor(tvEvent2, R.color.signup_color, getApplicationContext());

        // Log.v("Alert Screen",potentialConflictsAlert+""+potentialConfiltsCount);
        if (potentialConfiltsCount == 1) {

            clearPotentialConflicts();

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                // only for gingerbread and newer versions

                if (Others.checkPermission(AlertsScreen.this,
                        "android.permission.READ_CALENDAR")) {
                    viewPotentialConflicts(inputDateGlobal);

                    liPotentialConfilts.setVisibility(View.VISIBLE);

                    Others.displayBackGround(ivArrow, R.drawable.up_arrow, getApplicationContext());
                } else {
                    potentialConfiltsCount = 0;
                    if (!potentialConflictsAlert) {
                        isInitial = false;
                        Others.permissionCheckMarshMallow(AlertsScreen.this,
                                "android.permission.READ_CALENDAR");
                        potentialConflictsAlert = true;
                        // potentialConflictsLogic();
                    }
                }
            } else {
                viewPotentialConflicts(inputDateGlobal);
                liPotentialConfilts.setVisibility(View.VISIBLE);

                Others.displayBackGround(ivArrow, R.drawable.up_arrow, getApplicationContext());
            }
        } else {
            liPotentialConfilts.setVisibility(View.GONE);

            Others.displayBackGround(ivArrow, R.drawable.arrow, getApplicationContext());

        }

    }

    // clearing the entire data on user operation
    private void clearPotentialConflicts() {
        // TODO Auto-generated method stub
        Log.v("clearPotentialConflicts", "am here");
        beforeTime = null;
        afterTime = null;
        endDateBeforeTime = null;
        endDateAfterTime = null;
        beforeEvent = "No events within 2 hours prior to your scheduled appointment.";
        afterEvent = "No events within 2 hours after your scheduled appointment.";
        tvEvent1.setText("beforeEvent");
        tvEvent2.setText("Appointment with Dr. "
                + tvPhysician.getText().toString());

        Others.displayTextColor(tvEvent2, R.color.signup_color, getApplicationContext());
        tvEvent3.setText("afterEvent");
        tvEvent1Date.setText("");
        tvEvent2Date.setText(fullDate);
        tvEvent3Date.setText("");
    }

    // on decline button click
    public void btnDecline(View v) {

        new AlertDialog.Builder(AlertsScreen.this)

                .setMessage("Would you like to see other available slots?")
                .setPositiveButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(),
                                LoginScreen.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }
                })
                .setNegativeButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                               /* Intent i = new Intent(getApplicationContext(),
                                        WhiteSlotes.class);
                                startActivity(i);
                                finish();*/
                                onBackPressed();

                            }
                        }).show();

    }

    // on accept button request
    public void btnAccept(View v) {
        checkWriteCalenderPermission();
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        /*Intent i = new Intent(getApplicationContext(), LoginScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        finish();*/
    }

    // requesting server accepting the white slote
    private void acceptUrlRequest(String appointmentId2) {
        // TODO Auto-generated method stub
        try {
            JSONObject obj = new JSONObject();
            session = new SessionManager(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails(); // name
            authenticationToken = user.get(SessionManager.authenticationToken);
            userId = user.get(SessionManager.userId);
            facilityId = user.get(SessionManager.facilityId);
            obj.put("_AuthenticationToken", authenticationToken);
            obj.put("_UserId", userId);
            obj.put("_FacilityId", facilityId);
            obj.put("_AppointmentId", appointmentId2);
            obj.put("_Accept", "true");
            if (con.isConnectingToInternet()) {
                accept(obj);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readUrlRequest(String appointmentId) {
        try {
            JSONObject obj = new JSONObject();
            session = new SessionManager(getApplicationContext());
            HashMap<String, String> user = session.getUserDetails(); // name
            authenticationToken = user.get(SessionManager.authenticationToken);
            userId = user.get(SessionManager.userId);
            facilityId = user.get(SessionManager.facilityId);

            obj.put("_AuthenticationToken", authenticationToken);
            obj.put("_UserId", userId);
            obj.put("_FacilityId", facilityId);
            obj.put("_AppointmentId", appointmentId);
            if (con.isConnectingToInternet()) {
                read(obj);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void read(JSONObject obj) {
        serverCall = new ServerCall(AlertsScreen.this, AlertsScreen.this);
        serverCall.postUrlRequest(Config.URL_READ_APPT, obj, "readAlert");
    }

    private void accept(JSONObject obj) {
        // TODO Auto-generated method stub
        serverCall = new ServerCall(AlertsScreen.this, AlertsScreen.this);
        serverCall.postUrlRequest(Config.URL_ACCEPT_APPOINTMENT, obj, "accept");
    }

    private void checkWriteCalenderPermission() {
        // only for Marshmallow and newer versions
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (Others.checkPermission(AlertsScreen.this,
                    "android.permission.WRITE_CALENDAR")) {
                // Log.v("Alert Screen","am here");
                acceptButtonPermissionCheck = false;
                acceptUrlRequest(appointmentID);
                ////Log.v()
            } else {
                // check if accept button is pressed first time
                if (!acceptButtonPermissionCheck) {
                    Others.permissionCheckMarshMallow(AlertsScreen.this,
                            "android.permission.WRITE_CALENDAR");
                    acceptButtonPermissionCheck = true;
                    //checkWriteCalenderPermission();
                } // user denaied the permission for access
                else {
                    acceptUrlRequest(appointmentID);
                }
            }
        } else {
            acceptButtonPermissionCheck = false;
            acceptUrlRequest(appointmentID);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void createEventInCalender() {
        // TODO Auto-generated method stub
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy' at 'hh:mm a");
        try {
            cal.setTime(sdf.parse(fullDate));
            long startMillis;
            long endMillis;
            startMillis = cal.getTimeInMillis();
            endMillis = startMillis + 3600000;
            ContentValues values = new ContentValues();
            TimeZone timeZone = TimeZone.getDefault();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
            values.put(CalendarContract.Events.TITLE, "Appointment with Dr. "
                    + tvPhysician.getText().toString());
            values.put(CalendarContract.Events.DESCRIPTION,
                    "we can develop an application in our company ");
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            Uri uri = getContentResolver().insert(
                    CalendarContract.Events.CONTENT_URI, values);
            String eventID = uri.getLastPathSegment();
            values.put(CalendarContract.Events.ORIGINAL_SYNC_ID, eventID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this is for timer display purpose
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public class CounterClass extends CountDownTimer {
        TextView tvTimer;

        CounterClass(long millisInFuture, long countDownInterval,
                     TextView tvTimer) {
            super(millisInFuture, countDownInterval);
            this.tvTimer = tvTimer;
        }

        @Override
        public void onFinish() {
            tvTimer.setText("Completed.");
        }

        @SuppressLint({"NewApi", "DefaultLocale"})
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void onTick(long millisUntilFinished) {
            String hms;
            if (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) < 5)
                hms = String.format(
                        "%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                                .toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                                .toMinutes(millisUntilFinished)));
            else
                hms = String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                                .toHours(millisUntilFinished)));
            //System.out.println(hms);
            tvTimer.setText(hms);
        }
    }
}

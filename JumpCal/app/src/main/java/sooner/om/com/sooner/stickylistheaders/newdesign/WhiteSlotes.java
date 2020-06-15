package sooner.om.com.sooner.stickylistheaders.newdesign;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import sooner.om.com.sooner.AlertsScreen;
import sooner.om.com.sooner.LoginScreen;
import sooner.om.com.sooner.R;
import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.helper.CustomizeDialog;
import sooner.om.com.sooner.helper.NewAlertScreen;
import sooner.om.com.sooner.helper.Others;
import sooner.om.com.sooner.helper.SessionManager;
import sooner.om.com.sooner.network.ServerCall;
import sooner.om.com.sooner.stickylistheaders.StickyListHeadersListView;


@SuppressLint("SimpleDateFormat")
public class WhiteSlotes extends Activity implements
        AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener,
        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        StickyListHeadersListView.OnStickyHeaderChangedListener, ServerCall.AsyncTaskInterface, CustomizeDialog.AsyncTaskInterface {

    String authenticationToken, userId, facilityId;
    Others others;
    // stroes the list of white slots
    private List<NewAlertScreen> list;
    private ServerCall serverCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the UI
        setContentView(R.layout.main);
        others = new Others(this);
        //others.showProgressWithOutMessage();

        initialize();
        sendData();


    }

    // home button click navigation
    public void ivBack(View v) {
        /*Intent i = new Intent(getApplicationContext(), LoginScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);*/
        finish();
    }

    // back button click navigation
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
       /* Intent i = new Intent(getApplicationContext(), LoginScreen.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);*/
        finish();
    }

    // initialize the UI elements to java class
    private void initialize() {
        // TODO Auto-generated method stub
        //others = new Others(this);
        list = new ArrayList<>();
        // refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails(); // name
        authenticationToken = user
                .get(SessionManager.authenticationToken);
        userId = user.get(SessionManager.userId);
        facilityId = user.get(SessionManager.facilityId);
    }

    public void sendData() {
        JSONObject obj = new JSONObject();
        // adding required parameters to the json object
        try {
            obj.put("_AuthenticationToken",
                    authenticationToken);
            obj.put("_UserId", userId);
            obj.put("_FacilityId", facilityId);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String _currentTime = sdf.format(new Date());
            SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");
            String _currentDate = sdfDate.format(new Date());
            obj.put("_Time", _currentTime);
            obj.put("_Date", _currentDate);
            jsonObjectRequest(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jsonObjectRequest(JSONObject obj) {
        // TODO Auto-generated method stub

        serverCall = new ServerCall(WhiteSlotes.this, WhiteSlotes.this);
        serverCall.postUrlRequest(Config.URL_WHITE_SLOTS, obj, "loadAlertScreen");

    }

    private void loadAlertData(JSONObject response) {
        // TODO Auto-generated method stub
        try {
            JSONObject whiteSlotes = response.getJSONObject("WhiteSlots");
            Iterator<String> iter = whiteSlotes.keys();

            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONArray data = whiteSlotes.getJSONArray(key);
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        String appointmentId = obj.getString("AppointmentId");
                        String startTime = obj.getString("StartTime");
                        String date = obj.getString("Date").substring(0, obj.getString("Date").indexOf("T"));
                        String providerName = obj.getString("ProviderName");
                        String locationName = obj.getString("LocationName");
                        String specialtyName = Others.optString(obj, "SpecialtyName");
                        String isRead = obj.getString("btRead");
                        //Log.v("data", appointmentId+"/"+providerName+"/"+specialtyName+"/"+locationName+"/"+date+"/"+startTime+"/"+isRead);
                        list.add(new NewAlertScreen(new String[]{appointmentId, providerName, specialtyName, locationName, date, startTime, isRead}));
                    }


                } catch (JSONException e) {
                    // Something went wrong!
                    e.printStackTrace();
                }
            }
            WhiteSlotesAdapter mAdapter = new WhiteSlotesAdapter(this, list);
            StickyListHeadersListView stickyList = (StickyListHeadersListView) findViewById(R.id.list);
            stickyList.setOnItemClickListener(this);
            stickyList.setOnHeaderClickListener(this);
            stickyList.setOnStickyHeaderChangedListener(this);
            stickyList.setOnStickyHeaderOffsetChangedListener(this);
            stickyList.setEmptyView(findViewById(R.id.tvEmpty));
            stickyList.setDrawingListUnderStickyHeader(true);
            stickyList.setAreHeadersSticky(true);
            stickyList.setAdapter(mAdapter);
            TextView tvEmpty = (TextView) findViewById(R.id.tvEmpty);

            //stickyList.setStickyHeaderTopOffset(-20);

            if (list.size() > 0) {
                tvEmpty.setVisibility(View.GONE);
                stickyList.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.VISIBLE);
                stickyList.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Toast.makeText(this, "Item " + list.get(position).getAppointmentId() + " clicked!", Toast.LENGTH_SHORT).show();
        checkData(list.get(position).getAppointmentId());
    }

    public void checkData(String appointmentID) {
        JSONObject obj = new JSONObject();
        //adding the required parameters to the json object
        try {
            obj.put("_AuthenticationToken",
                    authenticationToken);
            obj.put("_UserId", userId);
            obj.put("_FacilityId", facilityId);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String _currentTime = sdf.format(new Date());
            SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");
            String _currentDate = sdfDate.format(new Date());
            obj.put("_currentTime", _currentTime);
            obj.put("_currentDate", _currentDate);
            obj.put("_NotificationAppointmentId", appointmentID);
            checkIsAvilable(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkIsAvilable(final JSONObject obj) {
        // TODO Auto-generated method stub
        // checking weather the white slote is available
        serverCall.postUrlRequest(Config.URL_SINGLE_WHITE_SLOTE_DETAILS, obj, "isWhiteSlotAvilable");
    }

    @Override
    public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
        //Toast.makeText(this, "Header " + headerId + " currentlySticky ? " + currentlySticky, Toast.LENGTH_SHORT).show();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onStickyHeaderOffsetChanged(StickyListHeadersListView l, View header, int offset) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            header.setAlpha(1 - (offset / (float) header.getMeasuredHeight()));
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onStickyHeaderChanged(StickyListHeadersListView l, View header, int itemPosition, long headerId) {
        header.setAlpha(1);
    }

    @Override
    public void onAsyncTaskInterfaceResponse(JSONObject result, String tag) {
        // initial loading this will execute
        if (tag.equals("loadAlertScreen")) {
            loadAlertData(result);
        }// clicked on any white slot for more details this will run
        else if (tag.equals("isWhiteSlotAvilable")) {
            try {
                boolean error = result.getBoolean("Error");

                // if the white slot is available then this intent will trigger
                if (!error) {
                    Intent i = new Intent(getApplicationContext(), AlertsScreen.class);
                    i.putExtra("nav_type", "landingScreen");
                    i.putExtra("jsonData", result.toString());
                    startActivity(i);
                    //finish();

                } else {
                    if (result.getString("Message").equals("Some one already booked this appointment.") || result.getString("Message").equals("No notification with this id.")) {
                        CustomizeDialog customizeDialog = new CustomizeDialog(WhiteSlotes.this, "alreadyBooked", WhiteSlotes.this);
                        customizeDialog.setMessage("Sorry! This appointment is booked already by other user.");
                        customizeDialog.show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // if the white slot is already accepted then reload the view
    @Override
    public void onAsyncTaskInterfaceResponse(String result) {
        Intent i = new Intent(getApplicationContext(), WhiteSlotes.class);
        startActivity(i);
        finish();
    }
}
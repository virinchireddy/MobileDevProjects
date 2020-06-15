package sooner.om.com.sooner.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sooner.om.com.sooner.PatientLandingScreen;
import sooner.om.com.sooner.R;
import sooner.om.com.sooner.adapter.LoadAppointmentsAdapter;
import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.helper.PatientAppointmentsList;
import sooner.om.com.sooner.network.ConnectionDetector;
import sooner.om.com.sooner.network.ServerCall;

public class FragmentDashboard extends Fragment implements ServerCall.AsyncTaskInterface {
    View rootView;

    private ConnectionDetector con; // for checking the internet connection
    private RecyclerView mRecyclerView;
    private LoadAppointmentsAdapter mAdapter;
    private List<PatientAppointmentsList> PatientAppointmentsList = null;
    private LinearLayout linoAppointment;
    private TextView tvpatientname;

    //this method finds weather the a json object has the corresponding value which is not equals to null
    public static String optString(JSONObject json, String key) {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key) || json.optString(key).equals("-1")
                || json.optString(key).equals("-1.00"))
            return "";
        else
            return json.optString(key);
    }

    //first executable method
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        // inflateing the dashboard to the UI
        rootView = inflater.inflate(R.layout.fragment_patient_dashboard,
                container, false);

        return rootView;

    }

    //if we minimize the application and re open the app this method will execute
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // initializing the UI views to the code
        initialize();
        // clearing the existing appointment if any
        PatientAppointmentsList.clear();
        mRecyclerView.setAdapter(null);
        // loading the server data
        loadData();
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    // clear all the existing views in the pause state of the screen
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        int size = PatientAppointmentsList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                PatientAppointmentsList.remove(0);
            }
            mAdapter.notifyItemRangeRemoved(0, size);
        }

    }

    @SuppressLint("SimpleDateFormat")
    private void loadData() {
        // TODO Auto-generated method stub
        // required information is adding to JSON object to get the dashboard
        try {
            JSONObject appointments = new JSONObject();
            appointments.put("_AuthenticationToken",
                    PatientLandingScreen.authenticationToken);
            appointments.put("_UserId", PatientLandingScreen.userId);
            appointments.put("_FacilityId", PatientLandingScreen.facilityId);
            appointments.put("_Time", new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
            appointments.put("_Date", new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()));
            // checking for internet connection
            if (con.isConnectingToInternet()) {
                getAppointmentList(appointments);
                Log.v("dashBoard", appointments.toString());
            } else {
                con.failureAlert();
            }
            // loadInitialBars(bars);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    // server request for data
    private void getAppointmentList(JSONObject appointments) {
        // TODO Auto-generated method stub
        ServerCall serverCall = new ServerCall(getActivity(), FragmentDashboard.this);
        serverCall.postUrlRequest(Config.URL_LOAD_APPOINTMENTS, appointments, "dashBoard");
    }

    // loading the data to variables
    @SuppressLint({"SimpleDateFormat", "DefaultLocale"})
    private void loadAppointmentList(JSONObject response) {

        try {

            // total_records = response.getInt("total_notifications");

            String ShowTimerBefore = response.getString("ShowTimerBefore");
            JSONArray AppointmentData = response.optJSONArray("PatientAppointments");
            // storing all the records in variables
            for (int i = 0; i < AppointmentData.length(); i++) {
                // getting single appointment details
                JSONObject singleUser = AppointmentData.getJSONObject(i);
                String AppointmentId = singleUser.getString("AppointmentId");
                String PatientName = optString(singleUser, "PatientName");
                String Date = optString(singleUser, "Date");
                String Time = optString(singleUser, "Time");
                // for adding +5 and -5 values from desktop application
                String timeGap = optString(singleUser, "TimeGap");
                timeGap = timeGap.substring(0, timeGap.indexOf('.'));
                int varibleTime = singleUser.getInt("VariableTime") * 60;
                varibleTime = (Integer.parseInt(timeGap) + varibleTime);
                timeGap = varibleTime + "";
                // displaying other details
                String PhysicanName = optString(singleUser, "PhysicanName");
                String FacilityName = optString(singleUser, "FacilityName");
                String Location = optString(singleUser, "Location");
                String Specialty = optString(singleUser, "Specialty");
                String TimeDifference = optString(singleUser, "TimeDifference");
                /*String timeGap=optString(singleUser,"TimeGap");
				timeGap=timeGap.substring(0,timeGap.indexOf('.'));*/
                String timeZone = optString(singleUser, "TimeZone");
                String[] finalString = {AppointmentId, PatientName, Date,
                        Time, PhysicanName, FacilityName, Location, Specialty,
                        TimeDifference, ShowTimerBefore, timeGap, timeZone};

                PatientAppointmentsList.add(new PatientAppointmentsList(finalString));

            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    //initialises the various attributes used in this class
    public void initialize() {

        con = new ConnectionDetector(getActivity());
        PatientAppointmentsList = new ArrayList<>();
        PatientAppointmentsList.clear();
        mRecyclerView = (RecyclerView) rootView
                .findViewById(R.id.rvAppointmentList);
        linoAppointment = (LinearLayout) rootView
                .findViewById(R.id.linoAppointment);
        tvpatientname = (TextView) rootView.findViewById(R.id.tvpatientname);

    }

    // interface implemented method which will handle the response
    @Override
    public void onAsyncTaskInterfaceResponse(JSONObject result, String tag) {
        try {

            String patientname = result
                    .getString("PatientName");
            Log.v("data", result.toString());

            tvpatientname.setText("Welcome " + patientname + ",");
            // for showing bell count
            String tickers = result.getString("UnreadNotifications");
            if (Integer.parseInt(tickers) > 0) {
                PatientLandingScreen.tvTicker.setText(tickers);
                PatientLandingScreen.tvTicker.setVisibility(View.VISIBLE);
            } else {
                PatientLandingScreen.tvTicker.setVisibility(View.GONE);
            }
            loadAppointmentList(result);

            // create an Object for Adapter
            mAdapter = new LoadAppointmentsAdapter(PatientAppointmentsList);

            mRecyclerView.setAdapter(mAdapter);
            // mAdapter.notifyDataSetChanged();
            // if the data is not there should show a message to user saying no appointments available
            if (PatientAppointmentsList.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                linoAppointment.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                linoAppointment.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

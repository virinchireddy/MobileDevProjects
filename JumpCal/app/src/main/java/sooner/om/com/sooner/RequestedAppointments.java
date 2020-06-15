package sooner.om.com.sooner;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sooner.om.com.sooner.adapter.RequestedAppointmentsAdapter;
import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.fragment.FragmentFacility;
import sooner.om.com.sooner.helper.Others;
import sooner.om.com.sooner.helper.RequestedAppointmentDetails;
import sooner.om.com.sooner.network.ConnectionDetector;
import sooner.om.com.sooner.network.ServerCall;

// design screen
public class RequestedAppointments extends Activity implements ServerCall.AsyncTaskInterface {

    private TextView tvEmptyView;
    private RecyclerView mRecyclerView;
    private ConnectionDetector con;
    private RequestedAppointmentsAdapter mAdapter;
    private List<RequestedAppointmentDetails> requestedAppointmentsList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.requested_appointments);
        initialize();
        loadData();
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(RequestedAppointments.this);
        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);

    }

    private void loadData() {
        try {
            JSONObject obj=new JSONObject();
            obj.put("_UserId",PatientLandingScreen.userId);
            obj.put("_AuthenticationToken",PatientLandingScreen.authenticationToken);
            obj.put("_FacilityId",PatientLandingScreen.facilityId);
            if (con.isConnectingToInternet()) {
                ServerCall serverCall = new ServerCall(RequestedAppointments.this, RequestedAppointments.this);
                serverCall.postUrlRequest(Config.URL_REQUESTED_APPOINTMENTS, obj, "requestedAppointment");
            } else {
                con.failureAlert();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void initialize() {

        con = new ConnectionDetector(RequestedAppointments.this);
        requestedAppointmentsList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.rvRequestedAppointmentList);
        tvEmptyView = (TextView)findViewById(R.id.tvEmptyView);


    }

    public void back(View v) {
        finish();
    }

    public void home(View v) {
        finish();
    }

    @Override
    public void onAsyncTaskInterfaceResponse(JSONObject result, String tag) {

        loadRequestesList(result);

        mAdapter = new RequestedAppointmentsAdapter(requestedAppointmentsList, RequestedAppointments.this);

        mRecyclerView.setAdapter(mAdapter);


        if (requestedAppointmentsList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);

        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }
    }

    private void loadRequestesList(JSONObject result) {

        try{
            JSONArray appointments=result.getJSONArray("requestedAppointments");
            for(int i=0;i<appointments.length();i++){
                JSONObject obj=appointments.getJSONObject(i);
                String time=obj.getString("dtRequestedDate");
                time=time.substring(0,time.indexOf('T'));
                time= Others.changeDateFormat(time)+" at "+obj.getString("ReQuestedTime");
                requestedAppointmentsList.add(new RequestedAppointmentDetails(new String[]{obj.getString("ProviderName"),obj.getString("vcSpecialtyName")
                        ,obj.getString("vcNotes"),obj.getString("Locationname"),time}));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}


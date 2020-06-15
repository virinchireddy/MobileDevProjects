package com.om.virinchi.ricelake.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.om.virinchi.ricelake.Activites.LandingScreen;
import com.om.virinchi.ricelake.Adapter.UpdatesAdapter;
import com.om.virinchi.ricelake.Helper.SessionManager;
import com.om.virinchi.ricelake.Helper.UpdatesListDetails;
import com.om.virinchi.ricelake.Network.AppController;
import com.om.virinchi.ricelake.Network.Config;
import com.om.virinchi.ricelake.Network.ConnectionDetector;
import com.om.virinchi.ricelake.Network.Others;
import com.om.virinchi.ricelake.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Virinchi on 8/20/2016.
 */
public class FragmentSafetyUpdates extends Fragment implements View.OnClickListener {
    public static List<UpdatesListDetails> updatesList = null;
    public static UpdatesAdapter mAdapter;
    TextView tvInspectorsname, tvProjectmanager, tvSuperintendent, tvJobno, tvDate;
    Button btnNewInspection, btnProjectname;
    String project_id, Inspector, project_manager, job_no, lastupdateddate, superintendent, projectName, history;
    Others others;
    ConnectionDetector con;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    View rootView;
    static String projectUpdateId;
    int length;
    SessionManager session;

    public static String optString(JSONObject json, String key) {
        // http://code.google.com/p/android/issues/detail?id=13830-+
        if (json.isNull(key) || json.optString(key).equals("-1")
                || json.optString(key).equals("-1.00"))
            return "Not Updated";
        else
            return json.optString(key);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        rootView = inflater.inflate(R.layout.fragment_safetyupdates,
                container, false);
        savedInstanceState = getArguments();

        project_id = savedInstanceState.getString("project_id");
        Log.v("pid", project_id.toString());
        Inspector = savedInstanceState.getString("Inspector");
        project_manager = savedInstanceState.getString("project_manager");
        job_no = savedInstanceState.getString("job_no");
        lastupdateddate = savedInstanceState.getString("ludate");
        superintendent = savedInstanceState.getString("Superintendant");
        projectName = savedInstanceState.getString("project_name");
        history = savedInstanceState.getString("History");
        intilize();
        Log.v("who_is_this", LandingScreen.userType);
        if (LandingScreen.userType.equals("Super Admin") || LandingScreen.userType.equals("Staff") || LandingScreen.userType.equals("Project Manager") || LandingScreen.userType.equals("Superintendent")) {
            btnNewInspection.setVisibility(View.GONE);


        } else if (history == "history") {
            btnNewInspection.setVisibility(View.GONE);
        }
       /* if (LandingScreen.userType.equals("Super Admin") || LandingScreen.userType.equals("Staff") || LandingScreen.userType.equals("Project Manager") || LandingScreen.userType.equals("Superintendent")) {
            btnNewInspection.setVisibility(View.GONE);


        }*/


        loaddata();
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setNestedScrollingEnabled(false);

        return rootView;


    }

    public void intilize() {

        LandingScreen.ivBack.setVisibility(View.VISIBLE);
        updatesList = new ArrayList<UpdatesListDetails>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvUpdatesList);
        con = new ConnectionDetector(getActivity());
        others = new Others(getActivity());
        btnNewInspection = (Button) rootView.findViewById(R.id.btnNewInspection);
        btnNewInspection.setOnClickListener(this);
        btnProjectname = (Button) rootView.findViewById(R.id.btnProjectname);
        btnProjectname.setText(projectName + " - List of Updates");
        tvInspectorsname = (TextView) rootView.findViewById(R.id.tvInspectorsname);
        tvInspectorsname.setText("Inspector Name: " + Inspector);
        tvProjectmanager = (TextView) rootView.findViewById(R.id.tvProjectmanager);
        tvProjectmanager.setText("Project Manager: " + project_manager);
        tvSuperintendent = (TextView) rootView.findViewById(R.id.tvSuperintendent);
        tvSuperintendent.setText("Superintendent: " + superintendent);
        tvJobno = (TextView) rootView.findViewById(R.id.tvJobno);
        tvJobno.setText("Job Number: " + job_no);
        tvDate = (TextView) rootView.findViewById(R.id.tvDate);
        tvDate.setText("Date: " + lastupdateddate);
        session = new SessionManager(getActivity());
    }

    public void onClick(View v) {

        if (v.getId() == R.id.btnNewInspection) {
            ((AppController) getActivity().getApplication()).setNew("new");
            Log.v("images" ,((AppController) getActivity().getApplication()).getNew().toString() );
            Bundle bundle = new Bundle();
            bundle.putString("project_id", project_id);

            if (length == 0) {
                //   bundle.putString("update_id", "0");
                projectUpdateId = "0";
                ((AppController) getActivity().getApplication()).setUpdateVariable("0");
            } else {
                //  bundle.putString("update_id", projectUpdateId);
                ((AppController) getActivity().getApplication()).setUpdateVariable(projectUpdateId);
            }
            bundle.putString("projectName", projectName);
            bundle.putString("navigation", "NewInspection");
            bundle.putString("job_no", job_no);

            //    bundle.putString("update_id" , updateListDetails.getupdateId());


            //  bundle.putString("navigation","UpdateInspection");
            FragmentNewInspection inspection = new FragmentNewInspection();
            inspection.setArguments(bundle);
            Log.v("mexico", String.valueOf(bundle));
            this.getFragmentManager().beginTransaction().replace(R.id.flContainer, inspection).addToBackStack(null).commit();
        }


    }

    public void loaddata() {
        try {
            JSONObject updates = new JSONObject();
            updates.put("projectid", project_id);
            updates.put("UserId", LandingScreen.userId);
            updates.put("Token", LandingScreen.authenticationToken);
            Log.v("oppo", project_id);
            if (con.isConnectingToInternet()) {
                getUpdatesList(updates);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getUpdatesList(JSONObject updates) {
        String tag_string_req = "req_FacilityList";
        others.showProgressWithOutMessage();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_UPDATES, updates,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            boolean error = response.getBoolean("Error");
                            if (!error) {

                                loadUpdatesList(response);

                                // create an Object for Adapter
                                mAdapter = new UpdatesAdapter(updatesList,
                                        mRecyclerView, getActivity());

                                mRecyclerView.setAdapter(mAdapter);
                                // mAdapter.notifyDataSetChanged();


                            } else {
                                others.ToastMessage(response.getString("error_msg"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley", "error: " + error);
                others.hideDialog();
                NetworkResponse response = error.networkResponse;

                if (response != null) {
                    switch (response.statusCode) {
                        case 401:
                           /* Intent i = new Intent(getActivity(), LoginScreen.class);
                            session.logoutUser();
                            startActivity(i);*/
                            session.logoutUser();
                            getActivity().finish();
                            Toast.makeText(getActivity(),
                                    "User Logged in from other device",
                                    Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }
        });
        jsObjRequest.setShouldCache(false);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest,
                tag_string_req);
    }

    public void loadUpdatesList(JSONObject response) {
        try {
            JSONArray projectupdates = response.optJSONArray("projectupdates");
            for (int i = 0; i < projectupdates.length(); i++) {
                length = projectupdates.length();
                JSONObject singleupdate = projectupdates.getJSONObject(i);
                if (i == 0)
                    projectUpdateId = optString(singleupdate, "intProjectProcessUpdateId");
                Log.v("singleUpdate", projectUpdateId + "/" + singleupdate.toString());

                String projectId = optString(singleupdate, "intProjectId");

                String createdDate = optString(singleupdate, "dtCreatedDate");
                String lastupdated = optString(singleupdate, "dtModifiedDate");
                String updateId = optString(singleupdate, "intProjectProcessUpdateId");
                if ((i - 1) == projectupdates.length()) {
                    projectUpdateId = updateId;
                }
                String inspectorsName = optString(singleupdate, "InspectorName");
                String categoriesCount = optString(singleupdate, "SelectedCategoriesCount");
                String subcategoriesCount = optString(singleupdate, "SelectedSubCategoryCount");
                String okCount = optString(singleupdate, "OKCount");
                String actCount = optString(singleupdate, "ACTCount");
                String naCount = optString(singleupdate, "NACount");
                String blCount = optString(singleupdate, "BlankCount");
                String comments = optString(singleupdate, "CommentsCount");
                String photos = optString(singleupdate, "ImagesCount");
                String jobno = job_no;
                String projectname = projectName;
                if (history == "history") {
                    String history = "history";

                    String[] finalString = {projectId, createdDate, updateId, inspectorsName, categoriesCount, subcategoriesCount, okCount, actCount, naCount, comments, photos, lastupdated, blCount, projectname, history, jobno};
                    updatesList.add(new UpdatesListDetails(finalString));
                } else {
                    String history = "project";
                    String[] finalString = {projectId, createdDate, updateId, inspectorsName, categoriesCount, subcategoriesCount, okCount, actCount, naCount, comments, photos, lastupdated, blCount, projectname, history, jobno};
                    updatesList.add(new UpdatesListDetails(finalString));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.om.virinchi.ricelake.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.om.virinchi.ricelake.Activites.LandingScreen;
import com.om.virinchi.ricelake.Adapter.ProjectAdapter;
import com.om.virinchi.ricelake.Helper.ProjectListDetails;
import com.om.virinchi.ricelake.Helper.SessionManager;
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

public class FragmentProjects extends Fragment implements View.OnClickListener {
    public static ProjectAdapter mAdapter;
    public static List<ProjectListDetails> projectList = null;
    LinearLayoutManager mLayoutManager;
    Others others;
    ConnectionDetector con;
    RecyclerView mRecyclerView;
    SessionManager session;
    View rootView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    SearchView svProjectName;
    TextView tvNoDataFound, tvAccountname;
    //for searching the project
    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String query) {

            final List<ProjectListDetails> filteredList = new ArrayList<>();


            for (int i = 0; i < projectList.size(); i++) {
                query = query.toLowerCase();
                ProjectListDetails list = projectList.get(i);
                Log.v(query, query + "");
                if (list.getprojectName().toLowerCase().contains(query) || list.getjobNo().toLowerCase().contains(query)) {


                    String projectName = list.getprojectName();
                    String jobNo = list.getjobNo();
                    String superIntendent = list.getsuperIntendent();
                    String inspector = list.getinspector();
                    String projectManager = list.getprojectManager();
                    String updates = list.getupdates();
                    String photos = list.getphotos();
                    String lastUpdated = list.getlastUpdate();
                    String remarks = list.getRemarks();
                    String projectId = list.getProjectId();
                    String projectCreatedDate = list.getdatecreated();
                    String[] finalString = {projectName, jobNo, superIntendent, inspector, projectManager, updates, photos, lastUpdated, remarks, projectId, projectCreatedDate};
                    filteredList.add(new ProjectListDetails(finalString));

                    mRecyclerView.setVisibility(View.VISIBLE);
                    tvNoDataFound.setVisibility(View.GONE);

                }
                if (filteredList.size() == 0) {
                    tvNoDataFound.setVisibility(View.VISIBLE);
                }

            }

            mRecyclerView.setLayoutManager(new LinearLayoutManager(
                    getActivity()));
            mAdapter = new ProjectAdapter(filteredList, mRecyclerView,
                    getActivity());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged(); // data set changed
            return true;

        }

        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };

    public static String optString(JSONObject json, String key) {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key) || json.optString(key).equals("-1")
                || json.optString(key).equals("-1.00"))
            return "";
        else
            return json.optString(key);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        rootView = inflater.inflate(R.layout.fragment_projects,
                container, false);

        return rootView;


    }

    @Override
    public void onResume() {
        super.onResume();
        intilize();
        svProjectName.setQuery("", false);
        rootView.requestFocus() ;
        projectList.clear();
        loaddata();
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        //  mRecyclerView.setNestedScrollingEnabled(false);
    }

    private void intilize() {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvProjectsList);
        con = new ConnectionDetector(getActivity());
        others = new Others(getActivity());
        tvNoDataFound = (TextView) rootView.findViewById(R.id.tvNoDataFound);
        projectList = new ArrayList<ProjectListDetails>();
        LandingScreen.ivBack.setVisibility(View.INVISIBLE);
        LandingScreen.ivCamera.setVisibility(View.INVISIBLE);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                projectList.clear();
                loaddata();
                svProjectName.setQuery("", false);
                svProjectName.clearFocus();
                tvNoDataFound.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(R.color.blue);
        svProjectName = (SearchView) rootView.findViewById(R.id.svProjectName);
        svProjectName.setOnQueryTextListener(listener);
        svProjectName.setQueryHint("Search by project name / job#");

        svProjectName.setIconifiedByDefault(false);
        tvAccountname = (TextView) rootView.findViewById(R.id.tvAccountname);
        tvAccountname.setText("Welcome " + LandingScreen.userName);

        session = new SessionManager(getActivity());
    }

    public void loaddata() {
        try {
            JSONObject projects = new JSONObject();
            projects.put("Token", LandingScreen.authenticationToken);
            projects.put("UserId", LandingScreen.userId);
            if (con.isConnectingToInternet()) {
                getProjectList(projects);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getProjectList(JSONObject projects) {
        String tag_string_req = "req_FacilityList";
        others.showProgressWithOutMessage();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_PROJECTS, projects,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            boolean error = response.getBoolean("Error");
                            if (!error) {

                                loadProjectsList(response);
                                Log.v("in_response", response.toString());
                                // create an Object for Adapter
                                mAdapter = new ProjectAdapter(projectList,
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
                          /*  Intent i = new Intent(getActivity(), LoginScreen.class);
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

    public void loadProjectsList(JSONObject response) {
        try {
            JSONArray projectdata = response.optJSONArray("projectdata");
            for (int i = 0; i < projectdata.length(); i++) {
                JSONObject singleproject = projectdata.getJSONObject(i);
                String projectName = optString(singleproject, "vcProjectName");
                String jobNo = optString(singleproject, "intJobNumber");
                String superIntendent = optString(singleproject, "Superintendant");
                String inspector = optString(singleproject, "Inspector");
                String projectManager = optString(singleproject, "ProjectManager");
                String updates = optString(singleproject, "Updates");
                String photos = optString(singleproject, "Images");
                String lastUpdated = optString(singleproject, "dtModifiedDate");
                String remarks = optString(singleproject, "vcRemarks");
                String projectId = optString(singleproject, "intProjectId");
                String dateCreated = optString(singleproject, "dtCreatedDate");

                String[] finalString = {projectName, jobNo, superIntendent, inspector, projectManager, updates, photos, lastUpdated, remarks, projectId, dateCreated};
                projectList.add(new ProjectListDetails(finalString));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {

    }
}
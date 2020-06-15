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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.om.virinchi.ricelake.Activites.LandingScreen;
import com.om.virinchi.ricelake.Adapter.CommentsAdapter;
import com.om.virinchi.ricelake.Helper.CommentsListDetails;
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
 * Created by Virinchi on 8/24/2016.
 */
public class FragmentComments extends Fragment implements View.OnClickListener {
    public static List<CommentsListDetails> commentsList = null;
    public static CommentsAdapter mAdapter;
    View rootView;
    Button btnaddnewcomment, btnProjectname;
    Others others;
    ConnectionDetector con;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    SessionManager session;
    String update_id, project_name, navigation, commentControl, freeze, history ,  nw;
    public static int nav = 2;

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
        rootView = inflater.inflate(R.layout.fragment_comments,
                container, false);
        savedInstanceState = getArguments();
        update_id = savedInstanceState.getString("update_id");
        project_name = savedInstanceState.getString("projectName");
        navigation = savedInstanceState.getString("navigation");
        commentControl = savedInstanceState.getString("commentcontrol");
        nw  = ((AppController) getActivity().getApplication()).getNew();
        freeze = savedInstanceState.getString("freeze");
        history = savedInstanceState.getString("history");
        intilize();
        if (freeze == "true" && commentControl.equals("newInspection")) {
            btnaddnewcomment.setVisibility(View.VISIBLE);
        } else if (freeze == "true") {
            btnaddnewcomment.setVisibility(View.GONE);
        } else {
            btnaddnewcomment.setVisibility(View.VISIBLE);
        }
        if (LandingScreen.userType.equals("Super Admin") || LandingScreen.userType.equals("Staff") || LandingScreen.userType.equals("Project Manager") || LandingScreen.userType.equals("Superintendent")) {
            btnaddnewcomment.setVisibility(View.GONE);

        }
        Log.v("buridi", savedInstanceState.toString());
        loaddata();
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);

        return rootView;


    }

    public void intilize() {
        LandingScreen.ivBack.setVisibility(View.VISIBLE);
        commentsList = new ArrayList<CommentsListDetails>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvCommentsList);
        con = new ConnectionDetector(getActivity());
        others = new Others(getActivity());
        session = new SessionManager(getActivity());
        btnaddnewcomment = (Button) rootView.findViewById(R.id.btnaddnewcomment);
        btnaddnewcomment.setOnClickListener(this);

        btnProjectname = (Button) rootView.findViewById(R.id.btnProjectname);
        btnProjectname.setText(project_name + "  - Comments");


        if (commentControl.equals("newInspection") && navigation.equals("NewInspection")) {
            btnaddnewcomment.setVisibility(View.VISIBLE);
        } else if (!commentControl.equals("newInspection") && navigation.equals("NewInspection")) {
            btnaddnewcomment.setVisibility(View.GONE);
            mAdapter = new CommentsAdapter(navigation, commentControl);
        } else if (navigation.equals("UpdateInspection")) {
            btnaddnewcomment.setVisibility(View.VISIBLE);
        }

    }

    public void loaddata() {
        try {
            JSONObject comments = new JSONObject();
            comments.put("ProjectUpdateId", update_id);
            comments.put("Token", LandingScreen.authenticationToken);
            comments.put("UserId", LandingScreen.userId);
            if (con.isConnectingToInternet()) {
                getCommentsList(comments);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getCommentsList(JSONObject comments) {
        String tag_string_req = "req_FacilityList";
        others.showProgressWithOutMessage();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_LOAD_COMMENTS, comments,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            boolean error = response.getBoolean("Error");
                            if (!error) {

                                loadCommentsList(response);

                                // create an Object for Adapter
                                mAdapter = new CommentsAdapter(commentsList,
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

    public void loadCommentsList(JSONObject response) {
        try {
            JSONArray comments = response.optJSONArray("CommentsUpdateData");
            for (int i = 0; i < comments.length(); i++) {
                JSONObject singlecomment = comments.getJSONObject(i);
                String refNum = optString(singlecomment, "RefNum");
                String commentsupdated_id = optString(singlecomment, "CommentsUpdatedId");
                String description = optString(singlecomment, "vcDescription");
                String action = optString(singlecomment, "vcActionTaken");
                String createdDate = optString(singlecomment, "dtCreatedDate");
                String update_id = optString(singlecomment, "intProjectProcessUpdateId");
                String projectName = project_name;
                String NEW  = nw ;
                String[] finalString = {refNum, commentsupdated_id, description, action, createdDate, update_id, projectName,freeze,history,navigation , NEW};
                commentsList.add(new CommentsListDetails(finalString));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnaddnewcomment) {
            if (nw == "new") {
                Toast.makeText(getActivity(),
                        "Save an inspection to perform this operation",
                        Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("update_id", update_id);
                bundle.putString("c_u_id", "0");
                bundle.putString("Comment", "");
                bundle.putString("action", "");
                bundle.putString("RefNum", "0");
                bundle.putString("projectName", project_name);
                bundle.putString("buttonText", "Post");
                bundle.putString("commentType", " - Add Comment");
                bundle.putString("freeze", freeze);
                bundle.putString("history", history);
                bundle.putString("navigation", navigation);
                Log.v("kashmora", String.valueOf(bundle));
                FragmentAddComment addComment = new FragmentAddComment();
                addComment.setArguments(bundle);

                this.getFragmentManager().beginTransaction().replace(R.id.flContainer, addComment).addToBackStack(null).commit();
            }
        }
    }
}

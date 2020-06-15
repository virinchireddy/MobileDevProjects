package com.om.virinchi.ricelake.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.om.virinchi.ricelake.Activites.LandingScreen;
import com.om.virinchi.ricelake.Helper.SessionManager;
import com.om.virinchi.ricelake.Network.AppController;
import com.om.virinchi.ricelake.Network.Config;
import com.om.virinchi.ricelake.Network.ConnectionDetector;
import com.om.virinchi.ricelake.Network.Others;
import com.om.virinchi.ricelake.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Virinchi on 8/24/2016.
 */
public class FragmentAddComment extends Fragment implements View.OnClickListener {
    Button btnUpdate, btnCancel, btnProjectname;
    Spinner spinner;
    View rootView;
    String update_id, c_update_id, action, comment, RefNum, project_name, commentlen, actionlen, buttonText, commentType, freeze, history,navigation , nw;
    EditText etComment, etActionTaken;
    ConnectionDetector con;
    SessionManager session;
    Others others;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        rootView = inflater.inflate(R.layout.fragement_addcomment,
                container, false);
        savedInstanceState = getArguments();
        update_id = savedInstanceState.getString("update_id");
        c_update_id = savedInstanceState.getString("c_u_id");
        action = savedInstanceState.getString("action");
        comment = savedInstanceState.getString("Comment");
        RefNum = savedInstanceState.getString("RefNum");
        project_name = savedInstanceState.getString("projectName");
        buttonText = savedInstanceState.getString("buttonText");
        commentType = savedInstanceState.getString("commentType");
        freeze = savedInstanceState.getString("freeze");
        history = savedInstanceState.getString("history");
        navigation=savedInstanceState.getString("navigation");
        nw  = ((AppController) getActivity().getApplication()).getNew();
        intilize();
        if(nw == "new"){
            btnUpdate.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            etActionTaken.setEnabled(false);
            etComment.setEnabled(false);

        }
        Log.v("kirak", String.valueOf(savedInstanceState));
        //history handliong , freeze inspection|| history.equals("history")
        if (LandingScreen.userType.equals("Super Admin") || LandingScreen.userType.equals("Staff") || LandingScreen.userType.equals("Project Manager") || LandingScreen.userType.equals("Superintendent")  || (freeze.equals("true") && navigation.equals("UpdateInspection"))) {
            btnUpdate.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            spinner.setEnabled(false);
            etActionTaken.setEnabled(false);
            etComment.setEnabled(false);


        }


        return rootView;


    }


    public void intilize() {

        LandingScreen.ivBack.setVisibility(View.VISIBLE);
        session = new SessionManager(getActivity());
        btnProjectname = (Button) rootView.findViewById(R.id.btnProjectname);
        btnProjectname.setText(project_name + commentType);
        btnUpdate = (Button) rootView.findViewById(R.id.btnUpdate);
        btnUpdate.setText(buttonText);
        btnUpdate.setOnClickListener(this);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        con = new ConnectionDetector(getActivity());
        others = new Others(getActivity());
        etComment = (EditText) rootView.findViewById(R.id.etComment);
        etComment.setText(comment);
        etActionTaken = (EditText) rootView.findViewById(R.id.etActionTaken);
        etActionTaken.setText(action);


        if (RefNum.length() >= 1) {
            spinner.setSelection(Integer.parseInt(RefNum));

        }

    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnUpdate) {
            commentlen = etComment.getText().toString();
            actionlen = etActionTaken.getText().toString();
            if (spinner.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Select a Category", Toast.LENGTH_SHORT).show();

            } else if (etComment.getText().length() == 0) {
                Toast.makeText(getActivity(), "Please enter Comment", Toast.LENGTH_SHORT).show();
            } else if (etActionTaken.getText().length() == 0) {

                Toast toast = Toast.makeText(getActivity(), "Please enter Action", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                sendCommentsToServer();
            }


        } else if (v.getId() == R.id.btnCancel) {
            getFragmentManager().popBackStack();
        }

    }

    private void sendCommentsToServer() {
        String Description = etComment.getText().toString();
        String Actiontaken = etActionTaken.getText().toString();

        try {
            JSONObject obj = new JSONObject();
            obj.put("CategoryMasterId", spinner.getSelectedItemPosition());
            obj.put("Description", Description);
            obj.put("ActionTaken", Actiontaken);
            obj.put("ProjectUpdateId", update_id);
            obj.put("CommentUploadedId", c_update_id);
            obj.put("UserId", LandingScreen.userId);
            obj.put("Token", LandingScreen.authenticationToken);

            Log.v("in_camlll", spinner.getSelectedItemPosition() + Description + Actiontaken + update_id + c_update_id + 1);
            if (con.isConnectingToInternet()) {
                getUploadResponse(obj);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public void getUploadResponse(final JSONObject obj) {

        Log.v("gandi", obj.toString());
        String tag_string_req = "req_response";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_UPDATE_COMMENTS, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            boolean error = response.getBoolean("Error");
                            if (!error) {

                                String responseMessage = response.getString("Message");
                                //    Toast.makeText(getActivity(), responseMessage, Toast.LENGTH_SHORT);
                                Log.v("bharadwaj", response.toString());
                                getFragmentManager().popBackStack();
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
                //con.serverErrorAlert();
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
}
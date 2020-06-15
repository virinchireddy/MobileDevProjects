package com.info.socialnetworking.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.BarOwnerLandingPage;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class FragmentBarOwnerLandingPage extends Fragment{
	
	TextView tvPatrons,tvCheckinPatrons,tvTotalCheckins,tvFavorites,tvEvent,tvPromotions;
	View rootView;
	ConnectionDetector con;
	Others others;
	LinearLayout liMainScreen;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(
				R.layout.fragment_bar_owner_landingpage, container, false);
		initiolize();
		loadData();
		return rootView;
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		tvPatrons=(TextView)rootView.findViewById(R.id.tvPatrons);
		tvCheckinPatrons=(TextView)rootView.findViewById(R.id.tvCheckinPatrons);
		tvTotalCheckins=(TextView)rootView.findViewById(R.id.tvTotalCheckins);
		tvFavorites=(TextView)rootView.findViewById(R.id.tvFavorites);
		tvEvent=(TextView)rootView.findViewById(R.id.tvEvent);
		tvPromotions=(TextView)rootView.findViewById(R.id.tvPromotions);
		liMainScreen=(LinearLayout)rootView.findViewById(R.id.liMainScreen);
		con = new ConnectionDetector(getActivity());
		others = new Others(getActivity());
	}
	private void loadData() {
		// TODO Auto-generated method stub
		try{
			JSONObject obj=new JSONObject();
			obj.put("user_id", BarOwnerLandingPage.user_id);
			getData(obj);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
	}
	private void getData(JSONObject dashbord) {

		String tag_string_req = "req_Notifications";
		others.showProgressWithOutMessage();

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_DASHBORD, dashbord,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						others.hideDialog();
						try {
							boolean error = response.getBoolean("error");
							if (!error) {
								liMainScreen.setVisibility(View.VISIBLE);
								loadData(response);

								
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
						con.serverErrorAlert();
					}
				});
		jsObjRequest.setShouldCache(false);
		jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);
	}

	protected void loadData(JSONObject response) {
		// TODO Auto-generated method stub
		try {
			JSONObject dashbord=response.getJSONObject("dashboard_values");
			Log.v("string",dashbord.toString());
			tvPatrons.setText(dashbord.getString("patrons_at_bar"));
			tvCheckinPatrons.setText(dashbord.getString("checkedin_patrons"));
			tvEvent.setText(dashbord.getString("events"));
			tvFavorites.setText(dashbord.getString("favorites"));
			tvPromotions.setText(dashbord.getString("promotions"));
			tvTotalCheckins.setText(dashbord.getString("total_checkins"));
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
	}
}

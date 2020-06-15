package com.info.socialnetworking.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.CurrentLocation;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.helper.SessionManager;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.MainMenu;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

public class FragmentBarHoppersMenu extends Fragment implements OnClickListener {

	LinearLayout liProfile, liMatchMeQuestions, liFeedback, liBeadsRoses,
			liSubscriptions, liHelp, liLogout, liBlockList, liPrivacy;
	SessionManager session;
	Others others;
	CurrentLocation location;
	ConnectionDetector con;
	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = inflater.inflate(R.layout.fragment_bar_hoppers_menu,
				container, false);
		((BarHopperHomeScreen) getActivity()).insideScreens();

		initiolize();

		return rootView;
	}

	private void initiolize() {
		// TODO Auto-generated method stub
		liProfile = (LinearLayout) rootView.findViewById(R.id.liProfile);
		liMatchMeQuestions = (LinearLayout) rootView
				.findViewById(R.id.liPrivateQuestions);
		// liFeedback = (LinearLayout) rootView.findViewById(R.id.liFeedback);
		// liBeadsRoses = (LinearLayout)
		// rootView.findViewById(R.id.liBeadsRoses);
		// liSubscriptions = (LinearLayout)
		// rootView.findViewById(R.id.liSubscriptions);
		liHelp = (LinearLayout) rootView.findViewById(R.id.liHelp);
		liLogout = (LinearLayout) rootView.findViewById(R.id.liLogout);
		liBlockList = (LinearLayout) rootView.findViewById(R.id.liBlockList);
		// liPrivacy = (LinearLayout) rootView.findViewById(R.id.liPrivacy);

		liProfile.setOnClickListener(this);
		liMatchMeQuestions.setOnClickListener(this);
		// liFeedback.setOnClickListener(this);
		// liBeadsRoses.setOnClickListener(this);
		// liSubscriptions.setOnClickListener(this);
		liHelp.setOnClickListener(this);
		liLogout.setOnClickListener(this);
		liBlockList.setOnClickListener(this);
		// liPrivacy.setOnClickListener(this);
		others = new Others(getActivity());
		con = new ConnectionDetector(getActivity());
		session = new SessionManager(getActivity());

	}

	@Override
	public void onClick(View v) {

		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.liProfile:
			Others.navigation = "menu";
			FragmentMyProfile activeUserProfile = new FragmentMyProfile();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, activeUserProfile)
					.addToBackStack(null).commit();

			break;
		case R.id.liPrivateQuestions:
			Others.navigation = "menu";
			PrivateQuestions privateQuestions = new PrivateQuestions();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, privateQuestions)
					.addToBackStack(null).commit();

			break;

		/*
		 * case R.id.liFeedback:
		 * 
		 * VisitedBars activeUserFeedback = new VisitedBars();
		 * this.getFragmentManager().beginTransaction()
		 * .replace(R.id.flContainer, activeUserFeedback)
		 * .addToBackStack(null).commit();
		 * 
		 * break; case R.id.liBeadsRoses:
		 * 
		 * FragmentBeadsAndRoses beadsAndRoses = new FragmentBeadsAndRoses();
		 * this.getFragmentManager().beginTransaction()
		 * .replace(R.id.flContainer, beadsAndRoses)
		 * .addToBackStack(null).commit();
		 * 
		 * break; case R.id.liSubscriptions:
		 * 
		 * FragmentUserSubscriptions userSubscription = new
		 * FragmentUserSubscriptions();
		 * this.getFragmentManager().beginTransaction()
		 * .replace(R.id.flContainer, userSubscription)
		 * .addToBackStack(null).commit();
		 * 
		 * break;
		 */
		case R.id.liHelp:
			Others.navigation = "menu";
			MenuHelp menuHelp = new MenuHelp();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, menuHelp).addToBackStack(null)
					.commit();

			break;
		case R.id.liBlockList:
			Others.navigation = "menu";
			BlockListPage blockList = new BlockListPage();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, blockList).addToBackStack(null)
					.commit();

			break;
		/*
		 * case R.id.liPrivacy:
		 * 
		 * FragmentPrivacy privacy = new FragmentPrivacy();
		 * this.getFragmentManager().beginTransaction()
		 * .replace(R.id.flContainer, privacy).addToBackStack(null) .commit();
		 * 
		 * break;
		 */

		case R.id.liLogout:
			if (con.isConnectingToInternet())
				LogoutUrl();
			else
				con.failureAlert();

			break;
		default:
			break;
		}
	}

	private void LogoutUrl() {
		// TODO Auto-generated method stub
		String tag_string_req = "req_Logout";
		others.showProgressWithOutMessage();
		JSONObject logOut = null;
		try {

			logOut = new JSONObject();
			logOut.put("user_id", BarHopperHomeScreen.user_id);

			JsonObjectRequest jsObjRequest = new JsonObjectRequest(
					Request.Method.POST, Config.URL_LOGOUT, logOut,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							others.hideDialog();
							try {
								boolean error = response.getBoolean("error");
								if (!error) {
									session.logoutUser();
									getActivity().finish();
								} else {
									if (response.getString("error_msg").equals(
											"User already logged out")) {
										session.logoutUser();
									} else {
										Log.v("logout",
												response.getString("error_msg"));
										others.ToastMessage(response
												.getString("error_msg"));
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Log.i("volley", "error: " + error);
							others.ToastMessage(error.getMessage());
							others.hideDialog();
						}
					});
			jsObjRequest.setShouldCache(false);
			// Adding request to request queue
			AppController.getInstance().addToRequestQueue(jsObjRequest,
					tag_string_req);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}

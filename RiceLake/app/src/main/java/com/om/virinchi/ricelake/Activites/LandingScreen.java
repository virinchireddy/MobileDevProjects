package com.om.virinchi.ricelake.Activites;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.om.virinchi.ricelake.Fragments.FragmentAccount;
import com.om.virinchi.ricelake.Fragments.FragmentHistory;
import com.om.virinchi.ricelake.Fragments.FragmentProjects;
import com.om.virinchi.ricelake.Helper.SessionManager;
import com.om.virinchi.ricelake.R;


public class LandingScreen extends Activity {
    public static ImageView ivBack, ivCamera;
    public static String authenticationToken, userId, password, userType, userName;
    ImageView ivdashboard, ivfacility, profile;
    TextView tvDashboard, tvfacility, profiletxt;
    LinearLayout liDash, liJobs, liprofile;
    CoordinatorLayout clCoordinateLayout;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        savedInstanceState = getIntent().getExtras();
        authenticationToken = savedInstanceState.getString("authenticationToken");
        userId = savedInstanceState.getString("userId");
        password = savedInstanceState.getString("password");
        userType = savedInstanceState.getString("userType");
        userName = savedInstanceState.getString("userName");

        setContentView(R.layout.activity_landingscreen);
        FragmentProjects dashboard = new FragmentProjects();
        this.getFragmentManager().beginTransaction()
                .replace(R.id.flContainer, dashboard).addToBackStack(null)
                .commit();
        intilize();

        ivdashboard
                .setImageDrawable(getResources().getDrawable(R.mipmap.d_c));
        tvDashboard.setTextColor(getResources().getColor(R.color.BackGround));
    }

    public void intilize() {
        liDash = (LinearLayout) findViewById(R.id.liDash);
        liJobs = (LinearLayout) findViewById(R.id.liJobs);
        ivdashboard = (ImageView) findViewById(R.id.ivdashboard);
        ivfacility = (ImageView) findViewById(R.id.ivfacility);
        tvDashboard = (TextView) findViewById(R.id.tvDashboard);
        tvfacility = (TextView) findViewById(R.id.tvfacility);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivCamera = (ImageView) findViewById(R.id.ivCamera);

        liprofile = (LinearLayout) findViewById(R.id.liProfile);
        profile = (ImageView) findViewById(R.id.ivprofile);
        profiletxt = (TextView) findViewById(R.id.tvProfile);

        clCoordinateLayout = (CoordinatorLayout) findViewById(R.id.clCoordinateLayout);
        session = new SessionManager(getApplicationContext());

    }

    public void liProfile(View v) {
        ivdashboard
                .setImageDrawable(getResources().getDrawable(R.mipmap.d_n));
        tvDashboard.setTextColor(getResources().getColor(R.color.grey));

        ivfacility.setImageDrawable(getResources().getDrawable(
                R.mipmap.facilities_n));
        tvfacility.setTextColor(getResources().getColor(R.color.grey));


        profile.setImageDrawable(getResources().getDrawable(R.mipmap.p_c));
        profiletxt.setTextColor(getResources().getColor(R.color.BackGround));

        FragmentAccount prof = new FragmentAccount();
        this.getFragmentManager().beginTransaction()
                .replace(R.id.flContainer, prof).addToBackStack(null)
                .commit();

    }

    public void liDash(View v) {
        ivdashboard
                .setImageDrawable(getResources().getDrawable(R.mipmap.d_c));
        tvDashboard.setTextColor(getResources().getColor(R.color.BackGround));

        profile.setImageDrawable(getResources().getDrawable(R.mipmap.p_n));
        profiletxt.setTextColor(getResources().getColor(R.color.grey));

        ivfacility.setImageDrawable(getResources().getDrawable(
                R.mipmap.facilities_n));
        tvfacility.setTextColor(getResources().getColor(R.color.grey));


        FragmentProjects dashboard = new FragmentProjects();
        this.getFragmentManager().beginTransaction()
                .replace(R.id.flContainer, dashboard).addToBackStack(null)
                .commit();
    }

    public void liJobs(View v) {
        ivdashboard
                .setImageDrawable(getResources().getDrawable(R.mipmap.d_n));
        tvDashboard.setTextColor(getResources().getColor(R.color.grey));


        profile.setImageDrawable(getResources().getDrawable(R.mipmap.p_n));
        profiletxt.setTextColor(getResources().getColor(R.color.grey));


        ivfacility.setImageDrawable(getResources().getDrawable(
                R.mipmap.facilities_c));
        tvfacility.setTextColor(getResources().getColor(R.color.BackGround));

        FragmentHistory projects = new FragmentHistory();
        this.getFragmentManager().beginTransaction()
                .replace(R.id.flContainer, projects).addToBackStack(null)
                .commit();

    }

    public void ivBack(View v) {

        getFragmentManager().popBackStack();


    }

    public void onBackPressed() {
        Snackbar snackbar = Snackbar
                .make(clCoordinateLayout, "You want to exit the app", Snackbar.LENGTH_LONG)
                .setAction("Yes", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), LoginScreen.class);
                        session.logoutUser();
                        startActivity(i);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }
}
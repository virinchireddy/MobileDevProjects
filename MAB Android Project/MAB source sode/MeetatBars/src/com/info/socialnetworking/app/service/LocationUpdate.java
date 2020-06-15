package com.info.socialnetworking.app.service;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationUpdate implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener {

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

	private Location mLastLocation;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	// boolean flag to toggle periodic location updates
	private boolean mRequestingLocationUpdates = false;

	private LocationRequest mLocationRequest;

	// Location updates intervals in sec
	private static int UPDATE_INTERVAL = 10000; // 10 sec
	private static int FATEST_INTERVAL = 5000; // 5 sec
	private static int DISPLACEMENT = 10; // 10 meters

	Activity activity;
	
	public LocationUpdate(Activity activity) {
		// TODO Auto-generated constructor stub
		// First we need to check availability of play services
		this.activity=activity;
		if (checkPlayServices()) {

			// Building the GoogleApi client
			buildGoogleApiClient();
			
			createLocationRequest();

			
		}
		//onStart();
		//onResume();
		
	
		
	}
	
	public void periodicLocationUpdates() {
		
		togglePeriodicLocationUpdates();
	}

	public void onStart() {
		
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
		
	}
	public void onResume() {
		checkPlayServices();

		// Resuming the periodic location updates
		if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
			startLocationUpdates();
			togglePeriodicLocationUpdates();
		}
		
	}
	
	public void onStop() {
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
		
	}
	public void onPuse() {
		stopLocationUpdates();
	}
	
	/**
	 * Method to display the location on UI
	 * */
	private void displayLocation() {

		mLastLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);

		if (mLastLocation != null) {
			double latitude = mLastLocation.getLatitude();
			double longitude = mLastLocation.getLongitude();
			Toast.makeText(activity, latitude+"/"+longitude,
					Toast.LENGTH_SHORT).show();

		} else {

			// error getting location
		}
	}
	
	
	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		mLastLocation = location;

		Toast.makeText(activity, "Location changed!",
				Toast.LENGTH_SHORT).show();

		// Displaying the new location on UI
		displayLocation();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

		Log.v("failure", "Connection failed: ConnectionResult.getErrorCode() = "
				+ result.getErrorCode());
		
	}

	@Override
	public void onConnected(@Nullable Bundle arg0) {
		// TODO Auto-generated method stub
		displayLocation();

		if (mRequestingLocationUpdates) {
			startLocationUpdates();
		}
		
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();

	}

	/**
	 * Method to verify google play services on the device
	 * */
	@SuppressWarnings("deprecation")
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(activity);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(activity.getApplicationContext(),
						"This device is not supported.", Toast.LENGTH_LONG).show();
				//activity.finish();
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Creating google api client object
	 * */
	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(activity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}
	
	private void togglePeriodicLocationUpdates() {
		if (!mRequestingLocationUpdates) {

			mRequestingLocationUpdates = true;

			// Starting the location updates
			startLocationUpdates();


		} else {
			// Changing the button text
			

			mRequestingLocationUpdates = false;

			// Stopping the location updates
			stopLocationUpdates();

			//Log.d(TAG, "Periodic location updates stopped!");
		}
	}
	
	/**
	 * Starting the location updates
	 * */
	protected void startLocationUpdates() {

		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, mLocationRequest, this);

	}

	/**
	 * Stopping location updates
	 */
	protected void stopLocationUpdates() {
		LocationServices.FusedLocationApi.removeLocationUpdates(
				mGoogleApiClient, this);
	}
	
	
	/**
	 * Creating location request object
	 * */
	protected void createLocationRequest() {
		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FATEST_INTERVAL);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
	}
	
	

}

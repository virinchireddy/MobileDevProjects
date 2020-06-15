package com.info.socialnetworking.app.service;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.interfaces.FusedLocationReceiver;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

public class FusedLocationService implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "FusedLocationService";

    private static final long INTERVAL = 1000 * 20;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    private static final long ONE_MIN = 1000 * 60;
    private static final long REFRESH_TIME = ONE_MIN * 5;
    private static final float MINIMUM_ACCURACY = 50.0f;
    Activity locationActivity;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private Location location;
    private FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;

    FusedLocationReceiver locationReceiver = null;

    public FusedLocationService(Activity locationActivity, FusedLocationReceiver fusedLocationReceiver) {
        this.locationReceiver = fusedLocationReceiver;
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        this.locationActivity = locationActivity;

        googleApiClient = new GoogleApiClient.Builder(locationActivity)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "I'm connected now");
        Location currentLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);
        if (currentLocation != null && currentLocation.getTime() > REFRESH_TIME) {
            location = currentLocation;
			Toast.makeText(locationActivity, "location created", Toast.LENGTH_SHORT).show();
        } else {
            fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            // Schedule a Thread to unregister location listeners
            Executors.newScheduledThreadPool(1).schedule(new Runnable() {
                @Override
                public void run() {
                    fusedLocationProviderApi.removeLocationUpdates(googleApiClient,
                            FusedLocationService.this);
                }
            }, ONE_MIN, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "Location is changed!");
		Toast.makeText(locationActivity, "location changed", Toast.LENGTH_SHORT).show();

        	//if the existing location is empty or
        	//the current location accuracy is greater than existing accuracy
        	//then store the current location
        
        if (null == this.location || location.getAccuracy() < this.location.getAccuracy()) {
            this.location = location;
            // let's inform my client class through the receiver
            locationReceiver.onLocationChanged();
            sendLocation();
            
            //if the accuracy is not better, remove all location updates for this listener
            if (this.location.getAccuracy() < MINIMUM_ACCURACY) {
                fusedLocationProviderApi.removeLocationUpdates(googleApiClient, this);
            }
        }
    }

    private void sendLocation() {
		// TODO Auto-generated method stub
    	double latitude = location.getLatitude();
    	double longitude = location.getLongitude();
    	
    	//Log.v("user_id", user_id+"");
    	try {
			
    		JSONObject locationObj=new JSONObject();
        	locationObj.put("user_id", BarHopperHomeScreen.user_id);
        	locationObj.put("latitude", latitude);
        	locationObj.put("longitude", longitude);
        	sendLocation(locationObj);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

public void sendLocation(final JSONObject location) {
		
		String tag_string_req = "req_sendLocation";

		JsonObjectRequest jsObjRequest = new JsonObjectRequest(
				Request.Method.POST, Config.URL_LOCATION_UPDATE,
				location, new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.v("hello", "I am at Responce block");
						try {
							boolean error = response.getBoolean("error");
							if (!error) {

								Toast.makeText(locationActivity, "location sent", Toast.LENGTH_SHORT).show();

							} else {

								String error_msg = response
										.getString("error_msg");
								Log.v("hello", error_msg.toString());
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.i("volley", "error: " + error);

					}
				});
		jsObjRequest.setShouldCache(false);
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(jsObjRequest,
				tag_string_req);
		
	}
    
	public Location getLocation() {
        return this.location;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
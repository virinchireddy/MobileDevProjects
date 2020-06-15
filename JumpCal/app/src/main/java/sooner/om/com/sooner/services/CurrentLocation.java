package sooner.om.com.sooner.services;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import sooner.om.com.sooner.LoginScreen;
import sooner.om.com.sooner.PatientLandingScreen;
import sooner.om.com.sooner.app.AppController;
import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.helper.Others;
import sooner.om.com.sooner.network.ConnectionDetector;


//this class can use the current location from "gps" and get the current "latitude" and "longitude" .
public class CurrentLocation extends Service implements LocationListener {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 20; // 5 seconds
    static FusedLocationReceiver locationReceiver = null;
    private final Context mContext;
    // status of Location dialog box
    public boolean dialog_status = true;
    // Declaring a Location Manager
    protected LocationManager locationManager;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    ConnectionDetector con;

     public CurrentLocation(Context context, FusedLocationReceiver loctionReceier) {
        CurrentLocation.locationReceiver = loctionReceier;
        this.mContext = context;
        con = new ConnectionDetector(context);
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled) {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    if (Others.checkPermission((Activity) mContext,
                            "android.permission.ACCESS_FINE_LOCATION")) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            if (Others.checkPermission((Activity) mContext,
                    "android.permission.ACCESS_FINE_LOCATION")) {
                locationManager.removeUpdates(CurrentLocation.this);
            }
        }
    }

    /**
     * Function to get latitude
     */
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    // this will send the request to server for location update
    public void updateLocation(String user_id, String authToken) {

        if (canGetLocation()) {

            double latitude = getLatitude();
            double longitude = getLongitude();

            // adding required parameters to the json object
            try {

                JSONObject locationObj = new JSONObject();
                locationObj.put("_AuthenticationToken", authToken);
                locationObj.put("_UserId", user_id);
                locationObj.put("_latitude", latitude);
                locationObj.put("_longitude", longitude);
                Log.v("inLocation", locationObj.toString());
                if (con.isConnectingToInternet())
                    sendLocation(locationObj);
            } catch (Exception e) {
                // TODO: handle exception
            }
        } else {
            if (!LoginScreen.isLocationDisabled)
                showSettingsAlert();

        }
    }

    // updating location in server
    public void sendLocation(JSONObject location) {

        String tag_string_req = "req_sendLocation";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_POST_LOCATION,
                location, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("hello", "I am at Responce block");
                try {
                    boolean error = response.getBoolean("Error");
                    if (error) {

                        String error_msg = response
                                .getString("error_msg");
                        Log.v("hello", error_msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        jsObjRequest.setShouldCache(false);
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest,
                tag_string_req);

    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    @SuppressWarnings("deprecation")
    public boolean showSettingsAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is disabled");

        // Setting Dialog Message
        alertDialog.setMessage(Html.fromHtml("JumpCal needs to use location to provide you the best appointment available.<br><br>Enable location settings?"));

        // On pressing Settings button
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                        dialog_status = true;
                    }
                });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.cancel();
                LoginScreen.isLocationDisabled = true;
            }
        });

        // Showing Alert Message
        alertDialog.show();

        return dialog_status;
    }

    @Override
    public void onLocationChanged(Location location) {
        //Toast.makeText(mContext.getApplicationContext(), "Location changed", Toast.LENGTH_SHORT).show();
        updateLocation(PatientLandingScreen.userId, PatientLandingScreen.authenticationToken);
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
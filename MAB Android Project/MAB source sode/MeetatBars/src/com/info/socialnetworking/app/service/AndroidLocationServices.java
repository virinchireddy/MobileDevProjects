package com.info.socialnetworking.app.service;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.widget.Toast;

public class AndroidLocationServices extends Service {

WakeLock wakeLock;
Context _context;

private LocationManager locationManager;

public AndroidLocationServices() {
    // TODO Auto-generated constructor stub
	//_context=context;
}

@Override
public IBinder onBind(Intent arg0) {
    // TODO Auto-generated method stub
    return null;
}

@Override
public void onCreate() {
    // TODO Auto-generated method stub
    super.onCreate();

    PowerManager pm = (PowerManager) getSystemService(this.POWER_SERVICE);

    wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "DoNotSleep");

    // Toast.makeText(getApplicationContext(), "Service Created",
    // Toast.LENGTH_SHORT).show();

    Log.e("Google", "Service Created");

}

@Override
@Deprecated
public void onStart(Intent intent, int startId) {
    // TODO Auto-generated method stub
    super.onStart(intent, startId);

    Log.e("Google", "Service Started");

    
    locationManager = (LocationManager) getApplicationContext()
            .getSystemService(Context.LOCATION_SERVICE);

    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            5000, 5, listener);

}

private LocationListener listener = new LocationListener() {

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

        Log.e("Google", "Location Changed");

        if (location == null)
            return;

        	JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();

            try {
                Log.e("latitude", location.getLatitude() + "");
                Log.e("longitude", location.getLongitude() + "");

                jsonObject.put("latitude", location.getLatitude());
                jsonObject.put("longitude", location.getLongitude());

                jsonArray.put(jsonObject);
                Toast.makeText(getApplicationContext(), jsonArray.toString(), Toast.LENGTH_SHORT).show();
                Log.v("request", jsonArray.toString());

               /* LocationWebService location_obj=new LocationWebService(_context);
                location_obj.SendLocation(jsonObject);*/
                
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
};

@Override
public void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();

    wakeLock.release();

}
}

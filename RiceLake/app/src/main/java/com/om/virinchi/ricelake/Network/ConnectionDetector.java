package com.om.virinchi.ricelake.Network;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

@SuppressWarnings("deprecation")
public class ConnectionDetector {
    private Context _context;
     
    public ConnectionDetector(Context context){
        this._context = context;
    }
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
          if (connectivity != null) 
          {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null) 
                  for (int i = 0; i < info.length; i++) 
                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
                      {
                          return true;
                      }
          }
          return false;
    }
    
    public void failureAlert() {
		
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);
    	 
        // Setting Dialog Title
        alertDialog.setTitle("No Internet Connection");
 
        // Setting Dialog Message
        alertDialog.setMessage("You don't have internet connection.");
         
        // Setting alert dialog icon

 
        // Setting OK Button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
				Intent i=new Intent(Settings.ACTION_SETTINGS);
				_context.startActivity(i);
				
			}
		});
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
				//((Activity)_context).finish();
			}
		});
        // Showing Alert Message
        AlertDialog alert = alertDialog.create();
        alert.show();
	}
    public void serverErrorAlert() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);
   	 
        // Setting Dialog Title
        alertDialog.setTitle("Something Went Wrong");
 
        // Setting Dialog Message
        alertDialog.setMessage("Sorry for inconvenience, Please try again later. ");
 
        // Setting OK Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
				
			}
		});
        AlertDialog alert = alertDialog.create();
        alert.show();
    }
}
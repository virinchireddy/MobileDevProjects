package com.info.socialnetworking.app.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ServerError {

	private Context _context;
	
	public ServerError(Context context)
	{
		 this._context = context;
	}
	
	public void serverErrorAlert() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);
   	 
        // Setting Dialog Title
        alertDialog.setTitle("OOPS!");
 
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
	}
}

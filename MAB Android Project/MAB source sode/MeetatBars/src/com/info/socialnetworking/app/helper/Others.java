package com.info.socialnetworking.app.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.meetatbars.R;

@SuppressLint("SimpleDateFormat") @SuppressWarnings("deprecation")
public class Others {

	ProgressDialog pDialog;
	static Context _context;
	public static boolean isCheckedIn=false;
	public static String NavigationDefaultValue=-1+"";
	public static String navigation=NavigationDefaultValue;
	


	public Others(Context context)
	{
		_context=context;
		
		
	}
	
	public void showDialog() {
		pDialog = new ProgressDialog(_context);
		pDialog.setCancelable(false);
		if (!pDialog.isShowing())
			pDialog.show();
	}

	public void setMessage(String message) {
		
		pDialog.setMessage(message);
	}
	
	// Hiding the dialog box
	public void hideDialog() {
		if (pDialog.isShowing())
			pDialog.dismiss();
	}
	public boolean getCheckInStatus()
	{
		return Others.isCheckedIn;
	}
	public void setCheckInStatus(boolean status)
	{
		Others.isCheckedIn=status;
	}
	
	public static void hideKeyBoad(Activity activity) {
		View view = activity.getCurrentFocus();
		if (view != null) {  
		    InputMethodManager imm = (InputMethodManager)_context.getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
	
	public void ToastMessage(String message) {
		Toast toast = Toast.makeText(_context, message,
				Toast.LENGTH_SHORT);
		View view = toast.getView();
		view.setBackgroundDrawable(_context.getResources().getDrawable(
				R.drawable.toast_message_background));
		toast.setGravity(Gravity.CENTER, 0, 0);
		// TextView text = (TextView) view.findViewById(android.R.id.message);
		// text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
		toast.show();
	}
	
	public void ToastMessageLong(String message) {
		Toast toast = Toast.makeText(_context, message,
				Toast.LENGTH_LONG);
		View view = toast.getView();
		view.setBackgroundDrawable(_context.getResources().getDrawable(
				R.drawable.toast_message_background));
		toast.setGravity(Gravity.CENTER, 0, 0);
		// TextView text = (TextView) view.findViewById(android.R.id.message);
		// text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
		toast.show();

	}
	
	public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
                dialog.show();
        } catch (BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progressdialog);
        // dialog.setMessage(Message);
        return dialog;
}
	
	public void showProgressWithOutMessage()
	{
		  if (pDialog == null) {
			  pDialog = createProgressDialog(_context);
			  pDialog.show();
		       } else {
		    	   pDialog.show();
		       }
		
	}
	
	public static void loadImages(String urlThumbnail, ImageView ivProfilePhoto) {

		ImageLoader imageLoader = AppController.getInstance().getImageLoader();
		// If you are using normal ImageView
		imageLoader.get(urlThumbnail, ImageLoader.getImageListener(
                ivProfilePhoto, R.drawable.image_loading, R.drawable.image_notavilable));
		
    }
	
	public static void setImageAsBackground(String urlThumbnail,final ImageView ivProfilePhoto ) {
		
		ImageLoader imageLoader = AppController.getInstance().getImageLoader();
		 Log.v("image url", urlThumbnail);
		// If you are using normal ImageView
		imageLoader.get(urlThumbnail, new ImageListener() {
		 
		   	@Override
			public void onErrorResponse(VolleyError arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onResponse(ImageContainer response, boolean arg1) {
				// TODO Auto-generated method stub
				if (response.getBitmap() != null) {
		            // load image into imageview
					BitmapDrawable ob=new BitmapDrawable(_context.getResources(), response.getBitmap());
					ivProfilePhoto.setBackgroundDrawable(ob);
		        }
			}
		});
		
	}
	
	public static String getTimeInCST(){
		
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sourceFormat.setTimeZone(TimeZone.getDefault());
		Date parsed = null;
		try {
			parsed = sourceFormat.parse(sourceFormat.format(Calendar.getInstance().getTime()));
			Log.v("present time",sourceFormat.format(Calendar.getInstance().getTime()));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // => Date is in UTC now

		TimeZone tz = TimeZone.getTimeZone("GMT-5");
		SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		destFormat.setTimeZone(tz);

		String timeStamp = destFormat.format(parsed);
		
		Log.v("us/central time",timeStamp);
		
		return timeStamp;
	}
	
	public static String cstToLocal(String timeStamp){
		
		SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
		Date parsed = null;
		try {
			parsed = sourceFormat.parse(timeStamp);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // => Date is in UTC now

		TimeZone tz = TimeZone.getDefault();
		SimpleDateFormat destFormat = new SimpleDateFormat("dd-MMM HH:mm");
		destFormat.setTimeZone(tz);

		String result = destFormat.format(parsed);
		
	    
	    return result;
		
		
	}
	
	@SuppressLint("DefaultLocale") 
	public static String firstLetterCapital(String text){
		String st;
		char c = text.charAt(0);
		if(Character.isLetter(c))
		{
			st=text.substring(0,1).toUpperCase() + text.substring(1);
		}
		else
		{
			st=text;
		}
		return 	st;
	}
	
	//to remove unwanted paragraph symbol coming from promotions feeder from website promotions update features
	public static String removeParagraphSymbols(String paragraphString)
	{
		String normalString1=paragraphString.replace("<p>","");
		String normalString2=normalString1.replace("</p>",""); 
		return normalString2;
		
	}
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}
	
}

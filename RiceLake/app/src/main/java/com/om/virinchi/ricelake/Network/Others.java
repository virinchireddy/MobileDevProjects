package com.om.virinchi.ricelake.Network;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.ActivityCompat;
import android.test.mock.MockPackageManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.BadTokenException;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.om.virinchi.ricelake.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


@SuppressWarnings("deprecation")
public class Others {

    private static final int REQUEST_CODE_PERMISSION = 2;
    public static String Oldpassword;
    public static boolean isCheckedIn = false;
    public static String NavigationDefaultValue = -1 + "";
    public static String navigation = NavigationDefaultValue;
    public static Activity activity = null;
    public static boolean gcm_navigation = false;
    static Context _context;
    ProgressDialog pDialog;

    public Others(Context context) {
        _context = context;

    }

    public static void hideKeyBoad(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) _context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progressdialog);
        // dialog.setMessage(Message);
        return dialog;
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertTimeZoneToLocal(String time) {
        Log.v("othres", time);
        SimpleDateFormat sourceFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss");
        sourceFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parsed = null;
        try {
            parsed = sourceFormat.parse(time);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // => Date is in UTC now

        TimeZone tz = TimeZone.getDefault();
        SimpleDateFormat destFormat = new SimpleDateFormat(
                "MM/dd/yyyy HH:mm:ss");
        destFormat.setTimeZone(tz);

        String result = destFormat.format(parsed);
        Log.v("othres ctime", result);

        return result;

    }

    //permission for accessing camera in this application
    public static boolean checkPermission(Activity activity, String permission) {

        // String permission = "android.permission.CAMERA";
        int res = activity.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    // permissions for marshmallow
    public static void permissionCheckMarshMallow(Activity activity,
                                                  String permission) {
        // TODO Auto-generated method stub
        try {
            if (ActivityCompat.checkSelfPermission(activity, permission) != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(activity,
                        new String[]{permission}, REQUEST_CODE_PERMISSION);
                // Log.v("Others","permission check marsh mallow");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public boolean getCheckInStatus() {
        return Others.isCheckedIn;
    }

    public void setCheckInStatus(boolean status) {
        Others.isCheckedIn = status;
    }

    public void ToastMessage(String message) {
        Toast toast = Toast.makeText(_context, message, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.setBackgroundDrawable(_context.getResources().getDrawable(
                com.om.virinchi.ricelake.R.drawable.toast_message_background));
        toast.setGravity(Gravity.CENTER, 0, 0);
        // TextView text = (TextView) view.findViewById(android.R.id.message);
        // text.setShadowLayer(0, 0, 0, Color.TRANSPARENT);
        toast.show();

    }

    public void showProgressWithOutMessage() {
        if (pDialog == null) {
            pDialog = createProgressDialog(_context);
            pDialog.show();
        } else {
            pDialog.show();
        }

    }
    public static void loadImages(String urlThumbnail, ImageView ivUploadimage) {

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        // If you are using normal ImageView
        imageLoader.get(urlThumbnail, ImageLoader.getImageListener(
                ivUploadimage, R.mipmap.image_loading,
                R.mipmap.image_notavilable));

    }
    public static void setImageAsBackground(String urlThumbnail,final ImageView ivProfilePhoto ) {

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        Log.v("image url", urlThumbnail);
        // If you are using normal ImageView
        imageLoader.get(urlThumbnail, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                // TODO Auto-generated method stub
                if (response.getBitmap() != null) {
                    // load image into imageview
                    BitmapDrawable ob=new BitmapDrawable(_context.getResources(), response.getBitmap());
                    ivProfilePhoto.setImageDrawable(ob);
                }
            }
        });

    }
}

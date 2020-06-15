package sooner.om.com.sooner.helper;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import sooner.om.com.sooner.R;
import sooner.om.com.sooner.app.AppController;

//this class contains many useful methods which we use in many parts of the code.
//by this the code reusability increases.
@SuppressWarnings("deprecation")
public class Others {

    private static final int REQUEST_CODE_PERMISSION = 2;
    public static boolean isInsuranceUploaded = false;
    public static String navigationDashbord = "dashbord";
    private Context _context;
    private ProgressDialog pDialog;


    public Others(Context context) {
        _context = context;
    }

    //progress dialog
    private static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        try {
            dialog.show();
            dialog.setCancelable(false);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.progressdialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // dialog.setMessage(Message);
        return dialog;
    }

    //this method accepts "url of a image" and loads the image .
    public static void loadImages(String urlThumbnail, ImageView ivUploadimage) {

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        // If you are using normal ImageView
        imageLoader.get(urlThumbnail, ImageLoader.getImageListener(
                ivUploadimage, R.drawable.image_loading,
                R.drawable.image_notavilable));

    }

    public static void displayBackGround(View view, int id, Context context) {
        view.setBackgroundDrawable(context.getResources().getDrawable(id));
    }

    public static void displayImageDrawable(ImageView view, int id, Context context) {
        view.setImageDrawable(context.getResources().getDrawable(id));
    }

    public static void displayBackGroundColor(View view, int id, Context context) {
        view.setBackgroundColor(context.getResources().getColor(id));
    }

    public static void displayTextColor(TextView view, int id, Context context) {
        view.setTextColor(context.getResources().getColor(id));
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

    //used for returning  boolean value for a condition regarding "start time" and "end time".
    public static boolean checkTimeLiesBetween(String start, String end,
                                               String check) {
        try {
            Date time1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .parse(start);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            Date time2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(end);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);

            Date d = new SimpleDateFormat("MMM dd, yyyy' at 'hh:mm a")
                    .parse(check);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                // checkes whether the current time is between 14:49:00 and
                // 20:11:13.
                // System.out.println(true);
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    // takes JSON object and checks weather the "key" exists or not. if it exists ,returns the data with the name of the "key".
    public static String optString(JSONObject json, String key) {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key) || json.optString(key).equals("null"))
            return "NA";
        else
            return json.optString(key);
    }

    //this method returns display the date in "MMM-dd, yyyy' at 'hh:mm a" format
    @SuppressLint("SimpleDateFormat")
    public static String AppointmentDataAndTime(String time) {
        // TODO Auto-generated method stub
        String newDate = null;
        try {
            String format = "MM/dd/yyyy HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date cDate = sdf.parse(time);
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "EEE, MMM dd, yyyy' at 'hh:mm a");
            newDate = formatter.format(cDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    //this method converts given time to hh:mm:am/pm format.
    @SuppressLint("SimpleDateFormat")
    public static String timeTo12Hours(String time) {

        try {
            // //log.v("time",time);
            String format = "HH:mm:ss";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date cDate = sdf.parse(time);
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
            time = formatter.format(cDate);
            // //log.v("converted time",time);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String changeDateFormat(String date) {
        try {
            // //log.v("time",time);
            String format = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date cDate = sdf.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd, yyyy");
            date = formatter.format(cDate);
            // //log.v("converted time",time);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String changeDateFormatDashBoard(String date) {
        try {
            // //log.v("time",time);
            String format = "MM/dd/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            Date cDate = sdf.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd, yyyy");
            date = formatter.format(cDate);
            // //log.v("converted time",time);
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //set text for a dialog
    public void setMessage(String message) {
        pDialog.setMessage(message);
    }

    // Hiding the dialog box
    public void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //to setup a short length toast message
    public void ToastMessage(String message) {
        Toast toast = Toast.makeText(_context, message, Toast.LENGTH_SHORT);
        View view = toast.getView();

        displayBackGround(view, R.drawable.toast_message_background, _context);
        toast.setGravity(Gravity.CENTER, 0, 0);

        toast.show();

    }

    // to just show the progress
    public void showProgressWithOutMessage() {
        if (pDialog == null) {
            pDialog = createProgressDialog(_context);
            pDialog.show();
        } else {
            pDialog.show();
        }

    }

    public void showProgressWithOutMessage(Context _context) {
        if (pDialog == null) {
            pDialog = createProgressDialog(_context);
            pDialog.show();
        } else {
            pDialog.show();
        }

    }
}

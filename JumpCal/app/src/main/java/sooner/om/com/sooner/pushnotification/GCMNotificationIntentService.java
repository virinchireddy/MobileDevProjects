package sooner.om.com.sooner.pushnotification;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import sooner.om.com.sooner.AlertDialogue;
import sooner.om.com.sooner.AlertsScreen;
import sooner.om.com.sooner.LoginScreen;
import sooner.om.com.sooner.PatientLandingScreen;
import sooner.om.com.sooner.R;
import sooner.om.com.sooner.fragment.FragmentDashboard;

//this class will execute after receiving push notification 
public class GCMNotificationIntentService extends IntentService {


    public static String facilityid, appointmentid, alert, alertType, cancelNavigation = null;
    String jasonData;
    private NotificationManager mNotificationManager;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    // this call will handle the intent data
    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        jasonData = extras.getString("message");

        Log.v("appointment", jasonData + "");
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
        // if the gcm data is not empty
        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {

                identifyNavigation(jasonData);

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {

                identifyNavigation(jasonData);

            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                identifyNavigation(jasonData);
                //Log.i(TAG, extras.toString());
                System.out.print(extras.toString());
            }
        }
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    // identify the notification type
    public void identifyNavigation(String data) {

        try {
            JSONObject jsonObj = new JSONObject(data + "");
            // identify the alert type
            if (jsonObj.has("alertType")) {
                String alertType = jsonObj.getString("alertType");
                GCMNotificationIntentService.alertType = "refreshDashboard";
                // if alert type is book appointment then this will execute
                switch (alertType) {
                    case "Book Appointment": {
                        facilityid = jsonObj.getString("facilityId");
                        JSONObject jb = jsonObj.getJSONObject("aps");
                        alert = jb.getString("alert");
                        sendNotificationDashBoard("" + alert, facilityid);

                        break;
                    }
                    case "Cancel Appointment": {
                        facilityid = jsonObj.getString("facilityId");
                        JSONObject jb = jsonObj.getJSONObject("aps");
                        alert = jb.getString("alert");
                        sendNotificationDashBoard("" + alert, facilityid);
                        break;
                    }
                    case "Dynamic Timer":
                        silentNotification();
                        break;
                }

            } // if it is alert type is white slot then this will run
            else {
                alertType = "notification";
                facilityid = jsonObj.getString("facilityId");
                appointmentid = jsonObj.getString("appointmentId");
                JSONObject jb = jsonObj.getJSONObject("aps");
                alert = jb.getString("alert");
                sendNotification("" + alert, facilityid, appointmentid);
            }

        } catch (JSONException e) { // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // silent notification is used to refresh the dashboard if the timer is increased by physician
    private void silentNotification() {
        // TODO Auto-generated method stub
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        ActivityManager am = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        // if the app is opened and the user is in dashboard screen then this will execute
        if (taskInfo.get(0).topActivity.getPackageName()
                .equalsIgnoreCase(this.getPackageName())) {
            cancelNavigation = taskInfo.get(0).topActivity.getClassName();
            Fragment f = PatientLandingScreen.activity.getFragmentManager()
                    .findFragmentById(R.id.flContainer);
            if (f instanceof FragmentDashboard) {
                Log.v("hello", "am here");
                Intent i = new Intent(getApplicationContext(),
                        LoginScreen.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
                PatientLandingScreen.activity.finish();
            }
        }
    }

    // if the app is opened this will run
    public void sendNotificationDashBoard(String msg, String facilityid) {
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        ActivityManager am = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        // check weather the app is opened
        if (!taskInfo.get(0).topActivity.getPackageName()
                .equalsIgnoreCase(this.getPackageName())) {

            mNotificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Intent in = new Intent(this, LoginScreen.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            in.putExtra("facilityId", facilityid);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this)
                    .setContentTitle("JumpCal")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentText(
                            msg.substring(0, msg.length() - 1).replace("\\n", "\n"))
                    .setSound(
                            RingtoneManager
                                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            // notification icon for above lollipop and pre lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.drawable.logo);
            } else {
                mBuilder.setSmallIcon(R.drawable.logo);
            }
            // intent will be triggered if the notification is clicked
            PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), in, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());

            //Log.d(TAG, "Notification sent successfully.");
        } else {
            cancelNavigation = taskInfo.get(0).topActivity.getClassName();
            Fragment f = PatientLandingScreen.activity.getFragmentManager()
                    .findFragmentById(R.id.flContainer);
            // identifying the cancel navigation if it is in dashboard then reload the screen else dismiss the dialog
            if (f instanceof FragmentDashboard) {
                cancelNavigation = "FragmentDashboard";
            } else {
                cancelNavigation = "OtherScreen";
            }
            startActivity(new Intent(this, AlertDialogue.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    // if the app is closed this block will run
    private void sendNotification(String msg, String facilityid,
                                  String appointmentID) {
        //Log.d(TAG, "Preparing to send notification...: " + msg);

        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        ActivityManager am = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        if (!taskInfo.get(0).topActivity.getPackageName()
                .equalsIgnoreCase(this.getPackageName())) {

            mNotificationManager = (NotificationManager) this
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Intent in = new Intent(this, AlertsScreen.class);
            in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            in.putExtra("nav_type", "gcm");
            in.putExtra("facilityId", facilityid);
            in.putExtra("appointmentId", appointmentID);
            Log.v("notification data", facilityid + "/" + appointmentID);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    this)
                    .setContentTitle("JumpCal")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentText(
                            msg.substring(0, msg.length() - 1).replace("\\n", "\n"))
                    .setSound(
                            RingtoneManager
                                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBuilder.setSmallIcon(R.drawable.logo);
            } else {
                mBuilder.setSmallIcon(R.drawable.logo);
            }

            PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), in, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(contentIntent);
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());

            //Log.d(TAG, "Notification sent successfully.");
        } else {
            startActivity(new Intent(this, AlertDialogue.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

}

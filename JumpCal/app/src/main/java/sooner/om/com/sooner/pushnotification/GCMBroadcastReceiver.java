package sooner.om.com.sooner.pushnotification;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

//this class helps to wake the screen of device when a PusNotification is received by my device. 
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
    // on receiving notification from GCM server
    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName comp = new ComponentName(context.getPackageName(),
                GCMNotificationIntentService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
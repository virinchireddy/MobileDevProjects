package in.spoors.effort1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class BootCompletedReceiver extends BroadcastReceiver {
	private static final String TAG = "BootCompletedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Boot completed. Starting Splash Service.");
		}

		// Utils.detectImsiChangeAndSave(context, "boot");
		Utils.flushLocations(context.getApplicationContext());
		WakefulIntentService.sendWakefulWork(context, SplashService.class);
	}

}

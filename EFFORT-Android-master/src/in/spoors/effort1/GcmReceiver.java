package in.spoors.effort1;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmReceiver extends WakefulBroadcastReceiver {

	private static final String TAG = "GcmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Received a push message from GCM.");
		}

		Utils.sync(context.getApplicationContext());
	}

}

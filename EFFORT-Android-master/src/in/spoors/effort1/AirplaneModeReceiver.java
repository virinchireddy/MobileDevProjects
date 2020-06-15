package in.spoors.effort1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AirplaneModeReceiver extends BroadcastReceiver {
	private static final String TAG = "AirplaneModeReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Change in airplane mode.");
		}

		Utils.flushLocations(context.getApplicationContext());
	}

}

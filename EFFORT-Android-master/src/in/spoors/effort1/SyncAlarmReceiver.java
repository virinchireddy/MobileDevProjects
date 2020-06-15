package in.spoors.effort1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SyncAlarmReceiver extends BroadcastReceiver {
	static final String TAG = "SyncAlarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		EffortApplication.log(TAG, "onReceive");
		Utils.sync(context.getApplicationContext());
	}

}

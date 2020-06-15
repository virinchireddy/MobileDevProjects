package in.spoors.effort1;

import in.spoors.effort1.dao.SimCardChangeSmsDao;
import in.spoors.effort1.dto.SimChangeMessage;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SMSTheftReceiver extends BroadcastReceiver {

	String TAG = "Utils";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (BuildConfig.DEBUG) {
			Log.v(TAG, "Theft receiver");
			Log.v(TAG, "action " + intent.getAction());
		}

		if (intent != null) {
			// -1 : Successfully sent
			int resultCode = getResultCode();

			if (resultCode == Activity.RESULT_OK) {
				if (BuildConfig.DEBUG) {
					Log.v(TAG, "resultCode " + resultCode);
				}

				if (intent.getExtras().containsKey("message")) {
					SimChangeMessage simChangeMessage = (SimChangeMessage) intent
							.getSerializableExtra("message");
					if (simChangeMessage != null) {
						SimCardChangeSmsDao simCardChangeSmsDao = SimCardChangeSmsDao
								.getInstance(context);
						simCardChangeSmsDao
								.deleteSentSmsRecord(simChangeMessage);
					}
				}
			}
		}
	}

}

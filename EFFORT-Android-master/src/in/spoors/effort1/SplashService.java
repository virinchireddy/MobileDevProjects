package in.spoors.effort1;

import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class SplashService extends WakefulIntentService {
	public static final String TAG = SplashService.class.getSimpleName();

	public SplashService() {
		super(TAG);
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		try {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In onHandleIntent.");
				Log.d(TAG, "Restarting tracking alarm.");
			}
			EffortApplication.restartTrackingAlarm();

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Restarting sync alarm.");
			}

			EffortApplication.restartSyncAlarm();

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Sleep for 1 second...");
			}

			Thread.sleep(1000);

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Broadcasting Splash Done Intent.");
			}

			Intent doneIntent = new Intent(SplashDoneReceiver.DONE_ACTION);
			sendBroadcast(doneIntent);

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

package in.spoors.effort1;

import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider;

import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class ConnectivityChangedReceiver extends BroadcastReceiver {
	private static final String TAG = "ConnectivityChangedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Connectivity changed.");
		}
		// send pending messages related to sim card change
		WakefulIntentService.sendWakefulWork(context.getApplicationContext(),
				BackgroundSmsSendService.class);

		SettingsDao settingsDao = SettingsDao.getInstance(context
				.getApplicationContext());
		int trackingFrequency = settingsDao.getInt(
				EffortProvider.Settings.KEY_TRACKING_FREQUENCY, 60000);
		Date lastAlarmTime = settingsDao.getDate("lastAlarmTime", null);

		if (lastAlarmTime == null
				|| System.currentTimeMillis() - lastAlarmTime.getTime() > (2 * trackingFrequency)) {
			WakefulIntentService.sendWakefulWork(context, SplashService.class);
		}

		if (Utils.isConnected(context)) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Connected.");
			}

			Utils.sync(context.getApplicationContext());

			if (!Utils.isServiceRunning(context,
					"BackgroundFileTransferService")) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Starting BFTS.");
				}

				WakefulIntentService.sendWakefulWork(context,
						BackgroundFileTransferService.class);
			} else {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "BFTS is already running.");
				}
			}
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Disconnected.");
			}

			// Utils.flushLocations(context.getApplicationContext());
		}
	}

}

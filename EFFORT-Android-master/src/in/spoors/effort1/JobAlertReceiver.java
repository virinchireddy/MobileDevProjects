package in.spoors.effort1;

import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * Used for snoozing/dismissing a late to job notification
 * 
 * @author Susmitha M <susmitha.m@spoors.in>
 */
public class JobAlertReceiver extends BroadcastReceiver {
	static final String TAG = "JobAlertReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		EffortApplication.log(TAG, "onReceive");
		String action = intent.getStringExtra("action");

		if (TextUtils.equals(action, "snooze")) {
			SettingsDao settingsDao = SettingsDao.getInstance(context
					.getApplicationContext());
			settingsDao.saveSetting("lastLateToJobNotificationMillis", ""
					+ System.currentTimeMillis());
		} else if (TextUtils.equals(action, "dismiss")) {
			long localJobId = intent.getLongExtra(EffortProvider.Jobs._ID, 0);
			JobsDao jobsDao = JobsDao.getInstance(context
					.getApplicationContext());
			jobsDao.updateLateAlertDismissedFlag(localJobId, true);
		} else {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "Invalid action " + action
						+ ". Must be snooze or dismiss.");
			}
		}

		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(Utils.LATE_TO_JOB_NOTIFICATION);
	}

}

package in.spoors.effort1;

import in.spoors.effort1.dto.Job;

import java.util.Date;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

/**
 * Used for snoozing/dismissing a start and finsh job notification
 * 
 * @author Susmitha M <susmitha.m@spoors.in>
 */
public class JobAlarmStartAndEndNotificationReceiver extends BroadcastReceiver {
	static final String TAG = "JobAlarmStartAndEndNotificationReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Job job = new Job();
		String action = "";
		String alarmType = "";
		Bundle extras = intent.getExtras();
		long timeForSnooze = 5 * 60 * 1000;
		int id = 0;
		if (extras != null) {
			if (extras.containsKey("job")) {
				job = (Job) extras.getSerializable("job");
			}
			if (extras.containsKey("action")) {
				action = intent.getStringExtra("action");
			}
			if (extras.containsKey("type")) {
				alarmType = extras.getString("type");
			}
		}

		if (TextUtils.equals(action, "snooze")) {
			if ("start".equalsIgnoreCase(alarmType)) {
				job.setStartTime(new Date(System.currentTimeMillis()));
				Utils.setAlarmsForStartTime(context.getApplicationContext(),
						job, timeForSnooze);
			} else if ("end".equalsIgnoreCase(alarmType)) {
				job.setEndTime(new Date(System.currentTimeMillis()));
				Utils.setAlarmsForEndTime(context.getApplicationContext(), job,
						timeForSnooze);
			}

		} else if (TextUtils.equals(action, "dismiss")) {
			// do nothing
		} else {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "Invalid action " + action
						+ ". Must be snooze or dismiss.");
			}
		}
		if (intent.getExtras() != null && intent.getExtras().containsKey("id")) {
			id = intent.getExtras().getInt("id");
		}
		if (BuildConfig.DEBUG) {
			Log.v("alarm", " id " + id + "action " + action + " type "
					+ alarmType);
		}
		NotificationManager notificationManager = (NotificationManager) context
				.getApplicationContext().getSystemService(
						Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(id);
	}

}

package in.spoors.effort1;

import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Job;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Settings;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Called when an alarm related to start job or ned job triggers generates a
 * notification for that job with snooze and dismiss options
 * 
 * @author Susmitha M <susmitha.m@spoors.in>
 */
public class JobStartAndEndAlarmReceiver extends BroadcastReceiver {

	String alarmType = "";
	String TAG = "JobStartAndFinishAlarmReceiver";
	int notificationId = 0;
	String notificationHeading = "";
	String notificationDescription = "";
	SettingsDao settingsDao;
	boolean canShowNotification = true;

	@SuppressLint("InlinedApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		if (BuildConfig.DEBUG) {
			Log.v("alarm",
					"id " + notificationId + "action " + intent.getAction()
							+ " type " + alarmType);
		}
		canShowNotification = true;
		String jobLabel = "";
		settingsDao = SettingsDao.getInstance(context.getApplicationContext());
		jobLabel = settingsDao.getLabel(Settings.LABEL_JOB_SINGULAR_KEY,
				Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE);
		Bundle jobBundle = intent.getExtras();
		Job job = new Job();
		if (jobBundle != null) {
			if (jobBundle.containsKey("job")) {
				job = (Job) jobBundle.getSerializable("job");
			}

			if (jobBundle.containsKey("type")) {
				alarmType = jobBundle.getString("type");
				if ("start".equalsIgnoreCase(alarmType)) {
					notificationId = Utils.START_JOB_NOTIFICATION;
					notificationHeading = job.getTitle();
					int minutes = SettingsDao.getInstance(
							context.getApplicationContext()).getInt(
							EffortProvider.Settings.KEY_REMINDER_MINUTES,
							EffortProvider.Settings.DEFAULT_REMINDER_MINUTES);
					notificationDescription = "This " + jobLabel
							+ " starts in " + minutes + " mins.";
					// "name : "+job.getTitle()+" at : "+job.getArea()+", "+job.getCity();
				} else if ("end".equalsIgnoreCase(alarmType)) {
					notificationId = Utils.END_JOB_NOTIFICATION;
					notificationHeading = job.getTitle();
					notificationDescription = "This "
							+ jobLabel
							+ " is getting delayed. Call your dispatcher if you think this will take more time.";
				}
			}

			if (job != null) {
				// set alarms for the next job
				if ("start".equalsIgnoreCase(alarmType)) {
					settingsDao.saveSetting(
							EffortProvider.Settings.KEY_PREVIOUS_START_JOB_ID,
							"" + job.getLocalId());
				} else if ("end".equalsIgnoreCase(alarmType)) {
					settingsDao.saveSetting(
							EffortProvider.Settings.KEY_PREVIOUS_END_JOB_ID, ""
									+ job.getLocalId());
				}

				Utils.setAlarmsForJobs(context);
			}
		}

		// check here is the job is completed or not
		Job presentStatusOfJob = JobsDao.getInstance(
				context.getApplicationContext()).getJobWithLocalId(
				job.getLocalId());
		if (presentStatusOfJob != null) {
			job = presentStatusOfJob;
		}
		if (job != null && job.getCompleted()) {
			canShowNotification = false;
		} else {
			canShowNotification = true;
		}

		if (canShowNotification) {
			Intent dismissIntent = new Intent(context.getApplicationContext(),
					JobAlarmStartAndEndNotificationReceiver.class);
			dismissIntent.setData(intent.getData());
			Bundle dismissBundle = jobBundle;
			dismissBundle.putString("type", alarmType);
			dismissBundle.putInt("id", notificationId);
			dismissBundle.putString("action", "dismiss");

			dismissIntent.putExtras(dismissBundle);
			PendingIntent piDismiss = PendingIntent.getBroadcast(context, 1,
					dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			Intent snoozeIntent = new Intent(context.getApplicationContext(),
					JobAlarmStartAndEndNotificationReceiver.class);
			snoozeIntent.setData(intent.getData());
			Bundle snoozeBundle = jobBundle;
			snoozeBundle.putString("type", alarmType);
			snoozeBundle.putInt("id", notificationId);
			snoozeBundle.putString("action", "snooze");
			snoozeIntent.putExtras(snoozeBundle);
			PendingIntent piSnooze = PendingIntent.getBroadcast(context, 2,
					snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			Intent resultIntent = new Intent(context, JobActivity.class);
			resultIntent.setData(intent.getData());
			resultIntent.putExtra(EffortProvider.Jobs._ID, job.getLocalId());
			resultIntent.putExtra("launchedFromNotification", true);
			PendingIntent pendingIntent = PendingIntent.getActivity(
					context.getApplicationContext(), 0, resultIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			Notification notification = new NotificationCompat.Builder(
					context.getApplicationContext()).setAutoCancel(true)
					.setDefaults(Notification.DEFAULT_ALL)
					.setContentTitle(notificationHeading)
					.setContentText(notificationDescription)
					.setSmallIcon(R.drawable.ic_launcher_transparent)
					.addAction(0, "Snooze", piSnooze)
					.addAction(0, "Dismiss", piDismiss)
					.setTicker(notificationDescription)
					.setContentIntent(pendingIntent).build();
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(notificationId, notification);
		}
	}
}
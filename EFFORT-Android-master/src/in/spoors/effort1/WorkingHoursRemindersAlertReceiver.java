package in.spoors.effort1;

import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Job;
import in.spoors.effort1.dto.WorkingHour;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

/**
 * Called when an alarm related to working day start or working day end or
 * working day middle triggers generates a notification
 * 
 * @author Susmitha M <susmitha.m@spoors.in>
 */
public class WorkingHoursRemindersAlertReceiver extends BroadcastReceiver {
	public static final String TAG = "WorkingHoursRemindersAlertReceiver";
	int notificationId = 0;
	JobsDao jobsDao;
	SettingsDao settingsDao;
	boolean showNotification = true;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		String notificationsHeading = "";
		String notificationsDescription = "";
		String jobsLabel = "", jobLabel = "";
		List<WorkingHour> workHours = Utils.getWorkingHoursOfToday(context);
		if (workHours != null) {
			settingsDao = SettingsDao.getInstance(context
					.getApplicationContext());
			jobsLabel = settingsDao.getLabel(Settings.LABEL_JOB_PLURAL_KEY,
					Settings.LABEL_JOB_PLURAL_DEFAULT_VLAUE);
			jobLabel = settingsDao.getLabel(Settings.LABEL_JOB_SINGULAR_KEY,
					Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE);
			jobsDao = JobsDao.getInstance(context.getApplicationContext());
			if (TextUtils.equals(action, "workstart")) {
				int numOfJobs = jobsDao.getJobsForWorkingHours().size();
				if (numOfJobs > 0) {
					if (numOfJobs == 1) {
						notificationsHeading = "Today's " + jobLabel;
					} else {
						notificationsHeading = "Today's " + jobsLabel;
					}
					Job nextJob = jobsDao.getNextFutureJobBasedOnStartTime();
					notificationsDescription = "Total " + jobsLabel + ": "
							+ numOfJobs;
					if (nextJob != null) {
						long gapInMilliSecs = nextJob.getStartTime().getTime()
								- System.currentTimeMillis();
						int gapInMinutes = ((int) gapInMilliSecs / 60000);
						if (gapInMinutes == 1) {
							notificationsDescription = notificationsDescription
									+ ". Your first " + jobLabel
									+ " starts in " + gapInMinutes + " minute.";
						} else {
							notificationsDescription = notificationsDescription
									+ ". Your first " + jobLabel
									+ " starts in " + gapInMinutes
									+ " minutes.";
						}
						notificationId = Utils.START_WORKING_DAY_NOTIFICATION;
					}
				} else {
					showNotification = false;
				}
			} else if (TextUtils.equals(action, "workend")) {

				WorkingHour workingHour = new WorkingHour();
				workingHour.setStartTime(workHours.get(0).getStartTime());
				workingHour.setEndTime(workHours.get(workHours.size() - 1)
						.getEndTime());
				notificationsHeading = "Delayed " + jobsLabel + " summary";
				int numOfJobs = jobsDao.getPendingJobsWithinTimerange(
						workingHour).size();
				if (numOfJobs > 0) {
					notificationsHeading = "Delayed " + jobsLabel + " summary";
					if (numOfJobs == 1) {
						notificationsDescription = "You still have "
								+ numOfJobs + " delayed " + jobLabel + ".";
					} else {
						notificationsDescription = "You still have "
								+ numOfJobs + " delayed " + jobsLabel + ".";
					}

					notificationId = Utils.END_WORKING_DAY_NOTIFICATION;
				} else {
					showNotification = false;
				}
			} else if (TextUtils.equals(action, "workmiddle")) {
				WorkingHour workingHour = new WorkingHour();
				workingHour.setStartTime(workHours.get(0).getStartTime());
				workingHour
						.setEndTime(new Date((workHours.get(0).getStartTime()
								.getTime() + workHours
								.get(workHours.size() - 1).getEndTime()
								.getTime()) / 2));
				int numOfJobs = jobsDao.getPendingJobsWithinTimerange(
						workingHour).size();
				if (numOfJobs > 0) {
					notificationsHeading = "Delayed " + jobsLabel + " summary";
					if (numOfJobs == 1)
						notificationsDescription = "You still have "
								+ numOfJobs + " delayed " + jobLabel + ".";
					else
						notificationsDescription = "You still have "
								+ numOfJobs + " delayed " + jobsLabel + ".";

					notificationId = Utils.MIDDLE_WORKING_DAY_NOTIFICATION;
				} else {
					showNotification = false;
				}
			} else {
				if (BuildConfig.DEBUG) {
					Log.w(TAG, "Invalid action " + action
							+ ". Must be snooze or dismiss.");
				}
			}
			if (showNotification) {
				Intent contentIntent = new Intent(context, JobsActivity.class);
				contentIntent.putExtra("launchedFromNotification", true);
				PendingIntent contentPendingIntent = PendingIntent.getActivity(
						context.getApplicationContext(), 0, contentIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				Notification notification = new NotificationCompat.Builder(
						context.getApplicationContext()).setAutoCancel(true)
						.setDefaults(Notification.DEFAULT_ALL)
						.setContentTitle(notificationsHeading)
						.setContentText(notificationsDescription)
						.setTicker(notificationsDescription)
						.setSmallIcon(R.drawable.ic_launcher_transparent)
						.setContentIntent(contentPendingIntent).build();
				NotificationManager mNotificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(notificationId, notification);
			}
		}
	}
}

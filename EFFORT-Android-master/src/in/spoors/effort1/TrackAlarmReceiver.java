package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.SettingsDao;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class TrackAlarmReceiver extends BroadcastReceiver {
	static final String TAG = "TrackAlarmReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (BuildConfig.DEBUG) {
			EffortApplication.log(TAG, "onReceive");
		}

		SettingsDao.getInstance(context.getApplicationContext())
				.saveSetting("lastAlarmTime",
						SQLiteDateTimeUtils.getCurrentSQLiteDateTime());

		WakefulIntentService.sendWakefulWork(context,
				LocationCaptureService.class);
	}
}

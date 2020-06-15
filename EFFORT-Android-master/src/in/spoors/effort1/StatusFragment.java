package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StatusFragment extends Fragment {

	// private static final String TAG = "StatusFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_status, container, false);
	}

	public void updateStatus() {
		Activity activity = getActivity();

		TextView tv = (TextView) activity.findViewById(R.id.syncStatusTextView);

		SettingsDao settingsDao = SettingsDao.getInstance(activity
				.getApplicationContext());
		String localSyncTimeSQLite = settingsDao.getString("localSyncTime");

		String syncStatus = "Never";

		if (localSyncTimeSQLite != null) {
			Date localSyncTime = SQLiteDateTimeUtils
					.getLocalTime(localSyncTimeSQLite);
			if (DateUtils.isToday(localSyncTime.getTime())) {
				syncStatus = Utils.getTimeFormat(
						activity.getApplicationContext()).format(localSyncTime);
			} else {
				syncStatus = Utils.getDateTimeFormatWithOutYear(
						activity.getApplicationContext()).format(localSyncTime);
			}

		}

		tv.setText("Last sync: " + syncStatus);

		tv = (TextView) activity.findViewById(R.id.trackStatusTextView);
		boolean canTrack = Utils.canTrackNow(activity.getApplicationContext());

		int trackingType = settingsDao.getInt(Settings.KEY_TRACKING_TYPE,
				Settings.TRACKING_TYPE_IN_WORK_HOURS);

		if (canTrack && trackingType != Settings.TRACKING_TYPE_NEVER
				&& trackingType != Settings.TRACKING_TYPE_IN_ACTIVITIES) {

			tv.setText("Track: ON");
		} else {
			tv.setText("Track: OFF");
		}
	}

}

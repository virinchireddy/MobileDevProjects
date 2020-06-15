package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.LocationDto;
import in.spoors.effort1.provider.EffortProvider;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SmsSentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent != null) {

			SettingsDao settingsDao = SettingsDao.getInstance(context
					.getApplicationContext());
			LocationDto track = (LocationDto) intent.getExtras()
					.getSerializable("locationDto");
			LocationsDao locationsDao = LocationsDao.getInstance(context
					.getApplicationContext());
			int resultCode = getResultCode();
			if (resultCode == Activity.RESULT_OK) {

				if (!track.getIsFallBack()) {

					locationsDao.updateLocationDirtyFlag(track.getLocalId(),
							false);
					locationsDao.deleteSyncedLocations();

				}

				settingsDao
						.saveSetting(
								EffortProvider.Settings.KEY_LAST_UPDATE_TO_CLOUD_VIA_SMS,
								SQLiteDateTimeUtils.getCurrentSQLiteDateTime());

				settingsDao.saveSetting(
						EffortProvider.Settings.KEY_LAST_UPDATE_AT_SMS_SENT,
						SQLiteDateTimeUtils.getCurrentSQLiteDateTime());

			} else {

				locationsDao.updateSmsProcessFlag(track.getLocalId(), false);

			}

		}

	}

}

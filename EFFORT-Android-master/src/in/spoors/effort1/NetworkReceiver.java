package in.spoors.effort1;

import in.spoors.effort1.dao.LocationsDao;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class NetworkReceiver extends BroadcastReceiver {

	private static final String TAG = "NetworkReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		EffortApplication.log(TAG, "In onReceive.");

		if (intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)) {
			EffortApplication.log(TAG, "Has location extra.");
			Location location = (Location) intent
					.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);

			if (location == null) {
				EffortApplication.log(TAG,
						"Location is null. Not doing further processing.");
			} else {
				Intent updateIntent = new Intent(context,
						LocationUpdateService.class);
				updateIntent.putExtra(LocationManager.KEY_LOCATION_CHANGED,
						location);
				WakefulIntentService.sendWakefulWork(context, updateIntent);
				if (BuildConfig.DEBUG) {
					EffortApplication.log(TAG,
							"Started location update service with network location: "
									+ location.toString());
				}
			}
		}

		if (intent.hasExtra(LocationManager.KEY_PROVIDER_ENABLED)) {
			boolean enabled = intent.getBooleanExtra(
					LocationManager.KEY_PROVIDER_ENABLED, false);

			EffortApplication.log(TAG,
					"Received provider enabled update [enabled=" + enabled
							+ "]");

			if (!enabled) {
				LocationsDao locationsDao = LocationsDao.getInstance(context
						.getApplicationContext());
				locationsDao.finalizeNetworkLocations();
				Utils.stopNetworkReceiver(context.getApplicationContext(), true);
				EffortApplication.log(TAG,
						"Forcefully stopped network receiver.");
			}
		}

		if (intent.hasExtra(LocationManager.KEY_STATUS_CHANGED)) {
			EffortApplication.log(
					TAG,
					"Received status change [status="
							+ intent.getIntExtra(
									LocationManager.KEY_STATUS_CHANGED, -1)
							+ "]");
		}
	}

}

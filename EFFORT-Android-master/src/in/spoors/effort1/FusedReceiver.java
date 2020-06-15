package in.spoors.effort1;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.android.gms.location.LocationClient;

public class FusedReceiver extends WakefulBroadcastReceiver {

	private static final String TAG = "FusedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		EffortApplication.log(TAG, "In onReceive.");

		if (intent.hasExtra(LocationClient.KEY_LOCATION_CHANGED)) {
			EffortApplication.log(TAG, "Has location extra.");

			Location location = (Location) intent
					.getParcelableExtra(LocationClient.KEY_LOCATION_CHANGED);

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
							"Started location update service with fused location: "
									+ location.toString());
				}

				// SettingsDao settingsDao = SettingsDao.getInstance(context
				// .getApplicationContext());
				// String prevLatitude =
				// settingsDao.getString("nearbyLatitude");
				// String prevLongitude =
				// settingsDao.getString("nearbyLongitude");
				//
				// Utils.fetchNearbyCustomersIfRequired(
				// context.getApplicationContext(), prevLatitude,
				// prevLongitude, location.getLatitude(),
				// location.getLongitude());
				//
				// if (BuildConfig.DEBUG) {
				// EffortApplication.log(TAG,
				// "Fetching near by customers (if required).");
				// }
			}
		}

	}

}

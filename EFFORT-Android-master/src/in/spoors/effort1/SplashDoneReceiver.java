package in.spoors.effort1;

import in.spoors.effort1.dao.SettingsDao;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class SplashDoneReceiver extends BroadcastReceiver {
	public static final String TAG = SplashDoneReceiver.class.getSimpleName();
	public static final String DONE_ACTION = "in.spoors.effort1.ACTION_SPLASH_DONE";
	private SplashActivity activityToBeFinished;

	public SplashDoneReceiver(SplashActivity activityToBeFinished) {
		this.activityToBeFinished = activityToBeFinished;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Received splash done broadcast.");
			Log.d(TAG, "Finishing Splash Activity.");
		}

		activityToBeFinished.finish();

		SettingsDao settingsDao = SettingsDao.getInstance(context
				.getApplicationContext());
		String defaultAppScreen = settingsDao.getString("defaultAppScreen");
		Intent activityIntent = null;

		if (TextUtils.isEmpty(defaultAppScreen)
				|| "Agenda".equals(defaultAppScreen)) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Starting agenda activity.");
			}

			activityIntent = new Intent(context, AgendaActivity.class);
		} else if ("Home".equals(defaultAppScreen)) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Starting home activity.");
			}

			activityIntent = new Intent(context, HomeActivity.class);
		} else if ("Jobs".equals(defaultAppScreen)) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Starting jobs activity.");
			}

			activityIntent = new Intent(context, JobsActivity.class);
		} else if ("Customers".equals(defaultAppScreen)) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Starting customers activity.");
			}

			activityIntent = new Intent(context, CustomersActivity.class);
		} else if ("Forms".equals(defaultAppScreen)) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Starting forms activity.");
			}

			activityIntent = new Intent(context, FormSpecsActivity.class);
		} else if ("Messages".equals(defaultAppScreen)) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Starting messages activity.");
			}

			activityIntent = new Intent(context, MessagesActivity.class);
		} else if ("Knowledge Base".equals(defaultAppScreen)) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Starting articles activity.");
			}

			activityIntent = new Intent(context, ArticlesActivity.class);
		} else if ("Named Locations".equals(defaultAppScreen)) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Starting locations activity.");
			}

			activityIntent = new Intent(context, LocationsActivity.class);
		}
		if (activityIntent == null) {
			Toast.makeText(
					context,
					"Default app screen setting '" + defaultAppScreen
							+ "' is invalid. Launching agenda screen.",
					Toast.LENGTH_LONG).show();
			activityIntent = new Intent(context, AgendaActivity.class);
		}

		context.startActivity(activityIntent);

		try {
			context.unregisterReceiver(this);
		} catch (IllegalArgumentException e) {
			// ignore the exception
		}
	}

}

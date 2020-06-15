package in.spoors.effort1;

import in.spoors.effort1.dao.SettingsDao;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class SplashActivity extends Activity {
	public static final String TAG = "SplashActivity";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	public static final String EXTRA_MESSAGE = "message";

	SplashDoneReceiver receiver = null;
	SettingsDao settingsDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}

		requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		settingsDao = SettingsDao.getInstance(getApplicationContext());

		TextView versionTextView = (TextView) findViewById(R.id.versionTextView);
		versionTextView.setText("Version "
				+ Utils.getVersionName(getApplicationContext()));

		final SettingsDao settingsDao = SettingsDao
				.getInstance(getApplicationContext());
		boolean locationAllowed = settingsDao.getBoolean("locationAllowed",
				false);

		if (!locationAllowed) {
			new AlertDialog.Builder(this)
					.setTitle("Allow location to be used?")
					.setMessage(
							getString(R.string.app_name)
									+ " collects, stores, and sends your device's location information to your signed-up "
									+ getString(R.string.app_name)
									+ " account only. If you agree to share your location details, please tap Allow. Tap Don't allow to disallow and stop using the app.")
					.setPositiveButton("Allow",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									settingsDao.saveSetting("locationAllowed",
											"true");
									Log.d(TAG,
											"Registering Splash Done Receiver.");
									receiver = new SplashDoneReceiver(
											SplashActivity.this);
									registerReceiver(
											receiver,
											new IntentFilter(
													SplashDoneReceiver.DONE_ACTION));

									Log.d(TAG, "Starting Splash Service.");
									WakefulIntentService.sendWakefulWork(
											SplashActivity.this,
											SplashService.class);
								}
							})
					.setNegativeButton("Don't allow",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									finish();
								}
							}).show();
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Registering Splash Done Receiver.");
			}

			receiver = new SplashDoneReceiver(this);
			registerReceiver(receiver, new IntentFilter(
					SplashDoneReceiver.DONE_ACTION));

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Starting Splash Service.");
			}

			WakefulIntentService.sendWakefulWork(this, SplashService.class);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

	// @Override
	// protected void onPause() {
	// super.onPause();
	// EffortApplication.activityPaused();
	// }

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
		checkPlayServices();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(
						this,
						"This device is not supported because it does not have Google Play Services.",
						Toast.LENGTH_LONG).show();
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	@Override
	protected void onPause() {
		if (receiver != null) {
			Log.d(TAG, "Unregistering Splash Done Receiver.");

			try {
				unregisterReceiver(receiver);
			} catch (IllegalArgumentException e) {
				Log.d(TAG,
						"Ignored the exception caught while unregistering Splash Done Receiver: "
								+ e.toString());
			}
		}

		super.onPause();
		EffortApplication.activityPaused();
	}
}

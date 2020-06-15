package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LocationPickerActivity extends ActionBarActivity {

	public static final String TAG = "LocationPickerActivity";

	private SettingsDao settingsDao;
	private SimpleDateFormat dateTimeFormat;
	private String gpsLatitude;
	private String gpsLongitude;
	private String networkLatitude;
	private String networkLongitude;
	private String fusedLatitude;
	private String fusedLongitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location_picker);
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		dateTimeFormat = Utils.getDateTimeFormat(getApplicationContext());

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean fused = Settings.LOCATION_PROVIDER_FUSED.equals(settingsDao
				.getString(Settings.KEY_LOCATION_PROVIDER));

		boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean networkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (fused) {
			findViewById(R.id.androidLocationLayout).setVisibility(View.GONE);
			findViewById(R.id.fusedLocationLayout).setVisibility(View.VISIBLE);
			TextView textView = (TextView) findViewById(R.id.fusedStatusTextView);
			textView.setText((gpsEnabled || networkEnabled) ? "On" : "Off");

			// textView = (TextView) findViewById(R.id.gpsStatusSinceLabel);
			// textView.setText(enabled ? "On since" : "Off since");
			//
			// textView = (TextView) findViewById(R.id.gpsStatusSinceTextView);
			// textView.setText(enabled ? "On since" : "Off since");
			//
			textView = (TextView) findViewById(R.id.lastKnownFusedLatitudeTextView);
			fusedLatitude = settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_FUSED_LATITUDE);
			setNotAvailableOrValue(textView, fusedLatitude);

			textView = (TextView) findViewById(R.id.lastKnownFusedLongitudeTextView);
			fusedLongitude = settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_FUSED_LONGITUDE);
			setNotAvailableOrValue(textView, fusedLongitude);

			Button useFusedLocationButton = (Button) findViewById(R.id.useFusedLocationButton);
			Button showFusedLocationButton = (Button) findViewById(R.id.showFusedLocationButton);

			if (!TextUtils.isEmpty(fusedLatitude)
					&& !TextUtils.isEmpty(fusedLongitude)) {
				useFusedLocationButton.setVisibility(View.VISIBLE);
				showFusedLocationButton.setVisibility(View.VISIBLE);
			} else {
				useFusedLocationButton.setVisibility(View.GONE);
				showFusedLocationButton.setVisibility(View.GONE);
			}

			textView = (TextView) findViewById(R.id.lastKnownFusedFixTimeTextView);
			setDateTimeOrEmptyValue(
					textView,
					settingsDao
							.getString(EffortProvider.Settings.KEY_LAST_FUSED_FIX_TIME),
					getString(R.string.not_available));
		} else {
			findViewById(R.id.androidLocationLayout)
					.setVisibility(View.VISIBLE);
			findViewById(R.id.fusedLocationLayout).setVisibility(View.GONE);

			TextView textView = (TextView) findViewById(R.id.gpsStatusTextView);
			textView.setText(gpsEnabled ? "On" : "Off");

			// textView = (TextView) findViewById(R.id.gpsStatusSinceLabel);
			// textView.setText(enabled ? "On since" : "Off since");
			//
			// textView = (TextView) findViewById(R.id.gpsStatusSinceTextView);
			// textView.setText(enabled ? "On since" : "Off since");
			//
			textView = (TextView) findViewById(R.id.lastKnownGpsLatitudeTextView);
			gpsLatitude = settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_GPS_LATITUDE);
			setNotAvailableOrValue(textView, gpsLatitude);

			textView = (TextView) findViewById(R.id.lastKnownGpsLongitudeTextView);
			gpsLongitude = settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_GPS_LONGITUDE);
			setNotAvailableOrValue(textView, gpsLongitude);

			Button useGpsLocationButton = (Button) findViewById(R.id.useGpsLocationButton);
			Button showGpsLocationButton = (Button) findViewById(R.id.showGpsLocationButton);

			if (!TextUtils.isEmpty(gpsLatitude)
					&& !TextUtils.isEmpty(gpsLongitude)) {
				useGpsLocationButton.setVisibility(View.VISIBLE);
				showGpsLocationButton.setVisibility(View.VISIBLE);
			} else {
				useGpsLocationButton.setVisibility(View.GONE);
				showGpsLocationButton.setVisibility(View.GONE);
			}

			textView = (TextView) findViewById(R.id.lastKnownGpsFixTimeTextView);
			setDateTimeOrEmptyValue(
					textView,
					settingsDao
							.getString(EffortProvider.Settings.KEY_LAST_GPS_FIX_TIME),
					getString(R.string.not_available));

			textView = (TextView) findViewById(R.id.networkStatusTextView);
			textView.setText(networkEnabled ? "On" : "Off");

			textView = (TextView) findViewById(R.id.lastKnownNetworkLatitudeTextView);
			networkLatitude = settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_NETWORK_LATITUDE);
			setNotAvailableOrValue(textView, networkLatitude);

			textView = (TextView) findViewById(R.id.lastKnownNetworkLongitudeTextView);
			networkLongitude = settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_NETWORK_LONGITUDE);
			setNotAvailableOrValue(textView, networkLongitude);

			Button useNetworkLocationButton = (Button) findViewById(R.id.useNetworkLocationButton);
			Button showNetworkLocationButton = (Button) findViewById(R.id.showNetworkLocationButton);

			if (!TextUtils.isEmpty(networkLatitude)
					&& !TextUtils.isEmpty(networkLongitude)) {
				useNetworkLocationButton.setVisibility(View.VISIBLE);
				showNetworkLocationButton.setVisibility(View.VISIBLE);
			} else {
				useNetworkLocationButton.setVisibility(View.GONE);
				showNetworkLocationButton.setVisibility(View.GONE);
			}

			textView = (TextView) findViewById(R.id.lastKnownNetworkFixTimeTextView);
			setDateTimeOrEmptyValue(
					textView,
					settingsDao
							.getString(EffortProvider.Settings.KEY_LAST_NETWORK_FIX_TIME),
					getString(R.string.not_available));
		}
	}

	private void setNotAvailableOrValue(TextView textView, String value) {
		if (TextUtils.isEmpty(value)) {
			textView.setText(R.string.not_available);
		} else {
			textView.setText(value);
		}
	}

	private void setDateTimeOrEmptyValue(TextView textView,
			String sqliteDateTime, String emptyValue) {
		Date date = SQLiteDateTimeUtils.getLocalTime(sqliteDateTime);

		if (TextUtils.isEmpty(sqliteDateTime) || date == null) {
			textView.setText(emptyValue);
		} else {
			textView.setText(dateTimeFormat.format(date));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onOptionsItemSelected: groupId=" + item.getGroupId()
					+ ", itemId=" + item.getItemId());
		}

		switch (item.getItemId()) {
		case android.R.id.home:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "ActionBar's Up/home button clicked.");
			}
			finish();
			break;

		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "No menu item handler found.");
			}
		}

		return super.onOptionsItemSelected(item);
	}

	public void onUseGpsLocationButtonClick(View view) {
		Intent data = new Intent();
		data.putExtra("latitude", gpsLatitude);
		data.putExtra("longitude", gpsLongitude);
		setResult(Activity.RESULT_OK, data);
		finish();
	}

	public void onUseNetworkLocationButtonClick(View view) {
		Intent data = new Intent();
		data.putExtra("latitude", networkLatitude);
		data.putExtra("longitude", networkLongitude);
		setResult(Activity.RESULT_OK, data);
		finish();
	}

	public void onUseFusedLocationButtonClick(View view) {
		Intent data = new Intent();
		data.putExtra("latitude", fusedLatitude);
		data.putExtra("longitude", fusedLongitude);
		setResult(Activity.RESULT_OK, data);
		finish();
	}

	public void onShowGpsLocationButtonClick(View view) {
		if (gpsLatitude == null || gpsLongitude == null) {
			return;
		}

		if (gpsLatitude != null && gpsLongitude != null) {
			Intent intent = new Intent(this, MapActivity.class);
			intent.putExtra("name", "GPS Location");
			intent.putExtra("latitude", Double.parseDouble(gpsLatitude));
			intent.putExtra("longitude", Double.parseDouble(gpsLongitude));
			startActivity(intent);
		}
	}

	public void onShowNetworkLocationButtonClick(View view) {
		if (networkLatitude == null || networkLongitude == null) {
			return;
		}

		if (networkLatitude != null && networkLongitude != null) {
			Intent intent = new Intent(this, MapActivity.class);
			intent.putExtra("name", "Network Location");
			intent.putExtra("latitude", Double.parseDouble(networkLatitude));
			intent.putExtra("longitude", Double.parseDouble(networkLongitude));
			startActivity(intent);
		}
	}

	public void onShowFusedLocationButtonClick(View view) {
		if (fusedLatitude == null || fusedLongitude == null) {
			return;
		}

		if (fusedLatitude != null && fusedLongitude != null) {
			Intent intent = new Intent(this, MapActivity.class);
			intent.putExtra("name", "Location");
			intent.putExtra("latitude", Double.parseDouble(fusedLatitude));
			intent.putExtra("longitude", Double.parseDouble(fusedLongitude));
			startActivity(intent);
		}
	}

}

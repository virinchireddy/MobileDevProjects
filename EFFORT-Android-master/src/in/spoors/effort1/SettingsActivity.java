package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.FormSpecsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.protocol.SyncBasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends ActionBarActivity {

	public static final String TAG = "SettingsActivity";
	public static final int REQUEST_PICK_LOCATION = 1;
	public static final int REQUEST_ENABLE_BLUETOOTH = 2;

	private SettingsDao settingsDao;
	private Spinner remindersSpinner;
	private Spinner sortSpinner;
	private Spinner uploadSpinner;
	private SimpleDateFormat dateTimeFormat;
	private String[] reminderOptions = { "1 minute", "5 minutes", "10 minutes",
			"15 minutes", "20 minutes", "25 minutes", "30 minutes",
			"45 minutes", "1 hour", "2 hours", "3 hours", "12 hours",
			"24 hours", "2 days", "1 week" };

	private int[] reminderMinutes = { 1, 5, 10, 15, 20, 25, 30, 45, 60, 120,
			180, 720, 1440, 2880, 10080 };

	String[] trackingModes = { "", "Track via data only", "Track via SMS only",
			"Track via data and use SMS as fall back" };

	private String[] sortOptions;
	private static final int SORT_BY_COMPANY_NAME = 0;
	private static final int SORT_BY_CONTACT_NAME = 1;

	private String[] uploadOptions = { "On Wi-Fi only",
			"On Wi-Fi and mobile data" };
	private static final int WIFI_ONLY = 0;
	private static final int UPLOAD_ANY = 1;
	private TextView homeLatitudeTextView;
	private TextView homeLongitudeTextView;
	private LinearLayout fallbackIntervalLayout;
	private DrawerFragment drawerFragment;
	private Spinner printerSpinner;
	private List<String> printerNames = new ArrayList<String>();
	private List<String> printerAddresses = new ArrayList<String>();
	private BluetoothAdapter bluetooth;
	ArrayAdapter<String> printerAdapter;
	private StateChangeReceiver stateChangeReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_settings);
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		dateTimeFormat = Utils.getDateTimeFormat(getApplicationContext());
		setSupportProgressBarIndeterminateVisibility(false);

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// edit mode widgets
		TextView textView = (TextView) findViewById(R.id.remindTextView);
		textView.setText(settingsDao.getLabel(Settings.LABEL_JOB_SINGULAR_KEY,
				Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE) + " reminders");

		remindersSpinner = (Spinner) findViewById(R.id.remindersSpinner);
		ArrayAdapter<String> remindersAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, reminderOptions);
		remindersAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		remindersSpinner.setAdapter(remindersAdapter);
		int minutes = settingsDao.getInt(
				EffortProvider.Settings.KEY_REMINDER_MINUTES,
				EffortProvider.Settings.DEFAULT_REMINDER_MINUTES);
		int position = 0;
		for (int i = 0; i < reminderMinutes.length; ++i) {
			if (minutes == reminderMinutes[i]) {
				position = i;
				break;
			}
		}
		remindersSpinner.setSelection(position);
		remindersSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						settingsDao.saveSetting(
								EffortProvider.Settings.KEY_REMINDER_MINUTES,
								"" + reminderMinutes[position]);
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}
				});

		sortOptions = new String[] {
				settingsDao.getLabel(Settings.LABEL_CUSTOMER_SINGULAR_KEY,
						Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE)
						+ " name", "Primary contact name" };
		sortSpinner = (Spinner) findViewById(R.id.sortSpinner);
		TextView sorttextView = (TextView) findViewById(R.id.sortTextView);
		sorttextView.setText("Sort "
				+ settingsDao.getLabel(Settings.LABEL_CUSTOMER_SINGULAR_KEY,
						Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE)
				+ " list");
		ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, sortOptions);
		sortAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sortSpinner.setAdapter(sortAdapter);
		sortSpinner.setSelection(EffortProvider.Settings.SORT_ORDER_COMPANY
				.equals(settingsDao
						.getString(EffortProvider.Settings.KEY_SORT_ORDER)) ? 0
				: 1);

		sortSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == SORT_BY_COMPANY_NAME) {
					settingsDao.saveSetting(
							EffortProvider.Settings.KEY_SORT_ORDER,
							EffortProvider.Settings.SORT_ORDER_COMPANY);
				} else if (position == SORT_BY_CONTACT_NAME) {
					settingsDao.saveSetting(
							EffortProvider.Settings.KEY_SORT_ORDER,
							EffortProvider.Settings.SORT_ORDER_CONTACT);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		uploadSpinner = (Spinner) findViewById(R.id.uploadSpinner);
		ArrayAdapter<String> uploadAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, uploadOptions);
		uploadAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		uploadSpinner.setAdapter(uploadAdapter);
		uploadSpinner
				.setSelection(EffortProvider.Settings.UPLOAD_WIFI
						.equals(settingsDao
								.getString(EffortProvider.Settings.KEY_UPLOAD)) ? 0
						: 1);

		uploadSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == WIFI_ONLY) {
					settingsDao.saveSetting(EffortProvider.Settings.KEY_UPLOAD,
							EffortProvider.Settings.UPLOAD_WIFI);
				} else if (position == UPLOAD_ANY) {
					settingsDao.saveSetting(EffortProvider.Settings.KEY_UPLOAD,
							EffortProvider.Settings.UPLOAD_ANY);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});

		FormSpecsDao formSpecsDao = FormSpecsDao
				.getInstance(getApplicationContext());

		if (formSpecsDao.hasPrintTemplates()) {
			findViewById(R.id.printerLayout).setVisibility(View.VISIBLE);

			bluetooth = BluetoothAdapter.getDefaultAdapter();

			if (bluetooth.isEnabled()) {
				updatePrinters();
			} else {
				stateChangeReceiver = new StateChangeReceiver();
				registerReceiver(stateChangeReceiver, new IntentFilter(
						BluetoothAdapter.ACTION_STATE_CHANGED));
				startActivityForResult(new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE),
						REQUEST_ENABLE_BLUETOOTH);
			}

			printerSpinner = (Spinner) findViewById(R.id.printerSpinner);
			printerAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, printerNames);
			printerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			printerSpinner.setAdapter(printerAdapter);
			printerSpinner.setSelection(Utils.getStringIndex(printerNames,
					settingsDao.getString(Settings.KEY_PRINTER_NAME)));

			printerSpinner
					.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent,
								View view, int position, long id) {
							settingsDao.saveSetting(Settings.KEY_PRINTER_NAME,
									printerNames.get(position));
							settingsDao.saveSetting(
									Settings.KEY_PRINTER_ADDRESS,
									printerAddresses.get(position));
						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
						}
					});
		} else {
			findViewById(R.id.printerLayout).setVisibility(View.GONE);
		}

		textView = (TextView) findViewById(R.id.locationProviderTextView);

		boolean fused = Settings.LOCATION_PROVIDER_FUSED.equals(settingsDao
				.getString(Settings.KEY_LOCATION_PROVIDER));
		if (fused) {
			textView.setText("Unified");
		} else {
			textView.setText("GPS & Network");
		}

		int trackBy = settingsDao.getInt(EffortProvider.Settings.KEY_TRACK_BY,
				1);

		fallbackIntervalLayout = (LinearLayout) findViewById(R.id.fallbackIntervalLayout);

		textView = (TextView) findViewById(R.id.updateModeTextView);
		if (trackBy < 1) {
			textView.setText("None");
		} else {
			textView.setText(trackingModes[trackBy]);
		}

		textView = (TextView) findViewById(R.id.fallbackIntervalTextView);

		int fallbackInterval = settingsDao.getInt(
				EffortProvider.Settings.KEY_SMS_TRACK_FREQUENCY,
				EffortProvider.Settings.DEFAULT_FALL_BACK_INTERVAL) / 60000;

		if (fallbackInterval <= 0) {
			textView.setText("Never");
		} else {
			textView.setText(fallbackInterval + " minutes");
		}

		setFallbackIntervalView(trackBy);

		textView = (TextView) findViewById(R.id.trackingFrequencyTextView);
		int trackingFrequency = settingsDao.getInt(
				EffortProvider.Settings.KEY_TRACKING_FREQUENCY,
				EffortProvider.Settings.DEFAULT_TRACKING_FREQUENCY) / 1000;
		if (trackingFrequency <= 0) {
			textView.setText("Never");
		} else {
			textView.setText(trackingFrequency + " seconds");
		}

		textView = (TextView) findViewById(R.id.lastUpdateToCloudTextView);

		setDateTimeOrEmptyValue(
				textView,
				settingsDao
						.getString(EffortProvider.Settings.KEY_LAST_UPDATE_TO_CLOUD),
				"Never");
		if (trackBy == 2) {
			setDateTimeOrEmptyValue(
					textView,
					settingsDao
							.getString(EffortProvider.Settings.KEY_LAST_UPDATE_TO_CLOUD_VIA_SMS),
					"Never");
		}

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean networkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (fused) {
			findViewById(R.id.androidLocationLayout).setVisibility(View.GONE);
			findViewById(R.id.fusedLocationLayout).setVisibility(View.VISIBLE);
			textView = (TextView) findViewById(R.id.fusedStatusTextView);
			textView.setText((gpsEnabled || networkEnabled) ? "On" : "Off");

			String latitude = settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_FUSED_LATITUDE);
			textView = (TextView) findViewById(R.id.lastKnownFusedLatitudeTextView);
			setNotAvailableOrValue(textView, latitude);

			String longitude = settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_FUSED_LONGITUDE);
			textView = (TextView) findViewById(R.id.lastKnownFusedLongitudeTextView);
			setNotAvailableOrValue(textView, longitude);

			textView = (TextView) findViewById(R.id.lastKnownFusedFixTimeTextView);
			setDateTimeOrEmptyValue(
					textView,
					settingsDao
							.getString(EffortProvider.Settings.KEY_LAST_FUSED_FIX_TIME),
					getString(R.string.not_available));

			findViewById(R.id.viewFusedMapButton)
					.setVisibility(
							Utils.isLatitudeLongitudeValid(latitude, longitude) ? View.VISIBLE
									: View.GONE);
		} else {
			findViewById(R.id.androidLocationLayout)
					.setVisibility(View.VISIBLE);
			findViewById(R.id.fusedLocationLayout).setVisibility(View.GONE);

			textView = (TextView) findViewById(R.id.gpsStatusTextView);
			textView.setText(gpsEnabled ? "On" : "Off");

			// textView = (TextView) findViewById(R.id.gpsStatusSinceLabel);
			// textView.setText(enabled ? "On since" : "Off since");
			//
			// textView = (TextView) findViewById(R.id.gpsStatusSinceTextView);
			// textView.setText(enabled ? "On since" : "Off since");
			//
			String latitude = settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_GPS_LATITUDE);
			textView = (TextView) findViewById(R.id.lastKnownGpsLatitudeTextView);
			setNotAvailableOrValue(textView, latitude);

			String longitude = settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_GPS_LONGITUDE);
			textView = (TextView) findViewById(R.id.lastKnownGpsLongitudeTextView);
			setNotAvailableOrValue(textView, longitude);

			textView = (TextView) findViewById(R.id.lastKnownGpsFixTimeTextView);
			setDateTimeOrEmptyValue(
					textView,
					settingsDao
							.getString(EffortProvider.Settings.KEY_LAST_GPS_FIX_TIME),
					getString(R.string.not_available));

			findViewById(R.id.viewGpsMapButton)
					.setVisibility(
							Utils.isLatitudeLongitudeValid(latitude, longitude) ? View.VISIBLE
									: View.GONE);

			textView = (TextView) findViewById(R.id.networkStatusTextView);
			textView.setText(networkEnabled ? "On" : "Off");

			// textView = (TextView) findViewById(R.id.gpsStatusSinceLabel);
			// textView.setText(enabled ? "On since" : "Off since");
			//
			// textView = (TextView) findViewById(R.id.gpsStatusSinceTextView);
			// textView.setText(enabled ? "On since" : "Off since");
			//
			latitude = settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_NETWORK_LATITUDE);
			textView = (TextView) findViewById(R.id.lastKnownNetworkLatitudeTextView);
			setNotAvailableOrValue(textView, latitude);

			longitude = settingsDao
					.getString(EffortProvider.Settings.KEY_LAST_NETWORK_LONGITUDE);
			textView = (TextView) findViewById(R.id.lastKnownNetworkLongitudeTextView);
			setNotAvailableOrValue(textView, longitude);

			textView = (TextView) findViewById(R.id.lastKnownNetworkFixTimeTextView);
			setDateTimeOrEmptyValue(
					textView,
					settingsDao
							.getString(EffortProvider.Settings.KEY_LAST_NETWORK_FIX_TIME),
					getString(R.string.not_available));

			findViewById(R.id.viewNetworkMapButton)
					.setVisibility(
							Utils.isLatitudeLongitudeValid(latitude, longitude) ? View.VISIBLE
									: View.GONE);
		}

		textView = (TextView) findViewById(R.id.versionTextView);
		textView.setText(Utils.getVersionName(getApplicationContext()));

		textView = (TextView) findViewById(R.id.effortIdTextView);
		textView.setText(settingsDao
				.getString(EffortProvider.Settings.KEY_CODE));

		textView = (TextView) findViewById(R.id.effortIdTextView);
		textView.setText(settingsDao
				.getString(EffortProvider.Settings.KEY_CODE));

		textView = (TextView) findViewById(R.id.employeeTextView);
		textView.setText(settingsDao.getLabel(
				Settings.LABEL_EMPLOYEE_SINGULAR_KEY,
				Settings.LABEL_EMPLOYEE_SINGULAR_DEFAULT_VLAUE)
				+ " home location");

		homeLatitudeTextView = (TextView) findViewById(R.id.employeeHomeLatitudeTextView);
		setNotAvailableOrValue(homeLatitudeTextView,
				settingsDao.getString("employeeHomeLatitude"));

		homeLongitudeTextView = (TextView) findViewById(R.id.employeeHomeLongitudeTextView);
		setNotAvailableOrValue(homeLongitudeTextView,
				settingsDao.getString("employeeHomeLongitude"));
		updateModifyLocationButton();

		// TODO
		/*
		 * findViewById(R.id.sendClientReportToServerButton).setVisibility(
		 * View.INVISIBLE);
		 */
		findViewById(R.id.sendClientReportToServerButton).setEnabled(true);

		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_settings, "Settings",
				null, null);
	}

	private void setFallbackIntervalView(int trackingModePosition) {

		if (trackingModePosition == 3) {
			fallbackIntervalLayout.setVisibility(View.VISIBLE);
			TextView textView = (TextView) findViewById(R.id.lastUpdateToCloudViaSmsTextView);
			setDateTimeOrEmptyValue(
					textView,
					settingsDao
							.getString(EffortProvider.Settings.KEY_LAST_UPDATE_TO_CLOUD_VIA_SMS),
					"Never");
		} else {
			fallbackIntervalLayout.setVisibility(View.GONE);
		}

	}

	private void updateModifyLocationButton() {
		Button modifyLocationButton = (Button) findViewById(R.id.modifyLocationButton);
		if (settingsDao.getBoolean(
				Settings.KEY_PERMISSION_HOME_LOCATION_MODIFY, true)) {
			modifyLocationButton.setEnabled(true);
		} else {
			modifyLocationButton.setEnabled(false);
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
		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}

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
				Log.w(TAG, "No menu item handler found.");
			}
		}

		return super.onOptionsItemSelected(item);
	}

	public void onModifyLocationButtonClick(View view) {
		Intent intent = new Intent(this, LocationPickerFromMapActivity.class);
		intent.putExtra("latitude", Double.parseDouble(settingsDao.getString(
				"employeeHomeLatitude", 0.0 + "")));
		intent.putExtra("longitude", Double.parseDouble(settingsDao.getString(
				"employeeHomeLongitude", "" + 0.0)));
		intent.putExtra("of", "your home");
		startActivityForResult(intent, REQUEST_PICK_LOCATION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK
				&& requestCode == REQUEST_PICK_LOCATION && data != null) {
			settingsDao.saveSetting("employeeHomeLocationDirty", "true");
			settingsDao.saveSetting("employeeHomeLatitude",
					data.getStringExtra("latitude"));
			settingsDao.saveSetting("employeeHomeLongitude",
					data.getStringExtra("longitude"));
			setNotAvailableOrValue(homeLatitudeTextView,
					settingsDao.getString("employeeHomeLatitude"));
			setNotAvailableOrValue(homeLongitudeTextView,
					settingsDao.getString("employeeHomeLongitude"));
			Utils.sync(getApplicationContext());
			updateModifyLocationButton();
		} else if (requestCode == REQUEST_ENABLE_BLUETOOTH
				&& resultCode != RESULT_OK) {
			Toast.makeText(this,
					"You must enable bluetooth to choose the printer.",
					Toast.LENGTH_LONG).show();
		}
	}

	public void onLegalNoticesButtonClick(View view) {
		Intent intent = new Intent(this, LegalNoticesActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerFragment.onPostCreate(savedInstanceState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		drawerFragment.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);

		super.onBackPressed();
	}

	private void showMap(String name, String latitudeString,
			String longitudeString) {
		Intent intent = new Intent(this, MapActivity.class);

		if (TextUtils.isEmpty(latitudeString)
				|| TextUtils.isEmpty(longitudeString)) {
			Toast.makeText(this, "Latitude or longitude is missing.",
					Toast.LENGTH_LONG).show();
			return;
		}

		double latitude = EffortApplication.INDIA_LATITUDE;
		double longitude = EffortApplication.INDIA_LONGITUDE;

		try {
			latitude = Double.parseDouble(latitudeString);
			longitude = Double.parseDouble(longitudeString);
		} catch (NumberFormatException e) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG,
						"Ignored the number format exception that was caught.",
						e);
			}

			Toast.makeText(this, "Invalid latitude or longitude.",
					Toast.LENGTH_LONG).show();
			return;
		}

		intent.putExtra("name", name);
		intent.putExtra("latitude", latitude);
		intent.putExtra("longitude", longitude);

		startActivity(intent);

	}

	public void onViewFusedMapButtonClick(View v) {
		showMap("Location",
				settingsDao
						.getString(EffortProvider.Settings.KEY_LAST_FUSED_LATITUDE),
				settingsDao
						.getString(EffortProvider.Settings.KEY_LAST_FUSED_LONGITUDE));
	}

	public void onViewGpsMapButtonClick(View v) {
		showMap("GPS Location",
				settingsDao
						.getString(EffortProvider.Settings.KEY_LAST_GPS_LATITUDE),
				settingsDao
						.getString(EffortProvider.Settings.KEY_LAST_GPS_LONGITUDE));
	}

	public void onViewNetworkMapButtonClick(View v) {
		showMap("Network Location",
				settingsDao
						.getString(EffortProvider.Settings.KEY_LAST_NETWORK_LATITUDE),
				settingsDao
						.getString(EffortProvider.Settings.KEY_LAST_NETWORK_LONGITUDE));
	}

	private void updatePrinters() {
		Set<BluetoothDevice> devices = bluetooth.getBondedDevices();
		printerNames.clear();
		printerAddresses.clear();

		for (BluetoothDevice device : devices) {
			printerNames.add(device.getName());
			printerAddresses.add(device.getAddress());
		}

		if (printerAdapter != null) {
			printerAdapter.notifyDataSetChanged();
		}
	}

	class StateChangeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle extras = intent.getExtras();
			Log.i(TAG, "In stateChangeReceiver's onReceive. intent=" + intent
					+ ", extras=" + extras);

			for (String key : extras.keySet()) {
				Object value = extras.get(key);
				Log.i(TAG, "key=" + key + ", value="
						+ value.getClass().getName());
			}

			if (intent.hasExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE)) {
				Log.i(TAG,
						"Previous state="
								+ getStateName(intent.getIntExtra(
										BluetoothAdapter.EXTRA_PREVIOUS_STATE,
										-1)));
			}

			if (intent.hasExtra(BluetoothAdapter.EXTRA_STATE)) {
				Log.i(TAG,
						"State="
								+ getStateName(intent.getIntExtra(
										BluetoothAdapter.EXTRA_STATE, -1)));
				if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON) {
					updatePrinters();
				}
			}
		}
	}

	private String getStateName(int state) {
		switch (state) {
		case BluetoothAdapter.STATE_CONNECTED:
			return "connected";
		case BluetoothAdapter.STATE_CONNECTING:
			return "connecting";
		case BluetoothAdapter.STATE_DISCONNECTED:
			return "disconnected";
		case BluetoothAdapter.STATE_DISCONNECTING:
			return "disconnecting";
		case BluetoothAdapter.STATE_OFF:
			return "off";
		case BluetoothAdapter.STATE_ON:
			return "on";
		case BluetoothAdapter.STATE_TURNING_OFF:
			return "turningOff";
		case BluetoothAdapter.STATE_TURNING_ON:
			return "turningOn";
		}

		return "" + state;
	}

	public void onSendClientReportButtonClick(View v) {
		setSupportProgressBarIndeterminateVisibility(true);
		new UploadClientReportTask().execute();
		findViewById(R.id.sendClientReportToServerButton).setEnabled(false);
	}

	private class UploadClientReportTask extends
			AsyncTask<String, Integer, Long> {

		@Override
		protected Long doInBackground(String... params) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In doInBackground.");
			}

			AndroidHttpClient httpClient = AndroidHttpClient
					.newInstance("EFFORT");
			SyncBasicHttpContext httpContext = new SyncBasicHttpContext(null);
			String employeeId = settingsDao.getString("employeeId");
			String serverBaseUrl = getString(R.string.server_base_url);

			Builder builder = Uri.parse(serverBaseUrl).buildUpon()
					.appendEncodedPath("media/file/upload/part/" + employeeId);
			Utils.appendCommonQueryParameters(getApplicationContext(), builder);

			String path = DBHelper.getInstance(getApplicationContext())
					.getReadableDatabase().getPath();
			Log.i(TAG, "The file path is: " + path);
			File file = new File(path);
			String md5hash = Utils.getMD5Hash(path);

			builder.appendQueryParameter("fileName", file.getName())
					.appendQueryParameter("contentType",
							"application/octet-stream")
					.appendQueryParameter("partChecksum", md5hash)
					.appendQueryParameter("finalChecksum", md5hash)
					.appendQueryParameter("fileId", "" + 1)
					.appendQueryParameter("partNo", "1")
					.appendQueryParameter("totalPart", "1");
			Uri uri = builder.build();

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Media upload URL: " + uri.toString());
			}
			HttpPost httpPost = new HttpPost(uri.toString());
			Utils.setTimeouts(httpPost);

			FileEntity requestEntity = new FileEntity(new File(path),
					"application/octet-stream");
			httpPost.setEntity(requestEntity);
			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Request entity: isChunked="
								+ requestEntity.isChunked() + ", isStreaming="
								+ requestEntity.isStreaming()
								+ ", isRepeatable="
								+ requestEntity.isRepeatable());
			}

			try {

				HttpResponse response = httpClient.execute(httpPost,
						httpContext);

				HttpEntity responseEntity = response.getEntity();
				long contentLength = responseEntity.getContentLength();
				String contentType = responseEntity.getContentType().getValue();
				String jsonResponse = EntityUtils.toString(responseEntity);

				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Response entity: isChunked="
									+ responseEntity.isChunked()
									+ ", isRepeatable="
									+ responseEntity.isRepeatable()
									+ ", isStreaming="
									+ responseEntity.isStreaming()
									+ ", contentLength=" + contentLength);
					Log.d(TAG, "Response entity: contentType" + contentType);
					Log.d(TAG, "Response: " + jsonResponse);
				}

				Long mediaId = null;

				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					mediaId = getMediaId(jsonResponse);
				} else {
					Log.w(TAG, "Media upload failed with status: "
							+ response.getStatusLine().toString());
				}

				if (mediaId != null) {
					Log.e(TAG, "Media ID is found in the response.");

					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							Toast.makeText(getApplicationContext(),
									"Reported successfully.", Toast.LENGTH_LONG)
									.show();
						}
					});
				}

			} catch (MalformedURLException e) {
				Log.e(TAG, "Bad URL: " + e.toString(), e);
			} catch (IOException e) {
				Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(
								getApplicationContext(),
								"Failed to upload due to network error. Please try again later",
								Toast.LENGTH_LONG).show();
					}
				});
			} catch (Exception e) {
				Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
			} finally {
				if (httpClient != null) {
					httpClient.close();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Upload finished.");
			}
			setSupportProgressBarIndeterminateVisibility(false);
			findViewById(R.id.sendClientReportToServerButton).setEnabled(true);
		}
	}

	private Long getMediaId(String jsonResponse) {
		JSONTokener tokener = new JSONTokener(jsonResponse);

		try {
			JSONObject json = (JSONObject) tokener.nextValue();
			return json.getLong("mediaId");
		} catch (JSONException e) {
			return null;
		}
	}
}

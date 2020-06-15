package in.spoors.effort1;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends ActionBarActivity {

	public static final String TAG = "MapActivity";

	private GoogleMap map;
	private LatLng latLng;
	private Button directionsButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_map);
		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);

		directionsButton = (Button) findViewById(R.id.useTheseValuesButton);
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapFragment);
		map = mapFragment.getMap();

		if (map == null) {
			Toast.makeText(this, "Failed to get map object.", Toast.LENGTH_LONG)
					.show();
		} else {
			map.setMyLocationEnabled(true);

			double latitude = EffortApplication.INDIA_LATITUDE;
			double longitude = EffortApplication.INDIA_LONGITUDE;

			if (getIntent().hasExtra("latitude")
					&& getIntent().hasExtra("longitude")) {
				latitude = getIntent().getDoubleExtra("latitude",
						EffortApplication.INDIA_LATITUDE);
				longitude = getIntent().getDoubleExtra("longitude",
						EffortApplication.INDIA_LONGITUDE);
				latLng = new LatLng(latitude, longitude);
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
						latLng, 12);
				addMarker();
				map.moveCamera(cameraUpdate);
				directionsButton.setEnabled(true);
				setSupportProgressBarIndeterminateVisibility(false);
			} else {
				directionsButton.setEnabled(false);
				setSupportProgressBarIndeterminateVisibility(true);
				new GetLocationTask().execute(getIntent().getStringExtra(
						"address"));
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Not calling super.onSaveInstanceState to prevent the following
		// exception:
		// 07-25 20:52:06.476: E/AndroidRuntime(5502):
		// java.lang.NoSuchMethodError: android.os.Bundle.getClassLoader
		// 07-25 20:52:06.476: E/AndroidRuntime(5502): at
		// com.google.android.gms.maps.SupportMapFragment.onSaveInstanceState(Unknown
		// Source)
		// super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
		int errorCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		if (errorCode != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(errorCode, this, 0);
		}
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

	private void addMarker() {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.title(getIntent().getStringExtra("name"));
		markerOptions.position(latLng);
		map.addMarker(markerOptions).showInfoWindow();
	}

	public void onDirectionsButtonClick(View view) {
		if (latLng == null) {
			return;
		}

		String url = "http://maps.google.com/maps?daddr=" + latLng.latitude
				+ "," + latLng.longitude;

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Directions Url: " + url);
		}

		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse(url));
		startActivity(intent);
	}

	class GetLocationTask extends AsyncTask<String, Integer, LatLng> {

		@Override
		protected LatLng doInBackground(String... params) {
			Geocoder geocoder = new Geocoder(MapActivity.this);
			try {
				List<Address> addresses = geocoder.getFromLocationName(
						params[0], 1);

				if (addresses != null && addresses.size() > 0) {
					Address address = addresses.get(0);

					if (address != null && address.hasLatitude()
							&& address.hasLongitude()) {
						return new LatLng(address.getLatitude(),
								address.getLongitude());
					}
				} else {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									MapActivity.this,
									"Failed to get latitude and longitude from address.",
									Toast.LENGTH_LONG).show();

						}
					});
				}
			} catch (IOException e) {
				Log.w(TAG,
						"Failed to get latitude and longitude from address.", e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(
								MapActivity.this,
								"Failed to get latitude and longitude from address.",
								Toast.LENGTH_LONG).show();

					}
				});
			}

			return null;
		}

		@Override
		protected void onPostExecute(LatLng result) {
			setSupportProgressBarIndeterminateVisibility(false);
			if (result != null && map != null) {
				latLng = result;
				addMarker();
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(result, 12));
				directionsButton.setEnabled(true);
			} else {
				directionsButton.setEnabled(false);
			}
		}
	}

}

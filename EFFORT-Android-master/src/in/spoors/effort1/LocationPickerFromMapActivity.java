package in.spoors.effort1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationPickerFromMapActivity extends ActionBarActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

	public static final String TAG = "MapActivity";
	private GoogleMap map;
	private LatLng currentLocation;
	private LatLng markerLocation;
	// private Button useThisLocationButton;
	private Button showCurrentLocationButton;
	private Button showMarkerLocationButton;
	private Button moveMarkerToCurrentLocationButton;
	private Button useMarkerLocationButton;
	// LatLng userSelectedLocation;

	int zoomlevel = 0;
	Context context;
	double latitude;
	double longitude;
	private LocationClient mLocationClient;

	boolean firstTime = true;

	// These settings are the same as the settings for the map. They will in
	// fact give you updates
	// at the maximal rates currently possible.
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(5000) // 5 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_map_picker);
		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Choose location");
		String of = getIntent().getStringExtra("of");

		if (!TextUtils.isEmpty(of)) {
			actionBar.setSubtitle("Of " + of);
		}
		showCurrentLocationButton = (Button) findViewById(R.id.showCurrentLocationButton);
		showMarkerLocationButton = (Button) findViewById(R.id.showMarkerLocationButton);
		moveMarkerToCurrentLocationButton = (Button) findViewById(R.id.moveMarkerToCurrentLocationButton);
		useMarkerLocationButton = (Button) findViewById(R.id.useMarkerLocationButton);
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapFragment);
		map = mapFragment.getMap();
		showCurrentLocationButton.setEnabled(false);
		showMarkerLocationButton.setEnabled(false);
		moveMarkerToCurrentLocationButton.setEnabled(false);
		useMarkerLocationButton.setEnabled(false);

		if (map == null) {
			Toast.makeText(this, "Failed to get map object.", Toast.LENGTH_LONG)
					.show();
		} else {
			map.setMyLocationEnabled(true);

			if (getIntent().hasExtra("latitude")
					&& getIntent().hasExtra("longitude")) {
				latitude = getIntent().getDoubleExtra("latitude", 0.0);
				longitude = getIntent().getDoubleExtra("longitude", 0.0);

			}
			zoomlevel = 12;

			// means a valid location
			if (latitude != 0.0 && longitude != 0.0) {
				markerLocation = new LatLng(latitude, longitude);
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
						markerLocation, zoomlevel);
				addMarkerAtGivenPoint(markerLocation);
				map.moveCamera(cameraUpdate);
				showMarkerLocationButton.setEnabled(true);
				useMarkerLocationButton.setEnabled(true);
			}

			setSupportProgressBarIndeterminateVisibility(false);
			map.setOnMapLongClickListener(new OnMapLongClickListener() {

				@Override
				public void onMapLongClick(LatLng point) {
					markerLocation = point;
					addMarkerAtGivenPoint(point);
				}
			});
		}
		Toast.makeText(context, R.string.location_picker_hint,
				Toast.LENGTH_LONG).show();
	}

	void addMarkerAtGivenPoint(LatLng latLng) {
		if (latLng != null) {
			showMarkerLocationButton.setEnabled(true);
			useMarkerLocationButton.setEnabled(true);
			markerLocation = latLng;
			map.clear();
			map.addMarker(new MarkerOptions().title(
					latLng.latitude + " ,\n " + latLng.longitude).position(
					latLng));
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

		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
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

	public void onShowCurrentLocationButtonClick(View view) {
		moveToThisLocation(currentLocation);
	}

	public void onShowMarkerLocationButtonClick(View view) {
		moveToThisLocation(markerLocation);
	}

	public void onMoveMarkerToCurrentLocationButtonClick(View view) {
		map.clear();
		markerLocation = currentLocation;
		addMarkerAtGivenPoint(currentLocation);
		moveToThisLocation(currentLocation);
	}

	public void onUseMarkerLocationButtonClick(View view) {
		if (markerLocation != null) {
			Intent data = new Intent();
			data.putExtra("latitude", markerLocation.latitude + "");
			data.putExtra("longitude", markerLocation.longitude + "");
			setResult(Activity.RESULT_OK, data);
		}
		finish();
	}

	@Override
	public void onLocationChanged(Location location) {
		currentLocation = new LatLng(location.getLatitude(),
				location.getLongitude());
		// means the user did not send any location
		showCurrentLocationButton.setEnabled(true);
		moveMarkerToCurrentLocationButton.setEnabled(true);
		if (latitude == 0.0 && longitude == 0.0 && firstTime) {
			addMarkerAtGivenPoint(currentLocation);
			moveToThisLocation(currentLocation);
		}
		firstTime = false;
		if (BuildConfig.DEBUG) {
			Log.v("LOC", "location is " + location);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(REQUEST, this); // LocationListener
	}

	@Override
	public void onDisconnected() {

	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	void moveToThisLocation(LatLng position) {
		if (position != null) {
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					position, zoomlevel);
			map.moveCamera(cameraUpdate);
		}
	}
}

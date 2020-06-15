package in.spoors.effort1;

import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dto.Customer;
import in.spoors.effort1.provider.EffortProvider.Customers;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class CustomersRouteMapActivity extends ActionBarActivity {
	private static final String TAG = "CustomersRouteMapActivity";
	private List<Customer> customers;
	private String customerIds;
	private Long assignRouteId;
	private HashMap<String, Long> customersMarkersIds = new HashMap<String, Long>();
	private GoogleMap map;
	private Double avgLongitude = 0.0;
	private Double avgLatitude = 0.0;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * set up the screen
		 */
		setContentView(R.layout.activity_map);
		if (savedInstanceState == null) {
			customerIds = getIntent().getStringExtra("customerids");
			assignRouteId = getIntent().getLongExtra(
					CustomerActivities.ASSIGN_ROUTE_ID, 0);
			customersMarkersIds = new HashMap<String, Long>();
		} else {
			customerIds = savedInstanceState.getString("customerids");
			assignRouteId = savedInstanceState
					.getLong(CustomerActivities.ASSIGN_ROUTE_ID);
			customersMarkersIds = (HashMap<String, Long>) savedInstanceState
					.getSerializable("customerHashMap");
		}
		Button getDrivingDirectionsBtn = (Button) findViewById(R.id.useTheseValuesButton);
		getDrivingDirectionsBtn.setVisibility(View.GONE);
		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(R.string.all_customers);
		createCustomerList();

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.mapFragment);
		map = mapFragment.getMap();

		if (map == null) {
			Toast.makeText(this, "Failed to get map object.", Toast.LENGTH_LONG)
					.show();
		} else {
			map.setMyLocationEnabled(true);

			for (Customer customer : customers) {
				avgLongitude = avgLongitude + customer.getLongitude();
				avgLatitude = avgLatitude + customer.getLatitude();
				LatLng latLng = new LatLng(customer.getLatitude(),
						customer.getLongitude());

				addMarkerAtGivenPoint(latLng, customer.getName(),
						customer.getRemoteId());

			}
			int zoomlevel = 12;
			LatLng avgLatLng = new LatLng(avgLatitude / customers.size(),
					avgLongitude / customers.size());

			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
					avgLatLng, zoomlevel);
			map.moveCamera(cameraUpdate);

		}

	}

	void addMarkerAtGivenPoint(LatLng latLng, String customerName,
			Long customerId) {

		MarkerOptions position = new MarkerOptions().title(customerName)
				.position(latLng);
		Marker marker = map.addMarker(position);
		// marker.showInfoWindow();
		customersMarkersIds.put(marker.getId(), customerId);
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				Long customerId = customersMarkersIds.get(marker.getId());

				Intent intent = new Intent(CustomersRouteMapActivity.this,
						CustomerActivities.class);
				intent.putExtra(CustomerActivities.CUSTOMER_ID, customerId);
				intent.putExtra(CustomerActivities.ASSIGN_ROUTE_ID,
						assignRouteId);
				intent.putExtra(CustomerActivities.CUSTOMER_NAME,
						marker.getTitle());
				finish();
				startActivity(intent);
			}
		});

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(CustomerActivities.ASSIGN_ROUTE_ID, assignRouteId);
		outState.putString("customerids", customerIds);
		outState.putSerializable("customerHashMap", customersMarkersIds);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case android.R.id.home:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "ActionBar's Up/home button clicked.");
			}
			finish();
			return true;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void createCustomerList() {
		CustomersDao customersDao = CustomersDao
				.getInstance(getApplicationContext());
		customers = customersDao.getCustomersHavingLocationWithRemoteIds(
				customerIds, Customers.DISTANCE);

	}

}

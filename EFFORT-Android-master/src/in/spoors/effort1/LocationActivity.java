package in.spoors.effort1;

import in.spoors.effort1.dao.CountriesDao;
import in.spoors.effort1.dao.NamedLocationsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.NamedLocation;
import in.spoors.effort1.provider.EffortProvider.NamedLocations;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LocationActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor> {

	public static final String TAG = "LocationActivity";
	public static final String ACTION_PICK = "pick";
	private String action;

	public static final int EDIT = 21;
	public static final int SAVE = 22;
	public static final int DISCARD = 23;
	public static final int REQUEST_PICK_LOCATION = 1;

	private long localLocationId;
	private boolean editMode;

	/**
	 * Named location on activity start
	 */
	private NamedLocation originalLocation;

	/**
	 * Named location that acts as the scratch pad
	 */
	private NamedLocation currentLocation;

	private NamedLocationsDao namedLocationsDao;
	private SettingsDao settingsDao;
	private CountriesDao countriesDao;

	// view mode controls
	private TextView nameTextView;
	private View descriptionLayout;
	private TextView descriptionTextView;
	private View addressLayout;
	private View streetLayout;
	private TextView streetTextView;
	private View areaLayout;
	private TextView areaTextView;
	private View landmarkLayout;
	private TextView landmarkTextView;
	private View cityLayout;
	private TextView cityTextView;
	private View stateLayout;
	private TextView stateTextView;
	private View pinCodeLayout;
	private TextView pinCodeTextView;
	private View countryLayout;
	private TextView countryTextView;
	private View locationLayout;
	private TextView latitudeTextView;
	private TextView longitudeTextView;
	private View viewMapButtonInAddressSection;

	// edit mode controls
	private EditText nameEditText;
	private EditText descriptionEditText;
	private EditText streetEditText;
	private EditText areaEditText;
	private EditText landmarkEditText;
	private EditText cityEditText;
	private EditText stateEditText;
	private Spinner countrySpinner;
	private EditText pinCodeEditText;
	private EditText latitudeEditText;
	private EditText longitudeEditText;

	private EditLocationFragment editLocationFragment;
	private ViewLocationFragment viewLocationFragment;
	private List<String> countries;
	private String singular;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);

		Intent intent = getIntent();
		action = intent.getAction();
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Intent action: " + action);
		}

		namedLocationsDao = NamedLocationsDao
				.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		countriesDao = CountriesDao.getInstance(getApplicationContext());

		singular = settingsDao.getLabel(
				Settings.LABEL_NAMEDLOCATION_SINGULAR_KEY,
				Settings.LABEL_NAMEDLOCATION_SINGULAR_DEFAULT_VLAUE);

		if (savedInstanceState == null) {
			localLocationId = getIntent().getLongExtra(NamedLocations._ID, 0);
		} else {
			localLocationId = savedInstanceState.getLong("localLocationId");
			editMode = savedInstanceState.getBoolean("editMode");
			originalLocation = (NamedLocation) savedInstanceState
					.getSerializable("originalLocation");
			currentLocation = (NamedLocation) savedInstanceState
					.getSerializable("currentLocation");
		}

		if (localLocationId == 0) {
			editMode = true;
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(singular);
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Activity launched for local location ID: "
					+ localLocationId);
		}

		countries = countriesDao.getCountries();

		// view mode controls
		nameTextView = (TextView) findViewById(R.id.nameTextView);
		descriptionLayout = findViewById(R.id.descriptionLayout);
		descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		addressLayout = findViewById(R.id.addressLayout);
		streetLayout = findViewById(R.id.streetLayout);
		streetTextView = (TextView) findViewById(R.id.streetTextView);
		areaLayout = findViewById(R.id.areaLayout);
		areaTextView = (TextView) findViewById(R.id.areaTextView);
		landmarkLayout = findViewById(R.id.landmarkLayout);
		landmarkTextView = (TextView) findViewById(R.id.landmarkTextView);
		cityLayout = findViewById(R.id.cityLayout);
		cityTextView = (TextView) findViewById(R.id.cityTextView);
		stateLayout = findViewById(R.id.stateLayout);
		stateTextView = (TextView) findViewById(R.id.stateTextView);
		pinCodeLayout = findViewById(R.id.pinCodeLayout);
		pinCodeTextView = (TextView) findViewById(R.id.pinCodeTextView);
		countryLayout = findViewById(R.id.countryLayout);
		countryTextView = (TextView) findViewById(R.id.countryTextView);
		locationLayout = findViewById(R.id.locationLayout);
		latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
		longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
		viewMapButtonInAddressSection = findViewById(R.id.viewMapButtonInAddressSection);

		// edit mode controls
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
		streetEditText = (EditText) findViewById(R.id.streetEditText);
		areaEditText = (EditText) findViewById(R.id.areaEditText);
		landmarkEditText = (EditText) findViewById(R.id.landmarkEditText);
		cityEditText = (EditText) findViewById(R.id.cityEditText);
		stateEditText = (EditText) findViewById(R.id.stateEditText);
		pinCodeEditText = (EditText) findViewById(R.id.pinCodeEditText);
		countrySpinner = (Spinner) findViewById(R.id.countrySpinner);

		latitudeEditText = (EditText) findViewById(R.id.latitudeEditText);
		longitudeEditText = (EditText) findViewById(R.id.longitudeEditText);

		viewLocationFragment = (ViewLocationFragment) getSupportFragmentManager()
				.findFragmentById(R.id.viewLocationFragment);
		editLocationFragment = (EditLocationFragment) getSupportFragmentManager()
				.findFragmentById(R.id.editLocationFragment);
		showRightFragment();

		countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, countries);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		countrySpinner.setAdapter(spinnerAdapter);

		getSupportLoaderManager().initLoader(0, null, this);
	}

	private void showRightFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		if (editMode) {
			ft.hide(viewLocationFragment);
			ft.show(editLocationFragment);
		} else {
			ft.hide(editLocationFragment);
			ft.show(viewLocationFragment);
		}

		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.location, menu);
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		boolean manageNamedLocations = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_MANAGE_NAMED_LOCATIONS, true);

		menu.findItem(R.id.editLocation).setVisible(
				manageNamedLocations && !editMode);

		menu.findItem(R.id.saveLocation).setVisible(editMode);
		menu.findItem(R.id.discardLocation).setVisible(editMode);

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("localLocationId", localLocationId);
		outState.putBoolean("editMode", editMode);
		outState.putSerializable("originalLocation", originalLocation);

		if (editMode) {
			useEditTextValues();
		}

		outState.putSerializable("currentLocation", currentLocation);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader for Loader ID: " + id
					+ ", Location ID: " + localLocationId);
		}

		return new CursorLoader(getApplicationContext(), Uri.withAppendedPath(
				NamedLocations.CONTENT_URI, "" + localLocationId),
				NamedLocations.ALL_COLUMNS, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}
		// DatabaseUtils.dumpCursor(cursor);

		if (cursor == null || !cursor.moveToFirst()) {
			if (originalLocation == null) {
				originalLocation = new NamedLocation();
			}

			if (currentLocation == null) {
				currentLocation = new NamedLocation();
			}

			return;
		}

		// don't touch these job objects, if they already exist
		// so that we don't lose the user's changes due to
		// orientation change
		if (originalLocation == null || originalLocation.getPartial()) {
			originalLocation = new NamedLocation();
			originalLocation.load(cursor);
		}

		if (currentLocation == null || currentLocation.getPartial()) {
			currentLocation = new NamedLocation();
			currentLocation.load(cursor);

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Current location after loading: "
								+ currentLocation.toString());
			}

			supportInvalidateOptionsMenu();
		}

		if (!editMode) {
			// name section
			String str = currentLocation.getName();
			nameTextView.setText(str);

			str = currentLocation.getDescription();
			descriptionTextView.setText(str);
			descriptionLayout.setVisibility(Utils.getVisibility(str));

			// address section
			str = currentLocation.getStreet();
			streetTextView.setText(str);
			streetLayout.setVisibility(Utils.getVisibility(str));

			str = currentLocation.getArea();
			areaTextView.setText(str);
			areaLayout.setVisibility(Utils.getVisibility(str));

			str = currentLocation.getLandmark();
			landmarkTextView.setText(str);
			landmarkLayout.setVisibility(Utils.getVisibility(str));

			str = currentLocation.getCity();
			cityTextView.setText(str);
			cityLayout.setVisibility(Utils.getVisibility(str));

			str = currentLocation.getState();
			stateTextView.setText(str);
			stateLayout.setVisibility(Utils.getVisibility(str));

			str = currentLocation.getPinCode();
			pinCodeTextView.setText(str);
			pinCodeLayout.setVisibility(Utils.getVisibility(str));

			str = currentLocation.getCountry();
			countryTextView.setText(str);
			countryLayout.setVisibility(Utils.getVisibility(str));

			addressLayout.setVisibility(Utils.getVisibility(streetLayout,
					areaLayout, landmarkLayout, cityLayout, stateLayout,
					pinCodeLayout, countryLayout));

			if (currentLocation.getLatitude() != null
					&& currentLocation.getLongitude() != null) {
				latitudeTextView.setText(String.valueOf(currentLocation
						.getLatitude()));
				longitudeTextView.setText(String.valueOf(currentLocation
						.getLongitude()));
				locationLayout.setVisibility(View.VISIBLE);

				if (addressLayout.getVisibility() == View.VISIBLE) {
					viewMapButtonInAddressSection.setVisibility(View.GONE);
				}
			} else {
				locationLayout.setVisibility(View.GONE);
			}
		} else {
			nameEditText.setText(currentLocation.getName());
			descriptionEditText.setText(currentLocation.getDescription());

			changeCountrySelection(currentLocation.getCountry());
			stateEditText.setText(currentLocation.getState());
			streetEditText.setText(currentLocation.getStreet());
			areaEditText.setText(currentLocation.getArea());
			landmarkEditText.setText(currentLocation.getLandmark());
			cityEditText.setText(currentLocation.getCity());
			pinCodeEditText.setText(currentLocation.getPinCode());

			if (currentLocation.getLatitude() != null
					&& currentLocation.getLongitude() != null) {
				latitudeEditText.setText(String.valueOf(currentLocation
						.getLatitude()));
				longitudeEditText.setText(String.valueOf(currentLocation
						.getLongitude()));
			}
		}

		if (currentLocation.getPartial()) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Marking location as complete.");
			}

			namedLocationsDao.updatePartialFlag(false,
					currentLocation.getLocalId());
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoaderReset.");
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

			onBackPressed();
			break;

		case R.id.editLocation:
			editMode = true;

			// invalidate to show save and discard buttons
			supportInvalidateOptionsMenu();
			getSupportLoaderManager().restartLoader(0, null, this);
			showRightFragment();

			break;

		case R.id.saveLocation:
			save();
			break;

		case R.id.discardLocation:
			discard();
			break;

		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "No menu item handler found.");
			}
		}

		return super.onOptionsItemSelected(item);
	}

	private void useEditTextValues() {
		currentLocation.setName(Utils.getString(nameEditText));
		currentLocation.setDescription(Utils.getString(descriptionEditText));
		currentLocation.setStreet(Utils.getString(streetEditText));
		currentLocation.setArea(Utils.getString(areaEditText));
		currentLocation.setLandmark(Utils.getString(landmarkEditText));

		currentLocation.setCountry((String) countrySpinner.getSelectedItem());
		currentLocation.setState(Utils.getString(stateEditText));

		currentLocation.setCity(Utils.getString(cityEditText));
		currentLocation.setPinCode(Utils.getString(pinCodeEditText));
		currentLocation.setLatitude(Utils.getDouble(latitudeEditText));
		currentLocation.setLongitude(Utils.getDouble(longitudeEditText));
	}

	@Override
	public void onBackPressed() {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "In onBackPressed.");
		}

		if (editMode) {
			useEditTextValues();
			if (hasUnsavedChanges()) {
				DialogFragment dialogFragment = MyAlertDialogFragment
						.newInstance(R.string.alert_dialog_title);
				dialogFragment.show(getSupportFragmentManager(), "dialog");
			} else {
				discard();
			}
		} else {
			super.onBackPressed();
		}
	}

	public static class MyAlertDialogFragment extends DialogFragment {
		public static MyAlertDialogFragment newInstance(int title) {
			MyAlertDialogFragment frag = new MyAlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int title = getArguments().getInt("title");

			return new AlertDialog.Builder(getActivity())
					.setTitle(title)
					.setPositiveButton(R.string.alert_dialog_save,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									((LocationActivity) (getActivity())).save();
								}
							})
					.setNegativeButton(R.string.alert_dialog_discard,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									((LocationActivity) (getActivity()))
											.discard();
								}
							}).create();
		}

	}

	private void save() {
		useEditTextValues();

		if (TextUtils.isEmpty(currentLocation.getName())) {
			Toast.makeText(this, singular + " name must be specified.",
					Toast.LENGTH_LONG).show();

			return;
		}

		String pinCode = currentLocation.getPinCode();

		if ("India".equals(currentLocation.getCountry())
				&& !TextUtils.isEmpty(pinCode)) {
			if (!(TextUtils.isDigitsOnly(pinCode) && pinCode.length() == 6)) {
				Toast.makeText(this, "Pin code must be a 6-digit number.",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		currentLocation.setDirty(true);
		currentLocation.setPartial(false);
		namedLocationsDao.save(currentLocation);
		Utils.sync(getApplicationContext());
		finish();
	}

	private void discard() {
		finish();
	}

	private boolean hasUnsavedChanges() {
		return !(TextUtils.equals(originalLocation.getName(),
				currentLocation.getName())
				&& TextUtils.equals(originalLocation.getStreet(),
						currentLocation.getStreet())
				&& TextUtils.equals(originalLocation.getArea(),
						currentLocation.getArea())
				&& TextUtils.equals(originalLocation.getLandmark(),
						currentLocation.getLandmark())
				&& TextUtils.equals(originalLocation.getCity(),
						currentLocation.getCity())
				&& TextUtils.equals(originalLocation.getState(),
						currentLocation.getState())
				&& TextUtils.equals(originalLocation.getCountry(),
						currentLocation.getCountry())
				&& TextUtils.equals(originalLocation.getPinCode(),
						currentLocation.getPinCode())
				&& Utils.doublesEqual(originalLocation.getLatitude(),
						currentLocation.getLatitude()) && Utils
					.doublesEqual(originalLocation.getLongitude(),
							currentLocation.getLongitude()));
	}

	public void onModifyLocationButtonClick(View view) {
		Intent intent = new Intent(this, LocationPickerActivity.class);
		startActivityForResult(intent, REQUEST_PICK_LOCATION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Activity.RESULT_OK
				&& requestCode == REQUEST_PICK_LOCATION && data != null) {
			String latitude = data.getStringExtra("latitude");
			String longitude = data.getStringExtra("longitude");
			latitudeEditText.setText(latitude);
			longitudeEditText.setText(longitude);

			Double doubleLatitude = null;
			Double doubleLongitude = null;

			try {
				doubleLatitude = Double.parseDouble(latitude);
				doubleLongitude = Double.parseDouble(longitude);
			} catch (NumberFormatException e) {
				Log.w(TAG, "Failed to parse lat/long", e);
			}

			currentLocation.setLatitude(doubleLatitude);
			currentLocation.setLongitude(doubleLongitude);
		}
	}

	public void onViewMapButtonClick(View view) {
		if (currentLocation == null) {
			return;
		}

		Intent intent = new Intent(this, MapActivity.class);
		intent.putExtra("name", currentLocation.getName());

		if (currentLocation.getLatitude() != null
				&& currentLocation.getLongitude() != null) {
			intent.putExtra("latitude", currentLocation.getLatitude());
			intent.putExtra("longitude", currentLocation.getLongitude());
		} else {
			String address = Utils.getAddressForMapDisplay(
					currentLocation.getStreet(), currentLocation.getArea(),
					currentLocation.getCity(), currentLocation.getState(),
					currentLocation.getCountry(), currentLocation.getPinCode());

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Launching map activity for address: " + address);
			}

			intent.putExtra("address", address);
		}

		startActivity(intent);
	}

	private void changeCountrySelection(String country) {
		if (TextUtils.isEmpty(country)) {
			countrySpinner.setSelection(0);
		} else {
			int index = Utils.getStringIndex(countries, country);

			if (index == -1) {
				Toast.makeText(
						this,
						"Sorry! "
								+ country
								+ " does not exist in the app's country list. Picking the first country.",
						Toast.LENGTH_LONG).show();
				countrySpinner.setSelection(0);
			} else {
				countrySpinner.setSelection(index);
			}
		}
	}

}

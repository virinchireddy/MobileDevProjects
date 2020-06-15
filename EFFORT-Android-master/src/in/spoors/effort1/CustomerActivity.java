package in.spoors.effort1;

import in.spoors.effort1.dao.CountriesDao;
import in.spoors.effort1.dao.CustomerTypesDao;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Customer;
import in.spoors.effort1.dto.CustomerType;
import in.spoors.effort1.provider.EffortProvider.Customers;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class CustomerActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor> {

	public static final String TAG = "CustomerActivity";
	public static final String ACTION_PICK = "pick";
	private String action;

	public static final int EDIT = 21;
	public static final int SAVE = 22;
	public static final int DISCARD = 23;
	public static final int REQUEST_PICK_LOCATION = 1;

	private long localCustomerId;
	private boolean editMode;

	/**
	 * Customer on activity start
	 */
	private Customer originalCustomer;

	/**
	 * Customer that acts as the scratch pad
	 */
	private Customer currentCustomer;

	private CustomersDao customersDao;
	private CustomerTypesDao customerTypesDao;
	private SettingsDao settingsDao;
	private CountriesDao countriesDao;

	// view mode controls
	private View companyLayout;
	private View nameLayout;
	private TextView nameTextView;
	private View idLayout;
	private TextView idTextView;
	private View typeLayout;
	private TextView typeTextView;
	private View phoneLayout;
	private TextView phoneTextView;
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
	private View pcLayout;
	private View pcFirstNameLayout;
	private TextView pcFirstNameTextView;
	private View pcLastNameLayout;
	private TextView pcLastNameTextView;
	private View pcTitleLayout;
	private TextView pcTitleTextView;
	private View pcPhoneLayout;
	private TextView pcPhoneTextView;
	private View pcEmailLayout;
	private TextView pcEmailTextView;
	private View scLayout;
	private View scFirstNameLayout;
	private TextView scFirstNameTextView;
	private View scLastNameLayout;
	private TextView scLastNameTextView;
	private View scTitleLayout;
	private TextView scTitleTextView;
	private View scPhoneLayout;
	private TextView scPhoneTextView;
	private View scEmailLayout;
	private TextView scEmailTextView;
	private View locationLayout;
	private TextView latitudeTextView;
	private TextView longitudeTextView;
	private View viewMapButtonInAddressSection;

	// edit mode controls
	private EditText nameEditText;
	private View typeEditLayout;
	private Spinner typeSpinner;
	private EditText phoneEditText;
	private EditText streetEditText;
	private EditText areaEditText;
	private EditText landmarkEditText;
	private EditText cityEditText;
	private EditText stateEditText;
	private Spinner countrySpinner;
	private EditText pinCodeEditText;
	private EditText pcFirstNameEditText;
	private EditText pcLastNameEditText;
	private EditText pcTitleEditText;
	private EditText pcPhoneEditText;
	private EditText pcEmailEditText;
	private EditText scFirstNameEditText;
	private EditText scLastNameEditText;
	private EditText scTitleEditText;
	private EditText scPhoneEditText;
	private EditText scEmailEditText;
	private EditText latitudeEditText;
	private EditText longitudeEditText;

	private EditCustomerFragment editCustomerFragment;
	private ViewCustomerFragment viewCustomerFragment;
	private FetchDoneReceiver fetchDoneReceiver = new FetchDoneReceiver();
	private boolean fetchInProgress;
	private List<CustomerType> types;
	private List<String> countries;
	private String singular;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_customer);

		Intent intent = getIntent();
		action = intent.getAction();
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Intent action: " + action);
		}

		customerTypesDao = CustomerTypesDao
				.getInstance(getApplicationContext());
		customersDao = CustomersDao.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		countriesDao = CountriesDao.getInstance(getApplicationContext());

		singular = settingsDao.getLabel(Settings.LABEL_CUSTOMER_SINGULAR_KEY,
				Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE);

		if (savedInstanceState == null) {
			localCustomerId = getIntent().getLongExtra(Customers._ID, 0);
		} else {
			localCustomerId = savedInstanceState.getLong("localCustomerId");
			editMode = savedInstanceState.getBoolean("editMode");
			originalCustomer = (Customer) savedInstanceState
					.getSerializable("originalCustomer");
			currentCustomer = (Customer) savedInstanceState
					.getSerializable("currentCustomer");
		}

		if (localCustomerId == 0) {
			editMode = true;
		} else {
			if (!customersDao.customerWithLocalIdExists(localCustomerId)) {
				Toast.makeText(
						this,
						"The selected " + singular
								+ " no longer exists on the device.",
						Toast.LENGTH_LONG).show();
				finish();
			}
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(singular);
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Activity launched for Local Customer ID: "
					+ localCustomerId);
		}

		types = customerTypesDao.getCustomerTypes();
		countries = countriesDao.getCountries();
		countries.add(0, getString(R.string.countrySpinnerHint));
		
		// view mode controls
		companyLayout = findViewById(R.id.companyLayout);
		TextView viewCompanySectionTextView = (TextView) findViewById(R.id.viewCompanySectionTextView);
		viewCompanySectionTextView.setText(singular);

		nameLayout = findViewById(R.id.nameLayout);
		idLayout = findViewById(R.id.customerIdLayout);
		typeLayout = findViewById(R.id.typeLayout);
		typeTextView = (TextView) findViewById(R.id.typeTextView);
		nameTextView = (TextView) findViewById(R.id.nameTextView);
		idTextView = (TextView) findViewById(R.id.customerIdTextView);
		phoneLayout = findViewById(R.id.phoneLayout);
		phoneTextView = (TextView) findViewById(R.id.phoneTextView);

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

		pcLayout = findViewById(R.id.pcLayout);
		pcFirstNameLayout = findViewById(R.id.pcFirstNameLayout);
		pcFirstNameTextView = (TextView) findViewById(R.id.pcFirstNameTextView);
		pcLastNameLayout = findViewById(R.id.pcLastNameLayout);
		pcLastNameTextView = (TextView) findViewById(R.id.pcLastNameTextView);
		pcTitleLayout = findViewById(R.id.pcTitleLayout);
		pcTitleTextView = (TextView) findViewById(R.id.pcTitleTextView);
		pcPhoneLayout = findViewById(R.id.pcPhoneLayout);
		pcPhoneTextView = (TextView) findViewById(R.id.pcPhoneTextView);
		pcEmailLayout = findViewById(R.id.pcEmailLayout);
		pcEmailTextView = (TextView) findViewById(R.id.pcEmailTextView);

		scLayout = findViewById(R.id.scLayout);
		scFirstNameLayout = findViewById(R.id.scFirstNameLayout);
		scFirstNameTextView = (TextView) findViewById(R.id.scFirstNameTextView);
		scLastNameLayout = findViewById(R.id.scLastNameLayout);
		scLastNameTextView = (TextView) findViewById(R.id.scLastNameTextView);
		scTitleLayout = findViewById(R.id.scTitleLayout);
		scTitleTextView = (TextView) findViewById(R.id.scTitleTextView);
		scPhoneLayout = findViewById(R.id.scPhoneLayout);
		scPhoneTextView = (TextView) findViewById(R.id.scPhoneTextView);
		scEmailLayout = findViewById(R.id.scEmailLayout);
		scEmailTextView = (TextView) findViewById(R.id.scEmailTextView);
		locationLayout = findViewById(R.id.locationLayout);
		latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
		longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
		viewMapButtonInAddressSection = findViewById(R.id.viewMapButtonInAddressSection);

		// edit mode controls
		TextView editCompanySectionTextView = (TextView) findViewById(R.id.editCompanySectionTextView);
		editCompanySectionTextView.setText(singular);
		nameEditText = (EditText) findViewById(R.id.nameEditText);
		typeEditLayout = findViewById(R.id.typeEditLayout);
		typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
		phoneEditText = (EditText) findViewById(R.id.phoneEditText);
		streetEditText = (EditText) findViewById(R.id.streetEditText);
		areaEditText = (EditText) findViewById(R.id.areaEditText);
		landmarkEditText = (EditText) findViewById(R.id.landmarkEditText);
		cityEditText = (EditText) findViewById(R.id.cityEditText);
		stateEditText = (EditText) findViewById(R.id.stateEditText);
		pinCodeEditText = (EditText) findViewById(R.id.pinCodeEditText);
		countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
		pcFirstNameEditText = (EditText) findViewById(R.id.pcFirstNameEditText);
		pcLastNameEditText = (EditText) findViewById(R.id.pcLastNameEditText);
		pcTitleEditText = (EditText) findViewById(R.id.pcTitleEditText);
		pcPhoneEditText = (EditText) findViewById(R.id.pcPhoneEditText);
		pcEmailEditText = (EditText) findViewById(R.id.pcEmailEditText);
		scFirstNameEditText = (EditText) findViewById(R.id.scFirstNameEditText);
		scLastNameEditText = (EditText) findViewById(R.id.scLastNameEditText);
		scTitleEditText = (EditText) findViewById(R.id.scTitleEditText);
		scPhoneEditText = (EditText) findViewById(R.id.scPhoneEditText);
		scEmailEditText = (EditText) findViewById(R.id.scEmailEditText);

		latitudeEditText = (EditText) findViewById(R.id.latitudeEditText);
		longitudeEditText = (EditText) findViewById(R.id.longitudeEditText);

		viewCustomerFragment = (ViewCustomerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.viewCustomerFragment);
		editCustomerFragment = (EditCustomerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.editCustomerFragment);
		showRightFragment();

		if (types != null && types.size() > 0) {
			ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, getTypeNames());
			typeSpinnerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			typeSpinner.setAdapter(typeSpinnerAdapter);
			typeEditLayout.setVisibility(View.VISIBLE);
		} else {
			typeEditLayout.setVisibility(View.GONE);
		}

		countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, countries);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		countrySpinner.setAdapter(spinnerAdapter);

		getSupportLoaderManager().initLoader(0, null, this);

		fetchInProgress = Utils.isServiceRunning(getApplicationContext(),
				FetchCustomerService.class.getName());
	}

	private void showRightFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		if (editMode) {
			ft.hide(viewCustomerFragment);
			ft.show(editCustomerFragment);
		} else {
			ft.hide(editCustomerFragment);
			ft.show(viewCustomerFragment);
		}

		ft.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.customer, menu);
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		boolean modifyCustomer = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_CUSTOMER_MODIFY, true);
		boolean isDeleted = (currentCustomer != null && currentCustomer
				.getDeleted() != null) ? currentCustomer.getDeleted() : false;

		menu.findItem(R.id.editCustomer)
				.setVisible(
						modifyCustomer
								&& !isDeleted
								&& !editMode
								&& (currentCustomer == null || (currentCustomer != null && currentCustomer
										.getRemoteId() != null)));

		menu.findItem(R.id.saveCustomer).setVisible(editMode);
		menu.findItem(R.id.discardCustomer).setVisible(editMode);

		menu.findItem(R.id.viewJobHistory).setVisible(
				!editMode && !ACTION_PICK.equals(action));

		menu.findItem(R.id.viewJobHistory).setTitle(
				"View "
						+ settingsDao.getLabel(Settings.LABEL_JOB_SINGULAR_KEY,
								Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE)
						+ " history");
		boolean addJob = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_JOB_ADD, true);

		menu.findItem(R.id.addJobToCustomer).setVisible(
				addJob && !editMode && !ACTION_PICK.equals(action));

		menu.findItem(R.id.addJobToCustomer).setTitle(
				"Add "
						+ settingsDao.getLabel(Settings.LABEL_JOB_SINGULAR_KEY,
								Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE));
		setSupportProgressBarIndeterminateVisibility(fetchInProgress);

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("localCustomerId", localCustomerId);
		outState.putBoolean("editMode", editMode);
		outState.putSerializable("originalCustomer", originalCustomer);

		if (editMode) {
			useEditTextValues();
		}

		outState.putSerializable("currentCustomer", currentCustomer);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onResume. Registering fetch done receiver.");
		}

		registerReceiver(fetchDoneReceiver, new IntentFilter(
				FetchCustomerService.ACTION_DONE));
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onPause. Unregistering fetch done receiver.");
		}

		try {
			unregisterReceiver(fetchDoneReceiver);
		} catch (IllegalArgumentException e) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Ignored the exception caught while unregistering fetch done receiver: "
								+ e.toString());
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader for Loader ID: " + id
					+ ", Customer ID: " + localCustomerId);
		}

		return new CursorLoader(getApplicationContext(), Uri.withAppendedPath(
				Customers.CONTENT_URI, "" + localCustomerId),
				Customers.ALL_COLUMNS, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}
		// DatabaseUtils.dumpCursor(cursor);

		if (cursor == null || !cursor.moveToFirst()) {
			if (originalCustomer == null) {
				originalCustomer = new Customer();
			}

			if (currentCustomer == null) {
				currentCustomer = new Customer();
			}

			return;
		}

		// don't touch these job objects, if they already exist
		// so that we don't lose the user's changes due to
		// orientation change
		if (originalCustomer == null || originalCustomer.getPartial()) {
			originalCustomer = new Customer();
			originalCustomer.load(cursor);
		}

		if (currentCustomer == null || currentCustomer.getPartial()) {
			currentCustomer = new Customer();
			currentCustomer.load(cursor);

			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Current customer after loading: "
								+ currentCustomer.toString());
			}

			supportInvalidateOptionsMenu();
		}

		if (!editMode) {
			// company section
			String str = currentCustomer.getName();
			nameTextView.setText(str);
			nameLayout.setVisibility(Utils.getVisibility(str));
			idTextView.setText(currentCustomer.getCustomerNum() + "");
			idLayout.setVisibility(Utils.getVisibility(currentCustomer
					.getCustomerNum()));
			if (types != null && types.size() > 0) {
				if (currentCustomer.getTypeId() == null) {
					typeLayout.setVisibility(View.GONE);
				} else {
					typeLayout.setVisibility(View.VISIBLE);
					typeTextView.setText(customerTypesDao
							.getName(currentCustomer.getTypeId()));
				}
			} else {
				typeLayout.setVisibility(View.GONE);
			}

			str = currentCustomer.getPhone();
			phoneTextView.setText(str);
			phoneLayout.setVisibility(Utils.getVisibility(str));

			companyLayout.setVisibility(Utils.getVisibility(nameLayout,
					phoneLayout));

			// address section
			str = currentCustomer.getStreet();
			streetTextView.setText(str);
			streetLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getArea();
			areaTextView.setText(str);
			areaLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getLandmark();
			landmarkTextView.setText(str);
			landmarkLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getCity();
			cityTextView.setText(str);
			cityLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getState();
			stateTextView.setText(str);
			stateLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getPinCode();
			pinCodeTextView.setText(str);
			pinCodeLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getCountry();
			countryTextView.setText(str);
			countryLayout.setVisibility(Utils.getVisibility(str));

			addressLayout.setVisibility(Utils.getVisibility(streetLayout,
					areaLayout, landmarkLayout, cityLayout, stateLayout,
					pinCodeLayout, countryLayout));

			// primary contact section
			str = currentCustomer.getPcFirstName();
			pcFirstNameTextView.setText(str);
			pcFirstNameLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getPcLastName();
			pcLastNameTextView.setText(str);
			pcLastNameLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getPcTitle();
			pcTitleTextView.setText(str);
			pcTitleLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getPcPhone();
			pcPhoneTextView.setText(str);
			pcPhoneLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getPcEmail();
			pcEmailTextView.setText(str);
			pcEmailLayout.setVisibility(Utils.getVisibility(str));

			pcLayout.setVisibility(Utils.getVisibility(pcFirstNameLayout,
					pcLastNameLayout, pcTitleLayout, pcPhoneLayout,
					pcEmailLayout));

			// secondary contact section
			str = currentCustomer.getScFirstName();
			scFirstNameTextView.setText(str);
			scFirstNameLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getScLastName();
			scLastNameTextView.setText(str);
			scLastNameLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getScTitle();
			scTitleTextView.setText(str);
			scTitleLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getScPhone();
			scPhoneTextView.setText(str);
			scPhoneLayout.setVisibility(Utils.getVisibility(str));

			str = currentCustomer.getScEmail();
			scEmailTextView.setText(str);
			scEmailLayout.setVisibility(Utils.getVisibility(str));

			scLayout.setVisibility(Utils.getVisibility(scFirstNameLayout,
					scLastNameLayout, scTitleLayout, scPhoneLayout,
					scEmailLayout));

			if (currentCustomer.getLatitude() != null
					&& currentCustomer.getLongitude() != null) {
				latitudeTextView.setText(String.valueOf(currentCustomer
						.getLatitude()));
				longitudeTextView.setText(String.valueOf(currentCustomer
						.getLongitude()));
				locationLayout.setVisibility(View.VISIBLE);

				if (addressLayout.getVisibility() == View.VISIBLE) {
					viewMapButtonInAddressSection.setVisibility(View.GONE);
				}
			} else {
				locationLayout.setVisibility(View.GONE);
			}
		} else {
			nameEditText.setText(currentCustomer.getName());

			if (types != null && types.size() > 0) {
				typeEditLayout.setVisibility(View.VISIBLE);

				if (currentCustomer.getTypeId() != null) {
					int index = getTypeIndex(currentCustomer.getTypeId());

					if (index == -1) {
						Toast.makeText(
								this,
								singular
										+ " type "
										+ currentCustomer.getTypeId()
										+ " is no longer available. Picking the first available "
										+ singular + " type.",
								Toast.LENGTH_LONG).show();
						typeSpinner.setSelection(0);
					} else {
						typeSpinner.setSelection(index);
					}
				}
			} else {
				typeEditLayout.setVisibility(View.GONE);
			}

			changeCountrySelection(currentCustomer.getCountry());
			stateEditText.setText(currentCustomer.getState());
			phoneEditText.setText(currentCustomer.getPhone());
			streetEditText.setText(currentCustomer.getStreet());
			areaEditText.setText(currentCustomer.getArea());
			landmarkEditText.setText(currentCustomer.getLandmark());
			cityEditText.setText(currentCustomer.getCity());
			pinCodeEditText.setText(currentCustomer.getPinCode());
			pcFirstNameEditText.setText(currentCustomer.getPcFirstName());
			pcLastNameEditText.setText(currentCustomer.getPcLastName());
			pcTitleEditText.setText(currentCustomer.getPcTitle());
			pcPhoneEditText.setText(currentCustomer.getPcPhone());
			pcEmailEditText.setText(currentCustomer.getPcEmail());
			scFirstNameEditText.setText(currentCustomer.getScFirstName());
			scLastNameEditText.setText(currentCustomer.getScLastName());
			scTitleEditText.setText(currentCustomer.getScTitle());
			scPhoneEditText.setText(currentCustomer.getScPhone());
			scEmailEditText.setText(currentCustomer.getScEmail());

			if (currentCustomer.getLatitude() != null
					&& currentCustomer.getLongitude() != null) {
				latitudeEditText.setText(String.valueOf(currentCustomer
						.getLatitude()));
				longitudeEditText.setText(String.valueOf(currentCustomer
						.getLongitude()));
			}
		}

		if (currentCustomer.getPartial()) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Fetching complete customer details.");
			}
			Intent intent = new Intent(this, FetchCustomerService.class);
			intent.putExtra("remoteCustomerId", currentCustomer.getRemoteId());
			WakefulIntentService.sendWakefulWork(this, intent);
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

		case R.id.editCustomer:
			editMode = true;

			// invalidate to show save and discard buttons
			supportInvalidateOptionsMenu();
			getSupportLoaderManager().restartLoader(0, null, this);
			showRightFragment();

			break;

		case R.id.saveCustomer:
			save();
			break;

		case R.id.discardCustomer:
			discard();
			break;

		case R.id.viewJobHistory:
			Intent intent = new Intent(this, JobsActivity.class);
			intent.putExtra("localCustomerId", localCustomerId);
			startActivity(intent);

			break;

		case R.id.addJobToCustomer:
			Intent jobIntent = new Intent(this, JobActivity.class);
			jobIntent.putExtra("localCustomerId", localCustomerId);
			startActivity(jobIntent);

			break;

		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "No menu item handler found.");
			}
		}

		return super.onOptionsItemSelected(item);
	}

	private void useEditTextValues() {
		currentCustomer.setName(Utils.getString(nameEditText));

		if (types != null && types.size() > 0) {
			String selectedItem = (String) typeSpinner.getSelectedItem();
			if ("None".equals(selectedItem)) {
				currentCustomer.setTypeId(null);
			} else {
				if (selectedItem != null) {
					currentCustomer.setTypeId(customerTypesDao
							.getId(selectedItem));
				}
			}
		} else {
			currentCustomer.setTypeId(null);
		}

		currentCustomer.setPhone(Utils.getString(phoneEditText));
		currentCustomer.setStreet(Utils.getString(streetEditText));
		currentCustomer.setArea(Utils.getString(areaEditText));
		currentCustomer.setLandmark(Utils.getString(landmarkEditText));

		if (countrySpinner.getSelectedItemPosition() != 0) {
			currentCustomer.setCountry((String) countrySpinner
					.getSelectedItem());
		}else{
			currentCustomer.setCountry("");
		}
		
		currentCustomer.setState(Utils.getString(stateEditText));

		currentCustomer.setCity(Utils.getString(cityEditText));
		currentCustomer.setPinCode(Utils.getString(pinCodeEditText));
		currentCustomer.setPcFirstName(Utils.getString(pcFirstNameEditText));
		currentCustomer.setPcLastName(Utils.getString(pcLastNameEditText));
		currentCustomer.setPcTitle(Utils.getString(pcTitleEditText));
		currentCustomer.setPcPhone(Utils.getString(pcPhoneEditText));
		currentCustomer.setPcEmail(Utils.getString(pcEmailEditText));
		currentCustomer.setScFirstName(Utils.getString(scFirstNameEditText));
		currentCustomer.setScLastName(Utils.getString(scLastNameEditText));
		currentCustomer.setScTitle(Utils.getString(scTitleEditText));
		currentCustomer.setScPhone(Utils.getString(scPhoneEditText));
		currentCustomer.setScEmail(Utils.getString(scEmailEditText));
		currentCustomer.setLatitude(Utils.getDouble(latitudeEditText));
		currentCustomer.setLongitude(Utils.getDouble(longitudeEditText));
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
									((CustomerActivity) (getActivity())).save();
								}
							})
					.setNegativeButton(R.string.alert_dialog_discard,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									((CustomerActivity) (getActivity()))
											.discard();
								}
							}).create();
		}

	}

	private void save() {
		useEditTextValues();

		if (TextUtils.isEmpty(currentCustomer.getName())) {
			Toast.makeText(this, singular + " name must be specified.",
					Toast.LENGTH_LONG).show();

			return;
		}

		if (!Utils.isEmailAddressValid(currentCustomer.getPcEmail(), false)) {
			Toast.makeText(this, "Primary contact's email is invalid.",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (!Utils.isEmailAddressValid(currentCustomer.getScEmail(), false)) {
			Toast.makeText(this, "Secondary contact's email is invalid.",
					Toast.LENGTH_LONG).show();
			return;
		}

		String pinCode = currentCustomer.getPinCode();

		if ("India".equals(currentCustomer.getCountry())
				&& !TextUtils.isEmpty(pinCode)) {
			if (!(TextUtils.isDigitsOnly(pinCode) && pinCode.length() == 6)) {
				Toast.makeText(this, "Pin code must be a 6-digit number.",
						Toast.LENGTH_LONG).show();
				return;
			}
		}

		currentCustomer.setDirty(true);
		currentCustomer.setPartial(false);
		currentCustomer.setDeleted(false);
		customersDao.save(currentCustomer);
		Utils.sync(getApplicationContext());
		finish();
	}

	private void discard() {
		finish();
	}

	private boolean hasUnsavedChanges() {
		return !(TextUtils.equals(originalCustomer.getName(),
				currentCustomer.getName())
				&& Utils.integersEqual(originalCustomer.getTypeId(),
						currentCustomer.getTypeId())
				&& TextUtils.equals(originalCustomer.getPhone(),
						currentCustomer.getPhone())
				&& TextUtils.equals(originalCustomer.getStreet(),
						currentCustomer.getStreet())
				&& TextUtils.equals(originalCustomer.getArea(),
						currentCustomer.getArea())
				&& TextUtils.equals(originalCustomer.getLandmark(),
						currentCustomer.getLandmark())
				&& TextUtils.equals(originalCustomer.getCity(),
						currentCustomer.getCity())
				&& TextUtils.equals(originalCustomer.getState(),
						currentCustomer.getState())
				&& TextUtils.equals(originalCustomer.getCountry(),
						currentCustomer.getCountry())
				&& TextUtils.equals(originalCustomer.getPinCode(),
						currentCustomer.getPinCode())
				&& Utils.doublesEqual(originalCustomer.getLatitude(),
						currentCustomer.getLatitude())
				&& Utils.doublesEqual(originalCustomer.getLongitude(),
						currentCustomer.getLongitude())
				&& TextUtils.equals(originalCustomer.getPcFirstName(),
						currentCustomer.getPcFirstName())
				&& TextUtils.equals(originalCustomer.getPcLastName(),
						currentCustomer.getPcLastName())
				&& TextUtils.equals(originalCustomer.getPcTitle(),
						currentCustomer.getPcTitle())
				&& TextUtils.equals(originalCustomer.getPcPhone(),
						currentCustomer.getPcPhone())
				&& TextUtils.equals(originalCustomer.getPcEmail(),
						currentCustomer.getPcEmail())
				&& TextUtils.equals(originalCustomer.getScFirstName(),
						currentCustomer.getScFirstName())
				&& TextUtils.equals(originalCustomer.getScLastName(),
						currentCustomer.getScLastName())
				&& TextUtils.equals(originalCustomer.getScTitle(),
						currentCustomer.getScTitle())
				&& TextUtils.equals(originalCustomer.getScPhone(),
						currentCustomer.getScPhone()) && TextUtils.equals(
				originalCustomer.getScEmail(), currentCustomer.getScEmail()));
	}

	class FetchDoneReceiver extends BroadcastReceiver {
		/**
		 * Static constants cannot be declared in inner classes. Thus, the
		 * following TAG is non-static.
		 */
		public final String TAG = "FetchDoneReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "In onReceive. Restarting loader.");
			}

			getSupportLoaderManager().restartLoader(0, null,
					CustomerActivity.this);
			fetchInProgress = false;
			supportInvalidateOptionsMenu();
		}
	}

	public void onModifyLocationButtonClick(View view) {
		Intent intent = new Intent(this, LocationPickerFromMapActivity.class);
		intent.putExtra("latitude", currentCustomer.getLatitude());
		intent.putExtra("longitude", currentCustomer.getLongitude());
		intent.putExtra("of", currentCustomer.getName());
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

			currentCustomer.setLatitude(doubleLatitude);
			currentCustomer.setLongitude(doubleLongitude);
		}
	}

	public void onViewMapButtonClick(View view) {
		if (currentCustomer == null) {
			return;
		}

		Intent intent = new Intent(this, MapActivity.class);
		intent.putExtra("name", currentCustomer.getName());

		if (currentCustomer.getLatitude() != null
				&& currentCustomer.getLongitude() != null) {
			intent.putExtra("latitude", currentCustomer.getLatitude());
			intent.putExtra("longitude", currentCustomer.getLongitude());
		} else {
			String address = Utils.getAddressForMapDisplay(
					currentCustomer.getStreet(), currentCustomer.getArea(),
					currentCustomer.getCity(), currentCustomer.getState(),
					currentCustomer.getCountry(), currentCustomer.getPinCode());

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Launching map activity for address: " + address);
			}

			intent.putExtra("address", address);
		}

		startActivity(intent);
	}

	private List<String> getTypeNames() {
		if (types == null) {
			return null;
		}

		List<String> names = new ArrayList<String>();

		names.add("None");

		for (CustomerType type : types) {
			names.add(type.getName());
		}

		return names;
	}

	private int getTypeIndex(int id) {
		if (types == null) {
			return -1;
		}

		int numTypes = types.size();

		for (int i = 0; i < numTypes; ++i) {
			if (types.get(i).getId() == id) {
				return i + 1; // + 1 to account for None
			}
		}

		return -1;
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

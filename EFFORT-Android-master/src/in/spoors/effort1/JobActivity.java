package in.spoors.effort1;

import in.spoors.common.EffortDatePickerDialog;
import in.spoors.common.EffortTimePickerDialog;
import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.CountriesDao;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.FormSpecsDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.JobHistoriesDao;
import in.spoors.effort1.dao.JobStageStatusesDao;
import in.spoors.effort1.dao.JobStatesDao;
import in.spoors.effort1.dao.JobTypesDao;
import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.NotesDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Customer;
import in.spoors.effort1.dto.FormSpec;
import in.spoors.effort1.dto.Job;
import in.spoors.effort1.dto.JobHistory;
import in.spoors.effort1.dto.JobStageStatus;
import in.spoors.effort1.dto.JobState;
import in.spoors.effort1.dto.JobType;
import in.spoors.effort1.dto.Note;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Customers;
import in.spoors.effort1.provider.EffortProvider.Notes;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Paint;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class JobActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, TabListener, OnDateSetListener,
		OnTimeSetListener, OnItemClickListener, OnCheckedChangeListener {

	public static final String TAG = "JobActivity";

	private static final int LOADER_NOTES = 3;
	private static final int LOADER_PROOFS = 4;
	// private static final int LOADER_EXTRAS = 5;

	private int proofsTabIndex;
	private int extrasTabIndex;

	public static final int CAPTURE_PROOF_IMAGE = 1;
	public static final int CAPTURE_PROOF_VIDEO = 2;
	public static final int CAPTURE_PROOF_AUDIO = 3;
	public static final int CAPTURE_PROOF_SIGNATURE = 4;
	public static final int PICK_PROOF_IMAGE = 5;
	public static final int PICK_PROOF_VIDEO = 6;
	public static final int PICK_PROOF_AUDIO = 7;
	public static final int PICK_PROOF_SIGNATURE = 8;
	public static final int CAPTURE_EXTRA_IMAGE = 21;
	public static final int CAPTURE_EXTRA_VIDEO = 22;
	public static final int CAPTURE_EXTRA_AUDIO = 23;
	public static final int CAPTURE_EXTRA_SIGNATURE = 24;
	public static final int PICK_EXTRA_IMAGE = 25;
	public static final int PICK_EXTRA_VIDEO = 26;
	public static final int PICK_EXTRA_AUDIO = 27;
	public static final int PICK_EXTRA_SIGNATURE = 28;
	public static final int PICK_CUSTOMER = 31;
	public static final int FILL_FORM = 32;
	public static final int REQUEST_PICK_LOCATION = 33;

	private String mediaPath;

	private long localJobId = 0;
	private boolean editMode;

	/**
	 * Job on activity start
	 */
	private Job originalJob;

	/**
	 * Job that acts as the scratch pad
	 */
	private Job currentJob;

	private EffortDatePickerDialog datePickerDialog;
	private EffortTimePickerDialog timePickerDialog;
	private EditJobFragment editJobFragment;
	private ViewJobFragment viewJobFragment;
	private StagesFragment stagesFragment;
	private NotesFragment notesFragment;
	private ProofsFragment proofsFragment;
	// private ExtrasFragment extrasFragment;

	private CustomersDao customersDao;
	private SettingsDao settingsDao;
	private JobsDao jobsDao;
	private JobTypesDao jobTypesDao;
	private JobStatesDao jobStatesDao;
	private JobStageStatusesDao jobStageStatusesDao;
	private FormSpecsDao formSpecsDao;
	private JobHistoriesDao jobHistoriesDao;
	private NotesDao notesDao;
	private CountriesDao countriesDao;
	private FormsDao formsDao;

	private ProgressUpdateThread progressUpdateThread;

	private TextView titleTextView;
	private TextView typeTextView;
	private TextView stageTextView;
	private View customerViewLayout;
	private TextView customerViewLabel;
	private TextView customerTextView;
	private TextView startTimeTextView;
	private TextView endTimeTextView;
	private View descriptionLayout;
	private TextView descriptionTextView;
	private TextView completionTimeTextView;
	private Button completeButton;
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

	private EditText titleEditText;
	private Spinner typeSpinner;
	private Button customerButton;
	private Button startDateButton;
	private Button startTimeButton;
	private Button endDateButton;
	private Button endTimeButton;
	private EditText descriptionEditText;
	private EditText noteEditText;
	private EditText streetEditText;
	private EditText areaEditText;
	private EditText landmarkEditText;
	private EditText cityEditText;
	private EditText stateEditText;
	private EditText pinCodeEditText;
	private Spinner countrySpinner;
	private CheckBox sameAsCustomerAddressCheckBox;
	private EditText latitudeEditText;
	private EditText longitudeEditText;
	private Button modifyJobLocationButton;

	private List<JobType> types;
	private List<JobState> stages;
	private List<String> countries;

	private ListView notesListView;
	private ListView proofsListView;
	// private ListView extrasListView;

	private String singular;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_job);
		customersDao = CustomersDao.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		jobsDao = JobsDao.getInstance(getApplicationContext());
		jobTypesDao = JobTypesDao.getInstance(getApplicationContext());
		jobStatesDao = JobStatesDao.getInstance(getApplicationContext());
		jobStageStatusesDao = JobStageStatusesDao
				.getInstance(getApplicationContext());
		formSpecsDao = FormSpecsDao.getInstance(getApplicationContext());
		jobHistoriesDao = JobHistoriesDao.getInstance(getApplicationContext());
		notesDao = NotesDao.getInstance(getApplicationContext());
		countriesDao = CountriesDao.getInstance(getApplicationContext());
		formsDao = FormsDao.getInstance(getApplicationContext());
		singular = settingsDao.getLabel(Settings.LABEL_JOB_SINGULAR_KEY,
				Settings.LABEL_JOB_SINGULAR_DEFAULT_VLAUE);

		if (savedInstanceState == null) {
			localJobId = getIntent().getLongExtra(EffortProvider.Jobs._ID, 0);
		} else {
			localJobId = savedInstanceState.getLong("localJobId");
			editMode = savedInstanceState.getBoolean("editMode");
			originalJob = (Job) savedInstanceState
					.getSerializable("originalJob");
			currentJob = (Job) savedInstanceState.getSerializable("currentJob");
			mediaPath = savedInstanceState.getString("mediaPath");
		}

		if (localJobId == 0) {
			Job job = new Job();
			job.setTemporary(true);
			job.setTypeId(jobTypesDao.getDefaultTypeId());
			job.setStateId(jobStatesDao.getStartStateId(jobTypesDao
					.getDefaultTypeId()));
			job.setStartTime(Utils.getDefaultJobStartTime());
			job.setEndTime(Utils.getDefaultJobEndTime());
			job.setDirty(true);
			job.setCompleted(false);
			job.setCached(false);
			job.setApproved(true);
			job.setRead(true);

			long customerId = getIntent().getLongExtra("localCustomerId", 0);

			if (customerId != 0) {
				job.setLocalCustomerId(customerId);
			}

			String employeeId = settingsDao.getString(Settings.KEY_EMPLOYEE_ID);
			if (!TextUtils.isEmpty(employeeId)) {
				job.setEmployeeId(Long.parseLong(employeeId));
			}

			jobsDao.save(job);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "New temporary job: " + job.toString());
			}

			localJobId = job.getLocalId();
			editMode = true;
		}

		if (originalJob == null) {
			originalJob = jobsDao.getJobWithLocalId(localJobId);
		}

		// don't touch these job objects, if they already exist
		// so that we don't lose the user's changes due to
		// orientation change
		if (currentJob == null) {
			currentJob = jobsDao.getJobWithLocalId(localJobId);
		}

		editJobFragment = (EditJobFragment) getSupportFragmentManager()
				.findFragmentById(R.id.editJobFragment);
		viewJobFragment = (ViewJobFragment) getSupportFragmentManager()
				.findFragmentById(R.id.viewJobFragment);
		stagesFragment = (StagesFragment) getSupportFragmentManager()
				.findFragmentById(R.id.stagesFragment);
		notesFragment = (NotesFragment) getSupportFragmentManager()
				.findFragmentById(R.id.notesFragment);
		proofsFragment = (ProofsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.proofsFragment);
		/*
		 * extrasFragment = (ExtrasFragment) getSupportFragmentManager()
		 * .findFragmentById(R.id.extrasFragment);
		 */

		// view mode controls
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		typeTextView = (TextView) findViewById(R.id.typeTextView);
		stageTextView = (TextView) findViewById(R.id.stageTextView);
		customerViewLayout = findViewById(R.id.customerViewLayout);
		customerViewLabel = (TextView) findViewById(R.id.customerViewLabel);
		customerViewLabel.setText(settingsDao.getLabel(
				Settings.LABEL_CUSTOMER_SINGULAR_KEY,
				Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE));
		customerTextView = (TextView) findViewById(R.id.customerTextView);
		startTimeTextView = (TextView) findViewById(R.id.startTimeTextView);
		endTimeTextView = (TextView) findViewById(R.id.endTimeTextView);
		descriptionLayout = findViewById(R.id.descriptionLayout);
		descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		completionTimeTextView = (TextView) findViewById(R.id.completionTimeTextView);
		completeButton = (Button) findViewById(R.id.completeButton);
		completeButton.setText("Complete this " + singular);

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
		TextView jobAddressTextView = (TextView) findViewById(R.id.jobAddressTextView);
		jobAddressTextView.setText(singular + " address");

		TextView customerEditLabel = (TextView) findViewById(R.id.customerEditLabel);
		customerEditLabel.setText(settingsDao.getLabel(
				Settings.LABEL_CUSTOMER_SINGULAR_KEY,
				Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE));
		titleEditText = (EditText) findViewById(R.id.titleEditText);
		typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
		customerButton = (Button) findViewById(R.id.customerButton);
		startDateButton = (Button) findViewById(R.id.startDateButton);
		startTimeButton = (Button) findViewById(R.id.startTimeButton);
		endDateButton = (Button) findViewById(R.id.endDateButton);
		endTimeButton = (Button) findViewById(R.id.endTimeButton);
		descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
		noteEditText = (EditText) findViewById(R.id.noteEditText);

		streetEditText = (EditText) findViewById(R.id.streetEditText);
		areaEditText = (EditText) findViewById(R.id.areaEditText);
		landmarkEditText = (EditText) findViewById(R.id.landmarkEditText);
		cityEditText = (EditText) findViewById(R.id.cityEditText);
		stateEditText = (EditText) findViewById(R.id.stateEditText);
		pinCodeEditText = (EditText) findViewById(R.id.pinCodeEditText);
		sameAsCustomerAddressCheckBox = (CheckBox) findViewById(R.id.sameAsCustomerAddressCheckBox);
		latitudeEditText = (EditText) findViewById(R.id.latitudeEditText);
		longitudeEditText = (EditText) findViewById(R.id.longitudeEditText);
		modifyJobLocationButton = (Button) findViewById(R.id.modifyJobLocationButton);
		types = jobTypesDao.getJobTypes();
		countries = countriesDao.getCountries();
		countries.add(0, getString(R.string.countrySpinnerHint));
		countrySpinner = (Spinner) findViewById(R.id.countrySpinner);
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, countries);
		spinnerAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		countrySpinner.setAdapter(spinnerAdapter);

		notesListView = (ListView) notesFragment.getView().findViewById(
				android.R.id.list);
		notesListView.setOnItemClickListener(this);

		proofsListView = (ListView) proofsFragment.getView().findViewById(
				android.R.id.list);
		proofsListView.setOnItemClickListener(this);
		registerForContextMenu(proofsListView);

		/*
		 * extrasListView = (ListView) extrasFragment.getView().findViewById(
		 * android.R.id.list);
		 */
		// extrasListView.setOnItemClickListener(this);
		// registerForContextMenu(extrasListView);

		if (currentJob.getTypeId() != null) {
			updateStages();
		}

		createTabs(savedInstanceState);
		displayJob();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Activity launched for Local Job ID: " + localJobId);
		}

		setSupportProgressBarIndeterminateVisibility(true);
		new FetchJobDetailsTask().execute("" + localJobId);
	}

	public void onModifyLocationButtonClick(View view) {
		Intent intent = new Intent(this, LocationPickerFromMapActivity.class);
		intent.putExtra("latitude", currentJob.getLatitude());
		intent.putExtra("longitude", currentJob.getLongitude());
		intent.putExtra("of", currentJob.getTitle());
		startActivityForResult(intent, REQUEST_PICK_LOCATION);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateContextMenu");
		}

		if (currentJob.getTemporary()) {
			return;
		}

		if (v == proofsListView) {
			AdapterContextMenuInfo acMenuInfo = (AdapterContextMenuInfo) menuInfo;

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Position: " + acMenuInfo.position + ", id: "
						+ acMenuInfo.id);
			}

			Note note = notesDao.getNoteWithLocalId(acMenuInfo.id);

			String action = note.getActionString(this, false);

			if (!action.startsWith("Tap")) {
				menu.add("Upload immediately");
			}
		}
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo acMenuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, item.getTitle().toString());
			Log.d(TAG, "Note id: " + acMenuInfo.id);
		}

		Note note = notesDao.getNoteWithLocalId(acMenuInfo.id);

		String action = note.getActionString(this, false);

		if (!action.startsWith("Tap")) {
			notesDao.updateUploadPriority(acMenuInfo.id);

			if ("Proofs".equals(getSupportActionBar().getSelectedTab()
					.getText())) {
				startBftsIfRequired(LOADER_PROOFS);
			}

			/*
			 * if ("Extras".equals(getSupportActionBar().getSelectedTab()
			 * .getText())) { startBftsIfRequired(LOADER_EXTRAS); }
			 */
		}

		return super.onContextItemSelected(item);
	}

	private void createTabs(Bundle savedInstanceState) {
		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setTitle(singular);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// we need to ensure that tab indexes are set before creating tabs, so
		// that invalidateOptionsMenu in tab listener methods doesn't
		// unnecessarily show proofs and extras submenus in the job tab
		// TODO
		// if (stages != null && stages.size() > 2) {
		proofsTabIndex = 3;
		extrasTabIndex = 4;
		// }
		/*
		 * else { proofsTabIndex = 2; extrasTabIndex = 3; }
		 */

		Tab jobTab = actionBar.newTab();
		jobTab.setText(singular);
		jobTab.setTabListener(this);
		actionBar.addTab(jobTab);

		// if (stages != null && stages.size() > 2) {
		Tab stagesTab = actionBar.newTab();
		stagesTab.setText("Stages");
		stagesTab.setTabListener(this);
		actionBar.addTab(stagesTab);
		// }

		Tab notesTab = actionBar.newTab();
		notesTab.setText("Notes");
		notesTab.setTabListener(this);
		actionBar.addTab(notesTab);

		Tab proofsTab = actionBar.newTab();
		proofsTab.setText("Proofs");
		proofsTab.setTabListener(this);
		actionBar.addTab(proofsTab);

		// TODO EXTRA TAB HIDING
		/*
		 * Tab extrasTab = actionBar.newTab(); extrasTab.setText("Extras");
		 * extrasTab.setTabListener(this); actionBar.addTab(extrasTab);
		 */

		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState
					.getInt("activeTab"));
		}
	}

	private void displayJob() {
		if (currentJob == null) {
			Toast.makeText(this, singular + " is no longer available locally.",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Current job after loading: " + currentJob.toString());
		}

		LinearLayout stagesLayout = (LinearLayout) findViewById(R.id.stagesLayout);
		stagesLayout.removeAllViews();

		if (stages != null) {

			for (JobState jobState : stages) {
				if (jobState.isDefaultState()) {
					// skip default states
					continue;
				}

				addStage(stagesLayout, jobState);
			}
		}

		if (editMode) {
			titleEditText.setText(currentJob.getTitle());
			titleEditText.requestFocus();

			updateDateTimeButtons();

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "type id: " + currentJob.getTypeId()
						+ ", state id: " + currentJob.getStateId());
			}

			ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<String>(
					this, android.R.layout.simple_spinner_item, getTypeNames());
			typeSpinnerAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			typeSpinner.setAdapter(typeSpinnerAdapter);

			if (currentJob.getTypeId() != null) {
				int index = getTypeIndex(currentJob.getTypeId());

				if (index == -1) {
					Toast.makeText(
							this,
							singular
									+ " type "
									+ currentJob.getTypeId()
									+ " is no longer available. Picking the first available "
									+ singular + " type.", Toast.LENGTH_LONG)
							.show();
					typeSpinner.setSelection(0);
				} else {
					typeSpinner.setSelection(index);
				}

				typeSpinner
						.setOnItemSelectedListener(new TypeSelectedListener());
			}

			// don't allow type to be edited if job has history
			typeSpinner.setEnabled(!jobHistoriesDao.hasHistory(localJobId));

			if (currentJob.getLocalCustomerId() != null) {
				customerButton.setText(customersDao
						.getCompanyNameWithLocalId(currentJob
								.getLocalCustomerId()));
			} else {
				customerButton
						.setText("Pick a "
								+ settingsDao
										.getLabel(
												Settings.LABEL_CUSTOMER_SINGULAR_KEY,
												Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE));
			}

			descriptionEditText.setText(currentJob.getDescription());

			changeCountrySelection(currentJob.getCountry());
			stateEditText.setText(currentJob.getState());

			streetEditText.setText(currentJob.getStreet());
			areaEditText.setText(currentJob.getArea());
			landmarkEditText.setText(currentJob.getLandmark());
			cityEditText.setText(currentJob.getCity());
			pinCodeEditText.setText(currentJob.getPinCode());

			if (currentJob.getLatitude() != null
					&& currentJob.getLongitude() != null) {
				latitudeEditText.setText(String.valueOf(currentJob
						.getLatitude()));
				longitudeEditText.setText(String.valueOf(currentJob
						.getLongitude()));
			}

			sameAsCustomerAddressCheckBox.setEnabled(currentJob
					.getLocalCustomerId() != null
					&& currentJob.getLocalCustomerId() > 0);
			sameAsCustomerAddressCheckBox.setText("Same as "
					+ settingsDao.getLabel(
							Settings.LABEL_CUSTOMER_SINGULAR_KEY,
							Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE)
					+ "'s address");
			sameAsCustomerAddressCheckBox
					.setChecked(sameAsCustomerAddressCheckBox.isEnabled()
							&& (currentJob.getSameAsCustomerAddress() == null || currentJob
									.getSameAsCustomerAddress()));
			enableAddressFields(!sameAsCustomerAddressCheckBox.isChecked());
			sameAsCustomerAddressCheckBox.setOnCheckedChangeListener(this);
		} else {
			SimpleDateFormat dtf = Utils
					.getDateTimeFormat(getApplicationContext());

			titleTextView.setText(currentJob.getTitle());

			String type = null;
			if (currentJob.getTypeId() != null) {
				type = jobTypesDao.getName(currentJob.getTypeId());
			}
			typeTextView.setText(type);

			String state = null;
			if (currentJob.getStateId() != null) {
				state = jobStatesDao.getName(currentJob.getStateId());
			}
			stageTextView.setText(state);

			if (currentJob.getLocalCustomerId() != null) {
				if (settingsDao.getBoolean(Settings.KEY_HIDE_CUSTOMER_INFO,
						false)
						|| !Utils.isMappedCustomer(settingsDao, customersDao,
								currentJob.getLocalCustomerId())) {
					customerTextView.setTextColor(0xff000000);
				} else {
					OnClickListener listener = new OnClickListener() {

						@Override
						public void onClick(View v) {
							onCustomerLinkClick(v);
						}
					};

					customerTextView.setTextColor(0xff0000ee);
					customerTextView.setPaintFlags(customerTextView
							.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
					customerViewLayout.setOnClickListener(listener);
					customerViewLabel.setOnClickListener(listener);
					customerTextView.setOnClickListener(listener);
				}

				customerViewLayout.setVisibility(View.VISIBLE);
				customerTextView.setText(customersDao
						.getCompanyNameWithLocalId(currentJob
								.getLocalCustomerId()));
			} else {
				customerViewLayout.setVisibility(View.GONE);
			}

			startTimeTextView.setText(dtf.format(currentJob.getStartTime()));
			endTimeTextView.setText(dtf.format(currentJob.getEndTime()));

			descriptionTextView.setText(currentJob.getDescription());
			descriptionLayout.setVisibility(Utils.getVisibility(currentJob
					.getDescription()));

			if (currentJob.getCompletionTime() != null) {
				completionTimeTextView.setText(dtf.format(currentJob
						.getCompletionTime()));
				completeButton.setEnabled(false);
			} else {
				completionTimeTextView.setText("Not completed yet.");
				completeButton.setEnabled(true);
			}

			// address section
			String str = currentJob.getStreet();
			streetTextView.setText(str);
			streetLayout.setVisibility(Utils.getVisibility(str));

			str = currentJob.getArea();
			areaTextView.setText(str);
			areaLayout.setVisibility(Utils.getVisibility(str));

			str = currentJob.getLandmark();
			landmarkTextView.setText(str);
			landmarkLayout.setVisibility(Utils.getVisibility(str));

			str = currentJob.getCity();
			cityTextView.setText(str);
			cityLayout.setVisibility(Utils.getVisibility(str));

			str = currentJob.getState();
			stateTextView.setText(str);
			stateLayout.setVisibility(Utils.getVisibility(str));

			str = currentJob.getPinCode();
			pinCodeTextView.setText(str);
			pinCodeLayout.setVisibility(Utils.getVisibility(str));

			str = currentJob.getCountry();
			countryTextView.setText(str);
			countryLayout.setVisibility(Utils.getVisibility(str));

			addressLayout.setVisibility(Utils.getVisibility(streetLayout,
					areaLayout, landmarkLayout, cityLayout, stateLayout,
					pinCodeLayout, countryLayout));

			if (currentJob.getLatitude() != null
					&& currentJob.getLongitude() != null) {
				latitudeTextView.setText(String.valueOf(currentJob
						.getLatitude()));
				longitudeTextView.setText(String.valueOf(currentJob
						.getLongitude()));
				locationLayout.setVisibility(View.VISIBLE);

				if (addressLayout.getVisibility() == View.VISIBLE) {
					viewMapButtonInAddressSection.setVisibility(View.GONE);
				}
			} else {
				locationLayout.setVisibility(View.GONE);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.job, menu);

		int index = getSupportActionBar().getSelectedNavigationIndex();

		boolean hasModifyJobPermission = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_JOB_MODIFY, true);

		menu.setGroupVisible(R.id.proofsItems, index == proofsTabIndex);
		menu.setGroupVisible(R.id.extrasItems, index == extrasTabIndex);

		if (editMode) {
			menu.findItem(R.id.editJob).setVisible(false);
			menu.findItem(R.id.completeJob).setVisible(false);
			menu.findItem(R.id.saveJob).setVisible(true);
			menu.findItem(R.id.discardJob).setVisible(true);
		} else {
			menu.findItem(R.id.saveJob).setVisible(false);
			menu.findItem(R.id.discardJob).setVisible(false);

			if (currentJob != null) {
				if (Utils.longsEqual(currentJob.getEmployeeId(),
						settingsDao.getLong(Settings.KEY_EMPLOYEE_ID))) {
					menu.findItem(R.id.editJob).setVisible(
							hasModifyJobPermission
									&& currentJob.getCompletionTime() == null);
					menu.findItem(R.id.completeJob).setVisible(
							hasModifyJobPermission
									&& currentJob.getCompletionTime() == null);
				} else {
					menu.findItem(R.id.editJob).setVisible(false);
					menu.findItem(R.id.completeJob).setVisible(false);
				}
			} else {
				menu.findItem(R.id.editJob).setVisible(true);
				menu.findItem(R.id.completeJob).setVisible(true);
			}
		}

		boolean canPickFromGallary = settingsDao.getBoolean(
				Settings.KEY_PICK_FROM_GALLARY, true);
		if (!canPickFromGallary) {
			menu.findItem(R.id.pickProofImage).setEnabled(false);
			menu.findItem(R.id.pickProofAudio).setEnabled(false);
			menu.findItem(R.id.pickProofVideo).setEnabled(false);
			menu.findItem(R.id.pickProofSignature).setEnabled(false);
			menu.findItem(R.id.pickExtraImage).setEnabled(false);
			menu.findItem(R.id.pickProofAudio).setEnabled(false);
			menu.findItem(R.id.pickProofVideo).setEnabled(false);
			menu.findItem(R.id.pickProofSignature).setEnabled(false);
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onResume.");
		}

		EffortApplication.activityResumed();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Entered onSaveInstanceState");
		}

		outState.putInt("activeTab", getSupportActionBar()
				.getSelectedNavigationIndex());
		outState.putLong("localJobId", localJobId);
		outState.putBoolean("editMode", editMode);
		outState.putSerializable("originalJob", originalJob);

		if (editMode) {
			useEditTextValues();
		}

		outState.putSerializable("currentJob", currentJob);
		outState.putString("mediaPath", mediaPath);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Leaving onSaveInstanceState");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onPause.");
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader for Loader ID: " + id + ", Job ID: "
					+ localJobId);
		}

		switch (id) {
		case LOADER_NOTES:
			return new CursorLoader(getApplicationContext(),
					EffortProvider.Notes.CONTENT_URI,
					EffortProvider.Notes.ALL_COLUMNS,
					EffortProvider.Notes.LOCAL_JOB_ID + " = ? AND "
							+ EffortProvider.Notes.NOTE_TYPE + " = ?",
					new String[] { "" + localJobId,
							"" + EffortProvider.Notes.NOTE_TYPE_NOTE },
					EffortProvider.Notes.NOTE_TIME + " DESC");

		case LOADER_PROOFS:
			return new CursorLoader(getApplicationContext(),
					EffortProvider.Notes.CONTENT_URI,
					EffortProvider.Notes.ALL_COLUMNS, "("
							+ EffortProvider.Notes.LOCAL_JOB_ID + " = ? AND "
							+ EffortProvider.Notes.NOTE_TYPE + " = ?) OR ("
							+ EffortProvider.Notes.LOCAL_JOB_ID + " = ? AND "
							+ EffortProvider.Notes.NOTE_TYPE + " = ?)",
					new String[] { "" + localJobId,
							"" + EffortProvider.Notes.NOTE_TYPE_PROOF,
							"" + localJobId,
							"" + EffortProvider.Notes.NOTE_TYPE_EXTRA },
					EffortProvider.Notes.NOTE_TIME + " DESC");

			/*
			 * case LOADER_EXTRAS: return new
			 * CursorLoader(getApplicationContext(),
			 * EffortProvider.Notes.CONTENT_URI,
			 * EffortProvider.Notes.ALL_COLUMNS,
			 * EffortProvider.Notes.LOCAL_JOB_ID + " = ? AND " +
			 * EffortProvider.Notes.NOTE_TYPE + " = ?", new String[] { "" +
			 * localJobId, "" + EffortProvider.Notes.NOTE_TYPE_EXTRA },
			 * EffortProvider.Notes.NOTE_TIME + " DESC");
			 */

		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished of loader " + loader.getId());
		}
		// DatabaseUtils.dumpCursor(cursor);

		switch (loader.getId()) {
		case LOADER_NOTES:
			String[] notesFrom = { EffortProvider.Notes.NOTE,
					EffortProvider.Notes.BY_NAME,
					EffortProvider.Notes.NOTE_TIME };
			int[] notesTo = { R.id.noteTextView, R.id.byTextView,
					R.id.timeTextView };

			SimpleCursorAdapter notesAdapter = new SimpleCursorAdapter(this,
					R.layout.list_item_note, cursor, notesFrom, notesTo,
					SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			notesAdapter.setViewBinder(new NotesViewBinder());
			notesFragment.setListAdapter(notesAdapter);
			break;

		case LOADER_PROOFS:
			String[] proofsFrom = { EffortProvider.Notes.MEDIA_ID,
					EffortProvider.Notes.TRANSFER_PERCENTAGE,
					EffortProvider.Notes.BY_NAME,
					EffortProvider.Notes.NOTE_TIME };
			int[] proofsTo = { R.id.mediaIdTextView, R.id.actionTextView,
					R.id.byTextView, R.id.timeTextView };

			SimpleCursorAdapter proofsAdapter = new SimpleCursorAdapter(this,
					R.layout.list_item_proof, cursor, proofsFrom, proofsTo,
					SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
			proofsAdapter.setViewBinder(new NotesViewBinder());
			proofsFragment.setListAdapter(proofsAdapter);
			break;

		/*
		 * case LOADER_EXTRAS: String[] extrasFrom = {
		 * EffortProvider.Notes.MEDIA_ID,
		 * EffortProvider.Notes.TRANSFER_PERCENTAGE,
		 * EffortProvider.Notes.BY_NAME, EffortProvider.Notes.NOTE_TIME }; int[]
		 * extrasTo = { R.id.mediaIdTextView, R.id.actionTextView,
		 * R.id.byTextView, R.id.timeTextView };
		 * 
		 * SimpleCursorAdapter extrasAdapter = new SimpleCursorAdapter(this,
		 * R.layout.list_item_extra, cursor, extrasFrom, extrasTo,
		 * SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		 */
		// extrasAdapter.setViewBinder(new NotesViewBinder());
		// extrasFragment.setListAdapter(extrasAdapter);
		// break;
		}

		if (BuildConfig.DEBUG) {
			Log.d(TAG,
					"Returning from onLoadFinished of loader " + loader.getId());
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoaderReset.");
		}
	}

	private Fragment getFragment(Tab tab) {
		String tabName = tab.getText().toString();

		if (singular.equals(tabName)) {
			if (editMode) {
				return editJobFragment;
			} else {
				return viewJobFragment;
			}
		} else if ("Stages".equals(tabName)) {
			return stagesFragment;
		} else if ("Notes".equals(tabName)) {
			return notesFragment;
		} else if ("Proofs".equals(tabName)) {
			return proofsFragment;
		}
		/*
		 * else if ("Extras".equals(tabName)) { return extrasFragment; }
		 */
		else {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "No fragment found for tab: " + tabName);
			}

			return null;
		}
	}

	private void hideAllFragments(FragmentTransaction ft) {
		ft.hide(viewJobFragment);
		ft.hide(editJobFragment);
		ft.hide(stagesFragment);
		ft.hide(notesFragment);
		ft.hide(proofsFragment);
		// ft.hide(extrasFragment);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onTabSelected: " + tab.getText());
		}

		if (editJobFragment != null && viewJobFragment != null
				&& stagesFragment != null && notesFragment != null
				&& proofsFragment != null) {// && extrasFragment != null) {
			if (singular.equals(tab.getText())) {
				hideAllFragments(ft);

				if (editMode) {
					ft.show(editJobFragment);
				} else {
					ft.show(viewJobFragment);
				}
			} else if ("Stages".equals(tab.getText())) {
				hideAllFragments(ft);
				ft.show(stagesFragment);
			} else if ("Notes".equals(tab.getText())) {
				hideAllFragments(ft);
				ft.show(notesFragment);
				noteEditText.requestFocus();
			} else if ("Proofs".equals(tab.getText())) {
				hideAllFragments(ft);
				startBftsIfRequired(LOADER_PROOFS);
				getSupportLoaderManager().restartLoader(LOADER_PROOFS, null,
						this);
				ft.show(proofsFragment);
			}

			/*
			 * else if ("Extras".equals(tab.getText())) { hideAllFragments(ft);
			 * startBftsIfRequired(LOADER_EXTRAS);
			 * getSupportLoaderManager().restartLoader(LOADER_EXTRAS, null,
			 * this); ft.show(extrasFragment); }
			 */
		}

		supportInvalidateOptionsMenu();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onTabUnselected: " + tab.getText());
		}

		View target = getFragment(tab).getView().findFocus();

		if (target != null) {
			InputMethodManager mgr = (InputMethodManager) getApplicationContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			mgr.hideSoftInputFromWindow(target.getWindowToken(), 0);
		}
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onTabReselected: " + tab.getText());
		}

		if (singular.equals(tab.getText())) {
			hideAllFragments(ft);
			if (editMode) {
				ft.show(editJobFragment);
			} else {
				ft.show(viewJobFragment);
			}
		}
	}

	private void captureImage(int requestCode) {
		if (!Utils.isSDCardValid(this, true)) {
			return;
		}

		// Lenovo P700i doesn't return the intent data in the expected format
		// if we don't specify the path explicitly
		// Explicitly specifying the path worked on HTC Desire Z also
		mediaPath = Environment.getExternalStorageDirectory() + "/EFFORT/img-"
				+ Utils.getDateTimeStamp() + ".jpg";

		File imageFile = new File(mediaPath);
		Uri outputFileUri = Uri.fromFile(imageFile);
		File imageDir = new File(imageFile.getParent());
		if (!imageDir.exists()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Creating directory: " + imageFile.getParent());
			}

			imageDir.mkdirs();
		}

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Capturing image as: " + mediaPath);
		}

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// Note: You need to pass the URI, not the path!
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, requestCode);
	}

	private void captureVideo(int requestCode) {
		if (!Utils.isSDCardValid(this, true)) {
			return;
		}

		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

		// 2013-03-22
		// HTC Desire Z is not saving the video at the given path.
		// Thus, we are not specifying the path, so that it will
		// return the path at which the video was saved in the intent data.
		// This mechanism (not specifying the path explicitly is working on
		// Lenovo P700i phone also
		// mediaPath = Environment.getExternalStorageDirectory() +
		// "/EFFORT/vid-"
		// + Utils.getDateTimeStamp() + ".3gp";
		//
		// Log.d(TAG, "Capturing video as: " + mediaPath);
		//
		// Note: You need to pass the URI, not the path!
		// File videoFile = new File(mediaPath);
		// Uri outputFileUri = Uri.fromFile(videoFile);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

		startActivityForResult(intent, requestCode);
	}

	private void captureAudio(int requestCode) {
		if (!Utils.isSDCardValid(this, true)) {
			return;
		}

		mediaPath = Environment.getExternalStorageDirectory() + "/EFFORT/aud-"
				+ Utils.getDateTimeStamp() + ".amr";

		File audioFile = new File(mediaPath);
		File audioDir = new File(audioFile.getParent());
		if (!audioDir.exists()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Creating directory: " + audioFile.getParent());
			}

			audioDir.mkdirs();
		}
		Uri outputFileUri = Uri.fromFile(audioFile);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Capturing audio as: " + mediaPath);
		}

		Intent intent = new Intent(this, AudioCaptureActivity.class);

		// Note: You need to pass the URI, not the path!
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, requestCode);
	}

	private void captureSignature(int requestCode) {
		if (!Utils.isSDCardValid(this, true)) {
			return;
		}

		mediaPath = Environment.getExternalStorageDirectory() + "/EFFORT/sign-"
				+ Utils.getDateTimeStamp() + ".png";

		File audioFile = new File(mediaPath);
		File audioDir = new File(audioFile.getParent());
		if (!audioDir.exists()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Creating directory: " + audioFile.getParent());
			}

			audioDir.mkdirs();
		}

		Uri outputFileUri = Uri.fromFile(audioFile);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Capturing signature as: " + mediaPath);
		}

		Intent intent = new Intent(this, SignatureCaptureActivity.class);

		// Note: You need to pass the URI, not the path!
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, requestCode);
	}

	private void pickImage(int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");

		// Lenovo P700i doesn't return the intent data in the expected format
		// if we don't specify the path explicitly
		// Explicitly specifying the path worked on HTC Desire Z also
		mediaPath = Environment.getExternalStorageDirectory() + "/EFFORT/img-"
				+ Utils.getDateTimeStamp() + ".jpg";

		File imageFile = new File(mediaPath);
		File imageDir = new File(imageFile.getParent());
		if (!imageDir.exists()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Creating directory: " + imageFile.getParent());
			}

			imageDir.mkdirs();
		}

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Picking compressed image as: " + mediaPath);
		}

		startActivityForResult(Intent.createChooser(intent, "Pick image from"),
				requestCode);
	}

	private void pickSignatureImage(int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(
				Intent.createChooser(intent, "Pick signature image from"),
				requestCode);
	}

	private void pickVideo(int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("video/*");
		startActivityForResult(Intent.createChooser(intent, "Pick video from"),
				requestCode);
	}

	private void pickAudio(int requestCode) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		startActivityForResult(Intent.createChooser(intent, "Pick audio from"),
				requestCode);
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
			return true;

		case R.id.captureProofImage:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating image capture.");
			}

			captureImage(CAPTURE_PROOF_IMAGE);
			return true;

		case R.id.captureExtraImage:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating image capture.");
			}

			captureImage(CAPTURE_EXTRA_IMAGE);
			return true;

		case R.id.pickProofImage:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating image picker.");
			}

			pickImage(PICK_PROOF_IMAGE);
			return true;

		case R.id.pickExtraImage:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating image picker.");
			}

			pickImage(PICK_EXTRA_IMAGE);
			return true;

		case R.id.pickProofSignature:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating signature image picker.");
			}

			pickSignatureImage(PICK_PROOF_SIGNATURE);
			return true;

		case R.id.pickExtraSignature:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating signature image picker.");
			}

			pickSignatureImage(PICK_EXTRA_SIGNATURE);
			return true;

		case R.id.captureProofVideo:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating video capture.");
			}

			captureVideo(CAPTURE_PROOF_VIDEO);
			return true;

		case R.id.captureExtraVideo:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating video capture.");
			}

			captureVideo(CAPTURE_EXTRA_VIDEO);
			return true;

		case R.id.pickProofVideo:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating video picker.");
			}

			pickVideo(PICK_PROOF_VIDEO);
			return true;

		case R.id.pickExtraVideo:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating video picker.");
			}

			pickVideo(PICK_EXTRA_VIDEO);
			return true;

		case R.id.captureProofAudio:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating audio capture.");
			}

			captureAudio(CAPTURE_PROOF_AUDIO);
			return true;

		case R.id.captureExtraAudio:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating audio capture.");
			}

			captureAudio(CAPTURE_EXTRA_AUDIO);
			return true;

		case R.id.captureProofSignature:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating signature capture.");
			}

			captureSignature(CAPTURE_PROOF_SIGNATURE);
			return true;

		case R.id.captureExtraSignature:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating signature capture.");
			}

			captureSignature(CAPTURE_EXTRA_SIGNATURE);
			return true;

		case R.id.pickProofAudio:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating audio picker.");
			}

			pickAudio(PICK_PROOF_AUDIO);
			return true;

		case R.id.pickExtraAudio:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Initiating audio picker.");
			}

			pickAudio(PICK_EXTRA_AUDIO);
			return true;

		case R.id.editJob:
			editMode = true;

			// reselect the tab, so that the fragment is switched to edit
			// fragment
			getSupportActionBar().setSelectedNavigationItem(0);

			// Restart job loader, to fill edit fields with values
			displayJob();
			supportInvalidateOptionsMenu();

			return true;

		case R.id.saveJob:
			save();
			return true;

		case R.id.discardJob:
			discard();
			return true;

		case R.id.completeJob:
			if (currentJob != null) {
				completeJob();
			}

			return true;

			// completionComment = "Job completed.";
			//
			// final EditText completionCommentEditText = new EditText(this);
			// completionCommentEditText
			// .setFilters(new InputFilter[] { new InputFilter.LengthFilter(
			// 500) });
			// completionCommentEditText.setText(completionComment);
			//
			// new AlertDialog.Builder(this)
			// .setTitle("Completion Note")
			// .setMessage("Please enter your completion note")
			// .setView(completionCommentEditText)
			// .setPositiveButton("Complete",
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog,
			// int whichButton) {
			// completionComment = completionCommentEditText
			// .getText().toString();
			// jobsDao.completeJob(localJobId,
			// completionComment);
			// Toast.makeText(JobActivity.this,
			// "Job has been completed.",
			// Toast.LENGTH_LONG).show();
			// finish();
			// }
			// })
			// .setNegativeButton("Don't complete",
			// new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog,
			// int whichButton) {
			// Toast.makeText(JobActivity.this,
			// "Job has been left uncompleted.",
			// Toast.LENGTH_LONG).show();
			// }
			// }).show();
			//

		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "No menu item handler found.");
			}

			return super.onOptionsItemSelected(item);
		}
	}

	public void onPostNoteButtonClick(View view) {
		String noteString = noteEditText.getText().toString().trim();

		if (TextUtils.isEmpty(noteString)) {
			Toast.makeText(this, getText(R.string.note_empty_toast_message),
					Toast.LENGTH_LONG).show();
			return;
		}

		// clear the text box
		noteEditText.setText("");

		Note note = new Note();
		note.setNote(noteString);
		note.setNoteTime(new Date());
		note.setLocalJobId(localJobId);
		note.setNoteType(EffortProvider.Notes.NOTE_TYPE_NOTE);
		note.setMediaType(EffortProvider.Notes.MEDIA_TYPE_NOTE);
		note.setMimeType("comment");
		setEmployeeDetails(note);
		setNoteState(note);
		notesDao.save(note);
		captureLocation(note);
		// Utils.sync(getApplicationContext());

		// Restart notes loader
		getSupportLoaderManager().restartLoader(LOADER_NOTES, null, this);
	}

	private void captureLocation(Note note) {
		Intent intent = new Intent(this, LocationCaptureService.class);
		intent.putExtra(EffortProvider.Locations.PURPOSE,
				EffortProvider.Locations.PURPOSE_NOTE);
		intent.putExtra(EffortProvider.Locations.FOR_ID, note.getLocalId());
		WakefulIntentService.sendWakefulWork(this, intent);
	}

	private void captureLocation(JobHistory jobHistory) {
		Intent intent = new Intent(this, LocationCaptureService.class);
		intent.putExtra(EffortProvider.Locations.PURPOSE,
				EffortProvider.Locations.PURPOSE_HISTORY);
		intent.putExtra(EffortProvider.Locations.FOR_ID,
				jobHistory.getLocalId());
		WakefulIntentService.sendWakefulWork(this, intent);
	}

	private boolean isProofRequest(int requestCode) {
		return requestCode == CAPTURE_PROOF_AUDIO
				|| requestCode == CAPTURE_PROOF_IMAGE
				|| requestCode == CAPTURE_PROOF_VIDEO
				|| requestCode == CAPTURE_PROOF_SIGNATURE
				|| requestCode == PICK_PROOF_AUDIO
				|| requestCode == PICK_PROOF_IMAGE
				|| requestCode == PICK_PROOF_VIDEO
				|| requestCode == PICK_PROOF_SIGNATURE;
	}

	/*
	 * private boolean isExtraRequest(int requestCode) { return requestCode ==
	 * CAPTURE_EXTRA_AUDIO || requestCode == CAPTURE_EXTRA_IMAGE || requestCode
	 * == CAPTURE_EXTRA_VIDEO || requestCode == CAPTURE_EXTRA_SIGNATURE ||
	 * requestCode == PICK_EXTRA_AUDIO || requestCode == PICK_EXTRA_IMAGE ||
	 * requestCode == PICK_EXTRA_VIDEO || requestCode == PICK_EXTRA_SIGNATURE; }
	 */

	/**
	 * Sets by ID (employee ID), and by name (employee name) properties of the
	 * given note object.
	 * 
	 * @param outNote
	 */
	private void setEmployeeDetails(Note outNote) {
		outNote.setById(settingsDao.getLong(Settings.KEY_EMPLOYEE_ID));
		outNote.setByName(settingsDao.getString(Settings.KEY_EMPLOYEE_NAME));
	}

	private void setNoteState(Note outNote) {
		if (currentJob.getCompleted() == null || !currentJob.getCompleted()) {
			outNote.setState(Notes.STATE_PRE_COMPLETE);
		} else {
			outNote.setState(Notes.STATE_POST_COMPLETE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG, "In onActivityResult. requestCode=" + requestCode
					+ ", resultCode=" + resultCode);
		}

		super.onActivityResult(requestCode, resultCode, data);

		boolean compressMedia = settingsDao.getBoolean(
				Settings.KEY_COMPRESS_MEDIA, Settings.DEFAULT_COMPRESS_MEDIA);

		if (resultCode == Activity.RESULT_OK) {
			Note note = new Note();
			note.setNoteTime(new Date());
			note.setLocalJobId(localJobId);

			switch (requestCode) {
			case REQUEST_PICK_LOCATION:
				if (data != null) {
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

					currentJob.setLatitude(doubleLatitude);
					currentJob.setLongitude(doubleLongitude);
				}
				return;
			case CAPTURE_PROOF_IMAGE:
			case CAPTURE_EXTRA_IMAGE:
				note.setMimeType("image/jpeg");
				note.setLocalMediaPath(mediaPath);
				Utils.rotateImageIfRequired(mediaPath, this);

				if (compressMedia) {
					Utils.compressImage(mediaPath, this);
				}

				note.setMediaType(EffortProvider.Notes.MEDIA_TYPE_IMAGE);
				break;

			case PICK_PROOF_IMAGE:
			case PICK_EXTRA_IMAGE:
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Intent data: " + data);
				}

				if (data != null && data.getData() != null) {
					Uri uri = data.getData();

					if (uri != null && "content".equals(uri.getScheme())) {
						Cursor cursor = getContentResolver()
								.query(uri,
										new String[] {
												MediaStore.Images.ImageColumns.DATA,
												MediaStore.Images.ImageColumns.MIME_TYPE },
										null, null, null);
						cursor.moveToFirst();

						if (TextUtils.isEmpty(cursor.getString(0))) {
							Toast.makeText(
									this,
									"Could not get the local media path. Please pick another image, or use another image source.",
									Toast.LENGTH_LONG).show();
							cursor.close();
							return;
						}

						if (compressMedia) {
							Utils.copyFile(this, cursor.getString(0), mediaPath);
							note.setLocalMediaPath(mediaPath);
							note.setMimeType("image/jpeg");
							Utils.compressImage(mediaPath, this);
						} else {
							note.setLocalMediaPath(cursor.getString(0));
							note.setMimeType(cursor.getString(1));
						}

						note.setMediaType(EffortProvider.Notes.MEDIA_TYPE_IMAGE);
						cursor.close();
					} else if (uri != null && "file".equals(uri.getScheme())) {
						if (compressMedia) {
							Utils.copyFile(this, uri.getPath(), mediaPath);
							note.setLocalMediaPath(mediaPath);
							note.setMimeType("image/jpeg");
							Utils.compressImage(mediaPath, this);
						} else {
							note.setMimeType(data.getType());
							note.setLocalMediaPath(uri.getPath());
						}

						note.setMediaType(EffortProvider.Notes.MEDIA_TYPE_IMAGE);
					} else {
						Toast.makeText(
								this,
								"The item you picked is not a valid image. Please pick another image.",
								Toast.LENGTH_LONG).show();
						return;
					}

					if (!note.getMimeType().startsWith("image/")) {
						Toast.makeText(
								this,
								"The item you picked is not an image. Please pick an image.",
								Toast.LENGTH_LONG).show();
						return;
					}
				}

				break;

			case PICK_PROOF_SIGNATURE:
			case PICK_EXTRA_SIGNATURE:
				Log.d(TAG, "Intent data: " + data);
				if (data != null && data.getData() != null) {
					Uri uri = data.getData();

					if (uri != null && "content".equals(uri.getScheme())) {
						Cursor cursor = getContentResolver()
								.query(uri,
										new String[] {
												MediaStore.Images.ImageColumns.DATA,
												MediaStore.Images.ImageColumns.MIME_TYPE },
										null, null, null);
						cursor.moveToFirst();

						if (TextUtils.isEmpty(cursor.getString(0))) {
							Toast.makeText(
									this,
									"Could not get the local media path. Please pick another image, or use another image source.",
									Toast.LENGTH_LONG).show();
							cursor.close();
							return;
						}

						note.setLocalMediaPath(cursor.getString(0));
						note.setMimeType(cursor.getString(1));
						note.setMediaType(EffortProvider.Notes.MEDIA_TYPE_SIGNATURE);
						cursor.close();
					} else if (uri != null && "file".equals(uri.getScheme())) {
						note.setMimeType(data.getType());
						note.setLocalMediaPath(uri.getPath());
						note.setMediaType(EffortProvider.Notes.MEDIA_TYPE_SIGNATURE);
					}

					if (!note.getMimeType().startsWith("image/")) {
						Toast.makeText(
								this,
								"The item you picked is not an image. Please pick an image.",
								Toast.LENGTH_LONG).show();
						return;
					}
				}

				break;

			case CAPTURE_PROOF_VIDEO:
			case CAPTURE_EXTRA_VIDEO:
			case PICK_PROOF_VIDEO:
			case PICK_EXTRA_VIDEO:
				Log.d(TAG, "Intent data: " + data);
				if (data != null && data.getData() != null) {
					Uri uri = data.getData();

					if (uri != null && "content".equals(uri.getScheme())) {
						Cursor cursor = getContentResolver()
								.query(uri,
										new String[] {
												MediaStore.Video.VideoColumns.DATA,
												MediaStore.Video.VideoColumns.MIME_TYPE },
										null, null, null);
						cursor.moveToFirst();
						note.setLocalMediaPath(cursor.getString(0));
						note.setMimeType(cursor.getString(1));
						note.setMediaType(EffortProvider.Notes.MEDIA_TYPE_VIDEO);
						cursor.close();
					} else if (uri != null && "file".equals(uri.getScheme())) {
						note.setMimeType(data.getType());
						note.setLocalMediaPath(uri.getPath());
						note.setMediaType(EffortProvider.Notes.MEDIA_TYPE_VIDEO);
					}

					if (!note.getMimeType().startsWith("video/")) {
						Toast.makeText(
								this,
								"The item you picked is not a video. Please pick a video.",
								Toast.LENGTH_LONG).show();
						return;
					}
				}

				break;

			case CAPTURE_PROOF_AUDIO:
			case CAPTURE_EXTRA_AUDIO:
				note.setMimeType("audio/3gpp");
				note.setLocalMediaPath(mediaPath);
				note.setMediaType(EffortProvider.Notes.MEDIA_TYPE_AUDIO);
				// our custom audio recorder, doesn't return any intent data
				// hence, break here.
				break;

			case CAPTURE_PROOF_SIGNATURE:
			case CAPTURE_EXTRA_SIGNATURE:
				note.setMimeType("image/png");
				note.setLocalMediaPath(mediaPath);
				note.setMediaType(EffortProvider.Notes.MEDIA_TYPE_SIGNATURE);
				// our custom signature recorder, doesn't return any intent data
				// hence, break here.
				break;

			case PICK_PROOF_AUDIO:
			case PICK_EXTRA_AUDIO:
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Intent data: " + data);
				}

				if (data != null && data.getData() != null) {
					Uri uri = data.getData();

					if (uri != null && "content".equals(uri.getScheme())) {
						Cursor cursor = getContentResolver()
								.query(uri,
										new String[] {
												MediaStore.Audio.AudioColumns.DATA,
												MediaStore.Audio.AudioColumns.MIME_TYPE },
										null, null, null);
						cursor.moveToFirst();
						note.setLocalMediaPath(cursor.getString(0));
						note.setMimeType(cursor.getString(1));
						note.setMediaType(EffortProvider.Notes.MEDIA_TYPE_AUDIO);
						cursor.close();
					} else if (uri != null && "file".equals(uri.getScheme())) {
						note.setMimeType(data.getType());
						note.setLocalMediaPath(uri.getPath());
						note.setMediaType(EffortProvider.Notes.MEDIA_TYPE_AUDIO);
					}

					if (!note.getMimeType().startsWith("audio/")) {
						Toast.makeText(
								this,
								"The item you picked is not an audio. Please pick an audio.",
								Toast.LENGTH_LONG).show();
						return;
					}
				}

				break;

			case PICK_CUSTOMER:
				long localCustomerId = data.getLongExtra("localCustomerId", 0);

				if (localCustomerId != 0) {
					if (currentJob.getLocalCustomerId() == null) {
						// check it by default
						sameAsCustomerAddressCheckBox.setChecked(true);
					}

					sameAsCustomerAddressCheckBox.setEnabled(true);

					currentJob.setLocalCustomerId(localCustomerId);
					CustomersDao customersDao = CustomersDao
							.getInstance(getApplicationContext());
					Customer customer = customersDao
							.getCustomerWithLocalId(localCustomerId);
					customerButton.setText(customer.getName());

					if (sameAsCustomerAddressCheckBox.isChecked()) {
						loadCustomerAddress(customer);
					}
				} else {
					currentJob.setLocalCustomerId(null);
					customerButton
							.setText("Pick a "
									+ settingsDao
											.getLabel(
													Settings.LABEL_CUSTOMER_SINGULAR_KEY,
													Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE));
					sameAsCustomerAddressCheckBox.setChecked(false);
					sameAsCustomerAddressCheckBox.setEnabled(false);
				}

				return;

			case FILL_FORM:
				long localFormId = data.getLongExtra("localFormId", 0);
				int stateId = data.getIntExtra(
						EffortProvider.Jobs.JOB_STATE_ID, 0);

				if (localFormId == 0) {
					return;
				}

				if (!currentJob.getCompleted()) {
					currentJob.setStateId(stateId);
					JobHistory jobHistory = jobHistoriesDao.getJobHistory(
							localJobId, stateId, localFormId);

					// don't create duplicate history records
					if (jobHistory == null) {
						jobHistory = new JobHistory();
						jobHistory.load(currentJob);
						jobHistory.setLocalFormId(localFormId == 0 ? null
								: localFormId);
						jobHistoriesDao.save(jobHistory);
						captureLocation(jobHistory);
					}

					int formCount = jobHistoriesDao.getFormCount(localJobId,
							stateId);

					JobState state = jobStatesDao.getJobState(stateId);

					if (formCount >= state.getMinSubmissions()) {
						jobStageStatusesDao.updateDone(localJobId, stateId,
								true, true, currentJob.getTemporary());
					}

					int index = getStateIndex(stateId);

					if (allStagesAreDone()) {
						completeJob();
					} else {
						if (index != -1 && index < stages.size() - 2) {
							int newState = stages.get(index + 1).getId();
							currentJob.setStateId(newState);
						}

						jobsDao.updateState(localJobId, currentJob.getStateId());
					}

					displayJob();
				}

				return;
			}

			setEmployeeDetails(note);

			// set file size
			File file = new File(note.getLocalMediaPath());
			note.setFileSize(file.length());

			setNoteState(note);

			if (isProofRequest(requestCode)) {
				note.setNoteType(EffortProvider.Notes.NOTE_TYPE_PROOF);

				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Note: " + note.toString());
				}

				notesDao.save(note);
				captureLocation(note);
				// Restart proofs loader
				getSupportLoaderManager().restartLoader(LOADER_PROOFS, null,
						this);
				startBftsIfRequired(LOADER_PROOFS);
			}

			/*
			 * else if (isExtraRequest(requestCode)) {
			 * note.setNoteType(EffortProvider.Notes.NOTE_TYPE_EXTRA);
			 * 
			 * if (BuildConfig.DEBUG) { Log.d(TAG, "Note: " + note.toString());
			 * }
			 * 
			 * notesDao.save(note); captureLocation(note); // Restart proofs
			 * loader getSupportLoaderManager().restartLoader(LOADER_EXTRAS,
			 * null, this); startBftsIfRequired(LOADER_EXTRAS); }
			 */

		}

	}

	private void loadCustomerAddress(Customer customer) {
		streetEditText.setText(customer.getStreet());
		areaEditText.setText(customer.getArea());
		landmarkEditText.setText(customer.getLandmark());
		cityEditText.setText(customer.getCity());
		pinCodeEditText.setText(customer.getPinCode());
		changeCountrySelection(customer.getCountry());
		stateEditText.setText(customer.getState());

		if (customer.getLatitude() != null && customer.getLongitude() != null) {
			latitudeEditText.setText(String.valueOf(customer.getLatitude()));
			longitudeEditText.setText(String.valueOf(customer.getLongitude()));
		} else {
			latitudeEditText.setText("");
			longitudeEditText.setText("");
		}
	}

	private void useEditTextValues() {
		currentJob.setTitle(Utils.getString(titleEditText));
		currentJob.setDescription(Utils.getString(descriptionEditText));
		currentJob.setStreet(Utils.getString(streetEditText));
		currentJob.setArea(Utils.getString(areaEditText));
		currentJob.setLandmark(Utils.getString(landmarkEditText));

		if (countrySpinner.getSelectedItemPosition() != 0) {
			currentJob.setCountry((String) countrySpinner.getSelectedItem());
		} else {
			currentJob.setCountry("");
		}

		currentJob.setState(Utils.getString(stateEditText));

		currentJob.setCity(Utils.getString(cityEditText));
		currentJob.setPinCode(Utils.getString(pinCodeEditText));
		currentJob.setSameAsCustomerAddress(sameAsCustomerAddressCheckBox
				.isChecked());

		String latitude = latitudeEditText.getText().toString();

		if (!TextUtils.isEmpty(latitude)) {
			currentJob.setLatitude(Utils.getDouble(latitudeEditText));
		} else {
			currentJob.setLatitude(null);
		}

		String longitude = longitudeEditText.getText().toString();

		if (!TextUtils.isEmpty(longitude)) {
			currentJob.setLongitude(Utils.getDouble(longitudeEditText));
		} else {
			currentJob.setLongitude(null);
		}
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

			if (getIntent().getBooleanExtra("launchedFromNotification", false)) {
				Intent jobsIntent = new Intent(this, JobsActivity.class);
				startActivity(jobsIntent);
			}
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
									((JobActivity) (getActivity())).save();
								}
							})
					.setNegativeButton(R.string.alert_dialog_discard,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									((JobActivity) (getActivity())).discard();
								}
							}).create();
		}

	}

	private void save() {
		useEditTextValues();

		String pinCode = currentJob.getPinCode();

		if (!currentJob.getTemporary()) {
			if ("India".equals(currentJob.getCountry())
					&& !TextUtils.isEmpty(pinCode)) {
				if (!(TextUtils.isDigitsOnly(pinCode) && pinCode.length() == 6)) {
					Toast.makeText(this, "Pin code must be a 6-digit number.",
							Toast.LENGTH_LONG).show();
					return;
				}
			}
		}

		if (TextUtils.isEmpty(currentJob.getTitle())) {
			currentJob.setTitle("Untitled " + singular);
		}

		currentJob.setEmployeeId(settingsDao.getLong(Settings.KEY_EMPLOYEE_ID));
		currentJob.setDirty(true);
		currentJob.setRead(true);
		currentJob.setTemporary(false);
		currentJob.setCached(false);

		jobsDao.save(currentJob);

		// Updating temparory flag to jobstageStatuses false relating to this
		// job
		jobStageStatusesDao.updateTemporaryFlag(currentJob.getLocalId(), false);
		// Updating temparory flag to forms false relating to this job
		formsDao.updateTemporaryFlagToJobRelatedForms(currentJob.getLocalId(),
				false);
		// Updating temparory flag to job histpories false relating to this job
		jobHistoriesDao.updateTemporaryFlagWithLocalJobId(
				currentJob.getLocalId(), false);

		Utils.sync(getApplicationContext());
		finish();

		if (getIntent().getBooleanExtra("launchedFromNotification", false)) {
			Intent jobsIntent = new Intent(this, JobsActivity.class);
			startActivity(jobsIntent);
		}
	}

	private void discard() {

		if (currentJob.getTemporary()) {
			// deleting temporary job
			jobsDao.deleteTemporaryJobs();
		}

		finish();

		if (getIntent().getBooleanExtra("launchedFromNotification", false)) {
			Intent jobsIntent = new Intent(this, JobsActivity.class);
			startActivity(jobsIntent);
		}
	}

	public void onCustomerButtonClick(View view) {
		Intent intent = new Intent(this, CustomersActivity.class);
		intent.setAction(CustomersActivity.ACTION_PICK);
		startActivityForResult(intent, PICK_CUSTOMER);
	}

	public void onStartDateButtonClick(View view) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentJob.getStartTime());

		datePickerDialog = new EffortDatePickerDialog(R.id.startDateButton,
				this, this, cal);
		datePickerDialog.show();
	}

	public void onEndDateButtonClick(View view) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentJob.getEndTime());

		datePickerDialog = new EffortDatePickerDialog(R.id.endDateButton, this,
				this, cal);
		datePickerDialog.show();
	}

	// update text of the date time buttons
	// based on the data from currentJob
	private void updateDateTimeButtons() {
		SimpleDateFormat df = Utils.getDateFormat(getApplicationContext());
		SimpleDateFormat tf = Utils.getTimeFormat(getApplicationContext());

		startDateButton.setText(df.format(currentJob.getStartTime()));
		startTimeButton.setText(tf.format(currentJob.getStartTime()));
		endDateButton.setText(df.format(currentJob.getEndTime()));
		endTimeButton.setText(tf.format(currentJob.getEndTime()));
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Date set for " + datePickerDialog.getUsedForViewId()
					+ ", " + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
		}

		Date date;
		Calendar cal;
		long offsetMillis;

		switch (datePickerDialog.getUsedForViewId()) {
		case R.id.startDateButton:
			offsetMillis = currentJob.getEndTime().getTime()
					- currentJob.getStartTime().getTime();
			date = currentJob.getStartTime();
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			currentJob.setStartTime(cal.getTime());

			if (currentJob.getStartTime().compareTo(currentJob.getEndTime()) > 0) {
				cal.add(Calendar.MILLISECOND, (int) offsetMillis);
				currentJob.setEndTime(cal.getTime());
			}

			break;

		case R.id.endDateButton:
			offsetMillis = currentJob.getStartTime().getTime()
					- currentJob.getEndTime().getTime();
			date = currentJob.getEndTime();
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, monthOfYear);
			cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			currentJob.setEndTime(cal.getTime());

			if (currentJob.getEndTime().compareTo(currentJob.getStartTime()) < 0) {
				cal.add(Calendar.MILLISECOND, (int) offsetMillis);
				currentJob.setStartTime(cal.getTime());
			}

			break;

		default:
			if (BuildConfig.DEBUG) {
				Log.w(TAG,
						"Date picker launched for unknown control. Please fix this.");
			}
		}

		updateDateTimeButtons();
	}

	public void onStartTimeButtonClick(View view) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentJob.getStartTime());

		timePickerDialog = new EffortTimePickerDialog(R.id.startTimeButton,
				this, this, cal);
		timePickerDialog.show();
	}

	public void onEndTimeButtonClick(View view) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentJob.getEndTime());

		timePickerDialog = new EffortTimePickerDialog(R.id.endTimeButton, this,
				this, cal);
		timePickerDialog.show();
	}

	@Override
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Time set for " + timePickerDialog.getUsedForViewId()
					+ ", " + hourOfDay + ":" + minute);
		}

		Date date;
		Calendar cal;
		long offsetMillis;

		switch (timePickerDialog.getUsedForViewId()) {
		case R.id.startTimeButton:
			offsetMillis = currentJob.getEndTime().getTime()
					- currentJob.getStartTime().getTime();
			date = currentJob.getStartTime();
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			currentJob.setStartTime(cal.getTime());

			if (currentJob.getStartTime().compareTo(currentJob.getEndTime()) >= 0) {
				cal.add(Calendar.MILLISECOND, (int) offsetMillis);
				currentJob.setEndTime(cal.getTime());
			}
			break;

		case R.id.endTimeButton:
			offsetMillis = currentJob.getStartTime().getTime()
					- currentJob.getEndTime().getTime();
			date = currentJob.getEndTime();
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			currentJob.setEndTime(cal.getTime());

			if (currentJob.getEndTime().compareTo(currentJob.getStartTime()) <= 0) {
				cal.add(Calendar.MILLISECOND, (int) offsetMillis);
				currentJob.setStartTime(cal.getTime());
			}

			break;

		default:
			if (BuildConfig.DEBUG) {
				Log.w(TAG,
						"Time picker launched for unknown control. Please fix this.");
			}
		}

		updateDateTimeButtons();
	}

	private boolean hasUnsavedChanges() {
		return !(TextUtils
				.equals(originalJob.getTitle(), currentJob.getTitle())
				&& TextUtils.equals(originalJob.getDescription(),
						currentJob.getDescription())
				&& Utils.datesEqual(originalJob.getStartTime(),
						currentJob.getStartTime())
				&& Utils.datesEqual(originalJob.getEndTime(),
						currentJob.getEndTime())
				&& TextUtils.equals(originalJob.getStreet(),
						currentJob.getStreet())
				&& TextUtils
						.equals(originalJob.getArea(), currentJob.getArea())
				&& TextUtils.equals(originalJob.getLandmark(),
						currentJob.getLandmark())
				&& TextUtils
						.equals(originalJob.getCity(), currentJob.getCity())
				&& TextUtils.equals(originalJob.getState(),
						currentJob.getState())
				&& TextUtils.equals(originalJob.getCountry(),
						currentJob.getCountry())
				&& TextUtils.equals(originalJob.getPinCode(),
						currentJob.getPinCode())
				&& Utils.doublesEqual(originalJob.getLatitude(),
						currentJob.getLatitude()) && Utils.doublesEqual(
				originalJob.getLongitude(), currentJob.getLongitude()))
				|| (currentJob.getTemporary() && notesDao
						.jobHasNotes(currentJob.getLocalId()));
	}

	private class NotesViewBinder implements ViewBinder {
		private SimpleDateFormat dtf;
		Note note;

		public NotesViewBinder() {
			dtf = Utils.getDateTimeFormat(getApplicationContext());
			note = new Note();
		}

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			note.clear();
			note.load(cursor, getApplicationContext());

			if (columnIndex == EffortProvider.Notes.MEDIA_ID_INDEX) {

				if (note.isProof() || note.isExtra()) {
					TextView tv = (TextView) view;

					// set media title
					if (note.getMediaId() == null) {
						if (note.getLocalMediaPath() != null) {
							File file = new File(note.getLocalMediaPath());
							tv.setText(file.getName());
						}
					} else {
						tv.setText("" + note.getMediaId());
					}

					// set media icon
					if ((note.getMediaType() != null && note.getMediaType() == Notes.MEDIA_TYPE_IMAGE)) {
						tv.setCompoundDrawablesWithIntrinsicBounds(
								getResources().getDrawable(
										R.drawable.ic_note_type_image), null,
								null, null);
					} else if ((note.getMediaType() != null && note
							.getMediaType() == Notes.MEDIA_TYPE_VIDEO)
							|| note.isVideo()) {
						tv.setCompoundDrawablesWithIntrinsicBounds(
								getResources().getDrawable(
										R.drawable.ic_note_type_video), null,
								null, null);
					} else if ((note.getMediaType() != null && note
							.getMediaType() == Notes.MEDIA_TYPE_AUDIO)
							|| note.isAudio()) {
						tv.setCompoundDrawablesWithIntrinsicBounds(
								getResources().getDrawable(
										R.drawable.ic_note_type_audio), null,
								null, null);
					} else if ((note.getMediaType() != null && note
							.getMediaType() == Notes.MEDIA_TYPE_SIGNATURE)) {
						tv.setCompoundDrawablesWithIntrinsicBounds(
								getResources().getDrawable(
										R.drawable.ic_note_type_signature),
								null, null, null);
					} else {
						tv.setCompoundDrawablesWithIntrinsicBounds(
								getResources().getDrawable(
										R.drawable.ic_note_type_file), null,
								null, null);
					}

					return true;
				}
			}

			if (columnIndex == EffortProvider.Notes.TRANSFER_PERCENTAGE_INDEX) {
				TextView tv = (TextView) view;
				tv.setText(note.getActionString(getApplicationContext(),
						currentJob.getTemporary()));
				return true;
			}

			if (columnIndex == EffortProvider.Notes.NOTE_TIME_INDEX) {
				TextView tv = (TextView) view;

				Date noteTime = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(EffortProvider.Notes.NOTE_TIME_INDEX));

				if (noteTime == null) {
					tv.setText("Time missing");
				} else {
					tv.setText(dtf.format(noteTime));
				}

				return true;
			}

			return false;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onItemClick. Parent=" + parent + ", view=" + view
					+ ", position=" + position + ", id=" + id);
		}

		final Note note = notesDao.getNoteWithLocalId(id);

		if (parent == proofsListView) {
			if (note.getLocalMediaPath() != null) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				File file = new File(note.getLocalMediaPath());
				// mime type must be specified, otherwise
				// some devices such as HTC Desire Z try to open every file as a
				// PDF file.
				intent.setDataAndType(Uri.fromFile(file), note.getMimeType());

				PackageManager pm = getPackageManager();
				ComponentName cn = intent.resolveActivity(pm);

				if (cn == null) {
					Toast.makeText(this,
							"No viewer found for " + note.getMimeType(),
							Toast.LENGTH_LONG).show();
				} else {
					startActivity(intent);
				}

				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Launching actvitiy for intent: "
									+ intent.toString());
				}
			} else {
				if (note.getMediaId() != null) {
					if (note.getDownloadRequested() != null
							&& note.getDownloadRequested()) {
						if (BuildConfig.DEBUG) {
							Log.d(TAG,
									"Cancelling download of note "
											+ note.getLocalId()
											+ " with media id "
											+ note.getMediaId());
						}

						note.setDownloadRequested(null);
					} else {
						if (BuildConfig.DEBUG) {
							Log.d(TAG,
									"Requesting download of note "
											+ note.getLocalId()
											+ " with media id "
											+ note.getMediaId());
						}

						note.setDownloadRequested(true);
					}

					notesDao.save(note);
					Utils.sync(getApplicationContext());

					if (note.isProof()) {
						startBftsIfRequired(LOADER_PROOFS);
					}
					/*
					 * else if (note.isExtra()) {
					 * startBftsIfRequired(LOADER_EXTRAS); }
					 */
				}
			}
		} else {
			if (!Utils.longsEqual(
					settingsDao.getLong(Settings.KEY_EMPLOYEE_ID),
					note.getById())) {
				Toast.makeText(this, "You cannot edit this note.",
						Toast.LENGTH_LONG).show();
				return;
			}

			final EditText commentEditText = new EditText(this);
			commentEditText
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							500) });
			commentEditText.setText(note.getNote());

			new AlertDialog.Builder(this)
					.setTitle("Modify note")
					.setMessage("Please update your note")
					.setView(commentEditText)
					.setPositiveButton("Save",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									note.setNote(commentEditText.getText()
											.toString());
									note.setDirty(true);
									notesDao.save(note);
									Toast.makeText(JobActivity.this,
											"Note has been saved.",
											Toast.LENGTH_LONG).show();
									getSupportLoaderManager().restartLoader(
											LOADER_NOTES, null,
											JobActivity.this);
									Utils.sync(getApplicationContext());
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									Toast.makeText(JobActivity.this,
											"Note has been left as-is.",
											Toast.LENGTH_LONG).show();
								}
							}).show();

		}
	}

	/**
	 * Starts the background file transfer service, if:
	 * <ol>
	 * <li>Connected to network</li>
	 * <li>SD card is mounted</li>
	 * <li>Current job has any downloads that the user requested or has any
	 * pending uploads</li>
	 * <li>Service is not already running</li>
	 * </ol>
	 * 
	 * 
	 * @return true if all the pre-conditions hold, and the service is started
	 *         or it has already been running; false, otherwise.
	 */
	public boolean startBftsIfRequired(int loaderId) {
		boolean includeLargeFiles = Utils
				.includeLargeFiles(getApplicationContext());

		boolean bftsRequired = Utils.isConnected(getApplicationContext())
				&& Utils.isSDCardValid(getApplicationContext(), false)
				&& notesDao.hasPendingTransfers(getApplicationContext(),
						localJobId, includeLargeFiles);

		if (bftsRequired) {
			if (progressUpdateThread != null) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"Interrupting the current progress update thread.");
				}

				progressUpdateThread.interrupt();
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Starting a new progress update thread.");
			}

			progressUpdateThread = new ProgressUpdateThread(loaderId);
			progressUpdateThread.start();

			if (Utils.isServiceRunning(getApplicationContext(),
					"BackgroundFileTransferService")) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "BFTS is already running.");
				}

				return true;
			} else {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Starting BFTS.");
				}

				WakefulIntentService.sendWakefulWork(this,
						BackgroundFileTransferService.class);
				return true;
			}
		} else {
			return false;
		}
	}

	private class ProgressUpdateThread extends Thread {

		public static final String TAG = "ProgressUpdateThread";
		private int loaderId;

		public ProgressUpdateThread(int loaderId) {
			this.loaderId = loaderId;
		}

		@Override
		public void run() {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Progress update thread started for loader "
						+ loaderId);
			}

			while (!isInterrupted()) {
				// Restart proofs loader
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Restrted loader " + loaderId);
				}

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						getSupportLoaderManager().restartLoader(loaderId, null,
								JobActivity.this);
					}
				});

				if (!notesDao.hasPendingTransfers(getApplicationContext(),
						localJobId,
						Utils.includeLargeFiles(getApplicationContext()))) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "No pending transfers for job " + localJobId
								+ ". Returning from progress update thread.");
					}

					return;
				}

				try {
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "Sleeping for a second.");
					}

					sleep(1000);

					if (BuildConfig.DEBUG) {
						Log.d(TAG, "Woke up from sleep.");
					}
				} catch (InterruptedException e) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG,
								"Thread interrupted. Returning from run method.");
					}

					return;
				}
			}
		}
	}

	public void onCustomerLinkClick(View view) {
		if (currentJob != null && currentJob.getLocalCustomerId() != null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Launching customer activity for customer id "
						+ currentJob.getLocalCustomerId());
			}

			Intent intent = new Intent(this, CustomerActivity.class);
			intent.putExtra(Customers._ID, currentJob.getLocalCustomerId());
			startActivity(intent);
		}
	}

	private List<String> getTypeNames() {
		if (types == null) {
			return null;
		}

		List<String> names = new ArrayList<String>();

		for (JobType type : types) {
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
				return i;
			}
		}

		return -1;
	}

	private int getStateIndex(int id) {
		if (stages == null) {
			return -1;
		}

		int numStates = stages.size();

		for (int i = 0; i < numStates; ++i) {
			if (stages.get(i).getId() == id) {
				return i;
			}
		}

		return -1;
	}

	private class FetchJobDetailsTask extends AsyncTask<String, Integer, Long> {

		@Override
		protected Long doInBackground(String... params) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In doInBackground.");
			}

			jobsDao.updateReadFlag(localJobId, true);
			AndroidHttpClient httpClient = null;

			try {
				Job job = null;

				if (localJobId != 0) {
					job = jobsDao.getJobWithLocalId(localJobId);
				}

				if (job == null || job.getRemoteId() == null
						|| (job.getCached() != null && !job.getCached())
						|| job.getCached() == null) {
					return null;
				}

				String url = getUrl(job.getRemoteId());

				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Fetch job notes URL: " + url);
				}

				httpClient = AndroidHttpClient.newInstance("EFFORT");
				HttpPost httpPost = new HttpPost(url);
				Utils.setTimeouts(httpPost);

				String existedFormSpecs = Utils
						.getExistedFormSpecsAsString(getApplicationContext());

				List<NameValuePair> namedValuePairs = new ArrayList<NameValuePair>();
				namedValuePairs.add(new BasicNameValuePair("formSpecIds",
						existedFormSpecs));

				HttpEntity requestEntity = new UrlEncodedFormEntity(
						namedValuePairs);
				httpPost.setEntity(requestEntity);

				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Fetch jobs forms BODY: " + requestEntity);
				}

				HttpResponse httpResponse = httpClient.execute(httpPost);
				String response = EntityUtils
						.toString(httpResponse.getEntity());

				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Response JSON: " + response);
				}

				JSONTokener tokener = new JSONTokener(response);
				Object obj = tokener.nextValue();

				if (!(obj instanceof JSONObject)) {
					Log.e(TAG,
							"Invalid job details response. Expected a JSON object but did not get it.");
					return null;
				}

				JSONObject jsonObject = (JSONObject) obj;
				FormResponse formResponse = new FormResponse(
						getApplicationContext());
				formResponse.parse(response);
				boolean isCached = false;
				formResponse.save(isCached);
				JSONArray jsonArray = jsonObject.getJSONArray("comments");
				List<Note> addedNotes = new ArrayList<Note>(jsonArray.length());
				Utils.addNotes(jsonArray, addedNotes, getApplicationContext());

				for (Note note : addedNotes) {
					notesDao.save(note);
				}

				jsonArray = jsonObject.getJSONArray("histories");
				List<JobHistory> addedHistories = new ArrayList<JobHistory>(
						jsonArray.length());
				Utils.addHistories(jsonArray, addedHistories,
						getApplicationContext());

				for (JobHistory history : addedHistories) {
					jobHistoriesDao.save(history);
				}

				jsonArray = jsonObject.getJSONArray("states");
				List<JobStageStatus> addedStatuses = new ArrayList<JobStageStatus>(
						jsonArray.length());
				Utils.addStatuses(jsonArray, addedStatuses,
						getApplicationContext());

				for (JobStageStatus responseStatus : addedStatuses) {
					Long localJobId = responseStatus.getLocalJobId();

					if (localJobId == null) {
						localJobId = jobsDao.getLocalId(responseStatus
								.getRemoteJobId());
					}

					jobStageStatusesDao.updateDone(localJobId,
							responseStatus.getStateId(),
							responseStatus.isDone(), false,
							currentJob.getTemporary());
				}

				jobsDao.updateCachedFlag(localJobId, false);
			} catch (MalformedURLException e) {
				Log.e(TAG, "Bad URL: " + e.toString(), e);
			} catch (IOException e) {
				Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),
								"Failed to fetch notes due to network error.",
								Toast.LENGTH_LONG).show();
					}
				});
			} catch (JSONException e) {
				Log.e(TAG, "Failed to parse JSON response: " + e.toString(), e);
			} catch (ParseException e) {
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
				Log.d(TAG, "Fetch finished.");
			}

			getSupportLoaderManager().initLoader(LOADER_NOTES, null,
					JobActivity.this);
			getSupportLoaderManager().initLoader(LOADER_PROOFS, null,
					JobActivity.this);
			/*
			 * getSupportLoaderManager().initLoader(LOADER_EXTRAS, null,
			 * JobActivity.this);
			 */

			displayJob();

			setSupportProgressBarIndeterminateVisibility(false);
		}

		private String getUrl(long remoteJobId) {
			String serverBaseUrl = getString(R.string.server_base_url);
			Builder builder = Uri
					.parse(serverBaseUrl)
					.buildUpon()
					.appendEncodedPath(
							"service/visit/details/all/" + remoteJobId + "/"
									+ settingsDao.getString("employeeId"));
			Utils.appendCommonQueryParameters(getApplicationContext(), builder);
			Uri syncUri = builder.build();
			return syncUri.toString();
		}
	}

	private void updateStages() {
		if (currentJob != null && currentJob.getTypeId() != null) {
			stages = jobStatesDao.getJobStates(currentJob.getTypeId());

			if (stages == null) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "State list for type " + currentJob.getTypeId()
							+ " is null.");
				}

				return;
			} else {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "State list for type " + currentJob.getTypeId()
							+ " has " + stages.size() + " states.");
				}
				if (stages.size() > 2) {
					TextView tv = (TextView) findViewById(R.id.emptyStagesView);
					tv.setVisibility(View.GONE);
				} else {
					TextView tv = (TextView) findViewById(R.id.emptyStagesView);
					tv.setVisibility(View.VISIBLE);
				}
			}

			if (currentJob.getTemporary() && stages.size() > 2) {
				currentJob.setStateId(stages.get(1).getId());
			}
		}
	}

	private class TypeSelectedListener implements OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (currentJob != null && editMode) {
				int typeId = types.get(position).getId();

				if (currentJob.getTypeId() == null
						|| (currentJob.getTypeId() != null && currentJob
								.getTypeId() != typeId)) {
					if (BuildConfig.DEBUG) {
						Log.d(TAG, "Changing type id to " + typeId);
					}

					currentJob.setTypeId(typeId);
					updateStages();
					useEditTextValues();
					displayJob();
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "No type selected.");
			}
		}

	}

	// public void onFormEditButtonClick(View view) {
	// if (currentJob != null && currentJob.getTypeId() != null
	// && currentJob.getStateId() != null) {
	// JobState state = jobStatesDao.getJobState(currentJob.getStateId());
	//
	// if (state.getFormSpecId() != null) {
	// Intent intent = new Intent(this, FormActivity.class);
	// intent.setAction("fill");
	// intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID,
	// state.getFormSpecId());
	// intent.putExtra(EffortProvider.Forms._ID, localFormId);
	// intent.putExtra("editMode", true);
	// startActivityForResult(intent, FILL_FORM);
	// }
	// }
	// }
	//
	// public void onFormViewButtonClick(View view) {
	// if (currentJob != null && currentJob.getTypeId() != null
	// && currentJob.getStateId() != null) {
	// JobState state = jobStatesDao.getJobState(currentJob.getStateId());
	//
	// if (state.getFormSpecId() != null) {
	// if (localFormId != 0) {
	// Intent intent = new Intent(this, FormActivity.class);
	// intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID,
	// state.getFormSpecId());
	// intent.putExtra(EffortProvider.Forms._ID, localFormId);
	// startActivity(intent);
	// }
	// }
	// }
	// }

	private boolean allStagesAreDone() {
		for (JobState state : stages) {
			if (state.isDefaultState()) {
				continue;
			}

			if (!jobStageStatusesDao.isDoneForCompletion(localJobId,
					state.getId())) {
				return false;
			}
		}

		return true;
	}

	private void addStage(LinearLayout layout, final JobState state) {
		LayoutParams labelLayoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);

		TextView labelTV = new TextView(this);
		labelTV.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		labelTV.setText(state.getName()
				+ (state.isMandatoryForCompletion() ? "*" : ""));

		LayoutParams checkBoxlayoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		final int stateId = state.getId();
		boolean isDone = jobStageStatusesDao.isDone(localJobId, stateId);
		CheckBox checkBox = new CheckBox(this);

		/* if (currentJob.getCompleted() || currentJob.getTemporary()) { */
		if (currentJob.getCompleted()) {
			checkBox.setEnabled(false);
		} else {
			checkBox.setEnabled(true);
		}

		checkBox.setChecked(isDone);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				int index = getStateIndex(stateId);

				if (index == -1) {
					return;
				}

				if (isChecked) {
					int formCount = jobHistoriesDao.getFormCount(localJobId,
							state.getId());

					// check if form has been filled
					if (state.getFormSpecId() != null
							&& formCount < state.getMinSubmissions()) {
						FormSpec formSpec = formSpecsDao.getFormSpec(state
								.getFormSpecId());

						if (state.getMinSubmissions() == 1) {
							Toast.makeText(
									JobActivity.this,
									formSpec.getTitle()
											+ " is required. Please fill it.",
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(
									JobActivity.this,
									"You must submit at least "
											+ state.getMinSubmissions() + " "
											+ formSpec.getTitle()
											+ " forms. Please fill it.",
									Toast.LENGTH_LONG).show();
						}

						buttonView.setChecked(false);

						return;
					}

					jobStageStatusesDao.updateDone(localJobId, stateId,
							isChecked, true, currentJob.getTemporary());

					saveHistory(state);

					if (allStagesAreDone()) {
						completeJob();
					} else {
						if (index < stages.size() - 2) {
							int newState = stages.get(index + 1).getId();
							currentJob.setStateId(newState);
							jobsDao.updateState(localJobId, newState);
						}
					}

					displayJob();
				} else {
					if (!state.isRevisitable()) {
						Toast.makeText(JobActivity.this,
								state.getName() + " cannot be unchecked.",
								Toast.LENGTH_LONG).show();
						buttonView.setChecked(true);
						return;
					}

					jobStageStatusesDao.updateDone(localJobId, stateId,
							isChecked, true, currentJob.getTemporary());
					currentJob.setStateId(stateId);
					jobsDao.updateState(localJobId, stateId);
					displayJob();
				}
			}
		});

		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		linearLayout.addView(labelTV, labelLayoutParams);
		linearLayout.addView(checkBox, checkBoxlayoutParams);

		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin = getResources().getDimensionPixelSize(
				R.dimen.label_margin_left);
		layoutParams.rightMargin = getResources().getDimensionPixelSize(
				R.dimen.label_margin_right);
		layoutParams.topMargin = getResources().getDimensionPixelSize(
				R.dimen.label_margin_top);
		layoutParams.bottomMargin = getResources().getDimensionPixelSize(
				R.dimen.label_margin_bottom);

		layout.addView(linearLayout, layoutParams);

		List<JobHistory> histories = jobHistoriesDao.getHistories(
				currentJob.getLocalId(), state.getId());

		if (histories != null) {
			for (int i = 0; i < histories.size(); ++i) {
				JobHistory history = histories.get(i);
				addHistory(layout, state, history, i == histories.size() - 1);
				addGap(this, layout);
			}
		} else {
			addHistory(layout, state, null, true);
		}

		FormActivity.addSeparator(this, layout);
	}

	private void saveHistory(JobState state) {
		JobHistory jobHistory = new JobHistory();
		jobHistory.load(currentJob);
		jobHistory.setStateId(state.getId());
		jobHistoriesDao.save(jobHistory);
		captureLocation(jobHistory);
	}

	private void addHistory(LinearLayout layout, final JobState state,
			final JobHistory history, boolean isLastHistory) {
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin = getResources().getDimensionPixelSize(
				R.dimen.label_margin_left);
		layoutParams.rightMargin = getResources().getDimensionPixelSize(
				R.dimen.label_margin_right);
		layoutParams.topMargin = getResources().getDimensionPixelSize(
				R.dimen.label_margin_top);
		layoutParams.bottomMargin = getResources().getDimensionPixelSize(
				R.dimen.label_margin_bottom);

		LayoutParams buttonLayoutParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		buttonLayoutParams.leftMargin = getResources().getDimensionPixelSize(
				R.dimen.edit_margin_left);
		buttonLayoutParams.rightMargin = getResources().getDimensionPixelSize(
				R.dimen.edit_margin_right);
		buttonLayoutParams.topMargin = getResources().getDimensionPixelSize(
				R.dimen.edit_margin_top);
		buttonLayoutParams.bottomMargin = getResources().getDimensionPixelSize(
				R.dimen.edit_margin_bottom);

		TextView timeTV = new TextView(this);
		timeTV.setTextAppearance(this, android.R.style.TextAppearance_Small);

		if (history != null) {
			Date time = history.getRemoteCreationTime() != null ? history
					.getRemoteCreationTime() : history.getLocalCreationTime();
			timeTV.setText("Activity time: "
					+ Utils.getDateTimeFormat(getApplicationContext()).format(
							time));
			layout.addView(timeTV, layoutParams);
		}

		if (state.getFormSpecId() != null) {
			String formTitle = formSpecsDao.getFormTitle(state.getFormSpecId());
			String label = "";

			int formCount = jobHistoriesDao.getFormCount(localJobId,
					state.getId());

			if (history == null || history.getLocalFormId() == null) {
				label = "Fill " + formTitle
						+ (formCount < state.getMinSubmissions() ? "*" : "");
			} else {
				label = "View " + formTitle;
			}

			Button button = new Button(this);
			button.setText(label);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// long formSpecId = formSpecsDao.getLatestFormSpecId(state
					// .getFormSpecId());

					// if (BuildConfig.DEBUG) {
					// Log.i(TAG, "Launching form activity for form spec id "
					// + state.getFormSpecId());
					// Log.i(TAG,
					// "Latest version of form spec id "
					// + state.getFormSpecId() + " is "
					// + formSpecId);
					// }

					Intent intent = new Intent(JobActivity.this,
							FormActivity.class);
					intent.setAction("fill");

					intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID,
							state.getFormSpecId());
					intent.putExtra(EffortProvider.Jobs.JOB_STATE_ID,
							state.getId());
					if (currentJob.getTemporary() != null
							&& currentJob.getTemporary() == true) {
						intent.putExtra(EffortProvider.JobHistories.TEMPORARY,
								true);
					} else {
						intent.putExtra(EffortProvider.JobHistories.TEMPORARY,
								false);
					}

					if (currentJob.getLocalCustomerId() != null) {
						intent.putExtra(FormActivity.LOCAL_CUSTOMER_ID,
								currentJob.getLocalCustomerId());
					}

					if (history != null && history.getLocalFormId() != null) {
						intent.putExtra(EffortProvider.Forms._ID,
								history.getLocalFormId());
					}

					// intent.putExtra("editMode", true);
					startActivityForResult(intent, FILL_FORM);
				}
			});

			/*
			 * if (!currentJob.getTemporary() && ((!currentJob.getCompleted() &&
			 * history == null && formCount < state .getMaxSubmissions()) ||
			 * (history != null && history .getLocalFormId() != null))) {
			 * layout.addView(button, buttonLayoutParams); }
			 */
			if (((!currentJob.getCompleted() && history == null && formCount < state
					.getMaxSubmissions()) || (history != null && history
					.getLocalFormId() != null))) {
				layout.addView(button, buttonLayoutParams);
			}
			if (!currentJob.getCompleted() && history != null && isLastHistory
					&& formCount < state.getMaxSubmissions()) {
				Button fillAnotherFormbutton = new Button(this);

				fillAnotherFormbutton.setText("Fill new " + formTitle
						+ (formCount < state.getMinSubmissions() ? "*" : ""));
				fillAnotherFormbutton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						long formSpecId = state.getFormSpecId();
						if (BuildConfig.DEBUG) {
							Log.i(TAG,
									"Launching form activity for local form id "
											+ state.getFormSpecId());
							Log.i(TAG, "Latest version of form spec id "
									+ state.getFormSpecId() + " is "
									+ formSpecId);
						}

						Intent intent = new Intent(JobActivity.this,
								FormActivity.class);
						intent.setAction("fill");

						if (currentJob.getLocalCustomerId() != null) {
							intent.putExtra(FormActivity.LOCAL_CUSTOMER_ID,
									currentJob.getLocalCustomerId());
						}

						intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID,
								formSpecId);
						intent.putExtra(EffortProvider.Jobs.JOB_STATE_ID,
								state.getId());

						// intent.putExtra("editMode", true);
						startActivityForResult(intent, FILL_FORM);
					}
				});

				layout.addView(fillAnotherFormbutton, buttonLayoutParams);
			}
		}
	}

	private void completeJob() {
		if (currentJob == null) {
			return;
		}

		if (!allStagesAreDone()) {
			Toast.makeText(
					this,
					"You must complete the mandatory stages before completing this "
							+ singular + ".", Toast.LENGTH_LONG).show();
			return;
		}

		new AlertDialog.Builder(JobActivity.this)
				.setTitle("Complete this " + singular)
				.setMessage("Do you want to complete this " + singular + "?")
				.setPositiveButton("Complete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								jobsDao.completeJob(localJobId, null);
								int endStateId = jobStatesDao
										.getEndStateId(currentJob.getTypeId());
								saveHistory(jobStatesDao
										.getJobState(endStateId));
								jobsDao.updateState(localJobId, jobStatesDao
										.getEndStateId(currentJob.getTypeId()));
								Toast.makeText(JobActivity.this,
										singular + " has been completed.",
										Toast.LENGTH_LONG).show();
								if (currentJob.getTemporary()) {
									save();
								}
								finish();
							}
						})
				.setNegativeButton("Don't complete",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Toast.makeText(
										JobActivity.this,
										singular
												+ " has been left uncompleted.",
										Toast.LENGTH_LONG).show();
							}
						}).show();
	}

	public void onCompleteButtonClick(View view) {
		completeJob();
	}

	public void addGap(Context context, LinearLayout layout) {
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				5 /* pixels */);
		View separator = new View(context);
		layout.addView(separator, layoutParams);
	}

	public void onViewMapButtonClick(View view) {
		if (currentJob == null) {
			return;
		}

		Intent intent = new Intent(this, MapActivity.class);
		intent.putExtra("name", currentJob.getTitle());

		if (currentJob.getLatitude() != null
				&& currentJob.getLongitude() != null) {
			intent.putExtra("latitude", currentJob.getLatitude());
			intent.putExtra("longitude", currentJob.getLongitude());
		} else {
			String address = Utils.getAddressForMapDisplay(
					currentJob.getStreet(), currentJob.getArea(),
					currentJob.getCity(), currentJob.getState(),
					currentJob.getCountry(), currentJob.getPinCode());

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

	private void enableAddressFields(boolean enabled) {
		streetEditText.setEnabled(enabled);
		areaEditText.setEnabled(enabled);
		landmarkEditText.setEnabled(enabled);
		cityEditText.setEnabled(enabled);
		pinCodeEditText.setEnabled(enabled);
		countrySpinner.setEnabled(enabled);
		stateEditText.setEnabled(enabled);
		latitudeEditText.setEnabled(enabled);
		longitudeEditText.setEnabled(enabled);
		modifyJobLocationButton.setEnabled(enabled);

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			if (currentJob.getLocalCustomerId() != null
					&& currentJob.getLocalCustomerId() > 0) {
				CustomersDao customersDao = CustomersDao
						.getInstance(getApplicationContext());
				Customer customer = customersDao
						.getCustomerWithLocalId(currentJob.getLocalCustomerId());
				loadCustomerAddress(customer);
			}

			enableAddressFields(false);
		} else {
			enableAddressFields(true);
		}
	}

}

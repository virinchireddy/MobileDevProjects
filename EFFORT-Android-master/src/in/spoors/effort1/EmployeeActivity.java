package in.spoors.effort1;

import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Employee;
import in.spoors.effort1.provider.EffortProvider.Employees;
import in.spoors.effort1.provider.EffortProvider.Settings;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class EmployeeActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor> {

	public static final String TAG = "EmployeeActivity";
	private String action;

	private long localEmployeeId;

	/**
	 * Employee that acts as the scratch pad
	 */
	private Employee currentEmployee;

	// view mode controls

	private View detailLayout;
	private View firstNameLayout;
	private TextView firstNameTextView;
	private View lastNameLayout;
	private TextView lastNameTextView;
	private View phoneLayout;
	private TextView phoneTextView;
	private View emailLayout;
	private TextView emailTextView;
	private String singular;
	private SettingsDao settingsDao;

	// edit mode controls

	// private EditEmployeeFragment editEmployeeFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_employee);

		Intent intent = getIntent();
		action = intent.getAction();
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Intent action: " + action);
		}

		if (savedInstanceState == null) {
			localEmployeeId = getIntent().getLongExtra(Employees._ID, 0);
		} else {
			localEmployeeId = savedInstanceState.getLong("localEmployeeId");
			currentEmployee = (Employee) savedInstanceState
					.getSerializable("currentEmployee");
		}
		settingsDao = SettingsDao.getInstance(getApplicationContext());

		singular = settingsDao.getLabel(Settings.LABEL_EMPLOYEE_SINGULAR_KEY,
				Settings.LABEL_EMPLOYEE_SINGULAR_DEFAULT_VLAUE);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(singular);

		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Activity launched for Local Employee ID: "
					+ localEmployeeId);
		}

		detailLayout = findViewById(R.id.pcLayout);
		firstNameLayout = findViewById(R.id.pcFirstNameLayout);
		firstNameTextView = (TextView) findViewById(R.id.pcFirstNameTextView);
		lastNameLayout = findViewById(R.id.pcLastNameLayout);
		lastNameTextView = (TextView) findViewById(R.id.pcLastNameTextView);
		phoneLayout = findViewById(R.id.pcPhoneLayout);
		phoneTextView = (TextView) findViewById(R.id.pcPhoneTextView);
		emailLayout = findViewById(R.id.pcEmailLayout);
		emailTextView = (TextView) findViewById(R.id.pcEmailTextView);

		getSupportLoaderManager().initLoader(0, null, this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "ActionBar's Up/home button clicked.");
			}

			onBackPressed();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("localEmployeeId", localEmployeeId);

		outState.putSerializable("currentEmployee", currentEmployee);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onResume. Registering fetch done receiver.");
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onPause. Unregistering fetch done receiver.");
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader for Loader ID: " + id
					+ ", Employee ID: " + localEmployeeId);
		}

		return new CursorLoader(getApplicationContext(), Uri.withAppendedPath(
				Employees.CONTENT_URI, "" + localEmployeeId),
				Employees.ALL_COLUMNS, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}
		// DatabaseUtils.dumpCursor(cursor);

		if (cursor != null && cursor.moveToFirst()) {
			currentEmployee = new Employee();
			currentEmployee.load(cursor);
		}

		if (currentEmployee != null) {

			// primary contact section
			String str = currentEmployee.getEmpFirstName();
			firstNameTextView.setText(str);
			firstNameLayout.setVisibility(Utils.getVisibility(str));

			str = currentEmployee.getEmpLastName();
			lastNameTextView.setText(str);
			lastNameLayout.setVisibility(Utils.getVisibility(str));

			str = currentEmployee.getEmpPhone();
			phoneTextView.setText(str);
			phoneLayout.setVisibility(Utils.getVisibility(str));

			str = currentEmployee.getEmpEmail();
			emailTextView.setText(str);
			emailLayout.setVisibility(Utils.getVisibility(str));

			detailLayout.setVisibility(Utils.getVisibility(firstNameLayout,
					lastNameLayout, phoneLayout, emailLayout));

		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoaderReset.");
		}
	}

}

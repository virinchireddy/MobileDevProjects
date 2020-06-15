package in.spoors.effort1;

import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider.Employees;
import in.spoors.effort1.provider.EffortProvider.Settings;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class EmployeesActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, OnClickListener {

	public static final String ACTION_PICK = "pick";
	private ListView listView;
	private SimpleCursorAdapter adapter;
	private String TAG = "EmployeesActivity";
	private String plural;
	private SettingsDao settingsDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_employees);

		settingsDao = SettingsDao.getInstance(getApplicationContext());

		plural = settingsDao.getLabel(Settings.LABEL_EMPLOYEE_PLURAL_KEY,
				Settings.LABEL_EMPLOYEE_PLURAL_DEFAULT_VLAUE);

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(plural);

		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		emptyTextView.setText("No "
				+ settingsDao.getLabel(Settings.LABEL_EMPLOYEE_PLURAL_KEY,
						Settings.LABEL_EMPLOYEE_PLURAL_DEFAULT_VLAUE) + ".");

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);

		getSupportLoaderManager().initLoader(0, null, this);

		String[] columns = new String[] { Employees.EMPLOYEE_FIRST_NAME,
				Employees._ID };
		int[] views = { R.id.employeeNameTextView, R.id.alternateActionButton };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_employee, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);

	}

	class MyViewBinder implements ViewBinder {

		@SuppressWarnings("unused")
		private final String TAG = "CustomersActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

			switch (columnIndex) {
			case Employees.EMPLOYEE_FIRST_NAME_INDEX:
				TextView nameTextView = (TextView) view;
				String fullName = cursor
						.getString(Employees.EMPLOYEE_FIRST_NAME_INDEX)
						+ " "
						+ cursor.getString(+Employees.EMPLOYEE_LAST_NAME_INDEX);
				nameTextView.setText(fullName);
				return true;

			case Employees._ID_INDEX:
				ImageButton button = (ImageButton) view;
				button.setOnClickListener(EmployeesActivity.this);
				button.setTag(cursor.getLong(Employees._ID_INDEX));
				button.setImageResource(R.drawable.ic_action_about);
				button.setVisibility(View.VISIBLE);
				return true;
			default:
				return false;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
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
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {

		return new CursorLoader(getApplicationContext(), Employees.CONTENT_URI,
				Employees.ALL_COLUMNS, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}

		adapter.swapCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoaderReset.");
		}

		// swapCursor method requires API level 11 or higher.
		// Thus, use SimpleCursorAdapter from support library.
		adapter.swapCursor(null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.putExtra("localEmployeeId", id);
		setResult(RESULT_OK, intent);
		finish();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.alternateActionButton:
			Intent intent = new Intent(EmployeesActivity.this,
					EmployeeActivity.class);
			intent.putExtra(Employees._ID, (Long) v.getTag());
			startActivity(intent);
			break;

		default:
			break;
		}

	}

}

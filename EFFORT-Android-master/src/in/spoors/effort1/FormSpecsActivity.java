package in.spoors.effort1;

import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.FormSpec;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.FormSpecs;
import in.spoors.effort1.provider.EffortProvider.Settings;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class FormSpecsActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, OnClickListener,
		TabListener, RefreshListener {

	public static final String TAG = "FormSpecsActivity";

	private ListView listView;
	private SimpleCursorAdapter adapter;
	private boolean hasAddFormPermission;
	private DrawerFragment drawerFragment;
	private String plural;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}
		setContentView(R.layout.activity_form_specs);

		SettingsDao settingsDao = SettingsDao
				.getInstance(getApplicationContext());
		hasAddFormPermission = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_FORM_ADD, true);

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);

		plural = settingsDao.getLabel(Settings.LABEL_FORM_PLURAL_KEY,
				Settings.LABEL_FORM_PLURAL_DEFAULT_VLAUE);
		String title = "";

		actionBar.setHomeButtonEnabled(true);
		title = plural;
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		Tab activeTab = actionBar.newTab();
		activeTab.setText("Active");
		activeTab.setTabListener(this);
		actionBar.addTab(activeTab);

		Tab withdrawnTab = actionBar.newTab();
		withdrawnTab.setText("Withdrawn");
		withdrawnTab.setTabListener(this);
		actionBar.addTab(withdrawnTab);

		if (savedInstanceState != null) {
			actionBar.setSelectedNavigationItem(savedInstanceState
					.getInt("activeTab"));
		}

		actionBar.setTitle(title);

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);
		getSupportLoaderManager().initLoader(0, null, this);

		String[] columns = { EffortProvider.FormSpecs.TITLE,
				EffortProvider.FormSpecs._ID };
		int[] views = { R.id.titleTextView, R.id.alternateActionButton };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_form_spec, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);
		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_forms, title, null,
				this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.form_specs, menu);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader.");
		}

		switch (id) {
		case 0:
			String[] columns = new String[FormSpecs.ALL_COLUMNS.length];

			for (int i = 0; i < FormSpecs.ALL_COLUMNS.length; ++i) {
				columns[i] = "s1." + FormSpecs.ALL_COLUMNS[i];
			}

			return new CursorLoader(
					getApplicationContext(),
					FormSpecs.CONTENT_URI,
					columns,
					"s1."
							+ FormSpecs.VISIBLE
							+ " = 'true'"
							+ " AND s1."
							+ FormSpecs.WITHDRAWN
							+ " = '"
							+ (getSupportActionBar().getNavigationMode() != ActionBar.NAVIGATION_MODE_TABS
									|| getSupportActionBar()
											.getSelectedNavigationIndex() == 0 ? "false"
									: "true") + "'", null, "UPPER(s1."
							+ FormSpecs.TITLE + ")");

		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}

		switch (loader.getId()) {
		case 0:
			adapter.swapCursor(cursor);
		}
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

	class MyViewBinder implements ViewBinder {

		@SuppressWarnings("unused")
		private final String TAG = "FormSpecsActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
			// + cursor.getPosition() + ", columnIndex=" + columnIndex);
			FormSpec formSpec = new FormSpec();
			formSpec.load(cursor);

			switch (columnIndex) {
			case FormSpecs.TITLE_INDEX:
				TextView tv = (TextView) view;
				tv.setText(cursor.getString(FormSpecs.TITLE_INDEX));
				return true;

			case FormSpecs._ID_INDEX:
				ImageButton button = (ImageButton) view;
				button.setOnClickListener(FormSpecsActivity.this);
				button.setTag(cursor.getLong(FormSpecs._ID_INDEX));

				// set only the right drawable
				button.setImageResource(R.drawable.ic_content_new_inverse);

				if (hasAddFormPermission && !formSpec.getWithdrawn()) {
					button.setVisibility(View.VISIBLE);
				} else {
					button.setVisibility(View.INVISIBLE);
				}

				return true;

			default:
				return false;
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Parent: " + parent.toString() + ", View: "
							+ view.toString() + ", Position: " + position
							+ ", ID: " + id);
		}

		TextView textView = (TextView) view.findViewById(R.id.titleTextView);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Selected form template: " + textView.getText());
		}

		Intent intent = new Intent(this, FormsActivity.class);
		intent.putExtra(EffortProvider.FormSpecs._ID, id);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}

		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
	}

	@Override
	public void onClick(View v) {
		if (v.getTag() != null) {
			long formSpecId = (Long) v.getTag();

			Intent intent = new Intent(this, FormActivity.class);
			intent.putExtra(EffortProvider.Forms._ID, 0L);
			intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID, formSpecId);
			startActivity(intent);
		}
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// do nothing
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("activeTab", getSupportActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);

		super.onBackPressed();
	}

	@Override
	public void onRefresh() {
		getSupportLoaderManager().restartLoader(0, null, this);
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

}

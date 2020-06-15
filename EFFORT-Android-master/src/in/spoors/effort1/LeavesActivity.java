package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.dao.LeaveStatusDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.provider.EffortProvider.Leaves;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class LeavesActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, RefreshListener {

	public static final String TAG = "LeavesActivity";

	private ListView listView;
	private SimpleCursorAdapter adapter;
	private SimpleDateFormat dateTimeFormat;
	private LeaveStatusDao leaveStatusDao;
	private SettingsDao settingsDao;
	private DrawerFragment drawerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}

		setContentView(R.layout.activity_leaves);
		dateTimeFormat = Utils.getDateTimeFormat(getApplicationContext());
		leaveStatusDao = LeaveStatusDao.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);
		getSupportLoaderManager().initLoader(0, null, this);

		String[] columns = { Leaves.STATUS, Leaves.START_TIME };
		int[] views = { R.id.statusTextView, R.id.timeTextView };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_leave, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);
		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_leaves, "Leaves",
				null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.leaves, menu);
		boolean hasAddLeavePermission = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_LEAVE_ADD, true);
		menu.findItem(R.id.applyForLeave).setVisible(
				!drawerFragment.isDrawerOpen() && hasAddLeavePermission);
		return true;
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
			Log.d(TAG, "In onCreateLoader.");
		}

		return new CursorLoader(getApplicationContext(), Leaves.CONTENT_URI,
				Leaves.ALL_COLUMNS, Leaves.CANCELLED + " = 'false'", null,
				Leaves.START_TIME);
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

	class MyViewBinder implements ViewBinder {

		@SuppressWarnings("unused")
		private final String TAG = "LeavesActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
			// + cursor.getPosition() + ", columnIndex=" + columnIndex);

			TextView tv;

			switch (columnIndex) {
			case Leaves.STATUS_INDEX:
				tv = (TextView) view;
				tv.setText(leaveStatusDao.getName(cursor
						.getInt(Leaves.STATUS_INDEX)));
				return true;

			case Leaves.START_TIME_INDEX:
				Date startTime = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Leaves.START_TIME_INDEX));

				Date endTime = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Leaves.END_TIME_INDEX));

				tv = (TextView) view;
				tv.setText(dateTimeFormat.format(startTime) + " - "
						+ dateTimeFormat.format(endTime));

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

		Intent intent = new Intent(this, LeaveActivity.class);
		intent.putExtra(Leaves._ID, id);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}

		if (item.getItemId() == android.R.id.home) {
			finish();
		} else if (item.getItemId() == R.id.applyForLeave) {
			Intent intent = new Intent(this, LeaveActivity.class);
			intent.putExtra(Leaves._ID, 0L);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
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

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);

		super.onBackPressed();
	}

}

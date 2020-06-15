package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.provider.EffortProvider.Holidays;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class HolidaysActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, RefreshListener {

	public static final String TAG = "HolidaysActivity";

	private ListView listView;
	private SimpleCursorAdapter adapter;
	private SimpleDateFormat dateFormat;
	private int year;
	private DrawerFragment drawerFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}

		setContentView(R.layout.activity_holidays);
		dateFormat = Utils.getDateFormat(getApplicationContext());

		if (savedInstanceState == null) {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
		} else {
			year = savedInstanceState.getInt("year");
		}

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		listView = (ListView) findViewById(android.R.id.list);
		getSupportLoaderManager().initLoader(0, null, this);

		String[] columns = { Holidays.START_TIME, Holidays.TITLE,
				Holidays.DESCRIPTION };
		int[] views = { R.id.timeTextView, R.id.titleTextView,
				R.id.descriptionTextView };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_holiday, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);
		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_holidays, "Holidays",
				null, this);
		updateSubtitle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.holidays, menu);
		menu.findItem(R.id.prevYear).setVisible(!drawerFragment.isDrawerOpen());
		menu.findItem(R.id.nextYear).setVisible(!drawerFragment.isDrawerOpen());
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("year", year);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader.");
		}

		Calendar fromCal = Calendar.getInstance();
		fromCal.clear();
		fromCal.set(Calendar.YEAR, year);
		fromCal.set(Calendar.MONTH, Calendar.JANUARY);
		fromCal.set(Calendar.DAY_OF_MONTH, 1);
		String from = SQLiteDateTimeUtils
				.getSQLiteDateTimeFromLocalTime(fromCal.getTime());

		Calendar toCal = Calendar.getInstance();
		toCal.clear();
		toCal.set(Calendar.YEAR, year + 1);
		toCal.set(Calendar.MONTH, Calendar.JANUARY);
		toCal.set(Calendar.DAY_OF_MONTH, 1);
		String to = SQLiteDateTimeUtils.getSQLiteDateTimeFromLocalTime(toCal
				.getTime());

		return new CursorLoader(getApplicationContext(), Holidays.CONTENT_URI,
				Holidays.ALL_COLUMNS, Holidays.START_TIME + " >= ? AND "
						+ Holidays.END_TIME + " < ?",
				new String[] { from, to }, Holidays.START_TIME);
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
		private final String TAG = "HolidaysActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
			// + cursor.getPosition() + ", columnIndex=" + columnIndex);

			switch (columnIndex) {
			case Holidays.START_TIME_INDEX:
				Date startTime = SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(Holidays.START_TIME_INDEX));

				StringBuffer sb = new StringBuffer();

				if (DateUtils.isToday(startTime.getTime())) {
					sb.append("Today, ");
				}

				sb.append(new StringBuffer(dateFormat.format(startTime)));

				TextView tv = (TextView) view;
				tv.setText(sb.toString());
				view.setVisibility(View.VISIBLE);
				return true;

			case Holidays.DESCRIPTION_INDEX:
				TextView descriptionTextView = (TextView) view;
				String description = cursor
						.getString(Holidays.DESCRIPTION_INDEX);
				descriptionTextView.setText(description);
				descriptionTextView.setVisibility(Utils
						.getVisibility(description));
				return true;

			default:
				return false;
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}

		if (item.getItemId() == android.R.id.home) {
			finish();
		} else if (item.getItemId() == R.id.prevYear) {
			year--;
			updateSubtitle();
			getSupportLoaderManager().restartLoader(0, null, this);
		} else if (item.getItemId() == R.id.nextYear) {
			year++;
			updateSubtitle();
			getSupportLoaderManager().restartLoader(0, null, this);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	public void onRefresh() {
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	private void updateSubtitle() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setSubtitle("" + year);

		if (drawerFragment != null) {
			drawerFragment.setActivitySubtitle("" + year);
		}
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

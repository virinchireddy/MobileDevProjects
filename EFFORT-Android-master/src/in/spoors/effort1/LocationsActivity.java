package in.spoors.effort1;

import in.spoors.effort1.dao.NamedLocationsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.NamedLocation;
import in.spoors.effort1.provider.EffortProvider.NamedLocations;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class LocationsActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, OnQueryTextListener,
		RefreshListener {

	public static final String TAG = "LocationsActivity";

	private ListView listView;
	private SimpleCursorAdapter adapter;
	private MenuItem searchItem;
	private SearchView searchView;
	private boolean searchInProgress;
	public static final String ACTION_PICK = "pick";
	private String action;
	private String query;
	private NamedLocationsDao namedLocationsDao;
	private DrawerFragment drawerFragment;

	private String plural;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}

		searchInProgress = false;
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_locations);

		Intent intent = getIntent();
		action = intent.getAction();
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Intent action: " + action);
		}
		SettingsDao settingsDao = SettingsDao
				.getInstance(getApplicationContext());
		plural = settingsDao.getLabel(Settings.LABEL_NAMEDLOCATION_PLURAL_KEY,
				Settings.LABEL_NAMEDLOCATION_PLURAL_DEFAULT_VLAUE);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(plural);

		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);

		namedLocationsDao = NamedLocationsDao
				.getInstance(getApplicationContext());
		namedLocationsDao.deletePartialLocations();
		getSupportLoaderManager().initLoader(0, null, this);

		String[] columns = new String[] { NamedLocations.NAME };

		int[] views = { R.id.nameTextView };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_location, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		listView.setAdapter(adapter);
		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_locations, plural,
				null, this);
		updateSubtitle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.locations, menu);

		// Create the search view
		searchItem = menu.findItem(R.id.action_search);
		searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setQueryHint("Search for " + plural);
		searchView.setOnQueryTextListener(this);
		MenuItemCompat.setOnActionExpandListener(searchItem,
				new OnActionExpandListener() {

					@Override
					public boolean onMenuItemActionCollapse(MenuItem menuItem) {
						Log.i(TAG, "onMenuItemActionCollapse");
						return true;
					}

					@Override
					public boolean onMenuItemActionExpand(MenuItem menuItem) {
						Log.i(TAG, "onMenuItemActionExpand");
						query = "";
						getSupportLoaderManager().restartLoader(0, null,
								LocationsActivity.this);
						updateSubtitle();
						return true;

					}

				});

		if (searchInProgress) {
			searchItem.setVisible(false);
			setSupportProgressBarIndeterminateVisibility(true);
		} else {
			searchItem.setVisible(!drawerFragment.isDrawerOpen());
			setSupportProgressBarIndeterminateVisibility(false);
		}

		menu.findItem(R.id.addLocation).setVisible(
				!drawerFragment.isDrawerOpen());
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

		String selection = null;
		String[] selectionArgs = null;

		if (!TextUtils.isEmpty(query)) {
			selection = NamedLocations.NAME + " LIKE ?";
			selectionArgs = new String[] { "%" + query + "%" };
		}

		return new CursorLoader(getApplicationContext(),
				NamedLocations.CONTENT_URI, NamedLocations.ALL_COLUMNS,
				selection, selectionArgs, NamedLocations.NAME);
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
		// Log.i(TAG, "onItemClick Parent: " + parent.toString() + ", View: "
		// + view.toString() + ", Position: " + position + ", ID: " + id);

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Launching location activity for location id " + id);
		}

		Intent intent = new Intent(this, LocationActivity.class);
		intent.putExtra(NamedLocations._ID, id);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {
		case android.R.id.home:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "ActionBar's Up/home button clicked.");
			}

			onBackPressed();
			break;

		case R.id.addLocation:
			Intent intent = new Intent(this, LocationActivity.class);
			intent.putExtra(NamedLocations._ID, 0L);
			startActivity(intent);
			break;

		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Selected ID: " + item.getItemId());
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Query submitted: " + query);
		}

		this.query = query;
		updateSubtitle();
		getSupportLoaderManager().restartLoader(0, null, this);

		if (searchView != null) {
			searchInProgress = true;
			setSupportProgressBarIndeterminateVisibility(true);
			MenuItemCompat.collapseActionView(searchItem);
			new LocationSearchTask().execute();
			supportInvalidateOptionsMenu();
		}

		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Query text changed: " + newText);
		}

		// if (TextUtils.isEmpty(newText)) {
		// this.query = null;
		// updateSubtitle();
		// getSupportLoaderManager().restartLoader(0, null, this);
		// }

		return true;
	}

	class LocationSearchTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			searchInProgress = false;
			getSupportLoaderManager().restartLoader(0, null,
					LocationsActivity.this);
			supportInvalidateOptionsMenu();
			setSupportProgressBarIndeterminateVisibility(false);
		}

		@Override
		protected Void doInBackground(Void... params) {
			SettingsDao settingsDao = SettingsDao
					.getInstance(getApplicationContext());
			NamedLocationsDao namedLocationsDao = NamedLocationsDao
					.getInstance(getApplicationContext());

			AndroidHttpClient httpClient = null;

			try {
				if (TextUtils.isEmpty(query)) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"No query found in the intent. Doing nothing.");
					}

					return null;
				}

				String serverBaseUrl = getString(R.string.server_base_url);
				Builder builder = Uri
						.parse(serverBaseUrl)
						.buildUpon()
						.appendEncodedPath(
								"service/namedLocation/search/"
										+ settingsDao.getString("employeeId"));
				Utils.appendCommonQueryParameters(getApplicationContext(),
						builder);
				builder.appendQueryParameter("query", query);

				String url = builder.build().toString();

				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Named location search URL: " + url);
				}

				httpClient = AndroidHttpClient.newInstance("EFFORT");
				HttpGet httpGet = new HttpGet(url);
				Utils.setTimeouts(httpGet);

				HttpResponse httpResponse = httpClient.execute(httpGet);
				String response = EntityUtils
						.toString(httpResponse.getEntity());

				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Response JSON: " + response);
				}

				JSONTokener tokener = new JSONTokener(response);
				Object obj = tokener.nextValue();

				if (!(obj instanceof JSONArray)) {
					Log.e(TAG,
							"Invalid named location search response. Expected a JSON array but did not get it.");
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									getApplicationContext(),
									"Received unexpected response from the cloud.",
									Toast.LENGTH_LONG).show();
						}
					});

					return null;
				}

				List<NamedLocation> locations = new ArrayList<NamedLocation>();
				Utils.addLocations((JSONArray) obj, locations,
						getApplicationContext(), true);

				for (NamedLocation location : locations) {
					namedLocationsDao.save(location);
				}
			} catch (MalformedURLException e) {
				Log.e(TAG, "Bad URL: " + e.toString(), e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "Bad URL.",
								Toast.LENGTH_LONG).show();
					}
				});
			} catch (IOException e) {
				Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),
								"Search failed due to a network failure.",
								Toast.LENGTH_LONG).show();
					}
				});
			} catch (JSONException e) {
				Log.e(TAG, "Failed to parse response: " + e.toString(), e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),
								"Received unexpected response from the cloud.",
								Toast.LENGTH_LONG).show();
					}
				});
			} catch (ParseException e) {
				Log.e(TAG, "Failed to parse response: " + e.toString(), e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),
								"Received unexpected response from the cloud.",
								Toast.LENGTH_LONG).show();
					}
				});
			} finally {
				if (httpClient != null) {
					httpClient.close();
				}
			}

			return null;
		}
	}

	private void updateSubtitle() {
		String subtitle = "All " + plural;

		if (!TextUtils.isEmpty(query)) {
			subtitle = "Matching " + query;
		}

		getSupportActionBar().setSubtitle(subtitle);

		if (drawerFragment != null) {
			drawerFragment.setActivitySubtitle(subtitle);
		}
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

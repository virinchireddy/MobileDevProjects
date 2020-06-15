package in.spoors.effort1;

import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.EmployeesDao;
import in.spoors.effort1.dao.EntitiesDao;
import in.spoors.effort1.dao.EntityFieldsDao;
import in.spoors.effort1.dao.EntitySpecsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Customer;
import in.spoors.effort1.dto.Employee;
import in.spoors.effort1.dto.Entity;
import in.spoors.effort1.dto.EntityField;
import in.spoors.effort1.dto.EntitySpec;
import in.spoors.effort1.dto.ListFilteringCriteria;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.EntitiesView;
import in.spoors.effort1.provider.EffortProvider.EntityFields;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;
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
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.net.Uri.Builder;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
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
import android.widget.Toast;

public class EntitiesActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, OnQueryTextListener,
		OnClickListener {

	public static final String TAG = "EntitiesActivity";
	public static final int PAGE_SIZE = 10;
	public static final String ACTION_PICK = "pick";

	private ListView listView;
	private SimpleCursorAdapter adapter;
	private long entitySpecId;
	private EntitySpec entitySpec;
	private SettingsDao settingsDao;
	private EntitySpecsDao entitySpecsDao;
	private EntitiesDao entitiesDao;
	private CustomersDao customersDao;
	private EmployeesDao employeesDao;
	private SearchView searchView;
	private boolean searchInProgress;
	private String query;
	private MenuItem searchItem;
	private ArrayList<ListFilteringCriteria> filteringCriterias;

	// Note: use of
	// EffortProvider.Fields.LOCAL_VALUE instead of
	// EffortProvider.FormsView.LOCAL_VIEW, and
	// EffortProvider.Forms.LOCAL_CREATION_TIME instead of
	// EffortProvider.FormsView.LOCAL_CREATION_TIME
	// is intentional because SimpleCursorAdapter doesn't like table name
	// prefixed column names.
	String[] columns = { EntitiesView.DISPLAY_VALUE,
			EntityFields.LOCAL_ENTITY_ID };
	int[] views = { R.id.titleTextView, R.id.viewButton };
	private String plural;
	private EntityFieldsDao fieldsDao;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}

		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_entities);
		setSupportProgressBarIndeterminateVisibility(false);
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		entitySpecsDao = EntitySpecsDao.getInstance(getApplicationContext());
		entitiesDao = EntitiesDao.getInstance(getApplicationContext());
		fieldsDao = EntityFieldsDao.getInstance(getApplicationContext());
		customersDao = CustomersDao.getInstance(getApplicationContext());
		employeesDao = EmployeesDao.getInstance(getApplicationContext());
		employeesDao = EmployeesDao.getInstance(getApplicationContext());
		plural = settingsDao.getLabel(Settings.LABEL_LIST_PLURAL_KEY,
				Settings.LABEL_LIST_PLURAL_DEFAULT_VLAUE);

		if (savedInstanceState != null) {
			entitySpecId = savedInstanceState.getLong("entitySpecId");
			query = savedInstanceState.getString("query");
		} else {
			entitySpecId = getIntent().getLongExtra(
					EffortProvider.EntitySpecs._ID, 0);
		}

		entitySpec = entitySpecsDao.getEntitySpec(entitySpecId);
		filteringCriterias = (ArrayList<ListFilteringCriteria>) getIntent()
				.getSerializableExtra("filteringCriterias");

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(entitySpec.getTitle());
		updateSubtitle();

		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		emptyTextView.setText("No " + plural + ".");

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);
		getSupportLoaderManager().initLoader(0, null, this);

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_entity, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);
		searchInProgress = false;
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("entitySpecId", entitySpecId);
		outState.putString("query", query);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.entities, menu);
		searchItem = menu.findItem(R.id.action_search);
		searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		searchView.setQueryHint("Search for " + entitySpec.getTitle());
		searchView.setOnQueryTextListener(this);

		if (searchInProgress) {
			searchView.setVisibility(View.GONE);
		} else {
			searchView.setVisibility(View.VISIBLE);
		}

		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader.");
		}

		switch (id) {
		case 0:

			String filter = entitiesDao.getFilteringQuery(filteringCriterias,
					query);

			// TODO: Revisit the sort order
			return new CursorLoader(getApplicationContext(),
					EntitiesView.CONTENT_URI, columns, entitySpecId
							+ (TextUtils.isEmpty(query) ? "" : " AND "
									+ EntitiesView.DISPLAY_VALUE + " LIKE '%"
									+ query + "%'")
							+ (TextUtils.isEmpty(filter) ? "" : (" AND "
									+ EntitiesView.LOCAL_ENTITY_ID + " IN ("
									+ filter + ")")), null,
					EntitiesView.DISPLAY_VALUE);

		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}

		DatabaseUtils.dumpCursor(cursor);

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
		private final String TAG = "EntitiesActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
			// + cursor.getPosition() + ", columnIndex=" + columnIndex);
			switch (columnIndex) {
			case EntitiesView.DISPLAY_VALUE_INDEX:
				TextView tv = (TextView) view;
				int type = cursor.getInt(EntitiesView.TYPE_INDEX);

				if (type == FieldSpecs.TYPE_DATE
						|| type == FieldSpecs.TYPE_TIME
						|| type == FieldSpecs.TYPE_YES_OR_NO
						|| type == FieldSpecs.TYPE_ENTITY) {
					tv.setText(EntitiesDao.getDisplayValue(
							getApplicationContext(),
							cursor.getString(EntitiesView.LOCAL_VALUE_INDEX),
							cursor.getString(EntitiesView.REMOTE_VALUE_INDEX),
							type));
				} else {
					tv.setText(cursor
							.getString(EntitiesView.DISPLAY_VALUE_INDEX));
				}

				return true;

			case EntitiesView.LOCAL_ENTITY_ID_INDEX:
				ImageButton button = (ImageButton) view;
				button.setTag(cursor
						.getString(EntitiesView.LOCAL_ENTITY_ID_INDEX));
				button.setOnClickListener(EntitiesActivity.this);
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

		Intent intent = new Intent();
		intent.putExtra("localEntityId", id);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			break;

		case R.id.submitForm:
			Intent intent = new Intent(this, FormActivity.class);
			intent.putExtra(EffortProvider.Forms._ID, 0L);
			intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID, entitySpecId);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private class SearchEntitiesTask extends AsyncTask<String, Integer, Long> {
		private List<Entity> addedEntities = new ArrayList<Entity>();
		private List<EntityField> addedFields = new ArrayList<EntityField>();
		private List<Customer> addedCustomers = new ArrayList<Customer>();
		private List<Employee> employees = new ArrayList<Employee>();

		@Override
		protected Long doInBackground(String... params) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In doInBackground.");
			}

			settingsDao = SettingsDao.getInstance(getApplicationContext());
			AndroidHttpClient httpClient = null;

			try {
				String url = getUrl(params[0], params[1]);

				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Search entities URL: " + url);
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

				if (!(obj instanceof JSONObject)) {
					Log.e(TAG,
							"Invalid previous forms response. Expected a JSON object but did not get it.");
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

				JSONObject jsonObject = (JSONObject) obj;

				if (jsonObject.get("employees") instanceof JSONArray) {
					Utils.addEmployees(jsonObject.getJSONArray("employees"),
							employees, getApplicationContext());
				}

				if (jsonObject.get("customers") instanceof JSONArray) {
					Utils.addCustomers(jsonObject.getJSONArray("customers"),
							addedCustomers, getApplicationContext(), false);
				}

				if (jsonObject.get("entities") instanceof JSONArray) {
					Utils.addEntities(jsonObject.getJSONArray("entities"),
							addedEntities, getApplicationContext());
				}

				if (jsonObject.get("fields") instanceof JSONArray) {
					Utils.addEntityFields(jsonObject.getJSONArray("fields"),
							addedFields, getApplicationContext());
				}

				for (Employee employee : employees) {
					employeesDao.save(employee);
				}

				for (Customer customer : addedCustomers) {
					customersDao.save(customer);
				}

				if (addedEntities.size() <= 0) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(getApplicationContext(),
									"No " + plural + " matched your query.",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				for (Entity entity : addedEntities) {
					if (entity.getCached() == null) {
						entity.setCached(true);
					}

					entitiesDao.save(entity);
				}

				for (EntityField field : addedFields) {
					fieldsDao.save(field);
				}

				return null;
			} catch (MalformedURLException e) {
				Log.e(TAG, "Bad URL: " + e.toString(), e);
			} catch (IOException e) {
				Log.e(TAG, "Network Fetch Failed: " + e.toString(), e);

				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(getApplicationContext(),
								"Fetch failed due to network error.",
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

			getSupportLoaderManager().restartLoader(0, null,
					EntitiesActivity.this);

			searchInProgress = false;
			setSupportProgressBarIndeterminateVisibility(false);
			supportInvalidateOptionsMenu();
		}

		private String getUrl(String query, String entitySpecId) {
			String serverBaseUrl = getString(R.string.server_base_url);
			Builder builder = Uri
					.parse(serverBaseUrl)
					.buildUpon()
					.appendEncodedPath(
							"service/entity/search/"
									+ settingsDao.getString("employeeId"));
			Utils.appendCommonQueryParameters(getApplicationContext(), builder);
			builder.appendQueryParameter("query", query);
			builder.appendQueryParameter("entitySpecId", entitySpecId);

			return builder.build().toString();
		}
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
			// searchView.setQuery("", false);
			setSupportProgressBarIndeterminateVisibility(true);
			MenuItemCompat.collapseActionView(searchItem);
			supportInvalidateOptionsMenu();
			new SearchEntitiesTask().execute(query, "" + entitySpecId);
		}

		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Query text changed: " + newText);
		}

		if (TextUtils.isEmpty(newText)) {
			this.query = null;
			updateSubtitle();
			getSupportLoaderManager().restartLoader(0, null, this);
		}

		return false;
	}

	@Override
	public void onClick(View v) {
		if (v instanceof ImageButton) {
			Intent intent = new Intent(this, EntityActivity.class);
			intent.putExtra(EffortProvider.Entities._ID,
					Long.parseLong(v.getTag().toString()));
			startActivity(intent);
		}
	}

	private void updateSubtitle() {
		if (TextUtils.isEmpty(query)) {
			getSupportActionBar().setSubtitle("All " + plural);
		} else {
			getSupportActionBar().setSubtitle("Matching " + query);
		}
	}

}

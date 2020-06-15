package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.EmployeesDao;
import in.spoors.effort1.dao.EntitiesDao;
import in.spoors.effort1.dao.FieldValueSpecsDao;
import in.spoors.effort1.dao.FormSpecsDao;
import in.spoors.effort1.dao.FormsDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Form;
import in.spoors.effort1.dto.FormSpec;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;
import in.spoors.effort1.provider.EffortProvider.Forms;
import in.spoors.effort1.provider.EffortProvider.FormsView;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
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
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FormsActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, RefreshListener {

	public static final String TAG = "FormsActivity";
	public static final int PAGE_SIZE = 10;

	private ListView listView;
	private SimpleCursorAdapter adapter;
	private SimpleDateFormat dateFormat;
	private SimpleDateFormat dateTimeFormat;
	private long formSpecId;
	private FormSpec formSpec;
	private FormSpecsDao formSpecsDao;
	private FormsDao formsDao;
	private FieldValueSpecsDao fieldValueSpecsDao;
	private SettingsDao settingsDao;
	private CustomersDao customersDao;
	private EntitiesDao entitiesDao;
	private EmployeesDao employeesDao;
	private Button loadMoreButton;
	private boolean showFutureItemsOnly;
	private Long oldestFormFetched;
	private boolean cleanupFinished;
	private DrawerFragment drawerFragment;
	private String plural;
	private String singular;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreate.");
		}
		supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_forms);
		setSupportProgressBarIndeterminateVisibility(false);
		dateFormat = Utils.getDateFormat(getApplicationContext());
		dateTimeFormat = Utils.getDateTimeFormat(getApplicationContext());

		formsDao = FormsDao.getInstance(getApplicationContext());
		formSpecsDao = FormSpecsDao.getInstance(getApplicationContext());
		fieldValueSpecsDao = FieldValueSpecsDao
				.getInstance(getApplicationContext());
		settingsDao = SettingsDao.getInstance(getApplicationContext());
		customersDao = CustomersDao.getInstance(getApplicationContext());
		entitiesDao = EntitiesDao.getInstance(getApplicationContext());
		employeesDao = EmployeesDao.getInstance(getApplicationContext());
		// entityFieldsDao =
		// EntityFieldsDao.getInstance(getApplicationContext());

		loadMoreButton = (Button) findViewById(R.id.loadMoreButton);
		oldestFormFetched = null;

		if (savedInstanceState != null) {
			formSpecId = savedInstanceState.getLong("formSpecId");
			showFutureItemsOnly = savedInstanceState.getBoolean(
					"showFutureItemsOnly", true);
		} else {
			formSpecId = getIntent().getLongExtra(EffortProvider.FormSpecs._ID,
					0);
			showFutureItemsOnly = true;
		}

		formSpec = formSpecsDao.getFormSpec(formSpecId);

		singular = settingsDao.getLabel(Settings.LABEL_FORM_SINGULAR_KEY,
				Settings.LABEL_FORM_SINGULAR_DEFAULT_VLAUE);
		plural = settingsDao.getLabel(Settings.LABEL_FORM_PLURAL_KEY,
				Settings.LABEL_FORM_PLURAL_DEFAULT_VLAUE);

		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		emptyTextView.setText("No " + plural + ".");

		updateLoadMoreButtonText();

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setTitle(formSpec.getTitle());
		actionBar.setSubtitle(singular + " submissions");

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);

		// Note: use of
		// EffortProvider.Fields.LOCAL_VALUE instead of
		// EffortProvider.FormsView.LOCAL_VIEW, and
		// EffortProvider.Forms.LOCAL_CREATION_TIME instead of
		// EffortProvider.FormsView.LOCAL_CREATION_TIME
		// is intentional because SimpleCursorAdapter doesn't like table name
		// prefixed column names.
		String[] columns = { EffortProvider.Fields.LOCAL_VALUE,
				EffortProvider.Forms.REMOTE_CREATION_TIME,
				EffortProvider.Forms.TREE_DIRTY };
		int[] views = { R.id.titleTextView, R.id.timeTextView,
				R.id.syncImageView };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_form, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);
		new CleanUpTask().execute("None");
		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(-1, formSpec.getTitle(), singular
				+ " submissions", this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();

		if (cleanupFinished) {
			formsDao.updateTreeDirtyFlags();
			getSupportLoaderManager().restartLoader(0, null, this);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("formSpecId", formSpecId);
		outState.putBoolean("showFutureItemsOnly", showFutureItemsOnly);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		// Inflate the menu; this adds items to the action bar if it is present.
		boolean hasAddFormPermission = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_FORM_ADD, true);

		getMenuInflater().inflate(R.menu.forms, menu);
		menu.findItem(R.id.submitForm).setVisible(
				!drawerFragment.isDrawerOpen()
						&& hasAddFormPermission
						&& (formSpec == null || (formSpec != null && !formSpec
								.getWithdrawn())));
		menu.findItem(R.id.submitForm).setTitle("Submit " + singular);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader.");
		}

		String today = SQLiteDateTimeUtils
				.getSQLiteDateTimeForBeginningOfToday();

		switch (id) {
		case 0:
			return new CursorLoader(getApplicationContext(),
					FormsView.CONTENT_URI, FormsView.ALL_COLUMNS,
					FormsView.FORM_SPEC_UNIQUE_ID
							+ " = '"
							+ formSpecsDao.getUniqueId(formSpecId)
							+ "' AND "
							+ Forms.TABLE
							+ "."
							+ Forms.TEMPORARY
							+ " = 'false'"
							+ (showFutureItemsOnly ? " AND ("
									+ FormsView.REMOTE_CREATION_TIME + " >= '"
									+ today + "' OR "
									+ FormsView.LOCAL_MODIFICATION_TIME
									+ " >= '" + today + "')" : ""), null,
					FormsView.REMOTE_CREATION_TIME + ", "
							+ FormsView.LOCAL_CREATION_TIME);

		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onLoadFinished.");
		}

		// DatabaseUtils.dumpCursor(cursor);

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
		private final String TAG = "FormsActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
			// + cursor.getPosition() + ", columnIndex=" + columnIndex);
			switch (columnIndex) {
			case FormsView.LOCAL_VALUE_INDEX:
				String value = cursor.getString(FormsView.LOCAL_VALUE_INDEX);

				if (value == null) {
					value = cursor.getString(FormsView.REMOTE_VALUE_INDEX);
				}

				if (cursor.getInt(FormsView.TYPE_INDEX) == FieldSpecs.TYPE_CUSTOMER
						&& !TextUtils.isEmpty(value)) {
					value = customersDao.getCompanyNameWithLocalId(Long
							.parseLong(value));
				}

				else if (cursor.getInt(FormsView.TYPE_INDEX) == FieldSpecs.TYPE_EMPLOYEE
						&& !TextUtils.isEmpty(value)) {
					value = employeesDao.getEmployeeNameWithLocalId(Long
							.parseLong(value));
				}

				else if (cursor.getInt(FormsView.TYPE_INDEX) == FieldSpecs.TYPE_ENTITY
						&& !TextUtils.isEmpty(value)) {
					value = entitiesDao.getEntityName(Long.parseLong(value));
				} else if (cursor.getInt(FormsView.TYPE_INDEX) == FieldSpecs.TYPE_MULTI_LIST
						&& !TextUtils.isEmpty(value)) {
					String[] ids = value.split(",");
					ArrayList<String> displayValues = new ArrayList<String>();
					for (int i = 0; i < ids.length; i++) {
						ids[i] = ids[i].trim();
						displayValues.add(entitiesDao.getEntityName(Long
								.parseLong(ids[i])));
					}
					value = TextUtils.join(", ", displayValues);
				} else if (cursor.getInt(FormsView.TYPE_INDEX) == FieldSpecs.TYPE_MULTI_SELECT_LIST
						&& !TextUtils.isEmpty(value)) {
					String idsAsString = value;
					if (!TextUtils.isEmpty(idsAsString)) {
						String[] ids = idsAsString.split(",");
						ArrayList<String> values = new ArrayList<String>();
						for (int i = 0; i < ids.length; i++) {
							ids[i] = ids[i].trim();
							values.add(fieldValueSpecsDao.getValue(Long
									.parseLong(ids[i])));
						}
						value = TextUtils.join(", ", values);
					}

				}

				if (!TextUtils.isEmpty(value)) {
					switch (cursor.getInt(FormsView.TYPE_INDEX)) {
					case FieldSpecs.TYPE_DATE:
						value = dateFormat.format(SQLiteDateTimeUtils
								.getDate(value));
						break;

					case FieldSpecs.TYPE_YES_OR_NO:
						value = Boolean.parseBoolean(value) ? "Yes" : "No";
						break;

					case FieldSpecs.TYPE_SINGLE_SELECT_LIST:
						value = fieldValueSpecsDao.getValue(Long
								.parseLong(value));
						break;
					}
				}

				TextView tv = (TextView) view;

				tv.setText(value);
				return true;

			case FormsView.REMOTE_CREATION_TIME_INDEX:
				TextView timeTV = (TextView) view;
				timeTV.setText(dateTimeFormat.format(SQLiteDateTimeUtils.getLocalTime(cursor
						.getString(FormsView.REMOTE_CREATION_TIME_INDEX))));
				return true;

			case FormsView.TREE_DIRTY_INDEX:
				ImageView syncImageView = (ImageView) view;

				if ("true".equals(cursor.getString(FormsView.TREE_DIRTY_INDEX))) {
					syncImageView.setImageResource(R.drawable.ic_sync_pending);
					syncImageView.setVisibility(View.VISIBLE);
				} else {
					syncImageView.setVisibility(View.GONE);
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

		Intent intent = new Intent(this, FormActivity.class);
		intent.putExtra(EffortProvider.Forms._ID, id);
		intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID, formSpecId);
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}

		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			break;

		case R.id.submitForm:
			Intent intent = new Intent(this, FormActivity.class);
			intent.putExtra(EffortProvider.Forms._ID, 0L);
			intent.putExtra(EffortProvider.Forms.FORM_SPEC_ID, formSpecId);
			startActivity(intent);
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void updateLoadMoreButtonText() {
		int oldForms = formsDao.getNumberOfFormsOlderThanToday();

		if (showFutureItemsOnly && oldForms > 0) {
			loadMoreButton.setText("Show all " + plural + " stored locally");
		} else {
			loadMoreButton.setText("Fetch more " + plural + " from cloud");
		}
	}

	// TODO SHD DO
	public void onLoadMoreButtonClick(View view) {
		int oldForms = formsDao.getNumberOfFormsOlderThanToday();

		if (showFutureItemsOnly && oldForms > 0) {
			showFutureItemsOnly = false;
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Loading all local forms.");
			}

			if (cleanupFinished) {
				getSupportLoaderManager().restartLoader(0, null, this);
			}

			updateLoadMoreButtonText();
		} else {
			setSupportProgressBarIndeterminateVisibility(true);

			// if the job no longer exists in our local db, start from today
			String before = XsdDateTimeUtils.getXsdDateTimeFromLocalTime(Utils
					.getBeginningOfToday());

			String oldestFormCreationTime = null;

			if (oldestFormFetched != null) {
				oldestFormCreationTime = formsDao
						.getRemoteCreationTime(oldestFormFetched);

				if (oldestFormCreationTime != null) {
					before = XsdDateTimeUtils
							.getXsdDateTimeFromSQLiteDateTime(oldestFormCreationTime);
				}
			}

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Fetching forms before " + before);
			}

			loadMoreButton.setEnabled(false);
			new FetchPreviousFormsTask().execute(before);
		}
	}

	private class FetchPreviousFormsTask extends
			AsyncTask<String, Integer, Long> {
		private List<Form> addedForms = new ArrayList<Form>();

		@Override
		protected Long doInBackground(String... params) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "In doInBackground.");
			}

			settingsDao = SettingsDao.getInstance(getApplicationContext());
			AndroidHttpClient httpClient = null;

			try {
				String url = getUrl(params[0]);

				if (BuildConfig.DEBUG) {
					Log.i(TAG, "Fetch previous forms URL: " + url);
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
					Log.i(TAG, "Fetch previous forms BODY: " + existedFormSpecs);
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

				FormResponse fetchResponse = new FormResponse(
						getApplicationContext());
				fetchResponse.parse(response);
				boolean isCached = true;
				fetchResponse.save(isCached);
				addedForms = fetchResponse.getAddedForms();

				if (addedForms.size() <= 0) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(
									getApplicationContext(),
									"There are no more old " + plural
											+ " on the cloud.",
									Toast.LENGTH_LONG).show();
						}
					});
				}

				if (addedForms.size() > 0) {
					return addedForms.get(addedForms.size() - 1).getRemoteId();
				}
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

			setSupportProgressBarIndeterminateVisibility(false);
			if (result != null) {
				getSupportLoaderManager().restartLoader(0, null,
						FormsActivity.this);
				oldestFormFetched = result;
			}

			setSupportProgressBarIndeterminateVisibility(false);
			loadMoreButton.setEnabled(true);
		}

		private String getUrl(String before) {
			String serverBaseUrl = getString(R.string.server_base_url);
			Builder builder = Uri
					.parse(serverBaseUrl)
					.buildUpon()
					.appendEncodedPath(
							"service/form/before/all/"
									+ settingsDao.getString("employeeId") + "/"
									+ formSpecId);
			Utils.appendCommonQueryParameters(getApplicationContext(), builder);
			builder.appendQueryParameter("before", before);
			builder.appendQueryParameter("pageSize", "" + PAGE_SIZE);
			Uri syncUri = builder.build();
			return syncUri.toString();
		}

	}

	private class CleanUpTask extends AsyncTask<String, Integer, Long> {

		@Override
		protected Long doInBackground(String... params) {
			long rowsAffected = formsDao.deleteCachedForms();
			// rowsAffected += formsDao.deleteOldForms();
			rowsAffected = formsDao
					.deleteOldFormsThatAreNotRelatedWithAnyActivities();
			rowsAffected += formsDao.deleteTemporaryForms();
			formsDao.updateTreeDirtyFlags();

			return rowsAffected;
		}

		@Override
		protected void onPostExecute(Long result) {
			super.onPostExecute(result);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Fetch finished.");
			}

			cleanupFinished = true;
			getSupportLoaderManager().initLoader(0, null, FormsActivity.this);
		}
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
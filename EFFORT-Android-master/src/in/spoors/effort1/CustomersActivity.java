package in.spoors.effort1;

import in.spoors.effort1.dao.CustomerTypesDao;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dto.Customer;
import in.spoors.effort1.dto.CustomerType;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.Customers;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.MenuItemCompat.OnActionExpandListener;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class CustomersActivity extends ActionBarActivity implements
		LoaderCallbacks<Cursor>, OnItemClickListener, OnQueryTextListener,
		OnClickListener, TabListener, RefreshListener {

	public static final String TAG = "CustomersActivity";

	private ListView listView;
	private SimpleCursorAdapter adapter;
	private SearchView searchView;
	private boolean searchInProgress;
	private SearchDoneReceiver searchDoneReceiver = new SearchDoneReceiver();
	public static final String ACTION_PICK = "pick";
	private String action;
	private String query;
	private SettingsDao settingsDao;
	private CustomersDao customersDao;
	private CustomerTypesDao customerTypesDao;
	private List<CustomerType> types;
	private int numTypes;
	private FetchDoneReceiver fetchDoneReceiver = new FetchDoneReceiver();
	private boolean addJobPermission;
	private Menu menu;
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
		setContentView(R.layout.activity_customers);

		settingsDao = SettingsDao.getInstance(getApplicationContext());
		customerTypesDao = CustomerTypesDao
				.getInstance(getApplicationContext());
		customersDao = CustomersDao.getInstance(getApplicationContext());
		// customersDao.deletePartialCustomers();
		singular = settingsDao.getLabel(Settings.LABEL_CUSTOMER_SINGULAR_KEY,
				Settings.LABEL_CUSTOMER_SINGULAR_DEFAULT_VLAUE);
		plural = settingsDao.getLabel(Settings.LABEL_CUSTOMER_PLURAL_KEY,
				Settings.LABEL_CUSTOMER_PLURAL_DEFAULT_VLAUE);

		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		emptyTextView.setText("No " + plural + ".");

		types = customerTypesDao.getCustomerTypes();

		if (types == null) {
			numTypes = 0;
		} else {
			numTypes = types.size();
		}

		Intent intent = getIntent();
		action = intent.getAction();
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Intent action: " + action);
		}

		ActionBar actionBar = getSupportActionBar();
		Utils.updateActionBar(actionBar);

		if (!ACTION_PICK.equals(action)) {
			actionBar.setHomeButtonEnabled(true);
		}

		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(plural);

		if (settingsDao.getBoolean(Settings.KEY_PREFETCH_NEARBY_CUSTOMERS,
				false)) {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			Tab nameTab = actionBar.newTab();
			nameTab.setText("By name");
			nameTab.setTabListener(this);
			actionBar.addTab(nameTab);

			Tab nearnessTab = actionBar.newTab();
			nearnessTab.setText("By nearness");
			nearnessTab.setTabListener(this);
			actionBar.addTab(nearnessTab);

			if (savedInstanceState != null) {
				actionBar.setSelectedNavigationItem(savedInstanceState
						.getInt("activeTab"));
			}
		} else {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		}

		// actionBar.setDisplayShowHomeEnabled(true);

		listView = (ListView) findViewById(android.R.id.list);
		listView.setOnItemClickListener(this);

		addJobPermission = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_JOB_ADD, true);

		getSupportLoaderManager().initLoader(0, null, this);

		String[] columns = null;

		if (EffortProvider.Settings.SORT_ORDER_COMPANY.equals(settingsDao
				.getString(EffortProvider.Settings.KEY_SORT_ORDER))) {
			columns = new String[] { Customers.NAME, Customers.PC_FIRST_NAME,
					Customers.CUSTOMER_TYPE_ID, Customers._ID,
					Customers.DISTANCE };
		} else {
			columns = new String[] { Customers.PC_FIRST_NAME, Customers.NAME,
					Customers.CUSTOMER_TYPE_ID, Customers._ID,
					Customers.DISTANCE };
		}

		int[] views = { R.id.contactNameTextView, R.id.customerNameTextView,
				R.id.customerTypeTextView, R.id.alternateActionButton,
				R.id.distanceTextView };

		adapter = new SimpleCursorAdapter(getApplicationContext(),
				R.layout.list_item_customer, null, columns, views,
				SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		adapter.setViewBinder(new MyViewBinder());
		listView.setAdapter(adapter);
		searchInProgress = Utils.isServiceRunning(getApplicationContext(),
				CustomerSearchService.class.getName());
		drawerFragment = (DrawerFragment) getSupportFragmentManager()
				.findFragmentById(R.id.drawerFragment);
		drawerFragment.setActiveItem(R.drawable.ic_drawer_customers, plural,
				null, this);
		updateSubtitle();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateOptionsMenu.");
		}

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.customers, menu);

		this.menu = menu;
		// Create the search view
		MenuItem searchItem = menu.findItem(R.id.action_search);
		searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		Log.i(TAG, "searchView=" + searchView);

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
								CustomersActivity.this);
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

		if (numTypes > 1) {
			SubMenu subMenu = menu.addSubMenu(0, -1, 100, "Filter");
			MenuItem filterMenuItem = subMenu.getItem();
			filterMenuItem.setIcon(R.drawable.ic_content_filter);
			MenuItemCompat.setShowAsAction(filterMenuItem,
					MenuItemCompat.SHOW_AS_ACTION_ALWAYS
							| MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);

			int index = 0;
			for (CustomerType type : types) {
				MenuItem menuItem = subMenu.add(0, type.getId(), index,
						type.getName());
				menuItem.setCheckable(true);
				menuItem.setChecked(customerTypesDao.getChecked(type.getName()));
				index = index + 1;
			}

			filterMenuItem.setVisible(!drawerFragment.isDrawerOpen());
		}

		menu.findItem(R.id.pickNone).setVisible(
				!drawerFragment.isDrawerOpen() && ACTION_PICK.equals(action));

		boolean addCustomer = settingsDao.getBoolean(
				Settings.KEY_PERMISSION_CUSTOMER_ADD, true);
		menu.findItem(R.id.addCustomer).setVisible(
				!drawerFragment.isDrawerOpen() && addCustomer);
		menu.findItem(R.id.addCustomer).setTitle("Add " + singular);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		EffortApplication.activityResumed();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onResume. Registering search done receiver.");
		}

		registerReceiver(searchDoneReceiver, new IntentFilter(
				CustomerSearchService.ACTION_DONE));
		getSupportLoaderManager().restartLoader(0, null, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EffortApplication.activityPaused();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onPause. Unregistering search done receiver.");
		}

		try {
			unregisterReceiver(searchDoneReceiver);
		} catch (IllegalArgumentException e) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Ignored the exception caught while unregistering search done receiver: "
								+ e.toString());
			}
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In onCreateLoader.");
		}

		String selection = "(" + Customers.DELETED + " = 'false' AND ("
				+ Customers.CUSTOMER_TYPE_ID + " IS NULL OR "
				+ Customers.CUSTOMER_TYPE_ID + " IN "
				+ customerTypesDao.getCheckedTypesIn() + "))";

		String mappedCustomers = settingsDao.getString("mappedCustomers");

		if (!TextUtils.isEmpty(mappedCustomers)) {
			selection = selection + " AND (" + Customers.REMOTE_ID
					+ " IS NULL OR " + Customers.REMOTE_ID + " IN ("
					+ mappedCustomers + "))";
		}

		String[] selectionArgs = null;

		if (!TextUtils.isEmpty(query)) {
			selection = selection + " AND (" + Customers.NAME + " LIKE ? OR "
					+ Customers.PC_FIRST_NAME + " LIKE ? " + " OR "
					+ Customers.PC_LAST_NAME + " LIKE ?)";
			selectionArgs = new String[] { "%" + query + "%",
					"%" + query + "%", "%" + query + "%" };
		}

		return new CursorLoader(
				getApplicationContext(),
				Customers.CONTENT_URI,
				Customers.SUMMARY_COLUMNS,
				selection,
				selectionArgs,
				getSupportActionBar().getSelectedNavigationIndex() == 0 ? (EffortProvider.Settings.SORT_ORDER_COMPANY.equals(settingsDao
						.getString(EffortProvider.Settings.KEY_SORT_ORDER)) ? "UPPER("
						+ Customers.NAME
						+ ")"
						+ ", "
						+ "UPPER("
						+ Customers.PC_FIRST_NAME
						+ ")"
						+ ", "
						+ "UPPER("
						+ Customers.PC_LAST_NAME + ")"
						: "UPPER(" + Customers.PC_FIRST_NAME + ")" + ", "
								+ "UPPER(" + Customers.PC_LAST_NAME + ")"
								+ ", " + "UPPER(" + Customers.NAME + ")")
						: Customers.DISTANCE);
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
		private final String TAG = "CustomersActivity.MyViewBinder";

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			// Log.d(TAG, "In setViewValue. View=" + view + ", Cursor Position="
			// + cursor.getPosition() + ", columnIndex=" + columnIndex);

			switch (columnIndex) {
			case Customers.SUMMARY_PC_FIRST_NAME_INDEX:
				TextView tv = (TextView) view;

				StringBuffer sb = new StringBuffer();

				String firstName = cursor
						.getString(Customers.SUMMARY_PC_FIRST_NAME_INDEX);

				if (firstName != null) {
					sb.append(firstName);
					sb.append(" ");
				}

				String lastName = cursor
						.getString(Customers.SUMMARY_PC_LAST_NAME_INDEX);

				if (lastName != null) {
					sb.append(lastName);
				}

				String name = sb.toString().trim();
				tv.setText(name);

				// Not hiding the control, so that it is easier to tap on the
				// customer item
				// tv.setVisibility(Utils.getVisibility(name));
				return true;

			case Customers.SUMMARY_CUSTOMER_TYPE_ID_INDEX:
				TextView typeTextView = (TextView) view;

				if (numTypes > 0) {
					typeTextView.setText(customerTypesDao.getName(cursor
							.getInt(Customers.SUMMARY_CUSTOMER_TYPE_ID_INDEX)));
					typeTextView.setVisibility(View.VISIBLE);
				} else {
					typeTextView.setVisibility(View.GONE);
				}

				return true;

			case Customers.SUMMARY_ID_INDEX:
				ImageButton button = (ImageButton) view;
				button.setOnClickListener(CustomersActivity.this);
				button.setTag(cursor.getLong(Customers.SUMMARY_ID_INDEX));

				if (ACTION_PICK.equals(action)) {
					button.setImageResource(R.drawable.ic_action_about);
				} else {
					button.setImageResource(R.drawable.ic_content_new_inverse);

					if (addJobPermission) {
						button.setVisibility(View.VISIBLE);
					} else {
						button.setVisibility(View.GONE);
					}
				}

				return true;

			case Customers.SUMMARY_DISTANCE_INDEX:
				TextView distanceTextView = (TextView) view;

				if (getSupportActionBar().getSelectedNavigationIndex() == 1) {
					if (cursor.isNull(Customers.SUMMARY_DISTANCE_INDEX)) {
						distanceTextView.setText("N/A");
					} else {
						float distance = cursor
								.getFloat(Customers.SUMMARY_DISTANCE_INDEX);
						distanceTextView.setText(String.format("%.1f km",
								distance / 1000));
					}

					distanceTextView.setVisibility(View.VISIBLE);
				} else {
					distanceTextView.setVisibility(View.GONE);
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
		// Log.i(TAG, "onItemClick Parent: " + parent.toString() + ", View: "
		// + view.toString() + ", Position: " + position + ", ID: " + id);

		if (ACTION_PICK.equals(action)) {
			Customer customer = customersDao.getCustomerWithLocalId(id);

			if (customer.getPartial()) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Fetching complete customer details.");
					Toast.makeText(this,
							"Fetching " + singular + " details...",
							Toast.LENGTH_SHORT).show();
				}

				registerReceiver(fetchDoneReceiver, new IntentFilter(
						FetchCustomerService.ACTION_DONE));
				Intent fetchIntent = new Intent(this,
						FetchCustomerService.class);
				fetchIntent
						.putExtra("remoteCustomerId", customer.getRemoteId());
				WakefulIntentService.sendWakefulWork(this, fetchIntent);
			} else {
				Intent intent = new Intent();
				intent.putExtra("localCustomerId", id);
				setResult(RESULT_OK, intent);
				finish();
			}

		} else {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Launching customer activity for customer id " + id);
			}

			Intent intent = new Intent(this, CustomerActivity.class);
			intent.putExtra(Customers._ID, id);
			startActivity(intent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerFragment.onOptionsItemSelected(item)) {
			return true;
		}

		Intent intent = null;

		switch (item.getItemId()) {
		case android.R.id.home:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "ActionBar's Up/home button clicked.");
			}

			onBackPressed();
			break;

		case R.id.addCustomer:
			intent = new Intent(this, CustomerActivity.class);
			intent.putExtra("localCustomerId", 0L);
			startActivity(intent);
			break;

		case R.id.pickNone:
			intent = new Intent();
			intent.putExtra("localCustomerId", 0L);
			setResult(RESULT_OK, intent);
			finish();
			break;

		default:
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Selected ID: " + item.getItemId());
			}

			if (numTypes > 1) {
				for (CustomerType type : types) {
					if (item.getItemId() == type.getId()) {
						item.setChecked(!item.isChecked());
						customerTypesDao.saveChecked(type.getName(),
								item.isChecked());
						getSupportLoaderManager().restartLoader(0, null, this);
					}
				}
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		// Log.d(TAG, "onClick called " + view.toString());
		// Log.d(TAG, "selectedItemId " + listView.getSelectedItemId());
		// Log.d(TAG, "tag " + view.getTag());

		// if (listView.getSelectedItemId() != ListView.INVALID_ROW_ID) {
		// Intent intent = new Intent(this, CustomerActivity.class);
		// intent.putExtra("localCustomerId", listView.getSelectedItemId());
		// startActivity(intent);
		// }
		if (view.getTag() != null) {
			if (ACTION_PICK.equals(action)) {
				Intent intent = new Intent(this, CustomerActivity.class);
				intent.setAction(CustomerActivity.ACTION_PICK);
				intent.putExtra(Customers._ID, (Long) view.getTag());
				startActivity(intent);
			} else {
				if (addJobPermission) {
					Intent jobIntent = new Intent(this, JobActivity.class);
					jobIntent.putExtra("localCustomerId", (Long) view.getTag());
					startActivity(jobIntent);
				}
			}
		}
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Query submitted: " + query);
		}

		this.query = query;
		getSupportLoaderManager().restartLoader(0, null, this);

		if (searchView != null) {
			searchInProgress = true;
			Intent intent = new Intent(this, CustomerSearchService.class);
			intent.putExtra("query", query);
			WakefulIntentService.sendWakefulWork(this, intent);
			MenuItemCompat
					.collapseActionView(menu.findItem(R.id.action_search));
			supportInvalidateOptionsMenu();
		}
		updateSubtitle();
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

	class SearchDoneReceiver extends BroadcastReceiver {
		/**
		 * Static constants cannot be declared in inner classes. Thus, the
		 * following TAG is non-static.
		 */
		public final String TAG = "SearchDoneReceiver";

		@Override
		public void onReceive(Context context, Intent intent) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "In onReceive. Restarting loader.");
			}

			if (intent.hasExtra("error")) {
				Toast.makeText(CustomersActivity.this,
						intent.getStringExtra("error"), Toast.LENGTH_LONG)
						.show();
			} else {
				getSupportLoaderManager().restartLoader(0, null,
						CustomersActivity.this);
			}

			searchInProgress = false;
			supportInvalidateOptionsMenu();
		}
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

			unregisterReceiver(this);
			Intent returnCustomerIntent = new Intent();
			long localCustomerId = intent.getLongExtra("localCustomerId", 0);
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Returning local customer id" + localCustomerId);
			}
			returnCustomerIntent.putExtra("localCustomerId", localCustomerId);
			CustomersActivity.this.setResult(RESULT_OK, returnCustomerIntent);
			finish();
		}
	}

	private void updateSubtitle() {
		ActionBar actionBar = getSupportActionBar();
		String subtitle = "All " + plural;

		if (settingsDao.getBoolean(Settings.KEY_PREFETCH_NEARBY_CUSTOMERS,
				false)) {
			if (actionBar.getSelectedNavigationIndex() == 0) {
				if (!TextUtils.isEmpty(query)) {
					subtitle = "Matching " + query;
				}
			} else {
				String near = settingsDao.getString("nearbyAddress");

				if (TextUtils.isEmpty(near)) {
					subtitle = "";
				} else {
					subtitle = "Near " + near;
				}
			}
		} else {
			if (!TextUtils.isEmpty(query)) {
				subtitle = "Matching " + query;
			}
		}

		actionBar.setSubtitle(subtitle);

		if (drawerFragment != null) {
			drawerFragment.setActivitySubtitle(subtitle);
		}
	}

	@Override
	public void onBackPressed() {
		if (!ACTION_PICK.equals(action)) {
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}

		super.onBackPressed();
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		getSupportLoaderManager().restartLoader(0, null, this);
		updateSubtitle();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// do nothing
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		getSupportLoaderManager().restartLoader(0, null, this);
		updateSubtitle();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (settingsDao.getBoolean(Settings.KEY_PREFETCH_NEARBY_CUSTOMERS,
				false)) {
			outState.putInt("activeTab", getSupportActionBar()
					.getSelectedNavigationIndex());
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
	public void onRefresh() {
		getSupportLoaderManager().restartLoader(0, null, this);
	}

}

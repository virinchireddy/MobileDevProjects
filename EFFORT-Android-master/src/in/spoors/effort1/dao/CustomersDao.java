package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.Customer;
import in.spoors.effort1.provider.EffortProvider.Customers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

public class CustomersDao {

	public static final String TAG = "CustomersDao";
	private static CustomersDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static CustomersDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new CustomersDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private CustomersDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean customerWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Customers._ID
					+ ") AS count FROM " + Customers.TABLE + " WHERE "
					+ Customers._ID + " = " + localId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean customerWithRemoteIdExists(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Customers.REMOTE_ID
					+ ") AS count FROM " + Customers.TABLE + " WHERE "
					+ Customers.REMOTE_ID + " = " + remoteId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Saves the given job.
	 * 
	 * Local ID of the job is updated with the inserted ID.
	 * 
	 * @param customer
	 */
	public synchronized void save(Customer customer) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		if (customer.getLocalId() != null
				&& customerWithLocalIdExists(customer.getLocalId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting customer.");
			}

			customer.setLocalModificationTime(new Date());
			customer.setLocalCreationTime(getLocalCreationTimeWithLocalId(customer
					.getLocalId()));

			ContentValues values = customer.getContentValues(null);

			db.update(Customers.TABLE, values,
					Customers._ID + " = " + customer.getLocalId(), null);

			return;
		}

		if (customer.getRemoteId() != null
				&& customerWithRemoteIdExists(customer.getRemoteId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting customer.");
			}

			customer.setLocalModificationTime(new Date());
			customer.setLocalCreationTime(getLocalCreationTimeWithRemoteId(customer
					.getRemoteId()));

			ContentValues values = customer.getContentValues(null);

			db.update(Customers.TABLE, values, Customers.REMOTE_ID + " = "
					+ customer.getRemoteId(), null);

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new customer.");
		}

		Date now = new Date();
		customer.setLocalCreationTime(now);
		customer.setLocalModificationTime(now);

		ContentValues values = customer.getContentValues(null);
		long insertedId = db.insert(Customers.TABLE, null, values);
		customer.setLocalId(insertedId);
	}

	/**
	 * Returns the local customer id of the given remote customer id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Long getLocalId(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Customers._ID + " FROM "
					+ Customers.TABLE + " WHERE " + Customers.REMOTE_ID + " = "
					+ remoteId, null);

			if (cursor.moveToNext() && !cursor.isNull(0)) {
				return cursor.getLong(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Returns the remote customer id of the given local customer id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Long getRemoteId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Customers.REMOTE_ID + " FROM "
					+ Customers.TABLE + " WHERE " + Customers._ID + " = "
					+ localId, null);

			if (cursor.moveToNext() && !cursor.isNull(0)) {
				return cursor.getLong(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Returns the local creation time of the given local customer id.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithLocalId(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Customers.LOCAL_CREATION_TIME
					+ " FROM " + Customers.TABLE + " WHERE " + Customers._ID
					+ " = " + localId, null);

			if (cursor.moveToNext()) {
				return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
			} else {
				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Returns the local creation time of the given remote customer id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithRemoteId(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Customers.LOCAL_CREATION_TIME
					+ " FROM " + Customers.TABLE + " WHERE "
					+ Customers.REMOTE_ID + " = " + remoteId, null);

			if (cursor.moveToNext()) {
				return SQLiteDateTimeUtils.getLocalTime(cursor.getString(0));
			} else {
				return null;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public void deleteCustomer(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(Customers.TABLE, Customers._ID + " = ?",
				new String[] { "" + localId });

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted customer with local customer id: " + localId
					+ ", affectedRows=" + affectedRows);
		}
	}

	public void deletePartialCustomers() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		int affectedRows = db.delete(Customers.TABLE, Customers.PARTIAL
				+ " = 'true' AND " + Customers.IN_USE + " = 'false'", null);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " partial customers.");
		}
	}

	public Customer getCustomerWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Customer customer = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Customers.TABLE, Customers.ALL_COLUMNS,
					Customers._ID + " = " + localId, null, null, null, null);

			if (cursor.moveToNext()) {
				customer = new Customer();
				customer.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return customer;
	}

	public Customer getCustomerWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Customer customer = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Customers.TABLE, Customers.ALL_COLUMNS,
					Customers.REMOTE_ID + " = " + remoteId, null, null, null,
					null);

			if (cursor.moveToNext()) {
				customer = new Customer();
				customer.load(cursor);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return customer;
	}

	public String getCompanyNameWithLocalId(long localCustomerId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		String name = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Customers.TABLE, new String[] { Customers.NAME },
					Customers._ID + " = " + localCustomerId, null, null, null,
					null);

			if (cursor.moveToNext()) {
				name = cursor.getString(0);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return name;
	}

	/**
	 * Get the customers that are added on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<Customer> getAddedCustomers() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Customer> customers = null;

		try {
			cursor = db.query(Customers.TABLE, Customers.ALL_COLUMNS,
					Customers.REMOTE_ID + " IS NULL", null, null, null,
					Customers.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				customers = new ArrayList<Customer>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Customer customer = new Customer();
				customer.load(cursor);
				customers.add(customer);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return customers;
	}

	/**
	 * Get the customers that are modified on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<Customer> getModifiedCustomers() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Customer> customers = null;

		try {
			cursor = db.query(Customers.TABLE, Customers.ALL_COLUMNS,
					Customers.REMOTE_ID + " IS NOT NULL AND " + Customers.DIRTY
							+ " = 'true'", null, null, null,
					Customers.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				customers = new ArrayList<Customer>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Customer customer = new Customer();
				customer.load(cursor);
				customers.add(customer);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return customers;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasUnsyncedChanges() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Customers.TABLE, new String[] { Customers._ID },
					Customers.REMOTE_ID + " IS NULL OR " + Customers.DIRTY
							+ " = 'true'", null, null, null, null);

			return cursor.getCount() > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Get all the remote customer IDs.
	 * 
	 * @return
	 */
	public List<Long> getAllRemoteCustomerIds() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Long> ids = null;

		try {
			cursor = db.query(Customers.TABLE,
					new String[] { Customers.REMOTE_ID }, Customers.REMOTE_ID
							+ " IS NOT NULL", null, null, null,
					Customers.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				ids = new ArrayList<Long>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				ids.add(cursor.getLong(0));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return ids;
	}

	public synchronized void updateCustomerIdMapping(long localCustomerId,
			long remoteCustomerId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		db.execSQL("UPDATE " + Customers.TABLE + " SET " + Customers.REMOTE_ID
				+ " = " + remoteCustomerId + ", " + Customers.DIRTY
				+ " = 'false' WHERE " + Customers._ID + " = " + localCustomerId);
	}

	public synchronized void updateDirtyFlag(boolean dirty,
			long remoteCustomerId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		db.execSQL("UPDATE " + Customers.TABLE + " SET " + Customers.DIRTY
				+ " = '" + dirty + "' WHERE " + Customers.REMOTE_ID + " = "
				+ remoteCustomerId);
	}

	/**
	 * Get the customers that are added on the device, and need to be synced.
	 * 
	 * @return
	 */
	public List<Customer> getCustomersWithCoordinates() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Customer> customers = new ArrayList<Customer>();

		try {
			cursor = db.query(Customers.TABLE, Customers.ALL_COLUMNS,
					Customers.LATITUDE + " IS NOT NULL AND "
							+ Customers.LONGITUDE + " IS NOT NULL", null, null,
					null, Customers.LOCAL_CREATION_TIME);

			while (cursor.moveToNext()) {
				Customer customer = new Customer();
				customer.load(cursor);
				customers.add(customer);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return customers;
	}

	/**
	 * Get the customers with remote IDs.
	 * 
	 * @return
	 */
	public List<Customer> getCustomersWithRemoteIds(String remoteIds) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Customer> customers = new ArrayList<Customer>();

		try {
			cursor = db.query(Customers.TABLE, Customers.ALL_COLUMNS,
					Customers.REMOTE_ID + " IN (" + remoteIds + ")", null,
					null, null, Customers.NAME);

			while (cursor.moveToNext()) {
				Customer customer = new Customer();
				customer.load(cursor);
				customers.add(customer);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return customers;
	}

	public List<Customer> getCustomersWithRemoteIds(String remoteIds,
			String orderBy) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Customer> customers = new ArrayList<Customer>();

		try {
			cursor = db.query(Customers.TABLE, Customers.ALL_COLUMNS,
					Customers.REMOTE_ID + " IN (" + remoteIds + ")", null,
					null, null, orderBy + " ASC");

			while (cursor.moveToNext()) {
				Customer customer = new Customer();
				customer.load(cursor);
				customers.add(customer);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return customers;
	}

	public List<Customer> getCustomersHavingLocationWithRemoteIds(
			String remoteIds, String orderBy) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Customer> customers = new ArrayList<Customer>();

		try {
			cursor = db.query(Customers.TABLE, Customers.ALL_COLUMNS,
					Customers.REMOTE_ID + " IN (" + remoteIds + ") AND "
							+ Customers.LATITUDE + " IS NOT NULL AND "
							+ Customers.LONGITUDE + " IS NOT NULL", null, null,
					null, orderBy + " ASC");

			while (cursor.moveToNext()) {
				Customer customer = new Customer();
				customer.load(cursor);
				customers.add(customer);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return customers;
	}

	public synchronized void updateDistance(float distance, long localCustomerId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		db.execSQL("UPDATE " + Customers.TABLE + " SET " + Customers.DISTANCE
				+ " = " + distance + " WHERE " + Customers._ID + " = "
				+ localCustomerId);
	}

	/**
	 * Get the customers that are visible in customers screen
	 * 
	 * @return
	 */
	public List<Long> getVisibleCustomers() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		SettingsDao settingsDao = SettingsDao.getInstance(applicationContext);
		CustomerTypesDao customerTypesDao = CustomerTypesDao
				.getInstance(applicationContext);
		Cursor cursor = null;
		List<Long> customers = null;

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

		try {
			cursor = db.query(Customers.TABLE, new String[] { Customers._ID },
					selection, null, null, null, Customers.LOCAL_CREATION_TIME);

			if (cursor.getCount() > 0) {
				customers = new ArrayList<Long>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				customers.add(cursor.getLong(0));
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return customers;
	}

}

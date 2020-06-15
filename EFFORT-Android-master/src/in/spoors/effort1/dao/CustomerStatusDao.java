package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.CustomerStatusDto;
import in.spoors.effort1.provider.EffortProvider.CustomerStatus;
import in.spoors.effort1.provider.EffortProvider.Locations;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

public class CustomerStatusDao {
	public static final String TAG = "CustomerStatusDao";
	private static CustomerStatusDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static CustomerStatusDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new CustomerStatusDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private CustomerStatusDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(CustomerStatusDto customerStatus) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		if (customerStatus.getAssignRouteId() != null
				&& isExistsCustomerStatusWithAssignedRouteIdAndCustomerId(
						customerStatus.getAssignRouteId(),
						customerStatus.getCustomerId())) {
			CustomerStatusDto custStatus = getCustomerStatusWithAssignedRouteIdAndCustomerId(
					customerStatus.getCustomerId(),
					customerStatus.getAssignRouteId());
			customerStatus.setLoaclId(custStatus.getLoaclId());

			ContentValues values = customerStatus.getContentValues(null);

			if (BuildConfig.DEBUG) {
				Log.i(TAG,
						"Updating the existing activity: "
								+ customerStatus.toString());
			}

			db.update(CustomerStatus.TABLE, values, CustomerStatus._ID + " = "
					+ customerStatus.getLoaclId(), null);

			customerStatus.setLoaclId(getLocalId(customerStatus));
			return;
		}
		ContentValues values = customerStatus.getContentValues(null);
		long insertedId = db.insert(CustomerStatus.TABLE, null, values);
		customerStatus.setLoaclId(insertedId);
		if (BuildConfig.DEBUG) {
			Log.i(TAG,
					"Saved a new CustomerStatusDto: "
							+ customerStatus.toString());
		}
	}

	// public List<CustomerStatusDto> getAllUnSyncCustomersStatus() {
	// SQLiteDatabase db = DBHelper.getInstance(applicationContext)
	// .getReadableDatabase();
	//
	// Cursor cursor = null;
	// List<CustomerStatusDto> completedActivities = null;
	//
	// try {
	// // cursor = queryWith2WayJoin(JobHistories.REMOTE_ID +
	// // " IS NULL AND "
	// // + Locations.LOCATION_FINALIZED + " = 'true'", null, null);
	// cursor = db.query(CustomerStatus.TABLE, CustomerStatus.ALL_COLUMNS,
	// CustomerStatus.DIRTY + " = 'true'", null, null, null, null);
	// if (cursor.getCount() > 0) {
	// completedActivities = new ArrayList<CustomerStatusDto>(
	// cursor.getCount());
	// }
	//
	// while (cursor.moveToNext()) {
	// CustomerStatusDto completedActivity = new CustomerStatusDto();
	// completedActivity.load(cursor);
	// completedActivities.add(completedActivity);
	// }
	// } finally {
	// if (cursor != null) {
	// cursor.close();
	// }
	// }
	//
	// return completedActivities;
	// }

	public List<CustomerStatusDto> getUnsyncedCustomerStatus() {
		Cursor cursor = null;
		List<CustomerStatusDto> customersStatus = null;

		try {
			cursor = queryWithLocations(CustomerStatus.TABLE + "."
					+ CustomerStatus.DIRTY + " = 'true' AND ("
					+ Locations.LOCATION_FINALIZED + " = 'true' OR "
					+ Locations.LOCATION_FINALIZED + " IS NULL)", null, null);

			if (cursor.getCount() > 0) {
				customersStatus = new ArrayList<CustomerStatusDto>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				CustomerStatusDto customerStatus = new CustomerStatusDto();
				customerStatus.load(cursor);
				customersStatus.add(customerStatus);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return customersStatus;
	}

	private Cursor queryWithLocations(String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(CustomerStatus.TABLE + " LEFT JOIN "
				+ Locations.TABLE + " ON " + CustomerStatus.TABLE + "."
				+ CustomerStatus._ID + " = " + Locations.TABLE + "."
				+ Locations.FOR_ID + " AND " + Locations.TABLE + "."
				+ Locations.PURPOSE + " = "
				+ Locations.PURPOSE_CUSTOMER_STATUS_ACTIVITY);

		// qualify the columns by the table name, so that join
		// doesn't confuse the query engine with ambiguous column
		// names
		String[] columns = new String[CustomerStatus.ALL_COLUMNS.length];
		for (int i = 0; i < CustomerStatus.ALL_COLUMNS.length; ++i) {
			columns[i] = CustomerStatus.TABLE + "."
					+ CustomerStatus.ALL_COLUMNS[i];
		}

		builder.setDistinct(true);
		return builder.query(db, columns, selection, selectionArgs, null, null,
				sortOrder);
	}

	public CustomerStatusDto getCustomerStatusWithAssignedRouteIdAndCustomerId(
			long customerId, long assignedRouteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		CustomerStatusDto assignedRoute = null;

		try {
			cursor = db.query(CustomerStatus.TABLE, CustomerStatus.ALL_COLUMNS,
					CustomerStatus.ASSIGN_ROUTE_ID + " = " + assignedRouteId
							+ " AND " + CustomerStatus.CUSTOMER_ID + " = "
							+ customerId, null, null, null, null);

			if (cursor.moveToNext()) {

				assignedRoute = new CustomerStatusDto();
				assignedRoute.load(cursor);
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return assignedRoute;
	}

	public boolean isExistsCustomerStatusWithAssignedRouteIdAndCustomerId(
			long assignedRouteId, long customerId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(CustomerStatus.TABLE, CustomerStatus.ALL_COLUMNS,
					CustomerStatus.ASSIGN_ROUTE_ID + " = " + assignedRouteId
							+ " AND " + CustomerStatus.CUSTOMER_ID + " = "
							+ customerId, null, null, null, null);

			if (cursor.getCount() > 0) {
				return true;
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return false;
	}

	public int getNumberOfCompletedCustomerVisitsWithAssignRouteId(
			Long assignRouteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + CustomerStatus._ID
					+ ") FROM " + CustomerStatus.TABLE + " WHERE "
					+ CustomerStatus.ASSIGN_ROUTE_ID + "=" + assignRouteId
					+ " AND " + CustomerStatus.STATUS + " = 'true'", null);

			cursor.moveToNext();

			return cursor.getInt(0);

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public Long getLocalId(CustomerStatusDto customerStatus) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		if (customerStatus == null || customerStatus.getAssignRouteId() == null
				|| customerStatus.getCustomerId() == null) {
			return null;
		}
		try {
			cursor = db.rawQuery(
					"SELECT " + CustomerStatus._ID + " FROM "
							+ CustomerStatus.TABLE + " WHERE "
							+ CustomerStatus.ASSIGN_ROUTE_ID + " = "
							+ customerStatus.getAssignRouteId() + " AND "
							+ CustomerStatus.CUSTOMER_ID + " = "
							+ customerStatus.getCustomerId(), null);

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

}

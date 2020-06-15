package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.CustomerType;
import in.spoors.effort1.provider.EffortProvider.CustomerTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 
 * @author tiru
 */
public class CustomerTypesDao {
	public static final String TAG = "CustomerTypesDao";

	private static CustomerTypesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static CustomerTypesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new CustomerTypesDao(applicationContext);
		}

		return instance;
	}

	private CustomerTypesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public String getName(int id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(CustomerTypes.TABLE,
					new String[] { CustomerTypes.NAME }, CustomerTypes._ID
							+ " = " + id, null, null, null, null);

			if (cursor.moveToNext()) {
				return cursor.getString(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public Integer getId(String name) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(CustomerTypes.TABLE,
					new String[] { CustomerTypes._ID }, CustomerTypes.NAME
							+ " = ?", new String[] { name }, null, null, null);

			if (cursor.moveToNext()) {
				return cursor.getInt(0);
			} else {
				return null;
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * 
	 * @param name
	 * @return null if name is not found
	 */
	public Boolean getChecked(String name) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(CustomerTypes.TABLE,
					new String[] { CustomerTypes.CHECKED }, CustomerTypes.NAME
							+ " = ?", new String[] { name }, null, null, null);

			if (cursor.moveToNext()) {
				return Boolean.parseBoolean(cursor.getString(0));
			} else {
				return null;
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	public synchronized void saveChecked(String name, boolean checked) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(CustomerTypes.CHECKED, String.valueOf(checked));

		int rowsAffected = db.update(CustomerTypes.TABLE, values,
				CustomerTypes.NAME + " = ?", new String[] { name });

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Updated the checked state of " + rowsAffected
					+ " customer type.");
		}
	}

	/**
	 * 
	 * @return null if there are no job types
	 */
	public List<String> getNames() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<String> names = null;

		try {
			cursor = db.query(CustomerTypes.TABLE,
					new String[] { CustomerTypes.NAME }, null, null, null,
					null, CustomerTypes.NAME);

			if (cursor.getCount() > 0) {
				names = new ArrayList<String>();
			}

			while (cursor.moveToNext()) {
				names.add(cursor.getString(0));
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return names;
	}

	/**
	 * 
	 * @return null if there are no customer types
	 */
	public List<CustomerType> getCustomerTypes() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<CustomerType> types = null;

		try {
			cursor = db.query(CustomerTypes.TABLE, CustomerTypes.ALL_COLUMNS,
					null, null, null, null, "UPPER(" + CustomerTypes.NAME + ")"
							+ " ASC");

			if (cursor.getCount() > 0) {
				types = new ArrayList<CustomerType>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				CustomerType type = new CustomerType();
				type.load(cursor);
				types.add(type);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return types;
	}

	public boolean areTypesDifferent(List<CustomerType> list1,
			List<CustomerType> list2) {
		if (list1 == null && list2 == null) {
			return false;
		} else if (list1 == null || list2 == null) {
			return true;
		}

		int size = list1.size();

		if (size != list2.size()) {
			return true;
		}

		List<CustomerType> sortedList1 = new ArrayList<CustomerType>(list1);
		List<CustomerType> sortedList2 = new ArrayList<CustomerType>(list2);
		Collections.sort(sortedList1);
		Collections.sort(sortedList2);

		// we'll reach here when both the lists contains the same number of
		// items
		for (int i = 0; i < size; ++i) {
			CustomerType type1 = sortedList1.get(i);
			CustomerType type2 = sortedList2.get(i);

			if (type1.getId() != type2.getId()
					|| !type1.getName().equals(type2.getName())) {
				return true;
			}
		}

		return false;
	}

	public synchronized void save(CustomerType customerType) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = customerType.getContentValues(null);
		long id = db.insert(CustomerTypes.TABLE, null, values);

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Saved: " + customerType
					+ ". Inserted customer type ID: " + id);
		}
	}

	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.delete(CustomerTypes.TABLE, null, null);
	}

	/**
	 * 
	 * @return null if there are no job types
	 */
	public String getCheckedTypesIn() {
		StringBuffer sb = new StringBuffer("(");
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(CustomerTypes.TABLE,
					new String[] { CustomerTypes._ID }, CustomerTypes.CHECKED
							+ " = 'true'", null, null, null, null);

			int count = cursor.getCount();

			for (int i = 0; i < count; ++i) {
				cursor.moveToNext();
				sb.append(cursor.getInt(0));

				if (i < count - 1) {
					sb.append(", ");
				}
			}

			sb.append(")");
		} finally {
			if (cursor != null)
				cursor.close();
		}

		String in = sb.toString();
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "In clause: " + in);
		}

		return in;
	}

}

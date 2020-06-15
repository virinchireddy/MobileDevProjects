package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.Employee;
import in.spoors.effort1.provider.EffortProvider.Employees;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class EmployeesDao {

	public static final String TAG = "EmployeesDao";
	private static EmployeesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static EmployeesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new EmployeesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private EmployeesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(Employee employee) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (employee.getLocalId() != null
				&& employeeWithLocalIdExists(employee.getLocalId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting employee.");
			}

			ContentValues values = employee.getContentValues(null);

			db.update(Employees.TABLE, values,
					Employees._ID + " = " + employee.getLocalId(), null);

			return;
		}

		if (employee.getEmpId() != null
				&& employeeWithRemoteIdExists(employee.getEmpId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting employee.");
			}

			ContentValues values = employee.getContentValues(null);

			db.update(Employees.TABLE, values, Employees.EMPLOYEE_ID + " = "
					+ employee.getEmpId(), null);

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new employee.");
		}

		ContentValues values = employee.getContentValues(null);
		long insertedId = db.insert(Employees.TABLE, null, values);
		employee.setLocalId(insertedId);
	}

	public boolean employeeWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Employees._ID
					+ ") AS count FROM " + Employees.TABLE + " WHERE "
					+ Employees._ID + " = " + localId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean employeeWithRemoteIdExists(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Employees._ID
					+ ") AS count FROM " + Employees.TABLE + " WHERE "
					+ Employees.EMPLOYEE_ID + " = " + remoteId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<Employee> getEmployees(String employeeIds) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Employee> employees = null;

		try {
			cursor = db.query(Employees.TABLE, Employees.ALL_COLUMNS,
					Employees.EMPLOYEE_ID + " IN(" + employeeIds + ")", null,
					null, null, null);

			if (cursor.getCount() > 0) {
				employees = new ArrayList<Employee>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				Employee employee = new Employee();
				employee.load(cursor);
				employees.add(employee);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return employees;
	}

	public synchronized void deleteLeaveWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.execSQL("DELETE FROM " + Employees.TABLE + " WHERE " + Employees._ID
				+ " = " + localId);
	}

	public synchronized void deleteLeaveWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.execSQL("DELETE FROM " + Employees.TABLE + " WHERE "
				+ Employees.EMPLOYEE_ID + " = " + remoteId);
	}

	public String getEmployeeNameWithLocalId(long localCustomerId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		String name = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Employees.TABLE,
					new String[] { Employees.EMPLOYEE_FIRST_NAME },
					Employees._ID + " = " + localCustomerId, null, null, null,
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

	public Long getEmployeeRankWithId(long empId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Long name = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Employees.TABLE, new String[] { Employees.RANK },
					Employees.EMPLOYEE_ID + " = " + empId, null, null, null,
					null);

			if (cursor.moveToNext()) {
				name = cursor.getLong(0);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return name;
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
			cursor = db.rawQuery("SELECT " + Employees.EMPLOYEE_ID + " FROM "
					+ Employees.TABLE + " WHERE " + Employees._ID + " = "
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
	 * Returns the local form id of the given remote form id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Long getLocalId(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Employees._ID + " FROM "
					+ Employees.TABLE + " WHERE " + Employees.EMPLOYEE_ID
					+ " = " + remoteId, null);

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

	/*
	 * get all id's of all employees in db
	 */
	public List<Long> getEmployees() {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Long> employeeIds = null;

		try {

			SettingsDao settingsDao = SettingsDao
					.getInstance(applicationContext);
			String emploueeIds = settingsDao
					.getString(Settings.KEY_EMPLOYEE_UNDER_ID);
			cursor = db.query(Employees.TABLE, new String[] { Employees._ID },
					Employees.EMPLOYEE_ID + " IN(" + emploueeIds + ")", null,
					null, null, null);

			if (cursor.getCount() > 0) {
				employeeIds = new ArrayList<Long>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				employeeIds.add(cursor.getLong(0));
			}

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return employeeIds;
	}
}

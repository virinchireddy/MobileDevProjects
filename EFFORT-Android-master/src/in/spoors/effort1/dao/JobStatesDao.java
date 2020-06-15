package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dto.JobState;
import in.spoors.effort1.provider.EffortProvider.JobStates;
import in.spoors.effort1.provider.EffortProvider.TypeStateMappings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

/**
 * 
 * @author tiru
 */
public class JobStatesDao {
	public static final String TAG = "JobStatesDao";

	private static JobStatesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static JobStatesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new JobStatesDao(applicationContext);
		}

		return instance;
	}

	private JobStatesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public String getName(int id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(JobStates.TABLE, new String[] { JobStates.NAME },
					JobStates._ID + " = " + id, null, null, null, null);

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
			cursor = db.query(JobStates.TABLE, new String[] { JobStates._ID },
					JobStates.NAME + " = ?", new String[] { name }, null, null,
					null);

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

	public Integer getStartStateId(int typeId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + JobStates.TABLE + "."
					+ JobStates._ID + " FROM " + JobStates.TABLE + " JOIN "
					+ TypeStateMappings.TABLE + " ON " + JobStates.TABLE + "."
					+ JobStates._ID + " = " + TypeStateMappings.STATE_ID
					+ " WHERE " + TypeStateMappings.TYPE_ID + " = " + typeId
					+ " AND " + JobStates.START_STATE + " = 'true' AND "
					+ JobStates.DEFAULT_STATE + " = 'true'", null);

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

	public Integer getEndStateId(int typeId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + JobStates.TABLE + "."
					+ JobStates._ID + " FROM " + JobStates.TABLE + " JOIN "
					+ TypeStateMappings.TABLE + " ON " + JobStates.TABLE + "."
					+ JobStates._ID + " = " + TypeStateMappings.STATE_ID
					+ " WHERE " + TypeStateMappings.TYPE_ID + " = " + typeId
					+ " AND " + JobStates.END_STATE + " = 'true' AND "
					+ JobStates.DEFAULT_STATE + " = 'true'", null);

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
	 * @return null if there are no job states mapped to
	 */
	public List<String> getNames(int typeId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<String> names = null;

		try {
			cursor = db.rawQuery("SELECT " + JobStates.NAME + " FROM "
					+ JobStates.TABLE + " JOIN " + TypeStateMappings.TABLE
					+ " ON " + JobStates.TABLE + "." + JobStates._ID + " = "
					+ TypeStateMappings.STATE_ID + " WHERE "
					+ TypeStateMappings.TYPE_ID + " = " + typeId + " ORDER BY "
					+ TypeStateMappings.DISPLAY_ORDER, null);

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
	 * @return null if there are no job states
	 */
	public JobState getJobState(int id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(JobStates.TABLE, JobStates.ALL_COLUMNS,
					JobStates._ID + " = " + id, null, null, null, null);

			if (cursor.moveToNext()) {
				JobState state = new JobState();
				state.load(cursor);
				return state;
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return null;
	}

	public Long getFormSpecId(int id) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(JobStates.TABLE,
					new String[] { JobStates.FORM_SPEC_ID }, JobStates._ID
							+ " = " + id, null, null, null, null);

			if (cursor.moveToNext()) {
				if (cursor.isNull(0)) {
					return null;
				} else {
					return cursor.getLong(0);
				}
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return null;
	}

	/**
	 * @return null if there are no job states
	 */
	public List<JobState> getJobStates() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();

		Cursor cursor = null;
		List<JobState> states = null;

		try {
			cursor = db.query(JobStates.TABLE, JobStates.ALL_COLUMNS, null,
					null, null, null, null);

			if (cursor.getCount() > 0) {
				states = new ArrayList<JobState>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				JobState state = new JobState();
				state.load(cursor);
				states.add(state);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return states;
	}

	/**
	 * @param if not null, returns only the states associated with the given
	 *        type.
	 * @return null if there are no job states
	 */
	public List<JobState> getJobStates(Integer typeId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		Cursor cursor = null;
		List<JobState> states = null;

		int numColumns = JobStates.ALL_COLUMNS.length;
		String[] columns = new String[numColumns];
		for (int i = 0; i < numColumns; ++i) {
			columns[i] = JobStates.TABLE + "." + JobStates.ALL_COLUMNS[i];
		}

		try {
			builder.setTables(JobStates.TABLE + " JOIN "
					+ TypeStateMappings.TABLE + " ON " + JobStates.TABLE + "."
					+ JobStates._ID + " = " + TypeStateMappings.STATE_ID);
			cursor = builder.query(db, columns, typeId == null ? ""
					: TypeStateMappings.TYPE_ID + " = " + typeId, null, null,
					null, TypeStateMappings.DISPLAY_ORDER);

			if (cursor.getCount() > 0) {
				states = new ArrayList<JobState>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				JobState state = new JobState();
				state.load(cursor);
				states.add(state);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return states;
	}

	public boolean areStatesDifferent(List<JobState> list1, List<JobState> list2) {
		if (list1 == null && list2 == null) {
			return false;
		} else if (list1 == null || list2 == null) {
			return true;
		}

		int size = list1.size();

		if (size != list2.size()) {
			return true;
		}

		List<JobState> sortedList1 = new ArrayList<JobState>(list1);
		List<JobState> sortedList2 = new ArrayList<JobState>(list2);
		Collections.sort(sortedList1);
		Collections.sort(sortedList2);

		// we'll reach here when both the lists contains the same number of
		// items
		for (int i = 0; i < size; ++i) {
			JobState state1 = sortedList1.get(i);
			JobState state2 = sortedList2.get(i);

			if (state1.getId() != state2.getId()
					|| state1.getName().equals(state2.getName()) == false
					|| !Utils.longsEqual(state1.getFormSpecId(),
							state2.getFormSpecId())
					|| state1.isFormRequired() != state2.isFormRequired()
					|| state1.isStartState() != state2.isStartState()
					|| state1.isEndState() != state2.isEndState()
					|| state1.isDefaultState() != state2.isDefaultState()
					|| state1.isRevisitable() != state2.isRevisitable()
					|| state1.getMinSubmissions() != state2.getMinSubmissions()
					|| state1.getMaxSubmissions() != state2.getMaxSubmissions()) {
				return true;
			}
		}

		return false;
	}

	public boolean stateWithIdExists(long stateId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + JobStates._ID
					+ ") AS count FROM " + JobStates.TABLE + " WHERE "
					+ JobStates._ID + " = " + stateId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public synchronized void save(JobState jobState) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = jobState.getContentValues(null);

		if (stateWithIdExists(jobState.getId())) {
			db.update(JobStates.TABLE, values,
					JobStates._ID + " = " + jobState.getId(), null);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Updated: " + jobState);
			}
		} else {
			long id = db.insert(JobStates.TABLE, null, values);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Saved: " + jobState + ". Inserted job state ID: "
						+ id);
			}
		}
	}

	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.delete(JobStates.TABLE, null, null);
	}

}

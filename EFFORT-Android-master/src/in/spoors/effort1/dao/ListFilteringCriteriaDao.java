package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.ListFilteringCriteria;
import in.spoors.effort1.dto.ViewField;
import in.spoors.effort1.provider.EffortProvider.ListFilteringCriterias;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ListFilteringCriteriaDao {

	public static final String TAG = "VisibilityCriteriaDao";
	private static ListFilteringCriteriaDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static ListFilteringCriteriaDao getInstance(
			Context applicationContext) {
		if (instance == null) {
			instance = new ListFilteringCriteriaDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private ListFilteringCriteriaDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(ListFilteringCriteria listFilteringCriteria) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (listFilteringCriteria.getLocalId() != null
				&& employeeWithLocalIdExists(listFilteringCriteria.getLocalId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting list Filtering Criteria.");
			}

			ContentValues values = listFilteringCriteria.getContentValues(null);

			db.update(
					ListFilteringCriterias.TABLE,
					values,
					ListFilteringCriterias._ID + " = "
							+ listFilteringCriteria.getLocalId(), null);

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new list Filtering Criteria.");
		}

		ContentValues values = listFilteringCriteria.getContentValues(null);
		long insertedId = db.insert(ListFilteringCriterias.TABLE, null, values);
		listFilteringCriteria.setLocalId(insertedId);
	}

	public boolean employeeWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + ListFilteringCriterias._ID
					+ ") AS count FROM " + ListFilteringCriterias.TABLE
					+ " WHERE " + ListFilteringCriterias._ID + " = " + localId,
					null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<ListFilteringCriteria> getListFilteringCriterias(long formSpecId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<ListFilteringCriteria> listFilteringCriterias = null;

		try {
			cursor = db.query(ListFilteringCriterias.TABLE,
					ListFilteringCriterias.ALL_COLUMNS,
					ListFilteringCriterias.FORM_SPEC_ID + " = " + formSpecId,
					null, null, null, null);

			if (cursor.getCount() > 0) {
				listFilteringCriterias = new ArrayList<ListFilteringCriteria>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				ListFilteringCriteria listFilteringCriteria = new ListFilteringCriteria();
				listFilteringCriteria.load(cursor);
				listFilteringCriterias.add(listFilteringCriteria);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return listFilteringCriterias;
	}

	public boolean hasListFilteringCriteriaDependency(ViewField viewField,
			int type) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {

			cursor = db.query(
					ListFilteringCriterias.TABLE,
					new String[] { ListFilteringCriterias._ID },
					ListFilteringCriterias.FORM_SPEC_ID + " = "
							+ viewField.getFormSpecId() + " AND "
							+ ListFilteringCriterias.TYPE + " = " + type
							+ " AND " + ListFilteringCriterias.FIELD_SPEC_ID
							+ " = " + viewField.getFieldSpecId(), null, null,
					null, null);

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

	public List<ListFilteringCriteria> getListFilteringCriterias(
			long formSpecId, long fieldSpecId, int type) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<ListFilteringCriteria> listFilteringCriterias = null;

		try {
			cursor = db.query(ListFilteringCriterias.TABLE,
					ListFilteringCriterias.ALL_COLUMNS,
					ListFilteringCriterias.FORM_SPEC_ID + " = " + formSpecId
							+ " AND " + ListFilteringCriterias.TYPE + " = "
							+ type + " AND "
							+ ListFilteringCriterias.FIELD_SPEC_ID + " = "
							+ fieldSpecId, null, null, null, null);

			if (cursor.getCount() > 0) {
				listFilteringCriterias = new ArrayList<ListFilteringCriteria>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				ListFilteringCriteria listFilteringCriteria = new ListFilteringCriteria();
				listFilteringCriteria.load(cursor);
				listFilteringCriterias.add(listFilteringCriteria);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return listFilteringCriterias;
	}

	public synchronized void deleteListFilteringCriteriasWithLocalId(
			long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.execSQL("DELETE FROM " + ListFilteringCriterias.TABLE + " WHERE "
				+ ListFilteringCriterias._ID + " = " + localId);
	}

	public boolean hasFilteringCriteria(long formSpecId) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {

			cursor = db.query(ListFilteringCriterias.TABLE,
					new String[] { ListFilteringCriterias._ID },
					ListFilteringCriterias.FORM_SPEC_ID + " = " + formSpecId,
					null, null, null, null);

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

	public boolean isThisFieldrelatedToReferenceFieldExpression(
			long formSpecId, String selector) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {

			cursor = db
					.query(ListFilteringCriterias.TABLE,
							new String[] { ListFilteringCriterias._ID },
							ListFilteringCriterias.FORM_SPEC_ID
									+ " = "
									+ formSpecId
									+ " AND "
									+ ListFilteringCriterias.REFERENCE_FIELD_EXPRESSION_ID
									+ " = '" + selector + "'", null, null,
							null, null);

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

	public boolean isThisFieldhasFilteringCriteria(long formSpecId,
			String selector) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {

			cursor = db
					.query(ListFilteringCriterias.TABLE,
							new String[] { ListFilteringCriterias._ID },
							ListFilteringCriterias.FORM_SPEC_ID
									+ " = "
									+ formSpecId
									+ " AND "
									+ ListFilteringCriterias.REFERENCE_FIELD_EXPRESSION_ID
									+ " = '" + selector + "'", null, null,
							null, null);

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
}

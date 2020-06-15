package in.spoors.effort1.dao;

import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.ViewField;
import in.spoors.effort1.dto.VisibilityCriteria;
import in.spoors.effort1.provider.EffortProvider.VisibilityCriterias;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class VisibilityCriteriaDao {

	public static final String TAG = "VisibilityCriteriaDao";
	private static VisibilityCriteriaDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static VisibilityCriteriaDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new VisibilityCriteriaDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private VisibilityCriteriaDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public synchronized void save(VisibilityCriteria visibilityCriteria) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		if (visibilityCriteria.getLocalId() != null
				&& employeeWithLocalIdExists(visibilityCriteria.getLocalId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting visibility Criteria.");
			}

			ContentValues values = visibilityCriteria.getContentValues(null);

			db.update(
					VisibilityCriterias.TABLE,
					values,
					VisibilityCriterias._ID + " = "
							+ visibilityCriteria.getLocalId(), null);

			return;
		}

		if (visibilityCriteria.getId() != null
				&& employeeWithRemoteIdExists(visibilityCriteria.getId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting visibility Criteria.");
			}

			ContentValues values = visibilityCriteria.getContentValues(null);

			db.update(
					VisibilityCriterias.TABLE,
					values,
					VisibilityCriterias.REMOTE_ID + " = "
							+ visibilityCriteria.getId(), null);

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new employee.");
		}

		ContentValues values = visibilityCriteria.getContentValues(null);
		long insertedId = db.insert(VisibilityCriterias.TABLE, null, values);
		visibilityCriteria.setLocalId(insertedId);
	}

	public boolean employeeWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + VisibilityCriterias._ID
					+ ") AS count FROM " + VisibilityCriterias.TABLE
					+ " WHERE " + VisibilityCriterias._ID + " = " + localId,
					null);

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
			cursor = db.rawQuery("SELECT COUNT(" + VisibilityCriterias._ID
					+ ") AS count FROM " + VisibilityCriterias.TABLE
					+ " WHERE " + VisibilityCriterias.REMOTE_ID + " = "
					+ remoteId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public List<VisibilityCriteria> getVisibilityCriterias() {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<VisibilityCriteria> visibilityCriterias = null;

		try {
			cursor = db.query(VisibilityCriterias.TABLE,
					VisibilityCriterias.ALL_COLUMNS, null, null, null, null,
					null);

			if (cursor.getCount() > 0) {
				visibilityCriterias = new ArrayList<VisibilityCriteria>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				VisibilityCriteria visibilityCriteria = new VisibilityCriteria();
				visibilityCriteria.load(cursor);
				visibilityCriterias.add(visibilityCriteria);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public synchronized void deleteVisibilityCriteriaWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.execSQL("DELETE FROM " + VisibilityCriterias.TABLE + " WHERE "
				+ VisibilityCriterias._ID + " = " + localId);
	}

	public synchronized void deleteVisibilityCriteriaWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.execSQL("DELETE FROM " + VisibilityCriterias.TABLE + " WHERE "
				+ VisibilityCriterias.REMOTE_ID + " = " + remoteId);
	}

	public boolean hasVisibilityCriteria(long formSpecId) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {

			cursor = db.query(VisibilityCriterias.TABLE,
					new String[] { VisibilityCriterias._ID },
					VisibilityCriterias.FORM_SPEC_ID + " = " + formSpecId,
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

	public boolean isItRelatedToTargetExpression(long formSpecId,
			String selector) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {

			cursor = db.query(VisibilityCriterias.TABLE,
					new String[] { VisibilityCriterias._ID },
					VisibilityCriterias.FORM_SPEC_ID + " = " + formSpecId
							+ " AND "
							+ VisibilityCriterias.TARGET_FIELD_EXPRESSION
							+ " = '" + selector + "'", null, null, null, null);

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

	public boolean hasVisibilityCriteriaDependency(ViewField viewField,
			int fieldType) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery(
					"SELECT COUNT(" + VisibilityCriterias._ID
							+ ") AS count FROM " + VisibilityCriterias.TABLE
							+ " WHERE " + VisibilityCriterias.FIELD_SPEC_ID
							+ " = " + viewField.getFieldSpecId() + " AND "
							+ VisibilityCriterias.FORM_SPEC_ID + " = "
							+ viewField.getFormSpecId() + " AND "
							+ VisibilityCriterias.FIELD_TYPE + " = "
							+ fieldType, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	public List<VisibilityCriteria> getVisibilityCriterias(ViewField viewField,
			int fieldType) {

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<VisibilityCriteria> visibilityCriterias = null;

		try {

			cursor = db.query(VisibilityCriterias.TABLE,
					VisibilityCriterias.ALL_COLUMNS,
					VisibilityCriterias.FIELD_SPEC_ID + " = ? AND "
							+ VisibilityCriterias.FORM_SPEC_ID + " = ? AND "
							+ VisibilityCriterias.FIELD_TYPE + " = ?",
					new String[] { "" + viewField.getFieldSpecId(),
							"" + viewField.getFormSpecId(), "" + fieldType },
					null, null, null);

			if (cursor.getCount() > 0) {
				visibilityCriterias = new ArrayList<VisibilityCriteria>(
						cursor.getCount());
			}

			while (cursor.moveToNext()) {
				VisibilityCriteria visibilityCriteria = new VisibilityCriteria();
				visibilityCriteria.load(cursor);
				visibilityCriterias.add(visibilityCriteria);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return visibilityCriterias;
	}
}

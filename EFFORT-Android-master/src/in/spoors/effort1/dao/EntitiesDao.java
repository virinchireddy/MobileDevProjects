package in.spoors.effort1.dao;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.effort1.BuildConfig;
import in.spoors.effort1.DBHelper;
import in.spoors.effort1.Utils;
import in.spoors.effort1.dto.Entity;
import in.spoors.effort1.dto.EntityFilterResultsDto;
import in.spoors.effort1.dto.ListFilteringCriteria;
import in.spoors.effort1.provider.EffortProvider.Entities;
import in.spoors.effort1.provider.EffortProvider.EntitiesView;
import in.spoors.effort1.provider.EffortProvider.EntityFieldSpecs;
import in.spoors.effort1.provider.EffortProvider.EntityFields;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.text.TextUtils;
import android.util.Log;

public class EntitiesDao {

	public static final String TAG = "EntitiesDao";
	private static EntitiesDao instance;
	private Context applicationContext;
	private static EntityFieldSpecsDao entityFieldSpecsDao;
	private static EntityFieldValueSpecsDao entityFieldValueSpecsDao;
	private static CustomersDao customersDao;
	private static EmployeesDao employeesDao;
	private static EntitiesDao entitiesDao;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static EntitiesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new EntitiesDao(applicationContext);
			entityFieldSpecsDao = EntityFieldSpecsDao
					.getInstance(applicationContext);
			entityFieldValueSpecsDao = EntityFieldValueSpecsDao
					.getInstance(applicationContext);
			customersDao = CustomersDao.getInstance(applicationContext);
			employeesDao = EmployeesDao.getInstance(applicationContext);
			entitiesDao = EntitiesDao.getInstance(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private EntitiesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean entityWithLocalIdExists(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Entities._ID
					+ ") AS count FROM " + Entities.TABLE + " WHERE "
					+ Entities._ID + " = " + localId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public boolean entityWithRemoteIdExists(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT COUNT(" + Entities.REMOTE_ID
					+ ") AS count FROM " + Entities.TABLE + " WHERE "
					+ Entities.REMOTE_ID + " = " + remoteId, null);

			cursor.moveToNext();

			return cursor.getLong(0) > 0;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Saves the given entity.
	 * 
	 * Local ID of the form is updated with the inserted ID.
	 * 
	 * @param entity
	 */
	public synchronized void save(Entity entity) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		if (entity.getLocalId() != null
				&& entityWithLocalIdExists(entity.getLocalId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting entity.");
			}

			entity.setLocalModificationTime(new Date());
			entity.setLocalCreationTime(getLocalCreationTimeWithLocalId(entity
					.getLocalId()));

			if (entity.getRemoteCreationTime() == null) {
				entity.setRemoteCreationTime(entity.getLocalCreationTime());
			}

			ContentValues values = entity.getContentValues(null);

			db.update(Entities.TABLE, values,
					Entities._ID + " = " + entity.getLocalId(), null);

			return;
		}

		if (entity.getRemoteId() != null
				&& entityWithRemoteIdExists(entity.getRemoteId())) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Updating the exisiting entity.");
			}

			entity.setLocalModificationTime(new Date());
			entity.setLocalCreationTime(getLocalCreationTimeWithRemoteId(entity
					.getRemoteId()));

			if (entity.getRemoteCreationTime() == null) {
				entity.setRemoteCreationTime(entity.getLocalCreationTime());
			}

			ContentValues values = entity.getContentValues(null);

			db.update(Entities.TABLE, values, Entities.REMOTE_ID + " = "
					+ entity.getRemoteId(), null);

			return;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Saving a new entity.");
		}

		Date now = new Date();
		entity.setLocalCreationTime(now);

		if (entity.getRemoteCreationTime() == null) {
			entity.setRemoteCreationTime(entity.getLocalCreationTime());
		}

		entity.setLocalModificationTime(now);

		ContentValues values = entity.getContentValues(null);
		long insertedId = db.insert(Entities.TABLE, null, values);
		entity.setLocalId(insertedId);
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
			cursor = db.rawQuery("SELECT " + Entities._ID + " FROM "
					+ Entities.TABLE + " WHERE " + Entities.REMOTE_ID + " = "
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
	 * Returns the remote form id of the given local form id.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public Long getRemoteId(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Entities.REMOTE_ID + " FROM "
					+ Entities.TABLE + " WHERE " + Entities._ID + " = "
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
	 * Returns the remote form ids of the given local form ids.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public List<Long> getRemoteIds(String localIds) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Long> ids = null;
		try {
			cursor = db.rawQuery("SELECT " + Entities.REMOTE_ID + " FROM "
					+ Entities.TABLE + " WHERE " + Entities._ID + " IN("
					+ localIds + ")", null);

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

	/**
	 * Returns the local creation time of the given local form id.
	 * 
	 * @param localId
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithLocalId(Long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Entities.LOCAL_CREATION_TIME
					+ " FROM " + Entities.TABLE + " WHERE " + Entities._ID
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
	 * Returns the local creation time of the given remote form id.
	 * 
	 * @param remoteId
	 * @return null if no matching record found.
	 */
	public Date getLocalCreationTimeWithRemoteId(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Entities.LOCAL_CREATION_TIME
					+ " FROM " + Entities.TABLE + " WHERE "
					+ Entities.REMOTE_ID + " = " + remoteId, null);

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

	public synchronized void deleteEntity(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		EntityFieldsDao fieldsDao = EntityFieldsDao
				.getInstance(applicationContext);
		fieldsDao.deleteFields(localId);

		// FormFilesDao formFilesDao = FormFilesDao
		// .getInstance(applicationContext);
		// formFilesDao.deleteFormFiles(localId);
		//
		// LocationsDao locationsDao = LocationsDao
		// .getInstance(applicationContext);
		// locationsDao.deleteFormLocations(localId);
		//
		// now, delete the form
		int affectedRows = db.delete(Entities.TABLE, Entities._ID + " = ?",
				new String[] { "" + localId });

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted entity with local entity id: " + localId
					+ ", affectedRows=" + affectedRows);
		}
	}

	public Entity getEntityWithLocalId(long localId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Entity entity = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Entities.TABLE, Entities.ALL_COLUMNS,
					Entities._ID + " = " + localId, null, null, null, null);

			if (cursor.moveToNext()) {
				entity = new Entity();
				entity.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return entity;
	}

	public Entity getEntityWithRemoteId(long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Entity entity = null;
		Cursor cursor = null;

		try {
			cursor = db.query(Entities.TABLE, Entities.ALL_COLUMNS,
					Entities.REMOTE_ID + " = " + remoteId, null, null, null,
					null);

			if (cursor.moveToNext()) {
				entity = new Entity();
				entity.load(cursor, applicationContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return entity;
	}

	public long getEntitySpecId(long localEntityId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.query(Entities.TABLE,
					new String[] { Entities.ENTITY_SPEC_ID }, Entities._ID
							+ " = " + localEntityId, null, null, null, null);

			cursor.moveToNext();
			return cursor.getLong(0);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * 
	 * @param remoteId
	 * @return remote creation time of the form in SQLite Date Time format.
	 * 
	 */
	public String getRemoteCreationTime(Long remoteId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;

		try {
			cursor = db.rawQuery("SELECT " + Entities.REMOTE_CREATION_TIME
					+ " FROM " + Entities.TABLE + " WHERE "
					+ Entities.REMOTE_ID + " = " + remoteId, null);

			if (cursor.moveToNext()) {
				return cursor.getString(0);
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
	 * Deletes all cached forms along with their fields.
	 * 
	 */
	public synchronized int deleteCachedEntities() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		Cursor cursor = db.query(Entities.TABLE, new String[] { Entities._ID },
				Entities.CACHED + " = 'true'", null, null, null, null);

		while (cursor.moveToNext()) {
			deleteEntity(cursor.getLong(0));
		}

		int affectedRows = cursor.getCount();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Deleted " + affectedRows + " cached entities.");
		}

		cursor.close();
		return affectedRows;
	}

	public static String getDisplayValue(Context applicationContext,
			String localValue, String remoteValue, int type) {
		if (type == FieldSpecs.TYPE_CUSTOMER && !TextUtils.isEmpty(localValue)) {
			CustomersDao customersDao = CustomersDao
					.getInstance(applicationContext);
			return customersDao.getCompanyNameWithLocalId(Long
					.parseLong(localValue));
		}

		if (type == FieldSpecs.TYPE_EMPLOYEE && !TextUtils.isEmpty(localValue)) {
			EmployeesDao employeesDao = EmployeesDao
					.getInstance(applicationContext);
			return employeesDao.getEmployeeNameWithLocalId(Long
					.parseLong(localValue));
		}

		if (type == FieldSpecs.TYPE_ENTITY && !TextUtils.isEmpty(localValue)) {
			EntitiesDao entitiesDao = EntitiesDao
					.getInstance(applicationContext);
			return entitiesDao.getEntityName(Long.parseLong(localValue));
		}

		if (!TextUtils.isEmpty(remoteValue)) {
			switch (type) {
			case FieldSpecs.TYPE_DATE:
				return Utils.getDateFormat(applicationContext).format(
						SQLiteDateTimeUtils.getDate(remoteValue));

			case FieldSpecs.TYPE_TIME:
				String timeStr = remoteValue;
				int hourOfDay = Integer.parseInt(timeStr.substring(0, 2));
				int minute = Integer.parseInt(timeStr.substring(3, 5));
				SimpleDateFormat displayTimeFormat = Utils
						.getTimeFormat(applicationContext);
				return displayTimeFormat.format(Utils
						.getTime(hourOfDay, minute));

			case FieldSpecs.TYPE_YES_OR_NO:
				return Boolean.parseBoolean(remoteValue) ? "Yes" : "No";

			case FieldSpecs.TYPE_SINGLE_SELECT_LIST:
				EntityFieldValueSpecsDao valueSpecsDao = EntityFieldValueSpecsDao
						.getInstance(applicationContext);

				return valueSpecsDao.getValue(Long.parseLong(remoteValue));

			case FieldSpecs.TYPE_IMAGE:
				return "Image";

			case FieldSpecs.TYPE_SIGNATURE:
				return "Signature";
			}
		}

		return remoteValue;
	}

	public String getEntityName(long localEntityId) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		builder.setTables(EntityFields.TABLE + " JOIN "
				+ EntityFieldSpecs.TABLE + " ON " + EntityFields.TABLE + "."
				+ EntityFields.FIELD_SPEC_ID + " = " + EntityFieldSpecs.TABLE
				+ "." + EntityFieldSpecs._ID + " AND "
				+ EntityFieldSpecs.IDENTIFIER + " = 'true'");

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "SELECT * FROM " + builder.getTables() + " WHERE "
					+ EntityFields.LOCAL_ENTITY_ID + " = " + localEntityId);
		}

		String[] columns = {
				EntityFields.TABLE + "." + EntityFields.LOCAL_VALUE,
				EntityFields.TABLE + "." + EntityFields.REMOTE_VALUE,
				EntityFieldSpecs.TYPE };

		Cursor cursor = null;

		String localValue = null;
		String remoteValue = null;
		int type = -1;

		try {
			cursor = builder.query(db, columns, EntityFields.LOCAL_ENTITY_ID
					+ " = " + localEntityId, null, null, null, null);

			if (cursor.moveToNext()) {
				localValue = cursor.getString(0);
				remoteValue = cursor.getString(1);
				type = cursor.getInt(2);

				return getDisplayValue(applicationContext, localValue,
						remoteValue, type);
			} else {
				return "List Item ID " + getRemoteId(localEntityId);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * Get all the remote entity IDs.
	 * 
	 * @return
	 */
	public List<Long> getAllRemoteEntityIds() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<Long> ids = null;

		try {
			cursor = db.query(Entities.TABLE,
					new String[] { Entities.REMOTE_ID }, Entities.REMOTE_ID
							+ " IS NOT NULL", null, null, null,
					Entities.REMOTE_ID);

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

	public synchronized void updateDeletedFlag(List<Long> entityIds,
			boolean deleted) {
		if (entityIds == null || entityIds.size() == 0) {
			return;
		}

		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();
		String query = "UPDATE " + Entities.TABLE + " SET " + Entities.DELETED
				+ " = '" + deleted + "' WHERE " + Entities.REMOTE_ID + " IN ("
				+ TextUtils.join(",", entityIds) + ")";

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Entitiy update statement: " + query);
		}

		db.execSQL(query);
	}

	public String getFilteringCriteriaQuery(
			ArrayList<ListFilteringCriteria> filteringCriterias,
			String searchKeyword, long entitySpecId) {
		String filter = getFilteringQuery(
				(ArrayList<ListFilteringCriteria>) filteringCriterias, "");
		String selection = entitySpecId
				+ (TextUtils.isEmpty(searchKeyword) ? "" : " AND "
						+ EntitiesView.DISPLAY_VALUE + " LIKE '%"
						+ searchKeyword + "%'")
				+ (TextUtils.isEmpty(filter) ? ""
						: (" AND " + EntitiesView.LOCAL_ENTITY_ID + " IN ("
								+ filter + ")"));
		return selection;
	}

	public EntityFilterResultsDto getFilteredLocalEntityIdList(String selection) {

		EntityFilterResultsDto entityFilterResultsDto = new EntityFilterResultsDto();
		Cursor cursor = null;
		List<String> idList = null;
		// String filter = getFilteringQuery(
		// (ArrayList<ListFilteringCriteria>) filteringCriterias, "");
		ContentResolver contentResolver = applicationContext
				.getContentResolver();

		// String selection = entitySpecId
		// + (TextUtils.isEmpty(searchKeyword) ? "" : " AND "
		// + EntitiesView.DISPLAY_VALUE + " LIKE '%"
		// + searchKeyword + "%'")
		// + (TextUtils.isEmpty(filter) ? ""
		// : (" AND " + EntitiesView.LOCAL_ENTITY_ID + " IN ("
		// + filter + ")"));
		try {
			cursor = contentResolver.query(EntitiesView.CONTENT_URI,
					new String[] { EntitiesView.DISPLAY_VALUE,
							EntityFields.LOCAL_ENTITY_ID }, selection, null,
					EntitiesView.DISPLAY_VALUE);

			if (cursor.getCount() > 0) {
				idList = new ArrayList<String>();
			}

			while (cursor.moveToNext()) {
				idList.add(cursor.getString(EntitiesView.LOCAL_ENTITY_ID_INDEX));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			entityFilterResultsDto.setIdList(idList);
			entityFilterResultsDto.setQuery(selection);
		}

		return entityFilterResultsDto;
	}

	public String getFilteringQuery(
			ArrayList<ListFilteringCriteria> filteringCriterias,
			String searchKeyword) {
		StringBuilder fromSB = new StringBuilder();
		StringBuilder selectSB = new StringBuilder();
		final int EQUALS = 0;
		final int NOT_EQUALS = 1;

		if (filteringCriterias != null) {
			ArrayList<String> conditions = new ArrayList<String>();

			for (int i = 0; i < filteringCriterias.size(); ++i) {
				ListFilteringCriteria criteria = filteringCriterias.get(i);
				String alias = "e" + i;

				if (i == 0) {
					fromSB.append(EntityFields.TABLE + " " + alias + " ");
				} else {
					fromSB.append(" JOIN " + EntityFields.TABLE + " " + alias
							+ " ON " + alias + "."
							+ EntityFields.LOCAL_ENTITY_ID + " = " + "e0."
							+ EntityFields.LOCAL_ENTITY_ID + " ");
				}

				StringBuilder whereSB = new StringBuilder();

				int type = entityFieldSpecsDao.getType(criteria
						.getListFieldSpecId());

				Long localCustomerId = null;
				Long employeeId = null;
				Long entityId = null;

				if (type == FieldSpecs.TYPE_CUSTOMER) {
					if (!TextUtils.isEmpty(criteria.getValue())) {
						try {
							localCustomerId = customersDao.getLocalId(Long
									.parseLong(criteria.getValue()));
						} catch (NumberFormatException nfe) {
							// Ignoring
						}
						if (localCustomerId != null) {
							whereSB.append("(");
							whereSB.append(alias + ".");
							whereSB.append(EntityFields.FIELD_SPEC_ID + " = "
									+ criteria.getListFieldSpecId());
							whereSB.append(" AND ");
							whereSB.append(alias + ".");
							whereSB.append(EntityFields.LOCAL_VALUE);

							if (criteria.getCondition() == EQUALS) {
								whereSB.append(" = ");
							} else if (criteria.getCondition() == NOT_EQUALS) {
								whereSB.append(" != ");
							} else {
								if (BuildConfig.DEBUG) {
									Log.w(TAG, "Unknown filtering condition "
											+ criteria.getCondition());
								}
							}

							whereSB.append("'");
							whereSB.append(localCustomerId);
							whereSB.append("')");
						}
					}
				} else if (type == FieldSpecs.TYPE_EMPLOYEE) {
					if (!TextUtils.isEmpty(criteria.getValue())) {
						try {
							employeeId = employeesDao.getLocalId(Long
									.parseLong(criteria.getValue()));
						} catch (NumberFormatException nfe) {
							// Ignoring
						}
						if (employeeId != null) {
							whereSB.append("(");
							whereSB.append(alias + ".");
							whereSB.append(EntityFields.FIELD_SPEC_ID + " = "
									+ criteria.getListFieldSpecId());
							whereSB.append(" AND ");
							whereSB.append(alias + ".");
							whereSB.append(EntityFields.LOCAL_VALUE);

							if (criteria.getCondition() == EQUALS) {
								whereSB.append(" = ");
							} else if (criteria.getCondition() == NOT_EQUALS) {
								whereSB.append(" != ");
							} else {
								if (BuildConfig.DEBUG) {
									Log.w(TAG, "Unknown filtering condition "
											+ criteria.getCondition());
								}
							}

							whereSB.append("'");
							whereSB.append(employeeId);
							whereSB.append("')");
						}
					}
				} else if (type == FieldSpecs.TYPE_ENTITY) {
					if (!TextUtils.isEmpty(criteria.getValue())) {
						try {
							entityId = entitiesDao.getLocalId(Long
									.parseLong(criteria.getValue()));
						} catch (NumberFormatException nfe) {
							// Ignoring
						}
						if (entityId != null) {
							whereSB.append("(");
							whereSB.append(alias + ".");
							whereSB.append(EntityFields.FIELD_SPEC_ID + " = "
									+ criteria.getListFieldSpecId());
							whereSB.append(" AND ");
							whereSB.append(alias + ".");
							whereSB.append(EntityFields.LOCAL_VALUE);

							if (criteria.getCondition() == EQUALS) {
								whereSB.append(" = ");
							} else if (criteria.getCondition() == NOT_EQUALS) {
								whereSB.append(" != ");
							} else {
								if (BuildConfig.DEBUG) {
									Log.w(TAG, "Unknown filtering condition "
											+ criteria.getCondition());
								}
							}

							whereSB.append("'");
							whereSB.append(entityId);
							whereSB.append("')");
						}
					}
				} else {
					whereSB.append("(");
					whereSB.append(alias + ".");
					whereSB.append(EntityFields.FIELD_SPEC_ID + " = "
							+ criteria.getListFieldSpecId());
					whereSB.append(" AND UPPER(");
					whereSB.append(alias + ".");
					whereSB.append(EntityFields.REMOTE_VALUE);
					whereSB.append(")");

					if (criteria.getCondition() == EQUALS) {
						whereSB.append(" = ");
					} else if (criteria.getCondition() == NOT_EQUALS) {
						whereSB.append(" != ");
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "Unknown filtering condition "
									+ criteria.getCondition());
						}
					}

					whereSB.append("UPPER('");

					String value = null;

					if (type == FieldSpecs.TYPE_SINGLE_SELECT_LIST) {
						value = ""
								+ entityFieldValueSpecsDao.getId(
										criteria.getListFieldSpecId(),
										criteria.getValue());
					} else if (type == FieldSpecs.TYPE_YES_OR_NO) {

						// this condition is to satisfy dependency in yes/no
						// picker
						// component
						// keep this untill server side implementation changes
						if (criteria.getValue() != null
								&& criteria.getValue().equalsIgnoreCase("yes")) {
							value = "true";
						} else if (criteria.getValue() != null
								&& criteria.getValue().equalsIgnoreCase("no")) {
							value = "false";
						} else {
							value = criteria.getValue();
						}

					} else {
						value = criteria.getValue();
					}

					whereSB.append(value);
					whereSB.append("'))");
				}

				if (!TextUtils.isEmpty(whereSB.toString())) {
					conditions.add(whereSB.toString());
				}
			}

			selectSB.append("SELECT e0." + EntityFields.LOCAL_ENTITY_ID
					+ " FROM ");
			selectSB.append(fromSB);
			if (!conditions.isEmpty()) {
				selectSB.append(" WHERE ");
				selectSB.append(TextUtils.join(" AND ", conditions));
			}
		}

		String filter = selectSB.toString();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Filtering query: " + filter);
		}

		StringBuilder sb = new StringBuilder();

		if (!TextUtils.isEmpty(searchKeyword)) {
			sb.append(searchKeyword);
		}

		return filter;
	}

}

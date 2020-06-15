package in.spoors.effort1.dao;

import in.spoors.effort1.DBHelper;
import in.spoors.effort1.dto.TypeStateMapping;
import in.spoors.effort1.provider.EffortProvider.TypeStateMappings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author tiru
 */
public class TypeStateMappingsDao {
	public static final String TAG = "TypeStateMappingsDao";

	private static TypeStateMappingsDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static TypeStateMappingsDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new TypeStateMappingsDao(applicationContext);
		}

		return instance;
	}

	private TypeStateMappingsDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public boolean areMappingsDifferent(List<TypeStateMapping> list1,
			List<TypeStateMapping> list2) {
		if (list1 == null && list2 == null) {
			return false;
		} else if (list1 == null || list2 == null) {
			return true;
		}

		int size = list1.size();

		if (size != list2.size()) {
			return true;
		}

		List<TypeStateMapping> sortedList1 = new ArrayList<TypeStateMapping>(
				list1);
		List<TypeStateMapping> sortedList2 = new ArrayList<TypeStateMapping>(
				list2);
		Collections.sort(sortedList1);
		Collections.sort(sortedList2);

		// we'll reach here when both the lists contains the same number of
		// items
		for (int i = 0; i < size; ++i) {
			TypeStateMapping mapping1 = sortedList1.get(i);
			TypeStateMapping mapping2 = sortedList2.get(i);

			if (mapping1.getTypeId() != mapping2.getTypeId()
					|| mapping1.getStateId() != mapping2.getStateId()
					|| mapping1.getDisplayOrder() != mapping2.getDisplayOrder()) {
				return true;
			}
		}

		return false;
	}

	public synchronized void save(TypeStateMapping mapping) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		ContentValues values = mapping.getContentValues(null);
		db.insert(TypeStateMappings.TABLE, null, values);
	}

	public synchronized void deleteAll() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getWritableDatabase();

		db.delete(TypeStateMappings.TABLE, null, null);
	}

	/**
	 * 
	 * @return null if there are no mappings
	 */
	public List<TypeStateMapping> getMappings() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<TypeStateMapping> mappings = null;

		try {
			cursor = db
					.query(TypeStateMappings.TABLE,
							TypeStateMappings.ALL_COLUMNS, null, null, null,
							null, null);

			if (cursor.getCount() > 0) {
				mappings = new ArrayList<TypeStateMapping>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				TypeStateMapping mapping = new TypeStateMapping();
				mapping.load(cursor);
				mappings.add(mapping);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}

		return mappings;
	}

}

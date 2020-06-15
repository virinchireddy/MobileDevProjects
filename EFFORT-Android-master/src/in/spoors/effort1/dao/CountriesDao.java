package in.spoors.effort1.dao;

import in.spoors.effort1.DBHelper;
import in.spoors.effort1.provider.EffortProvider.Countries;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CountriesDao {

	public static final String TAG = "CountriesDao";
	private static CountriesDao instance;
	private Context applicationContext;

	/**
	 * As this is a a long-lived object, please pass application context only
	 * (don't pass activity or service context).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static CountriesDao getInstance(Context applicationContext) {
		if (instance == null) {
			instance = new CountriesDao(applicationContext);
		}

		return instance;
	}

	/**
	 * Use getInstance().
	 * 
	 * @param applicationContext
	 */
	private CountriesDao(Context applicationContext) {
		this.applicationContext = applicationContext;
	}

	public List<String> getCountries() {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		Cursor cursor = null;
		List<String> countries = new ArrayList<String>();

		try {
			cursor = db.query(Countries.TABLE, new String[] { Countries.NAME },
					null, null, null, null, Countries.DISPLAY_ORDER + ", "
							+ Countries.NAME);

			while (cursor.moveToNext()) {
				countries.add(cursor.getString(0));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return countries;
	}

}

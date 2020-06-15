package in.spoors.common;

import in.spoors.effort1.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

/**
 * Static utility methods for dealing with SQLite date time values.
 * 
 * Date time columns should be stored in the the database as TEXT. Don't store
 * them as INTEGERs. Date time values should always be stored in UTC, and must
 * be in ISO (yyyy-MM-dd HH:mm:ss) format.
 * 
 * 
 * @author tiru
 * 
 */
@SuppressLint("SimpleDateFormat")
public class SQLiteDateTimeUtils {

	public static final String TAG = "SQLiteDateTimeUtils";

	/**
	 * NOTE: SimpleDateFormat.format(Date) is not thread-safe. Hence, instead of
	 * re-using the SimpleDateFormat instances, create new instances everytime
	 * they are needed.
	 */
	private static final String SQLITE_DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final String SQLITE_TIME_FORMAT_PATTERN = "HH:mm:ss";
	private static final String SQLITE_DATE_FORMAT_PATTERN = "yyyy-MM-dd";

	/**
	 * Returns the local date time object corresponding to the SQLite date time
	 * string in yyyy-MM-dd HH:mm:ss format.
	 * 
	 * @param sqliteDateTime
	 *            SQLite Date Time value in UTC.
	 * 
	 * @return null if the given string is null/empty.
	 * @throws RuntimeException
	 *             if there is any error in parsing the string.
	 */
	public static Date getLocalTime(String sqliteDateTime) {
		if (TextUtils.isEmpty(sqliteDateTime)) {
			return null;
		}

		Date sqliteDateTimeObject = null;

		try {
			sqliteDateTimeObject = new SimpleDateFormat(
					SQLITE_DATE_TIME_FORMAT_PATTERN).parse(sqliteDateTime);
		} catch (ParseException e) {
			String message = "Failed to parse '" + sqliteDateTime
					+ "' as Date: " + e.toString();
			Log.e(TAG, message);
			e.printStackTrace();
			throw new RuntimeException(message, e);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(sqliteDateTimeObject.getTime()
				+ calendar.getTimeZone().getOffset(
						sqliteDateTimeObject.getTime()));

		return calendar.getTime();
	}

	/**
	 * Returns the UTC date time object corresponding to the SQLite date time
	 * string in yyyy-MM-dd HH:mm:ss format.
	 * 
	 * @param sqliteDateTime
	 *            SQLite Date Time value in UTC.
	 * 
	 * @return null if the given string is null/empty.
	 * @throws RuntimeException
	 *             if there is any error in parsing the string.
	 */
	public static Date getUtcTime(String sqliteDateTime) {
		if (TextUtils.isEmpty(sqliteDateTime)) {
			return null;
		}

		Date sqliteDateTimeObject = null;

		try {
			sqliteDateTimeObject = new SimpleDateFormat(
					SQLITE_DATE_TIME_FORMAT_PATTERN).parse(sqliteDateTime);
		} catch (ParseException e) {
			String message = "Failed to parse '" + sqliteDateTime
					+ "' as Date: " + e.toString();
			Log.e(TAG, message);
			e.printStackTrace();
			throw new RuntimeException(message, e);
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(sqliteDateTimeObject.getTime());

		return calendar.getTime();
	}

	/**
	 * Returns the date object corresponding to the SQLite date string in
	 * yyyy-MM-dd format.
	 * 
	 * @param sqliteDate
	 *            SQLite Date value.
	 * 
	 * @return null if the given string is null/empty.
	 * @throws RuntimeException
	 *             if there is any error in parsing the string.
	 */
	public static Date getDate(String sqliteDate) {
		if (TextUtils.isEmpty(sqliteDate)) {
			return null;
		}

		Date sqliteDateObject = null;

		try {
			sqliteDateObject = new SimpleDateFormat(SQLITE_DATE_FORMAT_PATTERN)
					.parse(sqliteDate);
		} catch (ParseException e) {
			String message = "Failed to parse '" + sqliteDate + "' as Date: "
					+ e.toString();
			Log.e(TAG, message);
			e.printStackTrace();
			throw new RuntimeException(message, e);
		}

		return sqliteDateObject;
	}

	/**
	 * Returns the SQLite Date Time string in UTC format, corresponding to the
	 * localMillis elapsed since epoch. This can be stored as-is in a SQLite
	 * database.
	 * 
	 * @param localMillis
	 * @return
	 */
	public static String getSQLiteDateTimeFromLocalMillis(long localMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(localMillis
				- calendar.getTimeZone().getOffset(localMillis));
		return new SimpleDateFormat(SQLITE_DATE_TIME_FORMAT_PATTERN)
				.format(calendar.getTime());
	}

	/**
	 * Returns the SQLite Time (only time part) string in UTC format,
	 * corresponding to the localMillis elapsed since epoch. This can be stored
	 * as-is in a SQLite database.
	 * 
	 * @param localMillis
	 * @return
	 */
	public static String getSQLiteTimeFromLocalMillis(long localMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(localMillis
				- calendar.getTimeZone().getOffset(localMillis));
		return new SimpleDateFormat(SQLITE_TIME_FORMAT_PATTERN).format(calendar
				.getTime());
	}

	/**
	 * Returns the SQLite Date Time string in UTC format, corresponding to the
	 * utcMillis elapsed since epoch. This can be stored as-is in a SQLite
	 * database.
	 * 
	 * @param localMillis
	 * @return
	 */
	public static String getSQLiteDateTimeFromUtcMillis(long utcMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(utcMillis);
		return new SimpleDateFormat(SQLITE_DATE_TIME_FORMAT_PATTERN)
				.format(calendar.getTime());
	}

	/**
	 * Returns the SQLite Time (only time part) string in UTC format,
	 * corresponding to the utcMillis elapsed since epoch. This can be stored
	 * as-is in a SQLite database.
	 * 
	 * @param localMillis
	 * @return
	 */
	public static String getSQLiteTimeFromUtcMillis(long utcMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(utcMillis);
		return new SimpleDateFormat(SQLITE_TIME_FORMAT_PATTERN).format(calendar
				.getTime());
	}

	/**
	 * Returns the SQLite Date Time string in UTC format, corresponding to the
	 * localTime. This can be stored as-is in a SQLite database.
	 * 
	 * @param localTime
	 * @return null if localTime is null.
	 */
	public static String getSQLiteDateTimeFromLocalTime(Date localTime) {
		if (localTime == null)
			return null;

		return getSQLiteDateTimeFromLocalMillis(localTime.getTime());
	}

	/**
	 * Returns the SQLite Date string, corresponding to the given date. This can
	 * be stored as-is in a SQLite database.
	 * 
	 */
	public static String getSQLiteDate(Date date) {
		if (date == null)
			return null;

		return new SimpleDateFormat(SQLITE_DATE_FORMAT_PATTERN).format(date);
	}

	/**
	 * Returns the SQLite Time (time part only) string in UTC format,
	 * corresponding to the localTime. This can be stored as-is in a SQLite
	 * database.
	 * 
	 * @param localTime
	 * @return null if localTime is null.
	 */
	public static String getSQLiteTimeFromLocalTime(Date localTime) {
		if (localTime == null)
			return null;

		return getSQLiteTimeFromLocalMillis(localTime.getTime());
	}

	/**
	 * Returns the SQLite Date Time string in UTC format, corresponding to the
	 * utcTime. This can be stored as-is in a SQLite database.
	 * 
	 * @param localTime
	 * @return null if localTime is null.
	 */
	public static String getSQLiteDateTimeFromUtcTime(Date utcTime) {
		if (utcTime == null)
			return null;

		return getSQLiteDateTimeFromUtcMillis(utcTime.getTime());
	}

	/**
	 * Returns the SQLite Time (time part only) string in UTC format,
	 * corresponding to the utcTime. This can be stored as-is in a SQLite
	 * database.
	 * 
	 * @param localTime
	 * @return null if localTime is null.
	 */
	public static String getSQLiteTimeFromUtcTime(Date utcTime) {
		if (utcTime == null)
			return null;

		return getSQLiteTimeFromUtcMillis(utcTime.getTime());
	}

	/**
	 * Returns current date time string in UTC format as SQLite date time
	 * functions assume that time is in UTC.
	 * 
	 * @return
	 */
	public static String getCurrentSQLiteDateTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(calendar.getTimeInMillis()
				- calendar.getTimeZone().getOffset(calendar.getTimeInMillis()));
		return new SimpleDateFormat(SQLITE_DATE_TIME_FORMAT_PATTERN)
				.format(calendar.getTime());
	}

	/**
	 * Returns UTC equivalent of midnight tonight.
	 * 
	 * @return
	 */
	public static String getSQLiteDateTimeForBeginningOfToday() {
		return getSQLiteDateTimeFromLocalTime(Utils.getBeginningOfToday());
	}

	/**
	 * Returns UTC equivalent of midnight tonight.
	 * 
	 * @return
	 */
	public static String getSQLiteDateTimeForBeginningOfDayFromTodayBefore(
			int days) {
		return getSQLiteDateTimeFromLocalTime(Utils
				.getBeginningOfDayFromTodayBefore(days));
	}

	/**
	 * Returns current date string.
	 * 
	 * @return
	 */
	public static String getCurrentSQLiteDate() {
		Calendar calendar = Calendar.getInstance();
		return new SimpleDateFormat(SQLITE_DATE_FORMAT_PATTERN).format(calendar
				.getTime());
	}

	/**
	 * Returns current time (time part only) string in UTC format as SQLite date
	 * time functions assume that time is in UTC.
	 * 
	 * @return
	 */
	public static String getCurrentSQLiteTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(calendar.getTimeInMillis()
				- calendar.getTimeZone().getOffset(calendar.getTimeInMillis()));
		return new SimpleDateFormat(SQLITE_TIME_FORMAT_PATTERN).format(calendar
				.getTime());
	}

	/**
	 * Returns the SQLite date time string corresponding to the XSD date time
	 * string in yyyy-MM-dd'T'HH:mm:ss'Z' format.
	 * 
	 * Note that this method doesn't validate whether the given string conforms
	 * to the required format. If an invalid string is passed, you may get
	 * strange results.
	 * 
	 */
	public static String getSQLiteDateTimeFromXsdDateTime(String xsdDateTime) {
		if (TextUtils.isEmpty(xsdDateTime)) {
			return null;
		}

		String sqliteDateTime = xsdDateTime.replace("T", " ");

		if (sqliteDateTime.endsWith("Z")) {
			// trim the ending Z
			sqliteDateTime = sqliteDateTime.substring(0,
					sqliteDateTime.length() - 1);
		}

		return sqliteDateTime;
	}

	/**
	 * Returns the local latest time between update via sms and update via data.
	 * 
	 * @param lastUpdateViaSms
	 * @param lastUpdateViaData
	 * @return
	 */

	public static long getLatestUpdateTimeLocal(String lastUpdateViaSms,
			String lastUpdateViaData) {

		long latestUpdateTime = 0;

		if (!TextUtils.isEmpty(lastUpdateViaData)
				&& !TextUtils.isEmpty(lastUpdateViaSms)) {
			latestUpdateTime = Math.max(
					SQLiteDateTimeUtils.getLocalTime(lastUpdateViaSms)
							.getTime(),
					SQLiteDateTimeUtils.getLocalTime(lastUpdateViaData)
							.getTime());
		} else if (!TextUtils.isEmpty(lastUpdateViaData)
				&& TextUtils.isEmpty(lastUpdateViaSms)) {
			latestUpdateTime = SQLiteDateTimeUtils.getLocalTime(
					lastUpdateViaData).getTime();
		} else if (TextUtils.isEmpty(lastUpdateViaData)
				&& !TextUtils.isEmpty(lastUpdateViaSms)) {
			latestUpdateTime = SQLiteDateTimeUtils.getLocalTime(
					lastUpdateViaSms).getTime();
		}

		return latestUpdateTime;
	}

}

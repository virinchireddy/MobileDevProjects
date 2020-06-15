package in.spoors.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Static utility methods for dealing with XSD date time values.
 * 
 * XSD date time is always returned in UTC (i.e. with Z suffix), and expects XSD
 * date time strings to end with Z suffix.
 * 
 * @author tiru
 * 
 */
@SuppressLint("SimpleDateFormat")
public class XsdDateTimeUtils {

	private static final String TAG = "XsdDateTimeUtils";

	/**
	 * NOTE: SimpleDateFormat.format(Date) is not thread-safe. Hence, instead of
	 * re-using the SimpleDateFormat instances, create new instances everytime
	 * they are needed.
	 */
	private static final String XSD_DATE_TIME_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	private static final String XSD_DATE_FORMAT_PATTERN = "yyyy-MM-dd'Z'";
	private static final String XSD_TIME_FORMAT_PATTERN = "HH:mm:ss'Z'";

	/**
	 * Returns the date object corresponding to the XSD date string in
	 * yyyy-MM-dd'Z' format.
	 * 
	 * @param xsdDate
	 * 
	 * @return null if the given string is null.
	 */
	public static Date getDate(String xsdDate) {
		if (xsdDate == null)
			return null;

		Date date = null;

		try {
			date = new SimpleDateFormat(XSD_DATE_FORMAT_PATTERN).parse(xsdDate);
		} catch (ParseException e) {
			Log.w(TAG,
					"Failed to parse '" + xsdDate + "' as date: "
							+ e.toString());
		}

		return date;
	}

	/**
	 * Returns the local date time object corresponding to the XSD date time
	 * string in yyyy-MM-dd'T'HH:mm:ss'Z' format.
	 * 
	 * @param xsdDateTime
	 * 
	 * @return null if the given string is null.
	 * @throws ParseException
	 */
	public static Date getLocalTime(String xsdDateTime) throws ParseException {
		if (xsdDateTime == null)
			return null;

		Date xsdDateTimeObject = new SimpleDateFormat(
				XSD_DATE_TIME_FORMAT_PATTERN).parse(xsdDateTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(xsdDateTimeObject.getTime()
				+ calendar.getTimeZone().getOffset(xsdDateTimeObject.getTime()));

		return calendar.getTime();
	}

	/**
	 * Returns the local date time object corresponding to the XSD time string
	 * in HH:mm:ss'Z' format.
	 * 
	 * @param xsdTime
	 * 
	 * @return null if the given string is null.
	 * @throws ParseException
	 */
	public static Date getLocalTimeFromXsdTime(String xsdTime)
			throws ParseException {
		if (xsdTime == null)
			return null;

		Date xsdDateTimeObject = new SimpleDateFormat(XSD_TIME_FORMAT_PATTERN)
				.parse(xsdTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(xsdDateTimeObject.getTime()
				+ calendar.getTimeZone().getOffset(xsdDateTimeObject.getTime()));

		return calendar.getTime();
	}

	/**
	 * Returns the UTC date time object corresponding to the XSD date time
	 * string in yyyy-MM-dd'T'HH:mm:ss'Z' format.
	 * 
	 * @param xsdDateTime
	 * 
	 * @return null if the given string is null.
	 * @throws ParseException
	 */
	public static Date getUtcTime(String xsdDateTime) throws ParseException {
		if (xsdDateTime == null)
			return null;

		Date xsdDateTimeObject = new SimpleDateFormat(
				XSD_DATE_TIME_FORMAT_PATTERN).parse(xsdDateTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(xsdDateTimeObject.getTime());

		return calendar.getTime();
	}

	/**
	 * Returns the XSD Date Time string, corresponding to the localMillis
	 * elapsed since epoch.
	 * 
	 * @param localMillis
	 * @return
	 */
	public static String getXsdDateTimeFromLocalMillis(long localMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(localMillis
				- calendar.getTimeZone().getOffset(localMillis));
		return new SimpleDateFormat(XSD_DATE_TIME_FORMAT_PATTERN)
				.format(calendar.getTime());
	}

	/**
	 * Returns the XSD Date Time string, corresponding to the utcMillis elapsed
	 * since epoch.
	 * 
	 * @param localMillis
	 * @return
	 */
	public static String getXsdDateTimeFromUtcMillis(long utcMillis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(utcMillis);
		return new SimpleDateFormat(XSD_DATE_TIME_FORMAT_PATTERN)
				.format(calendar.getTime());
	}

	/**
	 * Returns the XSD Date Time string, corresponding to the localTime.
	 * 
	 * @param localTime
	 * @return null if localTime is null.
	 */
	public static String getXsdDateTimeFromLocalTime(Date localTime) {
		if (localTime == null)
			return null;

		return getXsdDateTimeFromLocalMillis(localTime.getTime());
	}

	/**
	 * Returns the XSD Date Time string, corresponding to the utcTime.
	 * 
	 * @param localTime
	 * @return null if localTime is null.
	 */
	public static String getXsdDateTimeFromUtcTime(Date utcTime) {
		if (utcTime == null)
			return null;

		return getXsdDateTimeFromUtcMillis(utcTime.getTime());
	}

	/**
	 * Returns current date time string as XSD date time string.
	 * 
	 * @return
	 */
	public static String getCurrentXsdDateTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(calendar.getTimeInMillis()
				- calendar.getTimeZone().getOffset(calendar.getTimeInMillis()));
		return new SimpleDateFormat(XSD_DATE_TIME_FORMAT_PATTERN)
				.format(calendar.getTime());
	}

	/**
	 * Returns the XSD date time string corresponding to the SQLite date time
	 * string in yyyy-MM-dd HH:mm:ss format in UTC.
	 * 
	 * Note that this method doesn't validate whether the given string conforms
	 * to the required format. If an invalid string is passed, you may get
	 * strange results.
	 * 
	 */
	public static String getXsdDateTimeFromSQLiteDateTime(String sqliteDateTime) {
		if (sqliteDateTime == null)
			return null;

		String xsdDateTime = sqliteDateTime.replace(" ", "T");

		if (!xsdDateTime.endsWith("Z")) {
			// append Z
			xsdDateTime = xsdDateTime + "Z";
		}

		return xsdDateTime;
	}

	/**
	 * Returns the utc date time object corresponding to the XSD time string in
	 * HH:mm:ss'Z' format.
	 * 
	 * @param xsdTime
	 * 
	 * @return null if the given string is null.
	 * @throws ParseException
	 */
	public static Date getUtcTimeFromXsdTime(String xsdTime)
			throws ParseException {
		if (xsdTime == null)
			return null;

		Date xsdDateTimeObject = new SimpleDateFormat(XSD_TIME_FORMAT_PATTERN)
				.parse(xsdTime);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(xsdDateTimeObject.getTime());

		return calendar.getTime();
	}
}

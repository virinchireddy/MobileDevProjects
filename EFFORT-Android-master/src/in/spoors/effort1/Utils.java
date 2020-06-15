package in.spoors.effort1;

import in.spoors.common.SQLiteDateTimeUtils;
import in.spoors.common.XsdDateTimeUtils;
import in.spoors.effort1.dao.ArticlesDao;
import in.spoors.effort1.dao.CustomersDao;
import in.spoors.effort1.dao.EmployeesDao;
import in.spoors.effort1.dao.EntitiesDao;
import in.spoors.effort1.dao.FieldsDao;
import in.spoors.effort1.dao.FormFilesDao;
import in.spoors.effort1.dao.FormSpecsDao;
import in.spoors.effort1.dao.HolidaysDao;
import in.spoors.effort1.dao.JobsDao;
import in.spoors.effort1.dao.LeavesDao;
import in.spoors.effort1.dao.LocationsDao;
import in.spoors.effort1.dao.NotesDao;
import in.spoors.effort1.dao.SectionFieldsDao;
import in.spoors.effort1.dao.SectionFilesDao;
import in.spoors.effort1.dao.SettingsDao;
import in.spoors.effort1.dao.SimCardChangeSmsDao;
import in.spoors.effort1.dao.SpecialWorkingHoursDao;
import in.spoors.effort1.dao.WorkingHoursDao;
import in.spoors.effort1.dto.ActivitySpec;
import in.spoors.effort1.dto.Article;
import in.spoors.effort1.dto.AssignedRoute;
import in.spoors.effort1.dto.CompletedActivity;
import in.spoors.effort1.dto.Customer;
import in.spoors.effort1.dto.CustomerStatusDto;
import in.spoors.effort1.dto.Employee;
import in.spoors.effort1.dto.Entity;
import in.spoors.effort1.dto.EntityField;
import in.spoors.effort1.dto.Field;
import in.spoors.effort1.dto.Form;
import in.spoors.effort1.dto.Job;
import in.spoors.effort1.dto.JobHistory;
import in.spoors.effort1.dto.JobStageStatus;
import in.spoors.effort1.dto.LocationDto;
import in.spoors.effort1.dto.Message;
import in.spoors.effort1.dto.NamedLocation;
import in.spoors.effort1.dto.Note;
import in.spoors.effort1.dto.SectionField;
import in.spoors.effort1.dto.SimChangeMessage;
import in.spoors.effort1.dto.ViewField;
import in.spoors.effort1.dto.VisibilityCriteria;
import in.spoors.effort1.dto.WorkFlowFormSpecMapping;
import in.spoors.effort1.dto.WorkFlowHistory;
import in.spoors.effort1.dto.WorkFlowSpec;
import in.spoors.effort1.dto.WorkFlowStage;
import in.spoors.effort1.dto.WorkFlowStatusDto;
import in.spoors.effort1.dto.WorkingHour;
import in.spoors.effort1.provider.EffortProvider;
import in.spoors.effort1.provider.EffortProvider.FieldSpecs;
import in.spoors.effort1.provider.EffortProvider.FormFiles;
import in.spoors.effort1.provider.EffortProvider.Locations;
import in.spoors.effort1.provider.EffortProvider.SectionFiles;
import in.spoors.effort1.provider.EffortProvider.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.google.android.gms.location.LocationClient;

@SuppressLint("SimpleDateFormat")
public class Utils {

	public static final String TAG = "Utils";
	public static final String CALENDARS_CONTENT_URI = "content://com.android.calendar/calendars";
	public static final String EVENTS_CONTENT_URI = "content://com.android.calendar/events";
	public static final String REMINDERS_CONTENT_URI = "content://com.android.calendar/reminders";
	public static final String DATE_TIME_STAMP_PATTERN = "yyyy-MM-dd_HHmmss";
	public static final String SEND_SMS = "in.spoors.effort.sms_sent";
	public static final String SEND_SMS_URI = "content://in.spoors.effort.sms_sent";
	public static final String CLIENT_PLATFORM_ID_PARAM = "clientPlatform";
	public static final String CLIENT_PLATFORM_ID = "1";
	public static final String APP_VERSION_NAME_PARAM = "clientVersion";
	public static final String DISPLAY_WIDTH_PARAM = "dWidth";
	public static final String DISPLAY_HEIGHT_PARAM = "dHeight";
	public static final String OS_VERSION_NAME_PARAM = "osVersion";
	public static final String PROVISIONING_CODE_PARAM = "code";
	public static final String VERSION_CODE = "versionCode";
	public static final String API_LEVEL = "apiLevel";

	public static boolean gpsReceiverOn;
	public static boolean networkReceiverOn;
	public static boolean fusedReceiverOn;
	public static final int THREE_MINUTES_IN_MILLIS = 180000;
	public static final int FIVE_MINUTES_IN_MILLIS = 300000;
	// Constants used as notification IDs
	public static final int NEW_JOBS_NOTIFICATION = 0;
	public static final int LATE_TO_JOB_NOTIFICATION = 1;
	public static final int START_JOB_NOTIFICATION = 2;
	public static final int END_JOB_NOTIFICATION = 3;
	public static final int START_WORKING_DAY_NOTIFICATION = 4;
	public static final int END_WORKING_DAY_NOTIFICATION = 5;
	public static final int MIDDLE_WORKING_DAY_NOTIFICATION = 6;
	public static final int WORKFLOWS_NOTIFICATION = 7;
	public static final int ARTICLES_NOTIFICATION = 8;

	/**
	 * Returns the ID of the first calendar TODO: Change this to return the
	 * primary calendar or user's preferred calendar.
	 * 
	 * @param resolver
	 * @return null if there are no calendars configured on the device
	 */
	public static Integer getDefaultCalendarId(ContentResolver resolver) {
		Integer calendarId = null;
		Uri uri = Uri.parse(CALENDARS_CONTENT_URI);
		String[] projection = { "_id" };

		Cursor cursor = resolver.query(uri, projection, null, null, null);

		if (cursor.moveToNext()) {
			calendarId = cursor.getInt(0);
		} else {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "No calendars found on the device.");
			}
		}

		cursor.close();

		return calendarId;
	}

	/**
	 * Inserts or updates the calendar event for the given job.
	 * 
	 * @param resolver
	 * @param job
	 * @param reminderMinutes
	 */
	public static void insertOrUpdateCalendarEvent(ContentResolver resolver,
			Job job, int reminderMinutes) {
		ContentValues values = new ContentValues();

		Integer calendarId = getDefaultCalendarId(resolver);
		if (calendarId == null) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "No default calendar found on the device.");
			}

			return;
		}

		values.put("calendar_id", calendarId);

		values.put("title", job.getTitle());

		if (!TextUtils.isEmpty(job.getDescription())) {
			values.put("description", job.getDescription());
		}

		if (job.getStartTime() != null) {
			values.put("dtstart", job.getStartTime().getTime());
		}

		if (job.getEndTime() != null) {
			values.put("dtend", job.getEndTime().getTime());
		}

		values.put("eventTimezone",
				android.text.format.Time.getCurrentTimezone());

		if (job.getAndroidEventId() == null) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Inserting calendar event.");
			}

			Uri eventsUri = Uri.parse(EVENTS_CONTENT_URI);
			Uri insertedEventUri = resolver.insert(eventsUri, values);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "URI of the inserted calendar event: "
						+ insertedEventUri.toString());
			}

			int androidEventId = Integer.parseInt(insertedEventUri
					.getLastPathSegment());

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Parsed android event id: " + androidEventId);
			}

			job.setAndroidEventId(androidEventId);
		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Updating calendar event.");
			}

			resolver.update(ContentUris.withAppendedId(
					Uri.parse(EVENTS_CONTENT_URI), job.getAndroidEventId()),
					values, null, null);
		}

		createReminder(resolver, job.getAndroidEventId(), reminderMinutes);
	}

	public static void createReminder(ContentResolver resolver,
			int androidEventId, int minutes) {
		try {
			int affectedRows = resolver.delete(
					Uri.parse(REMINDERS_CONTENT_URI), "event_id = ?",
					new String[] { "" + androidEventId });
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Deleted " + affectedRows + " reminders.");
			}

			ContentValues values = new ContentValues();

			values.put("event_id", androidEventId);
			values.put("minutes", minutes);
			values.put("method", 0);
			Uri insertedUri = resolver.insert(Uri.parse(REMINDERS_CONTENT_URI),
					values);

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Inserted reminder URI: " + insertedUri);
			}

			int androidReminderId = Integer.parseInt(insertedUri
					.getLastPathSegment());

			if (BuildConfig.DEBUG) {
				Log.d(TAG, "Parsed android reminder id: " + androidReminderId);
			}
		} catch (SQLiteException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG,
						"Couldn't create reminder in device's native calendar.",
						e);
			}

			// TODO: Though this is very rare. Inform the user about this.
		}
	}

	public static void deleteCalendarEvent(ContentResolver resolver, int eventId) {
		int affectedRows = resolver.delete(Uri.parse(EVENTS_CONTENT_URI),
				"_id = ?", new String[] { "" + eventId });
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Deleted " + affectedRows + " events.");
		}
	}

	/**
	 * Returns the date format pattern.
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static String getDateFormatPattern(Context applicationContext) {
		String format = android.provider.Settings.System.getString(
				applicationContext.getContentResolver(),
				android.provider.Settings.System.DATE_FORMAT);

		if (TextUtils.isEmpty(format)) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG,
						"No Date Format setting found on the device. Using default medium date format.");
			}

			SimpleDateFormat sdf = (SimpleDateFormat) android.text.format.DateFormat
					.getMediumDateFormat(applicationContext);
			format = sdf.toPattern();
		}

		return format;
	}

	/**
	 * Returns the date format object (SimpleDateFormat).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SimpleDateFormat getDateFormat(Context applicationContext) {
		return new SimpleDateFormat(getDateFormatPattern(applicationContext));
	}

	/**
	 * Returns the user's date format setting, after trimming the year part.
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static String getDateFormatPatternWithOutYear(
			Context applicationContext) {
		String format = getDateFormatPattern(applicationContext);

		format = format.replace("y", "").trim();

		if (format.endsWith("/") || format.endsWith(",")
				|| format.endsWith("-")) {
			// trim the last character
			format = format.substring(0, format.length() - 1);
		}

		if (format.startsWith("/") || format.startsWith(",")
				|| format.startsWith("-")) {
			// trim the first character
			format = format.substring(1, format.length());
		}

		return format.trim();
	}

	/**
	 * Returns the date format object (SimpleDateFormat) that does not include
	 * the year part.
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SimpleDateFormat getDateFormatWithOutYear(
			Context applicationContext) {
		return new SimpleDateFormat(
				getDateFormatPatternWithOutYear(applicationContext));
	}

	/**
	 * Returns the time format pattern.
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static String getTimeFormatPattern(Context applicationContext) {
		SimpleDateFormat sdf = (SimpleDateFormat) android.text.format.DateFormat
				.getTimeFormat(applicationContext);

		return sdf.toPattern();
	}

	/**
	 * Returns the time format object (SimpleDateFormat).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SimpleDateFormat getTimeFormat(Context applicationContext) {
		return (SimpleDateFormat) android.text.format.DateFormat
				.getTimeFormat(applicationContext);
	}

	/**
	 * Returns the date time format pattern.
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static String getDateTimeFormatPattern(Context applicationContext) {
		return getDateFormatPattern(applicationContext) + " "
				+ getTimeFormatPattern(applicationContext);
	}

	/**
	 * Returns the date time format object (SimpleDateFormat).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SimpleDateFormat getDateTimeFormat(Context applicationContext) {
		return new SimpleDateFormat(
				getDateTimeFormatPattern(applicationContext));
	}

	/**
	 * Returns the date time format pattern.
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static String getDateTimeFormatPatternWithOutYear(
			Context applicationContext) {
		return getDateFormatPatternWithOutYear(applicationContext) + " "
				+ getTimeFormatPattern(applicationContext);
	}

	/**
	 * Returns the date time format object (SimpleDateFormat).
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static SimpleDateFormat getDateTimeFormatWithOutYear(
			Context applicationContext) {
		return new SimpleDateFormat(
				getDateTimeFormatPatternWithOutYear(applicationContext));
	}

	/**
	 * Checks whether two dates are same (i.e. when the time part is ignored).
	 * 
	 * @param day1
	 * @param day2
	 */
	public static boolean isSameDay(Calendar day1, Calendar day2) {
		return day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR)
				&& day1.get(Calendar.MONTH) == day2.get(Calendar.MONTH)
				&& day1.get(Calendar.DATE) == day2.get(Calendar.DATE);
	}

	/**
	 * Checks whether two dates are same (i.e. when the time part is ignored).
	 * 
	 * @param day1
	 * @param day2
	 */
	public static boolean isSameDay(Date day1, Date day2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(day1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(day2);

		return isSameDay(cal1, cal2);
	}

	/**
	 * Checks whether two dates are same (i.e. when the time part is ignored).
	 * 
	 * @param day1
	 *            the time as the number of milliseconds since Jan. 1, 1970
	 * @param day2
	 *            the time as the number of milliseconds since Jan. 1, 1970
	 */
	public static boolean isSameDay(long day1, long day2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(day1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTimeInMillis(day2);

		return isSameDay(cal1, cal2);
	}

	/**
	 * Checks whether two dates are same (i.e. when the time part is ignored).
	 * 
	 * @param sqliteDateTimeString1
	 * @param sqliteDateTimeString2
	 * @return
	 * @throws RuntimeException
	 *             if there is any error in parsing the string.
	 */
	public static boolean isSameDay(String sqliteDateTimeString1,
			String sqliteDateTimeString2) {
		Date day1 = SQLiteDateTimeUtils.getLocalTime(sqliteDateTimeString1);
		Date day2 = SQLiteDateTimeUtils.getLocalTime(sqliteDateTimeString2);

		return isSameDay(day1, day2);
	}

	/**
	 * Validates whether the SD card is present, mounted, and is ready to use.
	 * 
	 * If it is not usable, display toast message to the user
	 * 
	 * @param context
	 * @param showToast
	 * @return true if it is ready to use; false, otherwise.
	 */
	public static boolean isSDCardValid(Context context, boolean showToast) {
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}

		if (Environment.MEDIA_REMOVED.equals(state)) {
			if (showToast) {
				Toast.makeText(
						context,
						context.getText(R.string.sdcard_not_present_toast_message),
						Toast.LENGTH_LONG).show();
			}

			return false;
		}

		if (Environment.MEDIA_UNMOUNTED.equals(state)) {
			if (showToast) {
				Toast.makeText(
						context,
						context.getText(R.string.sdcard_not_mounted_toast_message),
						Toast.LENGTH_LONG).show();
			}

			return false;
		}

		if (showToast) {
			Toast.makeText(
					context,
					"The SD card in the device is in '" + state
							+ "' state, and cannot be used.", Toast.LENGTH_LONG)
					.show();
		}

		return false;
	}

	public static String getDateTimeStamp() {
		return new SimpleDateFormat(DATE_TIME_STAMP_PATTERN).format(new Date());
	}

	public static Date getDefaultJobStartTime() {
		Calendar now = Calendar.getInstance();
		Calendar result = Calendar.getInstance();

		if (now.get(Calendar.MINUTE) % 15 != 0) {
			now.add(Calendar.MINUTE, 15 - now.get(Calendar.MINUTE) % 15);
		}

		// clear the seconds part
		result.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
				now.get(Calendar.DATE), now.get(Calendar.HOUR_OF_DAY),
				now.get(Calendar.MINUTE), 0);

		return result.getTime();
	}

	public static Date getDefaultJobEndTime() {
		Calendar result = Calendar.getInstance();
		result.setTime(getDefaultJobStartTime());
		result.add(Calendar.HOUR_OF_DAY, 1);

		return result.getTime();
	}

	/**
	 * Checks whether two Dates are equal.
	 * 
	 * @param value1
	 * @param value2
	 * @return true if both value1 and value2 are null. false if one of them is
	 *         null and the other is non-null. when both are non-null, true if
	 *         they are holding the same time.
	 */
	public static boolean datesEqual(Date value1, Date value2) {
		if (value1 == null && value2 == null) {
			return true;
		}

		if (value1 == null || value2 == null) {
			return false;
		}

		if (value1 != null && value2 != null) {
			return value1.getTime() == value2.getTime();
		}

		return false;
	}

	/**
	 * Checks whether two Booleans are equal.
	 * 
	 * @param value1
	 * @param value2
	 * @return true if both value1 and value1 are null. false if one of them is
	 *         null and the other is non-null. when both are non-null, true if
	 *         they are holding the same value.
	 */
	public static boolean booleansEqual(Boolean value1, Boolean value2) {
		if (value1 == null && value2 == null) {
			return true;
		}

		if (value1 == null || value2 == null) {
			return false;

		}
		if (value1 != null && value2 != null) {
			return value1.booleanValue() == value2.booleanValue();
		}

		return false;
	}

	/**
	 * Checks whether two Integers are equal.
	 * 
	 * @param value1
	 * @param value2
	 * @return true if both value1 and value1 are null. false if one of them is
	 *         null and the other is non-null. when both are non-null, true if
	 *         they are holding the same value.
	 */
	public static boolean integersEqual(Integer value1, Integer value2) {
		if (value1 == null && value2 == null) {
			return true;
		}

		if (value1 == null || value2 == null) {
			return false;
		}

		if (value1 != null && value2 != null) {
			return value1.intValue() == value2.intValue();
		}

		return false;
	}

	/**
	 * Checks whether two Longs are equal.
	 * 
	 * @param value1
	 * @param value2
	 * @return true if both value1 and value1 are null. false if one of them is
	 *         null and the other is non-null. when both are non-null, true if
	 *         they are holding the same value.
	 */
	public static boolean longsEqual(Long value1, Long value2) {
		if (value1 == null && value2 == null) {
			return true;
		}

		if (value1 == null || value2 == null) {
			return false;
		}

		if (value1 != null && value2 != null) {
			return value1.longValue() == value2.longValue();
		}

		return false;
	}

	/**
	 * Checks whether two Doubles are equal.
	 * 
	 * @param value1
	 * @param value2
	 * @return true if both int1 and int2 are null. false if one of them is null
	 *         and the other is non-null. when both are non-null, true if they
	 *         are holding the same value.
	 */
	public static boolean doublesEqual(Double value1, Double value2) {
		if (value1 == null && value2 == null) {
			return true;
		}

		if (value1 == null || value2 == null) {
			return false;
		}

		if (value1 != null && value2 != null) {
			return value1.doubleValue() == value2.doubleValue();
		}

		return false;
	}

	/**
	 * Checks whether two Hashmaps are equal.
	 * 
	 * @param hashmap1
	 * @param hashmap2
	 * @return true if both Hashmaps and Hashmaps are null. false if one of them
	 *         is null and the other is non-null. when both are non-null, true
	 *         if they are holding the same value.
	 */
	public static boolean hashMapsEqual(HashMap<String, String> hashmap1,
			HashMap<String, String> hashmap2) {
		if (hashmap1 == null && hashmap2 == null) {
			return true;
		}

		if (hashmap1 == null || hashmap2 == null) {
			return false;
		}

		if (hashmap1 != null && hashmap2 != null) {
			if (hashmap1.size() == hashmap2.size()) {
				for (Entry<String, String> entry : hashmap1.entrySet()) {
					// Check if the current value is a key in the 2nd map
					if (hashmap2.containsKey(entry.getKey())) {
						// check is it have the same value associated with that
						// key
						if (!TextUtils.equals(hashmap1.get(entry.getKey()),
								hashmap2.get(entry.getKey()))) {
							return false;
						}
					} else {
						return false;
					}
				}
			} else {
				return false;
			}
		}

		return true;
	}

	/**
	 * Compare only the date parts of the given dates (ignoring time)
	 * 
	 * @return date1.compareTo(date2) (i.e. after trimming down the time part)
	 * 
	 */
	public static int compareDateParts(Date date1, Date date2) {
		Calendar cal = Calendar.getInstance();

		cal.setTime(date1);
		Calendar cal1 = Calendar.getInstance();
		cal1.clear();
		cal1.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));

		cal.setTime(date2);
		Calendar cal2 = Calendar.getInstance();
		cal2.clear();
		cal2.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH));

		return cal1.getTime().compareTo(cal2.getTime());
	}

	public static boolean isConnected(Context applicationContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) applicationContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}

	public static boolean isConnectedToWifi(Context applicationContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) applicationContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		return networkInfo != null && networkInfo.isConnectedOrConnecting()
				&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}

	/**
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static String getVersionName(Context applicationContext) {
		String appVersionName = "";

		try {
			appVersionName = applicationContext.getPackageManager()
					.getPackageInfo(applicationContext.getPackageName(), 0).versionName;

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "App Version Name: " + appVersionName);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG,
					"Fatal error: unable to get version information: "
							+ e.toString());
		}

		return appVersionName;
	}

	/**
	 * Appends common query parameters to the given builder.
	 * 
	 * @param applicationContext
	 *            Used for reading resource strings.
	 * @param builder
	 * @return The builder, which can be used for further method chaining.
	 */

	public static Builder appendCommonQueryParameters(
			Context applicationContext, Builder builder) {
		SettingsDao settingsDao = SettingsDao.getInstance(applicationContext);
		String code = settingsDao.getString("code");

		String appVersionName = getVersionName(applicationContext);
		int versionCode = getVersionCode(applicationContext);
		String apiLevel = applicationContext.getString(R.string.app_level);
		String osVersion = Build.VERSION.RELEASE;
		DisplayMetrics displayMetrics = applicationContext.getResources()
				.getDisplayMetrics();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Client Platform ID: " + CLIENT_PLATFORM_ID);
			Log.i(TAG, "OS Version: " + osVersion);
			Log.i(TAG, "Version code: " + versionCode);
			Log.i(TAG, "Width in Pixels: " + displayMetrics.widthPixels);
			Log.i(TAG, "Height in Pixels: " + displayMetrics.heightPixels);
		}

		builder.appendQueryParameter(PROVISIONING_CODE_PARAM, code)
				.appendQueryParameter(CLIENT_PLATFORM_ID_PARAM,
						CLIENT_PLATFORM_ID)
				.appendQueryParameter(OS_VERSION_NAME_PARAM, osVersion)
				.appendQueryParameter(APP_VERSION_NAME_PARAM, appVersionName)
				.appendQueryParameter(VERSION_CODE, versionCode + "")
				.appendQueryParameter(API_LEVEL, apiLevel)
				.appendQueryParameter(DISPLAY_WIDTH_PARAM,
						"" + displayMetrics.widthPixels)
				.appendQueryParameter(DISPLAY_HEIGHT_PARAM,
						"" + displayMetrics.heightPixels).build();

		String regid = settingsDao.getString("gcm_registration_id");

		if (!TextUtils.isEmpty(regid)) {
			builder.appendQueryParameter("pushId", regid);
		}

		return builder;
	}

	public static String getEffortPath() {
		return Environment.getExternalStorageDirectory() + "/EFFORT";
	}

	public static String getNewPathForMediaType(long mediaId, String contentType) {
		return Environment.getExternalStorageDirectory() + "/EFFORT/" + mediaId
				+ "_" + getDateTimeStamp() + "."
				+ contentType.substring(contentType.lastIndexOf("/") + 1);
	}

	public static String getNewPathForMediaTypeForKB(long mediaId,
			String contentType, Context applicationContext, boolean isSecured) {
		if (isSecured) {
			/*
			 * String path = DBHelper.getInstance(applicationContext)
			 * .getReadableDatabase().getPath(); int indexOf =
			 * path.indexOf(applicationContext .getString(R.string.db_name));
			 * String substring = path.substring(0, indexOf); Log.i(TAG,
			 * "substring value is: " + substring); return substring + mediaId +
			 * "_" + getDateTimeStamp() + "." +
			 * contentType.substring(contentType.lastIndexOf("/") + 1);
			 */
			return Environment.getExternalStorageDirectory() + "/EFFORT/KB/"
					+ mediaId + "_" + getDateTimeStamp();
		} else {
			return Environment.getExternalStorageDirectory() + "/EFFORT/KB/"
					+ mediaId + "_" + getDateTimeStamp() + "."
					+ contentType.substring(contentType.lastIndexOf("/") + 1);
		}

		// return Environment.getExternalStorageDirectory() + "/EFFORT/" +
		// mediaId
		// + "_" + getDateTimeStamp() + "."
		// + contentType.substring(contentType.lastIndexOf("/") + 1);
	}

	public static boolean isServiceRunning(Context applicationContext,
			String serviceClassName) {
		final ActivityManager activityManager = (ActivityManager) applicationContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		final List<RunningServiceInfo> services = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo runningServiceInfo : services) {
			if (runningServiceInfo.service.getClassName().equals(
					serviceClassName)
					&& runningServiceInfo.started) {
				return true;
			}
		}

		return false;
	}

	public static boolean isSyncInProgress(Context applicationContext) {
		return isServiceRunning(applicationContext, SyncService.class.getName());
	}

	public static void sync(Context applicationContext) {
		startBftsIfRequired(applicationContext);

		if (!isSyncInProgress(applicationContext)) {
			WakefulIntentService.sendWakefulWork(applicationContext,
					SyncService.class);
		}
	}

	public static void manualSync(final Context applicationContext) {
		startBftsIfRequired(applicationContext);

		if (!isSyncInProgress(applicationContext)) {
			Intent syncIntent = new Intent(applicationContext,
					SyncService.class);
			syncIntent.putExtra("manualSync", true);
			WakefulIntentService
					.sendWakefulWork(applicationContext, syncIntent);
		}

		if (SettingsDao.getInstance(applicationContext).getBoolean(
				Settings.KEY_PREFETCH_NEARBY_CUSTOMERS, false)) {
			Intent intent = new Intent(applicationContext,
					LocationCaptureService.class);
			intent.putExtra(EffortProvider.Locations.PURPOSE,
					EffortProvider.Locations.PURPOSE_NEARBY_CUSTOMERS);
			WakefulIntentService.sendWakefulWork(applicationContext, intent);
		}
	}

	/**
	 * Null-safe way to put a Boolean value into the given ContentValues map.
	 * 
	 * Note that Boolean values in this app are store as strings (true/false) in
	 * the database.
	 * 
	 * If value is null, calls cv.putNull().
	 * 
	 * @param cv
	 * @param key
	 * @param value
	 */
	public static void putNullOrValue(ContentValues cv, String key,
			Boolean value) {
		if (value == null) {
			cv.putNull(key);
		} else {
			cv.put(key, value.toString());
		}
	}

	/**
	 * Null-safe way to put an Integer value into the given ContentValues map.
	 * 
	 * If value is null, calls cv.putNull().
	 * 
	 * @param cv
	 * @param key
	 * @param value
	 */
	public static void putNullOrValue(ContentValues cv, String key,
			Integer value) {
		if (value == null) {
			cv.putNull(key);
		} else {
			cv.put(key, value);
		}
	}

	/**
	 * Null-safe way to put a Long value into the given ContentValues map.
	 * 
	 * If value is null, calls cv.putNull().
	 * 
	 * @param cv
	 * @param key
	 * @param value
	 */
	public static void putNullOrValue(ContentValues cv, String key, Long value) {
		if (value == null) {
			cv.putNull(key);
		} else {
			cv.put(key, value);
		}
	}

	/**
	 * Null-safe way to put a String value into the given ContentValues map.
	 * 
	 * If value is null, calls cv.putNull().
	 * 
	 * @param cv
	 * @param key
	 * @param value
	 */
	public static void putNullOrValue(ContentValues cv, String key, String value) {
		if (value == null) {
			cv.putNull(key);
		} else {
			cv.put(key, value);
		}
	}

	/**
	 * Null-safe way to put a Date value as a SQLite date time string into the
	 * given ContentValues map.
	 * 
	 * If value is null, calls cv.putNull().
	 * 
	 * @param cv
	 * @param key
	 * @param value
	 */
	public static void putNullOrValue(ContentValues cv, String key, Date value) {
		if (value == null) {
			cv.putNull(key);
		} else {
			cv.put(key,
					SQLiteDateTimeUtils.getSQLiteDateTimeFromLocalTime(value));
		}
	}

	/**
	 * Null-safe way to put a Float value into the given ContentValues map.
	 * 
	 * If value is null, calls cv.putNull().
	 * 
	 * @param cv
	 * @param key
	 * @param value
	 */
	public static void putNullOrValue(ContentValues cv, String key, Float value) {
		if (value == null) {
			cv.putNull(key);
		} else {
			cv.put(key, value);
		}
	}

	/**
	 * Null-safe way to put a Double value into the given ContentValues map.
	 * 
	 * If value is null, calls cv.putNull().
	 * 
	 * @param cv
	 * @param key
	 * @param value
	 */
	public static void putNullOrValue(ContentValues cv, String key, Double value) {
		if (value == null) {
			cv.putNull(key);
		} else {
			cv.put(key, value);
		}
	}

	/**
	 * Returns the MD5 hash of the data read from the given input stream.
	 * 
	 * Closes the stream after reading.
	 * 
	 * @param inputStream
	 * @return null if there is any exception while reading the input stream.
	 */
	public static String getMD5Hash(InputStream inputStream) {
		try {
			// Compute hash
			MessageDigest digester = MessageDigest.getInstance("MD5");
			byte[] bytes = new byte[8192];
			int byteCount;

			while ((byteCount = inputStream.read(bytes)) > 0) {
				digester.update(bytes, 0, byteCount);
			}

			byte[] digest = digester.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < digest.length; i++) {
				String hex = Integer.toHexString(0xFF & digest[i]);

				if (hex.length() == 2) {
					hexString.append(hex);
				} else if (hex.length() == 1) {
					// pad it with 0
					hexString.append("0");
					hexString.append(hex);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"Expected 2 hex characters. But got "
										+ hex.length() + " characters.");
					}
				}
			}

			return hexString.toString();
		} catch (IOException e) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Error while reading input stream for computing MD5 hash: "
								+ e.toString());
			}

			return null;
		} catch (NoSuchAlgorithmException e) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "MD5 algorithm not present (extremely unlikely): "
						+ e.toString());
			}

			return null;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"Error while closing input stream after computing MD5 hash: "
										+ e.toString());
					}
				}
			}
		}
	}

	/**
	 * Returns the MD5 hash of the file at the given path.
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getMD5Hash(String filePath) {
		try {
			FileInputStream in = new FileInputStream(filePath);
			return getMD5Hash(in);
		} catch (FileNotFoundException e) {
			if (BuildConfig.DEBUG) {
				Log.e(TAG,
						"Error reading file at path " + filePath + ": "
								+ e.toString());
			}

			return null;
		}
	}

	/**
	 * Returns View.VISIBLE or View.GONE based on the emptiness of the given
	 * value.
	 * 
	 * @param value
	 * @return
	 */
	public static int getVisibility(String value) {
		if (TextUtils.isEmpty(value)) {
			return View.GONE;
		} else {
			return View.VISIBLE;
		}
	}

	/**
	 * Returns View.VISIBLE or View.GONE based on the emptiness of the given
	 * value.
	 * 
	 * @param value
	 * @return
	 */
	public static int getVisibility(Long value) {
		if (value == null) {
			return View.GONE;
		} else {
			return View.VISIBLE;
		}
	}

	/**
	 * Returns View.VISIBLE or View.GONE based on the visibility of the views
	 * passed.
	 * 
	 * @param value
	 * @return VISIBLE if at least one of of the views is visible; returns GONE
	 *         if all the views are GONE.
	 */
	public static int getVisibility(View... views) {
		for (View view : views) {
			if (view.getVisibility() == View.VISIBLE) {
				return View.VISIBLE;
			}
		}

		return View.GONE;
	}

	/**
	 * Returns the object/array/property found at given path from the given
	 * jsonObject.
	 * 
	 * @param jsonObject
	 * @param path
	 *            - a / separated path
	 * @return null if path doesn't exist (or if path is null or jsonObject is
	 *         null)
	 */
	public static Object getJson(JSONObject jsonObject, String path) {
		if (jsonObject == null || path == null) {
			return null;
		}

		String[] segments = path.split("/");

		Object object = null;

		for (int i = 0; i < segments.length; ++i) {
			object = jsonObject.opt(segments[i]);

			if (object == null) {
				return null;
			} else {
				if (i != segments.length - 1) {
					if (object instanceof JSONObject) {
						jsonObject = (JSONObject) object;
					} else {
						// We cannot traverse further if the current object is
						// not a JSONObject
						return null;
					}
				}
			}
		}

		return object;
	}

	/**
	 * Returns the JSONObject found at given path from the given jsonObject.
	 * 
	 * This is an utility method which just casts the result returned by
	 * getJson(jsonObject, path) to JSONObject.
	 * 
	 * @param jsonObject
	 * @param path
	 *            - a / separated path
	 * @return null if path doesn't exist (or if path is null or jsonObject is
	 *         null)
	 */
	public static JSONObject getJsonObject(JSONObject jsonObject, String path) {
		return (JSONObject) getJson(jsonObject, path);
	}

	/**
	 * Returns the JSONArray found at given path from the given jsonObject.
	 * 
	 * This is an utility method which just casts the result returned by
	 * getJson(jsonObject, path) to JSONArray.
	 * 
	 * @param jsonObject
	 * @param path
	 *            - a / separated path
	 * @return null if path doesn't exist (or if path is null or jsonObject is
	 *         null)
	 */
	public static JSONArray getJsonArray(JSONObject jsonObject, String path) {
		Object obj = getJson(jsonObject, path);

		if (obj instanceof JSONArray) {
			return (JSONArray) obj;
		} else {
			return null;
		}
	}

	/**
	 * 1. JSONObject.getString(propertyName) throws JSONException if property
	 * value is null. This method does a null check using
	 * JSONObject.isNull(propertyName) before accessing the value.
	 * 
	 * 2. EFFORT server API is inconsistent, sometimes it returns empty string,
	 * and sometimes it returns null. To make sure that we store nulls in the
	 * db, instead of empty strings, this method returns null if server returns
	 * an empty string.
	 * 
	 * @param jsonObject
	 * @param propertyName
	 * @return
	 * @throws JSONException
	 */
	public static String getString(JSONObject jsonObject, String propertyName)
			throws JSONException {
		if (jsonObject.isNull(propertyName)) {
			return null;
		} else {
			if (TextUtils.isEmpty(jsonObject.getString(propertyName))) {
				return null;
			} else {
				return jsonObject.getString(propertyName);
			}
		}
	}

	public static Integer getInteger(JSONObject jsonObject, String propertyName)
			throws JSONException {
		if (jsonObject.isNull(propertyName)) {
			return null;
		} else {
			if (TextUtils.isEmpty(jsonObject.getString(propertyName))) {
				return null;
			} else {
				return jsonObject.getInt(propertyName);
			}
		}
	}

	public static List<Long> getLongArray(JSONObject jsonObject, String path) {
		List<Long> customerList = null;
		JSONArray customerJsonArray = getJsonArray(jsonObject, path);
		if (customerJsonArray != null && customerJsonArray.length() > 0) {
			customerList = new ArrayList<Long>();
			for (int i = 0; i < customerJsonArray.length(); i++) {
				try {
					customerList.add(customerJsonArray.getLong(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return customerList;
	}

	public static Long getLong(JSONObject jsonObject, String propertyName)
			throws JSONException {
		if (jsonObject.isNull(propertyName)) {
			return null;
		} else {
			if (TextUtils.isEmpty(jsonObject.getString(propertyName))) {
				return null;
			} else {
				return jsonObject.getLong(propertyName);
			}
		}
	}

	public static Double getDouble(JSONObject jsonObject, String propertyName)
			throws JSONException {
		if (jsonObject.isNull(propertyName)) {
			return null;
		} else {
			if (TextUtils.isEmpty(jsonObject.getString(propertyName))) {
				return null;
			} else {
				return jsonObject.getDouble(propertyName);
			}
		}
	}

	/**
	 * If property is missing, returns null.
	 * 
	 * If property is specified, and it consists of only digits, 0 is considered
	 * false, and any other value is considered true.
	 * 
	 * If property is specified, and it will parse "true" and "false" to the
	 * corresponding boolean value.
	 * 
	 * @param jsonObject
	 * @param propertyName
	 * @return
	 * @throws JSONException
	 */
	public static Boolean getBoolean(JSONObject jsonObject, String propertyName)
			throws JSONException {
		if (jsonObject.isNull(propertyName)) {
			return null;
		} else {
			String str = jsonObject.getString(propertyName);

			if (TextUtils.isEmpty(str)) {
				return null;
			} else {
				if (TextUtils.isDigitsOnly(str)) {
					return !(Integer.parseInt(str) == 0);
				} else {
					// parses both "true"/true and "false"/false as boolean
					return jsonObject.getBoolean(propertyName);
				}
			}
		}
	}

	public static Date getDate(JSONObject jsonObject, String propertyName)
			throws JSONException, ParseException {
		if (jsonObject.isNull(propertyName)) {
			return null;
		} else {
			String str = jsonObject.getString(propertyName);

			if (TextUtils.isEmpty(str)) {
				return null;
			} else {
				return XsdDateTimeUtils.getLocalTime(str);
			}
		}
	}

	/**
	 * Returns the trimmed string from the EditText view.
	 * 
	 * If the trimmed string is empty, returns null.
	 * 
	 * @param editText
	 * @return
	 */
	public static String getString(EditText editText) {
		String str = editText.getText().toString().trim();

		if (TextUtils.isEmpty(str)) {
			return null;
		} else {
			return str;
		}
	}

	/**
	 * 
	 * @param editText
	 * @return if text is empty, returns null.
	 */
	public static Integer getInteger(EditText editText) {
		String str = editText.getText().toString();

		if (TextUtils.isEmpty(str)) {
			return null;
		} else {
			return Integer.parseInt(str);
		}
	}

	/**
	 * 
	 * @param editText
	 * @return if text is empty, returns null.
	 */
	public static Double getDouble(EditText editText) {
		String str = editText.getText().toString();

		if (TextUtils.isEmpty(str)) {
			return null;
		} else {
			return Double.parseDouble(str);
		}
	}

	/**
	 * 
	 * @param stringBuffer
	 * @param value
	 */

	public static void putValueOnlyIfNotNull(StringBuffer stringBuffer,
			String value) {
		if (value != null) {
			stringBuffer.append(value + ",");
		} else {
			stringBuffer.append(",");
		}
	}

	/**
	 * 
	 * @param stringBuffer
	 * @param value
	 */
	public static void putValueOnlyIfNotNull(StringBuffer stringBuffer,
			Float value) {
		if (value != null) {
			stringBuffer.append(value + ",");
		} else {
			stringBuffer.append(",");
		}
	}

	/**
	 * 
	 * @param stringBuffer
	 * @param value
	 */

	public static void putValueOnlyIfNotNull(StringBuffer stringBuffer,
			Date value) {
		if (value != null) {
			stringBuffer.append(value.getTime() + ",");
		} else {
			stringBuffer.append(",");
		}

	}

	/**
	 * 
	 * @param stringBuffer
	 * @param value
	 */
	public static void putValueOnlyIfNotNull(StringBuffer stringBuffer,
			Double value) {
		if (value != null) {
			stringBuffer.append(value + ",");
		} else {
			stringBuffer.append(",");
		}
	}

	/**
	 * 
	 * @param stringBuffer
	 * @param value
	 */
	public static void putValueOnlyIfNotNull(StringBuffer stringBuffer,
			Integer value) {
		if (value != null) {
			stringBuffer.append(value + ",");
		} else {
			stringBuffer.append(",");
		}
	}

	/**
	 * 
	 * @param json
	 * @param key
	 * @param value
	 * @throws JSONException
	 */
	public static void putValueOnlyIfNotNull(JSONObject json, String key,
			String value) throws JSONException {
		if (value != null) {
			json.put(key, value);
		}
	}

	/**
	 * 
	 * @param json
	 * @param key
	 * @param value
	 * @throws JSONException
	 */
	public static void putValueOnlyIfNotNull(JSONObject json, String key,
			Integer value) throws JSONException {
		if (value != null) {
			json.put(key, value);
		}
	}

	/**
	 * 
	 * @param json
	 * @param key
	 * @param value
	 * @throws JSONException
	 */
	public static void putValueOnlyIfNotNull(JSONObject json, String key,
			Long value) throws JSONException {
		if (value != null) {
			json.put(key, value);
		}
	}

	/**
	 * 
	 * @param json
	 * @param key
	 * @param value
	 * @throws JSONException
	 */
	public static void putValueOnlyIfNotNull(JSONObject json, String key,
			Float value) throws JSONException {
		if (value != null) {
			json.put(key, value);
		}
	}

	/**
	 * 
	 * @param json
	 * @param key
	 * @param value
	 * @throws JSONException
	 */
	public static void putValueOnlyIfNotNull(JSONObject json, String key,
			Double value) throws JSONException {
		if (value != null) {
			json.put(key, value);
		}
	}

	/**
	 * 
	 * @param json
	 * @param key
	 * @param value
	 * @throws JSONException
	 */
	public static void putValueOnlyIfNotNull(JSONObject json, String key,
			Boolean value) throws JSONException {
		if (value != null) {
			json.put(key, value);
		}
	}

	/**
	 * Automatically converts values to XSD date time formats, and puts the
	 * resulting string value.
	 * 
	 * @param json
	 * @param key
	 * @param value
	 * @throws JSONException
	 */
	public static void putValueOnlyIfNotNull(JSONObject json, String key,
			Date value) throws JSONException {
		if (value != null) {
			json.put(key, XsdDateTimeUtils.getXsdDateTimeFromLocalTime(value));
		}
	}

	public static void notImplemented(Context context) {
		Toast.makeText(context,
				"Sorry, this feature is still under development.",
				Toast.LENGTH_SHORT).show();
	}

	public static void fillLocationDto(LocationDto locationDto,
			String provider, Location location, boolean cached, boolean finalize) {
		if ("fused".equals(provider)) {
			locationDto.setFusedFinalized(locationDto.getFusedFinalized()
					|| finalize);

			if (locationDto.getFusedFinalized()) {
				locationDto.setLocationFinalized(true);
			}

			locationDto.setFusedCached(cached);

			if (location == null) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG, "fillLocationDto for " + provider
							+ " provider called with null location.");
				}

				return;
			}

			locationDto.setFusedFixTime(new Date(location.getTime()));

			if (location.hasAccuracy()) {
				locationDto.setFusedAccuracy(location.getAccuracy());
			}

			locationDto.setFusedLatitude(location.getLatitude());
			locationDto.setFusedLongitude(location.getLongitude());

			if (location.hasAltitude()) {
				locationDto.setFusedAltitude(location.getAltitude());
			}

			if (location.hasSpeed()) {
				locationDto.setFusedSpeed(location.getSpeed());
			}

			if (location.hasBearing()) {
				locationDto.setFusedBearing(location.getBearing());
			}
		} else if (LocationManager.GPS_PROVIDER.equals(provider)) {
			locationDto.setGpsFinalized(locationDto.getGpsFinalized()
					|| finalize);

			if (locationDto.getGpsFinalized()
					&& locationDto.getNetworkFinalized()) {
				locationDto.setLocationFinalized(true);
			}

			locationDto.setGpsCached(cached);

			if (location == null) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG, "fillLocationDto for " + provider
							+ " provider called with null location.");
				}

				return;
			}

			locationDto.setGpsFixTime(new Date(location.getTime()));

			if (location.hasAccuracy()) {
				locationDto.setGpsAccuracy(location.getAccuracy());
			}

			locationDto.setGpsLatitude(location.getLatitude());
			locationDto.setGpsLongitude(location.getLongitude());

			if (location.hasAltitude()) {
				locationDto.setGpsAltitude(location.getAltitude());
			}

			if (location.hasSpeed()) {
				locationDto.setGpsSpeed(location.getSpeed());
			}

			if (location.hasBearing()) {
				locationDto.setGpsBearing(location.getBearing());
			}
		} else if (LocationManager.NETWORK_PROVIDER.equals(provider)) {
			locationDto.setNetworkFinalized(locationDto.getNetworkFinalized()
					|| finalize);

			if (locationDto.getNetworkFinalized()
					&& locationDto.getGpsFinalized()) {
				locationDto.setLocationFinalized(true);
			}

			locationDto.setNetworkCached(cached);

			if (location == null) {
				if (BuildConfig.DEBUG) {
					Log.w(TAG, "fillLocationDto for " + provider
							+ " provider called with null location.");
				}

				return;
			}

			locationDto.setNetworkFixTime(new Date(location.getTime()));

			if (location.hasAccuracy()) {
				locationDto.setNetworkAccuracy(location.getAccuracy());
			}

			locationDto.setNetworkLatitude(location.getLatitude());
			locationDto.setNetworkLongitude(location.getLongitude());
		}
	}

	// public static boolean isLocationUsable(int shelfLife, Location location)
	// {
	// if (location == null) {
	// return false;
	// }
	//
	// long diff = System.currentTimeMillis() - location.getTime();
	// if (diff > shelfLife) {
	// return false;
	// } else {
	// return true;
	// }
	// }

	// public static boolean isLocationUsable(Context applicationContext,
	// Location location) {
	// if (location == null) {
	// return false;
	// }
	//
	// SettingsDao settingsDao = SettingsDao.getInstance(applicationContext);
	//
	// int shelfLife = settingsDao.getInt(
	// EffortProvider.Settings.KEY_LOCATION_SHELF_LIFE, 600000);
	//
	// return isLocationUsable(shelfLife, location);
	// }

	public static int getBatteryLevel(Context applicationContext) {
		int level = -1;

		IntentFilter batteryLevelFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = applicationContext.registerReceiver(null,
				batteryLevelFilter);

		if (batteryStatus != null) {
			int rawlevel = batteryStatus.getIntExtra(
					BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE,
					-1);
			level = -1;
			if (rawlevel >= 0 && scale > 0) {
				level = (rawlevel * 100) / scale;
			}
		}

		return level;
	}

	/**
	 * For values greater than 0: -113 + 2 * asu For negative values, return asu
	 * itself.
	 * 
	 * @param asu
	 * @return
	 */
	public static int toDbm(int asu) {
		if (asu >= 0) {
			return -113 + 2 * asu;
		} else {
			return asu;
		}
	}

	public static List<WorkingHour> getWorkingHoursOfToday(
			Context applicationContext) {
		WorkingHoursDao whDao = WorkingHoursDao.getInstance(applicationContext);
		Calendar now = Calendar.getInstance();
		int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
		List<WorkingHour> workingHours = whDao.getWorkingHours(dayOfWeek);

		if (workingHours != null) {
			for (WorkingHour workingHour : workingHours) {
				addYearMonthDateToTheWorkingHour(workingHour);
			}
		}
		return workingHours;
	}

	// adds Year Month Date To The given Hour since mid night 1970
	public static WorkingHour addYearMonthDateToTheWorkingHour(
			WorkingHour workingHour) {
		Calendar now = Calendar.getInstance();
		Date today = Utils.getDatePartAsDate(now);
		Calendar startTime = Utils.getDatePartAsCalendar(now);
		startTime.add(Calendar.MILLISECOND, (int) workingHour.getStartTime()
				.getTime() + now.get(Calendar.ZONE_OFFSET));
		Date startDate = Utils.getDatePartAsDate(startTime);

		Calendar endTime = Utils.getDatePartAsCalendar(now);
		endTime.add(Calendar.MILLISECOND, (int) workingHour.getEndTime()
				.getTime() + now.get(Calendar.ZONE_OFFSET));
		Date endDate = Utils.getDatePartAsDate(endTime);
		if (startDate.before(today)) {
			startTime.add(Calendar.DATE, 1);
		} else if (startDate.after(today)) {
			startTime.add(Calendar.DATE, -1);
		}
		workingHour.setStartTime(new Date(startTime.getTimeInMillis()));

		if (endDate.before(today)) {
			endTime.add(Calendar.DATE, 1);
		} else if (endDate.after(today)) {
			endTime.add(Calendar.DATE, -1);
		}
		workingHour.setEndTime(new Date(endTime.getTimeInMillis()));
		if (BuildConfig.DEBUG) {
			Log.v("work", workingHour.getStartTime() + "");
			Log.v("work", workingHour.getEndTime() + "");
		}
		return workingHour;
	}

	public static boolean canTrackNow(Context applicationContext) {
		SettingsDao settingsDao = SettingsDao.getInstance(applicationContext);
		boolean working = settingsDao.getBoolean("working", false);

		if (working) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Tracking because work started manually.");
			}

			return true;
		}

		JobsDao jobsDao = JobsDao.getInstance(applicationContext);
		if (jobsDao.hasJobsNow()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Tracking because of job(s).");
			}

			return true;
		}

		LeavesDao leavesDao = LeavesDao.getInstance(applicationContext);
		if (leavesDao.isOnLeaveNow()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Not tracking because of approved leave(s).");
			}

			return false;
		}

		SpecialWorkingHoursDao swhDao = SpecialWorkingHoursDao
				.getInstance(applicationContext);
		if (swhDao.isSwhNow()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Tracking because of special working hour(s).");
			}

			return true;
		}

		HolidaysDao holidaysDao = HolidaysDao.getInstance(applicationContext);
		if (holidaysDao.isHolidayNow()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Not tracking because of holiday(s).");
			}

			return false;
		}

		WorkingHoursDao whDao = WorkingHoursDao.getInstance(applicationContext);
		if (whDao.workingHoursNow()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Tracking because of working hour(s).");
			}

			return true;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Not tracking because none of the conditions matched.");
		}

		return false;
	}

	/**
	 * 
	 * @param applicationContext
	 * @return -1 if should not be tracked, 1-4 if there is a valid reason
	 */
	public static int canTrackNowWithReason(Context applicationContext) {
		SettingsDao settingsDao = SettingsDao.getInstance(applicationContext);
		boolean working = settingsDao.getBoolean("working", false);

		if (working) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Tracking because work started manually.");
			}

			return 1;
		}

		JobsDao jobsDao = JobsDao.getInstance(applicationContext);
		if (jobsDao.hasJobsNow()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Tracking because of job(s).");
			}

			return 2;
		}

		LeavesDao leavesDao = LeavesDao.getInstance(applicationContext);
		if (leavesDao.isOnLeaveNow()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Not tracking because of approved leave(s).");
			}

			return -1;
		}

		SpecialWorkingHoursDao swhDao = SpecialWorkingHoursDao
				.getInstance(applicationContext);
		if (swhDao.isSwhNow()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Tracking because of special working hour(s).");
			}

			return 3;
		}

		HolidaysDao holidaysDao = HolidaysDao.getInstance(applicationContext);
		if (holidaysDao.isHolidayNow()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Not tracking because of holiday(s).");
			}

			return -1;
		}

		WorkingHoursDao whDao = WorkingHoursDao.getInstance(applicationContext);
		if (whDao.workingHoursNow()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Tracking because of working hour(s).");
			}

			return 4;
		}

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "Not tracking because none of the conditions matched.");
		}

		return -1;
	}

	public static synchronized void startGpsReceiver(Context applicationContext) {
		if (!gpsReceiverOn) {
			EffortApplication.log(TAG, "Requesting gps location updates.");
			Intent intent = new Intent(applicationContext, GpsReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					applicationContext, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			LocationManager locationManager = (LocationManager) applicationContext
					.getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, pendingIntent);
			gpsReceiverOn = true;
		} else {
			EffortApplication.log(TAG, "GPS receiver is already on.");
		}
	}

	private static synchronized void stopGpsReceiver(Context applicationContext) {
		if (gpsReceiverOn) {
			EffortApplication.log(TAG,
					"Stopped receiving gps location updates.");
			Intent intent = new Intent(applicationContext, GpsReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					applicationContext, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			LocationManager locationManager = (LocationManager) applicationContext
					.getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(pendingIntent);
			gpsReceiverOn = false;
		} else {
			EffortApplication.log(TAG, "GPS receiver is already off.");
		}
	}

	/**
	 * 
	 * @param applicationContext
	 * @param force
	 *            immediately stops the receiver, without considering whether
	 *            there are unfinalized locations
	 */
	public static void stopGpsReceiver(Context applicationContext, boolean force) {
		if (force) {
			stopGpsReceiver(applicationContext);
		} else {
			LocationsDao locationsDao = LocationsDao
					.getInstance(applicationContext);
			if (!locationsDao.hasUnfinalizedGpsLocations()) {
				stopGpsReceiver(applicationContext);
			}
		}
	}

	public static synchronized void startNetworkReceiver(
			Context applicationContext) {
		if (!networkReceiverOn) {
			EffortApplication.log(TAG, "Requesting network location updates.");
			Intent intent = new Intent(applicationContext,
					NetworkReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					applicationContext, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			LocationManager locationManager = (LocationManager) applicationContext
					.getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0, pendingIntent);
			networkReceiverOn = true;
		} else {
			EffortApplication.log(TAG, "Network receiver is already on.");
		}
	}

	private static void stopNetworkReceiver(Context applicationContext) {
		if (networkReceiverOn) {
			EffortApplication.log(TAG,
					"Stopped receiving network location updates.");
			Intent intent = new Intent(applicationContext,
					NetworkReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(
					applicationContext, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			LocationManager locationManager = (LocationManager) applicationContext
					.getSystemService(Context.LOCATION_SERVICE);
			locationManager.removeUpdates(pendingIntent);
			networkReceiverOn = false;
		} else {
			EffortApplication.log(TAG, "Network receiver is already off.");
		}
	}

	/**
	 * 
	 * @param applicationContext
	 * @param force
	 *            immediately stops the receiver, without considering whether
	 *            there are unfinalized locations
	 */
	public static void stopNetworkReceiver(Context applicationContext,
			boolean force) {
		if (force) {
			stopNetworkReceiver(applicationContext);
		} else {
			LocationsDao locationsDao = LocationsDao
					.getInstance(applicationContext);
			if (!locationsDao.hasUnfinalizedNetworkLocations()) {
				stopNetworkReceiver(applicationContext);
			}
		}
	}

	public static synchronized void startFusedReceiver(
			Context applicationContext) {
		if (!fusedReceiverOn) {
			EffortApplication.log(TAG, "Requesting fused location updates.");
			EffortApplication.locationClient = new LocationClient(
					applicationContext, EffortApplication.getInstance(),
					EffortApplication.getInstance());
			EffortApplication.locationClient.connect();
			fusedReceiverOn = true;
		} else {
			EffortApplication.log(TAG, "Fused receiver is already on.");
		}
	}

	private static synchronized void stopFusedReceiver(
			Context applicationContext) {
		if (fusedReceiverOn) {
			EffortApplication.log(TAG,
					"Stopped receiving fused location updates.");

			if (EffortApplication.locationClient != null) {
				Intent intent = new Intent(applicationContext,
						FusedReceiver.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						applicationContext, 1, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				EffortApplication.locationClient
						.removeLocationUpdates(pendingIntent);
				EffortApplication.locationClient.disconnect();
			}

			fusedReceiverOn = false;
		} else {
			EffortApplication.log(TAG, "Fused receiver is already off.");
		}
	}

	/**
	 * 
	 * @param applicationContext
	 * @param force
	 *            immediately stops the fused receiver, without considering
	 *            whether there are unfinalized locations
	 */
	public static void stopFusedReceiver(Context applicationContext,
			boolean force) {
		if (force) {
			stopFusedReceiver(applicationContext);
		} else {
			LocationsDao locationsDao = LocationsDao
					.getInstance(applicationContext);
			if (!locationsDao.hasUnfinalizedFusedLocations()) {
				stopFusedReceiver(applicationContext);
			}
		}
	}

	/**
	 * @param options
	 * @param option
	 * @return -1 if option is not found in options.
	 */
	public static int getPosition(List<String> options, String option) {
		if (options == null || option == null) {
			return -1;
		}

		int size = options.size();

		for (int i = 0; i < size; ++i) {
			if (option.equals(options.get(i))) {
				return i;
			}
		}

		return -1;
	}

	public static void sendSms(String phoneNumber, String message) {
		sendTextMessage(phoneNumber, null, message, null, null);
	}

	public static void sendSms(String phoneNumber, String message,
			PendingIntent pendingIntent) {
		sendTextMessage(phoneNumber, null, message, null, pendingIntent);
	}

	private static void sendTextMessage(String destinationAddress,
			String scAddress, String text, PendingIntent sentIntent,
			PendingIntent deliveryIntent) {
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(destinationAddress, scAddress, text,
				sentIntent, deliveryIntent);
	}

	public static boolean isEmailAddressValid(String email, boolean required) {
		if (TextUtils.isEmpty(email)) {
			if (required) {
				return false;
			} else {
				return true;
			}
		}

		return Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email);
	}

	public static boolean isNumberValid(String numericString, boolean required) {
		if (TextUtils.isEmpty(numericString)) {
			if (required) {
				return false;
			} else {
				return true;
			}
		}

		try {
			Double.parseDouble(numericString);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static String validateNumberRange(String remoteValue,
			Boolean required, Long minValue, Long maxValue) {
		if (TextUtils.isEmpty(remoteValue)) {
			if (required) {
				return " cannot be empty.";
			} else {
				return null;
			}
		}

		try {
			Double localValue = Double.parseDouble(remoteValue);

			if (minValue != null && localValue < minValue) {
				return " cannot be less than " + minValue + ".";
			}

			if (maxValue != null && localValue > maxValue) {
				return " cannot be greater than " + maxValue + ".";
			}
		} catch (NumberFormatException e) {
			return " is not a valid number.";
		}

		return null;
	}

	/**
	 * 
	 * @param remoteValue
	 * @param required
	 * @param minLength
	 * @param maxLength
	 * @return null when length is valid, and an error message when it is
	 *         invalid
	 */
	public static String validateLength(String remoteValue, Boolean required,
			Long minLength, Long maxLength) {

		if (TextUtils.isEmpty(remoteValue)) {
			if (required) {
				return " cannot be empty.";
			} else {
				return null;
			}
		}

		int len = remoteValue.length();

		if (minLength != null && len < minLength) {
			return " smaller than the minimum allowed length (" + minLength
					+ ").";
		}

		if (maxLength != null && len > maxLength) {
			return " larger than the maximum allowed length (" + maxLength
					+ ").";
		}

		return null;
	}

	public static boolean isLocationValid(String latLng, boolean required) {
		if (TextUtils.isEmpty(latLng)) {
			if (required) {
				return false;
			} else {
				return true;
			}
		}
		Pattern p = Pattern
				.compile("^([-+]?\\d{1,2}([.]\\d+)?),\\s*([-+]?\\d{1,3}([.]\\d+)?)$");
		Matcher m = p.matcher(latLng);
		return m.matches();
	}

	/**
	 * Returns beginning (i.e. midnight or 00:00) of today.
	 * 
	 * @return
	 */
	public static Date getBeginningOfToday() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.clear();
		cal.set(year, month, day);
		return cal.getTime();
	}

	public static Date getBeginningOfDayFromTodayBefore(int days) {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.clear();
		cal.set(year, month, day);
		// 1000*60*60*24
		cal.add(Calendar.MILLISECOND, -(days * 1000 * 60 * 60 * 24));
		return cal.getTime();
	}

	/**
	 * Returns beginning (i.e. midnight or 00:00) of the given date.
	 * 
	 * @return
	 */
	public static Date getBeginningOfDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		cal.clear();
		cal.set(year, month, day);
		return cal.getTime();
	}

	/**
	 * Adds the jobs found in the given JSON array, to the destination list.
	 * 
	 * @param source
	 * @param jobs
	 * @param customers
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addJobs(JSONArray source, List<Job> jobs,
			List<Customer> customers, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of jobs: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				JSONObject jobJson = source.getJSONObject(i);

				if (jobJson != null) {
					Job job = Job.parse(jobJson, applicationContext);

					if (job != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Job " + i + ": " + job.toString());
						}

						jobs.add(job);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "Job " + i + " is null.");
						}
					}

					// if the job has a customer object inside it, add that
					// customer to customer list
					if (!jobJson.isNull("customer")) {
						Customer customer = Customer.parse(
								jobJson.getJSONObject("customer"),
								applicationContext);
						if (customer != null) {
							customers.add(customer);
						}
					}
				}
			}
		}
	}

	/**
	 * Adds the notes found in the given JSON array, to the destination list.
	 * 
	 * @param source
	 * @param destination
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addNotes(JSONArray source, List<Note> destination,
			Context applicationContext) throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of notes: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				Note note = Note.parse(source.getJSONObject(i),
						applicationContext);

				if (note != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Note " + i + ": " + note.toString());
					}

					destination.add(note);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Note " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the forms found in the given JSON array, to the destination list.
	 * 
	 * @param source
	 * @param destination
	 * @param
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addForms(JSONArray source, List<Form> destination,
			Context applicationContext) throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of forms: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				Form form = Form.parse(source.getJSONObject(i),
						applicationContext);

				if (form != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Form " + i + ": " + form.toString());
					}

					destination.add(form);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Form " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the fields found in the given JSON array, to the destination list.
	 * 
	 * @param source
	 * @param destination
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addFields(JSONArray source, List<Field> destination,
			Context applicationContext) throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of fields: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				Field field = Field.parse(source.getJSONObject(i),
						applicationContext);

				if (field != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Field " + i + ": " + field.toString());
					}

					destination.add(field);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Field " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the section fields found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param destination
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addSectionFields(JSONArray source,
			List<SectionField> destination, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of section fields: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				SectionField field = SectionField.parse(
						source.getJSONObject(i), applicationContext);

				if (field != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Section field " + i + ": " + field.toString());
					}

					destination.add(field);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Section field " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the customer found in the given JSON array, to the destination list.
	 * 
	 * @param source
	 * @param destination
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addCustomers(JSONArray source,
			List<Customer> destination, Context applicationContext,
			boolean fromSearchResults) throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of customers: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				Customer customer = Customer.parse(source.getJSONObject(i),
						applicationContext);

				if (customer != null) {
					if (fromSearchResults) {
						if (customer.getLocalId() == null) {
							customer.setPartial(true);
							customer.setInUse(false);
							destination.add(customer);
						}
					} else {
						if (BuildConfig.DEBUG) {
							Log.i(TAG,
									"Customer " + i + ": "
											+ customer.toString());
						}

						destination.add(customer);
					}
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Customer " + i + " is null.");
					}
				}
			}
		}
	}

	public static void addEmployees(JSONArray source,
			List<Employee> destination, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of leaves: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				Employee employee = Employee.parse(source.getJSONObject(i),
						applicationContext);

				if (employee != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Leave " + i + ": " + employee.toString());
					}
					destination.add(employee);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Leave " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the histories found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param histories
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addHistories(JSONArray source,
			List<JobHistory> histories, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of histories: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				JSONObject historyJson = source.getJSONObject(i);

				if (historyJson != null) {
					JobHistory history = JobHistory.parse(historyJson,
							applicationContext);

					if (history != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG,
									"History " + i + ": " + history.toString());
						}

						histories.add(history);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "History " + i + " is null.");
						}
					}
				}
			}
		}
	}

	/**
	 * Adds the stage statuses found in the given JSON array, to the destination
	 * list.
	 * 
	 * @param source
	 * @param histories
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addStatuses(JSONArray source,
			List<JobStageStatus> statuses, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of statuses: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				JSONObject statusJson = source.getJSONObject(i);

				if (statusJson != null) {
					JobStageStatus status = JobStageStatus.parse(statusJson,
							applicationContext);

					if (status != null) {
						if (BuildConfig.DEBUG) {
							Log.i(TAG, "Status " + i + ": " + status.toString());
						}

						statuses.add(status);
					} else {
						if (BuildConfig.DEBUG) {
							Log.w(TAG, "Status " + i + " is null.");
						}
					}
				}
			}
		}
	}

	/**
	 * Adds the named locations found in the given JSON array, to the
	 * destination list.
	 * 
	 * @param source
	 * @param destination
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addLocations(JSONArray source,
			List<NamedLocation> destination, Context applicationContext,
			boolean calledFromSearch) throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of named locations: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				NamedLocation location = NamedLocation.parse(
						source.getJSONObject(i), applicationContext,
						calledFromSearch);

				if (location != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Named location " + i + ": "
										+ location.toString());
					}

					destination.add(location);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Named location " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the messages found in the given JSON array, to the destination list.
	 * 
	 * @param source
	 * @param destination
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addMessages(JSONArray source, List<Message> destination,
			Context applicationContext, boolean calledFromSearch)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of messages: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				Message notification = Message.parse(source.getJSONObject(i),
						applicationContext);

				if (notification != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Message " + i + ": " + notification.toString());
					}

					destination.add(notification);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Message " + i + " is null.");
					}
				}
			}
		}
	}

	public static boolean includeLargeFiles(Context applicationContext) {
		SettingsDao settingsDao = SettingsDao.getInstance(applicationContext);

		String uploadSetting = settingsDao
				.getString(EffortProvider.Settings.KEY_UPLOAD);
		boolean includeLargeFiles = (EffortProvider.Settings.UPLOAD_WIFI
				.equals(uploadSetting) && Utils
				.isConnectedToWifi(applicationContext))
				|| EffortProvider.Settings.UPLOAD_ANY.equals(uploadSetting);
		return includeLargeFiles;
	}

	/**
	 * Starts the background file transfer service, if:
	 * <ol>
	 * <li>Connected to network</li>
	 * <li>SD card is mounted</li>
	 * <li>Current job has any downloads that the user requested or has any
	 * pending uploads</li>
	 * <li>Service is not already running</li>
	 * </ol>
	 * 
	 * 
	 * @return true if all the pre-conditions hold, and the service is started
	 *         or it has already been running; false, otherwise.
	 */
	public static boolean startBftsIfRequired(Context applicationContext) {
		boolean includeLargeFiles = Utils.includeLargeFiles(applicationContext);

		NotesDao notesDao = NotesDao.getInstance(applicationContext);
		SettingsDao settingsDao = SettingsDao.getInstance(applicationContext);
		FormFilesDao formFilesDao = FormFilesDao
				.getInstance(applicationContext);
		SectionFilesDao sectionFilesDao = SectionFilesDao
				.getInstance(applicationContext);
		ArticlesDao articlesDao = ArticlesDao.getInstance(applicationContext);

		boolean bftsRequired = Utils.isConnected(applicationContext)
				&& Utils.isSDCardValid(applicationContext, false)
				&& (notesDao.hasPendingTransfers(applicationContext, null,
						includeLargeFiles)
						|| formFilesDao.hasPendingTransfers(applicationContext,
								null, includeLargeFiles)
						|| sectionFilesDao.hasPendingTransfers(
								applicationContext, null, includeLargeFiles)
						|| !TextUtils
								.isEmpty(settingsDao
										.getString(EffortProvider.Settings.KEY_APP_LOGO_URL)) || articlesDao
							.hasPendingDownloads(applicationContext, null));

		if (bftsRequired) {
			if (Utils.isServiceRunning(applicationContext,
					"BackgroundFileTransferService")) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "BFTS is already running.");
				}

				return true;
			} else {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "Starting BFTS.");
				}

				WakefulIntentService.sendWakefulWork(applicationContext,
						BackgroundFileTransferService.class);
				return true;
			}
		} else {
			return false;
		}
	}

	// public static void restartBfts(Context applicationContext) {
	// Intent intent = new Intent(applicationContext,
	// BackgroundFileTransferService.class);
	// applicationContext.stopService(intent);
	//
	// startBftsIfRequired(applicationContext);
	// }

	public static String getLocalLogoPath() {
		return Environment.getExternalStorageDirectory() + "/EFFORT/logo.png";
	}

	public static void updateActionBar(ActionBar actionBar) {
		if (actionBar == null) {
			return;
		}

		String path = getLocalLogoPath();
		File file = new File(path);

		if (file.exists()) {
			Drawable drawable = Drawable.createFromPath(path);

			if (drawable != null) {
				actionBar.setIcon(drawable);
			}
		}
	}

	public static String getMcc(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mccMnc = telephonyManager.getNetworkOperator();

		if (mccMnc == null) {
			return null;
		} else {
			if (TextUtils.isDigitsOnly(mccMnc) && mccMnc.length() >= 3) {
				return mccMnc.substring(0, 3);
			} else {
				return null;
			}
		}
	}

	/**
	 * 
	 * @param context
	 *            for displaying a toast in case of IO exceptions
	 * @param src
	 * @param dst
	 */
	public static void copyFile(Context context, String src, String dst) {
		if (TextUtils.equals(src, dst)) {
			if (BuildConfig.DEBUG) {
				Log.w(TAG, "Source (" + src + ") and destination (" + dst
						+ ") are the same. Skipping file copying.");
			}
			return;
		}

		FileInputStream in = null;
		FileOutputStream out = null;

		try {
			in = new FileInputStream(src);
			out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (IOException e) {
			Toast.makeText(
					context,
					"Failed to copy " + src + " to " + dst + ": "
							+ e.getMessage(), Toast.LENGTH_LONG).show();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"Ignored the exception caught while closing input stream for "
										+ src + ": " + e.getMessage(), e);
					}
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG,
								"Ignored the exception caught while closing output stream for "
										+ dst + ": " + e.getMessage(), e);
					}
				}
			}
		}
	}

	public static void handleVersionUpdate(final Context activityContext,
			final SettingsDao settingsDao) {
		String currentVersion = Utils.getVersionName(activityContext
				.getApplicationContext());

		if (!TextUtils.isEmpty(settingsDao.getString("appVersion"))
				&& !TextUtils.equals(currentVersion,
						settingsDao.getString("appVersion"))) {
			String preference = settingsDao
					.getString("versionUpdatePreference");
			if (TextUtils.isEmpty(preference)
					|| "notnow".equals(preference)
					|| ("tomorrow".equals(preference) && !TextUtils.equals(
							settingsDao.getString("versionDialogDate"),
							SQLiteDateTimeUtils.getCurrentSQLiteDate()))) {
				new AlertDialog.Builder(activityContext)
						.setTitle(
								"Update to EFFORT "
										+ settingsDao.getString("appVersion")
										+ "?")
						.setMessage(
								"You currently have EFFORT " + currentVersion
										+ ".\n"
										+ "What's new in this version:\n"
										+ settingsDao.getString("appChangeLog"))
						.setPositiveButton("Update now",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										Intent intent = new Intent(
												Intent.ACTION_VIEW,
												Uri.parse(settingsDao
														.getString("appUrl")));
										activityContext.startActivity(intent);
										settingsDao.saveSetting(
												"versionUpdatePreference", "");
									}
								})
						.setNeutralButton("Remind later",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										settingsDao
												.saveSetting(
														"versionDialogDate",
														SQLiteDateTimeUtils
																.getCurrentSQLiteDate());
										settingsDao.saveSetting(
												"versionUpdatePreference",
												"notnow");
									}
								})
						.setNegativeButton("Remind tomorrow",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										settingsDao.saveSetting(
												"versionUpdatePreference",
												"tomorrow");
										settingsDao
												.saveSetting(
														"versionDialogDate",
														SQLiteDateTimeUtils
																.getCurrentSQLiteDate());
									}
								}).show();
			}
		}
	}

	public static boolean isMappedCustomer(SettingsDao settingsDao,
			CustomersDao customersDao, long localCustomerId) {
		String mappedCustomers = settingsDao.getString("mappedCustomers");

		if (TextUtils.isEmpty(mappedCustomers)) {
			return true;
		}

		Long remoteCustomerId = customersDao.getRemoteId(localCustomerId);

		if (remoteCustomerId == null) {
			return true;
		}

		String[] mappedCustomersArray = mappedCustomers.split(",");

		for (String customerId : mappedCustomersArray) {
			if (TextUtils.equals(customerId, "" + remoteCustomerId)) {
				return true;
			}
		}

		return false;
	}

	public static void showSignupHelp(Context activityContext) {
		Toast.makeText(
				activityContext,
				"Please follow the instructions in help to sign up and provision your mobile.",
				Toast.LENGTH_LONG).show();
		Intent helpIntent = new Intent(activityContext, HelpActivity.class);
		helpIntent.putExtra("anchor", "set-up");
		helpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activityContext.startActivity(helpIntent);
	}

	public static void compressImage(String imagePath, Context activityContext) {
		System.gc();
		Options decodeBounds = new Options();
		decodeBounds.inJustDecodeBounds = true;

		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, decodeBounds);
		int numPixels = decodeBounds.outWidth * decodeBounds.outHeight;
		int maxPixels = 2048 * 1536; // requires 12 MB heap

		Options options = new Options();
		options.inSampleSize = (numPixels >= maxPixels) ? 2 : 1;

		bitmap = BitmapFactory.decodeFile(imagePath, options);

		FileOutputStream out;
		try {
			out = new FileOutputStream(imagePath);

			if (bitmap != null) {
				bitmap.compress(CompressFormat.JPEG, 60, out);
			}

		} catch (FileNotFoundException e) {
			Toast.makeText(activityContext,
					"Could not compress " + imagePath + ": " + e.getMessage(),
					Toast.LENGTH_LONG).show();
			Log.e(TAG, "File not found.", e);
		}
	}

	public static Bitmap decodeImageForDisplay(String imagePath,
			Context activityContext) {
		Options decodeBounds = new Options();
		decodeBounds.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(imagePath, decodeBounds);
		int numPixels = decodeBounds.outWidth * decodeBounds.outHeight;
		int maxPixels = 1024 * 768; // requires 6 MB heap

		Options options = new Options();
		options.inSampleSize = (numPixels >= maxPixels) ? 4 : 2;

		return BitmapFactory.decodeFile(imagePath, options);

	}

	/**
	 * Returns the index of the given string from the list of strings.
	 * 
	 * @param strings
	 * @param string
	 * @return -1 if either of the arguments is empty, or string is not found in
	 *         the list.
	 */
	public static int getStringIndex(List<String> strings, String string) {
		if (strings == null || strings.size() <= 0 || TextUtils.isEmpty(string)) {
			return -1;
		}

		int numItems = strings.size();

		for (int i = 0; i < numItems; ++i) {
			if (string.equals(strings.get(i))) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Returns the index of the given string from the array of strings.
	 * 
	 * @param strings
	 * @param string
	 * @return -1 if either of the arguments is empty, or string is not found in
	 *         the list.
	 */
	public static int getStringIndex(String[] strings, String string) {
		if (strings == null || strings.length <= 0 || TextUtils.isEmpty(string)) {
			return -1;
		}

		int numItems = strings.length;

		for (int i = 0; i < numItems; ++i) {
			if (string.equals(strings[i])) {
				return i;
			}
		}

		return -1;
	}

	public static String getAddressForMapDisplay(String street, String area,
			String city, String state, String country, String pinCode) {
		StringBuffer address = new StringBuffer();

		if (!TextUtils.isEmpty(street)) {
			address.append(street);
		}

		if (!TextUtils.isEmpty(area)) {
			if (address.length() > 0) {
				address.append(", ");
			}

			address.append(area);
		}

		if (!TextUtils.isEmpty(city)) {
			if (address.length() > 0) {
				address.append(", ");
			}

			address.append(city);
		}

		if (!TextUtils.isEmpty(state)) {
			if (address.length() > 0) {
				address.append(" ");
			}

			address.append(state);
		}

		if (pinCode != null) {
			if (address.length() > 0) {
				address.append(", ");
			}

			address.append(pinCode);
		}

		if (!TextUtils.isEmpty(country)) {
			if (address.length() > 0) {
				address.append(", ");
			}

			address.append(country);
		}

		return address.toString();
	}

	/**
	 * 
	 * @param settingsDao
	 * @return null if there is no usable GPS location
	 */
	public static Location getLastKnownGpsLocation(SettingsDao settingsDao) {
		String latitude = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_GPS_LATITUDE);
		String longitude = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_GPS_LONGITUDE);
		String fixTime = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_GPS_FIX_TIME);

		if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)
				|| TextUtils.isEmpty(fixTime)) {
			return null;
		}

		Location location = new Location(LocationManager.GPS_PROVIDER);
		location.setTime(SQLiteDateTimeUtils.getLocalTime(fixTime).getTime());
		location.setLatitude(Double.parseDouble(latitude));
		location.setLongitude(Double.parseDouble(longitude));

		String str = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_GPS_ALTITUDE);

		if (!TextUtils.isEmpty(str)) {
			location.setAltitude(Double.parseDouble(str));
		}

		str = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_GPS_ACCURACY);

		if (!TextUtils.isEmpty(str)) {
			location.setAccuracy(Float.parseFloat(str));
		}

		str = settingsDao.getString(EffortProvider.Settings.KEY_LAST_GPS_SPEED);

		if (!TextUtils.isEmpty(str)) {
			location.setSpeed(Float.parseFloat(str));
		}

		str = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_GPS_BEARING);

		if (!TextUtils.isEmpty(str)) {
			location.setBearing(Float.parseFloat(str));
		}

		return location;
	}

	/**
	 * 
	 * @param settingsDao
	 * @return null if there is no usable network location
	 */
	public static Location getLastKnownNetworkLocation(SettingsDao settingsDao) {
		String latitude = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_NETWORK_LATITUDE);
		String longitude = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_NETWORK_LONGITUDE);
		String fixTime = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_NETWORK_FIX_TIME);
		String accuracy = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_NETWORK_ACCURACY);

		if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)
				|| TextUtils.isEmpty(fixTime)) {
			return null;
		}

		Location location = new Location(LocationManager.NETWORK_PROVIDER);
		location.setTime(SQLiteDateTimeUtils.getLocalTime(fixTime).getTime());
		location.setLatitude(Double.parseDouble(latitude));
		location.setLongitude(Double.parseDouble(longitude));

		if (!TextUtils.isEmpty(accuracy)) {
			location.setAccuracy(Float.parseFloat(accuracy));
		}

		return location;
	}

	/**
	 * 
	 * @param settingsDao
	 * @return null if there is no usable fused location
	 */
	public static Location getLastKnownFusedLocation(SettingsDao settingsDao) {
		String latitude = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_FUSED_LATITUDE);
		String longitude = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_FUSED_LONGITUDE);
		String fixTime = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_FUSED_FIX_TIME);

		if (TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude)
				|| TextUtils.isEmpty(fixTime)) {
			return null;
		}

		Location location = new Location("fused");
		location.setTime(SQLiteDateTimeUtils.getLocalTime(fixTime).getTime());
		location.setLatitude(Double.parseDouble(latitude));
		location.setLongitude(Double.parseDouble(longitude));

		String str = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_FUSED_ALTITUDE);

		if (!TextUtils.isEmpty(str)) {
			location.setAltitude(Double.parseDouble(str));
		}

		str = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_FUSED_ACCURACY);

		if (!TextUtils.isEmpty(str)) {
			location.setAccuracy(Float.parseFloat(str));
		}

		str = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_FUSED_SPEED);

		if (!TextUtils.isEmpty(str)) {
			location.setSpeed(Float.parseFloat(str));
		}

		str = settingsDao
				.getString(EffortProvider.Settings.KEY_LAST_FUSED_BEARING);

		if (!TextUtils.isEmpty(str)) {
			location.setBearing(Float.parseFloat(str));
		}

		return location;
	}

	public synchronized static void flushLocations(Context applicationContext) {
		flushLocations(SettingsDao.getInstance(applicationContext));
		LocationManager locationManager = (LocationManager) applicationContext
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER,
				"delete_aiding_data", null);
		locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER,
				"force_time_injection", null);
		locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER,
				"force_xtra_injection", null);
	}

	public synchronized static void flushLocations(SettingsDao settingsDao) {
		flushGpsLocation(settingsDao);
		flushNetworkLocation(settingsDao);
		flushFusedLocation(settingsDao);
	}

	public synchronized static void flushGpsLocation(SettingsDao settingsDao) {
		settingsDao.saveSetting(EffortProvider.Settings.KEY_LAST_GPS_FIX_TIME,
				"");
		settingsDao.saveSetting(EffortProvider.Settings.KEY_LAST_GPS_LATITUDE,
				"");
		settingsDao.saveSetting(EffortProvider.Settings.KEY_LAST_GPS_LONGITUDE,
				"");
		settingsDao.saveSetting(EffortProvider.Settings.KEY_LAST_GPS_ALTITUDE,
				"");
		settingsDao.saveSetting(EffortProvider.Settings.KEY_LAST_GPS_ACCURACY,
				"");
		settingsDao.saveSetting(EffortProvider.Settings.KEY_LAST_GPS_SPEED, "");
		settingsDao.saveSetting(EffortProvider.Settings.KEY_LAST_GPS_BEARING,
				"");
	}

	public synchronized static void flushNetworkLocation(SettingsDao settingsDao) {
		settingsDao.saveSetting(
				EffortProvider.Settings.KEY_LAST_NETWORK_FIX_TIME, "");
		settingsDao.saveSetting(
				EffortProvider.Settings.KEY_LAST_NETWORK_LATITUDE, "");
		settingsDao.saveSetting(
				EffortProvider.Settings.KEY_LAST_NETWORK_LONGITUDE, "");
		settingsDao.saveSetting(
				EffortProvider.Settings.KEY_LAST_NETWORK_ACCURACY, "");
	}

	public synchronized static void flushFusedLocation(SettingsDao settingsDao) {
		settingsDao.saveSetting(
				EffortProvider.Settings.KEY_LAST_FUSED_FIX_TIME, "");
		settingsDao.saveSetting(
				EffortProvider.Settings.KEY_LAST_FUSED_LATITUDE, "");
		settingsDao.saveSetting(
				EffortProvider.Settings.KEY_LAST_FUSED_LONGITUDE, "");
		settingsDao.saveSetting(
				EffortProvider.Settings.KEY_LAST_FUSED_ALTITUDE, "");
		settingsDao.saveSetting(
				EffortProvider.Settings.KEY_LAST_FUSED_ACCURACY, "");
		settingsDao.saveSetting(EffortProvider.Settings.KEY_LAST_FUSED_SPEED,
				"");
		settingsDao.saveSetting(EffortProvider.Settings.KEY_LAST_FUSED_BEARING,
				"");
	}

	/**
	 * Adds the entities found in the given JSON array, to the destination list.
	 * 
	 * @param source
	 * @param destination
	 * @param
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addEntities(JSONArray source, List<Entity> destination,
			Context applicationContext) throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array passed to addEntities is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of entities: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				Entity entity = Entity.parse(source.getJSONObject(i),
						applicationContext);

				if (entity != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Entity " + i + ": " + entity.toString());
					}

					destination.add(entity);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Entity " + i + " is null.");
					}
				}
			}
		}
	}

	/**
	 * Adds the fields found in the given JSON array, to the destination list.
	 * 
	 * @param source
	 * @param destination
	 * @param applicationContext
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addEntityFields(JSONArray source,
			List<EntityField> destination, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG,
						"Source json array passed to addEntityFields is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of entity fields: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				EntityField field = EntityField.parse(source.getJSONObject(i),
						applicationContext);

				if (field != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"Entity field " + i + ": " + field.toString());
					}

					destination.add(field);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Entity field " + i + " is null.");
					}
				}
			}
		}
	}

	public static Date getTime(int hourOfDay, int minute) {
		Calendar cal = Calendar.getInstance();

		cal.clear();
		cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set(Calendar.MINUTE, minute);

		return cal.getTime();
	}

	/**
	 * Adds the articles found in the given JSON array, to the destination list.
	 * 
	 * @param source
	 * @param destination
	 * @param
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static void addArticles(JSONArray source, List<Article> destination,
			Context applicationContext) throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array passed to addArticles is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of articles: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				Article article = Article.parse(source.getJSONObject(i),
						applicationContext);

				if (article != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Article " + i + ": " + article.toString());
					}

					destination.add(article);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Article " + i + " is null.");
					}
				}
			}
		}
	}

	public static void rotateImageIfRequired(String imagePath,
			Context activityContext) {
		System.gc();
		int degrees = 0;

		try {
			ExifInterface exif = new ExifInterface(imagePath);
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degrees = 90;
				break;

			case ExifInterface.ORIENTATION_ROTATE_180:
				degrees = 180;
				break;

			case ExifInterface.ORIENTATION_ROTATE_270:
				degrees = 270;
				break;
			default:
				// no rotation required
				return;
			}
		} catch (IOException e) {
			Log.e(TAG, "Error in reading Exif data of " + imagePath, e);
		}

		Options decodeBounds = new Options();
		decodeBounds.inJustDecodeBounds = true;

		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, decodeBounds);
		int numPixels = decodeBounds.outWidth * decodeBounds.outHeight;
		int maxPixels = 2048 * 1536; // requires 12 MB heap

		Options options = new Options();
		options.inSampleSize = (numPixels >= maxPixels) ? 2 : 1;

		bitmap = BitmapFactory.decodeFile(imagePath, options);

		if (bitmap == null) {
			Toast.makeText(activityContext, "Could not rotate " + imagePath,
					Toast.LENGTH_LONG).show();
			return;
		}

		Matrix matrix = new Matrix();
		matrix.setRotate(degrees);

		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);

		FileOutputStream out;
		try {
			out = new FileOutputStream(imagePath);
			bitmap.compress(CompressFormat.JPEG, 90, out);
		} catch (FileNotFoundException e) {
			Toast.makeText(activityContext,
					"Could not save " + imagePath + ": " + e.getMessage(),
					Toast.LENGTH_LONG).show();
			Log.e(TAG, "File not found.", e);
		}
	}

	public static Date getDatePartAsDate(Calendar calendar) {
		return new GregorianCalendar(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH)).getTime();
	}

	public static Calendar getDatePartAsCalendar(Calendar calendar) {
		return new GregorianCalendar(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DAY_OF_MONTH));
	}

	public static void fetchNearbyCustomers(Context applicationContext,
			double latitude, double longitude) {
		Intent intent = new Intent(applicationContext,
				FetchNearbyCustomersService.class);
		intent.putExtra("latitude", latitude);
		intent.putExtra("longitude", longitude);
		WakefulIntentService.sendWakefulWork(applicationContext, intent);
	}

	/**
	 * 
	 * @param prevLatitudeString
	 *            - can be empty or null
	 * @param prevLongitudeString
	 *            - can be empty or null
	 */
	public static void fetchNearbyCustomersIfRequired(
			Context applicationContext, String prevLatitudeString,
			String prevLongitudeString, double latitude, double longitude) {
		if (!TextUtils.isEmpty(prevLatitudeString)
				&& !TextUtils.isEmpty(prevLongitudeString)) {
			double prevLatitude = Double.parseDouble(prevLatitudeString);
			double prevLongitude = Double.parseDouble(prevLongitudeString);
			float[] results = new float[1];

			Location.distanceBetween(prevLatitude, prevLongitude, latitude,
					longitude, results);

			if (results[0] > 2500) {
				fetchNearbyCustomers(applicationContext, latitude, longitude);
			}
		} else {
			fetchNearbyCustomers(applicationContext, latitude, longitude);
		}
	}

	public static void updateCustomerDistances(Context applicationContext) {
		SettingsDao settingsDao = SettingsDao.getInstance(applicationContext);
		CustomersDao customersDao = CustomersDao
				.getInstance(applicationContext);
		String prevLatitude = settingsDao.getString("nearbyLatitude");
		String prevLongitude = settingsDao.getString("nearbyLongitude");

		if (!TextUtils.isEmpty(prevLatitude)
				&& !TextUtils.isEmpty(prevLongitude)) {
			List<Customer> customers = customersDao
					.getCustomersWithCoordinates();

			float[] results = new float[1];

			double latitude = Double.parseDouble(prevLatitude);
			double longitude = Double.parseDouble(prevLongitude);

			for (Customer customer : customers) {
				Location.distanceBetween(latitude, longitude,
						customer.getLatitude(), customer.getLongitude(),
						results);
				customersDao.updateDistance(results[0], customer.getLocalId());
			}
		}
	}

	public static boolean isLatitudeLongitudeValid(String latitudeString,
			String longitudeString) {
		if (TextUtils.isEmpty(latitudeString)
				|| TextUtils.isEmpty(longitudeString)) {
			return false;
		}

		try {
			Double.parseDouble(latitudeString);
			Double.parseDouble(longitudeString);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/*
	 * Show just a simple notification without any options
	 */
	public static void showNotification(Context context, String message) {
		SettingsDao settingsDao = SettingsDao.getInstance(context);
		if (TextUtils.equals(message,
				settingsDao.getString("lastNotificationMessage"))) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Not showing notification because it was already shown. message="
								+ message);
			}

			return;
		}

		Intent intent = null;

		if (message.contains("New messages: ")) {
			intent = new Intent(context, MessagesActivity.class);
		} else {
			intent = new Intent(context, JobsActivity.class);
		}

		intent.putExtra("launchedFromNotification", true);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification notification = new NotificationCompat.Builder(context)
				.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
				.setContentText(message).setContentTitle("EFFORT")
				.setSmallIcon(R.drawable.ic_launcher_transparent)
				.setTicker(message).setContentIntent(pendingIntent).build();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NEW_JOBS_NOTIFICATION, notification);
		settingsDao.saveSetting("lastNotificationMessage", message);
	}

	public static void showLateToJobNotification(Context applicationContext,
			Job job, String ticker) {
		SettingsDao settingsDao = SettingsDao.getInstance(applicationContext);
		long prevMillis = settingsDao.getLong(
				"lastLateToJobNotificationMillis", 0);

		if ((System.currentTimeMillis() - prevMillis) < FIVE_MINUTES_IN_MILLIS) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Late to job notification was shown less than 5 minutes ago. Not showing again.");
			}

			return;
		}

		if (TextUtils.equals(ticker,
				settingsDao.getString("lastLateToJobNotificationMessage"))) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG,
						"Not showing notification because it was already shown. message="
								+ ticker);
			}

			return;
		}

		Intent intent = new Intent(applicationContext, JobActivity.class);
		intent.setData(Uri.parse("distance" + job.getLocalId()));
		intent.putExtra(EffortProvider.Jobs._ID, job.getLocalId());
		intent.putExtra("launchedFromNotification", true);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				applicationContext, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Intent snoozeIntent = new Intent(applicationContext,
				JobAlertReceiver.class);
		snoozeIntent.putExtra(EffortProvider.Jobs._ID, job.getLocalId());
		snoozeIntent.putExtra("action", "snooze");

		PendingIntent snoozePI = PendingIntent.getBroadcast(applicationContext,
				1, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		Intent dismissIntent = new Intent(applicationContext,
				JobAlertReceiver.class);
		dismissIntent.putExtra(EffortProvider.Jobs._ID, job.getLocalId());
		dismissIntent.putExtra("action", "dismiss");

		PendingIntent dismissPI = PendingIntent.getBroadcast(
				applicationContext, 2, dismissIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(
				applicationContext).setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_ALL).setContentText(ticker)
				.setContentTitle(job.getTitle())
				.setSmallIcon(R.drawable.ic_launcher_transparent)
				.addAction(0, "Snooze", snoozePI)
				.addAction(0, "Dismiss", dismissPI).setTicker(ticker)
				.setContentIntent(pendingIntent).build();
		NotificationManager notificationManager = (NotificationManager) applicationContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(LATE_TO_JOB_NOTIFICATION, notification);
		settingsDao.saveSetting("lastLateToJobNotificationMessage", ticker);
		settingsDao.saveSetting("lastLateToJobNotificationMillis",
				"" + System.currentTimeMillis());
	}

	/**
	 * Don't call this method from the UI thread.
	 * 
	 * @param applicationContext
	 * @param location
	 */
	public static void nagIfRequired(Context applicationContext,
			Location location) {
		JobsDao jobsDao = JobsDao.getInstance(applicationContext);

		Job nextJob = jobsDao.getNextFutureIncompleteJob();

		if (nextJob != null) {
			Double latitude = nextJob.getLatitude();
			Double longitude = nextJob.getLongitude();

			if (latitude == null && longitude == null) {
				String addr = Utils.getAddressForMapDisplay(
						nextJob.getStreet(), nextJob.getArea(),
						nextJob.getCity(), nextJob.getState(),
						nextJob.getCountry(), nextJob.getPinCode());

				Geocoder geocoder = new Geocoder(applicationContext);
				try {
					List<Address> addresses = geocoder.getFromLocationName(
							addr, 1);

					if (addresses != null && addresses.size() > 0) {
						Address address = addresses.get(0);

						if (address != null && address.hasLatitude()
								&& address.hasLongitude()) {
							latitude = address.getLatitude();
							longitude = address.getLongitude();
						}
					}
				} catch (IOException e) {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "IOException while resolving lat long of '"
								+ addr, e);
					}
				}
			}

			if (latitude != null && longitude != null) {
				float[] results = new float[1];
				Location.distanceBetween(location.getLatitude(),
						location.getLongitude(), latitude, longitude, results);

				float distance = results[0];
				long millis = nextJob.getStartTime().getTime()
						- System.currentTimeMillis();

				if (distance > 0 && millis > 0) {
					// reasonable travel speed: 15kmph
					if ((15000.0 * millis) / (60 * 60 * 1000) < distance) {

						String ticker = "You're still "
								+ String.format("%.1f", distance / 1000.0)
								+ " kms. away from the job starting in "
								+ (millis / 60000)
								+ "mins. Start immediately to avoid reaching late.";
						showLateToJobNotification(applicationContext, nextJob,
								ticker);
					}
				}
			}
		}

	}

	/**
	 * Call this method to set alarms 1) for future job that will start next,
	 * and (job having least start time) 2) for future job that will finish
	 * next, (job having least completed by time) by considering the criteria
	 * that jobs are not sequential, means that the job that is started first
	 * may not be completed fisrt.before the completion of prevoius job, next
	 * job may start
	 * 
	 * @param applicationContext
	 * @param
	 */
	public static void setAlarmsForJobs(Context applicationContext) {

		try {
			SettingsDao settingsDao = SettingsDao
					.getInstance(applicationContext);
			int minutes = settingsDao.getInt(
					EffortProvider.Settings.KEY_REMINDER_MINUTES,
					EffortProvider.Settings.DEFAULT_REMINDER_MINUTES);
			// send a minus value , because we need to substract
			long alertBeforeStart = (-1) * (minutes * 60 * 1000);
			long alertAfterEnd = 0;// immediatley after the job ends
			JobsDao jobsDao = JobsDao.getInstance(applicationContext);
			// for alarm for latest start job
			Job nextJob = jobsDao.getNextFutureJobBasedOnStartTime();
			// for alarm for latest end job
			Job nextEndJob = jobsDao.getNextFutureJobBasedOnCompletionTime();
			boolean showNextStartJob = true;
			boolean showNextEndJob = true;

			if (nextJob != null
					&& (settingsDao.getLong(
							EffortProvider.Settings.KEY_PREVIOUS_START_JOB_ID,
							EffortProvider.Settings.DEFAULT_JOB_ID) == nextJob
							.getLocalId() || nextJob.getLocalId() == 0)) {
				showNextStartJob = false;
			}
			if (nextEndJob != null
					&& (settingsDao.getLong(
							EffortProvider.Settings.KEY_PREVIOUS_END_JOB_ID,
							EffortProvider.Settings.DEFAULT_JOB_ID) == nextEndJob
							.getLocalId() || nextEndJob.getLocalId() == 0)) {
				showNextEndJob = false;
			}

			if (BuildConfig.DEBUG) {
				if (nextJob != null) {
					Log.v("alarm", nextJob.getTitle()
							+ "actual "
							+ new Date(nextJob.getStartTime().getTime()
									+ alertBeforeStart) + "");
					Log.v("alarm",
							"current " + new Date(System.currentTimeMillis()));
					Log.v("alarm",
							""
									+ nextJob.getStartTime()
											.after(new Date(System
													.currentTimeMillis())));
				} else {
					Log.v("alarm", "next startjob null");
				}
			}
			if (BuildConfig.DEBUG) {
				if (nextEndJob != null) {
					Log.v("alarm", nextEndJob.getTitle() + "actual "
							+ new Date(nextEndJob.getEndTime().getTime()) + "");
					Log.v("alarm",
							"current " + new Date(System.currentTimeMillis()));
					Log.v("alarm",
							""
									+ nextEndJob.getEndTime()
											.after(new Date(System
													.currentTimeMillis())));
				} else {
					Log.v("alarm", "next end job null");
				}
			}
			if (nextJob != null
					&& nextJob.getStartTime().getTime() + alertBeforeStart < System
							.currentTimeMillis()) {

				showNextStartJob = false;
			}

			if (nextEndJob != null
					&& nextEndJob.getEndTime().getTime() < System
							.currentTimeMillis()) {

				showNextEndJob = false;
			}
			if (showNextStartJob) {
				setAlarmsForStartTime(applicationContext, nextJob,
						alertBeforeStart);
			}
			if (showNextEndJob) {
				setAlarmsForEndTime(applicationContext, nextEndJob,
						alertAfterEnd);
			}
		} catch (Exception e) {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, e.toString());
			}
			e.printStackTrace();
		}
	}

	// set alarm for latest start job
	public static void setAlarmsForStartTime(Context applicationContext,
			Job nextJob, long beforeSecs) {

		// set alarm intent
		if (nextJob != null) {
			if (BuildConfig.DEBUG) {
				Log.v("alarm", nextJob.getTitle() + " "
						+ nextJob.getStartTime().toString());
			}
			Intent latestStartJobIntent = new Intent(applicationContext,
					JobStartAndEndAlarmReceiver.class);
			latestStartJobIntent.setData(Uri.parse("start"
					+ nextJob.getLocalId()));
			Bundle startJobBundle = new Bundle();
			startJobBundle.putString("type", "start");
			startJobBundle.putSerializable("job", nextJob);
			latestStartJobIntent.putExtras(startJobBundle);
			PendingIntent latestStartJobPendingIntent = PendingIntent
					.getBroadcast(applicationContext, 0, latestStartJobIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
			if (BuildConfig.DEBUG) {
				Log.v("alarm", "date is "
						+ new Date(nextJob.getStartTime().getTime()
								+ beforeSecs));
			}
			setAlarmsForGivenTime(applicationContext, nextJob.getStartTime()
					.getTime() + beforeSecs, latestStartJobPendingIntent);
		}
	}

	// for alarm for latest end job
	public static void setAlarmsForEndTime(Context applicationContext,
			Job nextEndJob, long beforeSecs) {
		// set alarm intent
		if (nextEndJob != null) {
			Intent latestEndJobIntent = new Intent(applicationContext,
					JobStartAndEndAlarmReceiver.class);
			latestEndJobIntent.setData(Uri.parse("end"
					+ nextEndJob.getLocalId()));
			Bundle endJobBundle = new Bundle();
			endJobBundle.putString("type", "end");
			endJobBundle.putSerializable("job", nextEndJob);
			latestEndJobIntent.putExtras(endJobBundle);
			PendingIntent latestEndJobPendingIntent = PendingIntent
					.getBroadcast(applicationContext, 1, latestEndJobIntent,
							PendingIntent.FLAG_UPDATE_CURRENT);
			// nextEndJob.setEndTime(new
			// Date(nextEndJob.getEndTime().getTime()+beforeSecs));
			if (BuildConfig.DEBUG) {
				Log.v("alarm", "date is "
						+ new Date(nextEndJob.getEndTime().getTime()
								+ beforeSecs));
			}
			setAlarmsForGivenTime(applicationContext, nextEndJob.getEndTime()
					.getTime() + beforeSecs, latestEndJobPendingIntent);
		}
	}

	// sets alarm for the given time with the given pending intent
	public static void setAlarmsForGivenTime(Context context, long time,
			PendingIntent pendingIntent) {
		AlarmManager alarmMgr = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.set(AlarmManager.RTC, time, pendingIntent);

	}

	/*
	 * called to cancel alarms for whole day i.e at starting of the day, middle
	 * of the day and end of the day param context
	 */
	public static void cancelAlarmsForDay(Context applicationContext) {
		cancelAlarm("workstart", 1, applicationContext);
		cancelAlarm("workend", 2, applicationContext);
		cancelAlarm("workmiddle", 3, applicationContext);
	}

	public static void cancelAlarm(String action, int requestCode,
			Context applicationContext) {
		Intent dayIntent = new Intent(applicationContext,
				WorkingHoursRemindersAlertReceiver.class);

		dayIntent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				applicationContext, requestCode, dayIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmMgr = (AlarmManager) applicationContext
				.getSystemService(Context.ALARM_SERVICE);
		alarmMgr.cancel(pendingIntent);
	}

	/*
	 * called to create alarms for whole day i.e at starting of the day, middle
	 * of the day and end of the day param context
	 */
	public static void setAlarmsForDay(Context applicationContext) {
		List<WorkingHour> workHours = getWorkingHoursOfToday(applicationContext);
		if (workHours != null) {
			setAlarm(applicationContext, "workstart", workHours);
			setAlarm(applicationContext, "workend", workHours);
			setAlarm(applicationContext, "workmiddle", workHours);
		}
	}

	// sets alarm based on action with proper time
	public static void setAlarm(Context applicationContext, String action,
			List<WorkingHour> workHours) {
		long alertTime = 0;
		int requestCode = 0;
		if (action.equalsIgnoreCase("workstart")) {
			alertTime = workHours.get(0).getStartTime().getTime();
			requestCode = 1;
		} else if (action.equalsIgnoreCase("workend")) {
			alertTime = workHours.get(workHours.size() - 1).getEndTime()
					.getTime();
			requestCode = 2;
		} else if (action.equalsIgnoreCase("workmiddle")) {
			alertTime = (workHours.get(0).getStartTime().getTime() + workHours
					.get(workHours.size() - 1).getEndTime().getTime()) / 2;
			requestCode = 3;
		}
		// set alarm intent
		if (alertTime >= System.currentTimeMillis()) {
			Intent dayIntent = new Intent(applicationContext,
					WorkingHoursRemindersAlertReceiver.class);
			dayIntent.setAction(action);
			PendingIntent dayPendingIntent = PendingIntent.getBroadcast(
					applicationContext, requestCode, dayIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			setAlarmsForGivenTime(applicationContext, alertTime,
					dayPendingIntent);
		}
	}

	// visible-0 hide-1 disable-2
	public static int decideVisibilityBasedOnCriterias(ViewField viewField,
			VisibilityCriteria criteria) {
		// 0 means it can be shown
		int canShow = 0;

		if (criteria != null) {

			String targetValue = criteria.getValue();
			String comparingValue = viewField.getRemoteValue();
			if (!TextUtils.isEmpty(viewField.getLocalValue())) {
				switch (viewField.getType()) {

				case FieldSpecs.TYPE_CUSTOMER:
					comparingValue = ""
							+ CustomersDao.getInstance(
									EffortApplication.getInstance())
									.getRemoteId(
											Long.parseLong(viewField
													.getLocalValue()));

					break;

				case FieldSpecs.TYPE_EMPLOYEE:
					comparingValue = ""
							+ EmployeesDao.getInstance(
									EffortApplication.getInstance())
									.getRemoteId(
											Long.parseLong(viewField
													.getLocalValue()));

					break;

				case FieldSpecs.TYPE_ENTITY:
					comparingValue = ""
							+ EntitiesDao.getInstance(
									EffortApplication.getInstance())
									.getRemoteId(
											Long.parseLong(viewField
													.getLocalValue()));

					break;

				case FieldSpecs.TYPE_MULTI_LIST:
					List<Long> remoteValues = EntitiesDao.getInstance(
							EffortApplication.getInstance()).getRemoteIds(
							viewField.getLocalValue());
					comparingValue = TextUtils.join(", ", remoteValues);
					break;

				default:
					break;
				}
			}
			if (comparingValue == null || targetValue == null) {
				return canShow;
			}

			comparingValue = comparingValue.toLowerCase(Locale.ENGLISH);
			targetValue = targetValue.toLowerCase(Locale.ENGLISH);

			int visibilityType = criteria.getVisibilityType();
			int fieldDataType = criteria.getFieldDataType();
			if (fieldDataType == EffortProvider.TYPE_TEXT
					|| fieldDataType == EffortProvider.TYPE_EMAIL
					|| fieldDataType == EffortProvider.TYPE_PHONE
					|| fieldDataType == EffortProvider.TYPE_URL) {
				if (criteria.getCondition() == EffortProvider.CON_TEXT_EQUALS) {
					if (comparingValue.equalsIgnoreCase(targetValue)) {
						canShow = visibilityType;
					}
				} else if (criteria.getCondition() == EffortProvider.CON_TEXT_CONTAINS) {
					if (comparingValue.indexOf(targetValue) != -1) {
						canShow = visibilityType;
					}
				} else if (criteria.getCondition() == EffortProvider.CON_TEXT_DOES_NOT_CONTAINS) {
					if (comparingValue.indexOf(targetValue) == -1) {
						canShow = visibilityType;
					}
				} else if (criteria.getCondition() == EffortProvider.CON_TEXT_START_WITH) {
					if (comparingValue.startsWith(targetValue)) {
						canShow = visibilityType;
					}
				} else if (criteria.getCondition() == EffortProvider.CON_TEXT_ENDS_WITH) {
					if (comparingValue.endsWith(targetValue)) {
						canShow = visibilityType;
					}
				}
			} else if (fieldDataType == EffortProvider.TYPE_NUMBER
					|| fieldDataType == EffortProvider.TYPE_CURRENCY) {

				if (criteria.getCondition() == EffortProvider.CON_NUMBER_LESS_THAN) {
					double comparingDouble = Double.parseDouble(comparingValue);
					double targetDouble = Double.parseDouble(targetValue);
					if (comparingDouble < targetDouble) {
						canShow = visibilityType;
					}
				} else if (criteria.getCondition() == EffortProvider.CON_NUMBER_GREATER_THAN_OR_EQUALS) {
					double comparingDouble = Double.parseDouble(comparingValue);
					double targetDouble = Double.parseDouble(targetValue);
					if (comparingDouble >= targetDouble) {
						canShow = visibilityType;
					}
				} else if (criteria.getCondition() == EffortProvider.CON_NUMBER_LESS_THAN_OR_EQUALS) {
					double comparingDouble = Double.parseDouble(comparingValue);
					double targetDouble = Double.parseDouble(targetValue);
					if (comparingDouble <= targetDouble) {
						canShow = visibilityType;
					}
				} else if (criteria.getCondition() == EffortProvider.CON_NUMBER_EQUALS) {
					double comparingDouble = Double.parseDouble(comparingValue);
					double targetDouble = Double.parseDouble(targetValue);
					if (comparingDouble == targetDouble) {
						canShow = visibilityType;
					}
				} else if (criteria.getCondition() == EffortProvider.CON_NUMBER_NOT_EQUAL) {
					double comparingDouble = Double.parseDouble(comparingValue);
					double targetDouble = Double.parseDouble(targetValue);
					if (comparingDouble != targetDouble) {
						canShow = visibilityType;
					}
				} else if (criteria.getCondition() == EffortProvider.CON_NUMBER_GREATER_THAN) {
					double comparingDouble = Double.parseDouble(comparingValue);
					double targetDouble = Double.parseDouble(targetValue);
					if (comparingDouble > targetDouble) {
						canShow = visibilityType;
					}
				}
			} else if (fieldDataType == EffortProvider.TYPE_DATE
					|| fieldDataType == EffortProvider.TYPE_TIME) {
				if (criteria.getCondition() == EffortProvider.CON_DATE_AFTER
						|| criteria.getCondition() == EffortProvider.CON_DATE_BEFORE
						|| criteria.getCondition() == EffortProvider.CON_DATE_ON
						|| criteria.getCondition() == EffortProvider.CON_DATE_NOT_ON) {
					Date comparingDate = null;
					Date targetDate = null;
					if (fieldDataType == 3) {
						comparingDate = getYearMonthDateFromString(comparingValue);
						targetDate = getYearMonthDateFromString(targetValue);
					} else if (fieldDataType == 11) {
						comparingDate = getHourAndMinuteFromString(comparingValue);
						targetDate = getHourAndMinuteFromString(targetValue);
					}

					if (criteria.getCondition() == EffortProvider.CON_DATE_AFTER) {
						if (comparingDate.after(targetDate)) {
							canShow = visibilityType;
						}
					} else if (criteria.getCondition() == EffortProvider.CON_DATE_BEFORE) {
						if (comparingDate.before(targetDate)) {
							canShow = visibilityType;
						}
					} else if (criteria.getCondition() == EffortProvider.CON_DATE_ON) {
						if (comparingDate.equals(targetDate)) {
							canShow = visibilityType;
						}
					} else if (criteria.getCondition() == EffortProvider.CON_DATE_NOT_ON) {
						if (!comparingDate.equals(targetDate)) {
							canShow = visibilityType;
						}
					}

				} else if (criteria.getCondition() == EffortProvider.CON_DATE_IN_BETWEEN) {
					Date comparingDate = null;
					String[] dateRanges = null;
					Date startingDate = null;
					Date endingDate = null;
					if (fieldDataType == EffortProvider.TYPE_DATE) {
						comparingDate = getYearMonthDateFromString(comparingValue);
						dateRanges = targetValue.split(",");
						startingDate = getYearMonthDateFromString(dateRanges[0]);
						endingDate = getYearMonthDateFromString(dateRanges[1]);
					} else if (fieldDataType == EffortProvider.TYPE_TIME) {
						comparingDate = getHourAndMinuteFromString(comparingValue);
						dateRanges = targetValue.split(",");
						startingDate = getHourAndMinuteFromString(dateRanges[0]);
						endingDate = getHourAndMinuteFromString(dateRanges[1]);
					}
					if (comparingDate.compareTo(startingDate) >= 0
							&& comparingDate.compareTo(endingDate) <= 0) {
						canShow = visibilityType;
					}

				}
			} else if (fieldDataType == EffortProvider.TYPE_SINGLE_SELECT_LIST
					|| fieldDataType == EffortProvider.TYPE_MULTI_SELECT_LIST
					|| fieldDataType == EffortProvider.TYPE_CUSTOMER
					|| fieldDataType == EffortProvider.TYPE_LIST
					|| fieldDataType == EffortProvider.TYPE_EMPLOYEE
					|| fieldDataType == EffortProvider.TYPE_MULTI_LIST) {
				String[] targetValuesArray = targetValue.split(",");
				ArrayList<String> targetValues = new ArrayList<String>();
				for (int i = 0; i < targetValuesArray.length; i++) {
					targetValues.add(targetValuesArray[i]);
				}

				String[] comparingValuesArray = comparingValue.split(",");
				ArrayList<String> comparingValues = new ArrayList<String>();
				for (int i = 0; i < comparingValuesArray.length; i++) {
					comparingValues.add(comparingValuesArray[i]);
				}
				int count = 0;
				for (int i = 0; i < comparingValues.size(); i++) {
					for (int j = 0; j < targetValues.size(); j++) {

						if (comparingValues.get(i).equalsIgnoreCase(
								targetValues.get(j))) {
							count = count + 1;
						}
					}
				}
				if (criteria.getCondition() == EffortProvider.CON_IN) {
					if (count > 0) {
						canShow = visibilityType;
					}
				} else if (criteria.getCondition() == EffortProvider.CON_NOT_IN) {
					if (count == 0) {
						canShow = visibilityType;
					}
				}
			} else if (fieldDataType == EffortProvider.TYPE_YES_OR_NO) {
				if (criteria.getCondition() == EffortProvider.CON_TYPE_YES) {
					targetValue = "True";
					if (comparingValue.equalsIgnoreCase(targetValue)) {
						canShow = visibilityType;
					}
				} else if (criteria.getCondition() == EffortProvider.CON_TYPE_NO) {
					targetValue = "False";
					if (comparingValue.equalsIgnoreCase(targetValue)) {
						canShow = visibilityType;
					}
				}
			}
		}
		return canShow;
	}

	private static Date getHourAndMinuteFromString(String time) {

		String[] timeParts = time.split(":");
		int hourOfDay = Integer.parseInt(timeParts[0]);
		int minute = Integer.parseInt(timeParts[1]);
		return getTime(hourOfDay, minute);

	}

	private static Date getYearMonthDateFromString(String time) {

		return SQLiteDateTimeUtils.getDate(time);
	}

	// ROUTE PLAN
	public static void addActivities(JSONArray source,
			List<ActivitySpec> destination, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of activities: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				ActivitySpec activity = ActivitySpec.parse(
						source.getJSONObject(i), applicationContext);

				if (activity != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "Activity " + i + ": " + activity.toString());
					}

					destination.add(activity);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "Activity " + i + " is null.");
					}
				}
			}
		}
	}

	public static void addCompletedActivities(JSONArray source,
			List<CompletedActivity> destination, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of CompletedActivities: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				CompletedActivity completedActivity = CompletedActivity.parse(
						source.getJSONObject(i), applicationContext);

				if (completedActivity != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "CompletedActivity " + i + ": "
								+ completedActivity.toString());
					}

					destination.add(completedActivity);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "CompletedActivity " + i + " is null.");
					}
				}
			}
		}
	}

	public static void addAssignedRoutes(JSONArray source,
			List<AssignedRoute> destination, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of AssignedRoutes: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				AssignedRoute assignedRoute = AssignedRoute.parse(
						source.getJSONObject(i), applicationContext, false);

				if (assignedRoute != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"AssignedRoute " + i + ": "
										+ assignedRoute.toString());
					}

					destination.add(assignedRoute);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "AssignedRoute " + i + " is null.");
					}
				}
			}
		}
	}

	public static void addCustomersStatus(JSONArray source,
			List<CustomerStatusDto> destination, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}
			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of CustomersStatus: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				CustomerStatusDto customerStatusDto = CustomerStatusDto.parse(
						source.getJSONObject(i), applicationContext);

				if (customerStatusDto != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "CustomerStatusDto " + i + ": "
								+ customerStatusDto.toString());
					}

					destination.add(customerStatusDto);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "CustomerStatusDto " + i + " is null.");
					}
				}
			}
		}
	}

	public static void addCompletedAssignedRoutes(JSONArray source,
			List<AssignedRoute> destination, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}

			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of CompletedAssignedRoute: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				AssignedRoute completedAssignRoute = AssignedRoute.parse(
						source.getJSONObject(i), applicationContext, true);

				if (completedAssignRoute != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "CompletedAssignedRoute " + i + ": "
								+ completedAssignRoute.toString());
					}

					destination.add(completedAssignRoute);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "CustomerStatusDto " + i + " is null.");
					}
				}
			}
		}

	}

	public static String getFormattedString(List<Long> ids) {
		String string = "";
		if (ids == null || ids.size() <= 0) {
			return null;
		}

		// ids.toArray(array);
		/*
		 * Object[] array1 = (Object[]) ids.toArray(); Long[] array = (Long[])
		 * array1;
		 * 
		 * // int numItems = ids.size(); Long array2 = new Long(ids.size());
		 */
		int numItems = ids.size();
		for (int i = 0; i < numItems; i++) {
			/*
			 * if (i == (numItems - 1)) { string = string + "'" + array[i] +
			 * "'";
			 * 
			 * } else { string = string + "'" + array[i] + "',"; }
			 */
			if (i == (numItems - 1)) {
				string = string + ids.get(i);

			} else {
				string = string + ids.get(i) + ",";
			}
		}

		return string;
	}

	public static String getExistedFormSpecsAsString(Context applicationContext) {
		// try {
		FormSpecsDao formSpecsDao = FormSpecsDao
				.getInstance(applicationContext);
		String existedFormSpesIdsAsString = TextUtils.join(",",
				formSpecsDao.getAllFormSpecIds());
		return existedFormSpesIdsAsString;
	}

	public static String getHint(ViewField viewField) {

		String hintText = "";
		if (viewField.getType() == FieldSpecs.TYPE_NUMBER
				|| viewField.getType() == FieldSpecs.TYPE_CURRENCY) {
			if (viewField.getMinValue() != null
					&& viewField.getMaxValue() != null) {
				if (viewField.getMaxValue() == viewField.getMinValue()) {
					hintText = "Value must be " + viewField.getMaxValue();
				} else {
					hintText = "Value >= " + viewField.getMinValue()
							+ " and <=" + viewField.getMaxValue();
				}
			} else if (viewField.getMinValue() != null) {
				hintText = "Value >= " + viewField.getMinValue();
			} else if (viewField.getMaxValue() != null) {
				hintText = "Value <= " + viewField.getMaxValue();
			}
		} else if (viewField.getType() == FieldSpecs.TYPE_TEXT) {

			if (viewField.getMinValue() != null
					&& viewField.getMaxValue() != null) {
				if (viewField.getMaxValue() == viewField.getMinValue()) {
					hintText = "Length must be " + viewField.getMaxValue();
				} else {
					hintText = "Length >= " + viewField.getMinValue()
							+ " and <= " + viewField.getMaxValue();
				}
			} else if (viewField.getMinValue() != null) {
				hintText = "Length >= " + viewField.getMinValue();
			} else if (viewField.getMaxValue() != null) {
				hintText = "Length <= " + viewField.getMaxValue();
			}
		} else {
			if (viewField.getRequired()) {
				hintText = "Required";
			} else {
				hintText = "Optional";
			}
		}
		return hintText;
	}

	/*
	 * Deletes Form Fields That Are Deleted At ServerSide after each sync
	 */
	public static void deleteFormFieldsThatAreDeletedAtServerSide(
			Context applicationContext, long localFormId,
			List<Field> fieldsAtServer) {
		FieldsDao fieldsDao = FieldsDao.getInstance(applicationContext);
		List<Field> fieldsInDB = fieldsDao.getFields(localFormId);

		if (fieldsInDB == null || fieldsInDB.size() == 0) {
			return;
		}

		for (Field dbField : fieldsInDB) {
			boolean deleted = true;

			if (fieldsAtServer != null && fieldsAtServer.size() > 0) {
				for (Field serverField : fieldsAtServer) {
					if (Utils.longsEqual(serverField.getLocalFormId(),
							localFormId)
							&& Utils.longsEqual(dbField.getFieldSpecId(),
									serverField.getFieldSpecId())) {
						deleted = false;
						break;
					}
				}
			}

			if (deleted) {
				// delete that field record from local db also
				fieldsDao.deleteFields(localFormId, dbField.getFieldSpecId());
			}
		}
	}

	/*
	 * Deletes section Fields That Are Deleted At ServerSide after each sync
	 */
	public static void deleteSectionFieldsThatAreDeletedAtServerSide(
			Context applicationContext, long localFormId,
			List<SectionField> sectionFieldsAtServer) {
		SectionFieldsDao sectionFieldsDao = SectionFieldsDao
				.getInstance(applicationContext);
		List<SectionField> fieldsInDB = sectionFieldsDao.getFields(localFormId);

		if (fieldsInDB == null || fieldsInDB.size() == 0) {
			return;
		}

		for (SectionField dbField : fieldsInDB) {
			boolean deleted = true;

			if (sectionFieldsAtServer != null
					&& sectionFieldsAtServer.size() > 0) {
				for (SectionField serverField : sectionFieldsAtServer) {
					if (Utils.longsEqual(serverField.getLocalFormId(),
							localFormId)
							&& Utils.integersEqual(
									serverField.getSectionInstanceId(),
									dbField.getSectionInstanceId())
							&& Utils.longsEqual(dbField.getFieldSpecId(),
									serverField.getFieldSpecId())) {
						deleted = false;
						break;
					}
				}
			}

			if (deleted) {
				// delete that field record from local db alse
				sectionFieldsDao.deleteFields(localFormId,
						dbField.getFieldSpecId(),
						dbField.getSectionInstanceId());
			}
		}
	}

	public static String getImsi(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSubscriberId();
	}

	public static void saveImsiToDB(String imsi, Context context) {
		SettingsDao settingsDao = SettingsDao.getInstance(context);
		if (!TextUtils.isEmpty(imsi)) {
			settingsDao.saveSetting(Settings.KEY_IMSI, imsi);
		} else {
			settingsDao.saveSetting(Settings.KEY_IMSI, "");
		}
	}

	public static void detectImsiChangeAndSave(Context context, String from) {
		SettingsDao settingsDao = SettingsDao.getInstance(context
				.getApplicationContext());
		String previousImsi = settingsDao.getString(Settings.KEY_IMSI, "");
		String presentImsi = getImsi(context);

		// if the sim card is not inserted properly or if the sim inserted
		// without switch off then imsi is null
		if (TextUtils.isEmpty(presentImsi)) {
			presentImsi = "";
		}

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "from " + from);
			Log.d(TAG, "previous Imsi" + previousImsi + previousImsi.length());
			Log.d(TAG, "present Imsi" + presentImsi + presentImsi.length());

		}

		// if satisfied it means sim card changed
		if (!presentImsi.equalsIgnoreCase(previousImsi)) {
			// send sms- and avoid sending sms for the first time means when the
			// app installed
			if (!settingsDao.getBoolean(
					Settings.KEY_IS_IT_IMSI_FIRST_CONFIGURATION, true)) {
				if (BuildConfig.DEBUG) {
					Log.d(TAG,
							"sending sms to "
									+ settingsDao.getString(Settings.KEY_SMSC));
				}

				// there is a chance that sms sending may be failed so save in
				// db
				SimCardChangeSmsDao simCardChangeSmsDao = SimCardChangeSmsDao
						.getInstance(context);
				String receiver = settingsDao.getString(Settings.KEY_SMSC);
				String messageBody = "Your sim changed: previous imsi : "
						+ previousImsi + " present imsi : " + presentImsi;
				SimChangeMessage simChangeMessage = new SimChangeMessage(
						receiver, messageBody, "false");
				simCardChangeSmsDao.save(simChangeMessage);

				// construct pending intent and send sms
				processPendingSMS(simChangeMessage, context);
			} else {
				if (BuildConfig.DEBUG) {
					Log.d(TAG, "first time so not sending sms");
				}
			}
			settingsDao.saveSetting(
					Settings.KEY_IS_IT_IMSI_FIRST_CONFIGURATION, "false");
			saveImsiToDB(presentImsi, context);

		} else {
			if (BuildConfig.DEBUG) {
				Log.d(TAG, "imsi s equal");
			}
		}
	}

	public static void processPendingSMS(SimChangeMessage simChangeMessage,
			Context context) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "processPendingSMS " + simChangeMessage.getId());
		}

		Intent sentIntent = new Intent(context.getApplicationContext(),
				SMSTheftReceiver.class);
		sentIntent.setAction("delete message");
		sentIntent.setData(Uri.parse(simChangeMessage.getId() + ""));
		sentIntent.putExtra("message", simChangeMessage);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				context.getApplicationContext(), 0, sentIntent, 0);

		sendSms(simChangeMessage.getReceiverNumber(),
				simChangeMessage.getMessageBody(), pendingIntent);
	}

	public static void addWorkFlowStages(JSONArray source,
			List<WorkFlowStage> destination, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}
			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of WorkFlowStage: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				WorkFlowStage workFlowStage = WorkFlowStage.parse(
						source.getJSONObject(i), applicationContext);

				if (workFlowStage != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"WorkFlowStage " + i + ": "
										+ workFlowStage.toString());
					}

					destination.add(workFlowStage);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "WorkFlowSpec " + i + " is null.");
					}
				}
			}
		}
	}

	public static void addWorkFlows(JSONArray source,
			List<WorkFlowSpec> destination, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}
			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of WorkFlowSpec's: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				WorkFlowSpec workFlowSpec = WorkFlowSpec.parse(
						source.getJSONObject(i), applicationContext);

				if (workFlowSpec != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG,
								"WorkFlowSpec " + i + ": "
										+ workFlowSpec.toString());
					}

					destination.add(workFlowSpec);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "WorkFlowSpec " + i + " is null.");
					}
				}
			}
		}
	}

	public static void addWorkFlowStatus(JSONArray source,
			List<WorkFlowStatusDto> destination, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}
			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of WorkFlowStatus: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				WorkFlowStatusDto workFlowStatus = WorkFlowStatusDto.parse(
						source.getJSONObject(i), applicationContext);

				if (workFlowStatus != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "WorkFlowStatus " + i + ": "
								+ workFlowStatus.toString());
					}

					destination.add(workFlowStatus);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "WorkFlowStatus " + i + " is null.");
					}
				}
			}
		}
	}

	public static void addWorkFlowsHistory(JSONArray source,
			List<WorkFlowHistory> destination, Context applicationContext)
			throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}
			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of WorkFlowHistory: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				WorkFlowHistory workFlowHistory = WorkFlowHistory.parse(
						source.getJSONObject(i), applicationContext);

				if (workFlowHistory != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "WorkFlowHistory " + i + ": "
								+ workFlowHistory.toString());
					}

					destination.add(workFlowHistory);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "WorkFlowHistory " + i + " is null.");
					}
				}
			}
		}
	}

	/*
	 * Show just a simple notification without any options, which tells about
	 * workflow status
	 */
	public static void showWorkFlowsNotification(Context context, String message) {
		Intent intent = null;
		intent = new Intent(context, WorkFlowsActivity.class);
		intent.setAction("WORKFLOW_STATUS");
		intent.setData(Uri.parse(System.currentTimeMillis() + ""));
		intent.putExtra(WorkFlowsActivity.SCREEN_PURPOSE,
				WorkFlowsActivity.FORM_CREATE);
		intent.putExtra(WorkFlowsActivity.onBackToHomeKey, true);
		intent.putExtra("launchedFromNotification", true);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(context)
				.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
				.setContentText(message).setContentTitle("EFFORT")
				.setSmallIcon(R.drawable.ic_launcher_transparent)
				.setTicker(message).setContentIntent(pendingIntent).build();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(WORKFLOWS_NOTIFICATION, notification);
	}

	/*
	 * Show just a simple notification without any options, which tells about
	 * Artilces status
	 */
	public static void showArtclesNotification(Context context, String message) {
		Intent intent = null;
		intent = new Intent(context, ArticlesActivity.class);
		intent.setAction("ARTICLES");
		intent.setData(Uri.parse(System.currentTimeMillis() + ""));
		intent.putExtra("launchedFromNotification", true);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notification = new NotificationCompat.Builder(context)
				.setAutoCancel(true).setDefaults(Notification.DEFAULT_ALL)
				.setContentText(message).setContentTitle("EFFORT")
				.setSmallIcon(R.drawable.ic_launcher_transparent)
				.setTicker(message).setContentIntent(pendingIntent).build();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(ARTICLES_NOTIFICATION, notification);
	}

	public static boolean isRejected(Long currentRank, Long empRank,
			Long managerRank, Long empGroupId, Long stageType, Long status,
			Long empLongId) {
		if (stageType != null) {

			if (Utils.longsEqual(stageType,
					WorkFlowStatusDto.STAGE_TYPE_ROLE_BASED)) {
				if (Utils.longsEqual(currentRank, empRank)
						&& Utils.longsEqual(status,
								WorkFlowStatusDto.STATUS_REJECTED)) {
					return true;
				} else {
					return false;
				}
			} else if (Utils.longsEqual(stageType,
					WorkFlowStatusDto.STAGE_TYPE_GROUP_BASED)) {
				if (empGroupId != null && empGroupId == -1) {
					return true;
				} else {
					return false;
				}
			} else if (Utils.longsEqual(stageType,
					WorkFlowStatusDto.STAGE_TYPE_HIRARCHIAL_BASED)) {
				if (Utils.longsEqual(managerRank, empLongId)) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			if (Utils.longsEqual(currentRank, empRank)
					&& Utils.longsEqual(status,
							WorkFlowStatusDto.STATUS_REJECTED)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static void addWorkFlowsFormSpecsMappings(JSONArray source,
			List<WorkFlowFormSpecMapping> destination,
			Context applicationContext) throws JSONException, ParseException {
		if (source == null) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Source json array is null");
			}
			return;
		}

		int n = source.length();

		if (BuildConfig.DEBUG) {
			Log.i(TAG, "No. of WorkFlowSpecsMapping: " + n);
		}

		if (n > 0) {
			for (int i = 0; i < n; ++i) {
				WorkFlowFormSpecMapping workFlowFormSpecsMapping = WorkFlowFormSpecMapping
						.parse(source.getJSONObject(i), applicationContext);

				if (workFlowFormSpecsMapping != null) {
					if (BuildConfig.DEBUG) {
						Log.i(TAG, "WorkFlowSpec " + i + ": "
								+ workFlowFormSpecsMapping.toString());
					}

					destination.add(workFlowFormSpecsMapping);
				} else {
					if (BuildConfig.DEBUG) {
						Log.w(TAG, "WorkFlowSpec " + i + " is null.");
					}
				}
			}
		}
	}

	public static void setTimeouts(AbstractHttpMessage httpMessage) {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params,
				THREE_MINUTES_IN_MILLIS);
		HttpConnectionParams.setSoTimeout(params, THREE_MINUTES_IN_MILLIS);
		httpMessage.setParams(params);
	}

	public static void captureLocation(Context context, int purpose) {
		Intent intent = new Intent(context, LocationCaptureService.class);
		intent.putExtra(EffortProvider.Locations.PURPOSE, purpose);
		WakefulIntentService.sendWakefulWork(context, intent);
	}

	public static Cursor queryWithLocationsForFiles(String selection,
			String[] selectionArgs, String sortOrder,
			Context applicationContext, int purpose) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext)
				.getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

		if (purpose == Locations.PURPOSE_FORM_FILE) {
			builder.setTables(FormFiles.TABLE + " JOIN " + Locations.TABLE
					+ " ON " + FormFiles.TABLE + "." + FormFiles._ID + " = "
					+ Locations.TABLE + "." + Locations.FOR_ID + " AND "
					+ Locations.TABLE + "." + Locations.PURPOSE + " = "
					+ purpose);
		} else if (purpose == Locations.PURPOSE_SECTION_FILE) {
			builder.setTables(SectionFiles.TABLE + " JOIN " + Locations.TABLE
					+ " ON " + SectionFiles.TABLE + "." + SectionFiles._ID
					+ " = " + Locations.TABLE + "." + Locations.FOR_ID
					+ " AND " + Locations.TABLE + "." + Locations.PURPOSE
					+ " = " + purpose);
		}

		// qualify the columns by the table name, so that join
		// doesn't confuse the query engine with ambiguous column
		// names
		String[] columns = new String[Locations.ALL_COLUMNS.length];
		for (int i = 0; i < Locations.ALL_COLUMNS.length; ++i) {
			columns[i] = Locations.TABLE + "." + Locations.ALL_COLUMNS[i];
		}

		builder.setDistinct(true);
		return builder.query(db, columns, selection, selectionArgs, null, null,
				sortOrder);
	}

	public static void cleanupSecureFiles(Context applicationContext) {
		ArticlesDao articlesDao = ArticlesDao.getInstance(applicationContext);

		String path = Environment.getExternalStorageDirectory() + "/EFFORT/KB/";
		File dir = new File(path);

		if (!dir.exists()) {
			if (BuildConfig.DEBUG) {
				Log.i(TAG, "Creating directory: " + dir);
			}

			dir.mkdirs();
		}

		File[] listFiles = dir.listFiles();
		if (listFiles != null) {

			for (File inFile : listFiles) {
				if (inFile.isDirectory()) {
					// is directory
					inFile.delete();
				} else {
					if (inFile != null) {
						boolean isExists = articlesDao
								.pathExistsAtInSecureArticle(inFile
										.getAbsolutePath());
						if (!isExists) {
							inFile.delete();

						}
					}
				}
			}
		}
	}

	public static boolean isExists(String filePath) {
		if (!TextUtils.isEmpty(filePath)) {
			File f = new File(filePath);
			if (f.exists()) {
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param applicationContext
	 * @return
	 */
	public static int getVersionCode(Context applicationContext) {
		int versionCode = 0;

		try {
			versionCode = applicationContext.getPackageManager()
					.getPackageInfo(applicationContext.getPackageName(), 0).versionCode;

			if (BuildConfig.DEBUG) {
				Log.i(TAG, "App Version code: " + versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG,
					"Fatal error: unable to get version information: "
							+ e.toString());
		}

		return versionCode;
	}
}

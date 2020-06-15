package sooner.om.com.sooner.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//this helper class is used for adding "events of appointment" to default android calander.
public class Utility {
    public static ArrayList<String> nameOfEvent = new ArrayList<String>();
    public static ArrayList<String> descriptions = new ArrayList<String>();
    public static List<HelperClass> liEvents;

    static String startTime;

    static Context _context;

    @SuppressLint("SimpleDateFormat")
    public static ArrayList<String> readCalendarEvent(Context context, String startTime) {
        _context = context;
        Utility.startTime = startTime;
        Cursor cursor = context.getContentResolver()
                .query(Uri.parse("content://com.android.calendar/events"),
                        new String[]{"calendar_id", "title", "dtstart", "dtend"}, " deleted != 1",
                        null, null);

        cursor.moveToFirst();
        liEvents = new ArrayList<HelperClass>();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];

        // fetching calendars id
        descriptions.clear();
        // retrieving all the calender data
        for (int i = 0; i < CNames.length; i++) {

            String nameOfEvent = cursor.getString(1);
            String eventDate = getDate(Long.parseLong(cursor.getString(2)));
            String eventEnd = null + "";
            if (cursor.getString(3) != null)
                eventEnd = getDate(Long.parseLong(cursor.getString(3)));
            boolean sameDay = compareDates(returnDate(startTime), returnDate(getDate(Long.parseLong(cursor.getString(2)))));
            // if the event is in same date add to the list
            if (sameDay) {
                liEvents.add(new HelperClass(nameOfEvent, eventDate, eventEnd));
            }
            cursor.moveToNext();
        }
        return nameOfEvent;
    }

    //return the date in yyyy-MM-dd HH:mm:ss format
    @SuppressLint("SimpleDateFormat")
    public static Date returnDate(String dateStr) {
        //Log.v("return date",dateStr);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    // compare the dates and find out weather it is in same day or not
    public static boolean compareDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        boolean sameDay = false;
        //Log.v(date1+"",date2+"");
        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)) {
            sameDay = true;

        } else
            sameDay = false;
        return sameDay;
    }

    // concerts milli seconds to specified format
    @SuppressLint("SimpleDateFormat")
    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
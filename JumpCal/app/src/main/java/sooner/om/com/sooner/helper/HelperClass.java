package sooner.om.com.sooner.helper;

import android.util.Log;

import java.io.Serializable;

//this class is used to retrieve the data from json array.and store in lists.
//with this we are adding events to the default calander.
public class HelperClass implements Serializable {


    private static final long serialVersionUID = 1L;
    private static int EventName = 0;
    private static int EventDate = 1;
    private static int EventEndDate = 2;
    private String[] events;

    HelperClass(String name, String date, String endDate) {
        events = new String[3];
        events[HelperClass.EventName] = name;
        events[HelperClass.EventDate] = date;
        events[HelperClass.EventEndDate] = endDate;

        Log.v("saikrupa", events[HelperClass.EventName] + "/" + events[HelperClass.EventDate] + "/" + events[HelperClass.EventEndDate]);

    }

    public String getEventName() {
        return events[EventName];
    }

    public String getEventDate() {
        return events[EventDate];
    }

    public String getEventEndDate() {
        return events[EventEndDate];
    }


}

package sooner.om.com.sooner.helper;

import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Ndroid on 1/11/2017.
 */

public class RequestedAppointmentDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int DoctorName = 0;
    private static int Speciality = 1;
    private static int PreferedTime = 2;
    private static int Location = 3;
    private static int RequestedTime = 4;
    private String[] RequestedAppointmentsList;

    public RequestedAppointmentDetails(String[] RequestedAppointmentsList) {
        this.RequestedAppointmentsList = new String[RequestedAppointmentsList.length];
        this.RequestedAppointmentsList = RequestedAppointmentsList;
        //Log.v("strin", Arrays.toString(RequestedAppointmentsList));
    }
    public String getDoctorName() { return RequestedAppointmentsList[DoctorName]; }
    public String getSpeciality() {
        return RequestedAppointmentsList[Speciality];
    }
    public String getPreferedTime() {
        return RequestedAppointmentsList[PreferedTime];
    }
    public String getLocation() {
        return RequestedAppointmentsList[Location];
    }
    public String getRequestedTime() {
        return RequestedAppointmentsList[RequestedTime];
    }

}

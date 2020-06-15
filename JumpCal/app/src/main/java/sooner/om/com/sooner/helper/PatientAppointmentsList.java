package sooner.om.com.sooner.helper;

import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;

//this class is used in helping to retrieve the "appointment" data .
public class PatientAppointmentsList implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int AppointmentId = 0;
    private static int PatientName = 1;
    private static int Date = 2;
    private static int Time = 3;
    private static int PhysicanName = 4;
    private static int FacilityName = 5;
    private static int Location = 6;
    private static int Specialty = 7;
    private static int TimeDifference = 8;
    private static int ShowTimerBefore = 9;
    private static int timeGap = 10;
    private static int timeZone = 11;
    private String[] PatientAppointmentsList;

    public PatientAppointmentsList(String[] PatientAppointmentsList) {
        this.PatientAppointmentsList = new String[PatientAppointmentsList.length];
        this.PatientAppointmentsList = PatientAppointmentsList;
        Log.v("strin", Arrays.toString(PatientAppointmentsList));
    }

    public String getPatientName() {
        return PatientAppointmentsList[PatientName];
    }

    public String getDate() {
        return PatientAppointmentsList[Date];
    }

    public void setDate(String appointmentDate) {
        this.PatientAppointmentsList[Date] = appointmentDate;
    }

    public String getTime() {
        return PatientAppointmentsList[Time];
    }

    public void setTime(String appointmentTime) {
        this.PatientAppointmentsList[Time] = appointmentTime;
    }

    public String getPhysicanName() {
        return PatientAppointmentsList[PhysicanName];
    }

    public String getTimeGap() {
        return PatientAppointmentsList[timeGap];
    }

    public void setTimeGap(String time) {
        this.PatientAppointmentsList[timeGap] = time;
    }

    public String getTimeZone() {
        return PatientAppointmentsList[timeZone];
    }

    public String getFacilityName() {
        return PatientAppointmentsList[FacilityName];
    }


    public String getLocation() {
        return PatientAppointmentsList[Location];
    }

    public void setLocation(String locationName) {
        this.PatientAppointmentsList[Location] = locationName;
    }

    public String getSpecialty() {
        return PatientAppointmentsList[Specialty];
    }


    public String getTimeDifference() {
        return PatientAppointmentsList[TimeDifference];
    }


    public String getShowTimeBefore() {
        return PatientAppointmentsList[ShowTimerBefore];
    }


}

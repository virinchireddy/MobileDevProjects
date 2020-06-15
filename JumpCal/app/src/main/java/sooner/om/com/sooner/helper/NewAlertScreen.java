package sooner.om.com.sooner.helper;

import java.io.Serializable;

public class NewAlertScreen implements Serializable {


    private static final long serialVersionUID = 1L;
    private static int AppointmentId = 0;
    private static int PhysicanName = 1;
    private static int SpecialityName = 2;
    private static int Location = 3;
    private static int Date = 4;
    private static int Time = 5;
    private static int isRead = 6;
    private String[] AlertsList;

    public NewAlertScreen(String[] AlertsList) {
        this.AlertsList = new String[AlertsList.length];
        this.AlertsList = AlertsList;
    }

    public String getAppointmentId() {
        return AlertsList[AppointmentId];
    }


    public String getPhysicanName() {
        return AlertsList[PhysicanName];
    }


    public String getSpecialityName() {
        return AlertsList[SpecialityName];
    }


    public String getLocation() {
        return AlertsList[Location];
    }

    public void setLocation(String locationName) {
        this.AlertsList[Location] = locationName;
    }

    public String getDate() {
        return AlertsList[Date];
    }

    public void setDate(String appointmentDate) {
        this.AlertsList[Date] = appointmentDate;
    }

    public String getTime() {
        return AlertsList[Time];
    }

    public void setTime(String appointmentTime) {
        this.AlertsList[Time] = appointmentTime;
    }

    public String getReadStatus() {
        return AlertsList[isRead];
    }


}

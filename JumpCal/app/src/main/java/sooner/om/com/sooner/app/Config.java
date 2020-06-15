package sooner.om.com.sooner.app;

//this class cointains all the URL's which we use in this application
public class Config {
    public static final String GOOGLE_PROJECT_ID = "848114874420";
    public static int sdk = android.os.Build.VERSION.SDK_INT;
    // public static String baseUrl = "http://devapi.jumpcal.com//api/";
    private static String baseUrl = "https://api.sooner.io//api/";
    public static String URL_LOGIN = baseUrl + "login";
    public static String URL_FORGET_PASSWORD = baseUrl + "ForgotPassword?Email=";
    public static String URL_CHANGE_PASSWORD = baseUrl + "UpdatePassword";
    public static String URL_SEND_PROFILE_DETAILS = baseUrl + "Post_Profile";
    public static String URL_GET_PROFILE_DETAILS = baseUrl + "Get_Profile";
    public static String URL_GET_DROPDOWN_DETAILS = baseUrl + "Get_Request_Appointment";
    public static String URL_REQUEST_APPOINTMENT = baseUrl + "Add_Request_Appointment";
    public static String URL_LOGOUT = baseUrl + "Logout";
    public static String URL_GET_FACILITIES = baseUrl + "GetFacilities";
    public static String URL_LOAD_APPOINTMENTS = baseUrl + "Load_Appointments";
    public static String URL_SET_GCM = baseUrl + "Post_Device_Info";
    public static String URL_ACCEPT_APPOINTMENT = baseUrl + "Reschedule_Appointment";
    public static String URL_WHITE_SLOTS = baseUrl + "Get_WhiteSlots";
    public static String URL_SINGLE_WHITE_SLOTE_DETAILS = baseUrl + "Get_RecentValid_Appointment";
    public static String URL_POST_LOCATION = baseUrl + "post_locations";
    public static String URL_READ_APPT = baseUrl + "Read_Appointment";
    public static String URL_POST_INSURANCE_CARD = baseUrl + "Post_Profile_InsurenceCard";
    public static String URL_DELETE_INSURANCE_CARD = baseUrl + "delete_Profile_InsurenceCard";
    public static String URL_REQUESTED_APPOINTMENTS=baseUrl+"getRequestedAppointments";
}

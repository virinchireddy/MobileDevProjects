package com.om.virinchi.ricelake.Network;

//All the API's used in this project
public class Config {
    //to find the version of android os.
    public static int sdk = android.os.Build.VERSION.SDK_INT;
    //API's
    public  static  String URL_PROJECTS = "http://qa.ricelake.org:443/api/getprojectdetails ";
    public static String URL_UPDATES= "http://qa.ricelake.org:443/api/getprojectupdates";
    public static String URL_NEW_UPDATES= "http://qa.ricelake.org:443/api/updatecategorydetails";
    public static String URL_LOAD_COMMENTS="http://qa.ricelake.org:443/api/updatedcomments_detailsget ";
    public static String URL_UPDATE_COMMENTS="http://qa.ricelake.org:443/api/updatecommentsdetails";
    public static String URL_GETUPDATE_VALUES="http://qa.ricelake.org:443/api/updatecategory_detailsget";
    public static String URL_LOGIN = "http://qa.ricelake.org:443/api/Account/Login ";
    public static String URL_GET_PROFILE_DETAILS= "http://qa.ricelake.org:443/api/Account/ProfileInfo";
    public static String URL_Multiple_Images="http://qa.ricelake.org:443/api/upload_images";
    public static String URL_PROFILE_PIC = "http://qa.ricelake.org:443/api/Account/ProfileImage";
    public static String URL_UPDATE_PROFILE = "http://qa.ricelake.org:443/api/Account/ChangePassword";
    public static String URL_FORGOT_PASSWORD="http://qa.ricelake.org:443/api/Account/ForgotPassword";
    public static String URL_HISTORY = "http://qa.ricelake.org:443/api/getproject_history";
    public static String URL_GETPROJECT_IMAGES="http://qa.ricelake.org:443/api/upload_images_get";
    public static String URL_DELETE_IMAGES="http://qa.ricelake.org:443/api/upload_images_delete";

}

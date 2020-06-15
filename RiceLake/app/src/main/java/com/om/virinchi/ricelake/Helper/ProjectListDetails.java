package com.om.virinchi.ricelake.Helper;

/**
 * Created by Virinchi on 9/28/2016.
 */
public class ProjectListDetails {
    private static final long serialVersionUID = 1L;
    private static int projectName = 0;
    private static int jobNo = 1;
    private static int superIntendent = 2;
    private static int inspector = 3;
    private static int projectManager = 4;
    private static  int updates = 5 ;
    private static int photos = 6;
    private static int lastUpdated = 7;
    private static int remarks = 8;
    private static int projectId=9;
    private static int dateCreated = 10;
    private String[] projectsList;

    public ProjectListDetails() {

    }
    public ProjectListDetails(String[] projectsList) {
        this.projectsList = new String[projectsList.length];
        this.projectsList = projectsList;


    }
    public String getprojectName() {
        return projectsList[projectName];
    }

    public void setprojectName(String projectname) {
        this.projectsList[projectName] = projectname;
    }

    public String getjobNo() {
        return projectsList[jobNo];
    }

    public void setJobNo(String jobno) {
        this.projectsList[jobNo] = jobno;
    }

    public String getsuperIntendent() {
        return projectsList[superIntendent];
    }

    public void setSuperIntendent(String superintendent) {
        this.projectsList[superIntendent] = superintendent;
    }

    public String getinspector() {
        return projectsList[inspector];
    }

    public void setinspector(String Inspector) {
        this.projectsList[inspector] = Inspector;
    }

    public String getprojectManager() {
        return projectsList[projectManager];
    }

    public void setprojectManager(String Projectmanager) {
        this.projectsList[projectManager] = Projectmanager;
    }
    public String getupdates( ){
       return  projectsList[updates];
    }
    public void setUpdates(String Updates){
        this.projectsList[updates] = Updates;

    }
    public String getphotos( ){
        return  projectsList[photos];
    }
    public void setphotos(String Photos){
        this.projectsList[photos] = Photos;

    }
    public String getlastUpdate( ){
        return  projectsList[lastUpdated];
    }
    public void setLastUpdated(String LU){
        this.projectsList[lastUpdated] = LU;

    }
    public String getRemarks( ){
        return  projectsList[remarks];
    }
    public void setRemarks(String Remarks){
        this.projectsList[remarks] = Remarks;

    }
    public String getProjectId() {
        return projectsList[projectId];
    }

    public  void setProjectId(String projectid) {
        this.projectsList[projectId] = projectid;
    }
    //////
    public String getdatecreated(){return projectsList[dateCreated];}
    public void setdatecreated(String createddate){this.projectsList[dateCreated]= createddate;}
}

package com.om.virinchi.ricelake.Helper;

/**
 * Created by Virinchi on 10/21/2016.
 */
public class HistoryListDetails {
    private static final long serialVersionUID = 1L;
    private static int projectName = 0;
    private static int jobNo = 1;
    private static int superIntendent = 2;
    private static int inspector = 3;
    private static int projectManager = 4;
    private static int updates = 5;
    private static int photos = 6;
    private static int lastUpdated = 7;
    private static int remarks = 8;
    private static int projectId = 9;
    private static int dateCreated = 10;
    private String[] historyList;

    public HistoryListDetails() {

    }

    public HistoryListDetails(String[] projectsList) {
        this.historyList = new String[projectsList.length];
        this.historyList = projectsList;


    }

    public String getprojectName() {
        return historyList[projectName];
    }

    public void setprojectName(String projectname) {
        this.historyList[projectName] = projectname;
    }

    public String getjobNo() {
        return historyList[jobNo];
    }

    public void setJobNo(String jobno) {
        this.historyList[jobNo] = jobno;
    }

    public String getsuperIntendent() {
        return historyList[superIntendent];
    }

    public void setSuperIntendent(String superintendent) {
        this.historyList[superIntendent] = superintendent;
    }

    public String getinspector() {
        return historyList[inspector];
    }

    public void setinspector(String Inspector) {
        this.historyList[inspector] = Inspector;
    }

    public String getprojectManager() {
        return historyList[projectManager];
    }

    public void setprojectManager(String Projectmanager) {
        this.historyList[projectManager] = Projectmanager;
    }

    public String getupdates() {
        return historyList[updates];
    }

    public void setUpdates(String Updates) {
        this.historyList[updates] = Updates;

    }

    public String getphotos() {
        return historyList[photos];
    }

    public void setphotos(String Photos) {
        this.historyList[photos] = Photos;

    }

    public String getlastUpdate() {
        return historyList[lastUpdated];
    }

    public void setLastUpdated(String LU) {
        this.historyList[lastUpdated] = LU;

    }

    public String getRemarks() {
        return historyList[remarks];
    }

    public void setRemarks(String Remarks) {
        this.historyList[remarks] = Remarks;

    }

    public String getProjectId() {
        return historyList[projectId];
    }

    public void setProjectId(String projectid) {
        this.historyList[projectId] = projectid;
    }

    //////
    public String getdatecreated() {
        return historyList[dateCreated];
    }

    public void setdatecreated(String createddate) {
        this.historyList[dateCreated] = createddate;
    }
}


package com.om.virinchi.ricelake.Helper;

/**
 * Created by Virinchi on 10/3/2016.
 */
public class UpdatesListDetails {
    private static final long serialVersionUID = 1L;
    private static int projectId = 0;
    private static int createdDate = 1;
    private static int updateId = 2;
    private static int inspectorsName = 3;
    private static int categoriesCount = 4;
    private static int subcategoriesCount = 5;
    private static int okCount = 6;
    private static int actCount = 7;
    private static int naCount = 8;
    private static int comments = 9;
    private static int photos = 10;
    private static int lastupdated = 11;
    private static int blCount = 12;
    private static int projectName = 13;
    private static int history = 14;
    private static int jobno  = 15 ;

    private String[] updatesList;

    public UpdatesListDetails() {

    }

    public UpdatesListDetails(String[] updatesList) {
        this.updatesList = new String[updatesList.length];
        this.updatesList = updatesList;


    }

    public String getprojectId() {
        return updatesList[projectId];
    }

    public void setProjectId(String projectid) {
        this.updatesList[projectId] = projectid;
    }

    public String getcreatedDate() {
        return updatesList[createdDate];
    }

    public void setcreatedDate(String createdate) {
        this.updatesList[createdDate] = createdate;
    }

    public String getlastupdated() {
        return updatesList[lastupdated];
    }

    public void setlastupdated(String updateddate) {
        this.updatesList[lastupdated] = updateddate;
    }

    public String getupdateId() {
        return updatesList[updateId];
    }

    public void setupdateId(String updateid) {
        this.updatesList[updateId] = updateid;
    }

    public String getinspectorsName() {
        return updatesList[inspectorsName];
    }

    public void setinspectorsName(String inspectName) {
        this.updatesList[inspectorsName] = inspectName;
    }

    public String getcategoriesCount() {
        return updatesList[categoriesCount];
    }

    public void setcategoriesCount(String catCount) {
        this.updatesList[categoriesCount] = catCount;
    }

    public String getsubcategoriesCount() {
        return updatesList[subcategoriesCount];
    }

    public void setsubcategoriesCount(String subCount) {
        this.updatesList[subcategoriesCount] = subCount;
    }

    public String getokcount() {
        return updatesList[okCount];
    }

    public void setokcount(String okcount) {
        this.updatesList[okCount] = okcount;
    }

    public String getnacount() {
        return updatesList[naCount];
    }

    public void setnacount(String nacount) {
        this.updatesList[naCount] = nacount;
    }

    public String getactcount() {
        return updatesList[actCount];
    }

    public void setactcount(String actcount) {
        this.updatesList[actCount] = actcount;
    }

    public String getblcount() {
        return updatesList[blCount];
    }

    public void setblcount(String blcount) {
        this.updatesList[blCount] = blcount;
    }

    public String getcomments() {
        return updatesList[comments];
    }

    public void setcomments(String Comments) {
        this.updatesList[comments] = Comments;
    }

    public String getphotos() {
        return updatesList[photos];
    }

    public void setphotos(String Photos) {
        this.updatesList[photos] = Photos;
    }

    public String getProjectName() {
        return updatesList[projectName];
    }

    public void setProjectName(String projectname) {
        this.updatesList[projectName] = projectname;
    }

    public String getHistory() {
        return updatesList[history];
    }

    public void setHistory(String History) {
        this.updatesList[history] = History;
    }

    public String getJobno(){return updatesList[jobno]; }
    public void setJobno(String Jobno){this.updatesList[jobno] = Jobno ;}
}

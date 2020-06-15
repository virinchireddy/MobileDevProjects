package com.om.virinchi.ricelake.Helper;

/**
 * Created by Virinchi on 10/3/2016.
 */
public class CommentsListDetails {
    private static final long serialVersionUID = 1L;
    private static int refNum = 0;
    private static int commentsupdated_id = 1;
    private static int description = 2;
    private static int action = 3;
    private static int createdDate = 4;
    private static int update_id = 5;
    private static int projectName=6;
    private static int freeze=7;
    private static int history=8;
    private static int navigation=9;
    private static int NEW  = 10 ;
    private String[] commentsList;

    public CommentsListDetails() {

    }

    public CommentsListDetails(String[] commentsList) {
        this.commentsList = new String[commentsList.length];
        this.commentsList = commentsList;


    }


    public String getNavigation() {
        return commentsList[navigation];
    }

    public void setNavigation(String navigation1) {
        this.commentsList[navigation] = navigation1;
    }



    public String getHistory() {
        return commentsList[history];
    }

    public void setHistory(String history1) {
        this.commentsList[history] = history1;
    }

    public String getFreeze() {
        return commentsList[freeze];
    }

    public void setFreeze(String freeze1) {
        this.commentsList[freeze] = freeze1;
    }




    public String getrefno() {
        return commentsList[refNum];
    }

    public void setrefno(String refno) {
        this.commentsList[refNum] = refno;
    }

    public String getcommentsupdatedid() {
        return commentsList[commentsupdated_id];
    }

    public void setcommentsupdatedid(String c_up_id) {
        this.commentsList[commentsupdated_id] = c_up_id;
    }

    public String getdescription() {
        return commentsList[description];
    }

    public void setdescription(String des) {
        this.commentsList[description] = des;
    }

    public String getaction() {
        return commentsList[action];
    }

    public void setaction(String act) {
        this.commentsList[action] = act;
    }
    public String getcreateddate() {
        return commentsList[createdDate];
    }

    public void setcreateddate(String cdate) {
        this.commentsList[createdDate] = cdate;
    }
    public String getupdateid(){
        return commentsList[update_id];
    }
    public void setupdateid(String Updateid){
        this.commentsList[update_id]= Updateid;
    }
    public String getProjectName() {
        return commentsList[projectName];
    }

    public void setProjectName(String projectname) {
        this.commentsList[projectName] = projectname;
    }
    public String getNEW(){return commentsList[NEW] ;}
    public void setNEW(String nw){this.commentsList[NEW] = nw ;}
}

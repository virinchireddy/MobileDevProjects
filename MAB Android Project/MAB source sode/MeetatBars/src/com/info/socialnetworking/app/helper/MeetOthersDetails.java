package com.info.socialnetworking.app.helper;

import java.io.Serializable;

public class MeetOthersDetails implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String[] meetOthersDetails;
	private static int user_id=0;
	private static int user_name=1;
	private static int age=2;
	private static int distance=3;
	private static int city=4;
	private static int tagLine=5;
	private static int status=6;
	private static int user_profile=7;
	private static int last_active=8;
	private static int online_offline=9;
	
	//private static int 
	public MeetOthersDetails()
	{
		
	}
	public MeetOthersDetails(String [] meetOthers) {
		// TODO Auto-generated constructor stub
		meetOthersDetails=new String[meetOthers.length];
		this.meetOthersDetails=meetOthers;
	}
	public String getUserId() {
		return meetOthersDetails[user_id];
	}
	public void setUserId(String userIdString) {
		this.meetOthersDetails[user_id]=userIdString;
	}
	
	public String getUserName() {
		return meetOthersDetails[user_name];
	}
	public void setUserName(String userNameString) {
		this.meetOthersDetails[user_name]=userNameString;
	}
	public String getAge() {
		return meetOthersDetails[age];
	}
	public void setAge(String ageString) {
		this.meetOthersDetails[age]=ageString;
	}
	
	public String getDistance() {
		return meetOthersDetails[distance];
	}
	public void setDistance(String distanceString) {
		this.meetOthersDetails[distance]=distanceString;
	}
	
	public String getCityName() {
		return meetOthersDetails[city];
	}
	
	public void setCityName(String cityNameString) {
		this.meetOthersDetails[city]=cityNameString;
	}
	
	public String getTaglinee() {
		return meetOthersDetails[tagLine];
	}
	
	public void setTageLine(String tageLineString) {
		this.meetOthersDetails[tagLine]=tageLineString;
	}
	
	public String getStatus() {
		return meetOthersDetails[status];
	}
	
	public void setStatus(String statusString) {
		this.meetOthersDetails[status]=statusString;
	}
	
	public String getUserProfile() {
		return meetOthersDetails[user_profile];
	}
	
	public void setUserProfile(String userProfileString) {
		this.meetOthersDetails[user_profile]=userProfileString;
	}
	
	public String getLastActive() {
		return meetOthersDetails[last_active];
	}

	public void setLastActive(String lastActiveString) {
		this.meetOthersDetails[last_active]=lastActiveString;
	}
	
	public String getOnlineOffline() {
		return meetOthersDetails[online_offline];
	}
	
	public void setOnlineOffline(String onlineOfflineString) {
		this.meetOthersDetails[online_offline]=onlineOfflineString;
	}
	
	public int getUserLength() {
		
		return meetOthersDetails.length;
	}
	
}

package com.info.socialnetworking.app.helper;

import java.io.Serializable;

public class FriendsDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] friendsDetails;
	private static int user_id = 0;
	private static int user_name = 1;
	private static int age = 2;
	private static int distance = 3;
	private static int city = 4;
	private static int status = 5;
	private static int user_profile = 6;
	private static int online_offline = 7;

	// empty constructor
	public FriendsDetails() {
		// TODO Auto-generated constructor stub
	}

	// constructor with arguments 
	public FriendsDetails(String[] friendsDetails) {
		this.friendsDetails = new String[friendsDetails.length];
		this.friendsDetails = friendsDetails;
	}

	// getting and setting the data
	
	public String getUserId() {
		return friendsDetails[user_id];
	}

	public void setUserId(String userIdString) {
		this.friendsDetails[user_id] = userIdString;
	}

	public String getUserName() {
		return friendsDetails[user_name];
	}

	public void setUserName(String userNameString) {
		this.friendsDetails[user_name] = userNameString;
	}

	public String getAge() {
		return friendsDetails[age];
	}

	public void setAge(String ageString) {
		this.friendsDetails[age] = ageString;
	}

	public String getDistance() {
		return friendsDetails[distance];
	}

	public void setDistance(String distanceString) {
		this.friendsDetails[distance] = distanceString;
	}

	public String getCityName() {
		return friendsDetails[city];
	}

	public void setCityName(String cityNameString) {
		this.friendsDetails[city] = cityNameString;
	}

	public String getStatus() {
		return friendsDetails[status];
	}

	public void setStatus(String statusString) {
		this.friendsDetails[status] = statusString;
	}

	public String getUserProfile() {
		return friendsDetails[user_profile];
	}

	public void setUserProfile(String userProfileString) {
		this.friendsDetails[user_profile] = userProfileString;
	}

	public String getOnlineOffline() {
		return friendsDetails[online_offline];
	}

	public void setOnlineOffline(String onlineOfflineString) {
		this.friendsDetails[online_offline] = onlineOfflineString;
	}

	public int getUserLength() {

		return friendsDetails.length;
	}

}

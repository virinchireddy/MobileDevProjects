package com.info.socialnetworking.app.helper;

import java.io.Serializable;

public class NotificationDetails implements Serializable{

	private static final long serialVersionUID = 1L;

	private String[] notificationDetails;
	private static int user_id = 0;
	private static int user_name = 1;
	private static int user_image_url = 2;
	private static int message=3;
	private static int declineStatus=4;
	private static int notificationTime=5;

	
	public NotificationDetails() {
		// TODO Auto-generated constructor stub
	}

	public NotificationDetails(String[] notificationDetails) {
		this.notificationDetails = new String[notificationDetails.length];
		this.notificationDetails = notificationDetails;
	}
	
	public String getUserId() {
		return notificationDetails[user_id];
	}

	public void setUserId(String userIdString) {
		this.notificationDetails[user_id] = userIdString;
	}
	
	public String getUserName() {
		return notificationDetails[user_name];
	}

	public void setUserName(String userNameString) {
		this.notificationDetails[user_name] = userNameString;
	}
	
	public String getUserProfile() {
		return notificationDetails[user_image_url];
	}

	public void setUserProfile(String userProfileString) {
		this.notificationDetails[user_image_url] = userProfileString;
	}
	
	public String getMessage() {
		return notificationDetails[message];
	}

	public void setMessage(String message) {
		this.notificationDetails[NotificationDetails.message] = message;
	}
	
	public String getTime() {
		return notificationDetails[notificationTime];
	}

	public void setTime(String time) {
		this.notificationDetails[NotificationDetails.notificationTime] = time;
	}
	
	public String getDeclineStatus() {
		return notificationDetails[declineStatus];
	}

	public void setDeclineStatus(String declineStatus) {
		this.notificationDetails[NotificationDetails.declineStatus] = declineStatus;
	}
	
}

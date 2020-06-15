package com.info.socialnetworking.app.helper;

import java.io.Serializable;

public class BarMembersDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] barMembersDetails;
	private static int user_id = 0;
	private static int user_name = 1;
	private static int user_image = 2;
	private static int online_offline = 3;

	public BarMembersDetails(String[] barMembers) {
		// TODO Auto-generated constructor stub
		barMembersDetails = new String[barMembers.length];
		this.barMembersDetails = barMembers;
	}

	public String getUserId() {
		return barMembersDetails[user_id];
	}

	public void setUserId(String userIdString) {
		this.barMembersDetails[user_id] = userIdString;
	}

	public String getUserName() {
		return barMembersDetails[user_name];
	}

	public void setUserName(String userNameString) {
		this.barMembersDetails[user_name] = userNameString;
	}

	public String getUserImage() {
		return barMembersDetails[user_image];
	}

	public void setUserImage(String userImage) {
		this.barMembersDetails[user_image] = userImage;
	}

	public String getOnlineOffline() {
		return barMembersDetails[online_offline];
	}

	public void setOnlineOffline(String onlineOfflineStatus) {
		this.barMembersDetails[online_offline] = onlineOfflineStatus;
	}

}

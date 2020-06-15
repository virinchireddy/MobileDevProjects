package com.info.socialnetworking.app.helper;

import java.io.Serializable;

public class BlockListDetails implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String[] blockListDetails;
	private static int user_id = 0;
	private static int user_name = 1;
	private static int user_image_url = 2;
	private static int timeStamp=3;
	public BlockListDetails() {
		// TODO Auto-generated constructor stub
	}

	public BlockListDetails(String[] blockListDetails) {
		this.blockListDetails = new String[blockListDetails.length];
		this.blockListDetails = blockListDetails;
	}
	
	public String getUserId() {
		return blockListDetails[user_id];
	}

	public void setUserId(String userIdString) {
		this.blockListDetails[user_id] = userIdString;
	}
	
	public String getUserName() {
		return blockListDetails[user_name];
	}

	public void setUserName(String userNameString) {
		this.blockListDetails[user_name] = userNameString;
	}
	
	public String getUserProfile() {
		return blockListDetails[user_image_url];
	}

	public void setUserProfile(String userProfileString) {
		this.blockListDetails[user_image_url] = userProfileString;
	}
	
	public String getTimeStamp() {
		return blockListDetails[timeStamp];
	}

	public void setTimeStamp(String timeStamp) {
		this.blockListDetails[BlockListDetails.timeStamp] = timeStamp;
	}
}

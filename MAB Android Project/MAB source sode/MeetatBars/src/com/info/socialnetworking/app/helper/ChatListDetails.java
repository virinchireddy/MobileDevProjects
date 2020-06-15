package com.info.socialnetworking.app.helper;

import java.io.Serializable;
import java.util.Arrays;

import android.util.Log;

public class ChatListDetails implements Serializable  {

	private static final long serialVersionUID = 1L;
	
	private String[] chatList;
	private static int chatUserId=0;
	private static int chatUserName=1;
	private static int chatUserImage=2;
	private static int chatUserMessage=3;
	private static int chatUnReadMessages=4;
	
	public ChatListDetails() {
		// TODO Auto-generated constructor stub
	}
	public ChatListDetails(String[] chatList) {
		this.chatList = new String[chatList.length];
		this.chatList = chatList;
		Log.v("strin",Arrays.toString(chatList));

	}
	public String getChatUserId() {
		return chatList[chatUserId];
	}

	public void setChatUserId(String userId) {
		this.chatList[chatUserId] = userId;
	}

	public String getChatUserName() {
		return chatList[chatUserName];
	}

	public void setChatUserName(String userName) {
		this.chatList[chatUserName] = userName;
	}
	
	public String getChatUserImage() {
		return chatList[chatUserImage];
	}

	public void setChatUserImage(String userImage) {
		this.chatList[chatUserImage] = userImage;
	}
	
	public String getChatUserMessage() {
		return chatList[chatUserMessage];
	}
	
	public void setChatUserMessage(String userMessage) {
		this.chatList[chatUserMessage] = userMessage;
	}
	
	public String getChatUnreadMessages() {
		return chatList[chatUnReadMessages];
	}
	
	public void setChatUnreadMessages(String unReadMessages) {
		this.chatList[chatUnReadMessages] = unReadMessages;
	}
}

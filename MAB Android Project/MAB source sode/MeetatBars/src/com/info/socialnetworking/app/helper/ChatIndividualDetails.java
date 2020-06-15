package com.info.socialnetworking.app.helper;

import java.io.Serializable;

public class ChatIndividualDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] chatList;
	private static int is_sender=0;
	private static int chatMessage=1;
	private static int chatMessageTime=2;

	
	public ChatIndividualDetails() {
		// TODO Auto-generated constructor stub
	}
	public ChatIndividualDetails(String[] chatList) {
		this.chatList = new String[chatList.length];
		this.chatList = chatList;
	}
	
	public String getIsSender() {
		return chatList[is_sender];
	}

	public void setIsSender(String is_sender) {
		this.chatList[ChatIndividualDetails.is_sender] = is_sender;
	}
	
	public String getMessage() {
		return chatList[chatMessage];
	}

	public void setMessage(String message) {
		this.chatList[chatMessage] = message;
	}
	public String getMessageTime() {
		return chatList[chatMessageTime];
	}

	public void setMessageTime(String messageTime) {
		this.chatList[chatMessageTime] = messageTime;
	}
	
	
}

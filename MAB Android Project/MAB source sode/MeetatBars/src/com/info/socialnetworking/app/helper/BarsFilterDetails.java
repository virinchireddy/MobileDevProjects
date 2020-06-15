package com.info.socialnetworking.app.helper;

import java.io.Serializable;

public class BarsFilterDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private String quetion;
	private String question_tag;
	private boolean is_selected;
	
	public BarsFilterDetails() {
		// TODO Auto-generated constructor stub
	}
	public BarsFilterDetails(String question,String question_tag){
		this.quetion=question;
		this.is_selected=false;
		this.question_tag=question_tag;
	}
	public String getQuestion(){
		return quetion;
	}
	public void setQuestion(String question){
		this.quetion=question;
	}
	public String getQuestionTag(){
		return question_tag;
	}
	public void setQuestionTag(String questionTag){
		this.question_tag=questionTag;
	}
	
	public boolean getIsSelected(){
		return is_selected;
	}
	public void setIsSelected(boolean is_selected){
		this.is_selected=is_selected;
	}
	
	
}

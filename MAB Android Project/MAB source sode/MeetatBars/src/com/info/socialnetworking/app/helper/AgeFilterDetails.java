package com.info.socialnetworking.app.helper;

public class AgeFilterDetails {

	private boolean arrowSide = true;
	private static String question;
	private static int fromAge;
	private static int ToAge;
	
	
	public AgeFilterDetails(String question) {
		// TODO Auto-generated constructor stub
		AgeFilterDetails.question=question;
		fromAge=18;
		ToAge=18;
	}
	
	public int getFromAge() {
		return fromAge;
	}
	public int getToAge() {
		return ToAge;
	}

	public void setFromAge(int fromAge) {
		AgeFilterDetails.fromAge=fromAge;
	}
	public void setToAge(int toAge) {
		AgeFilterDetails.ToAge=toAge;
	}
	
	public String getQuestion(){
		return question;
	}
	public void setArrowStatus(boolean arrowSide) {
		this.arrowSide = arrowSide;
	}

	public boolean getArrowStatus() {
		return arrowSide;
	}
	
	
}

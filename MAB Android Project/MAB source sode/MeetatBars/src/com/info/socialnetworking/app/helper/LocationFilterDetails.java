package com.info.socialnetworking.app.helper;

public class LocationFilterDetails {

	private String[] questionsData;
	private boolean arrowSide = true;
	private static int city = 0;
	private static int state = 1;
	private static int distance = 2;
	
	public LocationFilterDetails() {
		// TODO Auto-generated constructor stub
		questionsData=new String[3];
	}
	
	public String getCity() {
		return questionsData[city];
	}

	public void setCity(String cityName) {
		questionsData[city]=cityName;
	}
	public String getState() {
		return questionsData[state];
	}

	public void setState(String stateName) {
		questionsData[state]=stateName;
	}
	public String getDistance() {
		return questionsData[distance];
	}

	public void setDistance(String distance) {
		questionsData[LocationFilterDetails.distance]=distance;
	}
	public void setArrowStatus(boolean arrowSide) {
		this.arrowSide = arrowSide;
	}

	public boolean getArrowStatus() {
		return arrowSide;
	}
}

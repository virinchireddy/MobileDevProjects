package com.info.socialnetworking.app.helper;

import java.io.Serializable;

public class BarsDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	
	private String[] barsDetails;
	private static int barId = 0;    
	private static int barName = 1;
	private static int barDistance = 2;
	private static int typeOfBar = 3;
	private static int barPhoto = 4;
	private static int barCheckInCount = 5;
	private static int facilityCount=6;
	private static int userImage1 = 7;
	private static int userImage2 = 8;
	private static int userImage3 = 9;
	private static int userImage4 = 10;
	private static int userImage1Id = 11;
	private static int userImage2Id = 12;
	private static int userImage3Id = 13;
	private static int userImage4Id = 14;
	private static int facilityImage1=15;
	private static int facilityImage2=16;
	private static int facilityImage3=17;
	private static int facilityImage4=18;

	// private static int

	public BarsDetails() {
		// TODO Auto-generated constructor stub

	}

	public BarsDetails(String[] bars) {
		barsDetails = new String[bars.length];
		this.barsDetails = bars;
		
		
	}
	public String getBarId() {
		return barsDetails[barId];
	}

	public void setBarId(String barIdString) {
		this.barsDetails[barId] = barIdString;
	}

	public String getBarName() {
		return barsDetails[barName];
	}

	public void setBarName(String barNameString) {
		this.barsDetails[barName] = barNameString;
	}

	public String getBarDistance() {
		return barsDetails[barDistance];
	}

	public void setBarDistance(String barDistanceString) {

		this.barsDetails[barDistance] = barDistanceString;
	}

	public String getTypeOfBar() {
		return barsDetails[typeOfBar];
	}

	public void setTypeOfBar(String typeOfBarString) {

		this.barsDetails[typeOfBar] = typeOfBarString;
	}

	public String getBarOnlineMembers() {
		return barsDetails[barCheckInCount];
	}

	public void setBarOnlineMembers(String barOnlineMembers) {
		this.barsDetails[barCheckInCount] = barOnlineMembers;
	}

	public String getBarPhoto() {
		return barsDetails[barPhoto];
	}

	public void setBarPhoto(String barPhoto) {
		this.barsDetails[BarsDetails.barPhoto] = barPhoto;
	}

	public String getUserImage1() {
		return barsDetails[userImage1];
	}

	public void setUserImage1(String userImage1) {
		this.barsDetails[BarsDetails.userImage1] = userImage1;
	}

	public String getUserImage2() {
		return barsDetails[BarsDetails.userImage2];
	}

	public String getUserImage1ID() {
		return barsDetails[BarsDetails.userImage1Id];
	}
	public String getUserImage2ID() {
		return barsDetails[BarsDetails.userImage2Id];
	}
	public String getUserImage3ID() {
		return barsDetails[BarsDetails.userImage3Id];
	}
	public String getUserImage4ID() {
		return barsDetails[BarsDetails.userImage4Id];
	}
	
	public void setUserImage2(String userImage2) {
		this.barsDetails[BarsDetails.userImage2] = userImage2;
	}

	public String getUserImage3() {
		return barsDetails[BarsDetails.userImage3];
	}

	public void setUserImage3(String userImage3) {
		this.barsDetails[BarsDetails.userImage3] = userImage3;
	}

	public String getUserImage4() {
		return barsDetails[BarsDetails.userImage4];
	}

	public void setUserImage4(String userImage4) {
		this.barsDetails[BarsDetails.userImage4] = userImage4;
	}

	public String getFacilityCount() {
		return barsDetails[BarsDetails.facilityCount];
	}

	public void setFacilityCount(String facilityCount) {
		this.barsDetails[BarsDetails.facilityCount] = facilityCount;
	}

	public String getFacilityImage1() {
		//Log.v("image1",barsDetails[BarsDetails.facilityImage1]);
		
		//Log.v("image1",facilityImage1+"");

		return barsDetails[BarsDetails.facilityImage1];
	}

	public void setFacilityImage1(String facilityImage1) {
		this.barsDetails[BarsDetails.facilityImage1] = facilityImage1;
	}

	public String getFacilityImage2() {
		//Log.v("image1",barsDetails[BarsDetails.facilityImage2]);

		return barsDetails[BarsDetails.facilityImage2];
	}

	public void setFacilityImage2(String facilityImage2) {

		this.barsDetails[BarsDetails.facilityImage2] = facilityImage2;
	}

	public String getFacilityImage3() {
		//Log.v("image1",barsDetails[BarsDetails.facilityImage3]);

		return barsDetails[BarsDetails.facilityImage3];
	}

	public void setFacilityImage3(String facilityImage3) {
		
		this.barsDetails[BarsDetails.facilityImage3] = facilityImage3;
	}

	public String getFacilityImage4() {
		//Log.v("image1",barsDetails[BarsDetails.facilityImage4]);

		return barsDetails[BarsDetails.facilityImage4];
	}

	public void setFacilityImage4(String facilityImage4) {
		this.barsDetails[BarsDetails.facilityImage4] = facilityImage4;
	}

}

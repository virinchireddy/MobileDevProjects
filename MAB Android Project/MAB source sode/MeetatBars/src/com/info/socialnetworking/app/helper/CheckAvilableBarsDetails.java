package com.info.socialnetworking.app.helper;

import java.io.Serializable;

public class CheckAvilableBarsDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] AvilableBarsDetails;
	private static int bar_id = 0;
	private static int bar_name = 1;
	private static int bar_address = 2;
	private static int owner_id = 3;
	private static int bar_image=4;

	public CheckAvilableBarsDetails(String[] avilableBars) {
		// TODO Auto-generated constructor stub
		AvilableBarsDetails = new String[avilableBars.length];
		this.AvilableBarsDetails = avilableBars;
	}
	
	public CheckAvilableBarsDetails() {
		// TODO Auto-generated constructor stub
	}
	
	public String getBarID() {
		return AvilableBarsDetails[bar_id];
	}
	
	public String getBarName() {

		return AvilableBarsDetails[bar_name];

	}
	public String getBarAddress() {

		return AvilableBarsDetails[bar_address];

	}
	public String getOwnerId() {

		return AvilableBarsDetails[owner_id];

	}
	
	public String getBarImage() {

		return AvilableBarsDetails[bar_image];

	}

}

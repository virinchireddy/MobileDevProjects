package com.info.socialnetworking.app.helper;

public class HeightFilterHelper {

	private boolean arrowSide = true;
	private static String question;
	private static int selected_height_ft;
	private static int selected_height_in;
	private static int toSelected_height_ft;
	private static int toSelected_height_in;
	
	
	
	
	public HeightFilterHelper(String question) {
		// TODO Auto-generated constructor stub
		HeightFilterHelper.question=question;
		selected_height_ft=3;
		selected_height_in=0;
		toSelected_height_ft=3;
		toSelected_height_in=0;
	}

	public int getHeightFt() {
		return selected_height_ft;
	}
	public int getHeightIn() {
		return selected_height_in;
	}

	public void setHeightFt(int height_ft) {
		HeightFilterHelper.selected_height_ft=height_ft;
	}
	public void setHeightIn(int height_in) {
		HeightFilterHelper.selected_height_in=height_in;
	}
	
	public int toGetHeightFt() {
		return toSelected_height_ft;
	}
	public int toGetHeightIn() {
		return toSelected_height_in;
	}

	public void toSetHeightFt(int height_ft) {
		HeightFilterHelper.toSelected_height_ft=height_ft;
	}
	public void toSetHeightIn(int height_in) {
		HeightFilterHelper.toSelected_height_in=height_in;
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

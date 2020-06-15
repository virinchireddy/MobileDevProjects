package com.info.socialnetworking.app.helper;

import java.io.Serializable;

public class PromotionsDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	private String[] promotionsDetails;
	private static int bar_id = 0;
	private static int bar_name = 1;
	private static int bar_city = 2;
	private static int promotion_title = 3;
	private static int promotion_message = 4;
	private static int promotion_image = 5;
	private static int promotion_id=6;
	private static int promotion_type=7;

	public PromotionsDetails() {
		// TODO Auto-generated constructor stub
	}

	public PromotionsDetails(String[] promotionDetails) {
		this.promotionsDetails = new String[promotionDetails.length];
		this.promotionsDetails = promotionDetails;
	}

	public String getBarID() {
		return promotionsDetails[bar_id];
	}

	public void setBarID(String barIdString) {
		this.promotionsDetails[bar_id] = barIdString;
	}

	public String getBarName() {
		return promotionsDetails[bar_name];
	}

	public void setBarName(String barNameString) {
		this.promotionsDetails[bar_name] = barNameString;
	}

	public String getBarCity() {
		return promotionsDetails[bar_city];
	}

	public void setBarCity(String barCityString) {
		this.promotionsDetails[bar_city] = barCityString;
	}

	public String getPromotionsTitle() {
		return promotionsDetails[promotion_title];
	}

	public void setPromotionsTitle(String PromotionsTitle) {
		this.promotionsDetails[PromotionsDetails.promotion_title] = PromotionsTitle;
	}

	public String getPromotionsMessage() {
		return promotionsDetails[promotion_message];
	}

	public void setPromotionsMessage(String promotionsMessage) {
		this.promotionsDetails[PromotionsDetails.promotion_message] = promotionsMessage;
	}


	public String getPromotionsImage() {
		return promotionsDetails[promotion_image];
	}

	public void setPromotionsImage(String imageUrl) {
		this.promotionsDetails[PromotionsDetails.promotion_image] = imageUrl;
	}
	
	public String getPromotionsId() {
		return promotionsDetails[promotion_id];
	}

	public void setPromotionsId(String id) {
		this.promotionsDetails[PromotionsDetails.promotion_id] = id;
	}
	
	public String getPromotionsType() {
		return promotionsDetails[promotion_type];
	}

	public void setPromotionsType(String type) {
		this.promotionsDetails[PromotionsDetails.promotion_type] = type;
	}
	

}

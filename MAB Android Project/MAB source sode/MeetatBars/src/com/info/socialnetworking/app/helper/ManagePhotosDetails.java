package com.info.socialnetworking.app.helper;

public class ManagePhotosDetails {

    private String image;
    private String isBlocked;


    public ManagePhotosDetails(String imageUrl,String is_blocked) {
		// TODO Auto-generated constructor stub
    	this.image=imageUrl;
    	this.isBlocked=is_blocked;
	}
    
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
	
    public String getIsBlocked() {
        return isBlocked;
    }

    public void setIsBlocked(String isBlocked) {
        this.isBlocked = isBlocked;
    }
}

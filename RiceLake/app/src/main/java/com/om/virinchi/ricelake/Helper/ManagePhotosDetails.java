package com.om.virinchi.ricelake.Helper;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by Ndroid on 9/1/2016.
 */
public class ManagePhotosDetails {
    private Bitmap image;
    private String type;
    private String imageurl;
    private String updateId;
    private String history;
    private String freeze;
    private String nw;

    public ManagePhotosDetails(Bitmap image, String type, String updateId, String history, String freeze, String nw) {
        // TODO Auto-generated constructor stub
        this.image = image;
        this.type = type;
        this.updateId = updateId;
        this.history = history;
        this.freeze = freeze;
        this.nw = nw;
    }

    public ManagePhotosDetails(String imageurl, String type, String updateId, String history, String freeze, String nw) {
        // TODO Auto-generated constructor stub
        this.imageurl = imageurl;
        this.type = type;
        this.updateId = updateId;
        this.history = history;
        this.freeze = freeze;
        this.nw = nw;
    }

    /*  public ManagePhotosDetails(Bitmap imageUrl) {
          // TODO Auto-generated constructor stub
          this.image=imageUrl;
      }*/


    public String getUpdateId() {

        return updateId;
    }

    public void setUpdateId(String image) {
        this.updateId = image;
    }


    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }


    public String getFreeze() {
        return freeze;
    }

    public void setFreeze(String freeze) {
        this.freeze = freeze;
    }

    public String getNew() {
        return nw;
    }

    public void setNew(String NEW) {
        this.nw = NEW;
    }
}


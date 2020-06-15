package sooner.om.com.sooner.helper;

import android.util.Log;

import java.io.Serializable;
import java.util.Arrays;

public class FacilityListDetails implements Serializable {

    private static final long serialVersionUID = 1L;
    private String[] facilityList;

   /* public FacilityListDetails() {
        // TODO Auto-generated constructor stub
    }*/

    public FacilityListDetails(String[] facilityList) {
        this.facilityList = new String[facilityList.length];
        this.facilityList = facilityList;
        Log.v("strin", Arrays.toString(facilityList));

    }

    public String getfacilityId() {
        int facilityId = 0;
        return facilityList[facilityId];
    }

    public String getfacilityName() {
        int facilityName = 1;
        return facilityList[facilityName];
    }


}

package sooner.om.com.sooner.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import sooner.om.com.sooner.R;
import sooner.om.com.sooner.helper.InsuranceCard;
import sooner.om.com.sooner.helper.Others;


 //Created by SaiKrupa on 10/26/2016.


// this is the class which handles the insurance cards displaying in profile screen
public class InsuranceCardUpload extends PagerAdapter {

    // it will store the list of insurance card url's

    private Activity _activity;
    private List<InsuranceCard> liImagePaths;
    private LayoutInflater inflater;

    // constructor with images and activity reference
    public InsuranceCardUpload(Activity activity, List<InsuranceCard> images) {
        //referencing activity and image path to present class
        _activity = activity;
        liImagePaths = images;
        //Log.v("insurance card upload","am here");

    }

    // returns the size of the images list
    @Override
    public int getCount() {
        return liImagePaths.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // local variable to store the image
        ImageView imgDisplay;
        // inflating the ui to the code
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.insurance_card_item, container,
                false);

        imgDisplay = (ImageView) viewLayout.findViewById(R.id.ivInsuranceCard);
        imgDisplay.setImageDrawable(_activity.getResources().getDrawable(R.drawable.insurance_card_frame));
        Log.v("insurance card upload", liImagePaths.get(position).getImageUrl());
        // loading the image with url using volley library
        Others.loadImages(liImagePaths.get(position).getImageUrl(), imgDisplay);

        // displaying the 0 the position image on the screen
        container.addView(viewLayout, 0);

        return viewLayout;
    }

    // removing the view after the usage
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}

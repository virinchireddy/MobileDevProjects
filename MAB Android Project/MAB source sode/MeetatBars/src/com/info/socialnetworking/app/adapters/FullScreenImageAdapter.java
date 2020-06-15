package com.info.socialnetworking.app.adapters;


import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.info.socialnetworking.app.helper.ManagePhotosDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.helper.TouchImageView;
import com.info.socialnetworking.app.meetatbars.R;

public class FullScreenImageAdapter extends PagerAdapter{

	private Activity _activity;
	Context _context;
	private ArrayList<ManagePhotosDetails> _imagePaths;
	private LayoutInflater inflater;
	Others others;
	
	// constructor
	public FullScreenImageAdapter(Activity activity,
			ArrayList<ManagePhotosDetails> imagePaths) {
		this._activity = activity;
		_context=activity.getApplicationContext();
		this._imagePaths = imagePaths;
	}

	
	@Override
	public int getCount() {
		return this._imagePaths.size();
	}

	@Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
	
	@SuppressWarnings("deprecation")
	@Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView imgDisplay;
        ImageView ivBlocked1;
        others=new Others(_context);
        
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.fragment_touch_image_view, container,
                false);
 
        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        ivBlocked1=(ImageView)viewLayout.findViewById(R.id.ivBlocked1);
        
        if(_imagePaths.get(position).getIsBlocked().equals("1")){
        	
        	ivBlocked1.setVisibility(View.VISIBLE);
         	ivBlocked1.setBackgroundDrawable(_context.getResources().getDrawable(R.drawable.trans_blocked_red));
        	Others.loadImages(_imagePaths.get(position).getImage(), imgDisplay);
        	
        }else{
        	ivBlocked1.setVisibility(View.GONE);
        	Others.loadImages(_imagePaths.get(position).getImage(), imgDisplay);
        }
        
        
        
       
        
        
        
        
        
        // close button click event

        ((ViewPager) container).addView(viewLayout);
 
        return viewLayout;
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }

}

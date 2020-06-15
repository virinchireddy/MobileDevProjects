package com.info.socialnetworking.app.adapters;

import java.util.List;

import com.info.socialnetworking.app.helper.FriendsDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.R;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsAdapter extends BaseAdapter{

	List<FriendsDetails> friendsList;
	Activity activity;
	
	public FriendsAdapter(List<FriendsDetails> friendsList,Activity activity) {
		// TODO Auto-generated constructor stub
		this.friendsList=friendsList;
		this.activity=activity;
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return friendsList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return friendsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder view;
        LayoutInflater inflator = activity.getLayoutInflater();
        FriendsDetails singleFriend=friendsList.get(position);
        
        // create the view if not exist 
        if(convertView==null)
        {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.friends_element, null);
            
            // linking layout id's to java id's
            view.ivUserImage = (ImageView) convertView.findViewById(R.id.ivUserImage);
            view.ivOnlineOffline = (ImageView) convertView.findViewById(R.id.ivOnlineOffline);
            view.tvName=(TextView)convertView.findViewById(R.id.tvName);
            view.tvAge=(TextView)convertView.findViewById(R.id.tvAge);
            view.tvDistance=(TextView)convertView.findViewById(R.id.tvDistance);
            view.tvCity=(TextView)convertView.findViewById(R.id.tvCity);
            view.tvStatus=(TextView)convertView.findViewById(R.id.tvStatus);
 
            convertView.setTag(view);
        }
        // reuse the view if it is already created
        else
        {
            view = (ViewHolder) convertView.getTag();
        }
        
        // assigning the values to the layout dynamically 
        view.tvAge.setText(singleFriend.getAge());
        view.tvCity.setText(singleFriend.getCityName());
        view.tvDistance.setText(singleFriend.getDistance());
        view.tvName.setText(singleFriend.getUserName());
        view.tvStatus.setText(singleFriend.getStatus());
        // using setBackgroundDrawable because app should support below api level 16
        view.ivUserImage.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.image_loading));
        
        Others.setImageAsBackground(singleFriend.getUserProfile(),view.ivUserImage);
        // checking user online or offline condition 1 is online 0 is offline 
        Log.v("online_offline",singleFriend.getOnlineOffline() );
        if(singleFriend.getOnlineOffline().equals("1"))
            view.ivOnlineOffline.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.online_icon));
        else if(singleFriend.getOnlineOffline().equals("0"))
        	view.ivOnlineOffline.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.offline_icon));
        return convertView;
	}
	
	// reference view holder calss
    public static class ViewHolder
    {
        public ImageView ivUserImage;
        public ImageView ivOnlineOffline;
        public TextView tvName;
        public TextView tvAge;
        public TextView tvDistance;
        public TextView tvCity;
        public TextView tvStatus;
    }
	
}

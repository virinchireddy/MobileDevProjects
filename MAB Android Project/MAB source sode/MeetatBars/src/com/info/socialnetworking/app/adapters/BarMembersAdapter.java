package com.info.socialnetworking.app.adapters;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.info.socialnetworking.app.helper.BarMembersDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.R;

public class BarMembersAdapter extends BaseAdapter{
	
	List<BarMembersDetails> barMembersList;
	Activity activity;
	
	
	public BarMembersAdapter(List<BarMembersDetails> barMembersList,Activity activity) {
		// TODO Auto-generated constructor stub
		this.barMembersList=barMembersList;
		this.activity=activity;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return barMembersList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return barMembersList.get(position);
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
        BarMembersDetails singleMember=barMembersList.get(position);
        
        if(convertView==null)
        {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.bar_member_item, null);
 
            view.ivUserImage = (ImageView) convertView.findViewById(R.id.ivUserImage);
            view.ivOnlineOffline = (ImageView) convertView.findViewById(R.id.ivOnlineOffline);
            view.tvMemberName=(TextView)convertView.findViewById(R.id.tvMemberName);
            
            convertView.setTag(view);
        }
        else
        {
            view = (ViewHolder) convertView.getTag();
        }
 
        view.tvMemberName.setText(singleMember.getUserName());
        view.ivUserImage.setBackgroundDrawable(activity.getResources().getDrawable(R.drawable.image_loading));
        Others.setImageAsBackground(singleMember.getUserImage(),view.ivUserImage);
        
        return convertView;
	}
	
	
    public static class ViewHolder
    {
        public ImageView ivUserImage;
        public ImageView ivOnlineOffline;
        public TextView tvMemberName;
       
    }

}

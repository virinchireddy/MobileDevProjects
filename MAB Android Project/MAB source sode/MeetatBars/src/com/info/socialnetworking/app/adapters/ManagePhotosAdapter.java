package com.info.socialnetworking.app.adapters;

import java.util.ArrayList;

import javax.crypto.spec.IvParameterSpec;

import com.info.socialnetworking.app.helper.ManagePhotosDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.meetatbars.R;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class ManagePhotosAdapter extends ArrayAdapter<ManagePhotosDetails> {

	Activity activity;
	private int layoutResourceId;
	Others others;
	private ArrayList<ManagePhotosDetails> mGridData = new ArrayList<ManagePhotosDetails>();

	// String navigation;

	public ManagePhotosAdapter(Activity activity, int layoutResourceId,
			ArrayList<ManagePhotosDetails> mGridData) {
		super(activity, layoutResourceId, mGridData);
		this.layoutResourceId = layoutResourceId;
		this.activity = activity;
		this.mGridData = mGridData;
		// this.navigation=navigation;
		others = new Others(activity);
	}

	public void setGridData(ArrayList<ManagePhotosDetails> mGridData) {
		this.mGridData = mGridData;
		notifyDataSetChanged();
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder;

		if (row == null) {
			LayoutInflater inflater;
			inflater = activity.getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();
			holder.imageView = (ImageView) row
					.findViewById(R.id.grid_item_image);
			holder.ivIsBlocked = (ImageView) row.findViewById(R.id.ivBlocked);
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}

		ManagePhotosDetails item = mGridData.get(position);

		//Log.v(position + "", mGridData.get(position).getImage());

		if (item.getIsBlocked().equals("1")) {

			holder.ivIsBlocked.setVisibility(View.VISIBLE);
			holder.imageView.setVisibility(View.VISIBLE);
			Log.v("hello", "blocked");
			Others.setImageAsBackground(item.getImage(), (holder).imageView);
			(holder).ivIsBlocked.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.trans_blocked_red));

		} else {
			holder.ivIsBlocked.setVisibility(View.GONE);
			Log.v("hello2", "not blocked");
			holder.imageView.setVisibility(View.VISIBLE);

			(holder).imageView.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.image_loading));
			Others.setImageAsBackground(item.getImage(), holder.imageView);
		}

		return row;
	}

	static class ViewHolder {
		ImageView imageView;
		ImageView ivIsBlocked;
	}

}

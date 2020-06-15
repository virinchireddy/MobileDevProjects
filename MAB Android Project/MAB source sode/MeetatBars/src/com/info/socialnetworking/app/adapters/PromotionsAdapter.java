package com.info.socialnetworking.app.adapters;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.info.socialnetworking.app.Fragment.FragmentBarsProfileFb;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.helper.PromotionsDetails;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.R;

@SuppressWarnings("rawtypes")
public class PromotionsAdapter extends RecyclerView.Adapter {

	private List<PromotionsDetails> promotionsList;

	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private boolean loading;
	private OnLoadMoreListener onLoadMoreListener;
	static Activity _context;

	public PromotionsAdapter(List<PromotionsDetails> promotions,
			RecyclerView recyclerView, Activity context) {
		this.promotionsList = promotions;
		_context = context;
		if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

			final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
					.getLayoutManager();
			recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
						@Override
						public void onScrollStateChanged(int arg0) {
							// TODO Auto-generated method stub
						}

						@Override
						public void onScrolled(int arg0, int arg1) {
							// TODO Auto-generated method stub
							totalItemCount = linearLayoutManager.getItemCount();
							lastVisibleItem = linearLayoutManager
									.findLastVisibleItemPosition();
							if (!loading
									&& totalItemCount <= (lastVisibleItem + visibleThreshold)) {
								// End has been reached
								// Do something
								if (onLoadMoreListener != null) {
									onLoadMoreListener.onLoadMore();
								}
								loading = true;
							}}});
		}
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return promotionsList.size();
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("DefaultLocale") @SuppressWarnings("deprecation")
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		if (holder instanceof PromotionsViewHolder) {

			PromotionsDetails singlePromotionDetails = (PromotionsDetails) promotionsList.get(position);
			PromotionsViewHolder viewHolder=(PromotionsViewHolder)holder;
			//Log.v("userName",singlePromotionDetails.getUserName());
			
			viewHolder.tvBarName.setText(singlePromotionDetails.getBarName());
			viewHolder.tvLocation.setText(", "+singlePromotionDetails.getBarCity());
			
			//error rectified  
			
			viewHolder.tvPromotionTitle.setText(Others.firstLetterCapital(singlePromotionDetails.getPromotionsTitle()));
		//	viewHolder.tvPromotionMessage.setText(Others.firstLetterCapital(singlePromotionDetails.getPromotionsMessage()));
			
			
			
			//viewHolder.tvPromotionMessage.setText(Others.removeParagraphSymbols(singlePromotionDetails.getPromotionsMessage()));
			viewHolder.tvPromotionMessage.setText(Html.fromHtml(singlePromotionDetails.getPromotionsMessage()));
			
			
			
			viewHolder.ivPromotionsImage.setBackgroundDrawable(_context.getResources().getDrawable(R.drawable.image_loading));
			Others.setImageAsBackground(singlePromotionDetails.getPromotionsImage(),((PromotionsViewHolder) holder).ivPromotionsImage);
			viewHolder.promotionsDetails=singlePromotionDetails;
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder vh;

		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promotions_element, parent, false);

		vh = new PromotionsViewHolder(v);
		return vh;

	}

	public static class PromotionsViewHolder extends RecyclerView.ViewHolder {

		TextView tvBarName,tvLocation,tvPromotionTitle;
		TextView tvPromotionMessage;
		ImageView ivPromotionsImage;
		
		public PromotionsDetails promotionsDetails;
		
		public PromotionsViewHolder(View v) {
			super(v);
			// TODO Auto-generated constructor stub
			tvBarName=(TextView)v.findViewById(R.id.tvBarName);
			tvPromotionMessage=(TextView)v.findViewById(R.id.tvPromotionMessage);
			tvLocation=(TextView)v.findViewById(R.id.tvLocation);
			tvPromotionTitle=(TextView)v.findViewById(R.id.tvPromotionTitle);
			ivPromotionsImage=(ImageView)v.findViewById(R.id.ivPromotionsImage);
			v.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Others.navigation="promotions";
					Bundle bundle = new Bundle();
					bundle.putString("bar_id", promotionsDetails.getBarID());
					bundle.putString("barProfileNavigation", "homeScreen");
					bundle.putString("promotion_id", promotionsDetails.getPromotionsId());
					bundle.putString("promotion_type", promotionsDetails.getPromotionsType());
					FragmentBarsProfileFb bars = new FragmentBarsProfileFb();
					bars.setArguments(bundle);
					_context.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, bars)
					.commit();
					//Toast.makeText(_context, "under developement", Toast.LENGTH_SHORT).show();
					
				}
			});
			
			
		}
	}

	
}

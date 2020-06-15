package com.info.socialnetworking.app.adapters;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.info.socialnetworking.app.Fragment.FragmentBarsProfileFb;
import com.info.socialnetworking.app.Fragment.FragmentUserProfile;
import com.info.socialnetworking.app.helper.BarsDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.R;

@SuppressWarnings("rawtypes")
public class BarsAdapter extends RecyclerView.Adapter {

	private final int VIEW_ITEM = 1;
	private final int VIEW_PROG = 0;

	private List<BarsDetails> barsList;

	// The minimum amount of items to have below your current scroll position
	// before loading more.
	private int visibleThreshold = 5;

	private int lastVisibleItem, totalItemCount;
	private boolean loading;
	private OnLoadMoreListener onLoadMoreListener;
	static Activity _context;

	public BarsAdapter(List<BarsDetails> bars, RecyclerView recyclerView,
			Activity context) {
		// TODO Auto-generated constructor stub
		barsList = bars;
		BarsAdapter._context = context;
		if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

			final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
					.getLayoutManager();

			recyclerView
					.setOnScrollListener(new RecyclerView.OnScrollListener() {

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
							}

						}
					});

		}
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return barsList.size();
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return barsList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		RecyclerView.ViewHolder vh;
		if (viewType == VIEW_ITEM) {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.bar_element_new, parent, false);

			vh = new BarsViewHolder(v);
		} else {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.progress_item, parent, false);

			vh = new ProgressViewHolder(v);
		}
		return vh;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		if (holder instanceof BarsViewHolder) {

			BarsDetails singleBarDetails = (BarsDetails) barsList.get(position);

			((BarsViewHolder) holder).tvBarName.setText(singleBarDetails
					.getBarName());
			((BarsViewHolder) holder).tvBarDistance.setText(singleBarDetails
					.getBarDistance() + "");
			((BarsViewHolder) holder).tvTypeOfBar.setText(singleBarDetails
					.getTypeOfBar());
			// ((BarsViewHolder)
			// holder).tvBarOnlineMembers.setText(singleBarDetails.getBarOnlineMembers());
			((BarsViewHolder) holder).ivBarPhoto.setBackgroundDrawable(_context
					.getResources().getDrawable(R.drawable.image_loading));

			Others.setImageAsBackground(singleBarDetails.getBarPhoto(),
					((BarsViewHolder) holder).ivBarPhoto);
			((BarsViewHolder) holder).tvBarOnlineMembers
					.setText(singleBarDetails.getBarOnlineMembers());
			int onlineMembers = Integer.parseInt(singleBarDetails
					.getBarOnlineMembers());
			// Log.v("onlineMembers", onlineMembers + "");
			if (onlineMembers > 0) {
				setDefaultImages((BarsViewHolder) holder);
				((BarsViewHolder) holder).liOnlineMembers
						.setVisibility(View.VISIBLE);
				((BarsViewHolder) holder).liUserImages
						.setVisibility(View.VISIBLE);
				if (onlineMembers > 4)
					onlineMembers = 4;
				switch (onlineMembers) {
				case 1:
					Others.loadImages(singleBarDetails.getUserImage1(),
							((BarsViewHolder) holder).ivUserImage1);
					((BarsViewHolder) holder).ivUserImage1
							.setVisibility(View.VISIBLE);
					break;
				case 2:
					Others.loadImages(singleBarDetails.getUserImage1(),
							((BarsViewHolder) holder).ivUserImage1);
					((BarsViewHolder) holder).ivUserImage1
							.setVisibility(View.VISIBLE);
					Others.loadImages(singleBarDetails.getUserImage2(),
							((BarsViewHolder) holder).ivUserImage2);
					((BarsViewHolder) holder).ivUserImage2
							.setVisibility(View.VISIBLE);
					break;
				case 3:
					Others.loadImages(singleBarDetails.getUserImage1(),
							((BarsViewHolder) holder).ivUserImage1);
					((BarsViewHolder) holder).ivUserImage1
							.setVisibility(View.VISIBLE);
					Others.loadImages(singleBarDetails.getUserImage2(),
							((BarsViewHolder) holder).ivUserImage2);
					((BarsViewHolder) holder).ivUserImage2
							.setVisibility(View.VISIBLE);
					Others.loadImages(singleBarDetails.getUserImage3(),
							((BarsViewHolder) holder).ivUserImage3);
					((BarsViewHolder) holder).ivUserImage3
							.setVisibility(View.VISIBLE);
					break;
				case 4:
					Others.loadImages(singleBarDetails.getUserImage1(),
							((BarsViewHolder) holder).ivUserImage1);
					((BarsViewHolder) holder).ivUserImage1
							.setVisibility(View.VISIBLE);
					Others.loadImages(singleBarDetails.getUserImage2(),
							((BarsViewHolder) holder).ivUserImage2);
					((BarsViewHolder) holder).ivUserImage2
							.setVisibility(View.VISIBLE);
					Others.loadImages(singleBarDetails.getUserImage3(),
							((BarsViewHolder) holder).ivUserImage3);
					((BarsViewHolder) holder).ivUserImage3
							.setVisibility(View.VISIBLE);
					Others.loadImages(singleBarDetails.getUserImage4(),
							((BarsViewHolder) holder).ivUserImage4);
					((BarsViewHolder) holder).ivUserImage4
							.setVisibility(View.VISIBLE);
					break;
				default:
					break;
				}
			} else {
				((BarsViewHolder) holder).liOnlineMembers
						.setVisibility(View.GONE);
				((BarsViewHolder) holder).liUserImages.setVisibility(View.GONE);
			}
			int facilityCount = Integer.parseInt(singleBarDetails
					.getFacilityCount());
			if (facilityCount > 0) {
				((BarsViewHolder) holder).liFacilities
						.setVisibility(View.VISIBLE);
				// Log.v("Facilities","inFacilities");
				if (facilityCount > 4)
					facilityCount = 4;

				switch (facilityCount) {
				case 1:
					Others.setImageAsBackground(
							singleBarDetails.getFacilityImage1(),
							((BarsViewHolder) holder).ivFacility1);
					((BarsViewHolder) holder).ivFacility1
							.setVisibility(View.VISIBLE);
					break;

				case 2:
					Others.setImageAsBackground(
							singleBarDetails.getFacilityImage1(),
							((BarsViewHolder) holder).ivFacility1);
					((BarsViewHolder) holder).ivFacility1
							.setVisibility(View.VISIBLE);
					Others.setImageAsBackground(
							singleBarDetails.getFacilityImage2(),
							((BarsViewHolder) holder).ivFacility2);
					((BarsViewHolder) holder).ivFacility2
							.setVisibility(View.VISIBLE);
					break;

				case 3:
					Others.setImageAsBackground(
							singleBarDetails.getFacilityImage1(),
							((BarsViewHolder) holder).ivFacility1);
					((BarsViewHolder) holder).ivFacility1
							.setVisibility(View.VISIBLE);
					Others.setImageAsBackground(
							singleBarDetails.getFacilityImage2(),
							((BarsViewHolder) holder).ivFacility2);
					((BarsViewHolder) holder).ivFacility2
							.setVisibility(View.VISIBLE);
					Others.setImageAsBackground(
							singleBarDetails.getFacilityImage3(),
							((BarsViewHolder) holder).ivFacility3);
					((BarsViewHolder) holder).ivFacility3
							.setVisibility(View.VISIBLE);
					break;
				case 4:
					Others.setImageAsBackground(
							singleBarDetails.getFacilityImage1(),
							((BarsViewHolder) holder).ivFacility1);
					((BarsViewHolder) holder).ivFacility1
							.setVisibility(View.VISIBLE);
					Others.setImageAsBackground(
							singleBarDetails.getFacilityImage2(),
							((BarsViewHolder) holder).ivFacility2);
					((BarsViewHolder) holder).ivFacility2
							.setVisibility(View.VISIBLE);
					Others.setImageAsBackground(
							singleBarDetails.getFacilityImage3(),
							((BarsViewHolder) holder).ivFacility3);
					((BarsViewHolder) holder).ivFacility3
							.setVisibility(View.VISIBLE);
					Others.setImageAsBackground(
							singleBarDetails.getFacilityImage4(),
							((BarsViewHolder) holder).ivFacility4);
					((BarsViewHolder) holder).ivFacility4
							.setVisibility(View.VISIBLE);
					break;

				default:
					break;
				}
			} else {
				((BarsViewHolder) holder).liFacilities.setVisibility(View.GONE);
			}
			((BarsViewHolder) holder).barsDeratils = singleBarDetails;

		} else {
			((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
		}
	}

	@SuppressWarnings("deprecation")
	public void setDefaultImages(BarsViewHolder holder) {
		holder.ivUserImage1.setImageResource(R.drawable.add_profile);
		holder.ivUserImage2.setImageResource(R.drawable.add_profile);
		holder.ivUserImage3.setImageResource(R.drawable.add_profile);
		holder.ivUserImage4.setImageResource(R.drawable.add_profile);
	}

	public void setLoaded() {
		loading = false;

	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.onLoadMoreListener = onLoadMoreListener;
	}

	public static class BarsViewHolder extends RecyclerView.ViewHolder {
		public TextView tvBarName;
		public TextView tvBarDistance;
		public TextView tvTypeOfBar;
		public TextView tvBarOnlineMembers;
		public ImageView ivFacility1;
		public ImageView ivFacility2;
		public ImageView ivFacility3;
		public ImageView ivFacility4;
		public ImageView ivBarPhoto;
		public ImageView ivUserImage1;
		public ImageView ivUserImage2;
		public ImageView ivUserImage3;
		public ImageView ivUserImage4;
		public LinearLayout liOnlineMembers;
		public LinearLayout liUserImages;
		public LinearLayout liFacilities;

		public BarsDetails barsDeratils;

		public BarsViewHolder(View v) {
			super(v);
			tvBarName = (TextView) v.findViewById(R.id.tvBarName);
			tvBarDistance = (TextView) v.findViewById(R.id.tvBarDistance);
			tvTypeOfBar = (TextView) v.findViewById(R.id.tvTypeOfBar);
			tvBarOnlineMembers = (TextView) v
					.findViewById(R.id.tvBarOnlineMembers);
			ivFacility1 = (ImageView) v.findViewById(R.id.ivFacility1);
			ivFacility2 = (ImageView) v.findViewById(R.id.ivFacility2);
			ivFacility3 = (ImageView) v.findViewById(R.id.ivFacility3);
			ivFacility4 = (ImageView) v.findViewById(R.id.ivFacility4);
			ivBarPhoto = (ImageView) v.findViewById(R.id.ivBarPhoto);
			ivUserImage1 = (ImageView) v.findViewById(R.id.ivUserImage1);
			ivUserImage2 = (ImageView) v.findViewById(R.id.ivUserImage2);
			ivUserImage3 = (ImageView) v.findViewById(R.id.ivUserImage3);
			ivUserImage4 = (ImageView) v.findViewById(R.id.ivUserImage4);
			liOnlineMembers = (LinearLayout) v
					.findViewById(R.id.liOnlineMembers);
			liUserImages = (LinearLayout) v.findViewById(R.id.liUserImages);
			liFacilities = (LinearLayout) v.findViewById(R.id.liFacilities);

			ivBarPhoto.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					barProfile();
				}
			});
			tvBarName.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					barProfile();
				}
			});
			ivUserImage1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Log.v("string",barsDeratils.getVisinityUserIds().get(0)+"/"+barsDeratils.getBarName());
					userProfile(barsDeratils.getUserImage1ID());

				}
			});
			ivUserImage2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					userProfile(barsDeratils.getUserImage2ID());

				}
			});
			ivUserImage3.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					userProfile(barsDeratils.getUserImage3ID());

				}
			});
			ivUserImage4.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					userProfile(barsDeratils.getUserImage4ID());

				}
			});
		}

		protected void userProfile(String user_id) {
			// TODO Auto-generated method stub
			Others.hideKeyBoad(_context);
			Bundle bundle = new Bundle();
			bundle.putString("user_id", user_id);
			bundle.putString("Navigation", "bars");
			FragmentUserProfile user = new FragmentUserProfile();
			user.setArguments(bundle);
			_context.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, user).commit();
		}

		protected void barProfile() {
			Others.navigation = "bars";
			Others.hideKeyBoad(_context);
			Bundle bundle = new Bundle();
			bundle.putString("bar_id", barsDeratils.getBarId());
			bundle.putString("barProfileNavigation", "homeScreen");
			FragmentBarsProfileFb bars = new FragmentBarsProfileFb();
			bars.setArguments(bundle);
			_context.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, bars).commit();
		}
	}

	public static class ProgressViewHolder extends RecyclerView.ViewHolder {
		public ProgressBar progressBar;

		public ProgressViewHolder(View v) {
			super(v);
			progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
		}
	}

}
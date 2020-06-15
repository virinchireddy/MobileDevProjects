package com.info.socialnetworking.app.adapters;

import java.util.ArrayList;
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

import com.info.socialnetworking.app.Fragment.FragmentUserProfile;
import com.info.socialnetworking.app.helper.MeetOthersDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.R;

@SuppressWarnings("rawtypes")
public class MeetOthersAdapter extends RecyclerView.Adapter {

	private List<MeetOthersDetails> meetOthersList;

	private final int VIEW_ITEM = 1;
	private final int VIEW_PROG = 0;
	private int visibleThreshold = 5;
	static ArrayList<String> userIds = new ArrayList<String>();
	private int lastVisibleItem, totalItemCount;
	private boolean loading;
	private OnLoadMoreListener onLoadMoreListener;
	static Activity _context;

	public MeetOthersAdapter(List<MeetOthersDetails> meetOthers,
			RecyclerView recyclerView, Activity context,
			ArrayList<String> userIds) {
		// TODO Auto-generated constructor stub

		this.meetOthersList = meetOthers;
		MeetOthersAdapter._context = context;
		MeetOthersAdapter.userIds = userIds;
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
		return meetOthersList.size();
	}

	/*
	 * @Override public int getItemViewType(int position) { // TODO
	 * Auto-generated method stub return position; }
	 */

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return meetOthersList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {

		/*
		 * RecyclerView.ViewHolder vh; View v =
		 * LayoutInflater.from(parent.getContext()).inflate(
		 * R.layout.meet_others_elements, parent, false); vh = new
		 * MeetOthersViewHolder(v,viewType); return vh;
		 */

		RecyclerView.ViewHolder vh;
		if (viewType == VIEW_ITEM) {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.meet_others_elements, parent, false);

			vh = new MeetOthersViewHolder(v, viewType);
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
		if (holder instanceof MeetOthersViewHolder) {

			MeetOthersDetails singlePersonDetails = (MeetOthersDetails) meetOthersList
					.get(position);

			((MeetOthersViewHolder) holder).tvUserName
					.setText(singlePersonDetails.getUserName());
			((MeetOthersViewHolder) holder).tvAge.setText(singlePersonDetails
					.getAge());
			((MeetOthersViewHolder) holder).tvDistance
					.setText(singlePersonDetails.getDistance());
			((MeetOthersViewHolder) holder).tvCity.setText(singlePersonDetails
					.getCityName());
			if (singlePersonDetails.getTaglinee().equals(""))
				((MeetOthersViewHolder) holder).tvTagLine
						.setVisibility(View.GONE);
			else
				((MeetOthersViewHolder) holder).tvTagLine
						.setText(singlePersonDetails.getTaglinee());
			((MeetOthersViewHolder) holder).tvStatus
					.setText(singlePersonDetails.getStatus());
			((MeetOthersViewHolder) holder).ivUserImage
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.add_profile));
			Others.setImageAsBackground(singlePersonDetails.getUserProfile(),
					((MeetOthersViewHolder) holder).ivUserImage);
			if (singlePersonDetails.getOnlineOffline().equals("1"))
				((MeetOthersViewHolder) holder).ivOnlineOffline
						.setBackgroundDrawable(_context.getResources()
								.getDrawable(R.drawable.online_icon));
			else if (singlePersonDetails.getOnlineOffline().equals("0"))
				((MeetOthersViewHolder) holder).ivOnlineOffline
						.setBackgroundDrawable(_context.getResources()
								.getDrawable(R.drawable.offline_icon));

			if (singlePersonDetails.getUserLength() > 8) {
				((MeetOthersViewHolder) holder).liLastActive
						.setVisibility(View.VISIBLE);
				((MeetOthersViewHolder) holder).tvLastActive
						.setText(singlePersonDetails.getLastActive());

			} else {
				((MeetOthersViewHolder) holder).liLastActive
						.setVisibility(View.GONE);
			}
			((MeetOthersViewHolder) holder).meetOthersDeratils = singlePersonDetails;

		} else {
			((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
		}

	}

	public void setLoaded() {
		loading = false;
	}

	public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
		this.onLoadMoreListener = onLoadMoreListener;
	}

	public static class MeetOthersViewHolder extends RecyclerView.ViewHolder {
		public TextView tvUserName;
		public TextView tvAge;
		public TextView tvDistance;
		public TextView tvCity;
		public TextView tvTagLine;
		public TextView tvStatus;
		public TextView tvLastActive;

		public ImageView ivUserImage;
		public ImageView ivOnlineOffline;
		public ImageView ivLike;
		public LinearLayout liLastActive;

		public MeetOthersDetails meetOthersDeratils;

		public MeetOthersViewHolder(View v, final int position) {
			super(v);
			tvUserName = (TextView) v.findViewById(R.id.tvName);
			tvAge = (TextView) v.findViewById(R.id.tvAge);
			tvDistance = (TextView) v.findViewById(R.id.tvDistance);
			tvCity = (TextView) v.findViewById(R.id.tvCity);
			tvTagLine = (TextView) v.findViewById(R.id.tvTagLine);
			tvStatus = (TextView) v.findViewById(R.id.tvStatus);
			ivUserImage = (ImageView) v.findViewById(R.id.ivUserImage);
			ivOnlineOffline = (ImageView) v.findViewById(R.id.ivOnlineOffline);
			tvLastActive = (TextView) v.findViewById(R.id.tvLastActive);
			liLastActive = (LinearLayout) v.findViewById(R.id.liLastActive);

			/*
			 * ivLike.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { // TODO Auto-generated
			 * method stub Toast.makeText(v.getContext(),"OnClick :" +
			 * meetOthersDeratils.getUserId()+ " \n " +
			 * meetOthersDeratils.getUserName(), Toast.LENGTH_SHORT).show(); }
			 * });
			 */

			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					/*
					 * Toast.makeText( v.getContext(), "OnClick :" +
					 * meetOthersDeratils.getUserId() + " \n " +
					 * meetOthersDeratils.getUserName(),
					 * Toast.LENGTH_SHORT).show();
					 */
					/*
					 * HopperProfileSwipeScreen friends = new
					 * HopperProfileSwipeScreen(userIds, position);
					 * _context.getFragmentManager().beginTransaction()
					 * .replace(R.id.flContainer, friends).addToBackStack(null)
					 * .commit();
					 */
					Others.hideKeyBoad(_context);
					Bundle bundle = new Bundle();
					bundle.putString("user_id", meetOthersDeratils.getUserId());
					bundle.putString("Navigation", "meetOthers");
					FragmentUserProfile user = new FragmentUserProfile();
					user.setArguments(bundle);
					_context.getFragmentManager().beginTransaction()
							.replace(R.id.flContainer, user).commit();

				}
			});

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

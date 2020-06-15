package com.info.socialnetworking.app.adapters;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.ChatIndividualPage;
import com.info.socialnetworking.app.helper.ChatListDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressWarnings("rawtypes")
public class ChatListAdapter extends RecyclerView.Adapter {

	private List<ChatListDetails> chatList;

	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private boolean loading;
	private OnLoadMoreListener onLoadMoreListener;
	static Activity _context;
	static Others others;
	static ConnectionDetector con;

	public ChatListAdapter(List<ChatListDetails> chatList,
			RecyclerView recyclerView, Activity context) {
		this.chatList = chatList;
		_context = context;
		others = new Others(_context);
		con = new ConnectionDetector(_context);

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
							if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
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
		return chatList.size();
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		if (holder instanceof ChatListItemsViewHolder) {

			ChatListDetails singleUserDetails = (ChatListDetails) chatList.get(position);
			
			ChatListItemsViewHolder viewHolder = (ChatListItemsViewHolder) holder;
			Log.v("adapter text", singleUserDetails.getChatUserName()+"/"+singleUserDetails.getChatUserMessage());
			viewHolder.tvUserName.setText(singleUserDetails.getChatUserName());
			viewHolder.tvMessage.setText(singleUserDetails.getChatUserMessage());
			viewHolder.ivUserImage.setBackgroundDrawable(_context
					.getResources().getDrawable(R.drawable.add_profile));
			Others.setImageAsBackground(singleUserDetails.getChatUserImage(),
					((ChatListItemsViewHolder) holder).ivUserImage);
			Log.v("unreadMessags", singleUserDetails.getChatUnreadMessages());
			if(singleUserDetails.getChatUnreadMessages().equals("0"))
			{
				viewHolder.tvUnReadMessageCount.setVisibility(View.GONE);
			}else{
				viewHolder.tvUnReadMessageCount.setVisibility(View.VISIBLE);
				viewHolder.tvUnReadMessageCount.setText(singleUserDetails.getChatUnreadMessages());
			}
			viewHolder.chatListDetails = singleUserDetails;
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder vh;

		View v = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.chat_list_element, parent, false);

		vh = new ChatListItemsViewHolder(v);
		return vh;
	}

	public static class ChatListItemsViewHolder extends
			RecyclerView.ViewHolder {

		TextView tvUserName;
		TextView tvMessage;
		ImageView ivUserImage;
		TextView tvUnReadMessageCount;
		

		public ChatListDetails chatListDetails;

		public ChatListItemsViewHolder(View v) {
			super(v);
			// TODO Auto-generated constructor stub
			tvUserName = (TextView) v.findViewById(R.id.tvUserName);
			tvMessage = (TextView) v.findViewById(R.id.tvMessage);
			ivUserImage = (ImageView) v.findViewById(R.id.ivUserPhoto);
			tvUnReadMessageCount=(TextView)v.findViewById(R.id.tvUnReadMessageCount);

			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Others.hideKeyBoad(_context);
					/*Bundle bundle = new Bundle();
					bundle.putString("friend_id", chatListDetails.getChatUserId());
					bundle.putString("Navigation","chat");
					ChatIndividualPage chat = new ChatIndividualPage();
					chat.setArguments(bundle);
					_context.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, chat)
					.commit();*/
					Intent i=new Intent(_context,ChatIndividualPage.class);
					i.putExtra("user_id", BarHopperHomeScreen.user_id+"");
					i.putExtra("friend_id", chatListDetails.getChatUserId());
					_context.startActivity(i);
					_context.finish();
				}
			});

		}
	}
}

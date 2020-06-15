package com.info.socialnetworking.app.adapters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.info.socialnetworking.app.helper.ChatIndividualDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressLint("RtlHardcoded")
@SuppressWarnings("rawtypes")
public class ChatIndividualAdapter extends RecyclerView.Adapter {

	private List<ChatIndividualDetails> chatList;

	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private boolean loading;
	private OnLoadMoreListener onLoadMoreListener;
	static Activity _context;
	static Others others;
	static ConnectionDetector con;

	public ChatIndividualAdapter(List<ChatIndividualDetails> chatList,
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
		return chatList.size();
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressLint("RtlHardcoded")
	@SuppressWarnings("deprecation")
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		// TODO Auto-generated method stub
		if (holder instanceof ChatItemsViewHolder) {

			ChatIndividualDetails singleUserDetails = (ChatIndividualDetails) chatList
					.get(position);
			ChatItemsViewHolder viewHolder = (ChatItemsViewHolder) holder;
			// based on data send from server
			// message is mine
			if (singleUserDetails.getIsSender() == "1") {
				viewHolder.liBubbleLayout
						.setBackgroundResource(R.drawable.chat_sent_bubble2);
				// Log.v("Testing",singleUserDetails.getMessage());
				viewHolder.liParentLayout.setGravity(Gravity.RIGHT);
				viewHolder.tvMessage.setText(singleUserDetails.getMessage());
				viewHolder.tvTimeStamp
						.setText(ChangeTimeStampFormat(singleUserDetails
								.getMessageTime()));
			}
			// message is from others
			else {
				viewHolder.liBubbleLayout
						.setBackgroundResource(R.drawable.chat_received_bubble1);
				viewHolder.liParentLayout.setGravity(Gravity.LEFT);

				// Log.v("in else block",singleUserDetails.getMessage());
				viewHolder.tvMessage.setText(singleUserDetails.getMessage());
				viewHolder.tvTimeStamp
						.setText(ChangeTimeStampFormat(singleUserDetails
								.getMessageTime()));

			}

			viewHolder.chatList = singleUserDetails;
		}
	}

	@SuppressLint("SimpleDateFormat") 
	private String ChangeTimeStampFormat(String timeStamp) {
		// TODO Auto-generated method stub
		
		//testing 
		SimpleDateFormat sourceFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
		sourceFormat.setTimeZone(TimeZone.getTimeZone("GMT-5"));
		Date parsed = null;
		try {
			parsed = sourceFormat.parse(timeStamp);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // => Date is in UTC now

		TimeZone tz = TimeZone.getDefault();
		SimpleDateFormat destFormat = new SimpleDateFormat("dd-MMM HH:mm");
		destFormat.setTimeZone(tz);

		String result = destFormat.format(parsed);
		
	    
	    return result;
		
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// TODO Auto-generated method stub
		RecyclerView.ViewHolder vh;

		View v = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.chat_messages_item, parent, false);

		vh = new ChatItemsViewHolder(v);
		return vh;
	}

	public static class ChatItemsViewHolder extends RecyclerView.ViewHolder {

		TextView tvMessage;
		TextView tvTimeStamp;
		LinearLayout liBubbleLayout, liParentLayout;
		public ChatIndividualDetails chatList;

		public ChatItemsViewHolder(View v) {
			super(v);
			// TODO Auto-generated constructor stub
			tvMessage = (TextView) v.findViewById(R.id.tvMessage);
			liBubbleLayout = (LinearLayout) v.findViewById(R.id.liBubbleLayout);
			liParentLayout = (LinearLayout) v
					.findViewById(R.id.bubble_layout_parent);
			tvTimeStamp = (TextView) v.findViewById(R.id.tvTimeStamp);
		}
	}
}

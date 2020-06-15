package com.info.socialnetworking.app.adapters;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.Fragment.FragmentUserProfile;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.NotificationDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressWarnings("rawtypes")
public class NotificationsAdapter extends RecyclerView.Adapter {

	private List<NotificationDetails> notificationList;

	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private boolean loading;
	private OnLoadMoreListener onLoadMoreListener;
	static Activity _context;
	static Others others;
	static ConnectionDetector con;

	public NotificationsAdapter(List<NotificationDetails> notifications,
			RecyclerView recyclerView, Activity context) {
		this.notificationList = notifications;
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
		return notificationList.size();
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
		if (holder instanceof NotificationItemsViewHolder) {

			NotificationDetails singleUserDetails = (NotificationDetails) notificationList
					.get(position);
			NotificationItemsViewHolder viewHolder = (NotificationItemsViewHolder) holder;
			Log.v("userName", singleUserDetails.getUserName());
			viewHolder.tvUserName.setText(singleUserDetails.getUserName());
			//viewHolder.tvMessage.setText(singleUserDetails.getMessage());
			// checking the message and changing the format
			if(singleUserDetails.getMessage().equals("You are now friends"))
			{
				viewHolder.tvMessage.setText(Html.fromHtml( "You and " + "<b>"+ singleUserDetails.getUserName() + "</b>" + " are now friends"));
			}else if(singleUserDetails.getMessage().equals("You received friend request")){
				
				viewHolder.tvMessage.setText(Html.fromHtml( "You received a friend request from " + "<b>"+ singleUserDetails.getUserName() + "</b>" ));

			}else if(singleUserDetails.getMessage().equals("You sent friend request")){
				
				viewHolder.tvMessage.setText(Html.fromHtml( "You sent a friend request to " + "<b>"+ singleUserDetails.getUserName() + "</b>" ));

			}else if(singleUserDetails.getMessage().equals("You declined friend request")){
				
				viewHolder.tvMessage.setText(Html.fromHtml( "You declined a friend request from " + "<b>"+ singleUserDetails.getUserName() + "</b>"));
			}
			
			
			viewHolder.tvTime.setText(Others.cstToLocal(singleUserDetails.getTime()));

			viewHolder.ivUserImage.setBackgroundDrawable(_context
					.getResources().getDrawable(R.drawable.add_profile));
			Others.setImageAsBackground(singleUserDetails.getUserProfile(),
					((NotificationItemsViewHolder) holder).ivUserImage);
			if (singleUserDetails.getMessage()
					.equals("You received friend request")) {
				if (singleUserDetails.getDeclineStatus().equals("Not declined"))
					viewHolder.liFriendRequest.setVisibility(View.VISIBLE);
				else 
					viewHolder.liFriendRequest.setVisibility(View.GONE);
			} else
				viewHolder.liFriendRequest.setVisibility(View.GONE);

			viewHolder.notificationDetails = singleUserDetails;
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RecyclerView.ViewHolder vh;

		View v = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.notification_element, parent, false);

		vh = new NotificationItemsViewHolder(v);
		return vh;

	}

	public static class NotificationItemsViewHolder extends
			RecyclerView.ViewHolder {

		TextView tvUserName;
		TextView tvMessage;
		TextView tvTime;

		ImageView ivUserImage;
		LinearLayout liFriendRequest;
		Button btnDecline, btnAccept;

		public NotificationDetails notificationDetails;

		public NotificationItemsViewHolder(View v) {
			super(v);
			// TODO Auto-generated constructor stub
			tvUserName = (TextView) v.findViewById(R.id.tvUserName);
			tvMessage = (TextView) v.findViewById(R.id.tvMessage);
			tvTime = (TextView) v.findViewById(R.id.tvTime);

			ivUserImage = (ImageView) v.findViewById(R.id.ivUserPhoto);
			liFriendRequest = (LinearLayout) v
					.findViewById(R.id.liFriendRequest);
			btnDecline = (Button) v.findViewById(R.id.btnDecline);
			btnAccept = (Button) v.findViewById(R.id.btnAccept);

			ivUserImage.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Bundle bundle = new Bundle();
					bundle.putString("user_id", notificationDetails.getUserId());
					bundle.putString("Navigation", "Notifications");
					FragmentUserProfile user = new FragmentUserProfile();
					user.setArguments(bundle);
					_context.getFragmentManager().beginTransaction()
							.replace(R.id.flContainer, user).commit();
				}
			});
			btnAccept.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						JSONObject addFriend = new JSONObject();
						addFriend.put("user_id", BarHopperHomeScreen.user_id);
						addFriend.put("friend_id",
								notificationDetails.getUserId());
						sendFriendRequest(addFriend,"accept");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			btnDecline.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					try {
						JSONObject addFriend = new JSONObject();
						addFriend.put("user_id", BarHopperHomeScreen.user_id);
						addFriend.put("friend_id",
								notificationDetails.getUserId());
						addFriend.put("decline","decline");
						sendFriendRequest(addFriend,"decline");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

		private void sendFriendRequest(JSONObject addFriend,final String buttonIdentification) {
			// TODO Auto-generated method stub
			String tag_string_req = "req_friendRequest";
			others.showProgressWithOutMessage();
			JsonObjectRequest jsObjRequest = new JsonObjectRequest(
					Request.Method.POST, Config.URL_FRIEND_REQUEST, addFriend,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							Log.v("hello", "I am at Responce block");
							others.hideDialog();
							try {
								boolean error = response.getBoolean("error");
								if (!error) {

									if(buttonIdentification.equals("accept"))
									{
										notificationDetails.setMessage("You are now friends");
										liFriendRequest.setVisibility(View.GONE);
										tvMessage.setText("You are now friends");
									}else if(buttonIdentification.equals("decline"))
									{
										notificationDetails.setMessage("You declined friend request");
										liFriendRequest.setVisibility(View.GONE);
										Log.v("am here", "");
										tvMessage.setText("You declined friend request");

									}
									
								} else {
									String error_msg = response
											.getString("error_msg");
									Log.v("hello", error_msg.toString());
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Log.i("volley", "error: " + error);
							others.hideDialog();
							con.serverErrorAlert();
						}
					});
			jsObjRequest.setShouldCache(false);
			// Adding request to request queue
			AppController.getInstance().addToRequestQueue(jsObjRequest,
					tag_string_req);

		}
	}

}

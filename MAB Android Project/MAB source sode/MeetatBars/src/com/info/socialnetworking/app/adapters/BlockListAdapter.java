package com.info.socialnetworking.app.adapters;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.Fragment.BlockListPage;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.BlockListDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.BarHopperHomeScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

@SuppressWarnings("rawtypes")
public class BlockListAdapter extends RecyclerView.Adapter {

	private List<BlockListDetails> blockList;

	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private boolean loading;
	private OnLoadMoreListener onLoadMoreListener;
	static Activity _context;
	static Others others;
	static ConnectionDetector con;

	public BlockListAdapter(List<BlockListDetails> blockList,
			RecyclerView recyclerView, Activity context) {
		// TODO Auto-generated constructor stub
		this.blockList = blockList;
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
		return blockList.size();
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
		if (holder instanceof BlockListItemsViewHolder) {

			BlockListDetails singleUserDetails = (BlockListDetails) blockList
					.get(position);
			BlockListItemsViewHolder viewHolder = (BlockListItemsViewHolder) holder;
			Log.v("userName", singleUserDetails.getUserName());
			viewHolder.tvUserName.setText(singleUserDetails.getUserName());
			viewHolder.tvTimeStamp.setText(singleUserDetails.getTimeStamp());
			viewHolder.ivUserImage.setBackgroundDrawable(_context
					.getResources().getDrawable(R.drawable.add_profile));
			Others.setImageAsBackground(singleUserDetails.getUserProfile(),
					((BlockListItemsViewHolder) holder).ivUserImage);

			viewHolder.blockListDetails = singleUserDetails;
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		// TODO Auto-generated method stub
		RecyclerView.ViewHolder vh;

		View v = LayoutInflater.from(parent.getContext()).inflate(
				R.layout.blocklist_element, parent, false);

		vh = new BlockListItemsViewHolder(v);
		return vh;
	}

	public static class BlockListItemsViewHolder extends
			RecyclerView.ViewHolder {
		TextView tvUserName;
		TextView tvTimeStamp;
		ImageView ivUserImage;

		public BlockListDetails blockListDetails;

		public BlockListItemsViewHolder(View v) {
			super(v);
			tvUserName = (TextView) v.findViewById(R.id.tvUserName);
			tvTimeStamp = (TextView) v.findViewById(R.id.tvTimeStamp);
			ivUserImage = (ImageView) v.findViewById(R.id.ivUserPhoto);
			v.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					unBlockAlert(blockListDetails.getUserName(),
							blockListDetails.getUserId());
				}
			});
			// TODO Auto-generated constructor stub
		}
	}

	private static void unBlockAlert(String userName, final String userId) {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(_context);
		builder.setMessage("Would you like to unblock " + userName)
				.setCancelable(false)
				.setPositiveButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						})
				.setNegativeButton("Unblock",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Action for 'NO' Button
								dialog.cancel();
								unblockUser(userId);
							}
						});
		// Creating dialog box
		AlertDialog alert = builder.create();
		alert.setCanceledOnTouchOutside(true);
		// Setting the title manually
		alert.setTitle("Unblock");
		alert.show();
	}

	protected static void unblockUser(String other_userId) {
		// TODO Auto-generated method stub
		try {
			JSONObject blockList2 = new JSONObject();
			blockList2.put("user_id", BarHopperHomeScreen.user_id);
			blockList2.put("mode", "unblock");
			blockList2.put("other_id", other_userId);
			String tag_string_req = "req_unblock";
			others.showProgressWithOutMessage();

			JsonObjectRequest jsObjRequest = new JsonObjectRequest(
					Request.Method.POST, Config.URL_BLOCK_LIST, blockList2,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							others.hideDialog();
							try {
								boolean error = response.getBoolean("error");
								if (!error) {
									Toast.makeText(_context, "Unblocked successfully", Toast.LENGTH_SHORT).show();
									_context.getFragmentManager()
											.popBackStack();
									Others.navigation = "menu";
									BlockListPage blockList = new BlockListPage();
									_context.getFragmentManager()
											.beginTransaction()
											.replace(R.id.flContainer,
													blockList)
											.addToBackStack(null).commit();
								} else {
									others.ToastMessage(response
											.getString("error_msg"));
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
			jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			// Adding request to request queue
			AppController.getInstance().addToRequestQueue(jsObjRequest,
					tag_string_req);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

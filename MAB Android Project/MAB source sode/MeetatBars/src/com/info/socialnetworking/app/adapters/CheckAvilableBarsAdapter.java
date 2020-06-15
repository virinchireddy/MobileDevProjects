package com.info.socialnetworking.app.adapters;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.info.socialnetworking.app.Fragment.FragmentEditMyProfile;
import com.info.socialnetworking.app.adapters.MeetOthersAdapter.ProgressViewHolder;
import com.info.socialnetworking.app.app.AppController;
import com.info.socialnetworking.app.app.Config;
import com.info.socialnetworking.app.helper.CheckAvilableBarsDetails;
import com.info.socialnetworking.app.helper.Others;
import com.info.socialnetworking.app.interfaces.OnLoadMoreListener;
import com.info.socialnetworking.app.meetatbars.BarOwnerQuestions;
import com.info.socialnetworking.app.meetatbars.CheckAvilableBars;
import com.info.socialnetworking.app.meetatbars.MatchMeQuestionsScreen;
import com.info.socialnetworking.app.meetatbars.R;
import com.info.socialnetworking.app.network.ConnectionDetector;

@SuppressWarnings("rawtypes")
public class CheckAvilableBarsAdapter extends RecyclerView.Adapter {

	private final int VIEW_ITEM = 1;
	private final int VIEW_PROG = 0;

	private List<CheckAvilableBarsDetails> avilableBarsList;

	private int visibleThreshold = 5;
	private int lastVisibleItem, totalItemCount;
	private boolean loading;
	private OnLoadMoreListener onLoadMoreListener;
	static Context _context;
	static String navigation;

	public CheckAvilableBarsAdapter(
			List<CheckAvilableBarsDetails> avilableBars,
			RecyclerView recyclerView, Context context, String navigation) {
		// TODO Auto-generated constructor stub

		this.avilableBarsList = avilableBars;
		CheckAvilableBarsAdapter._context = context;
		CheckAvilableBarsAdapter.navigation = navigation;
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
		return avilableBarsList.size();
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return avilableBarsList.get(position) != null ? VIEW_ITEM : VIEW_PROG;

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		// TODO Auto-generated method stub

		if (holder instanceof AvilableBarsViewHolder) {

			CheckAvilableBarsDetails singleBarDetails = (CheckAvilableBarsDetails) avilableBarsList
					.get(position);

			((AvilableBarsViewHolder) holder).tvBarName
					.setText(singleBarDetails.getBarName());
			((AvilableBarsViewHolder) holder).tvBarAddress
					.setText(singleBarDetails.getBarAddress());
			((AvilableBarsViewHolder) holder).ivBarPhoto
					.setBackgroundDrawable(_context.getResources().getDrawable(
							R.drawable.image_loading));

			Others.setImageAsBackground(singleBarDetails.getBarImage(),
					((AvilableBarsViewHolder) holder).ivBarPhoto);

			((AvilableBarsViewHolder) holder).checkAvilableBarsDeratils = singleBarDetails;

		} else {
			((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
		}

	}

	
	
	
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		// TODO Auto-generated method stub
		RecyclerView.ViewHolder vh;
		if (viewType == VIEW_ITEM) {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.check_avilable_bars_elements, parent, false);

			vh = new AvilableBarsViewHolder(v);
		} else {
			View v = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.progress_item, parent, false);

			vh = new ProgressViewHolder(v);
		}
		return vh;
	}

	public static class AvilableBarsViewHolder extends RecyclerView.ViewHolder {

		public TextView tvBarName;
		public TextView tvBarAddress;
		public ImageView ivBarPhoto;

		public CheckAvilableBarsDetails checkAvilableBarsDeratils;

		public AvilableBarsViewHolder(View v) {
			super(v);
			tvBarName = (TextView) v.findViewById(R.id.tvBarName);
			tvBarAddress = (TextView) v.findViewById(R.id.tvBarAddress);
			ivBarPhoto = (ImageView) v.findViewById(R.id.ivBarPhoto);
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
					if (navigation.equals("BarOwners")) {
						if (checkAvilableBarsDeratils.getOwnerId().equals(
								"null")) {
							BarOwnerQuestions.barName = checkAvilableBarsDeratils
									.getBarName();
							BarOwnerQuestions.barAddress = checkAvilableBarsDeratils
									.getBarAddress();
							Others.setImageAsBackground(
									checkAvilableBarsDeratils.getBarImage(),
									BarOwnerQuestions.user_photo);
							if (!checkAvilableBarsDeratils.getBarID().equals(
									"null")) {
								BarOwnerQuestions.barId = checkAvilableBarsDeratils
										.getBarID();
							}
							((Activity) _context).finish();
						} else {
							Toast.makeText(_context, "something went wrong",
									Toast.LENGTH_SHORT).show();
						}
					}else if(navigation.equals("BarHopper")||navigation.equals("UserProfile"))
					{
						try{
							JSONObject obj=new JSONObject();
							obj.put("user_id",CheckAvilableBars.user_id);
							obj.put("favorite_bar_id", checkAvilableBarsDeratils.getBarID());
							Log.v("sending",obj.toString());
							addFavorite(obj);
							
						}catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					
				}
			});
		}

		protected void addFavorite(JSONObject obj) {
			// TODO Auto-generated method stub
			String tag_string_req = "req_add_favorite";
			final Others others=new Others(_context);
			final ConnectionDetector con=new ConnectionDetector(_context);
			others.showProgressWithOutMessage();

			JsonObjectRequest jsObjRequest = new JsonObjectRequest(
					Request.Method.POST, Config.URL_FAVORITE_BARS, obj,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							others.hideDialog();
							Log.v("hello", "I am at Responce block");
							try {
								boolean error = response.getBoolean("error");
								if (!error) {
									((Activity) _context).finish();
									if(navigation.equals("BarHopper"))
									MatchMeQuestionsScreen.favouriteBar=checkAvilableBarsDeratils
											.getBarName();
									else if(navigation.equals("UserProfile"))
										FragmentEditMyProfile.favorite_bar=checkAvilableBarsDeratils
										.getBarName();
								} else {
									String error_msg = response
											.getString("error_msg");
									Toast.makeText(_context,
											error_msg, Toast.LENGTH_SHORT).show();
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

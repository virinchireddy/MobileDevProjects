package com.om.virinchi.ricelake.Adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.om.virinchi.ricelake.Fragments.FragmentAddComment;
import com.om.virinchi.ricelake.Helper.CommentsListDetails;
import com.om.virinchi.ricelake.Network.ConnectionDetector;
import com.om.virinchi.ricelake.Network.Others;
import com.om.virinchi.ricelake.R;

import java.util.List;

/**
 * Created by Virinchi on 10/3/2016.
 */
public class CommentsAdapter extends RecyclerView.Adapter {
    public static List<CommentsListDetails> commentsList;
    static Others others;
    static ConnectionDetector con;
    static Activity _context;
    RecyclerView mRecycelarView;
    private boolean loading;

    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 5;
    String commentController;

    public CommentsAdapter(String navigation, String commentControl) {
        if (!commentControl.equals("newInspection") && navigation.equals("NewInspection")) {
            commentController = commentControl;

        }
    }

    public CommentsAdapter(List<CommentsListDetails> commentsList, RecyclerView recyclerView, Activity context) {
        this.commentsList = commentsList;
        _context = context;
        others = new Others(_context);
        con = new ConnectionDetector(_context);
        mRecycelarView = recyclerView;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();
            recyclerView
                    .setOnScrollListener(new RecyclerView.OnScrollListener() {

                        public void onScrollStateChanged(int arg0) {
                            // TODO Auto-generated method stub
                        }


                        public void onScrolled(int arg0, int arg1) {
                            // TODO Auto-generated method stub
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something

                                loading = true;
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return commentsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO Auto-generated method stub
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.comments_list, parent, false);

        vh = new CommentsListDetailsViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // TODO Auto-generated method stub

        if (holder instanceof CommentsListDetailsViewHolder) {


            CommentsListDetails commentsdetails = (CommentsListDetails) commentsList
                    .get(position);
            final CommentsListDetailsViewHolder viewHolder = (CommentsListDetailsViewHolder) holder;
            viewHolder.tvReference.setText("Reference: " + commentsdetails.getrefno());
            viewHolder.tvCreateddate.setText(commentsdetails.getcreateddate());
            viewHolder.tvComment.setText("Comment: " + commentsdetails.getdescription());
            viewHolder.tvActiontaken.setText("Action Taken: " + commentsdetails.getaction());
            viewHolder.commentListDetails = commentsdetails;
        }
    }

    public static class CommentsListDetailsViewHolder extends RecyclerView.ViewHolder {
        public CommentsListDetails commentListDetails;
        TextView tvReference, tvCreateddate, tvComment, tvActiontaken, tvViewdetails;

        public CommentsListDetailsViewHolder(View v) {
            super(v);
            tvReference = (TextView) v.findViewById(R.id.tvReference);
            tvCreateddate = (TextView) v.findViewById(R.id.tvCreateddate);
            tvComment = (TextView) v.findViewById(R.id.tvComment);
            tvActiontaken = (TextView) v.findViewById(R.id.tvActiontaken);
            tvViewdetails = (TextView) v.findViewById(R.id.tvViewdetails);
            tvViewdetails.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                        sendDetails();


                }
            });
        }

        public void sendDetails() {
            Bundle bundle = new Bundle();
            bundle.putString("update_id", commentListDetails.getupdateid());
            bundle.putString("c_u_id", commentListDetails.getcommentsupdatedid());
            bundle.putString("Comment", commentListDetails.getdescription());
            bundle.putString("action", commentListDetails.getaction());
            bundle.putString("RefNum", commentListDetails.getrefno());
            bundle.putString("projectName", commentListDetails.getProjectName());
            bundle.putString("commentType", " - Edit Comment");
            bundle.putString("buttonText", "Update");
            bundle.putString("freeze", commentListDetails.getFreeze());
            bundle.putString("history", commentListDetails.getHistory());
            bundle.putString("navigation", commentListDetails.getNavigation());
            Log.v("biker", String.valueOf(bundle));
            FragmentAddComment addComment = new FragmentAddComment();
            addComment.setArguments(bundle);
            _context.getFragmentManager().beginTransaction().replace(R.id.flContainer, addComment).addToBackStack(null).commit();
        }

        public void sendDetailsNewInspection() {
            Toast.makeText(_context, "please complete references first to add comments", Toast.LENGTH_SHORT).show();

        }
    }
}
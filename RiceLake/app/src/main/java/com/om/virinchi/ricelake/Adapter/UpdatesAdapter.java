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

import com.om.virinchi.ricelake.Fragments.FragmentNewInspection;
import com.om.virinchi.ricelake.Helper.UpdatesListDetails;
import com.om.virinchi.ricelake.Network.AppController;
import com.om.virinchi.ricelake.Network.ConnectionDetector;
import com.om.virinchi.ricelake.Network.Others;
import com.om.virinchi.ricelake.R;

import java.util.List;

/**
 * Created by Virinchi on 10/3/2016.
 */
public class UpdatesAdapter extends RecyclerView.Adapter {
    public static List<UpdatesListDetails> updatesList;
    static Others others;
    static ConnectionDetector con;
    static Activity _context;
    RecyclerView mRecycelarView;
    private boolean loading;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 5;

    public UpdatesAdapter(List<UpdatesListDetails> updatesList, RecyclerView recyclerView, Activity context) {

        this.updatesList = updatesList;
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
        return updatesList.size();
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
                R.layout.updates_list, parent, false);

        vh = new UpdatesListDetailsViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // TODO Auto-generated method stub

        if (holder instanceof UpdatesListDetailsViewHolder) {


            UpdatesListDetails updateDetails = (UpdatesListDetails) updatesList
                    .get(position);
            final UpdatesListDetailsViewHolder viewHolder = (UpdatesListDetailsViewHolder) holder;
            viewHolder.tvcreateddate.setText("Last Posted: " + updateDetails.getcreatedDate());
            viewHolder.tvComments.setText("Comments: " + updateDetails.getcomments());
            viewHolder.tvCategories.setText("Categories: " + updateDetails.getcategoriesCount() + "/11  | ");
            viewHolder.tvPhotos.setText("Photos: " + updateDetails.getphotos());
            viewHolder.tvSubCategories.setText("Line items: " + updateDetails.getsubcategoriesCount() + "/59");
            viewHolder.tvOk.setText("OK: " + updateDetails.getokcount() + " | ");
            viewHolder.tvAct.setText("ACT: " + updateDetails.getactcount() + " | ");
            viewHolder.tvNa.setText("NA: " + updateDetails.getnacount() + " | ");
            viewHolder.tvBlank.setText("BLANK: " + updateDetails.getblcount());
            viewHolder.tvLastupdated.setText("Last updated: " + updateDetails.getlastupdated());
            viewHolder.updateListDetails = updateDetails;
        }
    }

    public static class UpdatesListDetailsViewHolder extends RecyclerView.ViewHolder {
        public UpdatesListDetails updateListDetails;
        TextView tvcreateddate, tvComments, tvCategories, tvPhotos, tvSubCategories, tvOk, tvAct, tvNa, tvBlank, tvViewdetails, tvLastupdated;

        public UpdatesListDetailsViewHolder(View v) {
            super(v);
            tvcreateddate = (TextView) v.findViewById(R.id.tvcreateddate);
            tvComments = (TextView) v.findViewById(R.id.tvComments);
            tvCategories = (TextView) v.findViewById(R.id.tvCategories);
            tvPhotos = (TextView) v.findViewById(R.id.tvPhotos);
            tvSubCategories = (TextView) v.findViewById(R.id.tvSubCategories);
            tvOk = (TextView) v.findViewById(R.id.tvOk);
            tvAct = (TextView) v.findViewById(R.id.tvAct);
            tvNa = (TextView) v.findViewById(R.id.tvNa);
            tvBlank = (TextView) v.findViewById(R.id.tvBlank);
            tvLastupdated = (TextView) v.findViewById(R.id.tvLastupdated);
            tvViewdetails = (TextView) v.findViewById(R.id.tvViewdetails);
            tvViewdetails.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    ((AppController) _context.getApplication()).setNew("old");
                    sendDetails();

                }
            });

        }

        public void sendDetails() {
            Bundle bundle = new Bundle();
          //  bundle.putString("update_id", updateListDetails.getupdateId());
            ((AppController)_context.getApplication()).setUpdateVariable(updateListDetails.getupdateId());
            bundle.putString("project_id", updateListDetails.getprojectId());
            bundle.putString("projectName", updateListDetails.getProjectName());
            bundle.putString("navigation", "UpdateInspection");
            bundle.putString("job_no" , updateListDetails.getJobno());
            if (updateListDetails.getHistory() == "history") {
                bundle.putString("History", "history");
            }else {
                bundle.putString("History", "project");
            }

            FragmentNewInspection newInspection = new FragmentNewInspection();
            newInspection.setArguments(bundle);
            Log.v("mexico", String.valueOf(bundle));
            _context.getFragmentManager().beginTransaction().replace(R.id.flContainer, newInspection).addToBackStack(null).commit();
        }
    }
}
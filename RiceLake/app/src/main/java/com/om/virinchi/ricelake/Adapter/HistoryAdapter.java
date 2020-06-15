package com.om.virinchi.ricelake.Adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.om.virinchi.ricelake.Fragments.FragmentSafetyUpdates;
import com.om.virinchi.ricelake.Helper.HistoryListDetails;
import com.om.virinchi.ricelake.Network.ConnectionDetector;
import com.om.virinchi.ricelake.Network.Others;
import com.om.virinchi.ricelake.R;

import java.util.List;

/**
 * Created by Virinchi on 10/21/2016.
 */
public class HistoryAdapter  extends RecyclerView.Adapter  {
    public static List<HistoryListDetails> historyList;
    static Others others;
    static ConnectionDetector con;
    static Activity _context;
    RecyclerView mRecycelarView;
    private boolean loading;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 5;

    public HistoryAdapter(List<HistoryListDetails> historyList, RecyclerView recyclerView, Activity context) {

        this.historyList = historyList;
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
        return historyList.size();
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
                R.layout.projects_list, parent, false);

        vh = new HistoryListDetailsViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // TODO Auto-generated method stub

        if (holder instanceof HistoryListDetailsViewHolder) {


            HistoryListDetails projectDetails = (HistoryListDetails) historyList
                    .get(position);

            final HistoryListDetailsViewHolder viewHolder = (HistoryListDetailsViewHolder) holder;
            viewHolder.tvProjectname.setText(projectDetails.getprojectName());
            viewHolder.tvJobno.setText("Job#: " + projectDetails.getjobNo());
            viewHolder.tvProjectmanager.setText("Project Manager: " + projectDetails.getprojectManager());
            viewHolder.tvSuperintendent.setText("Superintendent: " + projectDetails.getsuperIntendent());
            viewHolder.tvPhotos.setText("Photos: " + projectDetails.getphotos());
            viewHolder.tvUpdates.setText("Updates: " + projectDetails.getupdates() + "  |  ");
            viewHolder.tvLastupdated.setText("Last Updated: " + projectDetails.getlastUpdate());
            viewHolder.tvRemarks.setText(projectDetails.getRemarks());
            viewHolder.historyListDetails = projectDetails;
        }

    }

    public static class HistoryListDetailsViewHolder extends RecyclerView.ViewHolder {
        public HistoryListDetails historyListDetails;
        TextView tvProjectname, tvJobno, tvSuperintendent, tvProjectmanager, tvRemarks, tvUpdates, tvPhotos, tvLastupdated;
        Button btnSafetyupdate;

        public HistoryListDetailsViewHolder(View v) {
            super(v);
            tvProjectname = (TextView) v.findViewById(R.id.tvProjectname);
            tvJobno = (TextView) v.findViewById(R.id.tvJobno);
            tvSuperintendent = (TextView) v.findViewById(R.id.tvSuperintendent);
            tvProjectmanager = (TextView) v.findViewById(R.id.tvProjectmanager);
            tvRemarks = (TextView) v.findViewById(R.id.tvRemarks);
            tvUpdates = (TextView) v.findViewById(R.id.tvUpdates);
            tvPhotos = (TextView) v.findViewById(R.id.tvPhotos);
            tvLastupdated = (TextView) v.findViewById(R.id.tvLastupdated);
            btnSafetyupdate = (Button) v.findViewById(R.id.btnSafetyupdate);
            btnSafetyupdate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
/////
                    sendDetails();
                }
            });

        }

        public void sendDetails() {

            Bundle bundle = new Bundle();
            bundle.putString("project_name" , historyListDetails.getprojectName());
            bundle.putString("project_id", historyListDetails.getProjectId());
            bundle.putString("Inspector", historyListDetails.getinspector());
            bundle.putString("project_manager", historyListDetails.getprojectManager());
            bundle.putString("job_no", historyListDetails.getjobNo());
            ///////
            bundle.putString("ludate", historyListDetails.getdatecreated());
            Log.v("ludate",historyListDetails.getdatecreated());
            bundle.putString("Superintendant",historyListDetails.getsuperIntendent());
            bundle.putString("History" , "history");
            Log.v("data", bundle.toString());
            FragmentSafetyUpdates safetyUpdates = new FragmentSafetyUpdates();
            safetyUpdates.setArguments(bundle);
            _context.getFragmentManager().beginTransaction().replace(R.id.flContainer, safetyUpdates).addToBackStack(null).commit();
        }
    }

}

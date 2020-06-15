package sooner.om.com.sooner.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sooner.om.com.sooner.R;
import sooner.om.com.sooner.helper.RequestedAppointmentDetails;


/**
 * Created by Ndroid on 1/10/2017.
 */

    public class RequestedAppointmentsAdapter extends RecyclerView.Adapter {

    private List<RequestedAppointmentDetails> requestedAppointmentsList;
    // instance of the main class will be stored here
    private Activity _context;

    public RequestedAppointmentsAdapter(List<RequestedAppointmentDetails> requestedAppointmentsList, Activity context){
        this.requestedAppointmentsList = requestedAppointmentsList;
        _context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.requested_appointment_item, parent, false);

        vh = new RequestedApptListItemsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RequestedAppointmentDetails singleReqAppDetails = requestedAppointmentsList.get(position);

        // setting the values to views
        RequestedApptListItemsViewHolder viewHolder = (RequestedApptListItemsViewHolder) holder;

        viewHolder.tvDoctorName.setText(singleReqAppDetails.getDoctorName());
        viewHolder.tvLocation.setText(singleReqAppDetails.getLocation());
        String prefTime=singleReqAppDetails.getPreferedTime().replace("Any Time - ","")+" (Preferred Time)";
        viewHolder.tvPrefTime.setText(prefTime);
        viewHolder.tvSpeaciality.setText(singleReqAppDetails.getSpeciality());
        String requestedOn="Requested On: "+singleReqAppDetails.getRequestedTime();
        viewHolder.tvReqTime.setText(requestedOn);
        viewHolder.requestedAppointments = singleReqAppDetails;
    }

    @Override
    public int getItemCount() {
        return requestedAppointmentsList.size();
    }

    public class RequestedApptListItemsViewHolder extends RecyclerView.ViewHolder {
        RequestedAppointmentDetails requestedAppointments;
        TextView tvDoctorName,tvLocation,tvSpeaciality,tvReqTime,tvPrefTime;


        RequestedApptListItemsViewHolder(View v){
            super(v);
            // linking ui elements to java classes
            tvDoctorName=(TextView)v.findViewById(R.id.tvDoctorName);
            tvLocation=(TextView)v.findViewById(R.id.tvLocation);
            tvSpeaciality=(TextView)v.findViewById(R.id.tvSpeaciality);
            tvReqTime=(TextView)v.findViewById(R.id.tvReqTime);
            tvPrefTime=(TextView)v.findViewById(R.id.tvPrefTime);

        }
    }

}

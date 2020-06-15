package sooner.om.com.sooner.adapter;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import sooner.om.com.sooner.R;
import sooner.om.com.sooner.helper.Others;
import sooner.om.com.sooner.helper.PatientAppointmentsList;


//this is an adapter class used for loading data to dashboard
@SuppressWarnings("rawtypes")
public class LoadAppointmentsAdapter extends RecyclerView.Adapter {


    private boolean featureAppointment = true;
    private List<PatientAppointmentsList> patientAppointmentsList;// appointment list will store here

    //constructor
    public LoadAppointmentsAdapter(
            List<PatientAppointmentsList> PatientAppointmentsList) {
        // assigning the data in data base to local variables
        this.patientAppointmentsList = PatientAppointmentsList;
    }

    // returns the total number of appointments count
    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return patientAppointmentsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    //holds the data into a view and displays it in the layout .
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // TODO Auto-generated method stub
        if (holder instanceof LoadAppointmentsViewHolder) {
            // retrieves the single dashboard
            PatientAppointmentsList appointmentDetails = patientAppointmentsList.get(position);
            LoadAppointmentsViewHolder viewHolder = (LoadAppointmentsViewHolder) holder;
            boolean showTime; // used to display the time or not

            int timeGapSeconds = Integer.parseInt(appointmentDetails.getTimeGap());
            // checks weather the time gap is with in the limit as per the rescheduling algorithm
            showTime = timeGapSeconds < (Integer.parseInt(appointmentDetails.getShowTimeBefore()) * 60 * 60);

            if (showTime) {
                viewHolder.tvFutureAppointments.setVisibility(View.GONE);
                viewHolder.tvNoteconstant.setVisibility(View.GONE);
                viewHolder.liDatenTime.setVisibility(View.GONE);
                viewHolder.liDate.setVisibility(View.GONE);
                viewHolder.tvAppointmentstarts.setVisibility(View.VISIBLE);
                viewHolder.tvYourAppointmenttext.setVisibility(View.VISIBLE);
                String time = Others.AppointmentDataAndTime(appointmentDetails
                        .getDate() + " " + appointmentDetails.getTime());

                viewHolder.tvPresentappointmentdateandtime.setText(time + " " + appointmentDetails.getTimeZone());
                viewHolder.tvPresentappointmentdateandtime.setVisibility(View.VISIBLE);
                viewHolder.tvtextViewTime.setVisibility(View.VISIBLE);

                viewHolder.tvtextViewTime.setText(appointmentDetails.getTimeDifference());
                // calculates the time gap and convert it to milli seconds
                long duration = timeGapSeconds * 1000;
                // displays the countdown timer in dashboard
                CounterClass timer = new CounterClass(duration, 1000,
                        viewHolder.tvtextViewTime, viewHolder.tvAppointmentstarts, viewHolder.tvNoteconstant, position);
                timer.cancel();

                timer.start();
                // if the position is 0 display the welcome message and upcoming appointment text
                if (position == 0) {
                    viewHolder.tvUsername.setText("Welcome  "
                            + appointmentDetails.getPatientName() + ",");
                    viewHolder.tvUsername.setTypeface(null, Typeface.BOLD);
                    viewHolder.tvFutureAppointment.setText(R.string.upcoming_appointments);
                    viewHolder.tvFutureAppointment.setVisibility(View.VISIBLE);

                   // display the feature appointment text only once
                    if (featureAppointment) {
                        featureAppointment = false; // feature appointment is false then tag will not display
                        viewHolder.tvFutureAppointments
                                .setVisibility(View.VISIBLE);
                        if(patientAppointmentsList.size()==1)
                            viewHolder.tvNoFeatureAppt.setVisibility(View.VISIBLE);
                    }
                } else {
                    viewHolder.tvUsername.setVisibility(View.GONE);
                }
            } else {

                viewHolder.tvTimeofappointment
                        .setText(Others.timeTo12Hours(appointmentDetails.getTime()) + " " + appointmentDetails.getTimeZone());
                viewHolder.tvDateOfAppointment.setText(Others.changeDateFormatDashBoard(appointmentDetails.getDate()).replace("/", "-"));
                // if the position is 0 display the welcome message and upcoming appointment text with out timer case
                if (position == 0) {
                    viewHolder.tvUsername.setText("Welcome  " + appointmentDetails.getPatientName() + ",");
                    viewHolder.tvUsername.setTypeface(null, Typeface.BOLD);
                    viewHolder.tvFutureAppointment.setText(R.string.upcoming_appointments);
                    viewHolder.tvFutureAppointment.setVisibility(View.VISIBLE);
                    viewHolder.liDatenTime.setVisibility(View.GONE);
                    viewHolder.liDate.setVisibility(View.GONE);
                    viewHolder.tvYourAppointmenttext
                            .setVisibility(View.VISIBLE);
                    String time = Others.AppointmentDataAndTime(appointmentDetails
                            .getDate() + " " + appointmentDetails.getTime());
                    // //log.v("new format", time);
                    viewHolder.tvPresentappointmentdateandtime.setText(time + " " + appointmentDetails.getTimeZone());
                    viewHolder.tvPresentappointmentdateandtime.setVisibility(View.VISIBLE);
                    // display the feature appointment text only once
                    if (featureAppointment) {
                        featureAppointment = false; // feature appointment is false then tag will not display
                        viewHolder.tvFutureAppointments
                                .setVisibility(View.VISIBLE);
                        if(patientAppointmentsList.size()==1)
                            viewHolder.tvNoFeatureAppt.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (featureAppointment) {
                        featureAppointment = false;
                        viewHolder.tvFutureAppointments
                                .setVisibility(View.GONE);
                        viewHolder.tvFutureAppointment
                                .setVisibility(View.VISIBLE);
                    }
                    viewHolder.tvUsername.setVisibility(View.GONE);
                    // hidding the date band in screen latest requirement on 27-12-2016
                    viewHolder.tvFeatureAppntDate.setVisibility(View.GONE);
                    viewHolder.tvFeatureAppntDate.setText(appointmentDetails.getDate());
                }

            }
            // displaying other information like name,location, speciality,time
            viewHolder.tvPhysicianname.setText("Dr. "+ appointmentDetails.getPhysicanName());
            viewHolder.tvFacilityname.setText(appointmentDetails
                    .getFacilityName());
            viewHolder.tvFacilityLocation.setText(appointmentDetails
                    .getLocation());
            viewHolder.tvSpecilaity.setText(appointmentDetails.getSpecialty());
            viewHolder.patientappointmentlist = appointmentDetails;

        }
    }

    // referencing the UI elements to code
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO Auto-generated method stub
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.appointments_list, parent, false);

        vh = new LoadAppointmentsViewHolder(v);
        return vh;
    }

    //inner class which is used to provide required timer data .
    private class CounterClass extends CountDownTimer {
        TextView timer;
        TextView tvAppintmentStarts, tvNoteconstant;
        PatientAppointmentsList appointmentDetails;

        // constructor which holds the data of countdown timer
        CounterClass(long millisInFuture, long countDownInterval,
                     TextView tvtextViewTime, TextView tvAppintmentStarts, TextView tvNoteAppoint, int position) {
            super(millisInFuture, countDownInterval);
            timer = tvtextViewTime;
            this.tvAppintmentStarts = tvAppintmentStarts;
            this.tvNoteconstant = tvNoteAppoint;
            // gets the preset appointment details access
            appointmentDetails = patientAppointmentsList.get(position);
        }

        //if the timer is finish ,it displays text "completed in the textview"
        @Override
        public void onFinish() {
            timer.setText("00:00:00");
            timer.setVisibility(View.GONE);
            appointmentDetails.setTimeGap(0 + "");
            tvAppintmentStarts.setVisibility(View.GONE);
            tvNoteconstant.setVisibility(View.VISIBLE);
        }

        //this method is used to display the countdown timer on the screen
        @SuppressLint({"NewApi", "DefaultLocale"})
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void onTick(long millisUntilFinished) {
            appointmentDetails.setTimeGap(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + "");
            String hms;
            // check weather the timer is lessthen then 5 minuets
            if (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) < 5)
                hms = String.format(
                        "%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                                .toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                                .toMinutes(millisUntilFinished)));
                // if the timer is greater then 5 minuets it will display as HH:MM
            else
                hms = String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                                .toHours(millisUntilFinished)));
            timer.setText(hms);
        }
    }

    //this is an inner class which is used to hold and display appointments in recyclerview .
    private class LoadAppointmentsViewHolder extends
            RecyclerView.ViewHolder {
        PatientAppointmentsList patientappointmentlist;
        TextView tvFutureAppointments, tvUsername,
                tvPresentappointmentdateandtime, tvPhysicianname,
                tvFacilityname, tvFacilityLocation, tvTimeofappointment,
                tvNoteconstant, tvAppointmentstarts, tvYourAppointmenttext,
                tvtextViewTime, tvFutureAppointment, tvSpecilaity, tvFeatureAppntDate, tvDateOfAppointment,tvNoFeatureAppt;
        LinearLayout liDatenTime, liTopView, liDate;

        LoadAppointmentsViewHolder(View v) {
            super(v);
            // binding UI to java file
            tvFutureAppointments = (TextView) v
                    .findViewById(R.id.tvFutureAppointments);
            tvFutureAppointment = (TextView) v
                    .findViewById(R.id.tvFutureAppointment);
            tvUsername = (TextView) v.findViewById(R.id.tvUsername);
            tvFeatureAppntDate = (TextView) v.findViewById(R.id.tvFeatureAppntDate);
            liDate = (LinearLayout) v.findViewById(R.id.liDate);
            tvDateOfAppointment = (TextView) v.findViewById(R.id.tvDateOfAppointment);
            tvPresentappointmentdateandtime = (TextView) v.findViewById(R.id.tvPresentappointmentdateandtime);
            tvPhysicianname = (TextView) v.findViewById(R.id.tvPhysicianname);
            tvFacilityname = (TextView) v.findViewById(R.id.tvFacilityname);
            tvFacilityLocation = (TextView) v
                    .findViewById(R.id.tvFacilityLocation);
            tvTimeofappointment = (TextView) v
                    .findViewById(R.id.tvTimeofappointment);
            tvNoteconstant = (TextView) v.findViewById(R.id.tvNoteconstant);
            tvAppointmentstarts = (TextView) v
                    .findViewById(R.id.tvAppointmentstarts);
            tvNoFeatureAppt=(TextView)v.findViewById(R.id.tvNoFeatureAppt);
            tvYourAppointmenttext = (TextView) v
                    .findViewById(R.id.tvYourAppointmenttext);
            tvSpecilaity = (TextView) v.findViewById(R.id.tvSpecilaity);
            tvtextViewTime = (TextView) v.findViewById(R.id.tvtextViewTime);
            liDatenTime = (LinearLayout) v.findViewById(R.id.liDatenTime);
            liTopView = (LinearLayout) v.findViewById(R.id.liTopView);

            // TODO Auto-generated constructor stub
        }

    }
}

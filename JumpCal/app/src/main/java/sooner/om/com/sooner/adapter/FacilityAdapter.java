package sooner.om.com.sooner.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import sooner.om.com.sooner.PatientLandingScreen;
import sooner.om.com.sooner.R;
import sooner.om.com.sooner.fragment.FragmentFacility;
import sooner.om.com.sooner.helper.FacilityListDetails;
import sooner.om.com.sooner.helper.Others;
import sooner.om.com.sooner.helper.SessionManager;

//this class is used to hold all the facilities and display them on the facilities screen.
@SuppressWarnings("rawtypes")
@SuppressLint("NewApi")
public class FacilityAdapter extends RecyclerView.Adapter {


    // stores the list of facilities avilable in server
    private List<FacilityListDetails> facilityList;
    // instance of the main class will be stored here
    private Activity _context;
    // list of facility views will be stored in this
    private Button btnView[];
    private SessionManager sessionManager;
    //constructor will assign all the data from server to variables
    public FacilityAdapter(List<FacilityListDetails> facilityList, Activity context) {
        this.facilityList = facilityList;
        btnView = new Button[facilityList.size()];
        _context = context;
        sessionManager= new SessionManager(_context);
    }

    // returns the size of the list
    @Override
    public int getItemCount() {
        // TODO Auto-generated method stub
        return facilityList.size();
    }

    //binds all  the information which is to be displayed .
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // TODO Auto-generated method stub

        // retrieves the single facility details from entire list and stores in separate variable
        FacilityListDetails singleFacilityDetails = facilityList.get(position);

        FacilityListItemsViewHolder viewHolder = (FacilityListItemsViewHolder) holder;

        viewHolder.btnFacilityname.setText(singleFacilityDetails.getfacilityName());

        // assigning the data to views
        btnView[position] = viewHolder.btnFacilityname;

        viewHolder.facilityListDetails = singleFacilityDetails;


    }

    // design view will be called for every element after the creation
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // TODO Auto-generated method stub
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.facilities_list, parent, false);

        vh = new FacilityListItemsViewHolder(v);
        return vh;
    }

    //this inner class is helps to hold all the view items of a layout into a main layout.
    private class FacilityListItemsViewHolder extends
            RecyclerView.ViewHolder {
        FacilityListDetails facilityListDetails;
        Button btnFacilityname;

        FacilityListItemsViewHolder(View v) {
            super(v);
            // referring the UI to the class file
            btnFacilityname = (Button) v.findViewById(R.id.btnFacilityname);
            // adding listener to the button
            // TODO Auto-generated constructor stub
            btnFacilityname.setOnClickListener(new OnClickListener() {
                // this will trigger after clicking on button
                @Override
                public void onClick(View v) {
                    // removing the existing data in the screen then load the fresh data with selected facility
                    clearAllViews();
                    Others.displayTextColor(btnFacilityname, R.color.white, _context);
                    // changing the entire application to other facility by assigning the value
                    PatientLandingScreen.selectedFacilityName = facilityListDetails
                            .getfacilityName();
                    PatientLandingScreen.facilityId = facilityListDetails
                            .getfacilityId();
                    sessionManager.updateLoginSession(facilityListDetails
                            .getfacilityId(),facilityListDetails
                            .getfacilityName());
                    // reload the screen
                    reload();
                }
            });

        }

        //this method is used to reload the facilities
        private void reload() {
            // TODO Auto-generated method stub
            FragmentFacility request = new FragmentFacility();
            _context.getFragmentManager().beginTransaction()
                    .replace(R.id.flContainer, request).addToBackStack(null)
                    .commit();
        }

        //this method is used to claer all facilities
        @SuppressWarnings("deprecation")
        void clearAllViews() {
            // TODO Auto-generated method stub
            for (Button aBtnView : btnView) {

                Others.displayBackGroundColor(aBtnView, R.color.white, _context);
                Others.displayTextColor(aBtnView, R.color.new_selected, _context);
            }
        }
    }
}


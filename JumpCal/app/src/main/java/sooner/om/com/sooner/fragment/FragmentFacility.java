package sooner.om.com.sooner.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sooner.om.com.sooner.PatientLandingScreen;
import sooner.om.com.sooner.R;
import sooner.om.com.sooner.adapter.FacilityAdapter;
import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.helper.FacilityListDetails;
import sooner.om.com.sooner.network.ConnectionDetector;
import sooner.om.com.sooner.network.ServerCall;

public class FragmentFacility extends Fragment implements ServerCall.AsyncTaskInterface {
    View rootView;

    private TextView tvEmptyView, tvText;
    private SearchView svFacilityName;
    private RecyclerView mRecyclerView;
    private ConnectionDetector con;
    private FacilityAdapter mAdapter;
    private List<FacilityListDetails> facilityList = null;
    // used to find the search result in the facility list
    @SuppressLint("DefaultLocale")
    SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String query) {
            query = query.toLowerCase();
            // assign the filtered list based on search in new array list
            final List<FacilityListDetails> filteredList = new ArrayList<>();
            // logic to filer as per user selection
            for (int i = 0; i < facilityList.size(); i++) {
                FacilityListDetails list = facilityList.get(i);
                tvText.setVisibility(View.GONE);
                if (list.getfacilityName().toLowerCase().contains(query)) {
                    String id = list.getfacilityId();
                    String name = list.getfacilityName();
                    String[] finalString = {id, name};
                    filteredList.add(new FacilityListDetails(finalString));
                }
            }
            // if the filtered list is having a minimum of one value then display the facilities
            if (filteredList.size() > 0) {
                mRecyclerView.setVisibility(View.VISIBLE);
                tvEmptyView.setVisibility(View.GONE);
            }// else empty view will display
            else {
                mRecyclerView.setVisibility(View.GONE);
                tvEmptyView.setVisibility(View.VISIBLE);
            }
            // adding the list to adapter class
            mAdapter = new FacilityAdapter(filteredList, getActivity());
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged(); // notify to the screen that the data set changed
            return true;

        }

        public boolean onQueryTextSubmit(String query) {
            return false;
        }
    };

    //checks for the "value" in the json object with corresponding key.
    public static String optString(JSONObject json, String key) {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key) || json.optString(key).equals("-1")
                || json.optString(key).equals("-1.00"))
            return "";
        else
            return json.optString(key);
    }

    //first executed method.
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        rootView = inflater.inflate(R.layout.fragment_facilities, container,
                false);
        initialize();
        loadData();
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        // use a linear layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        svFacilityName.setQuery("", false);
        rootView.requestFocus();

    }
    // request made to the server for list of facilities available
    private void loadData() {
        // TODO Auto-generated method stub
        try {
            // adding required parameters to the json object
            JSONObject facilities = new JSONObject();
            facilities.put("_AuthenticationToken",
                    PatientLandingScreen.authenticationToken);
            facilities.put("_UserId", PatientLandingScreen.userId);
            facilities.put("_UserType", "CLIENT");
            // checking for the internet connection
            if (con.isConnectingToInternet()) {
                getFacilityList(facilities);
            } else {
                con.failureAlert();
            }
            // loadInitialBars(bars);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    //getting facility list from server.
    private void getFacilityList(JSONObject facilities) {
        // TODO Auto-generated method stub
        ServerCall serverCall = new ServerCall(getActivity(), FragmentFacility.this);
        serverCall.postUrlRequest(Config.URL_GET_FACILITIES, facilities, "facility");
    }

    //adding the facilities to a list.
    private void loadFacilityList(JSONObject response) {
        // TODO Auto-generated method stub
        try {

            Log.v("output responce", response.toString());


            JSONArray facilityData = response.optJSONArray("UserFacilities");

            // adding the facilities to the arrayList
            for (int i = 0; i < facilityData.length(); i++) {
                JSONObject singleUser = facilityData.getJSONObject(i);
                String facility_id = singleUser.getString("FacilityId");
                String facility_username = optString(singleUser, "FacilityName");
                String tickers = singleUser.getString("UnreadNotifications");
                Log.v("UnreadNotifications",tickers+"");
                if(PatientLandingScreen.selectedFacilityName.equals(facility_username))
                if (Integer.parseInt(tickers) > 0) {
                    PatientLandingScreen.tvTicker.setText(tickers);
                    PatientLandingScreen.tvTicker.setVisibility(View.VISIBLE);
                } else {
                    PatientLandingScreen.tvTicker.setVisibility(View.GONE);
                }
                String[] finalString = {facility_id, facility_username};
                if (!PatientLandingScreen.selectedFacilityName.equals(facility_username)) {
                    // adding data to the arrayList
                    facilityList.add(new FacilityListDetails(finalString));
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    //initialising all the attributes in this class.
    private void initialize() {
        // TODO Auto-generated method stub
        // connection object
        con = new ConnectionDetector(getActivity());
        facilityList = new ArrayList<>();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvFacilityList);
        tvEmptyView = (TextView) rootView.findViewById(R.id.tvEmptyView);
        tvText = (TextView) rootView.findViewById(R.id.tvText);
        TextView tvSelectedFacilityName = (TextView) rootView.findViewById(R.id.tvSelectedFacilityName);
        svFacilityName = (SearchView) rootView.findViewById(R.id.svFacilityName);
        // adding listener to searchView
        svFacilityName.setOnQueryTextListener(listener);
        svFacilityName.setQueryHint("Search Facility");
        svFacilityName.setIconifiedByDefault(false);
        tvSelectedFacilityName.setText(PatientLandingScreen.selectedFacilityName);

    }

    // handling the server response in this class

    @Override
    public void onAsyncTaskInterfaceResponse(JSONObject result, String tag) {
        loadFacilityList(result);

        mAdapter = new FacilityAdapter(facilityList, getActivity());

        mRecyclerView.setAdapter(mAdapter);


        if (facilityList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyView.setVisibility(View.VISIBLE);

        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyView.setVisibility(View.GONE);
        }
    }
}
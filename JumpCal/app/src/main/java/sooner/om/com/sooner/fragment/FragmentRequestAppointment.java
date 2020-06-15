package sooner.om.com.sooner.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sooner.om.com.sooner.PatientLandingScreen;
import sooner.om.com.sooner.R;
import sooner.om.com.sooner.RequestedAppointments;
import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.helper.CustomizeDialog;
import sooner.om.com.sooner.helper.Others;
import sooner.om.com.sooner.network.ConnectionDetector;
import sooner.om.com.sooner.network.ServerCall;

//this class is used to request an appointment for a facility.
public class FragmentRequestAppointment extends Fragment implements
        OnClickListener, OnItemSelectedListener, CustomizeDialog.AsyncTaskInterface, ServerCall.AsyncTaskInterface {
    View rootView;

    private Spinner spCity, spSpecialty, spPhysician, spPTime;
    private String selectedSpeciality, selectedCity, selectedPhysician, selectedTime;
    private String selectedSpecialityId, selectedCityId;
    private List<String> liCity, liSpecialty, liPhysician, liPTime;
    private List<String> liCityKeys, liSpecialtyKeys, liPhysicianKeys;
    private JSONObject response;
    private JSONArray selectedResponse;
    private ConnectionDetector con;
    private boolean allSelected = false;
    private ServerCall serverCall;
    private RelativeLayout llSpeciality, llCity, llPTime, llPhysician;

    // first executable method
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        rootView = inflater.inflate(R.layout.fragment_request_appointment,
                container, false);
        initialize();
        loadData();

        return rootView;
    }

    // server request for loading initial data
    private void loadData() {
        // parameters required to get the response from server
        JSONObject obj = new JSONObject();
        try {
            obj.put("_UserId", PatientLandingScreen.userId);
            obj.put("_AuthenticationToken",
                    PatientLandingScreen.authenticationToken);
            obj.put("_FacilityId", PatientLandingScreen.facilityId);
            serverCall = new ServerCall(getActivity(), FragmentRequestAppointment.this);
            serverCall.postUrlRequest(Config.URL_GET_DROPDOWN_DETAILS, obj, "requestAppointment");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // load data to spinners based on selection criteria
    protected void loadDataToSpinners(JSONObject response) {
        // TODO Auto-generated method stub
        try {
            this.response = response;
            JSONArray specialty = response.getJSONArray("SpecialtyJson");
            // loading the specialty to the spinner
            for (int i = 0; i < specialty.length(); i++) {
                JSONObject obj = specialty.getJSONObject(i);
                liSpecialty.add(obj.getString("SpecialtyName"));
                liSpecialtyKeys.add(obj.getString("SpecialtyId"));
            }
            // adding the specialities to spinner
            addToSpinner(liSpecialty, spSpecialty);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // adding data to spinner.
    public void addToSpinner(final List<String> elements, final Spinner spnTemp) {
        //Log.v("request appointment", elements.size() + "");
        // removing the heading if there are only one data in list
        if (elements.size() == 2) {
            elements.remove(0);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, elements) {
            // this will run if nothing is selected
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //v.setMinimumHeight((int) (40*cx.getResources().getDisplayMetrics().density));
                ((TextView) v).setTextSize(16);

                ((TextView) v).setTextColor(getResources().getColor(R.color.new_selected));
                // any of the below is elected place the arrow else place the tick mark
                if (getItem(position).equals("Select Specialty")
                        || getItem(position).equals("Select Location")
                        || getItem(position).equals("Select Physician")
                        || getItem(position).equals("Preferred Time")) {

                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.right_arrow, 0);
                    // ((TextView) v).setVisibility(View.GONE);
                    return v;
                } else {
                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.done, 0);
                    return v;


                }
            }

            // this will run when the drop down is clicked
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {

                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextSize(16);
                v.setPadding(30, 20, 20, 20);
                ((TextView) v).setTypeface(null, Typeface.NORMAL);
                Others.displayTextColor((TextView) v, R.color.new_selected, getActivity());
                // increasing the size of the heading
                if (getItem(position).equals("Select Specialty")
                        || getItem(position).equals("Select Location")
                        || getItem(position).equals("Select Physician")
                        || getItem(position).equals("Preferred Time")) {
                    ((TextView) v).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    Others.displayTextColor((TextView) v, R.color.new_selected, getActivity());
                    ((TextView) v).setTypeface(null, Typeface.BOLD);
                    ((TextView) v).setTextSize(20);
                    v.setPadding(50, 20, 20, 20);

                    return v;
                }
                return v;
            }

        };
        // setting the adapter to spinner
        spnTemp.setAdapter(adapter);

    }

    // initialises all the attributes in this class.
    public void initialize() {

        Button btnReqappointment = (Button) rootView
                .findViewById(R.id.btnReqappointment);
        Button btnRequestedAppointment = (Button) rootView.findViewById(R.id.btnRequestedAppointment);
        btnRequestedAppointment.setOnClickListener(this);
        btnReqappointment.setOnClickListener(this);
        spCity = (Spinner) rootView.findViewById(R.id.spCity);
        spSpecialty = (Spinner) rootView.findViewById(R.id.spSpecialty);
        spPhysician = (Spinner) rootView.findViewById(R.id.spPhysician);
        spPTime = (Spinner) rootView.findViewById(R.id.spPTime);

        llSpeciality = (RelativeLayout) rootView.findViewById(R.id.liSpecialty);
        llCity = (RelativeLayout) rootView.findViewById(R.id.liCity);
        llPhysician = (RelativeLayout) rootView.findViewById(R.id.liPhysician);
        llPTime = (RelativeLayout) rootView.findViewById(R.id.liPTime);

        // initialising the city and speciality list
        liCity = new ArrayList<>();
        liSpecialty = new ArrayList<>();
        liSpecialty.add("Select Specialty");
        addToSpinner(liSpecialty, spSpecialty);
        liCity.add("Select Location");

        addToSpinner(liCity, spCity);
        liPhysician = new ArrayList<>();
        liPhysician.add("Select Physician");
        addToSpinner(liPhysician, spPhysician);
        liPTime = Arrays.asList(getResources().getStringArray(R.array.time));
        liCityKeys = new ArrayList<>();
        liSpecialtyKeys = new ArrayList<>();
        liPhysicianKeys = new ArrayList<>();
        addToSpinner(liPTime, spPTime);

        // adding listener to the speciality
        spSpecialty.setOnItemSelectedListener(this);
        // making all the others are non clicable untile the above one is selected
        spCity.setEnabled(false);
        spPhysician.setEnabled(false);
        spPTime.setEnabled(false);
        spCity.setOnItemSelectedListener(this);
        spPhysician.setOnItemSelectedListener(this);
        spPTime.setOnItemSelectedListener(this);

        con = new ConnectionDetector(getActivity());

    }

    // on click function will trigger when ever the button is clicked
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnReqappointment:
                requestAppointment();
                break;
            case R.id.btnRequestedAppointment:
                Intent i = new Intent(getActivity(), RequestedAppointments.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    // request appointment
    private void requestAppointment() {
        // TODO Auto-generated method stub

        selectedTime = spPTime.getSelectedItem().toString();
        // Log.v("Values", selectedSpeciality + "" + selectedCity + ""+
        // selectedPhysician + "" + selectedTime + "");
        if (selectedSpeciality.equals("Select Speciality")
                && selectedCity.equals("Select Location")
                && selectedPhysician.equals("Select Physician")
                && selectedTime.equals("Preferred Time")) {
            Toast.makeText(getActivity(), "Please select Speciality",
                    Toast.LENGTH_SHORT).show();
        } else if (selectedSpeciality.equals("Select Specialty")) {
            Toast.makeText(getActivity(), "Please select Specialty",
                    Toast.LENGTH_SHORT).show();
        } else if (selectedCity.equals("Select Location")) {
            Toast.makeText(getActivity(), "Please Select Location",
                    Toast.LENGTH_SHORT).show();
        } else if (selectedPhysician.equals("Select Physician")) {
            Toast.makeText(getActivity(), "Please select Physician",
                    Toast.LENGTH_SHORT).show();
        } else if (selectedTime.equals("Preferred Time")) {
            Toast.makeText(getActivity(), "Please select Time",
                    Toast.LENGTH_SHORT).show();
        } else {
            int Specialitypos = liSpecialty.indexOf(selectedSpeciality);

            int Citypos = liCity.indexOf(selectedCity);

            int Physicianpos = liPhysician.indexOf(selectedPhysician);
            // Log.v("request appointment", Specialitypos + "/" +
            // Citypos+"/"+Physicianpos+"/"+selectedPhysician);

            String specialityID, locationID, physicianID;


            if (Physicianpos == 0)
                physicianID = liPhysicianKeys.get(Physicianpos);
            else
                physicianID = liPhysicianKeys.get(Physicianpos - 1);

            try {
                /*Others.displayBackGround(liReqappointment,
						R.drawable.change_password_button_selector, getActivity());*/
                JSONObject obj = new JSONObject();
                obj.put("_UserId", PatientLandingScreen.userId);
                obj.put("_AuthenticationToken",
                        PatientLandingScreen.authenticationToken);
                obj.put("_FacilityId", PatientLandingScreen.facilityId);
                obj.put("_SpecialtyId", selectedSpecialityId);
                obj.put("_LocationId", selectedCityId);
                obj.put("_PhysicianId", physicianID);
                obj.put("_AppointmentType", selectedTime);
                // Log.v("hie", obj.toString());
                if (con.isConnectingToInternet()) {
                    requestappointment(obj);
                } else {
                    con.failureAlert();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // request appointment to the server
    private void requestappointment(JSONObject obj) {
        // TODO Auto-generated method stub
        serverCall.postUrlRequest(Config.URL_REQUEST_APPOINTMENT, obj, "requestAnAppointment");

    }

    // this is the main method which holds the logic of assigning the data to spinners based on the selection happen
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        // TODO Auto-generated method stub
        Spinner spinner = (Spinner) parent;
        switch (spinner.getId()) {

            case R.id.spSpecialty:

                selectedSpeciality = parent.getItemAtPosition(position).toString();
                // if nothing is selected then clear all the fields
                if (selectedSpeciality.equals("Select Specialty")) {
                    liCity.clear();
                    liCityKeys.clear();
                    liCity.add("Select Location");
                    liPhysician.clear();
                    liPhysicianKeys.clear();
                    liPhysician.add("Select Physician");
                    allSelected = false;

                    Others.displayBackGround(llSpeciality,
                            R.drawable.spinner_selector_not_selected, getActivity());
                    addToSpinner(liCity, spCity);
                    addToSpinner(liPhysician, spPhysician);
                    resetAllSpinners();

                }// if any of the speciality is selected then filter the data based on the speciality
                else {
                    try {
                        resetAllSpinners();
                        spCity.setEnabled(true);
                        Others.displayBackGround(llCity,
                                R.drawable.spinner_selector_not_selected, getActivity());
                        Others.displayBackGround(llSpeciality, R.drawable.green_stroke, getActivity());
                        JSONArray specialty = response
                                .getJSONArray("SpecialtyJson");
                        for (int i = 0; i < specialty.length(); i++) {
                            JSONObject obj = specialty.getJSONObject(i);
                            String spec = obj.getString("SpecialtyId");
                            if (position == 0)
                                selectedSpecialityId = liSpecialtyKeys.get(position);
                            else
                                selectedSpecialityId = liSpecialtyKeys.get(position - 1);
                            if (spec.equals(selectedSpecialityId)) {
                                JSONArray location = obj.getJSONArray("Locations");
                                selectedResponse=location;
                                liCity.clear();
                                liCityKeys.clear();
                                liCity.add("Select Location");
                                liPhysician.clear();
                                liPhysicianKeys.clear();
                                liPhysician.add("Select Physician");

                                for (int j = 0; j < location.length(); j++) {
                                    JSONObject locationObj = location
                                            .getJSONObject(j);
                                    liCity.add(locationObj
                                            .getString("LocationName"));
                                    liCityKeys.add(locationObj
                                            .getString("LocationId"));
                                }
                                addToSpinner(liCity, spCity);
                                addToSpinner(liPhysician, spPhysician);

                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.spCity:
                selectedCity = parent.getItemAtPosition(position).toString();
                Log.v("request",parent.getItemAtPosition(position)+" "+position);

                // if non of the location is selected reset the remaining fields
                if (selectedCity.equals("Select Location")) {

                    spPhysician.setEnabled(false);
                    liPhysician.clear();
                    liPhysicianKeys.clear();
                    liPhysician.add("Select Physician");
                    addToSpinner(liPhysician, spPhysician);
                    if (spCity.isEnabled())
                        Others.displayBackGround(llCity,
                                R.drawable.spinner_selector_not_selected, getActivity());

                }// if the location is selected then populate the result based on the selected location
                else {
                    try {
                        spPhysician.setEnabled(true);
                        Others.displayBackGround(llCity,
                                R.drawable.green_stroke, getActivity());
                        Others.displayBackGround(llPhysician,
                                R.drawable.spinner_selector_not_selected, getActivity());
                        Others.displayBackGround(llSpeciality, R.drawable.green_stroke, getActivity());
                        spPTime.setAdapter(null);
                        addToSpinner(liPTime, spPTime);
                          JSONArray city = selectedResponse;
                                for (int j = 0; j < city.length(); j++) {
                                    JSONObject cty = city.getJSONObject(j);
                                    String cit = cty.getString("LocationId");
                                    if (position == 0)
                                        selectedCityId = liCityKeys.get(position);
                                    else
                                        selectedCityId = liCityKeys.get(position - 1);
                                    if (cit.equals(selectedCityId)) {
                                        JSONArray physicians = cty
                                                .getJSONArray("Physicians");
                                        liPhysician.clear();
                                        liPhysicianKeys.clear();
                                        liPhysician.add("Select Physician");
                                        for (int k = 0; k < physicians.length(); k++) {
                                            JSONObject locationObj = physicians
                                                    .getJSONObject(k);
                                            liPhysician.add(locationObj
                                                    .getString("PhysicianName"));
                                            liPhysicianKeys.add(locationObj
                                                    .getString("PhysicianId"));
                                        }
                                        addToSpinner(liPhysician, spPhysician);
                                    }
                         }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.spPhysician:
                selectedPhysician = parent.getItemAtPosition(position).toString();
                // if the physician is selected then this will execute
                if (!selectedPhysician.equals("Select Physician")) {
                    allSelected = true;
                    spPTime.setEnabled(true);
                    spPTime.setAdapter(null);
                    addToSpinner(liPTime, spPTime);
                    Others.displayBackGround(llPhysician,
                            R.drawable.green_stroke, getActivity());
                    Others.displayBackGround(llPTime,
                            R.drawable.spinner_selector_not_selected, getActivity());
                    Others.displayBackGround(llSpeciality, R.drawable.green_stroke, getActivity());

                }// if the physician is not selected then reset the remaining time spinner
                else {
                    spPTime.setEnabled(false);
                    if (spPhysician.isEnabled())
                        Others.displayBackGround(llPhysician, R.drawable.spinner_selector_not_selected, getActivity());
                }

                break;
            case R.id.spPTime:
                selectedTime = parent.getItemAtPosition(position).toString();
                // if the time is selected then the user is ready to request an appointment
                if (!selectedTime.equals("Preferred Time")) {
                    if (allSelected) {
                        Others.displayBackGround(llPTime,
                                R.drawable.green_stroke, getActivity());
                    }
                } else {
                    if (spPTime.isEnabled())
                        Others.displayBackGround(llPTime,
                                R.drawable.spinner_selector_not_selected, getActivity());
                }

                break;

            default:
                break;
        }

    }

    // reset all spinners
    public void resetAllSpinners() {
        spCity.setEnabled(false);
        spPhysician.setEnabled(false);
        spPTime.setEnabled(false);

        Others.displayBackGround(llPhysician, R.drawable.spinner_selector_not_selected,
                getActivity());
        Others.displayBackGround(llCity, R.drawable.spinner_selector_not_selected,
                getActivity());
        Others.displayBackGround(llPTime, R.drawable.spinner_selector_not_selected,
                getActivity());

		/*Others.displayBackGround(liReqappointment,
				R.drawable.ash_button_selector, getActivity());*/

        spPTime.setAdapter(null);
        addToSpinner(liPTime, spPTime);
    }

    // if nothing selected
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

    // after requesting an appointment reload the page
    @Override
    public void onAsyncTaskInterfaceResponse(String result) {
        Fragment frg;
        frg = getFragmentManager().findFragmentById(R.id.flContainer);
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    // after requesting an appointment show the dialog with success message
    @Override
    public void onAsyncTaskInterfaceResponse(JSONObject result, String tag) {
        if (tag.equals("requestAppointment"))
            loadDataToSpinners(result);
        else {
            CustomizeDialog customizeDialog = new CustomizeDialog(getActivity(), "requestAppointment", FragmentRequestAppointment.this);
            customizeDialog.setTitle("Thank you for requesting an Appointment");
            customizeDialog.setMessage("You will receive a callback within one business day!");
            customizeDialog.show();
        }
    }
}

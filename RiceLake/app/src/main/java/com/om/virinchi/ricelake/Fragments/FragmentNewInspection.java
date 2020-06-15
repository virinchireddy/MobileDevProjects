package com.om.virinchi.ricelake.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.om.virinchi.ricelake.Activites.LandingScreen;
import com.om.virinchi.ricelake.Helper.SessionManager;
import com.om.virinchi.ricelake.Network.AppController;
import com.om.virinchi.ricelake.Network.Config;
import com.om.virinchi.ricelake.Network.ConnectionDetector;
import com.om.virinchi.ricelake.Network.Others;
import com.om.virinchi.ricelake.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Virinchi on 9/14/2016.
 */
public class FragmentNewInspection extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static String btFreezeUpdate;
    public static Button btnComments, btnPhotos, btnreferences, btnUpdate, btnCancel, btnProjectname;

    int Count = 0, Count1 = 0, Count2 = 0, Count3 = 0, Count4 = 0, Count5 = 0, Count6 = 0, Count7 = 0, Count8 = 0, Count9 = 0,
            Count10 = 0, OK = 5, ACT = 6, NA = 7;
    String commentcontrol = "null";
    ConnectionDetector con;
    Others others;
    JSONObject bids;
    SessionManager session;
    String project_id, project_name, history;
    LinearLayout liErosioncontrol, liGeneralsafety, liUtilities, liHousekeeping, liFireprotection, liExcavation, liFallprotection, liEquipment, liScaffold, liTools, liSubcontractor,
            liErosioncontrollineitem, liGeneralsafetylineitems, liUtilitylineitems, liHousekeepinglineitems, liFireprotectionlineitems,
            liEvacuationlineitems, liFallprotectionlineitems, liEquipmentlineitems, liScaffoldlineitems, liToolslineitems, liSubcontractorlineitems;
    RadioButton rd1ok, rd1act, rd1na, rd2ok, rd2act, rd2na, rd3ok, rd3act, rd3na, rd4ok, rd4act, rd4na, rd5ok, rd5act, rd5na,
            rd6ok, rd6act, rd6na, rd7ok, rd7act, rd7na, rd8ok, rd8act, rd8na, rd9ok, rd9act, rd9na, rd10ok, rd10act, rd10na,
            rd11ok, rd11act, rd11na, rd12ok, rd12act, rd12na, rd13ok, rd13act, rd13na, rd14ok, rd14act, rd14na, rd15ok,
            rd15act, rd15na, rd16ok, rd16act, rd16na, rd17ok, rd17act, rd17na, rd18ok, rd18act, rd18na, rd19ok, rd19act, rd19na,
            rd20ok, rd20act, rd20na, rd21ok, rd21act, rd21na, rd22ok, rd22act, rd22na, rd23ok, rd23act, rd23na,
            rd24ok, rd24act, rd24na, rd25ok, rd25act, rd25na, rd26ok, rd26act, rd26na, rd27ok, rd27act, rd27na, rd28ok, rd28act, rd28na, rd29ok,
            rd29act, rd29na, rd30ok, rd30act, rd30na, rd31ok, rd31act, rd31na, rd32ok, rd32act, rd32na, rd33ok, rd33act, rd33na,
            rd34ok, rd34act, rd34na, rd35ok, rd35act, rd35na, rd36ok, rd36act, rd36na, rd37ok, rd37act, rd37na, rd38ok, rd38act, rd38na,
            rd39ok, rd39act, rd39na, rd40ok, rd40act, rd40na, rd41ok, rd41act, rd41na, rd42ok, rd42act, rd42na, rd43ok, rd43act, rd43na,
            rd44ok, rd44act, rd44na, rd45ok, rd45act, rd45na, rd46ok, rd46act, rd46na, rd47ok, rd47act, rd47na, rd48ok, rd48act, rd48na,
            rd49ok, rd49act, rd49na, rd50ok, rd50act, rd50na, rd51ok, rd51act, rd51na, rd52ok, rd52act, rd52na, rd53ok, rd53act, rd53na, rd54ok, rd54act, rd54na,
            rd55ok, rd55act, rd55na, rd56ok, rd56act, rd56na, rd57ok, rd57act, rd57na, rd58ok, rd58act, rd58na, rd59ok, rd59act, rd59na;
    View rootView;


    String update_id, navigation, job_no;
    public String nw;
    TextView tvErosionControl, tvGeneralSafety, tvUtilities, tvHouseKeeping, tvFireProtection, tvExcavation, tvFallProtection, tvEquipment, tvScaffold, tvTools, tvSubcontractorSafety;

    int updateId;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        rootView = inflater.inflate(R.layout.fragment_new_inspection,
                container, false);
        savedInstanceState = getArguments();
        update_id = ((AppController) getActivity().getApplication()).getUpdateVariable();
        nw  = ((AppController) getActivity().getApplication()).getNew();
        Log.v("images" , nw.toString());
        // update_id = savedInstanceState.getString("update_id");
        updateId = Integer.parseInt(update_id);

        navigation = savedInstanceState.getString("navigation");


        project_name = savedInstanceState.getString("projectName");
        project_id = savedInstanceState.getString("project_id");
        history = savedInstanceState.getString("History");
        job_no = savedInstanceState.getString("job_no");
        Log.v("pid", project_id.toString());
        Log.v("job_no ", job_no.toString());


        intilize();


        receiveradiovalues();


        return rootView;


    }

    public void intilize() {
        LandingScreen.ivBack.setVisibility(View.VISIBLE);
        others = new Others(getActivity());
        con = new ConnectionDetector(getActivity());
        session = new SessionManager(getActivity());
        bids = new JSONObject();
        btnProjectname = (Button) rootView.findViewById(R.id.btnProjectname);
        btnProjectname.setText(project_name + " - Audit");
        btnComments = (Button) rootView.findViewById(R.id.btnComments);
        btnComments.setOnClickListener(this);
        btnPhotos = (Button) rootView.findViewById(R.id.btnPhotos);
        btnPhotos.setOnClickListener(this);
        btnreferences = (Button) rootView.findViewById(R.id.btnreferences);
        liErosioncontrol = (LinearLayout) rootView.findViewById(R.id.liErosioncontrol);
        liErosioncontrol.setOnClickListener(this);
        liGeneralsafety = (LinearLayout) rootView.findViewById(R.id.liGeneralsafety);
        liGeneralsafety.setOnClickListener(this);
        liUtilities = (LinearLayout) rootView.findViewById(R.id.liUtilities);
        liUtilities.setOnClickListener(this);
        liHousekeeping = (LinearLayout) rootView.findViewById(R.id.liHousekeeping);
        liHousekeeping.setOnClickListener(this);
        liFireprotection = (LinearLayout) rootView.findViewById(R.id.liFireprotection);
        liFireprotection.setOnClickListener(this);
        liExcavation = (LinearLayout) rootView.findViewById(R.id.liExcavation);
        liExcavation.setOnClickListener(this);
        liFallprotection = (LinearLayout) rootView.findViewById(R.id.liFallprotection);
        liFallprotection.setOnClickListener(this);
        liEquipment = (LinearLayout) rootView.findViewById(R.id.liEquipment);
        liEquipment.setOnClickListener(this);
        liScaffold = (LinearLayout) rootView.findViewById(R.id.liScaffold);
        liScaffold.setOnClickListener(this);
        liTools = (LinearLayout) rootView.findViewById(R.id.liTools);
        liTools.setOnClickListener(this);
        liSubcontractor = (LinearLayout) rootView.findViewById(R.id.liSubcontractor);
        liSubcontractor.setOnClickListener(this);
        liErosioncontrollineitem = (LinearLayout) rootView.findViewById(R.id.liErosioncontrollineitem);
        liGeneralsafetylineitems = (LinearLayout) rootView.findViewById(R.id.liGeneralsafetylineitems);
        liUtilitylineitems = (LinearLayout) rootView.findViewById(R.id.liUtilitylineitems);
        liHousekeepinglineitems = (LinearLayout) rootView.findViewById(R.id.liHousekeepinglineitems);
        liFireprotectionlineitems = (LinearLayout) rootView.findViewById(R.id.liFireprotectionlineitems);
        liEvacuationlineitems = (LinearLayout) rootView.findViewById(R.id.liEvacuationlineitems);
        liFallprotectionlineitems = (LinearLayout) rootView.findViewById(R.id.liFallprotectionlineitems);
        liEquipmentlineitems = (LinearLayout) rootView.findViewById(R.id.liEquipmentlineitems);
        liScaffoldlineitems = (LinearLayout) rootView.findViewById(R.id.liScaffoldlineitems);
        liToolslineitems = (LinearLayout) rootView.findViewById(R.id.liToolslineitems);
        liSubcontractorlineitems = (LinearLayout) rootView.findViewById(R.id.liSubcontractorlineitems);
        rd1ok = (RadioButton) rootView.findViewById(R.id.rd1ok);
        rd1ok.setOnCheckedChangeListener(this);
        rd1act = (RadioButton) rootView.findViewById(R.id.rd1act);
        rd1act.setOnCheckedChangeListener(this);
        rd1na = (RadioButton) rootView.findViewById(R.id.rd1na);
        rd1na.setOnCheckedChangeListener(this);
        rd2ok = (RadioButton) rootView.findViewById(R.id.rd2ok);
        rd2ok.setOnCheckedChangeListener(this);
        rd2act = (RadioButton) rootView.findViewById(R.id.rd2act);
        rd2act.setOnCheckedChangeListener(this);
        rd2na = (RadioButton) rootView.findViewById(R.id.rd2na);
        rd2na.setOnCheckedChangeListener(this);
        rd3ok = (RadioButton) rootView.findViewById(R.id.rd3ok);
        rd3ok.setOnCheckedChangeListener(this);
        rd3act = (RadioButton) rootView.findViewById(R.id.rd3act);
        rd3act.setOnCheckedChangeListener(this);
        rd3na = (RadioButton) rootView.findViewById(R.id.rd3na);
        rd3na.setOnCheckedChangeListener(this);
        rd4ok = (RadioButton) rootView.findViewById(R.id.rd4ok);
        rd4ok.setOnCheckedChangeListener(this);
        rd4act = (RadioButton) rootView.findViewById(R.id.rd4act);
        rd4act.setOnCheckedChangeListener(this);
        rd4na = (RadioButton) rootView.findViewById(R.id.rd4na);
        rd4na.setOnCheckedChangeListener(this);
        rd5ok = (RadioButton) rootView.findViewById(R.id.rd5ok);
        rd5ok.setOnCheckedChangeListener(this);
        rd5act = (RadioButton) rootView.findViewById(R.id.rd5act);
        rd5act.setOnCheckedChangeListener(this);
        rd5na = (RadioButton) rootView.findViewById(R.id.rd5na);
        rd5na.setOnCheckedChangeListener(this);
        rd6ok = (RadioButton) rootView.findViewById(R.id.rd6ok);
        rd6ok.setOnCheckedChangeListener(this);
        rd6act = (RadioButton) rootView.findViewById(R.id.rd6act);
        rd6act.setOnCheckedChangeListener(this);
        rd6na = (RadioButton) rootView.findViewById(R.id.rd6na);
        rd6na.setOnCheckedChangeListener(this);
        rd7ok = (RadioButton) rootView.findViewById(R.id.rd7ok);
        rd7ok.setOnCheckedChangeListener(this);
        rd7act = (RadioButton) rootView.findViewById(R.id.rd7act);
        rd7act.setOnCheckedChangeListener(this);
        rd7na = (RadioButton) rootView.findViewById(R.id.rd7na);
        rd7na.setOnCheckedChangeListener(this);
        rd8ok = (RadioButton) rootView.findViewById(R.id.rd8ok);
        rd8ok.setOnCheckedChangeListener(this);
        rd8act = (RadioButton) rootView.findViewById(R.id.rd8act);
        rd8act.setOnCheckedChangeListener(this);
        rd8na = (RadioButton) rootView.findViewById(R.id.rd8na);
        rd8na.setOnCheckedChangeListener(this);
        rd9ok = (RadioButton) rootView.findViewById(R.id.rd9ok);
        rd9ok.setOnCheckedChangeListener(this);
        rd9act = (RadioButton) rootView.findViewById(R.id.rd9act);
        rd9act.setOnCheckedChangeListener(this);
        rd9na = (RadioButton) rootView.findViewById(R.id.rd9na);
        rd9na.setOnCheckedChangeListener(this);
        rd10ok = (RadioButton) rootView.findViewById(R.id.rd10ok);
        rd10ok.setOnCheckedChangeListener(this);
        rd10act = (RadioButton) rootView.findViewById(R.id.rd10act);
        rd10act.setOnCheckedChangeListener(this);
        rd10na = (RadioButton) rootView.findViewById(R.id.rd10na);
        rd10na.setOnCheckedChangeListener(this);
        rd11ok = (RadioButton) rootView.findViewById(R.id.rd11ok);
        rd11ok.setOnCheckedChangeListener(this);
        rd11act = (RadioButton) rootView.findViewById(R.id.rd11act);
        rd11act.setOnCheckedChangeListener(this);
        rd11na = (RadioButton) rootView.findViewById(R.id.rd11na);
        rd11na.setOnCheckedChangeListener(this);
        rd12ok = (RadioButton) rootView.findViewById(R.id.rd12ok);
        rd12ok.setOnCheckedChangeListener(this);
        rd12act = (RadioButton) rootView.findViewById(R.id.rd12act);
        rd12act.setOnCheckedChangeListener(this);
        rd12na = (RadioButton) rootView.findViewById(R.id.rd12na);
        rd12na.setOnCheckedChangeListener(this);
        rd13ok = (RadioButton) rootView.findViewById(R.id.rd13ok);
        rd13ok.setOnCheckedChangeListener(this);
        rd13act = (RadioButton) rootView.findViewById(R.id.rd13act);
        rd13act.setOnCheckedChangeListener(this);
        rd13na = (RadioButton) rootView.findViewById(R.id.rd13na);
        rd13na.setOnCheckedChangeListener(this);
        rd14ok = (RadioButton) rootView.findViewById(R.id.rd14ok);
        rd14ok.setOnCheckedChangeListener(this);
        rd14act = (RadioButton) rootView.findViewById(R.id.rd14act);
        rd14act.setOnCheckedChangeListener(this);
        rd14na = (RadioButton) rootView.findViewById(R.id.rd14na);
        rd14na.setOnCheckedChangeListener(this);
        rd15ok = (RadioButton) rootView.findViewById(R.id.rd15ok);
        rd15ok.setOnCheckedChangeListener(this);
        rd15act = (RadioButton) rootView.findViewById(R.id.rd15act);
        rd15act.setOnCheckedChangeListener(this);
        rd15na = (RadioButton) rootView.findViewById(R.id.rd15na);
        rd15na.setOnCheckedChangeListener(this);
        rd16ok = (RadioButton) rootView.findViewById(R.id.rd16ok);
        rd16ok.setOnCheckedChangeListener(this);
        rd16act = (RadioButton) rootView.findViewById(R.id.rd16act);
        rd16act.setOnCheckedChangeListener(this);
        rd16na = (RadioButton) rootView.findViewById(R.id.rd16na);
        rd16na.setOnCheckedChangeListener(this);
        rd17ok = (RadioButton) rootView.findViewById(R.id.rd17ok);
        rd17ok.setOnCheckedChangeListener(this);
        rd17act = (RadioButton) rootView.findViewById(R.id.rd17act);
        rd17act.setOnCheckedChangeListener(this);
        rd17na = (RadioButton) rootView.findViewById(R.id.rd17na);
        rd17na.setOnCheckedChangeListener(this);
        rd18ok = (RadioButton) rootView.findViewById(R.id.rd18ok);
        rd18ok.setOnCheckedChangeListener(this);
        rd18act = (RadioButton) rootView.findViewById(R.id.rd18act);
        rd18act.setOnCheckedChangeListener(this);
        rd18na = (RadioButton) rootView.findViewById(R.id.rd18na);
        rd18na.setOnCheckedChangeListener(this);
        rd19ok = (RadioButton) rootView.findViewById(R.id.rd19ok);
        rd19ok.setOnCheckedChangeListener(this);
        rd19act = (RadioButton) rootView.findViewById(R.id.rd19act);
        rd19act.setOnCheckedChangeListener(this);
        rd19na = (RadioButton) rootView.findViewById(R.id.rd19na);
        rd19na.setOnCheckedChangeListener(this);
        rd20ok = (RadioButton) rootView.findViewById(R.id.rd20ok);
        rd20ok.setOnCheckedChangeListener(this);
        rd20act = (RadioButton) rootView.findViewById(R.id.rd20act);
        rd20act.setOnCheckedChangeListener(this);
        rd20na = (RadioButton) rootView.findViewById(R.id.rd20na);
        rd20na.setOnCheckedChangeListener(this);
        rd21ok = (RadioButton) rootView.findViewById(R.id.rd21ok);
        rd21ok.setOnCheckedChangeListener(this);
        rd21act = (RadioButton) rootView.findViewById(R.id.rd21act);
        rd21act.setOnCheckedChangeListener(this);
        rd21na = (RadioButton) rootView.findViewById(R.id.rd21na);
        rd21na.setOnCheckedChangeListener(this);
        rd22ok = (RadioButton) rootView.findViewById(R.id.rd22ok);
        rd22ok.setOnCheckedChangeListener(this);
        rd22act = (RadioButton) rootView.findViewById(R.id.rd22act);
        rd22act.setOnCheckedChangeListener(this);
        rd22na = (RadioButton) rootView.findViewById(R.id.rd22na);
        rd22na.setOnCheckedChangeListener(this);
        rd23ok = (RadioButton) rootView.findViewById(R.id.rd23ok);
        rd23ok.setOnCheckedChangeListener(this);
        rd23act = (RadioButton) rootView.findViewById(R.id.rd23act);
        rd23act.setOnCheckedChangeListener(this);
        rd23na = (RadioButton) rootView.findViewById(R.id.rd23na);
        rd23na.setOnCheckedChangeListener(this);
        rd24ok = (RadioButton) rootView.findViewById(R.id.rd24ok);
        rd24ok.setOnCheckedChangeListener(this);
        rd24act = (RadioButton) rootView.findViewById(R.id.rd24act);
        rd24act.setOnCheckedChangeListener(this);
        rd24na = (RadioButton) rootView.findViewById(R.id.rd24na);
        rd24na.setOnCheckedChangeListener(this);
        rd25ok = (RadioButton) rootView.findViewById(R.id.rd25ok);
        rd25ok.setOnCheckedChangeListener(this);
        rd25act = (RadioButton) rootView.findViewById(R.id.rd25act);
        rd25act.setOnCheckedChangeListener(this);
        rd25na = (RadioButton) rootView.findViewById(R.id.rd25na);
        rd25na.setOnCheckedChangeListener(this);
        rd26ok = (RadioButton) rootView.findViewById(R.id.rd26ok);
        rd26ok.setOnCheckedChangeListener(this);
        rd26act = (RadioButton) rootView.findViewById(R.id.rd26act);
        rd26act.setOnCheckedChangeListener(this);
        rd26na = (RadioButton) rootView.findViewById(R.id.rd26na);
        rd26na.setOnCheckedChangeListener(this);
        rd27ok = (RadioButton) rootView.findViewById(R.id.rd27ok);
        rd27ok.setOnCheckedChangeListener(this);
        rd27act = (RadioButton) rootView.findViewById(R.id.rd27act);
        rd27act.setOnCheckedChangeListener(this);
        rd27na = (RadioButton) rootView.findViewById(R.id.rd27na);
        rd27na.setOnCheckedChangeListener(this);
        rd28ok = (RadioButton) rootView.findViewById(R.id.rd28ok);
        rd28ok.setOnCheckedChangeListener(this);
        rd28act = (RadioButton) rootView.findViewById(R.id.rd28act);
        rd28act.setOnCheckedChangeListener(this);
        rd28na = (RadioButton) rootView.findViewById(R.id.rd28na);
        rd28na.setOnCheckedChangeListener(this);
        rd29ok = (RadioButton) rootView.findViewById(R.id.rd29ok);
        rd29ok.setOnCheckedChangeListener(this);
        rd29act = (RadioButton) rootView.findViewById(R.id.rd29act);
        rd29act.setOnCheckedChangeListener(this);
        rd29na = (RadioButton) rootView.findViewById(R.id.rd29na);
        rd29na.setOnCheckedChangeListener(this);
        rd30ok = (RadioButton) rootView.findViewById(R.id.rd30ok);
        rd30ok.setOnCheckedChangeListener(this);
        rd30act = (RadioButton) rootView.findViewById(R.id.rd30act);
        rd30act.setOnCheckedChangeListener(this);
        rd30na = (RadioButton) rootView.findViewById(R.id.rd30na);
        rd30na.setOnCheckedChangeListener(this);
        rd31ok = (RadioButton) rootView.findViewById(R.id.rd31ok);
        rd31ok.setOnCheckedChangeListener(this);
        rd31act = (RadioButton) rootView.findViewById(R.id.rd31act);
        rd31act.setOnCheckedChangeListener(this);
        rd31na = (RadioButton) rootView.findViewById(R.id.rd31na);
        rd31na.setOnCheckedChangeListener(this);
        rd32ok = (RadioButton) rootView.findViewById(R.id.rd32ok);
        rd32ok.setOnCheckedChangeListener(this);
        rd32act = (RadioButton) rootView.findViewById(R.id.rd32act);
        rd32act.setOnCheckedChangeListener(this);
        rd32na = (RadioButton) rootView.findViewById(R.id.rd32na);
        rd32na.setOnCheckedChangeListener(this);
        rd33ok = (RadioButton) rootView.findViewById(R.id.rd33ok);
        rd33ok.setOnCheckedChangeListener(this);
        rd33act = (RadioButton) rootView.findViewById(R.id.rd33act);
        rd33act.setOnCheckedChangeListener(this);
        rd33na = (RadioButton) rootView.findViewById(R.id.rd33na);
        rd33na.setOnCheckedChangeListener(this);
        rd34ok = (RadioButton) rootView.findViewById(R.id.rd34ok);
        rd34ok.setOnCheckedChangeListener(this);
        rd34act = (RadioButton) rootView.findViewById(R.id.rd34act);
        rd34act.setOnCheckedChangeListener(this);
        rd34na = (RadioButton) rootView.findViewById(R.id.rd34na);
        rd34na.setOnCheckedChangeListener(this);
        rd35ok = (RadioButton) rootView.findViewById(R.id.rd35ok);
        rd35ok.setOnCheckedChangeListener(this);
        rd35act = (RadioButton) rootView.findViewById(R.id.rd35act);
        rd35act.setOnCheckedChangeListener(this);
        rd35na = (RadioButton) rootView.findViewById(R.id.rd35na);
        rd35na.setOnCheckedChangeListener(this);
        rd36ok = (RadioButton) rootView.findViewById(R.id.rd36ok);
        rd36ok.setOnCheckedChangeListener(this);
        rd36act = (RadioButton) rootView.findViewById(R.id.rd36act);
        rd36act.setOnCheckedChangeListener(this);
        rd36na = (RadioButton) rootView.findViewById(R.id.rd36na);
        rd36na.setOnCheckedChangeListener(this);
        rd37ok = (RadioButton) rootView.findViewById(R.id.rd37ok);
        rd37ok.setOnCheckedChangeListener(this);
        rd37act = (RadioButton) rootView.findViewById(R.id.rd37act);
        rd37act.setOnCheckedChangeListener(this);
        rd37na = (RadioButton) rootView.findViewById(R.id.rd37na);
        rd37na.setOnCheckedChangeListener(this);
        rd38ok = (RadioButton) rootView.findViewById(R.id.rd38ok);
        rd38ok.setOnCheckedChangeListener(this);
        rd38act = (RadioButton) rootView.findViewById(R.id.rd38act);
        rd38act.setOnCheckedChangeListener(this);
        rd38na = (RadioButton) rootView.findViewById(R.id.rd38na);
        rd38na.setOnCheckedChangeListener(this);
        rd39ok = (RadioButton) rootView.findViewById(R.id.rd39ok);
        rd39ok.setOnCheckedChangeListener(this);
        rd39act = (RadioButton) rootView.findViewById(R.id.rd39act);
        rd39act.setOnCheckedChangeListener(this);
        rd39na = (RadioButton) rootView.findViewById(R.id.rd39na);
        rd39na.setOnCheckedChangeListener(this);
        rd40ok = (RadioButton) rootView.findViewById(R.id.rd40ok);
        rd40ok.setOnCheckedChangeListener(this);
        rd40act = (RadioButton) rootView.findViewById(R.id.rd40act);
        rd40act.setOnCheckedChangeListener(this);
        rd40na = (RadioButton) rootView.findViewById(R.id.rd40na);
        rd40na.setOnCheckedChangeListener(this);
        rd41ok = (RadioButton) rootView.findViewById(R.id.rd41ok);
        rd41ok.setOnCheckedChangeListener(this);
        rd41act = (RadioButton) rootView.findViewById(R.id.rd41act);
        rd41act.setOnCheckedChangeListener(this);
        rd41na = (RadioButton) rootView.findViewById(R.id.rd41na);
        rd41na.setOnCheckedChangeListener(this);
        rd42ok = (RadioButton) rootView.findViewById(R.id.rd42ok);
        rd42ok.setOnCheckedChangeListener(this);
        rd42act = (RadioButton) rootView.findViewById(R.id.rd42act);
        rd42act.setOnCheckedChangeListener(this);
        rd42na = (RadioButton) rootView.findViewById(R.id.rd42na);
        rd42na.setOnCheckedChangeListener(this);
        rd43ok = (RadioButton) rootView.findViewById(R.id.rd43ok);
        rd43ok.setOnCheckedChangeListener(this);
        rd43act = (RadioButton) rootView.findViewById(R.id.rd43act);
        rd43act.setOnCheckedChangeListener(this);
        rd43na = (RadioButton) rootView.findViewById(R.id.rd43na);
        rd43na.setOnCheckedChangeListener(this);
        rd44ok = (RadioButton) rootView.findViewById(R.id.rd44ok);
        rd44ok.setOnCheckedChangeListener(this);
        rd44act = (RadioButton) rootView.findViewById(R.id.rd44act);
        rd44act.setOnCheckedChangeListener(this);
        rd44na = (RadioButton) rootView.findViewById(R.id.rd44na);
        rd44na.setOnCheckedChangeListener(this);
        rd45ok = (RadioButton) rootView.findViewById(R.id.rd45ok);
        rd45ok.setOnCheckedChangeListener(this);
        rd45act = (RadioButton) rootView.findViewById(R.id.rd45act);
        rd45act.setOnCheckedChangeListener(this);
        rd45na = (RadioButton) rootView.findViewById(R.id.rd45na);
        rd45na.setOnCheckedChangeListener(this);
        rd46ok = (RadioButton) rootView.findViewById(R.id.rd46ok);
        rd46ok.setOnCheckedChangeListener(this);
        rd46act = (RadioButton) rootView.findViewById(R.id.rd46act);
        rd46act.setOnCheckedChangeListener(this);
        rd46na = (RadioButton) rootView.findViewById(R.id.rd46na);
        rd46na.setOnCheckedChangeListener(this);
        rd47ok = (RadioButton) rootView.findViewById(R.id.rd47ok);
        rd47ok.setOnCheckedChangeListener(this);
        rd47act = (RadioButton) rootView.findViewById(R.id.rd47act);
        rd47act.setOnCheckedChangeListener(this);
        rd47na = (RadioButton) rootView.findViewById(R.id.rd47na);
        rd47na.setOnCheckedChangeListener(this);
        rd48ok = (RadioButton) rootView.findViewById(R.id.rd48ok);
        rd48ok.setOnCheckedChangeListener(this);
        rd48act = (RadioButton) rootView.findViewById(R.id.rd48act);
        rd48act.setOnCheckedChangeListener(this);
        rd48na = (RadioButton) rootView.findViewById(R.id.rd48na);
        rd48na.setOnCheckedChangeListener(this);
        rd49ok = (RadioButton) rootView.findViewById(R.id.rd49ok);
        rd49ok.setOnCheckedChangeListener(this);
        rd49act = (RadioButton) rootView.findViewById(R.id.rd49act);
        rd49act.setOnCheckedChangeListener(this);
        rd49na = (RadioButton) rootView.findViewById(R.id.rd49na);
        rd49na.setOnCheckedChangeListener(this);
        rd50ok = (RadioButton) rootView.findViewById(R.id.rd50ok);
        rd50ok.setOnCheckedChangeListener(this);
        rd50act = (RadioButton) rootView.findViewById(R.id.rd50act);
        rd50act.setOnCheckedChangeListener(this);
        rd50na = (RadioButton) rootView.findViewById(R.id.rd50na);
        rd50na.setOnCheckedChangeListener(this);
        rd51ok = (RadioButton) rootView.findViewById(R.id.rd51ok);
        rd51ok.setOnCheckedChangeListener(this);
        rd51act = (RadioButton) rootView.findViewById(R.id.rd51act);
        rd51act.setOnCheckedChangeListener(this);
        rd51na = (RadioButton) rootView.findViewById(R.id.rd51na);
        rd51na.setOnCheckedChangeListener(this);
        rd52ok = (RadioButton) rootView.findViewById(R.id.rd52ok);
        rd52ok.setOnCheckedChangeListener(this);
        rd52act = (RadioButton) rootView.findViewById(R.id.rd52act);
        rd52act.setOnCheckedChangeListener(this);
        rd52na = (RadioButton) rootView.findViewById(R.id.rd52na);
        rd52na.setOnCheckedChangeListener(this);
        rd53ok = (RadioButton) rootView.findViewById(R.id.rd53ok);
        rd53ok.setOnCheckedChangeListener(this);
        rd53act = (RadioButton) rootView.findViewById(R.id.rd53act);
        rd53act.setOnCheckedChangeListener(this);
        rd53na = (RadioButton) rootView.findViewById(R.id.rd53na);
        rd53na.setOnCheckedChangeListener(this);
        rd54ok = (RadioButton) rootView.findViewById(R.id.rd54ok);
        rd54ok.setOnCheckedChangeListener(this);
        rd54act = (RadioButton) rootView.findViewById(R.id.rd54act);
        rd54act.setOnCheckedChangeListener(this);
        rd54na = (RadioButton) rootView.findViewById(R.id.rd54na);
        rd54na.setOnCheckedChangeListener(this);
        rd55ok = (RadioButton) rootView.findViewById(R.id.rd55ok);
        rd55ok.setOnCheckedChangeListener(this);
        rd55act = (RadioButton) rootView.findViewById(R.id.rd55act);
        rd55act.setOnCheckedChangeListener(this);
        rd55na = (RadioButton) rootView.findViewById(R.id.rd55na);
        rd55na.setOnCheckedChangeListener(this);
        rd56ok = (RadioButton) rootView.findViewById(R.id.rd56ok);
        rd56ok.setOnCheckedChangeListener(this);
        rd56act = (RadioButton) rootView.findViewById(R.id.rd56act);
        rd56act.setOnCheckedChangeListener(this);
        rd56na = (RadioButton) rootView.findViewById(R.id.rd56na);
        rd56na.setOnCheckedChangeListener(this);
        rd57ok = (RadioButton) rootView.findViewById(R.id.rd57ok);
        rd57ok.setOnCheckedChangeListener(this);
        rd57act = (RadioButton) rootView.findViewById(R.id.rd57act);
        rd57act.setOnCheckedChangeListener(this);
        rd57na = (RadioButton) rootView.findViewById(R.id.rd57na);
        rd57na.setOnCheckedChangeListener(this);
        rd58ok = (RadioButton) rootView.findViewById(R.id.rd58ok);
        rd58ok.setOnCheckedChangeListener(this);
        rd58act = (RadioButton) rootView.findViewById(R.id.rd58act);
        rd58act.setOnCheckedChangeListener(this);
        rd58na = (RadioButton) rootView.findViewById(R.id.rd58na);
        rd58na.setOnCheckedChangeListener(this);
        rd59ok = (RadioButton) rootView.findViewById(R.id.rd59ok);
        rd59ok.setOnCheckedChangeListener(this);
        rd59act = (RadioButton) rootView.findViewById(R.id.rd59act);
        rd59act.setOnCheckedChangeListener(this);
        rd59na = (RadioButton) rootView.findViewById(R.id.rd59na);
        rd59na.setOnCheckedChangeListener(this);
        btnUpdate = (Button) rootView.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);
        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);

        tvErosionControl = (TextView) rootView.findViewById(R.id.tvErosionControl);
        tvGeneralSafety = (TextView) rootView.findViewById(R.id.tvGeneralSafety);
        tvUtilities = (TextView) rootView.findViewById(R.id.tvUtilities);
        tvHouseKeeping = (TextView) rootView.findViewById(R.id.tvHouseKeeping);
        tvFireProtection = (TextView) rootView.findViewById(R.id.tvFireProtection);
        tvExcavation = (TextView) rootView.findViewById(R.id.tvExcavation);
        tvFallProtection = (TextView) rootView.findViewById(R.id.tvFallProtection);
        tvEquipment = (TextView) rootView.findViewById(R.id.tvEquipment);
        tvScaffold = (TextView) rootView.findViewById(R.id.tvScaffold);
        tvTools = (TextView) rootView.findViewById(R.id.tvTools);
        tvSubcontractorSafety = (TextView) rootView.findViewById(R.id.tvSubcontractorSafety);


    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String key;
        try {
            switch (buttonView.getId()) {
                case R.id.rd1ok:
                    key = rd1ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);


                    break;
                case R.id.rd1act:
                    key = rd1act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd1na:
                    key = rd1na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd2ok:
                    key = rd2ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd2act:
                    key = rd2act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd2na:
                    key = rd2na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd3ok:
                    key = rd3ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd3act:
                    key = rd3act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd3na:
                    key = rd3na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd4ok:
                    key = rd4ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd4act:
                    key = rd4act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd4na:
                    key = rd4na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd5ok:
                    key = rd5ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd5act:
                    key = rd5act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd5na:
                    key = rd5na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd6ok:
                    key = rd6ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd6act:
                    key = rd6act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd6na:
                    key = rd6na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd7ok:
                    key = rd7ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);
                    break;
                case R.id.rd7act:
                    key = rd7act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);
                    break;
                case R.id.rd7na:
                    key = rd7na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd8ok:
                    key = rd8ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;

                case R.id.rd8act:
                    key = rd8act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd8na:
                    key = rd8na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd9ok:
                    key = rd9ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);


                    break;
                case R.id.rd9act:
                    key = rd9act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd9na:
                    key = rd9na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd10ok:
                    key = rd10ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd10act:
                    key = rd10act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd10na:
                    key = rd10na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd11ok:
                    key = rd11ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd11act:
                    key = rd11act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd11na:
                    key = rd11na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd12ok:
                    key = rd12ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd12act:
                    key = rd12act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd12na:
                    key = rd12na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd13ok:
                    key = rd13ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd13act:
                    key = rd13act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd13na:
                    key = rd13na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd14ok:
                    key = rd14ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd14act:
                    key = rd14act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd14na:
                    key = rd14na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd15ok:
                    key = rd15ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd15act:
                    key = rd15act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd15na:
                    key = rd15na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd16ok:
                    key = rd16ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd16act:
                    key = rd16act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd16na:
                    key = rd16na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd17ok:
                    key = rd17ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd17act:
                    key = rd17act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd17na:
                    key = rd17na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd18ok:
                    key = rd18ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd18act:
                    key = rd18act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd18na:
                    key = rd18na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd19ok:
                    key = rd19ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd19act:
                    key = rd19act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd19na:
                    key = rd19na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd20ok:
                    key = rd20ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd20act:
                    key = rd20act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd20na:
                    key = rd20na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd21ok:
                    key = rd21ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd21act:
                    key = rd21act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd21na:
                    key = rd21na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd22ok:
                    key = rd22ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd22act:
                    key = rd22act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd22na:
                    key = rd22na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd23ok:
                    key = rd23ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd23act:
                    key = rd23act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd23na:
                    key = rd23na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd24ok:
                    key = rd24ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd24act:
                    key = rd24act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd24na:
                    key = rd24na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd25ok:
                    key = rd25ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd25act:
                    key = rd25act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd25na:
                    key = rd25na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd26ok:
                    key = rd26ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd26act:
                    key = rd26act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd26na:
                    key = rd26na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd27ok:
                    key = rd27ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd27act:
                    key = rd27act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd27na:
                    key = rd27na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd28ok:
                    key = rd28ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd28act:
                    key = rd28act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd28na:
                    key = rd28na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd29ok:
                    key = rd29ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd29act:
                    key = rd29act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd29na:
                    key = rd29na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd30ok:
                    key = rd30ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd30act:
                    key = rd30act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd30na:
                    key = rd30na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd31ok:
                    key = rd31ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd31act:
                    key = rd31act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd31na:
                    key = rd31na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd32ok:
                    key = rd32ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd32act:
                    key = rd32act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd32na:
                    key = rd32na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd33ok:
                    key = rd33ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd33act:
                    key = rd33act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd33na:
                    key = rd33na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd34ok:
                    key = rd34ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd34act:
                    key = rd34act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;

                case R.id.rd34na:
                    key = rd34na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd35ok:
                    key = rd35ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd35act:
                    key = rd35act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd35na:
                    key = rd35na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd36ok:
                    key = rd36ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd36act:
                    key = rd36act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd36na:
                    key = rd36na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd37ok:
                    key = rd37ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd37act:
                    key = rd37act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd37na:
                    key = rd37na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd38ok:
                    key = rd38ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;

                case R.id.rd38act:
                    key = rd38act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd38na:
                    key = rd38na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd39ok:
                    key = rd39ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd39act:
                    key = rd39act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd39na:
                    key = rd39na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd40ok:
                    key = rd40ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd40act:
                    key = rd40act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd40na:
                    key = rd40na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd41ok:
                    key = rd41ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd41act:
                    key = rd41act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd41na:
                    key = rd41na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd42ok:
                    key = rd42ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd42act:
                    key = rd42act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd42na:
                    key = rd42na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd43ok:
                    key = rd43ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd43act:
                    key = rd43act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd43na:
                    key = rd43na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd44ok:
                    key = rd44ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd44act:
                    key = rd44act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd44na:
                    key = rd44na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd45ok:
                    key = rd45ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd45act:
                    key = rd45act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd45na:
                    key = rd45na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd46ok:
                    key = rd46ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd46act:
                    key = rd46act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd46na:
                    key = rd46na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd47ok:
                    key = rd47ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd47act:
                    key = rd47act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd47na:
                    key = rd47na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd48ok:
                    key = rd48ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd48act:
                    key = rd48act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd48na:
                    key = rd48na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd49ok:
                    key = rd49ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd49act:
                    key = rd49act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd49na:
                    key = rd49na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd50ok:
                    key = rd50ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd50act:
                    key = rd50act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd50na:
                    key = rd50na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd51ok:
                    key = rd51ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd51act:
                    key = rd51act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd51na:
                    key = rd51na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd52ok:
                    key = rd52ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd52act:
                    key = rd52act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd52na:
                    key = rd52na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd53ok:
                    key = rd53ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd53act:
                    key = rd53act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd53na:
                    key = rd53na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd54ok:
                    key = rd54ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd54act:
                    key = rd54act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd54na:
                    key = rd54na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd55ok:
                    key = rd55ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd55act:
                    key = rd55act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd55na:
                    key = rd55na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd56ok:
                    key = rd56ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd56act:
                    key = rd56act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd56na:
                    key = rd56na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;

                case R.id.rd57ok:
                    key = rd57ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd57act:
                    key = rd57act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd57na:
                    key = rd57na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd58ok:
                    key = rd58ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd58act:
                    key = rd58act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd58na:
                    key = rd58na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;
                case R.id.rd59ok:
                    key = rd59ok.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, OK);

                    break;
                case R.id.rd59act:
                    key = rd59act.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, ACT);

                    break;
                case R.id.rd59na:
                    key = rd59na.getTag().toString();
                    if (isChecked == true)
                        bids.put(key, NA);

                    break;


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendradiovalues() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("InspectionUpdates", bids);
            if (navigation.equals("UpdateInspection"))
                obj.put("ProjectUpdateId", update_id);
            else if (navigation.equals("NewInspection")) {
                obj.put("ProjectUpdateId", "0");
            }

            obj.put("ProjectId", project_id);
            obj.put("UserId", LandingScreen.userId);
            obj.put("Token", LandingScreen.authenticationToken);
            if (con.isConnectingToInternet()) {

                sendradiodetails(obj);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }


    public void sendradiodetails(JSONObject obj) {
        String tag_string_req = "send_bids";
        others.showProgressWithOutMessage();
        Log.v("sending data", obj.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_NEW_UPDATES, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        Log.v("Response", response.toString());
                        // String changedemail = etEmail.getText().toString();
                        try {
                            Log.v("changedemail", response.toString());
                            if (!(response.getBoolean("Error"))) {

                                String msg=response.getString("Message");
                                Toast.makeText(getActivity(), msg.replace(",",""), Toast.LENGTH_LONG).show();
                                ((AppController) getActivity().getApplication()).setNew("old");
                                Log.v("images" , ((AppController) getActivity().getApplication()).getNew().toString()) ;
                                btnPhotos.setVisibility(View.VISIBLE);
                                btnComments.setVisibility(View.VISIBLE);


                                if (navigation.equals("UpdateInspection")) {
                                    //getFragmentManager().popBackStack();
                                } else {
                                    //////take a look at it , changed for testing
                                    //  update_id = response.getString("ProjectUpdateId");
                                    ((AppController) getActivity().getApplication()).setUpdateVariable(response.getString("ProjectUpdateId"));
                                    update_id = ((AppController) getActivity().getApplication()).getUpdateVariable();
                                    updateId = Integer.parseInt(update_id);


                                    receiveradiovalues();
                                }
                                // getFragmentManager().popBackStack();

                            }
                        } catch (Exception e) {

                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.i("volley", "error: " + error);
                others.hideDialog();
                // Toast.makeText(getActivity(),
                // "Something went wrong please try again later",
                // Toast.LENGTH_LONG).show();
            }

        }) {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        jsObjRequest.setShouldCache(false);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest,
                tag_string_req);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnPhotos) {
            String jobno = job_no;
            if (history == "history") {
                Log.v("History", history.toString());

                Bundle bundle = new Bundle();


                bundle.putString("update_id", update_id);
                bundle.putString("History", "history");
                bundle.putString("job_no", jobno);
                bundle.putString("projectName", project_name);
                bundle.putString("navigation", navigation);
                if (btFreezeUpdate == "true") {
                    bundle.putString("freeze", "true");
                } else {
                    bundle.putString("freeze", "false");
                }

                FragmentImages images = new FragmentImages();
                images.setArguments(bundle);
                this.getFragmentManager().beginTransaction().replace(R.id.flContainer, images).addToBackStack(null).commit();
            } else if (history == "project") {

                Bundle bundle = new Bundle();

                bundle.putString("update_id", update_id);
                bundle.putString("History", "project");
                bundle.putString("navigation", navigation);
                bundle.putString("job_no", jobno);
                bundle.putString("projectName", project_name);
                bundle.putString("navigation", navigation);
                if (btFreezeUpdate == "true") {
                    bundle.putString("freeze", "true");
                } else {
                    bundle.putString("freeze", "false");
                }
                /*if(nw == "new"){
                    bundle.putString("new" , "new");
                }else{
                    bundle.putString("new" , "old");
                }*/
                FragmentImages images = new FragmentImages();
                images.setArguments(bundle);
                Log.v("bondam", bundle.toString());
                this.getFragmentManager().beginTransaction().replace(R.id.flContainer, images).addToBackStack(null).commit();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("update_id", update_id);
                bundle.putString("History", "project");
                bundle.putString("job_no", jobno);
                bundle.putString("projectName", project_name);
                bundle.putString("navigation", navigation);
                if (btFreezeUpdate == "true") {
                    bundle.putString("freeze", "true");
                } else {
                    bundle.putString("freeze", "false");
                }
                /*if(nw == "new"){
                    bundle.putString("new" , "new");
                }else{
                    bundle.putString("new" , "old");
                }*/
                FragmentImages images = new FragmentImages();
                images.setArguments(bundle);
                Log.v("bondam", bundle.toString());
                this.getFragmentManager().beginTransaction().replace(R.id.flContainer, images).addToBackStack(null).commit();


            }


        } else if (v.getId() == R.id.btnComments) {
            Bundle bundle = new Bundle();
            bundle.putString("update_id", update_id);
            bundle.putString("projectName", project_name);
            bundle.putString("navigation", navigation);
            bundle.putString("commentcontrol", commentcontrol);
            if (btFreezeUpdate == "true") {
                bundle.putString("freeze", "true");
            } else {
                bundle.putString("freeze", "0");
            }
            if (history == "history") {
                bundle.putString("History", "history");
            } else {
                bundle.putString("History", "project");
            }
            /*if(nw == "new"){
                bundle.putString("new" , "new");
            }else{
                bundle.putString("new" , "old");
            }*/
            Log.v("Malli", navigation);
            FragmentComments comments = new FragmentComments();
            comments.setArguments(bundle);
            Log.v("soggadu", bundle.toString());
            this.getFragmentManager().beginTransaction().replace(R.id.flContainer, comments).addToBackStack(null).commit();

        } else if (v.getId() == R.id.btnCancel) {
            getFragmentManager().popBackStack();
        } else if (v.getId() == R.id.btnUpdate) {
            if (bids.length() == 0) {
                Toast.makeText(getActivity(), "Please update Inspection", Toast.LENGTH_SHORT).show();
            } else if (navigation.equals("NewInspection")) {

                sendradiovalues();
                // receiveradiovalues();
                Log.v("donga", "in_in_new_inspection");
                commentcontrol = "newInspection";
            } else if (navigation.equals("UpdateInspection")) {
                Log.v("donga", "in_in_update_inspection");
                sendradiovalues();
                commentcontrol = "UpdateInspection";
            }
        } else if (v.getId() == R.id.liErosioncontrol) {
            Count++;
            if (Count % 2 == 1) {
                hideAll();
                Count++;
                liErosioncontrollineitem.setVisibility(View.VISIBLE);


            } else if (Count % 2 == 0) {
                hideAll();

            }

        } else if (v.getId() == R.id.liGeneralsafety) {
            Count1++;
            if (Count1 % 2 == 1) {
                hideAll();

                Count1++;
                liGeneralsafetylineitems.setVisibility(View.VISIBLE);


            } else if (Count1 % 2 == 0) {
                hideAll();

            }
        } else if (v.getId() == R.id.liUtilities) {
            Count2++;
            if (Count2 % 2 == 1) {
                hideAll();
                Count2++;

                liUtilitylineitems.setVisibility(View.VISIBLE);


            } else if (Count2 % 2 == 0) {
                hideAll();

            }
        } else if (v.getId() == R.id.liHousekeeping) {
            Count3++;
            if (Count3 % 2 == 1) {
                hideAll();
                Count3++;
                liHousekeepinglineitems.setVisibility(View.VISIBLE);


            } else if (Count3 % 2 == 0) {
                hideAll();

            }
        } else if (v.getId() == R.id.liFireprotection) {
            Count4++;
            if (Count4 % 2 == 1) {

                hideAll();
                Count4++;
                liFireprotectionlineitems.setVisibility(View.VISIBLE);


            } else if (Count4 % 2 == 0) {
                hideAll();

            }
        } else if (v.getId() == R.id.liExcavation) {
            Count5++;
            if (Count5 % 2 == 1) {
                hideAll();
                Count5++;
                liEvacuationlineitems.setVisibility(View.VISIBLE);


            } else if (Count5 % 2 == 0) {
                hideAll();

            }
        } else if (v.getId() == R.id.liFallprotection) {
            Count6++;
            if (Count6 % 2 == 1) {
                hideAll();
                Count6++;
                liFallprotectionlineitems.setVisibility(View.VISIBLE);


            } else if (Count6 % 2 == 0) {
                hideAll();

            }
        } else if (v.getId() == R.id.liEquipment) {
            Count7++;
            if (Count7 % 2 == 1) {
                hideAll();
                Count7++;

                liEquipmentlineitems.setVisibility(View.VISIBLE);


            } else if (Count7 % 2 == 0) {
                hideAll();

            }
        } else if (v.getId() == R.id.liScaffold) {
            Count8++;
            if (Count8 % 2 == 1) {
                hideAll();
                Count8++;
                liScaffoldlineitems.setVisibility(View.VISIBLE);


            } else if (Count8 % 2 == 0) {
                hideAll();

            }
        } else if (v.getId() == R.id.liTools) {
            Count9++;
            if (Count9 % 2 == 1) {
                hideAll();

                Count9++;
                liToolslineitems.setVisibility(View.VISIBLE);

            } else if (Count9 % 2 == 0) {
                hideAll();


            }
        } else if (v.getId() == R.id.liSubcontractor) {
            Count10++;
            if (Count10 % 2 == 1) {
                hideAll();
                Count10++;
                liSubcontractorlineitems.setVisibility(View.VISIBLE);
            } else if (Count10 % 2 == 0) {
                hideAll();
            }

        }

    }

    public void hideAll() {
        Count = 0;
        Count1 = 0;
        Count2 = 0;
        Count3 = 0;
        Count4 = 0;
        Count5 = 0;
        Count6 = 0;
        Count7 = 0;
        Count8 = 0;
        Count9 = 0;
        Count10 = 0;
        liErosioncontrollineitem.setVisibility(View.GONE);
        liGeneralsafetylineitems.setVisibility(View.GONE);
        liUtilitylineitems.setVisibility(View.GONE);
        liHousekeepinglineitems.setVisibility(View.GONE);
        liFireprotectionlineitems.setVisibility(View.GONE);
        liEvacuationlineitems.setVisibility(View.GONE);
        liFallprotectionlineitems.setVisibility(View.GONE);
        liEquipmentlineitems.setVisibility(View.GONE);
        liScaffoldlineitems.setVisibility(View.GONE);
        liToolslineitems.setVisibility(View.GONE);
        liSubcontractorlineitems.setVisibility(View.GONE);
    }


    //by bharadwaj about getting data

    public void receiveradiovalues() {
        try {
            JSONObject obj = new JSONObject();
            //obj.put("InspectionUpdates", bids);
            obj.put("ProjectUpdateId", update_id);
            obj.put("UserId", LandingScreen.userId);
            obj.put("Token", LandingScreen.authenticationToken);
            Log.v("kali", update_id);
            //obj.put("ProjectId", project_id);
            //obj.put("UserId", 1);
            if (con.isConnectingToInternet()) {
                Log.v("testing", obj.toString());
                getradiodetails(obj);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    //by bharadwaj about getting data
    public void getradiodetails(JSONObject obj) {
        if (updateId == 0) {
            btnPhotos.setVisibility(View.INVISIBLE);
            btnComments.setVisibility(View.INVISIBLE);
        } else {
            btnPhotos.setVisibility(View.VISIBLE);
            btnComments.setVisibility(View.VISIBLE);
        }
        String tag_string_req = "send_bids";
        others.showProgressWithOutMessage();
        Log.v("Radio", obj.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_GETUPDATE_VALUES, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        Log.v("getReferences", response.toString());
                        // String changedemail = etEmail.getText().toString();
                        try {
                            // Log.v("changedemail", response.toString());
                            if (!(response.getBoolean("Error"))) {

                                Log.v("inside_response_box", response.toString());

                                getReferences(response);
                              /*  btnPhotos.setVisibility(View.VISIBLE);
                                btnComments.setVisibility(View.VISIBLE);*/

//                                Toast.makeText(getActivity(),
//                                        "getting updatesReferences successfully",
//                                        Toast.LENGTH_LONG).show();
//                                //      getFragmentManager().popBackStack();

                            } else {
                                Log.v("inside_response_box", "else_block");
                            }
                        } catch (Exception e
                                )

                        {
                            Log.v("inside_response_box", "catch_block");
                        }
                    }
                }

                , new Response.ErrorListener()

        {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.i("volley", "error: " + error);
                others.hideDialog();
                // Toast.makeText(getActivity(),
                // "Something went wrong please try again later",
                // Toast.LENGTH_LONG).show();
                NetworkResponse response = error.networkResponse;

                if (response != null) {
                    switch (response.statusCode) {
                        case 401:
                          /*  Intent i = new Intent(getActivity(), LoginScreen.class);
                            session.logoutUser();
                            startActivity(i);*/
                            session.logoutUser();
                            getActivity().finish();
                            Toast.makeText(getActivity(),
                                    "User Logged in from other device",
                                    Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }

        }

        )

        {

            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };
        jsObjRequest.setShouldCache(false);
        jsObjRequest.setRetryPolicy(new

                DefaultRetryPolicy(30000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)

        );
        // Adding request to request queue
        AppController.getInstance().

                addToRequestQueue(jsObjRequest,
                        tag_string_req);

    }


    public void
    getReferences(JSONObject response) {


        int okCount1 = 0;
        int actCount1 = 0;
        int naCount1 = 0;
        int blankCount1 = 0;
        int totalCount1 = 0;

        int okCount2 = 0;
        int actCount2 = 0;
        int naCount2 = 0;
        int blankCount2 = 0;
        int totalCount2 = 0;

        int okCount3 = 0;
        int actCount3 = 0;
        int naCount3 = 0;
        int blankCount3 = 0;
        int totalCount3 = 0;

        int okCount4 = 0;
        int actCount4 = 0;
        int naCount4 = 0;
        int blankCount4 = 0;
        int totalCount4 = 0;

        int okCount5 = 0;
        int actCount5 = 0;
        int naCount5 = 0;
        int blankCount5 = 0;
        int totalCount5 = 0;

        int okCount6 = 0;
        int actCount6 = 0;
        int naCount6 = 0;
        int blankCount6 = 0;
        int totalCount6 = 0;

        int okCount7 = 0;
        int actCount7 = 0;
        int naCount7 = 0;
        int blankCount7 = 0;
        int totalCount7 = 0;

        int okCount8 = 0;
        int actCount8 = 0;
        int naCount8 = 0;
        int blankCount8 = 0;
        int totalCount8 = 0;

        int okCount9 = 0;
        int actCount9 = 0;
        int naCount9 = 0;
        int blankCount9 = 0;
        int totalCount9 = 0;

        int okCount10 = 0;
        int actCount10 = 0;
        int naCount10 = 0;
        int blankCount10 = 0;
        int totalCount10 = 0;

        int okCount11 = 0;
        int actCount11 = 0;
        int naCount11 = 0;
        int blankCount11 = 0;
        int totalCount11 = 0;


        // CategoryUpdateData---arrayname
        try {
            JSONArray array = response.optJSONArray("CategoryUpdateData");
            Log.v("im_in_option", String.valueOf(array));

            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonobj = array.getJSONObject(i);

                Log.v("im_in_option_forloop", String.valueOf(jsonobj));

                int intProjectProcessUpdateId = jsonobj.getInt("intProjectProcessUpdateId");
                int intSubCategoryMasterId = jsonobj.getInt("intSubCategoryMasterId");
                int answer = jsonobj.getInt("intValue");
                btFreezeUpdate = jsonobj.getString("btFreezeUpdate");
                Log.v("im_in_option", String.valueOf(intProjectProcessUpdateId));
                Log.v("im_in_option", String.valueOf(intSubCategoryMasterId));
                Log.v("im_in_option", String.valueOf(answer));
                Log.v("im_in_option", String.valueOf(btFreezeUpdate));

                putOptions(answer, intSubCategoryMasterId);


                totalCount1 = 9;
                totalCount2 = 10;
                totalCount3 = 2;
                totalCount4 = 5;
                totalCount5 = 4;
                totalCount6 = 4;
                totalCount7 = 8;
                totalCount8 = 6;
                totalCount9 = 6;
                totalCount10 = 4;
                totalCount11 = 1;


                //1
                if (intSubCategoryMasterId >= 1 && intSubCategoryMasterId <= 9) {


                    if (answer == 5) {
                        okCount1++;
                    } else if (answer == 6) {
                        actCount1++;
                    } else if (answer == 7) {
                        naCount1++;
                    }


                }
                //2

                else if (intSubCategoryMasterId >= 10 && intSubCategoryMasterId <= 19) {
                    if (answer == 5) {
                        okCount2++;
                    } else if (answer == 6) {
                        actCount2++;
                    } else if (answer == 7) {
                        naCount2++;
                    }

                }
                //3
                else if (intSubCategoryMasterId >= 20 && intSubCategoryMasterId <= 21) {
                    if (answer == 5) {
                        okCount3++;
                    } else if (answer == 6) {
                        actCount3++;
                    } else if (answer == 7) {
                        naCount3++;
                    }

                }
                //4
                else if (intSubCategoryMasterId >= 22 && intSubCategoryMasterId <= 26) {
                    if (answer == 5) {
                        okCount4++;
                    } else if (answer == 6) {
                        actCount4++;
                    } else if (answer == 7) {
                        naCount4++;
                    }

                }
                //5
                else if (intSubCategoryMasterId >= 27 && intSubCategoryMasterId <= 30) {
                    if (answer == 5) {
                        okCount5++;
                    } else if (answer == 6) {
                        actCount5++;
                    } else if (answer == 7) {
                        naCount5++;
                    }

                }
                //6
                else if (intSubCategoryMasterId >= 31 && intSubCategoryMasterId <= 34) {
                    if (answer == 5) {
                        okCount6++;
                    } else if (answer == 6) {
                        actCount6++;
                    } else if (answer == 7) {
                        naCount6++;
                    }

                }
                //7
                else if (intSubCategoryMasterId >= 35 && intSubCategoryMasterId <= 42) {
                    if (answer == 5) {
                        okCount7++;
                    } else if (answer == 6) {
                        actCount7++;
                    } else if (answer == 7) {
                        naCount7++;
                    }

                }
                //8
                else if (intSubCategoryMasterId >= 43 && intSubCategoryMasterId <= 48) {
                    if (answer == 5) {
                        okCount8++;
                    } else if (answer == 6) {
                        actCount8++;
                    } else if (answer == 7) {
                        naCount8++;
                    }

                }
                //9
                else if (intSubCategoryMasterId >= 49 && intSubCategoryMasterId <= 54) {
                    if (answer == 5) {
                        okCount9++;
                    } else if (answer == 6) {
                        actCount9++;
                    } else if (answer == 7) {
                        naCount9++;
                    }

                }
                //10
                else if (intSubCategoryMasterId >= 55 && intSubCategoryMasterId <= 58) {
                    if (answer == 5) {
                        okCount10++;
                    } else if (answer == 6) {
                        actCount10++;
                    } else if (answer == 7) {
                        naCount10++;
                    }

                }
                //11
                else if (intSubCategoryMasterId == 59) {
                    if (answer == 5) {
                        okCount11++;
                    } else if (answer == 6) {
                        actCount11++;
                    } else if (answer == 7) {
                        naCount11++;
                    }

                }


            }
            blankCount1 = totalCount1 - (okCount1 + actCount1 + naCount1);
            blankCount2 = totalCount2 - (okCount2 + actCount2 + naCount2);
            blankCount3 = totalCount3 - (okCount3 + actCount3 + naCount3);
            blankCount4 = totalCount4 - (okCount4 + actCount4 + naCount4);
            blankCount5 = totalCount5 - (okCount5 + actCount5 + naCount5);
            blankCount6 = totalCount6 - (okCount6 + actCount6 + naCount6);
            blankCount7 = totalCount7 - (okCount7 + actCount7 + naCount7);
            blankCount8 = totalCount8 - (okCount8 + actCount8 + naCount8);
            blankCount9 = totalCount9 - (okCount9 + actCount9 + naCount9);
            blankCount10 = totalCount10 - (okCount10 + actCount10 + naCount10);
            blankCount11 = totalCount11 - (okCount11 + actCount11 + naCount11);


            tvErosionControl.setText("OK: " + okCount1 + " | " + "ACT: " + actCount1 + " | " + "NA: " + naCount1 + " | " + "BLANK: " + blankCount1);
            tvGeneralSafety.setText("OK: " + okCount2 + " | " + "ACT: " + actCount2 + " | " + "NA: " + naCount2 + " | " + "BLANK: " + blankCount2);
            tvUtilities.setText("OK: " + okCount3 + " | " + "ACT: " + actCount3 + " | " + "NA: " + naCount3 + " | " + "BLANK: " + blankCount3);
            tvHouseKeeping.setText("OK: " + okCount4 + " | " + "ACT: " + actCount4 + " | " + "NA: " + naCount4 + " | " + "BLANK: " + blankCount4);
            tvFireProtection.setText("OK: " + okCount5 + " | " + "ACT: " + actCount5 + " | " + "NA: " + naCount5 + " | " + "BLANK: " + blankCount5);
            tvExcavation.setText("OK: " + okCount6 + " | " + "ACT: " + actCount6 + " | " + "NA: " + naCount6 + " | " + "BLANK: " + blankCount6);
            tvFallProtection.setText("OK: " + okCount7 + " | " + "ACT: " + actCount7 + " | " + "NA: " + naCount7 + " | " + "BLANK: " + blankCount7);
            tvEquipment.setText("OK: " + okCount8 + " | " + "ACT: " + actCount8 + " | " + "NA: " + naCount8 + " | " + "BLANK: " + blankCount8);
            tvScaffold.setText("OK: " + okCount9 + " | " + "ACT: " + actCount9 + " | " + "NA: " + naCount9 + " | " + "BLANK: " + blankCount9);
            tvTools.setText("OK: " + okCount10 + " | " + "ACT: " + actCount10 + " | " + "NA: " + naCount10 + " | " + "BLANK: " + blankCount10);
            tvSubcontractorSafety.setText("OK: " + okCount11 + " | " + "ACT: " + actCount11 + " | " + "NA: " + naCount11 + " | " + "BLANK: " + blankCount11);


            if (history == "history" || LandingScreen.userType.equals("Super Admin") || LandingScreen.userType.equals("Staff") || LandingScreen.userType.equals("Project Manager") || LandingScreen.userType.equals("Superintendent")) {
                btnUpdate.setVisibility(View.GONE);
                btnCancel.setVisibility(View.GONE);
                btFreezeUpdate = "true";
                handlingRadioButtons(btFreezeUpdate);

            } else if (history == "project") {
                btnUpdate.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                handlingRadioButtons(btFreezeUpdate);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void handlingRadioButtons(String btFreezeUpdate) {
        if (btFreezeUpdate.equals("true")) {
            rd1ok.setEnabled(false);
            rd1act.setEnabled(false);
            rd1na.setEnabled(false);
            rd2ok.setEnabled(false);
            rd2act.setEnabled(false);
            rd2na.setEnabled(false);
            rd3ok.setEnabled(false);
            rd3act.setEnabled(false);
            rd3na.setEnabled(false);
            rd4ok.setEnabled(false);
            rd4act.setEnabled(false);
            rd4na.setEnabled(false);
            rd5ok.setEnabled(false);
            rd5act.setEnabled(false);
            rd5na.setEnabled(false);
            rd6ok.setEnabled(false);
            rd6act.setEnabled(false);
            rd6na.setEnabled(false);
            rd7ok.setEnabled(false);
            rd7act.setEnabled(false);
            rd7na.setEnabled(false);
            rd8ok.setEnabled(false);
            rd8act.setEnabled(false);
            rd8na.setEnabled(false);
            rd9ok.setEnabled(false);
            rd9act.setEnabled(false);
            rd9na.setEnabled(false);
            rd10ok.setEnabled(false);
            rd10act.setEnabled(false);
            rd10na.setEnabled(false);
            rd11ok.setEnabled(false);
            rd11act.setEnabled(false);
            rd11na.setEnabled(false);
            rd12ok.setEnabled(false);
            rd12act.setEnabled(false);
            rd12na.setEnabled(false);
            rd13ok.setEnabled(false);
            rd13act.setEnabled(false);
            rd13na.setEnabled(false);
            rd14ok.setEnabled(false);
            rd14act.setEnabled(false);
            rd14na.setEnabled(false);
            rd15ok.setEnabled(false);
            rd15act.setEnabled(false);
            rd15na.setEnabled(false);
            rd16ok.setEnabled(false);
            rd16act.setEnabled(false);
            rd16na.setEnabled(false);
            rd17ok.setEnabled(false);
            rd17act.setEnabled(false);
            rd17na.setEnabled(false);
            rd18ok.setEnabled(false);
            rd18act.setEnabled(false);
            rd18na.setEnabled(false);
            rd19ok.setEnabled(false);
            rd19act.setEnabled(false);
            rd19na.setEnabled(false);
            rd20ok.setEnabled(false);
            rd20act.setEnabled(false);
            rd20na.setEnabled(false);
            rd21ok.setEnabled(false);
            rd21act.setEnabled(false);
            rd21na.setEnabled(false);
            rd22ok.setEnabled(false);
            rd22act.setEnabled(false);
            rd22na.setEnabled(false);
            rd23ok.setEnabled(false);
            rd23act.setEnabled(false);
            rd23na.setEnabled(false);
            rd24ok.setEnabled(false);
            rd24act.setEnabled(false);
            rd24na.setEnabled(false);
            rd25ok.setEnabled(false);
            rd25act.setEnabled(false);
            rd25na.setEnabled(false);
            rd26ok.setEnabled(false);
            rd26act.setEnabled(false);
            rd26na.setEnabled(false);
            rd27ok.setEnabled(false);
            rd27act.setEnabled(false);
            rd27na.setEnabled(false);
            rd28ok.setEnabled(false);
            rd28act.setEnabled(false);
            rd28na.setEnabled(false);
            rd29ok.setEnabled(false);
            rd29act.setEnabled(false);
            rd29na.setEnabled(false);
            rd30ok.setEnabled(false);
            rd30act.setEnabled(false);
            rd30na.setEnabled(false);
            rd31ok.setEnabled(false);
            rd31act.setEnabled(false);
            rd31na.setEnabled(false);
            rd32ok.setEnabled(false);
            rd32act.setEnabled(false);
            rd32na.setEnabled(false);
            rd33ok.setEnabled(false);
            rd33act.setEnabled(false);
            rd33na.setEnabled(false);
            rd34ok.setEnabled(false);
            rd34act.setEnabled(false);
            rd34na.setEnabled(false);
            rd35ok.setEnabled(false);
            rd35act.setEnabled(false);
            rd35na.setEnabled(false);
            rd36ok.setEnabled(false);
            rd36act.setEnabled(false);
            rd36na.setEnabled(false);
            rd37ok.setEnabled(false);
            rd37act.setEnabled(false);
            rd37na.setEnabled(false);
            rd38ok.setEnabled(false);
            rd38act.setEnabled(false);
            rd38na.setEnabled(false);
            rd39ok.setEnabled(false);
            rd39act.setEnabled(false);
            rd39na.setEnabled(false);
            rd40ok.setEnabled(false);
            rd40act.setEnabled(false);
            rd40na.setEnabled(false);
            rd41ok.setEnabled(false);
            rd41act.setEnabled(false);
            rd41na.setEnabled(false);
            rd42ok.setEnabled(false);
            rd42act.setEnabled(false);
            rd42na.setEnabled(false);
            rd43ok.setEnabled(false);
            rd43act.setEnabled(false);
            rd43na.setEnabled(false);
            rd44ok.setEnabled(false);
            rd44act.setEnabled(false);
            rd44na.setEnabled(false);
            rd45ok.setEnabled(false);
            rd45act.setEnabled(false);
            rd45na.setEnabled(false);
            rd46ok.setEnabled(false);
            rd46act.setEnabled(false);
            rd46na.setEnabled(false);
            rd47ok.setEnabled(false);
            rd47act.setEnabled(false);
            rd47na.setEnabled(false);
            rd48ok.setEnabled(false);
            rd48act.setEnabled(false);
            rd48na.setEnabled(false);
            rd49ok.setEnabled(false);
            rd49act.setEnabled(false);
            rd49na.setEnabled(false);
            rd50ok.setEnabled(false);
            rd50act.setEnabled(false);
            rd50na.setEnabled(false);
            rd51ok.setEnabled(false);
            rd51act.setEnabled(false);
            rd51na.setEnabled(false);
            rd52ok.setEnabled(false);
            rd52act.setEnabled(false);
            rd52na.setEnabled(false);
            rd53ok.setEnabled(false);
            rd53act.setEnabled(false);
            rd53na.setEnabled(false);
            rd54ok.setEnabled(false);
            rd54act.setEnabled(false);
            rd54na.setEnabled(false);
            rd55ok.setEnabled(false);
            rd55act.setEnabled(false);
            rd55na.setEnabled(false);
            rd56ok.setEnabled(false);
            rd56act.setEnabled(false);
            rd56na.setEnabled(false);
            rd57ok.setEnabled(false);
            rd57act.setEnabled(false);
            rd57na.setEnabled(false);
            rd58ok.setEnabled(false);
            rd58act.setEnabled(false);
            rd58na.setEnabled(false);
            rd59ok.setEnabled(false);
            rd59act.setEnabled(false);
            rd59na.setEnabled(false);

            btnUpdate.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
        }
    }

    public void putOptions(int answer, int intSubCategoryMasterId) {

        switch (intSubCategoryMasterId) {
            case 1:
                if (answer == 5) {
                    rd1ok.setChecked(true);
                } else if (answer == 6) {
                    rd1act.setChecked(true);
                } else if (answer == 7) {
                    rd1na.setChecked(true);
                }
                break;

            case 2:
                if (answer == 5) {
                    rd2ok.setChecked(true);
                } else if (answer == 6) {
                    rd2act.setChecked(true);
                } else if (answer == 7) {
                    rd2na.setChecked(true);
                }
                break;
            case 3:
                if (answer == 5) {
                    rd3ok.setChecked(true);
                } else if (answer == 6) {
                    rd3act.setChecked(true);
                } else if (answer == 7) {
                    rd3na.setChecked(true);
                }
                break;
            case 4:
                if (answer == 5) {
                    rd4ok.setChecked(true);
                } else if (answer == 6) {
                    rd4act.setChecked(true);
                } else if (answer == 7) {
                    rd4na.setChecked(true);
                }
                break;
            case 5:
                if (answer == 5) {
                    rd5ok.setChecked(true);
                } else if (answer == 6) {
                    rd5act.setChecked(true);
                } else if (answer == 7) {
                    rd5na.setChecked(true);
                }
                break;
            case 6:
                if (answer == 5) {
                    rd6ok.setChecked(true);
                } else if (answer == 6) {
                    rd6act.setChecked(true);
                } else if (answer == 7) {
                    rd6na.setChecked(true);
                }
                break;
            case 7:
                if (answer == 5) {
                    rd7ok.setChecked(true);
                } else if (answer == 6) {
                    rd7act.setChecked(true);
                } else if (answer == 7) {
                    rd7na.setChecked(true);
                }
                break;
            case 8:
                if (answer == 5) {
                    rd8ok.setChecked(true);
                } else if (answer == 6) {
                    rd8act.setChecked(true);
                } else if (answer == 7) {
                    rd8na.setChecked(true);
                }
                break;
            case 9:
                if (answer == 5) {
                    rd9ok.setChecked(true);
                } else if (answer == 6) {
                    rd9act.setChecked(true);
                } else if (answer == 7) {
                    rd9na.setChecked(true);
                }
                break;
            case 10:
                if (answer == 5) {
                    rd10ok.setChecked(true);
                } else if (answer == 6) {
                    rd10act.setChecked(true);
                } else if (answer == 7) {
                    rd10na.setChecked(true);
                }
                break;

            case 11:
                if (answer == 5) {
                    rd11ok.setChecked(true);
                } else if (answer == 6) {
                    rd11act.setChecked(true);
                } else if (answer == 7) {
                    rd11na.setChecked(true);
                }
                break;

            case 12:
                if (answer == 5) {
                    rd12ok.setChecked(true);
                } else if (answer == 6) {
                    rd12act.setChecked(true);
                } else if (answer == 7) {
                    rd12na.setChecked(true);
                }
                break;

            case 13:
                if (answer == 5) {
                    rd13ok.setChecked(true);
                } else if (answer == 6) {
                    rd13act.setChecked(true);
                } else if (answer == 7) {
                    rd13na.setChecked(true);
                }
                break;

            case 14:
                if (answer == 5) {
                    rd14ok.setChecked(true);
                } else if (answer == 6) {
                    rd14act.setChecked(true);
                } else if (answer == 7) {
                    rd14na.setChecked(true);
                }
                break;
            case 15:
                if (answer == 5) {
                    rd15ok.setChecked(true);
                } else if (answer == 6) {
                    rd15act.setChecked(true);
                } else if (answer == 7) {
                    rd15na.setChecked(true);
                }
                break;
            case 16:
                if (answer == 5) {
                    rd16ok.setChecked(true);
                } else if (answer == 6) {
                    rd16act.setChecked(true);
                } else if (answer == 7) {
                    rd16na.setChecked(true);
                }
                break;

            case 17:
                if (answer == 5) {
                    rd17ok.setChecked(true);
                } else if (answer == 6) {
                    rd17act.setChecked(true);
                } else if (answer == 7) {
                    rd17na.setChecked(true);
                }
                break;

            case 18:
                if (answer == 5) {
                    rd18ok.setChecked(true);
                } else if (answer == 6) {
                    rd18act.setChecked(true);
                } else if (answer == 7) {
                    rd18na.setChecked(true);
                }
                break;
            case 20:
                if (answer == 5) {
                    rd19ok.setChecked(true);
                } else if (answer == 6) {
                    rd19act.setChecked(true);
                } else if (answer == 7) {
                    rd19na.setChecked(true);
                }
                break;

            case 21:
                if (answer == 5) {
                    rd20ok.setChecked(true);
                } else if (answer == 6) {
                    rd20act.setChecked(true);
                } else if (answer == 7) {
                    rd20na.setChecked(true);
                }
                break;

            case 22:
                if (answer == 5) {
                    rd21ok.setChecked(true);
                } else if (answer == 6) {
                    rd21act.setChecked(true);
                } else if (answer == 7) {
                    rd21na.setChecked(true);
                }
                break;

            case 23:
                if (answer == 5) {
                    rd22ok.setChecked(true);
                } else if (answer == 6) {
                    rd22act.setChecked(true);
                } else if (answer == 7) {
                    rd22na.setChecked(true);
                }
                break;

            case 24:
                if (answer == 5) {
                    rd23ok.setChecked(true);
                } else if (answer == 6) {
                    rd23act.setChecked(true);
                } else if (answer == 7) {
                    rd23na.setChecked(true);
                }
                break;

            case 25:
                if (answer == 5) {
                    rd24ok.setChecked(true);
                } else if (answer == 6) {
                    rd24act.setChecked(true);
                } else if (answer == 7) {
                    rd24na.setChecked(true);
                }
                break;

            case 26:
                if (answer == 5) {
                    rd25ok.setChecked(true);
                } else if (answer == 6) {
                    rd25act.setChecked(true);
                } else if (answer == 7) {
                    rd25na.setChecked(true);
                }
                break;

            case 27:
                if (answer == 5) {
                    rd26ok.setChecked(true);
                } else if (answer == 6) {
                    rd26act.setChecked(true);
                } else if (answer == 7) {
                    rd26na.setChecked(true);
                }
                break;

            case 28:
                if (answer == 5) {
                    rd27ok.setChecked(true);
                } else if (answer == 6) {
                    rd27act.setChecked(true);
                } else if (answer == 7) {
                    rd27na.setChecked(true);
                }
                break;

            case 29:
                if (answer == 5) {
                    rd28ok.setChecked(true);
                } else if (answer == 6) {
                    rd28act.setChecked(true);
                } else if (answer == 7) {
                    rd28na.setChecked(true);
                }
                break;

            case 30:
                if (answer == 5) {
                    rd29ok.setChecked(true);
                } else if (answer == 6) {
                    rd29act.setChecked(true);
                } else if (answer == 7) {
                    rd29na.setChecked(true);
                }
                break;
            case 31:
                if (answer == 5) {
                    rd30ok.setChecked(true);
                } else if (answer == 6) {
                    rd30act.setChecked(true);
                } else if (answer == 7) {
                    rd30na.setChecked(true);
                }
                break;

            case 32:
                if (answer == 5) {
                    rd31ok.setChecked(true);
                } else if (answer == 6) {
                    rd31act.setChecked(true);
                } else if (answer == 7) {
                    rd31na.setChecked(true);
                }
                break;
            case 33:
                if (answer == 5) {
                    rd32ok.setChecked(true);
                } else if (answer == 6) {
                    rd32act.setChecked(true);
                } else if (answer == 7) {
                    rd32na.setChecked(true);
                }
                break;
            case 34:
                if (answer == 5) {
                    rd33ok.setChecked(true);
                } else if (answer == 6) {
                    rd33act.setChecked(true);
                } else if (answer == 7) {
                    rd33na.setChecked(true);
                }
                break;
            case 35:
                if (answer == 5) {
                    rd34ok.setChecked(true);
                } else if (answer == 6) {
                    rd34act.setChecked(true);
                } else if (answer == 7) {
                    rd34na.setChecked(true);
                }
                break;
            case 36:
                if (answer == 5) {
                    rd35ok.setChecked(true);
                } else if (answer == 6) {
                    rd35act.setChecked(true);
                } else if (answer == 7) {
                    rd35na.setChecked(true);
                }
                break;
            case 37:
                if (answer == 5) {
                    rd36ok.setChecked(true);
                } else if (answer == 6) {
                    rd36act.setChecked(true);
                } else if (answer == 7) {
                    rd36na.setChecked(true);
                }
                break;
            case 38:
                if (answer == 5) {
                    rd37ok.setChecked(true);
                } else if (answer == 6) {
                    rd37act.setChecked(true);
                } else if (answer == 7) {
                    rd37na.setChecked(true);
                }
                break;
            case 39:
                if (answer == 5) {
                    rd38ok.setChecked(true);
                } else if (answer == 6) {
                    rd38act.setChecked(true);
                } else if (answer == 7) {
                    rd38na.setChecked(true);
                }
                break;
            case 40:
                if (answer == 5) {
                    rd39ok.setChecked(true);
                } else if (answer == 6) {
                    rd39act.setChecked(true);
                } else if (answer == 7) {
                    rd39na.setChecked(true);
                }
                break;
            case 41:
                if (answer == 5) {
                    rd40ok.setChecked(true);
                } else if (answer == 6) {
                    rd40act.setChecked(true);
                } else if (answer == 7) {
                    rd40na.setChecked(true);
                }
                break;
            case 42:
                if (answer == 5) {
                    rd41ok.setChecked(true);
                } else if (answer == 6) {
                    rd41act.setChecked(true);
                } else if (answer == 7) {
                    rd41na.setChecked(true);
                }
                break;
            case 43:
                if (answer == 5) {
                    rd42ok.setChecked(true);
                } else if (answer == 6) {
                    rd42act.setChecked(true);
                } else if (answer == 7) {
                    rd42na.setChecked(true);
                }
                break;
            case 44:
                if (answer == 5) {
                    rd43ok.setChecked(true);
                } else if (answer == 6) {
                    rd43act.setChecked(true);
                } else if (answer == 7) {
                    rd43na.setChecked(true);
                }
                break;
            case 45:
                if (answer == 5) {
                    rd44ok.setChecked(true);
                } else if (answer == 6) {
                    rd44act.setChecked(true);
                } else if (answer == 7) {
                    rd44na.setChecked(true);
                }
                break;
            case 46:
                if (answer == 5) {
                    rd45ok.setChecked(true);
                } else if (answer == 6) {
                    rd45act.setChecked(true);
                } else if (answer == 7) {
                    rd45na.setChecked(true);
                }
                break;
            case 47:
                if (answer == 5) {
                    rd46ok.setChecked(true);
                } else if (answer == 6) {
                    rd46act.setChecked(true);
                } else if (answer == 7) {
                    rd46na.setChecked(true);
                }
                break;
            case 48:
                if (answer == 5) {
                    rd47ok.setChecked(true);
                } else if (answer == 6) {
                    rd47act.setChecked(true);
                } else if (answer == 7) {
                    rd47na.setChecked(true);
                }
                break;
            case 49:
                if (answer == 5) {
                    rd48ok.setChecked(true);
                } else if (answer == 6) {
                    rd48act.setChecked(true);
                } else if (answer == 7) {
                    rd48na.setChecked(true);
                }
                break;
            case 50:
                if (answer == 5) {
                    rd49ok.setChecked(true);
                } else if (answer == 6) {
                    rd49act.setChecked(true);
                } else if (answer == 7) {
                    rd49na.setChecked(true);
                }
                break;
            case 51:
                if (answer == 5) {
                    rd50ok.setChecked(true);
                } else if (answer == 6) {
                    rd50act.setChecked(true);
                } else if (answer == 7) {
                    rd50na.setChecked(true);
                }
                break;
            case 52:
                if (answer == 5) {
                    rd51ok.setChecked(true);
                } else if (answer == 6) {
                    rd51act.setChecked(true);
                } else if (answer == 7) {
                    rd51na.setChecked(true);
                }
                break;
            case 53:
                if (answer == 5) {
                    rd52ok.setChecked(true);
                } else if (answer == 6) {
                    rd52act.setChecked(true);
                } else if (answer == 7) {
                    rd52na.setChecked(true);
                }
                break;
            case 54:
                if (answer == 5) {
                    rd53ok.setChecked(true);
                } else if (answer == 6) {
                    rd53act.setChecked(true);
                } else if (answer == 7) {
                    rd53na.setChecked(true);
                }
                break;
            case 55:
                if (answer == 5) {
                    rd54ok.setChecked(true);
                } else if (answer == 6) {
                    rd54act.setChecked(true);
                } else if (answer == 7) {
                    rd54na.setChecked(true);
                }
                break;
            case 56:
                if (answer == 5) {
                    rd55ok.setChecked(true);
                } else if (answer == 6) {
                    rd55act.setChecked(true);
                } else if (answer == 7) {
                    rd55na.setChecked(true);
                }
                break;
            case 57:
                if (answer == 5) {
                    rd56ok.setChecked(true);
                } else if (answer == 6) {
                    rd56act.setChecked(true);
                } else if (answer == 7) {
                    rd56na.setChecked(true);
                }
                break;
            case 58:
                if (answer == 5) {
                    rd57ok.setChecked(true);
                } else if (answer == 6) {
                    rd57act.setChecked(true);
                } else if (answer == 7) {
                    rd57na.setChecked(true);
                }
                break;
            case 59:
                if (answer == 5) {
                    rd58ok.setChecked(true);
                } else if (answer == 6) {
                    rd58act.setChecked(true);
                } else if (answer == 7) {
                    rd58na.setChecked(true);
                }
                break;
            case 19:
                if (answer == 5) {
                    rd59ok.setChecked(true);
                } else if (answer == 6) {
                    rd59act.setChecked(true);
                } else if (answer == 7) {
                    rd59na.setChecked(true);
                }
                break;

        }

    }


}

package com.om.virinchi.ricelake.Helper;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.om.virinchi.ricelake.Activites.LandingScreen;
import com.om.virinchi.ricelake.Network.AppController;
import com.om.virinchi.ricelake.Network.Config;
import com.om.virinchi.ricelake.Network.ConnectionDetector;
import com.om.virinchi.ricelake.Network.Others;
import com.om.virinchi.ricelake.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ndroid on 9/1/2016.
 */
public class CustomGridViewAdapter extends ArrayAdapter<ManagePhotosDetails> {
    Context context;
    int layoutResourceId;
    Others others;
    ManagePhotosDetails item;
    ConnectionDetector con = new ConnectionDetector(getContext());
    ArrayList<ManagePhotosDetails> data = new ArrayList<ManagePhotosDetails>();
    String deleteImage;
    int positiion;

    public CustomGridViewAdapter(Activity context, int layoutResourceId, ArrayList<ManagePhotosDetails> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new RecordHolder();
            holder.imageItem = (ImageView) row.findViewById(R.id.item_image);
            holder.cross = (ImageView) row.findViewById(R.id.checkBox);

            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        // ManagePhotosDetails item = data.get(position);
        item = data.get(position);
        holder.cross.setImageResource(R.mipmap.cross);

        if (item.getType().equals("imageUpload")) {
            holder.imageItem.setImageBitmap(item.getImage());

        } else {
            others.setImageAsBackground(item.getImageurl(), holder.imageItem);
            Log.v("darshi", item.getImageurl());

        }

        holder.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                // holder.cross.setImageResource(R.mipmap.cross);
                //data.remove(data.get(position));
                if (LandingScreen.userType.equals("Project Manager") || LandingScreen.userType.equals("Superintendent") || item.getHistory().equals("history") || item.getFreeze().equals("true") || item.getNew().equals("new")) {
                    if(item.getNew().equals("new"))
                    {
                        Toast.makeText(getContext(),"Please save references to perform this operation", Toast.LENGTH_LONG).show();
                    }
                    else if(LandingScreen.userType.equals("Project Manager"))
                    {
                        Toast.makeText(getContext(),"Project Manager cannot perform this operation", Toast.LENGTH_LONG).show();

                    }else if(LandingScreen.userType.equals("Superintendent"))
                    {
                        Toast.makeText(getContext(),"SuperIntendent cannot perform this operation", Toast.LENGTH_LONG).show();

                    }else if(item.getFreeze().equals("true"))
                    {
                        Toast.makeText(getContext(),"Inspection is freeze", Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (data.get(position).getType().equals("imageGet")) {
                        String stringImageUrl = data.get(position).getImageurl();
                        Log.v("delete image url", stringImageUrl);
                        String requiredImageUrl = stringImageUrl.replace("http://qa.ricelake.org:443", "").trim();
                        DeleteImageUrlToServer(requiredImageUrl, position);


                    } else {
                        data.remove(data.get(position));
                        notifyDataSetChanged();
                    }
                }
                //  others.hideDialog();

            }
        });
        notifyDataSetChanged();
        return row;
    }

    public void DeleteImageUrlToServer(String stringImageUrl, int position) {
        //  Log.v("bharat", String.valueOf(position));
        //  Log.v("tin" , stringImageUrl);
        try {
            JSONObject obj = new JSONObject();
            //obj.put("InspectionUpdates", bids);
            obj.put("ProjectUpdateId", item.getUpdateId());
            //   Log.v("in_hi_block", item.getUpdateId());
            obj.put("UserId", LandingScreen.userId);
            obj.put("Token", LandingScreen.authenticationToken);
            obj.put("vcImage", stringImageUrl);
            if (con.isConnectingToInternet()) {
                //  Log.v("testing", obj.toString());
                sendImageUrl(obj, position);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //by bharadwaj about sending url to server
    public void sendImageUrl(JSONObject obj, int position) {
        //  Log.v("bharat", String.valueOf(position));
        positiion = position;
        String tag_string_req = "send_bids";
        /// others.showProgressWithOutMessage();
        // Log.v("Radio", obj.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_DELETE_IMAGES, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // others.hideDialog();
                        //     Log.v("getReferences", response.toString());

                        try {
                            // Log.v("changedemail", response.toString());
                            if (!(response.getBoolean("Error"))) {
                                //   Log.v("inside_response_box", response.toString());
                                deleteImage = response.getString("Message");
                                if (deleteImage.equals("Image has been deleted")) {
                                    data.remove(data.get(positiion));
                                    notifyDataSetChanged();
                                }
//                                Toast.makeText(getContext(),
//                                        response.getString("Message"),
//                                        Toast.LENGTH_LONG).show();
                            } else {
                                //  Log.v("inside_response_box", "else_block");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            // Log.v("inside_response_box", "catch_block");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                //   Log.i("volley", "error: " + error.toString());
                // others.hideDialog();
            }
        }) {
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

    class RecordHolder {
        ImageView imageItem;
        ImageView cross;
    }


}




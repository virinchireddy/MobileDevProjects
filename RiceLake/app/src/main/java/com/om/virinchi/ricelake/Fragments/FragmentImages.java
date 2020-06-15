package com.om.virinchi.ricelake.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.om.virinchi.ricelake.Activites.LandingScreen;
import com.om.virinchi.ricelake.Helper.CustomGridViewAdapter;
import com.om.virinchi.ricelake.Helper.ExpandableHeightGridView;
import com.om.virinchi.ricelake.Helper.ManagePhotosDetails;
import com.om.virinchi.ricelake.Helper.SessionManager;
import com.om.virinchi.ricelake.Imagecrop.CropImage;
import com.om.virinchi.ricelake.Imagecrop.InternalStorageContentProviderFragmentImages;
import com.om.virinchi.ricelake.Network.AppController;
import com.om.virinchi.ricelake.Network.Config;
import com.om.virinchi.ricelake.Network.ConnectionDetector;
import com.om.virinchi.ricelake.Network.Others;
import com.om.virinchi.ricelake.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Ndroid on 10/27/2016.
 */
public class FragmentImages extends Fragment implements View.OnClickListener {

    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    public static String navigation;
    public static byte[] image_Array;
    public String currentDateTimeString, update_id, history, job_no, project_name;
    View rootView;
    LinearLayout liCameraopen;
    ImageView ivImage;
    ConnectionDetector con;
    ExpandableHeightGridView gridView;
    ArrayList<ManagePhotosDetails> gridArray = new ArrayList<ManagePhotosDetails>();
    CustomGridViewAdapter customGridAdapter;
   // Bitmap photo;
    Others others;
    String Text, freeze, nw;
    Button btnProjectname;
    private File mFileTemp;
    SessionManager session;
    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        rootView = inflater.inflate(R.layout.fragment_photos,
                container, false);
        savedInstanceState = getArguments();
        update_id = savedInstanceState.getString("update_id");
        //  Log.v("updte", update_id);
        history = savedInstanceState.getString("History");
        job_no = savedInstanceState.getString("job_no");
        // Log.v("jobno",job_no.toString());
        freeze = savedInstanceState.getString("freeze");
        project_name = savedInstanceState.getString("projectName");
        navigation = savedInstanceState.getString("navigation");
        nw = ((AppController) getActivity().getApplication()).getNew();
        Log.v("images", nw.toString());
        Date cDate = new Date();
        currentDateTimeString = new SimpleDateFormat("MM/dd/yyyy, kk:mm").format(cDate);
        intilize();

        return rootView;

    }

    public void intilize() {
        LandingScreen.ivBack.setVisibility(View.VISIBLE);
        liCameraopen = (LinearLayout) rootView.findViewById(R.id.liCameraopen);
        liCameraopen.setOnClickListener(this);
        ivImage = (ImageView) rootView.findViewById(R.id.ivImage);
        ivImage.setOnClickListener(this);
        others = new Others(getActivity());
        con = new ConnectionDetector(getActivity());
        gridView = (ExpandableHeightGridView) rootView.findViewById(R.id.gridView1);
        gridView.setExpanded(true);
        btnProjectname = (Button) rootView.findViewById(R.id.btnProjectname);
        btnProjectname.setText(project_name + " - Images ");
        session = new SessionManager(getActivity());
        if (freeze == "true" && navigation.equals("NewInspection")) {
            liCameraopen.setVisibility(View.VISIBLE);
            Log.v("test1", "1");
        } else if (freeze == "true") {
            Log.v("test1", "2");
            liCameraopen.setVisibility(View.GONE);

        } else if (history.equals("history")) {
            Log.v("test1", "3");
            liCameraopen.setVisibility(View.GONE);
        } else {
            Log.v("test1", "4");
            liCameraopen.setVisibility(View.VISIBLE);
        }
        if (LandingScreen.userType.equals("Super Admin") || LandingScreen.userType.equals("Staff") || LandingScreen.userType.equals("Project Manager") || LandingScreen.userType.equals("Superintendent")) {
            liCameraopen.setVisibility(View.GONE);

        }
        getProjectImages();

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getActivity().getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }


    }

    private void getPhoto() {
        // TODO Auto-generated method stub
        final String[] items = new String[]{"Take from camera",
                "Select from gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { // pick from
                // camera
                if (item == 0) {
                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {

                        if (Others.checkPermission(getActivity(),
                                "android.permission.CAMERA")) {
                            //Log.v("Fragment Profile","am in camera");
                            openCamera();
                        } else {
                            //Log.v("Fragment Profile","am in camera else");

                            Others.permissionCheckMarshMallow(getActivity(),
                                    "android.permission.CAMERA");
                        }
                    } else {
                        Log.v("Fragment Profile", "am in camera main else");

                        openCamera();

                    }
                } else { // pick from file

                    if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (Others.checkPermission(getActivity(),
                                "android.permission.WRITE_EXTERNAL_STORAGE")) {
                            openFile();
                        } else {
                            Others.permissionCheckMarshMallow(getActivity(),
                                    "android.permission.WRITE_EXTERNAL_STORAGE");
                        }
                    } else {
                        openFile();

                    }


                }
            }

        });

        final AlertDialog dialog = builder.create();
        dialog.show();


    }

    @SuppressWarnings("deprecation")
    private void openCamera() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri;

            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Log.v("testing_camera", "for less than lollipop");

                    mImageCaptureUri = Uri.fromFile(mFileTemp);
                } else {
                    Log.v("testing_camera", "for greater than lollipop");

                    mImageCaptureUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", mFileTemp);
                }
            } else {

                mImageCaptureUri = InternalStorageContentProviderFragmentImages.CONTENT_URI;
            }
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);



        } catch (ActivityNotFoundException e) {

            Log.d("Fragment Profile", "cannot take picture", e);
        }
    }

    private void startCropImage() {

        Intent intent = new Intent(getActivity(), CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, true);

        intent.putExtra(CropImage.ASPECT_X, 1);
        intent.putExtra(CropImage.ASPECT_Y, 1);


        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);


    }

    private void openFile() {
        // TODO Auto-generated method stub
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {

            return;
        }

        switch (requestCode) {

            case REQUEST_CODE_GALLERY:

                try {

                    InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                    FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                    copyStream(inputStream, fileOutputStream);
                    fileOutputStream.close();

                    assert inputStream != null;

                    inputStream.close();

                    startCropImage();

                } catch (Exception e) {

                    Log.e("Fragment Profile", "Error while creating temp file", e);
                }

                break;
            case REQUEST_CODE_TAKE_PICTURE:

                startCropImage();
                break;
            case REQUEST_CODE_CROP_IMAGE:

                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path == null) {

                    return;
                }

                Bitmap photo;
                photo = BitmapFactory.decodeFile(mFileTemp.getPath());
                Bitmap bit = printDateOnUri(photo);

                gridArray.add(new ManagePhotosDetails(bit, "imageUpload", update_id, history, freeze, nw));
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

        customGridAdapter = new CustomGridViewAdapter(getActivity(), R.layout.row_grid, gridArray);
        gridView.setAdapter(customGridAdapter);
    }

    public Bitmap printDateOnUri(Bitmap image) {
        Bitmap workingBitmap = Bitmap.createBitmap(image);
        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawBitmap(mutableBitmap, 0, 0, null);
        Paint mTxtPaint = new Paint();
        Paint.FontMetrics fm = new Paint.FontMetrics();
        mTxtPaint.setColor(Color.WHITE);
        mTxtPaint.setTextSize(18.0f);
        mTxtPaint.getFontMetrics(fm);
        int margin = 5;
        canvas.drawRect(10 - margin, 50 + fm.top - margin,
                75 + mTxtPaint.measureText(currentDateTimeString) + margin, 50 + fm.bottom
                        + margin, mTxtPaint);
        mTxtPaint.setColor(Color.BLACK);
        canvas.drawText(job_no + "  " + currentDateTimeString, 10, 50, mTxtPaint);
        return mutableBitmap;
    }

    //to encode the image from bitmap to base64.
    public String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        image_Array = baos.toByteArray();
        String imageEncoded = Base64
                .encodeToString(image_Array, Base64.DEFAULT);
        return imageEncoded;
    }


    public void sendImages(ArrayList<ManagePhotosDetails> gridArray) {

        Log.v("hi_matrix", gridArray.toString());
//        if (gridArray.size() == 0) {
//            Toast.makeText(getActivity(),
//                    "No images available",
//                    Toast.LENGTH_LONG).show();
//        }
        String base64Images;
        Log.v("images count", gridArray.size() + "");
        for (int i = 0; i < gridArray.size(); i++) {
            if (gridArray.get(i).getType().equals("imageUpload")) {
                Log.v("images count", "am inside");

                Bitmap bitmapImage = gridArray.get(i).getImage();
                Log.v("hi_option", bitmapImage.toString());
                base64Images = encodeTobase64(bitmapImage);
                details(base64Images);
            }
        }


    }

    private void details(String base64Images1) {
        // TODO Auto-generated method stub
        // Log.v("data", (base64Images1));
        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", LandingScreen.userId);

            obj.put("Token", LandingScreen.authenticationToken);
            Log.v("avenger", base64Images1.toString());
            obj.put("ProjectUpdateId", update_id);
            obj.put("vcImage", base64Images1);

            if (con.isConnectingToInternet()) {
                senddetails(obj);
                Log.v("data", obj.toString());

            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void senddetails(JSONObject obj) {

        String tag_string_req = "req_Notifications";
        others.showProgressWithOutMessage();
        Log.v("url", obj.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_Multiple_Images, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            //  Log.v("response", response.toString());
                            boolean error = response.getBoolean("Error");

                            if (!error) {
                                gridArray.clear();
                                getProjectImages();




                            } else {
                                others.ToastMessage(response
                                        .getString("Message"));
                                // Log.v("error", response.toString());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               /* // TODO Auto-generated method stub
                //  Log.i("volley", "error: " + error);
                others.hideDialog();*/


                Log.i("volley", "error: " + error);
                others.hideDialog();
                NetworkResponse response = error.networkResponse;

                if (response != null) {
                    switch (response.statusCode) {
                        case 401:
                           /* Intent i = new Intent(getActivity(), LoginScreen.class);
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


    private void getProjectImages() {
        // TODO Auto-generated method stub

        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", LandingScreen.userId);
            obj.put("Token", LandingScreen.authenticationToken);
            //   Log.v("avenger", base64Images1.toString());
            obj.put("ProjectUpdateId", update_id);
            //    obj.put("vcImage", base64Images1);
            if (con.isConnectingToInternet()) {
                getProjectImageDetails(obj);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getProjectImageDetails(JSONObject obj) {

        String tag_string_req = "req_Notifications";

        others.showProgressWithOutMessage();

        Log.v("url12345", obj.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_GETPROJECT_IMAGES, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            //  Log.v("response", response.toString());
                            boolean error = response.getBoolean("Error");

                            if (!error) {

                                // Log.v("error", response.toString());
                                loadInitialImages(response);


                            } else {
                                others.ToastMessage(response
                                        .getString("Message"));
                                // Log.v("error", response.toString());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                //  Log.i("volley", "error: " + error);
                others.hideDialog();

                NetworkResponse response = error.networkResponse;

                if (response != null) {
                    switch (response.statusCode) {

                        case 401:
                           /* Intent i = new Intent(getActivity(), LoginScreen.class);
                            session.logoutUser();
                            startActivity(i);*/
                            session.logoutUser();
                            getActivity().finish();
                            Toast.makeText(getActivity(),
                                    "User Logged in from other device",
                                    Toast.LENGTH_LONG).show();
                            break;

                        case 302:

                            String json;
                            json = new String(response.data);

                            try {
                                //  Log.v("response", response.toString());


                                //  Log.v("error", response.toString());

                                JSONObject obb = new JSONObject(json);

                                loadInitialImages(obb);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Log.v("digudigunatha", json);
                       /*     Toast.makeText(getApplicationContext(),
                                    "Loading Images......",
                                    Toast.LENGTH_LONG).show();*/
                            break;

                    }
                }
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

    private void loadInitialImages(JSONObject response) {
        // TODO Auto-generated method stub
        try {
            JSONArray photos = response.getJSONArray("tblImageData");
            if (photos.length() > 0) {
               /* Toast.makeText(getActivity(),
                        "Loading Images......",
                        Toast.LENGTH_LONG).show();*/
            }

            for (int i = 0; i < photos.length(); i++) {
                JSONObject pics = photos.getJSONObject(i);
                String imageUrl = pics.getString("vcImagePath");
                String imageurl2 = "http://qa.ricelake.org:443" + imageUrl;
                //  Log.v("hi_im_in_getimages", imageurl2);
                gridArray.add(new ManagePhotosDetails(imageurl2, "imageGet", update_id, history, freeze, nw));
            }
            customGridAdapter = new CustomGridViewAdapter(getActivity(), R.layout.row_grid, gridArray);
            gridView.setAdapter(customGridAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.liCameraopen) {
            if (nw == "new") {
                Toast.makeText(getActivity(),
                        "Save an inspection to perform this operation",
                        Toast.LENGTH_SHORT).show();
            } else {
                getPhoto();
            }
        } else if (v.getId() == R.id.ivImage) {


            sendImages(gridArray);


        }

    }
}
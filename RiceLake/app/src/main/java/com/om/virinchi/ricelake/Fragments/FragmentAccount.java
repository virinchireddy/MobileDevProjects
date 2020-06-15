package com.om.virinchi.ricelake.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.om.virinchi.ricelake.Activites.LoginScreen;
import com.om.virinchi.ricelake.Helper.CircularImageView;
import com.om.virinchi.ricelake.Helper.SessionManager;
import com.om.virinchi.ricelake.Imagecrop.CropImage;
import com.om.virinchi.ricelake.Imagecrop.InternalStorageContentProvider;
import com.om.virinchi.ricelake.Network.AppController;
import com.om.virinchi.ricelake.Network.Config;
import com.om.virinchi.ricelake.Network.ConnectionDetector;
import com.om.virinchi.ricelake.Network.Others;
import com.om.virinchi.ricelake.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Virinchi on 8/20/2016.
 */
public class FragmentAccount extends Fragment implements View.OnClickListener {
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    public static byte[] image_Array;
    Button btnLogout, btnChangePassword, btnUpdatedetailss, btnPasswordsavechanges, btnUpdatesavechanges;
    View rootView;
    LinearLayout liChangepassword, liUpdatedetails;
    ImageView ivCamera, ivUploadimage;
    Others others;
    ConnectionDetector con;
    SessionManager session;
    int Count = 0;
    EditText etOldpassword, etNewpassword, etCnnewpassword, etEmail, etPhoneno;
    BitmapDrawable image;
    String profile_image = "";
    Bitmap photo;
    String saveType;
    TextView tvSaveimage;
    private Uri mImageCaptureUri;
    private File mFileTemp;

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
        rootView = inflater.inflate(R.layout.fragment_profile,
                container, false);
        intilize();
        details();
        return rootView;


    }

    public void intilize() {

        LandingScreen.ivBack.setVisibility(View.INVISIBLE);
        LandingScreen.ivCamera.setVisibility(View.INVISIBLE);
        liChangepassword = (LinearLayout) rootView.findViewById(R.id.liChangepassword);
        liUpdatedetails = (LinearLayout) rootView.findViewById(R.id.liUpdatedetails);
        btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        btnChangePassword = (Button) rootView.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);
        btnUpdatedetailss = (Button) rootView.findViewById(R.id.btnUpdatedetailss);
        btnUpdatedetailss.setOnClickListener(this);
        etOldpassword = (EditText) rootView.findViewById(R.id.etOldpassword);
        etNewpassword = (EditText) rootView.findViewById(R.id.etNewpassword);
        etCnnewpassword = (EditText) rootView.findViewById(R.id.etCnnewpassword);
        btnPasswordsavechanges = (Button) rootView.findViewById(R.id.btnPasswordsavechanges);
        btnPasswordsavechanges.setOnClickListener(this);
        btnUpdatesavechanges = (Button) rootView.findViewById(R.id.btnUpdatesavechanges);
        btnUpdatesavechanges.setOnClickListener(this);
        etEmail = (EditText) rootView.findViewById(R.id.etEmail);
        etPhoneno = (EditText) rootView.findViewById(R.id.etPhoneno);
        etPhoneno.addTextChangedListener(new TextWatcher() {
            private boolean mFormatting; // this is a flag which prevents the  stack overflow.
            private int mAfter;

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // nothing to do here..
            }

            //called before the text is changed...
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing to do here...
                mAfter = after; // flag to detect backspace..

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Make sure to ignore calls to afterTextChanged caused by the work done below
                if (!mFormatting) {
                    mFormatting = true;
                    // using US formatting...
                    if (mAfter != 0) // in case back space ain't clicked...
                        PhoneNumberUtils.formatNumber(s, PhoneNumberUtils.getFormatTypeForLocale(Locale.US));
                    mFormatting = false;
                }
            }
        });


        session = new SessionManager(getActivity());
        others = new Others(getActivity());
        con = new ConnectionDetector(getActivity());
        ivUploadimage = (CircularImageView) rootView.findViewById(R.id.ivUploadimage);
        ivUploadimage.setOnClickListener(this);
        ivCamera = (ImageView) rootView.findViewById(R.id.ivCamera);
        ivCamera.setOnClickListener(this);
        tvSaveimage = (TextView) rootView.findViewById(R.id.tvSaveimage);
        tvSaveimage.setOnClickListener(this);
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getActivity().getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnLogout) {
            session.logoutUser();
            getActivity().finish();
        } else if (v.getId() == R.id.btnChangePassword) {
            Count++;
            if (Count % 2 == 1) {

                liChangepassword.setVisibility(View.VISIBLE);

            } else if (Count % 2 == 0) {
                hideAll();
            }
        } else if (v.getId() == R.id.btnUpdatedetailss) {
            Count++;
            if (Count % 2 == 1) {

                liUpdatedetails.setVisibility(View.VISIBLE);

            } else if (Count % 2 == 0) {
                hideAll();
            }
        } else if (v.getId() == R.id.ivCamera) {
            getPhoto();
        } else if (v.getId() == R.id.btnPasswordsavechanges) {
            String password = etOldpassword.getText().toString();
            String newpassword = etNewpassword.getText().toString();
            String cnnewpassword = etCnnewpassword.getText().toString();
            if (password.length() > 0 && newpassword.length() > 0 && cnnewpassword.length() > 0) {
                if (newpassword.length() >= 8 && cnnewpassword.length() >= 8) {


                    if (LandingScreen.password.equals(password)) {
                        if (newpassword.equals(cnnewpassword)) {

                            try {
                                JSONObject obj = new JSONObject();
                                obj.put("OldPassword", password);
                                obj.put("NewPassword", cnnewpassword);
                                obj.put("Token", LandingScreen.authenticationToken);
                                obj.put("UserId", LandingScreen.userId);
                                obj.put("Phone", "null");
                                Log.v("sending data", obj.toString() + LandingScreen.authenticationToken + "/" + LandingScreen.userId);
                                if (con.isConnectingToInternet()) {
                                    changepassword(obj);
                                } else {
                                    con.failureAlert();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            etCnnewpassword.setError("Password does not match");
                        }
                    } else {
                        etOldpassword.setError("Current password is incorrect");
                    }
                } else {
                    etNewpassword.setError("Password should be atleast 8 characters");
                }
            } else {
                Toast.makeText(getActivity(), "Please enter all the feilds",
                        Toast.LENGTH_LONG).show();
            }
        } else if (v.getId() == R.id.btnUpdatesavechanges) {
            String phoneno = etPhoneno.getText().toString();
            try {
                JSONObject obj = new JSONObject();
                obj.put("OldPassword", "null");
                obj.put("NewPassword", "null");
                obj.put("Phone", phoneno);
                obj.put("UserId", LandingScreen.userId);
                obj.put("Token", LandingScreen.authenticationToken);
                if (con.isConnectingToInternet()) {
                    updatedetails(obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (v.getId() == R.id.tvSaveimage) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("UserId", LandingScreen.userId);
                obj.put("Token", LandingScreen.authenticationToken);
                if (profile_image.equals("")) {
                    Log.v("dabidi", "dibide");
                    ivUploadimage.buildDrawingCache();
                    Bitmap bmap = ivUploadimage
                            .getDrawingCache();
                    profile_image = encodeTobase64(bmap);

                    obj.put("vcImage", profile_image);
                } else {
                    obj.put("vcImage", profile_image);
                }
                if (con.isConnectingToInternet()) {
                    uploadPhoto(obj);
                } else {
                    con.failureAlert();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void changepassword(JSONObject obj) {
        // TODO Auto-generated method stub
        Log.v("url request", obj.toString());
        String tag_string_req = "req_Notifications";

        others.showProgressWithOutMessage();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_UPDATE_PROFILE, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            boolean error = response.getBoolean("Error");
                            Log.v("response", response.toString());
                            if (!error) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage(
                                        "Password has been changed, Click OK to login.")
                                        .setCancelable(false)
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int id) {
                                                        dialog.cancel();
                                                        session.logoutUser();
                                                    }
                                                });
                                // Creating dialog box
                                AlertDialog alert = builder.create();
                                alert.setCanceledOnTouchOutside(true);

                                alert.show();


                                //Toast.makeText(getApplicationContext(), "Password has been changed, Login again", Toast.LENGTH_LONG).show();
                                //session.logoutUser();
                                /*Intent i = new Intent(getApplicationContext(), LoginScreen.class);
                                startActivity(i);*/
                            } else {
                                others.ToastMessage(response
                                        .getString("error_msg"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("volley", "error: " + error);
                others.hideDialog();
                con.serverErrorAlert();


            }
        });
        jsObjRequest.setShouldCache(false);
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest,
                tag_string_req);

    }

    public void updatedetails(JSONObject obj) {
        String tag_string_req = "req_Notifications";
        others.showProgressWithOutMessage();
        Log.v("url", obj.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_UPDATE_PROFILE, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            Log.v("response", response.toString());
                            boolean error = response.getBoolean("Error");

                            if (!error) {


                                Toast.makeText(getActivity(),
                                        "Details updated  successfully",
                                        Toast.LENGTH_LONG).show();


                            } else {
                                others.ToastMessage(response
                                        .getString("Message"));
                                Log.v("error", response.toString());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.i("volley", "error: " + error);
                others.hideDialog();


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


    private boolean isValidPhone(String phoneno) {
        if (phoneno != null && phoneno.length() > 0 && phoneno.length() <= 25) {
            return true;
        }
        return false;
    }

    public void uploadPhoto(JSONObject obj) {

        String tag_string_req = "req_Notifications";
        others.showProgressWithOutMessage();
        Log.v("url", obj.toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_PROFILE_PIC, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            Log.v("response", response.toString());
                            boolean error = response.getBoolean("Error");

                            if (!error) {


                                Toast.makeText(getActivity(),
                                        "Profile pic updated Sucessfully",
                                        Toast.LENGTH_LONG).show();


                            } else {
                                others.ToastMessage(response
                                        .getString("Message"));
                                Log.v("error", response.toString());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
              /*  // TODO Auto-generated method stub
                Log.i("volley", "error: " + error);
                others.hideDialog();*/

                Log.i("volley", "error: " + error);
                others.hideDialog();
                NetworkResponse response = error.networkResponse;

                if (response != null) {
                    switch (response.statusCode) {
                        case 400:
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

    public void details() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("UserId", LandingScreen.userId);
            obj.put("Token", LandingScreen.authenticationToken);
            if (con.isConnectingToInternet()) {
                Log.v("sending  data", obj.toString());
                getdetails(obj);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getdetails(JSONObject obj) {
        String tag_string_req = "req_Notifications";
        others.showProgressWithOutMessage();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, Config.URL_GET_PROFILE_DETAILS, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        others.hideDialog();
                        try {
                            boolean error = response.getBoolean("Error");
                            if (!error) {
                                String email = response.getString("EmailId");
                                etEmail.setText(email);
                                String phone = response.getString("Phone");
                                etPhoneno.setText(phone);
                                String userProfileUrl = response
                                        .getString("Image");
                                Log.v("response", response.toString());
                                if (!response.optString("Image").isEmpty()) {
                                    // Others.setImageAsBackground(userProfileUrl,
                                    // ivUploadimage);
                                    Log.v("here", userProfileUrl);
                                    //tag_string_req.get
                                    //Picasso.with(getActivity()).setLoggingEnabled(true);
//                                    Picasso.with(getActivity()).invalidate(userProfileUrl);
//                                    Picasso.with(getActivity())
//                                            .load(userProfileUrl)
                                    //                                      .into(ivUploadimage);
                                    Others.loadImages(userProfileUrl, ivUploadimage);
                                } else {
                                    others.ToastMessage(response
                                            .getString("error_msg"));
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
                NetworkResponse response = error.networkResponse;

                if (response != null) {
                    switch (response.statusCode) {
                        case 401:
                            Intent i = new Intent(getActivity(), LoginScreen.class);
                            session.logoutUser();
                            startActivity(i);
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

    public void hideAll() {
        liChangepassword.setVisibility(View.GONE);
        liUpdatedetails.setVisibility(View.GONE);
    }


    ///////////////////////////
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
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {


                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    Log.v("testing_camera", "for less than lollipop");
                    mImageCaptureUri = Uri.fromFile(mFileTemp);
                } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    Log.v("testing_camera", "for greater than lollipop");

                    mImageCaptureUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", mFileTemp);
                }

                //   mImageCaptureUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", mFileTemp);
            } else {
                /*
                 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
	        	 */
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
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
                    inputStream.close();
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

               /* bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
                ivUploadimage.setImageBitmap(bitmap);*/

                photo = BitmapFactory.decodeFile(mFileTemp.getPath());
                profile_image = encodeTobase64(photo);
                ivUploadimage.setImageBitmap(photo);

                //tvShowCard.setVisibility(View.GONE);
                //ivUpLoad.setVisibility(View.GONE);
                // ivInsuranceCard.setOnClickListener(null);


                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private class UsPhoneNumberFormatter implements TextWatcher {
        //This TextWatcher sub-class formats entered numbers as 1 (123) 456-7890
        private boolean mFormatting; // this is a flag which prevents the
        // stack(onTextChanged)
        private boolean clearFlag;
        private int mLastStartLocation;
        private String mLastBeforeText;
        private WeakReference<EditText> mWeakEditText;

        public UsPhoneNumberFormatter(WeakReference<EditText> weakEditText) {
            this.mWeakEditText = weakEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            if (after == 0 && s.toString().equals("1 ")) {
                clearFlag = true;
            }
            mLastStartLocation = start;
            mLastBeforeText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO: Do nothing
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Make sure to ignore calls to afterTextChanged caused by the work
            // done below
            if (!mFormatting) {
                mFormatting = true;
                int curPos = mLastStartLocation;
                String beforeValue = mLastBeforeText;
                String currentValue = s.toString();
                String formattedValue = formatUsNumber(s);
                if (currentValue.length() > beforeValue.length()) {
                    int setCusorPos = formattedValue.length()
                            - (beforeValue.length() - curPos);
                    mWeakEditText.get().setSelection(setCusorPos < 0 ? 0 : setCusorPos);
                } else {
                    int setCusorPos = formattedValue.length()
                            - (currentValue.length() - curPos);
                    if (setCusorPos > 0 && !Character.isDigit(formattedValue.charAt(setCusorPos - 1))) {
                        setCusorPos--;
                    }
                    mWeakEditText.get().setSelection(setCusorPos < 0 ? 0 : setCusorPos);
                }
                mFormatting = false;
            }
        }

        private String formatUsNumber(Editable text) {
            StringBuilder formattedString = new StringBuilder();
            // Remove everything except digits
            int p = 0;
            while (p < text.length()) {
                char ch = text.charAt(p);
                if (!Character.isDigit(ch)) {
                    text.delete(p, p + 1);
                } else {
                    p++;
                }
            }
            // Now only digits are remaining
            String allDigitString = text.toString();

            int totalDigitCount = allDigitString.length();

            if (totalDigitCount == 0
                    || (totalDigitCount > 10 && !allDigitString.startsWith("1"))
                    || totalDigitCount > 11) {
                // May be the total length of input length is greater than the
                // expected value so we'll remove all formatting
                text.clear();
                text.append(allDigitString);
                return allDigitString;
            }
            int alreadyPlacedDigitCount = 0;
            // Only '1' is remaining and user pressed backspace and so we clear
            // the edit text.
            if (allDigitString.equals("1") && clearFlag) {
                text.clear();
                clearFlag = false;
                return "";
            }
            if (allDigitString.startsWith("1")) {
                formattedString.append("1 ");
                alreadyPlacedDigitCount++;
            }
            // The first 3 numbers beyond '1' must be enclosed in brackets "()"
            if (totalDigitCount - alreadyPlacedDigitCount > 3) {
                formattedString.append("("
                        + allDigitString.substring(alreadyPlacedDigitCount,
                        alreadyPlacedDigitCount + 3) + ") ");
                alreadyPlacedDigitCount += 3;
            }
            // There must be a '-' inserted after the next 3 numbers
            if (totalDigitCount - alreadyPlacedDigitCount > 3) {
                formattedString.append(allDigitString.substring(
                        alreadyPlacedDigitCount, alreadyPlacedDigitCount + 3)
                        + "-");
                alreadyPlacedDigitCount += 3;
            }
            // All the required formatting is done so we'll just copy the
            // remaining digits.
            if (totalDigitCount > alreadyPlacedDigitCount) {
                formattedString.append(allDigitString
                        .substring(alreadyPlacedDigitCount));
            }

            text.clear();
            text.append(formattedString.toString());
            return formattedString.toString();
        }

    }
}


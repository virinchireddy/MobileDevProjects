package sooner.om.com.sooner.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import sooner.om.com.sooner.ChangePassword;
import sooner.om.com.sooner.PatientLandingScreen;
import sooner.om.com.sooner.R;
import sooner.om.com.sooner.adapter.InsuranceCardUpload;
import sooner.om.com.sooner.app.Config;
import sooner.om.com.sooner.helper.CircularImageView;
import sooner.om.com.sooner.helper.CustomizeDialog;
import sooner.om.com.sooner.helper.InsuranceCard;
import sooner.om.com.sooner.helper.Others;
import sooner.om.com.sooner.helper.SessionManager;
import sooner.om.com.sooner.imagecrop.CropImage;
import sooner.om.com.sooner.imagecrop.InternalStorageContentProvider;
import sooner.om.com.sooner.network.ConnectionDetector;
import sooner.om.com.sooner.network.ServerCall;

import static android.app.Activity.RESULT_OK;

//contains all the details of the user.
public class FragmentProfile extends Fragment implements OnClickListener, ServerCall.AsyncTaskInterface {

    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
    View rootView;
    private String profile_image = "Not changed", insurance_image = "Not changed";
    private Button btnDob;
    // LinearLayout liMainView;
    private ImageView ivInsuranceCard;
    private CircularImageView ivUploadimage;
    private TextView etEmail, etPhoneno, etAddress;
    private SessionManager session;
    private Others others;
    private ConnectionDetector con;
    private TextView tvUsername, tvImageCount;
    private String photoIdentification;
    private List<InsuranceCard> _images;
    private ServerCall serverCall;
    private ViewPager vpInsuranceCard;
    private int imagePosition = -1;
    private int day;
    private int month;
    private int year;
    private File mFileTemp;


    //returns boolean "value" for the given key in a json else returns NA
    public static String optString(JSONObject json, String key) {
        // http://code.google.com/p/android/issues/detail?id=13830
        if (json.isNull(key) || json.optString(key).equals("null") || json.optString(key).equals(""))
            return "NA";
        else
            return json.optString(key);
    }

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    //first executable method in this class
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        rootView = inflater.inflate(R.layout.fragment_patient_profile_screen,
                container, false);
        initialize();

        details();
        return rootView;
        // getdetails();

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    //initialises all the attributes in this class.
    public void initialize() {

        Button btnLogout = (Button) rootView.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(this);
        Button btnChangePassword = (Button) rootView
                .findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);
        btnDob = (Button) rootView.findViewById(R.id.btnDob);
        btnDob.setOnClickListener(this);
        etEmail = (TextView) rootView.findViewById(R.id.etEmail);
        etPhoneno = (TextView) rootView.findViewById(R.id.etPhoneno);
        etEmail.setOnClickListener(this);
        etPhoneno.setOnClickListener(this);
        ivInsuranceCard = (ImageView) rootView.findViewById(R.id.ivInsuranceCard);
        ivInsuranceCard.setOnClickListener(this);
        tvImageCount = (TextView) rootView.findViewById(R.id.tvImageCount);
        _images = new ArrayList<>();
        vpInsuranceCard = (ViewPager) rootView.findViewById(R.id.vpInsuranceCard);
        btnDob = (Button) rootView.findViewById(R.id.btnDob);
        etAddress = (TextView) rootView.findViewById(R.id.etAddress);
        tvUsername = (TextView) rootView.findViewById(R.id.tvUsername);
        Button btnSavechanges = (Button) rootView.findViewById(R.id.btnSavechanges);
        ImageView ivAddImage = (ImageView) rootView.findViewById(R.id.ivAddImage);
        ImageView ivDeleteImage = (ImageView) rootView.findViewById(R.id.ivDeleteImage);
        // adding listener to the buttons and view in the screen
        btnSavechanges.setOnClickListener(this);
        ivAddImage.setOnClickListener(this);
        ivUploadimage = (CircularImageView) rootView
                .findViewById(R.id.ivUploadimage);
        ivUploadimage.setOnClickListener(this);
        etAddress.setOnClickListener(this);
        ivDeleteImage.setOnClickListener(this);
        // getting the instance of the device calender
        Calendar cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        // creating the session and network objects
        session = new SessionManager(getActivity());
        others = new Others(getActivity());
        con = new ConnectionDetector(getActivity());

        // identifies weather the device is having external storage or not
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), Calendar.getInstance().getTimeInMillis()+".jpg");
        } else {
            mFileTemp = new File(getActivity().getFilesDir(), Calendar.getInstance().getTimeInMillis()+".jpg");
        }

    }

    // this will call after hitting the views
    public void onClick(View v) {

        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btnChangePassword:
                Intent i = new Intent(getActivity(), ChangePassword.class);
                startActivity(i);
                break;
            case R.id.etEmail:
                showInfoAlert("If you want to update your email, please contact Sound Sleep Health team.");
                break;
            case R.id.etPhoneno:
                showInfoAlert("If you want to update your phone number, please contact Sound Sleep Health team.");
                break;
            case R.id.etAddress:
                showInfoAlert("If you want to update your address, please contact Sound Sleep Health team.");
                break;
            case R.id.btnDob:
                DateDialog();
                break;
            case R.id.ivDeleteImage:
                // check weather the insurance images exist
                if (imagePosition > -1) {
                    deleteConfirmation();
                } else
                    others.ToastMessage("No images to delete");
                break;
            case R.id.btnLogout:
                session.logoutUser();
                break;
            case R.id.ivUploadimage:
                photoIdentification = "profile";
                getPhoto();
                break;
            case R.id.ivAddImage:
            case R.id.ivInsuranceCard:
                photoIdentification = "insuranceCard";
                getPhoto();
                break;
            case R.id.btnSavechanges:
                photoIdentification = "";
                sendUpDatedDetails();
                break;
        }
    }

    private void sendUpDatedDetails() {
        // adding the list of parameters required to get the result from server
        String email = etEmail.getText().toString();
        String address = etAddress.getText().toString();
        if (address.equals("NA"))
            address = "";
        String dob = btnDob.getText().toString();
        if (dob.equals("NA"))
            dob = "";
        String phone = etPhoneno.getText().toString().replace("(", "").replace(") - ", "").replace(" - ", "");
        try {
            JSONObject obj = new JSONObject();
            obj.put("_UserId", PatientLandingScreen.userId);
            obj.put("_AuthenticationToken", PatientLandingScreen.authenticationToken);
            obj.put("_FacilityId", PatientLandingScreen.facilityId);
            obj.put("_Email", email);
            if (!dob.equals(""))
                obj.put("_DOB", dob);
            obj.put("_Address", address);
            obj.put("_Contact", phone);
            obj.put("_UserName", tvUsername.getText().toString());
            obj.put("_UserType", "CLIENT");
            if (profile_image.equals("")) {
                obj.put("_Image", "Not changed");
            } else {
                obj.put("_Image", profile_image);
            }
            // cheking the internet connection
            if (con.isConnectingToInternet()) {
                senddetails(obj, Config.URL_SEND_PROFILE_DETAILS);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteConfirmation() {
        new AlertDialog.Builder(getActivity())

                .setMessage("Are you sure You want to delete the Image")
                .setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog,
                                    int id) {
                                deleteImageRequest();
                            }

                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    // sending the required parameters to server to delete the insurance card
    private void deleteImageRequest() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("_UserId", PatientLandingScreen.userId);
            obj.put("_AuthenticationToken",
                    PatientLandingScreen.authenticationToken);
            obj.put("_FacilityId",
                    PatientLandingScreen.facilityId);
            obj.put("_InsurenceCard", _images.get(imagePosition).getImageId());

            if (con.isConnectingToInternet()) {
                deleteInsuranceCard(obj, Config.URL_DELETE_INSURANCE_CARD);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // server url request for delete insurance card
    private void deleteInsuranceCard(JSONObject obj, String urlDeleteInsuranceCard) {
        serverCall.postUrlRequest(urlDeleteInsuranceCard, obj, "deleteInsurance");

    }

    // shows the dialog box
    private void showInfoAlert(String message) {
        // TODO Auto-generated method stub
        CustomizeDialog customizeDialog = new CustomizeDialog(getActivity());
        customizeDialog.setMessage(message);
        customizeDialog.show();
    }

    //send details to server.
    private void senddetails(JSONObject obj, String url) {
        // TODO Auto-generated method stub
        serverCall.putUrlRequest(url, obj, "profilePostData");
    }

    // reload the current screen after the data inserted or deleted
    protected void reloadView() {
        // TODO Auto-generated method stub
        // identify it is navigated from add insurance card so that recently uploded insurance card images will display
        Others.isInsuranceUploaded = true;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    // display the date dialog for date of birth
    private void DateDialog() {
        // TODO Auto-generated method stub

        OnDateSetListener listener = new OnDateSetListener() {
            // set the value after selecting date from dialog box
            @Override
            public void onDateSet(DatePicker view, int _year, int monthOfYear,
                                  int dayOfMonth) {

                btnDob.setText(monthOfYear + 1 + "/" + dayOfMonth + "/" + _year);
                day = dayOfMonth;
                month = monthOfYear;
                year = _year;
            }
        };

        DatePickerDialog dpDialog = new DatePickerDialog(getActivity(),
                listener, year, month, day);
        // setting the max date so that it can't exceed the current date
        dpDialog.getDatePicker().setMaxDate(new Date().getTime());
        dpDialog.show();
    }

    //server request for initial load of profile
    private void details() {
        // TODO Auto-generated method stub
        try {
            // parameters which the server accepts to get the profile
            JSONObject obj = new JSONObject();
            obj.put("_UserId", PatientLandingScreen.userId);
            obj.put("_AuthenticationToken",
                    PatientLandingScreen.authenticationToken);
            obj.put("_FacilityId", PatientLandingScreen.facilityId);
            obj.put("_UserType", "CLIENT");
            if (con.isConnectingToInternet()) {
                getDetails(obj);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getDetails(JSONObject obj) {
        serverCall = new ServerCall(getActivity(), FragmentProfile.this);
        serverCall.postUrlRequest(Config.URL_GET_PROFILE_DETAILS, obj, "profile");

    }

    //upload profile pic by using mobile camera or gallery.
    private void getPhoto() {
        // TODO Auto-generated method stub
        // dialog for selecting from camera and gallery
        final String[] items = new String[]{"Take from camera",
                "Select from gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
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
                            openCamera();
                        } else {
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
    // opening device camera
    private void openCamera() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                    mImageCaptureUri = Uri.fromFile(mFileTemp);
                } else{
                    mImageCaptureUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", mFileTemp);
                }

            } else {
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            if (photoIdentification.equals("profile"))
                intent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_FRONT);
            else
                intent.putExtra("android.intent.extras.CAMERA_FACING", Camera.CameraInfo.CAMERA_FACING_BACK);

            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startCropImage() {

        Intent intent = new Intent(getActivity(), CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, true);
        // this is for rectangular and square crop
        if (photoIdentification.equals("profile")) {
            intent.putExtra(CropImage.ASPECT_X, 1);
            intent.putExtra(CropImage.ASPECT_Y, 1);
        } else {
            intent.putExtra(CropImage.ASPECT_X, 3);
            intent.putExtra(CropImage.ASPECT_Y, 2);
        }
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    private void openFile() {
        // TODO Auto-generated method stub
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
    }

    private void loadResponse(JSONObject response) {

        try {
            etEmail.setText(response.getString("_EmailId"));
            tvUsername.setText(response.getString("_UserName"));
            String phoneno = optString(response,"_Contact");
            Log.v("phone number", phoneno);
            // displaying the phone number in US format
            if (phoneno.length() == 10) {
                phoneno = "(" + phoneno;
                phoneno = phoneno.substring(0, 4) + ") - " + phoneno.substring(4);
                phoneno = phoneno.substring(0, 11) + " - " + phoneno.substring(11);
            }
            etPhoneno.setText(phoneno);
            // displaying the date of birth in MM/DD/YYYY format
            btnDob.setText(optString(response, "_Dob"));
            //assigning the day,month,year to date dialog for changing later
            if (!optString(response, "_Dob").equals("NA")) {
                String date = optString(response, "_Dob");
                String[] splited = date.split("/");
                Log.v("date", Arrays.toString(splited));
                day = Integer.parseInt(splited[1]);
                month = Integer.parseInt(splited[0]) - 1;
                year = Integer.parseInt(splited[2]);
            }

            // attaching the entire address to single string and
            String address = "";
            if (!optString(response,"_Address").equals("NA"))
                address = optString(response,"_Address");
            if (!optString(response, "_city").equals("NA"))
                if(address.length()==0)
                    address =optString(response, "_city");
                else
                    address = address + "\n" + optString(response, "_city");
            if (!optString(response, "_state").equals("NA"))
                if(address.length()==0)
                    address =optString(response, "_state");
                else
                    address = address + "\n" + optString(response, "_state");
            if (!optString(response, "_Zipcode").equals("NA"))
                if(address.length()==0)
                    address =optString(response, "_Zipcode");
                else
                    address = address + "\n" + optString(response, "_Zipcode");
            if (address.length() == 0)
                address = "NA";
            etAddress.setText(address);
            // user profile picture
            String userProfileUrl = response
                    .getString("_ImageUrl");
            // loading the insurance card images to list
            final JSONObject insuranceCardUrl = response.getJSONObject("_InsurenceCard");
            if (insuranceCardUrl.length() > 0) {
                Iterator<String> keys = insuranceCardUrl.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String imageUrl = insuranceCardUrl.getString(key);
                    String[] finalString = {key, imageUrl};
                    _images.add(new InsuranceCard(finalString));
                }
                // assigning the list to viewpager for swiping
                vpInsuranceCard.setAdapter(new InsuranceCardUpload(getActivity(), _images));
                // identifying weather the card is recently uploaded or not
                if (Others.isInsuranceUploaded)
                    vpInsuranceCard.setCurrentItem(_images.size());
                else
                    vpInsuranceCard.setCurrentItem(0);
                // listener for image change in viewpager
                vpInsuranceCard.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        tvImageCount.setText("(" + (position + 1) + "/" + insuranceCardUrl.length() + ")");
                        imagePosition = position;
                    }

                    @Override
                    public void onPageSelected(int position) {

                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            } else {
                ivInsuranceCard.setVisibility(View.VISIBLE);
                vpInsuranceCard.setVisibility(View.GONE);
                tvImageCount.setText("");
                imagePosition = -1;

            }
            // for displaying user profile picture
            if (!response.optString("_ImageUrl").isEmpty() && !response.optString("_ImageUrl").equals("No Image")) {
                Picasso.with(getActivity()).invalidate(userProfileUrl);
                Picasso.with(getActivity())
                        .load(userProfileUrl)
                        .into(ivUploadimage);

            }// if user not uploaded the dummy image will display
            else {
                ivUploadimage.setImageDrawable(getResources().getDrawable(R.drawable.patient_profile_image));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            // if the image is taken from gallery then this block of code will execute
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
                    e.printStackTrace();
                }

                break;
            // if the user chooses the image to crop
            case REQUEST_CODE_TAKE_PICTURE:

                startCropImage();
                break;
            // after cropping the image
            case REQUEST_CODE_CROP_IMAGE:

                String path = data.getStringExtra(CropImage.IMAGE_PATH);
                if (path == null) {

                    return;
                }
                Bitmap photo;
                if (photoIdentification.equals("profile")) {
                    photo = BitmapFactory.decodeFile(mFileTemp.getPath());
                    profile_image = encodeTobase64(photo);
                    sendUpDatedDetails();
                } else {
                    photo = BitmapFactory.decodeFile(mFileTemp.getPath());
                    insurance_image = encodeTobase64(photo);
                    sendInsuranceCard();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // sending the insurance card to server
    private void sendInsuranceCard() {
        // TODO Auto-generated method stub
        try {
            // adding the required parameters to json object including the image
            JSONObject obj = new JSONObject();
            obj.put("_UserId", PatientLandingScreen.userId);
            obj.put("_AuthenticationToken",
                    PatientLandingScreen.authenticationToken);
            obj.put("_FacilityId",
                    PatientLandingScreen.facilityId);
            obj.put("_InsurenceCard", insurance_image);
            // check for internet connection
            if (con.isConnectingToInternet()) {
                senddetails(obj, Config.URL_POST_INSURANCE_CARD);
            } else {
                con.failureAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //to encode the image from bitmap to base64.
    public String encodeTobase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] image_Array = baos.toByteArray();
        return Base64.encodeToString(image_Array, Base64.DEFAULT);
    }

    // handles server response in this method
    @Override
    public void onAsyncTaskInterfaceResponse(JSONObject result, String tag) {

        switch (tag) {
            case "profile":
                loadResponse(result);
                break;
            case "deleteInsurance":
                reloadView();
                Toast.makeText(getActivity(), "Insurance Card deleted successfully", Toast.LENGTH_SHORT).show();
                break;
            default:
                try {
                    // identifying which button the user clicks
                    switch (photoIdentification) {
                        case "profile":
                            Toast.makeText(getActivity(),
                                    "Profile image Updated Successfully",
                                    Toast.LENGTH_LONG).show();
                            reloadView();
                            break;
                        case "insuranceCard":
                            Toast.makeText(getActivity(),
                                    "Insurance card updated successfully", Toast.LENGTH_LONG).show();
                            reloadView();
                            break;
                        default:
                            Toast.makeText(getActivity(),
                                    "Details updated successfully",
                                    Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
package sooner.om.com.sooner.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import sooner.om.com.sooner.LoginScreen;
import sooner.om.com.sooner.R;


public class AcceptTermsConditions extends Activity {


    private ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_conditions_screen);
        // linking ui elements to java code
        WebView wvTerms = (WebView) findViewById(R.id.wvTermsAndConditions);
        // accessing web setting in our app
        WebSettings settings = wvTerms.getSettings();
        // enabling java script
        settings.setJavaScriptEnabled(true);
        // enabling the scrollbar
        wvTerms.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        // loading bar will be show until the terms and conditions screen is loaded
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        progressBar = ProgressDialog.show(AcceptTermsConditions.this, "Terms & Conditions", "Loading...");

        wvTerms.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i("hello", "Finished loading URL: " + url);
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            // alert dialog will display if the error message occure
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("hello", "Error: " + description);
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
                alertDialog.setTitle("Error");
                alertDialog.setMessage(description);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialog.show();
            }
        });
        //settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // url which is to be loaded
        wvTerms.loadUrl("https://api.sooner.io/home/termsCondition");
    }

    // cancel button operation
    public void cancel(View v) {
        finish();
        LoginScreen.cTerms = false;
    }

    // agree button operation
    public void agree(View v) {
        LoginScreen.cTerms = true;
        finish();
    }
}





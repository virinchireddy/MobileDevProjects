package they2ze.android.om.com.they2ze;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import they2ze.android.om.com.they2ze.Network.ConnectionDetector;

/**
 * Created by Ndroid on 1/10/2017.
 */

public class LoginScreen extends Activity {

    static FrameLayout fullscreenV;
    WebView wvTerms;
    float dp;
    LinearLayout liContainer;
    ConnectionDetector con;
    private ProgressDialog progressBar;
    private MyWebChromeClient mWebChromeClient = null;
    private View mCustomView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view);

        con = new ConnectionDetector(LoginScreen.this);

            wvTerms = (WebView) findViewById(R.id.wvTermsAndConditions);
        liContainer = (LinearLayout) findViewById(R.id.liContainer);
            mWebChromeClient = new MyWebChromeClient();
            wvTerms.setWebChromeClient(mWebChromeClient);
            // accessing web setting in our app
            WebSettings settings = wvTerms.getSettings();

            // enabling java script
            settings.setJavaScriptEnabled(true);
            // enabling the scrollbar
            wvTerms.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            // loading bar will be show until the terms and conditions screen is loaded
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            progressBar = ProgressDialog.show(LoginScreen.this, "", "Loading...");

            wvTerms.setWebViewClient(new WebViewClient() {
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    //progressBar = ProgressDialog.show(LoginScreen.this, "", "Loading...");
                /*if (url.equals("http://optionmatrix.in/they2ze/mobile_dev/mobile/home/index")) {
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    return true; // Handle By application itself
                } else {
                    view.loadUrl(url);

                    return true;
                }*/
                    Log.v("navigate", url.toString());
                    if (url.contains("navigate-")) {
                        String navigate = url.replace("navigate-", "");
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(navigate));
                        startActivity(intent);
                        progressBar.dismiss();

                        //return true; // Handle By application itself

                    } else if (url.contains("tel")) {
                        Intent intent = new Intent(Intent.ACTION_DIAL,
                                Uri.parse(url));
                        startActivity(intent);
                        progressBar.dismiss();
                    } else if (!url.contains("navigate-") || !url.contains("tel")) {
                        view.loadUrl(url);

                        progressBar.dismiss();

                    }
                    return true;
                }

                public void onPageFinished(WebView view, String url) {
                    Log.i("for lodader", "Finished loading URL: " + url);
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                }

                // alert dialog will display if the error message occure
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    if (!con.isConnectingToInternet()) {
                        con.failureAlert();

                      liContainer.setVisibility(View.GONE);
                    } else {

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
                }
            });
            // url which is to be loaded

                wvTerms.loadUrl("http://optionmatrix.in/they2ze/mobile_dev/mobile/home/index");



    }

        @Override
        public void onBackPressed () {
            if (wvTerms.isFocused() && wvTerms.canGoBack()) {
                wvTerms.goBack();
            } else {
                super.onBackPressed();
                finish();
            }
        }

        public class MyWebChromeClient extends WebChromeClient {
            FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                // if a view already exists then immediately terminate the new one
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }
                liContainer = (LinearLayout) findViewById(R.id.liContainer);
                liContainer.setVisibility(View.GONE);
                mCustomViewContainer = new FrameLayout(LoginScreen.this);
                mCustomViewContainer.setLayoutParams(LayoutParameters);
                mCustomViewContainer.setBackgroundResource(android.R.color.black);
                view.setLayoutParams(LayoutParameters);
                mCustomViewContainer.addView(view);
                mCustomView = view;
                mCustomViewCallback = callback;
                mCustomViewContainer.setVisibility(View.VISIBLE);
                setContentView(mCustomViewContainer);
            }

            @Override
            public void onHideCustomView() {
                if (mCustomView == null) {
                    return;
                } else {
                    // Hide the custom view.
                    mCustomView.setVisibility(View.GONE);
                    // Remove the custom view from its container.
                    mCustomViewContainer.removeView(mCustomView);
                    mCustomView = null;
                    mCustomViewContainer.setVisibility(View.GONE);
                    mCustomViewCallback.onCustomViewHidden();
                    // Show the content view.
                    liContainer.setVisibility(View.VISIBLE);
                    setContentView(liContainer);
                }
            }
        }
    @Override
    public void onResume(){
        super.onResume();
        // put your code here...

    }
}

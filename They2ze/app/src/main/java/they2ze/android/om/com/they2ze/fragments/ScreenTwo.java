package they2ze.android.om.com.they2ze.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import they2ze.android.om.com.they2ze.MainActivity;
import they2ze.android.om.com.they2ze.R;


/**
 * Created by Ndroid on 1/10/2017.
 */

public class ScreenTwo extends Fragment {
    View rootView;
    TextView tvText;

    Button btnProceed;

    public ScreenTwo() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_screen_two, container, false);

        intilize();
        return rootView;
    }

    public void intilize() {
        tvText = (TextView) rootView.findViewById(R.id.tvText);
        tvText.setText(MainActivity.proceed_screen);
        tvText.setMovementMethod(new ScrollingMovementMethod());




    }

}

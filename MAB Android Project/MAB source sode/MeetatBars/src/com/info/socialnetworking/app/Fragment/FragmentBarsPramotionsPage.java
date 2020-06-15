package com.info.socialnetworking.app.Fragment;

import com.info.socialnetworking.app.meetatbars.MainMenu;
import com.info.socialnetworking.app.meetatbars.R;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class FragmentBarsPramotionsPage extends Fragment implements
		OnClickListener {

	TextView tvBars, tvPromotions;
	String filterIdentification,jsonObject;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_bars_page,
				container, false);
		savedInstanceState=getArguments();
		filterIdentification=savedInstanceState.getString("FilterIdentification");
		if(filterIdentification.equals("filter_applied"))
			jsonObject=savedInstanceState.getString("jsonObject");
		tvBars = (TextView) rootView.findViewById(R.id.tvBars);
		tvPromotions = (TextView) rootView.findViewById(R.id.tvPromotions);
		tvBars.setOnClickListener(this);
		tvPromotions.setOnClickListener(this);
		initialNavigation();
		return rootView;
	}

	private void initialNavigation() {
		// TODO Auto-generated method stub
		if (MainMenu.clickedmenu == 2) {
			tvPromotions.setBackgroundColor(getResources().getColor(
					R.color.tab_color));
			PromotionsPage promotion = new PromotionsPage();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flBarsPage, promotion).addToBackStack(null)
					.commit();
		} else {
			Bundle obj=new Bundle();
			if(filterIdentification.equals("filter_applied"))
				obj.putString("jsonObject", jsonObject);
			obj.putString("FilterIdentification",filterIdentification);
			BarsPage bars = new BarsPage();
			bars.setArguments(obj);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flBarsPage, bars).addToBackStack(null)
					.commit();
			tvBars.setBackgroundColor(getResources().getColor(
					R.color.tab_color));
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == R.id.tvBars) {

			tvBars.setBackgroundColor(getResources().getColor(
					R.color.tab_color));
			tvPromotions.setBackgroundColor(getResources().getColor(
					R.color.login_pressed));
			Bundle obj=new Bundle();
			obj.putString("FilterIdentification",MainMenu.barsFilterIdentification);
			BarsPage bars = new BarsPage();
			bars.setArguments(obj);
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flBarsPage, bars).addToBackStack(null)
					.commit();

		} else if (v.getId() == R.id.tvPromotions) {
			tvBars.setBackgroundColor(getResources()
					.getColor(R.color.login_pressed));
			tvPromotions.setBackgroundColor(getResources().getColor(
					R.color.tab_color));

			PromotionsPage promotions = new PromotionsPage();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flBarsPage, promotions).addToBackStack(null)
					.commit();
		}
	}

}

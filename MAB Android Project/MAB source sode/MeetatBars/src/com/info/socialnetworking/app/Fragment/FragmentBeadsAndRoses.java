package com.info.socialnetworking.app.Fragment;

import com.info.socialnetworking.app.meetatbars.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentBeadsAndRoses extends Fragment implements OnClickListener {

	Button btnBuy,btnSell;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_beads_and_roses,
				container, false);
		btnBuy=(Button)rootView.findViewById(R.id.btnBuy);
		btnSell=(Button)rootView.findViewById(R.id.btnSell);

		btnBuy.setOnClickListener(this);
		btnSell.setOnClickListener(this);
		
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.btnBuy){
			
			BuyBeadsRoses buyBeads = new BuyBeadsRoses();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, buyBeads).addToBackStack(null)
					.commit();
			
		}else if (v.getId()==R.id.btnSell) {
			
			SellBeadsRoses sellBeads = new SellBeadsRoses();
			this.getFragmentManager().beginTransaction()
					.replace(R.id.flContainer, sellBeads).addToBackStack(null)
					.commit();
			
		}
			
	}
}

package org.pocketcampus.plugin.bikes.android.ui;


import org.pocketcampus.R;
import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class BikesStationDialog extends Dialog implements OnClickListener {

	Context ctx_;
	
	BikeEmplacement displayedStation_;
	
	TextView title_;
	TextView empty_;
	TextView available_;

	
	public BikesStationDialog(Context context, BikeEmplacement bikeEmplacement) {
		super(context);
		
		ctx_ = context;
		displayedStation_ = bikeEmplacement;
		
		build();
		setContent(bikeEmplacement);
		
	}
	

	private void build(){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setTitle(ctx_.getResources().getString(R.string.directory_person_details_dialog_tile));
		setContentView(R.layout.bikes_details_dialog);
		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		setCanceledOnTouchOutside(true);
	}
	
	private void setContent(BikeEmplacement be){
		title_ = (TextView) findViewById(R.id.bikes_details_title_dialog);
		title_.setText(be.name);
		
		String ep;
		if(be.numberOfEmptySpaces == 1)
			ep = getString(R.string.bikes_empty_slot);
		else
			ep = getString(R.string.bikes_empty_slots);
		
		empty_ = (TextView) findViewById(R.id.bikes_textView_empty);
		empty_.setText(be.numberOfEmptySpaces + " " +ep);
		
		
		String ab;
		if(be.numberOfAvailableBikes == 1)
			ab = getString(R.string.bikes_available_bike);
		else
			ab = getString(R.string.bikes_available_bikes);
		available_ = (TextView) findViewById(R.id.bikes_textView_available);
		available_.setText(be.numberOfAvailableBikes + " " + ab);
		
	}
	

	private String getString(int resId) {
		return ctx_.getString(resId)+"";
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}

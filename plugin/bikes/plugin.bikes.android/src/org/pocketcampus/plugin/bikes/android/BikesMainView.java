package org.pocketcampus.plugin.bikes.android;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.*;
import org.pocketcampus.plugin.bikes.android.iface.IBikesView;
import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;


import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class BikesMainView extends PluginView implements IBikesView{

	private BikesController mController;
	private BikesModel mModel;
	
	private ListView mList;
	private StandardLayout mLayout;
	
	private OnItemClickListener oicl;

	/**
	 * Called once the view is connected to the controller.
	 * If you don't implement <code>getMainControllerClass()</code> 
	 * then the controller given here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		// Get and cast the controller and model
		mController = (BikesController) controller;
		mModel = (BikesModel) controller.getModel();
		
		mLayout = new StandardLayout(this);
		setContentView(mLayout);
		
		mController.getAvailableBikes();
		mLayout.setText(getString(R.string.bikes_loading));
		
		oicl = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int pos, long arg3) {
				PCItem item = (PCItem) adapter.getItemAtPosition(pos);
				if( item.isEmptyLayout() ){
					String msg = "";
					
					//make this a little less ugly, getting the relative layout  to get first textview using the hadcoded id and finally converting the charSequence to a string
					String stationsName = ((TextView)((PCEmptyLayoutItem)adapter.getItemAtPosition(pos)).getLayout().findViewById(4)).getText().toString();
					
					for(BikeEmplacement be: mModel.getAvailablesBikes()){
						if(be.name.equals(stationsName)){
							String ab;
							if(be.numberOfAvailableBikes == 1)
								ab = getString(R.string.bikes_available_bike);
							else
								ab = getString(R.string.bikes_available_bikes);
							
							String ep;
							if(be.numberOfEmptySpaces == 1)
								ep = getString(R.string.bikes_empty_slot);
							else
								ep = getString(R.string.bikes_empty_slots);
							
							msg = be.name + 
										//" is at:\n" +
										//"Lat: " + be.geoLat + "\n" +
										//"Lon: " + be.geoLng + "\n" +
										"\n"+ getString(R.string.bikes_has)+" " + be.numberOfAvailableBikes + ab +"\n" +
										getString(R.string.bikes_and) +" " + be.numberOfEmptySpaces + ep;
							
							//exiting the loop
							break;
						}
					}
					
					
					Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
					toast.show();
				}
			}
			
		};
		
		
	}
	
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return BikesController.class;
	}
	
	private void displayData(){

		if(mModel.getAvailablesBikes().size() > 0)
			mLayout.setText("");
		
		ArrayList<PCItem> items = new ArrayList<PCItem>();
//		boolean found = false;
		
	
		items.add(new PCSectionItem(getString(R.string.bikes_velopass),getString(R.string.bikes_Available)));
		
		for(BikeEmplacement be : mModel.getAvailablesBikes()){
			String nbBikes;
			int q = be.numberOfAvailableBikes;
			int pl = be.numberOfEmptySpaces + q;
			nbBikes = "" + q;
			
			nbBikes = nbBikes + " / ";
			
			if(pl < 10)
				nbBikes = nbBikes + " " + pl;
			else
				nbBikes = nbBikes + pl;
			
			
			if(pl > 0){
				RelativeLayout listElement = new RelativeLayout(this);
				float textSize = 15f;
				int layoutsWidth = 30;
				//bikeEmplacement name
				TextView titleView = new TextView(this);
				titleView.setText(be.name);
				titleView.setId(4);
				titleView.setTextSize(textSize);
				LayoutParams titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
				titleParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				titleParams.setMargins(15, 3, 3, 3);
				listElement.addView(titleView, titleParams);
				
				TextView totalPlacesView = new TextView(this);
				totalPlacesView.setText(pl + "");
				totalPlacesView.setGravity(Gravity.RIGHT);
				totalPlacesView.setTextSize(textSize);
				LayoutParams totalParams = new LayoutParams(layoutsWidth, LayoutParams.FILL_PARENT);
				totalParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				totalParams.setMargins(3, 3, 15, 3);
				totalPlacesView.setId(3);
				listElement.addView(totalPlacesView,totalParams);
				
				TextView slashView = new TextView(this);
				slashView.setText("/");
				slashView.setGravity(Gravity.RIGHT);
				slashView.setTextSize(textSize);
				slashView.setId(2);
				LayoutParams slashParams = new LayoutParams(layoutsWidth/3, LayoutParams.FILL_PARENT);
				slashParams.addRule(RelativeLayout.LEFT_OF, totalPlacesView.getId());
				slashParams.setMargins(3, 3, 3, 3);
				listElement.addView(slashView,slashParams);
				
				TextView bikesAvailableView = new TextView(this);
				bikesAvailableView.setText(q + "");
				bikesAvailableView.setGravity(Gravity.RIGHT);
				bikesAvailableView.setTextSize(textSize);
				bikesAvailableView.setId(1);
				LayoutParams availableParams = new LayoutParams(layoutsWidth, LayoutParams.FILL_PARENT);
				availableParams.addRule(RelativeLayout.LEFT_OF, slashView.getId());
				availableParams.setMargins(3, 3, 3, 3);
				listElement.addView(bikesAvailableView,availableParams);
				
				items.add(new PCEmptyLayoutItem(listElement));
			}
		}
		
		PCEntryAdapter adapter = new PCEntryAdapter(this, items);
		
		mList = new ListView(this);
		mList.setOnItemClickListener(oicl);
		mList.setAdapter(adapter);		
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mLayout.setLayoutParams(layoutParams);
		mList.setLayoutParams(layoutParams);
		mLayout.addView(mList);
		
	}
	
	ILabeler<BikeEmplacement> labeler = new ILabeler<BikeEmplacement>(){
		@Override
		public String getLabel(BikeEmplacement obj) {
			String nice;
			nice = obj.name + " " + obj.numberOfAvailableBikes; 
			return nice;
		}
	};

	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.bikes_network_error), Toast.LENGTH_SHORT);
		toast.show();
		
		mLayout.setText(getString(R.string.bikes_try_again_later));
	}

	@Override
	public void bikeListUpdated() {
		displayData();
		
	}
	
}

package org.pocketcampus.plugin.bikes.android;

import java.util.ArrayList;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.*;
import org.pocketcampus.plugin.bikes.android.iface.IBikesView;
import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;


import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
		mLayout.setText("Loading");
		
		oicl = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View arg1, int pos, long arg3) {
				if(!((PCItem) adapter.getItemAtPosition(pos)).isSection()){
					String msg = "";
					
					String stationsName = ((PCEntryItem)adapter.getItemAtPosition(pos)).title;
					
					for(BikeEmplacement be: mModel.getAvailablesBikes()){
						if(be.designation.equals(stationsName)){
							String ab;
							if(be.availableQuantity> 0)
								ab = " available bike";
							else
								ab = " availables bikes";
							
							String ep;
							if(be.empty > 0)
								ep = " empty bike slot";
							else
								ep = " empty bike slots";
							
							msg = be.designation + " is at:\n" +
										"Lat: " + be.geoLat + "\n" +
										"Lon: " + be.geoLng + "\n" +
										"and has " + be.availableQuantity + ab +"\n" +
										"and " + be.empty + ep;
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
		boolean found = false;
		
		//
		items.add(new PCSectionItem("Available bikes"));
		for(BikeEmplacement be:mModel.getAvailablesBikes()){
			if(be.availableQuantity > 0){
				items.add(new PCEntryItem(be.designation, be.availableQuantity+""));
				found = true;
				}
		}
		if(!found){
			items.add(new PCEntryItem("No bikes available", ""));
		}
		
		//
		found = false;
		//new section header
		items.add(new PCSectionItem("Empty docks"));
		//adding the content of the section
		for(BikeEmplacement be:mModel.getAvailablesBikes()){
			if(be.empty > 0){
				//element by element
				items.add(new PCEntryItem(be.designation, be.empty+""));
				found = true;
			}
		}if(!found){
			items.add(new PCEntryItem("No docks available", ""));
		}
		
		//coucou oriane, regarde ici pour savoir comment ajouter tout ce que tu veux!!!!!
		//svn comitt test
//		RelativeLayout t = new RelativeLayout(this);
//		TextView tt = new TextView(this);
//		tt.setText("houra haha");
//		t.addView( tt );
//		items.add(new PCEmptyLayoutItem(t));
		
		
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
			nice = obj.designation + " " + obj.availableQuantity; 
			return nice;
		}
	};

	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_SHORT);
		toast.show();
		
		mLayout.setText("Please try again later.");
	}

	@Override
	public void bikeListUpdated() {
		//Toast toast = Toast.makeText(getApplicationContext(), mModel.getAvailablesBikes().get(0).toString(), Toast.LENGTH_SHORT);
		//toast.show();
		displayData();
		
	}
	
}

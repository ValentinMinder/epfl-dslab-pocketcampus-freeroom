package org.pocketcampus.plugin.bikes;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.plugin.mainscreen.IAllowsID;
import org.pocketcampus.plugin.mainscreen.MainscreenAdapter;
import org.pocketcampus.plugin.mainscreen.MainscreenNews;
import org.pocketcampus.shared.plugin.bikes.BikeStation;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BikeStationList {
	
	private List<BikeStation> bikeStations_;
	
	private BikesPlugin main_;
	
	
	public BikeStationList(BikesPlugin main) {
		bikeStations_ = new ArrayList<BikeStation>();
		main_ = main;
	}
	
	public void loadBikes() {

		BikesPlugin.refreshing();
		
		class BikesRequest extends DataRequest {

			@Override
			public void onCancelled() {
				Log.d("BikeStationList", "Task cancelled");
				
			}

			@Override
			protected void doInUiThread(String result) {
				Log.d("BikeStationList", "Loading bikes");

				Type listType = new TypeToken<List<BikeStation>>() {}.getType();
				Gson gson = new Gson();
				
								
				bikeStations_ = gson.fromJson(result, listType);
				
				Log.d("BikeStationList","Bikes loaded");

				BikesPlugin.refreshed();
				
				main_.setBikeStationList(bikeStations_);
				main_.displayBikes();
				
				
			}
		}
		
		BikesPlugin.bikesRequestHandler.execute(new BikesRequest(), "bikes", (RequestParameters) null);
		
	}
	
	public List<BikeStation> getBikeStations() {
		return new ArrayList<BikeStation>(bikeStations_);
	}

}

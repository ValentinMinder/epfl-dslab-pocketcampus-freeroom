package org.pocketcampus.plugin.bikes;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.core.parser.JsonException;
import org.pocketcampus.shared.plugin.bikes.BikeStation;

import android.util.Log;
import android.widget.Toast;

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
				Toast.makeText(main_.getApplicationContext(),main_.getApplicationContext().getText(R.string.bikes_plugin_cancel), Toast.LENGTH_SHORT);
				BikesPlugin.refreshed();
			}

			@Override
			protected void doInUiThread(String result) {
				Log.d("BikeStationList", "Loading bikes");
			
				if(result == null) {
					cancel(true);
				}
								
				Type listType = new TypeToken<List<BikeStation>>() {}.getType();
				
				try {
					bikeStations_ = Json.fromJson(result, listType);
				} catch (JsonException e) {
					cancel(true);
				}
				
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

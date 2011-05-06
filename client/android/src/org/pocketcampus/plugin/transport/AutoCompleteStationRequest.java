package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.shared.plugin.transport.Location;
import org.pocketcampus.shared.plugin.transport.LocationType;

import com.google.gson.reflect.TypeToken;

abstract class AutoCompleteStationRequest extends DataRequest {
	private ArrayList<Location> locations_;

	public AutoCompleteStationRequest() {
		locations_ = new ArrayList<Location>();
	}
	
	@Override
	protected int expirationDelay() {
		// Location list is not likely to change.
		return 6 * 60 * 60;
	}

	@Override
	final protected void doInUiThread(String result) {
		// Extracts the result.
		Type AutocompleteType = new TypeToken<List<Location>>(){}.getType();
		locations_ = Json.fromJson(result, AutocompleteType);
		
		if(locations_ == null) {
			handleLocations(null);
			return;
		}
		
		// Keeps only the stations (no POIs etc)
		ArrayList<Location> stations = new ArrayList<Location>();;
		
		for(Location location : locations_) {
			if(location.type == LocationType.STATION) {
				stations.add(location);
			}
		}
		
		handleLocations(locations_);
	}

	protected abstract void handleLocations(ArrayList<Location> locations);
}



















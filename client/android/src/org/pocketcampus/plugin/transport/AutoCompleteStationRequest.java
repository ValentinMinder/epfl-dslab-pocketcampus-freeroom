package org.pocketcampus.plugin.transport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.shared.plugin.transport.Location;
import org.pocketcampus.shared.plugin.transport.LocationType;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

abstract class AutoCompleteStationRequest extends DataRequest {
	private ArrayList<Location> locations;

	public AutoCompleteStationRequest() {
		locations = new ArrayList<Location>();
	}
	
	@Override
	protected int expirationDelay() {
		// Location list is not likely to change.
		return 6 * 60 * 60;
	}

	@Override
	final protected void doInUiThread(String result) {
		// Extracts the result.
		Gson gson = new Gson();
		Type AutocompleteType = new TypeToken<List<Location>>(){}.getType();
		locations = gson.fromJson(result, AutocompleteType);
		
		// Keeps only the stations (no POIs etc)
		ArrayList<Location> stations = new ArrayList<Location>();
		
		for(Location location : locations) {
			if(location.type == LocationType.STATION) {
				stations.add(location);
			}
		}
		
		handleLocations(locations);
	}

	protected abstract void handleLocations(ArrayList<Location> locations);
}



















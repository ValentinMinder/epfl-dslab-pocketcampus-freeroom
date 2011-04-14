package org.pocketcampus.plugin.bikes;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.pocketcampus.core.plugin.Core;
import org.pocketcampus.shared.plugin.bikes.BikeStation;
import org.pocketcampus.shared.utils.URLLoader;

import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BikeStationParser {
	
	
	public static List<BikeStation> getBikeStations() throws IOException {
		
		String source = "[{\"empty_\": 9,\"bikes_\": 2,\"geoLat_\": 46.5205884245643,\"geoLng_\": 6.56823635101318,\"name_\": \"Avenue Piccard\"}]";//URLLoader.getSource(Core.getInstance().getServerUrl()+"/pocketcampus-server/Bikes/bikes");
		
		Type listType = new TypeToken<List<BikeStation>>() {}.getType();
		Gson gson = new Gson();
		List<BikeStation> b = gson.fromJson(source, listType);
		
		return b;
	}
	
	
}

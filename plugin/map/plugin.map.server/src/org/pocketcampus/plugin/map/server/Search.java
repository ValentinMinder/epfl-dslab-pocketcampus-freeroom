package org.pocketcampus.plugin.map.server;

import com.google.gson.Gson;
import org.pocketcampus.plugin.map.server.jsonitems.BasicSearchResponse;
import org.pocketcampus.plugin.map.server.jsonitems.GeometryR;
import org.pocketcampus.plugin.map.server.jsonitems.SearchProperties;
import org.pocketcampus.plugin.map.shared.MapItem;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;


/**
 * Class used to do routing search
 * 
 * @author Jonas, Johan
 *
 */
public class Search {
	/**
	 * Queries the plan.epfl.ch website and return the results
	 * @param text the query.
	 * @param max the maximum number of results.
	 * @return A list of MapElementBean containing the results.
	 */
	public static List<MapItem> searchTextOnEpflWebsite(String text, int max) {
		List<MapItem> list = new LinkedList<MapItem>();
		URL searchUrl;
		try {
			searchUrl = new URL("http://plan.epfl.ch/search?keyword=" + URLEncoder.encode(text, "UTF-8"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return list;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return list;
		}
		
		Gson gson = new Gson();

		try {
			InputStreamReader reader = new InputStreamReader(searchUrl.openStream());
			BasicSearchResponse response = gson.fromJson(reader, BasicSearchResponse.class);
			if(response != null && response.features != null) {
				for(int i = 0; i < response.features.length && i < max; i++) {
					GeometryR geometry = response.features[i].geometry;
					SearchProperties properties = response.features[i].properties;
					if(geometry != null && properties != null) {
						String description = null;
						if(properties.room != null && properties.room.length() > 0) {
							description = properties.room;
						}

                        double longitude = CoordinateConverter.convertEPSG4326ToLon(geometry.coordinates[0]);
                        double latitude = CoordinateConverter.convertEPSG4326ToLat(geometry.coordinates[1]);

						MapItem mapItem = new MapItem(properties.text, latitude, longitude, -1, -1);
						if (description != null) {
							mapItem.setDescription(description);
						}
						
						try {
							int floor = Integer.parseInt(properties.floor);
							mapItem.setFloor(floor);	
						} catch (Exception e){};
						
						mapItem.setCategory(properties.category);
						
						list.add(mapItem);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return list;
		}
		return list;
	}
}


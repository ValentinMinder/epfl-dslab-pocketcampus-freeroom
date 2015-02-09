package org.pocketcampus.plugin.food.server;

import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.map.shared.MapItem;

import java.text.Normalizer;
import java.util.List;

public class RestaurantLocatorImpl implements RestaurantLocator {
	@SuppressWarnings("unchecked")
	public MapItem findByName(String restaurantName) {
		// Query map plugin to get restaurant location
		try {
			String compatibleName = compatibleRestaurantNameForMap(restaurantName);
			List<MapItem> searchResults = (List<MapItem>) PocketCampusServer.invokeOnPlugin("map", "search", compatibleName);
			if (searchResults == null || searchResults.size() == 0) {
				System.err.println("INFO: map plugin returned 0 result for restaurant " + restaurantName);
				return null;
			} else {
				return searchResults.get(0); // assuming first result is the right one
			}
		} catch (Exception e) {
			System.err.println("Exception while querying map plugin for location of restaurant " + restaurantName);
			e.printStackTrace();
			return null;
		}
	}

	private static String compatibleRestaurantNameForMap(String restaurantName) {
		String normalizedName = Normalizer.normalize(restaurantName, Normalizer.Form.NFC).toLowerCase();
		String compatibleName = restaurantName;
		if (normalizedName.contains("puur innovation")) {
			compatibleName = "Puur Innovation";
		} else if (normalizedName.contains("table de vallotton")) {
			compatibleName = "Table de Vallotton";
		}
		return compatibleName;
	}
}
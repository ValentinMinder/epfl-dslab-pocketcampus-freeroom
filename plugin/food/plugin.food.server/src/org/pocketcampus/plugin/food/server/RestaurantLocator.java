package org.pocketcampus.plugin.food.server;

import org.pocketcampus.plugin.map.shared.MapItem;

public interface RestaurantLocator {
	MapItem findByName(String restaurantName);
}
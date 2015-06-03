package org.pocketcampus.plugin.food.server;

import org.pocketcampus.plugin.food.shared.MealType;

import java.util.Map;

public interface PictureSource {
	/** Gets the picture URLs of all meal types. */
	Map<MealType, String> getMealTypePictures();

	/** Gets the picture URL of the specified restaurant if it exists, otherwise null. */
	String forRestaurant(String restaurantName);
}
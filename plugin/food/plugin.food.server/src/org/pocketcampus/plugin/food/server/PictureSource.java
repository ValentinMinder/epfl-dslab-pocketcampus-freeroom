package org.pocketcampus.plugin.food.server;

import java.util.Map;

import org.pocketcampus.plugin.food.shared.MealType;

public interface PictureSource {
	/** Gets the picture URLs of all meal types. */
	Map<MealType, String> getMealTypePictures();

	/** Gets the picture URL of the specified restaurant if it exists, otherwise null. */
	String forRestaurant(String restaurantName);
}
package org.pocketcampus.plugin.food.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.pocketcampus.plugin.food.shared.MealType;

public final class PictureSourceImpl implements PictureSource {
	private static final String MEAL_PICS_FOLDER_URL = "http://pocketcampus.epfl.ch/backend/meal-pics/";
	private static final Map<MealType, String> MEAL_TYPE_PICTURE_URLS = new HashMap<MealType, String>();
	
	private static final String RESTAURANTS_PHOTOS_FOLDER_URL = "http://pocketcampus.epfl.ch/backend/restaurant-pics/";
	private static final String RESTAURANTS_PHOTOS_FOLDER_LOCAL_PATH = "/var/www/backend/restaurant-pics/";
	private static final String RESTAURANTS_PHOTOS_FILE_EXTENSION = ".jpg";

	static {
		for (MealType type : MealType.values()) {
			MEAL_TYPE_PICTURE_URLS.put(type, MEAL_PICS_FOLDER_URL + type + ".png");
		}
	}
	
	/** Gets the picture URLs of all meal types. */
	public Map<MealType, String> getMealTypePictures() {
		return MEAL_TYPE_PICTURE_URLS;
	}

	/** Gets the picture URL of the specified restaurant if it exists, otherwise null. */
	public String forRestaurant(String restaurantName) {
		String normalizedName = normalizedNameForFilename(restaurantName);
		String filePath = RESTAURANTS_PHOTOS_FOLDER_LOCAL_PATH + normalizedName + RESTAURANTS_PHOTOS_FILE_EXTENSION;
		File file = new File(filePath);
		if (!file.isFile()) {
			System.err.println("INFO: did not find expected photo file for restaurant " + restaurantName + " at path '" + filePath + "'");
			return null;
		}
		String urlString = RESTAURANTS_PHOTOS_FOLDER_URL + normalizedName + RESTAURANTS_PHOTOS_FILE_EXTENSION;
		return urlString;
	}

	/**
	 * @param restaurantName
	 * @return restaurantName that is accents-freed, lower-cased,
	 *         and apostrophes and spaces replaced by _
	 *         Examples:
	 *         L'Atlantide => l_atlantide
	 *         Cafétéria BC => cafeteria_bc
	 */
	private static String normalizedNameForFilename(String restaurantName) {
		restaurantName = StringUtils.stripAccents(restaurantName);
		restaurantName = restaurantName.toLowerCase();
		restaurantName = restaurantName.replace("'", "_");
		restaurantName = restaurantName.replace(" ", "_");
		return restaurantName;
	}
}
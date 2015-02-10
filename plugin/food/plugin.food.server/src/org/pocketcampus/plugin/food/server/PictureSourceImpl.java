package org.pocketcampus.plugin.food.server;

import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.platform.shared.utils.StringUtils;
import org.pocketcampus.plugin.food.shared.MealType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class PictureSourceImpl implements PictureSource {
	private static final Map<MealType, String> MEAL_TYPE_PICTURE_URLS = new HashMap<>();

	private static final String MEAL_PICS_FOLDER_URI = "backend/meal-pics";
	private static final String RESTAURANTS_PHOTOS_FOLDER_URI = "backend/restaurant-pics";
	private static final String RESTAURANTS_PHOTOS_FILE_EXTENSION = ".jpg";

	static {
		for (MealType type : MealType.values()) {
			String prefix = PocketCampusServer.CONFIG.getString("APACHE_SERVER_BASE_URL") + "/" + MEAL_PICS_FOLDER_URI;
			MEAL_TYPE_PICTURE_URLS.put(type, prefix + "/" + type + ".png");
		}
	}

	/** Gets the picture URLs of all meal types. */
	public Map<MealType, String> getMealTypePictures() {
		return MEAL_TYPE_PICTURE_URLS;
	}

	/** Gets the picture URL of the specified restaurant if it exists, otherwise null. */
	public String forRestaurant(String restaurantName) {
		String normalizedName = normalizedNameForFilename(restaurantName);
		String folderPath = PocketCampusServer.CONFIG.getString("FOOD_RESTAURANTS_PHOTOS_FOLDER_LOCATION");
		File file = new File(folderPath + "/" + normalizedName + RESTAURANTS_PHOTOS_FILE_EXTENSION);
		if (!file.isFile()) {
			System.err.println("Food: did not find expected photo file for restaurant " + restaurantName + " in '" + folderPath + "'");
			return null;
		}
		String prefix = PocketCampusServer.CONFIG.getString("APACHE_SERVER_BASE_URL") + "/" + RESTAURANTS_PHOTOS_FOLDER_URI;
		String filenamePostfix = PocketCampusServer.CONFIG.getString("FOOD_RESTAURANTS_PHOTOS_FILENAME_POSTFIX");
		if (filenamePostfix == null) {
			filenamePostfix = "";
		}
		return prefix + "/" + normalizedName + filenamePostfix + RESTAURANTS_PHOTOS_FILE_EXTENSION;
	}

	/**
	 * @return restaurantName that is accents-freed, lower-cased,
	 *         and apostrophes and spaces replaced by _
	 *         Examples:
	 *         L'Atlantide => l_atlantide
	 *         Cafétéria BC => cafeteria_bc
	 */
	private static String normalizedNameForFilename(String restaurantName) {
		restaurantName = StringUtils.removeAccents(restaurantName);
		restaurantName = restaurantName.toLowerCase();
		restaurantName = restaurantName.replace("'", "_");
		restaurantName = restaurantName.replace(" ", "_");
		return restaurantName;
	}
}
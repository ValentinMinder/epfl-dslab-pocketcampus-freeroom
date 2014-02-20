package org.pocketcampus.plugin.food.server;

import java.io.File;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.platform.sdk.server.HttpClient;
import org.pocketcampus.plugin.food.shared.*;
import org.pocketcampus.plugin.map.shared.MapItem;

import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

/**
 * Parses meals from the official meal list's HTML.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class MenuImpl implements Menu {
	// The URL to the meal list JSON.
	private static final String MEAL_LIST_URL = "http://menus.epfl.ch/cgi-bin/ws-getMenus";

	// The charset of the meal list JSON.
	private static final Charset MEAL_LIST_CHARSET = Charset.forName("UTF-8");

	// The URL parameters and values.
	private static final String URL_TIME_PARAMETER = "midisoir";
	private static final String URL_TIME_VALUE_LUNCH = "midi", URL_TIME_VALUE_DINNER = "soir";
	private static final String URL_DATE_PARAMETER = "date";
	private static final String URL_DATE_VALUE_FORMAT = "dd/MM/yyyy";

	// Constants related to meals
	private static final String HALF_PORTION_PRICE_TARGET = "demi Portion";
	private static final Map<String, PriceTarget> PRICE_TARGETS = new HashMap<String, PriceTarget>();
	private static final Map<String, MealType> MEAL_TYPES = new HashMap<String, MealType>();

	private static final String RESTAURANTS_PHOTOS_FOLDER_URL = "http://pocketcampus.epfl.ch/backend/restaurant-pics/";
	private static final String RESTAURANTS_PHOTOS_FOLDER_LOCAL_PATH = "/var/www/backend/restaurant-pics/";
	private static final String RESTAURANTS_PHOTOS_FILE_EXTENSION = ".jpg";

	// The HTTP client used to get the HTML data.
	private final HttpClient _client;

	static {
		PRICE_TARGETS.put("Etudiant", PriceTarget.STUDENT);
		PRICE_TARGETS.put("Doctorant", PriceTarget.PHD_STUDENT);
		PRICE_TARGETS.put("Campus", PriceTarget.STAFF);
		PRICE_TARGETS.put("Visiteur", PriceTarget.VISITOR);
		PRICE_TARGETS.put("Prix unique", PriceTarget.ALL);
		// When there's a half-price, there's a full price...
		PRICE_TARGETS.put("Portion", PriceTarget.ALL);
		// The "Copernic" restaurant uses this - it stands for "Plat" ("Main course").
		PRICE_TARGETS.put("Plat", PriceTarget.ALL);

		MEAL_TYPES.put("poisson", MealType.FISH);
		MEAL_TYPES.put("viande", MealType.MEAT);
		MEAL_TYPES.put("volaille", MealType.POULTRY);
		MEAL_TYPES.put("végétarien", MealType.VEGETARIAN);
		MEAL_TYPES.put("thaï", MealType.THAI);
		MEAL_TYPES.put("indien", MealType.INDIAN);
		MEAL_TYPES.put("Fourchette verte", MealType.GREEN_FORK);
		MEAL_TYPES.put("pasta", MealType.PASTA);
		MEAL_TYPES.put("pizza", MealType.PIZZA);
		MEAL_TYPES.put("libanais", MealType.LEBANESE);

	}

	public MenuImpl(HttpClient client) {
		_client = client;
	}

	/** Parses the menu from the official meal list's HTML. */
	public FoodResponse get(MealTime time, LocalDate date) throws Exception {

		String timeVal = time == MealTime.LUNCH ? URL_TIME_VALUE_LUNCH : URL_TIME_VALUE_DINNER;
		String dateVal = date.toString(URL_DATE_VALUE_FORMAT);
		String url = String.format("%s?%s=%s&%s=%s", MEAL_LIST_URL, URL_TIME_PARAMETER, timeVal, URL_DATE_PARAMETER, dateVal);

		String json = null;
		try {
			json = _client.getString(url, MEAL_LIST_CHARSET);
		} catch (Exception e) {
			return new FoodResponse().setStatusCode(FoodStatusCode.NETWORK_ERROR);
		}

		List<EpflRestaurant> restaurants = new ArrayList<EpflRestaurant>();
		JsonMenu jmenu = (JsonMenu) new GsonBuilder().create().fromJson(json, JsonMenu.class);

		for (JsonMeal jmeal : jmenu.menus) {
			EpflMeal meal = new EpflMeal();
			meal.setMName(jmeal.platPrincipal);

			String description = "";
			String[] parts = { jmeal.accompLegumes, jmeal.accompFeculents, jmeal.entree, jmeal.salade, jmeal.dessert };
			for (String part : parts) {
				if (!part.equals("")) {
					description += prettyPrint(part);
					description += System.lineSeparator();
				}
			}
			meal.setMDescription(description.trim());

			List<MealType> types = new ArrayList<MealType>();
			for (String type : jmeal.menuTags.split(",")) {
				if (MEAL_TYPES.containsKey(type)) {
					types.add(MEAL_TYPES.get(type));
				}
			}
			meal.setMTypes(types);

			Map<PriceTarget, Double> prices = new HashMap<PriceTarget, Double>();
			for (Map<String, Double> priceContainer : jmeal.prix) {
				// there's only one
				for (Entry<String, Double> price : priceContainer.entrySet()) {
					if (price.getValue() != 0.0) {
						if (PRICE_TARGETS.containsKey(price.getKey())) {
							prices.put(PRICE_TARGETS.get(price.getKey()), price.getValue());
						} else if (HALF_PORTION_PRICE_TARGET.equals(price.getKey())) {
							meal.setMHalfPortionPrice(price.getValue());
						}
					}
				}
			}
			meal.setMPrices(prices);

			meal.setMRating(new EpflRating(0.0, 0));
			meal.setMId(generateMealId(meal.getMName(), meal.getMDescription(), jmeal.restoName));

			fix(meal, jmeal.restoName);

			addMealToList(restaurants, meal, jmeal.restoName);
		}

		return new FoodResponse().setStatusCode(FoodStatusCode.OK).setMenu(restaurants);
	}

	/** Adds the specified meal to the specified list of restaurant using the specified restaurant name. */
	private static void addMealToList(List<EpflRestaurant> restaurants, EpflMeal meal, String restaurantName) {
		EpflRestaurant restaurant = null;

		for (EpflRestaurant r : restaurants) {
			if (r.getRName().equals(restaurantName)) {
				restaurant = r;
				break;
			}
		}

		if (restaurant == null) {
			restaurant = new EpflRestaurant();
			restaurant.setRId(restaurantName.hashCode());
			restaurant.setRName(restaurantName);
			restaurant.setRMeals(new ArrayList<EpflMeal>());
			restaurantSetSpecificAttributes(restaurant);
			restaurants.add(restaurant);
		}

		restaurant.getRMeals().add(meal);
	}

	/**
	 * Based on restaurant name, sets rPictureUrl and queries map plugin to
	 * set rLocation attributes
	 **/
	@SuppressWarnings("unchecked")
	private static void restaurantSetSpecificAttributes(EpflRestaurant restaurant) {
		// Query map plugin to get restaurant location
		try {
			String compatibleName = compatibleRestaurantNameForMap(restaurant.getRName());
			List<MapItem> searchResults = null;
			searchResults = (List<MapItem>) PocketCampusServer.invokeOnPlugin("map", "search", compatibleName);
			if (searchResults == null || searchResults.size() == 0) {
				System.err.println("INFO: map plugin returned 0 result for restaurant " + restaurant.getRName());
			} else {
				MapItem restaurantMapItem = searchResults.get(0); // assuming first result is the right one
				restaurant.setRLocation(restaurantMapItem);
			}
		} catch (Exception e) {
			System.err.println("Exception while querying map plugin for location of restaurant " + restaurant.getRName());
			e.printStackTrace();
		}

		String pictureURLString = restaurantPhotoURLStringIfExists(restaurant.getRName());
		if (pictureURLString != null) {
			// Checking in case Thrift definition does not accept
			// setting NULL for this parameter in the future
			restaurant.setRPictureUrl(pictureURLString);
		}
	}

	/**
	 * @param restaurantName
	 * @return url of restaurant's photo if it exists, null otherwise
	 */
	private static String restaurantPhotoURLStringIfExists(String restaurantName) {
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

	/** If necessary, fixes the specified meal. */
	private static void fix(EpflMeal meal, String restaurantName) {
		// Sometimes meals from "Maharaja", an indian trailer, are marked as thai
		if (restaurantName.equals("Maharaja")) {
			List<MealType> types = meal.getMTypes();
			if (types.contains(MealType.THAI)) {
				types.remove(MealType.THAI);
				types.add(MealType.INDIAN);
				meal.setMTypes(types);
			}
		}
	}

	/** Generates a meal ID (a hashcode) for the specified meal. */
	private static long generateMealId(String name, String description, String restaurantName) {
		final long prime = 31;
		long result = 1;
		result = prime * result + name.hashCode();
		result = prime * result + description.hashCode();
		result = prime * result + restaurantName.hashCode();
		return result;
	}

	/** Pretty-prints the specified string, capitalizing and trimming it. */
	private static String prettyPrint(String s) {
		s = s.trim();
		if (s.length() > 0) {
			return Character.toUpperCase(s.charAt(0)) + (s.length() == 1 ? "" : s.substring(1));
		}
		return "";
	}

	// Classes that correspond to the JSON schema of the meal list, used for easy deserialization

	private static final class JsonMenu {
		public JsonMeal[] menus;
	}

	private static final class JsonMeal {
		public String restoName;
		public String menuTags;
		public String platPrincipal;
		public String accompLegumes;
		public String accompFeculents;
		public String entree;
		public String salade;
		public String dessert;
		// This is awful, but there's no other way to parse [{"key", "value"},{"key", "value"}]
		public Map<String, Double>[] prix;
	}
}
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
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.joda.time.LocalDate;

/**
 * Parses meals from the official meal list's HTML.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class MenuImpl implements Menu {
	// The URL to the meal list. Not really an API, but we have no choice.
	private static final String MEAL_LIST_URL = "http://menus.epfl.ch/cgi-bin/getMenus";
	// The URL parameters and values.
	private static final String URL_TIME_PARAMETER = "midisoir";
	private static final String URL_TIME_VALUE_LUNCH = "midi", URL_TIME_VALUE_DINNER = "soir";
	private static final String URL_DATE_PARAMETER = "date";
	private static final String URL_DATE_VALUE_FORMAT = "dd/MM/yyyy";
	// The meal list's encoding. Should be windows-1252 but we might not run on Windows.
	private static final Charset MEAL_LIST_CHARSET = Charset.forName("ISO-8859-1");
	// The various HTML node tags, IDs and classes in the meal list.
	private static final String LIST_ROOT_ID = "menulist";
	private static final String MEAL_ROOT_TAG = "li";
	private static final String MEAL_TYPE_CONTAINER_CLASS = "logo";
	private static final String MEAL_TYPE_ID_PREFIX = "logo icon";
	private static final String MEAL_NAME_AND_DESCRIPTION_CLASS = "desc";
	private static final String MEAL_NAME_TAG = "strong";
	private static final String MEAL_RESTAURANT_CONTAINER_CLASS = "resto";
	private static final String MEAL_PRICES_CONTAINER_CLASS = "prix";
	private static final String MEAL_PRICE_CONTAINER_TAG = "span";

	// Constants related to string parsing
	private static final String MEAL_LIST_LINE_SEPARATOR = "\n";
	private static final Map<Character, Character> CHARACTERS_FIXES = new HashMap<Character, Character>();

	// Constants related to meal price
	private static final String HALF_PORTION_PRICE_TARGET = "1/2";
	private static final Map<String, PriceTarget> PRICE_TARGETS = new HashMap<String, PriceTarget>();

	// Constants related to meal types.
	private static final Map<Integer, MealType> MEAL_PRIMARY_TYPES = new HashMap<Integer, MealType>();
	private static final Map<Integer, MealType> MEAL_SECONDARY_TYPES = new HashMap<Integer, MealType>();

	private static final String RESTAURANTS_PHOTOS_FOLDER_URL = "http://pocketcampus.epfl.ch/backend/restaurant-pics/";
	private static final String RESTAURANTS_PHOTOS_FOLDER_LOCAL_PATH = "/var/www/backend/restaurant-pics/";
	private static final String RESTAURANTS_PHOTOS_FILE_EXTENSION = ".jpg";

	// The HTTP client used to get the HTML data.
	private final HttpClient _client;

	static {
		CHARACTERS_FIXES.put('', 'œ'); // the first char is non printable

		PRICE_TARGETS.put("E", PriceTarget.STUDENT);
		PRICE_TARGETS.put("D", PriceTarget.PHD_STUDENT);
		PRICE_TARGETS.put("C", PriceTarget.STAFF);
		PRICE_TARGETS.put("V", PriceTarget.VISITOR);
		PRICE_TARGETS.put("", PriceTarget.ALL);
		// The "Copernic" restaurant uses this - it stands for "Plat" ("Main course").
		PRICE_TARGETS.put("P", PriceTarget.ALL);

		MEAL_PRIMARY_TYPES.put(1, MealType.THAI);
		MEAL_PRIMARY_TYPES.put(2, MealType.INDIAN);
		MEAL_PRIMARY_TYPES.put(3, MealType.GREEN_FORK);
		MEAL_PRIMARY_TYPES.put(4, MealType.PASTA);
		MEAL_PRIMARY_TYPES.put(5, MealType.PIZZA);
		MEAL_PRIMARY_TYPES.put(6, MealType.LEBANESE);

		MEAL_SECONDARY_TYPES.put(1, MealType.FISH);
		MEAL_SECONDARY_TYPES.put(2, MealType.MEAT);
		MEAL_SECONDARY_TYPES.put(3, MealType.POULTRY);
		MEAL_SECONDARY_TYPES.put(4, MealType.VEGETARIAN);
	}

	public MenuImpl(HttpClient client) {
		_client = client;
	}

	/** Parses the menu from the official meal list's HTML. */
	public FoodResponse get(MealTime time, LocalDate date) throws Exception {
		List<EpflRestaurant> menu = new ArrayList<EpflRestaurant>();

		String timeVal = time == MealTime.LUNCH ? URL_TIME_VALUE_LUNCH : URL_TIME_VALUE_DINNER;
		String dateVal = date.toString(URL_DATE_VALUE_FORMAT);
		String url = String.format("%s?%s=%s&%s=%s", MEAL_LIST_URL, URL_TIME_PARAMETER, timeVal, URL_DATE_PARAMETER, dateVal);

		String html = null;
		try {
			html = _client.getString(url, MEAL_LIST_CHARSET);
		} catch (Exception e) {
			return new FoodResponse().setStatusCode(FoodStatusCode.NETWORK_ERROR);
		}

		Document doc = Jsoup.parse(html);

		for (Element elem : doc.getElementById(LIST_ROOT_ID).getElementsByTag(MEAL_ROOT_TAG)) {
			EpflMeal meal = new EpflMeal();

			// <div class="logo iconINDEX">
			Element typeElem = elem.getElementsByClass(MEAL_TYPE_CONTAINER_CLASS).get(0);
			int typeId = Integer.parseInt(typeElem.className().substring(MEAL_TYPE_ID_PREFIX.length()));
			int primType = typeId / 5;
			int secType = typeId % 5;
			List<MealType> types = new ArrayList<MealType>();
			if (MEAL_PRIMARY_TYPES.containsKey(primType)) {
				types.add(MEAL_PRIMARY_TYPES.get(primType));
			}
			if (MEAL_SECONDARY_TYPES.containsKey(secType)) {
				types.add(MEAL_SECONDARY_TYPES.get(secType));
			}
			if (types.size() == 0) {
				types.add(MealType.UNKNOWN);
			}
			meal.setMTypes(types);

			// <div class="desc"><strong>NAME</strong>DESC</div>
			Element nameAndDescElem = elem.getElementsByClass(MEAL_NAME_AND_DESCRIPTION_CLASS).get(0);
			Element nameElem = nameAndDescElem.getElementsByTag(MEAL_NAME_TAG).get(0);
			meal.setMName(nameElem.text().trim());
			nameElem.remove();
			meal.setMDescription(prettyPrint(getFullText(nameAndDescElem)));

			// <div class="resto"><a ...>RESTAURANT</a></div>
			Element restaurantElem = elem.getElementsByClass(MEAL_RESTAURANT_CONTAINER_CLASS).get(0);
			String restaurantName = restaurantElem.text().trim();

			// <div class="prix">
			// <span ...>TARGET</span>PRICE<br> (1 or more times)
			// </div>
			Map<PriceTarget, Double> prices = new HashMap<PriceTarget, Double>();
			for (Element priceElem : elem.getElementsByClass(MEAL_PRICES_CONTAINER_CLASS).get(0).getElementsByTag(MEAL_PRICE_CONTAINER_TAG)) {
				String target = priceElem.text().trim();
				double price = Double.parseDouble(((TextNode) priceElem.nextSibling()).text().trim());

				if (price > 0.0) {
					if (target.equals(HALF_PORTION_PRICE_TARGET)) {
						meal.setMHalfPortionPrice(price);
					} else if (PRICE_TARGETS.containsKey(target)) {
						prices.put(PRICE_TARGETS.get(target), price);
					}
				}
			}

			meal.setMPrices(prices);
			fix(meal, restaurantName);
			meal.setMRating(new EpflRating(0.0, 0));
			meal.setMId(generateMealId(meal.getMName(), meal.getMDescription(), restaurantName));
			addMealToList(menu, meal, restaurantName);
		}

		return new FoodResponse().setStatusCode(FoodStatusCode.OK).setMenu(menu);
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

	/** Gets the full text, newlines included, contained in an HTML element. */
	private static String getFullText(Element element) {
		List<TextNode> textNodes = element.textNodes();
		List<String> lines = new ArrayList<String>(textNodes.size());
		for (TextNode n : textNodes) {
			lines.add(n.text().trim());
		}
		return StringUtils.join(lines, MEAL_LIST_LINE_SEPARATOR);
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

	/** Pretty-prints the specified string, capitalizing and trimming each line. */
	private static String prettyPrint(String s) {
		for (Entry<Character, Character> entry : CHARACTERS_FIXES.entrySet()) {
			s = s.replace(entry.getKey(), entry.getValue());
		}

		List<String> lines = new ArrayList<String>();
		for (String line : s.split(MEAL_LIST_LINE_SEPARATOR)) {
			line = line.trim();
			if (line.length() > 0) {
				lines.add(Character.toUpperCase(line.charAt(0)) + (line.length() == 1 ? "" : line.substring(1)));
			}
		}

		return StringUtils.join(lines, MEAL_LIST_LINE_SEPARATOR);
	}
}
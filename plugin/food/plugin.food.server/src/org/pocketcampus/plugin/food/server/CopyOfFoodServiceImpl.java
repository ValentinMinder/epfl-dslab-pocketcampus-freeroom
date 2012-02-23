//package org.pocketcampus.plugin.food.server;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.Vector;
//
//import org.apache.thrift.TException;
//import org.pocketcampus.platform.sdk.shared.utils.Utils;
//import org.pocketcampus.plugin.food.server.db.FoodDB;
//import org.pocketcampus.plugin.food.server.parse.FeedUrlParser;
//import org.pocketcampus.plugin.food.server.parse.RssParser;
//import org.pocketcampus.plugin.food.server.parse.RssParser.RssFeed;
//import org.pocketcampus.plugin.food.shared.FoodService;
//import org.pocketcampus.plugin.food.shared.Meal;
//import org.pocketcampus.plugin.food.shared.Rating;
//import org.pocketcampus.plugin.food.shared.Restaurant;
//import org.pocketcampus.plugin.food.shared.Sandwich;
//import org.pocketcampus.plugin.food.shared.SubmitStatus;
//
///**
// * Takes care of handling the requests for information concerning the Food
// * plugin.
// * 
// * @author Elodie <elodienilane.triponez@epfl.ch>
// * @author Oriane <oriane.rodriguez@epfl.ch>
// */
//public class CopyOfFoodServiceImpl implements FoodService.Iface {
//	/** The last time the Meals were parsed from the web page. */
//	private Date mLastImportedMeals;
//
//	/** Interface to the database. */
//	private FoodDB mDatabase;
//
//	/** The list of Restaurants and the Url to their feeds. */
//	private HashMap<String, String> mRestaurantsFeeds;
//
//	/** The list of all Meals. */
//	private List<Meal> mAllMeals;
//
//	/** Ratings for all Meals, represents with their hashcode. */
//	private HashMap<Long, Rating> mMealRatings;
//
//	/** The list of DeviceIds that have already voted for a meal today. */
//	private ArrayList<String> mDeviceIds;
//
//	/** The list of Sandwiches for all Cafeterias. */
//	private List<Sandwich> mSandwiches;
//
//	// Character to filter because doesn't show right.
//	private final static int BAD_CHAR = 65533;
//
//	/** The interval in minutes at which the menu should be fetched. */
//	private int REFRESH_INTERVAL = 60;
//
//	/**
//	 * Constructor instantiates all containers for meals, ratings, sandwiches,
//	 * and the Database. Also import menus and sandwiches since it's the first
//	 * execution of the server.
//	 */
//	public CopyOfFoodServiceImpl() {
//		System.out.println("Starting Food plugin server ...");
//
//		mDatabase = new FoodDB();
//
//		mAllMeals = new ArrayList<Meal>();
//		mSandwiches = new ArrayList<Sandwich>();
//		mMealRatings = new HashMap<Long, Rating>();
//		mDeviceIds = new ArrayList<String>();
//
//		getRestaurantsList();
//		importMenus();
//		importSandwiches();
//	}
//
//	/**
//	 * Get all menus for today.
//	 * 
//	 * @return mAllMeals The list of meals.
//	 */
//	@Override
//	public List<Meal> getMeals() throws TException {
//
//		if (!isToday(mLastImportedMeals)) {
//			System.out
//					.println("<getMeals>: Date not valid. Reimporting Meals.");
//
//			mAllMeals.clear();
//			mDeviceIds.clear();
//			mMealRatings.clear();
//
//			importMenus();
//		} else if (!isUpToDate(mLastImportedMeals)) {
//			System.out
//					.println("<getMeals>: Time not valid. Reimporting Meals.");
//
//			refreshMenus();
//		} else {
//			System.out.println("<getMeals>: " + mLastImportedMeals
//					+ ", not reimporting Meals.");
//		}
//
//		return mAllMeals;
//	}
//
//	/**
//	 * Get all restaurants on campus.
//	 * 
//	 * @return mRestaurantList The list of restaurants.
//	 */
//	@Override
//	public List<Restaurant> getRestaurants() throws TException {
//		System.out.println("<getRestaurants>: getting restaurants");
//		ArrayList<Restaurant> mRestaurantList = new ArrayList<Restaurant>();
//
//		for (String r : mRestaurantsFeeds.keySet()) {
//			Restaurant newResto = new Restaurant(r.hashCode(), r);
//			mRestaurantList.add(newResto);
//		}
//		return mRestaurantList;
//	}
//
//	/**
//	 * Checks whether the user has already voted today
//	 */
//	public boolean hasVoted(String deviceId) throws TException {
//		if (mDeviceIds.contains(deviceId)
//				|| mDatabase.checkVotedDevice(deviceId)) {
//			System.out.println("<setRating>: Already in mDeviceIds.");
//			return true;
//		}
//		System.out.println("<setRating>: Not yet in mDeviceIds.");
//		return false;
//	}
//
//	/**
//	 * Get the Rating of a particular Meal.
//	 * 
//	 * @param meal
//	 *            The Meal for which we want the Rating.
//	 * @return rating The Meal's Rating.
//	 */
//	@Override
//	public Rating getRating(Meal meal) throws TException {
//		updateMenus();
//		System.out.println("<getRating>: Rating Request");
//
//		if (mMealRatings != null) {
//			return mMealRatings.get(meal.getMealId());
//		}
//
//		return null;
//	}
//
//	/**
//	 * Get all the Ratings for today's meals. Not working correctly, I think
//	 * when the menus are up to date (meaning it's still today) it doesn't get
//	 * the Ratings again 'cause of the updateMenus method...
//	 * 
//	 * @return mCampusMealRatings the map of all ratings associated with the
//	 *         corresponding meal.
//	 */
//	@Override
//	public Map<Long, Rating> getRatings() throws TException {
//		// Here is why we don't get the Ratings right?
//		updateMenus();
//		System.out.println("<getRatings>: Ratings Request.");
//
//		if (mMealRatings != null) {
//			return mMealRatings;
//		}
//
//		return null;
//	}
//
//	/**
//	 * Sets the Rating for a particular Meal Missing deviceID and mealHashcode
//	 * because we don't get parameters from the Request... TODO : find another
//	 * way to pass the info (lazy for now...)
//	 * 
//	 * @param rating
//	 *            The rating submitted by the user
//	 * @return submitStatus the Status of Submission (Valid, Error,
//	 *         Already_Voted or Too_Early)
//	 */
//	@Override
//	public SubmitStatus setRating(long mealId, double rating, String deviceId)
//			throws TException {
//		updateMenus();
//		System.out.println("<setRating>: Rating Request");
//
//		if (deviceId == null) {
//			return SubmitStatus.ERROR;
//		}
//
//		Calendar now = Calendar.getInstance();
//		now.setTime(new Date());
//
//		if (now.get(Calendar.HOUR_OF_DAY) < 11) {
//			return SubmitStatus.TOO_EARLY;
//		}
//
//		if (mDeviceIds.contains(deviceId)) {
//			System.out.println("<setRating>: Already in mDeviceIds.");
//			return SubmitStatus.ALREADY_VOTED;
//		}
//
//		boolean voted = mDatabase.checkVotedDevice(deviceId);
//
//		if (voted) {
//			System.out.println("<setRating>: Already in DB Database.");
//			return SubmitStatus.ALREADY_VOTED;
//		}
//
//		double ratingTotal;
//		double ratingValue;
//		int newNbVotes;
//
//		for (Meal currentMeal : mAllMeals) {
//			if (currentMeal.getMealId() == mealId) {
//
//				ratingTotal = currentMeal.getRating().getSumOfRatings()
//						+ rating;
//				newNbVotes = currentMeal.getRating().getNumberOfVotes() + 1;
//				if (newNbVotes != 0) {
//					ratingValue = ratingTotal / newNbVotes;
//				} else {
//					ratingValue = 0;
//				}
//
//				// Update Rating for this meal
//				currentMeal.getRating().setNumberOfVotes(newNbVotes);
//				currentMeal.getRating().setSumOfRatings(ratingTotal);
//				currentMeal.getRating().setRatingValue(ratingValue);
//
//				// Update Rating + deviceID on DB
//				mDatabase.insertRating(currentMeal);
//				mDatabase.insertVotedDevice(deviceId, mealId, rating);
//
//				// Add deviceID in the list
//				mDeviceIds.add(deviceId);
//
//				// Update Rating in the MealList
//				mMealRatings.put(mealId, currentMeal.getRating());
//				return SubmitStatus.VALID;
//			}
//		}
//
//		return SubmitStatus.ERROR;
//	}
//
//	/**
//	 * Gets all Sandwiches on campus.
//	 * 
//	 * @return mSandwiches the list of sandwiches
//	 */
//	@Override
//	public List<Sandwich> getSandwiches() throws TException {
//		if (mSandwiches == null || mSandwiches.isEmpty()) {
//			importSandwiches();
//			System.out.println("<getSandwiches>: Reimporting sandwiches.");
//		} else {
//			System.out.println("<getSandwiches>: Not reimporting sandwiches");
//		}
//		return mSandwiches;
//	}
//
//	/**
//	 * Initiates parsing of the restaurant list from the file stored on the
//	 * server
//	 */
//	private void getRestaurantsList() {
//		FeedUrlParser rlp = new FeedUrlParser(
//				"restaurants_list.txt");
//		mRestaurantsFeeds = rlp.getFeeds();
//	}
//
//	/**
//	 * Imports Menus from the RSS feed
//	 */
//	private void importMenus() {
//		List<Meal> mealsFromDB = mDatabase.getMeals();
//
//		if (mealsFromDB != null && !mealsFromDB.isEmpty()) {
//			mAllMeals = mealsFromDB;
//
//			for (Meal m : mAllMeals) {
//				mMealRatings.put(m.getMealId(), m.getRating());
//			}
//			mLastImportedMeals = new Date();
//			System.out.println("<importMenus>: Getting menus from DB");
//
//			List<Meal> newlyParsedMeals = parseMenus();
//			mDatabase.insertMeals(newlyParsedMeals);
//		} else {
//			parseMenus();
//			mDatabase.insertMeals(mAllMeals);
//		}
//	}
//
//	/**
//	 * Refresh menus because they have been imported too long ago
//	 */
//	private void refreshMenus() {
//		List<Meal> newlyParsedMeals = parseMenus();
//		if (newlyParsedMeals != null && !newlyParsedMeals.isEmpty()) {
//			mDatabase.insertMeals(newlyParsedMeals);
//		}
//	}
//
//	/**
//	 * Parse the menus from the RSS feeds
//	 * 
//	 * @return the list of meals that were just parsed and that were not in the
//	 *         list of meals previously on the server
//	 */
//	private List<Meal> parseMenus() {
//		Set<String> restaurants = mRestaurantsFeeds.keySet();
//		List<Meal> newlyParsedMeals = new ArrayList<Meal>();
//		List<String> notCapitalized = mDatabase.getNotCapitalized();
//
//		for (String r : restaurants) {
//			RssParser rp = new RssParser(mRestaurantsFeeds.get(r),
//					notCapitalized);
//			rp.parse();
//			RssFeed feed = rp.getFeed();
//
//			Restaurant newResto = new Restaurant(r.hashCode(), r);
//
//			if (feed != null && feed.items != null) {
//				for (int i = 0; i < feed.items.size(); i++) {
//					// New meal rating
//					Rating mealRating = new Rating(0, 0, 0);
//					// Meal name
//					String name = feed.items.get(i).title;
//					// Meal description
//					String description = feed.items.get(i).description;
//					// Meal id
//					long id = generateMealId(name, description, newResto);
//
//					Meal newMeal = new Meal(id, name, description, newResto,
//							mealRating);
//					if (!Utils.containsSpecialAscii(newMeal.mealDescription,
//							BAD_CHAR)
//							&& !Utils.containsSpecialAscii(newMeal.name,
//									BAD_CHAR)) {
//						if (!alreadyExist(newMeal)) {
//							mAllMeals.add(newMeal);
//							mMealRatings.put(newMeal.getMealId(), mealRating);
//							// Buffer list to then add to the database the new
//							// meals
//							newlyParsedMeals.add(newMeal);
//						}
//					}
//				}
//				mLastImportedMeals = new Date();
//			} else {
//				System.out.println("<importMenus>: Empty Feed for " + r);
//			}
//		}
//		if (mAllMeals.isEmpty()) {
//			mLastImportedMeals = new Date();
//		}
//		return newlyParsedMeals;
//	}
//
//	/**
//	 * Generate the Meal Id for a given Meal.
//	 * 
//	 * @param name
//	 *            The name of the Meal.
//	 * @param description
//	 *            The description of the Meal.
//	 * @param restaurant
//	 *            The Restaurant the Meal is available at.
//	 * @return The generated unique Id in long type.
//	 */
//	public static long generateMealId(String name, String description,
//			Restaurant restaurant) {
//		final long prime = 31;
//		long result = 1;
//		result = prime * result + ((name == null) ? 0 : name.hashCode());
//		result = prime * result
//				+ ((description == null) ? 0 : description.hashCode());
//		result = prime * result
//				+ ((restaurant == null) ? 0 : restaurant.getName().hashCode());
//		return result;
//	}
//
//	/**
//	 * Imports the Meals again if they are not up to date.
//	 */
//	private void updateMenus() {
//		if (!isToday(mLastImportedMeals)) {
//			mAllMeals.clear();
//			mMealRatings.clear();
//			mDeviceIds.clear();
//			importMenus();
//		}
//	}
//
//	/**
//	 * Check if a particular Meal is already into the menus list
//	 * 
//	 * @param meal
//	 *            The Meal we want to check
//	 * @return true if it's already in the list of Meals
//	 */
//	private boolean alreadyExist(Meal meal) {
//		long mealId = meal.getMealId();
//		for (Meal m : mAllMeals) {
//			if (m.getMealId() == mealId) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	/**
//	 * Import all Sandwiches on campus from a hardcoded list
//	 */
//	private void importSandwiches() {
//		/* Cafeteria INM */
//		Restaurant cafeteriaINM = new Restaurant(("Cafeteria INM").hashCode(),
//				"Cafeteria INM");
//		mSandwiches.add(new Sandwich(
//				(cafeteriaINM.getName() + "Poulet au Curry").hashCode(),
//				cafeteriaINM, "Poulet au Curry"));
//		mSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Thon")
//				.hashCode(), cafeteriaINM, "Thon"));
//		mSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Jambon")
//				.hashCode(), cafeteriaINM, "Jambon"));
//		mSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Fromage")
//				.hashCode(), cafeteriaINM, "Fromage"));
//		mSandwiches.add(new Sandwich(
//				(cafeteriaINM.getName() + "Tomate Mozzarella").hashCode(),
//				cafeteriaINM, "Tomate Mozzarella"));
//		mSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Jambon Cru")
//				.hashCode(), cafeteriaINM, "Jambon Cru"));
//		mSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Salami")
//				.hashCode(), cafeteriaINM, "Salami"));
//
//		/* Cafeteria BM */
//		mSandwiches.addAll(defaultSandwichList("Cafeteria BM"));
//
//		/* Cafeteria BC */
//		mSandwiches.addAll(defaultSandwichList("Cafeteria BC"));
//
//		/* Cafeteria SV */
//		mSandwiches.addAll(defaultSandwichList("Cafeteria SV"));
//
//		/* Cafeteria MX */
//		mSandwiches.addAll(defaultSandwichList("Cafeteria MX"));
//
//		/* Cafeteria PH */
//		mSandwiches.addAll(defaultSandwichList("Cafeteria PH"));
//
//		/* Cafeteria ELA */
//		mSandwiches.addAll(defaultSandwichList("Cafeteria ELA"));
//
//		/* Le Giacometti (Cafeteria SG) */
//		Restaurant leGiacometti = new Restaurant(("Le Giacometti").hashCode(),
//				"Le Giacometti");
//		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Jambon")
//				.hashCode(), leGiacometti, "Jambon"));
//		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Salami")
//				.hashCode(), leGiacometti, "Salami"));
//		mSandwiches.add(new Sandwich(
//				(leGiacometti.getName() + "Jambon de dinde").hashCode(),
//				leGiacometti, "Jambon de dinde"));
//		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Gruyère")
//				.hashCode(), leGiacometti, "Gruyière"));
//		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Viande Séchée")
//				.hashCode(), leGiacometti, "Viande Séchée"));
//		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Jambon Cru")
//				.hashCode(), leGiacometti, "Jambon Cru"));
//		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Roast-Beef")
//				.hashCode(), leGiacometti, "Roast-Beef"));
//		mSandwiches.add(new Sandwich(
//				(leGiacometti.getName() + "Poulet Jijommaise").hashCode(),
//				leGiacometti, "Poulet Jijommaise"));
//		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Crevettes")
//				.hashCode(), leGiacometti, "Crevettes"));
//		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Saumon Fumé")
//				.hashCode(), leGiacometti, "Saumon Fumé"));
//		mSandwiches.add(new Sandwich(
//				(leGiacometti.getName() + "Poulet au Curry").hashCode(),
//				leGiacometti, "Poulet au Curry"));
//
//		/* L'Esplanade */
//		Restaurant lEsplanade = new Restaurant(("L'Esplanade").hashCode(),
//				"L'Esplanade");
//		mSandwiches
//				.add(new Sandwich((lEsplanade.getName() + "Thon").hashCode(),
//						lEsplanade, "Thon"));
//		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Poulet au Curry")
//				.hashCode(), lEsplanade, "Poulet au Curry"));
//		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Aubergine")
//				.hashCode(), lEsplanade, "Aubergine"));
//		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Roast-Beef")
//				.hashCode(), lEsplanade, "Roast-Beef"));
//		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Jambon Cru")
//				.hashCode(), lEsplanade, "Jambon Cru"));
//		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Viande Séchée")
//				.hashCode(), lEsplanade, "Viande Séchée"));
//		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Saumon Fumé")
//				.hashCode(), lEsplanade, "Saumon Fumé"));
//
//		/* L'Arcadie */
//		mSandwiches.addAll(defaultSandwichList("L'Arcadie"));
//
//		/* Atlantide */
//		Restaurant lAtlantide = new Restaurant(("L'Atlantide").hashCode(),
//				"L'Atlantide");
//		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Sandwich long")
//				.hashCode(), lAtlantide, "Sandwich long"));
//		mSandwiches.add(new Sandwich(
//				(lAtlantide.getName() + "Sandwich au pavot").hashCode(),
//				lAtlantide, "Sandwich au pavot"));
//		mSandwiches.add(new Sandwich(
//				(lAtlantide.getName() + "Sandwich intégral").hashCode(),
//				lAtlantide, "Sandwich intégral"));
//		mSandwiches.add(new Sandwich(
//				(lAtlantide.getName() + "Sandwich provençal").hashCode(),
//				lAtlantide, "Sandwich provençal"));
//		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Parisette")
//				.hashCode(), lAtlantide, "Parisette"));
//		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Jambon")
//				.hashCode(), lAtlantide, "Jambon"));
//		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Salami")
//				.hashCode(), lAtlantide, "Salami"));
//		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Dinde")
//				.hashCode(), lAtlantide, "Dinde"));
//		mSandwiches
//				.add(new Sandwich((lAtlantide.getName() + "Thon").hashCode(),
//						lAtlantide, "Thon"));
//		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Mozzarella")
//				.hashCode(), lAtlantide, "Mozzarella"));
//		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Saumon Fumé")
//				.hashCode(), lAtlantide, "Saumon Fumé"));
//		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Viande Séchée")
//				.hashCode(), lAtlantide, "Viande Séchée"));
//		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Jambon Cru")
//				.hashCode(), lAtlantide, "Jambon Cru"));
//		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Roast-Beef")
//				.hashCode(), lAtlantide, "Roast-Beef"));
//
//		/* Satellite */
//		Restaurant satellite = new Restaurant(("Satellite").hashCode(),
//				"Satellite");
//		mSandwiches.add(new Sandwich((satellite.getName() + "Jambon")
//				.hashCode(), satellite, "Jambon"));
//		mSandwiches.add(new Sandwich((satellite.getName() + "Thon").hashCode(),
//				satellite, "Thon"));
//		mSandwiches.add(new Sandwich((satellite.getName() + "Jambon Fromage")
//				.hashCode(), satellite, "Jambon Fromage"));
//		mSandwiches.add(new Sandwich((satellite.getName() + "Roast-Beef")
//				.hashCode(), satellite, "Roast-Beef"));
//		mSandwiches.add(new Sandwich((satellite.getName() + "Poulet au Curry")
//				.hashCode(), satellite, "Poulet au Curry"));
//		mSandwiches.add(new Sandwich((satellite.getName() + "Jambon Cru")
//				.hashCode(), satellite, "Jambon Cru"));
//		mSandwiches.add(new Sandwich(
//				(satellite.getName() + "Tomate Mozzarella").hashCode(),
//				satellite, "Tomate Mozzarella"));
//		mSandwiches.add(new Sandwich((satellite.getName() + "Salami")
//				.hashCode(), satellite, "Salami"));
//		mSandwiches.add(new Sandwich((satellite.getName() + "Parmesan")
//				.hashCode(), satellite, "Parmesan"));
//		mSandwiches.add(new Sandwich(
//				(satellite.getName() + "Aubergine grillée").hashCode(),
//				satellite, "Aubergine grillée"));
//		mSandwiches.add(new Sandwich((satellite.getName() + "Viande séchée")
//				.hashCode(), satellite, "Viande séchée"));
//
//		/* Négoce */
//		Restaurant negoce = new Restaurant(("Négoce").hashCode(), "Négoce");
//		mSandwiches.add(new Sandwich((negoce.getName() + "Dinde").hashCode(),
//				negoce, "Dinde"));
//		mSandwiches.add(new Sandwich((negoce.getName() + "Thon").hashCode(),
//				negoce, "Thon"));
//		mSandwiches.add(new Sandwich((negoce.getName() + "Gratiné Jambon")
//				.hashCode(), negoce, "Gratiné Jambon"));
//		mSandwiches.add(new Sandwich((negoce.getName() + "Mozzarella Olives")
//				.hashCode(), negoce, "Mozzarella Olives"));
//		mSandwiches.add(new Sandwich((negoce.getName() + "Poulet au Curry")
//				.hashCode(), negoce, "Poulet au Curry"));
//		mSandwiches.add(new Sandwich((negoce.getName() + "Jambon Fromage")
//				.hashCode(), negoce, "Jambon Fromage"));
//		mSandwiches.add(new Sandwich((negoce.getName() + "Jambon").hashCode(),
//				negoce, "Jambon"));
//		mSandwiches.add(new Sandwich((negoce.getName() + "Salami").hashCode(),
//				negoce, "Salami"));
//		mSandwiches.add(new Sandwich((negoce.getName() + "Roast-Beef")
//				.hashCode(), negoce, "Roast-Beef"));
//		mSandwiches.add(new Sandwich((negoce.getName() + "Mozarrella")
//				.hashCode(), negoce, "Mozzarella"));
//	}
//
//	/**
//	 * The default sandwich list
//	 * 
//	 * @param name
//	 *            The name of the list
//	 * @return the sandwich list
//	 */
//	private Vector<Sandwich> defaultSandwichList(String name) {
//
//		Vector<Sandwich> defaultSandwichList = new Vector<Sandwich>();
//		Restaurant r = new Restaurant(name.hashCode(), name);
//
//		defaultSandwichList.add(new Sandwich((name + "Thon").hashCode(), r,
//				"Thon"));
//		defaultSandwichList.add(new Sandwich((name + "Jambon").hashCode(), r,
//				"Jambon"));
//		defaultSandwichList.add(new Sandwich((name + "Fromage").hashCode(), r,
//				"Fromage"));
//		defaultSandwichList.add(new Sandwich((name + "Tomate Mozzarella")
//				.hashCode(), r, "Tomate Mozzarella"));
//		defaultSandwichList.add(new Sandwich((name + "Jambon Cru").hashCode(),
//				r, "Jambon Cru"));
//		defaultSandwichList.add(new Sandwich((name + "Salami").hashCode(), r,
//				"Salami"));
//
//		return defaultSandwichList;
//	}
//
//	/**
//	 * Checks whether the last date the user had is today
//	 * 
//	 * @param oldDate
//	 * @return true if the date is today
//	 */
//	private boolean isToday(Date oldDate) {
//		if (oldDate == null) {
//			return false;
//		}
//		Calendar now = Calendar.getInstance();
//		now.setTime(new Date());
//		Calendar then = Calendar.getInstance();
//		then.setTime(oldDate);
//
//		return (now.get(Calendar.DAY_OF_WEEK) == then.get(Calendar.DAY_OF_WEEK)) ? true
//				: false;
//	}
//
//	/**
//	 * Checks whether the date is up to date (according to today and this
//	 * particular hour)
//	 * 
//	 * @param oldDate
//	 * @return true if it is
//	 */
//	private boolean isUpToDate(Date oldDate) {
//		if (oldDate == null) {
//			return false;
//		}
//		Calendar now = Calendar.getInstance();
//		now.setTime(new Date());
//		Calendar then = Calendar.getInstance();
//		then.setTime(oldDate);
//
//		if (now.get(Calendar.DAY_OF_WEEK) != then.get(Calendar.DAY_OF_WEEK)) {
//			return false;
//		} else {
//			if (getMinutes(then.getTime(), now.getTime()) > REFRESH_INTERVAL) {
//				return false;
//			}
//		}
//		return true;
//	}
//
//	/**
//	 * To get the minutes separating two different dates
//	 * 
//	 * @param then
//	 * @param now
//	 * @return the minutes that separate both dates
//	 */
//	private long getMinutes(Date then, Date now) {
//		long diff = now.getTime() - then.getTime();
//		long realDiff = diff / (60000);
//		return realDiff;
//	}
//}

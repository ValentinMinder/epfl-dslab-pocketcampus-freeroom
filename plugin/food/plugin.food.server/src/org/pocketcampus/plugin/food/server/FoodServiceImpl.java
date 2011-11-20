package org.pocketcampus.plugin.food.server;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.shared.utils.Utils;
import org.pocketcampus.plugin.food.server.db.FoodDB;
import org.pocketcampus.plugin.food.server.parse.RestaurantListParser;
import org.pocketcampus.plugin.food.server.parse.RssParser;
import org.pocketcampus.plugin.food.server.parse.RssParser.RssFeed;
import org.pocketcampus.plugin.food.shared.FoodService;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;
import org.pocketcampus.plugin.food.shared.Restaurant;
import org.pocketcampus.plugin.food.shared.Sandwich;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

/**
 * Takes care of handling the requests for information concerning the Food
 * plugin
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class FoodServiceImpl implements FoodService.Iface {
	/** The last time the Meals were parsed from the web page */
	private Date mLastImportedMeals;

	/** The last time the Sandwiches were imported */
	private Date mLastImportedSandwiches;

	/** Interface to the database */
	private FoodDB mDatabase;

	/** The list of Restaurants and the Url to their feeds */
	private HashMap<String, String> mRestaurantsFeeds;

	/** The list of all Meals */
	private List<Meal> mAllMeals;

	/** Ratings for all Meals, represents with their hashcode */
	private HashMap<Integer, Rating> mMealRatings;

	/** The list of DeviceIds that have already voted for a meal today */
	private ArrayList<String> mDeviceIds;

	/** The list of Sandwiches for all Cafeterias */
	private List<Sandwich> mSandwiches;

	// Character to filter because doesn't show right.
	private final static int BAD_CHAR = 65533;

	/** The interval in minutes at which the news should be fetched */
	private int REFRESH_INTERVAL = 60;

	/**
	 * Constructor instantiates all containers for meals, ratings, sandwiches,
	 * and the Database. Also import menus and sandwiches since it's the first
	 * execution of the server.
	 */
	public FoodServiceImpl() {
		System.out.println("Starting Food plugin server ...");

		mDatabase = new FoodDB();

		mAllMeals = new ArrayList<Meal>();
		mSandwiches = new ArrayList<Sandwich>();
		mMealRatings = new HashMap<Integer, Rating>();
		mDeviceIds = new ArrayList<String>();

		getRestaurantsList();

		importMenus();
		importSandwiches();

		mLastImportedSandwiches = new Date();
	}

	/**
	 * Get all menus for today
	 * 
	 * @return mAllMeals The list of meals
	 */
	@Override
	public List<Meal> getMeals() throws TException {

		if (!isToday(mLastImportedMeals)) {
			System.out
					.println("<getMeals>: Date not valid. Reimporting Meals.");

			mAllMeals.clear();
			mDeviceIds.clear();
			mMealRatings.clear();

			importMenus();
		} else if (!isUpToDate(mLastImportedMeals)) {
			System.out
					.println("<getMeals>: Time not valid. Reimporting Meals.");

			refreshMenus();
		} else {
			System.out.println("<getMeals>: " + mLastImportedMeals
					+ ", not reimporting Meals.");
		}

		return mAllMeals;
	}

	/**
	 * Get all restaurants on campus
	 * 
	 * @return mRestaurantList The list of restaurants
	 */
	@Override
	public List<Restaurant> getRestaurants() throws TException {
		System.out.println("<getRestaurants>: getting restaurants");
		ArrayList<Restaurant> mRestaurantList = new ArrayList<Restaurant>();

		if (mAllMeals != null) {

			for (Meal m : mAllMeals) {
				Restaurant r = m.getRestaurant();
				if (!mRestaurantList.contains(r)) {
					mRestaurantList.add(r);
				}
			}
		}

		return mRestaurantList;
	}

	/**
	 * Checks whether the user has already voted today
	 */
	public boolean hasVoted(String deviceId) throws TException {
		if (mDeviceIds.contains(deviceId)
				|| mDatabase.checkVotedDevice(deviceId)) {
			System.out.println("<setRating>: Already in mDeviceIds.");
			return true;
		}
		System.out.println("<setRating>: Not yet in mDeviceIds.");
		return false;
	}

	/**
	 * Get the Rating of a particular Meal
	 * 
	 * @param meal
	 *            The Meal for which we want the Rating
	 * @return rating The Meal's Rating
	 */
	@Override
	public Rating getRating(Meal meal) throws TException {
		updateMenus();
		System.out.println("<getRating>: Rating Request");

		int mealHashCode = meal.hashCode();

		if (mMealRatings != null) {
			return mMealRatings.get(mealHashCode);
		}

		return null;
	}

	/**
	 * Get all the Ratings for today's meals Not working correctly, I think when
	 * the menus are up to date (meaning it's still today) it doesn't get the
	 * Ratings again 'cause of the updateMenus method...
	 * 
	 * @return mCampusMealRatings the map of all ratings associated with the
	 *         corresponding meal
	 */
	@Override
	public Map<Integer, Rating> getRatings() throws TException {
		// Here is why we don't get the Ratings right?
		updateMenus();
		System.out.println("<getRatings>: Ratings Request.");

		if (mMealRatings != null) {
			return mMealRatings;
		}

		return null;
	}

	/**
	 * Sets the Rating for a particular Meal Missing deviceID and mealHashcode
	 * because we don't get parameters from the Request... TODO : find another
	 * way to pass the info (lazy for now...)
	 * 
	 * @param rating
	 *            The rating submitted by the user
	 * @return submitStatus the Status of Submission (Valid, Error,
	 *         Already_Voted or Too_Early)
	 */
	@Override
	public SubmitStatus setRating(Rating rating, Meal meal, String deviceId)
			throws TException {
		updateMenus();
		System.out.println("<setRating>: Rating Request");

		if (rating == null || meal == null || deviceId == null) {
			return SubmitStatus.ERROR;
		}

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		if (now.get(Calendar.HOUR_OF_DAY) < 11) {
			return SubmitStatus.TOOEARLY;
		}

		if (mDeviceIds.contains(deviceId)) {
			System.out.println("<setRating>: Already in mDeviceIds.");
			return SubmitStatus.ALREADY_VOTED;
		}

		boolean voted = mDatabase.checkVotedDevice(deviceId);

		if (voted) {
			System.out.println("<setRating>: Already in DB Database.");
			return SubmitStatus.ALREADY_VOTED;
		}

		int mealHashCode = meal.hashCode();

		double ratingTotal;
		double ratingValue;
		int newNbVotes;

		for (Meal currentMeal : mAllMeals) {
			if (currentMeal.hashCode() == mealHashCode) {

				ratingTotal = currentMeal.getRating().getTotalRating()
						+ rating.getRatingValue();
				newNbVotes = currentMeal.getRating().getNbVotes() + 1;
				if (newNbVotes != 0) {
					ratingValue = ratingTotal / newNbVotes;
				} else {
					ratingValue = 0;
				}

				// Update Rating for this meal
				currentMeal.getRating().setNbVotes(newNbVotes);
				currentMeal.getRating().setTotalRating(ratingTotal);
				currentMeal.getRating().setRatingValue(ratingValue);

				// Update Rating + deviceID on DB
				mDatabase.insertRating(currentMeal);
				mDatabase.insertVotedDevice(deviceId, mealHashCode,
						rating.getRatingValue());

				// Add deviceID in the list
				mDeviceIds.add(deviceId);

				// Update Rating in the MealList
				mMealRatings.put(mealHashCode, currentMeal.getRating());
				return SubmitStatus.VALID;
			}
		}

		return SubmitStatus.ERROR;
	}

	/**
	 * Gets all Sandwiches on campus
	 * 
	 * @return mSandwiches the list of sandwiches
	 */
	@Override
	public List<Sandwich> getSandwiches() throws TException {
		if (mSandwiches == null || mSandwiches.isEmpty()) {
			importSandwiches();
			System.out.println("<getSandwiches>: Reimporting sandwiches.");
		} else {
			System.out.println("<getSandwiches>: Not reimporting sandwiches");
		}
		return mSandwiches;
	}

	/**
	 * Initiates parsing of the restaurant list from the file stored on the
	 * server
	 */
	private void getRestaurantsList() {
		RestaurantListParser rlp = new RestaurantListParser(
				"restaurants_list.txt");
		mRestaurantsFeeds = rlp.getFeeds();
	}

	/**
	 * Imports Menus from the RSS feed
	 */
	private void importMenus() {
		List<Meal> mealsFromDB = mDatabase.getMeals();

		System.out.println("MealsFromDb: " + mealsFromDB.size());
		if (mealsFromDB != null && !mealsFromDB.isEmpty()) {
			mAllMeals = mealsFromDB;

			for (Meal m : mAllMeals) {
				mMealRatings.put(m.hashCode(), m.getRating());
			}
			mLastImportedMeals = new Date();
			System.out.println("<importMenus>: Getting menus from DB");
		} else {
			parseMenus();
			mDatabase.insertMeals(mAllMeals);
		}
	}

	/**
	 * Refresh menus because they have been imported too long ago
	 */
	private void refreshMenus() {
		parseMenus();
		mDatabase.insertMeals(mAllMeals);
	}

	/**
	 * Parse the menus from the RSS feeds
	 */
	private void parseMenus() {
		Set<String> restaurants = mRestaurantsFeeds.keySet();

		for (String r : restaurants) {
			RssParser rp = new RssParser(mRestaurantsFeeds.get(r));
			rp.parse();
			RssFeed feed = rp.getFeed();

			Restaurant newResto = new Restaurant(r.hashCode(), r);

			if (feed != null && feed.items != null) {
				for (int i = 0; i < feed.items.size(); i++) {
					Rating mealRating = new Rating(0, 0, 0);
					Meal newMeal = new Meal(
							(r + feed.items.get(i).title).hashCode(),
							feed.items.get(i).title,
							feed.items.get(i).description, newResto, mealRating);
					if (!Utils.containsSpecialAscii(newMeal.mealDescription,
							BAD_CHAR)
							&& !Utils.containsSpecialAscii(newMeal.name,
									BAD_CHAR)) {
						mAllMeals.add(newMeal);
						mMealRatings.put(newMeal.hashCode(), mealRating);
					}
				}
				mLastImportedMeals = new Date();
			} else {
				System.out.println("<importMenus>: Empty Feed for " + r);
			}
		}
		if (mAllMeals.isEmpty()) {
			mLastImportedMeals = new Date();
		}
	}

	/**
	 * Imports the Meals again if they are not up to date
	 */
	private void updateMenus() {
		if (!isToday(mLastImportedMeals)) {
			mAllMeals.clear();
			mMealRatings.clear();
			mDeviceIds.clear();
			importMenus();
		}
	}

	/**
	 * Check if a particular Meal is already into the menus list
	 * 
	 * @param meal
	 *            The Meal we want to check
	 * @return true if it's already in the list of Meals
	 */
	private boolean alreadyExist(Meal meal) {
		for (Meal m : mAllMeals) {
			if (m.hashCode() == meal.hashCode()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Import all Sandwiches on campus from a hardCoded list
	 */
	private void importSandwiches() {
		/* Cafeteria INM */
		Restaurant cafeteriaINM = new Restaurant(("Cafeteria INM").hashCode(),
				"Cafeteria INM");
		mSandwiches.add(new Sandwich(
				(cafeteriaINM.getName() + "Poulet au Curry").hashCode(),
				cafeteriaINM, "Poulet au Curry"));
		mSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Thon")
				.hashCode(), cafeteriaINM, "Thon"));
		mSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Jambon")
				.hashCode(), cafeteriaINM, "Jambon"));
		mSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Fromage")
				.hashCode(), cafeteriaINM, "Fromage"));
		mSandwiches.add(new Sandwich(
				(cafeteriaINM.getName() + "Tomate Mozzarella").hashCode(),
				cafeteriaINM, "Tomate Mozzarella"));
		mSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Jambon Cru")
				.hashCode(), cafeteriaINM, "Jambon Cru"));
		mSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Salami")
				.hashCode(), cafeteriaINM, "Salami"));
		mSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Autres")
				.hashCode(), cafeteriaINM, "Autres"));

		/* Cafeteria BM */
		mSandwiches.addAll(defaultSandwichList("Cafeteria BM"));

		/* Cafeteria BC */
		mSandwiches.addAll(defaultSandwichList("Cafeteria BC"));

		/* Cafeteria SV */
		mSandwiches.addAll(defaultSandwichList("Cafeteria SV"));

		/* Cafeteria MX */
		mSandwiches.addAll(defaultSandwichList("Cafeteria MX"));

		/* Cafeteria PH */
		mSandwiches.addAll(defaultSandwichList("Cafeteria PH"));

		/* Cafeteria ELA */
		mSandwiches.addAll(defaultSandwichList("Cafeteria ELA"));

		/* Le Giacometti (Cafeteria SG) */
		Restaurant leGiacometti = new Restaurant(("Le Giacometti").hashCode(),
				"Le Giacometti");
		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Jambon")
				.hashCode(), leGiacometti, "Jambon"));
		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Salami")
				.hashCode(), leGiacometti, "Salami"));
		mSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Jambon de dinde").hashCode(),
				leGiacometti, "Jambon de dinde"));
		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Gruyère")
				.hashCode(), leGiacometti, "Gruyière"));
		mSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Viande Séchée").hashCode(),
				leGiacometti, "Viande Séchée"));
		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Jambon Cru")
				.hashCode(), leGiacometti, "Jambon Cru"));
		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Roast-Beef")
				.hashCode(), leGiacometti, "Roast-Beef"));
		mSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Poulet Jijommaise").hashCode(),
				leGiacometti, "Poulet Jijommaise"));
		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Crevettes")
				.hashCode(), leGiacometti, "Crevettes"));
		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Saumon Fumé")
				.hashCode(), leGiacometti, "Saumon Fumé"));
		mSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Poulet au Curry").hashCode(),
				leGiacometti, "Poulet au Curry"));
		mSandwiches.add(new Sandwich((leGiacometti.getName() + "Autres")
				.hashCode(), leGiacometti, "Autres"));

		/* L'Esplanade */
		Restaurant lEsplanade = new Restaurant(("L'Esplanade").hashCode(),
				"L'Esplanade");
		mSandwiches
				.add(new Sandwich((lEsplanade.getName() + "Thon").hashCode(),
						lEsplanade, "Thon"));
		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Poulet au Curry")
				.hashCode(), lEsplanade, "Poulet au Curry"));
		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Aubergine")
				.hashCode(), lEsplanade, "Aubergine"));
		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Roast-Beef")
				.hashCode(), lEsplanade, "Roast-Beef"));
		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Jambon Cru")
				.hashCode(), lEsplanade, "Jambon Cru"));
		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Viande Séchée")
				.hashCode(), lEsplanade, "Viande Séchée"));
		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Saumon Fumé")
				.hashCode(), lEsplanade, "Saumon Fumé"));
		mSandwiches.add(new Sandwich((lEsplanade.getName() + "Autres")
				.hashCode(), lEsplanade, "Autres"));

		/* L'Arcadie */
		mSandwiches.addAll(defaultSandwichList("L'Arcadie"));

		/* Atlantide */
		Restaurant lAtlantide = new Restaurant(("L'Atlantide").hashCode(),
				"L'Atlantide");
		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Sandwich long")
				.hashCode(), lAtlantide, "Sandwich long"));
		mSandwiches.add(new Sandwich(
				(lAtlantide.getName() + "Sandwich au pavot").hashCode(),
				lAtlantide, "Sandwich au pavot"));
		mSandwiches.add(new Sandwich(
				(lAtlantide.getName() + "Sandwich intégral").hashCode(),
				lAtlantide, "Sandwich intégral"));
		mSandwiches.add(new Sandwich(
				(lAtlantide.getName() + "Sandwich provençal").hashCode(),
				lAtlantide, "Sandwich provençal"));
		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Parisette")
				.hashCode(), lAtlantide, "Parisette"));
		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Jambon")
				.hashCode(), lAtlantide, "Jambon"));
		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Salami")
				.hashCode(), lAtlantide, "Salami"));
		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Dinde")
				.hashCode(), lAtlantide, "Dinde"));
		mSandwiches
				.add(new Sandwich((lAtlantide.getName() + "Thon").hashCode(),
						lAtlantide, "Thon"));
		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Mozzarella")
				.hashCode(), lAtlantide, "Mozzarella"));
		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Saumon Fumé")
				.hashCode(), lAtlantide, "Saumon Fumé"));
		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Viande Séchée")
				.hashCode(), lAtlantide, "Viande Séchée"));
		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Jambon Cru")
				.hashCode(), lAtlantide, "Jambon Cru"));
		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Roast-Beef")
				.hashCode(), lAtlantide, "Roast-Beef"));
		mSandwiches.add(new Sandwich((lAtlantide.getName() + "Autres")
				.hashCode(), lAtlantide, "Autres"));

		/* Satellite */
		Restaurant satellite = new Restaurant(("Satellite").hashCode(),
				"Satellite");
		mSandwiches.add(new Sandwich((satellite.getName() + "Jambon")
				.hashCode(), satellite, "Jambon"));
		mSandwiches.add(new Sandwich((satellite.getName() + "Thon").hashCode(),
				satellite, "Thon"));
		mSandwiches.add(new Sandwich((satellite.getName() + "Jambon Fromage")
				.hashCode(), satellite, "Jambon Fromage"));
		mSandwiches.add(new Sandwich((satellite.getName() + "Roast-Beef")
				.hashCode(), satellite, "Roast-Beef"));
		mSandwiches.add(new Sandwich((satellite.getName() + "Poulet au Curry")
				.hashCode(), satellite, "Poulet au Curry"));
		mSandwiches.add(new Sandwich((satellite.getName() + "Jambon Cru")
				.hashCode(), satellite, "Jambon Cru"));
		mSandwiches.add(new Sandwich(
				(satellite.getName() + "Tomate Mozzarella").hashCode(),
				satellite, "Tomate Mozzarella"));
		mSandwiches.add(new Sandwich((satellite.getName() + "Salami")
				.hashCode(), satellite, "Salami"));
		mSandwiches.add(new Sandwich((satellite.getName() + "Parmesan")
				.hashCode(), satellite, "Parmesan"));
		mSandwiches.add(new Sandwich(
				(satellite.getName() + "Aubergine grillée").hashCode(),
				satellite, "Aubergine grillée"));
		mSandwiches.add(new Sandwich((satellite.getName() + "Viande séchée")
				.hashCode(), satellite, "Viande séchée"));
		mSandwiches.add(new Sandwich((satellite.getName() + "Autres")
				.hashCode(), satellite, "Autres"));

		/* N�goce */
		Restaurant negoce = new Restaurant(("Négoce").hashCode(), "Négoce");
		mSandwiches.add(new Sandwich((negoce.getName() + "Dinde").hashCode(),
				negoce, "Dinde"));
		mSandwiches.add(new Sandwich((negoce.getName() + "Thon").hashCode(),
				negoce, "Thon"));
		mSandwiches.add(new Sandwich((negoce.getName() + "Gratiné Jambon")
				.hashCode(), negoce, "Gratiné Jambon"));
		mSandwiches.add(new Sandwich((negoce.getName() + "Mozzarella Olives")
				.hashCode(), negoce, "Mozzarella Olives"));
		mSandwiches.add(new Sandwich((negoce.getName() + "Poulet au Curry")
				.hashCode(), negoce, "Poulet au Curry"));
		mSandwiches.add(new Sandwich((negoce.getName() + "Jambon Fromage")
				.hashCode(), negoce, "Jambon Fromage"));
		mSandwiches.add(new Sandwich((negoce.getName() + "Jambon").hashCode(),
				negoce, "Jambon"));
		mSandwiches.add(new Sandwich((negoce.getName() + "Salami").hashCode(),
				negoce, "Salami"));
		mSandwiches.add(new Sandwich((negoce.getName() + "Roast-Beef")
				.hashCode(), negoce, "Roast-Beef"));
		mSandwiches.add(new Sandwich((negoce.getName() + "Mozarrella")
				.hashCode(), negoce, "Mozzarella"));
		mSandwiches.add(new Sandwich((negoce.getName() + "Autres").hashCode(),
				negoce, "Autres"));

		mLastImportedSandwiches = new Date();
	}

	/**
	 * The default sandwich list
	 * 
	 * @param name
	 *            The name of the list
	 * @return the sandwich list
	 */
	private Vector<Sandwich> defaultSandwichList(String name) {

		Vector<Sandwich> defaultSandwichList = new Vector<Sandwich>();
		Restaurant r = new Restaurant(name.hashCode(), name);

		defaultSandwichList.add(new Sandwich((name + "Thon").hashCode(), r,
				"Thon"));
		defaultSandwichList.add(new Sandwich((name + "Jambon").hashCode(), r,
				"Jambon"));
		defaultSandwichList.add(new Sandwich((name + "Fromage").hashCode(), r,
				"Fromage"));
		defaultSandwichList.add(new Sandwich((name + "Tomate Mozzarella")
				.hashCode(), r, "Tomate Mozzarella"));
		defaultSandwichList.add(new Sandwich((name + "Jambon Cru").hashCode(),
				r, "Jambon Cru"));
		defaultSandwichList.add(new Sandwich((name + "Salami").hashCode(), r,
				"Salami"));
		defaultSandwichList.add(new Sandwich((name + "Autres").hashCode(), r,
				"Autres"));

		return defaultSandwichList;
	}

	/**
	 * Checks whether the last date the user had is today
	 * 
	 * @param oldDate
	 * @return true if the date is today
	 */
	private boolean isToday(Date oldDate) {
		if (oldDate == null)
			return false;

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		Calendar then = Calendar.getInstance();
		then.setTime(oldDate);

		return (now.get(Calendar.DAY_OF_WEEK) == then.get(Calendar.DAY_OF_WEEK)) ? true
				: false;
	}

	/**
	 * Checks whether the date is up to date (according to today and this
	 * particular hour)
	 * 
	 * @param oldDate
	 * @return true if it is
	 */
	private boolean isUpToDate(Date oldDate) {
		if (oldDate == null)
			return false;

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		Calendar then = Calendar.getInstance();
		then.setTime(oldDate);

		if (now.get(Calendar.DAY_OF_WEEK) != then.get(Calendar.DAY_OF_WEEK)) {
			return false;
		} else {
			if (getMinutes(then.getTime(), now.getTime()) > REFRESH_INTERVAL) {
				return false;
			}
		}
		return true;
	}

	/**
	 * To get the minutes separating two different dates
	 * 
	 * @param then
	 * @param now
	 * @return the minutes that separate both dates
	 */
	private long getMinutes(Date then, Date now) {
		long diff = now.getTime() - then.getTime();

		long realDiff = diff / 60000;

		return realDiff;
	}
}

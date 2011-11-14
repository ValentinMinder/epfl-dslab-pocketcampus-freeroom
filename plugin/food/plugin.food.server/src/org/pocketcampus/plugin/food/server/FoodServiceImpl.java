package org.pocketcampus.plugin.food.server;

import java.sql.Connection;
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
import org.pocketcampus.plugin.food.shared.RatingValue;
import org.pocketcampus.plugin.food.shared.Restaurant;
import org.pocketcampus.plugin.food.shared.Sandwich;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

public class FoodServiceImpl implements FoodService.Iface {
	private Date mLastImportedMenus;
	private Date mLastImportedSandwiches;

	private FoodDB mDB;

	private List<Meal> mCampusMeals;
	private List<Restaurant> mCampusRestaurants;
	private HashMap<Integer, Rating> mCampusMealRatings;
	private List<Sandwich> mCampusSandwiches;

	// Character to filter because doesn't show right.
	private final static int BAD_CHAR = 65533;

	private ArrayList<String> mDeviceIds;

	/**
	 * Constructor Instantiate all containers for meals, ratings, sandwiches,
	 * ... and the Database. Also import menus and sandwiches since it's the
	 * first execution of the server.
	 */
	public FoodServiceImpl() {
		System.out.println("Starting Food plugin server ...");

		mDB = new FoodDB("PocketCampusDB");

		mCampusMeals = new ArrayList<Meal>();
		mCampusSandwiches = new ArrayList<Sandwich>();
		mCampusMealRatings = new HashMap<Integer, Rating>();
		mDeviceIds = new ArrayList<String>();

		importMenus();
		importSandwiches();

		mLastImportedSandwiches = new Date();
	}

	/**
	 * Get all menus for today
	 * 
	 * @return mCampusMeals The list of meals
	 */
	@Override
	public List<Meal> getMeals() throws TException {

		if (!isToday(mLastImportedMenus)) {
			System.out
			.println("<getMeals>: Date not valid. Reimporting Meals.");

			mCampusMeals.clear();
			mDeviceIds.clear();
			mCampusMealRatings.clear();

			importMenus();
		} else if (!isUpToDate(mLastImportedMenus)) {
			System.out
			.println("<getMeals>: Time not valid. Reimporting Meals.");

			refreshMenus();
		} else {
			System.out.println("<getMeals>: " + mLastImportedMenus
					+ ", not reimporting Meals.");
		}

		return mCampusMeals;
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

		if (mCampusMeals != null) {

			for (Meal m : mCampusMeals) {
				Restaurant r = m.getRestaurant();
				if (!mRestaurantList.contains(r)) {
					mRestaurantList.add(r);
				}
			}
		}

		return mRestaurantList;
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

		if (mCampusMealRatings != null) {
			return mCampusMealRatings.get(mealHashCode);
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

		if (mCampusMealRatings != null) {
			return mCampusMealRatings;
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

		System.out.println("<setRating>: Now : "
				+ now.get(Calendar.HOUR_OF_DAY));

		if (now.get(Calendar.HOUR_OF_DAY) < 11) {
			return SubmitStatus.TOOEARLY;
		}

		Connection connection = mDB.createConnection();

		boolean voted = mDB.checkVotedDevice(connection, deviceId);

		if (mDeviceIds.contains(deviceId)) {
			System.out.println("<setRating>: Already in mDeviceIds.");
			mDB.closeConnection(connection);
			return SubmitStatus.ALREADY_VOTED;
		} else if (voted) {
			System.out.println("<setRating>: Already in ,DB Database.");
			mDB.closeConnection(connection);
			return SubmitStatus.ALREADY_VOTED;
		}

		int mealHashCode = meal.hashCode();
		System.out.println("<setRating>: mealHashCode: " + mealHashCode);

		double ratingTotal;
		double ratingValue;
		int newNbVotes;

		for (int i = 0; i < mCampusMeals.size(); i++) {
			Meal currentMeal = mCampusMeals.get(i);
			
			if (currentMeal.hashCode() == mealHashCode) {

				ratingTotal = currentMeal.getRating().getTotalRating()
						+ rating.getRatingValue();
				newNbVotes = currentMeal.getRating().getNbVotes() + 1;
				if( newNbVotes != 0) {				
					ratingValue = ratingTotal / newNbVotes;
				} else {
					ratingValue = 0;
				}

				System.out.println("<setRating>: Inside : "
						+ currentMeal.hashCode());

				// Update Rating for this meal
				currentMeal.getRating().setNbVotes(newNbVotes);
				currentMeal.getRating().setTotalRating(ratingTotal);
				currentMeal.getRating().setRatingValue(ratingValue);

				// Update Rating + deviceID on DB
				mDB.insertRating(connection, mealHashCode, currentMeal);
				mDB.insertVotedDevice(connection, deviceId, mealHashCode,
						rating.getRatingValue());

				// Add deviceID in the list
				mDeviceIds.add(deviceId);

				// Update Rating in the MealList
				mCampusMealRatings.put(mealHashCode, currentMeal.getRating());
				mDB.closeConnection(connection);
				return SubmitStatus.VALID;
			}
		}

		mDB.closeConnection(connection);
		return SubmitStatus.ERROR;
	}

	/**
	 * Gets all sandwiches on campus
	 * 
	 * @return mCampusSandwiches the list of sandwiches
	 */
	@Override
	public List<Sandwich> getSandwiches() throws TException {
		if (mCampusSandwiches == null || mCampusSandwiches.isEmpty()) {
			importSandwiches();
			System.out.println("<getSandwiches>: Reimporting sandwiches.");
		} else {
			System.out.println("<getSandwiches>: Not reimporting sandwiches");
		}
		return mCampusSandwiches;
	}

	/**
	 * Imports Menus from the RSS feed
	 */
	private void importMenus() {
		Connection connection = mDB.createConnection();
		List<Meal> mealsFromDB = mDB.getMeals(connection);

		System.out.println("MealsFromDb: " + mealsFromDB.size());
		if (mealsFromDB != null && !mealsFromDB.isEmpty()) {
			mCampusMeals = mealsFromDB;

			for (Meal m : mCampusMeals) {
				mCampusMealRatings.put(m.hashCode(), m.getRating());
			}
			mLastImportedMenus = new Date();
			System.out.println("<importMenus>: Getting menus from DB");
		} else {
			RestaurantListParser rlp = new RestaurantListParser();
			HashMap<String, String> restaurantsFeeds = rlp.getFeeds();
			Set<String> restaurants = restaurantsFeeds.keySet();

			for (String r : restaurants) {
				RssParser rp = new RssParser(restaurantsFeeds.get(r));
				rp.parse();
				RssFeed feed = rp.getFeed();

				Restaurant newResto = new Restaurant(r.hashCode(), r);

				if (feed != null && feed.items != null) {
					for (int i = 0; i < feed.items.size(); i++) {
						Rating mealRating = new Rating(0, 0,
								0);
						Meal newMeal = new Meal(
								(r + feed.items.get(i).title).hashCode(),
								feed.items.get(i).title,
								feed.items.get(i).description, newResto,
								mealRating);
						if (!Utils.containsSpecialAscii(
								newMeal.mealDescription, BAD_CHAR)
								&& !Utils.containsSpecialAscii(newMeal.name,
										BAD_CHAR)) {
							mCampusMeals.add(newMeal);
							mCampusMealRatings.put(newMeal.hashCode(),
									mealRating);
						}
					}
					mLastImportedMenus = new Date();
				} else {
					System.out.println("<importMenus>: Empty Feed for " + r);
				}
			}
			if (mCampusMeals.isEmpty()) {
				mLastImportedMenus = new Date();
			}
			mDB.insertMeals(mCampusMeals);
		}
	}

	/**
	 * 
	 */
	private void refreshMenus() {
		RestaurantListParser rlp = new RestaurantListParser();
		HashMap<String, String> restaurantFeeds = rlp.getFeeds();
		Set<String> restaurants = restaurantFeeds.keySet();

		for (String r : restaurants) {
			RssParser rp = new RssParser(restaurantFeeds.get(r));
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
					if (!alreadyExist(newMeal)
							&& !Utils.containsSpecialAscii(
									newMeal.mealDescription, BAD_CHAR)
									&& !Utils.containsSpecialAscii(newMeal.name,
											BAD_CHAR)) {
						mCampusMeals.add(newMeal);
						mCampusMealRatings.put(newMeal.hashCode(), mealRating);
					}
				}
				mLastImportedMenus = new Date();
			} else {
				System.out.println("<refreshMenus>: Empty Feed");
			}
		}
		if (mCampusMeals.isEmpty()) {
			mLastImportedMenus = new Date();
		}
		for (Meal m : mCampusMeals) {
			mDB.insertMeal(m);
			System.out.println("<refreshMenus>: Inserting meal " + m.getName()
					+ ", " + m.getRestaurant().getName() + " into DB");
		}
	}

	/**
	 * If meals are not up to date, imports them again
	 */
	private void updateMenus() {
		if (!isToday(mLastImportedMenus)) {
			mCampusMeals.clear();
			mCampusMealRatings.clear();
			mDeviceIds.clear();
			importMenus();
		}
	}

	/**
	 * Check if a particular Meal is already into the campus menus list
	 * 
	 * @param meal
	 *            The Meal we want to check
	 * @return true if it's already in there
	 */
	private boolean alreadyExist(Meal meal) {
		for (Meal m : mCampusMeals) {
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
		mCampusSandwiches.add(new Sandwich(
				(cafeteriaINM.getName() + "Poulet au Curry").hashCode(),
				cafeteriaINM, "Poulet au Curry"));
		mCampusSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Thon")
				.hashCode(), cafeteriaINM, "Thon"));
		mCampusSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Jambon")
				.hashCode(), cafeteriaINM, "Jambon"));
		mCampusSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Fromage")
				.hashCode(), cafeteriaINM, "Fromage"));
		mCampusSandwiches.add(new Sandwich(
				(cafeteriaINM.getName() + "Tomate Mozzarella").hashCode(),
				cafeteriaINM, "Tomate Mozzarella"));
		mCampusSandwiches.add(new Sandwich(
				(cafeteriaINM.getName() + "Jambon Cru").hashCode(),
				cafeteriaINM, "Jambon Cru"));
		mCampusSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Salami")
				.hashCode(), cafeteriaINM, "Salami"));
		mCampusSandwiches.add(new Sandwich((cafeteriaINM.getName() + "Autres")
				.hashCode(), cafeteriaINM, "Autres"));

		/* Cafeteria BM */
		mCampusSandwiches.addAll(defaultSandwichList("Cafeteria BM"));

		/* Cafeteria BC */
		mCampusSandwiches.addAll(defaultSandwichList("Cafeteria BC"));

		/* Cafeteria SV */
		mCampusSandwiches.addAll(defaultSandwichList("Cafeteria SV"));

		/* Cafeteria MX */
		mCampusSandwiches.addAll(defaultSandwichList("Cafeteria MX"));

		/* Cafeteria PH */
		mCampusSandwiches.addAll(defaultSandwichList("Cafeteria PH"));

		/* Cafeteria ELA */
		mCampusSandwiches.addAll(defaultSandwichList("Cafeteria ELA"));

		/* Le Giacometti (Cafeteria SG) */
		Restaurant leGiacometti = new Restaurant(("Le Giacometti").hashCode(),
				"Le Giacometti");
		mCampusSandwiches.add(new Sandwich((leGiacometti.getName() + "Jambon")
				.hashCode(), leGiacometti, "Jambon"));
		mCampusSandwiches.add(new Sandwich((leGiacometti.getName() + "Salami")
				.hashCode(), leGiacometti, "Salami"));
		mCampusSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Jambon de dinde").hashCode(),
				leGiacometti, "Jambon de dinde"));
		mCampusSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Gruy�re").hashCode(),
				leGiacometti, "Gruyi�re"));
		mCampusSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Viande S�ch�e").hashCode(),
				leGiacometti, "Viande S�ch�e"));
		mCampusSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Jambon Cru").hashCode(),
				leGiacometti, "Jambon Cru"));
		mCampusSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Roast-Beef").hashCode(),
				leGiacometti, "Roast-Beef"));
		mCampusSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Poulet Jijommaise").hashCode(),
				leGiacometti, "Poulet Jijommaise"));
		mCampusSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Crevettes").hashCode(),
				leGiacometti, "Crevettes"));
		mCampusSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Saumon Fum�").hashCode(),
				leGiacometti, "Saumon Fum�"));
		mCampusSandwiches.add(new Sandwich(
				(leGiacometti.getName() + "Poulet au Curry").hashCode(),
				leGiacometti, "Poulet au Curry"));
		mCampusSandwiches.add(new Sandwich((leGiacometti.getName() + "Autres")
				.hashCode(), leGiacometti, "Autres"));

		/* L'Esplanade */
		Restaurant lEsplanade = new Restaurant(("L'Esplanade").hashCode(),
				"L'Esplanade");
		mCampusSandwiches.add(new Sandwich((lEsplanade.getName() + "Thon")
				.hashCode(), lEsplanade, "Thon"));
		mCampusSandwiches.add(new Sandwich(
				(lEsplanade.getName() + "Poulet au Curry").hashCode(),
				lEsplanade, "Poulet au Curry"));
		mCampusSandwiches.add(new Sandwich((lEsplanade.getName() + "Aubergine")
				.hashCode(), lEsplanade, "Aubergine"));
		mCampusSandwiches.add(new Sandwich(
				(lEsplanade.getName() + "Roast-Beef").hashCode(), lEsplanade,
				"Roast-Beef"));
		mCampusSandwiches.add(new Sandwich(
				(lEsplanade.getName() + "Jambon Cru").hashCode(), lEsplanade,
				"Jambon Cru"));
		mCampusSandwiches.add(new Sandwich(
				(lEsplanade.getName() + "Viande S�ch�e").hashCode(),
				lEsplanade, "Viande S�ch�e"));
		mCampusSandwiches.add(new Sandwich(
				(lEsplanade.getName() + "Saumon Fum�").hashCode(),
				lEsplanade, "Saumon Fum�"));
		mCampusSandwiches.add(new Sandwich((lEsplanade.getName() + "Autres")
				.hashCode(), lEsplanade, "Autres"));

		/* L'Arcadie */
		mCampusSandwiches.addAll(defaultSandwichList("L'Arcadie"));

		/* Atlantide */
		Restaurant lAtlantide = new Restaurant(("L'Atlantide").hashCode(),
				"L'Atlantide");
		mCampusSandwiches.add(new Sandwich(
				(lAtlantide.getName() + "Sandwich long").hashCode(),
				lAtlantide, "Sandwich long"));
		mCampusSandwiches.add(new Sandwich(
				(lAtlantide.getName() + "Sandwich au pavot").hashCode(),
				lAtlantide, "Sandwich au pavot"));
		mCampusSandwiches.add(new Sandwich(
				(lAtlantide.getName() + "Sandwich int�gral").hashCode(),
				lAtlantide, "Sandwich int�gral"));
		mCampusSandwiches.add(new Sandwich(
				(lAtlantide.getName() + "Sandwich proven�al").hashCode(),
				lAtlantide, "Sandwich proven�al"));
		mCampusSandwiches.add(new Sandwich((lAtlantide.getName() + "Parisette")
				.hashCode(), lAtlantide, "Parisette"));
		mCampusSandwiches.add(new Sandwich((lAtlantide.getName() + "Jambon")
				.hashCode(), lAtlantide, "Jambon"));
		mCampusSandwiches.add(new Sandwich((lAtlantide.getName() + "Salami")
				.hashCode(), lAtlantide, "Salami"));
		mCampusSandwiches.add(new Sandwich((lAtlantide.getName() + "Dinde")
				.hashCode(), lAtlantide, "Dinde"));
		mCampusSandwiches.add(new Sandwich((lAtlantide.getName() + "Thon")
				.hashCode(), lAtlantide, "Thon"));
		mCampusSandwiches.add(new Sandwich(
				(lAtlantide.getName() + "Mozzarella").hashCode(), lAtlantide,
				"Mozzarella"));
		mCampusSandwiches.add(new Sandwich(
				(lAtlantide.getName() + "Saumon Fum�").hashCode(),
				lAtlantide, "Saumon Fum�"));
		mCampusSandwiches.add(new Sandwich(
				(lAtlantide.getName() + "Viande S�ch�e").hashCode(),
				lAtlantide, "Viande S�ch�e"));
		mCampusSandwiches.add(new Sandwich(
				(lAtlantide.getName() + "Jambon Cru").hashCode(), lAtlantide,
				"Jambon Cru"));
		mCampusSandwiches.add(new Sandwich(
				(lAtlantide.getName() + "Roast-Beef").hashCode(), lAtlantide,
				"Roast-Beef"));
		mCampusSandwiches.add(new Sandwich((lAtlantide.getName() + "Autres")
				.hashCode(), lAtlantide, "Autres"));

		/* Satellite */
		Restaurant satellite = new Restaurant(("Satellite").hashCode(),
				"Satellite");
		mCampusSandwiches.add(new Sandwich((satellite.getName() + "Thon")
				.hashCode(), satellite, "Thon"));
		mCampusSandwiches.add(new Sandwich(
				(satellite.getName() + "Jambon Fromage").hashCode(), satellite,
				"Jambon Fromage"));
		mCampusSandwiches.add(new Sandwich((satellite.getName() + "Roast-Beef")
				.hashCode(), satellite, "Roast-Beef"));
		mCampusSandwiches.add(new Sandwich(
				(satellite.getName() + "Poulet au Curry").hashCode(),
				satellite, "Poulet au Curry"));
		mCampusSandwiches.add(new Sandwich((satellite.getName() + "Jambon Cru")
				.hashCode(), satellite, "Jambon Cru"));
		mCampusSandwiches.add(new Sandwich(
				(satellite.getName() + "Tomate Mozzarella").hashCode(),
				satellite, "Tomate Mozzarella"));
		mCampusSandwiches.add(new Sandwich((satellite.getName() + "Salami")
				.hashCode(), satellite, "Salami"));
		mCampusSandwiches.add(new Sandwich((satellite.getName() + "Parmesan")
				.hashCode(), satellite, "Parmesan"));
		mCampusSandwiches.add(new Sandwich(
				(satellite.getName() + "Aubergine grill�e").hashCode(),
				satellite, "Aubergine grill�e"));
		mCampusSandwiches.add(new Sandwich(
				(satellite.getName() + "Viande s�ch�e").hashCode(),
				satellite, "Viande s�ch�e"));
		mCampusSandwiches.add(new Sandwich((satellite.getName() + "Autres")
				.hashCode(), satellite, "Autres"));

		/* N�goce */
		Restaurant negoce = new Restaurant(("N�goce").hashCode(), "N�goce");
		mCampusSandwiches.add(new Sandwich((negoce.getName() + "Dinde")
				.hashCode(), negoce, "Dinde"));
		mCampusSandwiches.add(new Sandwich((negoce.getName() + "Thon")
				.hashCode(), negoce, "Thon"));
		mCampusSandwiches.add(new Sandwich(
				(negoce.getName() + "Gratin� Jambon").hashCode(), negoce,
				"Gratin� Jambon"));
		mCampusSandwiches.add(new Sandwich(
				(negoce.getName() + "Mozzarella Olives").hashCode(), negoce,
				"Mozzarella Olives"));
		mCampusSandwiches.add(new Sandwich(
				(negoce.getName() + "Poulet au Curry").hashCode(), negoce,
				"Poulet au Curry"));
		mCampusSandwiches.add(new Sandwich(
				(negoce.getName() + "Jambon Fromage").hashCode(), negoce,
				"Jambon Fromage"));
		mCampusSandwiches.add(new Sandwich((negoce.getName() + "Jambon")
				.hashCode(), negoce, "Jambon"));
		mCampusSandwiches.add(new Sandwich((negoce.getName() + "Salami")
				.hashCode(), negoce, "Salami"));
		mCampusSandwiches.add(new Sandwich((negoce.getName() + "Roast-Beef")
				.hashCode(), negoce, "Roast-Beef"));
		mCampusSandwiches.add(new Sandwich((negoce.getName() + "Mozarrella")
				.hashCode(), negoce, "Mozzarella"));
		mCampusSandwiches.add(new Sandwich((negoce.getName() + "Autres")
				.hashCode(), negoce, "Autres"));

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
			if (getMinutes(then.getTime(), now.getTime()) > 60) {
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

	/**
	 * Converts a DOuble into its corresponding RatingValue
	 * 
	 * @param rating
	 * @return ratingValue corresponding to double
	 */
	private RatingValue doubleToStarRating(Double rating) {
		if (rating < 0.25) {
			return RatingValue.STAR_0_0;
		} else if (rating < 0.75) {
			return RatingValue.STAR_0_5;
		} else if (rating < 1.25) {
			return RatingValue.STAR_1_0;
		} else if (rating < 1.75) {
			return RatingValue.STAR_1_5;
		} else if (rating < 2.25) {
			return RatingValue.STAR_2_0;
		} else if (rating < 2.75) {
			return RatingValue.STAR_2_5;
		} else if (rating < 3.25) {
			return RatingValue.STAR_3_0;
		} else if (rating < 3.75) {
			return RatingValue.STAR_3_5;
		} else if (rating < 4.25) {
			return RatingValue.STAR_4_0;
		} else if (rating < 4.75) {
			return RatingValue.STAR_4_5;
		} else {
			return RatingValue.STAR_5_0;
		}
	}

	/**
	 * Converts a RatingValue into its corresponding double value
	 * 
	 * @param rating
	 * @return the double value of the rating
	 */
	private double starRatingToDouble(RatingValue rating) {
		double value = 0;

		switch (rating) {

		case STAR_0_0:
			break;
		case STAR_0_5:
			value = 0.5;
			break;
		case STAR_1_0:
			value = 1.0;
			break;
		case STAR_1_5:
			value = 1.5;
			break;
		case STAR_2_0:
			value = 2.0;
			break;
		case STAR_2_5:
			value = 2.5;
			break;
		case STAR_3_0:
			value = 3.0;
			break;
		case STAR_3_5:
			value = 3.5;
			break;
		case STAR_4_0:
			value = 4.0;
			break;
		case STAR_4_5:
			value = 4.5;
			break;
		case STAR_5_0:
			value = 5.0;
			break;
		default:
			break;
		}

		return value;
	}

}

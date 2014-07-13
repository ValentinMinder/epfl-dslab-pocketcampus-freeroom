package org.pocketcampus.plugin.food.server.old;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.food.server.old.OldRssParser.RssFeed;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;
import org.pocketcampus.plugin.food.shared.Restaurant;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

/**
 * The old food service.
 * Do not touch. I didn't refactor it properly since we'll drop it later.
 * 
 * Original authors:
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public final class OldFoodService {
	private static final String FEED_URL = "http://menus.epfl.ch/cgi-bin/rssMenus";

	/** The last time the Meals were parsed from the web page. */
	private Date mLastImportedMeals;

	/** The list of all Restaurants */
	private List<Restaurant> mRestaurantList;

	/** The list of all Meals. */
	private List<Meal> mAllMeals;

	// Character to filter because doesn't show right.
	private final static String BAD_CHAR = "" + (char) 65533;

	/** Interface to the database. */
	private OldFoodDB mDatabase;

	/** Ratings for all Meals, represents with their hashcode. */
	private HashMap<Long, Rating> mMealRatings;

	/** The list of DeviceIds that have already voted for a meal today. */
	private ArrayList<String> mDeviceIds;

	/** The interval in minutes at which the menu should be fetched. */
	private int REFRESH_INTERVAL = 60;

	public OldFoodService()
	{
		mDatabase = new OldFoodDB();

		mAllMeals = new ArrayList<Meal>();
		mMealRatings = new HashMap<Long, Rating>();
		mDeviceIds = new ArrayList<String>();
		mRestaurantList = new ArrayList<Restaurant>();

		importMenus();
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
	public SubmitStatus setRating(long mealId, double rating, String deviceId)
			throws TException {
		updateMenus();
		System.out.println("<setRating>: Rating Request");

		if (deviceId == null) {
			return SubmitStatus.ERROR;
		}

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		if (now.get(Calendar.HOUR_OF_DAY) < 11) {
			return SubmitStatus.TOO_EARLY;
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

		double ratingTotal;
		double ratingValue;
		int newNbVotes;

		for (Meal currentMeal : mAllMeals) {
			if (currentMeal.getMealId() == mealId) {

				ratingTotal = currentMeal.getRating().getSumOfRatings()
						+ rating;
				newNbVotes = currentMeal.getRating().getNumberOfVotes() + 1;
				if (newNbVotes != 0) {
					ratingValue = ratingTotal / newNbVotes;
				} else {
					ratingValue = 0;
				}

				// Update Rating for this meal
				currentMeal.getRating().setNumberOfVotes(newNbVotes);
				currentMeal.getRating().setSumOfRatings(ratingTotal);
				currentMeal.getRating().setRatingValue(ratingValue);

				// Update Rating + deviceID on DB
				mDatabase.insertRating(currentMeal);
				mDatabase.insertVotedDevice(deviceId, mealId, rating);

				// Add deviceID in the list
				mDeviceIds.add(deviceId);

				// Update Rating in the MealList
				mMealRatings.put(mealId, currentMeal.getRating());
				return SubmitStatus.VALID;
			}
		}

		return SubmitStatus.ERROR;
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
	 * Get all the Ratings for today's meals. Not working correctly, I think
	 * when the menus are up to date (meaning it's still today) it doesn't get
	 * the Ratings again 'cause of the updateMenus method...
	 * 
	 * @return mCampusMealRatings the map of all ratings associated with the
	 *         corresponding meal.
	 */
	public Map<Long, Rating> getRatings() throws TException {
		// Here is why we don't get the Ratings right?
		updateMenus();
		System.out.println("<getRatings>: Ratings Request.");

		if (mMealRatings != null) {
			return mMealRatings;
		}

		return null;
	}

	public List<Meal> getMeals() {

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
	 * Imports Menus from the RSS feed
	 */
	private void importMenus() {
		List<Meal> mealsFromDB = mDatabase.getMeals();

		if (mealsFromDB != null && !mealsFromDB.isEmpty()) {
			mAllMeals = mealsFromDB;

			for (Meal m : mAllMeals) {
				mMealRatings.put(m.getMealId(), m.getRating());
			}
			mLastImportedMeals = new Date();
			System.out.println("<importMenus>: Getting menus from DB");

			List<Meal> newlyParsedMeals = parseMenus();
			mDatabase.insertMeals(newlyParsedMeals);
		} else {
			parseMenus();
			mDatabase.insertMeals(mAllMeals);
		}
	}

	/**
	 * Refresh menus because they have been imported too long ago
	 */
	private void refreshMenus() {
		List<Meal> newlyParsedMeals = parseMenus();
		if (newlyParsedMeals != null && !newlyParsedMeals.isEmpty()) {
			mDatabase.insertMeals(newlyParsedMeals);
		}
	}

	/**
	 * Imports the Meals again if they are not up to date.
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
	 * Checks whether the last date the user had is today
	 * 
	 * @param oldDate
	 * @return true if the date is today
	 */
	private boolean isToday(Date oldDate) {
		if (oldDate == null) {
			return false;
		}
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
		if (oldDate == null) {
			return false;
		}
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
		long realDiff = diff / (60000);
		return realDiff;
	}

	/**
	 * Checks if the restaurant for the current meal is already in the list of
	 * restaurants.
	 * 
	 * @return true if the restaurant exists, false if not
	 */
	private boolean restaurantExists(String restaurant) {
		for (Restaurant r : mRestaurantList) {
			if (r.getName().equals(restaurant)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if a particular Meal is already into the menus list
	 * 
	 * @param meal
	 *            The Meal we want to check
	 * @return true if it's already in the list of Meals
	 */
	private boolean alreadyExist(Meal meal) {
		long mealId = meal.getMealId();
		for (Meal m : mAllMeals) {
			if (m.getMealId() == mealId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Parse the menus from the RSS feeds
	 * 
	 * @return the list of meals that were just parsed and that were not in the
	 *         list of meals previously on the server
	 */
	private List<Meal> parseMenus() {
		List<Meal> newlyParsedMeals = new ArrayList<Meal>();

		OldRssParser rp = new OldRssParser(FEED_URL);
		rp.parse();
		RssFeed feed = rp.getFeed();

		if (feed != null && feed.items != null) {
			for (int i = 0; i < feed.items.size(); i++) {
				// New meal rating
				Rating mealRating = new Rating(0, 0, 0);
				// Meal title
				String title = feed.items.get(i).title;

				// Get the meal name and restaurant from the item title.
				String[] items = title.split(":");
				String name = "";
				String restaurant = "";
				if (items.length == 2) {
					restaurant = items[0].trim();
					name = items[1].trim();
				}

				Restaurant newResto = new Restaurant(restaurant.hashCode(), restaurant);
				if (!restaurantExists(restaurant)) {
					mRestaurantList.add(newResto);
				}

				// Meal description
				String description = feed.items.get(i).description;
				// Meal id
				long id = generateMealId(name, description, newResto);

				Meal newMeal = new Meal(id, name, description, newResto,
						mealRating);
				if (!newMeal.getMealDescription().contains(BAD_CHAR)
						&& !newMeal.getName().contains(BAD_CHAR)) {
					if (!alreadyExist(newMeal)) {
						mAllMeals.add(newMeal);
						mMealRatings.put(newMeal.getMealId(), mealRating);
						// Buffer list to then add to the database the new
						// meals
						newlyParsedMeals.add(newMeal);
					}
				}
			}
			mLastImportedMeals = new Date();
		} else {
			System.out.println("<importMenus>: Empty Feed");
		}
		if (mAllMeals.isEmpty()) {
			mLastImportedMeals = new Date();
		}
		return newlyParsedMeals;
	}

	/**
	 * Generate the Meal Id for a given Meal.
	 * 
	 * @param name
	 *            The name of the Meal.
	 * @param description
	 *            The description of the Meal.
	 * @param restaurant
	 *            The Restaurant the Meal is available at.
	 * @return The generated unique Id in long type.
	 */
	public static long generateMealId(String name, String description,
			Restaurant restaurant) {
		final long prime = 31;
		long result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((restaurant == null) ? 0 : restaurant.getName().hashCode());
		return result;
	}

}

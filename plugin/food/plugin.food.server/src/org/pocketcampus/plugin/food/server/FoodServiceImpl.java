package org.pocketcampus.plugin.food.server;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.thrift.TException;
import org.pocketcampus.platform.sdk.shared.HttpClientImpl;
import org.pocketcampus.plugin.food.shared.*;
import org.joda.time.*;

/**
 * Provides information about the meals, and allows users to rate them.
 */
public class FoodServiceImpl implements FoodService.Iface {
	private static final Hours VOTING_MIN = Hours.hours(11);

	private final DeviceDatabase _deviceDatabase;
	private final RatingDatabase _ratingDatabase;
	private final MealList _mealList;
	
	private static final String MEAL_PICS_FOLDER_URL = "http://pocketcampus.epfl.ch/backend/meal-pics/";
	private static final Map<MealType, String> MEAL_TYPE_PICTURE_URLS = new HashMap<MealType, String>();
	
	static {
		for (MealType type : MealType.values()) {
			MEAL_TYPE_PICTURE_URLS.put(type, MEAL_PICS_FOLDER_URL+type+".png");
			//=> e.g. URL for PIZZA is http://pocketcampus.epfl.ch/backend/meal-pics/PIZZA.png
		}
	}

	public FoodServiceImpl(DeviceDatabase deviceDatabase, RatingDatabase ratingDatabase, MealList mealList) {
		_deviceDatabase = deviceDatabase;
		_ratingDatabase = ratingDatabase;
		_mealList = mealList;
	}

	public FoodServiceImpl() {
		this(new DeviceDatabaseImpl(), new RatingDatabaseImpl(), new MealListCache(new MealListImpl(new HttpClientImpl())));
	}

	@Override
	public FoodResponse getFood(FoodRequest foodReq) throws TException {
		LocalDate date = LocalDate.now();
		if (foodReq.isSetMealDate()) {
			date = getDateFromTimestamp(foodReq.getMealDate());
		}
		MealTime time = MealTime.LUNCH;
		if (foodReq.isSetMealTime()) {
			time = foodReq.getMealTime();
		}

		List<EpflRestaurant> menu = null;

		try {
			MealList.MenuResult result = _mealList.getMenu(time, date);
			menu = result.menu;

			if (result.hasChanged) {
				_ratingDatabase.insertMenu(menu);
			}
		} catch (Exception e) {
			menu = new ArrayList<EpflRestaurant>();
		}

		_ratingDatabase.setRatings(menu);

		return new FoodResponse(menu, MEAL_TYPE_PICTURE_URLS);
	}

	@Override
	public VoteResponse vote(VoteRequest voteReq) throws TException {
		try {
			if (voteReq.getRating() < 0 || voteReq.getRating() > 5) {
				throw new Exception("Invalid rating.");
			}

			if (_deviceDatabase.hasVotedToday(voteReq.getDeviceId())) {
				return new VoteResponse(SubmitStatus.ALREADY_VOTED);
			}

			if (DateTime.now().getHourOfDay() < VOTING_MIN.getHours()) {
				return new VoteResponse(SubmitStatus.TOO_EARLY);
			}

			_ratingDatabase.vote(voteReq.getMealId(), voteReq.getRating());
			_deviceDatabase.vote(voteReq.getDeviceId());

			return new VoteResponse(SubmitStatus.VALID);
		} catch (Exception _) {
			return new VoteResponse(SubmitStatus.ERROR);
		}
	}

	private static LocalDate getDateFromTimestamp(long timestamp) {
		if (timestamp < 0) {
			return LocalDate.now();
		}
		
		return new LocalDate(timestamp);
	}

	// OLD STUFF - DO NOT TOUCH

	private org.pocketcampus.plugin.food.server.old.OldFoodService _oldService;

	/**
	 * OBSOLETE.
	 * Gets the old version of this service, using lazy initialization
	 * to avoid initializing it during unit tests since it does stuff with databases.
	 */
	private org.pocketcampus.plugin.food.server.old.OldFoodService getOldService() {
		if (_oldService == null) {
			_oldService = new org.pocketcampus.plugin.food.server.old.OldFoodService();
		}
		return _oldService;
	}

	/**
	 * OBSOLETE. Gets all menus for today.
	 */
	@Override
	public List<Meal> getMeals() throws TException {
		return getOldService().getMeals();
	}

	/**
	 * OBSOLETE. Checks whether the user has already voted today
	 */
	public boolean hasVoted(String deviceId) throws TException {
		return getOldService().hasVoted(deviceId);
	}

	/**
	 * OBSOLETE. Gets all the Ratings for today's meals.
	 */
	@Override
	public Map<Long, Rating> getRatings() throws TException {
		return getOldService().getRatings();
	}

	/**
	 * OBSOLETE. Sets the Rating for a particular Meal.
	 */
	@Override
	public SubmitStatus setRating(long mealId, double rating, String deviceId) throws TException {
		return getOldService().setRating(mealId, rating, deviceId);
	}
}

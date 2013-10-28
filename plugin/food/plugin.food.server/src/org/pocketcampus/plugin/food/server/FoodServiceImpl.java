package org.pocketcampus.plugin.food.server;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.food.shared.*;

/**
 * Provides information about the meals, and allows users to rate them.
 */
public class FoodServiceImpl implements FoodService.Iface {
	private static final int TIMESTAMP_NOW = -1;
	private static final int VOTING_MIN_HOUR = 11;

	private final DeviceDatabase _deviceDatabase;
	private final RatingDatabase _ratingDatabase;
	private final MealList _mealList;

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
		Date date = new Date();
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
				_ratingDatabase.insert(menu);
			}
		} catch (Exception e) {
			menu = new ArrayList<EpflRestaurant>();
		}

		_ratingDatabase.setRatings(menu);

		return new FoodResponse(menu);
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

			if (getCurrentHour() <= VOTING_MIN_HOUR) {
				return new VoteResponse(SubmitStatus.TOO_EARLY);
			}

			_ratingDatabase.vote(voteReq.getMealId(), voteReq.getRating());
			_deviceDatabase.insert(voteReq.getDeviceId());

			return new VoteResponse(SubmitStatus.VALID);
		} catch (Exception _) {
			return new VoteResponse(SubmitStatus.ERROR);
		}
	}

	private static Date getDateFromTimestamp(long timestamp) {
		if (timestamp == TIMESTAMP_NOW) {
			return new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timestamp * 1000L);
		return c.getTime();
	}

	private static int getCurrentHour() {
		// and then people ask why I think Java is verbose...
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		return c.get(Calendar.HOUR_OF_DAY);
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

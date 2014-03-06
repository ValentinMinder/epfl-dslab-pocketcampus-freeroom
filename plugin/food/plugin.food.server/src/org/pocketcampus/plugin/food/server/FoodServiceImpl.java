package org.pocketcampus.plugin.food.server;

import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.launcher.server.PocketCampusServer;
import org.pocketcampus.platform.sdk.server.CachingProxy;
import org.pocketcampus.platform.sdk.server.HttpClientImpl;
import org.pocketcampus.plugin.food.shared.*;

import org.apache.thrift.TException;
import org.joda.time.*;

/**
 * Provides information about the meals, and allows users to rate them.
 */
public class FoodServiceImpl implements FoodService.Iface {
	private static final Hours VOTING_MIN = Hours.hours(11);
	private static final Duration MENU_CACHE_DURATION = Duration.standardHours(1);
	private static final Duration PICTURES_CACHE_DURATION = Duration.standardDays(1);
	private static final Duration LOCATIONS_CACHE_DURATION = Duration.standardDays(1);

	private final DeviceDatabase _deviceDatabase;
	private final RatingDatabase _ratingDatabase;
	private final Menu _menu;
	private final PictureSource _pictureSource;
	private final RestaurantLocator _locator;

	public FoodServiceImpl(DeviceDatabase deviceDatabase, RatingDatabase ratingDatabase, Menu menu,
			PictureSource pictureSource, RestaurantLocator locator) {
		_deviceDatabase = deviceDatabase;
		_ratingDatabase = ratingDatabase;
		_menu = menu;
		_pictureSource = pictureSource;
		_locator = locator;
	}

	public FoodServiceImpl() {
		this(new DeviceDatabaseImpl(), new RatingDatabaseImpl(),
				CachingProxy.create(new MenuImpl(new HttpClientImpl()), MENU_CACHE_DURATION, true),
				CachingProxy.create(new PictureSourceImpl(), PICTURES_CACHE_DURATION, false),
				CachingProxy.create(new RestaurantLocatorImpl(), LOCATIONS_CACHE_DURATION, false));
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

		FoodResponse response = null;
		try {
			response = _menu.get(time, date);
		} catch (Exception e) {
			throw new TException("An exception occurred while getting the menu", e);
		}
		_ratingDatabase.insertMenu(response.getMenu());
		_ratingDatabase.setRatings(response.getMenu());

		for (EpflRestaurant restaurant : response.getMenu()) {
			restaurant.setRPictureUrl(_pictureSource.forRestaurant(restaurant.getRName()));
			restaurant.setRLocation(_locator.findByName(restaurant.getRName()));
		}
	
		String sciper = PocketCampusServer.authGetUserSciper(foodReq);
		if(sciper != null) {
			List<String> userClasses = PocketCampusServer.ldapGetUserClassesFromSciper(sciper);
			response.setUserStatus(getPriceTarget(userClasses));
		}

		return response.setMealTypePictureUrls(_pictureSource.getMealTypePictures());
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

	private static PriceTarget getPriceTarget(List<String> userClasses) {
		if(userClasses.contains("Voie Diplôme"))
			return PriceTarget.STUDENT;
		if(userClasses.contains("Doctorant"))
			return PriceTarget.PHD_STUDENT;
		if(userClasses.size() > 0)
			return PriceTarget.STAFF;
		return PriceTarget.VISITOR;
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

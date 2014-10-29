package org.pocketcampus.plugin.food.server;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.server.CachingProxy;
import org.pocketcampus.platform.server.HttpClientImpl;
import org.pocketcampus.plugin.authentication.server.AuthenticationServiceImpl;
import org.pocketcampus.plugin.food.shared.*;

import org.apache.thrift.TException;
import org.joda.time.*;

import com.google.gson.JsonParseException;
import com.unboundid.ldap.sdk.*;

/**
 * Provides information about the meals, and allows users to rate them.
 */
public class FoodServiceImpl implements FoodService.Iface {
	private static final Days PAST_VOTE_MAX_DAYS = Days.days(5);
	private static final Duration MENU_CACHE_DURATION = Duration.standardHours(1);
	private static final Duration PICTURES_CACHE_DURATION = Duration.standardDays(1);
	private static final Duration LOCATIONS_CACHE_DURATION = Duration.standardDays(1);

	private final RatingDatabase _ratingDatabase;
	private final Menu _menu;
	private final PictureSource _pictureSource;
	private final RestaurantLocator _locator;

	public FoodServiceImpl(RatingDatabase ratingDatabase, Menu menu,
			PictureSource pictureSource, RestaurantLocator locator) {
		_ratingDatabase = ratingDatabase;
		_menu = menu;
		_pictureSource = pictureSource;
		_locator = locator;
	}

	public FoodServiceImpl() {
		this(new RatingDatabaseImpl(PAST_VOTE_MAX_DAYS),
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
		} catch (JsonParseException e) {
			throw new TException("An exception occurred while getting the menu", e);
		}
		try {
			_ratingDatabase.insertMenu(response.getMenu(), date, time);
			_ratingDatabase.setRatings(response.getMenu());
		} catch (SQLException e) {
			throw new TException("An exception occurred while inserting and fetching the ratings", e);
		}

		for (EpflRestaurant restaurant : response.getMenu()) {
			restaurant.setRPictureUrl(_pictureSource.forRestaurant(restaurant.getRName()));
			restaurant.setRLocation(_locator.findByName(restaurant.getRName()));
		}

		String gaspar = (foodReq.isSetUserGaspar() ? foodReq.getUserGaspar() : AuthenticationServiceImpl.authGetUserGaspar());
		System.out.println("Getting PriceTarget for " + gaspar);
		response.setUserStatus(getPriceTarget(gaspar));

		return response.setMealTypePictureUrls(_pictureSource.getMealTypePictures());
	}

	@Override
	public VoteResponse vote(VoteRequest voteReq) throws TException {
		try {
			if (voteReq.getRating() < 0 || voteReq.getRating() > 5) {
				throw new TException("Invalid rating.");
			}

			return new VoteResponse( _ratingDatabase.vote(voteReq.getDeviceId(), voteReq.getMealId(), voteReq.getRating()));
		} catch (SQLException e) {
			throw new TException("An error occurred during a vote", e);
		}
	}

	private static LocalDate getDateFromTimestamp(long timestamp) {
		if (timestamp < 0) {
			return LocalDate.now();
		}

		return new LocalDate(timestamp);
	}

	// TODO extract this to a common LDAP service used everytime we need it, not just in food
	private static PriceTarget getPriceTarget(String username) {
		if(username == null)
			return null;
		List<PriceTarget> classes = new LinkedList<PriceTarget>();
		try {
			LDAPConnection ldap = new LDAPConnection();
			ldap.connect("ldap.epfl.ch", 389);
			SearchResult searchResult = ldap.search("o=epfl,c=ch", SearchScope.SUB, DereferencePolicy.FINDING, 10, 0, false, "(|(uid=" + username + "@*)(uniqueidentifier=" + username + "))", (String[]) null);
			for (SearchResultEntry e : searchResult.getSearchEntries()) {
				String os = e.getAttributeValue("organizationalStatus");
				if ("Etudiant".equals(os)) {
					String uc = e.getAttributeValue("userClass");
					if ("Doctorant".equals(uc)) {
						classes.add(PriceTarget.PHD_STUDENT);
					} else {
						classes.add(PriceTarget.STUDENT);
					}
				} else if ("Personnel".equals(os)) {
					classes.add(PriceTarget.STAFF);
				} else { // Hôte, etc.
					//classes.add(PriceTarget.VISITOR);
					// It seems if the person has _any_ entry in LDAP, they are considered staff...
					classes.add(PriceTarget.STAFF);
				}
			}
		} catch (LDAPException e) {
			e.printStackTrace();
		}
		if (classes.contains(PriceTarget.STUDENT))
			return PriceTarget.STUDENT;
		if (classes.contains(PriceTarget.PHD_STUDENT))
			return PriceTarget.PHD_STUDENT;
		if (classes.contains(PriceTarget.STAFF))
			return PriceTarget.STAFF;
		return PriceTarget.VISITOR;
	}

	// OLD STUFF

	/**
	 * OBSOLETE. Gets all menus for today.
	 */
	@Override
	public List<Meal> getMeals() throws TException {
		FoodResponse resp = getFood(new FoodRequest());
		if (resp.getStatusCode() == FoodStatusCode.OK) {
			List<Meal> mealList = new LinkedList<Meal>();
			for (EpflRestaurant resto : resp.getMenu()) {
				for (EpflMeal meal : resto.getRMeals()) {
					mealList.add(new Meal(meal.getMId(), meal.getMName(), meal
							.getMDescription(), new Restaurant(resto.getRId(),
							resto.getRName()), new Rating(meal.getMRating()
							.getRatingValue() * 5, meal.getMRating()
							.getVoteCount(), meal.getMRating().getRatingValue()
							* 5 * meal.getMRating().getVoteCount())));
				}
			}
			return mealList;
		}
		throw new TException("getFood returned status "
				+ resp.getStatusCode().getValue());
	}

	/**
	 * OBSOLETE. Gets all the Ratings for today's meals.
	 */
	@Override
	public Map<Long, Rating> getRatings() throws TException {
		FoodResponse resp = getFood(new FoodRequest());
		if (resp.getStatusCode() == FoodStatusCode.OK) {
			Map<Long, Rating> ratings = new HashMap<Long, Rating>();
			for (EpflRestaurant resto : resp.getMenu()) {
				for (EpflMeal meal : resto.getRMeals()) {
					ratings.put(meal.getMId(), new Rating(meal.getMRating()
							.getRatingValue() * 5, meal.getMRating()
							.getVoteCount(), meal.getMRating().getRatingValue()
							* 5 * meal.getMRating().getVoteCount()));
				}
			}
			return ratings;
		}
		throw new TException("getFood returned status "
				+ resp.getStatusCode().getValue());
	}

	/**
	 * OBSOLETE. Sets the Rating for a particular Meal.
	 */
	@Override
	public SubmitStatus setRating(long mealId, double rating, String deviceId) throws TException {
		return vote(new VoteRequest(mealId, rating / 5.0, deviceId)).getSubmitStatus();
	}
}

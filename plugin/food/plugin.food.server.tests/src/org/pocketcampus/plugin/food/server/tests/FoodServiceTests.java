package org.pocketcampus.plugin.food.server.tests;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.pocketcampus.platform.server.Authenticator;
import org.pocketcampus.plugin.food.server.*;
import org.pocketcampus.plugin.food.shared.*;
import org.pocketcampus.plugin.map.shared.MapItem;
import org.joda.time.*;

/**
 * Tests for FoodServiceImpl.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class FoodServiceTests {
	// getFood returns the menu returned by MealList, with picture
	@Test
	public void getFoodWorks() throws Exception {
		Menu menu = getTestMenu();
		PictureSource source = getTestPictureSource();
		RestaurantLocator locator = getTestRestaurantLocator();
		FoodServiceImpl service = new FoodServiceImpl(getTestRatingDatabase(), menu, source, locator, getTestAuthenticator(), null);

		FoodResponse response = service.getFood(new FoodRequest());

		List<EpflRestaurant> restaurants = menu.get(MealTime.LUNCH, LocalDate.now()).getMenu();
		for (EpflRestaurant restaurant : restaurants) {
			restaurant.setRPictureUrl(source.forRestaurant(restaurant.getRName()));
			restaurant.setRLocation(locator.findByName(restaurant.getRName()));
		}

		assertEquals(restaurants, response.getMenu());
	}

	// getFood sets the ratings on the meals
	@Test
	public void mealRatingsAreSet() throws Exception {
		RatingDatabase ratingDatabase = getTestRatingDatabase();
		FoodServiceImpl service = new FoodServiceImpl(ratingDatabase, getTestMenu(),
				getTestPictureSource(), getTestRestaurantLocator(), getTestAuthenticator(), null);
		ratingDatabase.vote("A", 3, 3);
		ratingDatabase.vote("B", 12, 2);
		ratingDatabase.vote("C", 12, 5);

		FoodResponse response = service.getFood(new FoodRequest());
		List<EpflRestaurant> menu = response.getMenu();

		EpflRating r1 = menu.get(0).getRMeals().get(2).getMRating();
		assertEquals(1, r1.getVoteCount());
		assertEquals(3, r1.getRatingValue(), Double.MIN_VALUE);
		EpflRating r2 = menu.get(1).getRMeals().get(1).getMRating();
		assertEquals(2, r2.getVoteCount());
		assertEquals(3.5, r2.getRatingValue(), Double.MIN_VALUE);
	}

	// getFood sets the ratings on the restaurants
	@Test
	public void restaurantRatingsAreSet() throws Exception {
		RatingDatabase ratingDatabase = getTestRatingDatabase();
		FoodServiceImpl service = new FoodServiceImpl(ratingDatabase, getTestMenu(),
				getTestPictureSource(), getTestRestaurantLocator(), getTestAuthenticator(), null);
		ratingDatabase.vote("A", 3, 3);
		ratingDatabase.vote("B", 11, 2);
		ratingDatabase.vote("C", 12, 5);

		FoodResponse response = service.getFood(new FoodRequest());
		List<EpflRestaurant> menu = response.getMenu();

		EpflRating r1 = menu.get(0).getRRating();
		assertEquals(1, r1.getVoteCount());
		assertEquals(3, r1.getRatingValue(), Double.MIN_VALUE);
		EpflRating r2 = menu.get(1).getRRating();
		assertEquals(2, r2.getVoteCount());
		assertEquals(3.5, r2.getRatingValue(), Double.MIN_VALUE);
	}

	// meal pictures are set in the reply
	@Test
	public void mealPicturesAreSet() throws Exception {
		PictureSource source = getTestPictureSource();
		FoodServiceImpl service = new FoodServiceImpl(getTestRatingDatabase(), getTestMenu(),
				source, getTestRestaurantLocator(), getTestAuthenticator(), null);

		FoodResponse response = service.getFood(new FoodRequest());

		assertEquals(source.getMealTypePictures(), response.getMealTypePictureUrls());
	}

	// voting works
	@Test
	public void voteWorks() throws Exception {
		FoodServiceImpl service = new FoodServiceImpl(getTestRatingDatabase(), getTestMenu(),
				getTestPictureSource(), getTestRestaurantLocator(), getTestAuthenticator(), null);

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 29, 11, 00).getMillis());
		VoteResponse response = service.vote(new VoteRequest(11, 4.0, "12345"));

		assertEquals(SubmitStatus.VALID, response.getSubmitStatus());
	}

	// voting adds an entry into the ratings database
	@Test
	public void voteUsesRatingsDatabase() throws Exception {
		RatingDatabase ratingDatabase = getTestRatingDatabase();
		Menu mealList = getTestMenu();
		FoodServiceImpl service = new FoodServiceImpl(ratingDatabase, mealList,
				getTestPictureSource(), getTestRestaurantLocator(), getTestAuthenticator(), null);

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 29, 12, 30).getMillis());
		service.vote(new VoteRequest(11, 4.0, "12345"));
		List<EpflRestaurant> menu = mealList.get(MealTime.LUNCH, LocalDate.now()).getMenu();
		ratingDatabase.setRatings(menu);

		assertEquals(new EpflRating(4.0, 1), menu.get(1).getRMeals().get(0).getMRating());
	}

	// voting twice returns ALREADY_VOTED
	// FIXME: This has to be the responsibility of the database... problem...
	@Test
	@Ignore
	public void voteTwiceReturnsAlreadyVoted() throws Exception {
		FoodServiceImpl service = new FoodServiceImpl(getTestRatingDatabase(), getTestMenu(),
				getTestPictureSource(), getTestRestaurantLocator(), getTestAuthenticator(), null);

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 29, 12, 30).getMillis());
		service.vote(new VoteRequest(11, 4.0, "12345"));
		VoteResponse response = service.vote(new VoteRequest(12, 2.0, "12345"));

		assertEquals(SubmitStatus.ALREADY_VOTED, response.getSubmitStatus());
	}

	// voting before 11am returns TOO_EARLY
	// FIXME: This has to be the responsibility of the DB to get the meal time... problem...
	@Test
	@Ignore
	public void voteBefore11isTooEarly() throws Exception {
		FoodServiceImpl service = new FoodServiceImpl(getTestRatingDatabase(), getTestMenu(),
				getTestPictureSource(), getTestRestaurantLocator(), getTestAuthenticator(), null);

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 29, 10, 59).getMillis());
		VoteResponse response = service.vote(new VoteRequest(11, 4.0, "12345"));

		assertEquals(SubmitStatus.TOO_EARLY, response.getSubmitStatus());
	}

	// voting twice (different device ID) on the same day
	@Test
	public void voteWithDifferentIdOnSameDayWorks() throws Exception {
		FoodServiceImpl service = new FoodServiceImpl(getTestRatingDatabase(), getTestMenu(),
				getTestPictureSource(), getTestRestaurantLocator(), getTestAuthenticator(), null);

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 29, 12, 30).getMillis());
		service.vote(new VoteRequest(11, 4.0, "12345"));
		VoteResponse response = service.vote(new VoteRequest(11, 4.0, "67890"));

		assertEquals(SubmitStatus.VALID, response.getSubmitStatus());
	}

	private static RatingDatabase getTestRatingDatabase() {
		final Map<Long, EpflRating> RATINGS = new HashMap<Long, EpflRating>();

		return new RatingDatabase() {
			@Override
			public void insertMenu(List<EpflRestaurant> menu, LocalDate date, MealTime time) {
				// nothing
			}

			@Override
			public SubmitStatus vote(String deviceId, long mealId, double rating) {
				if (RATINGS.containsKey(mealId)) {
					EpflRating r = RATINGS.get(mealId);
					int voteCount = r.getVoteCount() + 1;
					RATINGS.put(mealId, new EpflRating((r.getRatingValue() * r.getVoteCount() + rating) / voteCount, voteCount));
				} else {
					RATINGS.put(mealId, new EpflRating(rating, 1));
				}
				return SubmitStatus.VALID;
			}

			@Override
			public void setRatings(List<EpflRestaurant> menu) {
				for (EpflRestaurant restaurant : menu) {
					double totalValue = 0.0;
					int totalCount = 0;
					for (EpflMeal meal : restaurant.getRMeals()) {
						if (RATINGS.containsKey(meal.getMId())) {
							EpflRating rating = RATINGS.get(meal.getMId());
							totalValue += rating.getRatingValue();
							totalCount += rating.getVoteCount();
							meal.setMRating(rating);
						} else {
							meal.setMRating(new EpflRating(0.0, 0));
						}
					}

					restaurant.setRRating(new EpflRating(totalCount == 0 ? 0.0 : totalValue / totalCount, totalCount));
				}
			}
		};
	}

	private static Menu getTestMenu() {
		return new Menu() {
			@Override
			public FoodResponse get(MealTime time, LocalDate date) {
				return new FoodResponse().setStatusCode(FoodStatusCode.OK).setMenu(Arrays.asList(new EpflRestaurant[] {
						new EpflRestaurant(100, "R100", Arrays.asList(new EpflMeal[] {
								makeMeal(1),
								makeMeal(2),
								makeMeal(3)
						}), new EpflRating(0.0, 0)),
						new EpflRestaurant(200, "R200", Arrays.asList(new EpflMeal[] {
								makeMeal(11),
								makeMeal(12)
						}), new EpflRating(0.0, 0)),
						new EpflRestaurant(300, "R300", Arrays.asList(new EpflMeal[] {
								makeMeal(21)
						}), new EpflRating(0.0, 0))
				}));
			}
		};
	}

	private static EpflMeal makeMeal(long id) {
		return new EpflMeal(id, "M" + id, "D" + id, new HashMap<PriceTarget, Double>(), Arrays.asList(new MealType[0]), null).setMRating(new EpflRating(0.0, 0));
	}

	private static PictureSource getTestPictureSource() {
		final Map<MealType, String> mealTypePictures = new HashMap<MealType, String>();
		return new PictureSource() {
			@Override
			public Map<MealType, String> getMealTypePictures() {
				return mealTypePictures;
			}

			@Override
			public String forRestaurant(String restaurantName) {
				return "PICTURE_" + restaurantName;
			}
		};
	}

	private static RestaurantLocator getTestRestaurantLocator() {
		return new RestaurantLocator() {
			@Override
			public MapItem findByName(String restaurantName) {
				return new MapItem(restaurantName, 0, 0, 0, 0);
			}
		};
	}

	private static Authenticator getTestAuthenticator() {
		return new Authenticator() {
			@Override
			public String getSciper() {
				return null;
			}

			@Override
			public String getGaspar() {
				return null;
			}

		};
	}
}
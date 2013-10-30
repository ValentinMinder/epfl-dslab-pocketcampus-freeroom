package org.pocketcampus.plugin.food.server.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.pocketcampus.plugin.food.server.*;
import org.pocketcampus.plugin.food.shared.*;

import org.joda.time.*;

/** 
 * Tests for FoodServiceImpl.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class FoodServiceTests {
	// getFood returns the menu returned by MealList
	@Test
	public void getFoodWorks() throws Exception {
		MealList mealList = getTestMealList();
		FoodServiceImpl service = new FoodServiceImpl(getTestDeviceDatabase(), getTestRatingDatabase(), getTestMealList());

		FoodResponse response = service.getFood(new FoodRequest("fr", MealTime.LUNCH, 0));

		assertEquals(mealList.getMenu(MealTime.LUNCH, LocalDate.now()).menu, response.getMatchingFood());
	}

	// getFood sets the ratings on the meals
	@Test
	public void mealRatingsAreSet() throws Exception {
		RatingDatabase ratingDatabase = getTestRatingDatabase();
		FoodServiceImpl service = new FoodServiceImpl(getTestDeviceDatabase(), ratingDatabase, getTestMealList());
		ratingDatabase.vote(3, 3);
		ratingDatabase.vote(12, 2);
		ratingDatabase.vote(12, 5);

		FoodResponse response = service.getFood(new FoodRequest("fr", MealTime.LUNCH, 0));
		List<EpflRestaurant> menu = response.getMatchingFood();

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
		FoodServiceImpl service = new FoodServiceImpl(getTestDeviceDatabase(), ratingDatabase, getTestMealList());
		ratingDatabase.vote(3, 3);
		ratingDatabase.vote(11, 2);
		ratingDatabase.vote(12, 5);

		FoodResponse response = service.getFood(new FoodRequest("fr", MealTime.LUNCH, 0));
		List<EpflRestaurant> menu = response.getMatchingFood();

		EpflRating r1 = menu.get(0).getRRating();
		assertEquals(1, r1.getVoteCount());
		assertEquals(3, r1.getRatingValue(), Double.MIN_VALUE);
		EpflRating r2 = menu.get(1).getRRating();
		assertEquals(2, r2.getVoteCount());
		assertEquals(3.5, r2.getRatingValue(), Double.MIN_VALUE);
	}

	// voting works
	@Test
	public void voteWorks() throws Exception {
		FoodServiceImpl service = new FoodServiceImpl(getTestDeviceDatabase(), getTestRatingDatabase(), getTestMealList());

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 29, 11, 00).getMillis());
		VoteResponse response = service.vote(new VoteRequest(11, 4.0, "12345"));

		assertEquals(SubmitStatus.VALID, response.getSubmitStatus());
	}

	// voting adds an entry into the ratings database
	@Test
	public void voteUsesRatingsDatabase() throws Exception {
		RatingDatabase ratingDatabase = getTestRatingDatabase();
		MealList mealList = getTestMealList();
		FoodServiceImpl service = new FoodServiceImpl(getTestDeviceDatabase(), ratingDatabase, mealList);

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 29, 12, 30).getMillis());
		service.vote(new VoteRequest(11, 4.0, "12345"));
		List<EpflRestaurant> menu = mealList.getMenu(MealTime.LUNCH, LocalDate.now()).menu;
		ratingDatabase.setRatings(menu);

		assertEquals(new EpflRating(4.0, 1), menu.get(1).getRMeals().get(0).getMRating());
	}

	// voting adds an entry into the device database
	@Test
	public void voteUsesDeviceDatabase() throws Exception {
		DeviceDatabase deviceDatabase = getTestDeviceDatabase();
		FoodServiceImpl service = new FoodServiceImpl(deviceDatabase, getTestRatingDatabase(), getTestMealList());

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 29, 12, 30).getMillis());
		service.vote(new VoteRequest(11, 4.0, "12345"));

		assertTrue(deviceDatabase.hasVotedToday("12345"));
	}

	// voting twice returns ALREADY_VOTED
	@Test
	public void voteTwiceReturnsAlreadyVoted() throws Exception {
		FoodServiceImpl service = new FoodServiceImpl(getTestDeviceDatabase(), getTestRatingDatabase(), getTestMealList());

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 29, 12, 30).getMillis());
		service.vote(new VoteRequest(11, 4.0, "12345"));
		VoteResponse response = service.vote(new VoteRequest(12, 2.0, "12345"));

		assertEquals(SubmitStatus.ALREADY_VOTED, response.getSubmitStatus());
	}

	// voting before 11am returns TOO_EARLY
	@Test
	public void voteBefore11isTooEarly() throws Exception {
		FoodServiceImpl service = new FoodServiceImpl(getTestDeviceDatabase(), getTestRatingDatabase(), getTestMealList());

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 29, 10, 59).getMillis());
		VoteResponse response = service.vote(new VoteRequest(11, 4.0, "12345"));

		assertEquals(SubmitStatus.TOO_EARLY, response.getSubmitStatus());
	}
	
	// voting twice (different device ID) on the same day
	@Test
	public void voteWithDifferentIdOnSameDayWorks() throws Exception {
		FoodServiceImpl service = new FoodServiceImpl(getTestDeviceDatabase(), getTestRatingDatabase(), getTestMealList());

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 29, 12, 30).getMillis());
		service.vote(new VoteRequest(11, 4.0, "12345"));
		VoteResponse response = service.vote(new VoteRequest(11, 4.0, "67890"));

		assertEquals(SubmitStatus.VALID, response.getSubmitStatus());
	}
	
	private static DeviceDatabase getTestDeviceDatabase() {
		final Set<String> HAVE_VOTED = new HashSet<String>();

		return new DeviceDatabase() {
			@Override
			public void vote(String deviceId) {
				HAVE_VOTED.add(deviceId);
			}

			@Override
			public boolean hasVotedToday(String deviceId) {
				return HAVE_VOTED.contains(deviceId);
			}
		};
	}

	private static RatingDatabase getTestRatingDatabase() {
		final Map<Long, EpflRating> RATINGS = new HashMap<Long, EpflRating>();

		return new RatingDatabase() {
			@Override
			public void insert(List<EpflRestaurant> menu) {
				// nothing
			}

			@Override
			public void vote(long mealId, double rating) {
				if (RATINGS.containsKey(mealId)) {
					EpflRating r = RATINGS.get(mealId);
					int voteCount = r.getVoteCount() + 1;
					RATINGS.put(mealId, new EpflRating((r.getRatingValue() * r.getVoteCount() + rating) / voteCount, voteCount));
				} else {
					RATINGS.put(mealId, new EpflRating(rating, 1));
				}
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

	private static MealList getTestMealList() {
		return new MealList() {
			@Override
			public MenuResult getMenu(MealTime time, LocalDate date) throws Exception {
				return new MenuResult(true, Arrays.asList(new EpflRestaurant[] {
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
}
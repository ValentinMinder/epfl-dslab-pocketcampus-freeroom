package org.pocketcampus.plugin.food.server.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pocketcampus.plugin.food.server.*;
import org.pocketcampus.plugin.food.server.MealList.MenuResult;
import org.pocketcampus.plugin.food.shared.*;

// TODO: Add time-sensitive tests.
// Adding a time service with DI could work but it's a bit cumbersome;
// could we take a dependency on Joda Time, which is 1000x better than Java's date API anyway?
// It allows us to override the current time.

// double-brace init is used for convenience, which can cause warnings
@SuppressWarnings("serial")
public class FoodServiceTests {
	// getFood returns the menu returned by MealList
	@Test
	public void getFoodWorks() throws Exception {
		MealList mealList = getTestMealList();
		FoodServiceImpl service = new FoodServiceImpl(getTestDeviceDatabase(), getTestRatingDatabase(), getTestMealList());

		FoodResponse response = service.getFood(new FoodRequest("fr", MealTime.LUNCH, 0));

		assertEquals(mealList.getMenu(MealTime.LUNCH, new Date(0)).menu, response.getMatchingFood());
	}
	
	// getFood sets the ratings on the meals
	@Test
	public void mealRatingsAreSet() throws Exception {
		RatingDatabase ratingDatabase = getTestRatingDatabase();
		FoodServiceImpl service = new FoodServiceImpl(getTestDeviceDatabase(), ratingDatabase, getTestMealList());
		ratingDatabase.vote(3, 3);
		ratingDatabase.vote(12,2);
		ratingDatabase.vote(12,5);
		
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
		ratingDatabase.vote(11,2);
		ratingDatabase.vote(12,5);
		
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
		
	    VoteResponse response = service.vote(new VoteRequest(11, 4.0, "12345"));
	    
	    assertEquals(SubmitStatus.VALID, response.getSubmitStatus());
	}
	
	// voting adds an entry into the ratings database
	@Test
	public void voteUsesRatingsDatabase() throws Exception {
		RatingDatabase ratingDatabase = getTestRatingDatabase();
		MealList mealList = getTestMealList();
		FoodServiceImpl service = new FoodServiceImpl(getTestDeviceDatabase(), ratingDatabase, mealList);
		
		service.vote(new VoteRequest(11, 4.0, "12345"));
		List<EpflRestaurant> menu = mealList.getMenu(MealTime.LUNCH, new Date()).menu;
		ratingDatabase.setRatings(menu);
		
		assertEquals(new EpflRating(4.0, 1), menu.get(1).getRMeals().get(0).getMRating());
	}
	
	// voting adds an entry into the device database
	@Test
	public void voteUsesDeviceDatabase() throws Exception {
		DeviceDatabase deviceDatabase = getTestDeviceDatabase();
		FoodServiceImpl service = new FoodServiceImpl(deviceDatabase, getTestRatingDatabase(), getTestMealList());
		
		service.vote(new VoteRequest(11, 4.0, "12345"));
		
		assertTrue(deviceDatabase.hasVotedToday("12345"));
	}
	
	// voting twice returns ALREADY_VOTED
	@Test
	public void voteTwiceReturnsAlreadyVoted() throws Exception {
		FoodServiceImpl service = new FoodServiceImpl(getTestDeviceDatabase(), getTestRatingDatabase(), getTestMealList());
		
		service.vote(new VoteRequest(11, 4.0, "12345"));
		VoteResponse response = service.vote(new VoteRequest(12, 2.0, "12345"));
		
		assertEquals(SubmitStatus.ALREADY_VOTED, response.getSubmitStatus());
	}
	
	
	private static DeviceDatabase getTestDeviceDatabase() {
		final Set<String> HAVE_VOTED = new HashSet<String>();

		return new DeviceDatabase() {
			@Override
			public void insert(String deviceId) {
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
		final MenuResult RETURN_VALUE = new MenuResult(true, new ArrayList<EpflRestaurant>() {{
				add(new EpflRestaurant(100, "R100", new ArrayList<EpflMeal>() {{
						add(makeMeal(1));
						add(makeMeal(2));
						add(makeMeal(3));
				}}, new EpflRating(0.0, 0)));
				add(new EpflRestaurant(200, "R200", new ArrayList<EpflMeal>() {{
						add(makeMeal(11));
						add(makeMeal(12));
				}}, new EpflRating(0.0, 0)));
				add(new EpflRestaurant(300, "R300", new ArrayList<EpflMeal>() {{
						add(makeMeal(21));
				}}, new EpflRating(0.0, 0)));
		}});

		return new MealList() {
			@Override
			public MenuResult getMenu(MealTime time, Date date) throws Exception {
				return RETURN_VALUE;
			}
		};
	}

	private static EpflMeal makeMeal(long id) {
		return new EpflMeal(id, "M" + id, "D" + id, new HashMap<PriceTarget, Double>(), new ArrayList<MealType>(), null).setMRating(new EpflRating(0.0, 0));
	}
}
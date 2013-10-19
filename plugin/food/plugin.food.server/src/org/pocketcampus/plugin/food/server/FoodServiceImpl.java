package org.pocketcampus.plugin.food.server;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.food.shared.*;

/**
 * Provides information about the meals, and allows users to rate them.
 */
public class FoodServiceImpl implements FoodService.Iface {
	public FoodServiceImpl() {

	}

	@Override
	public FoodResponse getFood(FoodRequest foodReq) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VoteResponse vote(VoteRequest voteReq) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	// OLD STUFF - DO NOT TOUCH

	private final org.pocketcampus.plugin.food.server.old.OldFoodService oldService = new org.pocketcampus.plugin.food.server.old.OldFoodService();

	/**
	 * OBSOLETE. Get all menus for today.
	 */
	@Override
	public List<Meal> getMeals() throws TException {
		return oldService.getMeals();
	}

	/**
	 * OBSOLETE. Checks whether the user has already voted today
	 */
	public boolean hasVoted(String deviceId) throws TException {
		return oldService.hasVoted(deviceId);
	}

	/**
	 * OBSOLETE. Get all the Ratings for today's meals.
	 */
	@Override
	public Map<Long, Rating> getRatings() throws TException {
		return oldService.getRatings();
	}

	/**
	 * OBSOLETE. Sets the Rating for a particular Meal.
	 */
	@Override
	public SubmitStatus setRating(long mealId, double rating, String deviceId)
			throws TException {
		return oldService.setRating(mealId, rating, deviceId);
	}
}

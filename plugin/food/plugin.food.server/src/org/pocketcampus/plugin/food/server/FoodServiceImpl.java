package org.pocketcampus.plugin.food.server;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.food.shared.FoodService;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;
import org.pocketcampus.plugin.food.shared.Restaurant;

public class FoodServiceImpl implements FoodService.Iface{

	@Override
	public List<Meal> getMeals() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Restaurant> getRestaurants() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rating getRating(Meal meal) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, Rating> getRatings() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRating(Rating rating) throws TException {
		// TODO Auto-generated method stub
		
	}

}

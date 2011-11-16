package org.pocketcampus.plugin.food.android.iface;

import java.util.List;

import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.shared.Meal;

public interface IFoodController {
	
	public void getRestaurantsList();
	public void getMeals();
	public void getSandwiches();
	public void getHasVoted();
	
	public List<MealTag> getMealTags();
	
	public void setRating(double rating, Meal meal);
}

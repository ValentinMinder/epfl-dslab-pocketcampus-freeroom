package org.pocketcampus.plugin.food.android.iface;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Restaurant;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

public interface IFoodModel {

	public void setRestaurantsList(List<Restaurant> list);
	public List<Restaurant> getRestaurantsList();
	
	public void setMeals(List<Meal> list);
	public List<Meal> getMeals();
	public HashMap<String, Vector<Meal>> getMealsByRestaurants();
	public List<MealTag> getMealTags();
	
	public void setRating(SubmitStatus status);
	
}

package org.pocketcampus.plugin.food.android.iface;

import java.util.List;

import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.shared.Restaurant;

public interface IFoodModel {

	public List<Restaurant> getRestaurantsList();
	public List<MealTag> getMealTags();
	
}

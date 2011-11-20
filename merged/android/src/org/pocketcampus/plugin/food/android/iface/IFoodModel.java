package org.pocketcampus.plugin.food.android.iface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;
import org.pocketcampus.plugin.food.shared.Restaurant;
import org.pocketcampus.plugin.food.shared.Sandwich;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

import android.content.Context;

/**
 * The interface that defines the public methods of FoodModel
 * 
 * @author Elodie (elodienilane.triponez@epfl.ch)
 * @author Oriane (oriane.rodriguez@epfl.ch)
 * 
 */
public interface IFoodModel {

	/**
	 * Gets the list of all Restaurants proposing menus
	 */
	public List<Restaurant> getRestaurantsList();

	/**
	 * Sets the list of all Restaurants proposing menus
	 * 
	 * @param list
	 *            the new list of restaurants
	 */
	public void setRestaurantsList(List<Restaurant> list);

	/**
	 * Returns the list of all Meals
	 */
	public List<Meal> getMeals();

	/**
	 * Sets the list of all meals
	 * 
	 * @param list
	 *            the new list of meals
	 */
	public void setMeals(List<Meal> list);

	/**
	 * Returns the list of Meals sorted by Restaurant
	 * 
	 * @param ctx
	 *            the context of the calling view, to get the preferences
	 */
	public HashMap<String, Vector<Meal>> getMealsByRestaurants(Context ctx);

	/**
	 * Returns the list of Meals sorted by Ratings
	 */
	public List<Meal> getMealsByRatings();

	/**
	 * Returns the list of all meal tags
	 */
	public List<MealTag> getMealTags();

	/**
	 * Gets the preferred restaurants as defined by the user
	 * 
	 * @param mealMap
	 *            the list of meals to filter
	 */
	public HashMap<String, Vector<Meal>> filterPreferredRestaurants(
			HashMap<String, Vector<Meal>> mealMap);

	/**
	 * Set the <code>Rating</code> for a particular <code>Meal</code> and notify
	 * the listeners
	 */
	public void setRating(SubmitStatus status);

	/**
	 * Set the <code>Ratings</code> for all meals <code>Meal</code> and notify
	 * the listeners
	 */
	public void setRatings(Map<Integer, Rating> map);

	/**
	 * Returns whether the user has already voted or not
	 */
	public boolean getHasVoted();

	/**
	 * Changes the value for the current state of the hasVoted boolean, which
	 * represents whether the user has voted yet
	 */
	public void setHasVoted(boolean hasVoted);

	/**
	 * Returns the list of Sandwiches sorted by Restaurant
	 */
	public HashMap<String, Vector<Sandwich>> getSandwiches();

	/**
	 * Update the list of Sandwiches and notify the View
	 * 
	 * @param list
	 *            the new list of sandwiches
	 */
	public void setSandwiches(List<Sandwich> list);
}
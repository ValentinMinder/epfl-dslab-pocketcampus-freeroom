package org.pocketcampus.plugin.food.android.iface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;
import org.pocketcampus.plugin.food.shared.Restaurant;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

import android.content.Context;

/**
 * The interface that defines the public methods of FoodModel
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public interface IFoodModel {

	/**
	 * Gets the list of all Restaurants that currently have menus available.
	 */
	public List<Restaurant> getRestaurantsList();

	/**
	 * Sets the list of all Restaurants that currently have menus available.
	 * 
	 * @param list
	 *            the new list of restaurants.
	 */
	public void setRestaurantsList(List<Restaurant> list);

	/**
	 * Returns the list of all Meals
	 */
	public List<Meal> getMeals();

	/**
	 * Sets the list of all meals.
	 * 
	 * @param list
	 *            the new list of meals.
	 */
	public void setMeals(List<Meal> list, Context ctx);

	/**
	 * Returns the list of Meals sorted by Restaurant.
	 * 
	 * @param context
	 *            the context of the calling view, to get the preferences.
	 */
	public HashMap<String, Vector<Meal>> getMealsByRestaurants(Context context);

	/**
	 * Returns the list of Meals sorted by Ratings.
	 */
	public List<Meal> getMealsByRatings();

	/**
	 * Returns the list of all meal tags.
	 */
	public List<MealTag> getMealTags();

	/**
	 * Gets the preferred restaurants as defined by the user.
	 * 
	 * @param mealMap
	 *            the list of meals to filter.
	 */
	public HashMap<String, Vector<Meal>> filterPreferredRestaurants(
			HashMap<String, Vector<Meal>> mealMap);

	/**
	 * Set the <code>Rating</code> for a particular <code>Meal</code> and notify
	 * the listeners.
	 */
	public void setRating(SubmitStatus status);

	/**
	 * Set the <code>Ratings</code> for all meals <code>Meal</code> and notify
	 * the listeners.
	 */
	public void setRatings(Map<Long, Rating> map);

	/**
	 * Returns whether the user has already voted or not.
	 */
	public boolean getHasVoted();

	/**
	 * Changes the value for the current state of the hasVoted boolean, which
	 * represents whether the user has voted yet.
	 */
	public void setHasVoted(boolean hasVoted);

	/**
	 * Called when an error has happened in a server request, and a particular
	 * message wants to be displayed.
	 */
	public void networkErrorHappened(String message);

}
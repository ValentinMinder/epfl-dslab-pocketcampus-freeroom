package org.pocketcampus.plugin.food.android.iface;

import java.util.List;

import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.shared.Meal;

/**
 * The interface that defines the public methods of FoodController.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public interface IFoodController {

	/**
	 * Initiates a request to the server to get the restaurants whose menus are
	 * going to be displayed.
	 */
	public void getRestaurants();

	/**
	 * Initiates a request to the server to get the meals of all restaurants.
	 */
	public void getMeals();

	/**
	 * Returns all the tags to be used for filtering the menus for suggestions.
	 */
	public List<MealTag> getMealTags();

	/**
	 * Initiates a request to the server to get the ratings for all meals.
	 * 
	 * @param groupPosition
	 *            the position of the currently selected group.
	 */
	public void getRatings();

	/**
	 * Uploads a user rating to the server using a <code>SetRatingRequest</code>.
	 * 
	 * @param rating
	 *            the rating to be uploaded.
	 * @param meal
	 *            the meal for which this rating was submitted.
	 */
	public void setRating(double rating, Meal meal);

	/**
	 * Initiates a request to the server to check whether the user has already
	 * voted for a meal today.
	 */
	public void getHasVoted();

}

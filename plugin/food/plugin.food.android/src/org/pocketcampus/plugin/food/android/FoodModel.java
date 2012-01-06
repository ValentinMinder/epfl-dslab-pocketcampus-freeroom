package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.food.android.iface.IFoodMainView;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.android.utils.FileCache;
import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.android.utils.MenuSorter;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;
import org.pocketcampus.plugin.food.shared.Restaurant;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * The Model of the food plugin, used to handle the information that is going to
 * be displayed in the views
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class FoodModel extends PluginModel implements IFoodModel {
	/** The listeners for the state of the view */
	IFoodMainView mListeners = (IFoodMainView) getListeners();
	/** The list of restaurants to display */
	private List<Restaurant> mRestaurantsList;
	/** The list of all meals for a day */
	private List<Meal> mMeals;
	// /** The list of all sandwiches */
	// private List<Sandwich> mSandwiches;
	/** Whether the user has already used his ability to vote */
	private boolean mHasVoted = false;

	/** The cache for the meals list */
	private FileCache mMealsCache;

	/** Object used to access and modify preferences on the phone */
	private SharedPreferences mRestoPrefs;
	/** The name of the preferences file on the phone */
	private static final String RESTO_PREFS_NAME = "RestoPrefs";

	/** Class used to sort the menus by restaurants/ratings */
	private MenuSorter mSorter;

	/**
	 * Returns the interface of the linked view
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IFoodMainView.class;
	}

	/**
	 * Gets the list of all Restaurants proposing menus
	 */
	@Override
	public List<Restaurant> getRestaurantsList() {
		return this.mRestaurantsList;
	}

	/**
	 * Sets the list of all Restaurants proposing menus
	 * 
	 * @param list
	 *            the new list of restaurants
	 */
	@Override
	public void setRestaurantsList(List<Restaurant> list) {
		if (mListeners != null) {
			this.mRestaurantsList = list;
			// Notify the view
			this.mListeners.restaurantsUpdated();
		}
	}

	/**
	 * Returns the list of all Meals
	 */
	@Override
	public List<Meal> getMeals() {
		return this.mMeals;
	}

	/**
	 * Sets the list of all meals. If the list if not null nor empty, it stores
	 * it to the cache
	 * 
	 * @param list
	 *            the new list of meals
	 */
	@Override
	public void setMeals(List<Meal> list, Context ctx) {
		this.mMeals = list;
		if (mMealsCache == null) {
			mMealsCache = new FileCache(ctx);
		}
		if (list != null && !list.isEmpty()) {
			mMealsCache.writeToFile(mMeals);
		}
		// Notify the view(s)
		this.mListeners.menusUpdated();
	}

	/**
	 * Called when an error happens while trying to contact the server.
	 */
	@Override
	public void networkErrorHappened(String message) {
		this.mListeners.networkErrorHappened(message);
		this.mListeners.menusUpdated();
	}

	/**
	 * Returns the list of Meals sorted by Restaurant. If the list is currently
	 * null, it tries to restore the meals list from the cache.
	 * 
	 * @param ctx
	 *            the context of the calling view, to get the preferences
	 */
	@Override
	public HashMap<String, Vector<Meal>> getMealsByRestaurants(Context ctx) {
		mRestoPrefs = ctx.getSharedPreferences(RESTO_PREFS_NAME, 0);

		if (mSorter == null) {
			mSorter = new MenuSorter();
		}
		if (mMeals == null || mMeals.isEmpty()) {
			if (mMealsCache == null) {
				mMealsCache = new FileCache(ctx);
			}
			mMeals = mMealsCache.readFromFile();
			if (mMeals != null && !mMeals.isEmpty()) {
				Toast.makeText(ctx,
						ctx.getString(R.string.food_displaying_from_cache),
						Toast.LENGTH_SHORT).show();
			}
		}
		if (mMeals != null) {
			HashMap<String, Vector<Meal>> allMeals = mSorter
					.sortByRestaurant(mMeals);
			if (mRestoPrefs.getAll().isEmpty()) {
				return allMeals;
			} else {
				return filterPreferredRestaurants(allMeals);
			}

		} else
			return new HashMap<String, Vector<Meal>>();
	}

	/**
	 * Returns the list of Meals sorted by Ratings.
	 */
	@Override
	public List<Meal> getMealsByRatings() {
		if (mSorter == null) {
			mSorter = new MenuSorter();
		}

		if (mMeals != null) {
			if (mRestoPrefs.getAll().isEmpty()) {
				return mSorter.sortByRatings(mMeals);
			} else {
				return mSorter.sortByRatings(filterPreferredRestaurants());
			}
		} else {
			return new ArrayList<Meal>();
		}
	}

	/**
	 * Returns the list of all meal tags.
	 */
	@Override
	public List<MealTag> getMealTags() {
		List<MealTag> tags = new ArrayList<MealTag>();
		MealTag[] arrayTags = MealTag.values();
		for (int i = 0; i < arrayTags.length; i++) {
			tags.add(arrayTags[i]);
		}
		return tags;
	}

	/**
	 * Gets the preferred restaurants as defined by the user.
	 * 
	 * @param mealMap
	 *            the hashMap of meals to filter
	 */
	public HashMap<String, Vector<Meal>> filterPreferredRestaurants(
			HashMap<String, Vector<Meal>> mealMap) {
		Set<String> set = mealMap.keySet();
		HashMap<String, Vector<Meal>> toDisplay = new HashMap<String, Vector<Meal>>();
		for (String r : set) {
			if (mRestoPrefs.getBoolean(r, false)) {
				toDisplay.put(r, mealMap.get(r));
			}
		}
		return toDisplay;
	}

	/**
	 * Gets the meals from all preferred restaurants as defined by the user.
	 * 
	 */
	public List<Meal> filterPreferredRestaurants() {
		List<Meal> filteredMeal = new ArrayList<Meal>();
		for (Meal m : mMeals) {
			if (mRestoPrefs.getBoolean(m.getRestaurant().getName(), false)) {
				filteredMeal.add(m);
			}
		}
		return filteredMeal;
	}

	/**
	 * Set the <code>Rating</code> for a particular <code>Meal</code> and notify
	 * the listeners.
	 */
	@Override
	public void setRating(SubmitStatus status) {
		// Notify the view(s)
		mListeners.ratingSubmitted(status);
	}

	/**
	 * Set the <code>Ratings</code> for all meals <code>Meal</code> and notify
	 * the listeners.
	 */
	@Override
	public void setRatings(Map<Long, Rating> result) {
		if (mMeals != null && !mMeals.isEmpty()) {
			for (Meal m : mMeals) {
				m.setRating(result.get(m.getMealId()));
			}
		}
		mListeners.ratingsUpdated();
	}

	/**
	 * Returns whether the user has already voted or not.
	 */
	@Override
	public boolean getHasVoted() {
		return this.mHasVoted;
	}

	/**
	 * Changes the value for the current state of the hasVoted boolean, which
	 * represents whether the user has voted yet.
	 */
	@Override
	public void setHasVoted(boolean hasVoted) {
		this.mHasVoted = hasVoted;
	}

}

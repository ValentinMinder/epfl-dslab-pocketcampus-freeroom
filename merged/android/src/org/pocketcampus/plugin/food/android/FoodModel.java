package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.food.android.iface.IFoodMainView;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.android.utils.MenuSorter;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;
import org.pocketcampus.plugin.food.shared.Restaurant;
import org.pocketcampus.plugin.food.shared.Sandwich;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

public class FoodModel extends PluginModel implements IFoodModel {
	IFoodMainView mListeners = (IFoodMainView) getListeners();
	private List<Restaurant> mRestaurantsList;
	private List<Meal> mMeals;
	private List<Sandwich> mSandwiches;
	private boolean mHasVoted;
	
	// private HashMap<Meal, Rating> mCampusMeals;
	private MenuSorter mSorter;

	@Override
	protected Class<? extends IView> getViewInterface() {
		return IFoodMainView.class;
	}

	/**
	 * Sets the list of all Restaurants proposing menus
	 * */
	@Override
	public void setRestaurantsList(List<Restaurant> list) {
		if (mListeners != null) {
			this.mRestaurantsList = list;
			// Notifiy the view
			this.mListeners.restaurantsUpdated();
		}
	}

	/**
	 * Gets the list of all Restaurants proposing menus
	 */
	@Override
	public List<Restaurant> getRestaurantsList() {
		return this.mRestaurantsList;
	}

	@Override
	public void setMeals(List<Meal> list) {
		this.mMeals = list;
		// Notify the view(s)
		this.mListeners.menusUpdated();
	}

	/**
	 * Returns the list of Meals
	 */
	@Override
	public List<Meal> getMeals() {
		return this.mMeals;
	}

	/**
	 * Returns the list of Meals sorted by Restaurant
	 */
	@Override
	public HashMap<String, Vector<Meal>> getMealsByRestaurants(){
		if(mSorter == null){
			mSorter = new MenuSorter();
		}
		return mSorter.sortByRestaurant(mMeals);
	}

	/**
	 * Returns the list of Meals sorted by Ratings
	 */
	@Override
	public List<Meal> getMealsByRatings() {
		if(mSorter == null) {
			mSorter = new MenuSorter();
		}
		return mSorter.sortByRatings(mMeals);
	}

	/**
	 * Returns the list of Sandwiches sorted by Restaurant
	 */
	@Override
	public HashMap<String, Vector<Sandwich>> getSandwichesByRestaurants() {
		if (mSorter == null) {
			mSorter = new MenuSorter();
		}
		if(mSandwiches == null) {
			mSandwiches = new ArrayList<Sandwich>();
		}
		return mSorter.sortByCafeterias(mSandwiches);
	}
	
	/**
	 * Returns the list of all meal tags
	 * */
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
	 * set the Rating for a particular Meal and notify the listeners
	 * */
	@Override
	public void setRating(SubmitStatus status) {
		//Notify the view(s)
		mListeners.ratingsUpdated(status);
	}

	/**
	 * Update the list of Sandwiches and update the View
	 */
	@Override
	public void setSandwiches(List<Sandwich> list) {
		mSandwiches = list;
		//Notify the view(s)
		mListeners.sandwichesUpdated();
	}

	@Override
	public void setHasVoted(boolean hasVoted) {
		this.mHasVoted = hasVoted;
	}

	@Override
	public boolean getHasVoted() {
		return this.mHasVoted;
	}

	@Override
	public void setRatings(Map<Integer, Rating> result) {
		if(mMeals != null && !mMeals.isEmpty()) {
			for (Meal m : mMeals) {
				m.setRating(result.get(m.hashCode()));
			}
		}
		mListeners.ratingsUpdated();
	}

}

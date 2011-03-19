package org.pocketcampus.plugin.food;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.menu.FoodMenu;
import org.pocketcampus.plugin.food.menu.Meal;
import org.pocketcampus.plugin.food.menu.MenuSorter;

import android.app.Activity;
import android.content.Context;

/**
 * Handles what is shown in the food plugin: Restaurant, ratings, suggestions or
 * sandwiches, as well as what day is shown.
 * 
 * @author Elodie
 * 
 */
public class FoodDisplayHandler {

	private FoodListAdapter currentListAdapter_;
	private String dayLabel_;
	private FoodDisplayType currentDisplayType_;
	private MenuSorter sorter_;

	private FoodMenu campusMenu_;
	private Activity ownerActivity_;
	private Context activityContext_;

	public FoodDisplayHandler(Activity ownerActivity) {
		activityContext_ = ownerActivity.getApplicationContext();
		currentListAdapter_ = new FoodListAdapter(activityContext_);
		campusMenu_ = new FoodMenu();
		ownerActivity_ = ownerActivity;
		sorter_ = new MenuSorter();
		currentDisplayType_ = FoodDisplayType.Restaurants;
		
		updateView();
	}
	
	public boolean valid(){
		return !campusMenu_.isEmpty();
	}

	public FoodListAdapter getListAdapter() {
		return currentListAdapter_;
	}

	/**
	 * Change display type
	 * 
	 * @param displayType
	 *            the number corresponding to the wanted display (according to
	 *            options menu numbers)
	 */
	public void setDisplayType(int displayType) {
		if (displayType <= 4 && displayType >= 1) {
			switch (displayType) {
			case 1:
				currentDisplayType_ = FoodDisplayType.Restaurants;
				break;
			case 2:
				currentDisplayType_ = FoodDisplayType.Ratings;
				break;
			case 3:
				currentDisplayType_ = FoodDisplayType.Sandwiches;
				break;
			case 4:
				currentDisplayType_ = FoodDisplayType.Suggestions;
				break;
			}
		}
		updateView();
	}

	public void updateView() {
		currentListAdapter_.removeSections();
		switch (currentDisplayType_) {
		case Restaurants:
			showMenusByRestaurants();
			break;
		case Ratings:
			showMenusByRatings();
			break;
		case Sandwiches:

		case Suggestions:

		}
	}

	public String getDayLabel() {
		return null;
	}

	/**
	 * Get the adapter to show the menus sorted by restaurants.
	 */
	public void showMenusByRestaurants() {
		// Sort meals by restaurant.
		HashMap<String, Vector<Meal>> mealHashMap = sorter_
				.sortByRestaurant(campusMenu_.getKeySet());
		FoodListSection menuListSection;

		/**
		 * Iterate over the different restaurant menus
		 */
		if (!campusMenu_.isEmpty()) {
			// Get the set of keys from the hash map to make sections.
			Set<String> restaurantFullMenu = mealHashMap.keySet();
			for (String restaurantName : restaurantFullMenu) {
				// For each restaurant, make a list of its meals to add in its
				// section
				menuListSection = new FoodListSection(
						mealHashMap.get(restaurantName), ownerActivity_);
				currentListAdapter_.addSection(restaurantName, menuListSection);
			}
		}
	}

	/**
	 * Show the menus according to their ratings. Better rated first.
	 */
	public void showMenusByRatings() {
		// Sort meals by ratings.
		if (campusMenu_ != null) {
			FoodListSection menuListSection;
			/**
			 * Iterate over the different restaurant menus
			 */

			if (!campusMenu_.isEmpty()) {
				Vector<Meal> mealVector = sorter_.sortByRatings(campusMenu_);
				// Get the set of keys from the hash map to make sections.
				menuListSection = new FoodListSection(mealVector,
						ownerActivity_);
				currentListAdapter_
						.addSection(
								activityContext_.getResources().getString(
										R.string.food_show_ratings),
								menuListSection);

			}
		}
	}

	public static enum FoodDisplayType {
		Restaurants(1), Ratings(2), Sandwiches(3), Suggestions(4);

		/** Attribute with the value associated with the enum */
		private final int value;

		/** Constructor which associates value with enum */
		private FoodDisplayType(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	};
}

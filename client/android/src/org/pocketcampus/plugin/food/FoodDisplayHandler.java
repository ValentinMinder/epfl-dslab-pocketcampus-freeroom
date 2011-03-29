package org.pocketcampus.plugin.food;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.menu.FoodMenu;
import org.pocketcampus.plugin.food.menu.Meal;
import org.pocketcampus.plugin.food.menu.MenuSorter;
import org.pocketcampus.plugin.food.menu.Rating;
import org.pocketcampus.plugin.food.menu.Sandwich;
import org.pocketcampus.plugin.food.menu.StarRating;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Handles what is shown in the food plugin: Restaurant, ratings, suggestions or
 * sandwiches, as well as what day is shown.
 * 
 * @author Elodie
 * 
 */
public class FoodDisplayHandler {

	private FoodListAdapter currentListAdapter_;
	// private String dayLabel_;
	private FoodDisplayType currentDisplayType_;
	private MenuSorter sorter_;

	private SandwichListStore sandwichListStore_;

	private FoodMenu campusMenu_;
	private HashMap<Meal, Rating> suggestionsMenu_;
	private Vector<Vector<Sandwich>> campusSandwich_;

	private Activity ownerActivity_;
	private Context activityContext_;

	public FoodDisplayHandler(Activity ownerActivity) {
		activityContext_ = ownerActivity.getApplicationContext();
		currentListAdapter_ = new FoodListAdapter(activityContext_);
		campusMenu_ = new FoodMenu(activityContext_);
		suggestionsMenu_ = new HashMap<Meal, Rating>();
		campusSandwich_ = new Vector<Vector<Sandwich>>();
		ownerActivity_ = ownerActivity;
		sorter_ = new MenuSorter();
		currentDisplayType_ = FoodDisplayType.Restaurants;

		updateView();
	}

	public boolean valid() {
		return !campusMenu_.isEmpty();
	}

	public void refreshMenu() {
		if(campusMenu_.isEmpty()){
			campusMenu_.loadCampusMenu();
			//TODO: also if it's yesterday's menu.
		} else {
			//Refresh only ratings.
		}
		updateView();
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
			showSandwiches();
			break;
		case Suggestions:
			showMenusBySuggestions();
			break;
		}
	}

	public String getDayLabel() {
		return null;
	}

	/**
	 * Get A List Of Menus
	 * 
	 * @return mealsList The list of menus (not anymore a HashMap)
	 * */
	public ArrayList<Meal> getMenusList() {
		ArrayList<Meal> mealsList = new ArrayList<Meal>();
		if (campusMenu_ != null && !campusMenu_.isEmpty()) {
			Vector<Meal> mealsVector = sorter_.sortByRatings(campusMenu_);
			for (Meal m : mealsVector) {
				mealsList.add(m);
			}
		}
		if (mealsList != null) {
			Toast.makeText(ownerActivity_, "Au moins je reçois les menus !",
					Toast.LENGTH_SHORT);
		}
		return mealsList;
	}

	/**
	 * Update Suggestions
	 * 
	 * @param suggestedMenus
	 *            the menus returned by the Suggestions Class
	 */
	public void updateSuggestions(ArrayList<Meal> suggestedMenus) {
		if (suggestedMenus != null) {
			HashMap<Meal, Rating> menus = new HashMap<Meal, Rating>();
			Toast.makeText(activityContext_, "Ya des menus suggested !",
					Toast.LENGTH_SHORT);
			for (Meal m : suggestedMenus) {
				Toast.makeText(activityContext_, m.getName(),
						Toast.LENGTH_SHORT);
				menus.put(m, new Rating(StarRating.STAR_1_0, 0));
			}
			this.suggestionsMenu_ = menus;
		} else {
			Toast.makeText(activityContext_, "Ya pas de menus suggested !",
					Toast.LENGTH_SHORT);
		}
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
				menuListSection = new FoodListSection(mealHashMap
						.get(restaurantName), ownerActivity_);
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
						.addSection(activityContext_.getResources().getString(
								R.string.food_show_ratings), menuListSection);

			}
		}
	}

	/**
	 * Show sandwich view
	 * 
	 * @author nicolas.tran@epfl.ch
	 * @throws ServerException
	 */
	public void showSandwiches() {
		sandwichListStore_ = new SandwichListStore();
		campusSandwich_ = sandwichListStore_.getStoreList();

		if (campusSandwich_ != null) {
			SandwichListSection sandwichListSection;

			if (!campusSandwich_.isEmpty()) {
				for (Vector<Sandwich> v : campusSandwich_) {
					sandwichListSection = new SandwichListSection(v,
							ownerActivity_, activityContext_);
					currentListAdapter_.addSection(v.get(0).getRestaurant(),
							sandwichListSection);
				}

			}
		}
	}

	public void showMenusBySuggestions() {

		FoodListSection menuListSection;

		/**
		 * Iterate over the different restaurant menus
		 */
		if (!suggestionsMenu_.isEmpty()) {

			HashMap<String, Vector<Meal>> mealHashMap = sorter_
					.sortByRestaurant(suggestionsMenu_.keySet());

			if (mealHashMap != null) {

				// Get the set of keys from the hash map to make sections.
				Set<String> restaurantFullMenu = mealHashMap.keySet();
				for (String restaurantName : restaurantFullMenu) {
					// For each restaurant, make a list of its meals to add in
					// its
					// section
					menuListSection = new FoodListSection(mealHashMap
							.get(restaurantName), ownerActivity_);
					currentListAdapter_.addSection(restaurantName,
							menuListSection);
				}
			}
		} else {
			Toast.makeText(
					activityContext_,
					activityContext_.getResources().getString(
							R.string.food_suggestions_nomeal_nosuggestion),
					Toast.LENGTH_LONG).show();
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

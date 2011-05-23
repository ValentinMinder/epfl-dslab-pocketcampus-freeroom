package org.pocketcampus.plugin.food;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.menu.FoodMenu;
import org.pocketcampus.plugin.food.menu.MenuSorter;
import org.pocketcampus.plugin.food.sandwiches.SandwichList;
import org.pocketcampus.plugin.food.sandwiches.SandwichListAdapter;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;
import org.pocketcampus.shared.plugin.food.Sandwich;

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

	private FoodDisplayType currentDisplayType_;

	private FoodListAdapter currentListAdapter_;
	private MenuSorter sorter_;

	private FoodMenu campusMenu_;
	private SandwichList sandwichList_;
	private HashMap<Meal, Rating> suggestionsMenu_;
	HashMap<String, Vector<Sandwich>> campusSandwich_;

	private FoodPlugin ownerActivity_;
	private Context activityContext_;

	public FoodDisplayHandler(FoodPlugin ownerActivity) {
		ownerActivity_ = ownerActivity;
		activityContext_ = ownerActivity.getApplicationContext();

		currentListAdapter_ = new FoodListAdapter(activityContext_);
		currentDisplayType_ = FoodDisplayType.Restaurants;

		campusMenu_ = new FoodMenu(ownerActivity_);
		campusSandwich_ = new HashMap<String, Vector<Sandwich>>();
		sandwichList_ = new SandwichList(ownerActivity_);

		suggestionsMenu_ = new HashMap<Meal, Rating>();

		sorter_ = new MenuSorter();

		updateView();
	}

	/**
	 * Checks whether a valid menu is available.
	 * 
	 * @return
	 */
	public boolean validMenus() {
		return !campusMenu_.isEmpty();
	}

	public boolean validSuggestions() {
		return !suggestionsMenu_.isEmpty();
	}

	/**
	 * Checks whether a valid sandwich list is available.
	 * 
	 * @return
	 */
	public boolean validSandwich() {
		return !sandwichList_.isEmpty();
	}

	/**
	 * 
	 * @return the date the last menus were imported
	 */
	public Date getDateLastUpdatedMenus() {
		return campusMenu_.getValidityDate();
	}

	/**
	 * Change display type
	 * 
	 * @param displayType
	 *            the number corresponding to the wanted display (according to
	 *            options menu numbers)
	 */
	public void setCurrentDisplayType(int displayType) {
		switch (displayType) {
		case R.id.food_menu_restaurants:
			currentDisplayType_ = FoodDisplayType.Restaurants;
			break;
		case R.id.food_menu_sandwiches:
			currentDisplayType_ = FoodDisplayType.Sandwiches;
			break;
		case R.id.food_menu_suggestions:
			currentDisplayType_ = FoodDisplayType.Suggestions;
			break;
		case 125:
			currentDisplayType_ = FoodDisplayType.Ratings;
			break;
		}
		updateView();
	}

	public FoodDisplayType getCurrentDisplayType() {
		return currentDisplayType_;
	}

	public Meal getUpdatedMeal(int hashCode) {
		List<Meal> list = campusMenu_.getCampusMenu();
		for (Meal m : list) {
			if (m.hashCode() == hashCode) {
				return m;
			}
		}
		return null;
	}

	public void updateView() {
		currentListAdapter_.removeSections();
		switch (currentDisplayType_) {
		case Restaurants:
			Tracker.getInstance().trackPageView("food/restaurants");
			showMenusByRestaurants();
			break;
		case Ratings:
			Tracker.getInstance().trackPageView("food/ratings");
			showMenusByRatings();
			break;
		case Sandwiches:
			Tracker.getInstance().trackPageView("food/sandwiches");
			showSandwiches();
			break;
		case Suggestions:
			Tracker.getInstance().trackPageView("food/suggestions");
			showMenusBySuggestions();
			break;
		}
		ownerActivity_.refreshActionBar(currentDisplayType_);
	}

	public void refreshView() {
		switch (currentDisplayType_) {
		case Restaurants:
		case Ratings:
			campusMenu_.refreshMenu();
			break;
		case Sandwiches:
			sandwichList_.refreshSandwiches();
			break;
		case Suggestions:
			break;
		}
	}

	public void refreshRatings() {
		campusMenu_.loadRatings(false);
		ownerActivity_.notifyDataSetChanged();
	}

	public FoodListAdapter getListAdapter() {
		return currentListAdapter_;
	}

	/**
	 * Get A List Of Menus
	 * 
	 * @return mealsList The list of menus (not anymore a HashMap)
	 * */
	public ArrayList<Meal> getMenusList() {
		ArrayList<Meal> mealsList = new ArrayList<Meal>();
		if (campusMenu_ != null && !campusMenu_.isEmpty()) {
			Vector<Meal> mealsVector = sorter_.sortByRatings(campusMenu_
					.getCampusMenu());
			for (Meal m : mealsVector) {
				mealsList.add(m);
			}
		}
		return mealsList;
	}

	// /**
	// * Get the adapter to show the menus sorted by restaurants.
	// */
	// public void showMenusByRestaurants() {
	// // Sort meals by restaurant.
	// HashMap<String, Vector<Meal>> mealHashMap = sorter_
	// .sortByRestaurant(campusMenu_.getMeals());
	// FoodListSection menuListSection;
	//
	// /**
	// * Iterate over the different restaurant menus
	// */
	// if (!campusMenu_.isEmpty()) {
	// // Get the set of keys from the hash map to make sections.
	// Set<String> restaurantFullMenuSet = mealHashMap.keySet();
	//
	// SortedSet<String> restaurantFullMenu = new
	// TreeSet<String>(restaurantFullMenuSet);
	//
	// for (String restaurantName : restaurantFullMenu) {
	// // For each restaurant, make a list of its meals to add in its
	// // section
	// menuListSection = new FoodListSection(mealHashMap
	// .get(restaurantName), ownerActivity_);
	// currentListAdapter_.addSection(restaurantName, menuListSection);
	// }
	// }
	// }

	public void showMenusByRestaurants() {
		List<Meal> menusPrefered = campusMenu_.getCampusMenuPrefered();
		HashMap<String, Vector<Meal>> mealHashMap = sorter_
				.sortByRestaurant(menusPrefered);

		RestaurantListAdapter restaurantList = null;

		/**
		 * Iterate over the different restaurant menus
		 */
		if (!menusPrefered.isEmpty()) {
			Set<String> restaurantFullMenuSet = mealHashMap.keySet();

			SortedSet<String> restaurantFullMenu = new TreeSet<String>(
					restaurantFullMenuSet);

			Vector<String> restaurants = new Vector<String>(restaurantFullMenu);

			if (currentListAdapter_.isEmpty()) {
				restaurantList = new RestaurantListAdapter(restaurants,
						mealHashMap, ownerActivity_);
				currentListAdapter_.addSection(
						activityContext_.getString(R.string.food_restaurants),
						restaurantList);
			}
		}
	}

	/**
	 * Show the menus according to their ratings. Better rated first.
	 */
	public void showMenusByRatings() {
		List<Meal> menusPrefered = campusMenu_.getCampusMenuPrefered();
		// Sort meals by ratings.
		if (menusPrefered != null) {
			FoodListSection menuListSection;
			/**
			 * Iterate over the different restaurant menus
			 */

			if (!menusPrefered.isEmpty()) {
				Vector<Meal> mealVector = sorter_.sortByRatings(menusPrefered);
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

	/**
	 * Show suggestions view.
	 */
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
					// its section
					menuListSection = new FoodListSection(
							mealHashMap.get(restaurantName), ownerActivity_);
					currentListAdapter_.addSection(restaurantName,
							menuListSection);
				}
			}
		} else {
			Toast.makeText(
					activityContext_,
					activityContext_.getResources().getString(
							R.string.food_suggestions_nothing_found),
					Toast.LENGTH_LONG).show();
			setCurrentDisplayType(R.id.food_menu_restaurants);
			updateView();
		}
	}

	/**
	 * Update Suggestions
	 * 
	 * @param suggestedMenus
	 *            the menus returned by the Suggestions Class
	 */
	public void updateSuggestions(ArrayList<Meal> suggestedMenus) {
		List<Meal> menusPrefered = campusMenu_.getCampusMenuPrefered();
		if (suggestedMenus != null) {
			HashMap<Meal, Rating> menus = new HashMap<Meal, Rating>();
			for (Meal m : suggestedMenus) {
				if (menusPrefered.contains(m)) {
					menus.put(m, new Rating());
				}
			}
			this.suggestionsMenu_ = menus;
		}
	}

	/**
	 * Show sandwich view
	 * 
	 * @author nicolas.tran@epfl.ch
	 * 
	 */
	public void showSandwiches() {
		campusSandwich_ = sandwichList_.getStoreList();

		SandwichListAdapter sandwichList = null;

		if (campusSandwich_ != null) {
			/**
			 * Iterate over the different restaurant menus
			 */
			if (!campusSandwich_.isEmpty()) {
				// Get the set of keys from the hash map to make sections.
				Set<String> restaurantFullMenuSet = campusSandwich_.keySet();

				SortedSet<String> restaurantFullMenu = new TreeSet<String>(
						restaurantFullMenuSet);

				Vector<String> sandwiches = new Vector<String>(
						restaurantFullMenu);

				if (currentListAdapter_.isEmpty()) {
					sandwichList = new SandwichListAdapter(sandwiches,
							campusSandwich_, ownerActivity_);
					currentListAdapter_
							.addSection(activityContext_
									.getString(R.string.food_restaurants),
									sandwichList);
				}
			}
		}
	}

	public static enum FoodDisplayType {
		Restaurants(R.id.food_menu_restaurants), Ratings(125), Sandwiches(
				R.id.food_menu_sandwiches), Suggestions(
				R.id.food_menu_suggestions);

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

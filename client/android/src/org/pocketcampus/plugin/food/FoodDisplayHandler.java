package org.pocketcampus.plugin.food;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.plugin.food.menu.FoodMenu;
import org.pocketcampus.plugin.food.menu.MenuSorter;
import org.pocketcampus.plugin.food.sandwiches.Sandwich;
import org.pocketcampus.plugin.food.sandwiches.SandwichListSection;
import org.pocketcampus.plugin.food.sandwiches.SandwichListStore;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;
import org.pocketcampus.shared.plugin.food.StarRating;

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
	private SandwichListStore sandwichListStore_;
	private HashMap<Meal, Rating> suggestionsMenu_;
	private Vector<Vector<Sandwich>> campusSandwich_;

	private FoodPlugin ownerActivity_;
	private Context activityContext_;

	public FoodDisplayHandler(FoodPlugin ownerActivity, RequestHandler requestHandler_) {
		ownerActivity_ = ownerActivity;
		activityContext_ = ownerActivity.getApplicationContext();

		currentListAdapter_ = new FoodListAdapter(activityContext_);
		currentDisplayType_ = FoodDisplayType.Restaurants;

		campusMenu_ = new FoodMenu(ownerActivity_, requestHandler_);
		suggestionsMenu_ = new HashMap<Meal, Rating>();
		campusSandwich_ = new Vector<Vector<Sandwich>>();

		sorter_ = new MenuSorter();

		updateView();
	}

	public boolean valid() {
		return !campusMenu_.isEmpty();
	}
	
	public Date getDateLastUpdatedMenus(){
		return campusMenu_.getValidityDate();
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
	
	public void refreshView(){
		switch (currentDisplayType_) {
		case Restaurants:
		case Ratings:
			campusMenu_.refreshMenu();
			updateView();
			break;
		case Sandwiches:
			
			break;
		case Suggestions:
			
			break;
		}
	}

	public FoodListAdapter getListAdapter() {
		return currentListAdapter_;
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
		return mealsList;
	}

	/**
	 * Get the adapter to show the menus sorted by restaurants.
	 */
	public void showMenusByRestaurants() {
		// Sort meals by restaurant.
		HashMap<String, Vector<Meal>> mealHashMap = sorter_
				.sortByRestaurant(campusMenu_.getMeals());
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
					// For each restaurant, make a list of its meals to add in its section
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
							R.string.food_suggestions_nothing_found),
					Toast.LENGTH_LONG).show();
			setDisplayType(1);
		}
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
			for (Meal m : suggestedMenus) {
				menus.put(m, new Rating(StarRating.STAR_1_0, 0));
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

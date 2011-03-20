package org.pocketcampus.plugin.food;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.plugin.food.menu.FoodMenu;
import org.pocketcampus.plugin.food.menu.Meal;
import org.pocketcampus.plugin.food.menu.MenuSorter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
	private String dayLabel_;
	private FoodDisplayType currentDisplayType_;
	private MenuSorter sorter_;

	private FoodMenu campusMenu_;
	private FoodMenu suggestionsMenu_;
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

	public boolean valid() {
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
	
	public FoodMenu getMenus(){
		return this.campusMenu_;
	}
	
	public void updateSuggestedMenus(FoodMenu suggestedMenus){
		if(suggestedMenus != null){			
			this.suggestionsMenu_ = suggestedMenus;
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

	/**
	 * Show sandwich view
	 * 
	 * @author nicolas.tran@epfl.ch
	 * @throws ServerException
	 */
	public void showSandwiches() {

		/*progressDialog_ = ProgressDialog.show(this,
				getString(R.string.please_wait),
				getString(R.string.loading_menus), true, false);
		new Thread() {
			public void run() {
				SandwichListStore listStore;
				try {
					System.out.println("avant sandwichListStore()");
					listStore = new SandwichListStore();
					System.out.println("après sandwichListStore()");
					sandwichListAdapter_ = new SandwichListAdapter(
							DailyMenus.this, listStore.getStoreList());
				} catch (ServerException e) {
					System.out.println("erreur : " + e);
				}
				handler.sendEmptyMessage(1);
			}
		}.start();*/

	}

	public void showMenusBySuggestions() {
		/*
			int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
			HashMap<Meal, Rating> meals = MealCache.getInstance()
				.getMealsOfDay(day);
		 */
		
		if (suggestionsMenu_ != null) {
			FoodListSection menuListSection;

			if (!suggestionsMenu_.isEmpty()) {
				Vector<Meal> mealsVector = sorter_.sortByRatings(campusMenu_);
				
				ArrayList<Meal> mealsList = new ArrayList<Meal>();

				for (Meal meal : mealsVector) {
					mealsList.add(meal);
				}
				
				menuListSection = new FoodListSection(mealsVector,
						ownerActivity_);
				currentListAdapter_
						.addSection(
								activityContext_.getResources().getString(
										R.string.food_show_suggestions),
								menuListSection);
				
			} else {
				Toast.makeText(activityContext_,
						activityContext_.getResources().getString(R.string.food_suggestions_nomeal_nosuggestion),
						Toast.LENGTH_LONG).show();
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

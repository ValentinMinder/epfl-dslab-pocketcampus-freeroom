package org.pocketcampus.plugin.food;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

public class FoodDisplay extends PluginBase {
	/*
	 * DailyMenus dailyMenusActivity; public static Restaurants
	 * restaurantActivity;
	 */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.food_main);
		// restaurantActivity = this;

		// Fixed Portrait orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		// Initialize activity tabs.
		setTabs();
		
		// Handle additional requests passed on creation.
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			handleRequest(extras);
		}
	}
	
	class PreExistingViewFactory implements TabContentFactory {

		private final View preExisting;

		protected PreExistingViewFactory(View view) {
			preExisting = view;
		}

		public View createTabContent(String tag) {
			return preExisting;
		}

	}

	private void setTabs() {
		Resources res = getResources();

		TabHost tabs = (TabHost) findViewById(android.R.id.tabhost);
		tabs.setup();

		String ownerPackage = FoodClassesData.getOwnerPackage();
		String[] tabNames = res.getStringArray(R.array.food_tabNames);
		String[] tabActivities = FoodClassesData.getTabActivities();
		
		Intent intent = null;
		
		if (tabNames != null && tabActivities != null && tabNames.length > tabActivities.length) {
			TabSpec tspec = tabs.newTabSpec("Nothing");
			
			for (int i = 0; i < tabActivities.length; i++) {
				intent = new Intent().setClassName(ownerPackage,
						tabActivities[i]);
				tspec = tabs.newTabSpec(tabNames[i])
						.setIndicator(new FoodTabIndicator(this, tabNames[i]))
						.setContent(intent);
				tabs.addTab(tspec);
			}
		}
		
		/*TabHost tabHost = (TabHost) findViewById(R.id.food_tabhost);
		tabHost.setup();

		// Retrieve information about what classes are going to be put in the
		// tabs.
		String[] tabNames = res.getStringArray(R.array.food_tabNames);
		String ownerPackage = FoodClassesData.getOwnerPackage();
		String[] tabActivities = FoodClassesData.getTabActivities();

		if (tabNames != null && tabActivities != null) {
			TabSpec spec;
			Intent intent;

			for (int i = 0; i < tabActivities.length; i++) {
				intent = new Intent().setClassName(ownerPackage,
						tabActivities[i]);
				spec = tabHost.newTabSpec(tabNames[i])
						.setIndicator(new FoodTabIndicator(this, tabNames[i]))
						.setContent(intent);
				tabHost.addTab(spec);
			}

			tabHost.setCurrentTab(0);
		}*/
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		/*
		 * if (MealCache.isInstanciated()){ MealCache.getInstance().flush(); }
		 */
	}

	private void handleRequest(Bundle extras) {
		/*
		 * dailyMenusActivity = DailyMenus._dailyMenusActivity;
		 * 
		 * int todayWeekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		 * 
		 * if (extras.containsKey("MealTag")) { // show matching Meals of the
		 * day for this Tag MealTag tag = (MealTag)
		 * (extras.getSerializable("MealTag")); showDailyMealsFor(tag,
		 * todayWeekday); } else if (extras.containsKey("Restaurant")) { // show
		 * daily Meals for given Restaurant Restaurant resto = (Restaurant)
		 * (extras.getSerializable("Restaurant")); showDailyMealsFor(resto,
		 * todayWeekday); }
		 */
	}

	@Override
	public PluginInfo getPluginInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginPreference getPluginPreference() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * private void showDailyMealsFor(Restaurant resto, int weekday) { MealCache
	 * cache = MealCache.getInstance(); tabHost.setCurrentTab(0); // if
	 * (resto.getName().equalsIgnoreCase("Table de Vallotton")) { // resto = new
	 * Restaurant("Restaurant Table de Vallotton", resto.getPosition()); // }
	 * HashMap<Meal, Rating> dailyMeals = cache.getMealsFor(resto, weekday);
	 * ListSeparator menuListSeparator = new ListSeparator(dailyMenusActivity);
	 * HashMap<String, Vector<Meal>> dailyMealsVectorMap =
	 * SortingMeals.sortByRestaurant(dailyMeals.keySet());
	 * 
	 * dailyMenusActivity.setContentView(R.layout.restaurant_dailymenu_main);
	 * 
	 * for (Entry<String,Vector<Meal>> entry : dailyMealsVectorMap.entrySet()) {
	 * // For each restaurant, make a list of its meals to add in its // section
	 * if (entry.getValue().size() != 0) { dailyMenusActivity.empty.setText("");
	 * MenuListAdapter menuListAdapter = new MenuListAdapter(dailyMenusActivity,
	 * entry.getValue(), dailyMenusActivity);
	 * menuListSeparator.addSection(entry.getKey(), menuListAdapter); } else {
	 * dailyMenusActivity.empty.setText(R.string.resto_empty_list); }
	 * 
	 * } dailyMenusActivity.setListAdapter(menuListSeparator); String infoText =
	 * getString(R.string.resto_dailymenu_showrestomeals);
	 * dailyMenusActivity.moreInfo.setText(infoText+resto.getName());
	 * tabHost.setCurrentTab(0); }
	 */

	/*
	 * private void showDailyMealsFor(MealTag tag, int weekday) { /*MealTagger
	 * tagger = new MealTagger(); MealCache cache = MealCache.getInstance();
	 * HashMap<Meal, Rating> dailyMeals = cache.getMealsOfDay(weekday);
	 * ListSeparator menuListSeparator = new ListSeparator(dailyMenusActivity);
	 * HashMap<String, Vector<Meal>> restaurantFullMenu = new HashMap<String,
	 * Vector<Meal>>();
	 * 
	 * dailyMenusActivity.setContentView(R.layout.restaurant_dailymenu_main);
	 * 
	 * if (dailyMeals != null) { Collection<Meal> suggestedMeals = Suggestions
	 * .computeSuggestions(dailyMeals.keySet(), tag, tagger); restaurantFullMenu
	 * = SortingMeals .sortByRestaurant(suggestedMeals);
	 * 
	 * } for (String restaurantName : restaurantFullMenu.keySet()) { // For each
	 * restaurant, make a list of its meals to add in its // section
	 * Vector<Meal> meals = restaurantFullMenu.get(restaurantName); if
	 * (meals.size() != 0) { MenuListAdapter menuListAdapter = new
	 * MenuListAdapter( dailyMenusActivity, meals, dailyMenusActivity);
	 * menuListSeparator.addSection(restaurantName, menuListAdapter);
	 * dailyMenusActivity.empty.setText(""); } else {
	 * dailyMenusActivity.empty.setText(R.string.resto_empty_list); } }
	 * 
	 * 
	 * dailyMenusActivity.setListAdapter(menuListSeparator);
	 * dailyMenusActivity.moreInfo
	 * .setText(getString(R.string.resto_dailymenu_showtagmeals
	 * )+tag.toString()); tabHost.setCurrentTab(0); }
	 */

}

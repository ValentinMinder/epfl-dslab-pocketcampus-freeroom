package org.pocketcampus.plugin.menu;

import org.pocketcampus.core.plugin.DisplayBase;

import android.app.TabActivity;

public class MenuDisplay extends TabActivity implements DisplayBase {
/*
	private String ownerPackage_ = MenuClassesData.getInstance().getOwnerPackage();
	private String[] tabActivities = MenuClassesData.getInstance().getTabClasses();
	
	DailyMenus dailyMenusActivity;
	public static Restaurants restaurantActivity;

	
	String[] tabNames;
	TabHost tabHost;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		restaurantActivity = this;
		
		setTitle(R.string.Activity_Names_Restaurants);
		setContentView(R.layout.restaurant_main);

		// Fixed Portrait orientation
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		setTabs();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			handleRequest(extras);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if (MealCache.isInstanciated()){
			MealCache.getInstance().flush();
		}
	}

	/**
	 * Adds sections for daily and weekly menus to the tab layout, and the activities that will be called
	 * when pressed.
	 */
	/*private void setTabs(){
		Resources res = getResources(); // Resource object to get Drawables

		tabNames = res.getStringArray(R.array.resto_tabNames);
		tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		for (int i = 0; i < tabActivities.length; i++) {
			intent = new Intent().setClassName(ownerPackage, tabActivities[i]);
			// Initialize a TabSpec for each tab and add it to the TabHost
			spec = tabHost.newTabSpec(tabNames[i])
					.setIndicator(new FeatureTabIndicator(this, tabNames[i]))
					.setContent(intent);
			tabHost.addTab(spec);
		}

		tabHost.setCurrentTab(0);
	}

	private void handleRequest(Bundle extras) {
		dailyMenusActivity = DailyMenus._dailyMenusActivity;

		int todayWeekday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
//		todayWeekday=Calendar.MONDAY;
		
		if (extras.containsKey("MealTag")) {
			// show matching Meals of the day for this Tag
			MealTag tag = (MealTag) (extras.getSerializable("MealTag"));
			showDailyMealsFor(tag, todayWeekday);
		} else if (extras.containsKey("Restaurant")) {
			// show daily Meals for given Restaurant
			Restaurant resto = (Restaurant) (extras.getSerializable("Restaurant"));
			showDailyMealsFor(resto, todayWeekday);
		}
	}

	private void showDailyMealsFor(Restaurant resto, int weekday) {
		MealCache cache = MealCache.getInstance();
		tabHost.setCurrentTab(0);
//		if (resto.getName().equalsIgnoreCase("Table de Vallotton")) {
//			resto = new Restaurant("Restaurant Table de Vallotton", resto.getPosition());
//		}
		HashMap<Meal, Rating> dailyMeals = cache.getMealsFor(resto, weekday);
		ListSeparator menuListSeparator = new ListSeparator(dailyMenusActivity);
		HashMap<String, Vector<Meal>> dailyMealsVectorMap = SortingMeals.sortByRestaurant(dailyMeals.keySet());
		
		dailyMenusActivity.setContentView(R.layout.restaurant_dailymenu_main);
		
		for (Entry<String,Vector<Meal>> entry : dailyMealsVectorMap.entrySet()) {
			// For each restaurant, make a list of its meals to add in its
			// section
			if (entry.getValue().size() != 0) {
				dailyMenusActivity.empty.setText("");
				MenuListAdapter menuListAdapter = new MenuListAdapter(dailyMenusActivity, entry.getValue(), dailyMenusActivity);
				menuListSeparator.addSection(entry.getKey(), menuListAdapter);
			} else {
				dailyMenusActivity.empty.setText(R.string.resto_empty_list);
			}

		}
		dailyMenusActivity.setListAdapter(menuListSeparator);
		String infoText = getString(R.string.resto_dailymenu_showrestomeals);
		dailyMenusActivity.moreInfo.setText(infoText+resto.getName());
		tabHost.setCurrentTab(0);
	}


	private void showDailyMealsFor(MealTag tag, int weekday) {
		MealTagger tagger = new MealTagger();
		MealCache cache = MealCache.getInstance();
		HashMap<Meal, Rating> dailyMeals = cache.getMealsOfDay(weekday);
		ListSeparator menuListSeparator = new ListSeparator(dailyMenusActivity);
		HashMap<String, Vector<Meal>> restaurantFullMenu = new HashMap<String, Vector<Meal>>();
		
		dailyMenusActivity.setContentView(R.layout.restaurant_dailymenu_main);
		
		if (dailyMeals != null) {
			Collection<Meal> suggestedMeals = Suggestions
					.computeSuggestions(dailyMeals.keySet(), tag, tagger);
			restaurantFullMenu = SortingMeals
					.sortByRestaurant(suggestedMeals);

		}
		for (String restaurantName : restaurantFullMenu.keySet()) {
			// For each restaurant, make a list of its meals to add in its
			// section
			Vector<Meal> meals = restaurantFullMenu.get(restaurantName);
			if (meals.size() != 0) {
				MenuListAdapter menuListAdapter = new MenuListAdapter( dailyMenusActivity, meals, dailyMenusActivity);
				menuListSeparator.addSection(restaurantName, menuListAdapter);
				dailyMenusActivity.empty.setText("");
			} else {
				dailyMenusActivity.empty.setText(R.string.resto_empty_list);
			}
		}

		
		dailyMenusActivity.setListAdapter(menuListSeparator);
		dailyMenusActivity.moreInfo.setText(getString(R.string.resto_dailymenu_showtagmeals)+tag.toString());
		tabHost.setCurrentTab(0);
	}

	/**
	 * Sets the layout of the tab indicator.
	 * 
	 * @author Elodie
	 * 
	 */
	/*public static class FeatureTabIndicator extends LinearLayout {
		public FeatureTabIndicator(Context context, String label) {
			super(context);

			View tab = View.inflate(context, R.layout.feature_tab_indicator,
					this);

			TextView tv = (TextView) tab.findViewById(R.id.feature_tab_label);
			tv.setText(label);
		}
	}

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1337;


	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("HERE");
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			
			TakePicture.onActivityResult(requestCode, resultCode, data, DailyMenus.isMealPicture());
		}
	}*/
}


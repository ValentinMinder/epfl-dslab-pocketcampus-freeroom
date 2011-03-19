package org.pocketcampus.plugin.food;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class FoodPlugin extends PluginBase {
	private ListView l_;
	private FoodDisplayHandler foodDisplayHandler;

	private static boolean takingMealPicture;
	public static FoodPlugin foodPluginActivity;
	public TextView empty;

	/**
	 * Method called on activity creation
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.food_main);

		// Owner activity
		foodPluginActivity = this;

		// Header
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
		actionBar.setTitle("PocketCampus EPFL");
		actionBar.addAction(new ActionBar.IntentAction(this, MainscreenPlugin
				.createIntent(this), R.drawable.mini_home));

		//ListView
		l_ = (ListView) findViewById(R.id.food_list);
		empty = (TextView) findViewById(R.id.food_empty);

		//DisplayHandler
		foodDisplayHandler = new FoodDisplayHandler(this);

		// At first, display food by restaurant
		displayView();
	}

	public void displayView() {
		// List view
		FoodListAdapter fla = foodDisplayHandler.getListAdapter();
		if (foodDisplayHandler.valid() && fla != null) {
			l_.setAdapter(foodDisplayHandler.getListAdapter());
		} else {
			empty.setText(getString(R.string.food_empty));
		}
	}

	public static void setMealPicture(boolean isMealPicture) {
		takingMealPicture = isMealPicture;
	}

	public static boolean isMealPicture() {
		return takingMealPicture;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Resources res = getResources();
		menu.add(0, 1, 0, res.getText(R.string.food_show_restaurant)).setIcon(
				R.drawable.food_opt_icon_resto);
		menu.add(0, 2, 0, res.getText(R.string.food_show_ratings)).setIcon(
				R.drawable.food_opt_icon_ratings);
		menu.add(0, 3, 0, res.getText(R.string.food_show_sandwiches)).setIcon(
				R.drawable.food_opt_icon_sandwich);
		menu.add(0, 4, 0, res.getText(R.string.food_show_suggestions)).setIcon(
				R.drawable.food_opt_icon_suggestions);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int selectedId = item.getItemId();

		switch (selectedId) {
		case 1: // Show menus by restaurant
		case 2: // Show menus by rating
			//setContentView(R.layout.food_main);
			foodDisplayHandler.setDisplayType(selectedId);
			displayView();
			return true;
		case 3: // show sandwiches /*
			/*setContentView(R.layout.restaurant_dailymenu_main_4_sandwich);
			showSandwich();

			return true;*/
		case 4: // show suggestions
			/*chargeMenuEPFL(2);
			return true;*/
		}

		return false;
	}

	/**
	 * Show sandwich view
	 * 
	 * @author nicolas.tran@epfl.ch
	 * @throws ServerException
	 */
	public void showSandwich() {
		/*
		 * progressDialog_ = ProgressDialog.show(this,
		 * getString(R.string.please_wait), getString(R.string.loading_menus),
		 * true, false); new Thread() { public void run() { SandwichListStore
		 * listStore; try { System.out.println("avant sandwichListStore()");
		 * listStore = new SandwichListStore();
		 * System.out.println("après sandwichListStore()"); sandwichListAdapter_
		 * = new SandwichListAdapter(DailyMenus.this, listStore.getStoreList());
		 * } catch (ServerException e) { System.out.println("erreur : " + e);}
		 * handler.sendEmptyMessage(1); } }.start();
		 */
	}

	public void showSuggestions() {
		/*
		 * int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		 * HashMap<Meal,Rating> meals =
		 * MealCache.getInstance().getMealsOfDay(day); if(meals != null){
		 * Vector<Meal> mealsVector = sorter.sortByRatings(meals);
		 * ArrayList<Meal> mealsList = new ArrayList<Meal>();
		 * 
		 * for(Meal meal : mealsVector){ mealsList.add(meal); }
		 * 
		 * Intent suggestions = new Intent(this.getApplicationContext(),
		 * Suggestions.class); suggestions.putExtra("Meals", mealsList);
		 * startActivityForResult(suggestions, 1); } else { Toast.makeText(this,
		 * getString(R.string.resto_suggestions_nomeal_nosuggestion),
		 * Toast.LENGTH_LONG).show(); }
		 */
	}

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1337;

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		 * super.onActivityResult(requestCode, resultCode, data); if
		 * (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
		 * TakePicture.onActivityResult(requestCode, resultCode, data,
		 * takingMealPicture); }
		 * 
		 * switch(requestCode) {
		 * 
		 * // Result of the Suggestions case (1) : if (resultCode ==
		 * Activity.RESULT_OK) {
		 * 
		 * Bundle extras = data.getExtras(); if(extras != null){
		 * 
		 * ArrayList<Meal> list = (ArrayList<Meal>)extras.getSerializable(
		 * "org.pocketcampus.suggestions.meals");
		 * 
		 * 
		 * if(list != null && !list.isEmpty()){ ServerAPI s = new ServerAPI();
		 * HashMap<Meal, Rating> mealH = new HashMap<Meal, Rating>();
		 * 
		 * for(Meal m : list){ Rating rating = null; try { rating =
		 * s.getRating(m); } catch (ServerException e) { new
		 * ServerException("Rating null"); } if(rating!=null){ mealH.put(m,
		 * rating); } }
		 * 
		 * HashMap<String, Vector<Meal>> mealHashMap =
		 * MenuSorter.sortByRestaurant(mealH.keySet());
		 * menuListSeparator.removeSections();
		 * setContentView(R.layout.restaurant_dailymenu_main);
		 * 
		 * /** Iterate over the different restaurant menus
		 */
		/*
		 * if (!mealH.isEmpty()) { // Get the set of keys from the hash map to
		 * make sections. Set<String> restaurantFullMenu = mealHashMap.keySet();
		 * for (String restaurantName : restaurantFullMenu) { // For each
		 * restaurant, make a list of its meals to add in its section
		 * menuListAdapter = new MenuListAdapter(this,
		 * mealHashMap.get(restaurantName), this);
		 * menuListSeparator.addSection(restaurantName, menuListAdapter); }
		 * setListAdapter(menuListSeparator); } }else{
		 * menuListSeparator.removeSections(); TextView txt =
		 * (TextView)findViewById(R.id.restaurant_dailymenu_moreinfo);
		 * txt.setText
		 * (getResources().getString(R.string.resto_suggestions_nothing_found));
		 * // TextView txt_2 =
		 * (TextView)findViewById(R.id.restaurant_empty_daily); //
		 * txt_2.setText(getResources().getString(R.string.resto_empty_list)); }
		 * } } break; }
		 */
	}

	/* will stop the progressBar and other thing (to implement like display sth) */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			/*
			 * if(progressDialog_ != null){ progressDialog_.dismiss(); }
			 * 
			 * /* show menus by Restaurants
			 */
			/*
			 * if(msg.what == 0) showMenusByRestaurants(); if(msg.what == 1)
			 * setListAdapter(sandwichListAdapter_); if(msg.what == 2) {
			 * if(sorter == null){ sorter = new MenuSorter(); }
			 * showSuggestions(); }
			 */
		}
	};

	@Override
	public PluginInfo getPluginInfo() {
		return new FoodInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		// TODO Auto-generated method stub
		return null;
	}
}

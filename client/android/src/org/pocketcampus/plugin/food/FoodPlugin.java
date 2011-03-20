package org.pocketcampus.plugin.food;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.plugin.food.menu.FoodMenu;
import org.pocketcampus.plugin.food.menu.Meal;
import org.pocketcampus.plugin.food.menu.MenuSorter;
import org.pocketcampus.plugin.food.menu.Rating;
import org.pocketcampus.plugin.food.menu.StarRating;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FoodPlugin extends PluginBase {
	private ListView l_;
	private FoodDisplayHandler foodDisplayHandler;
	private TextView txt_empty_;
	public TextView empty;
	private FoodMenu menus_;
	private MenuSorter sorter_;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.food_main);

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
		// List view ; works only for menus by rating & restaurant.
		if(txt_empty_ != null){
			txt_empty_.setText("");
		}
		
		FoodListAdapter fla = foodDisplayHandler.getListAdapter();
		if (foodDisplayHandler.valid() && fla != null) {
			l_.setAdapter(foodDisplayHandler.getListAdapter());
		} else {
			empty.setText(getString(R.string.food_empty));
		}
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
			showSandwich();*/
			return true;
		case 4: // show suggestions
			
			//Reste à n'avoir que les menus du jour
			menus_ = foodDisplayHandler.getMenus();

			if(menus_ != null){

				sorter_ = new MenuSorter();
				
				Vector<Meal> mealsVector = sorter_.sortByRatings(menus_);
				ArrayList<Meal> mealsList = new ArrayList<Meal>();
		
				for(Meal meal : mealsVector){
					mealsList.add(meal);
				}
				
				Intent suggestions = new Intent(getApplicationContext(), Suggestions.class);
				suggestions.putExtra("org.pocketcampus.suggestions.meals", mealsList);
				startActivityForResult(suggestions, 1);

				//foodDisplayHandler.setDisplayType(selectedId);
			}else{
				Toast.makeText(this, "Pas de menus !", Toast.LENGTH_LONG).show();
			}
			return true;			

		}

		return false;
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new FoodInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		// TODO Auto-generated method stub
		return null;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);

		switch(requestCode) { 

		//Result of the Suggestions
		case (1) : 			
			if (resultCode == Activity.RESULT_OK) {

				Bundle extras = data.getExtras();
				if(extras != null){

					ArrayList<Meal> list = (ArrayList<Meal>)extras.getSerializable("org.pocketcampus.suggestions.meals");
					FoodListAdapter flAdapter = foodDisplayHandler.getListAdapter();

					if(list != null && !list.isEmpty()){
						HashMap<Meal, Rating> mealH = new HashMap<Meal, Rating>();

						/*ServerAPI s = new ServerAPI();

						for(Meal m : list){
							Rating rating = null;
							try {
								rating = s.getRating(m);
							} catch (ServerException e) {
								new ServerException("Rating null");
							}
							if(rating!=null){
								mealH.put(m, rating);							
							}
						}*/
						
						for(Meal m : list){
							Rating rating = new Rating(StarRating.STAR_1_0, 0);
							mealH.put(m, rating);
						}

						HashMap<String, Vector<Meal>> mealHashMap = sorter_.sortByRestaurant(mealH.keySet());
						flAdapter.removeSections();

						/**
						 * Iterate over the different restaurant menus
						 */
						if (!mealHashMap.isEmpty()) {
							// Get the set of keys from the hash map to make sections.
							Set<String> restaurantFullMenu = mealHashMap.keySet();
							for (String restaurantName : restaurantFullMenu) {
								// For each restaurant, make a list of its meals to add in its section
								FoodListSection flSection = new FoodListSection(mealHashMap.get(restaurantName), this);
								flAdapter.addSection(restaurantName, flSection);
							}
							l_.setAdapter(flAdapter);
						}else{
							Toast.makeText(this, "mealHashMap vide !", Toast.LENGTH_LONG).show();
						}
					}else{
						flAdapter.removeSections();
						l_.setAdapter(flAdapter);
						
						txt_empty_ = (TextView)findViewById(R.id.food_empty);
						txt_empty_.setText(getResources().getString(R.string.food_suggestions_nothing_found));
						
						Toast.makeText(this, "Extras vides !", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(this, "Pas d'extras !", Toast.LENGTH_LONG).show();
				}
			}else{
				Toast.makeText(this, "RESULT_PAS_OK !", Toast.LENGTH_LONG).show();
			}
		break;		
		}
	}
}

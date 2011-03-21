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
import org.pocketcampus.plugin.food.menu.Meal;
import org.pocketcampus.plugin.food.menu.MenuSorter;
import org.pocketcampus.plugin.food.menu.Rating;
import org.pocketcampus.plugin.food.menu.StarRating;
import org.pocketcampus.plugin.mainscreen.MainscreenPlugin;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
	private ArrayList<Meal> menus_;
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
			menus_ = foodDisplayHandler.getMenusList();
			if(menus_ != null){
				Intent suggestions = new Intent(getApplicationContext(), Suggestions.class);
				suggestions.putExtra("org.pocketcampus.suggestions.meals", menus_);
				startActivityForResult(suggestions, 1);
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

		//Result from the Suggestions class
		case (1) : 			
			if (resultCode == Activity.RESULT_OK) {

				Bundle extras = data.getExtras();
				if(extras != null){
					
					ArrayList<Meal> list = (ArrayList<Meal>)extras.getSerializable("org.pocketcampus.suggestions.meals");
					
					foodDisplayHandler.updateSuggestions(list);
					foodDisplayHandler.setDisplayType(4);
					displayView();
										
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

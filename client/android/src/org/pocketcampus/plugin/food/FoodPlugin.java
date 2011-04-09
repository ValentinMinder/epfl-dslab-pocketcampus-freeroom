package org.pocketcampus.plugin.food;

import java.util.ArrayList;

import org.pocketcampus.R;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.shared.plugin.food.Meal;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FoodPlugin extends PluginBase {
	private ActionBar actionBar_;

	private ListView l_;
	private FoodDisplayHandler foodDisplayHandler;

	private TextView txt_empty_;
	private TextView empty;
	private TextView validityDate_;

	private ArrayList<Meal> suggestionMenus_;
	private boolean isSandwichDisplay_ = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.food_main);
		
		setupActionBar(true);

		// ListView
		l_ = (ListView) findViewById(R.id.food_list);
		empty = (TextView) findViewById(R.id.food_empty);
		validityDate_ = (TextView) findViewById(R.id.food_day_label);
		
		// DisplayHandler
		foodDisplayHandler = new FoodDisplayHandler(this, getRequestHandler());
	}

	private void loadFirstScreen(int layout) {
		setContentView(layout);

		setupActionBar(true);
		
		// ListView
		l_ = (ListView) findViewById(R.id.food_list);
		empty = (TextView) findViewById(R.id.food_empty);
		validityDate_ = (TextView) findViewById(R.id.food_day_label);
		
		// At first, display food by restaurant
		displayView();
	}


	@Override
	protected void setupActionBar(boolean addHomeButton) {

		actionBar_ = (ActionBar) findViewById(R.id.actionbar);
		actionBar_.addAction(new Action() {
			@Override
			public void performAction(View view) {
				foodDisplayHandler.refreshView();
				displayView();
			}
			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});
		super.setupActionBar(addHomeButton);
	}

	// @Override
	public void menuRefreshing() {
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}

	// @Override
	public void menuRefreshed() {
		foodDisplayHandler.updateView();
		displayView();
		actionBar_.setProgressBarVisibility(View.GONE);
	}

	/**
	 * Displays the current view, by restaurant or rating.
	 */
	public void displayView() {
		// List view ; works only for menus by rating & restaurant.
		if (txt_empty_ != null) {
			txt_empty_.setText("");
		}

		FoodListAdapter fla = foodDisplayHandler.getListAdapter();
		if (foodDisplayHandler.valid() && fla != null) {
			l_.setAdapter(fla);
			empty.setText("");
			if(foodDisplayHandler.getDateLastUpdatedMenus() == null){
				validityDate_.setText("Rien trouvé");
			} else {
				validityDate_.setText(foodDisplayHandler.getDateLastUpdatedMenus().toString());				
			}
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

	final int SUGGESTIONS_REQUEST_CODE= 1;
	
	public boolean onOptionsItemSelected(MenuItem item) {
		int selectedId = item.getItemId();

		switch (selectedId) {
		case 1: // Show menus by restaurant
		case 2: // Show menus by rating
			// setContentView(R.layout.food_main);
			if (isSandwichDisplay_) {
				loadFirstScreen(R.layout.food_main);
			}
			foodDisplayHandler.setDisplayType(selectedId);
			displayView();
			return true;
		case 3: // show sandwiches
			isSandwichDisplay_ = true;
			foodDisplayHandler.setDisplayType(selectedId);
			displayView();
			return true;
		case 4: // show suggestions
			suggestionMenus_ = foodDisplayHandler.getMenusList();
			if (suggestionMenus_ != null) {
				Intent suggestions = new Intent(getApplicationContext(),
						Suggestions.class);
				suggestions.putExtra("org.pocketcampus.suggestions.meals",
						suggestionMenus_);
				startActivityForResult(suggestions, SUGGESTIONS_REQUEST_CODE);
			} else {
				Toast.makeText(this, "Pas de menus !", Toast.LENGTH_LONG)
						.show();
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

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case SUGGESTIONS_REQUEST_CODE: // Result from the Suggestions class
			if (resultCode == Activity.RESULT_OK) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					ArrayList<Meal> list = (ArrayList<Meal>) extras.getSerializable("org.pocketcampus.suggestions.meals");

					foodDisplayHandler.updateSuggestions(list);
					foodDisplayHandler.setDisplayType(4);
					displayView();
				} else {
					Toast.makeText(this, "Pas d'extras !", Toast.LENGTH_LONG)
							.show();
				}
			} else {
				Toast.makeText(this, "RESULT_PAS_OK !", Toast.LENGTH_LONG)
						.show();
			}
			break;
		}
	}
}

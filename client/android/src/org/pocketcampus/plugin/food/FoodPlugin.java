package org.pocketcampus.plugin.food;

import java.util.ArrayList;
import java.util.Date;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.food.pictures.PictureTaker;
import org.pocketcampus.shared.plugin.food.Meal;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FoodPlugin extends PluginBase {
	private ActionBar actionBar_;

	private ListView l_;
	private static FoodDisplayHandler foodDisplayHandler_;
	private static RequestHandler foodRequestHandler_;
	private TextView txt_empty_;
	private TextView empty;
	private TextView validityDate_;
	private ProgressBar spinner_;

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

		spinner_ = (ProgressBar) findViewById(R.id.food_spinner);
		spinner_.setVisibility(View.VISIBLE);

		// RequestHandler
		foodRequestHandler_ = getRequestHandler();
		// DisplayHandler
		foodDisplayHandler_ = new FoodDisplayHandler(this);
	}

	public static RequestHandler getFoodRequestHandler() {
		return foodRequestHandler_;
	}
	
	public static ArrayList<String> getRestaurantList(){
		if(foodDisplayHandler_ != null){
			return foodDisplayHandler_.getRestaurantList(); 
		}else{
			return new ArrayList<String>();
		}
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
				foodDisplayHandler_.refreshView();
			}

			@Override
			public int getDrawable() {
				return R.drawable.refresh;
			}
		});
		super.setupActionBar(addHomeButton);
	}

	public void menuRefreshing() {
		actionBar_.setProgressBarVisibility(View.VISIBLE);
	}

	public void menuRefreshed(boolean successful) {
		if (!successful) {
			Toast.makeText(this,
					this.getResources().getString(R.string.food_menucancelled),
					Toast.LENGTH_SHORT).show();
		}
		this.notifyDataSetChanged();
		foodDisplayHandler_.updateView();
		displayView();
		actionBar_.setProgressBarVisibility(View.GONE);
	}

	public void menuRefreshedSandwich() {
		foodDisplayHandler_.updateView();
		displaySandwiches();
		actionBar_.setProgressBarVisibility(View.GONE);
	}

	public void notifyDataSetChanged() {
		foodDisplayHandler_.getListAdapter().notifyDataSetChanged();
	}

	/**
	 * Displays the current view, by restaurant or rating.
	 */
	public void displayView() {
		// List view ; works only for menus by rating & restaurant.
		if (txt_empty_ != null) {
			txt_empty_.setText("");
		}

		if (spinner_ != null && spinner_.isShown()) {
			spinner_.setVisibility(View.GONE);
		}

		FoodListAdapter fla = foodDisplayHandler_.getListAdapter();
		if (foodDisplayHandler_.valid() && fla != null) {
			l_.setAdapter(fla);
			empty.setText("");
			if (foodDisplayHandler_.getDateLastUpdatedMenus() == null) {
				validityDate_.setText("");
			} else {
				Date today = new Date();
				Date lastUpdated = foodDisplayHandler_
						.getDateLastUpdatedMenus();
				if (today.getDay() == lastUpdated.getDay()
						&& today.getMonth() == lastUpdated.getMonth()) {
					validityDate_.setText(getResources().getString(
							R.string.food_today_menus));
				} else {
					validityDate_.setText(lastUpdated.toLocaleString());
				}
			}
		} else {
			empty.setText(getString(R.string.food_empty));
		}
	}

	public void displaySandwiches() {
		FoodListAdapter fla = foodDisplayHandler_.getListAdapter();
		if (foodDisplayHandler_.valid() && fla != null) {
			l_.setAdapter(fla);
		} else {
			empty.setText(getString(R.string.food_empty));
		}
	}

	public void displaySuggestions() {
		FoodListAdapter fla = foodDisplayHandler_.getListAdapter();
		if (foodDisplayHandler_.valid() && fla != null) {
			l_.setAdapter(fla);
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

	final int SUGGESTIONS_REQUEST_CODE = 1;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1337;

	public boolean onOptionsItemSelected(MenuItem item) {
		int selectedId = item.getItemId();

		switch (selectedId) {
		case 1: // Show menus by restaurant
		case 2: // Show menus by rating
			// setContentView(R.layout.food_main);
			if (isSandwichDisplay_) {
				loadFirstScreen(R.layout.food_main);
			}
			foodDisplayHandler_.setDisplayType(selectedId);
			displayView();
			return true;
		case 3: // show sandwiches
			Log
					.d("SANDWICHES",
							"Il a compris qu'il devait afficher les sandwiches. [FoodPlugin]");
			isSandwichDisplay_ = true;
			foodDisplayHandler_.setDisplayType(selectedId);
			displaySandwiches();
			return true;
		case 4: // show suggestions
			suggestionMenus_ = foodDisplayHandler_.getMenusList();
			if (suggestionMenus_ != null) {
				Intent suggestions = new Intent(getApplicationContext(),
						Suggestions.class);
				suggestions.putExtra("org.pocketcampus.suggestions.meals",
						suggestionMenus_);
				startActivityForResult(suggestions, SUGGESTIONS_REQUEST_CODE);
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
		return new FoodPreference();
	}

	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case SUGGESTIONS_REQUEST_CODE: // Result from the Suggestions class
			if (resultCode == Activity.RESULT_OK) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					@SuppressWarnings("unchecked")
					ArrayList<Meal> list = (ArrayList<Meal>) extras
							.getSerializable("org.pocketcampus.suggestions.meals");

					foodDisplayHandler_.updateSuggestions(list);
					foodDisplayHandler_.setDisplayType(4);
					displaySuggestions();
				} else {
					Log.d("SUGGESTIONS", "Pas d'extras !");
				}
			} else {
				Log.d("SUGGESTIONS", "RESULT_PAS_OK !");
			}
			break;
		case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			PictureTaker.onActivityResult(requestCode, resultCode,
					data, true);
			Toast.makeText(this, "YOOOOOOOOOOOOO", Toast.LENGTH_SHORT).show();
			break;
		}
	}
}

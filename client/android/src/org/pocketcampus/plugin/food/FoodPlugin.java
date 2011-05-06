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
import org.pocketcampus.plugin.food.FoodDisplayHandler.FoodDisplayType;
import org.pocketcampus.plugin.food.pictures.PictureTaker;
import org.pocketcampus.plugin.food.sandwiches.SandwichListAdapter;
import org.pocketcampus.shared.plugin.food.Meal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Adapter;
import android.widget.ImageView;
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
	private ImageView expandMenus_;

	private ProgressBar spinner_;
	private RestaurantAction restaurantAction_;

	private ArrayList<Meal> suggestionMenus_;
	private boolean isSandwichDisplay_ = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadFirstScreen();
		restaurantAction_ = new RestaurantAction();
		// RequestHandler
		foodRequestHandler_ = getRequestHandler();
		// DisplayHandler
		foodDisplayHandler_ = new FoodDisplayHandler(this);
	}

	public static RequestHandler getFoodRequestHandler() {
		return foodRequestHandler_;
	}

	public static ArrayList<String> getRestaurantList() {
		if (foodDisplayHandler_ != null) {
			return foodDisplayHandler_.getRestaurantList();
		} else {
			return new ArrayList<String>();
		}
	}

	private void loadFirstScreen() {
		setContentView(R.layout.food_main);

		spinner_ = (ProgressBar) findViewById(R.id.food_spinner);
		spinner_.setVisibility(View.VISIBLE);

		setupActionBar(true);
		// ListView
		l_ = (ListView) findViewById(R.id.food_list);
		empty = (TextView) findViewById(R.id.food_empty);

		validityDate_ = (TextView) findViewById(R.id.food_day_label);
		expandMenus_ = (ImageView) findViewById(R.id.food_menus_expand);
		expandMenus_.setOnTouchListener(new ExpandListener());
	}

	private void resetScreen() {
		loadFirstScreen();
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

	protected void refreshActionBar(FoodDisplayType currentDisplayType) {
		if ((currentDisplayType == FoodDisplayType.Restaurants || currentDisplayType == FoodDisplayType.Ratings)
				&& !restaurantAction_.isShown()) {
			actionBar_.addAction(restaurantAction_, 0);
			restaurantAction_.setShown(true);
		} else if (!(currentDisplayType == FoodDisplayType.Restaurants || currentDisplayType == FoodDisplayType.Ratings)
				&& restaurantAction_.isShown_) {
			actionBar_.removeActionAt(0);
			restaurantAction_.setShown(false);
		}
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
		spinner_.setVisibility(View.GONE);
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

		if (spinner_ != null) {
			spinner_.setVisibility(View.GONE);
		}

		FoodListAdapter fla = foodDisplayHandler_.getListAdapter();
		expandMenus_.setVisibility(View.GONE);
		
		if (foodDisplayHandler_.valid() && fla != null) {
			l_.setAdapter(fla);
			empty.setText("");
			expandMenus_ = (ImageView) findViewById(R.id.food_menus_expand);
			expandMenus_.setOnTouchListener(new ExpandListener());
			if (foodDisplayHandler_.getDateLastUpdatedMenus() == null) {
				validityDate_.setText("");
			} else if (foodDisplayHandler_.getCurrentDisplayType() == FoodDisplayType.Sandwiches) {
				validityDate_.setText(getResources().getString(
						R.string.food_today_sandwiches));
				expandMenus_.setVisibility(View.VISIBLE);
			} else {
				if (foodDisplayHandler_.getCurrentDisplayType() == FoodDisplayType.Ratings) {
				} else {
					expandMenus_.setVisibility(View.VISIBLE);
				}
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

	public void displaySuggestions() {
		expandMenus_.setVisibility(View.GONE);
		FoodListAdapter fla = foodDisplayHandler_.getListAdapter();
		if (foodDisplayHandler_.valid() && fla != null) {
			l_.setAdapter(fla);
		} else {
			empty.setText(getString(R.string.food_empty));
		}
		actionBar_.addAction(new Action() {
			
			@Override
			public void performAction(View view) {
				actionBar_.removeActionAt(0);
				foodDisplayHandler_.setCurrentDisplayType(R.id.food_menu_restaurants);
				displayView();
			}
			
			@Override
			public int getDrawable() {
				return R.drawable.food_menus_by_restaurant;
			}
		}, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.food, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int selectedId = item.getItemId();

		switch (selectedId) {
		case R.id.food_menu_restaurants: // Show menus by restaurant
			// case R.id.food_menu_ratings: // Show menus by rating
			// setContentView(R.layout.food_main);
			if (isSandwichDisplay_) {
				resetScreen();
				isSandwichDisplay_ = false;
			}
			foodDisplayHandler_.setCurrentDisplayType(selectedId);
			displayView();
			return true;
		case R.id.food_menu_sandwiches: // show sandwiches
			Log
					.d("SANDWICHES",
							"Il a compris qu'il devait afficher les sandwiches. [FoodPlugin]");
			isSandwichDisplay_ = true;
			foodDisplayHandler_.setCurrentDisplayType(selectedId);
			displayView();
			return true;
		case R.id.food_menu_suggestions: // show suggestions
			suggestionMenus_ = foodDisplayHandler_.getMenusList();
			if (suggestionMenus_ != null) {
				Intent suggestions = new Intent(getApplicationContext(),
						Suggestions.class);
				suggestions.putExtra("org.pocketcampus.suggestions.meals",
						suggestionMenus_);
				startActivityForResult(suggestions, SUGGESTIONS_REQUEST_CODE);
			}
			return true;
		case R.id.food_menu_settings: // show food settings
			Intent intent = new Intent(this, FoodPreference.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}

		return false;
	}

	final int SUGGESTIONS_REQUEST_CODE = 1;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1337;

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
					foodDisplayHandler_
							.setCurrentDisplayType(R.id.food_menu_suggestions);
					displaySuggestions();
				} else {
					Log.d("SUGGESTIONS", "Pas d'extras !");
				}
			} else {
				Log.d("SUGGESTIONS", "RESULT_PAS_OK !");
			}
			break;
		case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			PictureTaker.onActivityResult(requestCode, resultCode, data, true);
			Toast.makeText(this, "YOOOOOOOOOOOOO", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	public PluginInfo getPluginInfo() {
		return new FoodInfo();
	}

	@Override
	public PluginPreference getPluginPreference() {
		return new FoodPreference();
	}

	class RestaurantAction implements Action {
		private boolean isRestaurants_;
		private boolean isShown_;

		RestaurantAction() {
			isRestaurants_ = true;
			isShown_ = false;
		}

		@Override
		public int getDrawable() {
			if (isRestaurants_) {
				return R.drawable.food_menus_by_ratings;
			} else {
				return R.drawable.food_menus_by_restaurant;
			}
		}

		@Override
		public void performAction(View view) {
			isRestaurants_ = !isRestaurants_;
			actionBar_.removeActionAt(0);
			actionBar_.addAction(this, 0);
			if (isRestaurants_) {
				if (isSandwichDisplay_) {
					resetScreen();
					isSandwichDisplay_ = false;
				}
				foodDisplayHandler_
						.setCurrentDisplayType(R.id.food_menu_restaurants);
			} else {
				foodDisplayHandler_.setCurrentDisplayType(125);
			}
			displayView();
			foodDisplayHandler_.refreshView();
		}

		public boolean isShown() {
			return isShown_;
		}

		public void setShown(boolean show) {
			isShown_ = show;
		}
	}

	class ExpandListener implements OnTouchListener {
		private boolean expanded = false;
		private Drawable expand_;
		private Drawable unexpand_;
		
		public ExpandListener() {
			expand_ = FoodPlugin.this.getResources().getDrawable(
					R.drawable.food_menus_expand);
			unexpand_ = FoodPlugin.this.getResources().getDrawable(
					R.drawable.food_menus_remballe);

			expandMenus_.setImageDrawable(expand_);
		}
		@Override
		public boolean onTouch(View view, MotionEvent arg1) {
			expanded = (!expanded);
			if (!expanded) {
				expandMenus_.setImageDrawable(expand_);
			} else {
				expandMenus_.setImageDrawable(unexpand_);
			}
			expandMenus_.invalidate();

			Adapter adapt = foodDisplayHandler_.getListAdapter()
					.getExpandableList(
							FoodPlugin.this
									.getString(R.string.food_restaurants));
			if (adapt != null) {
				if (adapt instanceof RestaurantListAdapter) {
					((RestaurantListAdapter) adapt).toggleAll(expanded);
				} else if (adapt instanceof SandwichListAdapter) {
					((SandwichListAdapter) adapt).toggleAll(expanded);
				}
			}
			return false;
		}
	}
}

package org.pocketcampus.plugin.food;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.plugin.ICallback;
import org.pocketcampus.core.plugin.NoIDException;
import org.pocketcampus.core.plugin.PluginBase;
import org.pocketcampus.core.plugin.PluginInfo;
import org.pocketcampus.core.plugin.PluginPreference;
import org.pocketcampus.core.ui.ActionBar;
import org.pocketcampus.core.ui.ActionBar.Action;
import org.pocketcampus.plugin.food.FoodDisplayHandler.FoodDisplayType;
import org.pocketcampus.plugin.food.menu.MenuSorter;
import org.pocketcampus.plugin.food.request.MenusRequest;
import org.pocketcampus.plugin.food.request.RatingsRequest;
import org.pocketcampus.plugin.food.sandwiches.SandwichListAdapter;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.plugin.mainscreen.IAllowsID;
import org.pocketcampus.plugin.mainscreen.IMainscreenNewsProvider;
import org.pocketcampus.plugin.mainscreen.MainscreenNews;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class FoodPlugin extends PluginBase implements IMainscreenNewsProvider,
		IAllowsID {
	// Bar at the top of the application
	private ActionBar actionBar_;
	public Context otherCtx_;

	// Activity's menus list
	private ListView listView_;
	private static FoodDisplayHandler foodDisplayHandler_;
	// private static FoodListAdapter foodListAdapter_;
	private static RequestHandler foodRequestHandler_;
	private TextView empty_;
	private TextView validityDate_;
	private ImageView expandMenus_;

	// Spinner to show while loading data.
	private ProgressBar spinner_;

	private MenusShowByAction restaurantAction_;

	private ArrayList<Meal> suggestionMenus_;
	private boolean isSandwichDisplay_ = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadFirstScreen();
		restaurantAction_ = new MenusShowByAction();
		// RequestHandler
		foodRequestHandler_ = getRequestHandler();
		// DisplayHandler
		foodDisplayHandler_ = new FoodDisplayHandler(this);

		Tracker.getInstance().trackPageView("food/home");

		handleIntent();
	}

	@Override
	public void onRestart() {
		super.onRestart();
		foodDisplayHandler_.refreshView();
	}

	private void handleIntent() {

		try {
			showMenu(getIDFromIntent());
		} catch (NoIDException e1) {
			Log.d("FoodPlugin", "Failed to get Intent's ID");
		}

		try {
			Log.d(this.getClass().toString(), hasIDInIntent() ? "Has ID "
					+ getIDFromIntent() : "Does not have ID");
		} catch (NoIDException e) {
			Log.d(this.getClass().toString(), "NoIDException");
			e.printStackTrace();
		}
	}

	/**
	 * Food request handler for requests to the server from the food plugin
	 * 
	 * @return RequestHandler for this plugin.
	 */
	public static RequestHandler getFoodRequestHandler() {
		return foodRequestHandler_;
	}

	/**
	 * Load the main screen of the food plugin.
	 */
	private void loadFirstScreen() {
		setContentView(R.layout.food_main);

		spinner_ = (ProgressBar) findViewById(R.id.food_spinner);
		spinner_.setVisibility(View.VISIBLE);

		setupActionBar(true);
		// ListView
		listView_ = (ListView) findViewById(R.id.food_list);
		empty_ = (TextView) findViewById(R.id.food_empty);

		validityDate_ = (TextView) findViewById(R.id.food_day_label);
		expandMenus_ = (ImageView) findViewById(R.id.food_menus_expand);
		expandMenus_.setOnTouchListener(new ExpandListener());
	}

	/**
	 * Put the screen back to its first state.
	 */
	private void resetScreen() {
		loadFirstScreen();
		displayView();
	}

	public FoodDisplayHandler getFoodDisplayHandler() {
		return foodDisplayHandler_;
	}

	/**
	 * Add home button to action bar or not.
	 */
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

	/**
	 * Checks whether the restaurant action should be shown (toggle show by
	 * restaurants/by ratings)-
	 * 
	 * @param currentDisplayType
	 *            the type of the display
	 */
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

	/**
	 * Called when the menus are being reloaded.
	 */
	public void menuRefreshing() {
		actionBar_.setProgressBarVisibility(View.VISIBLE);
		if (listView_ != null) {
			if (listView_.getAdapter() != null) {
				if (listView_.getAdapter().getCount() == 0) {
					spinner_ = (ProgressBar) findViewById(R.id.food_spinner);
					spinner_.setVisibility(View.VISIBLE);
					empty_.setText("");
				}
			}
		}
	}

	public void menuRefreshed(boolean successful) {
		if (!successful) {
			Toast.makeText(this,
					this.getResources().getString(R.string.food_menucancelled),
					Toast.LENGTH_SHORT).show();
		}
		if (foodDisplayHandler_ != null) {
			this.notifyDataSetChanged();
			foodDisplayHandler_.updateView();
			displayView();
		}
		refreshed();
	}

	public void refreshed() {
		if (spinner_ != null) {
			spinner_.setVisibility(View.GONE);
		}
		actionBar_.setProgressBarVisibility(View.GONE);
	}

	private void showMenu(int id) {
		ArrayList<String> restos = readFromFile(getApplicationContext());
		String r = findHashCode(restos, id);
		Log.d("FoodPlugin", "Found restaurant : " + r);
		int index = restos.indexOf(r);

		ArrayAdapter<String> adapt1 = foodDisplayHandler_.getListAdapter().headers_;
		
		Adapter adapt = foodDisplayHandler_.getListAdapter().getExpandableList(
				this.getString(R.string.food_restaurants));
		if (adapt != null) {
			if (adapt instanceof RestaurantListAdapter) {
				((RestaurantListAdapter) adapt).toggle(index);
			}
		} else {
			Log.d("FoodPlugin", "adapt is null");
		}

		Log.d("FoodPlugin", "Show menu with hashcode : " + id);
		// Tracker.getInstance().trackPageView("food/menusListToggle" + r);
	}

	private String findHashCode(ArrayList<String> restos, int hash) {
		String resto = "";

		for (String r : restos) {
			if (r.hashCode() == hash) {
				resto = r;
				break;
			}
		}
		return resto;
	}

	public void notifyDataSetChanged() {
		foodDisplayHandler_.getListAdapter().notifyDataSetChanged();
	}

	/**
	 * Displays the current view, by restaurant or rating.
	 */
	public void displayView() {
		// List view ; works only for menus by rating & restaurant.

		if (spinner_ != null) {
			spinner_.setVisibility(View.GONE);
		}

		FoodListAdapter fla = foodDisplayHandler_.getListAdapter();
		expandMenus_.setVisibility(View.GONE);

		if (foodDisplayHandler_.getCurrentDisplayType() != FoodDisplayType.Sandwiches
				&& foodDisplayHandler_.validMenus() && fla != null) {
			listView_.setAdapter(fla);
			empty_.setText("");
			expandMenus_ = (ImageView) findViewById(R.id.food_menus_expand);
			expandMenus_.setOnTouchListener(new ExpandListener());
			if (foodDisplayHandler_.getDateLastUpdatedMenus() == null) {
				validityDate_.setText("");
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
		} else if (foodDisplayHandler_.getCurrentDisplayType() == FoodDisplayType.Sandwiches) {
			empty_.setText("");
			if (foodDisplayHandler_.validSandwich()) {
				listView_.setAdapter(fla);
				expandMenus_ = (ImageView) findViewById(R.id.food_menus_expand);
				expandMenus_.setOnTouchListener(new ExpandListener());
				validityDate_.setText(getResources().getString(
						R.string.food_today_sandwiches));
				expandMenus_.setVisibility(View.VISIBLE);
				empty_.setText("");
			} else {
				validityDate_.setText("");
				empty_.setText(getString(R.string.food_empty));
				actionBar_.setProgressBarVisibility(View.GONE);
			}
		} else {
			listView_.setAdapter(fla);
			validityDate_.setText("");
			empty_.setText(getString(R.string.food_empty));
		}
	}

	public void displaySuggestions(FoodDisplayType previousDisplayType) {
		expandMenus_.setVisibility(View.GONE);
		FoodListAdapter fla = foodDisplayHandler_.getListAdapter();
		// refreshActionBar(foodDisplayHandler_.getCurrentDisplayType());
		restaurantAction_.setIsRestaurant(true);
		empty_.setText("");

		if (foodDisplayHandler_.validMenus()
				&& foodDisplayHandler_.validSuggestions() && fla != null) {
			listView_.setAdapter(fla);
			validityDate_.setText(getResources().getString(
					R.string.food_today_suggestions));

			if (previousDisplayType != FoodDisplayType.Suggestions) {
				actionBar_.addAction(new Action() {

					@Override
					public void performAction(View view) {
						actionBar_.removeActionAt(0);
						foodDisplayHandler_
								.setCurrentDisplayType(R.id.food_menu_restaurants);
						foodDisplayHandler_.updateView();
						displayView();
					}

					@Override
					public int getDrawable() {
						return R.drawable.food_menus_by_restaurant;
					}
				}, 0);
			}
		} else {
			foodDisplayHandler_
					.setCurrentDisplayType(FoodDisplayType.Restaurants
							.getValue());
			foodDisplayHandler_.updateView();
			displayView();
		}
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

	public void setSelected(int position) {
		listView_.setSelection(position + 1);
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
					FoodDisplayType previous = foodDisplayHandler_
							.getCurrentDisplayType();
					foodDisplayHandler_
							.setCurrentDisplayType(R.id.food_menu_suggestions);
					displaySuggestions(previous);
				} else {
					Log.d("SUGGESTIONS", "Pas d'extras !");
				}
			} else {
				Log.d("SUGGESTIONS", "RESULT_PAS_OK !");
			}
			break;
		case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			// PictureTaker.onActivityResult(requestCode, resultCode, data,
			// true);
			// Toast.makeText(this, "YOOOOOOOOOOOOO",
			// Toast.LENGTH_SHORT).show();
			// break;
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

	class MenusShowByAction implements Action {
		private boolean isRestaurants_;
		private boolean isShown_;

		MenusShowByAction() {
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

		public void setIsRestaurant(boolean isRestaurants) {
			isRestaurants_ = isRestaurants;
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

	@Override
	public void getNews(Context ctx, final ICallback callback) {
		final ArrayList<MainscreenNews> news = new ArrayList<MainscreenNews>();
		final FoodPlugin that = this;
		otherCtx_ = ctx;

		class MainscreenMenusRequest extends MenusRequest {

			@Override
			public void onCancelled() {
				Log.d("SERVER", "Task cancelled (Mainscreen)");
			}

			@Override
			public void updateMenus(final List<Meal> campusMenuList) {
				if (campusMenuList != null) {
					if (!campusMenuList.isEmpty()) {

						class MainscreenRatingsRequest extends RatingsRequest {
							Meal m_;

							@Override
							public void updateRatings(
									HashMap<Integer, Rating> ratings) {

								m_ = getBestMeal(ratings);

								if (m_ != null) {
									MainscreenNews bestMeal = new MainscreenNews(
											m_.getName_()
													+ "\n"
													+ m_.getRestaurant_()
															.getName(),
											m_.getDescription_(), m_
													.getRestaurant_().getName()
													.hashCode(), that,
											new Date());
									news.add(bestMeal);
									callback.callback(news);
								}
							}

							private Meal getBestMeal(
									HashMap<Integer, Rating> ratings) {
								ArrayList<String> restos = readFromFile(otherCtx_);
								Vector<Meal> mealsVector = new Vector<Meal>();
								MenuSorter sorter = new MenuSorter();

								for (Meal m : campusMenuList) {
									if (restos.contains(m.getRestaurant_()
											.getName())) {
										mealsVector.add(m);
									}
								}

								mealsVector = sorter.sortByRatings(mealsVector);

								if (mealsVector != null) {
									return mealsVector.get(0);
								} else {
									return null;
								}
							}

						}
						Log.d("SERVER", "Requesting ratings (Mainscreen)");
						getRequestHandler().execute(
								new MainscreenRatingsRequest(), "getRatings",
								(RequestParameters) null);
					}
				}
			}
		}

		Log.d("SERVER", "Requesting menus (Mainscreen)");
		getRequestHandler().execute(new MainscreenMenusRequest(), "getMenus",
				(RequestParameters) null);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> readFromFile(Context ctx) {
		String filename = "RestaurantsPref";
		ArrayList<String> restosDisplayed = null;
		File toGet = new File(ctx.getDir("preferences", 0), filename);
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(toGet);
			in = new ObjectInputStream(fis);

			restosDisplayed = (ArrayList<String>) in.readObject();

			in.close();
		} catch (IOException ex) {
		} catch (ClassNotFoundException ex) {
		} catch (ClassCastException cce) {
		}

		return restosDisplayed;
	}

}

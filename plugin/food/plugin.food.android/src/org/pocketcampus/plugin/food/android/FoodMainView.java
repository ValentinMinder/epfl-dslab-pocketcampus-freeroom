package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.dialog.MenuDialog;
import org.pocketcampus.android.platform.sdk.ui.dialog.RatingDialog;
import org.pocketcampus.android.platform.sdk.ui.element.RatableView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewConstructor;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewLabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.ui.list.RatableExpandableListViewElement;
import org.pocketcampus.android.platform.sdk.ui.list.RatableListViewElement;
import org.pocketcampus.plugin.food.android.iface.IFoodMainView;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Sandwich;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class FoodMainView extends PluginView implements IFoodMainView {
	/* Activity */
	private Activity mActivity;

	/* MVC */
	private FoodController mController;
	private IFoodModel mModel;

	/* Layout */
	private StandardLayout mLayout;
	private RatableExpandableListViewElement mList;

	/* Constants */
	private final int SUGGESTIONS_REQUEST_CODE = 1;

	/*Listeners*/
	private OnItemClickListener mOnLineClickListener;
	private OnItemClickListener mOnRatingClickListener;

	/*Preferences*/
	private SharedPreferences mRestoPrefs;
	private static final String RESTO_PREFS_NAME = "RestoPrefs";

	/**
	 * Defines what the main controller is for this view. This is optional, some
	 * view may not need a controller (see for example the dashboard).
	 * 
	 * This is only a shortcut for what is done in
	 * <code>getOtherController()</code> below: if you know you'll need a
	 * controller before doing anything else in this view, you can define it as
	 * you're main controller so you know it'll be ready as soon as
	 * <code>onDisplay()</code> is called.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return FoodController.class;
	}

	/**
	 * Called once the view is connected to the controller. If you don't
	 * implement <code>getMainControllerClass()</code> then the controller given
	 * here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		Log.d("ACTIVITY", "onDisplay");
		mActivity = this;
		// Get and cast the controller and model
		mController = (FoodController) controller;
		mModel = (FoodModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		// We need to force the display before asking the controller for the
		// data,
		// as the controller may take some time to get it.
		displayData();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("ACTIVITY", "onRestart");
		refreshDisplay();
	}

	/**
	 * Displays the data For now testing with Restaurants
	 */
	private void displayData() {
		mLayout.setText(getResources().getString(R.string.food_no_menus));
		mController.getRestaurantsList();
		mController.getMeals();
		mController.getSandwiches();
	}

	/**
	 * Refreshes the display after some changes, e.g. preferences or suggestions
	 */
	private void refreshDisplay() {
		showMenusByRestaurants();
	}

	/**
	 * Main Food Options menu contains access to Sandwiches, Suggestions and
	 * Settings
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.food_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.food_by_resto) {
			showMenusByRestaurants();
		} else if (item.getItemId() == R.id.food_by_sandwiches) {
			showSandwiches();
		} else if (item.getItemId() == R.id.food_by_ratings) {
			showMenusByRatings();
		} else if (item.getItemId() == R.id.food_by_suggestions) {
			//Extras to add to the Intent
			ArrayList<Meal> meals = (ArrayList<Meal>) mModel.getMeals();

			//Intent to start the SuggestionsView
			Intent suggestions = new Intent(getApplicationContext(),
					FoodSuggestionsView.class);
			suggestions.putExtra("org.pocketcampus.suggestions.meals", meals);
			startActivityForResult(suggestions, SUGGESTIONS_REQUEST_CODE);
		} else if (item.getItemId() == R.id.food_by_settings) {
			Intent settings = new Intent(getApplicationContext(),
					FoodPreferencesView.class);
			startActivity(settings);
		}

		return true;
	}

	public void restaurantsUpdated() {
		Log.d("RESTAURANT", "Restaurants updated");
	}

	@Override
	public void menusUpdated() {
		showMenusByRestaurants();
	}
	
	@Override
	public void ratingsUpdated() {
		Log.d("RATING", "All Ratings updated");
		//Refresh View
	}
	
	@Override
	public void ratingsUpdated(SubmitStatus status) {
		Log.d("RATING", "One Rating updated");
		//Toast with the status
		if(status.equals(SubmitStatus.VALID)) {
			Toast.makeText(this, R.string.food_rating_valid, Toast.LENGTH_SHORT).show();
		} else if (status.equals(SubmitStatus.ALREADY_VOTED)) {
			Toast.makeText(this, R.string.food_rating_already_voted, Toast.LENGTH_SHORT).show();
		} else if (status.equals(SubmitStatus.TOOEARLY)) {
			Toast.makeText(this, R.string.food_rating_too_early, Toast.LENGTH_SHORT).show();
		} else if (status.equals(SubmitStatus.ERROR)) {
			Toast.makeText(this, R.string.food_rating_error, Toast.LENGTH_SHORT).show();
		}
		
		//Refresh View
	}

	@Override
	public void sandwichesUpdated() {
		Log.d("SANDWICHES","Sandwiches updated");
		showSandwiches();
	}
	
	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!",
				Toast.LENGTH_SHORT);
		toast.show();
	}
	
	/**
	 * Shows menus sorted by Restaurant
	 */
	public void showMenusByRestaurants() {
		final HashMap<String, Vector<Meal>> mealHashMap = mModel
				.getMealsByRestaurants();
		Log.d("MEALS", "Size of list of meals : " + mealHashMap.size());

		/**
		 * Iterate over the different restaurant menus
		 */
		if (!mealHashMap.isEmpty()) {

			//Filtering restaurant that the user doesn't want to display
			mRestoPrefs = getSharedPreferences(RESTO_PREFS_NAME, 0);

			if(mRestoPrefs.getAll().isEmpty()){
				mList = new RatableExpandableListViewElement(this, mealHashMap,
						mMealLabeler, mViewConstructor);
			}else {
				mList = new RatableExpandableListViewElement(this, preferedRestaurants(mealHashMap), 
						mMealLabeler, mViewConstructor);
			}

			//Create Listeners
			if(mOnLineClickListener == null) {
				mOnLineClickListener = new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapter, View v,
							int positionInSection, long arg3) {

						final Meal meal = mealHashMap.get(v.getTag()).get(positionInSection);
						
						//Create the Builder for the Menu dialog
						MenuDialog.Builder b = new MenuDialog.Builder(mActivity);
						b.setCanceledOnTouchOutside(true);

						// Set different values for the dialog
						b.setTitle(meal.getRestaurant().getName() + " - " + meal.getName());
						b.setDescription(meal.getMealDescription());
						b.setRating((float) 0.0, meal.getRating().getNbVotes());

						b.setFirstButton(R.string.food_menu_dialog_firstButton,
								new MenuDialogListener(b, meal));
						b.setSecondButton(R.string.food_menu_dialog_secondButton,
								new MenuDialogListener(b, meal));
						b.setThirdButton(R.string.food_menu_dialog_thirdButton,
								new MenuDialogListener(b, meal));

						//Create the dialog and display it
						MenuDialog dialog = b.create();
						dialog.show();
					}
				};
			}

			if(mOnRatingClickListener == null) {
				mOnRatingClickListener = new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapter, View okButton,
							int positionInSection, long rating) {

						final Meal meal = mealHashMap.get(okButton.getTag()).get(positionInSection);
						
						//Create the Builder for the Rating dialog
						RatingDialog.Builder b = new
								RatingDialog.Builder(mActivity);
						
						//Set different values for the dialog
						b.setTitle(R.string.food_rating_dialog_title);
						b.setOkButton(R.string.food_rating_dialog_OK,
								new RatingDialogListener(b, meal, rating));
						b.setCancelButton(R.string.food_rating_dialog_cancel,
								new RatingDialogListener());

						//Create the dialog and display it
						RatingDialog dialog = b.create();
						dialog.show();
					}
				};
			}

			mLayout.setText("");
			mLayout.addView(mList);
		}
	}
	
	/**
	 * Shows menus sorted by Ratings
	 */
	public void showMenusByRatings() {
		List<Meal> mealsByRatings = mModel.getMealsByRatings();
		Log.d("RATING", "Size of meals list : " + mealsByRatings.size());
		
//		RatableListViewElement l = new RatableListViewElement(this, mealsByRatings, mMealLabeler);
//		l.setOnLineClickListener(mOnLineClickListener);
//		l.setOnRatingClickListener(mOnRatingClickListener);
//		mLayout.addView(l);
	}

	/**
	 * Shows the menus when suggestions are received
	 */
	public void showMenusBySuggestions(ArrayList<Meal> mealsBySuggestions) {
		Log.d("SUGGESTIONS", "Size of suggested meals : " + mealsBySuggestions.size());
		
//		RatableListViewElement l = new RatableListViewElement(this, mealsBySuggestions, mMealLabeler);
//		l.setOnLineClickListener(mOnLineClickListener);
//		l.setOnRatingClickListener(mOnRatingClickListener);
//		mLayout.addView(l);
	}
	
	/**
	 * Shows the sandwiches
	 */
	public void showSandwiches() {
		final HashMap<String, Vector<Sandwich>> mSandwiches = mModel.getSandwichesByRestaurants();
		Log.d("SANDWICHES", "Size of Sandwiches list : " + mSandwiches.size());
		
		//To be continued...
	}
	
	private HashMap<String, Vector<Meal>> preferedRestaurants(HashMap<String, Vector<Meal>> map){
		Set<String> set = map.keySet();
		HashMap<String, Vector<Meal>> toDisplay = new HashMap<String, Vector<Meal>>();

		for(String r : set) {
			if(mRestoPrefs.getBoolean(r, false)) {
				toDisplay.put(r, map.get(r));
			}
		}

		return toDisplay;
	}

	/**
	 * Called when one of the Menu Dialog button is clicked.
	 * 
	 */
	private class MenuDialogListener implements DialogInterface.OnClickListener {
		private MenuDialog.Builder builder;
		private Meal meal;
		private float rating;

		public MenuDialogListener(MenuDialog.Builder b, Meal m) {
			builder = b;
			meal = m;
		}

		@Override
		public void onClick(DialogInterface dialog, int code) {
			switch (code) {
			case DialogInterface.BUTTON1:
				rating = builder.getSubmittedRating();
				Log.d("RATING", "Rating submitted : " + rating);
				dialog.dismiss();
				mController.setRating((float) rating, meal);
				break;

			case DialogInterface.BUTTON2:
				// Pictures
				Log.d("PICTURES", "Picture taken");
				dialog.dismiss();
				break;

			case DialogInterface.BUTTON3:
				// Go there
				Log.d("MAP", "Go there clicked");
				dialog.dismiss();
				break;

			default:
				break;
			}
		}

	}

	/**
	 * Called when one of the Rating Dialog button is clicked.
	 * 
	 */
	private class RatingDialogListener implements
	DialogInterface.OnClickListener {
		private RatingDialog.Builder builder;
		private Meal meal;
		private float rating;

		public RatingDialogListener() {
		}

		public RatingDialogListener(RatingDialog.Builder b, Meal m, float r) {
			builder = b;
			meal = m;
			rating = r;
		}

		@Override
		public void onClick(DialogInterface dialog, int code) {
			switch (code) {

			case DialogInterface.BUTTON_POSITIVE:
				rating = builder.getSubmittedRating();
				Log.d("RATING", "Rating submitted : " + rating);
				dialog.dismiss();
				mController.setRating((float) rating, meal);
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				dialog.dismiss();
				break;

			default:
				break;
			}
		}

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case SUGGESTIONS_REQUEST_CODE: // Result from the Suggestions class
			Log.d("SUGGESTIONS", "OnActivityResult");

			if (resultCode == Activity.RESULT_OK) {
				Bundle extras = data.getExtras();
				if (extras != null) {

					@SuppressWarnings("unchecked")
					ArrayList<Meal> list = (ArrayList<Meal>) extras
					.getSerializable("org.pocketcampus.suggestions.meals");
					Log.d("SUGGESTIONS", "Meals in return : " + list.size());
					
					showMenusBySuggestions(list);

				} else {
					Log.d("SUGGESTIONS", "No extras !");
				}
			} else {
				Log.d("SUGGESTIONS", "RESULT_PAS_OK !");
			}
			break;
		}
	}

	IRatableViewLabeler<Meal> mMealLabeler = new IRatableViewLabeler<Meal>() {

		@Override
		public String getTitle(Meal meal) {
			return meal.getName();
		}

		@Override
		public String getDescription(Meal meal) {
			return meal.getMealDescription();
		}

		@Override
		public float getRating(Meal meal) {
			return (float) meal.getRating().getRatingValue();
		}

		@Override
		public int getNbVotes(Meal meal) {
			return meal.getRating().getNbVotes();
		}

		@Override
		public String getRestaurantName(Meal meal) {
			return meal.getRestaurant().getName();
		}
	};
	
	ILabeler<Sandwich> mSandwichLabeler = new ILabeler<Sandwich>() {

		@Override
		public String getLabel(Sandwich sandwich) {
			return sandwich.getName();
		}
		
	};

	IRatableViewConstructor mViewConstructor = new IRatableViewConstructor() {

		@Override
		public View getNewView(Object currentObject, Context context,
				IRatableViewLabeler<? extends Object> labeler, int position) {

			return new RatableView(currentObject, context, labeler,
					mOnLineClickListener, mOnRatingClickListener, position);
		}
	};

}

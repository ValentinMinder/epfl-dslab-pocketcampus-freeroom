package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.dialog.MenuDialog;
import org.pocketcampus.android.platform.sdk.ui.dialog.RatingDialog;
import org.pocketcampus.android.platform.sdk.ui.element.RatableView;
import org.pocketcampus.android.platform.sdk.ui.element.TextViewElement;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewConstructor;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewLabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.ExpandableListViewElement;
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
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

/**
 * The Main View of the Food plugin, first displayed when accessing Food.
 * 
 * Displays menus by restaurants, preferences, suggestions and ratings, as well
 * as sandwiches
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class FoodMainView extends PluginView implements IFoodMainView {
	/** Main Activity */
	private Activity mActivity;

	/* MVC */
	/** The controller that does the interface between model and view */
	private FoodController mController;
	/** The corresponding model */
	private IFoodModel mModel;

	/* Layout */
	/** A simple full screen layout */
	private StandardTitledLayout mLayout;

	/** The main list with menus and sandwiches */
	private RatableExpandableListViewElement mExpandableList;

	/** The main list with suggestions and ratings */
	private RatableListViewElement mList;

	/* Constants */
	private final int SUGGESTIONS_REQUEST_CODE = 1;

	/* Listeners */
	/** Listener for when you click on a line in the list */
	private OnItemClickListener mOnLineClickListener;
	/** Listener for when you click on a rating in the list */
	private OnItemClickListener mOnRatingClickListener;

	/** The action bar displayed in the food plugin */
	private ActionBar mActionBar;

	/** The action shown in action bar to toggle menus by restaurants or ratings */
	private ShowByRestaurantOrRatingsAction showAllMenusAction;

	/**
	 * Keeps in memory whether we are coming back from choosing restaurant
	 * preferences
	 */
	private boolean backFromPreferences;

	/**
	 * Defines what the main controller is for this view.
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

		// Ugly, but works for now
		backFromPreferences = false;

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardTitledLayout(this);
		mLayout.hideTitle();

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		mExpandableList = new RatableExpandableListViewElement(this);

		mList = new RatableListViewElement(this);

		// We need to force the display before asking the controller for the
		// data,
		// as the controller may take some time to get it.
		displayData();
	}

	/**
	 * Called when this view is accessed after already having been initialized
	 * before
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("ACTIVITY", "onRestart");
		if (backFromPreferences) {
			refreshDisplay();
		}
	}

	/**
	 * Initiates request for the restaurant, meal and sandwich data
	 */
	private void displayData() {
		mLayout.setText(getResources().getString(R.string.food_loading));
		mController.getRestaurants();
		mController.getMeals();
		mController.getSandwiches();
		mController.getHasVoted();
	}

	/**
	 * Refreshes the display after some changes, e.g. preferences or suggestions
	 */
	private void refreshDisplay() {
		showMenusByRestaurants();
		backFromPreferences = false;
	}

	/**
	 * Main Food Options menu contains access to Meals by restaurants, ratings,
	 * Sandwiches, Suggestions and Settings
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.food_main, menu);
		return true;
	}

	/**
	 * Decides what happens when the options menu is opened and an option is
	 * chosen (what view to display)
	 */
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.food_by_meals) {
			showMenusByRestaurants();
		} else if (item.getItemId() == R.id.food_by_sandwiches) {
			showSandwiches();
		} else if (item.getItemId() == R.id.food_by_suggestions) {
			// Extras to add to the Intent
			ArrayList<Meal> meals = (ArrayList<Meal>) mModel.getMeals();
			// Intent to start the SuggestionsView
			Intent suggestions = new Intent(getApplicationContext(),
					FoodSuggestionsView.class);
			suggestions.putExtra("org.pocketcampus.suggestions.meals", meals);
			startActivityForResult(suggestions, SUGGESTIONS_REQUEST_CODE);

		} else if (item.getItemId() == R.id.food_by_settings) {
			backFromPreferences = true;
			Intent settings = new Intent(getApplicationContext(),
					FoodPreferencesView.class);
			startActivity(settings);
		}
		return true;
	}

	/**
	 * Called when the list of restaurants has been updated
	 */
	public void restaurantsUpdated() {
		Log.d("RESTAURANT", "Restaurants updated");
	}

	/**
	 * Called when the list of menus has been updated Displays the view by
	 * restaurants.
	 */
	@Override
	public void menusUpdated() {
		showMenusByRestaurants();
	}

	/**
	 * Creates a menu dialog for a particular meal
	 * 
	 * @param meal
	 */
	public void menuDialog(Meal meal) {
		// Create the Builder for the Menu dialog
		MenuDialog.Builder b = new MenuDialog.Builder(mActivity);
		b.setCanceledOnTouchOutside(true);

		// Set different values for the dialog
		b.setTitle(meal.getRestaurant().getName() + " - " + meal.getName());
		b.setDescription(meal.getMealDescription());
		b.setRating(mModel.getHasVoted(), (float) 0.0, meal.getRating()
				.getNumberOfVotes());

		b.setFirstButton(R.string.food_menu_dialog_firstButton,
				new MenuDialogListener(b, meal));
		b.setSecondButton(R.string.food_menu_dialog_secondButton,
				new MenuDialogListener(b, meal));
		// b.setThirdButton(R.string.food_menu_dialog_thirdButton,
		// new MenuDialogListener(b, meal));

		// Create the dialog and display it
		MenuDialog dialog = b.create();
		dialog.show();
	}

	/**
	 * Called when the ratings have been updated Refreshes the view with the new
	 * ratings
	 */
	@Override
	public void ratingsUpdated() {
		Log.d("RATING", "All Ratings updated");
		if (mExpandableList.getAdapter() != null) {
			mExpandableList.notifyDataSetChanged();
		}
		if (mList.getAdapter() != null) {
			mList.notifyDataSetChanged();
		}
	}

	/**
	 * Creates a rating dialog for a particular meal
	 * 
	 * @param meal
	 */
	public void ratingDialog(Meal meal, long rating) {
		if (!mModel.getHasVoted()) {
			// Create the Builder for the Rating dialog
			RatingDialog.Builder b = new RatingDialog.Builder(mActivity);

			// Set different values for the dialog
			b.setTitle(R.string.food_rating_dialog_title);
			b.setOkButton(R.string.food_rating_dialog_OK,
					new RatingDialogListener(b, meal, rating));
			b.setCancelButton(R.string.food_rating_dialog_cancel,
					new RatingDialogListener());

			// Create the dialog and display it
			RatingDialog dialog = b.create();
			dialog.show();
		} else {
			Toast.makeText(
					this,
					getResources()
							.getString(R.string.food_rating_already_voted),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Called after a vote has been cast by a user to check whether it was
	 * successful
	 * 
	 * @param status
	 *            what the server returned upon submitting the rating
	 */
	@Override
	public void ratingSubmitted(SubmitStatus status) {
		Log.d("RATING", "One Rating updated");

		// Toast with the status
		if (status.equals(SubmitStatus.VALID)) {
			Log.d("RATING", "Valid");
			Toast.makeText(this, R.string.food_rating_valid, Toast.LENGTH_SHORT)
					.show();
			// Update the Ratings
			mController.getRatings();
			mModel.setHasVoted(true);
		} else if (status.equals(SubmitStatus.ALREADY_VOTED)) {
			Log.d("RATING", "Already Voted");
			Toast.makeText(this, R.string.food_rating_already_voted,
					Toast.LENGTH_SHORT).show();
		} else if (status.equals(SubmitStatus.TOO_EARLY)) {
			Log.d("RATING", "Too Early");
			Toast.makeText(this, R.string.food_rating_too_early,
					Toast.LENGTH_SHORT).show();
		} else if (status.equals(SubmitStatus.ERROR)) {
			Log.d("RATING", "Error");
			Toast.makeText(this, R.string.food_rating_error, Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * Called when the sandwiches list is updated
	 */
	@Override
	public void sandwichesUpdated() {
		Log.d("SANDWICHES", "Sandwiches updated");
	}

	/**
	 * Displays a toast when an error happens upon contacting the server
	 */
	@Override
	public void networkErrorHappened() {
		// Toast toast = Toast.makeText(getApplicationContext(),
		// getString(R.string.food_network_error),
		// Toast.LENGTH_SHORT);
		// TODO: DEPENDS ON THE REQUEST
		// toast.show();
	}

	/**
	 * Updates the view with what has to be currently displayed
	 */
	public void updateView() {
		// mList.getAdapter().removeSections();
		// switch (currentDisplayType_) {
		// case Restaurants:
		// showMenusByRestaurants();
		// break;
		// case Ratings:
		// showMenusByRatings();
		// break;
		// case Sandwiches:
		// showSandwiches();
		// break;
		// case Suggestions:
		// showMenusBySuggestions();
		// break;
		// }
		// ownerActivity_.refreshActionBar(currentDisplayType_);
	}

	/**
	 * Sets the listeners for when you click on a view, when you are displaying
	 * menus by restaurants.
	 * 
	 * @param mealHashMap
	 *            the <code>HashMap</code> containing the daily menus
	 */
	public void setHashMapOnClickListeners(
			final HashMap<String, Vector<Meal>> mealHashMap) {
		// Create Listeners
		mOnLineClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int positionInSection, long arg3) {

				final Meal meal = mealHashMap.get(v.getTag()).get(
						positionInSection);
				menuDialog(meal);
			}
		};

		mOnRatingClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View okButton,
					int positionInSection, long rating) {

				final Meal meal = mealHashMap.get(okButton.getTag()).get(
						positionInSection);

				ratingDialog(meal, rating);
			}
		};
	}

	/**
	 * Set the listeners for when you click on a view, when you are displaying a
	 * simple list of menus.
	 * 
	 * @param mealList
	 *            the corresponding list of menus
	 */
	public void setListOnClickListeners(final List<Meal> mealList,
			RatableListViewElement l) {
		// Create Listeners
		mOnLineClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {

				final Meal meal = mealList.get(position);
				menuDialog(meal);
			}
		};

		mOnRatingClickListener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View okButton,
					int position, long rating) {

				final Meal meal = mealList.get(position);

				ratingDialog(meal, rating);
			}
		};

		l.setOnLineClickListener(mOnLineClickListener);
		l.setOnRatingClickListener(mOnRatingClickListener);

	}

	/**
	 * Shows menus sorted by Restaurants
	 */
	public void showMenusByRestaurants() {
		final HashMap<String, Vector<Meal>> mealHashMap = mModel
				.getMealsByRestaurants(this);
		Log.d("MEALS", "Size of list of meals : " + mealHashMap.size());

		if (mealHashMap != null) {
			if (mActionBar == null) {
				mActionBar = getActionBar();
			}

			if (showAllMenusAction == null || !showAllMenusAction.isShown()) {
				showAllMenusAction = new ShowByRestaurantOrRatingsAction();
				mActionBar.addAction(showAllMenusAction, 0);
			} else {
				showAllMenusAction.setIsRestaurant(true);
			}

			// Iterate over the different restaurant menus
			mLayout.removeFillerView();

			if (!mealHashMap.isEmpty()) {

				// Filtering restaurant that the user doesn't want to display
				mExpandableList = new RatableExpandableListViewElement(this,
						mealHashMap, mMealLabeler, mMealsViewConstructor);

				setHashMapOnClickListeners(mealHashMap);

				// Hide the text that says the list is empty
				mLayout.hideText();
				// Set the title to Restaurants
				mLayout.setTitle(this.getString(R.string.food_by_restaurants));
				// Add the list containing the meals
				mLayout.addFillerView(mExpandableList);
			} else {
				// Set the centered text to empty menus
				mLayout.setText(getString(R.string.food_no_menus));
				// Hide the title as there is no content
				mLayout.hideTitle();
			}
		}
	}

	/**
	 * Shows menus sorted by Ratings
	 */
	public void showMenusByRatings() {
		List<Meal> mealsByRatings = mModel.getMealsByRatings();
		Log.d("RATING", "Size of meals list : " + mealsByRatings.size());

		if (showAllMenusAction == null) {
			showAllMenusAction = new ShowByRestaurantOrRatingsAction();
		}
		showAllMenusAction.setIsRestaurant(false);

		if (mealsByRatings != null && !mealsByRatings.isEmpty()) {
			mLayout.removeFillerView();

			// Create a new list by ratings
			mList = new RatableListViewElement(this, mealsByRatings,
					mMealWithRestaurantLabeler);

			setListOnClickListeners(mealsByRatings, mList);

			// Hide the text that says the list is empty
			mLayout.hideText();
			mLayout.setTitle(getString(R.string.food_by_ratings));
			mLayout.addFillerView(mList);
		} else {
			mLayout.removeFillerView();
			mLayout.setText(getString(R.string.food_no_menus));
			mLayout.hideTitle();
		}
	}

	/**
	 * Shows menus sorted by Suggestions
	 * 
	 * @param mealsBySuggestions
	 *            the list coming from the suggestions activity that has to be
	 *            displayed
	 */
	public void showMenusBySuggestions(ArrayList<Meal> mealsBySuggestions) {
		removeShowAllMenusAction();

		if (mLayout == null) {
			mLayout = new StandardTitledLayout(this);
		}

		if (mActionBar == null) {
			mActionBar = getActionBar();
		}

		showAllMenusAction = new ShowByRestaurantOrRatingsAction();
		showAllMenusAction.setIsRestaurant(false);
		mActionBar.addAction(showAllMenusAction, 0);

		if (mealsBySuggestions != null && !mealsBySuggestions.isEmpty()) {
			Log.d("RATING", "Size of meals by suggestions list : "
					+ mealsBySuggestions.size());

			mLayout.removeFillerView();
			mList = new RatableListViewElement(this, mealsBySuggestions,
					mMealWithRestaurantLabeler);

			setListOnClickListeners(mealsBySuggestions, mList);

			// Hide the text that says the list is empty
			mLayout.hideText();
			mLayout.setTitle(getString(R.string.food_by_suggestions));
			mLayout.addFillerView(mList);
		} else {
			mLayout.setText(getString(R.string.food_no_menus));
			mLayout.hideTitle();
		}
	}

	/**
	 * Shows the list of Sandwiches by Restaurants
	 */
	public void showSandwiches() {
		removeShowAllMenusAction();

		final HashMap<String, Vector<Sandwich>> mSandwiches = mModel
				.getSandwiches();
		Log.d("SANDWICHES", "Size of Sandwiches list : " + mSandwiches.size());

		if (mSandwiches != null) {

			mLayout.removeFillerView();

			ExpandableListViewElement mList = new ExpandableListViewElement(
					this, mSandwiches, mSandwichLabeler,
					mSandwichViewConstructor);

			if (!mSandwiches.isEmpty()) {
				mLayout.hideText();
				mLayout.setTitle(this.getString(R.string.food_by_sandwiches));
				mLayout.addFillerView(mList);
			} else {
				mLayout.setText(getString(R.string.food_no_sandwiches));
				mLayout.hideTitle();
			}
		}
	}

	/**
	 * Removes the button in the action bar to toggle menus by restaurant or
	 * ratings.
	 */
	public void removeShowAllMenusAction() {
		if (showAllMenusAction.isShown()) {
			if (mActionBar == null) {
				mActionBar = getActionBar();
			}
			mActionBar.removeActionAt(0);
			if (showAllMenusAction != null) {
				showAllMenusAction.setShown(false);
			}
		}
	}

	/**
	 * Called when one of the Menu Dialog buttons is clicked.
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
				// Log.d("PICTURES", "Picture taken");
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
	 * Called when one of the Rating Dialog buttons is clicked.
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

	/**
	 * Called when coming back from another activity that was called with an
	 * intent and from which we are expecting a result
	 * 
	 * @param requestCode
	 *            what request this result corresponds to
	 * @param resultCode
	 *            the status of the result
	 * @param data
	 *            the information gotten from the activity
	 */
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

	/**
	 * The labeler for a meal, to tell how it has to be displayed in a generic
	 * view.
	 */
	IRatableViewLabeler<Meal> mMealLabeler = new IRatableViewLabeler<Meal>() {

		/**
		 * Returns the title of a meal
		 * 
		 * @param meal
		 *            the meal to be displayed
		 * @return the title of the meal
		 */
		@Override
		public String getLabel(Meal meal) {
			return meal.getName();
		}

		/**
		 * Returns the description of a meal
		 * 
		 * @param meal
		 *            the meal to be displayed
		 * @return the description for the meal
		 */
		@Override
		public String getDescription(Meal meal) {
			return meal.getMealDescription();
		}

		/**
		 * Returns the Rating of a meal
		 * 
		 * @param meal
		 *            the meal to be displayed
		 * @return the current rating for the meal
		 */
		@Override
		public float getRating(Meal meal) {
			return (float) meal.getRating().getRatingValue();
		}

		/**
		 * Returns the Number Of Votes for a meal
		 * 
		 * @param meal
		 *            the meal to be displayed
		 * @return the number of votes for the meal
		 */
		@Override
		public int getNumberOfVotes(Meal meal) {
			return meal.getRating().getNumberOfVotes();
		}

		/**
		 * Returns the name of the Restaurant the meal is available at.
		 * 
		 * @param meal
		 *            the meal to be displayed
		 * @return the restaurant at which it is available
		 */
		@Override
		public String getPlaceName(Meal meal) {
			return meal.getRestaurant().getName();
		}
	};

	/**
	 * The labeler for a meal, to tell how it has to be displayed in a generic
	 * view.
	 */
	IRatableViewLabeler<Meal> mMealWithRestaurantLabeler = new IRatableViewLabeler<Meal>() {

		/**
		 * Returns the title of a meal
		 * 
		 * @param meal
		 *            the meal to be displayed
		 * @return the title of the meal
		 */
		@Override
		public String getLabel(Meal meal) {
			return meal.getName() + " @ " + meal.getRestaurant().getName();
		}

		/**
		 * Returns the description of a meal
		 * 
		 * @param meal
		 *            the meal to be displayed
		 * @return the description for the meal
		 */
		@Override
		public String getDescription(Meal meal) {
			return meal.getMealDescription();
		}

		/**
		 * Returns the Rating of a meal
		 * 
		 * @param meal
		 *            the meal to be displayed
		 * @return the current rating for the meal
		 */
		@Override
		public float getRating(Meal meal) {
			return (float) meal.getRating().getRatingValue();
		}

		/**
		 * Returns the Number Of Votes for a meal
		 * 
		 * @param meal
		 *            the meal to be displayed
		 * @return the number of votes for the meal
		 */
		@Override
		public int getNumberOfVotes(Meal meal) {
			return meal.getRating().getNumberOfVotes();
		}

		/**
		 * Returns the name of the Restaurant the meal is available at.
		 * 
		 * @param meal
		 *            the meal to be displayed
		 * @return the restaurant at which it is available
		 */
		@Override
		public String getPlaceName(Meal meal) {
			return meal.getRestaurant().getName();
		}
	};

	/**
	 * The labeler for a Sandwich, to tell how it has to be displayed in a
	 * generic view.
	 */
	IRatableViewLabeler<Sandwich> mSandwichLabeler = new IRatableViewLabeler<Sandwich>() {

		/**
		 * Returns the sandwich name
		 * 
		 * @param sandwich
		 *            The sandwich to be displayed
		 * @return the sandwich name
		 */
		@Override
		public String getLabel(Sandwich sandwich) {
			return sandwich.getName();
		}

		/**
		 * Returns the sandwich description
		 * 
		 * @param sandwich
		 *            The sandwich to be displayed
		 * @return the sandwich description (here empty)
		 */
		@Override
		public String getDescription(Sandwich sandwich) {
			return "";
		}

		/**
		 * Returns the sandwich rating
		 * 
		 * @param sandwich
		 *            The sandwich to be displayed
		 * @return the sandwich rating (here null)
		 */
		@Override
		public float getRating(Sandwich sandwich) {
			return (float) 0;
		}

		/**
		 * Returns the sandwich name number of votes
		 * 
		 * @param sandwich
		 *            The sandwich to be displayed
		 * @return the sandwich number of votes (here 0)
		 */
		@Override
		public int getNumberOfVotes(Sandwich sandwich) {
			return 0;
		}

		/**
		 * Returns the restaurant name where the sandwich is available
		 * 
		 * @param sandwich
		 *            The sandwich to be displayed
		 * @return the restaurant name
		 */
		@Override
		public String getPlaceName(Sandwich sandwich) {
			return sandwich.getRestaurant().getName();
		}
	};

	/**
	 * The constructor for a Meal View to be displayed in the list
	 */
	IRatableViewConstructor mMealsViewConstructor = new IRatableViewConstructor() {

		@Override
		public View getNewView(Object currentObject, Context context,
				IRatableViewLabeler<? extends Object> labeler, int position) {

			return new RatableView(currentObject, context, labeler,
					mOnLineClickListener, mOnRatingClickListener, position);
		}
	};

	/**
	 * The constructor for a Sandwich View to be displayed in the list
	 */
	IRatableViewConstructor mSandwichViewConstructor = new IRatableViewConstructor() {

		@Override
		public View getNewView(Object currentObject, Context context,
				IRatableViewLabeler<? extends Object> labeler, int position) {

			return new TextViewElement(currentObject, context, labeler, null,
					null, position);
		}
	};

	@Override
	public void networkErrorHappened(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Takes care of showing the "show by restaurants" or "show by ratings"
	 * button in the Action Bar
	 * 
	 * @author Elodie <elodienilane.triponez@epfl.ch>
	 * 
	 */
	private class ShowByRestaurantOrRatingsAction implements Action {
		private boolean mButtonByRestaurants;
		private boolean mIsShown;

		ShowByRestaurantOrRatingsAction() {
			mButtonByRestaurants = true;
			mIsShown = true;
		}

		@Override
		public int getDrawable() {
			if (mButtonByRestaurants) {
				return R.drawable.food_menus_by_ratings;
			} else {
				return R.drawable.food_menus_by_restaurant;
			}
		}

		@Override
		public void performAction(View view) {
			mButtonByRestaurants = !mButtonByRestaurants;
			mActionBar.removeActionAt(0);
			mActionBar.addAction(this, 0);
			if (mButtonByRestaurants) {
				// if (isSandwichDisplay_) {
				// resetScreen();
				// isSandwichDisplay_ = false;
				// }
				showMenusByRestaurants();
			} else {
				showMenusByRatings();
			}
			// displayView();
			// foodDisplayHandler_.refreshView();
		}

		/**
		 * Returns whether or not the button in the action bar is shown
		 */
		public boolean isShown() {
			return mIsShown;
		}

		public void setShown(boolean show) {
			mIsShown = show;
		}

		public void setIsRestaurant(boolean isRestaurants) {
			mButtonByRestaurants = isRestaurants;
		}
	}
}

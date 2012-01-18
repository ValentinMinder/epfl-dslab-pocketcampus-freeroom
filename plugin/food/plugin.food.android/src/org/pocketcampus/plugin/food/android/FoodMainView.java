package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.dialog.MenuDialog;
import org.pocketcampus.android.platform.sdk.ui.dialog.RatingDialog;
import org.pocketcampus.android.platform.sdk.ui.element.RatableView;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewConstructor;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewLabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.RatableExpandableListViewElement;
import org.pocketcampus.android.platform.sdk.ui.list.RatableListViewElement;
import org.pocketcampus.plugin.food.android.iface.IFoodMainView;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.shared.Meal;
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
 * Displays menus by restaurants, preferences, suggestions and ratings
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class FoodMainView extends PluginView implements IFoodMainView {
	/** Main Activity */
	private Activity mActivity;

	/* MVC */
	/** The controller that does the interface between Model and View */
	private FoodController mController;
	/** The corresponding model */
	private IFoodModel mModel;

	/* Layout */
	/** A simple full screen layout */
	private StandardTitledLayout mLayout;

	/** The main list with menus */
	private RatableExpandableListViewElement mExpandableList;

	/** The main list with suggestions and ratings */
	private RatableListViewElement mList;

	/* Constants */
	/** Code used to make a request to the suggestions activity */
	private final int SUGGESTIONS_REQUEST_CODE = 1;

	/* Listeners */
	/** Listener for when you click on a line in the list */
	private OnItemClickListener mOnLineClickListener;

	/** Listener for when you click on a rating in the list */
	private OnItemClickListener mOnRatingClickListener;

	/** The action bar displayed in the food plugin */
	private ActionBar mActionBar;

	/** The action shown in action bar to toggle menus by restaurants or ratings */
	private ShowAllAction mShowAllMenusAction;
	/** The action shown in action bar to toggle menus by restaurants or ratings */
	private ShowByRestaurantOrRatingsAction mShowMenusOrRatingAction;
	/** The action shown in action bar when suggestions are displayed */
	private ShowBySuggestionsAction mShowSuggestionsAction;

	/**
	 * Keeps in memory whether we are coming back from choosing restaurant
	 * preferences.
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
		// Tracker
		Tracker.getInstance().trackPageView("food");

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
	 * Initiates request for the restaurant and meal data, as well as whether
	 * the user has already voted today.
	 */
	private void displayData() {
		mLayout.setText(getResources().getString(R.string.food_loading));
		mController.getRestaurants();
		mController.getMeals();
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
	 * Suggestions and Settings
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
		if (item.getItemId() == R.id.food_by_suggestions) {
			// Extras to add to the Intent
			List<Meal> meals = mModel.getMealsByRatings();
			Vector<Meal> mealsV = new Vector<Meal>();
			mealsV.addAll(meals);

			// Intent to start the SuggestionsView
			Intent suggestions = new Intent(getApplicationContext(),
					FoodSuggestionsView.class);
			suggestions.putExtra("org.pocketcampus.suggestions.meals", mealsV);
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
	 * Not used
	 */
	public void restaurantsUpdated() {
	}

	/**
	 * Called when the list of menus has been updated. Displays the view by
	 * restaurants.
	 */
	@Override
	public void menusUpdated() {
		showMenusByRestaurants();
	}

	/**
	 * Creates a menu dialog for a particular meal. Contains the title of the
	 * menu with the restaurant at which it is available, as well as the
	 * content, and buttons to vote for the meal
	 * 
	 * @param meal
	 *            the meal for which to create a dialog
	 */
	public void menuDialog(Meal meal) {
		// Create the Builder for the Menu dialog
		MenuDialog.Builder b = new MenuDialog.Builder(mActivity);
		b.setCanceledOnTouchOutside(true);

		// Set different values for the dialog
		b.setTitle(meal.getName() + " @ " + meal.getRestaurant().getName());
		b.setDescription(meal.getMealDescription());
		b.setRating(mModel.getHasVoted(), (float) 0.0, meal.getRating()
				.getNumberOfVotes());

		b.setFirstButton(R.string.food_menu_dialog_firstButton,
				new MenuDialogListener(b, meal));
		b.setSecondButton(R.string.food_menu_dialog_secondButton,
				new MenuDialogListener(b, meal));

		// Create the dialog and display it
		MenuDialog dialog = b.create();
		dialog.show();
	}

	/**
	 * Called when the ratings have been updated. Refreshes the view with the
	 * new ratings.
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
	 * Creates a rating dialog for a particular meal.
	 * 
	 * @param meal
	 *            the meal for which to create the rating dialog
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
	 * successful.
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
	 * Displays a toast when an error happens upon contacting the server
	 */
	@Override
	public void networkErrorHappened() {
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

			/**
			 * Defines what is to be performed when the user clicks on an
			 * element of the mensu list
			 */
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int positionInSection, long arg3) {

				final Meal meal = mealHashMap.get(v.getTag()).get(
						positionInSection);
				// Tracker
				Tracker.getInstance()
						.trackPageView("food/menus/dialog/" + meal);
				menuDialog(meal);
			}
		};

		mOnRatingClickListener = new OnItemClickListener() {

			/**
			 * Defines what is to be performed when the user clicks on a Rating
			 * Bar in the list
			 */
			@Override
			public void onItemClick(AdapterView<?> adapter, View okButton,
					int positionInSection, long rating) {

				final Meal meal = mealHashMap.get(okButton.getTag()).get(
						positionInSection);

				// Tracker
				Tracker.getInstance().trackPageView(
						"food/menus/dialog/rating/" + meal);

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

			/**
			 * Defines what is to be performed when the user clicks on an
			 * element of the menus list
			 */
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {

				final Meal meal = mealList.get(position);

				// Tracker
				Tracker.getInstance().trackPageView(
						"food/ratingsORsuggestions/dialog/" + meal);
				menuDialog(meal);
			}
		};

		mOnRatingClickListener = new OnItemClickListener() {

			/**
			 * Defines what is to be performed when the user clicks on a
			 * RatingBar in the list view
			 */
			@Override
			public void onItemClick(AdapterView<?> adapter, View okButton,
					int position, long rating) {

				final Meal meal = mealList.get(position);

				// Tracker
				Tracker.getInstance().trackPageView(
						"food/ratingsORsuggestions/dialog/rating" + meal);
				ratingDialog(meal, rating);
			}
		};

		l.setOnLineClickListener(mOnLineClickListener);
		l.setOnRatingClickListener(mOnRatingClickListener);

	}

	/**
	 * Shows menus sorted by Restaurants.
	 */
	public void showMenusByRestaurants() {
		final HashMap<String, Vector<Meal>> mealHashMap = mModel
				.getMealsByRestaurants(this);
		if (mealHashMap != null) {
			if (mActionBar == null) {
				mActionBar = getActionBar();
			}

			// Removes everything
			mActionBar.removeAllActions();

			// Add the action bar's button to expand all menus
			if (mShowAllMenusAction == null) {
				mShowAllMenusAction = new ShowAllAction();
			} else {
				mShowAllMenusAction.setIsShown(false);
			}
			mActionBar.addAction(mShowAllMenusAction, 0);

			// Add the action bar's button to show menus sorted by ratings
			if (mShowMenusOrRatingAction == null
					|| !mShowMenusOrRatingAction.isShown()) {
				mShowMenusOrRatingAction = new ShowByRestaurantOrRatingsAction();
			} else {
				mShowMenusOrRatingAction.setIsRestaurant(true);
			}
			mActionBar.addAction(mShowMenusOrRatingAction, 1);

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
				Date today = new Date();
				int day = today.getDay();
				Log.d("FOOD", "Day is " + day);
				if (day == 0 || day == 6) {
					mLayout.setText(getString(R.string.food_no_menus_week_end));
				} else {
					mLayout.setText(getString(R.string.food_no_menus));
				}
				// Hide the title as there is no content
				mLayout.hideTitle();
			}
		}
	}

	/**
	 * Shows menus sorted by Ratings.
	 */
	public void showMenusByRatings() {
		List<Meal> mealsByRatings = mModel.getMealsByRatings();

		// Remove the action bar's button to expand all menus
		mActionBar.removeAction(mShowAllMenusAction);

		// Add the action bar's button to show the menus sorted by restaurants
		if (mShowMenusOrRatingAction == null) {
			mShowMenusOrRatingAction = new ShowByRestaurantOrRatingsAction();
		}
		mShowMenusOrRatingAction.setIsRestaurant(false);

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
			Date today = new Date();
			int day = today.getDay();
			Log.d("FOOD", "Day is " + day);
			if (day == 0 || day == 6) {
				mLayout.setText(getString(R.string.food_no_menus_week_end));
			} else {
				mLayout.setText(getString(R.string.food_no_menus));
			}
			mLayout.hideTitle();
		}
	}

	/**
	 * Shows menus sorted by Suggestions.
	 * 
	 * @param mealsBySuggestions
	 *            the list coming from the suggestions activity that has to be
	 *            displayed
	 */
	public void showMenusBySuggestions(ArrayList<Meal> mealsBySuggestions) {
		removeOtherActions();

		if (mLayout == null) {
			mLayout = new StandardTitledLayout(this);
		}
		if (mActionBar == null) {
			mActionBar = getActionBar();
		}

		mShowSuggestionsAction = new ShowBySuggestionsAction();
		mActionBar.removeAllActions();
		mActionBar.addAction(mShowSuggestionsAction);

		if (mealsBySuggestions != null && !mealsBySuggestions.isEmpty()) {
			mLayout.removeFillerView();
			mList = new RatableListViewElement(this, mealsBySuggestions,
					mMealWithRestaurantLabeler);

			setListOnClickListeners(mealsBySuggestions, mList);

			// Hide the text that says the list is empty
			mLayout.hideText();
			mLayout.setTitle(getString(R.string.food_by_suggestions));
			mLayout.addFillerView(mList);
		} else {
			Date today = new Date();
			int day = today.getDay();
			Log.d("FOOD", "Day is " + day);
			if (day == 0 || day == 6) {
				mLayout.removeFillerView();
				mLayout.setText(getString(R.string.food_no_menus_week_end));
			} else {
				mLayout.removeFillerView();
				mLayout.setText(getString(R.string.food_no_menus));
			}
			mLayout.hideTitle();
		}
	}

	/**
	 * Removes the button in the action bar to toggle menus by restaurant or
	 * ratings.
	 */
	public void removeOtherActions() {
		if (mActionBar == null) {
			mActionBar = getActionBar();
		}

		mActionBar.removeAllActions();

		// // Remove the expand action
		// mActionBar.removeActionAt(0);
		//
		// if (mShowMenusOrRatingAction.isShown()) {
		//
		// // Remove the restaurants/ratings action
		// mActionBar.removeActionAt(1);
		// if (mShowMenusOrRatingAction != null) {
		// mShowMenusOrRatingAction.setShown(false);
		// }
		// }
	}

	/**
	 * Called when one of the Menu Dialog buttons is clicked.
	 * 
	 */
	private class MenuDialogListener implements DialogInterface.OnClickListener {
		/** Builder for the dialog */
		private MenuDialog.Builder builder;
		/** The meal for which the dialog was displayed */
		private Meal meal;

		/** Constructor */
		public MenuDialogListener(MenuDialog.Builder b, Meal m) {
			builder = b;
			meal = m;
		}

		/**
		 * Defines what is to be performed when the user clicks on the dialog
		 * buttons. (rate, cancel)
		 */
		@Override
		public void onClick(DialogInterface dialog, int code) {
			switch (code) {
			case DialogInterface.BUTTON1:
				// Tracker
				Tracker.getInstance().trackPageView("food/dialog/button/rate");
				// Rate it
				float rating = builder.getSubmittedRating();
				dialog.dismiss();
				mController.setRating((float) rating, meal);
				break;

			case DialogInterface.BUTTON2:
				Tracker.getInstance()
						.trackPageView("food/dialog/button/cancel");
				// Cancel
				dialog.dismiss();
				break;

			case DialogInterface.BUTTON3:
				// Not defined
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
		/** The builder for the rating dialog */
		private RatingDialog.Builder mBuilder;
		/** The meal for which the dialog was displayed */
		private Meal mMeal;
		/** The rating displayed in the dialog */
		private float mRating;

		/** Empty constructor. */
		public RatingDialogListener() {
		}

		/**
		 * Constructor
		 * 
		 * @param builder
		 *            the builder for the rating dialog.
		 * @param meal
		 *            the meal for which the dialog was displayed.
		 * @param rating
		 *            the rating in the dialog.
		 */

		public RatingDialogListener(RatingDialog.Builder builder, Meal meal,
				float rating) {
			mBuilder = builder;
			mMeal = meal;
			mRating = rating;
		}

		/**
		 * Defines what is to be performed when the user click on the dialog
		 * buttons (positive or negative).
		 */
		@Override
		public void onClick(DialogInterface dialog, int code) {
			switch (code) {

			case DialogInterface.BUTTON_POSITIVE:
				Tracker.getInstance().trackPageView(
						"food/dialog/rating/button/rate");
				mRating = mBuilder.getSubmittedRating();
				Log.d("RATING", "Rating submitted : " + mRating);
				dialog.dismiss();
				mController.setRating((float) mRating, mMeal);
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				Tracker.getInstance().trackPageView(
						"food/dialog/rating/button/cancel");
				dialog.dismiss();
				break;

			default:
				break;
			}
		}

	}

	/**
	 * Called when coming back from another activity that was called with an
	 * intent and from which we are expecting a result.
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
	 * Display a message saying that there is nothing to display when an error
	 * occurs while contacting the server.
	 */
	@Override
	public void networkErrorHappened(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Takes care of showing the "show by restaurants" or "show by ratings"
	 * button in the Action Bar
	 * 
	 * @author Elodie <elodienilane.triponez@epfl.ch>
	 * @author Oriane <oriane.rodriguez@epfl.ch>
	 * 
	 */
	private class ShowByRestaurantOrRatingsAction implements Action {
		private boolean mButtonByRestaurants;
		private boolean mIsShown;

		/**
		 * The constructor sets the boolean "shown by restaurant" and "is shown"
		 * to true.
		 */
		public ShowByRestaurantOrRatingsAction() {
			mButtonByRestaurants = true;
			mIsShown = true;
		}

		/**
		 * Returns the resource for the button icon in the action bar.
		 */
		@Override
		public int getDrawable() {
			if (mButtonByRestaurants) {
				return R.drawable.food_menus_by_ratings;
			} else {
				return R.drawable.food_menus_by_restaurant;
			}
		}

		/**
		 * Defines what is to be performed when the user clicks on the button in
		 * the action bar
		 */
		@Override
		public void performAction(View view) {
			mButtonByRestaurants = !mButtonByRestaurants;
			mActionBar.removeAction(this);
			if (mButtonByRestaurants)
				mActionBar.addAction(this, 0);
			else
				mActionBar.addAction(this, 1);

			if (mButtonByRestaurants) {
				Tracker.getInstance().trackPageView(
						"food/actionbar/by/restaurants");
				showMenusByRestaurants();
			} else {
				Tracker.getInstance()
						.trackPageView("food/actionbar/by/ratings");
				showMenusByRatings();
			}
		}

		/**
		 * Returns whether or not the button in the action bar is shown.
		 */
		public boolean isShown() {
			return mIsShown;
		}

		/**
		 * Sets whether the button in the action bar is currently shown.
		 * 
		 * @param show
		 */
		// public void setShown(boolean show) {
		// mIsShown = show;
		// }

		/**
		 * Set whether the button being shown in the action bar is to show by
		 * restaurants
		 * 
		 * @param isRestaurants
		 */
		public void setIsRestaurant(boolean isRestaurants) {
			mButtonByRestaurants = isRestaurants;
		}
	}

	/**
	 * Takes care of showing the "show by restaurants" or "show by ratings"
	 * button in the Action Bar
	 * 
	 * @author Oriane <oriane.rodriguez@epfl.ch>
	 * @author Elodie <elodienilane.triponez@epfl.ch>
	 * 
	 */
	private class ShowBySuggestionsAction implements Action {

		/**
		 * Empty constructor
		 */
		ShowBySuggestionsAction() {
		}

		/**
		 * Returns the resource for the button icon in the action bar.
		 */
		@Override
		public int getDrawable() {
			return R.drawable.food_menus_by_suggestions;
		}

		/**
		 * Defines what is to be performed when the user clicks on the button in
		 * the action bar.
		 */
		@Override
		public void performAction(View view) {
			Tracker.getInstance().trackPageView(
					"food/actionbar/suggestions/back");
			mActionBar.removeAction(this);
			showMenusByRestaurants();
		}
	}

	/**
	 * Opens all restaurants or closes them all.
	 * 
	 * @author Oriane <oriane.rodriguez@epfl.ch>
	 * @author Elodie <elodienilane.triponez@epfl.ch>
	 * 
	 */
	private class ShowAllAction implements Action {
		/** Everything shown or everything closed. */
		private boolean mIsAllShown;

		/**
		 * Empty constructor
		 */
		ShowAllAction() {
			mIsAllShown = false;
		}

		/**
		 * Returns the resource for the button icon in the action bar.
		 */
		@Override
		public int getDrawable() {
			if (mIsAllShown) {
				return R.drawable.food_menus_collapse;
			} else {
				return R.drawable.food_menus_expand;
			}
		}

		/**
		 * Defines what is to be performed when the user clicks on the button in
		 * the action bar.
		 */
		@Override
		public void performAction(View view) {
			if (mExpandableList != null
					&& mExpandableList.getExpandableListAdapter() != null) {
				int i = 0;
				int count = mExpandableList.getExpandableListAdapter()
						.getGroupCount();

				if (mIsAllShown) {
					// Tracker
					Tracker.getInstance().trackPageView(
							"food/actionbar/collapse");
					while (i < count) {
						mExpandableList.collapseGroup(i);
						i++;
					}
				} else {
					// Tracker
					Tracker.getInstance()
							.trackPageView("food/actionbar/expand");
					while (i < count) {
						mExpandableList.expandGroup(i);
						i++;
					}
				}
			}

			mIsAllShown = !mIsAllShown;
			mActionBar.removeActionAt(0);
			mActionBar.addAction(this, 0);
		}

		/**
		 * Sets whether the restaurants are all shown or not.
		 * 
		 * @param isShown
		 */
		public void setIsShown(boolean isShown) {
			mIsAllShown = isShown;
		}
	}
}

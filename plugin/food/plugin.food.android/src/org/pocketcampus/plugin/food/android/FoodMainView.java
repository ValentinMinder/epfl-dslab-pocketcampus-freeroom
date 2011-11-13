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
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewConstructor;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRatableViewLabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.ui.list.ListViewElement;
import org.pocketcampus.android.platform.sdk.ui.list.RatableExpandableListViewElement;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.android.iface.IFoodView;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Restaurant;

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

public class FoodMainView extends PluginView implements IFoodView {
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

	/**
	 * Displays the data For now testing with Restaurants
	 */
	private void displayData() {
		mLayout.setText("No menus");
		// mController.getRestaurantsList();
		mController.getMeals();
	}

	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!",
				Toast.LENGTH_SHORT);
		toast.show();
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

		} else if (item.getItemId() == R.id.food_by_sandwiches) {

		} else if (item.getItemId() == R.id.food_by_suggestions) {
			Intent suggestions = new Intent(getApplicationContext(),
					FoodSuggestionsView.class);
			ArrayList<Meal> meals = (ArrayList<Meal>) mModel.getMeals();

			if (meals == null)
				Log.d("SUGGESTIONS", "Pas de meals envoyés");
			else
				Log.d("SUGGESTIONS", "Extras : " + meals.size());

			suggestions.putExtra("org.pocketcampus.suggestions.meals", meals);
			startActivityForResult(suggestions, SUGGESTIONS_REQUEST_CODE);
		} else if (item.getItemId() == R.id.food_by_settings) {
			Intent settings = new Intent(getApplicationContext(),
					FoodPreferences.class);
			startActivity(settings);
		}

		return true;
	}

	public void restaurantsUpdated() {
		List<Restaurant> mRestaurantList = mModel.getRestaurantsList();
		List<String> mRestaurantStringList = new ArrayList<String>();

		for (Restaurant r : mRestaurantList) {
			mRestaurantStringList.add(r.name);
			Log.d("RESTAURANT", "Restaurant : " + r.name);
		}

		ListViewElement l = new ListViewElement(this, mRestaurantStringList);

		mLayout.removeAllViews();
		mLayout.addView(l);
	}

	@Override
	public void menusUpdated() {
		// Update meals
		final List<Meal> mMealList = mModel.getMeals();

		showMenusByRestaurants();

		// if (mList == null && mMealList != null) {
		// showMenusByRestaurants();
		// } else {
		// mList.setAdapter(new RatableAdapter(this, mMealList, mLabeler));
		// }
		// mLayout.setText("");
		// mLayout.addView(mList);
	}

	public void showMenusByRestaurants() {
		HashMap<String, Vector<Meal>> mealHashMap = mModel
				.getMealsByRestaurants();

		/**
		 * Iterate over the different restaurant menus
		 */
		if (!mealHashMap.isEmpty()) {
			mList = new RatableExpandableListViewElement(this, mealHashMap,
					mLabeler, mViewConstructor);
			mList.setOnRatingClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View okButton,
						int position, long rating) {
					Toast.makeText(FoodMainView.this, "On rating",
							Toast.LENGTH_SHORT).show();
					// final Meal meal = mMealList.get(position);
					//
					// RatingDialog.Builder b = new
					// RatingDialog.Builder(mActivity);
					//
					// b.setTitle(R.string.food_rating_dialog_title);
					//
					// b.setOkButton(R.string.food_rating_dialog_OK,
					// new RatingDialogListener(b, meal, rating));
					// b.setCancelButton(R.string.food_rating_dialog_cancel,
					// new RatingDialogListener());
					//
					// RatingDialog dialog = b.create();
					// dialog.show();

				}
			});

			mLayout.setText("");
			mLayout.addView(mList);
		}
	}

	/**
	 * What happens when you click on a list entry
	 * 
	 * @return
	 */
	public void setOnLineClickListener(final List<Meal> mMealList) {

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

				} else {
					Log.d("SUGGESTIONS", "No extras !");
				}
			} else {
				Log.d("SUGGESTIONS", "RESULT_PAS_OK !");
			}
			break;
		}
	}

	IRatableViewLabeler<Meal> mLabeler = new IRatableViewLabeler<Meal>() {

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
	};

	IRatableViewConstructor mViewConstructor = new IRatableViewConstructor() {

		@Override
		public View getNewView(Object currentObject, Context context,
				IRatableViewLabeler<? extends Object> labeler, int position) {
			mList.setOnLineClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View arg1,
						int position, long rating) {
					Toast.makeText(FoodMainView.this, "On line",
							Toast.LENGTH_SHORT).show();
					// final Meal meal = mMealList.get(position);
					//
					// if (meal != null) {
					// Log.d("MEAL", meal.getName());
					//
					// MenuDialog.Builder b = new MenuDialog.Builder(mActivity);
					// b.setCanceledOnTouchOutside(true);
					//
					// // Set different values for the dialog
					// b.setTitle(meal.getName());
					// b.setDescription(meal.getMealDescription());
					// b.setRating((float) 0.0, meal.getRating().getNbVotes());
					//
					// b.setFirstButton(R.string.food_menu_dialog_firstButton,
					// new MenuDialogListener(b, meal));
					// b.setSecondButton(R.string.food_menu_dialog_secondButton,
					// new MenuDialogListener(b, meal));
					// b.setThirdButton(R.string.food_menu_dialog_thirdButton,
					// new MenuDialogListener(b, meal));
					//
					// MenuDialog dialog = b.create();
					// dialog.show();
					// }
				}
			});

			return new RatableView(currentObject, context, labeler,
					/*elementListener, ratingListener,*/ position);
		}
	};

}

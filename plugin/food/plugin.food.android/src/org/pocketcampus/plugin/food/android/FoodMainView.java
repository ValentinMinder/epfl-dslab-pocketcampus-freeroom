package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.element.ListViewElement;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.android.iface.IFoodView;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Restaurant;
import org.pocketcampus.plugin.food.shared.Sandwich;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

public class FoodMainView extends PluginView implements IFoodView {
	/*MVC*/
	private FoodController mController;
	private IFoodModel mModel;

	/*Layout*/
	private StandardLayout mLayout;

	/*Data*/
	private List<Restaurant> mRestaurantList;
	private List<Meal> mMealsList;
	private List<Sandwich> mSandwichesList;
	
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
		// Get and cast the controller and model
		mController = (FoodController) controller;
		mModel = (FoodModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this);
		
		//initialize data
		this.mMealsList = new ArrayList<Meal>();
		this.mRestaurantList = new ArrayList<Restaurant>();
		this.mSandwichesList = new ArrayList<Sandwich>();

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		// We need to force the display before asking the controller for the
		// data,
		// as the controller may take some time to get it.
		displayData();
	}

	/**
	 * Displays the data
	 * For now testing with Restaurants
	 */
	private void displayData() {
		mLayout.setText("No menus");
		mController.getRestaurantsList();
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
			startActivity(new Intent(this, FoodSuggestionsView.class));
		} else if (item.getItemId() == R.id.food_by_settings) {
			
		}

		return true;
	}
	
	public void restaurantsUpdated() {
		mRestaurantList = mModel.getRestaurantsList();
		List<String> listeuh = new ArrayList<String>();
		
		for(Restaurant r : mRestaurantList) {
			listeuh.add(r.name);
			Log.d("RESTAURANT", "Restaurant : " + r.name);
		}
		
		ListViewElement l = new ListViewElement(this, listeuh);
		
		mLayout.removeAllViews();
		mLayout.addView(l);
	}

	@Override
	public void menusUpdated() {
		// Update meals
		
	}
}

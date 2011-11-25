package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.ui.list.PreferencesListViewElement;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.shared.Restaurant;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Preferences view of the food plugin, displayed when a user wants to
 * filter what Restaurants to display in the different Food lists.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class FoodPreferencesView extends PluginView {
	/* MVC */
	/** The model to which the view is linked */
	private IFoodModel mModel;

	/* Layout */
	/** A simple full screen layout */
	private StandardLayout mLayout;
	/** The list to be displayed in the layout */
	private PreferencesListViewElement mListView;

	/* Preferences */
	/** The pointer to access and modify preferences stored on the phone */
	private SharedPreferences mRestoPrefs;
	/** Interface to modify values in SharedPreferences object */
	private Editor mRestoPrefsEditor;
	/** The name under which the preferences are stored on the phone */
	private static final String RESTO_PREFS_NAME = "RestoPrefs";

	/* Restaurants */
	/** The list of Restaurants the preferences are made on */
	private ArrayList<Restaurant> mRestaurants;

	/* Listener */
	/** Callback for when an item is clicked in the list */
	private OnItemClickListener mListener;

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
		// Get and cast the model
		mModel = (FoodModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this);

		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);

		// We need to force the display before asking the controller for the
		// data, as the controller may take some time to get it.
		displayData();
	}

	/**
	 * Displays the list of Restaurants which the user can choose from
	 */
	private void displayData() {
		// List of Restaurants
		mRestaurants = (ArrayList<Restaurant>) mModel.getRestaurantsList();

		if (mRestaurants != null && !mRestaurants.isEmpty()) {
			mListView = new PreferencesListViewElement(this, mRestaurants,
					restaurantLabeler, RESTO_PREFS_NAME);

			// Set onClickListener
			setOnListViewClickListener();

			mLayout.addView(mListView);

			mRestoPrefs = getSharedPreferences(RESTO_PREFS_NAME, 0);
			mRestoPrefsEditor = mRestoPrefs.edit();
			
			if (mRestoPrefs.getAll().isEmpty()) {
				Log.d("PREFERENCES", "First time instanciatation (FoodPreference)");
				for (Restaurant r : mRestaurants) {
					mRestoPrefsEditor.putBoolean(r.getName(), true);
				}
				mRestoPrefsEditor.commit();
			}
		} else {
			mLayout.setText("No Restaurants");
		}

	}

	/**
	 * Sets what happens when the user clicks on an item in the list of
	 * Restaurants
	 */
	private void setOnListViewClickListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View prefBox,
					int position, long isChecked) {

				if (isChecked == 1) {
					mRestoPrefsEditor.putBoolean(
							mRestaurants.get(position).name, true);
					mRestoPrefsEditor.commit();
				} else {
					mRestoPrefsEditor.putBoolean(
							mRestaurants.get(position).name, false);
					mRestoPrefsEditor.commit();
				}
			}
		});
	}

	/**
	 * The labeler for a Restaurant, to tell how it has to be displayed in a
	 * generic view.
	 */
	ILabeler<Restaurant> restaurantLabeler = new ILabeler<Restaurant>() {

		@Override
		public String getLabel(Restaurant resto) {
			return resto.getName();
		}
	};

//	/**
//	 * The constructor for a Restaurant View to be displayed in the list
//	 */
//	IViewConstructor restaurantConstructor = new IViewConstructor() {
//
//		@Override
//		public View getNewView(Object currentObject, Context context,
//				ILabeler<? extends Object> labeler, int position) {
//			return new PreferencesView(currentObject, context, labeler,
//					RESTO_PREFS_NAME, mListener, position);
//		}
//	};
}
package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.element.PreferencesView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.labeler.IViewConstructor;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.ui.list.PreferencesListViewElement;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.shared.Restaurant;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class FoodPreferencesView extends PluginView {
	/*MVC*/
	private IFoodModel mModel;

	/*Layout*/
	private StandardLayout mLayout;
	private PreferencesListViewElement mListView;

	/*Preferences*/
	private SharedPreferences mRestoPrefs;
	private Editor mRestoPrefsEditor;
	private static final String RESTO_PREFS_NAME = "RestoPrefs";

	/*Restaurants*/
	private ArrayList<Restaurant> mRestaurants;
	
	/*Listener*/
	private OnItemClickListener mListener;

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
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		// Get and cast the model
		mModel = (FoodModel) controller.getModel();

		// The StandardLayout is a RelativeLayout with a TextView in its center.
		mLayout = new StandardLayout(this);

		//List of Restaurants
		mRestaurants = (ArrayList<Restaurant>)mModel.getRestaurantsList();

		if(mRestaurants != null && !mRestaurants.isEmpty()) {

			mListView = new PreferencesListViewElement(this, mRestaurants, restaurantLabeler);

			//ClickLIstener
			//Set onClickListener
			setOnListViewClickListener();

			mLayout.addView(mListView);
			
			// We need to force the display before asking the controller for the
			// data,
			// as the controller may take some time to get it.
			displayData();
			
		} else {
			mLayout.setText("No Restaurants");
		}
		
		// The ActionBar is added automatically when you call setContentView
		setContentView(mLayout);
	}

	/**
	 * Displays the data
	 * For now testing with Restaurants
	 */
	private void displayData() {

		mRestoPrefs = getSharedPreferences(RESTO_PREFS_NAME, 0);
		mRestoPrefsEditor = mRestoPrefs.edit();

		if(mRestoPrefs.getAll().isEmpty()){
			Log.d("PREFERENCES","First time instanciatation (FoodPreference)");
			for(Restaurant r : mRestaurants){
				mRestoPrefsEditor.putBoolean(r.getName(), true);
			}
			mRestoPrefsEditor.commit();
		} 
	}

	private void setOnListViewClickListener(){

		mListView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View prefBox, int position,
					long isChecked) {

				if(isChecked == 1) {
					mRestoPrefsEditor.putBoolean(mRestaurants.get(position).name, true);
					mRestoPrefsEditor.commit();
				} else {
					mRestoPrefsEditor.putBoolean(mRestaurants.get(position).name, false);
					mRestoPrefsEditor.commit();
				}

			}

		});
	}

	
	ILabeler<Restaurant> restaurantLabeler = new ILabeler<Restaurant>() {

		@Override
		public String getLabel(Restaurant resto) {
			return resto.getName();
		}
		
	};
	
	IViewConstructor restaurantConstructor = new IViewConstructor() {

		@Override
		public View getNewView(Object currentObject, Context context,
				ILabeler<? extends Object> labeler, int position) {
			return new PreferencesView(currentObject, context, labeler, mListener, position);
		}
		
	};
	
}
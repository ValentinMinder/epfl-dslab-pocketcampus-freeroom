package org.pocketcampus.plugin.food.android;

import java.util.ArrayList;
import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.food.android.iface.IFoodModel;
import org.pocketcampus.plugin.food.android.iface.IFoodView;
import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.shared.Restaurant;

public class FoodModel extends PluginModel implements IFoodModel {
	private List<Restaurant> mRestaurantsList;
	
	@Override
	protected Class<? extends IView> getViewInterface() {
		return IFoodView.class;
	}
	
	/**
	 * get the list of all meal tags
	 * */
	@Override
	public List<MealTag> getMealTags() {
		List<MealTag> tags = new ArrayList<MealTag>();
		MealTag[] arrayTags = MealTag.values();
		
		for(int i=0; i < arrayTags.length; i++){
			tags.add(arrayTags[i]);
		}
		
		return tags;
	}
	
	/**
	 * get the list of all Restaurants proposing menus
	 * */
	@Override
	public List<Restaurant> getRestaurantsList(){
		return mRestaurantsList == null ? getRestaurants() : mRestaurantsList;
	}

	/**
	 * get the list of all Restaurants on the server
	 * */
	private List<Restaurant> getRestaurants(){
		List<Restaurant> list = new ArrayList<Restaurant>();
		
		//get all Restaurants from server
		
		return list;
	}
	
}

package org.pocketcampus.plugin.food.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.food.android.iface.IFoodController;
import org.pocketcampus.plugin.food.android.req.MealsRequest;
import org.pocketcampus.plugin.food.android.req.RestaurantsRequest;
import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.shared.FoodService.Client;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;

import android.util.Log;

public class FoodController extends PluginController implements IFoodController{

	private FoodModel mModel;
	private Iface mClient;
	private String mPluginName = "food";
	

	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new FoodModel();
		
		// ...as well as initializing the client.
		// The "client" is the connection we use to access the service.
		mClient = (Iface) getClient(new Client.Factory(), mPluginName);
	}
	
	/**
	 * The view will call this in order to register in the model's listener list.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	@Override
	public void getRestaurantsList() {
		Log.d("RESTAURANT", "Sending Restaurants request");
		new RestaurantsRequest().start(this, (Iface) getClient(new Client.Factory(), mPluginName), (Object)null);
	}

	@Override
	public void getMeals() {
		Log.d("MEALS", "Sending Meals request");
		new MealsRequest().start(this, (Iface) getClient(new Client.Factory(), mPluginName), (Object)null);
	}
	
	@Override
	public List<MealTag> getMealTags() {
		return mModel.getMealTags();
	}
		
}

package org.pocketcampus.plugin.food.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.food.android.iface.IFoodController;
import org.pocketcampus.plugin.food.android.req.MealsRequest;
import org.pocketcampus.plugin.food.android.req.RatingsRequest;
import org.pocketcampus.plugin.food.android.req.RestaurantsRequest;
import org.pocketcampus.plugin.food.android.req.SetRatingRequest;
import org.pocketcampus.plugin.food.android.req.VotedRequest;
import org.pocketcampus.plugin.food.android.utils.MealTag;
import org.pocketcampus.plugin.food.shared.FoodService.Client;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.FoodService.setRating_args;
import org.pocketcampus.plugin.food.shared.Meal;

import android.provider.Settings.Secure;
import android.util.Log;

/**
 * Controller for the food plugin. Takes care of interactions between the model
 * and the view and gets information from the server.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class FoodController extends PluginController implements IFoodController {

	/** The plugin's model. */
	private FoodModel mModel;

	/** The name of the plugin */
	private String mPluginName = "food";

	/**
	 * Initializes the plugin with a model and a client.
	 */
	@Override
	public void onCreate() {
		// Initializing the model is part of the controller's job...
		mModel = new FoodModel();

	}

	/**
	 * Returns the model for which this controller works.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	/**
	 * Initiates a request to the server to get the restaurants whose menus are
	 * going to be displayed
	 */
	@Override
	public void getRestaurants() {
		Log.d("RESTAURANT", "Sending Restaurants request");
		new RestaurantsRequest().start(this,
				(Iface) getClient(new Client.Factory(), mPluginName),
				(Object) null);
	}

	/**
	 * Initiates a request to the server to get the meals of all restaurants.
	 */
	@Override
	public void getMeals() {
		Log.d("MEALS", "Sending Meals request");
		new MealsRequest().start(this,
				(Iface) getClient(new Client.Factory(), mPluginName),
				(Object) null);
	}

	/**
	 * Returns all the tags to be used for filtering the menus for suggestions.
	 */
	@Override
	public List<MealTag> getMealTags() {
		return mModel.getMealTags();
	}

	/**
	 * Initiates a request to the server to get the ratings for all meals.
	 */
	@Override
	public void getRatings() {
		new RatingsRequest().start(this,
				(Iface) getClient(new Client.Factory(), mPluginName),
				(Object) null);
	}

	/**
	 * Uploads a user rating to the server using a <code>SetRatingRequest</code>
	 * 
	 * @param rating
	 *            the rating to be uploaded
	 * @param meal
	 *            the meal for which this rating was submitted
	 */
	@Override
	public void setRating(double rating, Meal meal) {
		String deviceID = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		Log.d("RATING",
				"Sending rating : " + rating + " for meal : " + meal.getName()
						+ " and device with ID : " + deviceID);

		setRating_args ratingArgs = new setRating_args(meal.getMealId(), rating, deviceID);

		new SetRatingRequest().start(this,
				(Iface) getClient(new Client.Factory(), mPluginName),
				ratingArgs);
	}

	/**
	 * Initiates a request to the server to check whether the user has already
	 * voted for a meal today
	 */
	@Override
	public void getHasVoted() {
		String deviceID = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);

		new VotedRequest().start(this,
				(Iface) getClient(new Client.Factory(), mPluginName), deviceID);
	}
}

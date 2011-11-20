package org.pocketcampus.plugin.food.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Meal;

import android.util.Log;

/**
 * 
 * A request to the server for all Meals in all Restaurants
 * 
 * @author Elodie (elodienilane.triponez@epfl.ch)
 * @author Oriane (oriane.rodriguez@epfl.ch)
 * 
 */
public class MealsRequest extends
		Request<FoodController, Iface, Object, List<Meal>> {

	/**
	 * Initiate the <code>getMeals</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request. Not used.
	 * @return the list of Meals from the server
	 */
	@Override
	protected List<Meal> runInBackground(Iface client, Object param)
			throws Exception {
		Log.d("MealsRequest", "run");
		return client.getMeals();
	}

	/**
	 * Tell the model the meals have been updated.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the meal list gotten from the server
	 */
	@Override
	protected void onResult(FoodController controller, List<Meal> result) {
		Log.d("MealsRequest", "onResult");
		((FoodModel) controller.getModel()).setMeals(result);
	}

	/**
	 * Notifies the Model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            the controller that initiated the request
	 */
	@Override
	protected void onError(FoodController controller, Exception e) {
		Log.d("NetworkError", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}

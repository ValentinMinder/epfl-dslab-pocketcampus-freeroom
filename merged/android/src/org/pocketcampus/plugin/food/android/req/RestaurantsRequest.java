package org.pocketcampus.plugin.food.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Restaurant;

import android.util.Log;

/**
 * 
 * A request to the server for all Restaurants
 * 
 * @author Elodie (elodienilane.triponez@epfl.ch)
 * @author Oriane (oriane.rodriguez@epfl.ch)
 * 
 */
public class RestaurantsRequest extends
		Request<FoodController, Iface, Object, List<Restaurant>> {

	/**
	 * Initiate the <code>getRestaurants</code> request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request. Not used.
	 * @return the list of restaurants from the server
	 */
	@Override
	protected List<Restaurant> runInBackground(Iface client, Object param)
			throws Exception {
		Log.d("<RestaurantsRequest>:", "Run");
		return client.getRestaurants();
	}

	/**
	 * Set the list of restaurants in the model.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the list of restaurants gotten from the server
	 */
	@Override
	protected void onResult(FoodController controller, List<Restaurant> result) {
		Log.d("<RestaurantsRequest>:", "onResult");
		((FoodModel) controller.getModel()).setRestaurantsList(result);
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
		Log.d("<RestaurantsRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}

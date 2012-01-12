package org.pocketcampus.plugin.food.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Restaurant;

/**
 * 
 * A request to the server for all restaurants.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class RestaurantsRequest extends
		Request<FoodController, Iface, Object, List<Restaurant>> {

	/**
	 * Initiates the <code>getRestaurants</code> request at the server.
	 * 
	 * @param client
	 *            The client that communicates with the server.
	 * @param param
	 *            The parameters to be sent for the request. Not used.
	 * @return The list of restaurants from the server.
	 */
	@Override
	protected List<Restaurant> runInBackground(Iface client, Object param)
			throws Exception {
		return client.getRestaurants();
	}

	/**
	 * Sets the list of restaurants in the model.
	 * 
	 * @param controller
	 *            The controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            The list of restaurants gotten from the server.
	 */
	@Override
	protected void onResult(FoodController controller, List<Restaurant> result) {
		((FoodModel) controller.getModel()).setRestaurantsList(result);
	}

	/**
	 * Notifies the model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            The controller that initiated the request.
	 */
	@Override
	protected void onError(FoodController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}
}

package org.pocketcampus.plugin.food.android.req;

import java.util.Map;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Rating;

/**
 * 
 * A request to the server for all ratings for all meals.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class RatingsRequest extends
		Request<FoodController, Iface, Object, Map<Long, Rating>> {

	/**
	 * Initiates the <code>getRatings</code> request at the server.
	 * 
	 * @param client
	 *            The client that communicates with the server.
	 * @param param
	 *            The parameters to be sent for the request. Not used.
	 * @return The <code>Map</code> of meal hashcodes with their corresponding
	 *         rating from the server.
	 */
	@Override
	protected Map<Long, Rating> runInBackground(Iface client, Object param)
			throws Exception {
		return client.getRatings();
	}

	/**
	 * Updates the model with the ratings gotten from the server.
	 * 
	 * @param controller
	 *            The controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            The ratings list gotten from the server.
	 */
	@Override
	protected void onResult(FoodController controller, Map<Long, Rating> result) {
		((FoodModel) controller.getModel()).setRatings(result);
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

package org.pocketcampus.plugin.food.android.req;

import java.util.Map;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Rating;

import android.util.Log;

/**
 * 
 * A request to the server for all Ratings for all Meals
 * 
 * @author Elodie (elodienilane.triponez@epfl.ch)
 * @author Oriane (oriane.rodriguez@epfl.ch)
 * 
 */
public class RatingsRequest extends
		Request<FoodController, Iface, Object, Map<Integer, Rating>> {

	/**
	 * Initiate the <code>getRatings</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request. Not used.
	 * @return the Map of meal hashcodes with their corresponding rating from
	 *         the server
	 */
	@Override
	protected Map<Integer, Rating> runInBackground(Iface client, Object param)
			throws Exception {
		Log.d("<RatingsRequest>:", "run");
		return client.getRatings();
	}

	/**
	 * Update the model with the ratings gotten from the server.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the ratings list gotten from the server
	 */
	@Override
	protected void onResult(FoodController controller,
			Map<Integer, Rating> result) {
		Log.d("<RatingsRequest>:", "onResult");
		((FoodModel) controller.getModel()).setRatings(result);
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
		Log.d("<RatingsRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

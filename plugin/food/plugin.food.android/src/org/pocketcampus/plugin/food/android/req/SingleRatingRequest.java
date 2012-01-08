package org.pocketcampus.plugin.food.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;

import android.util.Log;

/**
 * 
 * A request to the server for the rating of a single Meal.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SingleRatingRequest extends
		Request<FoodController, Iface, Meal, Rating> {

	/**
	 * Initiates the <code>getRating</code> request at the server.
	 * 
	 * @param client
	 *            The client that communicates with the server.
	 * @param param
	 *            The parameter meal for which we want the Rating.
	 * @return the <code>Rating</code> of the <code>Meal</code> for which we
	 *         wanted an update.
	 */
	@Override
	protected Rating runInBackground(Iface client, Meal param) throws Exception {
		Log.d("<RatingRequest>:", "run");
		if (!param.getClass().equals(Meal.class)) {
			throw new IllegalArgumentException();
		}
		return client.getRating(param);
	}

	/**
	 * Updates the model with the new rating.
	 * 
	 * @param controller
	 *            The controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            The new <code>Rating</code>.
	 */
	@Override
	protected void onResult(FoodController controller, Rating result) {

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

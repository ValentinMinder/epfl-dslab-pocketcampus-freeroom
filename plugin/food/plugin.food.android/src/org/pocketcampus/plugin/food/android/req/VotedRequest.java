package org.pocketcampus.plugin.food.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;

/**
 * 
 * A request to the server to check if a user has voted yet.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class VotedRequest extends
		Request<FoodController, Iface, String, Boolean> {

	/**
	 * Initiatey the <code>hasVoted</code> request at the server.
	 * 
	 * @param client
	 *            The client that communicates with the server.
	 * @param param
	 *            The parameter deviceId of the user phone.
	 * @return the <code>Rating</code> of the <code>Meal</code> for which we
	 *         wanted an update.
	 */
	@Override
	protected Boolean runInBackground(Iface client, String param)
			throws Exception {
		return client.hasVoted(param);
	}

	/**
	 * Updates the model with the voting status.
	 * 
	 * @param controller
	 *            The controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            Whether the user has already voted or not.
	 */
	@Override
	protected void onResult(FoodController controller, Boolean result) {
		((FoodModel) controller.getModel()).setHasVoted(result);
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

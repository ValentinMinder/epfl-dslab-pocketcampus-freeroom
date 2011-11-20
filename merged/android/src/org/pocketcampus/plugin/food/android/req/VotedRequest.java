package org.pocketcampus.plugin.food.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;

import android.util.Log;

/**
 * 
 * A request to the server to check if a user has voted yet
 * 
 * @author Elodie (elodienilane.triponez@epfl.ch)
 * @author Oriane (oriane.rodriguez@epfl.ch)
 * 
 */
public class VotedRequest extends
		Request<FoodController, Iface, String, Boolean> {

	/**
	 * Initiate the <code>hasVoted</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameter deviceId of the user phone.
	 * @return the Rating of the Meal for which we wanted an update
	 */
	@Override
	protected Boolean runInBackground(Iface client, String param)
			throws Exception {
		Log.d("HasVotedRequest", "run");
		return client.hasVoted(param);
	}

	/**
	 * Update the model with the voting status.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            whether the user has already voted or not 
	 */
	@Override
	protected void onResult(FoodController controller, Boolean result) {
		Log.d("HasVotedRequest", "onResult");
		((FoodModel) controller.getModel()).setHasVoted(result);
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

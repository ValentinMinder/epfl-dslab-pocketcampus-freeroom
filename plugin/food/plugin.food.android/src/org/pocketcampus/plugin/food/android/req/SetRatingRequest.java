package org.pocketcampus.plugin.food.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.FoodService.setRating_args;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

/**
 * 
 * A request to the server to submit a user <code>Rating</code>.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SetRatingRequest extends
		Request<FoodController, Iface, setRating_args, SubmitStatus> {

	/**
	 * Initiates the <code>setRating</code> request at the server.
	 * 
	 * @param client
	 *            The client that communicates with the server.
	 * @param setRatingParam
	 *            The parameters to be sent for the request. the meal ID, the
	 *            new Rating, the deviceId of the user.
	 * @return The status of the submission.
	 */
	@Override
	protected SubmitStatus runInBackground(Iface client,
			setRating_args setRatingParam) throws Exception {
		if (!setRatingParam.getClass().equals(setRating_args.class)) {
			throw new IllegalArgumentException();
		}
		return client.setRating(setRatingParam.getMealId(),
				setRatingParam.getRating(), setRatingParam.getDeviceId());
	}

	/**
	 * Used to tell the model what happened during the <code>Rating</code>
	 * upload.
	 * 
	 * @param controller
	 *            The controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            The status of the submission.
	 */
	@Override
	protected void onResult(FoodController controller, SubmitStatus result) {
		((FoodModel) controller.getModel()).setRating(result);
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

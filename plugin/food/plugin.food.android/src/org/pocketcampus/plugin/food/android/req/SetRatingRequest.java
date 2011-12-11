package org.pocketcampus.plugin.food.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.FoodService.setRating_args;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

import android.util.Log;

/**
 * 
 * A request to the server to submit a user Rating
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SetRatingRequest extends
		Request<FoodController, Iface, setRating_args, SubmitStatus> {

	/**
	 * Initiate the <code>setRating</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param setRatingParam
	 *            the parameters to be sent for the request. the meal ID, the
	 *            new Rating, the deviceId of the user
	 * @return the Status of the submission
	 */
	@Override
	protected SubmitStatus runInBackground(Iface client,
			setRating_args setRatingParam) throws Exception {
		Log.d("<SetRatingRequest>:", "run");
		if (!setRatingParam.getClass().equals(setRating_args.class)) {
			throw new IllegalArgumentException();
		}
		return client.setRating(setRatingParam.getMealId(),
				setRatingParam.getRating(), setRatingParam.getDeviceId());
	}

	/**
	 * Tell the model what happened during the Rating upload.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the status of the submission
	 */
	@Override
	protected void onResult(FoodController controller, SubmitStatus result) {
		Log.d("<SetRatingRequest>:", "onResult");
		((FoodModel) controller.getModel()).setRating(result);
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
		Log.d("<SetRatingRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

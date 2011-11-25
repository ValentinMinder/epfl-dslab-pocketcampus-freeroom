package org.pocketcampus.plugin.food.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Sandwich;

import android.util.Log;

/**
 * 
 * A request to the server for all Sandwiches
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SandwichesRequest extends
		Request<FoodController, Iface, Object, List<Sandwich>> {

	/**
	 * Initiate the <code>getSandwiches</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request. Not used.
	 * @return the list of all Sandwiches at all Restaurants
	 */
	@Override
	protected List<Sandwich> runInBackground(Iface client, Object param)
			throws Exception {
		Log.d("<SandwichesRequest>:", "Run");
		return client.getSandwiches();
	}

	/**
	 * Update the model with the Sandwiches gotten from the server.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the list of Sandwiches gotten from the server
	 */
	@Override
	protected void onResult(FoodController controller, List<Sandwich> result) {
		Log.d("<SandwichesRequest>:", "onResult");
		((FoodModel) controller.getModel()).setSandwiches(result);
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
		Log.d("<SandwichesRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

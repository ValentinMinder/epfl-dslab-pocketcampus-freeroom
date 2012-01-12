package org.pocketcampus.plugin.food.android.req;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Meal;

/**
 * A request to the server for all meals in all restaurants
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class MealsRequest extends
		Request<FoodController, Iface, Object, List<Meal>> {

	/**
	 * Initiates the <code>getMeals</code> request at the server.
	 * 
	 * @param client
	 *            The client that communicates with the server.
	 * @param param
	 *            The parameters to be sent for the request. Not used.
	 * @return The list of meals from the server.
	 */
	@Override
	protected List<Meal> runInBackground(Iface client, Object param)
			throws Exception {
		return client.getMeals();
	}

	/**
	 * Tells the model the meals have been updated.
	 * 
	 * @param controller
	 *            The controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            The meal list gotten from the server.
	 */
	@Override
	protected void onResult(FoodController controller, List<Meal> result) {
		((FoodModel) controller.getModel()).setMeals(result, controller);
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
		((FoodModel) controller.getModel()).networkErrorHappened(controller
				.getString(R.string.food_meals_request_error));
		e.printStackTrace();
	}
}

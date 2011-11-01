package org.pocketcampus.plugin.food.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Restaurant;

import android.util.Log;

public class RestaurantsRequest extends
		Request<FoodController, Iface, Object, List<Restaurant>> {

	@Override
	protected List<Restaurant> runInBackground(Iface client, Object param)
			throws Exception {
		Log.d("<RestaurantsRequest>:", "Run");
		return client.getRestaurants();
	}

	@Override
	protected void onResult(FoodController controller, List<Restaurant> result) {
		Log.d("<RestaurantsRequest>:","onResult");
		((FoodModel) controller.getModel()).setRestaurantsList(result);
	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		Log.d("<RestaurantsRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

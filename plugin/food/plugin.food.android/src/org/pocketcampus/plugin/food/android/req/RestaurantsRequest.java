package org.pocketcampus.plugin.food.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Restaurant;

public class RestaurantsRequest extends
		Request<FoodController, Iface, Object, List<Restaurant>> {

	@Override
	protected List<Restaurant> runInBackground(Iface client, Object param)
			throws Exception {
		System.out.println("Getting restaurants");
		return client.getRestaurants();
	}

	@Override
	protected void onResult(FoodController controller, List<Restaurant> result) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		// TODO Auto-generated method stub

	}

}

package org.pocketcampus.plugin.food.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;

public class SingleRatingRequest extends
		Request<FoodController, Iface, Meal, Rating> {

	@Override
	protected Rating runInBackground(Iface client, Meal param) throws Exception {
		System.out.println("Getting rating");
		if (!param.getClass().equals(Meal.class)) {
			throw new IllegalArgumentException();
		}
		return client.getRating(param);
	}

	@Override
	protected void onResult(FoodController controller, Rating result) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		// TODO Auto-generated method stub

	}

}

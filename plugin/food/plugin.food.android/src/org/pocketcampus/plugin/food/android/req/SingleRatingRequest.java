package org.pocketcampus.plugin.food.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;

import android.util.Log;

public class SingleRatingRequest extends
		Request<FoodController, Iface, Meal, Rating> {

	@Override
	protected Rating runInBackground(Iface client, Meal param) throws Exception {
		Log.d("<RatingRequest>:","run");
		if (!param.getClass().equals(Meal.class)) {
			throw new IllegalArgumentException();
		}
		return client.getRating(param);
	}

	@Override
	protected void onResult(FoodController controller, Rating result) {
		Log.d("<RatingRequest>:","onResult");
		//Notifiy the model

	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		Log.d("<RatingsRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

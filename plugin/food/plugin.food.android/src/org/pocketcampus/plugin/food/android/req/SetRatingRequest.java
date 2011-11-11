package org.pocketcampus.plugin.food.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

import android.util.Log;

public class SetRatingRequest extends
		Request<FoodController, Iface, Meal, SubmitStatus> {

	@Override
	protected SubmitStatus runInBackground(Iface client, Meal param) throws Exception {
		Log.d("<SetRatingRequest>:","run");
		if (!param.getClass().equals(Meal.class)) {
			throw new IllegalArgumentException();
		}
		//Device ID
		return client.setRating(param.getRating(), param, "");
	}
	
	@Override
	protected void onResult(FoodController controller, SubmitStatus result) {
		Log.d("<SetRatingRequest>:", "onResult");
		((FoodModel) controller.getModel()).setRating(result);
	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		Log.d("<SetRatingRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

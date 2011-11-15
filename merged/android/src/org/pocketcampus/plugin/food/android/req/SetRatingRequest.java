package org.pocketcampus.plugin.food.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.FoodService.setRating_args;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

import android.util.Log;

public class SetRatingRequest extends
		Request<FoodController, Iface, setRating_args, SubmitStatus> {

	@Override
	protected SubmitStatus runInBackground(Iface client, setRating_args param) throws Exception {
		Log.d("<SetRatingRequest>:","run");
		if (!param.getClass().equals(setRating_args.class)) {
			throw new IllegalArgumentException();
		}
		return client.setRating(param.getRating(), param.getMeal(), param.getDeviceID());
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

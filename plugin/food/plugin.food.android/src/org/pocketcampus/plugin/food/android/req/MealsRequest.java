package org.pocketcampus.plugin.food.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Meal;

import android.util.Log;

public class MealsRequest extends Request<FoodController, Iface, Object, List<Meal>> {

	@Override
	protected List<Meal> runInBackground(Iface client, Object param)
			throws Exception {
		Log.d("MealsRequest","run");
		return client.getMeals();
	}

	@Override
	protected void onResult(FoodController controller, List<Meal> result) {
		Log.d("MealsRequest", "onResult");
		((FoodModel) controller.getModel()).setMeals(result);
	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		Log.d("NetworkError", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

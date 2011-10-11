package org.pocketcampus.plugin.food.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.android.FoodModel;

public class MealsRequest extends Request<FoodController, Iface, Object, List<Meal>> {

	@Override
	protected List<Meal> runInBackground(Iface client, Object param)
			throws Exception {
		System.out.println("Gettings meals");
		return client.getMeals();
	}

	@Override
	protected void onResult(FoodController controller, List<Meal> result) {
		System.out.println("onResult");
		
	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		// TODO Auto-generated method stub
		
	}

}

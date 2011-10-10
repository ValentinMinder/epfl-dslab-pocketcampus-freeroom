package org.pocketcampus.plugin.food.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Meal;

public class MealsRequest extends Request<FoodController, Iface, Object, List<Meal>> {

	@Override
	protected List<Meal> runInBackground(Iface client, Object param)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onResult(FoodController controller, List<Meal> result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		// TODO Auto-generated method stub
		
	}

}

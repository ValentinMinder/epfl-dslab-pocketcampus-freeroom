package org.pocketcampus.plugin.food.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Sandwich;

import android.util.Log;

public class SandwichesRequest extends
		Request<FoodController, Iface, Object, List<Sandwich>> {

	@Override
	protected List<Sandwich> runInBackground(Iface client, Object param)
			throws Exception {
		Log.d("<SandwichesRequest>:", "Run");
		return client.getSandwiches();
	}

	@Override
	protected void onResult(FoodController controller, List<Sandwich> result) {
		Log.d("<SandwichesRequest>:","onResult");
		((FoodModel) controller.getModel()).setSandwiches(result);
	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		Log.d("<SandwichesRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

package org.pocketcampus.plugin.food.android.req;

import java.util.HashMap;
import java.util.Map;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Rating;

public class RatingsRequest extends
		Request<FoodController, Iface, Object, Map<Integer, Rating>> {

	@Override
	protected Map<Integer, Rating> runInBackground(Iface client, Object param)
			throws Exception {
		System.out.println("Getting ratings");
		return client.getRatings();
	}

	@Override
	protected void onResult(FoodController controller,
			Map<Integer, Rating> result) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		// TODO Auto-generated method stub

	}

}

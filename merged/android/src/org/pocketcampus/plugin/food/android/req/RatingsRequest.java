package org.pocketcampus.plugin.food.android.req;

import java.util.Map;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Rating;

import android.util.Log;

public class RatingsRequest extends
		Request<FoodController, Iface, Object, Map<Integer, Rating>> {

	@Override
	protected Map<Integer, Rating> runInBackground(Iface client, Object param)
			throws Exception {
		Log.d("<RatingsRequest>:","run");
		return client.getRatings();
	}

	@Override
	protected void onResult(FoodController controller,
			Map<Integer, Rating> result) {
		Log.d("<RatingsRequest>:","onResult");
		//Notifiy the model
	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		Log.d("<RatingsRequest>:","onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

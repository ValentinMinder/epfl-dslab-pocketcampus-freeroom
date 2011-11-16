package org.pocketcampus.plugin.food.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.food.android.FoodController;
import org.pocketcampus.plugin.food.android.FoodModel;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;
import org.pocketcampus.plugin.food.shared.Meal;

import android.util.Log;

public class VotedRequest extends Request<FoodController, Iface, String, Boolean> {

	@Override
	protected Boolean runInBackground(Iface client, String param)
			throws Exception {
		Log.d("HasVotedRequest","run");
		return client.hasVoted(param);
	}

	@Override
	protected void onResult(FoodController controller, Boolean result) {
		Log.d("HasVotedRequest", "onResult");
		((FoodModel) controller.getModel()).setHasVoted(result);
	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		Log.d("NetworkError", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

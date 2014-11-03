package org.pocketcampus.plugin.food.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.food.android.*;
import org.pocketcampus.plugin.food.android.iface.IFoodView;
import org.pocketcampus.plugin.food.shared.*;
import org.pocketcampus.plugin.food.shared.FoodService.Iface;

/**
 * GetFoodRequest
 * 
 * This class sends an HttpRequest using Thrift to the PocketCampus server
 * in order to get the list of meals in restos.
 * 
 * @author Amer <amer.chamseddine@epfl.ch>
 *
 */
public class GetFoodRequest extends Request<FoodController, Iface, FoodRequest, FoodResponse> {

	private IFoodView caller;
	
	public GetFoodRequest(IFoodView caller) {
		this.caller = caller;
	}
	
	@Override
	protected FoodResponse runInBackground(Iface client, FoodRequest param) throws Exception {
		return client.getFood(param);
	}

	@Override
	protected void onResult(FoodController controller, FoodResponse result) {
		if(result.getStatusCode() == FoodStatusCode.OK) {
			controller.setMealTypePicUrls(result.getMealTypePictureUrls());
			controller.setServerDetectedPriceTarget(result.getUserStatus());
			controller.setEpflMenus(result.getMenu());
			
			keepInCache();
		} else {
			caller.foodServersDown();
		}
	}

	@Override
	protected void onError(FoodController controller, Exception e) {
		if(foundInCache())
			caller.networkErrorCacheExists();
		else
			caller.networkErrorHappened();
		e.printStackTrace();
	}
	
}

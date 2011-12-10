package org.pocketcampus.plugin.bikes.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.bikes.android.BikesController;
import org.pocketcampus.plugin.bikes.android.BikesModel;
import org.pocketcampus.plugin.bikes.shared.BikeService.Iface;
import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;

public class BikesRequest extends Request<BikesController, Iface, Object, List<BikeEmplacement>> {

	@Override
	protected List<BikeEmplacement> runInBackground(Iface client, Object param) throws Exception {
		return client.getBikeStations();
	}

	@Override
	protected void onResult(BikesController controller, List<BikeEmplacement> result) {
		((BikesModel) controller.getModel()).setResults(result);
		
	}

	@Override
	protected void onError(BikesController controller, Exception e) {
		System.err.println("error in bikes request");
		controller.getModel().notifyNetworkError();
		
	}


}

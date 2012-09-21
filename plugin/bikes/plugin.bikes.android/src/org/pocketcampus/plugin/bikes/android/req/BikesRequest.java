package org.pocketcampus.plugin.bikes.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.bikes.android.BikesController;
import org.pocketcampus.plugin.bikes.android.BikesModel;
import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;
import org.pocketcampus.plugin.bikes.shared.BikesService.Iface;

import android.util.Log;

/**
 * A request to the server to get the <code>BikeEmplacement</code>
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public class BikesRequest extends Request<BikesController, Iface, Object, List<BikeEmplacement>> {

	/**
	 * Initiate the <code>getBikeStations</code> Request to the server.
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters sent to the server, useless here
	 * @return the News Items.
	 */
	@Override
	protected List<BikeEmplacement> runInBackground(Iface client, Object param) throws Exception {
		return client.getBikeStations();
	}

	/**
	 * Tells the model that the results have been updated
	 * 
	 * @param controller the controller that will be used to set the results in the model.
	 */
	@Override
	protected void onResult(BikesController controller, List<BikeEmplacement> result) {
		((BikesModel) controller.getModel()).setResults(result);
		
	}

	/**
	 * Notifies the Model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            the controller that initiated the request.
	 */
	@Override
	protected void onError(BikesController controller, Exception e) {
		Log.d("Bikes","error in bikes request");
		controller.getModel().notifyNetworkError();
		
	}


}

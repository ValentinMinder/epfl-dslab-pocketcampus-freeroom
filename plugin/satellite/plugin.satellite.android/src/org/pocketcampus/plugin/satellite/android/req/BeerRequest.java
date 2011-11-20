package org.pocketcampus.plugin.satellite.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.satellite.android.SatelliteController;
import org.pocketcampus.plugin.satellite.android.SatelliteModel;
import org.pocketcampus.plugin.satellite.shared.Beer;
import org.pocketcampus.plugin.satellite.shared.SatelliteService.Iface;

import android.util.Log;

/**
 * Request to the server to get the Beer of the month at Satellite
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class BeerRequest extends
		Request<SatelliteController, Iface, Object, Beer> {

	/**
	 * Initiate the <code>getBeerOfTheMonth</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request. Not used.
	 * @return the Beer of month at Satellite
	 */
	@Override
	protected Beer runInBackground(Iface client, Object param) throws Exception {
		Log.d("<BeerRequest>:", "Run");
		return client.getBeerOfTheMonth();
	}

	/**
	 * Update the model with the Beer gotten from the server.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the Beer gotten from the server
	 */
	@Override
	protected void onResult(SatelliteController controller, Beer result) {
		Log.d("<BeerRequest>:", "onResult");
		((SatelliteModel)controller.getModel()).setBeerOfMonth(result);
	}

	/**
	 * Notifies the Model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            the controller that initiated the request
	 */
	@Override
	protected void onError(SatelliteController controller, Exception e) {
		Log.d("<BeerRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

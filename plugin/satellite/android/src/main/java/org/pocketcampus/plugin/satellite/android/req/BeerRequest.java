package org.pocketcampus.plugin.satellite.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.satellite.android.SatelliteController;
import org.pocketcampus.plugin.satellite.android.SatelliteModel;
import org.pocketcampus.plugin.satellite.shared.Beer;
import org.pocketcampus.plugin.satellite.shared.SatelliteService.Iface;

/**
 * Request to the server to get the beer of the month at Satellite.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class BeerRequest extends
		Request<SatelliteController, Iface, Object, Beer> {

	/**
	 * Initiates the <code>getBeerOfTheMonth</code> request at the server.
	 * 
	 * @param client
	 *            The client that communicates with the server.
	 * @param param
	 *            The parameters to be sent for the request. Not used.
	 * @return The beer of month at Satellite.
	 */
	@Override
	protected Beer runInBackground(Iface client, Object param) throws Exception {
		return client.getBeerOfTheMonth();
	}

	/**
	 * Updates the model with the beer gotten from the server.
	 * 
	 * @param controller
	 *            The controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            The beer gotten from the server.
	 */
	@Override
	protected void onResult(SatelliteController controller, Beer result) {
		((SatelliteModel) controller.getModel()).setBeerOfMonth(result);
	}

	/**
	 * Notifies the model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            The controller that initiated the request.
	 */
	@Override
	protected void onError(SatelliteController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

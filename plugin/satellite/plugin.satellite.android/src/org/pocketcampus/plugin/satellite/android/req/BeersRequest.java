package org.pocketcampus.plugin.satellite.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.satellite.android.SatelliteController;
import org.pocketcampus.plugin.satellite.android.SatelliteModel;
import org.pocketcampus.plugin.satellite.shared.Beer;
import org.pocketcampus.plugin.satellite.shared.SatelliteService.Iface;

import android.util.Log;

/**
 * Request to the server to get the list of beers that Satellite proposes
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class BeersRequest extends
		Request<SatelliteController, Iface, Object, List<Beer>> {

	/**
	 * Initiate the <code>getAllBeers</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request. Not used.
	 * @return the list of all Beers at Satellite
	 */
	@Override
	protected List<Beer> runInBackground(Iface client, Object param)
			throws Exception {
		return client.getAllBeers();
	}

	/**
	 * Update the model with the Beers gotten from the server.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the list of Beers gotten from the server
	 */
	@Override
	protected void onResult(SatelliteController controller, List<Beer> result) {
		Log.d("<BeersRequest>:", "onResult");
//		((SatelliteModel) controller.getModel()).setAllBeers(result);
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
		Log.d("<BeersRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

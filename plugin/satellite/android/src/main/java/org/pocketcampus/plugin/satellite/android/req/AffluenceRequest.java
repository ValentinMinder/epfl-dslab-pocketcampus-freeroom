package org.pocketcampus.plugin.satellite.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.satellite.android.SatelliteController;
import org.pocketcampus.plugin.satellite.android.SatelliteModel;
import org.pocketcampus.plugin.satellite.shared.Affluence;
import org.pocketcampus.plugin.satellite.shared.SatelliteService.Iface;

/**
 * Request to the server to get the current affluence at Satellite.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class AffluenceRequest extends
		Request<SatelliteController, Iface, Object, Affluence> {

	/**
	 * Initiates the <code>getAffluence</code> request at the server.
	 * 
	 * @param client
	 *            The client that communicates with the server.
	 * @param param
	 *            The parameters to be sent for the request. Not used.
	 * @return The current affluence at Satellite.
	 */
	@Override
	protected Affluence runInBackground(Iface client, Object param)
			throws Exception {
		return client.getAffluence();
	}

	/**
	 * Updates the model with the affluence gotten from the server.
	 * 
	 * @param controller
	 *            The controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            The affluence gotten from the server.
	 */
	@Override
	protected void onResult(SatelliteController controller, Affluence result) {
		((SatelliteModel) controller.getModel()).setAffluence(result);
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

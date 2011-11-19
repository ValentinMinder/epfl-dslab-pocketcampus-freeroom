package org.pocketcampus.plugin.satellite.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.satellite.android.SatelliteController;
import org.pocketcampus.plugin.satellite.android.SatelliteModel;
import org.pocketcampus.plugin.satellite.shared.Affluence;
import org.pocketcampus.plugin.satellite.shared.SatelliteService.Iface;

import android.util.Log;

/**
 * Request to the server to get the affluence at Satellite
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class AffluenceRequest extends
		Request<SatelliteController, Iface, Object, Affluence> {

	/**
	 * Initiate the <code>getAffluence</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request. Not used.
	 * @return the current affluence at Satellite
	 */
	@Override
	protected Affluence runInBackground(Iface client, Object param)
			throws Exception {
		Log.d("<AffluenceRequest>:", "Run");
		return client.getAffluence();
	}

	/**
	 * Update the model with the affluence gotten from the server.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the affluence gotten from the server
	 */
	@Override
	protected void onResult(SatelliteController controller, Affluence result) {
		Log.d("<AffluenceRequest>:", "onResult");
		((SatelliteModel) controller.getModel()).setAffluence(result);
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
		Log.d("<AffluenceRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

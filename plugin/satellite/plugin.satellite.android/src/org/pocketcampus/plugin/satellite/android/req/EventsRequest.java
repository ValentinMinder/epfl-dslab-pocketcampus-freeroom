package org.pocketcampus.plugin.satellite.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.satellite.android.SatelliteController;
import org.pocketcampus.plugin.satellite.shared.Event;
import org.pocketcampus.plugin.satellite.shared.SatelliteService.Iface;

import android.util.Log;

/**
 * Request to the server to get the list of next events at Satellite
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class EventsRequest extends
		Request<SatelliteController, Iface, Object, List<Event>> {

	/**
	 * Initiate the <code>getEvents</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request. Not used.
	 * @return the list of all Events at Satellite
	 */
	@Override
	protected List<Event> runInBackground(Iface client, Object param)
			throws Exception {
		Log.d("<EventsRequest>:", "Run");
		return client.getNextEvents();
	}

	/**
	 * Update the model with the Events gotten from the server.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the list of Events gotten from the server
	 */
	@Override
	protected void onResult(SatelliteController controller, List<Event> result) {
		Log.d("<EventsRequest>:", "onResult");
//		((SatelliteModel) controller.getModel()).setEvents(result);
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
		Log.d("<EventsRequest>:", "onError");
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

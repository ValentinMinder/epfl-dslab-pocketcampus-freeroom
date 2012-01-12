package org.pocketcampus.plugin.transport.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.transport.android.TransportController;
import org.pocketcampus.plugin.transport.android.TransportModel;
import org.pocketcampus.plugin.transport.shared.QueryTripsResult;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;
import org.pocketcampus.plugin.transport.shared.TransportService.getTrips_args;

/**
 * A request to the server for the next departures between two stations.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public class NextDeparturesRequest extends
		Request<TransportController, Iface, getTrips_args, QueryTripsResult> {

	/**
	 * Initiates the <code>connections</code> request to the server.
	 * 
	 * @param client
	 *            The client communicating with the server.
	 * @param param
	 *            The parameters to be sent for the request : stations from and
	 *            to
	 * @return The list of connections gotten from the server.
	 */
	@Override
	protected QueryTripsResult runInBackground(Iface client, getTrips_args param)
			throws Exception {
		return client.getTrips(param.getFrom(), param.getTo());
	}

	/**
	 * Tells the model that the connections have been updated.
	 * 
	 * @param controller
	 *            The controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            The connections list gotten from the server.
	 */
	@Override
	protected void onResult(TransportController controller,
			QueryTripsResult result) {
		((TransportModel) controller.getModel()).setConnections(result);
	}

	/**
	 * Notifies the model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            The controller that initiated the request.
	 */
	@Override
	protected void onError(TransportController controller, Exception e) {
		controller.getModel().notifyNetworkError();
		e.printStackTrace();
	}

}

package org.pocketcampus.plugin.transport.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.transport.android.TransportController;
import org.pocketcampus.plugin.transport.android.TransportModel;
import org.pocketcampus.plugin.transport.shared.QueryTripsResult;
import org.pocketcampus.plugin.transport.shared.TransportConnection;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;
import org.pocketcampus.plugin.transport.shared.TransportService.getTrips_args;

import android.util.Log;

/**
 * A request to the server for the next departures between two stations
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 */
public class NextDeparturesRequest
		extends
		Request<TransportController, Iface, getTrips_args, QueryTripsResult> {

	/**
	 * Initiates the <code>connections</code> request to the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request : destination from
	 *            and to
	 * @return the list of connections from the server
	 */
	@Override
	protected QueryTripsResult runInBackground(Iface client, getTrips_args param) throws Exception {
		QueryTripsResult results = client.getTrips(param.getFrom(), param.getTo());
		System.out.println(results);
		
		for(TransportConnection part : results.connections.get(0).getParts()) {
			System.out.println("From: " + part.departureTime+ " " + part.isSetDepartureTime());
			System.out.println("To: " + part.arrivalTime + " " + part.isSetArrivalTime());
			System.out.println("Footway?: " + part.isFoot() + part.isSetFoot());
		}
		
		return results;
	}

	/**
	 * Tells the model that the connections have been updated.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the departures list gotten from the server
	 */
	@Override
	protected void onResult(TransportController controller, QueryTripsResult result) {
		((TransportModel) controller.getModel()).setConnections(result);
	}

	/**
	 * Notifies the Model that an error has occurred while processing the
	 * request.
	 * 
	 * @param controller
	 *            the controller that initiated the request
	 */
	@Override
	protected void onError(TransportController controller, Exception e) {
		controller.getModel().notifyNetworkError();
	}

}

package org.pocketcampus.plugin.transport.android.req;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.transport.android.TransportController;
import org.pocketcampus.plugin.transport.android.TransportModel;
import org.pocketcampus.plugin.transport.shared.QueryConnectionsResult;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;
import org.pocketcampus.plugin.transport.shared.TransportService.connections_args;

import android.util.Log;

/**
 * A request to the server for the next departures between two stations
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public class NextDeparturesFromEPFLRequest
		extends
		Request<TransportController, Iface, connections_args, QueryConnectionsResult> {

	/**
	 * Initiate the <code>connections</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request : destination from and to
	 * @return the list of Meals from the server
	 */
	@Override
	protected QueryConnectionsResult runInBackground(Iface client,
			connections_args param) throws Exception {
		Log.d("TRANSPORT", "run");
		return client.connections(param.getFrom(), param.getTo());
	}

	/**
	 * Tell the model the departures have been updated.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the departures list gotten from the server
	 */
	@Override
	protected void onResult(TransportController controller,
			QueryConnectionsResult result) {
		Log.d("TRANSPORT", "onResult");
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
		Log.d("TRANSPORT", "onError");
		e.printStackTrace();
	}

}

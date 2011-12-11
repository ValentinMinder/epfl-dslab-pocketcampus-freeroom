package org.pocketcampus.plugin.transport.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.transport.android.TransportController;
import org.pocketcampus.plugin.transport.android.TransportModel;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;
import org.pocketcampus.plugin.transport.shared.TransportService.getLocationsFromNames_args;

import android.util.Log;

/**
 * A request to the server for the Locations corresponding to each String in the
 * parameters
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public class LocationsFromNamesRequest
		extends
		Request<TransportController, Iface, getLocationsFromNames_args, List<TransportStation>> {

	/**
	 * Initiate the <code>getLocationsFromNames</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request : a list of String
	 *            for which we want the corresponding Locations
	 * @return the list of Locations from the server
	 */
	@Override
	protected List<TransportStation> runInBackground(Iface client,
			getLocationsFromNames_args param) throws Exception {
		Log.d("TRANSPORT", "run");
		return client.getLocationsFromNames(param.getNames());
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
			List<TransportStation> result) {
		Log.d("TRANSPORT", "onResult");
		((TransportModel) controller.getModel()).setLocationsFromNames(result);
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

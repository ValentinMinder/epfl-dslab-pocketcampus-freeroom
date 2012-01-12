package org.pocketcampus.plugin.transport.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.transport.android.TransportController;
import org.pocketcampus.plugin.transport.android.TransportModel;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;
import org.pocketcampus.plugin.transport.shared.TransportService.getLocationsFromNames_args;
import org.pocketcampus.plugin.transport.shared.TransportStation;

/**
 * A request to the server for the stations corresponding to each
 * <code>String</code> sent as parameters.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public class StationsFromNamesRequest
		extends
		Request<TransportController, Iface, getLocationsFromNames_args, List<TransportStation>> {

	/**
	 * Initiates the <code>getLocationsFromNames</code> request at the server.
	 * 
	 * @param client
	 *            The client communicating with the server.
	 * @param param
	 *            The parameters to be sent for the request : a list of
	 *            <code>String</code> for which we want the corresponding
	 *            stations.
	 * @return The list of stations gotten from the server.
	 */
	@Override
	protected List<TransportStation> runInBackground(Iface client,
			getLocationsFromNames_args param) throws Exception {
		return client.getLocationsFromNames(param.getNames());
	}

	/**
	 * Tells the model that the stations have been updated.
	 * 
	 * @param controller
	 *            The controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            The stations list gotten from the server.
	 */
	@Override
	protected void onResult(TransportController controller,
			List<TransportStation> result) {
		((TransportModel) controller.getModel()).setFavoriteStations(result);
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

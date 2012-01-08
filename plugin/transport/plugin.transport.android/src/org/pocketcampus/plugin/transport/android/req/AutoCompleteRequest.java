package org.pocketcampus.plugin.transport.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.transport.android.TransportController;
import org.pocketcampus.plugin.transport.android.TransportModel;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;

/**
 * A request to the server for the auto completion of a station.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public class AutoCompleteRequest extends
		Request<TransportController, Iface, String, List<TransportStation>> {

	/**
	 * Initiates the <code>autocomplete</code> request at the server.
	 * 
	 * @param client
	 *            The client communicating with the server.
	 * @param param
	 *            The parameters to be sent for the request : a
	 *            <code>String</code>, constraint for the auto completion.
	 */
	@Override
	protected List<TransportStation> runInBackground(Iface client,
			String constraint) throws Exception {
		return client.autocomplete(constraint);
	}

	/**
	 * Tells the model that the auto completion has been updated.
	 * 
	 * @param controller
	 *            The controller that initiated the request, of which we have to
	 *            notify of the result.
	 * @param result
	 *            The list of auto completed stations gotten from the server.
	 */
	@Override
	protected void onResult(TransportController controller,
			List<TransportStation> result) {
		((TransportModel) controller.getModel())
				.setAutoCompletedStations(result);
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

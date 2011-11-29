package org.pocketcampus.plugin.transport.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.transport.android.TransportController;
import org.pocketcampus.plugin.transport.android.TransportModel;
import org.pocketcampus.plugin.transport.shared.Location;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;

/**
 * A request to the server for the autocompletion of a destination
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class AutoCompleteRequest extends
		Request<TransportController, Iface, String, List<Location>> {

	/**
	 * Initiate the <code>autocomplete</code> Request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request : a string,
	 *            constraint for the autocompletion
	 */
	@Override
	protected List<Location> runInBackground(Iface client, String constraint)
			throws Exception {
		return client.autocomplete(constraint);
	}

	/**
	 * Tell the model the departures have been updated.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the list of auto completed destinations gotten from the server
	 */
	@Override
	protected void onResult(TransportController controller,
			List<Location> result) {
		System.out.println(result);

		((TransportModel) controller.getModel())
				.setAutoCompletedDestinations(result);
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
		e.printStackTrace();
	}

}

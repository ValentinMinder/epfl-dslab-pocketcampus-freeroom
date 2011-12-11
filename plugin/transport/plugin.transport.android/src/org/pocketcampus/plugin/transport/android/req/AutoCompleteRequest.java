package org.pocketcampus.plugin.transport.android.req;

import java.util.List;

import org.pocketcampus.android.platform.sdk.io.Request;
import org.pocketcampus.plugin.transport.android.TransportController;
import org.pocketcampus.plugin.transport.android.TransportModel;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;

/**
 * A request to the server for the auto completion of a destination.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public class AutoCompleteRequest extends
		Request<TransportController, Iface, String, List<TransportStation>> {

	/**
	 * Initiate the <code>autocomplete</code> request at the server
	 * 
	 * @param client
	 *            the client that communicates with the server
	 * @param param
	 *            the parameters to be sent for the request : a string,
	 *            constraint for the auto completion
	 */
	@Override
	protected List<TransportStation> runInBackground(Iface client, String constraint)
			throws Exception {
		return client.autocomplete(constraint);
	}

	/**
	 * Tells the model that the auto completion has been updated.
	 * 
	 * @param controller
	 *            the controller that initiated the request, of which we have to
	 *            notify of the result
	 * @param result
	 *            the list of auto completed destinations gotten from the server
	 */
	@Override
	protected void onResult(TransportController controller,
			List<TransportStation> result) {
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

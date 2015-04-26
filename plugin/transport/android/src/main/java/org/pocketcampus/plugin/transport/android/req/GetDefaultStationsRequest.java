package org.pocketcampus.plugin.transport.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.transport.android.TransportController;
import org.pocketcampus.plugin.transport.android.TransportModel;
import org.pocketcampus.plugin.transport.shared.TransportDefaultStationsResponse;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;

/**
 * 
 * @author silviu@pocketcampus.org
 *
 */
public class GetDefaultStationsRequest
		extends
		Request<TransportController, Iface, Void, TransportDefaultStationsResponse> {
	/**
	 * Executes the request for the default stations in background
	 */
	@Override
	protected TransportDefaultStationsResponse runInBackground(Iface client,
			Void param) throws Exception {
		return client.getDefaultStations();
	}

	@Override
	protected void onResult(TransportController controller,
			TransportDefaultStationsResponse result) {
		TransportModel model = (TransportModel) controller.getModel();
		model.setTransportDefaultStationsResponse(result);
	}

	@Override
	protected void onError(TransportController controller, Exception e) {
		TransportModel model = (TransportModel) controller.getModel();
		model.setTransportDefaultStationsResponse(null);
		e.printStackTrace();
	}

}

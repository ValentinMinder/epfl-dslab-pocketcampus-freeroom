package org.pocketcampus.plugin.transport.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.transport.android.TransportController;
import org.pocketcampus.plugin.transport.android.iface.ITransportModel;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;
import org.pocketcampus.plugin.transport.shared.TransportTripSearchRequest;
import org.pocketcampus.plugin.transport.shared.TransportTripSearchResponse;

public class SearchForTripsRequest
		extends
		Request<TransportController, Iface, TransportTripSearchRequest, TransportTripSearchResponse> {

	private TransportTripSearchRequest request;

	@Override
	protected TransportTripSearchResponse runInBackground(Iface client,
			TransportTripSearchRequest param) throws Exception {
		request = param;
		return client.searchForTrips(param);
	}

	@Override
	protected void onResult(TransportController controller,
			TransportTripSearchResponse result) {
		ITransportModel model = (ITransportModel) controller.getModel();
		model.setTransportTripSearchResponse(request, result);
	}

	@Override
	protected void onError(TransportController controller, Exception e) {
		ITransportModel model = (ITransportModel) controller.getModel();
		model.setTransportTripSearchResponse(request, null);
		e.printStackTrace();
	}

}

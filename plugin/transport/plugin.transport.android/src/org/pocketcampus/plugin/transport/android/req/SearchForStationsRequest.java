package org.pocketcampus.plugin.transport.android.req;

import org.pocketcampus.platform.android.io.Request;
import org.pocketcampus.plugin.transport.android.TransportController;
import org.pocketcampus.plugin.transport.android.iface.ITransportModel;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;
import org.pocketcampus.plugin.transport.shared.TransportStationSearchRequest;
import org.pocketcampus.plugin.transport.shared.TransportStationSearchResponse;

public class SearchForStationsRequest
		extends
		Request<TransportController, Iface, TransportStationSearchRequest, TransportStationSearchResponse> {

	private TransportStationSearchRequest request;

	@Override
	protected TransportStationSearchResponse runInBackground(Iface client,
			TransportStationSearchRequest param) throws Exception {
		request = param;
		return client.searchForStations(param);
	}

	@Override
	protected void onResult(TransportController controller,
			TransportStationSearchResponse result) {
		ITransportModel model = (ITransportModel) controller.getModel();
		model.setTransportStationSearchResponse(request, result);
	}

	@Override
	protected void onError(TransportController controller, Exception e) {
		ITransportModel model = (ITransportModel) controller.getModel();
		model.setTransportStationSearchResponse(request, null);
		e.printStackTrace();
	}

}

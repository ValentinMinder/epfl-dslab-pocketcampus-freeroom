package org.pocketcampus.plugin.transport.android;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.transport.android.iface.ITransportController;
import org.pocketcampus.plugin.transport.android.req.GetDefaultStationsRequest;
import org.pocketcampus.plugin.transport.android.req.SearchForStationsRequest;
import org.pocketcampus.plugin.transport.android.req.SearchForTripsRequest;
import org.pocketcampus.plugin.transport.shared.TransportService.Client;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportStationSearchRequest;
import org.pocketcampus.plugin.transport.shared.TransportTripSearchRequest;

import android.os.AsyncTask;

/**
 * The main controller of the Transport plugin. Takes care of interactions
 * between the model and the views and gets data from the server.
 * 
 * @author silviu@pocketcampus.org
 * 
 */
public class TransportController extends PluginController implements ITransportController {
	/** The plugin model. */
	private TransportModel mModel;
	/**
	 * This name must match given in the Server.java file in
	 * plugin.launcher.server. It's used to route the request to the right
	 * server implementation.
	 */
	private String mPluginName = "transport";

	private SearchForStationsRequest request;
	private String query;

	/**
	 * Called when first opening the Transport plugin. Initiates the model of
	 * the plugin.
	 */
	@Override
	public void onCreate() {
		mModel = new TransportModel(this);
	}

	/**
	 * Called in order to register in the model's listeners list.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	public void getDefaultStations() {
		new GetDefaultStationsRequest().start(this, (Iface) getClient(new Client.Factory(), mPluginName), null);
	}

	public boolean searchForStations(TransportAddView caller, String stationName) {
		if (stationName.trim().length() == 0) {
			return false;
		}
		if (request != null && !request.getStatus().equals(AsyncTask.Status.FINISHED))
			return false;
		if (query != null && query.equals(stationName))
			return false;
		query = stationName;
		request = new SearchForStationsRequest(caller);
		TransportStationSearchRequest req = new TransportStationSearchRequest(stationName);
		request.start(this, (Iface) getClient(new Client.Factory(), mPluginName), req);
		return true;
	}

	public void searchForTrips(TransportStation from, TransportStation to) {
		TransportTripSearchRequest request = new TransportTripSearchRequest(from, to);
		TransportModel model = (TransportModel) getModel();
		model.getTripsFor(request).setLoading(true);
		new SearchForTripsRequest().start(this, (Iface) getClient(new Client.Factory(), mPluginName), request);

	}
}

package org.pocketcampus.plugin.transport.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.transport.android.iface.ITransportController;
import org.pocketcampus.plugin.transport.android.req.AutoCompleteRequest;
import org.pocketcampus.plugin.transport.android.req.StationsFromNamesRequest;
import org.pocketcampus.plugin.transport.android.req.NextDeparturesRequest;
import org.pocketcampus.plugin.transport.shared.TransportService.Client;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;
import org.pocketcampus.plugin.transport.shared.TransportService.getLocationsFromNames_args;
import org.pocketcampus.plugin.transport.shared.TransportService.getTrips_args;

/**
 * The main controller of the Transport plugin. Takes care of interactions
 * between the model and the views and gets data from the server.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public class TransportController extends PluginController implements
		ITransportController {
	/** The plugin model. */
	private TransportModel mModel;
	/**
	 * This name must match given in the Server.java file in
	 * plugin.launcher.server. It's used to route the request to the right
	 * server implementation.
	 */
	private String mPluginName = "transport";

	/**
	 * Called when first opening the Transport plugin. Initiates the model of
	 * the plugin.
	 */
	@Override
	public void onCreate() {
		mModel = new TransportModel();
	}

	/**
	 * Called in order to register in the model's listeners list.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	/**
	 * Initiates a request to the server for the auto completion for the letters
	 * that the user is typed.
	 * 
	 * @param constraint
	 *            The letters that the user typed.
	 */
	@Override
	public void getAutocompletions(String constraint) {
		if (constraint != null) {
			new AutoCompleteRequest().start(this,
					(Iface) getClient(new Client.Factory(), mPluginName),
					constraint);
		}
	}

	/**
	 * Initiates a request to the server for the next departures between any two
	 * stations.
	 * 
	 * @param departure
	 *            The departure station.
	 * 
	 * @param arrival
	 *            The arrival station.
	 */
	@Override
	public void nextDepartures(String departure, String arrival) {
		if (departure != null && arrival != null) {
			getTrips_args args = new getTrips_args(departure, arrival);
			new NextDeparturesRequest().start(this,
					(Iface) getClient(new Client.Factory(), mPluginName), args);
		}
	}

	/**
	 * Initiates a request to the server for a list of
	 * <code>TransportStation</code> corresponding to the list of
	 * <code>String</code> which is sent as parameter.
	 * 
	 * @param list
	 *            The list of <code>String</code> for which we want the
	 *            corresponding stations.
	 */
	@Override
	public void getStationsFromNames(List<String> list) {
		if (list != null && !list.isEmpty()) {
			getLocationsFromNames_args args = new getLocationsFromNames_args(
					list);
			new StationsFromNamesRequest().start(this,
					(Iface) getClient(new Client.Factory(), mPluginName), args);
		}
	}

}

package org.pocketcampus.plugin.transport.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.transport.android.iface.ITransportController;
import org.pocketcampus.plugin.transport.android.req.AutoCompleteRequest;
import org.pocketcampus.plugin.transport.android.req.LocationsFromNamesRequest;
import org.pocketcampus.plugin.transport.android.req.NextDeparturesFromEPFLRequest;
import org.pocketcampus.plugin.transport.shared.TransportService.Client;
import org.pocketcampus.plugin.transport.shared.TransportService.Iface;
import org.pocketcampus.plugin.transport.shared.TransportService.getTrips_args;
import org.pocketcampus.plugin.transport.shared.TransportService.getLocationsFromNames_args;

import android.util.Log;

/**
 * The main controller of the Transport Plugin. Takes care of interactions
 * between the model and the view and gets information from the server.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public class TransportController extends PluginController implements
		ITransportController {
	/** The plugin model */
	private TransportModel mModel;
	/** The EPFL Station ID */
	private static final int EPFL_STATION_ID = 8501214;

	/**
	 * This name must match given in the Server.java file in
	 * plugin.launcher.server. It's used to route the request to the right
	 * server implementation.
	 */
	private String mPluginName = "transport";

	/**
	 * <code>onCreate</code>. Called when first opening the Transport Plugin.
	 * Initiates the model of the plugin
	 */
	@Override
	public void onCreate() {
		mModel = new TransportModel();
	}

	/**
	 * The view will call this in order to register in the model's listener
	 * list.
	 */
	@Override
	public PluginModel getModel() {
		return mModel;
	}

	/**
	 * Initiates a request to the server for the autocompletion for the letters
	 * the user typed
	 * 
	 * @param constraint
	 *            The letters that the user typed
	 */
	@Override
	public void getAutocompletions(String constraint) {
		if (constraint != null) {
			Log.d("TRANSPORT", "Autocomplete request (controller)");
			new AutoCompleteRequest().start(this,
					(Iface) getClient(new Client.Factory(), mPluginName),
					constraint);
		}
	}

	/**
	 * Initiates a request to the server for the Next Departures from EPFL to
	 * any destination
	 * 
	 * @param location
	 *            The arrival destination
	 */
	@Override
	public void nextDeparturesFromEPFL(String location) {
		if (location != null) {
			Log.d("TRANSPORT", "Departures request (controller)");
			getTrips_args args = new getTrips_args("EPFL", location);
			new NextDeparturesFromEPFLRequest().start(this,
					(Iface) getClient(new Client.Factory(), mPluginName), args);
		}
	}

	/**
	 * Initiates a request to the server for a list of Locations corresponding
	 * to the list of String which is sent as parameter
	 * 
	 * @param list
	 *            The list of Strings for which we want the corresponding
	 *            Locations
	 */
	@Override
	public void getLocationsFromNames(List<String> list) {
		if (list != null && !list.isEmpty()) {
			Log.d("TRANSPORT", "Locations from names request (controller)");
			getLocationsFromNames_args args = new getLocationsFromNames_args(
					list);
			new LocationsFromNamesRequest().start(this,
					(Iface) getClient(new Client.Factory(), mPluginName), args);
		}
	}

}

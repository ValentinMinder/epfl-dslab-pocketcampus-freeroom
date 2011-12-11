package org.pocketcampus.plugin.transport.android.iface;

import java.util.List;

/**
 * The interface that defines the methods implemented by a controller of the plugin.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public interface ITransportController {

	/**
	 * Initiates a request to the server for the auto completion for the letters
	 * the user typed.
	 * 
	 * @param constraint
	 *            The letters that the user typed
	 */
	public void getAutocompletions(String constraint);

	/**
	 * Initiates a request to the server for the Next Departures from EPFL to
	 * any destination.
	 * 
	 * @param location
	 *            The arrival destination
	 */
	public void nextDeparturesFromEPFL(String location);

	/**
	 * Initiates a request to the server for the Locations corresponding to each
	 * String of the list.
	 * 
	 * @param list
	 *            The list of Strings for which we want the corresponding
	 *            Locations
	 */
	public void getLocationsFromNames(List<String> list);

}

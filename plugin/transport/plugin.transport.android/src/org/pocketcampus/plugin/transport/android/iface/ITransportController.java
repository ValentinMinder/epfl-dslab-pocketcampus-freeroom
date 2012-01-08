package org.pocketcampus.plugin.transport.android.iface;

import java.util.List;

/**
 * The interface that defines the methods implemented by a controller of the
 * plugin.
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
	 *            The letters that the user typed.
	 */
	public void getAutocompletions(String constraint);

	/**
	 * Initiates a request to the server for the next departures between any two
	 * stations.
	 * 
	 * @param departure
	 *            The name of the departure station.
	 * @param arrival
	 *            The name of the arrival station.
	 */
	public void nextDepartures(String departure, String arrival);

	/**
	 * Initiates a request to the server for the stations corresponding to each
	 * <code>String</code> of the list.
	 * 
	 * @param list
	 *            The list of <code>String</code> for which we want the
	 *            corresponding stations.
	 */
	public void getStationsFromNames(List<String> list);

}

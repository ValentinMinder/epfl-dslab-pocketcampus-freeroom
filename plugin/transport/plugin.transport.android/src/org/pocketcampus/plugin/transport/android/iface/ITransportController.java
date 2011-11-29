package org.pocketcampus.plugin.transport.android.iface;

/**
 * The interface that defines the method implemented by a controller
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public interface ITransportController {
	
	/**
	 * Initiates a request to the server for the autocompletion for the letters
	 * the user typed
	 * 
	 * @param constraint
	 *            The letters that the user typed
	 */
	public void getAutocompletions(String constraint);

	/**
	 * Initiates a request to the server for the Next Departures from EPFL to
	 * any destination
	 * 
	 * @param location The arrival destination
	 */
	public void nextDeparturesFromEPFL(String location);

}

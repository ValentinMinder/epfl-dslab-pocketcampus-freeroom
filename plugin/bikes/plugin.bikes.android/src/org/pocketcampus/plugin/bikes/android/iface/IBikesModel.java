package org.pocketcampus.plugin.bikes.android.iface;

import java.util.List;

import org.pocketcampus.plugin.bikes.shared.BikeEmplacement;

/**
 * Interface to the public methods of the Bikes Model.
 * @author Pascal <pascal.scheiben@gmail.com>
 *
 */
public interface IBikesModel {
	
	/**
	 * Return the list of all BikeEmplacement
	 * @return the list of BikeEmplacement
	 */
	public List<BikeEmplacement> getAvailablesBikes();

	/**
	 * Update the list of BikeEmplacement and notify the View.
	 * @param result The list of the BikeEmplacement
	 */
	public void setResults(List<BikeEmplacement> result);
	
}

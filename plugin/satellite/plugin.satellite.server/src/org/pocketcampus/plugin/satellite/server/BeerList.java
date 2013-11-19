package org.pocketcampus.plugin.satellite.server;

import java.util.List;

import org.pocketcampus.plugin.satellite.shared.SatelliteBeer;

/**
 * Gets Satellite's list of beers.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public interface BeerList {
	List<SatelliteBeer> get() throws Exception;
}
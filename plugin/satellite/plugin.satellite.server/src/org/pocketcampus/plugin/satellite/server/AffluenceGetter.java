package org.pocketcampus.plugin.satellite.server;

import org.pocketcampus.plugin.satellite.shared.SatelliteAffluence;

/**
 * Gets the affluence at Satellite.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public interface AffluenceGetter {
	SatelliteAffluence get();
}
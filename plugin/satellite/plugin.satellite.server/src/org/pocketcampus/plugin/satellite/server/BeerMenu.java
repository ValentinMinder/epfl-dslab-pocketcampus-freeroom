package org.pocketcampus.plugin.satellite.server;

import org.pocketcampus.plugin.satellite.shared.*;

/**
 * Gets Satellite's beer menu.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public interface BeerMenu {
	BeersResponse get();
}
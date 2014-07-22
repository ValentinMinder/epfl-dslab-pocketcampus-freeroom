package org.pocketcampus.plugin.transport.server;

import java.io.IOException;
import java.util.List;

import org.pocketcampus.plugin.transport.shared.TransportStation;

/**
 * Service that allows the search of stations by name, partial or complete.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface StationService {
	/** Gets the station with the specified name, or null if no such station exists. */
	TransportStation getStation(final String name) throws IOException;
	
	/** Searches for stations by name using the specified query. */
	List<TransportStation> findStations(final String query) throws IOException;
}
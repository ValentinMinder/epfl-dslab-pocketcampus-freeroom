package org.pocketcampus.plugin.transport.server;

import org.pocketcampus.plugin.transport.shared.TransportGeoPoint;
import org.pocketcampus.plugin.transport.shared.TransportStation;

import java.io.IOException;
import java.util.List;

/**
 * Service that allows the search of stations by name, partial or complete.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface StationService {
	/** Gets the station with the specified name, or null if no such station exists. */
	TransportStation getStation(final String name) throws IOException;
	
	/** Searches for stations by name using the specified query, optionally ordering them by their proximity to a point. */
	List<TransportStation> findStations(final String query, final TransportGeoPoint location) throws IOException;
}
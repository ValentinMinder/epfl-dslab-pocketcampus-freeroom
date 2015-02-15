package org.pocketcampus.plugin.transport.server;

import org.joda.time.DateTime;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportTrip;

import java.io.IOException;
import java.util.List;

/**
 * Service that finds trips from one station to another.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface TripsService {
	/** Gets trips from the specified station, to the specified station, at the specified date and time. */
	List<TransportTrip> getTrips(final TransportStation start, final TransportStation end, final DateTime datetime) throws IOException;
}
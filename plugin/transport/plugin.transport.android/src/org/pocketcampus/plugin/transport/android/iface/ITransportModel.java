package org.pocketcampus.plugin.transport.android.iface;

import java.util.Collection;
import java.util.List;

import org.pocketcampus.plugin.transport.shared.TransportDefaultStationsResponse;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportStationSearchRequest;
import org.pocketcampus.plugin.transport.shared.TransportStationSearchResponse;
import org.pocketcampus.plugin.transport.shared.TransportTripSearchRequest;
import org.pocketcampus.plugin.transport.shared.TransportTripSearchResponse;

/**
 * The interface that defines the methods implemented by a model of the plugin.
 * 
 * @author silviu@pocketcampus.org
 * 
 */
public interface ITransportModel {

	/**
	 * Called by the controller to set up default stations
	 * 
	 * @param response
	 *            - the PocketCampus server's response
	 */
	public void setTransportDefaultStationsResponse(TransportDefaultStationsResponse response);

	/**
	 * Called by the controller to set up the result for a trip query
	 * 
	 * @param request
	 *            - the request
	 * @param response
	 *            - the PocketCampus server's response
	 */
	public void setTransportTripSearchResponse(TransportTripSearchRequest request, TransportTripSearchResponse response);

	/**
	 * Called by the controller to set up the result of a search for stations
	 * 
	 * @param request
	 *            - the request
	 * @param response
	 *            - the PocketCampus server's response
	 */
	public void setTransportStationSearchResponse(TransportStationSearchRequest request,
			TransportStationSearchResponse response);

	/**
	 * The View uses this method to get the stations the user has in their
	 * storage
	 * 
	 * @return the list of TransportStations
	 */
	public List<TransportStation> getPersistedTransportStations();

	public Collection<TransportTrips> getAllCachedTrips();

	public void departureStationChangedTo(TransportStation station);

	public List<TransportStation> getArrivalStations();

	public TransportStation getDepartureStation();

	public TransportTrips getTripsFor(TransportTripSearchRequest request);

	public void addTransportStationToPersistedStorage(TransportStation station);
	
	public void removeTransportStationFromPersistedStorage(TransportStation station);
}

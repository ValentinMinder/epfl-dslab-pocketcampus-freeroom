package org.pocketcampus.plugin.transport.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.transport.shared.*;

import de.schildbach.pte.NetworkProvider.WalkSpeed;
import de.schildbach.pte.SbbProvider;
import de.schildbach.pte.dto.LocationType;

/**
 * This is the server side implementation of the transport plugin. It handles
 * all the service provided to the client
 * 
 * @author Florian <florian.laurent@gmail.com>
 * @author Pascal <pascal.scheiben@gmail.com>
 */
public class TransportServiceImpl implements TransportService.Iface {
	/** Public Transport information provider */
	private SbbProvider mSbbProvider;

	/**
	 * Used by getTrips
	 * Number of milliseconds that should be deduced from current timestamp when requesting schedules,
	 * so that results can contain departures that just left or are leaving.
	 */
	private final long NUMBER_MS_IN_PAST_GET_TRIPS_REQUEST = 3 * 60 * 1000; // 3 min

	/**
	 * Constructor. Initializes the provider with the api key.
	 */
	public TransportServiceImpl() {
		mSbbProvider = new SbbProvider(
				"YJpyuPISerpXNNRTo50fNMP0yVu7L6IMuOaBgS0Xz89l3f6I3WhAjnto4kS9oz1");

		System.out.println("Transport started.");
	}

	/**
	 * Proposes several transport station corresponding to the user input.
	 * 
	 * @param constraint
	 *            Where the user wants to go.
	 * @return A list of <code>TransportStation</code> composed only of
	 *         LocationType.STATION and no POI or else
	 */
	@Override
	public List<TransportStation> autocomplete(String constraint)
			throws TException {

		List<de.schildbach.pte.dto.Location> sbbCompletions = null;
		try {
			sbbCompletions = mSbbProvider.autocompleteStations(constraint);
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<TransportStation>();
		}

		List<TransportStation> completions = new ArrayList<TransportStation>();
		for (de.schildbach.pte.dto.Location location : sbbCompletions) {
			// this is to get only Stations and nothing else
			if (location.type == LocationType.STATION)
				completions.add(new TransportStation(location.id, location.lat, location.lon,
						location.name));
		}

		return completions;
	}

	/**
	 * Retrieves the <code>TransportStation</code> object from it's name.
	 * 
	 * @param names
	 *            List of stations name.
	 * @return List of <code>TransportStation</code>
	 */
	@Override
	public List<TransportStation> getLocationsFromNames(List<String> names)
			throws TException {
		ArrayList<TransportStation> locList = new ArrayList<TransportStation>();

		for (String name : names) {
			try {
				TransportStation loc = SchildbachToPCConverter
						.convertSchToPC(mSbbProvider.autocompleteStations(name)
								.get(0));
				locList.add(loc);
			} catch (IOException e) {
				System.out.println("could not get stations from name: " + name);
			}
		}

		return locList;
	}

	/**
	 * Asks the provider how to get from A to B at present time. Calls a private
	 * method.
	 * 
	 * @param from
	 *            Departure station (A)
	 * @param to
	 *            Arrival station (B)
	 * @return Specific object converted from the Schildbach sdk containing all
	 *         the trip informations
	 */
	@Override
	public QueryTripsResult getTrips(String from, String to) throws TException {
		// requesting in past so that user can also see trips are leaving now or just left
		long now = (new Date()).getTime() - NUMBER_MS_IN_PAST_GET_TRIPS_REQUEST;
		QueryTripsResult result = getTripsFromSchildbach(from, to, now, true);
		return result;

	}

	/**
	 * Get all the informations from the provider, and convert them to the
	 * pocketcampus classes using the converters.
	 * 
	 * @param from
	 *            Departure station (A)
	 * @param to
	 *            Arrival Station (B)
	 * @param time
	 *            Epoch time in ms
	 * @param isDeparture
	 *            True if go from A to B and False if you go from B to A
	 * @return
	 */
	private QueryTripsResult getTripsFromSchildbach(String from, String to,
			long time, boolean isDeparture) {

		if (from == null || to == null) {
			return null;
		}

		de.schildbach.pte.dto.Location fromLoc = null, viaLoc = null, toLoc = null;

		try {
			// autocomplete not optimal, use connectionsFromStationsIDs
			// instead
			fromLoc = mSbbProvider.autocompleteStations(from).get(0);
			toLoc = mSbbProvider.autocompleteStations(to).get(0);

		} catch (IOException e) {
			e.printStackTrace();
		}

		if (fromLoc == null || toLoc == null) {
			return null;
		}

		Date date = new Date(time);
		String products = (String) null;
		WalkSpeed walkSpeed = WalkSpeed.NORMAL;

		QueryTripsResult tripResults = null;
		try {
			tripResults = SchildbachToPCConverter.convertSchToPC(mSbbProvider
					.queryConnections(fromLoc, viaLoc, toLoc, date,
							isDeparture, products, walkSpeed));

			if (tripResults.getConnections() != null) {
				for (TransportTrip tt : tripResults.getConnections()) {
					for (TransportConnection tc : tt.getParts()) {

						tc.setArrivalTimeIsSet(true);
						tc.setDepartureTimeIsSet(true);
						tc.setLineIsSet(true);
					}
				}
			} else if (tripResults.getFrom() == null && tripResults.getTo() == null) {
				tripResults = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return tripResults;
	}
}

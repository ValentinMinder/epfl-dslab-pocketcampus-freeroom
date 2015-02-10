package org.pocketcampus.plugin.transport.server;

import org.apache.thrift.TException;
import org.joda.time.DateTime;
import org.pocketcampus.platform.server.HttpClientImpl;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.transport.shared.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Server part of the transport plugin.
 *
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public class TransportServiceImpl implements TransportService.Iface {
    // Names of the default stations
    private static final String[] DEFAULT_STATION_NAMES = {"Lausanne-Flon", "EPFL"};

    private final StationService stationService;
    private final TripsService tripsService;

    private List<TransportStation> defaultStations;

    public TransportServiceImpl(final StationService stationService, final TripsService tripsService) {
        this.stationService = stationService;
        this.tripsService = tripsService;
    }

    /**
     * Constructor.
     */
    public TransportServiceImpl() {
        this(new StationServiceImpl(new HttpClientImpl(), PocketCampusServer.CONFIG.getString("TRANSPORT_HAFAS_TOKEN")),
                new TripsServiceImpl(new HttpClientImpl(), PocketCampusServer.CONFIG.getString("TRANSPORT_HAFAS_TOKEN")));
    }

    @Override
    public TransportStationSearchResponse searchForStations(TransportStationSearchRequest request) throws TException {
        List<TransportStation> stations;

        try {
            stations = stationService.findStations(request.getStationName(), request.getGeoPoint());
        } catch (IOException e) {
            return new TransportStationSearchResponse(TransportStatusCode.NETWORK_ERROR);
        }

        return new TransportStationSearchResponse(TransportStatusCode.OK).setStations(stations);
    }

    @Override
    public TransportDefaultStationsResponse getDefaultStations() throws TException {
        if (defaultStations == null) {
            defaultStations = new ArrayList<TransportStation>();
            for (String name : DEFAULT_STATION_NAMES) {
                try {
                    defaultStations.add(stationService.getStation(name));
                } catch (IOException e) {
                    defaultStations = null;
                    return new TransportDefaultStationsResponse(TransportStatusCode.NETWORK_ERROR);
                }
            }
        }

        return new TransportDefaultStationsResponse(TransportStatusCode.OK).setStations(defaultStations);
    }

    @Override
    public TransportTripSearchResponse searchForTrips(TransportTripSearchRequest request) throws TException {
        List<TransportTrip> trips;
        try {
            trips = tripsService.getTrips(request.getFromStation(), request.getToStation(), DateTime.now());
        } catch (IOException e) {
            return new TransportTripSearchResponse(TransportStatusCode.NETWORK_ERROR);
        }

        return new TransportTripSearchResponse(TransportStatusCode.OK).setTrips(trips);
    }


    // --- OLD STUFF ---

    /**
     * Proposes several transport station corresponding to the user input.
     *
     * @param constraint Where the user wants to go.
     * @return A list of TransportStation composed only of LocationType.STATION and no POI or else
     */
    @Override
    @Deprecated
    public List<TransportStation> autocomplete(String constraint)
            throws TException {
        try {
            // Don't use the cache, this will be used with extremely many unrelated queries
            return stationService.findStations(constraint, null);
        } catch (IOException e) {
            throw new TException("An IO error occurred.", e);
        }
    }

    /**
     * Retrieves the TransportStation object from it's name.
     *
     * @param names List of stations name.
     * @return List of TransportStations
     */
    @Override
    @Deprecated
    public List<TransportStation> getLocationsFromNames(List<String> names)
            throws TException {
        final List<TransportStation> stations = new ArrayList<TransportStation>();
        for (final String name : names) {
            stations.add(getStationFromName(name));
        }
        return stations;
    }

    /**
     * Asks the provider how to get from A to B at present time.
     */
    @Override
    @Deprecated
    public QueryTripsResult getTrips(String from, String to) throws TException {
        final TransportStation departure = getStationFromName(from);
        final TransportStation arrival = getStationFromName(to);
        final DateTime now = DateTime.now();

        List<TransportTrip> trips;
        try {
            trips = tripsService.getTrips(departure, arrival, now);
        } catch (IOException e) {
            throw new TException("An IO error occurred.", e);
        }

        // Compatibility with old clients: they understand "BBus\d*" but HAFAS has "Bus \d*".
        for (TransportTrip trip : trips) {
            for (TransportConnection connection : trip.getParts()) {
                TransportLine line = connection.getLine();
                if (line != null) {
                    line.setName(line.getName().replaceAll("Bus (\\d*)", "BBus$1"));
                }
            }
        }

        return new QueryTripsResult(departure, arrival, trips);
    }

    private TransportStation getStationFromName(final String name) throws TException {
        try {
            final TransportStation station = stationService.getStation(name);

            if (station == null) {
                throw new TException("Invalid station name.");
            }

            return station;
        } catch (IOException e) {
            throw new TException("An IO error occurred.", e);
        }
    }
}
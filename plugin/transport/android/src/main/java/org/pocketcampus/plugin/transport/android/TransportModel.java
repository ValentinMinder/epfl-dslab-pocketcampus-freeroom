package org.pocketcampus.plugin.transport.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.transport.android.iface.ErrorCause;
import org.pocketcampus.plugin.transport.android.iface.ITransportModel;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.android.iface.TransportTrips;
import org.pocketcampus.plugin.transport.shared.TransportDefaultStationsResponse;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportStationSearchRequest;
import org.pocketcampus.plugin.transport.shared.TransportStationSearchResponse;
import org.pocketcampus.plugin.transport.shared.TransportTrip;
import org.pocketcampus.plugin.transport.shared.TransportTripSearchRequest;
import org.pocketcampus.plugin.transport.shared.TransportTripSearchResponse;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * The Main Model for the Transport plugin. Handles all the data relative to
 * this plugin : favorite stations, auto completed stations, ...
 * 
 * @author silviu@pocketcampus.org
 */
public class TransportModel extends PluginModel implements ITransportModel {
	private static final String STATION_LONGITUDE = ".longitude";
	private static final String STATION_LATITUDE = ".latitude";
	private static final String STATION_ID = ".id";
	/** The views listening to updates in this model. */
	private ITransportView mListeners = (ITransportView) getListeners();

	/** The pointer to access and modify preferences stored on the phone. */
	private SharedPreferences mStationPrefs;
	private Editor mStationPrefsEditor;
	/** The name under which the preferences are stored on the phone. */
	private static final String DEST_PREFS_NAME = "TransportUserStations";

	private List<TransportStation> userTransportStations;

	private static final String STATION_NAMES = TransportModel.class.getName() + ".StationNames";

	private List<TransportStation> arrivalStations;
	private TransportStation departureStation;

	private LinkedHashMap<TransportTripSearchRequest, TransportTrips> cachedQueryTrips;

	/**
	 * Class constructor of the plugin initializing object instances.
	 */
	public TransportModel(Context context) {
		cachedQueryTrips = new LinkedHashMap<TransportTripSearchRequest, TransportTrips>();
		mStationPrefs = context.getSharedPreferences(DEST_PREFS_NAME, 0);
		Set<String> stationNames = mStationPrefs.getStringSet(STATION_NAMES, new HashSet<String>());
		userTransportStations = new ArrayList<TransportStation>();
		List<String> stationNamesSorted = new ArrayList<String>(stationNames);
		Collections.sort(stationNamesSorted);
		for (String stationName : stationNamesSorted) {
			int id = mStationPrefs.getInt(stationName + STATION_ID, 0);
			int latitude = mStationPrefs.getInt(stationName + STATION_LATITUDE, 0);
			int longitude = mStationPrefs.getInt(stationName + STATION_LONGITUDE, 0);
			TransportStation station = new TransportStation(id, latitude, longitude, stationName);
			userTransportStations.add(station);
		}
		mStationPrefsEditor = mStationPrefs.edit();
	}

	public void addTransportStationToPersistedStorage(TransportStation station) {
		if (!userTransportStations.contains(station)) {
			userTransportStations.add(0, station);
			departureStationChangedTo(departureStation);

			Set<String> stations = mStationPrefs.getStringSet(STATION_NAMES, new HashSet<String>());
			String stationName = station.getName();
			stations.add(stationName);
			mStationPrefsEditor.putStringSet(STATION_NAMES, stations);
			mStationPrefsEditor.putInt(stationName + STATION_ID, station.getId());
			mStationPrefsEditor.putInt(stationName + STATION_LATITUDE, station.getLatitude());
			mStationPrefsEditor.putInt(stationName + STATION_LONGITUDE, station.getLongitude());
			if (!mStationPrefsEditor.commit()) {
				Log.d(getClass().getName(), "Failed to commit to shared prefs");
			}
		}
	}

	/**
	 * Returns the interface that the views must implement.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ITransportView.class;
	}

	public void setTransportDefaultStationsResponse(TransportDefaultStationsResponse response) {
		if (response != null) {
			switch (response.getStatusCode()) {
			case OK:
				for (TransportStation station : response.getStations()) {
					addTransportStationToPersistedStorage(station);
				}
				mListeners.getDefaultStationsFinished(response.getStations());
				break;
			case NETWORK_ERROR:
				mListeners.getDefaultStationsFailed(ErrorCause.ServersDown);
			}
		} else {
			mListeners.getDefaultStationsFailed(ErrorCause.NetworkError);
		}
	}

	public List<TransportStation> getPersistedTransportStations() {
		return userTransportStations;
	}

	public void setTransportTripSearchResponse(TransportTripSearchRequest request, TransportTripSearchResponse response) {
		if (response != null) {
			switch (response.getStatusCode()) {
			case OK:
				if (this.cachedQueryTrips.get(request) == null) {
					Log.d(getClass().getName(), "There is no cached query trip for " + request
							+ ". Assuming user changed station");
					return;
				}
				Date now = new Date();
				List<TransportTrip> trips = response.getTrips();
				Iterator<TransportTrip> iterator = trips.iterator();
				while (iterator.hasNext()) {
					TransportTrip trip = iterator.next();
					Date dep = new Date();
					dep.setTime(trip.getDepartureTime());
					if (now.after(dep)) {
						iterator.remove();
					}
				}
				this.cachedQueryTrips.get(request).setTrips(trips);
				mListeners.searchForTripsFinished(request.getFromStation(), request.getToStation(), trips);
				break;
			case NETWORK_ERROR:
				this.cachedQueryTrips.get(request).setError(ErrorCause.ServersDown);

				mListeners.searchForTripsFailed(request.getFromStation(), request.getToStation(),
						ErrorCause.ServersDown);
			}
		} else {
			this.cachedQueryTrips.get(request).setError(ErrorCause.NetworkError);
			mListeners.searchForTripsFailed(request.getFromStation(), request.getToStation(), ErrorCause.NetworkError);
		}
	}

	public void setTransportStationSearchResponse(TransportStationSearchRequest request,
			TransportStationSearchResponse response) {
		if (response != null) {
			switch (response.getStatusCode()) {
			case OK:
				mListeners.searchForStationsFinished(request.getStationName(), response.getStations());
				break;
			case NETWORK_ERROR:
				mListeners.searchForStationsFailed(request.getStationName(), ErrorCause.ServersDown);
			}
		} else {
			mListeners.searchForStationsFailed(request.getStationName(), ErrorCause.NetworkError);
		}

	}

	public Collection<TransportTrips> getAllCachedTrips() {
		return this.cachedQueryTrips.values();
	}

	public void departureStationChangedTo(TransportStation station) {
		cachedQueryTrips.clear();
		if (station == null) {
			return;
		}
		arrivalStations = new ArrayList<TransportStation>(userTransportStations);
		arrivalStations.remove(station);
		departureStation = station;
		for (TransportStation arrivalStation : arrivalStations) {
			TransportTripSearchRequest request = new TransportTripSearchRequest(departureStation, arrivalStation);
			TransportTrips trip = new TransportTrips();
			trip.setLoading(true);
			trip.stationName = arrivalStation.getName();
			cachedQueryTrips.put(request, trip);
		}
	}

	public List<TransportStation> getArrivalStations() {
		return arrivalStations;
	}

	public TransportStation getDepartureStation() {
		return departureStation;
	}

	public TransportTrips getTripsFor(TransportTripSearchRequest request) {
		return cachedQueryTrips.get(request);
	}

	public void removeTransportStationFromPersistedStorage(TransportStation station) {
		Set<String> stations = mStationPrefs.getStringSet(STATION_NAMES, new HashSet<String>());
		String stationName = station.getName();
		stations.remove(stationName);
		mStationPrefsEditor.putStringSet(STATION_NAMES, stations);
		mStationPrefsEditor.remove(stationName + STATION_ID);
		mStationPrefsEditor.remove(stationName + STATION_LATITUDE);
		mStationPrefsEditor.remove(stationName + STATION_LONGITUDE);
		if (!mStationPrefsEditor.commit()) {
			Log.d(getClass().getName(), "Failed to commit to shared prefs");
		}

		userTransportStations.remove(station);
		if (departureStation.equals(station)) {
			if (userTransportStations.size() > 0) {
				TransportStation newDeparture = userTransportStations.get(0);
				departureStationChangedTo(newDeparture);
			} else {
				departureStationChangedTo(null);
			}
		} else {
			TransportTripSearchRequest request = new TransportTripSearchRequest(departureStation, station);
			cachedQueryTrips.remove(request);
			arrivalStations.remove(station);
		}
	}

}

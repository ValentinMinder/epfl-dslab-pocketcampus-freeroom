package org.pocketcampus.plugin.transport.android.iface;

import java.util.List;

import org.pocketcampus.plugin.transport.shared.TransportTrip;

public class TransportTrips {
	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading(boolean isLoading) {
		this.isLoading = isLoading;
		isError = false;
		trips = null;
	}

	public boolean isError() {
		return isError;
	}

	public void setError(ErrorCause cause) {
		this.isError = true;
		trips = null;
		isLoading = false;
		errorCause = cause;
	}

	public List<TransportTrip> getTrips() {
		return trips;
	}

	public void setTrips(List<TransportTrip> trips) {
		this.trips = trips;
		isError = false;
		isLoading = false;
	}

	public ErrorCause getErrorCause() {
		return errorCause;
	}

	private boolean isLoading;
	private boolean isError;
	private ErrorCause errorCause;
	private List<TransportTrip> trips;
	public String stationName;
}
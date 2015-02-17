package org.pocketcampus.plugin.transport.android.iface;

import java.util.List;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportTrip;

/**
 * The interface that defines the methods implemented by a view of the plugin.
 * 
 * @author silviu@pocketcampus.org
 *
 * 
 */
public interface ITransportView extends IView {

	public void searchForStationsFinished(String searchQuery,
			List<TransportStation> result);

	public void searchForStationsFailed(String searchQuery, ErrorCause cause);

	public void searchForTripsFinished(TransportStation from,
			TransportStation to, List<TransportTrip> result);

	public void searchForTripsFailed(TransportStation from,
			TransportStation to, ErrorCause cause);

	public void getDefaultStationsFinished(List<TransportStation> result);

	public void getDefaultStationsFailed(ErrorCause cause);
}

package org.pocketcampus.plugin.transport.android;

import java.util.Map;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.plugin.transport.R;
import org.pocketcampus.plugin.transport.android.utils.TransportFormatter;
import org.pocketcampus.plugin.transport.shared.TransportConnection;
import org.pocketcampus.plugin.transport.shared.TransportTrip;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.ListView;

public class TransportTripView extends PluginView {

	public static final String TRIP = TransportTripView.class.getName() + ".Trip";

	public static final String TITLE = TransportTripView.class.getName() + ".Title";

	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {
		TransportTrip trip = (TransportTrip) getIntent().getSerializableExtra(TRIP);
		if (null == trip) {
			throw new RuntimeException("Trip is null");
		}

		setActionBarTitle(getIntent().getStringExtra(TITLE));
		Preparated<TransportConnection> preparated = new Preparated<TransportConnection>(trip.getParts(),
				new Preparator<TransportConnection>() {

					@Override
					public Object content(int res, TransportConnection item) {
						Log.d("ITEM", item.toString());
						switch (res) {
						case R.id.transport_trip_details_arr_time: {
							String result = DateUtils.formatDateTime(TransportTripView.this, item.getArrivalTime(),
									DateUtils.FORMAT_SHOW_TIME);
							result = getString(R.string.transport_arrival_at) + " " + result;
							Log.d("RESOURCE", "R.id.transport_trip_details_arr_time " + result);
							return result;
						}
						case R.id.transport_trip_details_means: {
							String result = null;
							if (item.isFoot()) {
								result = getString(R.string.transport_by_feet);
							} else {
								result = TransportFormatter.getNiceName(item.getLine().getName());
								result = getString(R.string.transport_using) + " " + result;
								if (item.getDeparturePosition() != null) {
									result += " " + getString(R.string.transport_platform) + " "
											+ item.getDeparturePosition();
								}
							}
							Log.d("RESOURCE", "R.id.transport_trip_details_dep_line " + result);
							return result;
						}
						case R.id.transport_trip_details_dep_time: {
							String result = DateUtils.formatDateTime(TransportTripView.this, item.getDepartureTime(),
									DateUtils.FORMAT_SHOW_TIME);
							result = getString(R.string.transport_departure_at) + " " + result;
							Log.d("RESOURCE", "R.id.transport_trip_details_dep_time " + result);
							return result;
						}

						case R.id.transport_trip_details_station:
							return TransportDestinationTripsView.getTitle(item.getDeparture().getName(), item
									.getArrival().getName());
						default:
							throw new RuntimeException("Unknonw resource id " + res);
						}
					}

					@Override
					public int[] resources() {
						return new int[] { R.id.transport_trip_details_arr_time, R.id.transport_trip_details_means,
								R.id.transport_trip_details_station, R.id.transport_trip_details_dep_time };
					}

					@Override
					public void finalize(Map<String, Object> map, TransportConnection item) {
					}
				});

		setContentView(R.layout.transport_details);
		LazyAdapter adapter = new LazyAdapter(this, preparated.getMap(), R.layout.transport_details_row,
				preparated.getKeys(), preparated.getResources());

		((ListView) findViewById(R.id.transport_trip_details_list)).setAdapter(adapter);
	}

	@Override
	protected String screenName() {
		return "tripView";
	}

}

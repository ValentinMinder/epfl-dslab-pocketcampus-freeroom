package org.pocketcampus.plugin.transport.android;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter.Actuator;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.plugin.transport.R;
import org.pocketcampus.plugin.transport.shared.TransportTrip;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.ListView;

public class TransportDestinationTripsView extends PluginView {

	public static final String TRIPS = TransportDestinationTripsView.class
			.getName() + ".Trips";

	public static final String DESTINATION = TransportDestinationTripsView.class
			.getName() + ".Destination";

	public static final String DEPARTURE = TransportDestinationTripsView.class
			.getName() + ".Departure";

	private String title;

	private String toTimePeriod(long millis) {
		// hh:mm:ss
		return String.format(
				"%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(millis)));
	}

	public static String getTitle(String from, String to) {
		return from + " → " + to;
	}

	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {

		List<TransportTrip> trips = (List<TransportTrip>) getIntent()
				.getSerializableExtra(TRIPS);
		if (trips == null) {
			throw new RuntimeException("No trips!!!");
		}
		setContentView(R.layout.transport_destination_trips);

		title = getTitle(getIntent().getStringExtra(DEPARTURE), getIntent()
				.getStringExtra(DESTINATION));
		setActionBarTitle(title);
		Preparated<TransportTrip> preparator = new Preparated<TransportTrip>(
				trips, new Preparator<TransportTrip>() {

					@Override
					public Object content(int res, final TransportTrip item) {
						switch (res) {
						case R.id.transport_details_departure_time:
							return DateUtils.formatDateTime(
									TransportDestinationTripsView.this,
									item.getDepartureTime(),
									DateUtils.FORMAT_SHOW_TIME);

						case R.id.transport_details_arrival_time:
							return DateUtils.formatDateTime(
									TransportDestinationTripsView.this,
									item.getArrivalTime(),
									DateUtils.FORMAT_SHOW_TIME);
						case R.id.transport_details_duration_time:
							return toTimePeriod(item.getArrivalTime()
									- item.getDepartureTime());
						case R.id.transport_details_first_line:
							return TransportMainView.getNiceLogo(item);
						case R.id.transport_details_nb_changes:
							return "" + item.getPartsSize();
						case R.id.transport_destination_trips_row:
							return new LazyAdapter.Actuated("Silviu",
									new Actuator() {
										@Override
										public void triggered() {
											Intent intent = new Intent(
													TransportDestinationTripsView.this,
													TransportTripView.class);
											intent.putExtra(
													TransportTripView.TRIP,
													item);
											intent.putExtra(
													TransportTripView.TITLE,
													title);
											startActivity(intent);
										}
									});
						default:
							throw new RuntimeException("Unknonw resource id "
									+ res);
						}
					}

					@Override
					public int[] resources() {
						return new int[] { R.id.transport_details_arrival_time,
								R.id.transport_details_departure_time,
								R.id.transport_details_duration_time,
								R.id.transport_details_first_line,
								R.id.transport_details_nb_changes,
								R.id.transport_destination_trips_row };
					}

					@Override
					public void finalize(Map<String, Object> map,
							TransportTrip item) {

					}
				});

		LazyAdapter adapter = new LazyAdapter(this, preparator.getMap(),
				R.layout.transport_destination_trips_row, preparator.getKeys(),
				preparator.getResources());

		((ListView) findViewById(R.id.transport_destination_trips_listview))
				.setAdapter(adapter);
	}

	@Override
	protected String screenName() {
		return "/transport/trips";
	}

}

package org.pocketcampus.plugin.transport.android;

import static org.pocketcampus.platform.android.utils.DialogUtils.showSingleChoiceDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter.Actuator;
import org.pocketcampus.platform.android.utils.Callback;
import org.pocketcampus.platform.android.utils.DialogUtils.SingleChoiceHandler;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.R;
import org.pocketcampus.plugin.transport.android.iface.ErrorCause;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.android.iface.TransportTrips;
import org.pocketcampus.plugin.transport.android.utils.TransportFormatter;
import org.pocketcampus.plugin.transport.shared.TransportConnection;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportTrip;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.markupartist.android.widget.Action;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * The main view of the Transport plugin, first displayed when accessing
 * Transport.
 * 
 * Displays the next departures for the stations that the user has set as
 * favorite stations. The favorite stations are stored in the android
 * <code>SharedPreferences</code> and displayed each time the user accesses the
 * Transport plugin. He can go to the <code>TransportEditView Activity</code> to
 * delete them or add more stations.
 * 
 * @author silviu@pocketcampus.org
 * 
 */
public class TransportMainView extends PluginView implements ITransportView {
	/* MVC */
	/** The plugin controller. */
	private TransportController mController;
	/** The plugin model. */
	private TransportModel mModel;
	/** Change direction action in the action bar. */
	private SelectDepartureAction mSelectDepartureAction;

	private boolean automaticChoice = true;

	/**
	 * Defines what the main controller is for this view.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return TransportController.class;
	}

	/**
	 * Called when first displaying the view. Retrieves the model and the
	 * controller and calls the methods setting up the layout, the action bar
	 * and the stations with next departures.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {

		mController = (TransportController) controller;
		mModel = (TransportModel) mController.getModel();
		setDepartureStation();
	}

	/**
	 * Called when this view is accessed after already having been initialized
	 * before. Refreshes the next departures.
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		setDepartureStation();
	}

	@Override
	protected String screenName() {
		return "/transport";
	}

	/**
	 * Sets up the main layout of the plugin.
	 */
	private void setUpLayout() {
		setContentView(R.layout.transport_main);
		setActionBarTitle(getString(R.string.transport_plugin_name));
		setUpActionBar();

		updateTrips();
	}

	/**
	 * Retrieves the action bar and adds a refresh action to it.
	 */
	private void setUpActionBar() {
		removeAllActionsFromActionBar();

		if (mSelectDepartureAction == null) {
			mSelectDepartureAction = new SelectDepartureAction();
		}
		addActionToActionBar(mSelectDepartureAction);

		addActionToActionBar(new Action() {

			@Override
			public void performAction(View view) {
				trackEvent("UserStations", null);
				Intent i = new Intent(TransportMainView.this, TransportAddView.class);
				startActivity(i);
			}

			@Override
			public int getDrawable() {
				return R.drawable.transport_add_white;
			}

			@Override
			public String getDescription() {
				return getString(R.string.transport_add_station);
			}
		});

		addActionToActionBar(new Action() {

			@Override
			public void performAction(View view) {
				trackEvent("UserStations", null);
				Intent i = new Intent(TransportMainView.this, TransportRemoveView.class);
				startActivity(i);
			}

			@Override
			public int getDrawable() {
				return R.drawable.transport_remove;
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return null;
			}
		});

		addActionToActionBar(new RefreshAction());

	}

	/**
	 * Sets up which stations have to be displayed. First checks if there are
	 * persisted stations. If yes, asks for next departures, and if not, asks
	 * the server for default stations.
	 */
	private void updateTrips() {
		for (TransportStation station : mModel.getArrivalStations()) {
			mController.searchForTrips(mModel.getDepartureStation(), station);
		}
		updateDisplay();
	}

	public static String getNiceLogo(TransportTrip trip) {
		String logo = null;
		for (TransportConnection p : trip.getParts()) {
			if (!p.isFoot() && p.getLine() != null) {
				logo = p.getLine().getName();
				break;
			}
		}
		logo = TransportFormatter.getNiceName(logo);
		return logo;
	}

	private void startActivityShowingTripDetails(String destination, List<TransportTrip> trips) {
		Intent intent = new Intent(this, TransportDestinationTripsView.class);
		intent.putExtra(TransportDestinationTripsView.TRIPS, new ArrayList<TransportTrip>(trips));
		intent.putExtra(TransportDestinationTripsView.DESTINATION, destination);

		intent.putExtra(TransportDestinationTripsView.DEPARTURE, mModel.getDepartureStation().getName());
		startActivity(intent);
	}

	private void updateDisplay() {
		if (findViewById(R.id.transport_departure_station) == null) {
			return;
		}

		((TextView) findViewById(R.id.transport_departure_station)).setText(mModel.getDepartureStation().getName());

		((TextView) findViewById(R.id.transport_departure_station)).setText(mModel.getDepartureStation().getName());
		findViewById(R.id.transport_departure_station).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSelectDepartureAction.performAction(v);
			}
		});
		Preparated<TransportTrips> departures = new Preparated<TransportTrips>(new ArrayList<TransportTrips>(
				mModel.getAllCachedTrips()), new Preparator<TransportTrips>() {

			@Override
			public Object content(int res, final TransportTrips item) {
				switch (res) {
				case R.id.transport_destination_name:
					return item.stationName;
				case R.id.transport_departure_line0: {
					if (item.isError() || item.isLoading()) {
						return "";
					}
					int index = 0;
					if (item.getTrips().size() > index) {
						return new LazyAdapter.Customizer(getNiceLogo(item.getTrips().get(index)),
								new Callback<View>() {
									public void callback(View t) {
										((TextView) t).setTextColor(getResources().getColor(R.color.green_apple));
									}
								});
					}
					return null;
				}

				case R.id.transport_departure_line1: {
					if (item.isError() || item.isLoading()) {
						return "";
					}
					int index = 1;
					if (item.getTrips().size() > index) {
						return getNiceLogo(item.getTrips().get(index));
					}
					return null;
				}

				case R.id.transport_departure_line2: {
					if (item.isError() || item.isLoading()) {
						return "";
					}
					int index = 2;
					if (item.getTrips().size() > index) {
						return getNiceLogo(item.getTrips().get(index));
					}
					return null;
				}

				case R.id.transport_departure_time0: {
					if (item.isError()) {
						String original = null;
						switch (item.getErrorCause()) {
						case NetworkError:
							original = getString(R.string.sdk_connection_error_happened);
							break;
						case ServersDown:
							original = getString(R.string.sdk_upstream_server_down);
						}
						return new LazyAdapter.Customizer(original, new Callback<View>() {

							public void callback(View t) {
								((TextView) t).setTextColor(getResources().getColor(R.color.epfl_corrected_red));
							}
						});

					}
					if (item.isLoading()) {
						return getString(R.string.transport_loading);
					}
					int index = 0;
					if (item.getTrips().size() > index) {
						return new LazyAdapter.Customizer(timeString(item.getTrips().get(index).getDepartureTime()),
								new Callback<View>() {
									public void callback(View t) {
										((TextView) t).setTextColor(getResources().getColor(R.color.green_apple));
									}
								});

					}
					return null;
				}

				case R.id.transport_departure_time1: {
					if (item.isError() || item.isLoading()) {
						return "";
					}
					int index = 1;
					if (item.getTrips().size() > index) {
						return timeString(item.getTrips().get(index).getDepartureTime());
					}
					return null;
				}

				case R.id.transport_departure_time2: {
					if (item.isError() || item.isLoading()) {
						return "";
					}
					int index = 2;
					if (item.getTrips().size() > index) {
						return timeString(item.getTrips().get(index).getDepartureTime());
					}
					return null;
				}

				case R.id.transport_main_row:
					if (item.getTrips() != null) {
						return new LazyAdapter.Actuated(item.stationName, new Actuator() {
							@Override
							public void triggered() {
								startActivityShowingTripDetails(item.stationName, item.getTrips());
							}
						});
					} else {
						return null;
					}
				case R.id.transport_main_details_indicator:
					if (item.isError() || item.isLoading()) {
						return null;
					}
					return R.drawable.pocketcampus_list_arrow;
				default:
					Log.d("WTF", "Unknonw id " + res);
					return null;
				}
			}

			@Override
			public int[] resources() {
				return new int[] { R.id.transport_destination_name, R.id.transport_departure_line0,
						R.id.transport_departure_line1, R.id.transport_departure_line2, R.id.transport_departure_time0,
						R.id.transport_departure_time1, R.id.transport_departure_time2, R.id.transport_main_row };
			}

			@Override
			public void finalize(Map<String, Object> map, TransportTrips item) {

			}

		});

		LazyAdapter adapter = new LazyAdapter(this, departures.getMap(), R.layout.transport_main_row,
				departures.getKeys(), departures.getResources());
		((ListView) findViewById(R.id.transport_departure_times_list)).setAdapter(adapter);

		((ListView) findViewById(R.id.transport_departure_times_list)).setOnScrollListener(new PauseOnScrollListener(
				ImageLoader.getInstance(), true, true));

	}

	/**
	 * Takes the time before next departures in milliseconds and transforms it
	 * to a text in the form : "In x hour(s), y minute(s)."
	 * 
	 * @param milliseconds
	 *            The time until the next departure.
	 * @return s The <code>String</code> representation of the time left until
	 *         the departure.
	 */
	private String timeString(long milliseconds) {
		Date now = new Date();
		Date then = new Date();
		then.setTime(milliseconds);

		long diff = then.getTime() - now.getTime();

		if (diff > 60 * 60 * 1000) {
			// if more than 1 hour
			return DateUtils.formatDateTime(this, milliseconds, DateUtils.FORMAT_SHOW_TIME);
		}

		long minutes = diff / (60 * 1000);

		if (minutes > 0) {
			return minutes + "'";
		}
		return getString(R.string.transport_departure_now);

	}

	/**
	 * Refreshes the next departures when clicking on the action bar refresh
	 * button.
	 * 
	 * @author Oriane <oriane.rodriguez@epfl.ch>
	 * 
	 */
	private class RefreshAction implements Action {

		/**
		 * Class constructor which doesn't do anything.
		 */
		RefreshAction() {
		}

		/**
		 * Returns the resource for the icon of the button in the action bar.
		 */
		@Override
		public int getDrawable() {
			return R.drawable.sdk_action_bar_refresh;
		}

		/**
		 * Refreshes the departures when the user clicks on the button in the
		 * action bar.
		 */
		@Override
		public void performAction(View view) {
			trackEvent("Refresh", null);
			setDepartureStation();
		}

		@Override
		public String getDescription() {
			return getString(R.string.sdk_reload_title);
		}
	}

	/**
	 * Change the depature station
	 */
	private class SelectDepartureAction implements Action {

		/**
		 * The constructor which doesn't do anything
		 */
		SelectDepartureAction() {
		}

		/**
		 * Returns the resource for the icon of the button in the action bar.
		 */
		@Override
		public int getDrawable() {
			return R.drawable.transport_action_bar_change_direction;
		}

		/**
		 * Changes direction and refreshes the departures when the user clicks
		 * on the button in the action bar.
		 */
		@Override
		public void performAction(View view) {
			trackEvent("ChangeDirection", null);

			Map<Integer, String> mapFromIndexToName = new HashMap<Integer, String>();
			mapFromIndexToName.put(0, getString(R.string.transport_automatic));
			int index = 0;
			int stationIndex = 0;
			for (TransportStation station : mModel.getPersistedTransportStations()) {
				index++;
				mapFromIndexToName.put(index, station.getName());
				if (station.equals(mModel.getDepartureStation())) {
					stationIndex = index;
				}
			}

			showSingleChoiceDialog(TransportMainView.this, mapFromIndexToName,
					getString(R.string.transport_select_departure_station), automaticChoice ? 0 : stationIndex,
					new SingleChoiceHandler<Integer>() {

						@Override
						public void saveSelection(Integer t) {
							if (t == 0) {
								automaticChoice = true;
								locationLastRefresh = 0;
							} else {
								automaticChoice = false;
								TransportStation station = mModel.getPersistedTransportStations().get(t - 1);
								mModel.departureStationChangedTo(station);
							}
							setDepartureStation();
						}
					});
		}

		@Override
		public String getDescription() {
			return getString(R.string.transport_select_departure_station);
		}
	}

	@Override
	public void networkErrorHappened() {
		setUnrecoverableErrorOccurred(getString(R.string.sdk_connection_error_happened));
	}

	public void serversDown() {
		setUnrecoverableErrorOccurred(getString(R.string.sdk_upstream_server_down));
	}

	@Override
	public void searchForStationsFinished(String searchQuery, List<TransportStation> result) {
	}

	@Override
	public void searchForStationsFailed(String searchQuery, ErrorCause cause) {
	}

	@Override
	public void searchForTripsFinished(TransportStation from, TransportStation to, List<TransportTrip> result) {
		updateDisplay();
	}

	@Override
	public void searchForTripsFailed(TransportStation from, TransportStation to, ErrorCause cause) {
		Log.d("FAILURE", "Failed to get trips from " + from + " to " + to + " because of " + cause);
		updateDisplay();
	}

	@Override
	public void getDefaultStationsFinished(List<TransportStation> result) {
		locationLastRefresh = 0;
		setDepartureStation();
	}

	@Override
	public void getDefaultStationsFailed(ErrorCause cause) {
		switch (cause) {
		case NetworkError:
			networkErrorHappened();
			break;
		case ServersDown:
			serversDown();
		default:
			break;
		}
	}

	private long locationLastRefresh = 0;
	private static final long REFRESH_PERIOD = 60 * 1000;

	private void setDepartureStation() {
		if (mModel.getPersistedTransportStations().size() == 0) {
			mController.getDefaultStations();
			setLoadingContentScreen(getString(R.string.transport_get_default_stations));
			return;
		}

		if (!automaticChoice && mModel.getDepartureStation() != null) {
			setUpLayout();
			return;
		}
		if ((System.currentTimeMillis() - locationLastRefresh) < REFRESH_PERIOD) {
			setUpLayout();
			return;
		}

		setLoadingContentScreen(getString(R.string.transport_locating_you));

		// Acquire a reference to the system Location Manager
		final LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				locationLastRefresh = System.currentTimeMillis();
				// Called when a new location is found by the network location
				// provider.
				location.setAltitude(0);
				TransportStation departureStation = null;
				float minDistance = Float.MAX_VALUE;
				for (TransportStation station : mModel.getPersistedTransportStations()) {
					Location stationLocation = new Location(LocationManager.GPS_PROVIDER);
					double latitude = Integer.valueOf(station.getLatitude()).doubleValue();
					latitude /= 1000 * 1000;
					double longitude = Integer.valueOf(station.getLongitude()).doubleValue();
					longitude /= 1000 * 1000;
					stationLocation.setLatitude(latitude);
					stationLocation.setLongitude(longitude);
					float distance = location.distanceTo(stationLocation);
					if (distance < minDistance) {
						minDistance = distance;
						departureStation = station;
					}
				}

				mModel.departureStationChangedTo(departureStation);
				setUpLayout();
				locationManager.removeUpdates(this);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
	}
}

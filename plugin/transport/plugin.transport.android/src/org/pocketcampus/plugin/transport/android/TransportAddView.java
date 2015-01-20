package org.pocketcampus.plugin.transport.android;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.pocketcampus.platform.android.core.PluginController;
import org.pocketcampus.platform.android.core.PluginView;
import org.pocketcampus.platform.android.ui.adapter.LazyAdapter;
import org.pocketcampus.platform.android.utils.Preparated;
import org.pocketcampus.platform.android.utils.Preparator;
import org.pocketcampus.plugin.transport.R;
import org.pocketcampus.plugin.transport.android.iface.ErrorCause;
import org.pocketcampus.plugin.transport.android.iface.ITransportModel;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportTrip;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

/**
 * The view of the Transport plugin which lets the user add a favorite station.
 * This view displays an input bar in which the user can type a station, choose
 * among auto completed stations that the server is sending and set one of them
 * as a favorite station by just clicking on it. The user cannot save any
 * station that doesn't appear in the auto completion.
 * 
 * A thing that may seem weird but that we have to do that way is when the user
 * clicks on a station, we directly ask for the next departures without saving
 * the station in the favorite ones. This is because the auto completed stations
 * received from the server don't always match a real station, so we have to
 * wait for the result of next departures in order to save the exact right name
 * of the station. This is because of the library we use, and if we checked on
 * the server before sending auto completed stations, it would add a too long
 * delay for the user to get them, so we decided to keep it that way.
 * 
 * @author silviu@pocketcampus.org
 */
public class TransportAddView extends PluginView implements ITransportView {
	/** The plugin controller. */
	private TransportController mController;

	final long REFRESH_DELAY = 500;
	private Timer refreshTimer;
	private long lastKeyPress = 0;
	private boolean stopRefresh;

	/**
	 * Defines what the main controller is for this view.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return TransportController.class;
	}

	/**
	 * Called when first displaying the view. Retrieves the model and the
	 * controller and calls the <code>displayView</code> method that creates the
	 * input bar, adds it to the main layout and then calls the
	 * <code>createDestinationsList</code> method which fills the list with auto
	 * completed stations as the user is typing.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState, PluginController controller) {

		mController = (TransportController) controller;
		// Display the view
		setContentView(R.layout.transport_add);
		setActionBarTitle(getResources().getString(R.string.transport_add_station));
		setKeyListener();
	}

	@Override
	protected String screenName() {
		return "/transport/addStation";
	}

	private void setKeyListener() {
		final EditText searchBar = (EditText) findViewById(R.id.transport_add_searchinput);
		searchBar.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void afterTextChanged(Editable s) {
				lastKeyPress = System.currentTimeMillis();
			}
		});
	}

	private void performSearchIfNeeded() {
		String s = ((EditText) findViewById(R.id.transport_add_searchinput)).getText().toString();
		if (mController.searchForStations(s)) {
			trackEvent("Search", s);
		}
	}

	private TimerTask getRefreshTask() {
		return new TimerTask() {
			public void run() {
				if (stopRefresh)
					return;
				long interval = System.currentTimeMillis() - lastKeyPress;
				refreshTimer = new Timer();
				if (interval > REFRESH_DELAY) {
					runOnUiThread(new Runnable() {
						public void run() {
							performSearchIfNeeded();
						}
					});
					refreshTimer.schedule(getRefreshTask(), REFRESH_DELAY);
				} else {
					refreshTimer.schedule(getRefreshTask(), REFRESH_DELAY - interval);
				}
			}
		};
	}

	@Override
	public void networkErrorHappened() {
		throw new RuntimeException("Should not be called here");
	}

	@Override
	public void searchForStationsFinished(String searchQuery, List<TransportStation> result) {
		final ITransportModel model = (ITransportModel) mController.getModel();
		result.removeAll(model.getPersistedTransportStations());
		Preparated<TransportStation> preparated = new Preparated<TransportStation>(result,
				new Preparator<TransportStation>() {

					@Override
					public Object content(int res, final TransportStation item) {
						return new LazyAdapter.Actuated(item.getName(), new LazyAdapter.Actuator() {
							@Override
							public void triggered() {
								model.addTransportStationToPersistedStorage(item);
								finish();
							}
						});
					}

					@Override
					public int[] resources() {
						return new int[] { R.id.transport_add_station_name };
					}

					@Override
					public void finalize(Map<String, Object> map, TransportStation item) {
					}
				});

		LazyAdapter adapter = new LazyAdapter(this, preparated.getMap(), R.layout.transport_add_row,
				preparated.getKeys(), preparated.getResources());
		((ListView) findViewById(R.id.transport_add_listview)).setAdapter(adapter);
	}

	@Override
	public void searchForStationsFailed(String searchQuery, ErrorCause cause) {

	}

	@Override
	public void searchForTripsFinished(TransportStation from, TransportStation to, List<TransportTrip> result) {
	}

	@Override
	public void searchForTripsFailed(TransportStation from, TransportStation to, ErrorCause cause) {
	}

	@Override
	public void getDefaultStationsFinished(List<TransportStation> result) {
	}

	@Override
	public void getDefaultStationsFailed(ErrorCause cause) {
	}

	/**
	 * This is called when the Activity is resumed.
	 * 
	 * If the user presses back on the Authentication window, This Activity is
	 * resumed but we do not have the credentials. In this case we close the
	 * Activity.
	 */
	@Override
	protected void onResume() {
		super.onResume();

		stopRefresh = false;
		refreshTimer = new Timer();
		refreshTimer.schedule(getRefreshTask(), REFRESH_DELAY);
	}

	@Override
	protected void onPause() {
		super.onPause();

		stopRefresh = true;
	}

}

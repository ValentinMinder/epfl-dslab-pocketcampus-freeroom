package org.pocketcampus.plugin.transport.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCEntryAdapter;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCEntryItem;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCItem;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCSectionItem;
import org.pocketcampus.android.platform.sdk.ui.element.ButtonElement;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.android.ui.TransportTripDetailsDialog;
import org.pocketcampus.plugin.transport.android.utils.DestinationFormatter;
import org.pocketcampus.plugin.transport.android.utils.TransportFormatter;
import org.pocketcampus.plugin.transport.shared.QueryTripsResult;
import org.pocketcampus.plugin.transport.shared.TransportConnection;
import org.pocketcampus.plugin.transport.shared.TransportStation;
import org.pocketcampus.plugin.transport.shared.TransportTrip;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

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
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public class TransportMainView extends PluginView implements ITransportView {
	/* MVC */
	/** The plugin controller. */
	private TransportController mController;
	/** The plugin model. */
	private TransportModel mModel;
	/* Layout */
	/** The <code>ActionBar</code>. */
	private ActionBar mActionBar;
	/** Refresh action in the action bar. */
	private RefreshAction mRefreshAction;
	/** Change direction action in the action bar. */
	private ChangeDirectionAction mDirectionAction;
	/** The main Layout consisting of two inner layouts and a title. */
	private StandardTitledLayout mLayout;
	/** The list to display next departures. */
	private ListView mListView;

	/** The pointer to access and modify preferences stored on the phone. */
	private SharedPreferences mDestPrefs;
	/** Interface to modify values in the <code>SharedPreferences</code> object. */
	private Editor mDestPrefsEditor;
	/** The name under which the preferences are stored on the phone. */
	private static final String DEST_PREFS_NAME = "TransportDestinationsPrefs";

	/** A <code>Boolean</code> telling which direction is shown. */
	private boolean mFromEpfl;
	/** The name of the EPFL station. */
	private final String M_EPFL_STATION = "EPFL";

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
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("transport");

		mController = (TransportController) controller;
		mModel = (TransportModel) mController.getModel();

		mDestPrefs = getSharedPreferences(DEST_PREFS_NAME, 0);
		mDestPrefsEditor = mDestPrefs.edit();

		mFromEpfl = true;

		// Set up the main layout and the list view
		setUpLayout();
		setUpListView();

		// Set up the action bar with a button
		setUpActionBar();

		// Set up destinations that will be displayed
		mModel.freeDestinations();
		setUpDestinations();
	}

	/**
	 * Called when this view is accessed after already having been initialized
	 * before. Refreshes the next departures.
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		mModel.freeDestinations();
		mLayout.hideText();
		setUpDestinations();
	}

	/**
	 * Main Transport Options Menu containing access to the favorite stations.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.transport_menu, menu);
		return true;
	}

	/**
	 * Decides what happens when the Options Menu is opened and an option is
	 * chosen (which view to display).
	 */
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.transport_stations) {
			mFromEpfl = true;
			Intent i = new Intent(this, TransportEditView.class);
			startActivity(i);
		}
		return true;
	}

	/**
	 * Sets up the main layout of the plugin.
	 */
	private void setUpLayout() {
		// Main layout
		mLayout = new StandardTitledLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.transport_plugin_name));
		mLayout.hideTitle();
		setContentView(mLayout);
	}

	/**
	 * Sets up the list of stations found in the <code>SharedPreferences</code>
	 * along with their next departures.
	 */
	private void setUpListView() {
		// Creates the list view and sets its click listener
		mListView = new ListView(this);
		mListView.setId(1234);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * When the user clicks on a departure, shows a dialog with
			 * connections details about the whole trip.
			 */
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Find the name and departure time as a string
				String txt = ((PCEntryItem) ((ListView) arg0)
						.getItemAtPosition(arg2)).id;
				// Separate into name and departure time
				String[] s = txt.split(":");
				String name = s[0];
				System.out.println("****************" +name);
				long depTime = Long.valueOf(s[1]);
				long arrTime = Long.valueOf(s[2]);
				// Find the destination in the ones from the model
				List<TransportTrip> trips = mModel.getFavoriteStations().get(
						name);
				for (TransportTrip trip : trips) {
					if (trip.getDepartureTime() == depTime
							&& trip.getArrivalTime() == arrTime) {

						TransportTripDetailsDialog dialog = new TransportTripDetailsDialog(
								TransportMainView.this, trip);

						// Tracker
						Tracker.getInstance().trackPageView(
								"transport/dialog/" + trip.getFrom().getName()
										+ "/" + trip.getTo().getName());

						dialog.show();
						break;
					}
				}
			}
		});

		// Adds it to the layout
		mLayout.addFillerView(mListView);
	}

	/**
	 * Retrieves the action bar and adds a refresh action to it.
	 */
	private void setUpActionBar() {
		mActionBar = getActionBar();
		if (mActionBar != null) {
			mRefreshAction = new RefreshAction();
			mActionBar.addAction(mRefreshAction, 0);
		}
	}

	/**
	 * Sets up which stations have to be displayed. First checks if there are
	 * stations in the <code>SharedPreferences</code>. If yes, asks for next
	 * departures, and if not, displays a button to let the user add a station.
	 */
	@SuppressWarnings("unchecked")
	private void setUpDestinations() {
		Map<String, Integer> prefs = (Map<String, Integer>) mDestPrefs.getAll();
		// If no stations set, display a button that redirects to the add
		// view of the plugin
		if (prefs == null || prefs.isEmpty()) {
			if (mActionBar == null) {
				mActionBar = getActionBar();
			}
			if (mDirectionAction != null) {
				mActionBar.removeAction(mDirectionAction);
			}
			ButtonElement addButton = new ButtonElement(this, getResources()
					.getString(R.string.transport_add_station));
			LayoutParams l = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			l.addRule(RelativeLayout.CENTER_IN_PARENT);
			addButton.setLayoutParams(l);

			addButton.setOnClickListener(new OnClickListener() {

				/**
				 * When the user clicks on the add button, opens the
				 * <code>TransportAddView Activity</code>.
				 */
				@Override
				public void onClick(View v) {
					// Tracker
					Tracker.getInstance().trackPageView("transport/button/add");

					mFromEpfl = true;
					Intent add = new Intent(getApplicationContext(),
							TransportAddView.class);
					startActivity(add);
				}
			});

			mLayout.removeFillerView();
			mLayout.addFillerView(addButton);
		} else {
			// If station(s) are in the shared preferences, remove the
			// button of the main layout and adds the list view
			mLayout.removeFillerView();

			// adding the titles
			ArrayList<PCItem> items = new ArrayList<PCItem>();

			Set<String> set = prefs.keySet();
			List<String> list = new ArrayList<String>();

			list.addAll(set);
			Collections.sort(list);
			for (String s : list) {

				if (mFromEpfl)
					items.add(new PCSectionItem(M_EPFL_STATION + " - " + s));
				else
					items.add(new PCSectionItem(s + " - " + M_EPFL_STATION));

				items.add(new PCEntryItem("", "", ""));
			}

			PCEntryAdapter adapter = new PCEntryAdapter(this, items);
			mListView.setAdapter(adapter);
			mLayout.addFillerView(mListView);
			// Binds the names with actual TransportStation objects
			mController.getStationsFromNames(list);
		}
	}

	/**
	 * Asks the server for connections in order to display the list of favorite
	 * stations along with the next departures.
	 */
	private void displayDestinations() {
		mLayout.hideText();
		// Gets the user's preferred destinations from the model
		HashMap<String, List<TransportTrip>> stations = mModel
				.getFavoriteStations();
		if (stations != null && !stations.isEmpty()) {
			// The user wants to leave EPFL
			if (mFromEpfl) {
				for (String loc : stations.keySet()) {
					mController.nextDepartures(M_EPFL_STATION, loc);
				}
				// The user wants to go to EPFL
			} else {
				for (String loc : stations.keySet()) {
					mController.nextDepartures(loc, M_EPFL_STATION);
				}
			}
		}
	}

	/**
	 * Called by the model when the data for the resulted connections has been
	 * updated.
	 */
	@Override
	public void connectionsUpdated(QueryTripsResult result) {
		if (mActionBar != null) {
			mActionBar = getActionBar();
		}
		if (mDirectionAction == null) {
			mDirectionAction = new ChangeDirectionAction();
		}
		mActionBar.removeAction(mDirectionAction);
		mActionBar.addAction(mDirectionAction, 0);

		HashMap<String, List<TransportTrip>> mDisplayedLocations = mModel
				.getFavoriteStations();
		// In case the button is still here
		mLayout.removeFillerView();
		mLayout.addFillerView(mListView);
		// Fill in the list view with the next departures
		setItemsToDisplay(mDisplayedLocations);
	}

	/**
	 * Called by the model when the list of favorite stations has been updated
	 * and refreshes the view.
	 */
	@Override
	public void favoriteStationsUpdated() {
		displayDestinations();
	}

	/**
	 * Called by the model when the stations from the names have been updated
	 * and displays the next departures.
	 */
	@Override
	public void stationsFromNamesUpdated(List<TransportStation> result) {
		displayDestinations();
	}

	/**
	 * Displays a message when an error happens upon contacting the server.
	 */
	@Override
	public void networkErrorHappened() {
		// Tracker
		Tracker.getInstance().trackPageView("transport/network_error");

		if (mDestPrefs.getAll() != null && !mDestPrefs.getAll().isEmpty()) {
			mLayout.removeFillerView();
			mLayout.setText(getResources().getString(
					R.string.transport_network_error));
		}
	}

	/**
	 * Called when connections are received from the server. Creates the items
	 * to be displayed (Station name with time until departure) and updates the
	 * shared preferences. (This is not done before getting the result, to make
	 * sure we store the correct station name in the preferences).
	 */
	private void setItemsToDisplay(
			HashMap<String, List<TransportTrip>> mDisplayedLocations) {

		Set<String> set = mDisplayedLocations.keySet();
		ArrayList<PCItem> items = new ArrayList<PCItem>();
		List<String> dest = new ArrayList<String>();
		dest.addAll(set);
		Collections.sort(dest);
		for (String l : dest) {

			if (!mDisplayedLocations.get(l).isEmpty()) {
				String from = DestinationFormatter
						.getNiceName(mDisplayedLocations.get(l).get(0)
								.getFrom());
				TransportTrip t = mDisplayedLocations.get(l).get(0);
				String to = DestinationFormatter
						.getNiceName(t.getParts().get(t.getParts().size()-1).arrival.getName());

				items.add(new PCSectionItem(from + " - " + to));
				int i = 0;

				for (TransportTrip c : mDisplayedLocations.get(l)) {
					if (i < 3) {
						Date dep = new Date();
						dep.setTime(c.getDepartureTime());
						Date now = new Date();
						if (dep.after(now)) {
							i++;
							// Updates the shared preferences
							if (mFromEpfl) {
								if (!c.getTo().getName()
										.equals("Ecublens VD, EPFL")) {
									mDestPrefsEditor.putInt(
											c.getParts().get(c.getParts().size()-1).arrival.getName(), 
											c.getParts().get(c.getParts().size()-1).arrival.getId());
									mDestPrefsEditor.commit();
								}
							} else {
								if (!c.getFrom().getName()
										.equals("Ecublens VD, EPFL")) {
									mDestPrefsEditor.putInt(
											c.getParts().get(0).departure.getName(),
											c.getParts().get(0).departure.getId());
									mDestPrefsEditor.commit();
								}
							}
							// String representing the type of transport
							String logo = "";
							for (TransportConnection p : c.parts) {
								if (!p.foot && p.line != null) {
									logo = p.line.getName();
									break;
								}
							}
							logo = TransportFormatter.getNiceName(logo);
							if (mFromEpfl) {
								PCEntryItem entry = new PCEntryItem(
										timeString(c.getDepartureTime()), logo,
										c.getParts().get(c.getParts().size()-1).arrival.getName() + ":"
												+ c.getDepartureTime() + ":"
												+ c.getArrivalTime() + ":"
												+ c.getId());
								items.add(entry);

							} else {
								PCEntryItem entry = new PCEntryItem(
										timeString(c.getDepartureTime()), logo,
										c.getParts().get(0).departure.getName() + ":"
												+ c.getDepartureTime() + ":"
												+ c.getArrivalTime() + ":"
												+ c.getId());
								items.add(entry);
							}
							// Add this departure
						}
					}
				}
			} else {
				// just show the title
				String title;
				if (mFromEpfl)
					title = M_EPFL_STATION + " - " + l;
				else
					title = l + " - " + M_EPFL_STATION;
				items.add(new PCSectionItem(title));
				items.add(new PCEntryItem("", "", ""));
			}
		}
		PCEntryAdapter adapter = new PCEntryAdapter(this, items);
		// Update the list view
		mListView.setAdapter(adapter);
		mListView.invalidate();
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
		String s = getResources().getString(R.string.transport_in);

		Date now = new Date();
		Date then = new Date();
		then.setTime(milliseconds);

		long diff = then.getTime() - now.getTime();
		Date timeTillDeparture = new Date();
		timeTillDeparture.setTime(diff);

		diff = diff / 1000; // seconds
		int minutes = (int) diff / 60; // minutes
		int hours = (int) diff / 3660; // hours

		if (hours > 0) {
			if (hours == 1) {
				s = s.concat(" " + hours + " "
						+ getResources().getString(R.string.transport_hour)
						+ ",");
			} else {
				s = s.concat(" " + hours + " "
						+ getResources().getString(R.string.transport_hours)
						+ ",");
			}
		}

		while (minutes > 60) {
			minutes = minutes - 60;
		}

		if (minutes > 0) {
			if (minutes == 1) {
				s = s.concat(" " + minutes + " "
						+ getResources().getString(R.string.transport_minute));
			} else {
				s = s.concat(" " + minutes + " "
						+ getResources().getString(R.string.transport_minutes));
			}
		}

		if (hours == 0 && minutes == 0) {
			s = getResources().getString(R.string.transport_departure_now);
		}

		return s;
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
			// Tracker
			Tracker.getInstance().trackPageView("transport/refresh");

			mLayout.removeFillerView();
			// mLayout.addFillerView(mListView);
			mModel.freeConnections();
			if (mModel.getFavoriteStations() == null
					|| mModel.getFavoriteStations().isEmpty()) {
				setUpDestinations();
			} else {
//				displayDestinations();
				setUpDestinations();
			}
		}
	}

	/**
	 * Change the direction of the trips when clicking on the action bar change
	 * direction button.
	 * 
	 * @author Oriane <oriane.rodriguez@epfl.ch>
	 * 
	 */
	private class ChangeDirectionAction implements Action {

		/**
		 * The constructor which doesn't do anything
		 */
		ChangeDirectionAction() {
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
			// Tracker
			Tracker.getInstance().trackPageView("transport/changed/direction");

			if (mFromEpfl) {
				mFromEpfl = false;
			} else {
				mFromEpfl = true;
			}

			mModel.freeConnections();
			if (mModel.getFavoriteStations() == null
					|| mModel.getFavoriteStations().isEmpty()) {
				setUpDestinations();
			} else {
				displayDestinations();
			}
		}
	}

	/**
	 * Not used in this view.
	 */
	@Override
	public void autoCompletedStationsUpdated() {
	}
}

package org.pocketcampus.plugin.transport.android;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCEntryAdapter;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCEntryItem;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCItem;
import org.pocketcampus.android.platform.sdk.ui.PCSectionedList.PCSectionItem;
import org.pocketcampus.android.platform.sdk.ui.adapter.RichLabeledArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.ButtonElement;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRichLabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledDoubleLayout;
import org.pocketcampus.android.platform.sdk.ui.list.RichLabeledListViewElement;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.shared.Connection;
import org.pocketcampus.plugin.transport.shared.Location;
import org.pocketcampus.plugin.transport.shared.QueryConnectionsResult;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The Main View of the Transport plugin, first displayed when accessing
 * Transport.
 * 
 * Displays the next departures for the destinations that the user set as
 * preferred destinations
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 * 
 */
public class TransportMainView extends PluginView implements ITransportView {
	/* MVC */
	/** The plugin controller */
	private TransportController mController;
	/** The plugin model */
	private TransportModel mModel;

	/* Layout */
	/** The main Layout */
	private StandardTitledDoubleLayout mLayout;
	/** The text displayed if the user has no destination set yet */
	private TextView mText;
	/** The listView to display next departures */
	private RichLabeledListViewElement mDestinationsList;

	/** The ListView to display next departures */
	private ListView mListView;
	/** The items */
	private ArrayList<PCItem> items;

	/** The adapter to contain the destinations displayed in the list */
	private RichLabeledArrayAdapter mAdapter;
	/** Displayed locations */
	private HashMap<String, List<Connection>> mDisplayedLocations;

	/* Preferences */
	/** The pointer to access and modify preferences stored on the phone */
	private SharedPreferences mDestPrefs;
	/** Interface to modify values in SharedPreferences object */
	private Editor mDestPrefsEditor;
	/** The name under which the preferences are stored on the phone */
	private static final String DEST_PREFS_NAME = "TransportDestinationsPrefs";

	/* Labelers */
	/** The labeler that says how to display a Location */
	private ILabeler<Location> mLocationLabeler = new ILabeler<Location>() {
		@Override
		public String getLabel(Location dest) {
			return dest.getName();
		}

	};

	/** The labeler that says how to display a Location */
	private IRichLabeler<Connection> mConnectionLabeler = new IRichLabeler<Connection>() {
		@Override
		public String getLabel(Connection dest) {
			return "";
		}

		@Override
		public String getTitle(Connection obj) {
			return obj.getTo().getName();
		}

		@Override
		public String getDescription(Connection obj) {
			return timeString(obj.getDepartureTime());
		}

		@Override
		public double getValue(Connection obj) {
			return -1;
		}

		@Override
		public Date getDate(Connection obj) {
			return null;
		}

	};

	/* Constants */
	/** The EPFL Station ID */
	private static final int EPFL_STATION_ID = 8501214;

	/**
	 * Defines what the main controller is for this view.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return TransportController.class;
	}

	/**
	 * Called once the view is connected to the controller. If you don't
	 * implement <code>getMainControllerClass()</code> then the controller given
	 * here will simply be <code>null</code>.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (TransportController) controller;
		mModel = (TransportModel) mController.getModel();

		mDestPrefs = getSharedPreferences(DEST_PREFS_NAME, 0);

		mLayout = new StandardTitledDoubleLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.transport_plugin_name));

		setContentView(mLayout);

		Map<String, Integer> prefs = (Map<String, Integer>) mDestPrefs.getAll();

		if (prefs == null || prefs.isEmpty()) {
			Log.d("TRANSPORT", "Prefs were null");
			// mText = new TextView(this);
			// mText.setText(getResources().getString(
			// R.string.transport_main_no_destinations));
			// mLayout.addFillerView(mText);

			/** If no destinations are set, redirect to TransportTimeView */
			Intent i = new Intent(this, TransportTimeView.class);
			startActivity(i);
		} else {
			Set<String> set = prefs.keySet();
			List<String> list = new ArrayList<String>();

			for (String s : set) {
				Log.d("TRANSPORT", s + " was in prefs");
				list.add(s);
			}

			mController.getLocationsFromNames(list);
		}

	}

	/**
	 * Called when this view is accessed after already having been initialized
	 * before
	 */
	// @Override
	protected void onRestart() {
		super.onRestart();
		Log.d("ACTIVITY", "onRestart");
		displayDestinations();
	}

	/**
	 * Main Transport Options menu contains access to the preferred destinations
	 * and the settings
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.transport_menu, menu);
		return true;
	}

	/**
	 * Decides what happens when the options menu is opened and an option is
	 * chosen (what view to display)
	 */
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.transport_destinations) {
			Intent i = new Intent(this, TransportTimeView.class);
			startActivity(i);
		} /*
		 * else if (id == R.id.transport_settings) { Log.d("TRANSPORT",
		 * "Settings");
		 * 
		 * }
		 */

		return true;
	}

	/**
	 * Ask the server for connections in order to display the list of preferred
	 * destinations along with the next departures to go there
	 */
	private void displayDestinations() {
		ButtonElement b = new ButtonElement(this);
		b.setId(1);

		b.setText(getResources().getString(R.string.transport_add_destination));

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		b.setLayoutParams(params);

		b.setOnClickListener(new OnClickListener() {

			/**
			 * Starts the TransportTimeView
			 */
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(),
						TransportTimeView.class);
				startActivity(i);
			}
		});

		mLayout.addFirstLayoutFillerView(b);

		List<Location> locations = mModel.getPreferredDestinations();

		if (locations != null && !locations.isEmpty()) {

			// mDisplayedLocations = new ArrayList<Connection>();
			// mDestinationsList = new RichLabeledListViewElement(this,
			// mDisplayedLocations, mConnectionLabeler);
			//
			// mDestinationsList.setOnItemClickListener(new
			// OnItemClickListener() {
			//
			// @Override
			// public void onItemClick(AdapterView<?> arg0, View arg1,
			// int arg2, long arg3) {
			// Connection c = (Connection) arg0.getItemAtPosition(arg2);
			//
			// // Toast.makeText(getApplicationContext(),
			// // "Clicked on " + c.getTo().getName(), Toast.LENGTH_SHORT)
			// // .show();
			// }
			// });

			/** NEW */
			items = new ArrayList<PCItem>();

			mDisplayedLocations = new HashMap<String, List<Connection>>();

			for (Location loc : locations) {
				Log.d("TRANSPORT", "Added section " + loc.getName());

				mDisplayedLocations.put(loc.getName(), new ArrayList<Connection>());
				mController.nextDeparturesFromEPFL(loc.getName());
			}
			mListView = new ListView(this);
			mLayout.addSecondLayoutFillerView(mListView);
			
			setItemsToDisplay();
		}

	}

	/**
	 * Called by the model when the data for the resulted connection has been
	 * updated
	 */
	@Override
	public void connectionUpdated(QueryConnectionsResult result) {
		Log.d("TRANSPORT", "Connection Updated (view)");

		if (result != null) {
			List<Connection> connections = result.getConnections();

			if (connections != null && !connections.isEmpty()) {

				int i = 0;
				for (Connection c : connections) {
					if (i < 3) {
						i++;
						Log.d("TRANSPORT",
								"Added item " + timeString(c.getArrivalTime()));

						mDisplayedLocations.get(c.getTo().getName()).add(c);
						

						//						items.add(new PCEntryItem(
						//								timeString(c.getArrivalTime()), ""));

						//						PCEntryAdapter adapter = new PCEntryAdapter(this, items);

						//						mListView.setAdapter(adapter);
						//						mListView.invalidate();
					}

					// if (!mDisplayedLocations.contains(c)) {
					// mDisplayedLocations.add(c);
					// } else {
					//
					// }
					// mAdapter = new RichLabeledArrayAdapter(this,
					// mDisplayedLocations, mConnectionLabeler);

					// mDestinationsList.setAdapter(mAdapter);
					// mDestinationsList.invalidate();
				}
				setItemsToDisplay();
			}
		} else {
			Log.d("TRANSPORT", "Bouuuuhouhou ! (view)");
		}
	}

	/**
	 * Called by the model when the list of preferred destinations has been
	 * updated and refreshes the view
	 */
	@Override
	public void destinationsUpdated() {
		Log.d("TRANSPORT", "Destinations updated (view)");
		displayDestinations();
	}

	/**
	 * Called by the model when the locations from the destinations names have
	 * been updated and display the next departures
	 */
	@Override
	public void locationsFromNamesUpdated(List<Location> result) {
		Log.d("TRANSPORT", "Locations from Names updated (view)");
		displayDestinations();
	}

	/**
	 * Displays a toast when an error happens upon contacting the server
	 */
	@Override
	public void networkErrorHappened() {
		Log.d("TRANSPORT", "Network error (view)");
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!",
				Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * Not used in this view
	 */
	@Override
	public void autoCompletedDestinationsUpdated() {
	}

	/**
	 * Returns a string representing the date by its hours and minutes
	 * 
	 * @param millisec
	 * @return textDate The string representing the date as hours and minutes
	 */
	private String timeString(long millisec) {
		String textDate = "";

		Date now = new Date();
		Date date = new Date();
		date.setTime(millisec);

		Date minutes = new Date();

		Log.d("TRANSPORT", "Now : " + now);
		Log.d("TRANSPORT", "Departure : " + date);

		minutes.setTime(date.getTime() - now.getTime());
		textDate = "in " + (minutes.getHours() - 1) + " hours, "
				+ minutes.getMinutes() + " minutes";

		return textDate;
	}

	/**
	 * 
	 */
	private void setItemsToDisplay() {
		Set<String> set = mDisplayedLocations.keySet();

		for(String l : set) {
			items.add(new PCSectionItem(l));

			int i = 0;
			for (Connection c : mDisplayedLocations.get(l)) {
				if (i < 3) {
					i++;
					items.add(new PCEntryItem(timeString(c.getArrivalTime()), ""));
				}
			}
		}
		
		PCEntryAdapter adapter = new PCEntryAdapter(this, items);

//		mListView = new ListView(this);
		mListView.setAdapter(adapter);
		mListView.invalidate();
		
//		mLayout.removeSecondLayoutFillerView();
//		mLayout.addSecondLayoutFillerView(mListView);
	}
}

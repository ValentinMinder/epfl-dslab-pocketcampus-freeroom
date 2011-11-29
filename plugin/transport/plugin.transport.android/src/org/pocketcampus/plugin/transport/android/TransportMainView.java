package org.pocketcampus.plugin.transport.android;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
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
	private StandardTitledLayout mLayout;
	/** The text displayed if the user has no destination set yet */
	private TextView mText;

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

		mLayout = new StandardTitledLayout(this);

		mLayout.setTitle(getResources().getString(
				R.string.transport_plugin_name));

		setContentView(mLayout);

		if (mDestPrefs.getAll() == null || mDestPrefs.getAll().isEmpty()) {
			mText = new TextView(this);
			mText.setText(getResources().getString(
					R.string.transport_main_no_destinations));
			mLayout.addFillerView(mText);
		} else {
			Log.d("TRANSPORT", "Prefs weren't null");
			displayDestinations();
		}

	}

	/**
	 * Called when this view is accessed after already having been initialized
	 * before
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("ACTIVITY", "onRestart");
		// Refresh the list
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
		} /*else if (id == R.id.transport_settings) {
			Log.d("TRANSPORT", "Settings");

		}*/

		return true;
	}

	/**
	 * Display the list of preferred destinations along woth the next departures
	 * to go there
	 */
	private void displayDestinations() {
		List<Location> locations = mModel.getPreferredDestinations();

		if (locations != null && !locations.isEmpty()) {
			mLayout.removeFillerView();

			for (Location loc : locations) {
				mController.nextDepartures(loc.getName());
			}
		}

	}

	/**
	 * Not used in this view
	 */
	@Override
	public void autoCompletedDestinationsUpdated() {
//		Not used in this view
	}

	/**
	 * Called by the model when the data for the resulted connection has been
	 * updated
	 */
	@Override
	public void connectionUpdated(QueryConnectionsResult result) {
		Log.d("TRANSPORT", "Connection Updated (view)");

		if(result != null) {
			List<Connection> connections = result.getConnections();

			if(connections != null && !connections.isEmpty()) {
				for (Connection c : connections) {
					Date date = new Date();
					Location from = c.getFrom();
					Location to = c.getTo();
					date.setTime(c.getDepartureTime());
					Log.d("TRANSPORT", "Next departures from " + from.getName() + " to " + to.getName()
							+ " at " + date);
				}
			}
		} else {
			Log.d("TRANSPORT","Bouuuuhouhou ! (view)");
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
	 * Displays a toast when an error happens upon contacting the server
	 */
	@Override
	public void networkErrorHappened() {
		Log.d("TRANSPORT", "Network error (view)");
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!",
				Toast.LENGTH_SHORT);
		toast.show();
	}
}

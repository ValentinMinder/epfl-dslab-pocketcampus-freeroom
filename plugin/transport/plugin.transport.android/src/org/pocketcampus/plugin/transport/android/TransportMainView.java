package org.pocketcampus.plugin.transport.android;

import java.text.SimpleDateFormat;
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
import org.pocketcampus.android.platform.sdk.ui.dialog.PCDetailsDialog;
import org.pocketcampus.android.platform.sdk.ui.element.ButtonElement;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRichLabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledDoubleLayout;
import org.pocketcampus.android.platform.sdk.ui.list.RichLabeledListViewElement;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.android.utils.TransportFormatter;
import org.pocketcampus.plugin.transport.shared.Connection;
import org.pocketcampus.plugin.transport.shared.Location;
import org.pocketcampus.plugin.transport.shared.Part;
import org.pocketcampus.plugin.transport.shared.QueryConnectionsResult;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;

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
	/** Main Activity */
	private Activity mActivity;

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
	//	/** Displayed locations */
	//	private HashMap<String, List<Connection>> mDisplayedLocations;

	/* Preferences */
	/** The pointer to access and modify preferences stored on the phone */
	private SharedPreferences mDestPrefs;
	/** Interface to modify values in SharedPreferences object */
	private Editor mDestPrefsEditor;
	/** The name under which the preferences are stored on the phone */
	private static final String DEST_PREFS_NAME = "TransportDestinationsPrefs";

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
			return stringifier(obj);
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
		mActivity = this;
		mController = (TransportController) controller;
		mModel = (TransportModel) mController.getModel();

		mDestPrefs = getSharedPreferences(DEST_PREFS_NAME, 0);
		mDestPrefsEditor = mDestPrefs.edit();

		mLayout = new StandardTitledDoubleLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.transport_plugin_name));
		mLayout.hideTitle();

		setContentView(mLayout);

		/** Set up edit button in the action bar */
		Intent intent = new Intent(getApplicationContext(), TransportEditView.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ActionBar a = getActionBar();
		if(a != null) {			
			a.addAction(new ActionBar.IntentAction(getApplicationContext(), intent,
					R.drawable.transport_action_bar_edit));
		}

		Map<String, Integer> prefs = (Map<String, Integer>) mDestPrefs.getAll();

		if (prefs == null || prefs.isEmpty()) {
			Log.d("TRANSPORT", "Prefs were null");

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
	@Override
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
		/** List of next departures */
		HashMap<String, List<Connection>> locations = mModel.getPreferredDestinations();

		if (locations != null && !locations.isEmpty()) {
			items = new ArrayList<PCItem>();

			for (String loc : locations.keySet()) {
				Log.d("TRANSPORT", "Request for " + loc);
				mController.nextDeparturesFromEPFL(loc);
			}

			mListView = new ListView(this);
			mLayout.removeSecondLayoutFillerView();
			mLayout.addSecondLayoutFillerView(mListView);

			setItemsToDisplay(locations);
		}

	}

	/**
	 * Called by the model when the data for the resulted connection has been
	 * updated
	 */
	@Override
	public void connectionUpdated(QueryConnectionsResult result) {
		Log.d("TRANSPORT", "Connection Updated (view)");
		HashMap<String, List<Connection>> mDisplayedLocations = mModel.getPreferredDestinations();
		
		items = new ArrayList<PCItem>();
		
		mListView = new ListView(this);
		mLayout.removeSecondLayoutFillerView();
		mLayout.addSecondLayoutFillerView(mListView);

		setItemsToDisplay(mDisplayedLocations);

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
	 * 
	 */
	private void setItemsToDisplay(HashMap<String, List<Connection>> mDisplayedLocations) {
		Set<String> set = mDisplayedLocations.keySet();
		items = new ArrayList<PCItem>();

		for (String l : set) {
			if (!mDisplayedLocations.get(l).isEmpty()) {

				items.add(new PCSectionItem(l));

				int i = 0;
				for (Connection c : mDisplayedLocations.get(l)) {
					if (i < 3) {
						i++;
						
						mDestPrefsEditor.putInt(c.getTo().getName(), c.getTo().getId());
						mDestPrefsEditor.commit();
						
						String logo = "";
						for (Part p : c.parts) {
							if (!p.foot) {
								logo = p.line.label;
								break;
							}
						}

						logo = TransportFormatter.getNiceName(logo);
						PCEntryItem entry = new PCEntryItem(timeString(c.getDepartureTime()),
								logo, c.id);

						items.add(entry);
					}
				}
			}
		}

		PCEntryAdapter adapter = new PCEntryAdapter(this, items);

		mListView.setAdapter(adapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				String txt = ((PCEntryItem) ((ListView) arg0)
						.getItemAtPosition(arg2)).id;

				Set<String> s = mModel.getPreferredDestinations().keySet();
				String[] dests = new String[s.size()];
				dests = s.toArray(dests);

				if (dests != null) {
					detailsDialog(txt);
				}

			}
		});

		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				String txt = ((PCSectionItem) ((ListView) arg0)
						.getItemAtPosition(arg2)).getTitle();
				Toast.makeText(TransportMainView.this,
						"Removing " + txt + " from favourites",
						Toast.LENGTH_LONG).show();
				// TODO effectivly remove this station from the fav
				return true;
			}
		});

		mListView.invalidate();

	}

	private String timeString(long milliseconds) {
		String s = getResources().getString(R.string.transport_in);

		Date now = new Date();
		Date then = new Date();
		then.setTime(milliseconds);

		long diff = then.getTime()-now.getTime();
		Date timeTillDeparture = new Date();
		timeTillDeparture.setTime(diff);

		diff = diff/1000; //seconds
		int minutes = (int)diff/60; //minutes
		int hours = (int)diff/3660; //hours

		if(hours > 0) {
			if(hours == 1) {
				s = s.concat(" " + hours + " "+ getResources().getString(R.string.transport_hour )+",");
			}else {				
				s = s.concat(" " + hours + " "+ getResources().getString(R.string.transport_hours )+",");
			}
		}

		while(minutes > 60) {
			minutes = minutes - 60;
		}

		if(minutes > 0) {
			if(minutes == 1) {
				s = s.concat(" " + minutes + " " + getResources().getString(R.string.transport_minute));
			} else {
				s = s.concat(" " + minutes + " " + getResources().getString(R.string.transport_minutes));
			}
		}
		
		if(hours == 0 && minutes == 0) {
			s = getResources().getString(R.string.transport_departure_now);
		}

		return s;
	}

	/**
	 * 
	 * @param c
	 * @return
	 */
	private String stringifier(Connection c) {
		final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm");
		String r = getResources().getString(R.string.transport_departure_at) + " "
				+ FORMAT.format(c.getDepartureTime());

		return r;
	}

	/**
	 * 
	 * @param c
	 * @return
	 */
	private String stringifierDetails(Connection c) {
		final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm");
		String r = getResources().getString(R.string.transport_departure_at) + " "
				+ FORMAT.format(c.getDepartureTime()) + ", "
				+ getResources().getString(R.string.transport_arrival_at) + ": "
				+ FORMAT.format(c.getArrivalTime());

		r += "\n" + c.getFrom();
		for (Part p : c.getParts()) {
			r += " -> " + p.getArrival();
		}

		return r;
	}

	/**
	 * Creates a menu dialog for a particular meal
	 * 
	 * @param connection
	 */
	public void detailsDialog(String connection) {
		// Create the Builder for the Menu dialog
		PCDetailsDialog.Builder b = new PCDetailsDialog.Builder(mActivity);
		b.setCanceledOnTouchOutside(true);

		// Set different values for the dialog
		b.setTitle(connection);

		PCDetailsDialog dialog = b.create();
		dialog.show();
	}
}

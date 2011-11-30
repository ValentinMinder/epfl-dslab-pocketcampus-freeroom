package org.pocketcampus.plugin.transport.android;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.adapter.LabeledArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.InputBarElement;
import org.pocketcampus.android.platform.sdk.ui.element.OnKeyPressedListener;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledDoubleLayout;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.shared.Location;
import org.pocketcampus.plugin.transport.shared.QueryConnectionsResult;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A view of the Transport Plugin. Displays an input bar to let the user type a
 * destination, choose among autocompleted resulted destinations and set one of
 * them as a preferred destination.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author FLorian <florian.laurent@epfl.ch>
 */
public class TransportTimeView extends PluginView implements ITransportView {
	/** The plugin controller */
	private TransportController mController;
	/** The plugin model */
	private TransportModel mModel;
	/** The main layout */
	private StandardTitledDoubleLayout mLayout;
	/** The text layout */
	private TextView mText;
	/** An input bar element to type a destination */
	private InputBarElement mInputBar;
	/** The list view to display autocompleted destinations */
	private LabeledListViewElement mListView;
	/** The adapter to contain the destinations displayed in the list */
	LabeledArrayAdapter mAdapter;
	/* Preferences */
	/** The pointer to access and modify preferences stored on the phone */
	private SharedPreferences mDestPrefs;
	/** Interface to modify values in SharedPreferences object */
	private Editor mDestPrefsEditor;
	/** The name under which the preferences are stored on the phone */
	private static final String DEST_PREFS_NAME = "TransportDestinationsPrefs";

	/**
	 * Defines what the main controller is for this view.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return TransportController.class;
	}

	/**
	 * On display. Called when first displaying the view Retrieve the model,
	 * controller and the preferences and calls onDisplay to create the layout.
	 * Then it calls the method that creates the destinations list filled with
	 * the preferred destinations of the user
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (TransportController) controller;
		mModel = (TransportModel) mController.getModel();

		mDestPrefs = getSharedPreferences(DEST_PREFS_NAME, 0);
		mDestPrefsEditor = mDestPrefs.edit();

		displayView();

		mLayout.addSecondLayoutFillerView(mInputBar);
		setContentView(mLayout);

		createDestinationsList();
	}

	/**
	 * Creates the layout with the inputbar for typing a destination
	 */
	private void displayView() {

		mLayout = new StandardTitledDoubleLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.transport_plugin_name));

		mInputBar = new InputBarElement(this);
		mInputBar.setInputHint(getResources().getString(
				R.string.transport_set_destinations_message));

		mInputBar.setOnKeyPressedListener(new OnKeyPressedListener() {
			@Override
			public void onKeyPressed(String text) {
				mController.getAutocompletions(text);
			}
		});
	}

	/**
	 * Creates the list that will contain all autocompleted destinations and
	 * sets the click listener on one of these destinations
	 */
	private void createDestinationsList() {
		mListView = new LabeledListViewElement(this);
		mInputBar.addView(mListView);

		/** On Item click Listener */
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				Location location = (Location) adapter.getItemAtPosition(pos);
				Log.d("TRANSPORT", "Clicked on " + location.getName()
						+ " with ID : " + location.getId());
				mDestPrefsEditor.putInt(location.getName(), location.getId());
				mDestPrefsEditor.commit();
				mModel.setNewPreferredDestination(location);
				/** Go back to the main View and refreshed */
				finish();
			}
		});
	}

	/**
	 * Refreshes the adapter containing the current autocompleted destinations
	 */
	@Override
	public void autoCompletedDestinationsUpdated() {
		mAdapter = new LabeledArrayAdapter(this,
				mModel.getAutoCpmpletedDestinations(), mLocationLabeler);

		mListView.setAdapter(mAdapter);
		mListView.invalidate();
	}

	/**
	 * The labeler to tell a view how to display a Location
	 */
	private ILabeler<Location> mLocationLabeler = new ILabeler<Location>() {
		@Override
		public String getLabel(Location obj) {
			return obj.getName();
		}
	};

	/**
	 * Displays a toast when an error happens upon contacting the server
	 */
	@Override
	public void networkErrorHappened() {
		Toast toast = Toast.makeText(getApplicationContext(), "Network error!",
				Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * Not used in this view
	 */
	@Override
	public void destinationsUpdated() {}

	/**
	 * Not used in this view
	 */
	@Override
	public void connectionUpdated(QueryConnectionsResult result) {}

	/**
	 * Not used in this view
	 */
	@Override
	public void locationsFromNamesUpdated(List<Location> result) {}

}

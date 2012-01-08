package org.pocketcampus.plugin.transport.android;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.tracker.Tracker;
import org.pocketcampus.android.platform.sdk.ui.adapter.LabeledArrayAdapter;
import org.pocketcampus.android.platform.sdk.ui.element.InputBarElement;
import org.pocketcampus.android.platform.sdk.ui.element.OnKeyPressedListener;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.plugin.transport.android.iface.ITransportView;
import org.pocketcampus.plugin.transport.shared.QueryTripsResult;
import org.pocketcampus.plugin.transport.shared.TransportStation;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

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
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 */
public class TransportAddView extends PluginView implements ITransportView {
	/** The plugin controller. */
	private TransportController mController;
	/** The plugin model. */
	private TransportModel mModel;
	/** The main layout consisting of two inner layouts and a title. */
	private StandardTitledLayout mLayout;
	/** An <code>InputBarElement</code> to type a station. */
	private InputBarElement mInputBar;
	/** The list to display auto completed stations. */
	private LabeledListViewElement mListView;
	/** The adapter containing the stations displayed in the list. */
	LabeledArrayAdapter mAdapter;

	/**
	 * A labeler telling the view how to display a <code>TransportStation</code>
	 * .
	 */
	private ILabeler<TransportStation> mLocationLabeler = new ILabeler<TransportStation>() {
		@Override
		public String getLabel(TransportStation obj) {
			return obj.getName();
		}
	};

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
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		// Tracker
		Tracker.getInstance().trackPageView("transport/addView");

		mController = (TransportController) controller;
		mModel = (TransportModel) mController.getModel();
		// Display the view
		displayView();
		// Create the list of next departures
		createStationsList();
	}

	/**
	 * Creates the layout with the input bar in which the user can type a
	 * station, and sets the listener to get auto completion when a key is
	 * pressed.
	 */
	private void displayView() {
		// Layout
		mLayout = new StandardTitledLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.transport_add_station));

		// Input bar
		mInputBar = new InputBarElement(this);
		mInputBar.setInputHint(getResources().getString(
				R.string.transport_set_stations_message));

		mInputBar.setOnKeyPressedListener(new OnKeyPressedListener() {
			@Override
			public void onKeyPressed(String text) {
				mController.getAutocompletions(text);
			}
		});

		// Add the input bar to the layout
		mLayout.addFillerView(mInputBar);
		setContentView(mLayout);
	}

	/**
	 * Creates the list that containing all auto completed stations and sets the
	 * click listener on a station.
	 */
	private void createStationsList() {
		mListView = new LabeledListViewElement(this);
		mInputBar.addView(mListView);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			/**
			 * When the user clicks on a station, we ask the controller for next
			 * departures from EPFL to this station and close this
			 * <code>Activity</code>.
			 */
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				TransportStation station = (TransportStation) adapter
						.getItemAtPosition(pos);

				// Tracker
				Tracker.getInstance().trackPageView(
						"transport/addView/add" + station.getName());

				// Request for the next departures from EPFL to the station
				// the user just clicked
				mController.nextDepartures("EPFL", station.getName());
				// Go back to the main View
				finish();
			}
		});
	}

	/**
	 * Refreshes the adapter containing the current auto completed stations.
	 */
	@Override
	public void autoCompletedStationsUpdated() {
		mAdapter = new LabeledArrayAdapter(this,
				mModel.getAutoCompletedStations(), mLocationLabeler);
		mListView.setAdapter(mAdapter);
		mListView.invalidate();
	}

	/**
	 * Called when an error happens upon contacting the server.
	 */
	@Override
	public void networkErrorHappened() {
		// Tracker
		Tracker.getInstance().trackPageView("transport/addView/network_error");
	}

	/**
	 * Not used in this view.
	 */
	@Override
	public void favoriteStationsUpdated() {
	}

	/**
	 * Not used in this view.
	 */
	@Override
	public void connectionsUpdated(QueryTripsResult result) {
	}

	/**
	 * Not used in this view.
	 */
	@Override
	public void stationsFromNamesUpdated(List<TransportStation> result) {
	}

}

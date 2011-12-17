package org.pocketcampus.plugin.transport.android;

import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The view of the Transport plugin which lets the user add a preferred
 * destination. This view displays an input bar in which the user can type a
 * destination, choose among auto completed destinations that the server is
 * sending and set one of them as a preferred destination by just clicking on
 * it.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * @author Pascal <pascal.scheiben@epfl.ch>
 * @author Florian <florian.laurent@epfl.ch>
 */
public class TransportAddView extends PluginView implements ITransportView {
	/** The plugin controller */
	private TransportController mController;
	/** The plugin model */
	private TransportModel mModel;
	/** The main layout consisting of two inner layouts and a title */
	private StandardTitledLayout mLayout;
	/** An input bar element to type a destination */
	private InputBarElement mInputBar;
	/** The list view to display auto completed destinations */
	private LabeledListViewElement mListView;
	/** The adapter that contains the destinations displayed in the list */
	LabeledArrayAdapter mAdapter;

	/**
	 * The labeler to tell a view how to display a Location
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
	 * On display. Called when first displaying the view. Retrieve the model and
	 * the controller and calls the <code>displayView()</code> method that
	 * creates the input bar, adds it to the main layout and then calls the
	 * <code>createDestinationsList()</code> methods which fills the list with
	 * auto completed destinations as the user is typing.
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (TransportController) controller;
		mModel = (TransportModel) mController.getModel();
		// Display the view
		displayView();
		// Create the list of next departures
		createDestinationsList();
	}

	/**
	 * Creates the layout with the input bar to type a destination and sets the
	 * listener to get auto completion when a key is pressed.
	 */
	private void displayView() {
		// Layout
		mLayout = new StandardTitledLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.transport_add_destination));

		// Input bar
		mInputBar = new InputBarElement(this);
		mInputBar.setInputHint(getResources().getString(
				R.string.transport_set_destinations_message));

		mInputBar.setOnKeyPressedListener(new OnKeyPressedListener() {
			@Override
			public void onKeyPressed(String text) {
				mController.getAutocompletions(text);
			}
		});

		mLayout.addFillerView(mInputBar);
		setContentView(mLayout);
	}

	/**
	 * Creates the list that will contain all auto completed destinations and
	 * sets the click listener on these destinations
	 */
	private void createDestinationsList() {
		mListView = new LabeledListViewElement(this);
		mInputBar.addView(mListView);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				TransportStation location = (TransportStation) adapter
						.getItemAtPosition(pos);
				// Request for the next departures from EPFL to the destination
				// the user just clicked
				mController.nextDeparturesFromEPFL(location.getName());
				// Go back to the main View
				finish();
			}
		});
	}

	/**
	 * Refreshes the adapter containing the current auto completed destinations.
	 */
	@Override
	public void autoCompletedDestinationsUpdated() {
		mAdapter = new LabeledArrayAdapter(this,
				mModel.getAutoCompletedDestinations(), mLocationLabeler);
		mListView.setAdapter(mAdapter);
		mListView.invalidate();
	}

	/**
	 * Displays a toast when an error happens upon contacting the server.
	 */
	@Override
	public void networkErrorHappened() {
		// Toast toast = Toast.makeText(getApplicationContext(), getResources()
		// .getString(R.string.transport_network_error),
		// Toast.LENGTH_SHORT);
		// toast.show();
	}

	/**
	 * Not used in this view
	 */
	@Override
	public void destinationsUpdated() {
	}

	/**
	 * Not used in this view
	 */
	@Override
	public void connectionsUpdated(QueryTripsResult result) {
	}

	/**
	 * Not used in this view
	 */
	@Override
	public void locationsFromNamesUpdated(List<TransportStation> result) {
	}

}

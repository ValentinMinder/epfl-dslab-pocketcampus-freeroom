package org.pocketcampus.plugin.satellite.android;

import java.util.Date;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRichLabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardTitledLayout;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteEventsView;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteMainView;
import org.pocketcampus.plugin.satellite.shared.Event;

import android.app.Service;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * The Events View of the Satellite plugin. Displays the list of next events
 * scheduled at Satellite.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 */
public class SatelliteEventsView extends PluginView implements
		ISatelliteMainView {
	/** The Plugin controller */
	private SatelliteController mController;
	/** The Plugin Model */
	private SatelliteModel mModel;
	/** A Standard Titled Layout */
	private StandardTitledLayout mLayout;

	/** Returns the class of the SatelliteController */
	@Override
	protected Class<? extends Service> getMainControllerClass() {
		return SatelliteController.class;
	}

	/**
	 * Initializes the view for the events
	 */
	@Override
	protected void onDisplay(Bundle savedInstanceState,
			PluginController controller) {
		mController = (SatelliteController) controller;
		mModel = (SatelliteModel) mController.getModel();

		mLayout = new StandardTitledLayout(this);
		mLayout.setTitle(getResources().getString(
				R.string.satellite_menu_events));
		mLayout.setText(getResources().getString(
				R.string.satellite_nothing_to_display));
		setContentView(mLayout);

		showEvents();
	}

	private void showEvents() {
		mController.getEvents();
	}

	@Override
	public void eventsUpdated() {
		Log.d("SATELLITE", "Events updated (View)");
		mLayout.removeFillerView();
		
		List<Event> events = mModel.getEvents();

		if (events != null && !events.isEmpty()) {
			LabeledListViewElement l = new LabeledListViewElement(this,
					events, mEventLabeler);

			mLayout.hideText();
			mLayout.addFillerView(l);
		}
	}

	@Override
	public void networkErrorHappened() {
		Toast.makeText(this,
				getResources().getString(R.string.satellite_network_error),
				Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * The labeler for an Event, to tell how it has to be displayed in a generic
	 * view.
	 */
	IRichLabeler<Event> mEventLabeler = new IRichLabeler<Event>() {

		@Override
		public String getTitle(Event event) {
			return event.getTitle();
		}

		@Override
		public String getDescription(Event event) {
			return event.getDescription();
		}

		@Override
		public double getValue(Event event) {
			return event.getPrice();
		}

		@Override
		public Date getDate(Event event) {
			return new Date((long) event.getDate());
		}

		@Override
		public String getLabel(Event event) {
			return "";
		}

	};

	@Override
	public void beerUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beersUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void affluenceUpdated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sandwichesUpdated() {
		// TODO Auto-generated method stub
		
	}


}

package org.pocketcampus.plugin.satellite.android;

import java.util.Date;
import java.util.List;

import org.pocketcampus.R;
import org.pocketcampus.android.platform.sdk.core.PluginController;
import org.pocketcampus.android.platform.sdk.core.PluginView;
import org.pocketcampus.android.platform.sdk.ui.labeler.ILabeler;
import org.pocketcampus.android.platform.sdk.ui.labeler.IRichLabeler;
import org.pocketcampus.android.platform.sdk.ui.layout.StandardLayout;
import org.pocketcampus.android.platform.sdk.ui.list.LabeledListViewElement;
import org.pocketcampus.android.platform.sdk.ui.list.RichLabeledListViewElement;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteMainView;
import org.pocketcampus.plugin.satellite.shared.Affluence;
import org.pocketcampus.plugin.satellite.shared.Beer;
import org.pocketcampus.plugin.satellite.shared.Event;
import org.pocketcampus.plugin.satellite.shared.Sandwich;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

/**
 * The Main View of the Satellite plugin, first displayed when accessing
 * Satellite.
 * 
 * Displays the beer of the month, a list of all beers, the next events and the
 * affluence at Satellite.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SatelliteMainView extends PluginView implements ISatelliteMainView {

	/** Main Activity */
	private Activity mActivity;

	/** Satellite Model */
	private SatelliteModel mModel;

	/** Satellite Controller */
	private SatelliteController mController;

	/** Standard Layout */
	private StandardLayout mLayout;

	/**
	 * Defines what the main controller is for this view.
	 */
	@Override
	protected Class<? extends PluginController> getMainControllerClass() {
		return SatelliteController.class;
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
		mController = (SatelliteController) controller;
		mModel = (SatelliteModel) mController.getModel();

		mLayout = new StandardLayout(this);
		setContentView(mLayout);

		showMainPage();
	}

	/**
	 * Called when this view is accessed after already having been initialized
	 * before
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
	}

	public void showMainPage() {
		mLayout.setText(getResources().getString(
				R.string.satellite_nothing_to_display));

		mController.getAffluence();
	}

	public void showSandwiches() {
		mController.getSandwiches();
	}

	public void showEvents() {
		mController.getEvents();
	}

	public void showBeers() {

	}

	/**
	 * Main Satellite Options menu contains access to Beers, Events, Sandwiches
	 * and back to Main Page
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.satellite_menu, menu);
		return true;
	}

	/**
	 * Decides what happens when the options menu is opened and an option is
	 * chosen (what view to display)
	 */
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		if (item.getItemId() == R.id.satellite_main_page) {
			showMainPage();
		} else if (item.getItemId() == R.id.satellite_beers) {
			showBeers();
		} else if (item.getItemId() == R.id.satellite_events) {
			showEvents();
		} else if (item.getItemId() == R.id.satellite_sandwiches) {
			showSandwiches();
		}
		return true;
	}

	@Override
	public void beerUpdated() {

	}

	@Override
	public void beersUpdated() {
		Log.d("SATELLITE", "Beers updated (View)");
		List<Beer> beers = mModel.getAllBeers();

		if (beers != null && !beers.isEmpty()) {
			RichLabeledListViewElement l = new RichLabeledListViewElement(this,
					beers, mBeerLabeler);

			mLayout.addView(l);
		}

	}

	@Override
	public void sandwichesUpdated() {
		Log.d("SATELLITE", "Sandwiches updated (View)");
		List<Sandwich> sandwiches = mModel.getSandwiches();

		if (sandwiches != null && !sandwiches.isEmpty()) {
			mLayout.setText("");

			LabeledListViewElement l = new LabeledListViewElement(this,
					sandwiches, mSandwichLabeler);

			mLayout.addView(l);
		}
	}

	@Override
	public void affluenceUpdated() {
		Affluence a = mModel.getAffluence();

		if (a != null) {
			mLayout.setText(getResources().getString(
					R.string.satellite_affluence)
					+ " : " + a.name());
		}
	}

	@Override
	public void eventsUpdated() {

	}

	/**
	 * Displays a toast when an error happens upon contacting the server
	 */
	@Override
	public void networkErrorHappened() {
		Toast toast = Toast
				.makeText(getApplicationContext(),
						getString(R.string.satellite_network_error),
						Toast.LENGTH_SHORT);
		toast.show();
	}

	/**
	 * The labeler for a Sandwich, to tell how it has to be displayed in a
	 * generic view.
	 */
	ILabeler<Sandwich> mSandwichLabeler = new ILabeler<Sandwich>() {

		@Override
		public String getLabel(Sandwich sandwich) {
			return sandwich.getName();
		}

	};

	/**
	 * The labeler for a Beer, to tell how it has to be displayed in a generic
	 * view.
	 */
	IRichLabeler<Beer> mBeerLabeler = new IRichLabeler<Beer>() {

		@Override
		public String getTitle(Beer beer) {
			return beer.getName();
		}

		@Override
		public String getDescription(Beer beer) {
			return beer.getDescription();
		}

		@Override
		public double getValue(Beer beer) {
			return beer.getPrice();
		}

		@Override
		public Date getDate(Beer beer) {
			return null;
		}

	};

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

	};

}
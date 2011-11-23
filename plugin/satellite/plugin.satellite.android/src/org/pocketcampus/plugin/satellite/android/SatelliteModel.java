package org.pocketcampus.plugin.satellite.android;

import java.util.List;

import org.pocketcampus.android.platform.sdk.core.IView;
import org.pocketcampus.android.platform.sdk.core.PluginModel;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteEventsView;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteMainView;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteModel;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteSandwichesView;
import org.pocketcampus.plugin.satellite.shared.Affluence;
import org.pocketcampus.plugin.satellite.shared.Beer;
import org.pocketcampus.plugin.satellite.shared.Event;
import org.pocketcampus.plugin.satellite.shared.Sandwich;

import android.util.Log;

/**
 * The Model of the satellite plugin, used to handle the information that is
 * going to be displayed in the views
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SatelliteModel extends PluginModel implements ISatelliteModel {

	/** The listeners for the state of the view */
	ISatelliteMainView mListeners = (ISatelliteMainView) getListeners();
//	ISatelliteSandwichesView mSandwichesListeners = (ISatelliteSandwichesView) getListeners();
//	ISatelliteEventsView mEventsListeners = (ISatelliteEventsView) getListeners();

	private Beer mBeerOfMonth;
	private List<Beer> mBeers;
	private List<Sandwich> mSandwiches;
	private List<Event> mEvents;
	private Affluence mAffluence;

	/**
	 * Returns the interface of the linked view
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ISatelliteMainView.class;
	}

	/**
	 * Returns the beer of the month
	 */
	@Override
	public Beer getBeerOfMonth() {
		return mBeerOfMonth;
	}

	/**
	 * Sets the beer of the month and notify the view that the data has been
	 * updated
	 */
	@Override
	public void setBeerOfMonth(Beer beer) {
		if (beer != null) {
			mBeerOfMonth = beer;
			mListeners.beerUpdated();
		}
	}

	/**
	 * Returns the list of all beers Satellite proposes
	 */
	@Override
	public List<Beer> getAllBeers() {
		return mBeers;
	}

	/**
	 * Sets the list of beers and notify the view that the data has been updated
	 */
	@Override
	public void setAllBeers(List<Beer> list) {
		if (list != null && !list.isEmpty()) {
			mBeers = list;
			mListeners.beersUpdated();
		}
	}

	/**
	 * Returns the list of sandwiches Satellite proposes
	 */
	@Override
	public List<Sandwich> getSandwiches() {
		return mSandwiches;
	}

	/**
	 * Sets the list of sandwiches and notify the view that the data has been
	 * updated
	 */
	@Override
	public void setSandwiches(List<Sandwich> list) {
		if (list != null && !list.isEmpty()) {
			Log.d("SANDWICHES", "Got " + list.size()
					+ " sandwiches from the server");
			mSandwiches = list;
			mListeners.sandwichesUpdated();
		}
	}

	/**
	 * Returns the list of next events at Satellite
	 */
	@Override
	public List<Event> getEvents() {
		return mEvents;
	}

	/**
	 * Sets the list of event and notify the view that the data has been updated
	 */
	@Override
	public void setEvents(List<Event> list) {
		if (list != null && !list.isEmpty()) {
			mEvents = list;
			mListeners.eventsUpdated();
		}
	}

	/**
	 * Returns the current affluence at Satellite
	 */
	@Override
	public Affluence getAffluence() {
		return mAffluence;
	}

	/**
	 * Sets the affluence and notify the view that the data has been updated
	 */
	@Override
	public void setAffluence(Affluence affluence) {
		if (affluence != null) {
			mAffluence = affluence;
			mListeners.affluenceUpdated();
		}
	}

}

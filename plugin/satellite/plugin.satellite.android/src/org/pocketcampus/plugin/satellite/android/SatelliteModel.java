package org.pocketcampus.plugin.satellite.android;

import org.pocketcampus.platform.android.core.IView;
import org.pocketcampus.platform.android.core.PluginModel;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteMainView;
import org.pocketcampus.plugin.satellite.android.iface.ISatelliteModel;
import org.pocketcampus.plugin.satellite.shared.Affluence;
import org.pocketcampus.plugin.satellite.shared.Beer;

/**
 * The model of the Satellite plugin, handling the data of the plugin and the
 * notifications to the views when the data changes.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SatelliteModel extends PluginModel implements ISatelliteModel {
	/** The listeners for the state of the view. */
	ISatelliteMainView mListeners = (ISatelliteMainView) getListeners();
	/** The beer of the month at Satellite. */
	private Beer mBeerOfMonth;
	/** The current affluence at Satellite. */
	private Affluence mAffluence;

	/**
	 * Returns the interface of the linked view.
	 */
	@Override
	protected Class<? extends IView> getViewInterface() {
		return ISatelliteMainView.class;
	}

	/**
	 * Returns the beer of the month.
	 */
	@Override
	public Beer getBeerOfMonth() {
		return mBeerOfMonth;
	}

	/**
	 * Sets the beer of the month and notifies the view that the data has been
	 * updated.
	 */
	@Override
	public void setBeerOfMonth(Beer beer) {
		if (beer != null) {
			mBeerOfMonth = beer;
			mListeners.beerUpdated();
		}
	}

	/**
	 * Returns the current affluence at Satellite.
	 */
	@Override
	public Affluence getAffluence() {
		return mAffluence;
	}

	/**
	 * Sets the current affluence and notifies the view that the data has been
	 * updated.
	 */
	@Override
	public void setAffluence(Affluence affluence) {
		if (affluence != null) {
			mAffluence = affluence;
			mListeners.affluenceUpdated();
		}
	}
}

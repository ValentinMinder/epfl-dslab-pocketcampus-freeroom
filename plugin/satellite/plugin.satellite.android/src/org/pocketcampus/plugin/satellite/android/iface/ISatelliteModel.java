package org.pocketcampus.plugin.satellite.android.iface;

import org.pocketcampus.plugin.satellite.shared.Affluence;
import org.pocketcampus.plugin.satellite.shared.Beer;

/**
 * The interface that defines the public methods of the
 * <code>SatelliteModel</code>.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public interface ISatelliteModel {

	/**
	 * Returns the beer of the month at Satellite.
	 * 
	 * @return The <code>Beer</code> object.
	 */
	public Beer getBeerOfMonth();

	/**
	 * Sets the beer of the month.
	 * 
	 * @param beer
	 *            The beer of the month to be set.
	 */
	public void setBeerOfMonth(Beer beer);

	/**
	 * Returns the current affluence at Satellite.
	 * 
	 * @return The <code>Affluence</code> object.
	 */
	public Affluence getAffluence();

	/**
	 * Sets the affluence.
	 * 
	 * @param affluence
	 *            The affluence to be set.
	 */
	public void setAffluence(Affluence affluence);
}

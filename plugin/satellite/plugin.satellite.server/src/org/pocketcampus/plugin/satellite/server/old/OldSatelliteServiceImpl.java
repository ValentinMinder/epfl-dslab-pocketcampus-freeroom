package org.pocketcampus.plugin.satellite.server.old;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.satellite.shared.Affluence;
import org.pocketcampus.plugin.satellite.shared.Beer;

/**
 * Implementation of the Satellite server. Handles requests and sends
 * information to the Satellite plugin.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class OldSatelliteServiceImpl {
	/** The parser for the affluence. */
	private AffluenceParser mAffluenceParser;
	/** The parser for the beer of the month. */
	private BeerParser mBeerParser;

	/**
	 * Class constructor starting the Satellite server.
	 */
	public OldSatelliteServiceImpl() {
		System.out.println("Starting the Satellite server ...");
	}

	/**
	 * Parses the beer of the current month.
	 * 
	 * @return beer The beer of the month.
	 */
	public Beer getBeerOfTheMonth() throws TException {
		mBeerParser = new BeerParser();
		mBeerParser.parse();
		return mBeerParser.getBeerOfMonth();
	}

	/**
	 * Returns the current affluence at Satellite.
	 * 
	 * @return affluence The current affluence at Satellite
	 */
	public Affluence getAffluence() throws TException {
		mAffluenceParser = new AffluenceParser();
		Affluence affluence = mAffluenceParser.getAffluence();
		return affluence;
	}
}

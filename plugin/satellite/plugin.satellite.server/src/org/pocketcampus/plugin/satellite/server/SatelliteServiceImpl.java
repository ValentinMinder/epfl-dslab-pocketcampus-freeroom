package org.pocketcampus.plugin.satellite.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.pocketcampus.plugin.satellite.server.parse.AffluenceParser;
import org.pocketcampus.plugin.satellite.server.parse.BeerParser;
import org.pocketcampus.plugin.satellite.shared.Affluence;
import org.pocketcampus.plugin.satellite.shared.Beer;
import org.pocketcampus.plugin.satellite.shared.Event;
import org.pocketcampus.plugin.satellite.shared.Sandwich;
import org.pocketcampus.plugin.satellite.shared.SatelliteService;

/**
 * Implementation of the Satellite server. Handles requests and sends
 * information to the Satellite plugin.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SatelliteServiceImpl implements SatelliteService.Iface {
	/** The list of beers sold at Satellite (not used). */
	private List<Beer> mBeers;
	/** The list of sandwiches sold at Satellite (not used). */
	private List<Sandwich> mSandwiches;
	/** The list of next events at Satellite (not used). */
	private List<Event> mEvents;
	/** The parser for the affluence. */
	private AffluenceParser mAffluenceParser;
	/** The parser for the beer of the month. */
	private BeerParser mBeerParser;

	/**
	 * Class constructor starting the Satellite server.
	 */
	public SatelliteServiceImpl() {
		System.out.println("Starting the Satellite server ...");

		mBeers = new ArrayList<Beer>();
		mSandwiches = new ArrayList<Sandwich>();
		mEvents = new ArrayList<Event>();
	}

	/**
	 * Parses the beer of the current month.
	 * 
	 * @return beer The beer of the month.
	 */
	@Override
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
	@Override
	public Affluence getAffluence() throws TException {
		mAffluenceParser = new AffluenceParser();
		Affluence affluence = mAffluenceParser.getAffluence();
		return affluence;
	}

	/**
	 * Returns the list of beers.
	 * 
	 * Not used for now.
	 */
	@Override
	public List<Beer> getAllBeers() throws TException {
		return mBeers;
	}

	/**
	 * Returns the list of sandwiches.
	 * 
	 * Not used for now.
	 */
	@Override
	public List<Sandwich> getSatSandwiches() throws TException {
		return mSandwiches;
	}

	/**
	 * Returns the list of events.
	 * 
	 * Not used for now.
	 */
	@Override
	public List<Event> getNextEvents() throws TException {
		return mEvents;
	}
}

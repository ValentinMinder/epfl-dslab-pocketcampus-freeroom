package org.pocketcampus.plugin.satellite.server;

import java.util.ArrayList;
import java.util.Date;
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
 * Implementation of The Satellite server. Handles requests sending information
 * to the Satellite plugin.
 * 
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class SatelliteServiceImpl implements SatelliteService.Iface {
	/** The List of Beers sold at Satellite */
	private List<Beer> mBeers;
	/** The List of sandwiches sold at Satellite */
	private List<Sandwich> mSandwiches;
	/** The list of next events at Satellite */
	private List<Event> mEvents;

	/** Last import date of events */
	private Date mLastImportedEvents;
	/** Last import date of beers */
	private Date mLastImportedBeers;
	/** Last import date of the beer of the month */
	private Date mLastImportedBeer;
	
	/** The parser for the affluence */
	private AffluenceParser mAffluenceParser;
	/** The parser for the beer of the month */
	private BeerParser mBeerParser;

	/**
	 * The Constructor. When first started, the Satellite server gets the Beers
	 * and Sandwiches from the satellite website. (sadly there is no RSS feed).
	 */
	public SatelliteServiceImpl() {
		System.out.println("Starting Satellite plugin server ...");

		mBeers = new ArrayList<Beer>();
		mSandwiches = new ArrayList<Sandwich>();
		mEvents = new ArrayList<Event>();

		mLastImportedEvents = new Date();
		mLastImportedBeers = new Date();
		mLastImportedBeer = new Date();
	}

	/**
	 * Gets the Beer of the current month
	 * 
	 * @return beer The beer of the month
	 */
	@Override
	public Beer getBeerOfTheMonth() throws TException {
		mBeerParser = new BeerParser();
		mBeerParser.parse();
		return mBeerParser.getBeerOfMonth();
	}

	/**
	 * Gets the list of beers sold at Satellite
	 * 
	 * @return mBeers The list of beers
	 */
	@Override
	public List<Beer> getAllBeers() throws TException {
		return mBeers;
	}

	/**
	 * Gets the list of sandwiches sold at Satellite
	 * 
	 * @return mSandwiches The list of sandwiches
	 */
	@Override
	public List<Sandwich> getSatSandwiches() throws TException {
		if (mSandwiches == null || mSandwiches.isEmpty()) {
			importSatSandwiches();
			System.out
					.println("<getSandwiches>: Reimporting satellite sandwiches.");
		} else {
			System.out
					.println("<getSandwiches>: Not reimporting satellite sandwiches");
		}
		return mSandwiches;
	}

	/**
	 * Gets the current affluence at Satellite.
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
	 * Gets the list of next Events at Satellite
	 * 
	 * @return mEvents The list of events
	 */
	@Override
	public List<Event> getNextEvents() throws TException {
		return mEvents;
	}

	/**
	 * Import all Sandwiches at Satellite from a hardCoded list (there are no
	 * other ways except parsing the website, but since they almost never
	 * change, it's done that way).
	 */
	private void importSatSandwiches() {
		/* Satellite */
		mSandwiches.add(new Sandwich(("Jambon").hashCode(), "Jambon"));
		mSandwiches.add(new Sandwich(("Thon").hashCode(), "Thon"));
		mSandwiches.add(new Sandwich(("Jambon Fromage").hashCode(),
				"Jambon Fromage"));
		mSandwiches.add(new Sandwich(("Roast-Beef")
				.hashCode(), "Roast-Beef"));
		mSandwiches.add(new Sandwich(("Poulet au Curry")
				.hashCode(), "Poulet au Curry"));
		mSandwiches.add(new Sandwich(("Jambon Cru")
				.hashCode(), "Jambon Cru"));
		mSandwiches.add(new Sandwich(
				("Tomate Mozzarella").hashCode(), "Tomate Mozzarella"));
		mSandwiches.add(new Sandwich(("Salami")
				.hashCode(), "Salami"));
		mSandwiches.add(new Sandwich(("Parmesan")
				.hashCode(), "Parmesan"));
		mSandwiches.add(new Sandwich(("Aubergine grillée").hashCode(), "Aubergine grillée"));
		mSandwiches.add(new Sandwich(("Viande séchée")
				.hashCode(), "Viande séchée"));
	}
}

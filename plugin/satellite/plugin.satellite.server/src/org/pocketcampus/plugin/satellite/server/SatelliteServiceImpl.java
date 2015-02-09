package org.pocketcampus.plugin.satellite.server;

import org.apache.thrift.TException;
import org.joda.time.Duration;
import org.pocketcampus.platform.server.CachingProxy;
import org.pocketcampus.platform.server.HttpClientImpl;
import org.pocketcampus.plugin.satellite.shared.Affluence;
import org.pocketcampus.plugin.satellite.shared.Beer;
import org.pocketcampus.plugin.satellite.shared.BeersResponse;
import org.pocketcampus.plugin.satellite.shared.SatelliteService;

/**
 * Implementation of SatelliteService.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class SatelliteServiceImpl implements SatelliteService.Iface {
	private static final Duration MENU_CACHE_DURATION = Duration.standardDays(1);

	private final BeerMenu _beerMenu;

	public SatelliteServiceImpl(BeerMenu beerMenu) {
		_beerMenu = beerMenu;
	}

	public SatelliteServiceImpl() {
		this(CachingProxy.create(new BeerMenuImpl(new HttpClientImpl()), MENU_CACHE_DURATION, false));
	}

	@Override
	public BeersResponse getBeers() throws TException {
		return _beerMenu.get();
	}

	// OLD STUFF - DO NOT TOUCH
	private org.pocketcampus.plugin.satellite.server.old.OldSatelliteServiceImpl oldService = new org.pocketcampus.plugin.satellite.server.old.OldSatelliteServiceImpl();

	@Override
	public Beer getBeerOfTheMonth() throws TException {
		return oldService.getBeerOfTheMonth();
	}

	@Override
	public Affluence getAffluence() throws TException {
		return oldService.getAffluence();
	}
}

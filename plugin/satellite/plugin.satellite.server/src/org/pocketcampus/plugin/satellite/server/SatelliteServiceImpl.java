package org.pocketcampus.plugin.satellite.server;

import java.util.List;

import org.apache.thrift.TException;

import org.pocketcampus.platform.sdk.shared.CachingProxy;
import org.pocketcampus.platform.sdk.shared.HttpClientImpl;
import org.pocketcampus.plugin.satellite.shared.*;

import org.joda.time.Duration;

public final class SatelliteServiceImpl implements SatelliteService.Iface {
	private final BeerList _beers;
	private final AffluenceGetter _affluenceGetter;

	public SatelliteServiceImpl(BeerList beers, AffluenceGetter affluenceGetter) {
		_beers = beers;
		_affluenceGetter = affluenceGetter;
	}

	public SatelliteServiceImpl() {
		this(CachingProxy.create(new BeerListImpl(new HttpClientImpl()), Duration.standardDays(1)), 
			 CachingProxy.create(new AffluenceGetterImpl(new HttpClientImpl()), Duration.standardMinutes(1)));
	}

	@Override
	public BeersResponse getBeers() throws TException {
		List<SatelliteBeer> beers;
		try {
			beers = _beers.get();
		} catch (Exception e) {
			throw new TException("Something went wrong during the parsing of beers");
		}
		return new BeersResponse(beers);
	}

	@Override
	public AffluenceResponse getCurrentAffluence() throws TException {
		SatelliteAffluence affluence = _affluenceGetter.get();
		return new AffluenceResponse(affluence);
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
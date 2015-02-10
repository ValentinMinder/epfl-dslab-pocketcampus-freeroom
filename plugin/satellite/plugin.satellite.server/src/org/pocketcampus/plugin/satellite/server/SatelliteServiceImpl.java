package org.pocketcampus.plugin.satellite.server;

import org.apache.thrift.TException;
import org.joda.time.Duration;
import org.pocketcampus.platform.server.CachingProxy;
import org.pocketcampus.platform.server.HttpClientImpl;
import org.pocketcampus.plugin.satellite.shared.*;

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


    @Override
    @Deprecated
    public Beer getBeerOfTheMonth() throws TException {
        // This method only returns the first beer of the month.
        // It's wrong, but that's what the old code did.
        for (final SatelliteMenuPart menuPart : _beerMenu.get().getBeerList().values()) {
            for (final SatelliteBeer beer : menuPart.getBeersOfTheMonth()) {
                return new Beer(0, beer.getName(), beer.getDescription())
                        .setPrice(beer.getPrice());
            }
        }

        return null;
    }

    @Override
    @Deprecated
    public Affluence getAffluence() throws TException {
        // Satellite does not support affluence indicators any more.
        return Affluence.ERROR;
    }
}

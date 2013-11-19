package org.pocketcampus.plugin.satellite.server.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Scanner;

import org.pocketcampus.platform.sdk.shared.HttpClient;
import org.pocketcampus.plugin.satellite.server.BeerListImpl;
import org.pocketcampus.plugin.satellite.shared.*;

/**
 * Tests for BeerListImpl.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class BeerListTests {
	@Test
	public void beer() {
		SatelliteBeer beer = getBeers().get(0);

		assertEquals(false, beer.isBeerOfTheMonth());
		assertEquals(SatelliteBeerContainer.SMALL_BOTTLE, beer.getContainer());
		assertEquals("St-Feuillien Brune", beer.getName());
		assertEquals("St-Feuillien", beer.getBreweryName());
		assertEquals("Brune", beer.getBeerType());
		assertEquals("Belgique", beer.getOriginCountry());
		assertEquals(7.5, beer.getAlcoholRate(), Double.MIN_VALUE);
		assertEquals(6, beer.getPrice(), Double.MIN_VALUE);
		assertEquals(
				"Bière naturelle de fermentation haute, brassée avec des malts et des houblons de premier choix. Les notes fruitées se marient harmonieusement avec la dominante réglisse et le caramel. Son arôme est riche et son goût savoureux.",
				beer.getDescription());
	}

	@Test
	public void beerOfTheMonth() {
		SatelliteBeer beer = getBeers().get(62);

		assertEquals(true, beer.isBeerOfTheMonth());
		assertEquals(SatelliteBeerContainer.DRAFT, beer.getContainer());
	}

	@Test
	public void largeBottle() {
		SatelliteBeer beer = getBeers().get(59);

		assertEquals(false, beer.isBeerOfTheMonth());
		assertEquals(SatelliteBeerContainer.LARGE_BOTTLE, beer.getContainer());
	}

	@Test
	public void htmlEntitiesInDescription() {
		SatelliteBeer beer = getBeers().get(31);

		assertEquals(
				"Bière de fermentation haute, refermentée en bouteille, non filtrée.\r\n\r\nLe subtil mariage de ses trois malts lui donne un corps complexe et raffiné. En finale, l'héritage fruité de ses levures d'abbaye se prolonge par une fraîche astringence.",
				beer.getDescription());
	}

	@Test
	public void nonIntegerPrice() {
		SatelliteBeer beer = getBeers().get(7);

		assertEquals(4.5, beer.getPrice(), Double.MIN_VALUE);
	}

	private static List<SatelliteBeer> getBeers() {
		try {
			return new BeerListImpl(new TestHttpClient()).get();
		} catch (Exception e) {
			fail("An exception occurred.");
			return null;
		}
	}

	private static final class TestHttpClient implements HttpClient {
		private static final String RETURN_VALUE = getFileContents("ExampleBeerFeed.xml");

		@Override
		public String getString(String url, Charset charset) throws Exception {
			return RETURN_VALUE;
		}

		@SuppressWarnings("resource")
		private static String getFileContents(String name) {
			Scanner s = null;

			try {
				InputStream stream = new BeerListTests().getClass().getResourceAsStream(name);
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

				// smart trick from http://stackoverflow.com/a/5445161
				s = new Scanner(reader).useDelimiter("\\A");
				return s.hasNext() ? s.next() : "";
			} finally {
				if (s != null) {
					s.close();
				}
			}
		}
	}
}
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
				"Bi�re naturelle de fermentation haute, brass�e avec des malts et des houblons de premier choix. Les notes fruit�es se marient harmonieusement avec la dominante r�glisse et le caramel. Son ar�me est riche et son go�t savoureux.",
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
				"Bi�re de fermentation haute, referment�e en bouteille, non filtr�e.\r\n\r\nLe subtil mariage de ses trois malts lui donne un corps complexe et raffin�. En finale, l'h�ritage fruit� de ses levures d'abbaye se prolonge par une fra�che astringence.",
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
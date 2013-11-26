package org.pocketcampus.plugin.satellite.server.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.pocketcampus.platform.sdk.shared.HttpClient;
import org.pocketcampus.plugin.satellite.server.BeerMenuImpl;
import org.pocketcampus.plugin.satellite.shared.*;

/**
 * Tests for BeerMenuImpl.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class BeerMenuTests {
	@Test
	public void draftBeersOfTheMonth() {
		List<SatelliteBeer> beers = getBeers().get(SatelliteBeerContainer.DRAFT).getBeersOfTheMonth();

		assertEquals(1, beers.size());
		
		SatelliteBeer beer = beers.get(0);
		
		assertEquals("St-Feuillien Saison", beer.getName());
		assertEquals("St-Feuillien", beer.getBreweryName());
		assertEquals("Belgique", beer.getOriginCountry());
		assertEquals(6.5, beer.getAlcoholRate(), Double.MIN_VALUE);
		assertEquals(4, beer.getPrice(), Double.MIN_VALUE);
		assertEquals("Bi�re de terroir par excellence, la Saison trouve son origine dans les fermes-brasseries du Sud de la Belgique, et principalement en Hainaut. A l��poque, cette bi�re peu alcoolis�e et rafra�chissante �tanchait la soif des ouvriers saisonniers.\r\nA la Brasserie St-Feuillien, la Saison est une bi�re de fermentation haute, referment�e en bouteille, non filtr�e, d�un chaleureux blond dor�. Son profil aromatique est tout en nuances et son amertume bien marqu�e, l�ensemble soutenu par une belle pl�nitude en bouche. Un grand classique.", beer.getDescription());
	}
	
	@Test
	public void bottledBeer(){
		SatelliteBeer beer = getBeers().get(SatelliteBeerContainer.BOTTLE).getBeers().get("Vieille Brune").get(0);
		
		assertEquals("Duchesse de Bourgogne", beer.getName());
		assertEquals("Verhaeghe", beer.getBreweryName());
		assertEquals("Belgique", beer.getOriginCountry());
		assertEquals(6.2, beer.getAlcoholRate(), Double.MIN_VALUE);
		assertEquals(4, beer.getPrice(), Double.MIN_VALUE);
		assertEquals("Bi�re de haute fermentation, m�rie en f�t de ch�ne. Ceci lui conf�re un go�t entre lambic et cidre, fruit�e et acide � la fois.", beer.getDescription());
	}
	
	@Test
	public void beerWithNoCapsInType(){
		SatelliteBeer beer = getBeers().get(SatelliteBeerContainer.BOTTLE).getBeers().get("Brune").get(1);

		assertEquals("Gulden Draak", beer.getName());
	}

	private static Map<SatelliteBeerContainer, SatelliteMenuPart> getBeers() {
		try {
			return new BeerMenuImpl(new TestHttpClient()).get().getBeerList();
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
				InputStream stream = new BeerMenuTests().getClass().getResourceAsStream(name);
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
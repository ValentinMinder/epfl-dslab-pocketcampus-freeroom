package org.pocketcampus.plugin.food.server.tests;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.pocketcampus.platform.server.HttpClient;
import org.pocketcampus.plugin.food.server.MenuImpl;
import org.pocketcampus.plugin.food.shared.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Tests for MenuImpl.
 * 
 * N.B.: The test file was retrieved from the JSON API on 20/02/2014, and has been modified to include a meal with price 0.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class MenuTests {
	// Simple meal
	@Test
	public void simpleMeal() {
		List<EpflRestaurant> menu = getMenu();
		EpflRestaurant r = menu.get(0);
		EpflMeal m = r.getRMeals().get(0);

		assertEquals("La Table de Vallotton", r.getRName());
		
		assertEquals("Velouté de panais, écume ibérique «Lomo pata Négra»(ES)", m.getMName());
		assertEquals("Et croustine au romarin", m.getMDescription());
		assertFalse(m.isSetMHalfPortionPrice());
		assertEquals(1, m.getMPrices().size());
		assertTrue(m.getMPrices().containsKey(PriceTarget.ALL));
		assertEquals(16.00, m.getMPrices().get(PriceTarget.ALL), Double.MIN_VALUE);
		assertEquals(1, m.getMTypes().size());
		assertEquals(MealType.GREEN_FORK, m.getMTypes().get(0));
	}

	// Meal without description
	@Test
	public void mealWithoutDescription() {
		List<EpflRestaurant> menu = getMenu();
		EpflMeal m = menu.get(6).getRMeals().get(7);

		assertEquals("Crème de légume d'Hiver", m.getMName());
		assertEquals("", m.getMDescription());
	}

	// Meal with prices for all targets
	@Test
	public void mealWithManyPriceTargets() {
		List<EpflRestaurant> menu = getMenu();
		EpflMeal m = menu.get(1).getRMeals().get(0);
		Map<PriceTarget, Double> prices = m.getMPrices();

		assertEquals("Saltimbocca de poulet (CH) à la sauge", m.getMName());
		assertEquals(4, prices.size());
		assertFalse(prices.containsKey(PriceTarget.ALL));
		assertFalse(m.isSetMHalfPortionPrice());
		assertEquals(9.00, prices.get(PriceTarget.STUDENT), Double.MIN_VALUE);
		assertEquals(10.00, prices.get(PriceTarget.PHD_STUDENT), Double.MIN_VALUE);
		assertEquals(11.00, prices.get(PriceTarget.STAFF), Double.MIN_VALUE);
		assertEquals(12.00, prices.get(PriceTarget.VISITOR), Double.MIN_VALUE);
	}

	// Meal with half-portion available
	@Test
	public void mealWithHalfPortionPrice() {
		List<EpflRestaurant> menu = getMenu();
		EpflMeal m = menu.get(9).getRMeals().get(0);
		Map<PriceTarget, Double> prices = m.getMPrices();

		assertEquals("Pâtes maison avec poulet (HU)", m.getMName());
		assertEquals(1, prices.size());
		assertTrue(prices.containsKey(PriceTarget.ALL));
		assertEquals(12.90, prices.get(PriceTarget.ALL), Double.MIN_VALUE);
		assertTrue(m.isSetMHalfPortionPrice());
		assertEquals(9.90, m.getMHalfPortionPrice(), Double.MIN_VALUE);
	}

	// 'P' price target == ALL
	@Test
	public void mealWithSpecialPriceTarget() {
		List<EpflRestaurant> meals = getMenu();
		EpflMeal m = meals.get(6).getRMeals().get(5);
		Map<PriceTarget, Double> prices = m.getMPrices();

		assertEquals("Côtes d'agneau IR à la plancha", m.getMName());
		assertEquals(1, prices.size());
		assertTrue(prices.containsKey(PriceTarget.ALL));
		assertEquals(18.50, prices.get(PriceTarget.ALL), Double.MIN_VALUE);
	}
	
	// Price to 0 => no price
	@Test
	public void mealWithZeroPrice() {
		List<EpflRestaurant> meals = getMenu();
		EpflMeal m = meals.get(0).getRMeals().get(1);
		Map<PriceTarget, Double> prices = m.getMPrices();
		
		assertEquals("Café gourmand, pâte de fruits", m.getMName());
		assertEquals(0, prices.size());
	}

	// Maharaja dishes are not thai, even if the list says so
	@Test
	public void mealsFromMaharajaAreNeverThai() {
		List<EpflRestaurant> menu = getMenu();

		for (EpflRestaurant restaurant : menu) {
			if (restaurant.getRName().equals("Maharaja")) {
				for (EpflMeal meal : restaurant.getRMeals()) {
					assertTrue(meal.getMTypes().contains(MealType.INDIAN));
					assertFalse(meal.getMTypes().contains(MealType.THAI));
				}
			}
		}
	}
	
	// No "Entrée : " prefix on descriptions
	@Test
	public void mealWithAppetizerInDescription() {
		List<EpflRestaurant> meals = getMenu();
		EpflMeal m = meals.get(6).getRMeals().get(1);
		
		assertEquals("Burger de poisson au coulis de homard", m.getMName());
		assertEquals("Mélange de graines étuvées\nVelouté de légumes\nPetite saladine", m.getMDescription());
	}

	private static List<EpflRestaurant> getMenu() {
		try {
			return new MenuImpl(new TestHttpClient()).get(MealTime.LUNCH, LocalDate.now()).getMenu();
		} catch (Exception e) {
			e.printStackTrace();
			fail("An exception occured.");
			return null;
		}
	}

	private static final class TestHttpClient implements HttpClient {
		private static final String RETURN_VALUE = getFileContents("ExampleMenuList.json");

		@Override
		public String get(String url, Charset charset) throws IOException {
			return RETURN_VALUE;
		}
		
		@Override
		public String post(String url, byte[] body, Charset charset) throws IOException {
			throw new RuntimeException("post(String, byte[], Charset) should not be called.");
		}

		@SuppressWarnings("resource")
		private static String getFileContents(String name) {
			Scanner s = null;

			try {
				InputStream stream = new TestHttpClient().getClass().getResourceAsStream(name);
				s = new Scanner(stream, "UTF-8").useDelimiter("\\A");
				return s.hasNext() ? s.next() : "";
			} finally {
				if (s != null) {
					s.close();
				}
			}
		}
	}
}
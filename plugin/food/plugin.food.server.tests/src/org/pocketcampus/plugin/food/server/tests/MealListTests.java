package org.pocketcampus.plugin.food.server.tests;

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
import org.pocketcampus.plugin.food.server.*;
import org.pocketcampus.plugin.food.shared.*;

import org.joda.time.LocalDate;

/**
 * Tests for MealListImpl
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class MealListTests {
	// Simple meal
	@Test
	public void simpleMeal() {
		List<EpflRestaurant> menu = getMenu();
		EpflRestaurant r = menu.get(0);
		EpflMeal m = r.getRMeals().get(0);

		assertEquals("Le Copernic", r.getRName());
		
		assertEquals("Pavé de saumon mariné à la fleur de sel de guérande", m.getMName());
		assertEquals("Légumes de saison\nBol de riz", m.getMDescription());
		assertFalse(m.isSetMHalfPortionPrice());
		assertEquals(1, m.getMPrices().size());
		assertTrue(m.getMPrices().containsKey(PriceTarget.ALL));
		assertEquals(26.00, m.getMPrices().get(PriceTarget.ALL), Double.MIN_VALUE);
		assertEquals(1, m.getMTypes().size());
		assertEquals(MealType.FISH, m.getMTypes().get(0));
	}

	// Meal without description
	@Test
	public void mealWithoutDescription() {
		List<EpflRestaurant> menu = getMenu();
		EpflMeal m = menu.get(4).getRMeals().get(1);

		assertEquals("", m.getMDescription());
	}

	// Meal with prices for all targets
	@Test
	public void mealWithManyPriceTargets() {
		List<EpflRestaurant> menu = getMenu();
		EpflMeal m = menu.get(1).getRMeals().get(0);
		Map<PriceTarget, Double> prices = m.getMPrices();

		assertEquals(4, prices.size());
		assertFalse(prices.containsKey(PriceTarget.ALL));
		assertFalse(m.isSetMHalfPortionPrice());
		assertEquals(7.65, prices.get(PriceTarget.STUDENT), Double.MIN_VALUE);
		assertEquals(7.65, prices.get(PriceTarget.PHD_STUDENT), Double.MIN_VALUE);
		assertEquals(9.00, prices.get(PriceTarget.STAFF), Double.MIN_VALUE);
		assertEquals(9.00, prices.get(PriceTarget.VISITOR), Double.MIN_VALUE);
	}

	// Meal with half-portion available
	@Test
	public void mealWithHalfPortionPrice() {
		List<EpflRestaurant> menu = getMenu();
		EpflMeal m = menu.get(2).getRMeals().get(0);
		Map<PriceTarget, Double> prices = m.getMPrices();

		assertEquals(1, prices.size());
		assertTrue(prices.containsKey(PriceTarget.ALL));
		assertEquals(14.50, prices.get(PriceTarget.ALL), Double.MIN_VALUE);
		assertTrue(m.isSetMHalfPortionPrice());
		assertEquals(10.50, m.getMHalfPortionPrice(), Double.MIN_VALUE);
	}

	// 'P' price target == ALL
	@Test
	public void mealWithSpecialPriceTarget() {
		List<EpflRestaurant> meals = getMenu();
		EpflMeal m = meals.get(0).getRMeals().get(1);
		Map<PriceTarget, Double> prices = m.getMPrices();

		assertEquals(1, prices.size());
		assertTrue(prices.containsKey(PriceTarget.ALL));
		assertEquals(18.00, prices.get(PriceTarget.ALL), Double.MIN_VALUE);
	}

	// Meals from 'Le Vinci' shouldn't be parsed
	@Test
	public void mealsFromLeVinciAreIgnored() {
		List<EpflRestaurant> menu = getMenu();

		for (EpflRestaurant restaurant : menu) {
			if (restaurant.getRName().equals("Le Vinci")) {
				fail("There should be no meals from 'Le Vinci' as it's a duplicate of 'Le Parmentier' (same menus).");
			}
		}
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

	private static List<EpflRestaurant> getMenu() {
		try {
			return new MealListImpl(new TestHttpClient()).getMenu(MealTime.LUNCH, LocalDate.now());
		} catch (Exception e) {
			e.printStackTrace();
			fail("An exception occured.");
			return null;
		}
	}

	private static final class TestHttpClient implements HttpClient {
		private static final String RETURN_VALUE = getFileContents("ExampleMenuList.html");

		@Override
		public String getString(String url, Charset charset) throws Exception {
			return RETURN_VALUE;
		}

		@SuppressWarnings("resource")
		private static String getFileContents(String name) {
			Scanner s = null;

			try {
				InputStream stream = new TestHttpClient().getClass().getResourceAsStream(name);
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
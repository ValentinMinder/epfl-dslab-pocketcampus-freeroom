package org.pocketcampus.plugin.food.server.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.pocketcampus.plugin.food.shared.*;
import org.pocketcampus.plugin.food.server.*;
import org.pocketcampus.plugin.food.server.MealList.MenuResult;

import org.joda.time.*;

/** 
 * Tests for MealListCache.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class MealListCacheTests {
	// Getting the meal list for the first time works
	@Test
	public void getMenuForFirstTimeWorks() throws Exception {
		MealTime time = MealTime.LUNCH;
		LocalDate date = LocalDate.now();
		MenuResult expected = new MenuResult(true, new ArrayList<EpflRestaurant>());
		TestMealList mealList = new TestMealList();
		mealList.results.get(time).put(date, new TestMealList.Result(expected));
		MealListCache cache = new MealListCache(mealList);

		MenuResult actual = cache.getMenu(time, date);

		assertEquals(true, actual.hasChanged);
		assertEquals(expected.menu, actual.menu);
		assertEquals(1, mealList.results.get(time).get(date).hitCount);
	}

	// Getting the meal list for the second time sets hasChanged to false
	@Test
	public void getMenuHasNotChangedAfterFirstTime() throws Exception {
		MealTime time = MealTime.LUNCH;
		LocalDate date = LocalDate.now();
		MenuResult expected = new MenuResult(true, new ArrayList<EpflRestaurant>());
		TestMealList mealList = new TestMealList();
		mealList.results.get(time).put(date, new TestMealList.Result(expected));
		MealListCache cache = new MealListCache(mealList);

		cache.getMenu(time, date);
		MenuResult actual = cache.getMenu(time, date);

		assertEquals(false, actual.hasChanged);
	}

	// Getting the meal list for the second time caches it
	@Test
	public void getMenuIsCached() throws Exception {
		MealTime time = MealTime.LUNCH;
		LocalDate date = LocalDate.now();
		MenuResult result = new MenuResult(true, new ArrayList<EpflRestaurant>());
		TestMealList mealList = new TestMealList();
		mealList.results.get(time).put(date, new TestMealList.Result(result));
		MealListCache cache = new MealListCache(mealList);

		cache.getMenu(time, date);
		cache.getMenu(time, date);

		assertEquals(1, mealList.results.get(time).get(date).hitCount);
	}

	// Getting the menu for different times works
	@Test
	public void getMenuWorksForDifferentTimes() throws Exception {
		MealTime time1 = MealTime.LUNCH;
		MealTime time2 = MealTime.DINNER;
		LocalDate date = LocalDate.now();
		MenuResult expected1 = new MenuResult(true, new ArrayList<EpflRestaurant>());
		MenuResult expected2 = new MenuResult(true, new ArrayList<EpflRestaurant>());
		TestMealList mealList = new TestMealList();
		mealList.results.get(time1).put(date, new TestMealList.Result(expected1));
		mealList.results.get(time2).put(date, new TestMealList.Result(expected2));
		MealListCache cache = new MealListCache(mealList);

		cache.getMenu(time1, date);
		MenuResult actual2 = cache.getMenu(time2, date);

		assertEquals(expected2.menu, actual2.menu);
		assertEquals(1, mealList.results.get(time1).get(date).hitCount);
		assertEquals(1, mealList.results.get(time2).get(date).hitCount);
	}

	// Getting the menu for different dates works
	@Test
	public void getMenuWorksForDifferentDates() throws Exception {
		MealTime time = MealTime.LUNCH;
		LocalDate date1 = LocalDate.now();
		LocalDate date2 = new LocalDate(0);
		MenuResult expected1 = new MenuResult(true, new ArrayList<EpflRestaurant>());
		MenuResult expected2 = new MenuResult(true, new ArrayList<EpflRestaurant>());
		TestMealList mealList = new TestMealList();
		mealList.results.get(time).put(date1, new TestMealList.Result(expected1));
		mealList.results.get(time).put(date2, new TestMealList.Result(expected2));
		MealListCache cache = new MealListCache(mealList);

		cache.getMenu(time, date1);
		MenuResult actual2 = cache.getMenu(time, date2);

		assertEquals(expected2.menu, actual2.menu);
		assertEquals(1, mealList.results.get(time).get(date1).hitCount);
		assertEquals(1, mealList.results.get(time).get(date2).hitCount);
	}
	
	// The menu hasn't changed before an hour
	@Test
	public void getMenuHasNotChangedBefore1Hour() throws Exception {
		MealTime time = MealTime.LUNCH;
		LocalDate date = LocalDate.now();
		MenuResult expected = new MenuResult(true, new ArrayList<EpflRestaurant>());
		TestMealList mealList = new TestMealList();
		mealList.results.get(time).put(date, new TestMealList.Result(expected));
		MealListCache cache = new MealListCache(mealList);

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 30, 12, 00).getMillis());
		cache.getMenu(time, date);
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 30, 12, 59).getMillis());
		MenuResult actual = cache.getMenu(time, date);

		assertEquals(false, actual.hasChanged);
	}

	// The menu is refreshed after an hour
	@Test
	public void getMenuIsRefreshedAfter1Hour() throws Exception {
		MealTime time = MealTime.LUNCH;
		LocalDate date = LocalDate.now();
		MenuResult expected = new MenuResult(true, new ArrayList<EpflRestaurant>());
		TestMealList mealList = new TestMealList();
		mealList.results.get(time).put(date, new TestMealList.Result(expected));
		MealListCache cache = new MealListCache(mealList);

		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 30, 12, 00).getMillis());
		cache.getMenu(time, date);
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 30, 13, 00).getMillis());
		MenuResult actual = cache.getMenu(time, date);

		assertEquals(true, actual.hasChanged);
		assertEquals(2, mealList.results.get(time).get(date).hitCount);
	}

	private static class TestMealList implements MealList
	{
		public Map<MealTime, Map<LocalDate, Result>> results;

		public TestMealList()
		{
			results = new HashMap<MealTime, Map<LocalDate, Result>>();

			for (MealTime t : MealTime.values()) {
				results.put(t, new HashMap<LocalDate, Result>());
			}
		}

		@Override
		public MenuResult getMenu(MealTime time, LocalDate date) throws Exception {
			if (!results.get(time).containsKey(date)) {
				throw new Exception("The test is wrong. Please insert a result before using TestMealList.");
			}

			Result result = results.get(time).get(date);
			result.hitCount++;
			return result.result;
		}

		public static class Result
		{
			public int hitCount;
			public MenuResult result;

			public Result(MenuResult result)
			{
				this.hitCount = 0;
				this.result = result;
			}
		}
	}
}
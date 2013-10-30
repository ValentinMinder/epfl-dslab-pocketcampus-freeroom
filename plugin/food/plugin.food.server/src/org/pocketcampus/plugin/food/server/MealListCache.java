package org.pocketcampus.plugin.food.server;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.WeakHashMap;

import org.pocketcampus.plugin.food.shared.*;

import org.joda.time.*;

/**
 * Caches a MealList for an hour.
 * The list for days other than the current one is not cached.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public class MealListCache implements MealList {
	private static final Hours CACHE_DURATION = Hours.ONE;

	private final MealList _wrapped;
	// using a WeakHashMap is important to avoid out-of-memory exceptions
	private final Map<MealTime, WeakHashMap<LocalDate, CachedResult>> _cache;

	public MealListCache(MealList wrapped) {
		_wrapped = wrapped;

		_cache = new HashMap<MealTime, WeakHashMap<LocalDate, CachedResult>>();

		for (MealTime time : MealTime.values()) {
			_cache.put(time, new WeakHashMap<LocalDate, CachedResult>());
		}
	}

	@Override
	public MenuResult getMenu(MealTime time, LocalDate date) throws Exception {
		// don't use containsKey then get, it might cause a race condition
		// since the GC can kick in at any time and remove stuff from the WeakHashMap
		CachedResult cached = _cache.get(time).get(date);
		if (cached != null) {
			if (isUpToDate(cached.date)) {
				return new MenuResult(false, cached.menu);
			}
		}
		
		MenuResult result = _wrapped.getMenu(time, date);
		_cache.get(time).put(date, new CachedResult(DateTime.now(), result.menu));
		return result;
	}

	private static boolean isUpToDate(DateTime date) {
		DateTime now = new DateTime();
		return Days.daysBetween(now, date).getDays() == 0
				&& Hours.hoursBetween(date, now).isLessThan(CACHE_DURATION);
	}

	private static class CachedResult
	{
		public final DateTime date;
		public final List<EpflRestaurant> menu;

		public CachedResult(DateTime date, List<EpflRestaurant> menu) {
			this.date = date;
			this.menu = menu;
		}
	}
}
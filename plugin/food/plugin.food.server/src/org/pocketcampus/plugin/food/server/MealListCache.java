package org.pocketcampus.plugin.food.server;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.pocketcampus.plugin.food.shared.*;

/**
 * Caches a MealList for an hour.
 * The list for days other than the current one is not cached.
 * TODO: If non-current days get requested a lot, maybe we should cache them? 
 * (we'd need a weak map or something to avoid excess memory usage)
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public class MealListCache implements MealList {
	private static final int CACHE_DURATION = 1; // in hours

	private final MealList _wrapped;
	private Date _lastFetchDate;
	private Map<MealTime, List<EpflRestaurant>> _lastMenus;

	public MealListCache(MealList wrapped) {
		_wrapped = wrapped;
		_lastFetchDate = new Date(0); // min value
	}

	@Override
	public MenuResult getMenu(MealTime time, Date date) throws Exception {
		if (!areSameDay(date, new Date())) {
			return _wrapped.getMenu(time, date);
		}

		if (!isUpToDate(_lastFetchDate)) {
			for (MealTime t : MealTime.values()) {
				MenuResult result = _wrapped.getMenu(t, date);
				_lastMenus.put(t, result.menu);
			}
			_lastFetchDate = new Date();
		}

		return new MenuResult(false, _lastMenus.get(time));
	}

	private static boolean isUpToDate(Date date) {
		Date now = new Date();
		return areSameDay(date, now) && getHoursBetween(date, new Date()) < CACHE_DURATION;
	}

	private static boolean areSameDay(Date d1, Date d2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);

		return c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
	}

	private static int getHoursBetween(Date d1, Date d2) {
		return (int) ((d2.getTime() - d1.getTime()) / (60 * 60 * 1000));
	}
}
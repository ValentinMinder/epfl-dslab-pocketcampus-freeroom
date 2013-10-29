package org.pocketcampus.plugin.food.server;

import java.util.Date;
import java.util.List;

import org.pocketcampus.plugin.food.shared.*;

public interface MealList {
	MenuResult getMenu(MealTime time, Date date) throws Exception;

	public static class MenuResult {
		public final boolean hasChanged;
		public final List<EpflRestaurant> menu;

		public MenuResult(boolean hasChanged, List<EpflRestaurant> menu) {
			this.hasChanged = hasChanged;
			this.menu = menu;
		}
	}
}
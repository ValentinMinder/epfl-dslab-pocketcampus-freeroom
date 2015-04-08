package org.pocketcampus.plugin.food.server;

import java.sql.SQLException;

import org.joda.time.LocalDate;
import org.pocketcampus.plugin.food.shared.FoodResponse;
import org.pocketcampus.plugin.food.shared.MealTime;

public final class DatabaseInsertingMenu implements Menu {
	private final Menu menu;
	private final RatingDatabase database;

	public DatabaseInsertingMenu(final Menu menu, final RatingDatabase database) {
		this.menu = menu;
		this.database = database;
	}

	@Override
	public FoodResponse get(MealTime time, LocalDate date) {
		FoodResponse response = menu.get(time, date);
		try {
			database.insertMenu(response.getMenu(), date, time);
		} catch (SQLException e) {
			// HACK: This is bad
			throw new RuntimeException("Couldn't insert meals in the DB.", e);
		}
		return response;
	}

}

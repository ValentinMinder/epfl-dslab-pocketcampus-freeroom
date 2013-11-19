package org.pocketcampus.plugin.food.server;

import java.util.List;

import org.pocketcampus.plugin.food.shared.*;

import org.joda.time.LocalDate;

public interface MealList {
	List<EpflRestaurant> getMenu(MealTime time, LocalDate date) throws Exception;
}
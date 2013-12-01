package org.pocketcampus.plugin.food.server;

import org.pocketcampus.plugin.food.shared.*;

import org.joda.time.LocalDate;

/**
 * Fetches the EPFL menu.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public interface Menu {
	FoodResponse get(MealTime time, LocalDate date) throws Exception;
}
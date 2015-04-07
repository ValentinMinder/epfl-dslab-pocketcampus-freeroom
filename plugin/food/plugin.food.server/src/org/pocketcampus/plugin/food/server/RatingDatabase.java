package org.pocketcampus.plugin.food.server;

import org.joda.time.LocalDate;
import org.pocketcampus.plugin.food.shared.EpflRestaurant;
import org.pocketcampus.plugin.food.shared.MealTime;
import org.pocketcampus.plugin.food.shared.SubmitStatus;

import java.sql.SQLException;
import java.util.List;

/**
 * Ratings database.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public interface RatingDatabase {
	/** Inserts the specified menu from the specified date/time into the database */
	void insertMenu(List<EpflRestaurant> menu, LocalDate date, MealTime time) throws SQLException;
	
	/** Votes on the specified meal with the specified ID. 
	 *  Returns true if the deviceID hadn't voted before (for the meal's date/time). */
	SubmitStatus vote(String deviceId, long mealId, double rating) throws SQLException;
	
	/** Sets the ratings of the specified menu from the database. */
	void setRatings(List<EpflRestaurant> menu, LocalDate date, MealTime time) throws SQLException;
}
package org.pocketcampus.plugin.food.shared;


public class SharedFoodUtils {
	/**
	 * Computes a Meal's hashCode
	 */
	public static int getMealHashCode(Meal m) {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((m.getName() == null) ? 0 : m.getName().hashCode());
		result = prime
				* result
				+ ((m.getMealDescription() == null) ? 0 : m
						.getMealDescription().hashCode());
		result = prime
				* result
				+ ((m.getRestaurant() == null) ? 0 : m.getRestaurant()
						.getName().hashCode());
		return result;
	}
}

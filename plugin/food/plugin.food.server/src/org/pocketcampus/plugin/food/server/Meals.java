package org.pocketcampus.plugin.food.server;

import org.joda.time.LocalDate;
import org.pocketcampus.plugin.food.shared.EpflMeal;
import org.pocketcampus.plugin.food.shared.MealTime;

import java.text.Normalizer;

/**
 * Utility methods for {@link EpflMeal}.
 *
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class Meals {
    private Meals() {
        // Prevent instantiation of the class.
    }

    // This method mustn't take a Meal since the ID is required to build it.
    public static long computeId(final String name, final String description, final String restaurantName, final LocalDate date, final MealTime time) {
        final long prime = 31;
        long result = 17;
        result = prime * result + normalize(name).hashCode();
        result = prime * result + normalize(description).hashCode();
        result = prime * result + restaurantName.hashCode();
        result = prime * result + date.hashCode();
        result = prime * result + time.getValue();
        return result;
    }

    public static long computeTimeIndependentId(final EpflMeal meal, final long restaurantId) {
        final long prime = 31;
        long result = 17;
        result = prime * result + normalize(meal.getMName()).hashCode();
        result = prime * result + normalize(meal.getMDescription()).hashCode();
        result = prime * result + restaurantId;
        return result;
    }

    private static String normalize(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+", "");
        return s.replaceAll("\\W", "").toLowerCase();
    }
}

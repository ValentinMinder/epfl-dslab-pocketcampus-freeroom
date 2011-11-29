package org.pocketcampus.plugin.food.server.db;

import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.RatingValue;

/**
 * Class used for all Utils
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class FoodUtils {
	/**
	 * Converts a rating to RatingValue
	 * 
	 * @param totalRating
	 *            the cumulated rating
	 * @param numberOfVotes
	 *            the number of votes for the object
	 * @return the rating value corresponding to that vote
	 */
	public static double doubleToRatingValue(double totalRating,
			int numberOfVotes) {
		if (totalRating < 0 || numberOfVotes < 0) {
			return 0;
		}

		double rating = 0;
		if (numberOfVotes != 0) {
			rating = (totalRating / numberOfVotes);
		}

		return rating;

	}


	/**
	 * Converts a RatingValue to a double value
	 * 
	 * @param rating
	 *            the double to represent
	 * @return its double representation
	 */

	public static double ratingValueToDouble(RatingValue rating) {
		switch (rating) {
		case STAR_0_0:
			return 0.0;
		case STAR_0_5:
			return 0.5;
		case STAR_1_0:
			return 1.0;
		case STAR_1_5:
			return 1.5;
		case STAR_2_0:
			return 2.0;
		case STAR_2_5:
			return 2.5;
		case STAR_3_0:
			return 3.0;
		case STAR_3_5:
			return 3.5;
		case STAR_4_0:
			return 4.0;
		case STAR_4_5:
			return 4.5;
		case STAR_5_0:
			return 5.0;
		default:
			return 0.0;
		}
	}
}

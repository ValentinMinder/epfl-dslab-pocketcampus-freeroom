package org.pocketcampus.plugin.food.server.db;


/**
 * Class used for all Utils
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class FoodUtils {
	/**
	 * Converts a rating to RatingValue
	 * 
	 * @param sumOfRatings
	 *            the cumulated rating
	 * @param numberOfVotes
	 *            the number of votes for the object
	 * @return the rating value corresponding to that vote
	 */
	public static double totalRatingToRatingValue(double sumOfRatings,
			int numberOfVotes) {
		if (sumOfRatings < 0 || numberOfVotes < 0) {
			return 0;
		}

		double rating = 0;
		if (numberOfVotes != 0) {
			rating = (sumOfRatings / numberOfVotes);
		}

		return rating;

	}
}

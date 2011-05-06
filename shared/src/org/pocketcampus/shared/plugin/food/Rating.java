package org.pocketcampus.shared.plugin.food;

/**
 * Menu Rating class
 * 
 * @status incomplete
 * @author elodie
 * @license 
 *
 */

import java.io.Serializable;

public class Rating implements Serializable {

	private static final long serialVersionUID = -7175081667515809103L;
	private StarRating rating_;
	private int numberOfVotes_;
	private double totalRating_;

	public Rating() {
		this.rating_ = StarRating.STAR_0_0;
		this.numberOfVotes_ = 0;
		this.totalRating_ = 0;
	}

	/**
	 * 
	 * @param initialrating
	 *            : The initial average rating
	 * @param numberOfVotes
	 *            : The number of votes so far
	 * @return
	 */
//	public Rating(StarRating rating, int numberOfVotes, double totalRating) {
//		if (rating == null || numberOfVotes < 0 || totalRating < 0)
//			throw new IllegalArgumentException();
//
//		this.rating_ = rating;
//		this.numberOfVotes_ = numberOfVotes;
//		this.totalRating_ = totalRating;
//	}

	public StarRating getValue() {
		return rating_;
	}

	public int getNumberOfVotes() {
		return numberOfVotes_;
	}

	public double getTotalRating() {
		return totalRating_;
	}

	public void addRating(double ratingToAdd) {
		numberOfVotes_++;
		totalRating_ += ratingToAdd;
		rating_ = Restaurant.doubleToStarRating(totalRating_ / numberOfVotes_);
	}

	public String toString() {
		return "Average rating : " + rating_ + ", number of votes : "
				+ numberOfVotes_;
	}
	
	public enum SubmitStatus {
		AlreadyVoted,
		Valid,
		Error;
	}
}

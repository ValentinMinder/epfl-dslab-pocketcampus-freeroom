package org.pocketcampus.plugin.food.menu;

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
	
	/**
	 * 
	 * @param initialrating: The initial average rating
	 * @param numberOfVotes: The number of votes so far
	 * @return 
	 */
	public Rating(StarRating rating, int numberOfVotes) {
		if(rating==null || numberOfVotes<0)
			throw new IllegalArgumentException();
		
		this.rating_ = rating;
		this.numberOfVotes_ = numberOfVotes;
	}

	public StarRating getValue() {
		return rating_;
	}

	public int getNumberOfVotes() {
		return numberOfVotes_;
	}
	
	public String toString() {
		return "Average rating : "+rating_+ ", number of votes : "+numberOfVotes_;
		
	}
}

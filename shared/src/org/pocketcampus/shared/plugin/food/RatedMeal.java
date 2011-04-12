package org.pocketcampus.shared.plugin.food;

/**
 * Associates a meal with its rating, as the server returns
 * the rating for each meal.
 */
public class RatedMeal {
	private Rating rating;
	private Meal meal;
	
	public RatedMeal(){}
	public RatedMeal(Meal m, Rating r){
		this.meal = m;
		this.rating = r;
		valid();
	}
	
	public Rating getRating(){
		return this.rating;
	}
	
	public void setRating(Rating r){
		this.rating = r;
	}
	
	public Meal getMeal(){
		return this.meal;
	}
	
	private void valid(){
		if(meal == null || rating == null){
			throw new IllegalArgumentException("Arguments cannot be null");
		}
	}
}

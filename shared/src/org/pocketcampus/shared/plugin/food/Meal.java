package org.pocketcampus.shared.plugin.food;

/**
 * Campus meal class
 * 
 * @status incomplete
 * @author elodie
 * @license 
 *
 */

import java.io.Serializable;

public class Meal implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name_;
	private String description_;
	private Restaurant restaurant_;
	private boolean available_;
	private Rating rating_;
	
	// private HashMap<UserStatus,Price> prices;

	public Meal() {}
	
	public Meal(String name, String description, Restaurant restaurant, boolean available, Rating rating) {
		this.name_ = name;
		this.description_ = description;
		this.restaurant_ = restaurant;

		this.available_ = available;
		this.rating_ = rating;
		// this.prices = new HashMap<UserStatus, Price>(prices);
		valid();
	}

	private void valid() {
		if (restaurant_ == null)
			throw new IllegalArgumentException("specify a restaurant");
		// if (prices == null) throw new
		// IllegalArgumentException("price should be >= 0");

		/*
		 * for(Entry<UserStatus,Price> entry : prices.entrySet()) {
		 * if(entry.getValue() == null) throw new
		 * IllegalArgumentException("Entry is null"); }
		 */
	}
	
	public boolean isAvailable() {
		return available_;
	}

	@Override
	public String toString() {
		return restaurant_.getName() + " : " + name_
				+ " " + description_;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name_ == null) ? 0 : name_.hashCode());
		result = prime * result + ((description_ == null) ? 0 : description_.hashCode());
		result = prime * result
				+ ((restaurant_ == null) ? 0 : restaurant_.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Meal other = (Meal) obj;

		if (name_ == null) {
			if (other.name_ != null)
				return false;
		} else if (!name_.equals(other.name_))
			return false;
		if (restaurant_ == null) {
			if (other.restaurant_ != null)
				return false;
		} else if (!restaurant_.equals(other.restaurant_))
			return false;
		return true;
	}

	public String getName_() {
		return name_;
	}

	public String getDescription_() {
		return description_;
	}

	public Restaurant getRestaurant_() {
		return restaurant_;
	}
	
	public Rating getRating(){
		return this.rating_;
	}
	
	public void setRating(Rating r){
		rating_ = r;
	}

	public boolean isAvailable_() {
		return available_;
	}
}

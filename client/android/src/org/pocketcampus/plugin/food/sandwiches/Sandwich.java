package org.pocketcampus.plugin.food.sandwiches;

import java.io.Serializable;
import java.util.Date;

public class Sandwich implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String restaurant;
	private String name;
	private boolean available;
	private Date date;
	
	public Sandwich(String restaurant, String name, boolean available, Date date) {
		if (restaurant == null || restaurant.length() > 50)
			throw new IllegalArgumentException("restaurant cannot be null / max length is 50 characters");
		if (name == null || name.length() > 50)
			throw new IllegalArgumentException("name cannot be null / max length is 50 characters");
		if (date == null)
			throw new IllegalArgumentException("date cannot be null");
		
		this.restaurant = restaurant;
		this.name = name;
		this.available = available;
		this.date = date;
	}

	public String getRestaurant() {
		return restaurant;
	}

	public String getName() {
		return name;
	}

	public boolean isAvailable() {
		return available;
	}

	public Date getDate() {
		return date;
	}
	
	public String toString(){
		return "[" + restaurant + ", " + name + "]";
	}
}

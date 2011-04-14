package org.pocketcampus.shared.plugin.food;

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
	
	public Sandwich(){}
	
	public Sandwich(String restaurant, String name, boolean available) {
		if (restaurant == null || restaurant.length() > 50)
			throw new IllegalArgumentException("restaurant cannot be null / max length is 50 characters");
		if (name == null || name.length() > 50)
			throw new IllegalArgumentException("name cannot be null / max length is 50 characters");
		
		this.restaurant = restaurant;
		this.name = name;
		this.available = available;
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

	public String toString(){
		return "[" + restaurant + ", " + name + "]";
	}
}

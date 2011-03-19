package org.pocketcampus.plugin.food.menu;

/**
 * Campus meal class
 * 
 * @status incomplete
 * @author elodie
 * @license 
 *
 */

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Meal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name_;
	private String description_;
	private Restaurant restaurant_;
	private Date date_;
	private int day_;
	private int week_;
	private int year_;
	private boolean available_;

	// private HashMap<UserStatus,Price> prices;

	public Meal(String name, String description, Restaurant restaurant,
			Date date, boolean available) {

		this.name_ = name;
		this.description_ = description;
		this.restaurant_ = restaurant;

		this.date_ = date;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		this.day_ = calendar.get(Calendar.DAY_OF_WEEK);
		this.week_ = calendar.get(Calendar.WEEK_OF_YEAR);
		this.year_ = calendar.get(Calendar.YEAR);
		this.available_ = available;
		// this.prices = new HashMap<UserStatus, Price>(prices);
		valid();
	}

	private void valid() {
		if (restaurant_ == null)
			throw new IllegalArgumentException("specify a restaurant");
		if (date_ == null)
			throw new IllegalArgumentException("specifiy a date");
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

	public String getStringDate() {
		return day_ + "-" + week_ + "-" + year_;

	}

	public int getYear() {
		return year_;
	}

	public int getWeek() {
		return week_;
	}

	public int getDay() {
		return day_;
	}

	public String getName() {
		return name_;
	}

	public String getDescription() {
		return description_;
	}

	public Restaurant getRestaurant() {
		return restaurant_;
	}

	/*
	 * @Override public Price getPrice(UserStatus role) { Price price =
	 * prices.get(role); return price; }
	 */

	@Override
	public String toString() {
		return restaurant_.getName() + " (" + getStringDate() + ") : " + name_
				+ " " + description_;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + day_;
		result = prime * result + ((name_ == null) ? 0 : name_.hashCode());
		result = prime * result
				+ ((restaurant_ == null) ? 0 : restaurant_.hashCode());
		result = prime * result + week_;
		result = prime * result + year_;
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
		if (day_ != other.day_)
			return false;
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
		if (week_ != other.week_)
			return false;
		if (year_ != other.year_)
			return false;
		return true;
	}
}

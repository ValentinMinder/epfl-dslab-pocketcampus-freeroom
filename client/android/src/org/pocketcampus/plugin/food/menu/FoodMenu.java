/**
 * Campus menu class
 * 
 * @status incomplete
 * @author elodie
 * @license 
 *
 */
package org.pocketcampus.plugin.food.menu;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;


public class FoodMenu {
	private HashMap<Meal, Rating> campusMenu_;
	
	public FoodMenu(){
		//Instantiate menuEPFL
		campusMenu_ = new HashMap<Meal, Rating>();
		loadCampusMenu();
	}
	
	//Load menu from server
	private void loadCampusMenu(){
		//Test Example
		Restaurant r1 = new Restaurant("Corbu");
		Meal m1 = new Meal("Name1", "Description1", r1, new Date(), true);
		Meal m2 = new Meal("Name2", "Description2", r1, new Date(), true);
		Meal m3 = new Meal("Name3", "Description3", r1, new Date(), true);
		
		Rating rate1 = new Rating(StarRating.STAR_1_0, 5);
		
		campusMenu_.put(m1, rate1);
		campusMenu_.put(m2, rate1);
		campusMenu_.put(m3, rate1);
		
		Restaurant r2 = new Restaurant("Orni");
		Meal m4 = new Meal("Name1", "Description1", r2, new Date(), true);
		Meal m5 = new Meal("Name2", "Description2", r2, new Date(), true);
		Meal m6 = new Meal("Name3", "Description3", r2, new Date(), true);
		
		Rating rate2 = new Rating(StarRating.STAR_1_0, 5);

		campusMenu_.put(m4, rate2);
		campusMenu_.put(m5, rate2);
		campusMenu_.put(m6, rate2);
	}
	
	public Set<Meal> getKeySet(){
		return campusMenu_.keySet();
	}
	
	public Rating getRating(Meal m){
		return campusMenu_.get(m);
	}
	
	//Get menu to display
	public HashMap<Meal, Rating> getCampusMenu(){
		return this.campusMenu_;
	}

	public boolean isEmpty(){
		return campusMenu_.isEmpty();
	}
}

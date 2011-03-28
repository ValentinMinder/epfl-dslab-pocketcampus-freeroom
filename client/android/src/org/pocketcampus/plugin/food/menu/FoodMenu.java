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

import android.content.Context;


public class FoodMenu {
	private HashMap<Meal, Rating> campusMenu_;
	private Context ctx_;
	
	public FoodMenu(Context context){
		ctx_ = context;
		//Instantiate menuEPFL
		campusMenu_ = new HashMap<Meal, Rating>();
		loadCampusMenu();
	}
	
	//Load menu from server
	private void loadCampusMenu(){
		//Test Example
//		Restaurant r1 = new Restaurant("Corbu");
//		Meal m1 = new Meal("Name1", "Description1", r1, new Date(), true);
//		Meal m2 = new Meal("Name2", "Description2", r1, new Date(), true);
//		Meal m3 = new Meal("Name3", "Description3", r1, new Date(), true);
//		Meal m_mauvais = new Meal("Name4", "Mauvais repas", r1, new Date(), true);
//		
//		Rating rate1 = new Rating(StarRating.STAR_1_0, 5);
//		
//		campusMenu_.put(m1, rate1);
//		campusMenu_.put(m2, rate1);
//		campusMenu_.put(m3, rate1);
//		campusMenu_.put(m_mauvais, rate1);
//		
//		Restaurant r2 = new Restaurant("Orni");
//		Meal m4 = new Meal("Name1", "Description1", r2, new Date(), true);
//		Meal m5 = new Meal("Name2", "Description2", r2, new Date(), true);
//		Meal m6 = new Meal("Name3", "Description3", r2, new Date(), true);
//		Meal m_pourri = new Meal("Name4", "Plat pourri", r2, new Date(), true);
//		
//		Rating rate2 = new Rating(StarRating.STAR_1_0, 5);
//
//		campusMenu_.put(m4, rate2);
//		campusMenu_.put(m5, rate2);
//		campusMenu_.put(m6, rate2);
//		campusMenu_.put(m_pourri, rate2);
		MenuImporter mi = new MenuImporter(ctx_);
		
		campusMenu_ = mi.getMenu();
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
	
	public void setCampusMenu(HashMap<Meal, Rating> menus){
		this.campusMenu_ = menus;
	}

	public boolean isEmpty(){
		return campusMenu_.isEmpty();
	}
}

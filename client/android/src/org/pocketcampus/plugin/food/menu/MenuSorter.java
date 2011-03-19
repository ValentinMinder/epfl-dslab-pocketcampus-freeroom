package org.pocketcampus.plugin.food.menu;

/**
 * Sorts menus
 * 
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class MenuSorter {

	public MenuSorter() {}

	/**
	 * sorts the meals by Rating (best rates first)
	 * 
	 * @param the collection of meals for the day
	 * 
	 * @return the sorted list of meals
	 **/
	public Vector<Meal> sortByRatings(FoodMenu menu_) {
		if(menu_ == null){
			throw new IllegalArgumentException("The meals list cannot be null !");
		}
		
		List<RatedMeal> ratedMeals = new ArrayList<RatedMeal>();
		
		Set<Meal> set = menu_.getKeySet();
		for(Meal meal : set){
			if(!meal.getDescription().matches("\\s+")){
				Rating rate = menu_.getRating(meal);
				if(rate != null)
				ratedMeals.add(new RatedMeal(meal, rate));
			}
		}
		
		Collections.sort(ratedMeals, new RatingComparator());
		Collections.reverse(ratedMeals);
		
		Vector<Meal> mealsVector = new Vector<Meal>();
		
		for (RatedMeal ratedMeal : ratedMeals) {
			mealsVector.add(ratedMeal.getMeal());
		}

		return mealsVector;
	}

	/**
	 * sorts the meals by Restaurant (Alphabetical order)
	 * 
	 * @param the collection of meals for the day
	 * 
	 * @return the sorted list of meals
	 **/
	public HashMap<String, Vector<Meal>> sortByRestaurant(Collection<Meal> meals) {
		
		if(meals == null){
			throw new IllegalArgumentException("The meals list cannot be null !");
		}
		
		HashMap<String, Vector<Meal>> map = new HashMap<String, Vector<Meal>>();
		
		for (Meal meal : meals) {
			String resto = meal.getRestaurant().getName();

			if (!meal.getDescription().matches("\\s+")) {
				if (map.containsKey(resto)) {

					map.get(resto).add(meal);
				} else {
					Vector<Meal> vector = new Vector<Meal>();
					vector.add(meal);
					map.put(resto, vector);
				}
			} else {
				System.out.println("SortingMeals: Skip empty Description: "+meal.getDescription()+" - Name: "+meal.getName()+" Resto: "+meal.getRestaurant());
			}
		}
		return map;
	}

	/**
	 * sorts the meals by Day
	 * 
	 * @param the collection of meals for the week for a particular Restaurant
	 * 
	 * @return the sorted list of meals
	 **/
	public Vector<Vector<Meal>> sortByDay(FoodMenu meals) {
		
		if(meals == null){
			throw new IllegalArgumentException("The meals list cannot be null !");
		}
		
		Vector<Vector<Meal>> vec = new Vector<Vector<Meal>>();		
		
		for (int i=0; i<5; i++) {
			vec.add(new Vector<Meal>());
		}
		Set<Meal> set = meals.getKeySet();
		for (Meal meal : set) {
			if(!meal.getDescription().matches("\\s+")){
				int day = meal.getDay();
				vec.get(day-2).add(meal);
			}
		}	
		
		return vec;
	}

	/**
	 * Compares meals using their rating, in order to 
	 * sort them.
	 *
	 */
	private class RatingComparator implements Comparator<RatedMeal>{
		
		public int compare(RatedMeal arg0, RatedMeal arg1) {
			double d0 = Restaurant.starRatingToDouble(arg0.getRating().getValue());
			double d1 = Restaurant.starRatingToDouble(arg1.getRating().getValue());
			if(d0 != d1){
				return (d0 < d1 ? -1:1);
			} else{
				int n0 = arg0.getRating().getNumberOfVotes();
				int n1 = arg0.getRating().getNumberOfVotes();
				return (n0 < n1 ? -1:1);
			}
		}		
	}
}

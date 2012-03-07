package org.pocketcampus.plugin.food.android.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Restaurant;
import org.pocketcampus.plugin.food.shared.Sandwich;

/**
 * 
 * Used to sort the Meals according to their Restaurants or Ratings and sort
 * Sandwiches according to their Cafeterias.
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class MenuSorter {

	/**
	 * Empty Constructor.
	 */
	public MenuSorter() {
	}

	/**
	 * Sorts the Meals by Rating (best rates first).
	 * 
	 * @param menus
	 *            the collection of meals for the day.
	 * 
	 * @return the sorted list of meals.
	 **/
	public Vector<Meal> sortByRatings(List<Meal> menus) {
		if (menus == null) {
			throw new IllegalArgumentException(
					"The meals list cannot be null !");
		}

		Collections.sort(menus, new RatingComparator());
		Collections.reverse(menus);

		Vector<Meal> mealsVector = new Vector<Meal>();

		for (Meal meal : menus) {
			mealsVector.add(meal);
		}

		return mealsVector;
	}

	/**
	 * Sorts the Meals by Restaurant (Alphabetical order).
	 * 
	 * @param meals
	 *            the collection of meals for the day.
	 * 
	 * @return the sorted list of meals.
	 **/
	public HashMap<String, Vector<Meal>> sortByRestaurant(Collection<Meal> meals) {

		if (meals == null) {
			throw new IllegalArgumentException(
					"The meals list cannot be null !");
		}

		HashMap<String, Vector<Meal>> map = new HashMap<String, Vector<Meal>>();

		for (Meal meal : meals) {
			String resto = meal.getRestaurant().getName();

			if (!meal.getMealDescription().matches("\\s+")) {
				if (map.containsKey(resto)) {
					map.get(resto).add(meal);
				} else {
					Vector<Meal> vector = new Vector<Meal>();
					vector.add(meal);
					map.put(resto, vector);
				}
			} else {
				System.out.println("SortingMeals: Skip empty Description: "
						+ meal.getMealDescription() + " - Name: "
						+ meal.getName() + " Resto: " + meal.getRestaurant());
			}
		}
		Set<String> menus = map.keySet();

		// Sort menus alphabetically
		for (String resto : menus) {
			Collections.sort(map.get(resto), new Comparator<Meal>() {
				public int compare(Meal one, Meal other) {
					return one.getName().compareTo(other.getName());
				}
			});
		}

		return map;
	}

	/**
	 * Sorts the sandwiches by Cafeteria, alphabetically.
	 * 
	 * @param sandwiches
	 *            the collection of Sandwiches to sort.
	 * @return a sorted list of Sandwiches.
	 */
	public HashMap<String, Vector<Sandwich>> sortByCafeterias(
			Collection<Sandwich> sandwiches) {

		if (sandwiches == null) {
			throw new IllegalArgumentException(
					"The meals list cannot be null !");
		}

		HashMap<String, Vector<Sandwich>> map = new HashMap<String, Vector<Sandwich>>();

		for (Sandwich sandwich : sandwiches) {
			String resto = sandwich.getRestaurant().getName();

			if (!sandwich.getName().matches("\\s+")) {
				if (map.containsKey(resto)) {
					map.get(resto).add(sandwich);
				} else {
					Vector<Sandwich> vector = new Vector<Sandwich>();
					vector.add(sandwich);
					map.put(resto, vector);
				}
			} else {
				System.out.println("SortingSanwiches: Skip empty - Name: "
						+ sandwich.getName() + " Resto: "
						+ sandwich.getRestaurant().getName());
			}
		}
		Set<String> restos = map.keySet();

		// Sort menus alphabetically
		for (String resto : restos) {
			Collections.sort(map.get(resto), new Comparator<Sandwich>() {
				public int compare(Sandwich one, Sandwich other) {
					return one.getName().compareTo(other.getName());
				}
			});
		}

		return map;

	}

	/**
	 * Sorts a list of Restaurant alphabetically.
	 */
	public ArrayList<Restaurant> sortByRestaurant(
			ArrayList<Restaurant> collection) {
		Collections.sort(collection, new RestaurantComparator());
		return collection;
	}

	/**
	 * Compares meals using their rating, in order to sort them.
	 * 
	 */
	private class RatingComparator implements Comparator<Meal> {

		public int compare(Meal thisMeal, Meal otherMeal) {
			if (thisMeal.getRating() == null) {
				String thisMealString = "" + thisMeal;
				throw new IllegalArgumentException(thisMealString);
			}

			double d0 = round(thisMeal.getRating().getRatingValue());
			double d1 = round(otherMeal.getRating().getRatingValue());

			if (d0 != d1) {
				return (d0 < d1 ? -1 : 1);
			} else {
				int n0 = thisMeal.getRating().getNumberOfVotes();
				int n1 = otherMeal.getRating().getNumberOfVotes();
				
				if(n0 != n1) {
					return (n0 < n1 ? -1 : 1);
				}
				
				return 0;
			}
		}
	}

	/**
	 * Compares Restaurants according to their names.
	 */
	private class RestaurantComparator implements Comparator<Restaurant> {

		@Override
		public int compare(Restaurant arg0, Restaurant arg1) {
			String s1 = arg0.getName();
			String s2 = arg1.getName();

			return s1.compareToIgnoreCase(s2);
		}

	}

	/**
	 * Rounder
	 */
	private double round(double d) {
		double f = 0.5;
		double rounded = f * Math.round(d/f);
		return rounded;
	}
}

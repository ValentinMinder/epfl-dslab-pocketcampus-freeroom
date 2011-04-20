package org.pocketcampus.plugin.food;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.plugin.food.RssParser.RssFeed;
import org.pocketcampus.provider.mapelements.IMapElementsProvider;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;
import org.pocketcampus.shared.plugin.food.Restaurant;
import org.pocketcampus.shared.plugin.food.Sandwich;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;

public class Food implements IPlugin, IMapElementsProvider {

	private List<Meal> campusMeals_;
	private HashMap<Integer, Meal> campusHashMap_;
	private Date lastImportDateM_;

	private List<Sandwich> sandwichList_;
	private Date lastImportDateS_;

	/**
	 * Parse the menus on startup.
	 */
	public Food() {
		campusMeals_ = new ArrayList<Meal>();
		sandwichList_ = new ArrayList<Sandwich>();
		lastImportDateS_ = new Date();
		importSandwiches();
		importMenus();
	}

	/**
	 * Get all menus of the day.
	 * 
	 * @param request
	 * @return
	 */
	@PublicMethod
	public List<Meal> getMenus(HttpServletRequest request) {
		if (!isValid(lastImportDateM_)) {
			System.out
					.println("<getMenus>: Date not valid. Reimporting menus.");
			campusMeals_.clear();
			importMenus();
		} else {
			System.out.println("<getMenus>: Not reimporting menus.");
		}
		return campusMeals_;
	}

	/**
	 * Get all sandwiches of the day.
	 * 
	 * @param request
	 * @return the sandwich list
	 */
	@PublicMethod
	public List<Sandwich> getSandwiches(HttpServletRequest request) {
		if (!isValid(lastImportDateS_)) {
			 importSandwiches();
			System.out.println("Reimporting sandwiches.");
		} else {
			System.out.println("Not reimporting sandwiches.");
		}
		return sandwichList_;
	}

	/**
	 * Checks whether the saved menu is today's.
	 * 
	 * @return
	 */
	private boolean isValid(Date oldDate) {
		if (oldDate == null)
			return false;

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		Calendar then = Calendar.getInstance();
		then.setTime(oldDate);

		if (now.get(Calendar.DAY_OF_WEEK) == then.get(Calendar.DAY_OF_WEEK)) {
			return true;
		} else
			return false;
	}

	/**
	 * Set rating for a particular meal
	 * 
	 * @param meal
	 *            the meal for which we want to set the rating
	 * @param rating
	 *            the rating we want to put.
	 * @return whether the operation worked.
	 */
	@PublicMethod
	public boolean setRating(HttpServletRequest request) {
		System.out.println("Rating request.");

		String stringMeal = request.getParameter("meal");
		System.out.println(stringMeal + " " + campusMeals_.size());
		String stringRating = request.getParameter("rating");
		if (stringMeal == null || stringRating == null) {
			return false;
		}

		int mealHashCode = Integer.parseInt(stringMeal);
		double r = Double.parseDouble(stringRating);

		for (int i = 0; i < campusMeals_.size(); i++) {
			Meal currentMeal = campusMeals_.get(i);
			if (currentMeal.hashCode() == mealHashCode) {
				// Update rating for meal
				currentMeal.getRating().addRating(r);
				// writeToFile();
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the rating for a particular meal
	 * 
	 * @param meal
	 *            the meal for which we want the rating
	 * @return the corresponding rating.
	 */
	@PublicMethod
	public Rating getRating(HttpServletRequest request) {
		System.out.println("Single rating request.");
		String hashCodeString = request.getParameter("meal");

		if (hashCodeString == null) {
			return null;
		}

		int mealHashCode = Integer.parseInt(hashCodeString);
		return new Rating();
	}

	/**
	 * Import menus from the Rss feeds
	 */
	private void importMenus() {
		RestaurantListParser rlp = new RestaurantListParser();
		HashMap<String, String> restaurantFeeds = rlp.getFeeds();
		Set<String> restaurants = restaurantFeeds.keySet();

		for (String r : restaurants) {
			RssParser rp = new RssParser(restaurantFeeds.get(r));
			rp.parse();
			RssFeed feed = rp.getFeed();

			Restaurant newResto = new Restaurant(r);
			if (feed != null && feed.items != null) {
				for (int i = 0; i < feed.items.size(); i++) {
					Meal newMeal = new Meal(feed.items.get(i).title, feed.items
							.get(i).description, newResto, true, new Rating());
					campusMeals_.add(newMeal);
				}
				lastImportDateM_ = new Date();
			}
		}
		if (!campusMeals_.isEmpty()) {
			System.out.println("<importMenus>: Writing to file.");
			// writeToFile();
		}
		// else {
		// System.out.println("<importMenus>: Restoring");
		// campusMeals_ = restoreFromFile();
		// }
	}

	/**
	 * Creates the sandwich list
	 */
	 private void importSandwiches(){
			
	 /*Cafeteria INM*/
	 sandwichList_.add(new Sandwich("Cafeteria INM", "Poulet au Curry", true));
	 sandwichList_
	 .add(new Sandwich("Cafeteria INM", "Thon", true));
	 sandwichList_.add(new Sandwich("Cafeteria INM", "Jambon", true));
	 sandwichList_.add(new Sandwich("Cafeteria INM", "Fromage", true));
	 sandwichList_.add(new Sandwich("Cafeteria INM", "Tomate Mozzarella", true));
	 sandwichList_.add(new Sandwich("Cafeteria INM", "Jambon Cru", true));
	 sandwichList_.add(new Sandwich("Cafeteria INM", "Salami", true));
	 sandwichList_.add(new Sandwich("Cafeteria INM", "Autres", true));
	
	 /* Cafeteria BM */
	 sandwichList_.addAll(defaultSandwichList("Cafeteria BM"));
	
	 /* Cafeteria BC */
	 sandwichList_.addAll(defaultSandwichList("Cafeteria BM"));
	
	 /* Cafeteria SV */
	 sandwichList_.addAll(defaultSandwichList("Cafeteria SV"));
	
	 /* Cafeteria MX */
	 sandwichList_.addAll(defaultSandwichList("Cafeteria MX"));
	
	 /* Cafeteria PH */
	 sandwichList_.addAll(defaultSandwichList("Cafeteria PH"));
	
	 /* Cafeteria ELA */
	 sandwichList_.addAll(defaultSandwichList("Cafeteria ELA"));
	
	 /* Le Giacomettia (Cafeteria SG) */
			
	 sandwichList_.add(new Sandwich("Le Giacometti", "Jambon", true));
	 sandwichList_.add(new Sandwich("Le Giacometti", "Salami", true));
	 sandwichList_.add(new Sandwich("Le Giacometti", "Jambon de dinde", true));
	 sandwichList_.add(new Sandwich("Le Giacometti", "Gruyière", true));
	 sandwichList_.add(new Sandwich("Le Giacometti", "Viande Séchée", true));
	 sandwichList_.add(new Sandwich("Le Giacometti", "Jambon cru", true));
	 sandwichList_.add(new Sandwich("Le Giacometti", "Roast-Beef", true));
	 sandwichList_.add(new Sandwich("Le Giacometti", "Poulet Jijommaise", true));
	 sandwichList_.add(new Sandwich("Le Giacometti", "Crevettes", true));
	 sandwichList_.add(new Sandwich("Le Giacometti", "Saumon fumé", true));
	 sandwichList_.add(new Sandwich("Le Giacometti", "Poulet au Curry", true));
	 sandwichList_.add(new Sandwich("Le Giacometti", "Autres", true));
	
	 /* L'Esplanade */
	 sandwichList_.add(new Sandwich("L'Esplanade", "Thon", true));
	 sandwichList_.add(new Sandwich("L'Esplanade", "Poulet au Curry", true));
	 sandwichList_
	 .add(new Sandwich("L'Esplanade", "Aubergine", true));
	 sandwichList_.add(new Sandwich("L'Esplanade", "Roast-Beef", true));
	 sandwichList_.add(new Sandwich("L'Esplanade", "Jambon Cru", true));
	 sandwichList_.add(new Sandwich("L'Esplanade", "Vuabde Séchée", true));
	 sandwichList_.add(new Sandwich("L'Esplanade", "Saumon Fumé", true));
	 sandwichList_.add(new Sandwich("L'Esplanade", "Autres", true));
	
	 /* L'Arcadie */
	 sandwichList_.addAll(defaultSandwichList("L'Arcadie"));
	
	 /* Atlantide */
	 sandwichList_.add(new Sandwich("L'Atlantide", "Sandwich long", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Sandwich au pavot", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Sandwich intégral", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Sandwich provençal", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Parisette", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Jambon", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Salami", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Dinde", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Thon", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Mozzarella", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Saumon Fumé", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Viande Séchée", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Jambon Cru", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Roast-Beef", true));
	 sandwichList_.add(new Sandwich("L'Atlantide", "Autres", true));
	
	 /* Satellite */
	 sandwichList_.add(new Sandwich("Satellite", "Thon", true));
	 sandwichList_.add(new Sandwich("Satellite", "Jambon fromage", true));
	 sandwichList_.add(new Sandwich("Satellite", "Roast-Beef", true));
	 sandwichList_.add(new Sandwich("Satellite", "Poulet au Curry", true));
	 sandwichList_.add(new Sandwich("Satellite", "Jambon Cru", true));
	 sandwichList_.add(new Sandwich("Satellite", "Tomate mozza", true));
	 sandwichList_.add(new Sandwich("Satellite", "Salami", true));
	 sandwichList_.add(new Sandwich("Satellite", "Parmesan", true));
	 sandwichList_.add(new Sandwich("Satellite", "Aubergine grillé", true));
	 sandwichList_.add(new Sandwich("Satellite", "Viande séchée", true));
	 sandwichList_.add(new Sandwich("Satellite", "Autres", true));
	
	 /* Negoce */
	 sandwichList_.add(new Sandwich("Negoce", "Dinde", true));
	 sandwichList_.add(new Sandwich("Negoce", "Thon", true));
	 sandwichList_.add(new Sandwich("Negoce", "Gratine Jambon", true));
	 sandwichList_.add(new Sandwich("Negoce", "Mozza Olives", true));
	 sandwichList_.add(new Sandwich("Negoce", "Poulet au Curry", true));
	 sandwichList_.add(new Sandwich("Negoce", "Jambon fromage", true));
	 sandwichList_.add(new Sandwich("Negoce", "Jambon", true));
	 sandwichList_.add(new Sandwich("Negoce", "Salami", true));
	 sandwichList_.add(new Sandwich("Negoce", "RoseBeef", true));
	 sandwichList_.add(new Sandwich("Negoce", "Mozzarella", true));
	 sandwichList_.add(new Sandwich("Negoce", "Autres", true));
	
	 lastImportDateS_ = new Date();
	 }
	
	 private Vector<Sandwich> defaultSandwichList(String name) {
	
	 Vector<Sandwich> defaultSandwichList = new Vector<Sandwich>();
	
	 defaultSandwichList.add(new Sandwich(name, "Thon", true));
	 defaultSandwichList.add(new Sandwich(name, "Jambon", true));
	 defaultSandwichList.add(new Sandwich(name, "Fromage", true));
	 defaultSandwichList.add(new Sandwich(name, "Tomate Mozzarella", true));
	 defaultSandwichList.add(new Sandwich(name, "Jambon Cru", true));
	 defaultSandwichList.add(new Sandwich(name, "Salami", true));
	 defaultSandwichList.add(new Sandwich(name, "Autres", true));
	
	 return defaultSandwichList;
	 }

	@Override
	public List<MapElementBean> getLayerItems() {
		// TODO Auto-generated method stub
		return new ArrayList<MapElementBean>();
	}

	@Override
	public MapLayerBean getLayer() {
		// TODO Auto-generated method stub
		return new MapLayerBean("Restaurants", "", 15678, -1, true);
	}

	// public void writeToFile() {
	// lastImportDateM_ = new Date();
	// String filename =
	// "c:/Users/Elodie/workspace/pocketcampus-server/MenusCache";
	//
	// File menuFile = new File(filename);
	//
	// FileOutputStream fos = null;
	// ObjectOutputStream out = null;
	// try {
	// fos = new FileOutputStream(menuFile);
	// out = new ObjectOutputStream(fos);
	// out.writeObject(campusMeals_);
	// out.close();
	// } catch (IOException ex) {}
	// }

//	public List<Meal> restoreFromFile() {
//		String filename = "c:/Users/Elodie/workspace/pocketcampus-server/MenusCache";
//		List<Meal> menu = null;
//
//		File toGet = new File(filename);
//		FileInputStream fis = null;
//		ObjectInputStream in = null;
//		try {
//			fis = new FileInputStream(toGet);
//			in = new ObjectInputStream(fis);
//			Object obj = in.readObject();
//			
//			if(obj instanceof List<?>){
//				menu = (List<Meal>) obj;
//			}
//
//			in.close();
//		} catch (IOException ex) {
//		} catch (ClassNotFoundException ex) {
//		} catch (ClassCastException cce) {
//		}
//
//		return menu;
//	}

}

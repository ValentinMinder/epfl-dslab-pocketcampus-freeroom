package org.pocketcampus.plugin.food;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.plugin.food.RssParser.RssFeed;
import org.pocketcampus.provider.mapelements.IMapElementsProvider;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;
import org.pocketcampus.shared.plugin.food.Restaurant;
import org.pocketcampus.shared.plugin.food.Sandwich;
import org.pocketcampus.shared.plugin.food.StarRating;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class Food implements IPlugin, IMapElementsProvider {

	private List<Meal> campusMeals_;
	private HashMap<Meal, Double> realRatings_;
	private Date lastImportDateM_;
	private Date lastImportDateS_;
	private List<Sandwich> sandwichList_;

	/**
	 * Parse the menus on startup.
	 */
	public Food() {
		campusMeals_ = new ArrayList<Meal>();
		realRatings_ = new HashMap<Meal, Double>();
		sandwichList_ = new ArrayList<Sandwich>();
		lastImportDateS_ = new Date();
//		importSandwiches();
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
			importMenus();
			System.out.println("Reimporting menus.");
		} else {
			System.out.println("Not reimporting menus.");
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
//			importSandwiches();
			System.out.println("Reimporting sandwiches.");
		} else {
			System.out.println("Not reimporting sandwiches.");
		}
		
		List<Sandwich> test = new ArrayList<Sandwich>();
		test.add(new Sandwich("RestoTest", "Kangourou", true));
		return test;
//		return sandwichList_;
		
	}

	/**
	 * Checks whether the saved menu is today's.
	 * 
	 * @return
	 */
	private boolean isValid(Date oldDate) {
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
		String stringRating = request.getParameter("rating");
		if (stringMeal == null || stringRating == null) {
			return false;
		}

		Meal m = new Meal();
		double r = Double.parseDouble(stringRating);

		Gson gson = new Gson();

		Type mealType = new TypeToken<Meal>() {
		}.getType();
		try {
			m = gson.fromJson(stringMeal, mealType);
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
		} catch (NullPointerException npe) {
		}

		for (int index = 0; index < campusMeals_.size(); index++) {

		}
		if (campusMeals_.contains(m)) {
			for (int i = 0; i < campusMeals_.size(); i++) {
				Meal currentMeal = campusMeals_.get(i);
				if (currentMeal.equals(m)) {
					// Average in the new rating with the ones previously there.
					Double oldRatingTotal = realRatings_.get(m);

					int oldRatingCount = currentMeal.getRating()
					.getNumberOfVotes();
					int newRatingCount = oldRatingCount + 1;

					Rating newMenuRating = new Rating(Restaurant
							.doubleToStarRating((oldRatingTotal + r)
									/ newRatingCount), newRatingCount);

					// Update rating for meal
					currentMeal.setRating(newMenuRating);
					// Update the total value of ratings for meal
					realRatings_.put(m, oldRatingTotal + r);
					return true;
				}
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
	// @PublicMethod
	// public Rating getRating(Meal meal) {
	// return campusMenu_.get(meal);
	// }

	/**
	 * Import menus from the Rss feeds
	 */
	private void importMenus() {
		RestaurantListParser rlp = new RestaurantListParser();
		HashMap<String, String> restaurantFeeds = rlp.getFeeds();
		Set<String> restaurants = restaurantFeeds.keySet();

		Rating origRating = new Rating(StarRating.STAR_3_0, 0);

		for (String r : restaurants) {
			RssParser rp = new RssParser(restaurantFeeds.get(r));
			rp.parse();
			RssFeed feed = rp.getFeed();

			Restaurant newResto = new Restaurant(r);
			if (feed != null && feed.items != null) {
				for (int i = 0; i < feed.items.size(); i++) {
					Meal newMeal = new Meal(feed.items.get(i).title, feed.items
							.get(i).description, newResto, true, origRating);
					campusMeals_.add(newMeal);
					realRatings_.put(newMeal, new Double(0));
				}
			}
		}
		lastImportDateM_ = new Date();
	}

	/**
	 * Creates the sandwich list
	 */
//	private void importSandwiches(){
//		
//		/*Cafeteria INM*/
//		sandwichList_.add(new Sandwich("Cafeteria INM", "Poulet au Curry", true,
//				new Date()));
//		sandwichList_
//		.add(new Sandwich("Cafeteria INM", "Thon", true, new Date()));
//		sandwichList_.add(new Sandwich("Cafeteria INM", "Jambon", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Cafeteria INM", "Fromage", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Cafeteria INM", "Tomate Mozzarella",
//				true, new Date()));
//		sandwichList_.add(new Sandwich("Cafeteria INM", "Jambon Cru", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Cafeteria INM", "Salami", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Cafeteria INM", "Autres", true,
//				new Date()));
//
//		/* Cafeteria BM */
//		sandwichList_.addAll(defaultSandwichList("Cafeteria BM"));
//
//		/* Cafeteria BC */
//		sandwichList_.addAll(defaultSandwichList("Cafeteria BM"));
//
//		/* Cafeteria SV */
//		sandwichList_.addAll(defaultSandwichList("Cafeteria SV"));
//
//		/* Cafeteria MX */
//		sandwichList_.addAll(defaultSandwichList("Cafeteria MX"));
//
//		/* Cafeteria PH */
//		sandwichList_.addAll(defaultSandwichList("Cafeteria PH"));
//
//		/* Cafeteria ELA */
//		sandwichList_.addAll(defaultSandwichList("Cafeteria ELA"));
//
//		/* Le Giacomettia (Cafeteria SG) */
//		
//		sandwichList_.add(new Sandwich("Le Giacometti", "Jambon", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Le Giacometti", "Salami", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Le Giacometti", "Jambon de dinde", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Le Giacometti", "Gruyière", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Le Giacometti", "Viande Séchée", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Le Giacometti", "Jambon cru", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Le Giacometti", "Roast-Beef", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Le Giacometti", "Poulet Jijommaise",
//				true, new Date()));
//		sandwichList_.add(new Sandwich("Le Giacometti", "Crevettes", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Le Giacometti", "Saumon fumé", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Le Giacometti", "Poulet au Curry", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Le Giacometti", "Autres", true,
//				new Date()));
//
//		/* L'Esplanade */
//		sandwichList_.add(new Sandwich("L'Esplanade", "Thon", true, new Date()));
//		sandwichList_.add(new Sandwich("L'Esplanade", "Poulet au Curry", true,
//				new Date()));
//		sandwichList_
//		.add(new Sandwich("L'Esplanade", "Aubergine", true, new Date()));
//		sandwichList_.add(new Sandwich("L'Esplanade", "Roast-Beef", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("L'Esplanade", "Jambon Cru", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("L'Esplanade", "Vuabde Séchée", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("L'Esplanade", "Saumon Fumé", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("L'Esplanade", "Autres", true, new Date()));
//
//		/* L'Arcadie */
//		sandwichList_.addAll(defaultSandwichList("L'Arcadie"));
//
//		/* Atlantide */
//		sandwichList_.add(new Sandwich("L'Atlantide", "Sandwich long", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Sandwich au pavot", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Sandwich intégral", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Sandwich provençal", true,
//				new Date()));
//		sandwichList_
//		.add(new Sandwich("L'Atlantide", "Parisette", true, new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Jambon", true, new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Salami", true, new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Dinde", true, new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Thon", true, new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Mozzarella", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Saumon Fumé", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Viande Séchée", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Jambon Cru", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Roast-Beef", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("L'Atlantide", "Autres", true, new Date()));
//
//		/* Satellite */
//		sandwichList_.add(new Sandwich("Satellite", "Thon", true, new Date()));
//		sandwichList_.add(new Sandwich("Satellite", "Jambon fromage", true,
//				new Date()));
//		sandwichList_
//		.add(new Sandwich("Satellite", "Roast-Beef", true, new Date()));
//		sandwichList_.add(new Sandwich("Satellite", "Poulet au Curry", true,
//				new Date()));
//		sandwichList_
//		.add(new Sandwich("Satellite", "Jambon Cru", true, new Date()));
//		sandwichList_.add(new Sandwich("Satellite", "Tomate mozza", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Satellite", "Salami", true, new Date()));
//		sandwichList_.add(new Sandwich("Satellite", "Parmesan", true, new Date()));
//		sandwichList_.add(new Sandwich("Satellite", "Aubergine grillé", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Satellite", "Viande séchée", true,
//				new Date()));
//		sandwichList_.add(new Sandwich("Satellite", "Autres", true, new Date()));
//
//		/* Negoce */
//		sandwichList_.add(new Sandwich("Negoce", "Dinde", true, new Date()));
//		sandwichList_.add(new Sandwich("Negoce", "Thon", true, new Date()));
//		sandwichList_.add(new Sandwich("Negoce", "Gratine Jambon", true, new Date()));
//		sandwichList_.add(new Sandwich("Negoce", "Mozza Olives", true, new Date()));
//		sandwichList_.add(new Sandwich("Negoce", "Poulet au Curry", true, new Date()));
//		sandwichList_.add(new Sandwich("Negoce", "Jambon fromage", true, new Date()));
//		sandwichList_.add(new Sandwich("Negoce", "Jambon", true, new Date()));
//		sandwichList_.add(new Sandwich("Negoce", "Salami", true, new Date()));
//		sandwichList_.add(new Sandwich("Negoce", "RoseBeef", true, new Date()));
//		sandwichList_.add(new Sandwich("Negoce", "Mozzarella", true, new Date()));
//		sandwichList_.add(new Sandwich("Negoce", "Autres", true, new Date()));
//
//		lastImportDateS_ = new Date();
//	}
//
//	private Vector<Sandwich> defaultSandwichList(String name) {
//
//		Vector<Sandwich> defaultSandwichList = new Vector<Sandwich>();
//
//		defaultSandwichList.add(new Sandwich(name, "Thon", true, new Date()));
//		defaultSandwichList.add(new Sandwich(name, "Jambon", true, new Date()));
//		defaultSandwichList
//		.add(new Sandwich(name, "Fromage", true, new Date()));
//		defaultSandwichList.add(new Sandwich(name, "Tomate Mozzarella", true,
//				new Date()));
//		defaultSandwichList.add(new Sandwich(name, "Jambon Cru", true,
//				new Date()));
//		defaultSandwichList.add(new Sandwich(name, "Salami", true, new Date()));
//		defaultSandwichList.add(new Sandwich(name, "Autres", true, new Date()));
//
//		return defaultSandwichList;
//	}

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
}

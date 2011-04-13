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
import org.pocketcampus.shared.plugin.food.StarRating;
import org.pocketcampus.shared.plugin.map.MapElementBean;
import org.pocketcampus.shared.plugin.map.MapLayerBean;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class Food implements IPlugin, IMapElementsProvider {

	private List<Meal> campusMeals_;
	private HashMap<Meal, Double> realRatings_;
	private Date lastImportDate_;

	/**
	 * Parse the menus on startup.
	 */
	public Food() {
		campusMeals_ = new ArrayList<Meal>();
		realRatings_ = new HashMap<Meal, Double>();
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
		if (!isValid()) {
			importMenus();
			System.out.println("Reimporting menus.");
		} else {
			System.out.println("Not reimporting menus.");
		}
		System.out.println(campusMeals_);
		return campusMeals_;
	}

	/**
	 * Checks whether the saved menu is today's.
	 * 
	 * @return
	 */
	private boolean isValid() {
		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		Calendar then = Calendar.getInstance();
		then.setTime(lastImportDate_);

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
		lastImportDate_ = new Date();
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
}

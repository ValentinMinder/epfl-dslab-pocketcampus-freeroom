package org.pocketcampus.plugin.food;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.plugin.food.RssParser.RssFeed;
import org.pocketcampus.provider.mapelements.IMapElementsProvider;
import org.pocketcampus.shared.food.Meal;
import org.pocketcampus.shared.food.Rating;
import org.pocketcampus.shared.food.Restaurant;
import org.pocketcampus.shared.food.StarRating;

public class Food implements IPlugin, IMapElementsProvider {

	private HashMap<Meal, Rating> campusMenu_;

	/**
	 * Parse the menus on startup.
	 */
	public Food() {
		campusMenu_ = new HashMap<Meal, Rating>();
		importMenus();
		System.out.println("Importing menus");
	}

	@PublicMethod
	public String food(HttpServletRequest request) {
		return "Food tryouts.";
	}

	@PublicMethod
	public HashMap<Meal, Rating> getMenus(HttpServletRequest request) {
		return campusMenu_;
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
//	@PublicMethod
//	public boolean setRating(Meal meal, StarRating rating) {
//		if (campusMenu_.containsKey(meal)) {
//			//Average in the new rating with the ones previously there.
//			Rating oldRating = campusMenu_.get(meal);
//			double oldRatingValue = Restaurant.starRatingToDouble(oldRating
//					.getValue());
//			int oldRatingCount = oldRating.getNumberOfVotes();
//			double newRatingValue = Restaurant.starRatingToDouble(rating);
//
//			Rating newMenuRating = new Rating(Restaurant
//					.doubleToStarRating(((oldRatingValue) * oldRatingCount)
//							+ newRatingValue), oldRatingCount + 1);
//
//			campusMenu_.put(meal, newMenuRating);
//			return true;
//		}
//		return false;
//	}

	/**
	 * Get the rating for a particular meal
	 * 
	 * @param meal
	 *            the meal for which we want the rating
	 * @return the corresponding rating.
	 */
//	@PublicMethod
//	public Rating getRating(Meal meal) {
//		return campusMenu_.get(meal);
//	}

	/**
	 * Import menus from the Rss feeds
	 */
	private void importMenus() {
		RestaurantListParser rlp = new RestaurantListParser();
		HashMap<String, String> restaurantFeeds = rlp.getFeeds();
		Set<String> restaurants = restaurantFeeds.keySet();

		campusMenu_ = new HashMap<Meal, Rating>();

		for (String r : restaurants) {
			// For now, filter restaurants that cause encoding problems for
			// current day.
			if (!(r.equals("Parmentier") || r.equals("Le Vinci"))) {
				System.out.println(r);
				RssParser rp = new RssParser(restaurantFeeds.get(r));
				rp.parse();
				RssFeed feed = rp.getFeed();

				Restaurant newResto = new Restaurant(r);
				if (feed != null && feed.items != null) {
					for (int i = 0; i < feed.items.size(); i++) {
						Meal newMeal = new Meal(feed.items.get(i).title,
								feed.items.get(i).description, newResto,
								new Date(), true);
						campusMenu_.put(newMeal, new Rating(
								StarRating.STAR_3_0, 0));
					}
				} else {
					System.out.println("Debug: feed null for " + r + ".");
				}
			}
		}
	}

	@Override
	public String getLayerName() {
		return "Restaurants";
	}

	@Override
	public String getLayerDescription() {
		return "Places to eat.";
	}
}

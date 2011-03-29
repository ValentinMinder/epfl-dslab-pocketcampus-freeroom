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

import org.pocketcampus.plugin.food.menu.RssParser.RssFeed;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class FoodMenu {
	private HashMap<Meal, Rating> campusMenu_;
	private MenuDownloader menuDownloader_;
	private Context ctx_;

	public FoodMenu(Context context) {
		ctx_ = context;
		// Instantiate menuEPFL
		campusMenu_ = new HashMap<Meal, Rating>();
		loadCampusMenu();
	}

	// Load menu from server
	public void loadCampusMenu() {
		// Test Example
		// Restaurant r1 = new Restaurant("Corbu");
		// Meal m1 = new Meal("Name1", "Description1", r1, new Date(), true);
		// Meal m2 = new Meal("Name2", "Description2", r1, new Date(), true);
		// Meal m3 = new Meal("Name3", "Description3", r1, new Date(), true);
		// Meal m_mauvais = new Meal("Name4", "Mauvais repas", r1, new Date(),
		// true);
		//		
		// Rating rate1 = new Rating(StarRating.STAR_1_0, 5);
		//		
		// campusMenu_.put(m1, rate1);
		// campusMenu_.put(m2, rate1);
		// campusMenu_.put(m3, rate1);
		// campusMenu_.put(m_mauvais, rate1);
		//		
		// Restaurant r2 = new Restaurant("Orni");
		// Meal m4 = new Meal("Name1", "Description1", r2, new Date(), true);
		// Meal m5 = new Meal("Name2", "Description2", r2, new Date(), true);
		// Meal m6 = new Meal("Name3", "Description3", r2, new Date(), true);
		// Meal m_pourri = new Meal("Name4", "Plat pourri", r2, new Date(),
		// true);
		//		
		// Rating rate2 = new Rating(StarRating.STAR_1_0, 5);
		//
		// campusMenu_.put(m4, rate2);
		// campusMenu_.put(m5, rate2);
		// campusMenu_.put(m6, rate2);
		// campusMenu_.put(m_pourri, rate2);
		//				
		menuDownloader_ = new MenuDownloader(this);
		menuDownloader_.execute();
	}

	public Set<Meal> getKeySet() {
		return campusMenu_.keySet();
	}

	public Rating getRating(Meal m) {
		return campusMenu_.get(m);
	}

	// Get menu to display
	public HashMap<Meal, Rating> getCampusMenu() {
		return this.campusMenu_;
	}

	public void setCampusMenu(HashMap<Meal, Rating> menus) {
		this.campusMenu_ = menus;
	}

	public boolean isEmpty() {
		return campusMenu_.isEmpty();
	}

	class MenuDownloader extends AsyncTask<String, Void, HashMap<Meal, Rating>> {

		private FoodMenu foodMenu_;

		public MenuDownloader(FoodMenu foodMenu) {
			this.foodMenu_ = foodMenu;
		}

		@Override
		protected HashMap<Meal, Rating> doInBackground(String... params) {

			RestaurantListParser rlp = new RestaurantListParser(ctx_);
			HashMap<String, String> restaurantFeeds = rlp.getFeeds();
			HashMap<Meal, Rating> campusMenu = new HashMap<Meal, Rating>();
			Set<String> restaurants = restaurantFeeds.keySet();
			for (String r : restaurants) {
				RssParser rp = new RssParser(restaurantFeeds.get(r));
				rp.parse();
				RssFeed feed = rp.getFeed();

				Restaurant newResto = new Restaurant(r);
				if (feed != null && feed.items != null) {
					for (int i = 0; i < feed.items.size(); i++) {
						Log.d("MEAL", feed.items.get(i).title);
						Meal newMeal = new Meal(feed.items.get(i).title,
								feed.items.get(i).description, newResto,
								new Date(), true);
						campusMenu.put(newMeal, new Rating(StarRating.STAR_3_0,
								0));
					}
				}
			}
			return campusMenu;
		}

		@Override
		protected void onPostExecute(HashMap<Meal, Rating> result) {
			if (result != null) {
				foodMenu_.setCampusMenu(result);
			}
		}
	}

}

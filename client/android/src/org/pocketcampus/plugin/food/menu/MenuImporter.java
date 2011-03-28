package org.pocketcampus.plugin.food.menu;

/**
 * Goes on the server.
 * Imports menus from 
 * @author Elodie and Oriane
 */
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.pocketcampus.plugin.food.menu.RssParser.RssFeed;

import android.content.Context;
import android.util.Log;

public class MenuImporter {
	
	private HashMap<Meal, Rating> campusMenu_;
	private Context ctx_;
	
	MenuImporter(Context context){
		ctx_ = context;
		importMenus();
	}
	
	public HashMap<Meal, Rating> getMenu(){
		return campusMenu_;
	}
	
	private void importMenus(){
		RestaurantListParser rlp = new RestaurantListParser(ctx_);
		HashMap<String, String> restaurantFeeds = rlp.getFeeds();
		campusMenu_ = new HashMap<Meal, Rating>();
		Set<String> restaurants = restaurantFeeds.keySet();
		for(String r : restaurants){
			RssParser rp = new RssParser(restaurantFeeds.get(r));
			rp.parse();
			RssFeed feed = rp.getFeed();
			
			Restaurant newResto = new Restaurant(r);
			if (feed != null && feed.items != null) {
				for (int i = 0; i < feed.items.size(); i++) {
					Log.d("MEAL", feed.items.get(i).title);
					Meal newMeal = new Meal(feed.items.get(i).title,  feed.items.get(i).description, newResto,new Date(), true);
					campusMenu_.put(newMeal, new Rating(StarRating.STAR_3_0, 0));
				}
			}
		}
	}
}

/**
 * Campus menu class
 * 
 * @status incomplete
 * @author elodie
 * @license 
 *
 */
package org.pocketcampus.plugin.food.menu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.plugin.food.menu.RssParser.RssFeed;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class FoodMenu {

	private HashMap<Meal, Rating> campusMenu_;
	private MenuDownloader menuDownloader_;
	private FoodPlugin handler_;
	private Context ctx_;
	private Date validityDate_;

	public FoodMenu(FoodPlugin ownerActivity) {
		handler_ = ownerActivity;
		ctx_ = ownerActivity.getApplicationContext();
		// Instantiate menuEPFL
		campusMenu_ = new HashMap<Meal, Rating>();
		loadCampusMenu();
	}

	// Load menu from server
	private void loadCampusMenu() {
		handler_.menuRefreshing();
		menuDownloader_ = new MenuDownloader(this);
		menuDownloader_.execute();
	}

	public void writeToFile(Date currentDate) {
		String filename = "MenusCache";

		File menuFile = new File(ctx_.getCacheDir(), filename);

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(menuFile);
			out = new ObjectOutputStream(fos);
			out.writeObject(currentDate);
			out.writeObject(campusMenu_);
			out.close();
		} catch (IOException ex) {
			Toast.makeText(ctx_, "Writing IO Exception", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public HashMap<Meal, Rating> restoreFromFile() {
		String filename = "MenusCache";
		HashMap<Meal, Rating> menu = null;
		File toGet = new File(ctx_.getCacheDir(), filename);
		FileInputStream fis = null;
		ObjectInputStream in = null;
		Date date = null;
		try {
			fis = new FileInputStream(toGet);
			in = new ObjectInputStream(fis);
			date = (Date) in.readObject();
			setValidityDate(date);
			menu = (HashMap<Meal, Rating>) in.readObject();

			in.close();
		} catch (IOException ex) {
			// Toast.makeText(ctx_, "IO Exception", Toast.LENGTH_SHORT).show();
		} catch (ClassNotFoundException ex) {
			// Toast.makeText(ctx_, "Class not found",
			// Toast.LENGTH_SHORT).show();
		} catch (ClassCastException cce) {
			
		}

		return menu;
	}

	public Set<Meal> getMeals() {
		if (campusMenu_ == null) {
			return null;
		}
		return campusMenu_.keySet();
	}

	// Get rating for a menu
	public Rating getRating(Meal m) {
		return campusMenu_.get(m);
	}

	// Get menu to display
	public HashMap<Meal, Rating> getCampusMenu() {
		return this.campusMenu_;
	}

	public Date getValidityDate() {
		return validityDate_;
	}

	public void setValidityDate(Date date) {
		validityDate_ = date;
	}

	public void setCampusMenu(HashMap<Meal, Rating> menus) {
		this.campusMenu_ = menus;
	}

	public void refreshMenu() {
		loadCampusMenu();
		if (campusMenu_.isEmpty()) {
			// TODO: also if it's yesterday's menu.
		} else {
			// Refresh only ratings.
		}
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
			Set<String> restaurants = restaurantFeeds.keySet();

			HashMap<Meal, Rating> campusMenu = new HashMap<Meal, Rating>();

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
				if (result.isEmpty()) {
					/* TESTING IN PROGRESS */
					Toast.makeText(ctx_, "Reading from file",
							Toast.LENGTH_SHORT).show();
					HashMap<Meal, Rating> fromCache = restoreFromFile();
					if (fromCache != null) {
						foodMenu_.setCampusMenu(fromCache);
					} else {
						Toast.makeText(ctx_, "Empty cache", Toast.LENGTH_SHORT)
								.show();
					}
				} else {
					foodMenu_.setCampusMenu(result);
					Date currentDate = new Date();
					foodMenu_.setValidityDate(currentDate);
					writeToFile(currentDate);
					/* TESTING IN PROGRESS */
					Toast.makeText(ctx_, "Writing to file", Toast.LENGTH_SHORT)
							.show();
				}
				handler_.menuRefreshed();
			}
		}
	}
}

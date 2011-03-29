/**
 * Campus menu class
 * 
 * @status incomplete
 * @author elodie
 * @license 
 *
 */
package org.pocketcampus.plugin.food.menu;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.pocketcampus.plugin.food.menu.RssParser.RssFeed;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;
import android.util.Log;
import android.widget.Toast;

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
	@SuppressWarnings("unchecked")
	public void loadCampusMenu() {
		// HashMap<Meal, Rating> testExample = new HashMap<Meal, Rating>();

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
		// testExample.put(m1, rate1);
		// testExample.put(m2, rate1);
		// testExample.put(m3, rate1);
		// testExample.put(m_mauvais, rate1);
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
		// testExample.put(m4, rate2);
		// testExample.put(m5, rate2);
		// testExample.put(m6, rate2);
		// testExample.put(m_pourri, rate2);

		File cacheDir = ctx_.getCacheDir();
		// Toast.makeText(ctx_, cacheDir.getAbsolutePath(), Toast.LENGTH_LONG)
		// .show();

		FileInputStream fis;
		String campusMenuString;
		byte[] buffer = new byte[1024];

		boolean isDownloaded = true;

		menuDownloader_ = new MenuDownloader(this);
		menuDownloader_.execute();

//		File menuFile = new File(cacheDir, "MenusCache");

//		// if (campusMenu_ != null) {
//		campusMenuString = objectToString(campusMenu_);
//
//		FileOutputStream fos;
//		try {
//			fos = new FileOutputStream(menuFile);
//			fos.write(campusMenuString.getBytes());
//			fos.close();
//		} catch (FileNotFoundException e) {
//			Toast.makeText(ctx_, "File not found", Toast.LENGTH_SHORT).show();
//		} catch (IOException e) {
//			Toast.makeText(ctx_, "IO Exception", Toast.LENGTH_SHORT).show();
//		}
//		// } else {
//		// isDownloaded = false;
//		// }
//
//		// if (!isDownloaded) {
//		try {
//			fis = new FileInputStream(menuFile.getPath());
//			fis.read(buffer);
//			fis.close();
//
//			campusMenuString = new String(buffer);
//
//			campusMenu_ = (HashMap<Meal, Rating>) stringToObject(campusMenuString);
//		} catch (FileNotFoundException e) {
//			isDownloaded = false;
//		} catch (IOException e) {
//			isDownloaded = false;
//		}
//		// }
	}

	public Set<Meal> getKeySet() {
		if (campusMenu_ == null) {
			return null;
		}
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

	public static String objectToString(Serializable object) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			new ObjectOutputStream(out).writeObject(object);
			byte[] data = out.toByteArray();
			out.close();

			out = new ByteArrayOutputStream();
			Base64OutputStream b64 = new Base64OutputStream(out, Base64.DEFAULT);
			b64.write(data);
			b64.close();
			out.close();

			return new String(out.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object stringToObject(String encodedObject) {
		try {
			return new ObjectInputStream(new Base64InputStream(
					new ByteArrayInputStream(encodedObject.getBytes()),
					Base64.DEFAULT)).readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

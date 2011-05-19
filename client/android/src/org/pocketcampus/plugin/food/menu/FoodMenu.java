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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.parser.Json;
import org.pocketcampus.core.parser.JsonException;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.plugin.logging.Tracker;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class FoodMenu {

	private List<Meal> campusMenu_;
	private FoodPlugin pluginHandler_;
	private Context ctx_;
	private Date validityDate_;

	/**
	 * Food menu for the corresponding food plugin.
	 * 
	 * @param ownerActivity
	 */
	public FoodMenu(FoodPlugin ownerActivity) {
		pluginHandler_ = ownerActivity;
		ctx_ = ownerActivity.getApplicationContext();
		// Instantiate menuEPFL
		campusMenu_ = new ArrayList<Meal>();
		loadCampusMenu();
	}

	// Get menu to display
	public List<Meal> getCampusMenu() {
		return this.campusMenu_;
	}

	public List<Meal> getCampusMenuPrefered() {
		List<Meal> filteredMenus = filterMenus(this.campusMenu_);
		return filteredMenus;
	}

	private List<Meal> filterMenus(List<Meal> allMeals) {
		List<String> restaurants = restaurantsFromFile();
		List<Meal> prefMeals = new ArrayList<Meal>();
		if (restaurants != null) {

			for (String r : restaurants) {
				Log.d("PREFERENCES", "Resto in the File : " + r);
				for (Meal m : campusMenu_) {
					if (m.getRestaurant_().getName().equals(r)) {
						prefMeals.add(m);
					}
				}
			}
		} else {
			prefMeals = campusMenu_;
		}

		return prefMeals;
	}

	public void setCampusMenu(List<Meal> menus) {
		this.campusMenu_ = menus;
	}

	public void setCampusRatings(HashMap<Integer, Rating> ratings) {
		if (campusMenu_ != null && !campusMenu_.isEmpty()) {
			for (Meal m : campusMenu_) {
				m.setRating(ratings.get(m.hashCode()));
			}
		}
	}

	public Date getValidityDate() {
		return validityDate_;
	}

	public void setValidityDate(Date date) {
		validityDate_ = date;
	}

	public void refreshMenu() {
		Log.d("SERVER", "Refreshing.");
		Tracker.getInstance().trackPageView("food/refreshMenus");
		if (campusMenu_.isEmpty() || !isValidMenu()) {
			Log.d("SERVER", "Reloading menus");
			loadCampusMenu();
		} else {
			// Refresh only ratings.
			Log.d("SERVER", "Reloading ratings");
			loadRatings();
		}
	}

	public boolean isValidMenu() {
		Calendar cal = Calendar.getInstance();
		Calendar validity = Calendar.getInstance();
		validity.setTime(validityDate_);
		if (cal.get(Calendar.DAY_OF_MONTH) == validity
				.get(Calendar.DAY_OF_MONTH)) {
			if (cal.get(Calendar.MONTH) == validity.get(Calendar.MONTH)) {
				if (cal.get(Calendar.YEAR) == validity.get(Calendar.YEAR)) {
					if(getMinutes(validity.getTime(), cal.getTime()) < 10){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private long getMinutes(Date then, Date now){
		long diff = now.getTime() - then.getTime();

	    long diffMinutes = diff / (60 * 1000);
	    
	    System.out.println(diffMinutes);
		return diffMinutes;
	}

	public boolean isEmpty() {
		return campusMenu_.isEmpty();
	}

	// Load ratings from server
	private void loadRatings() {
		pluginHandler_.menuRefreshing();
		class RatingsRequest extends DataRequest {
			private HashMap<Integer, Rating> campusMenuRatingsList;

			@Override
			public void onCancelled() {
				Log.d("SERVER", "Task cancelled");
				pluginHandler_.menuRefreshed(false);
			}

			@Override
			protected void doInUiThread(String result) {
				campusMenuRatingsList = new HashMap<Integer, Rating>();
				if (result != null) {
					Log.d("SERVER", result);
				}
				// De-serializes the response

				Type menuType = new TypeToken<HashMap<Integer, Rating>>() {
				}.getType();
				try {
					campusMenuRatingsList = Json.fromJson(result, menuType);
				} catch (JsonSyntaxException e) {
					Log.d("SERVER", "Jsonsyntax");
					e.printStackTrace();
					pluginHandler_.menuRefreshed(false);
					return;
				} catch (JsonException e) {
					e.printStackTrace();
					pluginHandler_.menuRefreshed(false);
					return;
				}

				if (campusMenuRatingsList != null) {
					setCampusRatings(campusMenuRatingsList);
				} else {
					Log.d("SERVER", "null menu");
				}
				pluginHandler_.menuRefreshed(true);
			}
		}
		Log.d("SERVER", "Requesting menus.");
		FoodPlugin.getFoodRequestHandler().execute(new RatingsRequest(),
				"getRatings", (RequestParameters) null);

	}

	// Load menu from server
	private void loadCampusMenu() {
		pluginHandler_.menuRefreshing();
		class MenusRequest extends DataRequest {
			private List<Meal> campusMenuList;

			@Override
			public void onCancelled() {
				Log.d("SERVER", "Task cancelled");
				pluginHandler_.menuRefreshed(false);
			}

			@Override
			protected void doInUiThread(String result) {
				campusMenuList = new ArrayList<Meal>();
				// Deserializes the response

				Type menuType = new TypeToken<List<Meal>>() {
				}.getType();
				try {
					campusMenuList = Json.fromJson(result, menuType);
				} catch (JsonSyntaxException e) {
					Log.d("SERVER", "Jsonsyntax");
					e.printStackTrace();
					pluginHandler_.menuRefreshed(false);
					return;
				} catch (JsonException e) {
					e.printStackTrace();
					pluginHandler_.menuRefreshed(false);
					return;
				}

				if (campusMenuList != null) {
					if (campusMenuList.isEmpty()) {
						// List<Meal> fromCache = restoreFromFile();
						// if (fromCache != null) {
						// setCampusMenu(fromCache);
						// }
					} else {
						setCampusMenu(campusMenuList);
						Date currentDate = new Date();
						setValidityDate(currentDate);
						writeToFile(currentDate);
					}
				} else {
					Log.d("SERVER", "null menu");
					List<Meal> fromCache = restoreFromFile();	
					if (fromCache != null) {
						setCampusMenu(fromCache);
					}
				}
				pluginHandler_.menuRefreshed(true);
			}
		}
		Log.d("SERVER", "Requesting menus.");
		FoodPlugin.getFoodRequestHandler().execute(new MenusRequest(),
				"getMenus", (RequestParameters) null);
	}

	public void writeToFile(Date currentDate) {
		String filename = "MenusCache";
		File menuFile = new File(ctx_.getCacheDir(), filename);

		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		// cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(menuFile);
			out = new ObjectOutputStream(fos);
			out.writeObject(cal.getTime());
			out.writeObject(campusMenu_);
			out.close();
		} catch (IOException ex) {
			Toast.makeText(ctx_, "Writing IO Exception", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@SuppressWarnings("unchecked")
	public List<Meal> restoreFromFile() {
		String filename = "MenusCache";
		List<Meal> menu = null;
		File toGet = new File(ctx_.getCacheDir(), filename);
		FileInputStream fis = null;
		ObjectInputStream in = null;
		Date date = null;
		try {
			fis = new FileInputStream(toGet);
			in = new ObjectInputStream(fis);
			date = (Date) in.readObject();
			Log.d("Date", date.toString());
			setValidityDate(date);

			menu = (List<Meal>) in.readObject();

			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		}

		return menu;
	}

	@SuppressWarnings("unchecked")
	public List<String> restaurantsFromFile() {
		String filename = "RestaurantsPref";
		List<String> restos = null;
		File toGet = new File(ctx_.getDir("preferences", 0), filename);
		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(toGet);
			in = new ObjectInputStream(fis);

			restos = (List<String>) in.readObject();

			in.close();
		} catch (IOException ex) {
		} catch (ClassNotFoundException ex) {
		} catch (ClassCastException cce) {
		}

		return restos;
	}

}

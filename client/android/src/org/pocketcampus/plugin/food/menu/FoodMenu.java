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
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class FoodMenu {

	private List<Meal> campusMenu_;
	private List<Meal> campusMenuFull_;
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
		campusMenuFull_ = new ArrayList<Meal>();
		loadCampusMenu();
	}

	// Get menu to display
	public List<Meal> getCampusMenu() {
		return this.campusMenu_;
	}

	public void modifyRestaurant(boolean add, String restaurant){
		Log.d("PREFERENCES","It went through the whole thing ! [FoodMenu]");
		//Maintain a list of Restaurant we want to display ?
	}

	public void setCampusMenu(List<Meal> menus) {
		this.campusMenu_ = menus;
	}

	public void setCampusMenuFull(List<Meal> menus) {
		this.campusMenuFull_ = menus;
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
		if (campusMenu_.isEmpty() || !isTodayMenu()) {
			Log.d("SERVER", "Reloading menus");
			loadCampusMenu();
		} else {
			// Refresh only ratings.
			Log.d("SERVER", "Reloading ratings");
			loadRatings();
		}
	}

	public boolean isTodayMenu() {
		Calendar cal = Calendar.getInstance();
		Log.d("Tag", "1: " + cal.get(Calendar.DAY_OF_MONTH) + " 2: "
				+ cal.get(Calendar.MONTH) + " 3: " + cal.get(Calendar.YEAR));
		Calendar validity = Calendar.getInstance();
		validity.setTime(validityDate_);
		Log.d("Tag", "1: " + validity.get(Calendar.DAY_OF_MONTH) + " 2: "
				+ validity.get(Calendar.MONTH) + " 3: " + validity.get(Calendar.YEAR));
		if (cal.get(Calendar.DAY_OF_MONTH) == validity.get(Calendar.DAY_OF_MONTH)) {
			if (cal.get(Calendar.MONTH) == validity.get(Calendar.MONTH)) {
				if (cal.get(Calendar.YEAR) == validity.get(Calendar.YEAR)) {
					return true;
				}
			}
		}
		return false;
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
				Log.d("SERVER", result);
				// Deserializes the response
				Gson gson = new Gson();

				Type menuType = new TypeToken<HashMap<Integer, Rating>>() {
				}.getType();
				try {
					campusMenuRatingsList = gson.fromJson(result, menuType);
				} catch (JsonSyntaxException e) {
					Log.d("SERVER", "Jsonsyntax");
					e.printStackTrace();
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
				Log.d("SERVER", result);
				// Deserializes the response
				Gson gson = new Gson();

				Type menuType = new TypeToken<List<Meal>>() {
				}.getType();
				try {
					campusMenuList = gson.fromJson(result, menuType);
				} catch (JsonSyntaxException e) {
					Log.d("SERVER", "Jsonsyntax");
					e.printStackTrace();
					return;
				}

				if (campusMenuList != null) {
					if (campusMenuList.isEmpty()) {
						List<Meal> fromCache = restoreFromFile();
						if (fromCache != null) {
							setCampusMenu(fromCache);
							setCampusMenuFull(fromCache);
						}
					} else {
						setCampusMenu(campusMenuList);
						setCampusMenuFull(campusMenuList);
						Date currentDate = new Date();
						setValidityDate(currentDate);
						writeToFile(currentDate);
					}
				} else {
					Log.d("SERVER", "null menu");
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
			setValidityDate(date);

			menu = (List<Meal>) in.readObject();

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

}

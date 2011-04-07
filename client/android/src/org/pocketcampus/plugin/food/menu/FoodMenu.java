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
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.pocketcampus.core.communication.RequestHandler;
import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.ServerRequest;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.shared.food.Meal;
import org.pocketcampus.shared.food.Rating;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class FoodMenu {

	private HashMap<Meal, Rating> campusMenu_;
	private FoodPlugin pluginHandler_;
	private RequestHandler requestHandler_;
	private Context ctx_;
	private Date validityDate_;

	public FoodMenu(FoodPlugin ownerActivity, RequestHandler requestHandler) {
		pluginHandler_ = ownerActivity;
		requestHandler_ = requestHandler;
		ctx_ = ownerActivity.getApplicationContext();
		// Instantiate menuEPFL
		campusMenu_ = new HashMap<Meal, Rating>();
		loadCampusMenu();
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
		} catch (ClassCastException cce) {}

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

	// Load menu from server
	private void loadCampusMenu() {
		pluginHandler_.menuRefreshing();
		class MenusRequest extends ServerRequest {
			private HashMap<Meal, Rating> campusMenu;

			@Override
			protected void onPostExecute(String result) {

				Log.d("SERVER", "response: " + result);
				campusMenu = new HashMap<Meal, Rating>();
				// Deserializes the response
				Gson gson = new Gson();
				Type menuType = new TypeToken<HashMap<Meal, Rating>>() {
				}.getType();
				try {
					Log.d("SERVER", "Gson " + result);
					campusMenu = gson.fromJson(result, menuType);
				} catch (JsonSyntaxException e) {
					return;
				} catch (NullPointerException npe) {
					return;
				}

				if (campusMenu != null) {
					if (campusMenu.isEmpty()) {
						HashMap<Meal, Rating> fromCache = restoreFromFile();
						if (fromCache != null) {
							setCampusMenu(fromCache);
						}
					} else {
						setCampusMenu(campusMenu);
						Date currentDate = new Date();
						setValidityDate(currentDate);
						writeToFile(currentDate);
					}
					pluginHandler_.menuRefreshed();
				}
			}
		}
		MenusRequest menusRequest = new MenusRequest();
		// request of the menus
		requestHandler_.execute(menusRequest, "getMenus", (RequestParameters) null);
	}
}

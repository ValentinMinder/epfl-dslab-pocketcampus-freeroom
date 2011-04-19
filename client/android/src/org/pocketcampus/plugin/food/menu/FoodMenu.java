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
import java.util.Date;
import java.util.List;

import org.pocketcampus.core.communication.RequestParameters;
import org.pocketcampus.core.communication.DataRequest;
import org.pocketcampus.plugin.food.FoodPlugin;
import org.pocketcampus.shared.plugin.food.Meal;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class FoodMenu {

	private List<Meal> campusMenu_;
	private FoodPlugin pluginHandler_;
	private Context ctx_;
	private Date validityDate_;

	/**
	 * Food menu for the corresponding food plugin.
	 * @param ownerActivity
	 */
	public FoodMenu(FoodPlugin ownerActivity) {
		pluginHandler_ = ownerActivity;
		ctx_ = ownerActivity.getApplicationContext();
		// Instantiate menuEPFL
		campusMenu_ = new ArrayList<Meal>();
		loadCampusMenu();
	}

	/**
	 * Get meals in menu
	 * @return
	 */
	public List<Meal> getMeals() {
		return campusMenu_;
	}

	// Get menu to display
	public List<Meal> getCampusMenu() {
		return this.campusMenu_;
	}
	
	public void setCampusMenu(List<Meal> menus) {
		this.campusMenu_ = menus;
	}

	public Date getValidityDate() {
		return validityDate_;
	}

	public void setValidityDate(Date date) {
		validityDate_ = date;
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
		class MenusRequest extends DataRequest {
			private List<Meal> campusMenuList;
			
			@Override
			protected void doInUiThread(String result) {				
				campusMenuList = new ArrayList<Meal>();
				// Deserializes the response
				Gson gson = new Gson();

				Type menuType = new TypeToken<List<Meal>>() {}.getType();
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
						}
					} else {
						setCampusMenu(campusMenuList);
						Date currentDate = new Date();
						setValidityDate(currentDate);
						writeToFile(currentDate);
					}
				} else {
					Log.d("SERVER", "null menu");
				}
				pluginHandler_.menuRefreshed();
			}
		}
		Log.d("SERVER", "Requesting menus.");
		FoodPlugin.getFoodRequestHandler().execute(new MenusRequest(), "getMenus",
				(RequestParameters) null);
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

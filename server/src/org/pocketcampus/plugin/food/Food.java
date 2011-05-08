package org.pocketcampus.plugin.food;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.pocketcampus.core.plugin.IPlugin;
import org.pocketcampus.core.plugin.PublicMethod;
import org.pocketcampus.plugin.food.RssParser.RssFeed;
import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Rating;
import org.pocketcampus.shared.plugin.food.Restaurant;
import org.pocketcampus.shared.plugin.food.Sandwich;
import org.pocketcampus.shared.plugin.food.Rating.SubmitStatus;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class Food implements IPlugin/*, IMapElementsProvider*/ {

	private List<Meal> campusMeals_;
	private HashMap<Integer, Rating> campusMealRatings_;
	private ArrayList<String> deviceIds_;
	private Date lastImportMenus_;

	private List<Sandwich> sandwichList_;
	private Date lastImportDateS_;

	private FoodDB database_;
	private boolean noMealsToday_;

	/**
	 * Parse the menus on startup.
	 */
	public Food() {
		database_ = new FoodDB("PocketCampusDB");

		campusMeals_ = new ArrayList<Meal>();
		campusMealRatings_ = new HashMap<Integer, Rating>();
		sandwichList_ = new ArrayList<Sandwich>();
		lastImportDateS_ = new Date();
		deviceIds_ = new ArrayList<String>();
		importSandwiches();
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
		if (!isValid(lastImportMenus_)) {
			System.out
					.println("<getMenus>: Date not valid. Reimporting menus.");
			campusMeals_.clear();
			deviceIds_.clear();
			campusMealRatings_.clear();
			importMenus();
		} else {
			System.out.println("<getMenus>: " + lastImportMenus_
					+ ", not reimporting menus.");
		}
		return campusMeals_;
	}

	public List<String> getRestaurants(HttpServletRequest request) {
		ArrayList<String> restaurantList_ = new ArrayList<String>();

		if (campusMeals_ != null) {

			for (Meal m : campusMeals_) {
				String r = m.getRestaurant_().getName();
				if (!restaurantList_.contains(r)) {
					restaurantList_.add(r);
				}
			}
		}
		return restaurantList_;
	}

	/**
	 * Get all sandwiches of the day.
	 * 
	 * @param request
	 * @return the sandwich list
	 */
	@PublicMethod
	public List<Sandwich> getSandwiches(HttpServletRequest request) {
		if (!isValid(lastImportDateS_)) {
			importSandwiches();
			System.out.println("Reimporting sandwiches.");
		} else {
			System.out.println("Not reimporting sandwiches.");
		}
		return sandwichList_;
	}

	/**
	 * Checks whether the saved menu is today's.
	 * 
	 * @return
	 */
	private boolean isValid(Date oldDate) {
		if (oldDate == null)
			return false;

		Calendar now = Calendar.getInstance();
		now.setTime(new Date());

		Calendar then = Calendar.getInstance();
		then.setTime(oldDate);

		if (now.get(Calendar.DAY_OF_WEEK) == then.get(Calendar.DAY_OF_WEEK)) {
			return true;
		} else {
			return false;
		}
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
	public Rating.SubmitStatus setRating(HttpServletRequest request) {
		updateMenu();
		System.out.println("<setRating>: Rating request.");

		String deviceId = request.getParameter("deviceId");
		String stringMealHashCode = request.getParameter("meal");
		String stringRating = request.getParameter("rating");

		if (stringMealHashCode == null || stringRating == null
				|| deviceId == null) {
			return SubmitStatus.Error;
		}

		Connection connection = database_.createConnection();

		boolean voted = database_.checkVotedDevice(connection, deviceId);

		if (deviceIds_.contains(deviceId)) {
			System.out.println("Already in list");
			database_.closeConnection(connection);
			return SubmitStatus.AlreadyVoted;
		} else if (voted) {
			System.out.println("Already in database.");
			database_.closeConnection(connection);
			return SubmitStatus.AlreadyVoted;
		}

		int mealHashCode = Integer.parseInt(stringMealHashCode);
		double r = Double.parseDouble(stringRating);

		System.out.println(mealHashCode);
		for (int i = 0; i < campusMeals_.size(); i++) {
			Meal currentMeal = campusMeals_.get(i);
			System.out.println("Dedans " + currentMeal.hashCode());
			if (currentMeal.hashCode() == mealHashCode) {
				// Update rating for meal
				currentMeal.getRating().addRating(r);
				// Update rating in the database
				database_.insertRating(connection, mealHashCode, currentMeal);
				database_.insertVotedDevice(connection, deviceId);
				deviceIds_.add(deviceId);

				// Update rating in the list
				campusMealRatings_.put(mealHashCode, currentMeal.getRating());
				database_.closeConnection(connection);
				return SubmitStatus.Valid;
			}
		}
		database_.closeConnection(connection);
		return SubmitStatus.Error;
	}

	/**
	 * Upload a picture for a meal
	 * 
	 * @param request
	 * @return
	 */
	public boolean setPicture(HttpServletRequest request) {
		System.out.println("<setPicture>: Picture request.");
		String deviceID = request.getParameter("deviceId");
		String pictureString = request.getParameter("pictureArray");
		String mealHashCodeString = request.getParameter("meal");

		if (deviceID == null || pictureString == null
				|| mealHashCodeString == null) {
			return false;
		}

		int mealHashCode = Integer.parseInt(mealHashCodeString);
		Gson gson = new Gson();

		Type byteArrayType = new TypeToken<byte[]>() {
		}.getType();
		byte[] picture = null;
		try {
			picture = gson.fromJson(pictureString, byteArrayType);
		} catch (JsonSyntaxException e) {
			System.out.println("Json Syntax exception in retrieving picture.");
			e.printStackTrace();
			return false;
		}

		return database_.uploadPicture(deviceID, mealHashCode, picture);
	}

	/**
	 * Get the rating for a particular meal
	 * 
	 * @param meal
	 *            the meal for which we want the rating
	 * @return the corresponding rating.
	 */
	@PublicMethod
	public HashMap<Integer, Rating> getRatings(HttpServletRequest request) {
		updateMenu();
		System.out.println("<getRatings>: rating request.");

		if (campusMealRatings_ != null) {
			return campusMealRatings_;
		}
		return null;
	}

	private void updateMenu() {
		if (!isValid(lastImportMenus_)) {
			campusMeals_.clear();
			deviceIds_.clear();
			campusMealRatings_.clear();
			importMenus();
		}
	}

	/**
	 * Import menus from the Rss feeds
	 */
	private void importMenus() {

		Connection connection = database_.createConnection();

		List<Meal> mealsFromDB = database_.getMeals(connection);

		System.out.println(campusMeals_.size());
		if (mealsFromDB != null && !mealsFromDB.isEmpty()) {
			campusMeals_ = mealsFromDB;
			for (Meal m : campusMeals_) {
				campusMealRatings_.put(m.hashCode(), m.getRating());
			}
			lastImportMenus_ = new Date();
			System.out.println("<getMenus>: Got from database.");
		} else {
			RestaurantListParser rlp = new RestaurantListParser();
			HashMap<String, String> restaurantFeeds = rlp.getFeeds();
			Set<String> restaurants = restaurantFeeds.keySet();

			for (String r : restaurants) {
				RssParser rp = new RssParser(restaurantFeeds.get(r));
				rp.parse();
				RssFeed feed = rp.getFeed();

				Restaurant newResto = new Restaurant(r);
				if (feed != null && feed.items != null) {
					for (int i = 0; i < feed.items.size(); i++) {
						Rating mealRating = new Rating();
						Meal newMeal = new Meal(feed.items.get(i).title,
								feed.items.get(i).description, newResto, true,
								mealRating);
						campusMeals_.add(newMeal);
						campusMealRatings_.put(newMeal.hashCode(), mealRating);
					}
					lastImportMenus_ = new Date();
				} else {
					System.out.println("<importMenus>: empty feed");
				}
			}
			if(campusMeals_.isEmpty()){
				noMealsToday_ = true;
				lastImportMenus_ = new Date();
			}
			for (Meal m : campusMeals_) {
				database_.insertMeal(m);
				System.out.println("<importMenus>: Inserting meal "
						+ m.getName_() + ", " + m.getRestaurant_());
			}
		}
	}

	/**
	 * Creates the sandwich list
	 */
	private void importSandwiches() {

		/* Cafeteria INM */
		sandwichList_
				.add(new Sandwich("Cafeteria INM", "Poulet au Curry", true));
		sandwichList_.add(new Sandwich("Cafeteria INM", "Thon", true));
		sandwichList_.add(new Sandwich("Cafeteria INM", "Jambon", true));
		sandwichList_.add(new Sandwich("Cafeteria INM", "Fromage", true));
		sandwichList_.add(new Sandwich("Cafeteria INM", "Tomate Mozzarella",
				true));
		sandwichList_.add(new Sandwich("Cafeteria INM", "Jambon Cru", true));
		sandwichList_.add(new Sandwich("Cafeteria INM", "Salami", true));
		sandwichList_.add(new Sandwich("Cafeteria INM", "Autres", true));

		/* Cafeteria BM */
		sandwichList_.addAll(defaultSandwichList("Cafeteria BM"));

		/* Cafeteria BC */
		sandwichList_.addAll(defaultSandwichList("Cafeteria BM"));

		/* Cafeteria SV */
		sandwichList_.addAll(defaultSandwichList("Cafeteria SV"));

		/* Cafeteria MX */
		sandwichList_.addAll(defaultSandwichList("Cafeteria MX"));

		/* Cafeteria PH */
		sandwichList_.addAll(defaultSandwichList("Cafeteria PH"));

		/* Cafeteria ELA */
		sandwichList_.addAll(defaultSandwichList("Cafeteria ELA"));

		/* Le Giacomettia (Cafeteria SG) */

		sandwichList_.add(new Sandwich("Le Giacometti", "Jambon", true));
		sandwichList_.add(new Sandwich("Le Giacometti", "Salami", true));
		sandwichList_
				.add(new Sandwich("Le Giacometti", "Jambon de dinde", true));
		sandwichList_.add(new Sandwich("Le Giacometti", "Gruyière", true));
		sandwichList_.add(new Sandwich("Le Giacometti", "Viande Séchée", true));
		sandwichList_.add(new Sandwich("Le Giacometti", "Jambon cru", true));
		sandwichList_.add(new Sandwich("Le Giacometti", "Roast-Beef", true));
		sandwichList_.add(new Sandwich("Le Giacometti", "Poulet Jijommaise",
				true));
		sandwichList_.add(new Sandwich("Le Giacometti", "Crevettes", true));
		sandwichList_.add(new Sandwich("Le Giacometti", "Saumon fumé", true));
		sandwichList_
				.add(new Sandwich("Le Giacometti", "Poulet au Curry", true));
		sandwichList_.add(new Sandwich("Le Giacometti", "Autres", true));

		/* L'Esplanade */
		sandwichList_.add(new Sandwich("L'Esplanade", "Thon", true));
		sandwichList_.add(new Sandwich("L'Esplanade", "Poulet au Curry", true));
		sandwichList_.add(new Sandwich("L'Esplanade", "Aubergine", true));
		sandwichList_.add(new Sandwich("L'Esplanade", "Roast-Beef", true));
		sandwichList_.add(new Sandwich("L'Esplanade", "Jambon Cru", true));
		sandwichList_.add(new Sandwich("L'Esplanade", "Vuabde Séchée", true));
		sandwichList_.add(new Sandwich("L'Esplanade", "Saumon Fumé", true));
		sandwichList_.add(new Sandwich("L'Esplanade", "Autres", true));

		/* L'Arcadie */
		sandwichList_.addAll(defaultSandwichList("L'Arcadie"));

		/* Atlantide */
		sandwichList_.add(new Sandwich("L'Atlantide", "Sandwich long", true));
		sandwichList_
				.add(new Sandwich("L'Atlantide", "Sandwich au pavot", true));
		sandwichList_
				.add(new Sandwich("L'Atlantide", "Sandwich intégral", true));
		sandwichList_.add(new Sandwich("L'Atlantide", "Sandwich provençal",
				true));
		sandwichList_.add(new Sandwich("L'Atlantide", "Parisette", true));
		sandwichList_.add(new Sandwich("L'Atlantide", "Jambon", true));
		sandwichList_.add(new Sandwich("L'Atlantide", "Salami", true));
		sandwichList_.add(new Sandwich("L'Atlantide", "Dinde", true));
		sandwichList_.add(new Sandwich("L'Atlantide", "Thon", true));
		sandwichList_.add(new Sandwich("L'Atlantide", "Mozzarella", true));
		sandwichList_.add(new Sandwich("L'Atlantide", "Saumon Fumé", true));
		sandwichList_.add(new Sandwich("L'Atlantide", "Viande Séchée", true));
		sandwichList_.add(new Sandwich("L'Atlantide", "Jambon Cru", true));
		sandwichList_.add(new Sandwich("L'Atlantide", "Roast-Beef", true));
		sandwichList_.add(new Sandwich("L'Atlantide", "Autres", true));

		/* Satellite */
		sandwichList_.add(new Sandwich("Satellite", "Thon", true));
		sandwichList_.add(new Sandwich("Satellite", "Jambon fromage", true));
		sandwichList_.add(new Sandwich("Satellite", "Roast-Beef", true));
		sandwichList_.add(new Sandwich("Satellite", "Poulet au Curry", true));
		sandwichList_.add(new Sandwich("Satellite", "Jambon Cru", true));
		sandwichList_.add(new Sandwich("Satellite", "Tomate mozza", true));
		sandwichList_.add(new Sandwich("Satellite", "Salami", true));
		sandwichList_.add(new Sandwich("Satellite", "Parmesan", true));
		sandwichList_.add(new Sandwich("Satellite", "Aubergine grillé", true));
		sandwichList_.add(new Sandwich("Satellite", "Viande séchée", true));
		sandwichList_.add(new Sandwich("Satellite", "Autres", true));

		/* Negoce */
		sandwichList_.add(new Sandwich("Negoce", "Dinde", true));
		sandwichList_.add(new Sandwich("Negoce", "Thon", true));
		sandwichList_.add(new Sandwich("Negoce", "Gratine Jambon", true));
		sandwichList_.add(new Sandwich("Negoce", "Mozza Olives", true));
		sandwichList_.add(new Sandwich("Negoce", "Poulet au Curry", true));
		sandwichList_.add(new Sandwich("Negoce", "Jambon fromage", true));
		sandwichList_.add(new Sandwich("Negoce", "Jambon", true));
		sandwichList_.add(new Sandwich("Negoce", "Salami", true));
		sandwichList_.add(new Sandwich("Negoce", "RoseBeef", true));
		sandwichList_.add(new Sandwich("Negoce", "Mozzarella", true));
		sandwichList_.add(new Sandwich("Negoce", "Autres", true));

		lastImportDateS_ = new Date();
	}

	private Vector<Sandwich> defaultSandwichList(String name) {

		Vector<Sandwich> defaultSandwichList = new Vector<Sandwich>();

		defaultSandwichList.add(new Sandwich(name, "Thon", true));
		defaultSandwichList.add(new Sandwich(name, "Jambon", true));
		defaultSandwichList.add(new Sandwich(name, "Fromage", true));
		defaultSandwichList.add(new Sandwich(name, "Tomate Mozzarella", true));
		defaultSandwichList.add(new Sandwich(name, "Jambon Cru", true));
		defaultSandwichList.add(new Sandwich(name, "Salami", true));
		defaultSandwichList.add(new Sandwich(name, "Autres", true));

		return defaultSandwichList;
	}

//	@Override
//	public List<MapElementBean> getLayerItems(int layerId) {
//		// TODO Auto-generated method stub
//		return new ArrayList<MapElementBean>();
//	}
//
//	@Override
//	public List<MapLayerBean> getLayers() {
//		// TODO Auto-generated method stub
//		List<MapLayerBean> l = new ArrayList<MapLayerBean>();
//		l.add(new MapLayerBean("Restaurants", "", this, 1, -1, true));
//		return l;
//	}

	// public void writeToFile() {
	// lastImportDateM_ = new Date();
	// String filename =
	// "c:/Users/Elodie/workspace/pocketcampus-server/MenusCache";
	//
	// File menuFile = new File(filename);
	//
	// FileOutputStream fos = null;
	// ObjectOutputStream out = null;
	// try {
	// fos = new FileOutputStream(menuFile);
	// out = new ObjectOutputStream(fos);
	// out.writeObject(campusMeals_);
	// out.close();
	// } catch (IOException ex) {}
	// }

	// public List<Meal> restoreFromFile() {
	// String filename =
	// "c:/Users/Elodie/workspace/pocketcampus-server/MenusCache";
	// List<Meal> menu = null;
	//
	// File toGet = new File(filename);
	// FileInputStream fis = null;
	// ObjectInputStream in = null;
	// try {
	// fis = new FileInputStream(toGet);
	// in = new ObjectInputStream(fis);
	// Object obj = in.readObject();
	//
	// if(obj instanceof List<?>){
	// menu = (List<Meal>) obj;
	// }
	//
	// in.close();
	// } catch (IOException ex) {
	// } catch (ClassNotFoundException ex) {
	// } catch (ClassCastException cce) {
	// }
	//
	// return menu;
	// }

}

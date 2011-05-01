package org.pocketcampus.plugin.food;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.pocketcampus.shared.plugin.food.Meal;
import org.pocketcampus.shared.plugin.food.Restaurant;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

/**
 * Class that handles interactions with the test database to store and retrieve
 * meals, user id's and ratings
 * 
 * @author Elodie
 * 
 */
public class FoodDB {
	private String url_;
	private String userName_;
	private String passWord_;

	public FoodDB(String dbName) {
		userName_ = "pocketbuddy";
		passWord_ = "";
		url_ = "jdbc:mysql://ec2-46-51-131-245.eu-west-1.compute.amazonaws.com/test";
	}

	/**
	 * Creates a connection to the database.
	 * 
	 * @return
	 */
	public Connection createConnection() {
		Connection connection_ = null;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection_ = DriverManager.getConnection(url_, userName_,
					passWord_);
			System.out.println("Database connection established");
		} catch (Exception e) {
			System.err.println("Cannot connect to database server");
		}
		return connection_;
	}

	/**
	 * Closes the connection to the database.
	 * 
	 * @param connection_
	 *            the connection to close
	 */
	public void closeConnection(Connection connection_) {
		if (connection_ != null) {
			try {
				connection_.close();
				System.out.println("Database connection terminated");
			} catch (Exception e) { /* ignore close errors */
			}
		}
	}

	/**
	 * Insert a meal into the database.
	 * 
	 * @param connection_
	 * @param m
	 * @return
	 */
	public boolean insertMeal(Connection connection_, Meal m) {
		Statement s;
		String value = "";
		try {
			s = connection_.createStatement();
			int count = 0;
			String name = quote(m.getName_());
			String description = quote(m.getDescription_());
			String restaurant = quote(m.getRestaurant_().getName());
			double rating = Restaurant.starRatingToDouble(m.getRating()
					.getValue());
			int numberOfVotes = m.getRating().getNumberOfVotes();
			int hashcode = m.hashCode();

			Gson gson = new Gson();

			String jsonObject = quote("JsonObject");

			try {
				jsonObject = quote(gson.toJson(m));
			} catch (JsonSyntaxException e) {

			}

			Calendar cal = Calendar.getInstance();
			String dateString = quote(cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ cal.get(Calendar.DAY_OF_MONTH));

			value = name + ", " + description + ", " + restaurant + ", "
					+ rating + ", " + numberOfVotes + ", " + hashcode + ", "
					+ jsonObject + ", " + dateString;
			count = s
					.executeUpdate("INSERT INTO MENUS (Title, Description, Restaurant, Rating, NumberOfVotes, hashcode, JsonObject, stamp_created)"
							+ " VALUES (" + value + ")");
			s.close();
			System.out.println(count + " rows were inserted");
			return true;
		} catch (SQLException e) {
			System.out.println("Problem: " + value);
			return false;
		}
	}

	/**
	 * Get all meals for the day from the database.
	 * 
	 * @param connection_
	 * @return
	 */
	public List<Meal> getMeals(Connection connection_) {
		Statement s;
		try {
			s = connection_.createStatement();

			List<Meal> campusMeals = new ArrayList<Meal>();

			Calendar cal = Calendar.getInstance();
			String dateString = quote(cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ cal.get(Calendar.DAY_OF_MONTH));

			ResultSet rset = s
					.executeQuery("SELECT * FROM MENUS WHERE STAMP_CREATED = "
							+ dateString);

			System.out.println(dateString);
			Gson gson = new Gson();

			String jsonObject = "";
			Type mealType = new TypeToken<Meal>() {
			}.getType();

			while (rset.next()) {
				jsonObject = rset.getString("JsonObject");
				try {
					Meal newMeal = gson.fromJson(jsonObject, mealType);
					campusMeals.add(newMeal);
				} catch (JsonSyntaxException e) {
					System.out.println("JsonSyntaxException");
				}
			}

			s.close();
			return campusMeals;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Check if a device has already submitted a vote during the day.
	 * 
	 * @param connection
	 * @param deviceID
	 * @return
	 */
	public boolean checkVotedDevice(Connection connection, String deviceID) {
		Statement s;
		String value = "";
		try {
			s = connection.createStatement();

			ResultSet rset = s
					.executeQuery("SELECT count(DEVICEID) FROM DailyRatings WHERE DEVICEID = "
							+ quote(deviceID));

			while (rset.next()) {
				System.out.println(rset.getString("DeviceId"));
				if (Integer.parseInt(rset.getString("DeviceId")) == 1) {
					rset.close();
					s.close();
					return true;
				} else {
					rset.close();
					s.close();
					return false;
				}
			}
			return false;
		} catch (SQLException e) {
			System.out.println("Problem: " + value);
			return false;
		}
	}

	/**
	 * Inserts the id of a new voter.
	 * 
	 * @param connection_
	 * @param deviceId
	 */
	public void insertVotedDevice(Connection connection_, String deviceId) {
		Statement s;
		String value = "";
		try {
			s = connection_.createStatement();
			int count = 0;

			Calendar cal = Calendar.getInstance();
			String dateString = quote(cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ cal.get(Calendar.DAY_OF_MONTH));

			value = quote(deviceId) + ", " + dateString;

			count = s
					.executeUpdate("INSERT INTO DAILYRATINGS (DeviceId, stamp_created)"
							+ " VALUES (" + value + ")");
			s.close();
			System.out.println(count + " rows were inserted");
		} catch (SQLException e) {
			System.out.println("Problem: " + value);
		}
	}

	public void insertRating(Connection connection_, int hashCode, Meal meal) {
		Statement s;
		String value = "";
		try {
			s = connection_.createStatement();
			Gson gson = new Gson();
			String jsonObject = "";
			try {
				jsonObject = gson.toJson(meal);
			} catch (JsonSyntaxException e) {

			}
			s.executeUpdate("UPDATE Menus SET Rating="
					+ meal.getRating().getValue() + ", NumberOfVotes="
					+ meal.getRating().getNumberOfVotes() + ", JsonObject="
					+ quote(jsonObject) + "where hashcode=" + hashCode);
		} catch (SQLException e) {
			System.out.println("Problem: " + value);
		}
	}

	private String quote(String toQuote) {
		toQuote = toQuote.replace("\n", "$");
		toQuote = toQuote.replace("\'", "\'\'");
		return "\'" + toQuote + "\'";
	}

}

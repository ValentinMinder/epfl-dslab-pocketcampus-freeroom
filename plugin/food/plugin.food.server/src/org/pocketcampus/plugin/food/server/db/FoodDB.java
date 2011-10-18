package org.pocketcampus.plugin.food.server.db;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;
import org.pocketcampus.plugin.food.shared.Restaurant;

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
	public boolean insertMeal(Meal m) {
		Connection connection_ = createConnection();
		PreparedStatement insertMeal = null;
		String insertString = "INSERT INTO MENUS (Title, Description, Restaurant, Rating, NumberOfVotes, hashcode, JsonObject, stamp_created)"
				+ " VALUES (?,?,?,?,?,?,?,?)";

		try {
			String name = m.getName();
			String description = m.getMealDescription();
			String restaurant = m.getRestaurant().getName();
			double rating = m.getRating().getTotalRating();
			int numberOfVotes = m.getRating().getNbVotes();
			int hashcode = m.hashCode();

//			Gson gson = new Gson();

			String jsonObject = "JsonObject";

//			try {
//				jsonObject = gson.toJson(m);
//			} catch (JsonSyntaxException e) {
//			}

			Calendar cal = Calendar.getInstance();
			String dateString = cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ cal.get(Calendar.DAY_OF_MONTH);

			connection_.setAutoCommit(false);

			insertMeal = connection_.prepareStatement(insertString);

			insertMeal.setString(1, name);
			insertMeal.setString(2, description);
			insertMeal.setString(3, restaurant);
			insertMeal.setFloat(4, (float) rating);
			insertMeal.setInt(5, numberOfVotes);
			insertMeal.setInt(6, hashcode);
			insertMeal.setString(7, jsonObject);
			insertMeal.setString(8, dateString);

			insertMeal.execute();
			connection_.commit();

			System.out.println("inserted meal");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Problem in insert meal.");
			return false;
		} finally {
			try {
				if (insertMeal != null) {
					insertMeal.close();
				}
				connection_.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public Meal getMeal(int mealHashCode) {
		PreparedStatement getMeal = null;
		Connection connection_ = createConnection();
		String getString = "SELECT * FROM MENUS WHERE STAMP_CREATED = ? and HASHCODE=?";
		Meal newMeal = null;

		try {
			Calendar cal = Calendar.getInstance();
			String dateString = cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ (cal.get(Calendar.DAY_OF_MONTH));

			connection_.setAutoCommit(false);

			getMeal = connection_.prepareStatement(getString);
			getMeal.setString(1, dateString);
			getMeal.setInt(2, mealHashCode);

			ResultSet rset = getMeal.executeQuery();

			connection_.commit();

			System.out.println("<getMeal>: getting " + mealHashCode);

//			Gson gson = new Gson();

			String jsonObject = "";
//			Type mealType = new TypeToken<Meal>() {
//			}.getType();

			while (rset.next()) {
				jsonObject = rset.getString("JsonObject");
//				try {
//					newMeal = gson.fromJson(jsonObject, mealType);
//				} catch (JsonSyntaxException e) {
//					System.out.println("JsonSyntaxException");
//				}
			}

			return newMeal;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (getMeal != null) {
					getMeal.close();
				}
				if (connection_ != null) {
					connection_.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get all meals for the day from the database.
	 * 
	 * @param connection_
	 * @return
	 */
	public List<Meal> getMeals(Connection connection_) {
		if (connection_ == null) {
			return null;
		}
		PreparedStatement getMeals = null;
		String getString = "SELECT * FROM MENUS WHERE STAMP_CREATED = ?";
		try {
			List<Meal> campusMeals = new ArrayList<Meal>();

			Calendar cal = Calendar.getInstance();
			String dateString = cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ (cal.get(Calendar.DAY_OF_MONTH));

			connection_.setAutoCommit(false);

			getMeals = connection_.prepareStatement(getString);
			getMeals.setString(1, dateString);
			ResultSet rset = getMeals.executeQuery();

			connection_.commit();

			System.out.println("<getMeals>: getting " + dateString);

//			Gson gson = new Gson();

			String jsonObject = "";
//			Type mealType = new TypeToken<Meal>() {
//			}.getType();

			while (rset.next()) {
				jsonObject = rset.getString("JsonObject");
//				try {
//					Meal newMeal = gson.fromJson(jsonObject, mealType);
//					campusMeals.add(newMeal);
//				} catch (JsonSyntaxException e) {
//					System.out.println("JsonSyntaxException");
//				}
			}

			return campusMeals;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (getMeals != null) {
					getMeals.close();
				}
				connection_.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
		if (connection == null) {
			return false;
		}
		PreparedStatement checkVotedDevice = null;
		String getString = "SELECT count(DEVICEID) FROM PEOPLEVOTES WHERE DEVICEID = ? and STAMP_CREATED = ?";
		ResultSet rset = null;
		boolean found = false;

		try {
			Calendar cal = Calendar.getInstance();
			String dateString = cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ cal.get(Calendar.DAY_OF_MONTH);

			checkVotedDevice = connection.prepareStatement(getString);
			connection.setAutoCommit(false);
			checkVotedDevice.setString(1, deviceID);
			System.out.println(deviceID + " " + dateString);
			checkVotedDevice.setString(2, dateString);

			rset = checkVotedDevice.executeQuery();

			while (rset.next()) {
				if (Integer.parseInt(rset.getString(1)) == 1) {
					found = true;
				}
			}
			rset.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Problem in checkVotedToday: " + deviceID);
			return false;
		} finally {
			try {
				if (checkVotedDevice != null) {
					checkVotedDevice.close();
				}
				if (rset != null) {
					rset.close();
				}
				if (connection != null) {
					connection.setAutoCommit(true);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return found;
	}

	/**
	 * Inserts the id of a new voter.
	 * 
	 * @param connection_
	 * @param deviceId
	 */
	public void insertVotedDevice(Connection connection_, String deviceId, int hashCode, Double myRating) {
		if (connection_ == null) {
			return;
		}
		PreparedStatement insertVotedDevice = null;
		String insertString = "INSERT INTO PEOPLEVOTES (DeviceId, stamp_created, Rating, HashCode) VALUES (?, ?, ?, ?)";
		ResultSet rset = null;

		try {
			insertVotedDevice = connection_.prepareStatement(insertString);
			int count = 0;

			Calendar cal = Calendar.getInstance();
			String dateString = cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ cal.get(Calendar.DAY_OF_MONTH);

			insertVotedDevice.setString(1, deviceId);
			insertVotedDevice.setString(2, dateString);
			insertVotedDevice.setDouble(3, myRating);
			insertVotedDevice.setInt(4, hashCode);

			count = insertVotedDevice.executeUpdate();
			System.out.println(count + " rows were inserted");
		} catch (SQLException e) {
			System.out.println("Problem in insert voted device.");
		} finally {
			try {
				if (insertVotedDevice != null) {
					insertVotedDevice.close();
				}
				if (rset != null) {
					rset.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void insertRating(Connection connection_, int hashCode, Meal meal) {
		if (connection_ == null) {
			return;
		}
		System.out.println("Inserting rating.");
		PreparedStatement insertRating = null;
		String insertString = "UPDATE Menus SET Rating = ?, NumberOfVotes=?, JsonObject=? where hashcode=?";

		String jsonObject = "";
		try {
			insertRating = connection_.prepareStatement(insertString);
//			Gson gson = new Gson();
			jsonObject = "";
//			try {
//				jsonObject = gson.toJson(meal);
//			} catch (JsonSyntaxException e) {
//			}

			Rating r = meal.getRating();
			insertRating.setFloat(1,
					(float) r.getTotalRating());
			insertRating.setInt(2, r.getNbVotes());
			insertRating.setString(3, jsonObject);
			insertRating.setInt(4, hashCode);

			insertRating.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Problem: could not insert rating:" + "Rating="
					+ meal.getRating().getRatingValue() + ", NumberOfVotes="
					+ meal.getRating().getNbVotes() + ", JsonObject="
					+ jsonObject + "where hashcode=" + hashCode);
			e.printStackTrace();
		} finally {
			try {
				if (insertRating != null) {
					insertRating.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean uploadPicture(String uploader, int hashCode, byte[] picture) {

		System.out.println("Inserting picture.");
		Connection con = createConnection();
		PreparedStatement uploadPicture = null;

		String insertString = "INSERT INTO Pictures (Picture, Uploader, MealHashCode, stamp_created)"
				+ " VALUES (?,?,?,?)";

		Calendar cal = Calendar.getInstance();
		String dateString = cal.get(Calendar.YEAR) + "."
				+ (cal.get(Calendar.MONTH) + 1) + "."
				+ cal.get(Calendar.DAY_OF_MONTH);

		try {
			con.setAutoCommit(false);
			uploadPicture = con.prepareStatement(insertString);

			uploadPicture.setBytes(1, picture);
			uploadPicture.setString(2, uploader);
			uploadPicture.setInt(3, hashCode);
			uploadPicture.setString(4, dateString);

			uploadPicture.executeUpdate();

			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			if (con != null) {
				try {
					System.err.print("Transaction is being rolled back");
					con.rollback();
				} catch (SQLException excep) {
					excep.printStackTrace();
				}
			}
			return false;
		} finally {
			try {
				if (uploadPicture != null) {
					uploadPicture.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
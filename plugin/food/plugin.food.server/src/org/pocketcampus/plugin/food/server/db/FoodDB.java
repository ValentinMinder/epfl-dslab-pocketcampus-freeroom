package org.pocketcampus.plugin.food.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;
import org.pocketcampus.plugin.food.shared.Restaurant;

/**
 * Class that handles interactions with the test database to store and retrieve
 * meals, user ids and ratings
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * 
 */
public class FoodDB {
	/** The URL to the database */
	private String mUrl;

	/** The user name to be used at the database */
	private String mUserName;

	/** The password to be used at the database */
	private String mPassWord;

	/** The connection to the database */
	private Connection mConnection;

	/**
	 * Constructor for the Food Database Handler
	 */
	public FoodDB() {
		mUserName = "pocketbuddy";
		mPassWord = "";
		mUrl = "jdbc:mysql://ec2-46-51-131-245.eu-west-1.compute.amazonaws.com/pocketcampus";
		createConnection();
	}

	/**
	 * Creates and returns a new connection to the database.
	 * 
	 * @return the connection that was created
	 */
	public void createConnection() {
		if (mConnection == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				mConnection = DriverManager.getConnection(mUrl, mUserName,
						mPassWord);
				System.out.println("Database connection established");
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Cannot connect to database server");
			}
		}
	}

	/**
	 * Check whether the connection is still valid
	 * 
	 * @return the status of the connection
	 */
	private boolean isValidConnection() {
		createConnection();
		if (mConnection == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Closes the connection to the database.
	 * 
	 * @param connection
	 *            the connection to close
	 */
	public void closeConnection() {
		if (mConnection != null) {
			try {
				mConnection.close();
				System.out.println("Database connection terminated");
			} catch (Exception e) { /* ignore close errors */
			}
		}
	}

	/**
	 * Insert a meal into the database.
	 * 
	 * @param meal
	 *            the meal to insert
	 * @return
	 */
	public boolean insertMeal(Meal meal) {
		if (!isValidConnection() || meal == null) {
			return false;
		}
		PreparedStatement insertMeal = null;
		String insertString = "INSERT INTO CAMPUSMENUS (Title, Description, Restaurant, TotalRating, NumberOfVotes, MealId, stamp_created)"
				+ " VALUES (?,?,?,?,?,?,?)";

		try {
			String name = meal.getName();
			String description = meal.getMealDescription();
			String restaurant = meal.getRestaurant().getName();
			double totalRating = meal.getRating().getSumOfRatings();
			int numberOfVotes = meal.getRating().getNumberOfVotes();
			long mealId = meal.getMealId();

			// Get today's date
			Calendar cal = Calendar.getInstance();
			String dateString = cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ cal.get(Calendar.DAY_OF_MONTH);

			mConnection.setAutoCommit(false);

			insertMeal = mConnection.prepareStatement(insertString);

			// Insert values in corresponding fields
			insertMeal.setString(1, name);
			insertMeal.setString(2, description);
			insertMeal.setString(3, restaurant);
			insertMeal.setFloat(4, (float) totalRating);
			insertMeal.setInt(5, numberOfVotes);
			insertMeal.setLong(6, mealId);
			insertMeal.setString(7, dateString);

			// Insert meal in database
			insertMeal.execute();
			mConnection.commit();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("#Food Database: Problem in insert meal.");
			return false;
		} finally {
			try {
				if (insertMeal != null) {
					insertMeal.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Insert a list of Meals into the database.
	 * 
	 * @param mMeals
	 *            the list of Meals to be inserted
	 * @return the status of the insertion
	 */
	public boolean insertMeals(List<Meal> mMeals) {
		if (!isValidConnection() || mMeals == null || mMeals.isEmpty()) {
			return false;
		}
		PreparedStatement statement = null;
		try {
			String statementString = "INSERT INTO CAMPUSMENUS (Title, Description, Restaurant, TotalRating, NumberOfVotes, MealId, stamp_created)"
					+ " VALUES (?,?,?,?,?,?,?)";

			statement = mConnection.prepareStatement(statementString);
			for (Meal m : mMeals) {
				String name = m.getName();
				String description = m.getMealDescription();
				String restaurant = m.getRestaurant().getName();
				double totalRating = m.getRating().getSumOfRatings();
				int numberOfVotes = m.getRating().getNumberOfVotes();
				long mealId = m.getMealId();

				// Get today's date
				Calendar cal = Calendar.getInstance();
				String dateString = cal.get(Calendar.YEAR) + "."
						+ (cal.get(Calendar.MONTH) + 1) + "."
						+ cal.get(Calendar.DAY_OF_MONTH);

				// Insert values in corresponding fields
				statement.setString(1, name);
				statement.setString(2, description);
				statement.setString(3, restaurant);
				statement.setFloat(4, (float) totalRating);
				statement.setInt(5, numberOfVotes);
				statement.setLong(6, mealId);
				statement.setString(7, dateString);

				statement.addBatch();
			}
			statement.executeBatch();
			System.out.println("#Food Database: inserted meals");
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("#Food Database: Problem in insert meal.");
			return false;
		} finally {
			if (statement != null)
				try {
					statement.close();
				} catch (SQLException logOrIgnore) {
				}
		}
	}

	/**
	 * Get all meals for the day from the database.
	 * 
	 * @return the list of Meals
	 */
	public List<Meal> getMeals() {
		if (!isValidConnection()) {
			return null;
		}
		PreparedStatement getMeals = null;
		String getString = "SELECT * FROM CAMPUSMENUS WHERE STAMP_CREATED = ?";
		try {
			List<Meal> campusMeals = new ArrayList<Meal>();

			Calendar cal = Calendar.getInstance();
			String dateString = cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ (cal.get(Calendar.DAY_OF_MONTH));

			mConnection.setAutoCommit(false);

			getMeals = mConnection.prepareStatement(getString);
			getMeals.setString(1, dateString);
			ResultSet rset = getMeals.executeQuery();

			mConnection.commit();

			System.out.println("<getMeals>: getting " + dateString);

			String name = null;
			String description = null;
			String restaurant = null;
			double totalRating = 0;
			int numberOfVotes = 0;
			int mealId = 0;

			// Treat the answer from the database
			while (rset.next()) {
				name = rset.getString("Title");
				description = rset.getString("Description");
				restaurant = rset.getString("Restaurant");
				totalRating = rset.getFloat("TotalRating");
				numberOfVotes = rset.getInt("NumberOfVotes");
				mealId = rset.getInt("MealId");

				// Create a new meal from the info we got in the database
				Rating mealRating = new Rating(FoodUtils.totalRatingToRatingValue(
						totalRating, numberOfVotes), numberOfVotes, totalRating);
				Restaurant mealResto = new Restaurant(restaurant.hashCode(),
						restaurant);

				Meal gottenMeal = new Meal(mealId, name, description, mealResto,
						mealRating);

				campusMeals.add(gottenMeal);
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
				mConnection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Check if a device has already submitted a vote during the day.
	 * 
	 * @param deviceID
	 *            the ID of the device that needs to be checked
	 * @return
	 */
	public boolean checkVotedDevice(String deviceID) {
		if (!isValidConnection() || deviceID == null) {
			return false;
		}
		PreparedStatement checkVotedDevice = null;
		String getString = "SELECT count(DEVICEID) FROM DAILYRATINGS WHERE DEVICEID = ? and STAMP_CREATED = ?";
		ResultSet rset = null;
		boolean found = false;

		try {
			Calendar cal = Calendar.getInstance();
			String dateString = cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ cal.get(Calendar.DAY_OF_MONTH);

			checkVotedDevice = mConnection.prepareStatement(getString);
			mConnection.setAutoCommit(false);
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
				if (mConnection != null) {
					mConnection.setAutoCommit(true);
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
	 * @param deviceId
	 *            the ID to be inserted
	 */
	public void insertVotedDevice(String deviceId, long mealId, Double myRating) {
		if (!isValidConnection() || myRating == null) {
			return;
		}
		PreparedStatement insertVotedDevice = null;
		String insertString = "INSERT INTO DAILYRATINGS (DeviceId, stamp_created, Rating, MealId) VALUES (?, ?, ?, ?)";
		ResultSet rset = null;

		try {
			insertVotedDevice = mConnection.prepareStatement(insertString);
			int count = 0;

			Calendar cal = Calendar.getInstance();
			String dateString = cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ cal.get(Calendar.DAY_OF_MONTH);

			insertVotedDevice.setString(1, deviceId);
			insertVotedDevice.setString(2, dateString);
			insertVotedDevice.setDouble(3, myRating);
			insertVotedDevice.setLong(4, mealId);

			count = insertVotedDevice.executeUpdate();
			System.out.println("<Food> " + count + " rows were inserted");
		} catch (SQLException e) {
			System.out.println("<Food> Problem in insert voted device.");
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

	/**
	 * Insert a rating for a Meal
	 * 
	 * @param meal
	 *            the Meal for which the rating was submitted
	 */
	public void insertRating(Meal meal) {
		if (!isValidConnection() || meal == null) {
			return;
		}
		System.out.println("Inserting rating.");
		PreparedStatement insertRating = null;
		String insertString = "UPDATE CAMPUSMENUS SET TotalRating=?, NumberOfVotes=? where MealId=?";

		try {
			insertRating = mConnection.prepareStatement(insertString);

			Rating r = meal.getRating();
			insertRating.setFloat(1, (float) r.getSumOfRatings());
			insertRating.setInt(2, r.getNumberOfVotes());
			insertRating.setLong(3, meal.getMealId());

			System.out.println(insertRating);

			insertRating.executeUpdate();
		} catch (SQLException e) {
			System.out.println("Problem: could not insert rating:" + "Rating="
					+ meal.getRating().getRatingValue() + ", NumberOfVotes="
					+ meal.getRating().getNumberOfVotes() + "where MealId="
					+ meal.getMealId());
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

	/**
	 * Upload a picture to the database.
	 * 
	 * @param uploader
	 *            the ID of the device that uploaded the picture
	 * @param mealId
	 *            the ID of the Meal for which this picture was submitted
	 * @param picture
	 *            the Meal picture
	 * @return
	 */
	public boolean uploadPicture(String uploader, int mealId, byte[] picture) {
		if (!isValidConnection() || picture == null) {
			return false;
		}
		System.out.println("<Food> Inserting picture.");
		PreparedStatement uploadPicture = null;

		String insertString = "INSERT INTO Pictures (Picture, Uploader, MealId, stamp_created)"
				+ " VALUES (?,?,?,?)";

		Calendar cal = Calendar.getInstance();
		String dateString = cal.get(Calendar.YEAR) + "."
				+ (cal.get(Calendar.MONTH) + 1) + "."
				+ cal.get(Calendar.DAY_OF_MONTH);

		try {
			mConnection.setAutoCommit(false);
			uploadPicture = mConnection.prepareStatement(insertString);

			uploadPicture.setBytes(1, picture);
			uploadPicture.setString(2, uploader);
			uploadPicture.setInt(3, mealId);
			uploadPicture.setString(4, dateString);

			uploadPicture.executeUpdate();

			mConnection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			if (mConnection != null) {
				try {
					System.err.print("Transaction is being rolled back");
					mConnection.rollback();
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
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
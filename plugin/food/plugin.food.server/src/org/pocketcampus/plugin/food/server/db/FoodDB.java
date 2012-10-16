package org.pocketcampus.plugin.food.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.food.shared.Meal;
import org.pocketcampus.plugin.food.shared.Rating;
import org.pocketcampus.plugin.food.shared.Restaurant;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

/**
 * Class that handles interactions with the test database to store and retrieve
 * meals, user ids and ratings
 * 
 * @author Elodie <elodienilane.triponez@epfl.ch>
 * @author Oriane <oriane.rodriguez@epfl.ch>
 * 
 */
public class FoodDB {
	/** The connection to the database */
	private ConnectionManager mConnectionManager;

	/** The name of the table on the database */
	private final String MENUS_TABLE = "campusmenus";

	/**
	 * Constructor for the Food Database Handler
	 */
	public FoodDB() {
		try {
			this.mConnectionManager = new ConnectionManager(PC_SRV_CONFIG.getString("DB_URL"),
					PC_SRV_CONFIG.getString("DB_USERNAME"), PC_SRV_CONFIG.getString("DB_PASSWORD"));
		} catch (ServerException e) {
			e.printStackTrace();
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
		if (mMeals == null || mMeals.isEmpty()) {
			return false;
		}
		PreparedStatement statement = null;
		try {
			Connection dbConnection = mConnectionManager.getConnection();

			String statementString = "INSERT INTO campusmenus (Title, Description, Restaurant, TotalRating, NumberOfVotes, MealId, stamp_created)"
					+ " VALUES (?,?,?,?,?,?,?)";

			statement = dbConnection.prepareStatement(statementString);
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
		PreparedStatement getMeals = null;
		String getString = "SELECT * FROM " + MENUS_TABLE
				+ " WHERE stamp_created = ?";
		Connection dbConnection = null;
		try {
			dbConnection = mConnectionManager.getConnection();

			List<Meal> campusMeals = new ArrayList<Meal>();

			Calendar cal = Calendar.getInstance();
			String dateString = cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ (cal.get(Calendar.DAY_OF_MONTH));

			dbConnection.setAutoCommit(false);

			getMeals = dbConnection.prepareStatement(getString);
			getMeals.setString(1, dateString);
			ResultSet rset = getMeals.executeQuery();

			dbConnection.commit();

			System.out.println("<getMeals>: getting " + dateString);

			String name = null;
			String description = null;
			String restaurant = null;
			double totalRating = 0;
			int numberOfVotes = 0;
			long mealId = 0;

			// Treat the answer from the database
			while (rset.next()) {
				name = rset.getString("Title");
				description = rset.getString("Description");
				restaurant = rset.getString("Restaurant");
				totalRating = rset.getFloat("TotalRating");
				numberOfVotes = rset.getInt("NumberOfVotes");
				mealId = rset.getLong("MealId");

				// Create a new meal from the info we got in the database
				Rating mealRating = new Rating(
						FoodUtils.totalRatingToRatingValue(totalRating,
								numberOfVotes), numberOfVotes, totalRating);
				Restaurant mealResto = new Restaurant(restaurant.hashCode(),
						restaurant);

				Meal gottenMeal = new Meal(mealId, name, description,
						mealResto, mealRating);

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
				if (dbConnection != null) {
					dbConnection.setAutoCommit(true);
				}
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
	 * @return whether the device with the given ID has already voted today.
	 */
	public boolean checkVotedDevice(String deviceID) {
		if (deviceID == null) {
			return false;
		}
		Connection dbConnection = null;
		PreparedStatement checkVotedDevice = null;
		String getString = "SELECT count(DEVICEID) FROM dailyratings WHERE DeviceId = ? and stamp_created = ?";
		ResultSet rset = null;

		boolean found = false;

		try {
			dbConnection = mConnectionManager.getConnection();
			Calendar cal = Calendar.getInstance();
			String dateString = cal.get(Calendar.YEAR) + "."
					+ (cal.get(Calendar.MONTH) + 1) + "."
					+ cal.get(Calendar.DAY_OF_MONTH);

			checkVotedDevice = dbConnection.prepareStatement(getString);
			dbConnection.setAutoCommit(false);
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
				if (dbConnection != null) {
					dbConnection.setAutoCommit(true);
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
		if (myRating == null) {
			return;
		}
		Connection dbConnection = null;
		PreparedStatement insertVotedDevice = null;
		String insertString = "INSERT INTO dailyratings (DeviceId, stamp_created, Rating, MealId) VALUES (?, ?, ?, ?)";
		ResultSet rset = null;

		try {
			dbConnection = mConnectionManager.getConnection();
			insertVotedDevice = dbConnection.prepareStatement(insertString);
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
		if (meal == null) {
			return;
		}
		
		Connection dbConnection = null;
		PreparedStatement insertRating = null;
		String insertString = "UPDATE " + MENUS_TABLE
				+ " SET TotalRating=?, NumberOfVotes=? where MealId=?";

		try {
			dbConnection = mConnectionManager.getConnection();
			insertRating = dbConnection.prepareStatement(insertString);

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
		if (picture == null) {
			return false;
		}

		Connection dbConnection = null;
		PreparedStatement uploadPicture = null;

		String insertString = "INSERT INTO Pictures (Picture, Uploader, MealId, stamp_created)"
				+ " VALUES (?,?,?,?)";

		Calendar cal = Calendar.getInstance();
		String dateString = cal.get(Calendar.YEAR) + "."
				+ (cal.get(Calendar.MONTH) + 1) + "."
				+ cal.get(Calendar.DAY_OF_MONTH);

		try {
			dbConnection = mConnectionManager.getConnection();
			dbConnection.setAutoCommit(false);
			uploadPicture = dbConnection.prepareStatement(insertString);

			uploadPicture.setBytes(1, picture);
			uploadPicture.setString(2, uploader);
			uploadPicture.setInt(3, mealId);
			uploadPicture.setString(4, dateString);

			uploadPicture.executeUpdate();

			dbConnection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			if (dbConnection != null) {
				try {
					System.err.print("Transaction is being rolled back");
					dbConnection.rollback();
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
	
	/**
	 * Get strings that should not be capitalized.
	 * 
	 * @return the list of strings that should not be capitalized
	 */
	public List<String> getNotCapitalized() {
		List<String> notCapitalized = new ArrayList<String>();
		
		try {
			Connection dbConnection = mConnectionManager.getConnection();
			Statement statement = dbConnection.createStatement();
			ResultSet rs = statement.executeQuery("select * from not_capitalized");

			while (rs.next()) {
				notCapitalized.add(rs.getString("not_capitalized"));
			}

			statement.close();
			mConnectionManager.disconnect();
			
		} catch (SQLException e) {
			System.err.println("Error with SQL");
			e.printStackTrace();
		}
		
		return notCapitalized;
	}

}
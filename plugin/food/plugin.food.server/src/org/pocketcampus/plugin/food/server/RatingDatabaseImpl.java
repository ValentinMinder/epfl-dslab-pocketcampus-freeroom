package org.pocketcampus.plugin.food.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.sql.*;
import java.util.List;

import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.food.shared.*;

/**
 * The database holding the ratings.
 * TODO: Review this code; I've never written SQL or JDBC stuff before.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class RatingDatabaseImpl implements RatingDatabase {
	private ConnectionManager _connectionManager;

	public RatingDatabaseImpl() {
		try {
			_connectionManager = new ConnectionManager(PC_SRV_CONFIG.getString("DB_URL"), PC_SRV_CONFIG.getString("DB_USERNAME"), PC_SRV_CONFIG.getString("DB_PASSWORD"));
		} catch (ServerException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void insert(List<EpflRestaurant> menu) {
		try {
			Connection connection = _connectionManager.getConnection();
			String insertCommand = "INSERT INTO Meals VALUES (?, ?) " +
					"ON DUPLICATE KEY UPDATE MealId = MealId"; // i.e. do nothing

			for (EpflRestaurant restaurant : menu) {
				PreparedStatement statement = null;
				try {
					statement = connection.prepareStatement(insertCommand);

					for (EpflMeal meal : restaurant.getRMeals()) {
						statement.setLong(1, meal.getMId());
						statement.setLong(2, restaurant.getRId());
						statement.addBatch();
					}

					statement.executeBatch();
				} finally {
					if (statement != null) {
						statement.close();
					}
				}
			}
		} catch (Exception e) {
			// TODO: What to do?
			e.printStackTrace();
		}
	}

	@Override
	public void vote(long mealId, double rating) {
		PreparedStatement statement = null;
		try {
			try {
				Connection connection = _connectionManager.getConnection();
				String command = "INSERT INTO MealRatings VALUES (?, ?, 1) " +
						"ON DUPLICATE KEY UPDATE RatingTotal = RatingTotal + ?, RatingCount = RatingCount + 1";

				statement = connection.prepareStatement(command);
				statement.setLong(1, mealId);
				statement.setDouble(2, rating);
				statement.setDouble(3, rating);
				statement.executeUpdate();
			} finally {
				if (statement != null) {
					statement.close();
				}
			}
		} catch (Exception e) {
			// TODO: What to do?
			e.printStackTrace();
		}
	}

	@Override
	public void setRatings(List<EpflRestaurant> menu) {
		try {
			Connection connection = _connectionManager.getConnection();

			for (EpflRestaurant restaurant : menu) {

				for (EpflMeal meal : restaurant.getRMeals()) {
					PreparedStatement mealQuery = null;
					try {
						String query = "SELECT RatingTotal / RatingCount, RatingCount " +
								"FROM MealRatings " +
								"WHERE MealId = ?";

						mealQuery = connection.prepareStatement(query);
						mealQuery.setLong(1, meal.getMId());
						mealQuery.setLong(2, restaurant.getRId());

						ResultSet result = mealQuery.executeQuery();
						meal.setMRating(new EpflRating(result.getDouble(1), result.getInt(2)));
					} finally {
						mealQuery.close();
					}
				}

				PreparedStatement restaurantQuery = null;
				try {
					String query = "SELECT SUM(RatingTotal) / SUM(RatingCount), SUM(RatingCount) " +
							"FROM MealRatings JOIN Meals ON MealRatings.MealId = Meals.MealId " +
							"WHERE RestaurantId = ?";

					restaurantQuery = connection.prepareStatement(query);
					restaurantQuery.setLong(0, restaurant.getRId());

					ResultSet result = restaurantQuery.executeQuery();
					restaurant.setRRating(new EpflRating(result.getDouble(1), result.getInt(2)));
				} finally {
					restaurantQuery.close();
				}
			}
		} catch (Exception e) {
			// TODO: What to do?
			e.printStackTrace();
		}
	}
}
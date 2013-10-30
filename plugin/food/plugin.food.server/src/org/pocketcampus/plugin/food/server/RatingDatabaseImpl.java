package org.pocketcampus.plugin.food.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.sql.*;
import java.util.List;

import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.food.shared.*;

/**
 * The database holding the ratings.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class RatingDatabaseImpl implements RatingDatabase {
	private ConnectionManager _connectionManager;

	public RatingDatabaseImpl() {
		this(PC_SRV_CONFIG.getString("DB_URL"), PC_SRV_CONFIG.getString("DB_USERNAME"), PC_SRV_CONFIG.getString("DB_PASSWORD"));
	}

	public RatingDatabaseImpl(String databaseUrl, String userName, String password) {
		try {
			_connectionManager = new ConnectionManager(databaseUrl, userName, password);
		} catch (ServerException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void insert(List<EpflRestaurant> menu) {
		try {
			Connection connection = _connectionManager.getConnection();
			String insertCommand = "REPLACE INTO meals (MealId, RestaurantId) VALUES (?, ?)";

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
				String command = "INSERT INTO mealratings (MealId, RatingTotal, RatingCount) VALUES (?, ?, 1) " +
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
								"FROM mealratings " +
								"WHERE MealId = ?";

						mealQuery = connection.prepareStatement(query);
						mealQuery.setLong(1, meal.getMId());

						ResultSet result = mealQuery.executeQuery();
						if (result.next()) {
							meal.setMRating(new EpflRating(result.getDouble(1), result.getInt(2)));
						} else {
							meal.setMRating(new EpflRating(0.0, 0));
						}
					} finally {
						mealQuery.close();
					}
				}

				PreparedStatement restaurantQuery = null;
				try {
					String query = "SELECT SUM(RatingTotal) / SUM(RatingCount), SUM(RatingCount) " +
							"FROM mealratings INNER JOIN meals ON mealratings.MealId = meals.MealId " +
							"WHERE RestaurantId = ?";

					restaurantQuery = connection.prepareStatement(query);
					restaurantQuery.setLong(1, restaurant.getRId());

					ResultSet result = restaurantQuery.executeQuery();
					if (result.next()) {
						restaurant.setRRating(new EpflRating(result.getDouble(1), result.getInt(2)));
					} else {
						restaurant.setRRating(new EpflRating(0.0, 0));
					}
				} finally {
					restaurantQuery.close();
				}
			}
		} catch (Exception e) {
			// TODO: What to do?
			e.printStackTrace();
		}
	}

	/** Cleans the databases. Not part of the RatingDatabase interface, but used for unit tests. */
	public void clean() {
		try {
			Connection connection = _connectionManager.getConnection();
			String[] deleteCommands = new String[] { "TRUNCATE TABLE meals", "TRUNCATE TABLE mealratings" };

			for (String command : deleteCommands) {
				PreparedStatement statement = null;

				try {
					statement = connection.prepareStatement(command);
					statement.execute();
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
}
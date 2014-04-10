package org.pocketcampus.plugin.food.server;

import static org.pocketcampus.platform.launcher.server.PCServerConfig.PC_SRV_CONFIG;

import java.sql.*;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.pocketcampus.platform.sdk.server.database.ConnectionManager;
import org.pocketcampus.platform.sdk.server.database.handlers.exceptions.ServerException;
import org.pocketcampus.plugin.food.shared.*;

/**
 * The database holding the ratings.
 * 
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class RatingDatabaseImpl implements RatingDatabase {
	private final ConnectionManager _connectionManager;
	private final Days _maxVotingDaysInPast;

	public RatingDatabaseImpl(Days maxVotingDaysInPast) {
		this(PC_SRV_CONFIG.getString("DB_URL"), PC_SRV_CONFIG.getString("DB_USERNAME"), PC_SRV_CONFIG.getString("DB_PASSWORD"), maxVotingDaysInPast);
	}

	public RatingDatabaseImpl(String databaseUrl, String userName, String password, Days maxVotingDaysInPast) {
		try {
			_connectionManager = new ConnectionManager(databaseUrl, userName, password);
			_maxVotingDaysInPast = maxVotingDaysInPast;
		} catch (ServerException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void insertMenu(List<EpflRestaurant> menu, LocalDate date, MealTime time) throws Exception {
		Connection connection = _connectionManager.getConnection();

		for (EpflRestaurant restaurant : menu) {
			PreparedStatement mealStatement = null;
			try {
				String mealCommand = "REPLACE INTO meals (Id, Name, RestaurantId, TimeIndependentId, Date, Time) VALUES (?, ?, ?, ?, ?, ?)";
				mealStatement = connection.prepareStatement(mealCommand);

				for (EpflMeal meal : restaurant.getRMeals()) {
					mealStatement.setLong(1, meal.getMId());
					mealStatement.setString(2, meal.getMName());
					mealStatement.setLong(3, restaurant.getRId());
					mealStatement.setLong(4, getTimeIndependentId(meal));
					mealStatement.setDate(5, new Date(date.toDate().getTime()));
					mealStatement.setString(6, time.name());
					mealStatement.addBatch();
				}

				mealStatement.executeBatch();
			} finally {
				if (mealStatement != null) {
					mealStatement.close();
				}
			}
		}
	}

	@Override
	public SubmitStatus vote(String deviceId, long mealId, double rating) throws Exception {
		Connection connection = _connectionManager.getConnection();

		LocalDate date;
		MealTime time;
		long timeIndependentId;
		PreparedStatement getDateTimeStatement = null;
		try {
			String getDateTimeCommand = "SELECT Date, Time, TimeIndependentId FROM meals WHERE Id = ?";

			getDateTimeStatement = connection.prepareStatement(getDateTimeCommand);
			getDateTimeStatement.setLong(1, mealId);

			ResultSet results = getDateTimeStatement.executeQuery();
			results.next();
			date = new LocalDate( results.getDate(1));
			time = MealTime.valueOf(results.getString(2));
			timeIndependentId = results.getLong(3);
		} finally {
			if (getDateTimeStatement != null) {
				getDateTimeStatement.close();
			}
		}
		
		if(date.isAfter(LocalDate.now())){
			return SubmitStatus.MEAL_IN_FUTURE;
		}
		if(Days.daysBetween(date, LocalDate.now()).isGreaterThan(_maxVotingDaysInPast)){
			return SubmitStatus.MEAL_IN_DISTANT_PAST;
		}

		PreparedStatement checkStatement = null;
		try {
			String checkCommand = "SELECT * " +
					"FROM mealratings INNER JOIN meals ON meals.TimeIndependentId = mealratings.MealTimeIndependentId " +
					"WHERE Date = ? AND Time = ? AND DeviceId = ?";

			checkStatement = connection.prepareStatement(checkCommand);
			checkStatement.setDate(1, new Date(date.toDate().getTime()));
			checkStatement.setInt(2, time.getValue());
			checkStatement.setString(3, deviceId);

			if (checkStatement.executeQuery().next()) { // there is a result
				return SubmitStatus.ALREADY_VOTED;
			}
		} finally {
			if (checkStatement != null) {
				checkStatement.close();
			}
		}

		PreparedStatement voteStatement = null;
		try {
			String command = "INSERT INTO mealratings (DeviceId, MealTimeIndependentId, Rating) VALUES (?, ?, ?)";

			voteStatement = connection.prepareStatement(command);
			voteStatement.setString(1, deviceId);
			voteStatement.setLong(2, timeIndependentId);
			voteStatement.setDouble(3, rating);
			voteStatement.executeUpdate();
		} finally {
			if (voteStatement != null) {
				voteStatement.close();
			}
		}

		return SubmitStatus.VALID;
	}

	@Override
	public void setRatings(List<EpflRestaurant> menu) throws Exception {
		Connection connection = _connectionManager.getConnection();

		for (EpflRestaurant restaurant : menu) {
			for (EpflMeal meal : restaurant.getRMeals()) {
				PreparedStatement mealQuery = null;
				try {
					String query = "SELECT SUM(Rating) / COUNT(*), COUNT(*) FROM mealratings WHERE MealTimeIndependentId = ?";

					mealQuery = connection.prepareStatement(query);
					mealQuery.setLong(1, getTimeIndependentId(meal));

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
				String query = "SELECT SUM(Rating) / COUNT(*), COUNT(*) " +
						"FROM mealratings INNER JOIN meals ON mealratings.MealTimeIndependentId = meals.TimeIndependentId " +
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
			e.printStackTrace();
		}
	}

	private static long getTimeIndependentId(EpflMeal meal) {
		final int prime = 31;
		int result = 1;
		// TODO: Anything else?
		result = prime * result + meal.getMName().hashCode();
		result = prime * result + meal.getMDescription().hashCode();
		return result;
	}
}
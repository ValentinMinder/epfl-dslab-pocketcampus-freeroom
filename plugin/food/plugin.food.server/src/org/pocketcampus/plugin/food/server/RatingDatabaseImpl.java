package org.pocketcampus.plugin.food.server;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.pocketcampus.platform.server.database.ConnectionManager;
import org.pocketcampus.platform.server.launcher.PocketCampusServer;
import org.pocketcampus.plugin.food.shared.*;

import java.sql.*;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The database holding the ratings.
 *
 * @author Solal Pirelli <solal@pocketcampus.org>
 */
public final class RatingDatabaseImpl implements RatingDatabase {
    private static final Map<MealTime, LocalTime> VOTING_MIN_HOURS;

    static {
        VOTING_MIN_HOURS = new HashMap<>();
        VOTING_MIN_HOURS.put(MealTime.LUNCH, new LocalTime(11, 00, 00));
        VOTING_MIN_HOURS.put(MealTime.DINNER, new LocalTime(18, 00, 00));
    }

    private final ConnectionManager _connectionManager;
    private final Days _maxVotingDaysInPast;

    public RatingDatabaseImpl(Days maxVotingDaysInPast) {
        this(PocketCampusServer.CONFIG.getString("DB_URL"), PocketCampusServer.CONFIG.getString("DB_USERNAME"),
                PocketCampusServer.CONFIG.getString("DB_PASSWORD"), maxVotingDaysInPast);
    }

    public RatingDatabaseImpl(String databaseUrl, String userName, String password, Days maxVotingDaysInPast) {
        _connectionManager = new ConnectionManager(databaseUrl, userName, password);
        _maxVotingDaysInPast = maxVotingDaysInPast;
    }

    @Override
    public void insertMenu(List<EpflRestaurant> menu, LocalDate date, MealTime time) throws SQLException {
        Connection connection = _connectionManager.getConnection();

        PreparedStatement restaurantStatement = null;
        PreparedStatement mealStatement = null;
        try {
            String restaurantCommand = "REPLACE INTO restaurants (Id, Name) VALUES (?, ?)";
            restaurantStatement = connection.prepareStatement(restaurantCommand);
            String mealCommand = "REPLACE INTO meals (Id, Name, Description, RestaurantId, TimeIndependentId, Date, Time) VALUES (?, ?, ?, ?, ?, ?, ?)";
            mealStatement = connection.prepareStatement(mealCommand);

            for (EpflRestaurant restaurant : menu) {
                restaurantStatement.setLong(1, restaurant.getRId());
                restaurantStatement.setString(2, restaurant.getRName());
                restaurantStatement.addBatch();

                for (EpflMeal meal : restaurant.getRMeals()) {
                    mealStatement.setLong(1, meal.getMId());
                    mealStatement.setString(2, meal.getMName());
                    mealStatement.setString(3, meal.getMDescription());
                    mealStatement.setLong(4, restaurant.getRId());
                    mealStatement.setLong(5, getTimeIndependentId(meal));
                    mealStatement.setDate(6, new Date(date.toDate().getTime()));
                    mealStatement.setString(7, time.name());
                    mealStatement.addBatch();
                }
            }

            restaurantStatement.executeBatch();
            mealStatement.executeBatch();
        } finally {
            if (restaurantStatement != null) {
                restaurantStatement.close();
            }
            if (mealStatement != null) {
                mealStatement.close();
            }
        }
    }

    @Override
    public SubmitStatus vote(String deviceId, long mealId, double rating) throws SQLException {
        Connection connection = _connectionManager.getConnection();

        LocalDate date;
        MealTime time;
        PreparedStatement getDateTimeStatement = null;
        try {
            String getDateTimeCommand = "SELECT Date, Time FROM meals WHERE Id = ?";

            getDateTimeStatement = connection.prepareStatement(getDateTimeCommand);
            getDateTimeStatement.setLong(1, mealId);

            ResultSet results = getDateTimeStatement.executeQuery();
            results.next();
            date = new LocalDate(results.getDate(1));
            time = MealTime.valueOf(results.getString(2));
        } finally {
            if (getDateTimeStatement != null) {
                getDateTimeStatement.close();
            }
        }

        if (date.isAfter(LocalDate.now())) {
            return SubmitStatus.TOO_EARLY;
        }
        if (Days.daysBetween(date, LocalDate.now()).isGreaterThan(_maxVotingDaysInPast)) {
            return SubmitStatus.MEAL_IN_DISTANT_PAST;
        }
        if (date.equals(LocalDate.now()) && LocalTime.now().isBefore(VOTING_MIN_HOURS.get(time))) {
            return SubmitStatus.TOO_EARLY;
        }

        PreparedStatement checkStatement = null;
        try {
            String checkCommand = "SELECT * " +
                    "FROM mealratings INNER JOIN meals ON meals.Id = mealratings.MealId " +
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
            String command = "INSERT INTO mealratings (DeviceId, MealId, Rating) VALUES (?, ?, ?)";

            voteStatement = connection.prepareStatement(command);
            voteStatement.setString(1, deviceId);
            voteStatement.setLong(2, mealId);
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
    public void setRatings(List<EpflRestaurant> menu, LocalDate date, MealTime time) throws SQLException {
        Connection connection = _connectionManager.getConnection();

        Map<Long, EpflRating> mealRatings = new HashMap<>();
        String mealQueryString = "SELECT TimeIndependentId, AVG(Rating), COUNT(*) " +
                "FROM mealratings INNER JOIN meals ON mealratings.MealId = meals.Id " +
                "WHERE TimeIndependentId IN (SELECT m.TimeIndependentId " +
                "FROM meals m WHERE m.`Time` = ? AND m.`Date` = ?) " +
                "GROUP BY TimeIndependentId ";
        try (PreparedStatement mealQuery = connection.prepareStatement(mealQueryString)) {
        	mealQuery.setString(1, time.name());
        	mealQuery.setDate(2, new Date(date.toDate().getTime()));
            ResultSet result = mealQuery.executeQuery();
            while(result.next()) {
                mealRatings.put(result.getLong(1), new EpflRating(result.getDouble(2), result.getInt(3)));
            }
        }
        
        Map<Long, EpflRating> restoRatings = new HashMap<>();
        String restaurantQueryString = "SELECT RestaurantId, AVG(Rating), COUNT(*) " +
                "FROM mealratings INNER JOIN meals ON mealratings.MealId = meals.Id " +
                "GROUP BY RestaurantId ";
        try (PreparedStatement restaurantQuery = connection.prepareStatement(restaurantQueryString)) {
            ResultSet result = restaurantQuery.executeQuery();
            while(result.next()) {
            	restoRatings.put(result.getLong(1), new EpflRating(result.getDouble(2), result.getInt(3)));
            }
        }
        
        for (EpflRestaurant restaurant : menu) {
            for (EpflMeal meal : restaurant.getRMeals()) {
            	EpflRating mealRating = mealRatings.get(getTimeIndependentId(meal));
                if (mealRating != null) {
                    meal.setMRating(mealRating);
                } else {
                    meal.setMRating(new EpflRating(0.0, 0));
                }
            }

        	EpflRating restoRating = restoRatings.get(restaurant.getRId());
            if (restoRating != null) {
                restaurant.setRRating(restoRating);
            } else {
                restaurant.setRRating(new EpflRating(0.0, 0));
            }
        }
    }

    /**
     * Cleans the databases. Not part of the RatingDatabase interface, but used for unit tests.
     */
    public void clean() {
        try {
            Connection connection = _connectionManager.getConnection();
            String[] deleteCommands = new String[]{"TRUNCATE TABLE meals", "TRUNCATE TABLE restaurants", "TRUNCATE TABLE mealratings"};

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static long getTimeIndependentId(EpflMeal meal) {
        final int prime = 31;
        int result = 1;
        // TODO: Anything else?
        result = prime * result + normalize(meal.getMName()).hashCode();
        result = prime * result + normalize(meal.getMDescription()).hashCode();
        return result;
    }

    private static String normalize(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+", "");
        return s.replaceAll("\\W", "").toLowerCase();
    }
}

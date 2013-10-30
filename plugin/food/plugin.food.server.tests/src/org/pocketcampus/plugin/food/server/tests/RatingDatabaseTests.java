package org.pocketcampus.plugin.food.server.tests;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

import org.pocketcampus.plugin.food.server.RatingDatabaseImpl;
import org.pocketcampus.plugin.food.shared.*;

/**
 * Tests for RatingDatabaseImpl.
 * Don't forget to have a MySQL instance running and to change the DB_ constants if you want to run them.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class RatingDatabaseTests {
	// change this to whatever you're using (just don't use the production database)
	private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/pocketcampus_test", DB_USERNAME = "root", DB_PASSWORD = "MySQL";

	private RatingDatabaseImpl _database;

	@Before
	public void beforeTest() {
		_database = new RatingDatabaseImpl(DB_URL, DB_USERNAME, DB_PASSWORD);
	}

	@After
	public void afterTest() {
		_database.clean();
	}

	// Inserting the menu works
	@Test
	public void insertWorks() {
		_database.insert(getTestMenu());
	}

	// Duplicate menu insertions work
	@Test
	public void insertDuplicateWorks() {
		_database.insert(getTestMenu());
		_database.insert(getTestMenu());
	}

	// Voting works
	@Test
	public void voteWorks() {
		_database.insert(getTestMenu());
		_database.vote(101, 4.0);
	}

	// Voting multiple times works
	@Test
	public void multipleVoteWorks() {
		_database.insert(getTestMenu());
		_database.vote(101, 4.0);
		_database.vote(101, 2.0);
	}

	// Restaurant votes are fetched correctly
	@Test
	public void restaurantVotesAreSet() {
		List<EpflRestaurant> menu = getTestMenu();

		_database.insert(menu);
		_database.vote(101, 4.0);
		_database.vote(101, 2.0);
		_database.vote(102, 2.0);
		_database.setRatings(menu);

		assertEquals(new EpflRating(8.0 / 3.0, 3), menu.get(0).getRRating());
	}

	// Meal votes are fetched correctly
	@Test
	public void mealVotesAreSet() {
		List<EpflRestaurant> menu = getTestMenu();

		_database.insert(menu);
		_database.vote(101, 4.0);
		_database.vote(101, 2.0);
		_database.vote(102, 2.0);
		_database.setRatings(menu);

		assertEquals(new EpflRating(3.0, 2), menu.get(0).getRMeals().get(0).getMRating());
	}

	private static List<EpflRestaurant> getTestMenu() {
		return Arrays.asList(new EpflRestaurant[] {
				new EpflRestaurant(100, "R1", Arrays.asList(new EpflMeal[] {
						makeMeal(101),
						makeMeal(102)
				}), new EpflRating(0.0, 0)),
				new EpflRestaurant(200, "R2", Arrays.asList(new EpflMeal[] {
						makeMeal(201)
				}), new EpflRating(0.0, 0))
		});
	}

	private static EpflMeal makeMeal(long id) {
		return new EpflMeal(id, "M" + id, "D" + id, new HashMap<PriceTarget, Double>(), Arrays.asList(new MealType[0]), null).setMRating(new EpflRating(0.0, 0));
	}
}
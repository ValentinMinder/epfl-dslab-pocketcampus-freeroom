package org.pocketcampus.plugin.food.server.tests;

import static org.junit.Assert.*;

import org.joda.time.Days;
import org.joda.time.LocalDate;
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
		_database = new RatingDatabaseImpl(DB_URL, DB_USERNAME, DB_PASSWORD, Days.days(5));
	}

	@After
	public void afterTest() {
		_database.clean();
	}

	// Inserting the menu works
	@Test
	@Ignore
	public void insertMenu() throws Exception {
		_database.insertMenu(getTestMenu(0), LocalDate.now(), MealTime.LUNCH);
	}

	// Duplicate menu insertions work
	@Test
	@Ignore
	public void insertDuplicateMenu() throws Exception {
		LocalDate now = LocalDate.now();
		_database.insertMenu(getTestMenu(0), now, MealTime.LUNCH);
		_database.insertMenu(getTestMenu(0), now, MealTime.LUNCH);
	}

	// Voting works
	@Test
	@Ignore
	public void oneVote() throws Exception {
		_database.insertMenu(getTestMenu(0), LocalDate.now(), MealTime.LUNCH);

		assertEquals(SubmitStatus.VALID, _database.vote("A", 0, 4.0));
	}
	
	// Vote for meal in the future is refused
	@Test
	@Ignore
	public void voteForFutureMeal() throws Exception {
		_database.insertMenu(getTestMenu(0), LocalDate.now().plusDays(1), MealTime.LUNCH);

		assertEquals(SubmitStatus.TOO_EARLY, _database.vote("A", 0, 4.0));
	}
	
	// Vote for meal in distant past is refused
	@Test
	@Ignore
	public void voteForVeryOldMeal() throws Exception {
		_database.insertMenu(getTestMenu(0), LocalDate.now().plusDays(-200), MealTime.LUNCH);

		assertEquals(SubmitStatus.MEAL_IN_DISTANT_PAST, _database.vote("A", 0, 4.0));
	}

	// Different devices each voting once works
	@Test
	@Ignore
	public void differentDevicesVoting() throws Exception {
		_database.insertMenu(getTestMenu(0), LocalDate.now(), MealTime.LUNCH);

		_database.vote("A", 0, 4.0);
		assertEquals(SubmitStatus.VALID, _database.vote("B", 0, 2.0));
	}

	// Different devices each voting once works
	@Test
	@Ignore
	public void sameDeviceVotingForSameDateTimeIsRefused() throws Exception {
		_database.insertMenu(getTestMenu(0), LocalDate.now(), MealTime.LUNCH);

		_database.vote("A", 0, 4.0);
		assertEquals(SubmitStatus.ALREADY_VOTED, _database.vote("A", 1, 2.0));
	}

	// Different devices each voting once works
	@Test
	@Ignore
	public void sameDeviceVotingForDifferentTime() throws Exception {
		LocalDate now = LocalDate.now();
		_database.insertMenu(getTestMenu(0), now, MealTime.LUNCH);
		_database.insertMenu(getTestMenu(1000), now, MealTime.DINNER);

		_database.vote("A", 0, 4.0);
		assertEquals(SubmitStatus.VALID, _database.vote("A", 1000, 2.0));
	}

	// Different devices each voting once works
	@Test
	@Ignore
	public void sameDeviceVotingForDifferentDate() throws Exception {
		LocalDate now = LocalDate.now();
		_database.insertMenu(getTestMenu(0), now, MealTime.LUNCH);
		_database.insertMenu(getTestMenu(1000), now.minusDays(1), MealTime.LUNCH);

		_database.vote("A", 0, 4.0);
		assertEquals(SubmitStatus.VALID, _database.vote("A", 1000, 2.0));
	}

	// Restaurant votes are fetched correctly
	@Test
	@Ignore
	public void restaurantVotesAreSet() throws Exception {
		List<EpflRestaurant> menu = getTestMenu(0);

		_database.insertMenu(menu, LocalDate.now(), MealTime.LUNCH);
		_database.vote("A", 0, 4.0);
		_database.vote("B", 0, 2.0);
		_database.vote("C", 1, 2.0);
		_database.setRatings(menu);

		assertEquals(new EpflRating(8.0 / 3.0, 3), menu.get(0).getRRating());
	}

	// Meal votes are fetched correctly
	@Test
	@Ignore
	public void mealVotesAreSet() throws Exception {
		List<EpflRestaurant> menu = getTestMenu(0);

		_database.insertMenu(menu, LocalDate.now(), MealTime.LUNCH);
		_database.vote("A", 0, 4.0);
		_database.vote("B", 0, 2.0);
		_database.vote("C", 1, 2.0);
		_database.setRatings(menu);

		assertEquals(new EpflRating(3.0, 2), menu.get(0).getRMeals().get(0).getMRating());
	}

	private static List<EpflRestaurant> getTestMenu(long baseId) {
		return Arrays.asList(new EpflRestaurant[] {
				new EpflRestaurant(baseId, "R1", Arrays.asList(new EpflMeal[] {
						makeMeal(baseId),
						makeMeal(baseId + 1)
				}), new EpflRating(0.0, 0)),
				new EpflRestaurant(baseId + 1, "R2", Arrays.asList(new EpflMeal[] {
						makeMeal(baseId + 3)
				}), new EpflRating(0.0, 0))
		});
	}

	private static EpflMeal makeMeal(long id) {
		return new EpflMeal(id, "M" + id, "D" + id, new HashMap<PriceTarget, Double>(), Arrays.asList(new MealType[0]), null).setMRating(new EpflRating(0.0, 0));
	}
}
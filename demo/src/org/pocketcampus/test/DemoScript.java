package org.pocketcampus.test;

import java.util.Arrays;

import junit.framework.Assert;

import org.pocketcampus.plugin.bikes.android.BikesMainView;
import org.pocketcampus.plugin.dashboard.android.DashboardView;
import org.pocketcampus.plugin.directory.android.DirectorySearchView;
import org.pocketcampus.plugin.food.android.FoodMainView;
import org.pocketcampus.plugin.food.android.FoodPreferencesView;
import org.pocketcampus.plugin.food.android.FoodSuggestionsView;
import org.pocketcampus.plugin.map.android.MapMainView;
import org.pocketcampus.plugin.news.android.NewsMainView;
import org.pocketcampus.plugin.transport.android.TransportMainView;

import android.app.Activity;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;

import com.jayway.android.robotium.solo.Solo;

public class DemoScript extends ActivityInstrumentationTestCase2<DashboardView> {

	private Solo solo;
	private int width = 720;
	private int height = 1100;

	public DemoScript() {
		super("org.pocketcampus", DashboardView.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	private boolean clickAndStartActivity(String text,
			Class<? extends Activity> activity) {
		if (!solo.waitForText(text, 1, 5000)) {
			System.out.println("Cannot find " + text);
			return false;
		}
		solo.clickOnText(text);
		solo.waitForActivity(activity.getClass().getName(), 5000);
		if (solo.getCurrentActivity().getClass() != activity) {
			System.out.println("Expecting " + activity.getName()
					+ " instead got "
					+ solo.getCurrentActivity().getClass().getName());
			return false;
		}
		return true;
	}

	private void showBikes() {
		try {
			Thread.sleep(1000);
			if (!clickAndStartActivity("Bikes", BikesMainView.class)) {
				Assert.fail("did not succeed in starting Bikes");
			}
			Thread.sleep(3000);
			if (!solo.searchText("Turing")) {
				System.out.println("Cannot find Turing");
				return;
			}
			solo.clickOnText("Turing");
			Thread.sleep(6000);
			solo.goBack();
			Thread.sleep(1000);
			solo.goBack();

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private void showDirectory() {
		try {
			Thread.sleep(1000);
			if (!clickAndStartActivity("Directory", DirectorySearchView.class)) {
				Assert.fail("did not succeed in starting Directory");
			}
			String text = "Florian ";
			for (int i = 0; i < text.length(); i++) {
				char ch = text.charAt(i);
				solo.enterText(0, "" + ch);
				Thread.sleep(500);
			}
			String fullName = "Florian Laurent";
			if (!solo.searchText(fullName)) {
				Assert.fail("did not succeed in finding Florian");
			}
			Thread.sleep(1000);
			solo.clickOnText(fullName);
			Thread.sleep(5000);
			solo.goBack();

			solo.goBack();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private final int expandX = 586;
	private final int expandY = 100;

	private final int starX = 678;
	private final int starY = expandY;

	private final int restaurantX = starX;
	private final int restaurantY = starY;

	private final int fiveStarX = 612;
	private final int fiveStarY = 533;

	private void showFood() {
		try {
			if (!clickAndStartActivity("Food", FoodMainView.class)) {
				Assert.fail("did not succeed in starting Food");
			}

			solo.clickOnScreen(expandX, expandY);
			Thread.sleep(10000);
			solo.clickOnScreen(expandX, expandY);

			Thread.sleep(2000);

			solo.clickOnScreen(starX, starY);
			Thread.sleep(10000);
			solo.clickOnScreen(starX, starY);

			Thread.sleep(2000);

			String restaurantName = "L'Ornithorynque";
			if (!solo.searchText(restaurantName)) {
				Assert.fail("did not find " + restaurantName);
			}
			solo.clickOnText(restaurantName);

			Thread.sleep(4000);

			String dish = "Plat du jour 1";
			if (!solo.searchText(dish)) {
				Assert.fail("did not find dish");
			}
			solo.clickOnText(dish);

			Thread.sleep(5000);

			solo.clickOnScreen(fiveStarX, fiveStarY);

			Thread.sleep(5000);

			String buttonText = "Rate it!";
			if (!solo.searchButton(buttonText)) {
				Assert.fail("cannot find " + buttonText);
			}
			solo.clickOnButton(buttonText);

			Thread.sleep(2000);
			String suggestions = "Suggestions";
			solo.clickOnMenuItem(suggestions);

			if (solo.getCurrentActivity().getClass() != FoodSuggestionsView.class
					&& !solo.waitForActivity(
							FoodSuggestionsView.class.getName(), 20000)) {
				Assert.fail("cannot start " + suggestions);
			}

			Thread.sleep(3000);
			for (String choice : new String[] { "Meat", "Pasta", "Porc",
					"Pizza" }) {
				if (!solo.searchText(choice)) {
					Assert.fail("cannot find " + choice);
				}
				solo.clickOnText(choice);

				Thread.sleep(1000);
			}

			buttonText = "See Suggestions";
			if (!solo.searchButton(buttonText)) {
				Assert.fail("cannot find " + buttonText);
			}
			solo.clickOnButton(buttonText);

			Thread.sleep(10000);
			solo.clickOnScreen(restaurantX, restaurantY);

			suggestions = "Preferences";
			solo.clickOnMenuItem(suggestions);

			if (solo.getCurrentActivity().getClass() != FoodPreferencesView.class
					&& !solo.waitForActivity(
							FoodPreferencesView.class.getName(), 5000)) {
				Assert.fail("cannot start " + suggestions);
			}

			for (String restaurant : new String[] { "Bistro 31", "L'Atlantide",
					"L'Esplanade", "L'Ornithorynque", "La Table de Vallotton",
					"Le Copernic", "Le Corbusier", "Le Hodler",
					"Le Parmentier", "Le Vinci" }) {
				if (!solo.searchText(restaurant)) {
					Assert.fail("cannot find " + restaurant);
				}
				solo.clickOnText(restaurant);

				Thread.sleep(500);
			}

			solo.goBack();

			Thread.sleep(5000);

			suggestions = "Preferences";
			solo.clickOnMenuItem(suggestions);

			for (String restaurant : new String[] { "Bistro 31", "L'Atlantide",
					"L'Esplanade", "L'Ornithorynque", "La Table de Vallotton",
					"Le Copernic", "Le Corbusier", "Le Hodler",
					"Le Parmentier", "Le Vinci" }) {
				if (!solo.searchText(restaurant)) {
					Assert.fail("cannot find " + restaurant);
				}
				solo.clickOnText(restaurant);

				Thread.sleep(500);
			}

			solo.goBack();
			solo.goBack();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private void showMap() {
		try {
			if (!clickAndStartActivity("Map", MapMainView.class)) {
				Assert.fail("Cannot start Map");
			}

			String menu = "My position";
			solo.clickOnMenuItem(menu);
			Thread.sleep(10000);
			MotionEvent.PointerCoords[] coordinates = new MotionEvent.PointerCoords[2];
			coordinates[0] = new PointerCoords();
			coordinates[1] = new PointerCoords();

			coordinates[0].x = width / 2 + 10;
			coordinates[0].y = height / 2 - 10;

			coordinates[1].x = width / 2 - 10;
			coordinates[1].y = height / 2 + 10;

			final View view = solo.getCurrentViews().get(0);
			int[] identifiers = new int[] { 0, 1 };
			final MotionEvent evt = MotionEvent.obtain(
					SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
					MotionEvent.ACTION_DOWN, 2, identifiers, coordinates, 0, 2,
					2, 0, 0, 0, 0);
			view.post(new Runnable() {
				public void run() {
					view.dispatchTouchEvent(evt);
				}
			});

			for (int i = 0; i < 10; i++) {
				Thread.sleep(100);
				coordinates = Arrays.copyOf(coordinates, 2);
				coordinates[0].x += 10;
				coordinates[0].y += -10;

				coordinates[1].x += -10;
				coordinates[1].y += 10;
				final MotionEvent evt2 = MotionEvent.obtain(
						SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_DOWN, 2, identifiers, coordinates,
						0, 2, 2, 0, 0, 0, 0);
				view.post(new Runnable() {
					public void run() {
						view.dispatchTouchEvent(evt2);
					}
				});
			}

			coordinates = new PointerCoords[1];
			coordinates[0] = new PointerCoords();
			identifiers = new int[1];
			identifiers[0] = 0;
			coordinates[0].x = width / 2 + 10;
			coordinates[0].y = height / 2 - 10;

			for (int i = 0; i < 10; i++) {
				Thread.sleep(50);
				coordinates = Arrays.copyOf(coordinates, 1);
				coordinates[0].x += 10;
				final MotionEvent evt2 = MotionEvent.obtain(
						SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_MOVE, 1, identifiers, coordinates,
						0, 2, 2, 0, 0, 0, 0);
				view.post(new Runnable() {
					public void run() {
						view.dispatchTouchEvent(evt2);
					}
				});
			}

			for (int i = 0; i < 10; i++) {
				Thread.sleep(50);
				coordinates = Arrays.copyOf(coordinates, 1);
				coordinates[0].x += -10;
				final MotionEvent evt2 = MotionEvent.obtain(
						SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
						MotionEvent.ACTION_MOVE, 1, identifiers, coordinates,
						0, 2, 2, 0, 0, 0, 0);
				view.post(new Runnable() {
					public void run() {
						view.dispatchTouchEvent(evt2);
					}
				});
			}
			Thread.sleep(10000);

			menu = "Search";
			solo.clickOnMenuItem(menu);
			Thread.sleep(5000);

			solo.enterText(0, "INN 329");
			Thread.sleep(3000);
			solo.sendKey(Solo.ENTER);
			Thread.sleep(10000);
			solo.goBack();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private void showNews() {
		try {
			if (!clickAndStartActivity("News", NewsMainView.class)) {
				Assert.fail("Cannot start News");
			}

			Thread.sleep(10000);
			for (int i = 0; i < 3; i++) {
				solo.scrollDown();
				Thread.sleep(2000);
			}
			for (int i = 0; i < 3; i++) {
				solo.scrollUp();
				Thread.sleep(2000);
			}

			solo.clickOnScreen(width / 2, height / 2);

			Thread.sleep(10000);

			solo.goBack();
			solo.goBack();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

	private void showTransport() {
		try {
			if (!clickAndStartActivity("Transport", TransportMainView.class)) {
				Assert.fail("Cannot start Transport");
			}

			String button = "Add a station";
			if (!solo.searchButton(button)) {
				Assert.fail("cannot find " + button);
			}

			solo.clickOnButton(button);

			String text = "Lausanne-Flon";
			for (int i = 0; i < text.length(); i++) {
				char ch = text.charAt(i);
				solo.enterText(0, "" + ch);
				Thread.sleep(200);
			}
			solo.sendKey(Solo.ENTER);
			Thread.sleep(5000);
			solo.sendKey(Solo.ENTER);

			Thread.sleep(20000);

			String menu = "Edit stations";
			solo.clickOnMenuItem(menu);
			Thread.sleep(5000);
			if (!solo.searchText(text)) {
				Assert.fail("cannot find to remove " + text);
			}
			solo.clickOnText("Lausanne");
			Thread.sleep(5000);

			text = "Yes";
			if (!solo.searchText(text)) {
				Assert.fail("cannot find to remove " + text);
			}
			solo.clickOnText(text);

			Thread.sleep(5000);
			solo.goBack();
			solo.goBack();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	public void test() {

		while (true) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			showFood();

			showFood();

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			showTransport();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			showNews();

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			showBikes();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			showDirectory();

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			showMap();
		}

	}

	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

}

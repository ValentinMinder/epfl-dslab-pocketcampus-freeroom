package org.pocketcampus.test;

import java.util.Arrays;

import junit.framework.Assert;

import org.pocketcampus.plugin.bikes.android.BikesMainView;
import org.pocketcampus.plugin.dashboard.android.DashboardView;
import org.pocketcampus.plugin.directory.android.DirectorySearchView;
import org.pocketcampus.plugin.food.android.FoodMainView;
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
	private int height = 1200;

	public DemoScript() {
		super("org.pocketcampus", DashboardView.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	private boolean clickOnText(String text) {
		if (!solo.searchText(text)) {
			if (!solo.waitForText(text, 1, 1000)) {
				return false;
			}
		}
		solo.clickOnText(text);
		return true;
	}

	private boolean clickAndStartActivity(String text,
			Class<? extends Activity> activity) {
		if (!solo.waitForText(text, 1, 5000)) {
			System.out.println("Cannot find " + text);
			return false;
		}
		solo.clickOnText(text);
		if (!solo.waitForActivity(activity.getClass().getName(), 10000)) {
			if (activity.getName().equals(
					solo.getCurrentActivity().getClass().getName())) {
				return true;
			}
			System.out.println("Expecting " + activity.getName()
					+ " instead got "
					+ solo.getCurrentActivity().getClass().getName());
			return false;
		}
		return true;
	}

	private void showBikes() {
		solo.sleep(1000);
		if (!clickAndStartActivity("Bikes", BikesMainView.class)) {
			Assert.fail("did not succeed in starting Bikes");
		}
		solo.sleep(3000);
		if (!clickOnText("Turing")) {
			System.out.println("Cannot find Turing");
			return;
		}

		solo.sleep(6000);
		solo.goBack();
		solo.sleep(1000);
		solo.goBack();
	}

	private void showDirectory() {
		if (!clickAndStartActivity("Directory", DirectorySearchView.class)) {
			Assert.fail("did not succeed in starting Directory");
		}
		String text = "Florian ";
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			solo.enterText(0, "" + ch);
			solo.sleep(500);
		}

		solo.sleep(3000);
		String fullName = "Florian Laurent";
		if (!clickOnText(fullName)) {
			Assert.fail("did not succeed in finding Florian");
		}

		solo.sleep(5000);
		solo.goBack();

		solo.goBack();
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
		if (!clickAndStartActivity("Food", FoodMainView.class)) {
			Assert.fail("did not succeed in starting Food");
		}

		solo.clickOnScreen(expandX, expandY);
		solo.sleep(10000);

		solo.scrollDown();
		solo.sleep(5000);
		solo.scrollUp();
		solo.sleep(5000);

		solo.clickOnScreen(expandX, expandY);

		solo.sleep(2000);
		solo.clickOnScreen(starX, starY);
		solo.sleep(10000);
		solo.clickOnScreen(starX, starY);

		solo.sleep(2000);
		String restaurantName = "L'Ornithorynque";
		if (!clickOnText(restaurantName)) {
			Assert.fail("did not find " + restaurantName);
		}
		solo.sleep(4000);
		String dish = "Plat du jour 1";
		if (!clickOnText(dish)) {
			Assert.fail("did not find dish");
		}
		solo.sleep(5000);
		solo.clickOnScreen(fiveStarX, fiveStarY);
		solo.sleep(5000);
		String buttonText = "Rate it!";
		if (!solo.searchButton(buttonText)) {
			Assert.fail("cannot find " + buttonText);
		}
		solo.clickOnButton(buttonText);
		solo.sleep(2000);

		String suggestions = "Suggestions";
		solo.clickOnMenuItem(suggestions);
		if (solo.getCurrentActivity().getClass() != FoodSuggestionsView.class
				&& !solo.waitForActivity(FoodSuggestionsView.class.getName(),
						20000)) {
			Assert.fail("cannot start " + suggestions);
		}
		solo.sleep(3000);
		for (String choice : new String[] { "Meat", "Pasta", "Porc", "Pizza" }) {
			if (!clickOnText(choice)) {
				Assert.fail("cannot find " + choice);
			}
			solo.sleep(200);
		}
		solo.sleep(5000);
		buttonText = "See Suggestions";
		if (!solo.searchButton(buttonText)) {
			Assert.fail("cannot find " + buttonText);
		}
		solo.clickOnButton(buttonText);
		solo.sleep(10000);
		solo.clickOnScreen(restaurantX, restaurantY);

		solo.goBack();
	}

	private void showMap() {
		if (!clickAndStartActivity("Map", MapMainView.class)) {
			Assert.fail("Cannot start Map");
		}

		String menu = "My position";
		solo.clickOnMenuItem(menu);
		solo.sleep(10000);
		MotionEvent.PointerCoords[] coordinates = new MotionEvent.PointerCoords[2];
		coordinates[0] = new PointerCoords();
		coordinates[1] = new PointerCoords();

		coordinates[0].x = width / 2 + 10;
		coordinates[0].y = height / 2 - 10;

		coordinates[1].x = width / 2 - 10;
		coordinates[1].y = height / 2 + 10;

		final View view = solo.getCurrentViews().get(0);
		int[] identifiers = new int[] { 0, 1 };
		final MotionEvent evt = MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 2,
				identifiers, coordinates, 0, 2, 2, 0, 0, 0, 0);
		view.post(new Runnable() {
			public void run() {
				view.dispatchTouchEvent(evt);
			}
		});

		for (int i = 0; i < 10; i++) {
			solo.sleep(100);
			coordinates = Arrays.copyOf(coordinates, 2);
			coordinates[0].x += 10;
			coordinates[0].y += -10;

			coordinates[1].x += -10;
			coordinates[1].y += 10;
			final MotionEvent evt2 = MotionEvent.obtain(
					SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
					MotionEvent.ACTION_DOWN, 2, identifiers, coordinates, 0, 2,
					2, 0, 0, 0, 0);
			view.post(new Runnable() {
				public void run() {
					view.dispatchTouchEvent(evt2);
				}
			});
		}

		solo.drag(coordinates[0].x, coordinates[0].x + 20, coordinates[0].y,
				coordinates[0].y, 10);
		solo.sleep(200);
		solo.drag(coordinates[0].x + 20, coordinates[0].x, coordinates[0].y,
				coordinates[0].y, 10);

		menu = "Search";
		solo.clickOnMenuItem(menu);
		solo.sleep(5000);

		solo.enterText(0, "INN 329");
		solo.sleep(3000);
		solo.sendKey(Solo.ENTER);
		solo.sleep(10000);
		solo.goBack();
	}

	private void showNews() {
		if (!clickAndStartActivity("News", NewsMainView.class)) {
			Assert.fail("Cannot start News");
		}

		solo.sleep(10000);
		for (int i = 0; i < 1; i++) {
			solo.scrollDown();
			solo.sleep(2000);
		}
		for (int i = 0; i < 1; i++) {
			solo.scrollUp();
			solo.sleep(2000);
		}

		solo.clickOnScreen(width / 2, height / 2);

		solo.sleep(10000);

		solo.goBack();
		solo.goBack();

	}

	private void showTransport() {
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
			solo.sleep(200);
		}
		solo.sleep(5000);
		solo.sendKey(Solo.ENTER);
		solo.sleep(2000);
		solo.sendKey(Solo.ENTER);

		solo.sleep(20000);

		String menu = "Edit stations";
		solo.clickOnMenuItem(menu);
		solo.sleep(5000);

		text = "Lausanne";
		if (!solo.searchText(text)) {
			Assert.fail("cannot find to remove " + text);
		}
		solo.clickOnText(text);
		solo.sleep(5000);

		text = "Yes";
		if (!solo.searchText(text)) {
			Assert.fail("cannot find to remove " + text);
		}
		solo.clickOnText(text);

		solo.sleep(5000);
		solo.goBack();
		solo.goBack();
	}

	public void test() {

		while (true) {

			solo.sleep(3000);
			showFood();

			solo.sleep(3000);
			showTransport();

			solo.sleep(3000);
			showNews();
			//
			solo.sleep(3000);
			showBikes();

			solo.sleep(3000);
			showDirectory();
			//
			// solo.sleep(3000);
			// showMap();
		}

	}

	protected void tearDown() throws Exception {
		solo.finishOpenedActivities();
	}

}

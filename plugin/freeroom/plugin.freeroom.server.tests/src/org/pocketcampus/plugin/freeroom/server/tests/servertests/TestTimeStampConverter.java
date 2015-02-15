package org.pocketcampus.plugin.freeroom.server.tests.servertests;

import org.junit.Test;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

import java.util.Calendar;

import static org.junit.Assert.assertTrue;

/**
 * 
 * @author FreeRoom Project Team (2014/05)
 * @author Julien WEBER <julien.weber@epfl.ch>
 * @author Valentin MINDER <valentin.minder@epfl.ch>
 * 
 */
public class TestTimeStampConverter {

	/**
	 * Given that the converter takes the next matching day (i.e if we are
	 * thursday and we want wednesday, it should take the next one), this tests
	 * check all the possible day to ensure the tests are relevant whenever you
	 * run them.
	 */
	@Test
	public void testMonday819() {
		FRPeriod period = FRTimes.convertFRPeriod(Calendar.MONDAY, 8, 19);
		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(tsStart);
		int day = mCalendar.get(Calendar.DAY_OF_WEEK);
		int startHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		mCalendar.setTimeInMillis(tsEnd);
		int endHour = mCalendar.get(Calendar.HOUR_OF_DAY);

		assertTrue(day == Calendar.MONDAY);
		assertTrue(startHour == 8);
		assertTrue(endHour == 19);
	}

	@Test
	public void testMonday88() {
		FRPeriod period = FRTimes.convertFRPeriod(Calendar.MONDAY, 8, 8);
		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(tsStart);
		int day = mCalendar.get(Calendar.DAY_OF_WEEK);
		int startHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		mCalendar.setTimeInMillis(tsEnd);
		int endHour = mCalendar.get(Calendar.HOUR_OF_DAY);

		assertTrue(day == Calendar.MONDAY);
		assertTrue(startHour == 8);
		assertTrue(endHour == 8);
	}

	@Test
	public void testTuesday89() {
		FRPeriod period = FRTimes.convertFRPeriod(Calendar.TUESDAY, 8, 9);
		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(tsStart);
		int day = mCalendar.get(Calendar.DAY_OF_WEEK);
		int startHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		mCalendar.setTimeInMillis(tsEnd);
		int endHour = mCalendar.get(Calendar.HOUR_OF_DAY);

		assertTrue(day == Calendar.TUESDAY);
		assertTrue(startHour == 8);
		assertTrue(endHour == 9);
	}

	@Test
	public void testWednesday89() {
		FRPeriod period = FRTimes.convertFRPeriod(Calendar.WEDNESDAY, 8, 9);
		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(tsStart);
		int day = mCalendar.get(Calendar.DAY_OF_WEEK);
		int startHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		mCalendar.setTimeInMillis(tsEnd);
		int endHour = mCalendar.get(Calendar.HOUR_OF_DAY);

		assertTrue(day == Calendar.WEDNESDAY);
		assertTrue(startHour == 8);
		assertTrue(endHour == 9);
	}

	@Test
	public void testThursday89() {
		FRPeriod period = FRTimes.convertFRPeriod(Calendar.THURSDAY, 8, 9);
		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(tsStart);
		int day = mCalendar.get(Calendar.DAY_OF_WEEK);
		int startHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		mCalendar.setTimeInMillis(tsEnd);
		int endHour = mCalendar.get(Calendar.HOUR_OF_DAY);

		assertTrue(day == Calendar.THURSDAY);
		assertTrue(startHour == 8);
		assertTrue(endHour == 9);
	}

	@Test
	public void testFriday89() {
		FRPeriod period = FRTimes.convertFRPeriod(Calendar.FRIDAY, 8, 9);
		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(tsStart);
		int day = mCalendar.get(Calendar.DAY_OF_WEEK);
		int startHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		mCalendar.setTimeInMillis(tsEnd);
		int endHour = mCalendar.get(Calendar.HOUR_OF_DAY);

		assertTrue(day == Calendar.FRIDAY);
		assertTrue(startHour == 8);
		assertTrue(endHour == 9);
	}

	@Test
	public void testSaturday89() {
		FRPeriod period = FRTimes.convertFRPeriod(Calendar.SATURDAY, 8, 9);
		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(tsStart);
		int day = mCalendar.get(Calendar.DAY_OF_WEEK);
		int startHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		mCalendar.setTimeInMillis(tsEnd);
		int endHour = mCalendar.get(Calendar.HOUR_OF_DAY);

		assertTrue(day == Calendar.SATURDAY);
		assertTrue(startHour == 8);
		assertTrue(endHour == 9);
	}

	@Test
	public void testSunday89() {
		FRPeriod period = FRTimes.convertFRPeriod(Calendar.SUNDAY, 8, 9);
		long tsStart = period.getTimeStampStart();
		long tsEnd = period.getTimeStampEnd();

		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(tsStart);
		int day = mCalendar.get(Calendar.DAY_OF_WEEK);
		int startHour = mCalendar.get(Calendar.HOUR_OF_DAY);
		mCalendar.setTimeInMillis(tsEnd);
		int endHour = mCalendar.get(Calendar.HOUR_OF_DAY);

		assertTrue(day == Calendar.SUNDAY);
		assertTrue(startHour == 8);
		assertTrue(endHour == 9);
	}

}

package org.pocketcampus.plugin.freeroom.server.tests;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Test;
import org.pocketcampus.plugin.freeroom.shared.FRPeriod;
import org.pocketcampus.plugin.freeroom.shared.FreeRoomRequest;
import org.pocketcampus.plugin.freeroom.shared.utils.FRTimes;

public class TestTimeStampConverter {

	/**
	 * Given that the converter takes the next matching day (i.e if we are
	 * thursday and we want wednesday, it should take the next one), this tests
	 * check all the possible day to ensure the tests are relevant whenever you
	 * run them.
	 */
	@Test
	public void testMonday819() {
		FreeRoomRequest request = FRTimes.convert(Calendar.MONDAY, 8, 19);
		FRPeriod period = request.getPeriod();
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
		
		int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		int todayDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		
		assertTrue(todayDay <= dayOfMonth);
	}

	@Test
	public void testMonday88() {
		FreeRoomRequest request = FRTimes.convert(Calendar.MONDAY, 8, 8);
		FRPeriod period = request.getPeriod();
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
		FreeRoomRequest request = FRTimes.convert(Calendar.TUESDAY, 8, 9);
		FRPeriod period = request.getPeriod();
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
		
		int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		int todayDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		
		assertTrue(todayDay <= dayOfMonth);
	}

	@Test
	public void testWednesday89() {
		FreeRoomRequest request = FRTimes.convert(Calendar.WEDNESDAY, 8, 9);
		FRPeriod period = request.getPeriod();
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
		
		int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		int todayDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		
		assertTrue(todayDay <= dayOfMonth);
	}

	@Test
	public void testThursday89() {
		FreeRoomRequest request = FRTimes.convert(Calendar.THURSDAY, 8, 9);
		FRPeriod period = request.getPeriod();
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
		
		int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		int todayDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		
		assertTrue(todayDay <= dayOfMonth);
	}

	@Test
	public void testFriday89() {
		FreeRoomRequest request = FRTimes.convert(Calendar.FRIDAY, 8, 9);
		FRPeriod period = request.getPeriod();
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
		
		int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		int todayDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		
		assertTrue(todayDay <= dayOfMonth);
	}

	@Test
	public void testSaturday89() {
		FreeRoomRequest request = FRTimes.convert(Calendar.SATURDAY, 8, 9);
		FRPeriod period = request.getPeriod();
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
		
		int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		int todayDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		
		assertTrue(todayDay <= dayOfMonth);
	}
	
	@Test
	public void testSunday89() {
		FreeRoomRequest request = FRTimes.convert(Calendar.SUNDAY, 8, 9);
		FRPeriod period = request.getPeriod();
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
		
		int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		int todayDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		
		assertTrue(todayDay <= dayOfMonth);
	}
	
}

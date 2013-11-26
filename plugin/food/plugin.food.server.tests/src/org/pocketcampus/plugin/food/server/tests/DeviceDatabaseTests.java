package org.pocketcampus.plugin.food.server.tests;

import static org.junit.Assert.*;
import org.junit.Test;

import org.pocketcampus.plugin.food.server.DeviceDatabaseImpl;

import org.joda.time.*;

/** 
 * Tests for DeviceDatabaseImpl.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public final class DeviceDatabaseTests {
	// Voting works
	@Test
	public void votingWorks() {
		DeviceDatabaseImpl deviceDatabase = new DeviceDatabaseImpl();
		
		deviceDatabase.vote("12345");
	}
	
	// hasVotedToday returns false when it's not the case
	@Test
	public void hasVotedTodayCorrectlyReturnsFalse() {
		DeviceDatabaseImpl deviceDatabase = new DeviceDatabaseImpl();
		
		deviceDatabase.vote("not 12345");
		
		assertFalse(deviceDatabase.hasVotedToday("12345"));
	}
	
	// hasVotedToday returns false when it's the case
	@Test
	public void hasVotedTodayCorrectlyReturnsTrue() {
		DeviceDatabaseImpl deviceDatabase = new DeviceDatabaseImpl();
		
		deviceDatabase.vote("12345");
		
		assertTrue(deviceDatabase.hasVotedToday("12345"));
	}
	
	// hasVotedToday stays true before the next day
	@Test
	public void hasVotedTodayStillReturnsTrueUntilNextDay() {
		DeviceDatabaseImpl deviceDatabase = new DeviceDatabaseImpl();
		
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 30, 12, 00).getMillis());
		deviceDatabase.vote("12345");
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 30, 23, 59).getMillis());
		
		assertTrue(deviceDatabase.hasVotedToday("12345"));
	}
	
	// hasVotedToday returns false after 1 day
	@Test
	public void hasVotedTodayReturnsFalseNextDay() {
		DeviceDatabaseImpl deviceDatabase = new DeviceDatabaseImpl();
		
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 30, 12, 00).getMillis());
		deviceDatabase.vote("12345");
		DateTimeUtils.setCurrentMillisFixed(new DateTime(2013, 10, 31, 00, 00).getMillis());
		
		assertFalse(deviceDatabase.hasVotedToday("12345"));
	}
}
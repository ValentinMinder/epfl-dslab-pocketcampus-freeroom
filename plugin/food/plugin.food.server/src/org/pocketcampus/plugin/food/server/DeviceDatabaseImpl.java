package org.pocketcampus.plugin.food.server;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * The database containing the device IDs of people who voted on the current day.
 * This is a simple in-memory database, a persistent one is not needed.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public class DeviceDatabaseImpl implements DeviceDatabase {
	private Date _lastVoteDate;
	private final Set<String> _votedToday;

	public DeviceDatabaseImpl() {
		_lastVoteDate = new Date();
		_votedToday = new HashSet<String>(100);
	}

	@Override
	public void insert(String deviceId) {
		ensureToday();
		_votedToday.add(deviceId);
	}

	@Override
	public boolean hasVotedToday(String deviceId) {
		ensureToday();
		return _votedToday.contains(deviceId);
	}

	private void ensureToday() {
		Date now = new Date();
		if (daysBetween(now, _lastVoteDate) >= 1) {
			_lastVoteDate = now;
			_votedToday.clear();
		}
	}

	private int daysBetween(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);

		return c1.get(Calendar.DAY_OF_YEAR) - c2.get(Calendar.DAY_OF_YEAR);
	}
}
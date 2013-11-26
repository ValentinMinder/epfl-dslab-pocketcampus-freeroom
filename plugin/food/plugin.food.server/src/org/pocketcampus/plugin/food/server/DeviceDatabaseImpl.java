package org.pocketcampus.plugin.food.server;

import java.util.Set;
import java.util.HashSet;

import org.joda.time.LocalDate;

/**
 * The database containing the device IDs of people who voted on the current day.
 * This is a simple in-memory database, a persistent one is not needed.
 * 
 * @author Solal Pirelli <solal.pirelli@epfl.ch>
 */
public class DeviceDatabaseImpl implements DeviceDatabase {
	private LocalDate _currentDay;
	private final Set<String> _votedToday;

	public DeviceDatabaseImpl() {
		_currentDay = LocalDate.now();
		_votedToday = new HashSet<String>(100);
	}

	@Override
	public void vote(String deviceId) {
		ensureToday();
		_votedToday.add(deviceId);
	}

	@Override
	public boolean hasVotedToday(String deviceId) {
		ensureToday();
		return _votedToday.contains(deviceId);
	}

	private void ensureToday() {
		LocalDate now = LocalDate.now();
		if (now.compareTo(_currentDay) != 0) {
			_currentDay = now;
			_votedToday.clear();
		}
	}
}
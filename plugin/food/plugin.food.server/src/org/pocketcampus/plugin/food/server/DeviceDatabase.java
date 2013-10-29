package org.pocketcampus.plugin.food.server;

public interface DeviceDatabase {
	void insert(String deviceId);
	boolean hasVotedToday(String deviceId);
}
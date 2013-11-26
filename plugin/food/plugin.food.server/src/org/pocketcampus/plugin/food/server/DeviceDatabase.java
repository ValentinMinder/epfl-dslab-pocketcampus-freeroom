package org.pocketcampus.plugin.food.server;

public interface DeviceDatabase {
	void vote(String deviceId);
	boolean hasVotedToday(String deviceId);
}
package org.pocketcampus.plugin.transport.android.iface;

import java.util.List;

import org.pocketcampus.plugin.transport.shared.Location;

public interface ITransportController {
	/**
	 * 
	 * @param constraint
	 */
	public void getAutocompletions(String constraint);
	
	/**
	 * 
	 * @param location
	 */
	public void nextDepartures(String location);

}

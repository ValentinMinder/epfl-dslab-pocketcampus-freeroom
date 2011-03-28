package org.pocketcampus.plugin.map.elements;

import java.util.ArrayList;

public class MapElementsList extends ArrayList<IMapElement> {
	private static final long serialVersionUID = 1054957477020550085L;
	
	private int cacheTimeInMinutes_;

	public int getCacheTimeInMinutes() {
		return cacheTimeInMinutes_;
	}

	/**
	 * Set the time in minutes the elements can be cached.
	 * 0 mean no cache at all.
	 * -1 means never refresh automatically
	 * 
	 * @param cacheTimeInMinutes Cache time
	 */
	public void setCacheTimeInMinutes(int cacheTimeInMinutes) {
		this.cacheTimeInMinutes_ = cacheTimeInMinutes;
	}
	
	
}

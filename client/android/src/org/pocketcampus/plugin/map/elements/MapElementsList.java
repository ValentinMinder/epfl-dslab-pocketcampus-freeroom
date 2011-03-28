package org.pocketcampus.plugin.map.elements;

import java.util.ArrayList;

public class MapElementsList extends ArrayList<MapElement> {
	private static final long serialVersionUID = 1054957477020550085L;

	private String layerTitle_;
	private int cacheTimeInMinutes_;
	
	public MapElementsList(String title, int cache) {
		this.layerTitle_ = title;
		this.cacheTimeInMinutes_ = cache;
	}

	public String getLayerTitle() {
		return layerTitle_;
	}

	public void setLayerTitle(String layerTitle) {
		this.layerTitle_ = layerTitle;
	}

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

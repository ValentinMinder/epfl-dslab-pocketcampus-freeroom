package org.pocketcampus.plugin.map.elements;

import java.util.ArrayList;

import org.osmdroid.views.overlay.OverlayItem;

import android.graphics.drawable.Drawable;

public class MapElementsList extends ArrayList<OverlayItem> {

	private static final long serialVersionUID = 1054957477020550085L;

	/**
	 * The title of the layer, for example restaurant, parking, etc.
	 */
	private String layerTitle_;
	private int cacheTimeInMinutes_;
	
	public MapElementsList(String title, int cache) {
		this.layerTitle_ = title;
		this.cacheTimeInMinutes_ = cache;
	}
		
		
	public Drawable getDefaultDrawable() {
		//TODO 
		return null;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((layerTitle_ == null) ? 0 : layerTitle_.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapElementsList other = (MapElementsList) obj;
		if (layerTitle_ == null) {
			if (other.layerTitle_ != null)
				return false;
		} else if (!layerTitle_.equals(other.layerTitle_))
			return false;
		return true;
	}
	
	
}

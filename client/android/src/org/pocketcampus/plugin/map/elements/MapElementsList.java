package org.pocketcampus.plugin.map.elements;

import java.util.ArrayList;

import org.osmdroid.views.overlay.OverlayItem;
import org.pocketcampus.shared.plugin.map.MapLayerBean;

import android.graphics.drawable.Drawable;

public class MapElementsList extends ArrayList<OverlayItem> {

	private static final long serialVersionUID = 1054957477020550085L;

	/**
	 * The title of the layer, for example restaurant, parking, etc.
	 */
	private String layerTitle_;
	private int cacheTimeInMinutes_;
	private int layerId_;
	
	public MapElementsList(String title, int cache) {
		this.layerTitle_ = title;
		this.cacheTimeInMinutes_ = cache;
	}
	
	public MapElementsList(MapLayerBean mlb) {
		this.layerTitle_ = mlb.getName();
		this.cacheTimeInMinutes_ = mlb.getCache();
		this.layerId_ = mlb.getId();
		//XXX more?
	}
		
	public int getLayerId() {
		return layerId_;
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
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else
			if (obj.getClass() != getClass())
				return false;
			else
				return ((MapElementsList)obj).layerId_ == this.layerId_ &&
					((MapElementsList)obj).layerTitle_.equals(this.layerTitle_);
	}
	
	@Override
	public String toString() {
		return "MapElementsList:<" + this.layerTitle_ + ">";
	}

}

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
	private String layerId_;
	private String iconUrl_;
	
	public MapElementsList(String title, int cache) {
		this.layerTitle_ = title;
		this.cacheTimeInMinutes_ = cache;
	}
	
	public MapElementsList(MapLayerBean mlb) {
		this.layerTitle_ = mlb.getName();
		this.cacheTimeInMinutes_ = mlb.getCache();
		this.layerId_ = mlb.getExternalId();
		this.iconUrl_ = mlb.getDrawableUrl();
		//XXX more?
	}
		
	public String getLayerId() {
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
		
	public String getIconUrl() {
		return iconUrl_;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl_ = iconUrl;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + ((layerId_ == null) ? 0 : layerId_.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		MapElementsList other = (MapElementsList) obj;
		if (layerId_ == null) {
			if (other.layerId_ != null)
				return false;
		} else if (!layerId_.equals(other.layerId_))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MapElementsList:<" + this.layerTitle_ + ">";
	}

}

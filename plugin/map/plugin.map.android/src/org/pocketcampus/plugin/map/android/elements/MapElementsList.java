package org.pocketcampus.plugin.map.android.elements;

import java.util.ArrayList;

/**
 * Custom List class for <code>MapElement</code>s.
 * 
 * @author Florian <florian.laurent@epfl.ch>
 *
 */
public class MapElementsList extends ArrayList<MapElement> {

	private static final long serialVersionUID = 1054957477020550085L;

	/**
	 * The title of the layer, for example restaurant, parking, etc.
	 */
	private String layerTitle_;
	private int cacheTimeInSeconds_;
	private long layerId_;
	private String iconUrl_;
	private boolean isDisplayable_;
	
	public MapElementsList(String title, int cache) {
		layerTitle_ = title;
		cacheTimeInSeconds_ = cache;
	}
	
	public MapElement getItemFromId(int id) {
		for(MapElement me : this) {
			if(me.getItemId() == id) {
				return me;
			}
		}
		
		return null;
	}
		
	public long getLayerId() {
		return layerId_;
	}
	
	public String getLayerTitle() {
		return layerTitle_;
	}

	public void setLayerTitle(String layerTitle) {
		this.layerTitle_ = layerTitle;
	}

	public int getCacheTimeInSeconds() {
		return cacheTimeInSeconds_;
	}

	/**
	 * Set the time in seconds the elements can be cached.
	 * 0 means no cache at all.
	 * -1 means never refresh automatically
	 * 
	 * @param cacheTimeInSeconds Cache time
	 */
	public void setCacheTimeInSeconds(int cacheTimeInSeconds) {
		this.cacheTimeInSeconds_ = cacheTimeInSeconds;
	}
		
	public String getIconUrl() {
		return iconUrl_;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl_ = iconUrl;
	}

	public boolean isDisplayable() {
		return isDisplayable_;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + cacheTimeInSeconds_;
		result = prime * result
				+ ((iconUrl_ == null) ? 0 : iconUrl_.hashCode());
		result = prime * result + (isDisplayable_ ? 1231 : 1237);
		result = prime * result + (int) (layerId_ ^ (layerId_ >>> 32));
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
		if (cacheTimeInSeconds_ != other.cacheTimeInSeconds_)
			return false;
		if (iconUrl_ == null) {
			if (other.iconUrl_ != null)
				return false;
		} else if (!iconUrl_.equals(other.iconUrl_))
			return false;
		if (isDisplayable_ != other.isDisplayable_)
			return false;
		if (layerId_ != other.layerId_)
			return false;
		if (layerTitle_ == null) {
			if (other.layerTitle_ != null)
				return false;
		} else if (!layerTitle_.equals(other.layerTitle_))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MapElementsList:<" + this.layerTitle_ + ">";
	}

}

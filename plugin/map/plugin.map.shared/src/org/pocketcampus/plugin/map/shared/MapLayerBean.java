package org.pocketcampus.plugin.map.shared;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Container that represents a layer on the map.
 * 
 * @status Complete
 * 
 * @author Jonas, Johan
 *
 */
public class MapLayerBean implements Serializable {

	private static final long serialVersionUID = -314236678555986755L;
	
	private String name;
	private String drawableUrl;
	private long externalId;
	private transient int pluginInternalId; // Do not serialize it
	private int cacheInSeconds;
	private boolean displayable;
	
	/**
	 * Constructor used by server plugins to provide a layer
	 * @param name Name of the layers, displayed to the user
	 * @param drawable_url URL of the icon representing the items of the layer
	 * @param pluginInstance Plugin that generates the layer, used to generate a unique ID
	 * @param layerId Internal ID of the layer. This ID is unique only on plugin's scope
	 * @param cacheInSeconds Number of seconds to wait before updating the layer
	 * @param displayable Tells if the layer can be displayed on the map (or only used for the search)
	 */
	public MapLayerBean(String name, String drawable_url, Object pluginInstance, int layerId, int cacheInSeconds, boolean displayable) {
		this.name = name;
		this.drawableUrl = drawable_url;
		this.pluginInternalId = layerId;
		this.cacheInSeconds = cacheInSeconds;
		this.displayable = displayable;
		
		// Create the unique ID using MD5, plugin's classname and layer ID
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			String s = new String(pluginInstance.getClass().getCanonicalName() + layerId);
		    m.update(s.getBytes(),0,s.length());
		    BigInteger i = new BigInteger(1,m.digest());
			//this.externalId =  String.format("%1$032X", i);
		    this.externalId =  Integer.parseInt(i.toString());
			
		} catch (NoSuchAlgorithmException e) {
			this.externalId = 0;//pluginInstance.getClass().getCanonicalName() + layerId;
		}
	}
	
	public boolean isDisplayable() {
		return displayable;
	}
	public void setDisplayable(boolean displayable) {
		this.displayable = displayable;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDrawableUrl() {
		return drawableUrl;
	}
	public void setDrawable_url(String drawable_url) {
		this.drawableUrl = drawable_url;
	}
	
	public long getExternalId() {
		return externalId;
	}
	public int getInternalId() {
		return pluginInternalId;
	}
	
	public int getCacheInSeconds() {
		return cacheInSeconds;
	}
	public void setCacheInSeconds(int cache) {
		this.cacheInSeconds = cache;
	}
	
	@Override
	public String toString() {
		return name;
	}
}

package org.pocketcampus.shared.plugin.map;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MapLayerBean implements Serializable{

	private static final long serialVersionUID = -314236678555986755L;
	private String name, drawableUrl;
	private String externalId;
	private transient int pluginInternalId; // Do not serialize it
	private int cacheInSeconds;
	private boolean displayable;
	
	public MapLayerBean(String name, String drawable_url, String pluginClassname, int layerId, int cache, boolean displayable) {
		this.name = name;
		this.drawableUrl = drawable_url;
		this.pluginInternalId = layerId;
		this.cacheInSeconds = cache;
		this.displayable = displayable;
		
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			String s = new String(pluginClassname + layerId);
		    m.update(s.getBytes(),0,s.length());
		    BigInteger i = new BigInteger(1,m.digest());
			this.externalId =  String.format("%1$032X", i);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public String getExternalId() {
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
	
	
}

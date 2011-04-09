package org.pocketcampus.shared.plugin.map;

public class MapLayerBean {
	private String name, drawable_url;
	private int id,cache;
	private boolean displayable;
	
	public MapLayerBean() {
		
	}
	
	public MapLayerBean(String name, String drawable_url, int id, int cache, boolean displayable) {
		this.name = name;
		this.drawable_url = drawable_url;
		this.id = id;
		this.cache = cache;
		this.displayable = displayable;
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
	public String getDrawable_url() {
		return drawable_url;
	}
	public void setDrawable_url(String drawable_url) {
		this.drawable_url = drawable_url;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCache() {
		return cache;
	}
	public void setCache(int cache) {
		this.cache = cache;
	}
	
	
}

package org.pocketcampus.shared.map;

public class MapLayerBean {
	private String name, drawable_url;
	private int id,cache;
	
	public MapLayerBean() {
		
	}
	
	public MapLayerBean(String name, String drawable_url, int id, int cache) {
		this.name = name;
		this.drawable_url = drawable_url;
		this.id = id;
		this.cache = cache;
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
